package com.lance.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lance.common.util.DensityUtil;

import java.util.List;

/**
 * Created by lindan on 17-3-6.
 * Banner轮播图控件
 */
public class ImageBannerLayout extends FrameLayout implements ImageBanner.ImageBannerListener {
    private static final boolean DEFAULT_SHOW_INDICATOR = true;
    private static final int DEFAULT_INDICATOR_COLOR_RES = R.drawable.dot_selector;
    private static final int INDICATOR_GRAVITY_LEFT = 1;
    private static final int INDICATOR_GRAVITY_LEFT_TOP = 2;
    private static final int INDICATOR_GRAVITY_LEFT_BOTTOM = 3;
    private static final int INDICATOR_GRAVITY_TOP = 4;
    private static final int INDICATOR_GRAVITY_TOP_LEFT = 5;
    private static final int INDICATOR_GRAVITY_TOP_RIGHT = 6;
    private static final int INDICATOR_GRAVITY_RIGHT = 7;
    private static final int INDICATOR_GRAVITY_RIGHT_TOP = 8;
    private static final int INDICATOR_GRAVITY_RIGHT_BOTTOM = 9;
    private static final int INDICATOR_GRAVITY_BOTTOM = 10;
    private static final int INDICATOR_GRAVITY_BOTTOM_LEFT = 11;
    private static final int INDICATOR_GRAVITY_BOTTOM_RIGHT = 12;
    private static final int DEFAULT_INDICATOR_GRAVITY = INDICATOR_GRAVITY_BOTTOM;
    private static final int DEFAULT_INDICATOR_SIZE_DP = 10;
    private static final int DEFAULT_INDICATOR_MARGIN_DP = 10;

    private LinearLayout indicatorContainer;
    private List<String> imageUrls;
    private int position;//当前索引

    private boolean showIndicator;
    private int indicatorColorRes;
    private int indicatorGravity;
    private float indicatorMarginLeft, indicatorMarginTop, indicatorMarginRight, indicatorMarginBottom, indicatorMargin;
    private int indicatorSize;


    /**
     * 滑动切换监听器
     */
    public interface OnImageSwitchListener {
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

    private OnImageSwitchListener onImageSwitchListener;

    public ImageBannerLayout(@NonNull Context context) {
        super(context);
        initAttrs(context, null);
    }

    public ImageBannerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public ImageBannerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageBannerLayout);
            showIndicator = ta.getBoolean(R.styleable.ImageBannerLayout_showIndicator, DEFAULT_SHOW_INDICATOR);
            indicatorSize = (int) ta.getDimension(R.styleable.ImageBannerLayout_indicatorSize, DensityUtil.dp2px(context, DEFAULT_INDICATOR_SIZE_DP));
            indicatorColorRes = ta.getResourceId(R.styleable.ImageBannerLayout_indicatorColor, DEFAULT_INDICATOR_COLOR_RES);
            indicatorGravity = ta.getInteger(R.styleable.ImageBannerLayout_indicatorGravity, DEFAULT_INDICATOR_GRAVITY);
            indicatorMarginLeft = ta.getDimension(R.styleable.ImageBannerLayout_indicatorMarginLeft, 0);
            indicatorMarginTop = ta.getDimension(R.styleable.ImageBannerLayout_indicatorMarginTop, 0);
            indicatorMarginRight = ta.getDimension(R.styleable.ImageBannerLayout_indicatorMarginRight, 0);
            indicatorMarginBottom = ta.getDimension(R.styleable.ImageBannerLayout_indicatorMarginBottom, 0);
            indicatorMargin = ta.getDimension(R.styleable.ImageBannerLayout_indicatorMargin, 0);
            ta.recycle();
        } else {
            showIndicator = DEFAULT_SHOW_INDICATOR;
            indicatorColorRes = DEFAULT_INDICATOR_COLOR_RES;
            indicatorGravity = DEFAULT_INDICATOR_GRAVITY;
            indicatorMargin = indicatorMarginLeft = indicatorMarginTop = indicatorMarginRight = indicatorMarginBottom = 0;
            indicatorSize = DensityUtil.dp2px(context, DEFAULT_INDICATOR_SIZE_DP);
        }
        if (showIndicator) {
            if (indicatorMarginLeft == 0
                    || indicatorMarginTop == 0
                    || indicatorMarginRight == 0
                    || indicatorMarginBottom == 0
                    || indicatorMargin == 0) {
                indicatorMargin = DensityUtil.dp2px(context, DEFAULT_INDICATOR_MARGIN_DP);
            }
        }
    }

    //初始化Banner
    private void initImageBannerViews() {
        ImageBanner imageBanner = new ImageBanner(getContext());
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageBanner.setLayoutParams(lp);
        imageBanner.setImageUrls(imageUrls);
        imageBanner.setPosition(position);
        addView(imageBanner);
        imageBanner.setImageBannerListener(this);
    }

    //初始化指示器
    private void initIndicatorViews() {
        if (!showIndicator) {
            return;
        }
        if (indicatorContainer == null) {
            indicatorContainer = new LinearLayout(getContext());
            int orientation = judgeOrientation();
            if (orientation == LinearLayout.VERTICAL) {
                indicatorContainer.setOrientation(LinearLayout.VERTICAL);
            } else {
                indicatorContainer.setOrientation(LinearLayout.HORIZONTAL);
            }
            indicatorContainer.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            handleGravity(lp);
            handleMargin(lp);
            indicatorContainer.setLayoutParams(lp);
            addView(indicatorContainer);
        } else {
            indicatorContainer.removeAllViews();
        }
        int margin = DensityUtil.dp2px(getContext(), 2f);
        for (int i = 0, count = imageUrls.size(); i < count; i++) {
            View view = new View(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
            lp.leftMargin = lp.rightMargin = lp.topMargin = lp.bottomMargin = margin;
            view.setLayoutParams(lp);
            if (indicatorColorRes > 0) {
                view.setBackgroundResource(indicatorColorRes);
            } else {
                view.setBackgroundResource(DEFAULT_INDICATOR_COLOR_RES);
            }
            view.setSelected(position == i);
            indicatorContainer.addView(view);
        }
    }

    private void handleGravity(FrameLayout.LayoutParams lp) {
        switch (indicatorGravity) {
            case INDICATOR_GRAVITY_LEFT:
                lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case INDICATOR_GRAVITY_LEFT_TOP:
                lp.gravity = Gravity.START | Gravity.TOP;
                break;
            case INDICATOR_GRAVITY_LEFT_BOTTOM:
                lp.gravity = Gravity.START | Gravity.BOTTOM;
                break;
            case INDICATOR_GRAVITY_TOP:
                lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case INDICATOR_GRAVITY_TOP_LEFT:
                lp.gravity = Gravity.TOP | Gravity.START;
                break;
            case INDICATOR_GRAVITY_TOP_RIGHT:
                lp.gravity = Gravity.TOP | Gravity.END;
                break;
            case INDICATOR_GRAVITY_RIGHT:
                lp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
            case INDICATOR_GRAVITY_RIGHT_TOP:
                lp.gravity = Gravity.END | Gravity.TOP;
                break;
            case INDICATOR_GRAVITY_RIGHT_BOTTOM:
                lp.gravity = Gravity.END | Gravity.BOTTOM;
                break;
            case INDICATOR_GRAVITY_BOTTOM:
                lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            case INDICATOR_GRAVITY_BOTTOM_LEFT:
                lp.gravity = Gravity.BOTTOM | Gravity.START;
                break;
            case INDICATOR_GRAVITY_BOTTOM_RIGHT:
                lp.gravity = Gravity.BOTTOM | Gravity.END;
                break;
            default:
                lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
        }
    }

    private int judgeOrientation() {
        switch (indicatorGravity) {
            case INDICATOR_GRAVITY_LEFT:
            case INDICATOR_GRAVITY_LEFT_TOP:
            case INDICATOR_GRAVITY_LEFT_BOTTOM:
            case INDICATOR_GRAVITY_RIGHT:
            case INDICATOR_GRAVITY_RIGHT_TOP:
            case INDICATOR_GRAVITY_RIGHT_BOTTOM:
                return LinearLayout.VERTICAL;
            case INDICATOR_GRAVITY_TOP:
            case INDICATOR_GRAVITY_TOP_LEFT:
            case INDICATOR_GRAVITY_TOP_RIGHT:
            case INDICATOR_GRAVITY_BOTTOM:
            case INDICATOR_GRAVITY_BOTTOM_LEFT:
            case INDICATOR_GRAVITY_BOTTOM_RIGHT:
            default:
                return LinearLayout.HORIZONTAL;
        }
    }

    private void handleMargin(FrameLayout.LayoutParams lp) {
        if (indicatorMargin > 0) {
            lp.bottomMargin = lp.topMargin = lp.leftMargin = lp.rightMargin = (int) indicatorMargin;
        }
        if (indicatorMarginLeft > 0) {
            lp.leftMargin = (int) indicatorMarginLeft;
        }
        if (indicatorMarginTop > 0) {
            lp.topMargin = (int) indicatorMarginTop;
        }
        if (indicatorMarginRight > 0) {
            lp.rightMargin = (int) indicatorMarginRight;
        }
        if (indicatorMarginBottom > 0) {
            lp.bottomMargin = (int) indicatorMarginBottom;
        }
    }

    @Override
    public void onSelected(int position) {
        if (showIndicator) {
            selectIndicatorView(position);
        }
        if (onImageSwitchListener != null) {
            onImageSwitchListener.onSelected(position);
        }
    }

    @Override
    public void onScrolled(int scrollX, int scrollY) {
        if (onImageSwitchListener != null) {
            onImageSwitchListener.onScrolled(scrollX, scrollY);
        }
    }

    private void selectIndicatorView(int position) {
        for (int i = 0, count = indicatorContainer.getChildCount(); i < count; i++) {
            View child = indicatorContainer.getChildAt(i);
            child.setSelected(position == i);
        }
    }

    /**
     * 设置图片URL
     *
     * @param imageUrls 图片URL
     */
    public void setImageUrls(List<String> imageUrls) {
        setImageUrls(imageUrls, 0);
    }

    /**
     * 设置图片URL
     *
     * @param imageUrls 图片URL
     * @param position  初始索引
     */
    public void setImageUrls(List<String> imageUrls, int position) {
        this.imageUrls = imageUrls;
        if (position < 0 || position >= imageUrls.size()) {
            this.position = 0;
        }
        removeAllViews();
        initImageBannerViews();
        initIndicatorViews();
    }

    public void setOnImageSwitchListener(OnImageSwitchListener onImageSwitchListener) {
        this.onImageSwitchListener = onImageSwitchListener;
    }

    /**
     * 设置选中位置
     *
     * @param position 位置
     */
    public void setSelectedPosition(int position) {
        this.position = position;
    }

    /**
     * 获取当前显示的图片索引
     *
     * @return 索引
     */
    public int getselectedPosition() {
        return position;
    }
}
