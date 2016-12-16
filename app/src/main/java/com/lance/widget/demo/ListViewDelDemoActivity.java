package com.lance.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.lance.common.widget.swipelayout.SwipeLayout;
import com.lance.widget.demo.adapter.CommonAdapter;
import com.lance.widget.demo.adapter.ViewHolder;
import com.lance.widget.demo.bean.SwipeBean;

import java.util.ArrayList;
import java.util.List;

public class ListViewDelDemoActivity extends AppCompatActivity {
    private ListView mLv;
    private List<SwipeBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mLv = (ListView) findViewById(R.id.test);

        initData();
        mLv.setAdapter(new CommonAdapter<SwipeBean>(this, mData, R.layout./*item_swipe_menu*/item_cst_swipe) {
            @Override
            public void convert(final ViewHolder holder, SwipeBean swipeBean, final int position, View convertView) {
                //((CstSwipeDelMenu)holder.getConvertView()).setIos(false);//这句话关掉IOS阻塞式交互效果
                holder.setText(R.id.content, swipeBean.name);
                holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ListViewDelDemoActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
                    }
                });

                holder.setOnClickListener(R.id.btnDelete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ListViewDelDemoActivity.this, "删除:" + position, Toast.LENGTH_SHORT).show();
                        //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                        ((SwipeLayout) holder.getConvertView()).quickClose();
                        mData.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void initData() {
        mData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mData.add(new SwipeBean("" + i));
        }
    }
}