package com.lance.common.widget.photoview.gestures;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class CupcakeGestureDetector implements GestureDetector {
    private static final String TAG = "CupcakeGestureDetector";

    protected OnGestureListener listener;
    float lastTouchX;
    float lastTouchY;
    private final float touchSlop;
    private final float minimumVelocity;

    @Override
    public void setOnGestureListener(OnGestureListener listener) {
        this.listener = listener;
    }

    public CupcakeGestureDetector(Context context) {
        final ViewConfiguration configuration = ViewConfiguration
                .get(context);
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        touchSlop = configuration.getScaledTouchSlop();
    }

    private VelocityTracker velocityTracker;
    private boolean isDragging;

    float getActiveX(MotionEvent ev) {
        return ev.getX();
    }

    float getActiveY(MotionEvent ev) {
        return ev.getY();
    }

    @Override
    public boolean isScaling() {
        return false;
    }

    @Override
    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                velocityTracker = VelocityTracker.obtain();
                if (null != velocityTracker) {
                    velocityTracker.addMovement(ev);
                }

                lastTouchX = getActiveX(ev);
                lastTouchY = getActiveY(ev);
                isDragging = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = getActiveX(ev);
                final float y = getActiveY(ev);
                final float dx = x - lastTouchX, dy = y - lastTouchY;

                if (!isDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    isDragging = Math.sqrt((dx * dx) + (dy * dy)) >= touchSlop;
                }

                if (isDragging) {
                    listener.onDrag(dx, dy);
                    lastTouchX = x;
                    lastTouchY = y;

                    if (null != velocityTracker) {
                        velocityTracker.addMovement(ev);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                // Recycle Velocity Tracker
                if (null != velocityTracker) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (isDragging) {
                    if (null != velocityTracker) {
                        lastTouchX = getActiveX(ev);
                        lastTouchY = getActiveY(ev);

                        // Compute velocity within the last 1000ms
                        velocityTracker.addMovement(ev);
                        velocityTracker.computeCurrentVelocity(1000);

                        final float vX = velocityTracker.getXVelocity(), vY = velocityTracker
                                .getYVelocity();

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= minimumVelocity) {
                            listener.onFling(lastTouchX, lastTouchY, -vX,
                                    -vY);
                        }
                    }
                }

                // Recycle Velocity Tracker
                if (null != velocityTracker) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
            }
        }
        return true;
    }
}