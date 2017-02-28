package com.lance.common.widget.dialog;

import android.content.Context;

/**
 * Created by lindan on 16-9-10.
 * 对话框工具
 */
public class DialogUtil {
    private DialogUtil() {
        throw new UnsupportedOperationException("can not create an instance of DialogUtil");
    }

    /**
     * 显示警告对话框，点击按钮关闭
     *
     * @param context Context
     * @param title   title
     * @param message message
     * @param button  button text
     */
    public static void showAlertDialog(Context context, String title, String message, String button) {
        CustomizableAlertDialog alertDialog = new CustomizableAlertDialog(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(button);
        alertDialog.setOnClickListener(new IDialog.OnClickListener() {
            @Override
            public void onClick(IDialog dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * 显示警告对话框，点击按钮关闭
     *
     * @param context Context
     * @param title   title
     * @param message message
     * @param button  button text
     */
    public static void showAlertDialog(Context context, String title, String message, String button, IDialog.OnClickListener onClickListener) {
        CustomizableAlertDialog alertDialog = new CustomizableAlertDialog(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(button);
        alertDialog.setOnClickListener(onClickListener);
        alertDialog.show();
    }

    /**
     * 显示确认对话框
     *
     * @param context            Context
     * @param title              title
     * @param content            content
     * @param positiveButtonText positive button text
     * @param negativeButtonText negative button text
     * @param listener           button click listener
     */
    public static void showConfirmDialog(Context context, String title, String content,
                                         String positiveButtonText, String negativeButtonText,
                                         IDialog.OnClickListener listener) {
        final CustomizableConfirmDialog confirmDialog = new CustomizableConfirmDialog(context);
        confirmDialog.setTitle(title);
        confirmDialog.setMessage(content);
        confirmDialog.setPositiveButton(positiveButtonText);
        confirmDialog.setNegativeButton(negativeButtonText);
        confirmDialog.setOnClickListener(listener);
        confirmDialog.show();
    }
}
