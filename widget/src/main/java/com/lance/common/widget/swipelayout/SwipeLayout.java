package com.lance.common.widget.swipelayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.lance.common.widget.R;

/**
 * 【Item侧滑菜单】
 * 继承自ViewGroup，实现滑动出现删除等选项的效果，
 * 思路：跟随手势将item向左滑动，
 * 在onMeasure时 将第一个Item设为屏幕宽度
 * 【解决屏幕上多个侧滑删除菜单】：内设一个类静态View类型变量 ViewCache，存储的是当前正处于右滑状态的CstSwipeMenuItemViewGroup，
 * 每次Touch时对比，如果两次Touch的不是一个View，那么令ViewCache恢复普通状态，并且设置新的CacheView
 * 只要有一个侧滑菜单处于打开状态， 就不给外层布局上下滑动了
 */
public class SwipeLayout extends ViewGroup {
    private static final String TAG = "SwipeLayout";

    private int scaleTouchSlop;//为了处理单击事件的冲突
    private int maxVelocity;//计算滑动速度用
    private int pointerId;//多点触摸只算第一根手指的速度
    private int rightMenuWidths;//右侧菜单宽度总和(最大滑动距离)
    private int limit;//滑动判定临界值（右侧菜单宽度的40%） 手指抬起时，超过了展开，没超过收起menu

    private View contentView;//存储contentView(第一个View)


    private PointF lastP = new PointF();//上一次的xy
    //仿QQ，侧滑菜单展开时，点击除侧滑菜单之外的区域，关闭侧滑菜单。
    //增加一个布尔值变量，dispatch函数里，每次down时，为true，move时判断，如果是滑动动作，设为false。
    //在Intercept函数的up时，判断这个变量，如果仍为true 说明是点击事件，则关闭菜单。 
    private boolean isUnMoved = true;

    //判断手指起始落点，如果距离属于滑动了，就屏蔽一切点击事件。
    //up-down的坐标，判断是否是滑动，如果是，则屏蔽一切点击事件
    private PointF firstP = new PointF();
    private boolean isUserSwiped;

    //存储的是当前正在展开的View
    private static SwipeLayout viewCache;

    //防止多只手指一起滑我的flag 在每次down里判断， touch事件结束清空
    private static boolean isTouching;

    private VelocityTracker velocityTracker;//滑动速度变量
    private boolean isSwipeEnable;//右滑删除功能的开关,默认开
    private boolean isIos;//IOS、QQ式交互，默认开
    private boolean iosInterceptFlag;//IOS类型下，是否拦截事件的flag
    private boolean isLeftSwipe;//左滑右滑的开关,默认左滑打开菜单

    private ValueAnimator expandAnim, closeAnim;//平滑展开

    private boolean isExpand;//代表当前是否是展开状态

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public boolean isSwipeEnable() {
        return isSwipeEnable;
    }

    /**
     * 设置侧滑功能开关
     *
     * @param swipeEnable 是否允许侧滑
     */
    public void setSwipeEnable(boolean swipeEnable) {
        isSwipeEnable = swipeEnable;
    }


    /**
     * 是否IOS阻塞式交互
     *
     * @return 是否IOS阻塞式交互
     */
    public boolean isIos() {
        return isIos;
    }

    /**
     * 设置是否开启IOS阻塞式交互
     *
     * @param ios 是否IOS阻塞式交互
     */
    public SwipeLayout setIos(boolean ios) {
        isIos = ios;
        return this;
    }

    public boolean isLeftSwipe() {
        return isLeftSwipe;
    }

    /**
     * 设置是否开启左滑出菜单，设置false 为右滑出菜单
     *
     * @param leftSwipe 是否左侧滑出
     */
    public SwipeLayout setLeftSwipe(boolean leftSwipe) {
        isLeftSwipe = leftSwipe;
        return this;
    }

    /**
     * 返回ViewCache
     */
    public static SwipeLayout getViewCache() {
        return viewCache;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        ViewConfiguration vc = ViewConfiguration.get(context);
        scaleTouchSlop = vc.getScaledTouchSlop();
        maxVelocity = vc.getScaledMaximumFlingVelocity();
        //初始化滑动帮助类对象
        //scroller = new Scroller(context);

        //右滑删除功能的开关,默认开
        isSwipeEnable = true;
        //IOS、QQ式交互，默认开
        isIos = true;
        //左滑右滑的开关,默认左滑打开菜单
        isLeftSwipe = true;
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwipeLayout, defStyleAttr, 0);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = ta.getIndex(i);
            //如果引用成AndroidLib 资源都不是常量，无法使用switch case
            if (attr == R.styleable.SwipeLayout_swipeEnable) {
                isSwipeEnable = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.SwipeLayout_ios) {
                isIos = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.SwipeLayout_leftSwipeEnable) {
                isLeftSwipe = ta.getBoolean(attr, true);
            }
        }
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.d(TAG, "onMeasure() called with: " + "widthMeasureSpec = [" + widthMeasureSpec + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setClickable(true);//令自己可点击，从而获取触摸事件

        rightMenuWidths = 0;//由于ViewHolder的复用机制，每次这里要手动恢复初始值
        int mHeight = 0;
        int contentWidth = 0;//2016 11 09 add,适配GridLayoutManager，将以第一个子Item(即ContentItem)的宽度为控件宽度
        int childCount = getChildCount();

        //为了子View的高，可以matchParent(参考的FrameLayout 和LinearLayout的Horizontal)
        final boolean measureMatchParentChildren = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        boolean isNeedMeasureChildHeight = false;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //令每一个子View可点击，从而获取触摸事件
            childView.setClickable(true);
            if (childView.getVisibility() != GONE) {
                //后续计划加入上滑、下滑，则将不再支持Item的margin
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                //measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                mHeight = Math.max(mHeight, childView.getMeasuredHeight()/* + lp.topMargin + lp.bottomMargin*/);
                if (measureMatchParentChildren && lp.height == LayoutParams.MATCH_PARENT) {
                    isNeedMeasureChildHeight = true;
                }
                if (i > 0) {
                    //第一个布局是Left item，从第二个开始才是RightMenu
                    rightMenuWidths += childView.getMeasuredWidth();
                } else {
                    contentView = childView;
                    contentWidth = childView.getMeasuredWidth();
                }
            }
        }
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentWidth,
                mHeight + getPaddingTop() + getPaddingBottom());//宽度取第一个Item(Content)的宽度
        limit = rightMenuWidths * 4 / 10;//滑动判断的临界值
        //Log.d(TAG, "onMeasure() called with: " + "rightMenuWidths = [" + rightMenuWidths);
        if (isNeedMeasureChildHeight) {
            //如果子View的height有MatchParent属性的，设置子View高度
            forceUniformHeight(childCount, widthMeasureSpec);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 给MatchParent的子View设置高度
     *
     * @param count            子View个数
     * @param widthMeasureSpec 父View的widthMeasureSpec
     */
    private void forceUniformHeight(int count, int widthMeasureSpec) {
        // Pretend that the linear layout has an exact size. This is the measured height of
        // ourselves. The measured height should be the max height of the children, changed
        // to accommodate the heightMeasureSpec from the parent
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
                MeasureSpec.EXACTLY);//以父布局高度构建一个Exactly的测量参数
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    // Temporarily force children to reuse their old measured width
                    // FIXME: this may not be right for something like wrapping text?
                    int oldWidth = lp.width;//measureChildWithMargins 这个函数会用到宽，所以要保存一下
                    lp.width = child.getMeasuredWidth();
                    // Remeasure with new dimensions
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //LogUtils.e(TAG, "onLayout() called with: " + "changed = [" + changed + "], l = [" + l + "], t = [" + t + "], r = [" + r + "], b = [" + b + "]");
        int childCount = getChildCount();
        int left = getPaddingLeft();
        int right = getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                if (i == 0) {
                    //第一个子View是内容 宽度设置为全屏
                    childView.layout(left, getPaddingTop(), left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                    left = left + childView.getMeasuredWidth();
                } else {
                    if (isLeftSwipe) {
                        childView.layout(left, getPaddingTop(), left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                        left = left + childView.getMeasuredWidth();
                    } else {
                        childView.layout(right - childView.getMeasuredWidth(), getPaddingTop(), right, getPaddingTop() + childView.getMeasuredHeight());
                        right = right - childView.getMeasuredWidth();
                    }
                }
            }
        }
        //Log.d(TAG, "onLayout() called with: " + "maxScrollGap = [" + maxScrollGap + "], l = [" + l + "], t = [" + t + "], r = [" + r + "], b = [" + b + "]");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //LogUtils.d(TAG, "dispatchTouchEvent() called with: " + "ev = [" + ev + "]");
        if (isSwipeEnable) {
            acquireVelocityTracker(ev);
            final VelocityTracker verTracker = velocityTracker;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isUserSwiped = false;//判断手指起始落点，如果距离属于滑动了，就屏蔽一切点击事件。
                    isUnMoved = true;//仿QQ，侧滑菜单展开时，点击内容区域，关闭侧滑菜单。
                    iosInterceptFlag = false;//每次DOWN时，默认是不拦截的
                    if (isTouching) {//如果有别的指头摸过了，那么就return false。这样后续的move..等事件也不会再来找这个View了。
                        return false;
                    } else {
                        isTouching = true;//第一个摸的指头，赶紧改变标志，宣誓主权。
                    }
                    lastP.set(ev.getRawX(), ev.getRawY());
                    firstP.set(ev.getRawX(), ev.getRawY());//判断手指起始落点，如果距离属于滑动了，就屏蔽一切点击事件。

                    //如果down，view和CacheView不一样，则立马让它还原。且把它置为null
                    if (viewCache != null) {
                        if (viewCache != this) {
                            viewCache.smoothClose();
                            iosInterceptFlag = isIos;//IOS模式开启的话，且当前有侧滑菜单的View，且不是自己的，就该拦截事件
                        }
                        //只要有一个侧滑菜单处于打开状态， 就不给外层布局上下滑动了
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    //求第一个触点的id， 此时可能有多个触点，但至少一个，计算滑动速率用
                    pointerId = ev.getPointerId(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //IOS模式开启的话，且当前有侧滑菜单的View，且不是自己的，就该拦截事件咯。滑动也不该出现
                    if (iosInterceptFlag) {
                        break;
                    }
                    float gap = lastP.x - ev.getRawX();
                    //为了在水平滑动中禁止父类ListView等再竖直滑动
                    if (Math.abs(gap) > 10 || Math.abs(getScrollX()) > 10) {//2016 09 29 修改此处，使屏蔽父布局滑动更加灵敏
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    //仿QQ，侧滑菜单展开时，点击内容区域，关闭侧滑菜单。begin
                    if (Math.abs(gap) > scaleTouchSlop) {
                        isUnMoved = false;
                    }
                    //仿QQ，侧滑菜单展开时，点击内容区域，关闭侧滑菜单。end
                    //如果scroller还没有滑动结束 停止滑动动画
                    scrollBy((int) (gap), 0);//滑动使用scrollBy
                    //越界修正
                    if (isLeftSwipe) {//左滑
                        if (getScrollX() < 0) {
                            scrollTo(0, 0);
                        }
                        if (getScrollX() > rightMenuWidths) {
                            scrollTo(rightMenuWidths, 0);
                        }
                    } else {//右滑
                        if (getScrollX() < -rightMenuWidths) {
                            scrollTo(-rightMenuWidths, 0);
                        }
                        if (getScrollX() > 0) {
                            scrollTo(0, 0);
                        }
                    }

                    lastP.set(ev.getRawX(), ev.getRawY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //判断手指起始落点，如果距离属于滑动了，就屏蔽一切点击事件。
                    if (Math.abs(ev.getRawX() - firstP.x) > scaleTouchSlop) {
                        isUserSwiped = true;
                    }

                    //IOS模式开启的话，且当前有侧滑菜单的View，且不是自己的，就该拦截事件咯。滑动也不该出现
                    if (!iosInterceptFlag) {//且滑动了 才判断是否要收起、展开menu
                        //求伪瞬时速度
                        verTracker.computeCurrentVelocity(1000, maxVelocity);
                        final float velocityX = verTracker.getXVelocity(pointerId);
                        if (Math.abs(velocityX) > 1000) {//滑动速度超过阈值
                            if (velocityX < -1000) {
                                if (isLeftSwipe) {//左滑
                                    //平滑展开Menu
                                    smoothExpand();
                                } else {
                                    //平滑关闭Menu
                                    smoothClose();
                                }
                            } else {
                                if (isLeftSwipe) {//左滑
                                    // 平滑关闭Menu
                                    smoothClose();
                                } else {
                                    //平滑展开Menu
                                    smoothExpand();
                                }
                            }
                        } else {
                            if (Math.abs(getScrollX()) > limit) {//否则就判断滑动距离
                                //平滑展开Menu
                                smoothExpand();
                            } else {
                                // 平滑关闭Menu
                                smoothClose();
                            }
                        }
                    }
                    //释放
                    releaseVelocityTracker();
                    //LogUtils.i(TAG, "onTouch A ACTION_UP ACTION_CANCEL:velocityY:" + velocityX);
                    isTouching = false;//没有手指在摸我了
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //禁止侧滑时，点击事件不受干扰。
        if (isSwipeEnable) {
            switch (ev.getAction()) {
                // fix 长按事件和侧滑的冲突。
                case MotionEvent.ACTION_MOVE:
                    //屏蔽滑动时的事件
                    if (Math.abs(ev.getRawX() - firstP.x) > scaleTouchSlop) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //为了在侧滑时，屏蔽子View的点击事件
                    if (isLeftSwipe) {
                        if (getScrollX() > scaleTouchSlop) {
                            //这里判断落点在内容区域屏蔽点击，内容区域外，允许传递事件继续向下
                            if (ev.getX() < getWidth() - getScrollX()) {
                                //仿QQ，侧滑菜单展开时，点击内容区域，关闭侧滑菜单。
                                if (isUnMoved) {
                                    smoothClose();
                                }
                                return true;//true表示拦截
                            }
                        }
                    } else {
                        if (-getScrollX() > scaleTouchSlop) {
                            if (ev.getX() > -getScrollX()) {//点击范围在菜单外 屏蔽
                                //仿QQ，侧滑菜单展开时，点击内容区域，关闭侧滑菜单。
                                if (isUnMoved) {
                                    smoothClose();
                                }
                                return true;
                            }
                        }
                    }
                    // 判断手指起始落点，如果距离属于滑动了，就屏蔽一切点击事件。
                    if (isUserSwiped) {
                        return true;
                    }
                    break;
            }
            //模仿IOS 点击其他区域关闭：
            if (iosInterceptFlag) {
                //IOS模式开启，且当前有菜单的View，且不是自己的 拦截点击事件给子View
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void smoothExpand() {
        //展开就加入ViewCache：
        viewCache = SwipeLayout.this;

        //侧滑菜单展开，屏蔽content长按
        if (null != contentView) {
            contentView.setLongClickable(false);
        }

        cancelAnim();
        expandAnim = ValueAnimator.ofInt(getScrollX(), isLeftSwipe ? rightMenuWidths : -rightMenuWidths);
        expandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        expandAnim.setInterpolator(new OvershootInterpolator());
        expandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExpand = true;
            }
        });
        expandAnim.setDuration(300).start();
    }

    /**
     * 每次执行动画之前都应该先取消之前的动画
     */
    private void cancelAnim() {
        if (closeAnim != null && closeAnim.isRunning()) {
            closeAnim.cancel();
        }
        if (expandAnim != null && expandAnim.isRunning()) {
            expandAnim.cancel();
        }
    }

    /**
     * 平滑关闭
     */
    public void smoothClose() {
        viewCache = null;

        //2016 11 13 add 侧滑菜单展开，屏蔽content长按
        if (null != contentView) {
            contentView.setLongClickable(true);
        }

        cancelAnim();
        closeAnim = ValueAnimator.ofInt(getScrollX(), 0);
        closeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        closeAnim.setInterpolator(new AccelerateInterpolator());
        closeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExpand = false;
            }
        });
        closeAnim.setDuration(300).start();
        //LogUtils.d(TAG, "smoothClose() called with:getScrollX() " + getScrollX());
    }


    /**
     * @param event 向VelocityTracker添加MotionEvent
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == velocityTracker) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    /**
     * 释放VelocityTracker
     */
    private void releaseVelocityTracker() {
        if (null != velocityTracker) {
            velocityTracker.clear();
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    //每次ViewDetach的时候，判断一下 ViewCache是不是自己，如果是自己，关闭侧滑菜单，且ViewCache设置为null，
    // 理由：1 防止内存泄漏(ViewCache是一个静态变量)
    // 2 侧滑删除后自己后，这个View被Recycler回收，复用，下一个进入屏幕的View的状态应该是普通状态，而不是展开状态。
    @Override
    protected void onDetachedFromWindow() {
        if (this == viewCache) {
            viewCache.smoothClose();
            viewCache = null;
        }
        super.onDetachedFromWindow();
    }

    //展开时，禁止长按
    @Override
    public boolean performLongClick() {
        return Math.abs(getScrollX()) <= scaleTouchSlop && super.performLongClick();
    }

    /**
     * 快速关闭。
     * 用于点击侧滑菜单上的选项,同时想让它快速关闭(删除 置顶)。
     * 这个方法在ListView里是必须调用的，
     * 在RecyclerView里，视情况而定，如果是mAdapter.notifyItemRemoved(pos)方法不用调用。
     */
    public void quickClose() {
        if (this == viewCache) {
            //先取消展开动画
            cancelAnim();
            viewCache.scrollTo(0, 0);//关闭
            viewCache = null;
        }
    }
}