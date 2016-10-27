package com.lance.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lance.common.util.DensityUtil;

/**
 * Created by lindan on 16-10-25.
 * 通用可定制的TopBar
 */

public class TopBar extends RelativeLayout {
    private static final String TAG = "TopBar";

    private static final int ALIGNMENT_LEFT_TO_TEXT = 0;//图片在文本左边
    private static final int ALIGNMENT_RIGHT_TO_TEXT = 1;//图片在文本右边

    private static final int DEFAULT_TOP_BAR_HEIGHT = 48;//默认TopBar高度dp
    private static final int DEFAULT_TITLE_TEXT_COLOR = Color.BLACK;//默认标题颜色
    private static final float DEFAULT_TITLE_TEXT_SIZE = 20;//默认标题大小sp

    private static final boolean DEFAULT_LEFT_VISIBILITY = true;//默认显示左侧部分
    private static final int DEFAULT_LEFT_TEXT_COLOR = Color.BLACK;//默认左侧文字颜色
    private static final float DEFAULT_LEFT_TEXT_SIZE = 18;//默认左侧文字大小sp
    private static final int DEFAULT_LEFT_DRAWABLE_ALIGNMENT = ALIGNMENT_RIGHT_TO_TEXT;//左侧图片默认在文本右边
    private static final int DEFAULT_LEFT_PADDING = 16;//左侧默认内边距dp

    private static final boolean DEFAULT_RIGHT_VISIBILITY = false;//默认不显示右侧部分
    private static final int DEFAULT_RIGHT_TEXT_COLOR = Color.BLACK;//默认右侧文字颜色
    private static final float DEFAULT_RIGHT_TEXT_SIZE = 18;//默认右侧文字大小sp
    private static final int DEFAULT_RIGHT_DRAWABLE_ALIGNMENT = ALIGNMENT_LEFT_TO_TEXT;//左侧图片默认在文本左边
    private static final int DEFAULT_RIGHT_PADDING = 16;//右侧默认内边距dp

    private static final float DEFAULT_IMAGE_SIZE_RATIO = 0.6f;//图片是一个正方形，边长占TopBar的比例系数
    private static final float DEFAULT_INTERNAL_SPACING = 8;//默认标题、左侧、右侧之间的间距dp

    //标题相关
    private String mTitle;
    private int mTitleTextColor;
    private float mTitleTextSize;

    //左侧相关
    private boolean mLeftVisibility;
    private String mLeftText;
    private int mLeftTextColor;
    private float mLeftTextSize;
    private Drawable mLeftDrawable;
    private int mLeftDrawableAlignment;
    private int mLeftPadding;

    //右侧相关
    private boolean mRightVisibility;
    private String mRightText;
    private int mRightTextColor;
    private float mRightTextSize;
    private Drawable mRightDrawable;
    private int mRightDrawableAlignment;
    private int mRightPadding;

    //其他参数
    private int mInternalSpacing;//标题、左侧、右侧之间的间距

    public interface OnClickListener {
        void onClickLeft();

        void onClickTitle();

        void onClickRight();
    }

    private OnClickListener mOnClickListener;
    private View.OnClickListener mInternalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int id = view.getId();
            if (id == R.id.com_lance_common_widget_TopBar_title_id) {
                if (!TextUtils.isEmpty(mTitle) && mOnClickListener != null) {
                    mOnClickListener.onClickTitle();
                }
            } else if (id == R.id.com_lance_common_widget_TopBar_left_id) {
                if (mLeftVisibility && (!TextUtils.isEmpty(mLeftText) || mLeftDrawable != null) && mOnClickListener != null) {
                    mOnClickListener.onClickLeft();
                }
            } else if (id == R.id.com_lance_common_widget_TopBar_right_id) {
                if (mRightVisibility && (!TextUtils.isEmpty(mRightText) || mRightDrawable != null) && mOnClickListener != null) {
                    mOnClickListener.onClickRight();
                }
            }
        }
    };

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initViews();
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TopBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
        initViews();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        mTitle = ta.getString(R.styleable.TopBar_titleText);
        mTitleTextColor = ta.getColor(R.styleable.TopBar_titleTextColor, DEFAULT_TITLE_TEXT_COLOR);
        mTitleTextSize = ta.getDimension(R.styleable.TopBar_titleTextSize, DensityUtil.sp2px(context, DEFAULT_TITLE_TEXT_SIZE));

        mLeftVisibility = ta.getBoolean(R.styleable.TopBar_leftVisibility, DEFAULT_LEFT_VISIBILITY);
        mLeftText = ta.getString(R.styleable.TopBar_leftText);
        mLeftTextColor = ta.getColor(R.styleable.TopBar_leftTextColor, DEFAULT_LEFT_TEXT_COLOR);
        mLeftTextSize = ta.getDimension(R.styleable.TopBar_leftTextSize, DensityUtil.sp2px(context, DEFAULT_LEFT_TEXT_SIZE));
        mLeftDrawable = ta.getDrawable(R.styleable.TopBar_leftDrawable);
        mLeftDrawableAlignment = ta.getInt(R.styleable.TopBar_leftDrawableAlignment, DEFAULT_LEFT_DRAWABLE_ALIGNMENT);
        mLeftPadding = ta.getDimensionPixelSize(R.styleable.TopBar_leftPadding,
                DensityUtil.dp2px(context, DEFAULT_LEFT_PADDING));

        mRightVisibility = ta.getBoolean(R.styleable.TopBar_rightVisibility, DEFAULT_RIGHT_VISIBILITY);
        mRightText = ta.getString(R.styleable.TopBar_rightText);
        mRightTextColor = ta.getColor(R.styleable.TopBar_rightTextColor, DEFAULT_RIGHT_TEXT_COLOR);
        mRightTextSize = ta.getDimension(R.styleable.TopBar_rightTextSize, DensityUtil.sp2px(context, DEFAULT_RIGHT_TEXT_SIZE));
        mRightDrawable = ta.getDrawable(R.styleable.TopBar_rightDrawable);
        mRightDrawableAlignment = ta.getInt(R.styleable.TopBar_rightDrawableAlignment, DEFAULT_RIGHT_DRAWABLE_ALIGNMENT);
        mRightPadding = ta.getDimensionPixelSize(R.styleable.TopBar_rightPadding,
                DensityUtil.dp2px(context, DEFAULT_RIGHT_PADDING));

        mInternalSpacing = (int) ta.getDimension(R.styleable.TopBar_internalSpacing, DensityUtil.dp2px(context, DEFAULT_INTERNAL_SPACING));
        ta.recycle();
    }

    private void initViews() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                int height = getMeasuredHeight();
                int width = getMeasuredWidth();
                Log.d(TAG, "onPreDraw: width = " + width + ", height = " + height);
                //添加标题
                createTitleView();
                //添加左侧部分
                createLeftView();
                //添加右侧部分
                createRightView();
                return true;
            }
        });
    }

    private void createTitleView() {
        if (!TextUtils.isEmpty(mTitle)) {
            TextView titleTextView = new TextView(getContext());
            titleTextView.setId(R.id.com_lance_common_widget_TopBar_title_id);
            titleTextView.setText(mTitle);
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
            titleTextView.setTextColor(mTitleTextColor);
            titleTextView.setMaxLines(1);
            titleTextView.setEllipsize(TextUtils.TruncateAt.END);
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            //如果不显示左右两部分，为标题设置左右边距
            if ((!mLeftVisibility || (TextUtils.isEmpty(mLeftText)) && mLeftDrawable == null)
                    && (!mRightVisibility || (TextUtils.isEmpty(mRightText)) && mRightDrawable == null)) {
                titleParams.leftMargin = mLeftPadding;
                titleParams.rightMargin = mRightPadding;
            } else {
                if (!mLeftVisibility || (TextUtils.isEmpty(mLeftText)) && mLeftDrawable == null) {
                    //存在左边
                    titleParams.leftMargin = DensityUtil.dp2px(getContext(), 8);
                } else if (!mRightVisibility || (TextUtils.isEmpty(mRightText)) && mRightDrawable == null) {
                    //存在右边
                    titleParams.rightMargin = DensityUtil.dp2px(getContext(), 8);
                } else {
                    titleParams.leftMargin = titleParams.rightMargin = mInternalSpacing;
                }
            }
            titleTextView.setLayoutParams(titleParams);
            titleTextView.setOnClickListener(mInternalListener);
            addView(titleTextView);
        }
    }

    private void createLeftView() {
        if (mLeftVisibility) {
            if (!TextUtils.isEmpty(mLeftText) && mLeftDrawable != null) {
                //如果左侧同时存在文本与图片，则为其加上一个包装layout
                RelativeLayout leftLayout = new RelativeLayout(getContext());
                leftLayout.setId(R.id.com_lance_common_widget_TopBar_left_id);
                RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//居于左侧
                leftParams.addRule(RelativeLayout.CENTER_VERTICAL);//垂直居中
                if (!TextUtils.isEmpty(mTitle)) {
                    leftParams.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_title_id);//位于标题左侧
                    leftParams.rightMargin = mInternalSpacing;//距离标题
                }
                if (mLeftPadding > 0) {
                    leftParams.leftMargin = mLeftPadding;
                }
                leftLayout.setLayoutParams(leftParams);

                ImageView leftImage = new ImageView(getContext());
                int parentHeight = getHeight();
                int imageSize = (int) ((parentHeight - getPaddingBottom() - getPaddingTop()) * DEFAULT_IMAGE_SIZE_RATIO);
                LayoutParams leftImageParams = new LayoutParams(imageSize, imageSize);
                LayoutParams leftTextParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                leftImage.setId(R.id.com_lance_common_widget_TopBar_left_image_id);
                leftImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                leftImage.setImageDrawable(mLeftDrawable);
                TextView leftText = new TextView(getContext());
                leftText.setId(R.id.com_lance_common_widget_TopBar_left_text_id);
                leftText.setAllCaps(false);
                leftText.setMaxLines(1);
                leftText.setEllipsize(TextUtils.TruncateAt.END);//尾部截断
                leftText.setText(mLeftText);
                leftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);
                leftText.setTextColor(mLeftTextColor);
                if (mLeftDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                    //图片在文本左侧
                    leftImageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    leftImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    leftTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    leftTextParams.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_left_image_id);
                    leftTextParams.leftMargin = mInternalSpacing / 2;
                } else if (mLeftDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                    //图片在文本右侧
                    leftTextParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    leftTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    leftImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    leftImageParams.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_left_text_id);
                    leftImageParams.leftMargin = mInternalSpacing / 2;
                }
                leftImage.setLayoutParams(leftImageParams);
                leftText.setLayoutParams(leftTextParams);
                leftLayout.addView(leftImage);
                leftLayout.addView(leftText);
                leftLayout.setOnClickListener(mInternalListener);
                addView(leftLayout);
            } else if (!TextUtils.isEmpty(mLeftText)) {
                //左侧只有文本
                TextView leftTextView = createTextView(mLeftText, mLeftTextSize, mLeftTextColor);
                leftTextView.setId(R.id.com_lance_common_widget_TopBar_left_id);
                RelativeLayout.LayoutParams leftParams = (LayoutParams) leftTextView.getLayoutParams();
                leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//居于左侧
                leftParams.addRule(RelativeLayout.CENTER_VERTICAL);//垂直居中
                if (!TextUtils.isEmpty(mTitle)) {
                    leftParams.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_title_id);//位于标题左侧
                    leftParams.rightMargin = mInternalSpacing;//距离标题
                }
                if (mLeftPadding > 0) {
                    leftParams.leftMargin = mLeftPadding;
                }
                leftTextView.setLayoutParams(leftParams);
                leftTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                leftTextView.setOnClickListener(mInternalListener);
                addView(leftTextView);
            } else if (mLeftDrawable != null) {
                //左侧只有图片
                ImageView leftImageView = createImageView(mLeftDrawable);
                leftImageView.setId(R.id.com_lance_common_widget_TopBar_left_id);
                RelativeLayout.LayoutParams leftParams = (LayoutParams) leftImageView.getLayoutParams();
                leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//居于左侧
                leftParams.addRule(RelativeLayout.CENTER_VERTICAL);//垂直居中
                if (mLeftPadding > 0) {
                    leftParams.leftMargin = mLeftPadding;
                }
                leftImageView.setLayoutParams(leftParams);
                leftImageView.setOnClickListener(mInternalListener);
                addView(leftImageView);
            }
        }
    }

    private void createRightView() {
        if (mRightVisibility) {
            if (!TextUtils.isEmpty(mRightText) && mRightDrawable != null) {
                //如果右侧同时存在文本与图片，则为其加上一个包装layout
                RelativeLayout rightLayout = new RelativeLayout(getContext());
                rightLayout.setId(R.id.com_lance_common_widget_TopBar_right_id);
                RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//居于右侧
                rightParams.addRule(RelativeLayout.CENTER_VERTICAL);//垂直居中
                if (!TextUtils.isEmpty(mTitle)) {
                    rightParams.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_title_id);//位于标题右侧
                    rightParams.leftMargin = mInternalSpacing;//距离标题
                }
                if (mRightPadding > 0) {
                    rightParams.rightMargin = mRightPadding;
                }
                rightLayout.setLayoutParams(rightParams);

                ImageView rightImage = new ImageView(getContext());
                int parentHeight = getHeight();
                int imageSize = (int) ((parentHeight - getPaddingBottom() - getPaddingTop()) * DEFAULT_IMAGE_SIZE_RATIO);
                LayoutParams rightImageParams = new LayoutParams(imageSize, imageSize);
                LayoutParams rightTextParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rightImage.setId(R.id.com_lance_common_widget_TopBar_right_image_id);
                rightImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                rightImage.setImageDrawable(mRightDrawable);
                TextView rightText = new TextView(getContext());
                rightText.setId(R.id.com_lance_common_widget_TopBar_right_text_id);
                rightText.setAllCaps(false);
                rightText.setMaxLines(1);
                rightText.setEllipsize(TextUtils.TruncateAt.END);//尾部截断
                rightText.setText(mRightText);
                rightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
                rightText.setTextColor(mRightTextColor);
                if (mRightDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                    //图片在文本左侧
                    rightTextParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    rightTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    rightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    rightImageParams.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_right_text_id);
                    rightImageParams.rightMargin = mInternalSpacing / 2;
                } else if (mRightDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                    //图片在文本右侧
                    rightImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    rightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    rightTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    rightTextParams.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_right_image_id);
                    rightTextParams.rightMargin = mInternalSpacing / 2;
                }
                rightImage.setLayoutParams(rightImageParams);
                rightText.setLayoutParams(rightTextParams);
                rightLayout.addView(rightImage);
                rightLayout.addView(rightText);
                rightLayout.setOnClickListener(mInternalListener);
                addView(rightLayout);
            } else if (!TextUtils.isEmpty(mRightText)) {
                //右侧只有文本
                TextView rightTextView = createTextView(mRightText, mRightTextSize, mRightTextColor);
                rightTextView.setId(R.id.com_lance_common_widget_TopBar_right_id);
                RelativeLayout.LayoutParams rightParams = (LayoutParams) rightTextView.getLayoutParams();
                rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//居于右侧
                rightParams.addRule(RelativeLayout.CENTER_VERTICAL);//垂直居中
                if (!TextUtils.isEmpty(mTitle)) {
                    rightParams.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_title_id);//位于标题左侧
                    rightParams.leftMargin = mInternalSpacing;//距离标题
                }
                if (mRightPadding > 0) {
                    rightParams.rightMargin = mRightPadding;
                }
                rightTextView.setLayoutParams(rightParams);
                rightTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                rightTextView.setOnClickListener(mInternalListener);
                addView(rightTextView);
            } else if (mRightDrawable != null) {
                //右侧只有图片
                ImageView rightImageView = createImageView(mRightDrawable);
                rightImageView.setId(R.id.com_lance_common_widget_TopBar_right_id);
                RelativeLayout.LayoutParams rightParams = (LayoutParams) rightImageView.getLayoutParams();
                rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//居于右侧
                rightParams.addRule(RelativeLayout.CENTER_VERTICAL);//垂直居中
                if (mRightPadding > 0) {
                    rightParams.rightMargin = mRightPadding;
                }
                rightImageView.setLayoutParams(rightParams);
                rightImageView.setOnClickListener(mInternalListener);
                addView(rightImageView);
            }
        }
    }

    private TextView createTextView(String text, float textSize, int textColor) {
        TextView textView = new TextView(getContext());
        textView.setAllCaps(false);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);//尾部截断
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(textColor);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private ImageView createImageView(Drawable drawable) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(drawable);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int parentHeight = getHeight();
        layoutParams.width = layoutParams.height = (int) ((parentHeight - getPaddingBottom() - getPaddingTop()) * DEFAULT_IMAGE_SIZE_RATIO);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightValue = 0;
        if (heightMode == MeasureSpec.EXACTLY) {
            heightValue = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //如果设置高度为wrap_content，则设置固定高度为默认值
            heightValue = DensityUtil.dp2px(getContext(), DEFAULT_TOP_BAR_HEIGHT);
        }
        setMeasuredDimension(getMeasuredWidth(), heightValue);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.mTitleTextColor = titleTextColor;
    }

    public float getTitleTextSize() {
        return mTitleTextSize;
    }

    public void setTitleTextSize(float titleTextSize) {
        this.mTitleTextSize = titleTextSize;
    }

    public boolean isLeftVisible() {
        return mLeftVisibility;
    }

    public void setLeftVisible(boolean leftVisible) {
        this.mLeftVisibility = leftVisible;
    }

    public String getLeftText() {
        return mLeftText;
    }

    public void setLeftText(String leftText) {
        this.mLeftText = leftText;
    }

    public int getLeftTextColor() {
        return mLeftTextColor;
    }

    public void setLeftTextColor(int leftTextColor) {
        this.mLeftTextColor = leftTextColor;
    }

    public float getLeftTextSize() {
        return mLeftTextSize;
    }

    public void setLeftTextSize(float leftTextSize) {
        this.mLeftTextSize = leftTextSize;
    }

    public Drawable getLeftDrawable() {
        return mLeftDrawable;
    }

    public void setLeftDrawable(Drawable leftDrawable) {
        this.mLeftDrawable = leftDrawable;
    }

    public int getLeftDrawableAlignment() {
        return mLeftDrawableAlignment;
    }

    public void setLeftDrawableAlignment(int leftDrawableAlignment) {
        this.mLeftDrawableAlignment = leftDrawableAlignment;
    }

    public int getLeftPadding() {
        return mLeftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.mLeftPadding = leftPadding;
    }

    public boolean isRightVisible() {
        return mRightVisibility;
    }

    public void setRightVisible(boolean rightVisible) {
        this.mRightVisibility = rightVisible;
    }

    public String getRightText() {
        return mRightText;
    }

    public void setRightText(String rightText) {
        this.mRightText = rightText;
    }

    public int getRightTextColor() {
        return mRightTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        this.mRightTextColor = rightTextColor;
    }

    public float getRightTextSize() {
        return mRightTextSize;
    }

    public void setRightTextSize(float rightTextSize) {
        this.mRightTextSize = rightTextSize;
    }

    public Drawable getRightDrawable() {
        return mRightDrawable;
    }

    public void setRightDrawable(Drawable rightDrawable) {
        this.mRightDrawable = rightDrawable;
    }

    public int getRightDrawableAlignment() {
        return mRightDrawableAlignment;
    }

    public void setRightDrawableAlignment(int rightDrawableAlignment) {
        this.mRightDrawableAlignment = rightDrawableAlignment;
    }

    public int getRightPadding() {
        return mRightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.mRightPadding = rightPadding;
    }

    public void setOnClickTopBarListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }
}
