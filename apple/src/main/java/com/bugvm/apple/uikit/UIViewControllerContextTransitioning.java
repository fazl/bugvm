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
package com.bugvm.apple.uikit;

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
import com.bugvm.apple.coreanimation.*;
import com.bugvm.apple.coregraphics.*;
import com.bugvm.apple.coredata.*;
import com.bugvm.apple.coreimage.*;
import com.bugvm.apple.coretext.*;
import com.bugvm.apple.corelocation.*;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*//*</annotations>*/
/*<visibility>*/public/*</visibility>*/ interface /*<name>*/UIViewControllerContextTransitioning/*</name>*/ 
    /*<implements>*/extends NSObjectProtocol/*</implements>*/ {

    /*<ptr>*/
    /*</ptr>*/
    /*<bind>*/
    /*</bind>*/
    /*<constants>*//*</constants>*/
    /*<properties>*/
    
    /*</properties>*/
    /*<methods>*/
    @Method(selector = "containerView")
    UIView getContainerView();
    @Method(selector = "isAnimated")
    boolean isAnimated();
    @Method(selector = "isInteractive")
    boolean isInteractive();
    @Method(selector = "transitionWasCancelled")
    boolean transitionWasCancelled();
    @Method(selector = "presentationStyle")
    UIModalPresentationStyle getPresentationStyle();
    @Method(selector = "updateInteractiveTransition:")
    void updateInteractiveTransition(@MachineSizedFloat double percentComplete);
    @Method(selector = "finishInteractiveTransition")
    void finishInteractiveTransition();
    @Method(selector = "cancelInteractiveTransition")
    void cancelInteractiveTransition();
    @Method(selector = "completeTransition:")
    void completeTransition(boolean didComplete);
    @Method(selector = "viewControllerForKey:")
    UIViewController getViewController(UITransitionContextViewControllerType key);
    /**
     * @since Available in iOS 8.0 and later.
     */
    @Method(selector = "viewForKey:")
    UIView getView(UITransitionContextViewType key);
    /**
     * @since Available in iOS 8.0 and later.
     */
    @Method(selector = "targetTransform")
    @ByVal CGAffineTransform getTargetTransform();
    @Method(selector = "initialFrameForViewController:")
    @ByVal CGRect getInitialFrame(UIViewController vc);
    @Method(selector = "finalFrameForViewController:")
    @ByVal CGRect getFinalFrame(UIViewController vc);
    /*</methods>*/
    /*<adapter>*/
    /*</adapter>*/
}