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
package com.bugvm.apple.webkit;

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
import com.bugvm.apple.uikit.*;
import com.bugvm.apple.coregraphics.*;
import com.bugvm.apple.security.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 8.0 and later.
 */
/*</javadoc>*/
@ForceLinkClass(WKError.class)
/*<annotations>*/@Marshaler(ValuedEnum.AsMachineSizedSIntMarshaler.class)/*</annotations>*/
public enum /*<name>*/WKErrorCode/*</name>*/ implements NSErrorCode {
    /*<values>*/
    Unknown(1L),
    WebContentProcessTerminated(2L),
    WebViewInvalidated(3L),
    JavaScriptExceptionOccurred(4L),
    /**
     * @since Available in iOS 9.0 and later.
     */
    JavaScriptResultTypeIsUnsupported(5L);
    /*</values>*/

    /*<bind>*/
    /*</bind>*/
    /*<constants>*//*</constants>*/
    /*<methods>*//*</methods>*/

    private final long n;

    private /*<name>*/WKErrorCode/*</name>*/(long n) { this.n = n; }
    public long value() { return n; }
    public static /*<name>*/WKErrorCode/*</name>*/ valueOf(long n) {
        for (/*<name>*/WKErrorCode/*</name>*/ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException("No constant with value " + n + " found in " 
            + /*<name>*/WKErrorCode/*</name>*/.class.getName());
    }
}
