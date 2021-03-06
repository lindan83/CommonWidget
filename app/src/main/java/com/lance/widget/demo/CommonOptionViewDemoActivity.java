package com.lance.widget.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.OptionView;

public class CommonOptionViewDemoActivity extends AppCompatActivity {
    private OptionView mOptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_option_view_demo);

        mOptionView = (OptionView) findViewById(R.id.option_view);

        mOptionView.setTextColor(Color.RED);
        mOptionView.setTextSize(18);
        mOptionView.setLeftIconResId(R.mipmap.icon_wodehangye);
        mOptionView.setText("修改后的文字修改后的文字修改后的文字修改后的文字修改后的文字");
    }

    public void showDemo(View v) {
        switch (v.getId()) {
            case R.id.option_view:
                ToastUtil.showShort(this, String.valueOf(mOptionView.getId()));
                break;
        }
    }
}
