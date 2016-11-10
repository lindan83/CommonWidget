package com.lance.widget.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showDemo(View v) {
        switch (v.getId()) {
            case R.id.btn_common_top_bar_demo:
                startActivity(new Intent(this, CommonTopBarDemoActivity.class));
                break;
            case R.id.btn_custom_top_bar_demo:
                startActivity(new Intent(this, CustomTopBarDemoActivity.class));
                break;
            case R.id.btn_common_option_view_demo:
                startActivity(new Intent(this, CommonOptionViewDemoActivity.class));
                break;
            case R.id.btn_multi_image_view_demo:
                startActivity(new Intent(this, MultiImageViewDemoActivity.class));
                break;
        }
    }
}
