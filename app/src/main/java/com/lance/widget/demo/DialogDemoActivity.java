package com.lance.widget.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.dialog.CommonAlertDialog;
import com.lance.common.widget.dialog.CommonConfirmDialog;
import com.lance.common.widget.dialog.IDialog;

public class DialogDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_demo);
    }

    public void showDemo(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_alert_dialog_demo:
                showAlertDialog();
                break;
            case R.id.btn_confirm_dialog_demo:
                showConfirmDialog();
                break;
        }
    }

    private void showAlertDialog() {
        CommonAlertDialog dialog = new CommonAlertDialog.AlertDialogBuilder(this)
                .setDialogViewLayoutId(R.layout.dialog_alert)
                .setDialogTitleViewId(R.id.tv_dialog_title)
                .setDialogMessageViewId(R.id.tv_dialog_content)
                .setButtonViewId(R.id.tv_positive_button)
                .setDialogBackground(R.drawable.dialog_corner_bg)
                .setTitle("警告")
                .setTitleTextColor(Color.BLUE)
                .setTitleTextSize(18)
                .setMessage("测试警告对话框")
                .setMessageTextColor(Color.YELLOW)
                .setMessageTextSize(16)
                .setButtonText("我知道了")
                .setButtonTextSize(18)
                .setButtonTextColor(Color.RED)
                .setButtonBackground(R.drawable.dialog_positive_button_bg)
                .setOnShowListener(new IDialog.OnShowListener() {
                    @Override
                    public void onShow(IDialog dialog) {
                        ToastUtil.showShort(DialogDemoActivity.this, "Show");
                    }
                })
                .setOnDismissListener(new IDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(IDialog dialog) {
                        ToastUtil.showShort(DialogDemoActivity.this, "Dismiss");
                    }
                })
                .setOnCancelListener(new IDialog.OnCancelListener() {
                    @Override
                    public void onCancel(IDialog dialog) {
                        ToastUtil.showShort(DialogDemoActivity.this, "Cancel");
                    }
                })
                .setOnClickListener(new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog, int which) {
                        ToastUtil.showShort(DialogDemoActivity.this, "Click");
                        dialog.dismiss();
                    }
                })
                .createDialog();
        dialog.show();
    }

    private void showConfirmDialog() {
        CommonConfirmDialog dialog = new CommonConfirmDialog.ConfirmDialogBuilder(this)
                .setDialogViewLayoutId(R.layout.dialog_confirm)
                .setDialogTitleViewId(R.id.tv_dialog_title)
                .setDialogMessageViewId(R.id.tv_dialog_content)
                .setPositiveButtonViewId(R.id.tv_positive_button)
                .setNegativeButtonViewId(R.id.tv_negative_button)
                .setDialogBackground(R.drawable.dialog_corner_bg)
                .setTitle("确认")
                .setTitleTextColor(Color.BLUE)
                .setTitleTextSize(18)
                .setMessage("测试确认对话框")
                .setMessageTextColor(Color.YELLOW)
                .setMessageTextSize(16)
                .setPositiveButtonText("确定")
                .setPositiveButtonTextSize(16)
                .setPositiveButtonTextColor(Color.WHITE)
                .setPositiveButtonBackgroundColor(Color.BLACK)
                .setNegativeButtonText("取消")
                .setNegativeButtonTextSize(16)
                .setNegativeButtonTextColor(Color.BLACK)
                .setNegativeButtonBackgroundColor(Color.WHITE)
                .setOnShowListener(new IDialog.OnShowListener() {
                    @Override
                    public void onShow(IDialog dialog) {
                        ToastUtil.showShort(DialogDemoActivity.this, "Show");
                    }
                })
                .setOnDismissListener(new IDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(IDialog dialog) {
                        ToastUtil.showShort(DialogDemoActivity.this, "Dismiss");
                    }
                })
                .setOnCancelListener(new IDialog.OnCancelListener() {
                    @Override
                    public void onCancel(IDialog dialog) {
                        ToastUtil.showShort(DialogDemoActivity.this, "Cancel");
                    }
                })
                .setOnClickListener(new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog, int which) {
                        if (which == IDialog.BUTTON_POSITIVE) {
                            ToastUtil.showShort(DialogDemoActivity.this, "Click ok");
                        } else if (which == IDialog.BUTTON_NEGATIVE) {
                            ToastUtil.showShort(DialogDemoActivity.this, "Click cancel");
                        }
                        dialog.dismiss();
                    }
                })
                .createDialog();
        dialog.show();
    }
}
