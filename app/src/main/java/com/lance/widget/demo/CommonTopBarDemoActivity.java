package com.lance.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.TopBar;

public class CommonTopBarDemoActivity extends AppCompatActivity {
    private TopBar mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_top_bar_demo);

        mTopBar = (TopBar) findViewById(R.id.top_bar);
        //changeLeft(null);

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

    public void changeLeft(View v) {
        mTopBar.setLeftText("");
        mTopBar.setLeftDrawable(getResources().getDrawable(R.mipmap.icon_shezhi));
        //mTopBar.setLeftDrawable(getResources().getDrawable(R.mipmap.icon_shezhi), TopBar.ALIGNMENT_RIGHT_TO_TEXT);
        //mTopBar.setLeftPadding(200);
        mTopBar.setLeftVisible(false);
    }

    public void changeRight(View v) {
        mTopBar.setRightText("搜索");
        //mTopBar.setRightDrawable(getResources().getDrawable(R.mipmap.icon_shezhi));
        mTopBar.setRightDrawable(getResources().getDrawable(R.mipmap.icon_shezhi), TopBar.ALIGNMENT_RIGHT_TO_TEXT);
        //mTopBar.setRightPadding(200);
        mTopBar.setRightVisible(false);
    }
}
