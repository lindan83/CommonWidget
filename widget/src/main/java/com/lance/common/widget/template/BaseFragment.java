package com.lance.common.widget.template;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lindan on 17-3-30.
 * Fragment模板基类
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private boolean isVisible;
    private boolean isInit;
    private boolean isFirstLoad = true;
    private SparseArray<View> views;
    private View convertView;

    /**
     * 获取布局资源ID
     *
     * @return Layout resource id
     */
    public abstract int getLayoutId();

    /**
     * 初始化Views
     */
    public abstract void initViews();

    /**
     * 初始化事件监听对象
     */
    public abstract void initEventListeners();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 处理单击事件
     */
    public abstract void processClick(View v);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        views = new SparseArray<>();
        convertView = inflater.inflate(getLayoutId(), container, false);
        initViews();
        isInit = true;
        lazyLoad();
        return convertView;
    }

    private void lazyLoad() {
        if (!isFirstLoad || !isVisible || !isInit) {
            return;
        }
        initEventListeners();
        initData();
        isFirstLoad = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }
    }

    /**
     * 根据View id获取View
     *
     * @param id  View id
     * @param <V> View Type
     * @return View object
     */
    public <V extends View> V getView(int id) {
        if (convertView != null) {
            V view = (V) views.get(id);
            if (view == null) {
                view = (V) convertView.findViewById(id);
                views.put(id, view);
            }
            return view;
        }
        return null;
    }

    /**
     * 为View设置点击事件
     *
     * @param view View
     * @param <V>  View Type
     */
    public <V extends View> void setOnClick(V view) {
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        processClick(v);
    }
}
