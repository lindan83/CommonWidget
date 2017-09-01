package com.lance.common.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by lindan on 17-6-15.
 * 通用的PopupWindow，可支持在各个方向弹出
 */
public class CommonPopupWindow extends PopupWindow {
    private final PopupController controller;

    @Override
    public int getWidth() {
        return controller.popupView.getMeasuredWidth();
    }

    @Override
    public int getHeight() {
        return controller.popupView.getMeasuredHeight();
    }

    public interface ViewInterface {
        void getChildView(View view, int layoutResId);
    }

    private CommonPopupWindow(Context context) {
        controller = new PopupController(context, this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        controller.setBackGroundLevel(1.0f);
    }

    public static class Builder {
        private final PopupController.PopupParams params;
        private ViewInterface listener;

        public Builder(Context context) {
            params = new PopupController.PopupParams(context);
        }

        /**
         * @param layoutResId 设置PopupWindow 布局ID
         * @return Builder
         */
        public Builder setView(int layoutResId) {
            params.view = null;
            params.layoutResId = layoutResId;
            return this;
        }

        /**
         * @param view 设置PopupWindow布局
         * @return Builder
         */
        public Builder setView(View view) {
            params.view = view;
            params.layoutResId = 0;
            return this;
        }

        /**
         * 设置子View
         *
         * @param listener ViewInterface
         * @return Builder
         */
        public Builder setViewOnClickListener(ViewInterface listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 设置宽度和高度 如果不设置 默认是wrap_content
         *
         * @param width 宽
         * @return Builder
         */
        public Builder setWidthAndHeight(int width, int height) {
            params.width = width;
            params.height = height;
            return this;
        }

        /**
         * 设置背景灰色程度
         *
         * @param level 0.0f-1.0f
         * @return Builder
         */
        public Builder setBackGroundLevel(float level) {
            params.isShowBg = true;
            params.bgLevel = level;
            return this;
        }

        /**
         * 是否可点击Outside消失
         *
         * @param touchable 是否可点击
         * @return Builder
         */
        public Builder setOutsideTouchable(boolean touchable) {
            params.isTouchable = touchable;
            return this;
        }

        /**
         * 设置动画
         *
         * @return Builder
         */
        public Builder setAnimationStyle(int animationStyle) {
            params.isShowAnim = true;
            params.animationStyle = animationStyle;
            return this;
        }

        public CommonPopupWindow create() {
            final CommonPopupWindow popupWindow = new CommonPopupWindow(params.context);
            params.apply(popupWindow.controller);
            if (listener != null && params.layoutResId != 0) {
                listener.getChildView(popupWindow.controller.popupView, params.layoutResId);
            }
            measureWidthAndHeight(popupWindow.controller.popupView);
            return popupWindow;
        }

        /**
         * 测量View的宽高
         *
         * @param view View
         */
        private void measureWidthAndHeight(View view) {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
        }
    }
}

class PopupController {
    private int layoutResId;//布局id
    private Context context;
    private PopupWindow popupWindow;
    View popupView;//弹窗布局View
    private View view;

    PopupController(Context context, PopupWindow popupWindow) {
        this.context = context;
        this.popupWindow = popupWindow;
    }

    public void setView(int layoutResId) {
        view = null;
        this.layoutResId = layoutResId;
        installContent();
    }

    public void setView(View view) {
        this.view = view;
        this.layoutResId = 0;
        installContent();
    }

    private void installContent() {
        if (layoutResId != 0) {
            popupView = LayoutInflater.from(context).inflate(layoutResId, null);
        } else if (view != null) {
            popupView = view;
        }
        popupWindow.setContentView(popupView);
    }

    /**
     * 设置宽度
     *
     * @param width  宽
     * @param height 高
     */
    private void setWidthAndHeight(int width, int height) {
        if (width == 0 || height == 0) {
            //如果没设置宽高，默认是WRAP_CONTENT
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            popupWindow.setWidth(width);
            popupWindow.setHeight(height);
        }
    }


    /**
     * 设置背景灰色程度
     *
     * @param level 0.0f-1.0f
     */
    void setBackGroundLevel(float level) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = level;
        window.setAttributes(params);
    }


    /**
     * 设置动画
     */
    private void setAnimationStyle(int animationStyle) {
        popupWindow.setAnimationStyle(animationStyle);
    }

    /**
     * 设置Outside是否可点击
     *
     * @param touchable 是否可点击
     */
    private void setOutsideTouchable(boolean touchable) {
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));//设置透明背景
        popupWindow.setOutsideTouchable(touchable);//设置outside可点击
        popupWindow.setFocusable(touchable);
    }

    /**
     * PopupWindow的参数
     */
    static class PopupParams {
        int layoutResId;//布局id
        Context context;
        int width, height;//弹窗的宽和高
        boolean isShowBg, isShowAnim;
        float bgLevel;//屏幕背景灰色程度
        int animationStyle;//动画Id
        View view;
        boolean isTouchable = true;

        PopupParams(Context mContext) {
            this.context = mContext;
        }

        void apply(PopupController controller) {
            if (view != null) {
                controller.setView(view);
            } else if (layoutResId != 0) {
                controller.setView(layoutResId);
            } else {
                throw new IllegalArgumentException("PopupView's contentView is null");
            }
            controller.setWidthAndHeight(width, height);
            controller.setOutsideTouchable(isTouchable);//设置outside可点击
            if (isShowBg) {
                //设置背景
                controller.setBackGroundLevel(bgLevel);
            }
            if (isShowAnim) {
                controller.setAnimationStyle(animationStyle);
            }
        }
    }
}