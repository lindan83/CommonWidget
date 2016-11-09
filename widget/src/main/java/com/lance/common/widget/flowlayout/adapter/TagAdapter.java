package com.lance.common.widget.flowlayout.adapter;

import android.view.View;

import com.lance.common.widget.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lindan on 16-11-9.
 * <p>
 * TagFlowLayout的适配器
 */

public abstract class TagAdapter<T> {
    private List<T> mTagData;
    private OnDataChangedListener mOnDataChangedListener;
    private HashSet<Integer> mCheckedPosList = new HashSet<>();

    public interface OnDataChangedListener {
        void onChanged();
    }

    public TagAdapter(List<T> data) {
        mTagData = data;
    }

    public TagAdapter(T[] data) {
        mTagData = new ArrayList<>(Arrays.asList(data));
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }

    public void setSelectedList(int... positions) {
        Set<Integer> set = new HashSet<>();
        for (int pos : positions) {
            set.add(pos);
        }
        setSelectedList(set);
    }

    public void setSelectedList(Set<Integer> set) {
        mCheckedPosList.clear();
        if (set != null) {
            mCheckedPosList.addAll(set);
        }
        notifyDataChanged();
    }

    public HashSet<Integer> getPreCheckedList() {
        return mCheckedPosList;
    }

    public int getCount() {
        return mTagData == null ? 0 : mTagData.size();
    }

    public void notifyDataChanged() {
        mOnDataChangedListener.onChanged();
    }

    public T getItem(int position) {
        return mTagData.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

    public boolean setSelected(int position, T t) {
        return false;
    }
}