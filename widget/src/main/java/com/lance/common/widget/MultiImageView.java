package com.lance.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lance.common.util.DensityUtil;

import java.util.List;

/**
 * 多图显示控件
 */
public class MultiImageView extends LinearLayout {
    private static final String TAG = "MultiImageView";

    private static final int DEFAULT_IMAGE_SPACING = 8;//默认图片之间间隔
    private static final int DEFAULT_PER_ROW_COUNT = 3;//默认每行显示的数量
    private static final float DEFAULT_MULTI_IMAGE_WIDTH_PERCENT = 1f;//默认显示多图时总宽度占比
    private static final float DEFAULT_SINGLE_IMAGE_WIDTH_PERCENT = 0.7f;//默认显示单图时总宽度占比

    //宽度
    private int width;
    //最大宽度
    private int maxWidth;

    // 照片的Url列表
    private List<String> imageList;

    //单位为Pixel
    private int pxOneMaxWH;  // 单张图最大允许宽高
    private int pxMoreWH = 0;// 多张图情况下每张图的宽高
    private float imageSpacing = DEFAULT_IMAGE_SPACING;//图片之间间隔
    private int pxImagePadding = DensityUtil.dp2px(getContext(), imageSpacing);// 图片间的间距

    private int perRowCount = DEFAULT_PER_ROW_COUNT;// 每行显示最大数
    private float widthPercentMulti;//多图显示总宽度占比
    private float widthPercentSingle;//单图显示总宽度占比

    //单图的布局参数
    private LayoutParams onePicParam;
    //多图的布局参数和第一列的布局参数
    private LayoutParams morePicParam, moreColumnFirstParam;
    //行布局参数(多图时)
    private LayoutParams rowParam;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public MultiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiImageView);
        imageSpacing = typedArray.getDimension(R.styleable.MultiImageView_imageSpacing, DEFAULT_IMAGE_SPACING);//图片之间间隔
        perRowCount = typedArray.getInt(R.styleable.MultiImageView_numColumns, DEFAULT_PER_ROW_COUNT);// 每行显示最大数
        widthPercentMulti = typedArray.getFloat(R.styleable.MultiImageView_multiImageWidthPercent, DEFAULT_MULTI_IMAGE_WIDTH_PERCENT);
        widthPercentSingle = typedArray.getFloat(R.styleable.MultiImageView_singleImageWidthPercent, DEFAULT_SINGLE_IMAGE_WIDTH_PERCENT);
        typedArray.recycle();
    }

    public void setList(List<String> lists) throws IllegalArgumentException {
        if (lists == null) {
            throw new IllegalArgumentException("imageList is null...");
        }
        imageList = lists;

        if (maxWidth > 0) {
            pxMoreWH = (maxWidth - pxImagePadding * (perRowCount - 1)) / perRowCount; //解决右侧图片和内容对不齐问题
            pxOneMaxWH = (int) (width * widthPercentSingle);//单图宽高
            initImageLayoutParams();
        }
        initView();
    }

    public List<String> getList() {
        return imageList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxWidth == 0) {
            int width = measureWidth(widthMeasureSpec);
            if (width > 0) {
                this.width = width;
                maxWidth = (int) (width * widthPercentMulti);
                if (imageList != null && imageList.size() > 0) {
                    setList(imageList);
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

        //onePicParam = new LayoutParams(wrap, wrap);
        //单图的宽占一半宽度
        onePicParam = new LayoutParams(pxOneMaxWH, pxOneMaxWH);
        onePicParam.setMargins(0, 0, 0, 0);
        moreColumnFirstParam = new LayoutParams(pxMoreWH, pxMoreWH);
        morePicParam = new LayoutParams(pxMoreWH, pxMoreWH);
        morePicParam.setMargins(pxImagePadding, 0, 0, 0);

        rowParam = new LayoutParams(match, wrap);
    }

    /**
     * 根据ImageView的数量初始化不同的View布局,还要为每一个View作点击效果
     */
    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if (maxWidth == 0) {
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
            addView(new View(getContext()));
            return;
        }

        if (imageList == null || imageList.size() == 0) {
            return;
        }

        if (imageList.size() == 1) {
            //单图的情况
            addView(createImageView(0, false));
        } else {
            //多图的情况
            int allCount = imageList.size();//图片数量
            int rowCount = allCount / perRowCount
                    + (allCount % perRowCount > 0 ? 1 : 0);// 行数
            for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
                //每行是一个单独的水平线性布局
                LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                rowLayout.setLayoutParams(rowParam);
                if (rowCursor != 0) {
                    rowLayout.setPadding(0, pxImagePadding, 0, 0);
                }

                int columnCount = allCount % perRowCount == 0 ? perRowCount
                        : allCount % perRowCount;//每行的列数
                //如果不是最后一行，有可能出现不够3张图片的情况
                if (rowCursor != rowCount - 1) {
                    columnCount = perRowCount;
                }
                addView(rowLayout);

                int rowOffset = rowCursor * perRowCount;// 行偏移
                for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
                    int position = columnCursor + rowOffset;
                    rowLayout.addView(createImageView(position, true));
                }
            }
        }
    }

    /**
     * 创建多图对应的ImageView
     *
     * @param position     位置
     * @param isMultiImage 是否多图模式
     * @return ImageView
     */
    private ImageView createImageView(int position, final boolean isMultiImage) {
        String url = imageList.get(position);
        ImageView imageView = new ColorFilterImageView(getContext());
        if (isMultiImage) {
            //多图
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setLayoutParams(position % perRowCount == 0 ? moreColumnFirstParam : morePicParam);
        } else {
            //单图
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setMaxHeight(pxOneMaxWH);
            imageView.setMaxWidth(pxOneMaxWH);
            imageView.setLayoutParams(onePicParam);
        }

        imageView.setId(url.hashCode());
        imageView.setOnClickListener(new OnClickImageListener(position));
        Glide.with(getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                .thumbnail(0.1f)//先显示缩略图
                .error(R.mipmap.icon_notpic)
                .into(imageView);

        return imageView;
    }

    private class OnClickImageListener implements OnClickListener {

        private int position;

        public OnClickImageListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, position);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}