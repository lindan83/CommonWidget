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
public class CommonConfirmDialog implements IDialog, View.OnClickListener {
    private ConfirmDialogBuilder dialogBuilder;
    private View dialogView;
    private TextView titleView;
    private TextView messageView;
    private TextView positiveButtonView, negativeButtonView;
    private AlertDialog ad;
    private OnCancelListener onCancelListener;
    private OnDismissListener onDismissListener;
    private OnShowListener onShowListener;
    private OnClickListener onClickListener;

    public CommonConfirmDialog(@NonNull ConfirmDialogBuilder dialogBuilder) {
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
        positiveButtonView = (TextView) dialogView.findViewById(dialogBuilder.dialogPositiveButtonViewId);
        positiveButtonView.setTextSize(dialogBuilder.positiveButtonTextSize);
        positiveButtonView.setBackgroundDrawable(dialogBuilder.positiveButtonBackground);
        negativeButtonView = (TextView) dialogView.findViewById(dialogBuilder.dialogNegativeButtonViewId);
        negativeButtonView.setTextSize(dialogBuilder.negativeButtonTextSize);
        negativeButtonView.setBackgroundDrawable(dialogBuilder.negativeButtonBackground);

        if (dialogBuilder.positiveButtonTextColorId != 0) {
            positiveButtonView.setTextColor(dialogBuilder.context.getResources().getColorStateList(dialogBuilder.positiveButtonTextColorId));
        } else {
            positiveButtonView.setTextColor(dialogBuilder.positiveButtonTextColor);
        }
        positiveButtonView.setText(dialogBuilder.positiveButtonText);
        positiveButtonView.setOnClickListener(this);

        if (dialogBuilder.negativeButtonTextColorId != 0) {
            negativeButtonView.setTextColor(dialogBuilder.context.getResources().getColorStateList(dialogBuilder.negativeButtonTextColorId));
        } else {
            negativeButtonView.setTextColor(dialogBuilder.negativeButtonTextColor);
        }
        negativeButtonView.setText(dialogBuilder.negativeButtonText);
        negativeButtonView.setOnClickListener(this);

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
            positiveButtonView.setVisibility(View.GONE);
        } else {
            positiveButtonView.setVisibility(View.VISIBLE);
            positiveButtonView.setText(text);
        }
    }

    @Override
    public void setPositiveButtonText(@StringRes int resId) {
        String text = dialogBuilder.context.getString(resId);
        setPositiveButtonText(text);
    }

    @Override
    public void setNegativeButtonText(String text) {
        if (TextUtils.isEmpty(text)) {
            negativeButtonView.setVisibility(View.GONE);
        } else {
            negativeButtonView.setVisibility(View.VISIBLE);
            negativeButtonView.setText(text);
        }
    }

    @Override
    public void setNegativeButtonText(@StringRes int resId) {
        String text = dialogBuilder.context.getString(resId);
        setNegativeButtonText(text);
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

    public void setPositiveButtonBackground(@NonNull Drawable buttonBackground) {
        positiveButtonView.setBackgroundDrawable(buttonBackground);
    }

    public void setNegativeButtonBackground(@NonNull Drawable buttonBackground) {
        negativeButtonView.setBackgroundDrawable(buttonBackground);
    }

    public void setPositiveButtonTextColorResId(@ColorRes int buttonTextColorId) {
        positiveButtonView.setTextColor(dialogBuilder.context.getResources().getColorStateList(buttonTextColorId));
    }

    public void setNegativeButtonTextColorResId(@ColorRes int buttonTextColorId) {
        negativeButtonView.setTextColor(dialogBuilder.context.getResources().getColorStateList(buttonTextColorId));
    }

    public void setPositiveButtonTextColor(@ColorInt int buttonTextColor) {
        positiveButtonView.setTextColor(buttonTextColor);
    }

    public void setNegativeButtonTextColor(@ColorInt int buttonTextColor) {
        negativeButtonView.setTextColor(buttonTextColor);
    }

    public void setPositiveButtonTextSize(@FloatRange(from = 12) float buttonTextSize) {
        positiveButtonView.setTextSize(buttonTextSize);
    }

    public void setNegativeButtonTextSize(@FloatRange(from = 12) float buttonTextSize) {
        negativeButtonView.setTextSize(buttonTextSize);
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
        if (id == dialogBuilder.dialogPositiveButtonViewId) {
            if (onClickListener != null) {
                onClickListener.onClick(this, BUTTON_POSITIVE);
            }
        } else if (id == dialogBuilder.dialogNegativeButtonViewId) {
            if (onClickListener != null) {
                onClickListener.onClick(this, BUTTON_NEGATIVE);
            }
        }
    }

    /**
     * CommonConfirmDialog构建器
     */
    public static class ConfirmDialogBuilder extends DialogBuilder<ConfirmDialogBuilder, CommonConfirmDialog> {
        int dialogPositiveButtonViewId;
        int dialogNegativeButtonViewId;
        String positiveButtonText, negativeButtonText;
        Drawable positiveButtonBackground, negativeButtonBackground;
        int positiveButtonTextColor = DEFAULT_BUTTON_TEXT_COLOR;
        int negativeButtonTextColor = DEFAULT_BUTTON_TEXT_COLOR;
        int positiveButtonTextColorId, negativeButtonTextColorId;
        float positiveButtonTextSize = DEFAULT_BUTTON_TEXT_SIZE;
        float negativeButtonTextSize = DEFAULT_BUTTON_TEXT_SIZE;


        public ConfirmDialogBuilder(Context context) {
            super(context);
        }

        //-------------------------------按钮背景、文本颜色、大小相关-------------------------------------/
        public ConfirmDialogBuilder setPositiveButtonViewId(@IdRes int dialogPositiveButtonViewId) {
            this.dialogPositiveButtonViewId = dialogPositiveButtonViewId;
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonViewId(@IdRes int dialogNegativeButtonViewId) {
            this.dialogNegativeButtonViewId = dialogNegativeButtonViewId;
            return this;
        }

        public ConfirmDialogBuilder setPositiveButtonBackground(@NonNull Drawable positiveButtonBackground) {
            this.positiveButtonBackground = positiveButtonBackground;
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonBackground(@NonNull Drawable negativeButtonBackground) {
            this.negativeButtonBackground = negativeButtonBackground;
            return this;
        }

        public ConfirmDialogBuilder setPositiveButtonBackground(@DrawableRes int positiveButtonBackgroundId) {
            this.positiveButtonBackground = context.getResources().getDrawable(positiveButtonBackgroundId);
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonBackground(@DrawableRes int negativeButtonBackgroundId) {
            this.negativeButtonBackground = context.getResources().getDrawable(negativeButtonBackgroundId);
            return this;
        }


        public ConfirmDialogBuilder setPositiveButtonBackgroundColor(@ColorInt int positiveButtonBackgroundColor) {
            this.positiveButtonBackground = new ColorDrawable(positiveButtonBackgroundColor);
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonBackgroundColor(@ColorInt int negativeButtonBackgroundColor) {
            this.negativeButtonBackground = new ColorDrawable(negativeButtonBackgroundColor);
            return this;
        }

        public ConfirmDialogBuilder setPositiveButtonTextColor(@ColorInt int positiveButtonTextColor) {
            this.positiveButtonTextColor = positiveButtonTextColor;
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonTextColor(@ColorInt int negativeButtonTextColor) {
            this.negativeButtonTextColor = negativeButtonTextColor;
            return this;
        }

        public ConfirmDialogBuilder setPositiveButtonTextColorId(@ColorRes int positiveButtonTextColorId) {
            this.positiveButtonTextColorId = positiveButtonTextColorId;
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonTextColorId(@ColorRes int negativeButtonTextColorId) {
            this.negativeButtonTextColorId = negativeButtonTextColorId;
            return this;
        }

        public ConfirmDialogBuilder setPositiveButtonText(String text) {
            this.positiveButtonText = text;
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonText(String text) {
            this.negativeButtonText = text;
            return this;
        }

        public ConfirmDialogBuilder setPositiveButtonTextSize(@FloatRange(from = 12) float textSize) {
            this.positiveButtonTextSize = textSize;
            return this;
        }

        public ConfirmDialogBuilder setNegativeButtonTextSize(@FloatRange(from = 12) float textSize) {
            this.negativeButtonTextSize = textSize;
            return this;
        }

        public CommonConfirmDialog createDialog() {
            if (dialogBackground == null) {
                throw new Resources.NotFoundException("必须指定对话框背景");
            }
            CommonConfirmDialog dialog = new CommonConfirmDialog(this);
            dialog.setDialogBackground(dialogBackground);

            dialog.setTitle(title);
            dialog.setTitleTextSize(titleTextSize);
            dialog.setTitleTextColor(titleTextColor);

            dialog.setMessage(message);
            dialog.setMessageTextSize(messageTextSize);
            dialog.setMessageTextColor(messageTextColor);

            dialog.setPositiveButtonText(positiveButtonText);
            if (positiveButtonBackground == null) {
                positiveButtonBackground = context.getResources().getDrawable(DEFAULT_BUTTON_BACKGROUND_ID);
            }
            dialog.setPositiveButtonBackground(positiveButtonBackground);
            if (positiveButtonTextColorId != 0) {
                dialog.setPositiveButtonTextColorResId(positiveButtonTextColorId);
            } else {
                dialog.setPositiveButtonTextColor(positiveButtonTextColor);
            }
            dialog.setPositiveButtonTextSize(positiveButtonTextSize);

            dialog.setNegativeButtonText(negativeButtonText);
            if (negativeButtonBackground == null) {
                negativeButtonBackground = context.getResources().getDrawable(DEFAULT_BUTTON_BACKGROUND_ID);
            }
            dialog.setNegativeButtonBackground(negativeButtonBackground);
            if (negativeButtonTextColorId != 0) {
                dialog.setNegativeButtonTextColorResId(negativeButtonTextColorId);
            } else {
                dialog.setNegativeButtonTextColor(negativeButtonTextColor);
            }
            dialog.setNegativeButtonTextSize(negativeButtonTextSize);

            dialog.setOnCancelListener(onCancelListener);
            dialog.setOnClickListener(onClickListener);
            dialog.setOnDismissListener(onDismissListener);
            dialog.setOnShowListener(onShowListener);
            return dialog;
        }
    }
}