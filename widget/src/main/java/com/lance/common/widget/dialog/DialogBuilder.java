package com.lance.common.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.lance.common.widget.R;

/**
 * Created by lindan on 17-9-1.
 * 对话框构造器基类
 * T DialogBuilder的子类
 * D 要创建的Dialog类型
 */
public abstract class DialogBuilder<T extends DialogBuilder, D> {
    protected static final int DEFAULT_TITLE_TEXT_SIZE = 20;
    protected static final int DEFAULT_TITLE_TEXT_COLOR = Color.BLACK;
    protected static final int DEFAULT_MESSAGE_TEXT_SIZE = 16;
    protected static final int DEFAULT_MESSAGE_TEXT_COLOR = Color.BLACK;
    protected static final int DEFAULT_BUTTON_TEXT_SIZE = 16;
    protected static final int DEFAULT_BUTTON_TEXT_COLOR = Color.BLACK;
    protected static final int DEFAULT_BUTTON_BACKGROUND_ID = R.drawable.dialog_positive_button_bg;

    protected Context context;

    protected int dialogViewLayoutId;
    protected int dialogTitleViewId;
    protected int dialogMessageViewId;

    protected boolean cancelable;

    protected Drawable dialogBackground;

    protected String title;
    protected float titleTextSize = DEFAULT_TITLE_TEXT_SIZE;
    protected int titleTextColor = DEFAULT_TITLE_TEXT_COLOR;

    protected String message;
    protected float messageTextSize = DEFAULT_MESSAGE_TEXT_SIZE;
    protected int messageTextColor = DEFAULT_MESSAGE_TEXT_COLOR;

    protected IDialog.OnCancelListener onCancelListener;
    protected IDialog.OnDismissListener onDismissListener;
    protected IDialog.OnShowListener onShowListener;
    protected IDialog.OnClickListener onClickListener;

    public DialogBuilder(Context context) {
        this.context = context;
    }

    public T setDialogViewLayoutId(@LayoutRes int dialogViewLayoutId) {
        this.dialogViewLayoutId = dialogViewLayoutId;
        return (T) this;
    }

    public T setDialogTitleViewId(@IdRes int dialogTitleViewId) {
        this.dialogTitleViewId = dialogTitleViewId;
        return (T) this;
    }

    public T setDialogMessageViewId(@IdRes int dialogMessageViewId) {
        this.dialogMessageViewId = dialogMessageViewId;
        return (T) this;
    }

    public T setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return (T) this;
    }

    public T setTitle(@StringRes int resId) {
        String text = context.getString(resId);
        return setTitle(text);
    }

    public T setTitle(String title) {
        this.title = title;
        return (T) this;
    }

    public T setMessage(@StringRes int resId) {
        String text = context.getString(resId);
        return setMessage(text);
    }

    public T setMessage(String message) {
        this.message = message;
        return (T) this;
    }

    public T setDialogBackground(@NonNull Drawable dialogBackground) {
        this.dialogBackground = dialogBackground;
        return (T) this;
    }

    public T setDialogBackground(@DrawableRes int dialogBackground) {
        this.dialogBackground = context.getResources().getDrawable(dialogBackground);
        return (T) this;
    }

    //***************************标题相关******************************/
    public T setTitleTextSize(@FloatRange(from = 12) float titleTextSize) {
        this.titleTextSize = titleTextSize;
        return (T) this;
    }

    public T setTitleTextColor(@ColorInt int titleTextColor) {
        this.titleTextColor = titleTextColor;
        return (T) this;
    }

    //-------------------------------内容相关-------------------------------------/
    public T setMessageTextSize(@FloatRange(from = 12) float messageTextSize) {
        this.messageTextSize = messageTextSize;
        return (T) this;
    }

    public T setMessageTextColor(@ColorInt int messageTextColor) {
        this.messageTextColor = messageTextColor;
        return (T) this;
    }

    //----------------------------监听器相关---------------------------------/
    public T setOnCancelListener(@NonNull IDialog.OnCancelListener listener) {
        this.onCancelListener = listener;
        return (T) this;
    }

    public T setOnDismissListener(@NonNull IDialog.OnDismissListener listener) {
        this.onDismissListener = listener;
        return (T) this;
    }

    public T setOnShowListener(@NonNull IDialog.OnShowListener listener) {
        this.onShowListener = listener;
        return (T) this;
    }

    public T setOnClickListener(@NonNull IDialog.OnClickListener listener) {
        this.onClickListener = listener;
        return (T) this;
    }

    public abstract D createDialog();
}
