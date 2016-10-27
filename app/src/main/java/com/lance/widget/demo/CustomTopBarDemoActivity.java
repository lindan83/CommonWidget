package com.lance.widget.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.TopBar;

public class CustomTopBarDemoActivity extends AppCompatActivity {
    private TopBar mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_top_bar_demo);

        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setOnClickTopBarListener(new TopBar.OnClickListener() {
            @Override
            public void onClickLeft() {
                ToastUtil.showShort(CustomTopBarDemoActivity.this, "click left");
            }

            @Override
            public void onClickTitle() {
                ToastUtil.showShort(CustomTopBarDemoActivity.this, "click title");
            }

            @Override
            public void onClickRight() {
                ToastUtil.showShort(CustomTopBarDemoActivity.this, "click right");
            }
        });
    }
}
