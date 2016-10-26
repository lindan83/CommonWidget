package com.lance.common.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by lindan on 16-9-10.
 * 对话框工具
 */
public class DialogUtil {
    private DialogUtil() {
        throw new UnsupportedOperationException("can not create an instance of DialogUtil");
    }

    /**
     * 显示错误对话框
     *
     * @param context Context
     * @param title   title
     * @param error   error message
     * @param button  button text
     */
    public static void showErrorDialog(Context context, String title, String error, String button) {
        CustomizableAlertDialog alertDialog = new CustomizableAlertDialog(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(error);
        alertDialog.setButton(button, null);
        alertDialog.show();
    }

    /**
     * 显示失败对话框
     *
     * @param activity activity
     * @param title    title
     * @param content  content
     * @param button   button text
     * @param close    whether to close the activity
     */
    public static void showSuccessDialog(final Activity activity, String title, String content, String button, final boolean close) {
        final CustomizableAlertDialog alertDialog = new CustomizableAlertDialog(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setButton(button, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (close) {
                    activity.finish();
                }
            }
        });
        alertDialog.show();
    }

    /**
     * 显示普通警告对话框
     *
     * @param context Context
     * @param title   title
     * @param content content
     * @param button  button text
     */
    public static void showAlertDialog(Context context, String title, String content, String button) {
        final CustomizableAlertDialog alertDialog = new CustomizableAlertDialog(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setButton(button, null);
        alertDialog.show();
    }

    /**
     * 显示确认对话框
     *
     * @param context                       Context
     * @param title                         title
     * @param content                       content
     * @param positiveButtonText            positive button text
     * @param negativeButtonText            negative button text
     * @param onClickPositiveButtonListener positive button click listener
     * @param onClickNegativeButtonListener negative button click listener
     */
    public static void showConfirmDialog(Context context, String title, String content,
                                         String positiveButtonText, String negativeButtonText,
                                         View.OnClickListener onClickPositiveButtonListener,
                                         View.OnClickListener onClickNegativeButtonListener) {
        final CustomizableConfirmDialog confirmDialog = new CustomizableConfirmDialog(context);
        confirmDialog.setTitle(title);
        confirmDialog.setMessage(content);
        confirmDialog.setPositiveButton(positiveButtonText, onClickPositiveButtonListener);
        confirmDialog.setNegativeButton(negativeButtonText, onClickNegativeButtonListener);
        confirmDialog.show();
    }
}
