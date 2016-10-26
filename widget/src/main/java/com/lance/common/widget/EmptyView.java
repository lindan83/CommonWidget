package com.lance.common.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.FrameLayout;

/**
 * Created by lindan on 16-7-18.
 * <p>
 * 可用于ListView或GridView的EmptyView
 */
public class EmptyView extends FrameLayout {
    private Integer mLayoutResId;
    private View mEmptyView;

    public EmptyView(Context context, int layoutResourceId) {
        super(context);
        mLayoutResId = layoutResourceId;
        initView();
    }

    public EmptyView(Context context, View emptyView) {
        super(context);
        mEmptyView = emptyView;
        initView();
    }

    private void initView() {
        if (mLayoutResId == null && mEmptyView == null) {
            return;
        }
        if (mLayoutResId != null) {
            View v = LayoutInflater.from(getContext()).inflate(mLayoutResId, null);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            v.setLayoutParams(lp);
            addView(v);
        } else {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mEmptyView.setLayoutParams(lp);
            addView(mEmptyView);
        }
    }

    public void setTargetView(AdapterView targetView) {
        ViewParent viewParent = targetView.getParent();
        if (viewParent != null && viewParent instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) viewParent;
            parent.addView(this);
            targetView.setEmptyView(this);
        }
    }
}
