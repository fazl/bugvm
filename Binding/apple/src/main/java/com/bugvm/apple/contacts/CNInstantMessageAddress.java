/*
 * Copyright (C) 2013-2015 RoboVM AB
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
package com.bugvm.apple.contacts;

/*<imports>*/
import java.io.*;
import java.nio.*;
import java.util.*;
import com.bugvm.objc.*;
import com.bugvm.objc.annotation.*;
import com.bugvm.objc.block.*;
import com.bugvm.rt.*;
import com.bugvm.rt.annotation.*;
import com.bugvm.rt.bro.*;
import com.bugvm.rt.bro.annotation.*;
import com.bugvm.rt.bro.ptr.*;
import com.bugvm.apple.foundation.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 9.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("Contacts") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/CNInstantMessageAddress/*</name>*/ 
    extends /*<extends>*/NSObject/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class CNInstantMessageAddressPtr extends Ptr<CNInstantMessageAddress, CNInstantMessageAddressPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(CNInstantMessageAddress.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public CNInstantMessageAddress() {}
    protected CNInstantMessageAddress(SkipInit skipInit) { super(skipInit); }
    public CNInstantMessageAddress(String username, String service) { super((SkipInit) null); initObject(init(username, service)); }
    /*</constructors>*/
    public CNInstantMessageAddress(String username, CNInstantMessageService service) {
        super((SkipInit) null); 
        initObject(init(username, service.value().toString()));
    }
    /*<properties>*/
    @Property(selector = "username")
    public native String getUsername();
    @Property(selector = "service")
    public native String getService();
    /*</properties>*/
    /*<members>*//*</members>*/
    public static String getLocalizedService(CNInstantMessageService service) {
        return getLocalizedService(service.value().toString());
    }
    /*<methods>*/
    @Method(selector = "initWithUsername:service:")
    protected native @Pointer long init(String username, String service);
    @Method(selector = "localizedStringForKey:")
    public static native String getLocalizedProperty(CNInstantMessageAddressPropertyKey key);
    @Method(selector = "localizedStringForService:")
    public static native String getLocalizedService(String service);
    /*</methods>*/
}
