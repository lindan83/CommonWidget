package com.lance.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lance.common.widget.ImageBannerLayout;

import java.util.ArrayList;
import java.util.List;

public class ImageBannerActivity extends AppCompatActivity {
    private static final String TAG = "ImageBannerActivity";

    private ImageBannerLayout banner1, banner2, banner3, banner4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_banner);

        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("http://pic2.ooopic.com/10/55/23/14b1OOOPIC34.jpg");
        imageUrls.add("http://pic2.ooopic.com/01/15/67/57b1OOOPICe7.jpg");
        imageUrls.add("http://pic.58pic.com/58pic/17/68/29/55787d04688ef_1024.jpg");
        imageUrls.add("http://pic2.ooopic.com/12/56/62/72b1OOOPICf3.jpg");

        banner1 = (ImageBannerLayout) findViewById(R.id.banner_1);
        banner1.setImageUrls(imageUrls);
        banner1.setSelectedPosition(2);
        banner1.setOnImageSwitchListener(new ImageBannerLayout.OnImageSwitchListener() {
            @Override
            public void onSelected(int position) {
                Log.d(TAG, "onSelected: position=" + position);
            }

            @Override
            public void onScrolled(int scrollX, int scrollY) {
                //Log.d(TAG, "onScrolled: scrollX=" + scrollX + ", scrollY=" + scrollY);
            }
        });

        banner2 = (ImageBannerLayout) findViewById(R.id.banner_2);
        banner2.setImageUrls(imageUrls);

        banner3 = (ImageBannerLayout) findViewById(R.id.banner_3);
        banner3.setImageUrls(imageUrls);

        banner4 = (ImageBannerLayout) findViewById(R.id.banner_4);
        banner4.setImageUrls(imageUrls);
    }
}
