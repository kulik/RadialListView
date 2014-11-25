package com.kulik.radial;

import android.view.MotionEvent;
import android.view.View;

/**
 * User: kulik
 * Date: 6/6/13
 * Time: 10:34 PM
 */
public class ScrollStateMachine implements View.OnTouchListener{

    enum State {
        STARTED,
        STOPPED
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }
}