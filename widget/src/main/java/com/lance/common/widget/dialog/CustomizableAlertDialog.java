package com.lance.common.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lance.common.widget.R;

/**
 * Created by lindan on 16-9-8.
 * 警告对话框工具
 */
public class CustomizableAlertDialog implements IDialog, View.OnClickListener {
    private Context context;
    private android.app.AlertDialog ad;
    private TextView titleView;
    private TextView messageView;
    private TextView buttonView;

    private OnClickListener onClickListener;
    private OnShowListener onShowListener;
    private OnDismissListener onDismissListener;
    private OnCancelListener onCancelListener;

    public CustomizableAlertDialog(Context context) {
        this(context, false, false);
    }

    public CustomizableAlertDialog(Context context, boolean cancelable, boolean canceledOnTouchOutside) {
        this.context = context;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.context);
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_alert, null, false);
        titleView = (TextView) view.findViewById(R.id.tv_dialog_title);
        messageView = (TextView) view.findViewById(R.id.tv_dialog_content);
        buttonView = (TextView) view.findViewById(R.id.tv_positive_button);
        buttonView.setOnClickListener(this);
        builder.setView(view);
        ad = builder.create();
        ad.setCancelable(cancelable);
        ad.setCanceledOnTouchOutside(canceledOnTouchOutside);
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
            buttonView.setVisibility(View.GONE);
        } else {
            buttonView.setVisibility(View.VISIBLE);
            buttonView.setText(text);
        }
    }

    @Override
    public void setPositiveButton(int resId) {
        String text = context.getString(resId);
        setPositiveButton(text);
    }

    @Override
    public void setNegativeButton(String text) {

    }

    @Override
    public void setNegativeButton(int resId) {

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
        }
    }
}