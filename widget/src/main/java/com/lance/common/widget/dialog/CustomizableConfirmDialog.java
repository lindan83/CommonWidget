package com.lance.common.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lance.common.widget.R;

/**
 * Created by lindan on 16-9-8.
 * 询问对话框
 */
public class CustomizableConfirmDialog {
    private Context context;
    private android.app.AlertDialog ad;
    private TextView titleView;
    private TextView messageView;
    private TextView positiveButtonView;
    private TextView negativeButtonView;

    public CustomizableConfirmDialog(Context context) {
        this.context = context;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.context);
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_confirm, null, false);
        titleView = (TextView) view.findViewById(R.id.tv_dialog_title);
        messageView = (TextView) view.findViewById(R.id.tv_dialog_content);
        positiveButtonView = (TextView) view.findViewById(R.id.tv_positive_button);
        negativeButtonView = (TextView) view.findViewById(R.id.tv_negative_button);
        builder.setView(view);
        ad = builder.create();
        ad.setCancelable(false);
        ad.setCanceledOnTouchOutside(false);
    }

    public void setTitle(int resId) {
        titleView.setText(context.getString(resId));
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
        messageView.setText(context.getString(resId));
    }

    public void setMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
        }
    }

    /**
     * 设置正按钮
     *
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text, final View.OnClickListener listener) {
        positiveButtonView.setText(text);
        if (listener != null) {
            positiveButtonView.setOnClickListener(listener);
        }
        dismiss();
    }

    /**
     * 设置负按钮
     *
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text, final View.OnClickListener listener) {
        negativeButtonView.setText(text);
        if (listener != null) {
            negativeButtonView.setOnClickListener(listener);
        }
        dismiss();
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        if (ad.isShowing()) {
            ad.dismiss();
        }
    }

    public void show() {
        if (!ad.isShowing()) {
            ad.show();
        }
    }
}