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
public class CustomizableAlertDialog implements View.OnClickListener {
    private Context context;
    private android.app.AlertDialog ad;
    private TextView titleView;
    private TextView messageView;
    private TextView buttonView;

    public CustomizableAlertDialog(Context context) {
        this.context = context;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.context);
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_alert, null, false);
        titleView = (TextView) view.findViewById(R.id.tv_dialog_title);
        messageView = (TextView) view.findViewById(R.id.tv_dialog_content);
        buttonView = (TextView) view.findViewById(R.id.tv_positive_button);
        buttonView.setOnClickListener(this);
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
     * 设置按钮
     *
     * @param text
     * @param listener
     */
    public void setButton(String text, final View.OnClickListener listener) {
        buttonView.setText(text);
        if (listener != null) {
            buttonView.setOnClickListener(listener);
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

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.tv_positive_button) {
            dismiss();
        }
    }
}