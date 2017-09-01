package com.lance.common.widget.dialog;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.KeyEvent;

/**
 * Created by lindan on 17-2-27.
 * 对话框接口
 */
public interface IDialog {
    /**
     * 正按钮标志
     */
    int BUTTON_POSITIVE = -1;
    /**
     * 负按钮标志
     */
    int BUTTON_NEGATIVE = -2;
    /**
     * 中按钮标志
     */
    int BUTTON_NEUTRAL = -3;

    /**
     * 取消显示监听器
     */
    interface OnCancelListener {
        void onCancel(IDialog dialog);
    }

    /**
     * 关闭监听器
     */
    interface OnDismissListener {
        void onDismiss(IDialog dialog);
    }

    /**
     * 显示监听器
     */
    interface OnShowListener {
        void onShow(IDialog dialog);
    }

    /**
     * 点击按钮监听器
     */
    interface OnClickListener {
        void onClick(IDialog dialog, int which);
    }

    /**
     * 按键监听器
     */
    interface OnKeyListener {
        boolean onKey(IDialog dialog, int keyCode, KeyEvent event);
    }

    /**
     * 设置标题字符串资源ID
     *
     * @param resId Title String Resource ID
     */
    void setTitle(@StringRes int resId);

    /**
     * 设置标题字符串
     *
     * @param title Title String
     */
    void setTitle(String title);

    /**
     * 设置消息字符串资源ID
     *
     * @param resId Message String Resource ID
     */
    void setMessage(@StringRes int resId);

    /**
     * 设置消息字符串
     *
     * @param message Message String
     */
    void setMessage(String message);

    /**
     * 设置正按钮文本
     *
     * @param text 正按钮文本
     */
    void setPositiveButtonText(String text);

    /**
     * 设置正按钮文本字符串资源ID
     *
     * @param resId 正按钮文本字符串资源ID
     */
    void setPositiveButtonText(@StringRes int resId);

    /**
     * 设置负按钮文本
     *
     * @param text 负按钮文本
     */
    void setNegativeButtonText(String text);

    /**
     * 设置负按钮文本字符串资源ID
     *
     * @param resId 负按钮文本字符串资源ID
     */
    void setNegativeButtonText(@StringRes int resId);

    /**
     * 设置中按钮文本字符串
     *
     * @param text 中按钮文本字符串
     */
    void setNeutralButtonText(String text);

    /**
     * 设置中按钮文本字符串资源ID
     *
     * @param resId 中按钮文本字符串资源ID
     */
    void setNeutralButtonText(@StringRes int resId);

    /**
     * 设置对话框是否可以取消
     *
     * @param cancelable true为可以取消
     */
    void setCancelable(boolean cancelable);

    /**
     * 设置对话框是否可以点击外部取消
     *
     * @param canceledOnTouchOutside true为可以取消
     */
    void setCanceledOnTouchOutside(boolean canceledOnTouchOutside);

    /**
     * 关闭对话框
     */
    void dismiss();

    /**
     * 显示对话框
     */
    void show();

    /**
     * 取消显示对话框
     */
    void cancel();

    /**
     * 设置取消监听器
     *
     * @param listener OnCancelListener
     */
    void setOnCancelListener(@NonNull OnCancelListener listener);

    /**
     * 设置关闭监听器
     *
     * @param listener onDismissListener
     */
    void setOnDismissListener(@NonNull OnDismissListener listener);

    /**
     * 设置显示监听器
     *
     * @param listener OnShowListener
     */
    void setOnShowListener(@NonNull OnShowListener listener);

    /**
     * 设置点击监听器
     *
     * @param listener OnClickListener
     */
    void setOnClickListener(@NonNull OnClickListener listener);

    /**
     * 设置按键监听器
     *
     * @param listener OnKeyListener
     */
    void setOnKeyListener(@NonNull OnKeyListener listener);
}
