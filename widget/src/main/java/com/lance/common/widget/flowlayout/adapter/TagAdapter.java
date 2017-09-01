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
 * TagFlowLayout的适配器
 */

public abstract class TagAdapter<T> {
    private List<T> tagData;
    private OnDataChangedListener onDataChangedListener;
    private HashSet<Integer> checkedPosList = new HashSet<>();

    public interface OnDataChangedListener {
        void onChanged();
    }

    public TagAdapter(List<T> data) {
        tagData = data;
    }

    public TagAdapter(T[] data) {
        tagData = new ArrayList<>(Arrays.asList(data));
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        onDataChangedListener = listener;
    }

    public void setSelectedList(int... positions) {
        Set<Integer> set = new HashSet<>();
        for (int pos : positions) {
            set.add(pos);
        }
        setSelectedList(set);
    }

    public void setSelectedList(Set<Integer> set) {
        checkedPosList.clear();
        if (set != null) {
            checkedPosList.addAll(set);
        }
        notifyDataChanged();
    }

    public HashSet<Integer> getPreCheckedList() {
        return checkedPosList;
    }

    public int getCount() {
        return tagData == null ? 0 : tagData.size();
    }

    public void notifyDataChanged() {
        onDataChangedListener.onChanged();
    }

    public T getItem(int position) {
        return tagData.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

    public boolean setSelected(int position, T t) {
        return false;
    }
}