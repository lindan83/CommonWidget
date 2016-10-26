package com.lance.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lance.common.util.DensityUtil;
import com.lance.common.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 多图选择显示控件
 */
public class MultiImageSelectorView extends LinearLayout {
    private static final String TAG = "MultiImageSelectorView";
    private static final int DEFAULT_IMAGE_SPACING = 8;//默认图片之间间隔
    private static final int DEFAULT_PER_ROW_COUNT = 4;//默认每行显示的数量
    private static final int DEFAULT_IMAGE_MAX_COUNT = 9;//默认最多选择图片的数量
    private static final int DEFAULT_ADD_IMAGE_RESOURCE_ID = R.mipmap.icon_tianjia;//默认显示的添加图片图标

    private int mMaxWidth;//最大宽度
    private float mImageSpacing = DEFAULT_IMAGE_SPACING;//图片之间间隔
    private List<String> mImageList;//图片的Url列表
    private int mImageMaxCount = DEFAULT_IMAGE_MAX_COUNT;//图片的允许选择数量
    private int mAddImageResId = DEFAULT_ADD_IMAGE_RESOURCE_ID;//选择图片的图标
    private int mPerRowCount = DEFAULT_PER_ROW_COUNT;// 每行显示最大数

    //单位为Pixel
    private int mPxMoreWH = 0;// 每张图的宽高
    private int mPxImagePadding = DensityUtil.dp2px(getContext(), mImageSpacing);// 图片间的间距


    private LayoutParams mMorePicParam, mMoreColumnFirstParam;//多图的布局参数和第一列的布局参数
    private LayoutParams mRowParam;//行布局参数(多图时)

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public MultiImageSelectorView(Context context) {
        super(context);
        init();
    }

    public MultiImageSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
    }

    private void init() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mMaxWidth = getWidth();
                setList(new ArrayList<String>());
            }
        });
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiImageSelectorView);
        mImageSpacing = typedArray.getDimension(R.styleable.MultiImageSelectorView_imageSpacing, DEFAULT_IMAGE_SPACING);//图片之间间隔
        mImageMaxCount = typedArray.getInt(R.styleable.MultiImageSelectorView_imageMaxCount, DEFAULT_IMAGE_MAX_COUNT);//图片的允许选择数量
        mAddImageResId = typedArray.getResourceId(R.styleable.MultiImageSelectorView_addImage, DEFAULT_ADD_IMAGE_RESOURCE_ID);//选择图片的图标
        mPerRowCount = typedArray.getInt(R.styleable.MultiImageSelectorView_numColumns, DEFAULT_PER_ROW_COUNT);// 每行显示最大数
        typedArray.recycle();
    }

    public void setList(List<String> lists) throws IllegalArgumentException {
        if (lists == null) {
            throw new IllegalArgumentException("imageList is null...");
        }
        mImageList = lists;

        if (mMaxWidth > 0) {
            mPxMoreWH = (mMaxWidth - mPxImagePadding * (mPerRowCount - 1)) / mPerRowCount; //解决右侧图片和内容对不齐问题
            initImageLayoutParams();
        }
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxWidth == 0) {
            int width = measureWidth(widthMeasureSpec);
            if (width > 0) {
                mMaxWidth = width;
                if (mImageList != null && mImageList.size() > 0) {
                    setList(mImageList);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * 初始化各种布局参数
     */
    private void initImageLayoutParams() {
        int wrap = LayoutParams.WRAP_CONTENT;
        int match = LayoutParams.MATCH_PARENT;

        mMoreColumnFirstParam = new LayoutParams(mPxMoreWH, mPxMoreWH);
        mMorePicParam = new LayoutParams(mPxMoreWH, mPxMoreWH);
        mMorePicParam.setMargins(mPxImagePadding, 0, 0, 0);

        mRowParam = new LayoutParams(match, wrap);
    }

    /**
     * 根据ImageView的数量初始化不同的View布局,还要为每一个View作点击效果
     */
    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if (mMaxWidth == 0) {
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageSelectorView的宽设置为match_parent
            addView(new View(getContext()));
            return;
        }

        int allCount = mImageList.size() + 1;//图片数量
        int rowCount = allCount / mPerRowCount
                + (allCount % mPerRowCount > 0 ? 1 : 0);// 行数
        for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
            //每行是一个单独的水平线性布局
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            rowLayout.setLayoutParams(mRowParam);
            if (rowCursor != 0) {
                rowLayout.setPadding(0, mPxImagePadding, 0, 0);
            }

            int columnCount = allCount % mPerRowCount == 0 ? mPerRowCount
                    : allCount % mPerRowCount;//每行的列数
            //如果不是最后一行，有可能出现不够3张图片的情况
            if (rowCursor != rowCount - 1) {
                columnCount = mPerRowCount;
            }
            addView(rowLayout);

            int rowOffset = rowCursor * mPerRowCount;// 行偏移
            for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
                int position = columnCursor + rowOffset;
                if (position == allCount - 1) {
                    //添加图标
                    rowLayout.addView(createAddImageView(position));
                } else {
                    rowLayout.addView(createImageView(position));
                }
            }
        }
    }

    /**
     * 创建多图对应的ImageView
     *
     * @param position 位置
     * @return
     */
    private ImageView createImageView(int position) {
        String url = mImageList.get(position);
        ImageView imageView = new ColorFilterImageView(getContext());
        //多图
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setLayoutParams(position % mPerRowCount == 0 ? mMoreColumnFirstParam : mMorePicParam);

        imageView.setId(url.hashCode());
        imageView.setOnClickListener(new OnClickImageListener(position));
        Glide.with(getContext()).load(url).placeholder(R.color.color_F8F8F8).error(R.mipmap.icon_notpic).into(imageView);
        return imageView;
    }

    /**
     * 创建添加图标的ImageView
     *
     * @param position
     * @return
     */
    private ImageView createAddImageView(int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setLayoutParams(position % mPerRowCount == 0 ? mMoreColumnFirstParam : mMorePicParam);

        imageView.setOnClickListener(new OnClickImageListener(position));
        imageView.setImageResource(mAddImageResId);
        return imageView;
    }

    private class OnClickImageListener implements OnClickListener {
        private int position;

        public OnClickImageListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                if (position == mImageList.size()) {
                    if (mImageList.size() >= mImageMaxCount) {
                        ToastUtil.showShort(getContext(), "最多只能选择" + mImageMaxCount + "张图片");
                        return;
                    }
                }
                mOnItemClickListener.onItemClick(view, position);
            }
        }
    }

    /**
     * 指定位置是否为添加图标
     *
     * @param position
     * @return
     */
    public boolean isClickOnAddImageIcon(int position) {
        return position == mImageList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}