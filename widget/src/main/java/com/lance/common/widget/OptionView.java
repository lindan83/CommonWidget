package com.lance.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.lance.common.util.DensityUtil;

/**
 * Created by lindan on 16-11-1.
 * 命令选项View
 */

public class OptionView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "OptionView";
    private static final int DEFAULT_TEXT_SIZE = 14;//默认文本字体sp
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#606060");//默认文本颜色
    private static final int DEFAULT_LEFT_RIGHT_PADDING = 12;//默认左右内边距
    private static final int DEFAULT_INTERNAL_SPACING = 8;//默认Left Icon与Text的间距

    private int leftIconResId;
    private int rightIconResId;
    private String text;
    private float textSize;
    private int textColor;
    private int padding;
    private int internalSpacing;

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

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OptionView);
        leftIconResId = ta.getResourceId(R.styleable.OptionView_leftIcon, 0);
        rightIconResId = ta.getResourceId(R.styleable.OptionView_rightIcon, 0);
        text = ta.getString(R.styleable.OptionView_text);
        textSize = ta.getDimension(R.styleable.OptionView_textSize, DensityUtil.sp2px(getContext(), DEFAULT_TEXT_SIZE));
        textColor = ta.getColor(R.styleable.OptionView_textColor, DEFAULT_TEXT_COLOR);
        padding = (int) ta.getDimension(R.styleable.OptionView_padding, DensityUtil.dp2px(getContext(), DEFAULT_LEFT_RIGHT_PADDING));
        internalSpacing = (int) ta.getDimension(R.styleable.OptionView_internalSpacing, DensityUtil.dp2px(getContext(), DEFAULT_INTERNAL_SPACING));
        ta.recycle();
    }

    private void init() {
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(padding, getPaddingTop(), padding, getPaddingBottom());

        if (leftIconResId != 0 && rightIconResId != 0) {
            setCompoundDrawablesWithIntrinsicBounds(leftIconResId, 0, rightIconResId, 0);
        } else if (leftIconResId != 0) {
            setCompoundDrawablesWithIntrinsicBounds(leftIconResId, 0, 0, 0);
        } else if (rightIconResId != 0) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, rightIconResId, 0);
        }

        if (internalSpacing > 0) {
            setCompoundDrawablePadding(internalSpacing);
        }

        setMaxLines(1);
        setEllipsize(TextUtils.TruncateAt.END);
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        super.setTextColor(textColor);
        super.setText(text);
    }

    public Drawable getLeftIcon() {
        if (leftIconResId != 0) {
            return getResources().getDrawable(leftIconResId);
        }
        return null;
    }

    public void setLeftIconResId(int leftIconId) {
        this.leftIconResId = leftIconId;
        setCompoundDrawablesWithIntrinsicBounds(leftIconResId, 0, rightIconResId, 0);
    }

    public Drawable getRightIcon() {
        if (rightIconResId != 0) {
            return getResources().getDrawable(rightIconResId);
        }
        return null;
    }

    public void setRightIconResId(int rightIconResId) {
        this.rightIconResId = rightIconResId;
        setCompoundDrawablesWithIntrinsicBounds(leftIconResId, 0, this.rightIconResId, 0);
    }

    public void setLeftAndRightIconResIds(int leftIconId, int rightIconId) {
        this.leftIconResId = leftIconId;
        this.rightIconResId = rightIconId;
        setCompoundDrawablesWithIntrinsicBounds(leftIconResId, 0, rightIconResId, 0);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (TextUtils.equals(this.text, text)) {
            return;
        }
        this.text = text;
        setText(this.text);
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        if (this.textSize == textSize) {
            return;
        }
        if (textSize > 0) {
            this.textSize = textSize;
            setTextSize(textSize);
        }
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        if (this.textColor == textColor) {
            return;
        }
        this.textColor = textColor;
        setTextColor(this.textColor);
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        if (this.padding == padding) {
            return;
        }
        this.padding = padding;
        setPadding(this.padding, this.padding, this.padding, this.padding);
    }

    public int getInternalSpacing() {
        return internalSpacing;
    }

    public void setInternalSpacing(int internalSpacing) {
        if (this.internalSpacing == internalSpacing) {
            return;
        }
        if (internalSpacing > 0) {
            this.internalSpacing = internalSpacing;
            setCompoundDrawablePadding(this.internalSpacing);
            requestLayout();
        }
    }
}
