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
    private String title;
    private int titleTextColor;
    private float titleTextSize;
    private boolean titleVisibility;

    //左侧相关
    private boolean leftVisibility;
    private String leftText;
    private int leftTextColor;
    private float leftTextSize;
    private Drawable leftDrawable;
    private int leftDrawableAlignment;
    private int leftPadding;

    //右侧相关
    private boolean rightVisibility;
    private String rightText;
    private int rightTextColor;
    private float rightTextSize;
    private Drawable rightDrawable;
    private int rightDrawableAlignment;
    private int rightPadding;

    //自定义相关
    private int customTitleViewLayoutId;
    private int customLeftViewLayoutId;
    private int customRightViewLayoutId;

    //其他参数
    private int internalSpacing;//标题、左侧、右侧之间的间距

    private View titleView;
    private boolean isCustomTitleView;//是否自定义的TitleView
    private View leftView;
    private boolean isCustomLeftView;//是否自定义的LeftView
    private View rightView;
    private boolean isCustomRightView;//是否自定义的RightView

    private int height;
    private int drawableSize;

    public interface OnClickListener {
        void onClickLeft();

        void onClickTitle();

        void onClickRight();
    }

    private OnClickListener onClickListener;
    private View.OnClickListener internalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int id = view.getId();
            if (id == R.id.com_lance_common_widget_TopBar_title_id) {
                if (findViewById(R.id.com_lance_common_widget_TopBar_title_id) != null && onClickListener != null) {
                    onClickListener.onClickTitle();
                }
            } else if (id == R.id.com_lance_common_widget_TopBar_left_id) {
                if (findViewById(R.id.com_lance_common_widget_TopBar_left_id) != null && onClickListener != null) {
                    onClickListener.onClickLeft();
                }
            } else if (id == R.id.com_lance_common_widget_TopBar_right_id) {
                if (findViewById(R.id.com_lance_common_widget_TopBar_right_id) != null && onClickListener != null) {
                    onClickListener.onClickRight();
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
        title = ta.getString(R.styleable.TopBar_titleText);
        titleTextColor = ta.getColor(R.styleable.TopBar_titleTextColor, DEFAULT_TITLE_TEXT_COLOR);
        titleTextSize = ta.getDimension(R.styleable.TopBar_titleTextSize, DensityUtil.sp2px(context, DEFAULT_TITLE_TEXT_SIZE));
        titleVisibility = ta.getBoolean(R.styleable.TopBar_titleVisibility, DEFAULT_TITLE_VISIBILITY);

        leftVisibility = ta.getBoolean(R.styleable.TopBar_leftVisibility, DEFAULT_LEFT_VISIBILITY);
        leftText = ta.getString(R.styleable.TopBar_leftText);
        leftTextColor = ta.getColor(R.styleable.TopBar_leftTextColor, DEFAULT_LEFT_TEXT_COLOR);
        leftTextSize = ta.getDimension(R.styleable.TopBar_leftTextSize, DensityUtil.sp2px(context, DEFAULT_LEFT_TEXT_SIZE));
        leftDrawable = ta.getDrawable(R.styleable.TopBar_leftDrawable);
        leftDrawableAlignment = ta.getInt(R.styleable.TopBar_leftDrawableAlignment, DEFAULT_LEFT_DRAWABLE_ALIGNMENT);
        leftPadding = ta.getDimensionPixelSize(R.styleable.TopBar_leftPadding,
                DensityUtil.dp2px(context, DEFAULT_LEFT_PADDING));

        rightVisibility = ta.getBoolean(R.styleable.TopBar_rightVisibility, DEFAULT_RIGHT_VISIBILITY);
        rightText = ta.getString(R.styleable.TopBar_rightText);
        rightTextColor = ta.getColor(R.styleable.TopBar_rightTextColor, DEFAULT_RIGHT_TEXT_COLOR);
        rightTextSize = ta.getDimension(R.styleable.TopBar_rightTextSize, DensityUtil.sp2px(context, DEFAULT_RIGHT_TEXT_SIZE));
        rightDrawable = ta.getDrawable(R.styleable.TopBar_rightDrawable);
        rightDrawableAlignment = ta.getInt(R.styleable.TopBar_rightDrawableAlignment, DEFAULT_RIGHT_DRAWABLE_ALIGNMENT);
        rightPadding = ta.getDimensionPixelSize(R.styleable.TopBar_rightPadding,
                DensityUtil.dp2px(context, DEFAULT_RIGHT_PADDING));

        internalSpacing = (int) ta.getDimension(R.styleable.TopBar_internalSpacing, DensityUtil.dp2px(context, DEFAULT_INTERNAL_SPACING));

        customTitleViewLayoutId = ta.getResourceId(R.styleable.TopBar_customTitleViewLayoutId, 0);
        customLeftViewLayoutId = ta.getResourceId(R.styleable.TopBar_customLeftViewLayoutId, 0);
        customRightViewLayoutId = ta.getResourceId(R.styleable.TopBar_customRightViewLayoutId, 0);
        ta.recycle();

        isCustomTitleView = customTitleViewLayoutId != 0;
        isCustomLeftView = customLeftViewLayoutId != 0;
        isCustomRightView = customRightViewLayoutId != 0;
    }

    private void initViews() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                height = getMeasuredHeight();
                drawableSize = (int) ((height - getPaddingTop() - getPaddingBottom()) * DEFAULT_IMAGE_SIZE_RATIO);
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
        if (customTitleViewLayoutId != 0) {
            titleView = LayoutInflater.from(getContext()).inflate(customTitleViewLayoutId, null);
            titleView.setVisibility(titleVisibility ? VISIBLE : GONE);
            titleView.setId(R.id.com_lance_common_widget_TopBar_title_id);
            titleView.setOnClickListener(internalListener);
        }
        if (customLeftViewLayoutId != 0) {
            leftView = LayoutInflater.from(getContext()).inflate(customLeftViewLayoutId, null);
            leftView.setVisibility(leftVisibility ? VISIBLE : GONE);
            leftView.setId(R.id.com_lance_common_widget_TopBar_left_id);
            leftView.setOnClickListener(internalListener);
        }
        if (customRightViewLayoutId != 0) {
            rightView = LayoutInflater.from(getContext()).inflate(customRightViewLayoutId, null);
            rightView.setVisibility(rightVisibility ? VISIBLE : GONE);
            rightView.setId(R.id.com_lance_common_widget_TopBar_right_id);
            rightView.setOnClickListener(internalListener);
        }

        //对于没有指定的自定义布局，则直接使用其他指定相关的属性构建
        if (titleView == null) {
            titleView = createDefaultTitleView();
        }
        if (leftView == null) {
            leftView = createDefaultLeftOrRightView(true);
        }
        if (rightView == null) {
            rightView = createDefaultLeftOrRightView(false);
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
        if (title != null) {
            titleTextView.setText(title);
        }
        titleTextView.setVisibility(titleVisibility ? VISIBLE : GONE);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        titleTextView.setTextColor(titleTextColor);
        titleTextView.setMaxLines(1);
        titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        titleTextView.setOnClickListener(internalListener);
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
            textView.setText(leftText);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
            textView.setTextColor(leftTextColor);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setCompoundDrawablePadding(internalSpacing / 2);
            if (leftDrawable != null) {
                leftDrawable.setBounds(0, 0, drawableSize, drawableSize);
                if (leftDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                    textView.setCompoundDrawables(leftDrawable, null, null, null);
                } else if (leftDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                    textView.setCompoundDrawables(null, null, leftDrawable, null);
                }
            }
        } else {
            //设置右侧View 文本
            textView.setId(R.id.com_lance_common_widget_TopBar_right_id);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(rightText);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
            textView.setTextColor(rightTextColor);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setCompoundDrawablePadding(internalSpacing / 2);
            if (rightDrawable != null) {
                rightDrawable.setBounds(0, 0, drawableSize, drawableSize);
                if (rightDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                    textView.setCompoundDrawables(rightDrawable, null, null, null);
                } else if (rightDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                    textView.setCompoundDrawables(null, null, rightDrawable, null);
                }
            }
        }
        if (left) {
            wrapOuterLayout.setVisibility(leftVisibility ? VISIBLE : GONE);
            if (TextUtils.isEmpty(leftText) && leftDrawable == null) {
                wrapOuterLayout.setVisibility(GONE);
            }
        } else {
            wrapOuterLayout.setVisibility(rightVisibility ? VISIBLE : GONE);
            if (TextUtils.isEmpty(rightText) && rightDrawable == null) {
                wrapOuterLayout.setVisibility(GONE);
            }
        }
        textView.setOnClickListener(internalListener);
        wrapOuterLayout.addView(textView);
        return textView;
    }

    /**
     * 设置内部Views到正确位置
     */
    private void setInternalViews() {
        removeAllViews();
        //设置标题
        if (isCustomTitleView) {
            //如果是自定义标题View
            if (titleView != null) {
                //先获取原布局参数
                ViewGroup.LayoutParams oldTitleParams = titleView.getLayoutParams();
                RelativeLayout.LayoutParams newTitleParams;
                if (oldTitleParams != null) {
                    newTitleParams = new LayoutParams(oldTitleParams);
                } else {
                    newTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                titleView.setLayoutParams(newTitleParams);
                addView(titleView);
            }
        } else {
            //如果不是自定义标题View
            if (titleView != null) {
                ViewGroup.LayoutParams oldTitleParams = titleView.getLayoutParams();
                RelativeLayout.LayoutParams newTitleParams;
                if (oldTitleParams != null) {
                    newTitleParams = new LayoutParams(oldTitleParams);
                } else {
                    newTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                //如果左侧不可见，或没有设置文本与图片
                if (!leftVisibility || (TextUtils.isEmpty(leftText) && leftDrawable == null)) {
                    if (leftPadding > 0) {
                        newTitleParams.leftMargin = leftPadding;
                    }
                } else {
                    newTitleParams.leftMargin = internalSpacing;
                }
                //如果右侧不可见，或没有设置文本与图片
                if (!rightVisibility || (TextUtils.isEmpty(rightText) && rightDrawable == null)) {
                    if (rightPadding > 0) {
                        newTitleParams.rightMargin = rightPadding;
                    }
                } else {
                    newTitleParams.rightMargin = internalSpacing;
                }
                titleView.setLayoutParams(newTitleParams);
                titleView.setOnClickListener(internalListener);
                addView(titleView);
            }
        }
        //设置Left View
        if (isCustomLeftView) {
            //如果是自定义Left View
            if (leftView != null) {
                //先获取原布局参数，对于自定义View，只进行定位，不对其他参数进行干预
                ViewGroup.LayoutParams oldLeftParams = leftView.getLayoutParams();
                RelativeLayout.LayoutParams newLeftParams;
                if (oldLeftParams != null) {
                    newLeftParams = new LayoutParams(oldLeftParams);
                } else {
                    newLeftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newLeftParams.addRule(RelativeLayout.CENTER_VERTICAL);
                newLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                if (leftPadding > 0) {
                    newLeftParams.leftMargin = leftPadding;
                }
                if (titleView != null && titleVisibility) {
                    newLeftParams.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_title_id);
                    newLeftParams.rightMargin = internalSpacing;
                }
                leftView.setLayoutParams(newLeftParams);
                addView(leftView);
            }
        } else {
            //如果不是自定义Left View
            //如果最外层包装的FrameLayout，设定边界
            FrameLayout leftWrap = (FrameLayout) leftView.getParent();
            //如果左侧的View外层是个FrameLayout，表明是默认创建的View
            LayoutParams wrapParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            wrapParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            wrapParam.addRule(RelativeLayout.CENTER_VERTICAL);
            //如果标题存在且可见
            if (titleVisibility && titleView != null && !TextUtils.isEmpty(title)) {
                wrapParam.addRule(RelativeLayout.LEFT_OF, R.id.com_lance_common_widget_TopBar_title_id);
            }
            if (leftPadding > 0) {
                wrapParam.leftMargin = leftPadding;
            }
            FrameLayout.LayoutParams leftParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            leftParam.gravity = Gravity.LEFT;
            leftView.setLayoutParams(leftParam);
            leftWrap.setLayoutParams(wrapParam);
            addView(leftWrap);
        }
        //设置Right View
        if (isCustomRightView) {
            //如果是自定义Right View
            //如果是自定义Left View
            if (rightView != null) {
                //先获取原布局参数，对于自定义View，只进行定位，不对其他参数进行干预
                ViewGroup.LayoutParams oldRightParams = rightView.getLayoutParams();
                RelativeLayout.LayoutParams newRightParams;
                if (oldRightParams != null) {
                    newRightParams = new LayoutParams(oldRightParams);
                } else {
                    newRightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                newRightParams.addRule(RelativeLayout.CENTER_VERTICAL);
                newRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                if (rightPadding > 0) {
                    newRightParams.rightMargin = rightPadding;
                }
                if (titleView != null && titleVisibility) {
                    newRightParams.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_title_id);
                    newRightParams.leftMargin = internalSpacing;
                }
                rightView.setLayoutParams(newRightParams);
                addView(rightView);
            }
        } else {
            //如果不是自定义Right View
            //如果最外层包装的FrameLayout，设定边界
            FrameLayout rightWrap = (FrameLayout) rightView.getParent();
            //如果右侧的View外层是个FrameLayout，表明是默认创建的View
            LayoutParams wrapParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            wrapParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            wrapParam.addRule(RelativeLayout.CENTER_VERTICAL);
            //如果标题存在且可见
            if (titleVisibility && titleView != null && !TextUtils.isEmpty(title)) {
                wrapParam.addRule(RelativeLayout.RIGHT_OF, R.id.com_lance_common_widget_TopBar_title_id);
            }
            if (rightPadding > 0) {
                wrapParam.rightMargin = rightPadding;
            }
            //设置RelativeLayout宽高自适应
            FrameLayout.LayoutParams rightViewParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rightViewParam.gravity = Gravity.RIGHT;
            rightView.setLayoutParams(rightViewParam);
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
        height = h;
        drawableSize = (int) ((height - getPaddingBottom() - getPaddingTop()) * DEFAULT_IMAGE_SIZE_RATIO);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (TextUtils.equals(this.title, title)) {
            return;
        }
        if (!isCustomTitleView) {
            this.title = title;
            ((TextView) titleView).setText(this.title);
        }
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        if (this.titleTextColor == titleTextColor) {
            return;
        }
        if (!isCustomTitleView) {
            this.titleTextColor = titleTextColor;
            ((TextView) titleView).setTextColor(this.titleTextColor);
        }
    }

    public float getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(float titleTextSize) {
        if (this.titleTextSize == titleTextSize) {
            return;
        }
        if (!isCustomTitleView) {
            this.titleTextSize = titleTextSize;
            ((TextView) titleView).setTextSize(this.titleTextSize);
        }
    }

    public boolean isTitleVisible() {
        return titleVisibility;
    }

    public void setTitleVisible(boolean titleVisible) {
        if (titleVisibility == titleVisible) {
            return;
        }
        this.titleVisibility = titleVisible;
        titleView.setVisibility(titleVisibility ? VISIBLE : GONE);
    }

    public boolean isLeftVisible() {
        return leftVisibility;
    }

    public void setLeftVisible(boolean leftVisible) {
        if (leftVisibility == leftVisible) {
            return;
        }
        this.leftVisibility = leftVisible;
        if (isCustomLeftView) {
            leftView.setVisibility(leftVisibility ? VISIBLE : GONE);
        } else {
            ((FrameLayout) leftView.getParent()).setVisibility(leftVisibility ? VISIBLE : GONE);
        }
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        if (TextUtils.equals(this.leftText, leftText)) {
            return;
        }
        if (!isCustomLeftView) {
            this.leftText = leftText;
            ((TextView) leftView).setText(this.leftText);
        }
    }

    public int getLeftTextColor() {
        return leftTextColor;
    }

    public void setLeftTextColor(int leftTextColor) {
        if (this.leftTextColor == leftTextColor) {
            return;
        }
        if (!isCustomLeftView) {
            this.leftTextColor = leftTextColor;
            ((TextView) leftView).setTextColor(this.leftTextColor);
        }
    }

    public float getLeftTextSize() {
        return leftTextSize;
    }

    public void setLeftTextSize(float leftTextSize) {
        if (this.leftTextSize == leftTextSize) {
            return;
        }
        if (!isCustomLeftView) {
            this.leftTextSize = leftTextSize;
            ((TextView) leftView).setTextSize(this.leftTextSize);
        }
    }

    public Drawable getLeftDrawable() {
        return leftDrawable;
    }

    public void setLeftDrawable(Drawable leftDrawable) {
        if (!isCustomLeftView) {
            this.leftDrawable = leftDrawable;
            this.leftDrawable.setBounds(0, 0, drawableSize, drawableSize);
            final TextView textView = (TextView) leftView;
            if (leftDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(this.leftDrawable, null, null, null);
            } else if (leftDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, this.leftDrawable, null);
            }
        }
    }

    public void setLeftDrawable(Drawable leftDrawable, int alignment) {
        if (!isCustomLeftView) {
            if (alignment != ALIGNMENT_RIGHT_TO_TEXT && alignment != ALIGNMENT_LEFT_TO_TEXT) {
                throw new IllegalArgumentException("alignment value must be ALIGNMENT_RIGHT_TO_TEXT or ALIGNMENT_LEFT_TO_TEXT");
            }
            this.leftDrawable = leftDrawable;
            this.leftDrawableAlignment = alignment;
            this.leftDrawable.setBounds(0, 0, drawableSize, drawableSize);
            final TextView textView = (TextView) leftView;
            if (leftDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(this.leftDrawable, null, null, null);
            } else if (leftDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, this.leftDrawable, null);
            }
        }
    }

    public int getLeftDrawableAlignment() {
        return leftDrawableAlignment;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        if (this.leftPadding == leftPadding) {
            return;
        }
        if (this.leftPadding > 0) {
            this.leftPadding = leftPadding;
            if (isCustomLeftView) {
                RelativeLayout.LayoutParams leftParam = (LayoutParams) leftView.getLayoutParams();
                leftParam.leftMargin = this.leftPadding;
            } else {
                FrameLayout frameLayout = (FrameLayout) leftView.getParent();
                RelativeLayout.LayoutParams leftParam = (LayoutParams) frameLayout.getLayoutParams();
                leftParam.leftMargin = this.leftPadding;
            }
            requestLayout();
        }
    }

    public boolean isRightVisible() {
        return rightVisibility;
    }

    public void setRightVisible(boolean rightVisible) {
        if (rightVisibility == rightVisible) {
            return;
        }
        this.rightVisibility = rightVisible;
        if (isCustomRightView) {
            rightView.setVisibility(rightVisibility ? VISIBLE : GONE);
        } else {
            ((FrameLayout) rightView.getParent()).setVisibility(rightVisibility ? VISIBLE : GONE);
        }
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        if (TextUtils.equals(this.rightText, rightText)) {
            return;
        }
        if (!isCustomRightView) {
            this.rightText = rightText;
            ((TextView) rightView).setText(this.rightText);
        }
    }

    public int getRightTextColor() {
        return rightTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        if (this.rightTextColor == rightTextColor) {
            return;
        }
        if (!isCustomRightView) {
            this.rightTextColor = rightTextColor;
            ((TextView) rightView).setTextColor(this.rightTextColor);
        }
    }

    public float getRightTextSize() {
        return rightTextSize;
    }

    public void setRightTextSize(float rightTextSize) {
        if (this.rightTextSize == rightTextSize) {
            return;
        }
        if (!isCustomRightView) {
            this.rightTextSize = rightTextSize;
            ((TextView) rightView).setTextSize(this.rightTextSize);
        }
    }

    public Drawable getRightDrawable() {
        return rightDrawable;
    }

    public void setRightDrawable(Drawable rightDrawable) {
        if (!isCustomRightView) {
            this.rightDrawable = rightDrawable;
            this.rightDrawable.setBounds(0, 0, drawableSize, drawableSize);
            final TextView textView = (TextView) rightView;
            if (rightDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(this.rightDrawable, null, null, null);
            } else if (rightDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, this.rightDrawable, null);
            }
        }
    }

    public void setRightDrawable(Drawable rightDrawable, int alignment) {
        if (!isCustomRightView) {
            if (alignment != ALIGNMENT_RIGHT_TO_TEXT && alignment != ALIGNMENT_LEFT_TO_TEXT) {
                throw new IllegalArgumentException("alignment value must be ALIGNMENT_RIGHT_TO_TEXT or ALIGNMENT_LEFT_TO_TEXT");
            }
            this.rightDrawable = rightDrawable;
            this.rightDrawableAlignment = alignment;
            this.rightDrawable.setBounds(0, 0, drawableSize, drawableSize);
            final TextView textView = (TextView) rightView;
            if (rightDrawableAlignment == ALIGNMENT_LEFT_TO_TEXT) {
                textView.setCompoundDrawables(this.rightDrawable, null, null, null);
            } else if (rightDrawableAlignment == ALIGNMENT_RIGHT_TO_TEXT) {
                textView.setCompoundDrawables(null, null, this.rightDrawable, null);
            }
        }
    }

    public int getRightDrawableAlignment() {
        return rightDrawableAlignment;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        if (this.rightPadding == rightPadding) {
            return;
        }
        if (this.rightPadding > 0) {
            this.rightPadding = rightPadding;
            if (isCustomRightView) {
                RelativeLayout.LayoutParams rightParam = (LayoutParams) rightView.getLayoutParams();
                rightParam.rightMargin = this.rightPadding;
            } else {
                FrameLayout frameLayout = (FrameLayout) rightView.getParent();
                RelativeLayout.LayoutParams rightParam = (LayoutParams) frameLayout.getLayoutParams();
                rightParam.rightMargin = this.rightPadding;
            }
            requestLayout();
        }
    }

    public View getTitleView() {
        return titleView;
    }

    public View getLeftView() {
        return leftView;
    }

    public View getRightView() {
        return rightView;
    }

    public void setCustomTitleView(View titleView) {
        if (titleView == null) {
            return;
        }
        this.titleView = titleView;
        isCustomTitleView = true;
        title = null;
        titleVisibility = titleView.getVisibility() == VISIBLE;
        setInternalViews();
    }

    public void setCustomLeftView(View leftView) {
        if (leftView == null) {
            return;
        }
        this.leftView = leftView;
        isCustomLeftView = true;
        leftText = null;
        leftDrawable = null;
        leftVisibility = leftView.getVisibility() == VISIBLE;
        setInternalViews();
    }

    public void setCustomRightView(View rightView) {
        if (rightView == null) {
            return;
        }
        this.rightView = rightView;
        isCustomRightView = true;
        rightText = null;
        rightDrawable = null;
        rightVisibility = rightView.getVisibility() == VISIBLE;
        setInternalViews();
    }

    public void setOnClickTopBarListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
