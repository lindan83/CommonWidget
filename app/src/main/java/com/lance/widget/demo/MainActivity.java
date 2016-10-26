package com.lance.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.TopBar;

public class MainActivity extends AppCompatActivity {
    private TopBar mTopBarView1, mTopBarView2, mTopBarView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTopBarView1 = (TopBar) findViewById(R.id.top_bar_1);
        mTopBarView2 = (TopBar) findViewById(R.id.top_bar_2);
        mTopBarView3 = (TopBar) findViewById(R.id.top_bar_3);

        mTopBarView1.setOnClickTopBarListener(new TopBar.OnClickListener() {
            @Override
            public void onClickLeft() {
                ToastUtil.showShort(MainActivity.this, "click left");
            }

            @Override
            public void onClickTitle() {
                ToastUtil.showShort(MainActivity.this, "click title");
            }

            @Override
            public void onClickRight() {
                ToastUtil.showShort(MainActivity.this, "click right");
            }
        });

        mTopBarView2.setOnClickTopBarListener(new TopBar.OnClickListener() {
            @Override
            public void onClickLeft() {
                ToastUtil.showShort(MainActivity.this, "click left");
            }

            @Override
            public void onClickTitle() {
                ToastUtil.showShort(MainActivity.this, "click title");
            }

            @Override
            public void onClickRight() {
                ToastUtil.showShort(MainActivity.this, "click right");
            }
        });

        mTopBarView3.setOnClickTopBarListener(new TopBar.OnClickListener() {
            @Override
            public void onClickLeft() {
                ToastUtil.showShort(MainActivity.this, "click left");
            }

            @Override
            public void onClickTitle() {
                ToastUtil.showShort(MainActivity.this, "click title");
            }

            @Override
            public void onClickRight() {
                ToastUtil.showShort(MainActivity.this, "click right");
            }
        });
    }
}
