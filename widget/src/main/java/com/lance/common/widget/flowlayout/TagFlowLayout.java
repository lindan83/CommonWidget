package com.lance.common.widget.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lance.common.util.DensityUtil;
import com.lance.common.widget.R;
import com.lance.common.widget.flowlayout.adapter.TagAdapter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lindan on 16-11-9.
 * 支持标签的流式布局
 */

public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {
    private static final String TAG = "TagFlowLayout";

    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";


    private TagAdapter mTagAdapter;
    private boolean mAutoSelectEffect = true;
    private int mSelectedMax = -1;//-1为不限制数量
    private MotionEvent mMotionEvent;
    private OnSelectListener mOnSelectListener;
    private OnTagClickListener mOnTagClickListener;

    private Set<Integer> mSelectedView = new HashSet<>();

    public interface OnSelectListener {
        void onSelected(Set<Integer> selectPosSet);
    }

    public interface OnTagClickListener {
        boolean onTagClick(View view, int position, FlowLayout parent);
    }


    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mAutoSelectEffect = ta.getBoolean(R.styleable.TagFlowLayout_autoSelectEffect, true);
        mSelectedMax = ta.getInt(R.styleable.TagFlowLayout_maxSelect, -1);
        ta.recycle();

        if (mAutoSelectEffect) {
            setClickable(true);
        }
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context) {
        this(context, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TagView tagView = (TagView) getChildAt(i);
            if (tagView.getVisibility() == View.GONE) {
                continue;
            }
            if (tagView.getTagView().getVisibility() == View.GONE) {
                tagView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
        if (mOnSelectListener != null) {
            setClickable(true);
        }
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
        if (onTagClickListener != null) {
            setClickable(true);
        }
    }


    public void setAdapter(TagAdapter adapter) {
        mTagAdapter = adapter;
        mTagAdapter.setOnDataChangedListener(this);
        mSelectedView.clear();
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        TagAdapter adapter = mTagAdapter;
        TagView tagViewContainer;
        HashSet preCheckedList = mTagAdapter.getPreCheckedList();
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));

            tagViewContainer = new TagView(getContext());
            tagView.setDuplicateParentStateEnabled(true);
            if (tagView.getLayoutParams() != null) {
                tagViewContainer.setLayoutParams(tagView.getLayoutParams());
            } else {
                MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.setMargins(DensityUtil.dp2px(getContext(), 5),
                        DensityUtil.dp2px(getContext(), 5),
                        DensityUtil.dp2px(getContext(), 5),
                        DensityUtil.dp2px(getContext(), 5));
                tagViewContainer.setLayoutParams(lp);
            }
            tagViewContainer.addView(tagView);
            addView(tagViewContainer);

            if (preCheckedList.contains(i)) {
                tagViewContainer.setChecked(true);
            }

            if (mTagAdapter.setSelected(i, adapter.getItem(i))) {
                mSelectedView.add(i);
                tagViewContainer.setChecked(true);
            }
        }
        mSelectedView.addAll(preCheckedList);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mMotionEvent = MotionEvent.obtain(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        if (mMotionEvent == null) {
            return super.performClick();
        }

        int x = (int) mMotionEvent.getX();
        int y = (int) mMotionEvent.getY();
        mMotionEvent = null;

        TagView child = findChild(x, y);
        int pos = findPosByView(child);
        if (child != null) {
            doSelect(child, pos);
            if (mOnTagClickListener != null) {
                return mOnTagClickListener.onTagClick(child.getTagView(), pos, this);
            }
        }
        return true;
    }

    public void setMaxSelectCount(int count) {
        if (mSelectedView.size() > count) {
            mSelectedView.clear();
        }
        mSelectedMax = count;
    }

    public Set<Integer> getSelectedList() {
        return new HashSet<>(mSelectedView);
    }

    private void doSelect(TagView child, int position) {
        if (mAutoSelectEffect) {
            if (!child.isChecked()) {
                //处理max_select==1的情况
                if (mSelectedMax == 1 && mSelectedView.size() == 1) {
                    Iterator<Integer> iterator = mSelectedView.iterator();
                    Integer preIndex = iterator.next();
                    TagView pre = (TagView) getChildAt(preIndex);
                    pre.setChecked(false);
                    child.setChecked(true);
                    mSelectedView.remove(preIndex);
                    mSelectedView.add(position);
                } else {
                    if (mSelectedMax > 0 && mSelectedView.size() >= mSelectedMax) {
                        return;
                    }
                    child.setChecked(true);
                    mSelectedView.add(position);
                }
            } else {
                child.setChecked(false);
                mSelectedView.remove(position);
            }
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelected(new HashSet<>(mSelectedView));
            }
        }
    }

    public TagAdapter getAdapter() {
        return mTagAdapter;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());

        StringBuilder selectPos = new StringBuilder(10);
        if (mSelectedView.size() > 0) {
            for (int key : mSelectedView) {
                selectPos.append(key).append("|");
            }
            selectPos.deleteCharAt(selectPos.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos.toString());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String selectPos = bundle.getString(KEY_CHOOSE_POS);
            if (!TextUtils.isEmpty(selectPos)) {
                String[] split = selectPos.split("\\|");
                for (String pos : split) {
                    int index = Integer.parseInt(pos);
                    mSelectedView.add(index);
                    TagView tagView = (TagView) getChildAt(index);
                    if (tagView != null) {
                        tagView.setChecked(true);
                    }
                }
            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private int findPosByView(View child) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);
            if (v == child) {
                return i;
            }
        }
        return -1;
    }

    private TagView findChild(int x, int y) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TagView v = (TagView) getChildAt(i);
            if (v.getVisibility() == View.GONE) {
                continue;
            }
            Rect outRect = new Rect();
            v.getHitRect(outRect);
            if (outRect.contains(x, y)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void onChanged() {
        mSelectedView.clear();
        changeAdapter();
    }
}