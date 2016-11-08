package com.lance.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.lance.common.util.DensityUtil;

/**
 * Created by lindan on 16-11-1.
 * 命令选项View
 */

public class OptionView extends TextView {
    private static final String TAG = "OptionView";
    private static final int DEFAULT_TEXT_SIZE = 14;//默认文本字体sp
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#606060");//默认文本颜色
    private static final int DEFAULT_LEFT_RIGHT_PADDING = 12;//默认左右内边距
    private static final int DEFAULT_INTERNAL_SPACING = 8;//默认Left Icon与Text的间距

    private Drawable mLeftIcon;
    private Drawable mRightIcon;
    private String mText;
    private float mTextSize;
    private int mTextColor;
    private int mPadding;
    private int mInternalSpacing;

    public OptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
    }

    public OptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OptionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OptionView);
        mLeftIcon = ta.getDrawable(R.styleable.OptionView_leftIcon);
        mRightIcon = ta.getDrawable(R.styleable.OptionView_rightIcon);
        mText = ta.getString(R.styleable.OptionView_text);
        mTextSize = ta.getDimension(R.styleable.OptionView_textSize, DensityUtil.sp2px(getContext(), DEFAULT_TEXT_SIZE));
        mTextColor = ta.getColor(R.styleable.OptionView_textColor, DEFAULT_TEXT_COLOR);
        mPadding = (int) ta.getDimension(R.styleable.OptionView_padding, DensityUtil.dp2px(getContext(), DEFAULT_LEFT_RIGHT_PADDING));
        mInternalSpacing = (int) ta.getDimension(R.styleable.OptionView_internalSpacing, DensityUtil.dp2px(getContext(), DEFAULT_INTERNAL_SPACING));
        ta.recycle();
    }

    private void init() {
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(mPadding, getPaddingTop(), mPadding, getPaddingBottom());

        setMaxLines(1);
        setEllipsize(TextUtils.TruncateAt.END);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        setTextColor(mTextColor);
        if (!TextUtils.isEmpty(mText)) {
            setText(mText);
            setVisibility(VISIBLE);
        }

        if (mLeftIcon != null && mRightIcon != null) {
            setCompoundDrawables(mLeftIcon, null, mRightIcon, null);
        } else if (mLeftIcon != null) {
            setCompoundDrawables(mLeftIcon, null, null, null);
        } else if (mRightIcon != null) {
            setCompoundDrawables(null, null, mRightIcon, null);
        }
    }

    public Drawable getLeftIcon() {
        return mLeftIcon;
    }

    public void setLeftIcon(Drawable leftIcon) {
        this.mLeftIcon = leftIcon;
        if (mLeftIcon == null) {
            setCompoundDrawables(null, null, getCompoundDrawables()[2], null);
        } else {
            setCompoundDrawables(mLeftIcon, null, getCompoundDrawables()[2], null);
        }
    }

    public Drawable getRightIcon() {
        return mRightIcon;
    }

    public void setRightIcon(Drawable rightIcon) {
        this.mRightIcon = rightIcon;
        if (mRightIcon == null) {
            setCompoundDrawables(getCompoundDrawables()[0], null, null, null);
        } else {
            setCompoundDrawables(getCompoundDrawables()[0], null, mRightIcon, null);
        }
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        if (TextUtils.equals(mText, text)) {
            return;
        }
        this.mText = text;
        setText(mText);
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        if (mTextSize == textSize) {
            return;
        }
        if (textSize > 0) {
            this.mTextSize = textSize;
            setTextSize(textSize);
        }

    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        if (mTextColor == textColor) {
            return;
        }
        this.mTextColor = textColor;
        setTextColor(mTextColor);
    }

    public int getPadding() {
        return mPadding;
    }

    public void setPadding(int padding) {
        if (mPadding == padding) {
            return;
        }
        this.mPadding = padding;
        setPadding(mPadding, mPadding, mPadding, mPadding);
    }

    public int getInternalSpacing() {
        return mInternalSpacing;
    }

    public void setInternalSpacing(int internalSpacing) {
        if (mInternalSpacing == internalSpacing) {
            return;
        }
        if (internalSpacing > 0) {
            this.mInternalSpacing = internalSpacing;
            setCompoundDrawablePadding(mInternalSpacing);
            requestLayout();
        }
    }
}
