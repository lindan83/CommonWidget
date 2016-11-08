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

    private int mLeftIconResId;
    private int mRightIconResId;
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
        mLeftIconResId = ta.getResourceId(R.styleable.OptionView_leftIcon, 0);
        mRightIconResId = ta.getResourceId(R.styleable.OptionView_rightIcon, 0);
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

        if (mLeftIconResId != 0 && mRightIconResId != 0) {
            setCompoundDrawablesWithIntrinsicBounds(mLeftIconResId, 0, mRightIconResId, 0);
        } else if (mLeftIconResId != 0) {
            setCompoundDrawablesWithIntrinsicBounds(mLeftIconResId, 0, 0, 0);
        } else if (mRightIconResId != 0) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, mRightIconResId, 0);
        }

        if(mInternalSpacing > 0) {
            setCompoundDrawablePadding(mInternalSpacing);
        }

        setMaxLines(1);
        setEllipsize(TextUtils.TruncateAt.END);
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        super.setTextColor(mTextColor);
        super.setText(mText);
    }

    public Drawable getLeftIcon() {
        if (mLeftIconResId != 0) {
            return getResources().getDrawable(mLeftIconResId);
        }
        return null;
    }

    public void setLeftIconResId(int leftIconId) {
        this.mLeftIconResId = leftIconId;
        setCompoundDrawablesWithIntrinsicBounds(mLeftIconResId, 0, mRightIconResId, 0);
    }

    public Drawable getRightIcon() {
        if (mRightIconResId != 0) {
            return getResources().getDrawable(mRightIconResId);
        }
        return null;
    }

    public void setRightIconResId(int rightIconResId) {
        this.mRightIconResId = rightIconResId;
        setCompoundDrawablesWithIntrinsicBounds(mLeftIconResId, 0, mRightIconResId, 0);
    }

    public void setLeftAndRightIconResIds(int leftIconId, int rightIconId) {
        this.mLeftIconResId = leftIconId;
        this.mRightIconResId = rightIconId;
        setCompoundDrawablesWithIntrinsicBounds(mLeftIconResId, 0, mRightIconResId, 0);
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
