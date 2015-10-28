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
package com.bugvm.apple.scenekit;

/*<imports>*/

import com.bugvm.apple.coreanimation.CAMediaTimingFunction;
import com.bugvm.apple.foundation.NSObject;
import com.bugvm.objc.ObjCRuntime;
import com.bugvm.objc.annotation.Block;
import com.bugvm.objc.annotation.Method;
import com.bugvm.objc.annotation.NativeClass;
import com.bugvm.objc.*;
import com.bugvm.rt.*;
import com.bugvm.rt.annotation.*;
import com.bugvm.rt.bro.*;
import com.bugvm.rt.bro.annotation.*;
import com.bugvm.rt.bro.ptr.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 8.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("SceneKit") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/SCNTransaction/*</name>*/ 
    extends /*<extends>*/NSObject/*</extends>*/
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class SCNTransactionPtr extends Ptr<SCNTransaction, SCNTransactionPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(SCNTransaction.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public SCNTransaction() {}
    protected SCNTransaction(SkipInit skipInit) { super(skipInit); }
    /*</constructors>*/
    /*<properties>*/
    
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @Method(selector = "begin")
    public static native void begin();
    @Method(selector = "commit")
    public static native void commit();
    @Method(selector = "flush")
    public static native void flush();
    @Method(selector = "lock")
    public static native void lock();
    @Method(selector = "unlock")
    public static native void unlock();
    @Method(selector = "animationDuration")
    public static native double getAnimationDuration();
    @Method(selector = "setAnimationDuration:")
    public static native void setAnimationDuration(double duration);
    @Method(selector = "animationTimingFunction")
    public static native CAMediaTimingFunction getAnimationTimingFunction();
    @Method(selector = "setAnimationTimingFunction:")
    public static native void setAnimationTimingFunction(CAMediaTimingFunction function);
    @Method(selector = "disableActions")
    public static native boolean areActionsDisabled();
    @Method(selector = "setDisableActions:")
    public static native void setActionsDisabled(boolean flag);
    @Method(selector = "completionBlock")
    public static native @Block
    Runnable getCompletionBlock();
    @Method(selector = "setCompletionBlock:")
    public static native void setCompletionBlock(@Block Runnable block);
    @Method(selector = "valueForKey:")
    public static native NSObject getValue(String key);
    @Method(selector = "setValue:forKey:")
    public static native void setValue(NSObject anObject, String key);
    /*</methods>*/
}