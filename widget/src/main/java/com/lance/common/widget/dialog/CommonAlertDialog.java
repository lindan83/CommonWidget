package com.lance.common.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


/**
 * Created by lindan on 16-9-8.
 * 通用自定义警告对话框工具
 */
public class CommonAlertDialog implements IDialog, View.OnClickListener {
    private AlertDialogBuilder dialogBuilder;
    private View dialogView;
    private TextView titleView;
    private TextView messageView;
    private TextView buttonView;
    private AlertDialog ad;
    private OnCancelListener onCancelListener;
    private OnDismissListener onDismissListener;
    private OnShowListener onShowListener;
    private OnClickListener onClickListener;

    public CommonAlertDialog(@NonNull AlertDialogBuilder dialogBuilder) {
        this.dialogBuilder = dialogBuilder;
        onCancelListener = dialogBuilder.onCancelListener;
        onDismissListener = dialogBuilder.onDismissListener;
        onShowListener = dialogBuilder.onShowListener;
        onClickListener = dialogBuilder.onClickListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(dialogBuilder.context);
        dialogView = LayoutInflater.from(dialogBuilder.context).inflate(dialogBuilder.dialogViewLayoutId, null, false);
        dialogView.setBackgroundDrawable(dialogBuilder.dialogBackground);
        titleView = (TextView) dialogView.findViewById(dialogBuilder.dialogTitleViewId);
        titleView.setTextSize(dialogBuilder.titleTextSize);
        titleView.setTextColor(dialogBuilder.titleTextColor);
        setTitle(dialogBuilder.title);
        messageView = (TextView) dialogView.findViewById(dialogBuilder.dialogMessageViewId);
        messageView.setTextSize(dialogBuilder.messageTextSize);
        messageView.setTextColor(dialogBuilder.messageTextColor);
        setMessage(dialogBuilder.message);
        buttonView = (TextView) dialogView.findViewById(dialogBuilder.dialogButtonViewId);
        buttonView.setTextSize(dialogBuilder.buttonTextSize);
        buttonView.setBackgroundDrawable(dialogBuilder.buttonBackground);
        if (dialogBuilder.buttonTextColorId != 0) {
            buttonView.setTextColor(dialogBuilder.context.getResources().getColorStateList(dialogBuilder.buttonTextColorId));
        } else {
            buttonView.setTextColor(dialogBuilder.buttonTextColor);
        }
        buttonView.setText(dialogBuilder.buttonText);
        buttonView.setOnClickListener(this);
        builder.setView(dialogView);
        ad = builder.create();
        ad.setCancelable(dialogBuilder.cancelable);
        ad.setCanceledOnTouchOutside(dialogBuilder.cancelable);
        Window window = ad.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void setTitle(@StringRes int resId) {
        String text = dialogBuilder.context.getString(resId);
        setTitle(text);
    }

    @Override
    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        }
    }

    @Override
    public void setMessage(@StringRes int resId) {
        String text = dialogBuilder.context.getString(resId);
        setMessage(text);
    }

    public void setMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
        }
    }

    @Override
    public void setPositiveButtonText(String text) {
        if (TextUtils.isEmpty(text)) {
            buttonView.setVisibility(View.GONE);
        } else {
            buttonView.setVisibility(View.VISIBLE);
            buttonView.setText(text);
        }
    }

    @Override
    public void setPositiveButtonText(@StringRes int resId) {
        String text = dialogBuilder.context.getString(resId);
        setPositiveButtonText(text);
    }

    @Override
    public void setNegativeButtonText(String text) {

    }

    @Override
    public void setNegativeButtonText(@StringRes int resId) {

    }

    @Override
    public void setNeutralButtonText(String text) {

    }

    @Override
    public void setNeutralButtonText(@StringRes int resId) {

    }

    public void setDialogBackground(@NonNull Drawable dialogBackground) {
        dialogView.setBackgroundDrawable(dialogBackground);
    }

    public void setTitleTextSize(@FloatRange(from = 12) float titleTextSize) {
        titleView.setTextSize(titleTextSize);
    }

    public void setTitleTextColor(@ColorInt int titleTextColor) {
        titleView.setTextColor(titleTextColor);
    }

    public void setMessageTextSize(@FloatRange(from = 12) float messageTextSize) {
        messageView.setTextSize(messageTextSize);
    }

    public void setMessageTextColor(@ColorInt int messageTextColor) {
        messageView.setTextColor(messageTextColor);
    }

    public void setButtonBackground(@NonNull Drawable buttonBackground) {
        buttonView.setBackgroundDrawable(buttonBackground);
    }

    public void setButtonTextColorResId(@ColorRes int buttonTextColorId) {
        buttonView.setTextColor(dialogBuilder.context.getResources().getColorStateList(buttonTextColorId));
    }

    public void setButtonTextColor(@ColorInt int buttonTextColor) {
        buttonView.setTextColor(buttonTextColor);
    }

    public void setButtonTextSize(@FloatRange(from = 12) float buttonTextSize) {
        buttonView.setTextSize(buttonTextSize);
    }

    public void setCancelable(boolean cancelable) {
        ad.setCancelable(cancelable);
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        ad.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    public void dismiss() {
        if (ad.isShowing()) {
            ad.dismiss();
            if (onDismissListener != null) {
                onDismissListener.onDismiss(this);
            }
        }
    }

    public void show() {
        if (!ad.isShowing()) {
            ad.show();
            if (onShowListener != null) {
                onShowListener.onShow(this);
            }
        }
    }

    @Override
    public void cancel() {
        if (ad.isShowing()) {
            ad.cancel();
            if (onCancelListener != null) {
                onCancelListener.onCancel(this);
            }
        }
    }

    @Override
    public void setOnCancelListener(@NonNull OnCancelListener listener) {
        this.onCancelListener = listener;
    }

    @Override
    public void setOnDismissListener(@NonNull OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void setOnShowListener(@NonNull OnShowListener listener) {
        this.onShowListener = listener;
    }

    @Override
    public void setOnClickListener(@NonNull OnClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public void setOnKeyListener(@NonNull OnKeyListener listener) {

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == dialogBuilder.dialogButtonViewId) {
            if (onClickListener != null) {
                onClickListener.onClick(this, BUTTON_POSITIVE);
            }
        }
    }

    /**
     * CommonAlertDialog构建器
     */
    public static class AlertDialogBuilder extends DialogBuilder<AlertDialogBuilder, CommonAlertDialog> {
        int dialogButtonViewId;
        String buttonText;
        Drawable buttonBackground;
        int buttonTextColor = DEFAULT_BUTTON_TEXT_COLOR;
        int buttonTextColorId;
        float buttonTextSize = DEFAULT_BUTTON_TEXT_SIZE;

        public AlertDialogBuilder(Context context) {
            super(context);
        }

        //-------------------------------按钮背景、文本颜色、大小相关-------------------------------------/
        public AlertDialogBuilder setButtonViewId(@IdRes int dialogButtonViewId) {
            this.dialogButtonViewId = dialogButtonViewId;
            return this;
        }

        public AlertDialogBuilder setButtonBackground(@NonNull Drawable buttonBackground) {
            this.buttonBackground = buttonBackground;
            return this;
        }

        public AlertDialogBuilder setButtonBackground(@DrawableRes int buttonBackgroundId) {
            this.buttonBackground = context.getResources().getDrawable(buttonBackgroundId);
            return this;
        }

        public AlertDialogBuilder setButtonBackgroundColor(@ColorInt int buttonBackgroundColor) {
            this.buttonBackground = new ColorDrawable(buttonBackgroundColor);
            return this;
        }

        public AlertDialogBuilder setButtonTextColor(@ColorInt int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        public AlertDialogBuilder setButtonTextColorId(@ColorRes int buttonTextColorId) {
            this.buttonTextColorId = buttonTextColorId;
            return this;
        }

        public AlertDialogBuilder setButtonText(String text) {
            this.buttonText = text;
            return this;
        }

        public AlertDialogBuilder setButtonTextSize(@FloatRange(from = 12) float textSize) {
            this.buttonTextSize = textSize;
            return this;
        }

        @Override
        public CommonAlertDialog createDialog() {
            if (dialogBackground == null) {
                throw new Resources.NotFoundException("必须指定对话框背景");
            }
            CommonAlertDialog dialog = new CommonAlertDialog(this);
            dialog.setDialogBackground(dialogBackground);

            dialog.setTitle(title);
            dialog.setTitleTextSize(titleTextSize);
            dialog.setTitleTextColor(titleTextColor);

            dialog.setMessage(message);
            dialog.setMessageTextSize(messageTextSize);
            dialog.setMessageTextColor(messageTextColor);

            dialog.setPositiveButtonText(buttonText);
            if (buttonBackground == null) {
                buttonBackground = context.getResources().getDrawable(DEFAULT_BUTTON_BACKGROUND_ID);
            }
            dialog.setButtonBackground(buttonBackground);
            if (buttonTextColorId != 0) {
                dialog.setButtonTextColorResId(buttonTextColorId);
            } else {
                dialog.setButtonTextColor(buttonTextColor);
            }

            dialog.setButtonTextSize(buttonTextSize);
            dialog.setOnCancelListener(onCancelListener);
            dialog.setOnClickListener(onClickListener);
            dialog.setOnDismissListener(onDismissListener);
            dialog.setOnShowListener(onShowListener);
            return dialog;
        }
    }
}