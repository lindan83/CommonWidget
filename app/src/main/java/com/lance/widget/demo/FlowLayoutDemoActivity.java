package com.lance.widget.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.flowlayout.FlowLayout;
import com.lance.common.widget.flowlayout.TagFlowLayout;
import com.lance.common.widget.flowlayout.adapter.TagAdapter;

import java.util.Set;

public class FlowLayoutDemoActivity extends AppCompatActivity {
    private String[] countries = {"中国", "美国", "俄罗斯", "英国", "法国", "德国", "加拿大", "印度", "巴基斯坦", "意大利"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout_demo);

        initViews();
    }

    private void initViews() {
        FlowLayout flowLayout = (FlowLayout) findViewById(R.id.fl_tag_container);
        for (String country : countries) {
            TextView tv = new TextView(this);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(14);
            tv.setText(country);
            tv.setBackgroundResource(R.drawable.bg_tag);
            flowLayout.addView(tv);
        }

        TagFlowLayout tagFlowLayout = (TagFlowLayout) findViewById(R.id.tfl_container);
        final TagAdapter<String> adapter = new TagAdapter<String>(countries) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = new TextView(FlowLayoutDemoActivity.this);
                tv.setTextColor(Color.YELLOW);
                tv.setTextSize(14);
                tv.setText(s);
                tv.setBackgroundResource(R.drawable.bg_tag_selector);
                return tv;
            }
        };
        tagFlowLayout.setAdapter(adapter);
        tagFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                ToastUtil.showShort(FlowLayoutDemoActivity.this, "onSelect:" + selectPosSet.toString());
            }
        });
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                ToastUtil.showShort(FlowLayoutDemoActivity.this, String.valueOf(position));
                return true;
            }
        });
    }
}
