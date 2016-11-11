package com.lance.common.widget.photoview.gestures;

import android.view.MotionEvent;

public interface GestureDetector {
    boolean onTouchEvent(MotionEvent ev);

    boolean isScaling();

    boolean isDragging();

    void setOnGestureListener(OnGestureListener listener);
}
