package com.lance.common.widget.photoview.scrollerproxy;

import android.content.Context;
import android.widget.OverScroller;

public class GingerScroller extends ScrollerProxy {
    protected final OverScroller scroller;

    public GingerScroller(Context context) {
        scroller = new OverScroller(context);
    }

    @Override
    public boolean computeScrollOffset() {
        return scroller.computeScrollOffset();
    }

    @Override
    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY,
                      int overX, int overY) {
        scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
    }

    @Override
    public void forceFinished(boolean finished) {
        scroller.forceFinished(finished);
    }

    @Override
    public boolean isFinished() {
        return scroller.isFinished();
    }

    @Override
    public int getCurrX() {
        return scroller.getCurrX();
    }

    @Override
    public int getCurrY() {
        return scroller.getCurrY();
    }
}