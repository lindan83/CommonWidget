package com.lance.common.widget.template;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by lindan on 17-3-30.
 * Activity模板基类
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private SparseArray<View> views;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = new SparseArray<>();
        setContentView(getLayoutId());
        initViews();
        initEventListeners();
        initData();
    }

    @Override
    public void onClick(View v) {
        processClick(v);
    }

    /**
     * 根据View id获取View
     *
     * @param id  View id
     * @param <V> View Type
     * @return View object
     */
    public <V extends View> V getView(int id) {
        V view = (V) views.get(id);
        if (view == null) {
            view = (V) findViewById(id);
            views.put(id, view);
        }
        return view;
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
}
