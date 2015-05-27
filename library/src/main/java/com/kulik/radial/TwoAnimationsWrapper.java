/*
        Copyright 2015 Yevgen Kulik

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.kulik.radial;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * User: kulik
 * Date: 7/3/13
 * Time: 5:03 PM
 */
public class TwoAnimationsWrapper {
    public static final int ANIMATION_DELAY = 200;

    public final ObjectAnimator objectAnimator = new ObjectAnimator();
    public final ObjectAnimator delimiterAnimator = new ObjectAnimator();

    public TwoAnimationsWrapper() {
        Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.setTarget(null);
            }
        };
        objectAnimator.setPropertyName("rotation");
        objectAnimator.addListener(listener);
        objectAnimator.setDuration(ANIMATION_DELAY);

        delimiterAnimator.setPropertyName("rotation");
        delimiterAnimator.addListener(listener);
        delimiterAnimator.setDuration(ANIMATION_DELAY);
    }

    public Object getDelimiter() {
        return delimiterAnimator.getTarget();
    }

    public Object getMain() {
        return objectAnimator.getTarget();
    }
}