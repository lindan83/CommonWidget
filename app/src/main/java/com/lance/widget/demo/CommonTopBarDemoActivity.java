package com.lance.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.TopBar;

public class CommonTopBarDemoActivity extends AppCompatActivity {
    private TopBar mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_top_bar_demo);

        mTopBar = (TopBar) findViewById(R.id.top_bar);

        if (mTopBar != null) {
            mTopBar.setOnClickTopBarListener(new TopBar.OnClickListener() {
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
}
