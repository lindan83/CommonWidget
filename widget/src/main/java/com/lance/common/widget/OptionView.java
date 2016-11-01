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
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lance.common.util.DensityUtil;

/**
 * Created by lindan on 16-11-1.
 * 命令选项View
 */

public class OptionView extends RelativeLayout {
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

    private ImageView mLeftImageView;
    private ImageView mRightImageView;
    private TextView mTextView;

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
        LayoutInflater.from(getContext()).inflate(R.layout.layout_option_view, this, true);
        setPadding(mPadding, mPadding, mPadding, mPadding);

        mLeftImageView = (ImageView) findViewById(R.id.iv_left_icon);
        mTextView = (TextView) findViewById(R.id.iv_text);
        mRightImageView = (ImageView) findViewById(R.id.iv_right_icon);

        setViewParams();

        if (mLeftIcon != null) {
            mLeftImageView.setImageDrawable(mLeftIcon);
            mLeftImageView.setVisibility(VISIBLE);
        } else {
            mLeftImageView.setImageDrawable(null);
            mLeftImageView.setVisibility(GONE);
        }

        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTextView.setTextColor(mTextColor);
        if (!TextUtils.isEmpty(mText)) {
            mTextView.setText(mText);
            mTextView.setVisibility(VISIBLE);
        } else {
            mTextView.setText(null);
            mTextView.setVisibility(GONE);
        }

        if (mRightIcon != null) {
            mRightImageView.setImageDrawable(mRightIcon);
            mRightImageView.setVisibility(VISIBLE);
        } else {
            mRightImageView.setImageDrawable(null);
            mRightImageView.setVisibility(GONE);
        }
    }

    private void setViewParams() {
        RelativeLayout.LayoutParams leftImageParam = (LayoutParams) mLeftImageView.getLayoutParams();
        RelativeLayout.LayoutParams textParam = (LayoutParams) mTextView.getLayoutParams();
        RelativeLayout.LayoutParams rightImageParam = (LayoutParams) mRightImageView.getLayoutParams();

        if (mInternalSpacing > 0) {
            if (mLeftIcon != null) {
                textParam.leftMargin = mInternalSpacing;
            } else {
                textParam.leftMargin = 0;
            }
            if (mRightIcon != null) {
                textParam.rightMargin = mInternalSpacing;
            } else {
                textParam.rightMargin = 0;
            }
        }

        if (mLeftIcon == null) {
            textParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }

        mLeftImageView.setLayoutParams(leftImageParam);
        mTextView.setLayoutParams(textParam);
        mRightImageView.setLayoutParams(rightImageParam);
    }

    public Drawable getLeftIcon() {
        return mLeftIcon;
    }

    public void setLeftIcon(Drawable leftIcon) {
        this.mLeftIcon = leftIcon;
        if (mLeftIcon == null) {
            mLeftImageView.setImageDrawable(null);
            mLeftImageView.setVisibility(GONE);
        } else {
            mLeftImageView.setImageDrawable(mLeftIcon);
            mLeftImageView.setVisibility(VISIBLE);
        }
        setViewParams();
    }

    public Drawable getRightIcon() {
        return mRightIcon;
    }

    public void setRightIcon(Drawable rightIcon) {
        this.mRightIcon = rightIcon;
        if (mRightIcon == null) {
            mRightImageView.setImageDrawable(null);
            mRightImageView.setVisibility(GONE);
        } else {
            mRightImageView.setImageDrawable(mRightIcon);
            mRightImageView.setVisibility(VISIBLE);
        }
        setViewParams();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        if (TextUtils.equals(mText, text)) {
            return;
        }
        this.mText = text;
        if (TextUtils.isEmpty(mText)) {
            mTextView.setText(null);
            mTextView.setVisibility(GONE);
        } else {
            mTextView.setText(mText);
            mTextView.setVisibility(VISIBLE);
        }
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
            mTextView.setTextSize(textSize);
        }

    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        if(mTextColor == textColor) {
            return;
        }
        this.mTextColor = textColor;
        mTextView.setTextColor(mTextColor);
    }

    public int getPadding() {
        return mPadding;
    }

    public void setPadding(int padding) {
        if(mPadding == padding) {
            return;
        }
        this.mPadding = padding;
        setPadding(mPadding, mPadding, mPadding, mPadding);
    }

    public int getInternalSpacing() {
        return mInternalSpacing;
    }

    public void setInternalSpacing(int internalSpacing) {
        if(mInternalSpacing == internalSpacing) {
            return;
        }
        if(internalSpacing > 0) {
            this.mInternalSpacing = internalSpacing;
            setViewParams();
            requestLayout();
        }
    }
}
