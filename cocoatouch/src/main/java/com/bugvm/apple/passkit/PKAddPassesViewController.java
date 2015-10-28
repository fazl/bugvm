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
package com.bugvm.apple.passkit;

/*<imports>*/

import com.bugvm.apple.foundation.NSArray;
import com.bugvm.apple.uikit.UIViewController;
import com.bugvm.objc.ObjCRuntime;
import com.bugvm.objc.annotation.Method;
import com.bugvm.objc.annotation.NativeClass;
import com.bugvm.objc.annotation.Property;
import com.bugvm.objc.*;
import com.bugvm.rt.*;
import com.bugvm.rt.annotation.*;
import com.bugvm.rt.bro.*;
import com.bugvm.rt.bro.annotation.*;
import com.bugvm.rt.bro.ptr.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 6.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("PassKit") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/PKAddPassesViewController/*</name>*/ 
    extends /*<extends>*/UIViewController/*</extends>*/
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class PKAddPassesViewControllerPtr extends Ptr<PKAddPassesViewController, PKAddPassesViewControllerPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(PKAddPassesViewController.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public PKAddPassesViewController() {}
    protected PKAddPassesViewController(SkipInit skipInit) { super(skipInit); }
    public PKAddPassesViewController(PKPass pass) { super((SkipInit) null); initObject(init(pass)); }
    /**
     * @since Available in iOS 7.0 and later.
     */
    public PKAddPassesViewController(NSArray<PKPass> passes) { super((SkipInit) null); initObject(init(passes)); }
    /*</constructors>*/
    /*<properties>*/
    @Property(selector = "delegate")
    public native PKAddPassesViewControllerDelegate getDelegate();
    @Property(selector = "setDelegate:", strongRef = true)
    public native void setDelegate(PKAddPassesViewControllerDelegate v);
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @Method(selector = "initWithPass:")
    protected native @Pointer long init(PKPass pass);
    /**
     * @since Available in iOS 7.0 and later.
     */
    @Method(selector = "initWithPasses:")
    protected native @Pointer long init(NSArray<PKPass> passes);
    /**
     * @since Available in iOS 8.0 and later.
     */
    @Method(selector = "canAddPasses")
    public static native boolean canAddPasses();
    /*</methods>*/
}