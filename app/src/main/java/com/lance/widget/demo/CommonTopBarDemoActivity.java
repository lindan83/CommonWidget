package com.lance.widget.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.TopBar;

public class CommonTopBarDemoActivity extends AppCompatActivity {
    private TopBar mTopBarView1, mTopBarView2, mTopBarView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_top_bar_demo);

        mTopBarView1 = (TopBar) findViewById(R.id.top_bar_1);
        mTopBarView2 = (TopBar) findViewById(R.id.top_bar_2);
        mTopBarView3 = (TopBar) findViewById(R.id.top_bar_3);

        mTopBarView1.setOnClickTopBarListener(new TopBar.OnClickListener() {
            @Override
            public void onClickLeft() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click left");
            }

            @Override
            public void onClickTitle() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click title");
            }

            @Override
            public void onClickRight() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click right");
            }
        });

        mTopBarView2.setOnClickTopBarListener(new TopBar.OnClickListener() {
            @Override
            public void onClickLeft() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click left");
            }

            @Override
            public void onClickTitle() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click title");
            }

            @Override
            public void onClickRight() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click right");
            }
        });

        mTopBarView3.setOnClickTopBarListener(new TopBar.OnClickListener() {
            @Override
            public void onClickLeft() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click left");
            }

            @Override
            public void onClickTitle() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click title");
            }

            @Override
            public void onClickRight() {
                ToastUtil.showShort(CommonTopBarDemoActivity.this, "click right");
            }
        });
    }
}
