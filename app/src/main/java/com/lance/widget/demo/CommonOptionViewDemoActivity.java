package com.lance.widget.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lance.common.util.ToastUtil;

public class CommonOptionViewDemoActivity extends AppCompatActivity {
    private OptionView mOptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_option_view_demo);

        mOptionView = (OptionView) findViewById(R.id.option_view);

        mOptionView.setText("修改后的文字修改后的文字修改后的文字修改后的文字修改后的文字");
        mOptionView.setTextColor(Color.RED);
        mOptionView.setTextSize(18);
        mOptionView.setLeftIcon(getResources().getDrawable(R.mipmap.icon_wodehangye));
    }

    public void showDemo(View v) {
        switch (v.getId()) {
            case R.id.option_view:
                ToastUtil.showShort(this, String.valueOf(mOptionView.getId()));
                break;
        }
    }
}
