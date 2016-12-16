package com.lance.widget.demo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用ListViewAdapter
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    private static final String TAG = "CommonAdapter";
    protected Context mContext;
    protected List<T> mData;
    protected LayoutInflater mInflater;
    private int layoutId;

    public CommonAdapter(Context context, List<T> data, int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
                layoutId, position);
        Log.d(TAG, "getView() called with: " + "position = [" + position + "], convertView = [" + convertView + "], parent = [" + parent + "]");
        convert(holder, getItem(position), position, convertView);
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T t, int position, View convertView);

    /**
     * 刷新数据，初始化数据
     */
    public void setData(List<T> list) {
        if (this.mData != null) {
            if (null != list) {
                List<T> temp = new ArrayList<>();
                temp.addAll(list);
                this.mData.clear();
                this.mData.addAll(temp);
            } else {
                this.mData.clear();
            }
        } else {
            this.mData = list;
        }
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     *
     * @param i position
     */
    public void remove(int i) {
        if (null != mData && mData.size() > i && i > -1) {
            mData.remove(i);
            notifyDataSetChanged();
        }
    }

    /**
     * 加载更多数据
     *
     * @param list data
     */
    public void addData(List<T> list) {
        if (null != list) {
            List<T> temp = new ArrayList<>();
            temp.addAll(list);
            if (this.mData != null) {
                this.mData.addAll(temp);
            } else {
                this.mData = temp;
            }
            notifyDataSetChanged();
        }
    }

    public List<T> getData() {
        return mData;
    }
}