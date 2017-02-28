package com.lance.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lance.common.util.ToastUtil;
import com.lance.common.widget.dialog.DialogUtil;
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
        DialogUtil.showAlertDialog(this, "警告", "测试警告对话框", "确定", new IDialog.OnClickListener() {
            @Override
            public void onClick(IDialog dialog, int which) {
                dialog.dismiss();
                ToastUtil.showShort(DialogDemoActivity.this, "点击了确定");
            }
        });
    }

    private void showConfirmDialog() {
        DialogUtil.showConfirmDialog(this, "确认", "测试确认对话框", "确定", "取消", new IDialog.OnClickListener() {
            @Override
            public void onClick(IDialog dialog, int which) {
                dialog.dismiss();
                if(which == IDialog.BUTTON_POSITIVE) {
                    ToastUtil.showShort(DialogDemoActivity.this, "点击了确定");
                } else if(which == IDialog.BUTTON_NEGATIVE) {
                    ToastUtil.showShort(DialogDemoActivity.this, "点击了取消");
                }
            }
        });
    }
}
