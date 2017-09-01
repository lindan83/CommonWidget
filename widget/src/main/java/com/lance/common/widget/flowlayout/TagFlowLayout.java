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
import android.view.ViewGroup;

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


    private TagAdapter tagAdapter;
    private boolean autoSelectEffect = true;
    private int selectedMax = -1;//-1为不限制数量
    private MotionEvent motionEvent;
    private OnSelectListener onSelectListener;
    private OnTagClickListener onTagClickListener;

    private Set<Integer> selectedView = new HashSet<>();

    public interface OnSelectListener {
        void onSelected(Set<Integer> selectPosSet);
    }

    public interface OnTagClickListener {
        boolean onTagClick(View view, int position, FlowLayout parent);
    }


    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        autoSelectEffect = ta.getBoolean(R.styleable.TagFlowLayout_autoSelectEffect, true);
        selectedMax = ta.getInt(R.styleable.TagFlowLayout_maxSelect, -1);
        ta.recycle();

        if (autoSelectEffect) {
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
        this.onSelectListener = onSelectListener;
        if (this.onSelectListener != null) {
            setClickable(true);
        }
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
        if (onTagClickListener != null) {
            setClickable(true);
        }
    }


    public void setAdapter(TagAdapter adapter) {
        tagAdapter = adapter;
        tagAdapter.setOnDataChangedListener(this);
        selectedView.clear();
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        TagAdapter adapter = tagAdapter;
        TagView tagViewContainer;
        HashSet preCheckedList = tagAdapter.getPreCheckedList();
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));

            tagViewContainer = new TagView(getContext());
            tagView.setDuplicateParentStateEnabled(true);
            if (tagView.getLayoutParams() != null) {
                tagViewContainer.setLayoutParams(tagView.getLayoutParams());
            } else {
                MarginLayoutParams lp = new MarginLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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

            if (tagAdapter.setSelected(i, adapter.getItem(i))) {
                selectedView.add(i);
                tagViewContainer.setChecked(true);
            }
        }
        selectedView.addAll(preCheckedList);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            motionEvent = MotionEvent.obtain(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        if (motionEvent == null) {
            return super.performClick();
        }

        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        motionEvent = null;

        TagView child = findChild(x, y);
        int pos = findPosByView(child);
        if (child != null) {
            doSelect(child, pos);
            if (onTagClickListener != null) {
                return onTagClickListener.onTagClick(child.getTagView(), pos, this);
            }
        }
        return true;
    }

    public void setMaxSelectCount(int count) {
        if (selectedView.size() > count) {
            selectedView.clear();
        }
        selectedMax = count;
    }

    public Set<Integer> getSelectedList() {
        return new HashSet<>(selectedView);
    }

    private void doSelect(TagView child, int position) {
        if (autoSelectEffect) {
            if (!child.isChecked()) {
                //处理max_select==1的情况
                if (selectedMax == 1 && selectedView.size() == 1) {
                    Iterator<Integer> iterator = selectedView.iterator();
                    Integer preIndex = iterator.next();
                    TagView pre = (TagView) getChildAt(preIndex);
                    pre.setChecked(false);
                    child.setChecked(true);
                    selectedView.remove(preIndex);
                    selectedView.add(position);
                } else {
                    if (selectedMax > 0 && selectedView.size() >= selectedMax) {
                        return;
                    }
                    child.setChecked(true);
                    selectedView.add(position);
                }
            } else {
                child.setChecked(false);
                selectedView.remove(position);
            }
            if (onSelectListener != null) {
                onSelectListener.onSelected(new HashSet<>(selectedView));
            }
        }
    }

    public TagAdapter getAdapter() {
        return tagAdapter;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());

        StringBuilder selectPos = new StringBuilder(10);
        if (selectedView.size() > 0) {
            for (int key : selectedView) {
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
                    selectedView.add(index);
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
        selectedView.clear();
        changeAdapter();
    }
}