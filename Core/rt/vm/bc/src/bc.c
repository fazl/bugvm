/*
 * Copyright (C) 2012 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <bugvm.h>
#include "uthash.h"
#include "utlist.h"
#include "MurmurHash3.h"
#include "classinfo.h"

#define LOG_TAG "bc"

#define ALLOC_NATIVE_FRAMES_SIZE 8

typedef struct {
    ClassInfoHeader* classInfoHeader;
    void* start;
    void* end;
} AddressClassLookup;

typedef struct {
    ClassInfoHeader* exHeader;
    jint landingPadId;
} LandingPad;

typedef struct {
    TrycatchContext tc;
    LandingPad** landingPads;
} BcTrycatchContext;

const char* __attribute__ ((weak)) _bcMainClass = NULL;
extern char** _bcStaticLibs;
extern char** _bcBootclasspath;
extern char** _bcClasspath;
extern void* _bcBootClassesHash;
extern void* _bcClassesHash;
extern void* _bcStrippedMethodStubs;
extern void* _bcRuntimeData;
static Class* loadBootClass(Env*, const char*, Object*);
static Class* loadUserClass(Env*, const char*, Object*);
static void classInitialized(Env*, Class*);
static Interface* loadInterfaces(Env*, Class*);
static Field* loadFields(Env*, Class*);
static Method* loadMethods(Env*, Class*);
static Class* findClassAt(Env*, void*);
static Class* createClass(Env*, ClassInfoHeader*, Object*);
static jboolean exceptionMatch(Env* env, TrycatchContext*);
static ObjectArray* listBootClasses(Env*, Class*);
static ObjectArray* listUserClasses(Env*, Class*);
static Options options = {0};
static VM* vm = NULL;
static jint addressClassLookupsCount = 0;
static AddressClassLookup* addressClassLookups = NULL;

static void initOptions() {
    options.mainClass = (char*) _bcMainClass;
    options.rawBootclasspath = _bcBootclasspath;
    options.rawClasspath = _bcClasspath;
    options.loadBootClass = loadBootClass;
    options.loadUserClass = loadUserClass;
    options.classInitialized = classInitialized;
    options.loadInterfaces = loadInterfaces;
    options.loadFields = loadFields;
    options.loadMethods = loadMethods;
    options.findClassAt = findClassAt;
    options.exceptionMatch = exceptionMatch;
    options.staticLibs = _bcStaticLibs;
    options.runtimeData = &_bcRuntimeData;
    options.listBootClasses = listBootClasses;
    options.listUserClasses = listUserClasses;
}

static int bcmain(int argc, char* argv[]) {
    initOptions();

    if (!bugvmInitOptions(argc, argv, &options, FALSE)) {
        fprintf(stderr, "bugvmInitOptions(...) failed!\n");
        return 1;
    }
    Env* env = bugvmStartup(&options);
    if (!env) {
        fprintf(stderr, "bugvmStartup(...) failed!\n");
        return 1;
    }
    vm = env->vm;
    jint result = bugvmRun(env) ? 0 : 1;
    bugvmShutdown(env, result);
    return result;
}

int __attribute__ ((weak)) main(int argc, char* argv[]) {
    bcmain( argc, argv );
}

static ClassInfoHeader** getClassInfosBase(void* hash) {
    uint32_t size = ((uint32_t*) hash)[1];
    void* base = hash
            + sizeof(uint32_t) /* count */
            + sizeof(uint32_t) /* size */
            + (size << 2)
            + sizeof(uint32_t) /* this is for the last end index in the hash */;
    // Make sure base is properly aligned
    return (ClassInfoHeader**) (((uintptr_t) base + sizeof(void*) - 1) & ~(sizeof(void*) - 1));
}

static uint32_t getClassInfosCount(void* hash) {
    return ((uint32_t*) hash)[0];
}

static ClassInfoHeader* lookupClassInfo(Env* env, const char* className, void* hash) {
    ClassInfoHeader** base = getClassInfosBase(hash);

    // Hash the class name
    uint32_t h = 0;
    MurmurHash3_x86_32(className, strlen(className) + 1, 0x1ce79e5c, &h);
    uint32_t size = ((uint32_t*) hash)[1];
    h &= size - 1;

    // Get the start and end indexes
    hash += sizeof(uint32_t) + sizeof(uint32_t); // Skip count and size
    uint32_t start = ((uint32_t*) hash)[h];
    uint32_t end = ((uint32_t*) hash)[h + 1];

    // Iterate through the ClassInfoHeaders between start and end
    for (uint32_t i = start; i < end; i++) {
        ClassInfoHeader* header = base[i];
        if (header && !strcmp(header->className, className)) {
            return header;
        }
    }
    return NULL;
}

static void iterateClassInfos(Env* env, jboolean (*callback)(Env*, ClassInfoHeader*, MethodInfo*, void*), void* hash, void* data) {
    ClassInfoHeader** base = getClassInfosBase(hash);
    uint32_t count = getClassInfosCount(hash);
    uint32_t i = 0;
    for (i = 0; i < count; i++) {
        ClassInfoHeader* header = base[i];
        if ((header->flags & CI_ERROR) == 0) {
            ClassInfo ci;
            void* p = base[i];
            readClassInfo(&p, &ci);
            skipInterfaceNames(&p, &ci);
            skipFieldInfos(&p, &ci);
            jint j;
            for (j = 0; j < ci.methodCount; j++) {
                MethodInfo mi;
                readMethodInfo(&p, &mi);
                if (!callback(env, base[i], &mi, data)) {
                    break;
                }
            }
        }
    }
}

static ObjectArray* listClasses(Env* env, Class* instanceofClazz, Object* classLoader, void* hash) {
    if (instanceofClazz && (CLASS_IS_ARRAY(instanceofClazz) || CLASS_IS_PRIMITIVE(instanceofClazz))) {
        return NULL;
    }
    ClassInfoHeader** base = getClassInfosBase(hash);
    uint32_t count = getClassInfosCount(hash);
    uint32_t i = 0;
    jint matches = count;
    TypeInfo* instanceofTypeInfo = instanceofClazz ? instanceofClazz->typeInfo : NULL;
    if (instanceofTypeInfo) {
        matches = 0;
        for (i = 0; i < count; i++) {
            ClassInfoHeader* header = base[i];
            if ((header->flags & CI_ERROR) == 0) {
                if ((!CLASS_IS_INTERFACE(instanceofClazz) 
                    && bugvmIsClassTypeInfoAssignable(env, header->typeInfo, instanceofTypeInfo))
                    || (CLASS_IS_INTERFACE(instanceofClazz) 
                    && bugvmIsInterfaceTypeInfoAssignable(env, header->typeInfo, instanceofTypeInfo))) {

                    matches++;
                }
            }
        }
    }

    if (matches == 0) return NULL;
    ObjectArray* result = bugvmNewObjectArray(env, matches, java_lang_Class, NULL, NULL);
    if (!result) return NULL;

    jint j = 0;
    for (i = 0; i < count; i++) {
        ClassInfoHeader* header = base[i];
        if ((header->flags & CI_ERROR) == 0) {
            if (!instanceofTypeInfo || ((!CLASS_IS_INTERFACE(instanceofClazz) 
                && bugvmIsClassTypeInfoAssignable(env, header->typeInfo, instanceofTypeInfo))
                || (CLASS_IS_INTERFACE(instanceofClazz) 
                && bugvmIsInterfaceTypeInfoAssignable(env, header->typeInfo, instanceofTypeInfo)))) {

                result->values[j++] = (Object*) (header->clazz ? header->clazz : createClass(env, header, classLoader));
                if (bugvmExceptionOccurred(env)) return NULL;
            }
        }
    }
    return result;
}
static ObjectArray* listBootClasses(Env* env, Class* instanceofClazz) {
    return listClasses(env, instanceofClazz, NULL, _bcBootClassesHash);
}
static ObjectArray* listUserClasses(Env* env, Class* instanceofClazz) {
    return listClasses(env, instanceofClazz, systemClassLoader, _bcClassesHash);
}

static Class* loadClass(Env* env, const char* className, Object* classLoader, void* hash) {
    ClassInfoHeader* header = lookupClassInfo(env, className, hash);
    if (!header) return NULL;
    if (header->flags & CI_ERROR) {
        ClassInfoError* error = (ClassInfoError*) header;
        switch (error->errorType) {
        case CI_ERROR_TYPE_NO_CLASS_DEF_FOUND:
            bugvmThrowNoClassDefFoundError(env, error->errorMessage);
            break;
        case CI_ERROR_TYPE_ILLEGAL_ACCESS:
            bugvmThrowIllegalAccessError(env, error->errorMessage);
            break;
        case CI_ERROR_TYPE_INCOMPATIBLE_CLASS_CHANGE:
            bugvmThrowIncompatibleClassChangeError(env, error->errorMessage);
            break;
        }
        return NULL;
    }
    if (header->clazz) return header->clazz;
    return createClass(env, header, classLoader);
}

static Class* loadBootClass(Env* env, const char* className, Object* classLoader) {
    return loadClass(env, className, classLoader, _bcBootClassesHash);
}

static Class* loadUserClass(Env* env, const char* className, Object* classLoader) {
    return loadClass(env, className, classLoader, _bcClassesHash);
}

static void wrapClassNotFoundException(Env* env, const char* className) {
    Object* exception = bugvmExceptionOccurred(env);
    if (exception && exception->clazz == java_lang_ClassNotFoundException) {
        // If ClassNotFoundException is thrown we have to wrap it in a NoClassDefFoundError
        exception = bugvmExceptionClear(env);
        Method* constructor = bugvmGetInstanceMethod(env, java_lang_NoClassDefFoundError, "<init>", "(Ljava/lang/String;)V");
        if (!constructor) return;
        Object* message = bugvmNewStringUTF(env, className, -1);
        if (!message) return;
        Object* wrappedException = bugvmNewObject(env, java_lang_NoClassDefFoundError, constructor, message);
        if (!wrappedException) return;
        Class* java_lang_StackTraceElement = bugvmFindClassUsingLoader(env, "java/lang/StackTraceElement", NULL);
        if (!java_lang_StackTraceElement) return;
        ObjectArray* stackTrace = bugvmNewObjectArray(env, 0, java_lang_StackTraceElement, NULL, NULL);
        if (!stackTrace) return;
        Method* setStackTrace = bugvmGetInstanceMethod(env, java_lang_Throwable, "setStackTrace", "([Ljava/lang/StackTraceElement;)V");
        if (!setStackTrace) return;
        bugvmCallVoidInstanceMethod(env, wrappedException, setStackTrace, stackTrace);
        if (bugvmExceptionCheck(env)) return;
        Method* initCause = bugvmGetInstanceMethod(env, java_lang_NoClassDefFoundError, "initCause", "(Ljava/lang/Throwable;)Ljava/lang/Throwable;");
        if (!initCause) return;
        bugvmCallObjectInstanceMethod(env, wrappedException, initCause, exception);
        if (!bugvmExceptionCheck(env)) bugvmThrow(env, wrappedException);
    }
}

static Class* createClass(Env* env, ClassInfoHeader* header, Object* classLoader) {
    ClassInfo ci;
    void* p = header;
    readClassInfo(&p, &ci);

    Class* superclass = NULL;
    if (ci.superclassName) {
        superclass = bugvmFindClassUsingLoader(env, ci.superclassName, classLoader);
        if (!superclass) return NULL;
    }

    bugvmObtainClassLock(env);

    Class* clazz = bugvmAllocateClass(env, header->className, superclass, classLoader, ci.access, header->typeInfo, header->vitable, header->itables,
            header->classDataSize, header->instanceDataSize, header->instanceDataOffset, header->classRefCount, 
            header->instanceRefCount, ci.attributes, header->initializer);

    if (clazz) {
        if (!bugvmRegisterClass(env, clazz)) {
            bugvmReleaseClassLock(env);
            return NULL;
        }
        header->clazz = clazz;
        bugvmHookClassLoaded(env, clazz, (void*)header);
    }

    bugvmReleaseClassLock(env);
    
    return clazz;
}

static void classInitialized(Env* env, Class* clazz) {
    ClassInfoHeader* header = lookupClassInfo(env, clazz->name, 
        !clazz->classLoader || !bugvmGetParentClassLoader(env, clazz->classLoader) ? _bcBootClassesHash : _bcClassesHash);
    if (!header) return;
    bugvmAtomicStoreInt(&header->flags, header->flags | CI_INITIALIZED);
}

static Interface* loadInterfaces(Env* env, Class* clazz) {
    ClassInfoHeader* header = lookupClassInfo(env, clazz->name, 
        !clazz->classLoader || !bugvmGetParentClassLoader(env, clazz->classLoader) ? _bcBootClassesHash : _bcClassesHash);
    if (!header) return NULL;

    ClassInfo ci;
    jint i;
    void* p = header;
    readClassInfo(&p, &ci);

    Interface* first = NULL;
    for (i = 0; i < ci.interfaceCount; i++) {
        const char* interfaceName = readInterfaceName(&p);
        Class* interfaceClass = bugvmFindClassUsingLoader(env, interfaceName, clazz->classLoader);
        if (!interfaceClass) goto error;
        Interface* interf = bugvmAllocateInterface(env, interfaceClass);
        if (!interf) goto error;
        LL_APPEND(first, interf); // Interfaces has to be in the correct order so we need to use the slower LL_APPEND
    }
    return first;
error:
    while (first) {
        Interface* next = first->next;
        bugvmFreeMemoryUncollectable(env, first);
        first = next;
    }
    return NULL;
}

static Field* loadFields(Env* env, Class* clazz) {
    ClassInfoHeader* header = lookupClassInfo(env, clazz->name, 
        !clazz->classLoader || !bugvmGetParentClassLoader(env, clazz->classLoader) ? _bcBootClassesHash : _bcClassesHash);
    if (!header) return NULL;

    ClassInfo ci;
    jint i;
    void* p = header;
    readClassInfo(&p, &ci);

    skipInterfaceNames(&p, &ci);

    Field* first = NULL;
    for (i = 0; i < ci.fieldCount; i++) {
        FieldInfo fi;
        readFieldInfo(&p, &fi);
        Field* f = bugvmAllocateField(env, clazz, fi.name, fi.desc, fi.access, fi.offset, fi.attributes);
        if (!f) goto error;
        LL_PREPEND(first, f);
    }
    return first;
error:
    while (first) {
        Field* next = first->next;
        bugvmFreeMemoryUncollectable(env, first);
        first = next;
    }
    return NULL;
}

static inline jboolean isStrippedMethod(MethodInfo* mi) {
    if (mi->impl) {
        for (void** p = &_bcStrippedMethodStubs; *p; p++) {
            if (mi->impl == *p) {
                return TRUE;
            }
        }
    }
    return FALSE;
}

static Method* loadMethods(Env* env, Class* clazz) {
    ClassInfoHeader* header = lookupClassInfo(env, clazz->name, 
        !clazz->classLoader || !bugvmGetParentClassLoader(env, clazz->classLoader) ? _bcBootClassesHash : _bcClassesHash);
    if (!header) return NULL;

    ClassInfo ci;
    jint i;
    void* p = header;
    readClassInfo(&p, &ci);
    
    skipInterfaceNames(&p, &ci);
    skipFieldInfos(&p, &ci);

    Method* first = NULL;
    for (i = 0; i < ci.methodCount; i++) {
        MethodInfo mi;
        readMethodInfo(&p, &mi);
        if (!isStrippedMethod(&mi)) {
            Method* m = NULL;
            if (mi.targetFnPtr) {
                m = (Method*) bugvmAllocateBridgeMethod(env, clazz, mi.name, mi.desc, mi.vtableIndex, mi.access, mi.size, mi.impl, mi.synchronizedImpl, mi.targetFnPtr, mi.attributes);
            } else if (mi.callbackImpl) {
                m = (Method*) bugvmAllocateCallbackMethod(env, clazz, mi.name, mi.desc, mi.vtableIndex, mi.access, mi.size, mi.impl, mi.synchronizedImpl, mi.linetable, mi.callbackImpl, mi.attributes);
            } else {
                m = bugvmAllocateMethod(env, clazz, mi.name, mi.desc, mi.vtableIndex, mi.access, mi.size, mi.impl, mi.synchronizedImpl, mi.linetable, mi.attributes);
            }
            if (!m) goto error;
            LL_PREPEND(first, m);
        }
    }
    return first;
error:
    while (first) {
        Method* next = first->next;
        bugvmFreeMemoryUncollectable(env, first);
        first = next;
    }
    return NULL;
}

static inline jboolean hasImpl(MethodInfo* mi) {
    if (!mi->impl) {
        return FALSE;
    }
    return !isStrippedMethod(mi);
}

static jboolean countClassesWithConcreteMethodsCallback(Env* env, ClassInfoHeader* header, MethodInfo* mi, void* d) {
    if (hasImpl(mi)) {
        jint* count = (jint*) d;
        *count = *count + 1;
        return FALSE;
    }
    return TRUE;
}

static jboolean initAddressClassLookupsCallback(Env* env, ClassInfoHeader* header, MethodInfo* mi, void* d) {
    if (hasImpl(mi)) {
        AddressClassLookup** lookupPtr = (AddressClassLookup**) d;
        AddressClassLookup* lookup = *lookupPtr;
        if (lookup->classInfoHeader != header) {
            if (lookup->classInfoHeader) {
                *lookupPtr += 1;
                lookup = *lookupPtr;
            }
            lookup->classInfoHeader = header;
            lookup->start = mi->impl;
            lookup->end = mi->impl + mi->size;
        } else if (lookup->start > mi->impl) {
            lookup->start = mi->impl;
        } else if (lookup->end <= mi->impl) {
            lookup->end = mi->impl + mi->size;
        }
    }
    return TRUE;
}

static int addressClassLookupCompareBSearch(const void* _a, const void* _b) {
    AddressClassLookup* needle = (AddressClassLookup*) _a;
    AddressClassLookup* el = (AddressClassLookup*) _b;
    void* pc = needle->start;
    return (pc >= el->start && pc < el->end) ? 0 : ((pc < el->start) ? -1 : 1);
}

static int addressClassLookupCompareQSort(const void* _a, const void* _b) {
    AddressClassLookup* a = (AddressClassLookup*) _a;
    AddressClassLookup* b = (AddressClassLookup*) _b;
    return (a->end <= b->start) ? -1 : 1;
}

static AddressClassLookup* getAddressClassLookups(Env* env) {
    if (!addressClassLookups) {
        jint count = 0;
        iterateClassInfos(env, countClassesWithConcreteMethodsCallback, _bcBootClassesHash, &count);
        iterateClassInfos(env, countClassesWithConcreteMethodsCallback, _bcClassesHash, &count);
        AddressClassLookup* lookups = bugvmAllocateMemoryAtomicUncollectable(env, sizeof(AddressClassLookup) * count);
        if (!lookups) return NULL;
        AddressClassLookup* _lookups = lookups;
        iterateClassInfos(env, initAddressClassLookupsCallback, _bcBootClassesHash, &_lookups);
        iterateClassInfos(env, initAddressClassLookupsCallback, _bcClassesHash, &_lookups);
        qsort(lookups, count, sizeof(AddressClassLookup), addressClassLookupCompareQSort);
        addressClassLookupsCount = count;
        addressClassLookups = lookups;
    }
    return addressClassLookups;
}

Class* findClassAt(Env* env, void* pc) {
    AddressClassLookup* lookups = getAddressClassLookups(env);
    if (!lookups) return NULL;
    AddressClassLookup needle = {NULL, pc, pc};
    AddressClassLookup* result = bsearch(&needle, lookups, addressClassLookupsCount, sizeof(AddressClassLookup), addressClassLookupCompareBSearch);
    if (!result) return NULL;
    ClassInfoHeader* header = result->classInfoHeader;
    Class* clazz = header->clazz;
    if (!clazz) {
        Object* loader = NULL;
        if (lookupClassInfo(env, header->className, _bcClassesHash) == header) {
            loader = systemClassLoader;
        }
        clazz = bugvmFindClassUsingLoader(env, header->className, loader);
    }
    return clazz;
}

jboolean exceptionMatch(Env* env, TrycatchContext* _tc) {
    BcTrycatchContext* tc = (BcTrycatchContext*) _tc;
    LandingPad* lps = tc->landingPads[tc->tc.sel - 1];
    Object* throwable = bugvmExceptionOccurred(env);
    jint i;
    for (i = 0; lps[i].landingPadId > 0; i++) {
        ClassInfoHeader* header = lps[i].exHeader;
        if (!header) {
            // NULL means java.lang.Throwable which always matches
            tc->tc.sel = lps[i].landingPadId;
            return TRUE;
        }
        if (!header->clazz) {
            // Exception class not yet loaded so it cannot match.
            continue;
        }
        Class* clazz = header->clazz;
        Class* c = throwable->clazz;
        while (c && c != clazz) {
            c = c->superclass;
        }
        if (c == clazz) {
            tc->tc.sel = lps[i].landingPadId;
            return TRUE;
        }
    }
    return FALSE;
}

#define ENTER bugvmPushGatewayFrame(env)
#define LEAVEV \
    bugvmPopGatewayFrame(env); \
    if (bugvmExceptionCheck(env)) bugvmRaiseException(env, bugvmExceptionOccurred(env))
#define LEAVE(result) \
    bugvmPopGatewayFrame(env); \
    if (bugvmExceptionCheck(env)) bugvmRaiseException(env, bugvmExceptionOccurred(env)); \
    return result

static Class* ldcClass(Env* env, ClassInfoHeader* header) {
    Class* clazz = header->clazz;
    if (!clazz) {
        Object* loader = NULL;
        if (lookupClassInfo(env, header->className, _bcClassesHash) == header) {
            loader = systemClassLoader;
        }
        clazz = bugvmFindClassUsingLoader(env, header->className, loader);
        if (!clazz) wrapClassNotFoundException(env, header->className);
    }
    return clazz;
}
static void initializeClass(Env* env, ClassInfoHeader* header) {
    Class* clazz = ldcClass(env, header);
    if (clazz) bugvmInitialize(env, clazz);
}
void _bcInitializeClass(Env* env, ClassInfoHeader* header) {
    ENTER;
    initializeClass(env, header);
    LEAVEV;
}

static void* lookupVirtualMethod(Env* env, Object* thiz, char* name, char* desc) {
    Method* method = bugvmGetMethod(env, thiz->clazz, name, desc);
    if (!method) return NULL;
    if (METHOD_IS_ABSTRACT(method)) {
        bugvmThrowAbstractMethodError(env, ""); // TODO: Message
        return NULL;
    }
    return method->synchronizedImpl ? method->synchronizedImpl : method->impl;
}
void* _bcLookupVirtualMethod(Env* env, Object* thiz, char* name, char* desc) {
    ENTER;
    void* result = lookupVirtualMethod(env, thiz, name, desc);
    LEAVE(result);
}

void* lookupInterfaceMethod(Env* env, ClassInfoHeader* header, Object* thiz, char* name, char* desc) {
    initializeClass(env, header);
    if (bugvmExceptionCheck(env)) return NULL;
    Class* ownerInterface = header->clazz;
    if (!bugvmIsInstanceOf(env, thiz, ownerInterface)) {
        char message[256];
        snprintf(message, 256, "Class %s does not implement the requested interface %s", 
            bugvmToBinaryClassName(env, thiz->clazz->name), bugvmToBinaryClassName(env, ownerInterface->name));
        bugvmThrowIncompatibleClassChangeError(env, message);
        return NULL;
    }
    Method* method = bugvmGetInstanceMethod(env, thiz->clazz, name, desc);
    Object* throwable = bugvmExceptionClear(env);
    if (!method && throwable->clazz != java_lang_NoSuchMethodError) { 
        bugvmThrow(env, throwable);
        return NULL;
    }
    if (!method || METHOD_IS_ABSTRACT(method)) {
        bugvmThrowAbstractMethodError(env, ""); // TODO: Message
        return NULL;
    }
    if (!METHOD_IS_PUBLIC(method)) {
        bugvmThrowIllegalAccessError(env, ""); // TODO: Message
        return NULL;
    }
    return method->synchronizedImpl ? method->synchronizedImpl : method->impl;
}
void* _bcLookupInterfaceMethod(Env* env, ClassInfoHeader* header, Object* thiz, char* name, char* desc) {
    ENTER;
    void* result = lookupInterfaceMethod(env, header, thiz, name, desc);
    LEAVE(result);
}

void* _bcLookupInterfaceMethodImpl(Env* env, ClassInfoHeader* header, Object* thiz, uint32_t index) {
    TypeInfo* typeInfo = header->typeInfo;
    ITables* itables = thiz->clazz->itables;
    ITable* itable = itables->cache;
    if (itable->typeInfo == typeInfo) {
        return itable->table.table[index];
    }
    uint32_t i;
    for (i = 0; i < itables->count; i++) {
        itable = itables->table[i];
        if (itable->typeInfo == typeInfo) {
            itables->cache = itable;
            return itable->table.table[index];
        }
    }

    ENTER;
    initializeClass(env, header);
    Class* ownerInterface = header->clazz;
    char message[256];
    snprintf(message, 256, "Class %s does not implement the requested interface %s", 
        bugvmToBinaryClassName(env, thiz->clazz->name), bugvmToBinaryClassName(env, ownerInterface->name));
    bugvmThrowIncompatibleClassChangeError(env, message);
    LEAVE(NULL);
}

void _bcAbstractMethodCalled(Env* env, Object* thiz) {
    ENTER;
    char msg[256];
    char* name = env->reserved0;
    char* desc = env->reserved1;
    snprintf(msg, sizeof(msg), "%s.%s%s", thiz->clazz->name, name, desc);
    char* s = msg;
    while (*s != '\0') {
        if (*s == '/') *s = '.';
        s++;
    }
    bugvmThrowAbstractMethodError(env, msg);
    LEAVEV;
}

void _bcNonPublicMethodCalled(Env* env, Object* thiz) {
    ENTER;
    char msg[256];
    char* name = env->reserved0;
    char* desc = env->reserved1;
    snprintf(msg, sizeof(msg), "%s.%s%s not public", thiz->clazz->name, name, desc);
    char* s = msg;
    while (*s != '\0') {
        if (*s == '/') *s = '.';
        s++;
    }
    bugvmThrowIllegalAccessError(env, msg);
    LEAVEV;
}

void _bcMoveMemory16(void* dest, const void* src, jlong n) {
    bugvmMoveMemory16(dest, src, n);
}

void _bcMoveMemory32(void* dest, const void* src, jlong n) {
    bugvmMoveMemory32(dest, src, n);
}

void _bcTrycatchLeave(Env* env) {
    bugvmTrycatchLeave(env);
}

void _bcThrow(Env* env, Object* throwable) {
    bugvmRaiseException(env, throwable);
}

void _bcThrowIfExceptionOccurred(Env* env) {
    Object* throwable = bugvmExceptionOccurred(env);
    if (throwable) bugvmRaiseException(env, throwable);
}

Object* _bcExceptionClear(Env* env) {
    return bugvmExceptionClear(env);
}

void _bcThrowNullPointerException(Env* env) {
    ENTER;
    bugvmThrowNullPointerException(env);
    LEAVEV;
}

void _bcThrowArrayIndexOutOfBoundsException(Env* env, jint length, jint index) {
    ENTER;
    bugvmThrowArrayIndexOutOfBoundsException(env, length, index);
    LEAVEV;
}

void _bcThrowArithmeticException(Env* env) {
    ENTER;
    bugvmThrowArithmeticException(env);
    LEAVEV;
}

void _bcThrowUnsatisfiedLinkError(Env* env, char* msg) {
    ENTER;
    bugvmThrowUnsatisfiedLinkError(env, msg);
    LEAVEV;
}

void _bcThrowUnsatisfiedLinkErrorBridgeNotBound(Env* env, const char* className,
                                                const char* methodName, const char* methodDesc) {
    ENTER;
    bugvmThrowNewf(env, java_lang_UnsatisfiedLinkError,
                 "@Bridge method %s.%s%s not bound",
                 className, methodName, methodDesc);
    LEAVEV;
}

void _bcThrowUnsatisfiedLinkErrorOptionalBridgeNotBound(Env* env, const char* className,
                                                const char* methodName, const char* methodDesc) {
    ENTER;
    bugvmThrowNewf(env, java_lang_UnsatisfiedLinkError,
                 "Optional @Bridge method %s.%s%s not bound",
                 className, methodName, methodDesc);
    LEAVEV;
}

void _bcThrowNoClassDefFoundError(Env* env, char* msg) {
    ENTER;
    bugvmThrowNoClassDefFoundError(env, msg);
    LEAVEV;
}

void _bcThrowNoSuchFieldError(Env* env, char* msg) {
    ENTER;
    bugvmThrowNoSuchFieldError(env, msg);
    LEAVEV;
}

void _bcThrowNoSuchMethodError(Env* env, char* msg) {
    ENTER;
    bugvmThrowNoSuchMethodError(env, msg);
    LEAVEV;
}

void _bcThrowIllegalAccessError(Env* env, char* msg) {
    ENTER;
    bugvmThrowIllegalAccessError(env, msg);
    LEAVEV;
}

void _bcThrowInstantiationError(Env* env, char* msg) {
    ENTER;
    bugvmThrowInstantiationError(env, msg);
    LEAVEV;
}

void _bcThrowAbstractMethodError(Env* env, char* msg) {
    ENTER;
    bugvmThrowAbstractMethodError(env, msg);
    LEAVEV;
}

void _bcThrowIncompatibleClassChangeError(Env* env, char* msg) {
    ENTER;
    bugvmThrowIncompatibleClassChangeError(env, msg);
    LEAVEV;
}

void _bcThrowClassCastException(Env* env, ClassInfoHeader* header, Object* o) {
    ENTER;
    Class* clazz = ldcClass(env, header);
    bugvmThrowClassCastException(env, clazz, o->clazz);
    LEAVEV;
}

void _bcThrowClassCastExceptionArray(Env* env, Class* arrayClass, Object* o) {
    ENTER;
    bugvmThrowClassCastException(env, arrayClass, o->clazz);
    LEAVEV;
}

Object* _bcAllocate(Env* env, ClassInfoHeader* header) {
    ENTER;
    Object* obj = bugvmAllocateObject(env, header->clazz);
    LEAVE(obj);
}

void _bcRegisterFinalizer(Env* env, Object* obj) {
    ENTER;
    bugvmRegisterFinalizer(env, obj);
    LEAVEV;
}

BooleanArray* _bcNewBooleanArray(Env* env, jint length) {
    ENTER;
    BooleanArray* array = bugvmNewBooleanArray(env, length);
    LEAVE(array);
}

ByteArray* _bcNewByteArray(Env* env, jint length) {
    ENTER;
    ByteArray* array = bugvmNewByteArray(env, length);
    LEAVE(array);
}

CharArray* _bcNewCharArray(Env* env, jint length) {
    ENTER;
    CharArray* array = bugvmNewCharArray(env, length);
    LEAVE(array);
}

ShortArray* _bcNewShortArray(Env* env, jint length) {
    ENTER;
    ShortArray* array = bugvmNewShortArray(env, length);
    LEAVE(array);
}

IntArray* _bcNewIntArray(Env* env, jint length) {
    ENTER;
    IntArray* array = bugvmNewIntArray(env, length);
    LEAVE(array);
}

LongArray* _bcNewLongArray(Env* env, jint length) {
    ENTER;
    LongArray* array = bugvmNewLongArray(env, length);
    LEAVE(array);
}

FloatArray* _bcNewFloatArray(Env* env, jint length) {
    ENTER;
    FloatArray* array = bugvmNewFloatArray(env, length);
    LEAVE(array);
}

DoubleArray* _bcNewDoubleArray(Env* env, jint length) {
    ENTER;
    DoubleArray* array = bugvmNewDoubleArray(env, length);
    LEAVE(array);
}

ObjectArray* _bcNewObjectArray(Env* env, jint length, Class* arrayClass) {
    ENTER;
    ObjectArray* array = bugvmNewObjectArray(env, length, NULL, arrayClass, NULL);
    LEAVE(array);
}

Array* _bcNewMultiArray(Env* env, jint dims, jint* lengths, Class* arrayClass) {
    ENTER;
    Array* array = bugvmNewMultiArray(env, dims, lengths, arrayClass);
    LEAVE(array);
}

void _bcSetObjectArrayElement(Env* env, ObjectArray* array, jint index, Object* value) {
    if (!value) {
        array->values[index] = value;
        return;        
    }
    ENTER;
    Class* componentType = array->object.clazz->componentType;
    jboolean assignable = bugvmIsAssignableFrom(env, value->clazz, componentType);
    if (!bugvmExceptionCheck(env) && !assignable) {
        bugvmThrowArrayStoreException(env, value->clazz, array->object.clazz);
    }
    if (!bugvmExceptionCheck(env)) array->values[index] = value;
    LEAVEV;
}


Object* _bcLdcString(Env* env, Object** ptr, char* s, jint length) {
    Object* o = *ptr;
    if (o) return o;
    ENTER;
    o = bugvmNewInternedStringUTF(env, s, length);
    if (!bugvmExceptionCheck(env)) {
        *ptr = o;
        bugvmRegisterDisappearingLink(env, (void**) ptr, o);
    }
    LEAVE(o);
}

Object* _bcLdcArrayBootClass(Env* env, Class** arrayClassPtr, char* name) {
    Class* arrayClass = *arrayClassPtr;
    if (arrayClass) return (Object*) arrayClass;
    ENTER;    
    arrayClass = bugvmFindClassUsingLoader(env, name, NULL);
    wrapClassNotFoundException(env, name);
    if (!bugvmExceptionCheck(env)) {
        *arrayClassPtr = arrayClass;
    }
    LEAVE((Object*) arrayClass);
}

Object* _bcLdcArrayClass(Env* env, Class** arrayClassPtr, char* name) {
    Class* arrayClass = *arrayClassPtr;
    if (arrayClass) return (Object*) arrayClass;
    ENTER;
    arrayClass = bugvmFindClassUsingLoader(env, name, systemClassLoader);
    wrapClassNotFoundException(env, name);
    if (!bugvmExceptionCheck(env)) {
        *arrayClassPtr = arrayClass;
    }
    LEAVE((Object*) arrayClass);
}

Object* _bcLdcClass(Env* env, ClassInfoHeader* header) {
    Class* clazz = header->clazz;
    if (clazz) return (Object*) clazz;
    ENTER;
    clazz = ldcClass(env, header);
    LEAVE((Object*) clazz);
}


void _bcMonitorEnter(Env* env, Object* obj) {
    ENTER;
    bugvmLockObject(env, obj);
    LEAVEV;
}

void _bcMonitorExit(Env* env, Object* obj) {
    ENTER;
    bugvmUnlockObject(env, obj);
    LEAVEV;
}

Object* _bcCheckcast(Env* env, ClassInfoHeader* header, Object* o) {
    if (!o) return o;
    ENTER;
    Class* clazz = ldcClass(env, header);
    if (clazz) {
        jboolean b = bugvmIsAssignableFrom(env, o->clazz, clazz);
        if (!bugvmExceptionCheck(env) && !b) {
            bugvmThrowClassCastException(env, clazz, o->clazz);
        }
    }
    LEAVE(o);
}

Object* _bcCheckcastArray(Env* env, Class* arrayClass, Object* o) {
    if (!o) return o;
    ENTER;
    jboolean b = bugvmIsAssignableFrom(env, o->clazz, arrayClass);
    if (!bugvmExceptionCheck(env) && !b) {
        bugvmThrowClassCastException(env, arrayClass, o->clazz);
    }
    LEAVE(o);
}

jint _bcInstanceof(Env* env, ClassInfoHeader* header, Object* o) {
    if (!o) return (jint) FALSE;
    ENTER;
    Class* clazz = ldcClass(env, header);
    jboolean b = FALSE;
    if (clazz) {
        b = bugvmIsInstanceOf(env, o, clazz);
    }
    LEAVE((jint) b);
}

jint _bcInstanceofArray(Env* env, Class* arrayClass, Object* o) {
    if (!o) return (jint) FALSE;
    ENTER;
    jboolean b = bugvmIsInstanceOf(env, o, arrayClass);
    LEAVE((jint) b);
}

void _bcPushNativeFrame(Env* env, GatewayFrame* gwFrame, void* frameAddress) {
    bugvmPushGatewayFrame0(env, gwFrame, frameAddress, NULL);
}

void _bcPopNativeFrame(Env* env) {
    bugvmPopGatewayFrame(env);
}

void _bcPushCallbackFrame(Env* env, GatewayFrame* gwFrame, void* frameAddress) {
    bugvmPushGatewayFrame0(env, gwFrame, frameAddress, NULL);
}

void _bcPopCallbackFrame(Env* env) {
    bugvmPopGatewayFrame(env);
}

void* _bcResolveNative(Env* env, Class* clazz, char* name, char* desc, char* shortMangledName, char* longMangledName, void** ptr) {
    if (*ptr != NULL) return *ptr;
    TRACEF("_bcResolveNative: owner=%s, name=%s, desc=%s, shortMangledName=%s, longMangledName=%s", 
        clazz->name, name, desc, shortMangledName, longMangledName);
    NativeMethod* method = (NativeMethod*) bugvmGetMethod(env, clazz, name, desc);
    void* impl = NULL;
    if (method) {
        impl = bugvmResolveNativeMethodImpl(env, method, shortMangledName, longMangledName, clazz->classLoader, ptr);
    }
    return impl;
}

Env* _bcAttachThreadFromCallback(void) {
    Env* env = bugvmGetEnv();
    if (!env) {
        // This thread has never been attached or it has been
        // attached, then detached in the TLS destructor. In the
        // latter case, we are getting called back by native code
        // e.g. an auto-release pool, that is triggered after
        // the TLS destructor.
        if (bugvmAttachCurrentThreadAsDaemon(vm, &env, NULL, NULL) != JNI_OK) {
            bugvmAbort("Failed to attach thread in callback");
        }
    }
    return env;
}

void _bcDetachThreadFromCallback(Env* env) {
    if(bugvmHasThreadBeenDetached()) {
        // this can only ever be called after
        // the TLS destructor for this thread
        // has been called. It happens when
        // an auto-release pool calls back
        // into our Java code. In that case
        // bcAttachThreadFromCallback must
        // reattach the thread first.
        bugvmDetachCurrentThread(vm, TRUE, TRUE);
    }
}

void* _bcCopyStruct(Env* env, void* src, jint size) {
    ENTER;
    void* result = bugvmCopyMemory(env, src, size);
    LEAVE(result);
}

void _bcHookInstrumented(DebugEnv* debugEnv, jint lineNumber, jint lineNumberOffset, jbyte* bptable, void* pc) {
    Env* env = (Env*) debugEnv;
    // Temporarily clear any exception that has been thrown. Could be the case if we are called from a catch block.
    Object* throwable = bugvmExceptionClear(env);
    // We cannot use ENTER/LEAVEV as it will raise any exception thrown in here. We just want the code to 
    // proceed normally after we return.
    bugvmPushGatewayFrame(env);
    bugvmHookInstrumented(debugEnv, lineNumber, lineNumberOffset, bptable, pc);
    bugvmPopGatewayFrame(env);
    // Restore the exception if one had been thrown when this function was called.
    env->throwable = throwable;
}

jint JNI_GetDefaultJavaVMInitArgs(void* vm_args) {
    return JNI_OK;
}

static inline jint startsWith(char* s, char* prefix) {
    return s && strncasecmp(s, prefix, strlen(prefix)) == 0;
}

jint JNI_CreateJavaVM(JavaVM** p_vm, JNIEnv** p_env, void* pvm_args) {
    initOptions();

    JavaVMInitArgs* vmArgs = (JavaVMInitArgs*) pvm_args;
    if (vmArgs) {
        for (int i = 0; i < vmArgs->nOptions; i++) {
            JavaVMOption* opt = &vmArgs->options[i];
            if (startsWith(opt->optionString, "-bugvm:")) {
                char* arg = &opt->optionString[5];
                bugvmParseOption(arg, &options);
            } else if (startsWith(opt->optionString, "-X")) {
                char* arg = &opt->optionString[2];
                bugvmParseOption(arg, &options);
            } else if (startsWith(opt->optionString, "-D")) {
                char* arg = &opt->optionString[1];
                bugvmParseOption(arg, &options);
            } else if (startsWith(opt->optionString, "-verbose")) {
                bugvmParseOption("log=trace", &options);
            }
        }
    }

    if (!bugvmInitOptions(0, NULL, &options, FALSE)) {
        return JNI_ERR;
    }

    // Start up bugvm (JNI)
    Env* env = bugvmStartup(&options);
    if (!env) {
        return JNI_ERR;
    }

    vm = env->vm;

    // Return values.
    if (p_vm) {
        *p_vm = &vm->javaVM;
    }
    if (p_env) {
        *p_env = &env->jni;
    }

    return JNI_OK;
}

jint JNI_GetCreatedJavaVMs(JavaVM** vmBuf, jsize bufLen, jsize* nVMs) {
    int numVms = (vm) ? 1 : 0;
    numVms = (bufLen < 1) ? bufLen : 1;
    if ((NULL == vmBuf) || (NULL == vm)) {
        return JNI_ERR;
    }
    if (bufLen >= 1) {
        *vmBuf = &vm->javaVM;
    }
    if (NULL != nVMs) {
        *nVMs = numVms;
    }
    return JNI_OK;
}
