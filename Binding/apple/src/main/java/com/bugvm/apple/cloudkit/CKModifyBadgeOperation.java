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
package com.bugvm.apple.cloudkit;

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
import com.bugvm.apple.corelocation.*;
import com.bugvm.apple.contacts.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 8.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("CloudKit") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/CKModifyBadgeOperation/*</name>*/ 
    extends /*<extends>*/CKOperation/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class CKModifyBadgeOperationPtr extends Ptr<CKModifyBadgeOperation, CKModifyBadgeOperationPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(CKModifyBadgeOperation.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public CKModifyBadgeOperation() {}
    protected CKModifyBadgeOperation(SkipInit skipInit) { super(skipInit); }
    public CKModifyBadgeOperation(@MachineSizedUInt long badgeValue) { super((SkipInit) null); initObject(init(badgeValue)); }
    /*</constructors>*/
    /*<properties>*/
    @Property(selector = "badgeValue")
    public native @MachineSizedUInt long getBadgeValue();
    @Property(selector = "setBadgeValue:")
    public native void setBadgeValue(@MachineSizedUInt long v);
    @Property(selector = "modifyBadgeCompletionBlock")
    public native @Block VoidBlock1<NSError> getModifyBadgeCompletionBlock();
    @Property(selector = "setModifyBadgeCompletionBlock:")
    public native void setModifyBadgeCompletionBlock(@Block VoidBlock1<NSError> v);
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @Method(selector = "initWithBadgeValue:")
    protected native @Pointer long init(@MachineSizedUInt long badgeValue);
    /*</methods>*/
}
