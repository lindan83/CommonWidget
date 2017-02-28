package com.lance.common.widget.dialog;

import android.view.KeyEvent;

/**
 * Created by lindan on 17-2-27.
 */

public interface IDialog {
    int BUTTON_POSITIVE = -1;
    int BUTTON_NEGATIVE = -2;
    int BUTTON_NEUTRAL = -3;

    interface OnCancelListener {
        void onCancel(IDialog dialog);
    }

    interface OnDismissListener {
        void onDismiss(IDialog dialog);
    }

    interface OnShowListener {
        void onShow(IDialog dialog);
    }

    interface OnClickListener {
        void onClick(IDialog dialog, int which);
    }

    interface OnKeyListener {
        boolean onKey(IDialog dialog, int keyCode, KeyEvent event);
    }

    void setTitle(int resId);

    void setTitle(String title);

    void setMessage(int resId);

    void setMessage(String message);

    void setPositiveButton(String text);

    void setPositiveButton(int resId);

    void setNegativeButton(String text);

    void setNegativeButton(int resId);

    void setNeutralButton(String text);

    void setNeutralButton(int resId);

    void setCancelable(boolean cancelable);

    void setCanceledOnTouchOutside(boolean canceledOnTouchOutside);

    void dismiss();

    void show();

    void cancel();

    void setOnCancelListener(OnCancelListener listener);

    void setOnDismissListener(OnDismissListener listener);

    void setOnShowListener(OnShowListener listener);

    void setOnClickListener(OnClickListener listener);

    void setOnKeyListener(OnKeyListener listener);
}
