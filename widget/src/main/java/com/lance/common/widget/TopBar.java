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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lance.common.util.DensityUtil;

/**
 * Created by lindan on 16-10-25.
 * 通用可定制的TopBar
 */

public class TopBar extends RelativeLayout {
    private static final String TAG = "TopBar";

    public static final int ALIGNMENT_LEFT_TO_TEXT = 0;//图片在文本左边
    public static final int ALIGNMENT_RIGHT_TO_TEXT = 1;//图片在文本右边

    private static final int DEFAULT_TOP_BAR_HEIGHT = 48;//默认TopBar高度dp
    private static final int DEFAULT_TITLE_TEXT_COLOR = Color.BLACK;//默认标题颜色
    private static final float DEFAULT_TITLE_TEXT_SIZE = 20;//默认标题大小sp
    private static final boolean DEFAULT_TITLE_VISIBILITY = true;//默认显示标题View

    private static final boolean DEFAULT_LEFT_VISIBILITY = true;//默认显示左侧部分
    private static final int DEFAULT_LEFT_TEXT_COLOR = Color.BLACK;//默认左侧文字颜色
    private static final float DEFAULT_LEFT_TEXT_SIZE = 18;//默认左侧文字大小sp
    private static final int DEFAULT_LEFT_DRAWABLE_ALIGNMENT = ALIGNMENT_LEFT_TO_TEXT;//左侧图片默认在文本左边
    private static final int DEFAULT_LEFT_PADDING = 12;//左侧默认内边距dp

    private static final boolean DEFAULT_RIGHT_VISIBILITY = false;//默认不显示右侧部分
    private static final int DEFAULT_RIGHT_TEXT_COLOR = Color.BLACK;//默认右侧文字颜色
    private static final float DEFAULT_RIGHT_TEXT_SIZE = 18;//默认右侧文字大小sp
    private static final int DEFAULT_RIGHT_DRAWABLE_ALIGNMENT = ALIGNMENT_RIGHT_TO_TEXT;//左侧图片默认在文本右边
    private static final int DEFAULT_RIGHT_PADDING = 12;//右侧默认内边距dp

    private static final float DEFAULT_IMAGE_SIZE_RATIO = 0.6f;//图片是一个正方形，边长占TopBar的比例系数
    private static final float DEFAULT_INTERNAL_SPACING = 8;//默认标题、左侧、右侧之间的间距dp

    //标题相关
    private String mTitle;
    private int mTitleTextColor;
    private float mTitleTextSize;
    private boolean mTitleVisibility;

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

    //自定义相关
    private int mCustomTitleViewLayoutId;
    private int mCustomLeftViewLayoutId;
    private int mCustomRightViewLayoutId;

    //其他参数
    private int mInternalSpacing;//标题、左侧、右侧之间的间距

    private View mTitleView;
    private boolean mIsCustomTitleView;//是否自定义的TitleView
    private View mLeftView;
    private boolean mIsCustomLeftView;//是否自定义的LeftView
    private View mRightView;
    private boolean mIsCustomRightView;//是否自定义的RightView

    private int mHeight;
    private int mDrawableSize;

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
                if (findViewById(R.id.com_lance_common_widget_TopBar_title_id) != null && mOnClickListener != null) {
                    mOnClickListener.onClickTitle();
                }
            } else if (id == R.id.com_lance_common_widget_TopBar_left_id) {
                if (findViewById(R.id.com_lance_common_widget_TopBar_left_id) != null && mOnClickListener != null) {
                    mOnClickListener.onClickLeft();
                }
            } else if (id == R.id.com_lance_common_widget_TopBar_right_id) {
                if (findViewById(R.id.com_lance_common_widget_TopBar_right_id) != null && mOnClickListener != null) {
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
        mTitleVisibility = ta.getBoolean(R.styleable.TopBar_titleVisibility, DEFAULT_TITLE_VISIBILITY);

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

        mCustomTitleViewLayoutId = ta.getResourceId(R.styleable.TopBar_customTitleViewLayoutId, 0);
        mCustomLeftViewLayoutId = ta.getResourceId(R.styleable.TopBar_customLeftViewLayoutId, 0);
        mCustomRightViewLayoutId = ta.getResourceId(R.styleable.TopBar_customRightViewLayoutId, 0);
        ta.recycle();

        mIsCustomTitleView = mCustomTitleViewLayoutId != 0;
        mIsCustomLeftView = mCustomLeftViewLayoutId != 0;
        mIsCustomRightView = mCustomRightViewLayoutId != 0;
    }

    private void initViews() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                mHeight = getMeasuredHeight();
                mDrawableSize = (int) ((mHeight - getPaddingTop() - getPaddingBottom()) * DEFAULT_IMAGE_SIZE_RATIO);
                generateInternalViews();
                return true;
            }
        });
    }

    /**
     * 创建内部Views，如果用户既指定某一位置的自定义View Layout ID，又指定同一位置的其他相关属性，将优先使用自定义View
     * 如果没有指定自定义View Layout ID，将利用同一位置的其他相关属性创建默认View
     */
    private void generateInternalViews() {
        //如果用户指定了自定义的标题Layout，则优先选择自定义标题
        if (mCustomTitleViewLayoutId != 0) {
            mTitleView = LayoutInflater.from(getContext()).inflate(mCustomTitleViewLayoutId, null);
            mTitleView.setVisibility(mTitleVisibility ? VISIBLE : GONE);
            mTitleView.setId(R.id.com_lance_common_widget_TopBar_title_id);
            mTitleView.setOnClickListener(mInternalListener);
        }
        if (mCustomLeftViewLayoutId != 0) {
            mLeftView = LayoutInflater.from(getContext()).inflate(mCustomLeftViewLayoutId, null);
            mLeftView.setVisibility(mLeftVisibility ? VISIBLE : GONE);
            mLeftView.setId(R.id.com_lance_common_widget_TopBar_left_id);
            mLeftView.setOnClickListener(mInternalListener);
        }
        if (mCustomRightViewLayoutId != 0) {
            mRightView = LayoutInflater.from(getContext()).inflate(mCustomRightViewLayoutId, null);
            mRightView.setVisibility(mRightVisibility ? VISIBLE : GONE);
            mRightView.setId(R.id.com_lance_common_widget_TopBar_right_id);
            mRightView.setOnClickListener(mInternalListener);
        }

        //对于没有指定的自定义布局，则直接使用其他指定相关的属性构建
        if (mTitleView == null) {
            mTitleView = createDefaultTitleView();
        }
        if (mLeftView == null) {
            mLeftView = createDefaultLeftOrRightView(true);
        }
        if (mRightView == null) {
            mRightView = createDefaultLeftOrRightView(false);
        }
        setInternalViews();
    }

    /**
     * 创建默认标题View
     *
     * @return Title View
     */
    private View createDefaultTitleView() {
        TextView titleTextView = new TextView(getContext());
        titleTextView.setId(R.id.com_lance_common_widget_TopBar_title_id);
        if (mTitle != null) {
            titleTextView.setText(mTitle);
        }
        titleTextView.setVisibility(mTitleVisibility ? VISIBLE : GONE);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
        titleTextView.setTextColor(mTitleTextColor);
        titleTextView.setMaxLines(1);
        titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        titleTextView.setOnClickListener(mInternalListener);
        return titleTextView;
    }

    /**
     * 创建左侧或右侧View
     *
     * @param left 创建左边还是右边的View true为左边
     * @return Left Or Right View
     */
    private View createDefaultLeftOrRightView(boolean left) {
        //在最外层增加一个FrameLayout规范位置与边界
        FrameLayout wrapOuterLayout = new FrameLayout(getContext());
        wrapOuterLayout.setId(left ? R.id.com_lance_common_widget_TopBar_left_wrap_id : R.id.com_lance_common_widget_TopBar_right_wrap_id);
        TextView textView = new TextView(getContext());
        if (left) {
            //设置左侧View 文本
            textView.setId(R.id.com_lance_common_widget_TopBar_left_id);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(mLeftText);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);
            textView.setTextColor(mLeftTextColor);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setCompoundDrawablePadding(mInternalSpacing / 2);
            if (mLeftDrawable != null) {
                mLeftDrawable.setBounds(0, 0, mDrawableSize, mDrawableSize);
                if (mLeftDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                    textView.setCompoundDrawables(mLeftDrawable, null, null, null);
                } else if (mLeftDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                    textView.setCompoundDrawables(null, mLeftDrawable, null, null);
                }
            }
        } else {
            //设置右侧View 文本
            textView.setId(R.id.com_lance_common_widget_TopBar_right_id);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(mRightText);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
            textView.setTextColor(mRightTextColor);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setCompoundDrawablePadding(mInternalSpacing / 2);
            if (mRightDrawable != null) {
                mRightDrawable.setBounds(0, 0, mDrawableSize, mDrawableSize);
                if (mRightDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                    textView.setCompoundDrawables(mRightDrawable, null, null, null);
                } else if (mRightDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                    textView.setCompoundDrawables(null, mRightDrawable, null, null);
                }
            }
        }
        if (left) {
            wrapOuterLayout.setVisibility(mLeftVisibility ? VISIBLE : GONE);
            if (TextUtils.isEmpty(mLeftText) && mLeftDrawable == null) {
                wrapOuterLayout.setVisibility(GONE);
            }
        } else {
            wrapOuterLayout.setVisibility(mRightVisibility ? VISIBLE : GONE);
            if (TextUtils.isEmpty(mRightText) && mRightDrawable == null) {
                wrapOuterLayout.setVisibility(GONE);
            }
        }
        textView.setOnClickListener(mInternalListener);
        wrapOuterLayout.addView(textView);
        return textView;
    }

    /**
     * 设置内部Views到正确位置
     */
    private void setInternalViews() {
        removeAllViews();
        //设置标题
        if (mIsCustomTitleView) {
            //如果是自定义标题View
            if (mTitleView != null) {
                //先获取原布局参数
                ViewGroup.LayoutParams oldTitleParams = mTitleView.getLayoutParams();
                RelativeLayout.LayoutParams newTitleParams;
                if (oldTitleParams != null) {
                    newTitleParams = new LayoutParams(oldTitleParams);
                } else {
                    newTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mTitleView.setLayoutParams(newTitleParams);
                addView(mTitleView);
            }
        } else {
            //如果不是自定义标题View
            if (mTitleView != null) {
                ViewGroup.LayoutParams oldTitleParams = mTitleView.getLayoutParams();
                RelativeLayout.LayoutParams newTitleParams;
                if (oldTitleParams != null) {
                    newTitleParams = new LayoutParams(oldTitleParams);
                } else {
                    newTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                //如果左侧不可见，或没有设置文本与图片
                if (!mLeftVisibility || (TextUtils.isEmpty(mLeftText) && mLeftDrawable == null)) {
                    if (mLeftPadding > 0) {
                        newTitleParams.leftMargin = mLeftPadding;
                    }
                } else {
                    newTitleParams.leftMargin = mInternalSpacing;
                }
                //如果右侧不可见，或没有设置文本与图片
                if (!mRightVisibility || (TextUtils.isEmpty(mRightText) && mRightDrawable == null)) {
                    if (mRightPadding > 0) {
                        newTitleParams.rightMargin = mRightPadding;
                    }
                } else {
                    newTitleParams.rightMargin = mInternalSpacing;
                }
                mTitleView.setLayoutParams(newTitleParams);
                mTitleView.setOnClickListener(mInternalListener);
                addView(mTitleView);
            }
        }
        //设置Left View
        if (mIsCustomLeftView) {
            //如果是自定义Left View
            if (mLeftView != null) {
                //先获取原布局参数，对于自定义View，只进行定位，不对其他参数进行干预
                ViewGroup.LayoutParams oldLeftParams = mLeftView.getLayoutParams();
                RelativeLayout.LayoutParams newLeftParams;
                if (oldLeftParams != null) {
                    newLeftParams = new LayoutParams(oldLeftParams);
                } else {
                    newLeftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newLeftParams.addRule(RelativeLayout.CENTER_VERTICAL);
                newLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                if (mLeftPadding > 0) {
                    newLeftParams.leftMargin = mLeftPadding;
                }
                if (mTitleView != null && mTitleVisibility) {
                    newLeftParams.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_title_id);
                    newLeftParams.rightMargin = mInternalSpacing;
                }
                mLeftView.setLayoutParams(newLeftParams);
                addView(mLeftView);
            }
        } else {
            //如果不是自定义Left View
            //如果最外层包装的FrameLayout，设定边界
            FrameLayout leftWrap = (FrameLayout) mLeftView.getParent();
            //如果左侧的View外层是个FrameLayout，表明是默认创建的View
            LayoutParams wrapParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            wrapParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            wrapParam.addRule(RelativeLayout.CENTER_VERTICAL);
            //如果标题存在且可见
            if (mTitleVisibility && mTitleView != null && !TextUtils.isEmpty(mTitle)) {
                wrapParam.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_title_id);
            }
            if (mLeftPadding > 0) {
                wrapParam.leftMargin = mLeftPadding;
            }
            FrameLayout.LayoutParams leftParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            leftParam.gravity = Gravity.LEFT;
            mLeftView.setLayoutParams(leftParam);
            leftWrap.setLayoutParams(wrapParam);
            addView(leftWrap);
        }
        //设置Right View
        if (mIsCustomRightView) {
            //如果是自定义Right View
            //如果是自定义Left View
            if (mRightView != null) {
                //先获取原布局参数，对于自定义View，只进行定位，不对其他参数进行干预
                ViewGroup.LayoutParams oldRightParams = mRightView.getLayoutParams();
                RelativeLayout.LayoutParams newRightParams;
                if (oldRightParams != null) {
                    newRightParams = new LayoutParams(oldRightParams);
                } else {
                    newRightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newRightParams.addRule(RelativeLayout.CENTER_VERTICAL);
                newRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                if (mRightPadding > 0) {
                    newRightParams.rightMargin = mRightPadding;
                }
                if (mTitleView != null && mTitleVisibility) {
                    newRightParams.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_title_id);
                    newRightParams.leftMargin = mInternalSpacing;
                }
                mRightView.setLayoutParams(newRightParams);
                addView(mRightView);
            }
        } else {
            //如果不是自定义Right View
            //如果最外层包装的FrameLayout，设定边界
            FrameLayout rightWrap = (FrameLayout) mRightView.getParent();
            //如果右侧的View外层是个FrameLayout，表明是默认创建的View
            LayoutParams wrapParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            wrapParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            wrapParam.addRule(RelativeLayout.CENTER_VERTICAL);
            //如果标题存在且可见
            if (mTitleVisibility && mTitleView != null && !TextUtils.isEmpty(mTitle)) {
                wrapParam.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_title_id);
            }
            if (mRightPadding > 0) {
                wrapParam.rightMargin = mRightPadding;
            }
            //设置RelativeLayout宽高自适应
            FrameLayout.LayoutParams rightViewParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rightViewParam.gravity = Gravity.RIGHT;
            mRightView.setLayoutParams(rightViewParam);
            rightWrap.setLayoutParams(wrapParam);
            addView(rightWrap);
        }
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mDrawableSize = (int) ((mHeight - getPaddingBottom() - getPaddingTop()) * DEFAULT_IMAGE_SIZE_RATIO);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        if (TextUtils.equals(mTitle, title)) {
            return;
        }
        if (!mIsCustomTitleView) {
            this.mTitle = title;
            ((TextView) mTitleView).setText(mTitle);
        }
    }

    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        if (mTitleTextColor == titleTextColor) {
            return;
        }
        if (!mIsCustomTitleView) {
            this.mTitleTextColor = titleTextColor;
            ((TextView) mTitleView).setTextColor(mTitleTextColor);
        }
    }

    public float getTitleTextSize() {
        return mTitleTextSize;
    }

    public void setTitleTextSize(float titleTextSize) {
        if (mTitleTextSize == titleTextSize) {
            return;
        }
        if (!mIsCustomTitleView) {
            this.mTitleTextSize = titleTextSize;
            ((TextView) mTitleView).setTextSize(mTitleTextSize);
        }
    }

    public boolean isTitleVisible() {
        return mTitleVisibility;
    }

    public void setTitleVisible(boolean titleVisible) {
        if (mTitleVisibility == titleVisible) {
            return;
        }
        this.mTitleVisibility = titleVisible;
        mTitleView.setVisibility(mTitleVisibility ? VISIBLE : GONE);
    }

    public boolean isLeftVisible() {
        return mLeftVisibility;
    }

    public void setLeftVisible(boolean leftVisible) {
        if (mLeftVisibility == leftVisible) {
            return;
        }
        this.mLeftVisibility = leftVisible;
        if (mIsCustomLeftView) {
            mLeftView.setVisibility(mLeftVisibility ? VISIBLE : GONE);
        } else {
            ((FrameLayout) mLeftView.getParent()).setVisibility(mLeftVisibility ? VISIBLE : GONE);
        }
    }

    public String getLeftText() {
        return mLeftText;
    }

    public void setLeftText(String leftText) {
        if (TextUtils.equals(mLeftText, leftText)) {
            return;
        }
        if (!mIsCustomLeftView) {
            this.mLeftText = leftText;
            ((TextView) mLeftView).setText(mLeftText);
        }
    }

    public int getLeftTextColor() {
        return mLeftTextColor;
    }

    public void setLeftTextColor(int leftTextColor) {
        if (mLeftTextColor == leftTextColor) {
            return;
        }
        if (!mIsCustomLeftView) {
            this.mLeftTextColor = leftTextColor;
            ((TextView) mLeftView).setTextColor(mLeftTextColor);
        }
    }

    public float getLeftTextSize() {
        return mLeftTextSize;
    }

    public void setLeftTextSize(float leftTextSize) {
        if (mLeftTextSize == leftTextSize) {
            return;
        }
        if (!mIsCustomLeftView) {
            this.mLeftTextSize = leftTextSize;
            ((TextView) mLeftView).setTextSize(mLeftTextSize);
        }
    }

    public Drawable getLeftDrawable() {
        return mLeftDrawable;
    }

    public void setLeftDrawable(Drawable leftDrawable) {
        if (!mIsCustomLeftView) {
            this.mLeftDrawable = leftDrawable;
            this.mLeftDrawable.setBounds(0, 0, mDrawableSize, mDrawableSize);
            final TextView textView = (TextView) mLeftView;
            if (mLeftDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(mLeftDrawable, null, null, null);
            } else if (mLeftDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, mLeftDrawable, null);
            }
        }
    }

    public void setLeftDrawable(Drawable leftDrawable, int alignment) {
        if (!mIsCustomLeftView) {
            if(alignment != ALIGNMENT_RIGHT_TO_TEXT && alignment != ALIGNMENT_LEFT_TO_TEXT) {
                throw new IllegalArgumentException("alignment value must be ALIGNMENT_RIGHT_TO_TEXT or ALIGNMENT_LEFT_TO_TEXT");
            }
            this.mLeftDrawable = leftDrawable;
            this.mLeftDrawableAlignment = alignment;
            this.mLeftDrawable.setBounds(0, 0, mDrawableSize, mDrawableSize);
            final TextView textView = (TextView) mLeftView;
            if (mLeftDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(mLeftDrawable, null, null, null);
            } else if (mLeftDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, mLeftDrawable, null);
            }
        }
    }

    public int getLeftDrawableAlignment() {
        return mLeftDrawableAlignment;
    }

    public int getLeftPadding() {
        return mLeftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        if (mLeftPadding == leftPadding) {
            return;
        }
        if (mLeftPadding > 0) {
            this.mLeftPadding = leftPadding;
            if (mIsCustomLeftView) {
                RelativeLayout.LayoutParams leftParam = (LayoutParams) mLeftView.getLayoutParams();
                leftParam.leftMargin = mLeftPadding;
            } else {
                FrameLayout frameLayout = (FrameLayout) mLeftView.getParent();
                RelativeLayout.LayoutParams leftParam = (LayoutParams) frameLayout.getLayoutParams();
                leftParam.leftMargin = mLeftPadding;
            }
            requestLayout();
        }
    }

    public boolean isRightVisible() {
        return mRightVisibility;
    }

    public void setRightVisible(boolean rightVisible) {
        if (mRightVisibility == rightVisible) {
            return;
        }
        this.mRightVisibility = rightVisible;
        if (mIsCustomRightView) {
            mRightView.setVisibility(mRightVisibility ? VISIBLE : GONE);
        } else {
            ((FrameLayout) mRightView.getParent()).setVisibility(mRightVisibility ? VISIBLE : GONE);
        }
    }

    public String getRightText() {
        return mRightText;
    }

    public void setRightText(String rightText) {
        if (TextUtils.equals(mRightText, rightText)) {
            return;
        }
        if (!mIsCustomRightView) {
            this.mRightText = rightText;
            ((TextView) mRightView).setText(mRightText);
        }
    }

    public int getRightTextColor() {
        return mRightTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        if (mRightTextColor == rightTextColor) {
            return;
        }
        if (!mIsCustomRightView) {
            this.mRightTextColor = rightTextColor;
            ((TextView) mRightView).setTextColor(mRightTextColor);
        }
    }

    public float getRightTextSize() {
        return mRightTextSize;
    }

    public void setRightTextSize(float rightTextSize) {
        if (mRightTextSize == rightTextSize) {
            return;
        }
        if (!mIsCustomRightView) {
            this.mRightTextSize = rightTextSize;
            ((TextView) mRightView).setTextSize(mRightTextSize);
        }
    }

    public Drawable getRightDrawable() {
        return mRightDrawable;
    }

    public void setRightDrawable(Drawable rightDrawable) {
        if (!mIsCustomRightView) {
            this.mRightDrawable = rightDrawable;
            this.mRightDrawable.setBounds(0, 0, mDrawableSize, mDrawableSize);
            final TextView textView = (TextView) mRightView;
            if (mRightDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(mRightDrawable, null, null, null);
            } else if (mRightDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, mRightDrawable, null);
            }
        }
    }

    public void setRightDrawable(Drawable rightDrawable, int alignment) {
        if (!mIsCustomRightView) {
            if(alignment != ALIGNMENT_RIGHT_TO_TEXT && alignment != ALIGNMENT_LEFT_TO_TEXT) {
                throw new IllegalArgumentException("alignment value must be ALIGNMENT_RIGHT_TO_TEXT or ALIGNMENT_LEFT_TO_TEXT");
            }
            this.mRightDrawable = rightDrawable;
            this.mRightDrawableAlignment = alignment;
            this.mRightDrawable.setBounds(0, 0, mDrawableSize, mDrawableSize);
            final TextView textView = (TextView) mRightView;
            if (mRightDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(mRightDrawable, null, null, null);
            } else if (mRightDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, mRightDrawable, null);
            }
        }
    }

    public int getRightDrawableAlignment() {
        return mRightDrawableAlignment;
    }

    public int getRightPadding() {
        return mRightPadding;
    }

    public void setRightPadding(int rightPadding) {
        if (mRightPadding == rightPadding) {
            return;
        }
        if (mRightPadding > 0) {
            this.mRightPadding = rightPadding;
            if (mIsCustomRightView) {
                RelativeLayout.LayoutParams rightParam = (LayoutParams) mRightView.getLayoutParams();
                rightParam.rightMargin = mRightPadding;
            } else {
                FrameLayout frameLayout = (FrameLayout) mRightView.getParent();
                RelativeLayout.LayoutParams rightParam = (LayoutParams) frameLayout.getLayoutParams();
                rightParam.rightMargin = mRightPadding;
            }
            requestLayout();
        }
    }

    public View getTitleView() {
        return mTitleView;
    }

    public View getLeftView() {
        return mLeftView;
    }

    public View getRightView() {
        return mRightView;
    }

    public void setCustomTitleView(View titleView) {
        if (titleView == null) {
            return;
        }
        mTitleView = titleView;
        mIsCustomTitleView = true;
        mTitle = null;
        mTitleVisibility = titleView.getVisibility() == VISIBLE;
        setInternalViews();
    }

    public void setCustomLeftView(View leftView) {
        if (leftView == null) {
            return;
        }
        mLeftView = leftView;
        mIsCustomLeftView = true;
        mLeftText = null;
        mLeftDrawable = null;
        mLeftVisibility = leftView.getVisibility() == VISIBLE;
        setInternalViews();
    }

    public void setCustomRightView(View rightView) {
        if (rightView == null) {
            return;
        }
        mRightView = rightView;
        mIsCustomRightView = true;
        mRightText = null;
        mRightDrawable = null;
        mRightVisibility = rightView.getVisibility() == VISIBLE;
        setInternalViews();
    }

    public void setOnClickTopBarListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }
}
