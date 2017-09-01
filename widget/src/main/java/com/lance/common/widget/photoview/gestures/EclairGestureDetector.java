package com.lance.common.widget.photoview.gestures;

import android.content.Context;
import android.view.MotionEvent;

import com.lance.common.widget.photoview.Compat;

public class EclairGestureDetector extends CupcakeGestureDetector {

    private static final int INVALID_POINTER_ID = -1;
    private int activePointerId = INVALID_POINTER_ID;
    private int activePointerIndex = 0;

    public EclairGestureDetector(Context context) {
        super(context);
    }

    @Override
    float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(activePointerIndex);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    @Override
    float getActiveY(MotionEvent ev) {
        try {
            return ev.getY(activePointerIndex);
        } catch (Exception e) {
            return ev.getY();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // Ignore deprecation, ACTION_POINTER_ID_MASK and
                // ACTION_POINTER_ID_SHIFT has same value and are deprecated
                // You can have either deprecation or lint target api warning
                final int pointerIndex = Compat.getPointerIndex(ev.getAction());
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    activePointerId = ev.getPointerId(newPointerIndex);
                    lastTouchX = ev.getX(newPointerIndex);
                    lastTouchY = ev.getY(newPointerIndex);
                }
                break;
        }

        activePointerIndex = ev
                .findPointerIndex(activePointerId != INVALID_POINTER_ID ? activePointerId
                        : 0);
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // Fix for support lib bug, happening when onDestroy is
            return true;
        }
    }
}
