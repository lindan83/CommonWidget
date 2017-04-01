package com.lance.common.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.lance.common.widget.R;

import static com.lance.common.widget.R.id.tv_positive_button;

/**
 * Created by lindan on 16-9-8.
 * 确认对话框
 */
public class CustomizableConfirmDialog implements IDialog, View.OnClickListener {
    private Context context;
    private android.app.AlertDialog ad;
    private TextView titleView;
    private TextView messageView;
    private TextView positiveButtonView;
    private TextView negativeButtonView;

    private OnClickListener onClickListener;
    private OnShowListener onShowListener;
    private OnDismissListener onDismissListener;
    private OnCancelListener onCancelListener;

    public CustomizableConfirmDialog(Context context) {
        this(context, false, false);
    }

    public CustomizableConfirmDialog(Context context, boolean cancelable, boolean canceledOnTouchOutside) {
        this.context = context;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.context);
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_confirm, null, false);
        titleView = (TextView) view.findViewById(R.id.tv_dialog_title);
        messageView = (TextView) view.findViewById(R.id.tv_dialog_content);
        positiveButtonView = (TextView) view.findViewById(tv_positive_button);
        negativeButtonView = (TextView) view.findViewById(R.id.tv_negative_button);
        positiveButtonView.setOnClickListener(this);
        negativeButtonView.setOnClickListener(this);
        builder.setView(view);
        ad = builder.create();
        ad.setCancelable(cancelable);
        ad.setCanceledOnTouchOutside(canceledOnTouchOutside);
        Window window = ad.getWindow();
        if(window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void setTitle(int resId) {
        String text = context.getString(resId);
        setTitle(text);
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        }
    }

    public void setMessage(int resId) {
        String text = context.getString(resId);
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
    public void setPositiveButton(String text) {
        if (TextUtils.isEmpty(text)) {
            positiveButtonView.setVisibility(View.GONE);
        } else {
            positiveButtonView.setVisibility(View.VISIBLE);
            positiveButtonView.setText(text);
        }
    }

    @Override
    public void setPositiveButton(int resId) {
        String text = context.getString(resId);
        setPositiveButton(text);
    }

    @Override
    public void setNegativeButton(String text) {
        if (TextUtils.isEmpty(text)) {
            negativeButtonView.setVisibility(View.GONE);
        } else {
            negativeButtonView.setVisibility(View.VISIBLE);
            negativeButtonView.setText(text);
        }
    }

    @Override
    public void setNegativeButton(int resId) {
        String text = context.getString(resId);
        setNegativeButton(text);
    }

    @Override
    public void setNeutralButton(String text) {

    }

    @Override
    public void setNeutralButton(int resId) {

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
    public void setOnCancelListener(OnCancelListener listener) {
        this.onCancelListener = listener;
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void setOnShowListener(OnShowListener listener) {
        this.onShowListener = listener;
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public void setOnKeyListener(OnKeyListener listener) {

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_positive_button) {
            if (onClickListener != null) {
                onClickListener.onClick(this, BUTTON_POSITIVE);
            }
        } else if (id == R.id.tv_negative_button) {
            if (onClickListener != null) {
                onClickListener.onClick(this, BUTTON_NEGATIVE);
            }
        }
    }
}