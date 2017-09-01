package com.lance.common.widget.photoview.gestures;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class FroyoGestureDetector extends EclairGestureDetector {
    protected final ScaleGestureDetector detector;

    public FroyoGestureDetector(Context context) {
        super(context);
        ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }
                listener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        };
        detector = new ScaleGestureDetector(context, mScaleListener);
    }

    @Override
    public boolean isScaling() {
        return detector.isInProgress();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            detector.onTouchEvent(ev);
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // Fix for support lib bug, happening when onDestroy is
            return true;
        }
    }
}