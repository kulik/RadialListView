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