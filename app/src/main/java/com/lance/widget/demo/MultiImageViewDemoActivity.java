package com.lance.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lance.common.widget.MultiImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiImageViewDemoActivity extends AppCompatActivity {
    private MultiImageView mIvMulti, mIvSingle;
    private final String[] mPhotos = {
            "http://f.hiphotos.baidu.com/image/pic/item/faf2b2119313b07e97f760d908d7912396dd8c9c.jpg",
            "http://g.hiphotos.baidu.com/image/pic/item/4b90f603738da977c76ab6fab451f8198718e39e.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/902397dda144ad343de8b756d4a20cf430ad858f.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/a6efce1b9d16fdfa0fbc1ebfb68f8c5495ee7b8b.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134e61e0f90211f95cad1c85e36.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/7dd98d1001e939011b9c86d07fec54e737d19645.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/f11f3a292df5e0fecc3e83ef586034a85edf723d.jpg",
            "http://cdn.duitang.com/uploads/item/201309/17/20130917111400_CNmTr.thumb.224_0.png",
            "http://pica.nipic.com/2007-10-17/20071017111345564_2.jpg",
            "http://pic4.nipic.com/20091101/3672704_160309066949_2.jpg",
            "http://pic4.nipic.com/20091203/1295091_123813163959_2.jpg",
            "http://pic31.nipic.com/20130624/8821914_104949466000_2.jpg",
            "http://pic6.nipic.com/20100330/4592428_113348099353_2.jpg",
            "http://pic9.nipic.com/20100917/5653289_174356436608_2.jpg",
            "http://img10.3lian.com/sc6/show02/38/65/386515.jpg",
            "http://pic1.nipic.com/2008-12-09/200812910493588_2.jpg",
            "http://pic2.ooopic.com/11/79/98/31bOOOPICb1_1024.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_image_view_demo);

        mIvMulti = (MultiImageView) findViewById(R.id.miv_multi);
        mIvSingle = (MultiImageView) findViewById(R.id.miv_single);

        mIvMulti.setList(getImages(8));
        mIvSingle.setList(getImages(1));
    }

    private List<String> getImages(int count) {
        if (count <= 0) {
            count = 1;
        }
        if (count > mPhotos.length) {
            count = mPhotos.length;
        }
        List<String> imageList = new ArrayList<>(count);
        Random random = new Random();
        for (int i = 0; i < count; ) {
            String url = mPhotos[random.nextInt(mPhotos.length)];
            if (imageList.contains(url)) {
                continue;
            }
            imageList.add(url);
            i++;
        }
        return imageList;
    }
}
