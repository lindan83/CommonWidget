package com.lance.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.lance.common.glideimageloader.GlideBitmapLoadingListener;
import com.lance.common.glideimageloader.GlideImageLoader;
import com.lance.common.util.ScreenUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lindan on 17-3-6.
 * 图片轮播图
 */
public class ImageBanner extends ViewGroup {
    private static final int MSG_AUTO_SCROLL = 100;
    private List<String> imageUrls;
    private int imageWidth, imageHeight;

    private Scroller scroller;
    private int downX;//每次按下时的X坐标
    private int index;//当前滑动到的图片位置
    private int lastIndex = -1;//上一次的滑动位置，用于进行页面滑动判断
    private boolean autoScroll = true;//是否自动滑动
    private int autoScrollInterval = 2000;//自动滑动的间隔时间，单位秒

    private ImageBannerListener imageBannerListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_SCROLL) {
                if (++index >= imageUrls.size()) {
                    index = 0;
                }
                //scrollTo(index * imageWidth, 0);
                int scrollX = getScrollX();
                scroller.startScroll(scrollX, 0, index * imageWidth - scrollX, 0, 200);
                postInvalidate();
            }
        }
    };

    /**
     * 滑动切换监听器
     */
    public interface ImageBannerListener {
        /**
         * 滑动位置改变监听
         *
         * @param position 当前索引
         */
        void onSelected(int position);

        /**
         * 滑动位置改变监听
         *
         * @param scrollX 横坐标滑动距离
         * @param scrollY 纵坐标滑动距离
         */
        void onScrolled(int scrollX, int scrollY);
    }

    public ImageBanner(Context context) {
        super(context);
        init();
    }

    public ImageBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext());
        imageWidth = ScreenUtil.getScreenWidth(getContext());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (autoScroll) {
                    handler.sendEmptyMessage(MSG_AUTO_SCROLL);
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 200, autoScrollInterval);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            if (imageBannerListener != null) {
                imageBannerListener.onScrolled(getScrollX(), getScrollY());
                if (lastIndex != index) {
                    imageBannerListener.onSelected(index);
                    lastIndex = index;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            View firstChild = getChildAt(0);
            imageHeight = firstChild.getMeasuredHeight();
            int width = imageWidth * childCount;
            int height = imageHeight;
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            if (childCount == 0) {
                return;
            }
            int marginLeft = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                child.layout(marginLeft, 0, marginLeft + imageWidth, imageHeight);
                marginLeft += imageWidth;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int dx = moveX - downX;
                scrollBy(-dx, 0);
                downX = moveX;
                if (imageBannerListener != null) {
                    imageBannerListener.onScrolled(getScrollX(), getScrollY());
                }
                break;
            case MotionEvent.ACTION_UP:
                index = (getScrollX() + (imageWidth / 2)) / imageWidth;
                int childCount = getChildCount();
                if (index < 0) {
                    index = 0;
                } else if (index > childCount - 1) {
                    index = childCount - 1;
                }
                int scrollX = getScrollX();
                scroller.startScroll(scrollX, 0, index * imageWidth - scrollX, 0, 400);
                postInvalidate();
                //scrollTo(imageWidth * index, 0);
                if (imageBannerListener != null) {
                    imageBannerListener.onSelected(index);
                }
                break;
        }
        return true;
    }

    /**
     * 设置图片URL
     *
     * @param imageUrls 图片URL
     */
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        removeAllViews();
        if (this.imageUrls != null && !this.imageUrls.isEmpty()) {
            for (int i = 0, count = this.imageUrls.size(); i < count; i++) {
                final ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ViewGroup.LayoutParams lp = new LayoutParams(imageWidth, LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(lp);
                addView(imageView);
                GlideImageLoader.loadBitmap(getContext(), this.imageUrls.get(i), new GlideBitmapLoadingListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        }
        requestLayout();
    }

    /**
     * 设置滑动监听器
     */
    public void setImageBannerListener(ImageBannerListener imageBannerListener) {
        this.imageBannerListener = imageBannerListener;
    }

    /**
     * 设置选中的索引
     *
     * @param position 索引
     */
    public void setPosition(int position) {
        index = position;
    }

    /**
     * 设置是否自动滑动
     *
     * @param autoScroll 是否自动滚动
     */
    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    /**
     * 设置自动滑动的间隔秒数
     *
     * @param second 秒
     */
    public void setAutoScrollInterval(int second) {
        autoScrollInterval = second;
    }

    public int getAutoScrollInterval() {
        return autoScrollInterval;
    }
}
