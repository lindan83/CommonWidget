package com.lance.common.widget.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lance.common.widget.photoview.gestures.OnGestureListener;
import com.lance.common.widget.photoview.gestures.VersionedGestureDetector;
import com.lance.common.widget.photoview.scrollerproxy.ScrollerProxy;

import java.lang.ref.WeakReference;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class PhotoViewAttacher implements IPhotoView, View.OnTouchListener, OnGestureListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "PhotoViewAttacher";

    private Interpolator interpolator = new AccelerateDecelerateInterpolator();
    private int ZOOM_DURATION = DEFAULT_ZOOM_DURATION;

    private static final int EDGE_NONE = -1;
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_BOTH = 2;

    private static int SINGLE_TOUCH = 1;

    private float minScale = DEFAULT_MIN_SCALE;
    private float midScale = DEFAULT_MID_SCALE;
    private float maxScale = DEFAULT_MAX_SCALE;

    private boolean allowParentInterceptOnEdge = true;
    private boolean blockParentIntercept = false;

    private WeakReference<ImageView> imageView;

    // Gesture Detectors
    private GestureDetector gestureDetector;
    private com.lance.common.widget.photoview.gestures.GestureDetector scaleDragDetector;

    // These are set so we don't keep allocating them on the heap
    private final Matrix baseMatrix = new Matrix();
    private final Matrix drawMatrix = new Matrix();
    private final Matrix suppMatrix = new Matrix();
    private final RectF displayRect = new RectF();
    private final float[] matrixValues = new float[9];

    // Listeners
    private OnMatrixChangedListener matrixChangeListener;
    private OnPhotoTapListener photoTapListener;
    private OnViewTapListener viewTapListener;
    private OnLongClickListener longClickListener;
    private OnScaleChangeListener scaleChangeListener;
    private OnSingleFlingListener singleFlingListener;

    private int ivTop, ivRight, ivBottom, ivLeft;
    private FlingRunnable currentFlingRunnable;
    private int scrollEdge = EDGE_BOTH;
    private float baseRotation;

    private boolean zoomEnabled;
    private ScaleType scaleType = ScaleType.FIT_CENTER;

    private static void checkZoomLevels(float minZoom, float midZoom, float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    /**
     * @return true if the ImageView exists, and its Drawable exists
     */
    private static boolean hasDrawable(ImageView imageView) {
        return null != imageView && null != imageView.getDrawable();
    }

    /**
     * @return true if the ScaleType is supported.
     */
    private static boolean isSupportedScaleType(final ScaleType scaleType) {
        if (null == scaleType) {
            return false;
        }

        switch (scaleType) {
            case MATRIX:
                throw new IllegalArgumentException(scaleType.name() + " is not supported in PhotoView");
            default:
                return true;
        }
    }

    /**
     * Sets the ImageView's ScaleType to Matrix.
     */
    private static void setImageViewScaleTypeMatrix(ImageView imageView) {
        /*
          PhotoView sets its own ScaleType to Matrix, then diverts all calls
          setScaleType to this.setScaleType automatically.
         */
        if (null != imageView && !(imageView instanceof IPhotoView)) {
            if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
                imageView.setScaleType(ScaleType.MATRIX);
            }
        }
    }

    public PhotoViewAttacher(ImageView imageView) {
        this(imageView, true);
    }

    public PhotoViewAttacher(ImageView imageView, boolean zoomable) {
        this.imageView = new WeakReference<>(imageView);

        imageView.setDrawingCacheEnabled(true);
        imageView.setOnTouchListener(this);

        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (null != observer)
            observer.addOnGlobalLayoutListener(this);

        // Make sure we using MATRIX Scale Type
        setImageViewScaleTypeMatrix(imageView);

        if (imageView.isInEditMode()) {
            return;
        }
        // Create Gesture Detectors...
        scaleDragDetector = VersionedGestureDetector.newInstance(
                imageView.getContext(), this);

        gestureDetector = new GestureDetector(imageView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {

                    // forward long click listener
                    @Override
                    public void onLongPress(MotionEvent e) {
                        if (null != longClickListener) {
                            longClickListener.onLongClick(getImageView());
                        }
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        return singleFlingListener != null && getScale() <= DEFAULT_MIN_SCALE && !(e1.getPointerCount() > SINGLE_TOUCH || e2.getPointerCount() > SINGLE_TOUCH) && singleFlingListener.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        gestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
        baseRotation = 0.0f;

        // Finally, update the UI so that we're zoomable
        setZoomable(zoomable);
    }

    @Override
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener) {
        if (newOnDoubleTapListener != null) {
            this.gestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
        } else {
            this.gestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
        }
    }

    @Override
    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.scaleChangeListener = onScaleChangeListener;
    }

    @Override
    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.singleFlingListener = onSingleFlingListener;
    }

    @Override
    public boolean canZoom() {
        return zoomEnabled;
    }

    public void cleanup() {
        if (null == imageView) {
            return; // cleanup already done
        }

        final ImageView imageView = this.imageView.get();

        if (null != imageView) {
            // Remove this as a global layout listener
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (null != observer && observer.isAlive()) {
                observer.removeGlobalOnLayoutListener(this);
            }

            // Remove the ImageView's reference to this
            imageView.setOnTouchListener(null);

            // make sure a pending fling runnable won't be run
            cancelFling();
        }

        if (null != gestureDetector) {
            gestureDetector.setOnDoubleTapListener(null);
        }

        // Clear listeners too
        matrixChangeListener = null;
        photoTapListener = null;
        viewTapListener = null;

        // Finally, clear ImageView
        this.imageView = null;
    }

    @Override
    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    @Override
    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }

        if (null == imageView.getDrawable()) {
            return false;
        }

        suppMatrix.set(finalMatrix);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();

        return true;
    }

    public void setBaseRotation(final float degrees) {
        baseRotation = degrees % 360;
        update();
        setRotationBy(baseRotation);
        checkAndDisplayMatrix();
    }

    @Override
    public void setRotationTo(float degrees) {
        suppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    @Override
    public void setRotationBy(float degrees) {
        suppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public ImageView getImageView() {
        ImageView imageView = null;

        if (null != this.imageView) {
            imageView = this.imageView.get();
        }

        // If we don't have an ImageView, call cleanup()
        if (null == imageView) {
            cleanup();
        }

        return imageView;
    }

    @Override
    public float getMinimumScale() {
        return minScale;
    }

    @Override
    public float getMediumScale() {
        return midScale;
    }

    @Override
    public float getMaximumScale() {
        return maxScale;
    }

    @Override
    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(suppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(suppMatrix, Matrix.MSKEW_Y), 2));
    }

    @Override
    public ScaleType getScaleType() {
        return scaleType;
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (scaleDragDetector.isScaling()) {
            return; // Do not drag if we are already scaling
        }

        ImageView imageView = getImageView();
        suppMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();

        /*
          Here we decide whether to let the ImageView's parent to start taking
          over the touch event.

          First we check whether this function is enabled. We never want the
          parent to take over if we're scaling. We then check the edge we're
          on, and the direction of the scroll (i.e. if we're pulling against
          the edge, aka 'overscrolling', let the parent take over).
         */
        ViewParent parent = imageView.getParent();
        if (allowParentInterceptOnEdge && !scaleDragDetector.isScaling() && !blockParentIntercept) {
            if (scrollEdge == EDGE_BOTH
                    || (scrollEdge == EDGE_LEFT && dx >= 1f)
                    || (scrollEdge == EDGE_RIGHT && dx <= -1f)) {
                if (null != parent) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
        } else {
            if (null != parent) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX,
                        float velocityY) {
        ImageView imageView = getImageView();
        currentFlingRunnable = new FlingRunnable(imageView.getContext());
        currentFlingRunnable.fling(getImageViewWidth(imageView),
                getImageViewHeight(imageView), (int) velocityX, (int) velocityY);
        imageView.post(currentFlingRunnable);
    }

    @Override
    public void onGlobalLayout() {
        ImageView imageView = getImageView();

        if (null != imageView) {
            if (zoomEnabled) {
                final int top = imageView.getTop();
                final int right = imageView.getRight();
                final int bottom = imageView.getBottom();
                final int left = imageView.getLeft();

                /*
                  We need to check whether the ImageView's bounds have changed.
                  This would be easier if we targeted API 11+ as we could just use
                  View.OnLayoutChangeListener. Instead we have to replicate the
                  work, keeping track of the ImageView's bounds and then checking
                  if the values change.
                 */
                if (top != ivTop || bottom != ivBottom || left != ivLeft
                        || right != ivRight) {
                    // Update our base matrix, as the bounds have changed
                    updateBaseMatrix(imageView.getDrawable());

                    // Update values as something has changed
                    ivTop = top;
                    ivRight = right;
                    ivBottom = bottom;
                    ivLeft = left;
                }
            } else {
                updateBaseMatrix(imageView.getDrawable());
            }
        }
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if ((getScale() < maxScale || scaleFactor < 1f) && (getScale() > minScale || scaleFactor > 1f)) {
            if (null != scaleChangeListener) {
                scaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
            }
            suppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        boolean handled = false;

        if (zoomEnabled && hasDrawable((ImageView) v)) {
            ViewParent parent = v.getParent();
            switch (ev.getAction()) {
                case ACTION_DOWN:
                    // First, disable the Parent from intercepting the touch
                    // event
                    if (null != parent) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }

                    // If we're flinging, and the user presses down, cancel
                    // fling
                    cancelFling();
                    break;

                case ACTION_CANCEL:
                case ACTION_UP:
                    // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (getScale() < minScale) {
                        RectF rect = getDisplayRect();
                        if (null != rect) {
                            v.post(new AnimatedZoomRunnable(getScale(), minScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    }
                    break;
            }

            // Try the Scale/Drag detector
            if (null != scaleDragDetector) {
                boolean wasScaling = scaleDragDetector.isScaling();
                boolean wasDragging = scaleDragDetector.isDragging();

                handled = scaleDragDetector.onTouchEvent(ev);

                boolean didntScale = !wasScaling && !scaleDragDetector.isScaling();
                boolean didntDrag = !wasDragging && !scaleDragDetector.isDragging();

                blockParentIntercept = didntScale && didntDrag;
            }

            // Check to see if the user double tapped
            if (null != gestureDetector && gestureDetector.onTouchEvent(ev)) {
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        allowParentInterceptOnEdge = allow;
    }

    @Override
    public void setMinimumScale(float minimumScale) {
        checkZoomLevels(minimumScale, midScale, maxScale);
        minScale = minimumScale;
    }

    @Override
    public void setMediumScale(float mediumScale) {
        checkZoomLevels(minScale, mediumScale, maxScale);
        midScale = mediumScale;
    }

    @Override
    public void setMaximumScale(float maximumScale) {
        checkZoomLevels(minScale, midScale, maximumScale);
        maxScale = maximumScale;
    }

    @Override
    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        checkZoomLevels(minimumScale, mediumScale, maximumScale);
        minScale = minimumScale;
        midScale = mediumScale;
        maxScale = maximumScale;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        longClickListener = listener;
    }

    @Override
    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        matrixChangeListener = listener;
    }

    @Override
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        photoTapListener = listener;
    }

    @Nullable
    OnPhotoTapListener getOnPhotoTapListener() {
        return photoTapListener;
    }

    @Override
    public void setOnViewTapListener(OnViewTapListener listener) {
        viewTapListener = listener;
    }

    @Nullable
    OnViewTapListener getOnViewTapListener() {
        return viewTapListener;
    }

    @Override
    public void setScale(float scale) {
        setScale(scale, false);
    }

    @Override
    public void setScale(float scale, boolean animate) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            setScale(scale,
                    (imageView.getRight()) / 2,
                    (imageView.getBottom()) / 2,
                    animate);
        }
    }

    @Override
    public void setScale(float scale, float focalX, float focalY,
                         boolean animate) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            // Check to see if the scale is within bounds
            if (scale < minScale || scale > maxScale) {
                return;
            }

            if (animate) {
                imageView.post(new AnimatedZoomRunnable(getScale(), scale,
                        focalX, focalY));
            } else {
                suppMatrix.setScale(scale, scale, focalX, focalY);
                checkAndDisplayMatrix();
            }
        }
    }

    /**
     * Set the zoom interpolator
     *
     * @param interpolator the zoom interpolator
     */
    public void setZoomInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (isSupportedScaleType(scaleType) && scaleType != this.scaleType) {
            this.scaleType = scaleType;
            // Finally update
            update();
        }
    }

    @Override
    public void setZoomable(boolean zoomable) {
        zoomEnabled = zoomable;
        update();
    }

    public void update() {
        ImageView imageView = getImageView();

        if (null != imageView) {
            if (zoomEnabled) {
                // Make sure we using MATRIX Scale Type
                setImageViewScaleTypeMatrix(imageView);

                // Update the base matrix using the current drawable
                updateBaseMatrix(imageView.getDrawable());
            } else {
                // Reset the Matrix...
                resetMatrix();
            }
        }
    }

    /**
     * Get the display matrix
     *
     * @param matrix target matrix to copy to
     */
    @Override
    public void getDisplayMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    /**
     * Get the current support matrix
     */
    public void getSuppMatrix(Matrix matrix) {
        matrix.set(suppMatrix);
    }

    private Matrix getDrawMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(suppMatrix);
        return drawMatrix;
    }

    private void cancelFling() {
        if (null != currentFlingRunnable) {
            currentFlingRunnable.cancelFling();
            currentFlingRunnable = null;
        }
    }

    public Matrix getImageMatrix() {
        return drawMatrix;
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    private void checkImageViewScaleType() {
        ImageView imageView = getImageView();

        /*
          PhotoView's getScaleType() will just divert to this.getScaleType() so
          only call if we're not attached to a PhotoView.
         */
        if (null != imageView && !(imageView instanceof IPhotoView)) {
            if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
                throw new IllegalStateException(
                        "The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher. You should call setScaleType on the PhotoViewAttacher instead of on the ImageView");
            }
        }
    }

    private boolean checkMatrixBounds() {
        final ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }

        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight(imageView);
        if (height <= viewHeight) {
            switch (scaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getImageViewWidth(imageView);
        if (width <= viewWidth) {
            switch (scaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
            scrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            scrollEdge = EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            scrollEdge = EDGE_RIGHT;
        } else {
            scrollEdge = EDGE_NONE;
        }

        // Finally actually translate the matrix
        suppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            Drawable d = imageView.getDrawable();
            if (null != d) {
                displayRect.set(0, 0, d.getIntrinsicWidth(),
                        d.getIntrinsicHeight());
                matrix.mapRect(displayRect);
                return displayRect;
            }
        }
        return null;
    }

    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = getImageView();
        return imageView == null ? null : imageView.getDrawingCache();
    }

    @Override
    public void setZoomTransitionDuration(int milliseconds) {
        if (milliseconds < 0)
            milliseconds = DEFAULT_ZOOM_DURATION;
        this.ZOOM_DURATION = milliseconds;
    }

    @Override
    public IPhotoView getIPhotoViewImplementation() {
        return this;
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     - Matrix to unpack
     * @param whichValue - Which value from Matrix.M* to return
     * @return float - returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(matrixValues);
        return matrixValues[whichValue];
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private void resetMatrix() {
        suppMatrix.reset();
        setRotationBy(baseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        ImageView imageView = getImageView();
        if (null != imageView) {

            checkImageViewScaleType();
            imageView.setImageMatrix(matrix);

            // Call MatrixChangedListener if needed
            if (null != matrixChangeListener) {
                RectF displayRect = getDisplayRect(matrix);
                if (null != displayRect) {
                    matrixChangeListener.onMatrixChanged(displayRect);
                }
            }
        }
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = getImageView();
        if (null == imageView || null == d) {
            return;
        }

        final float viewWidth = getImageViewWidth(imageView);
        final float viewHeight = getImageViewHeight(imageView);
        final int drawableWidth = d.getIntrinsicWidth();
        final int drawableHeight = d.getIntrinsicHeight();

        baseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        if (scaleType == ScaleType.CENTER) {
            baseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (scaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            if ((int) baseRotation % 180 != 0) {
                mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
            }

            switch (scaleType) {
                case FIT_CENTER:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                    break;
                case FIT_START:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
                    break;
                case FIT_END:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
                    break;
                case FIT_XY:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                    break;
                default:
                    break;
            }
        }

        resetMatrix();
    }

    private int getImageViewWidth(ImageView imageView) {
        if (null == imageView)
            return 0;
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        if (null == imageView)
            return 0;
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    /**
     * Interface definition for a callback to be invoked when the internal Matrix has changed for
     * this View.
     *
     * @author Chris Banes
     */
    public interface OnMatrixChangedListener {
        /**
         * Callback for when the Matrix displaying the Drawable has changed. This could be because
         * the View's bounds have changed, or the user has zoomed.
         *
         * @param rect - Rectangle displaying the Drawable's new bounds.
         */
        void onMatrixChanged(RectF rect);
    }

    /**
     * Interface definition for callback to be invoked when attached ImageView scale changes
     *
     * @author Marek Sebera
     */
    public interface OnScaleChangeListener {
        /**
         * Callback for when the scale changes
         *
         * @param scaleFactor the scale factor (less than 1 for zoom out, greater than 1 for zoom in)
         * @param focusX      focal point X position
         * @param focusY      focal point Y position
         */
        void onScaleChange(float scaleFactor, float focusX, float focusY);
    }

    /**
     * Interface definition for a callback to be invoked when the Photo is tapped with a single
     * tap.
     *
     * @author Chris Banes
     */
    public interface OnPhotoTapListener {

        /**
         * A callback to receive where the user taps on a photo. You will only receive a callback if
         * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the of the Drawable, as percentage of the
         *             Drawable width.
         * @param y    - where the user tapped from the top of the Drawable, as percentage of the
         *             Drawable height.
         */
        void onPhotoTap(View view, float x, float y);

        /**
         * A simple callback where out of photo happened;
         */
        void onOutsidePhotoTap();
    }

    /**
     * Interface definition for a callback to be invoked when the ImageView is tapped with a single
     * tap.
     *
     * @author Chris Banes
     */
    public interface OnViewTapListener {

        /**
         * A callback to receive where the user taps on a ImageView. You will receive a callback if
         * the user taps anywhere on the view, tapping on 'whitespace' will not be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the left of the View.
         * @param y    - where the user tapped from the top of the View.
         */
        void onViewTap(View view, float x, float y);
    }

    /**
     * Interface definition for a callback to be invoked when the ImageView is fling with a single
     * touch
     *
     * @author tonyjs
     */
    public interface OnSingleFlingListener {

        /**
         * A callback to receive where the user flings on a ImageView. You will receive a callback if
         * the user flings anywhere on the view.
         *
         * @param e1        - MotionEvent the user first touch.
         * @param e2        - MotionEvent the user last touch.
         * @param velocityX - distance of user's horizontal fling.
         * @param velocityY - distance of user's vertical fling.
         */
        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }

    private class AnimatedZoomRunnable implements Runnable {

        private final float focalX, focalY;
        private final long startTime;
        private final float zoomStart, zoomEnd;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            this.focalX = focalX;
            this.focalY = focalY;
            startTime = System.currentTimeMillis();
            zoomStart = currentZoom;
            zoomEnd = targetZoom;
        }

        @Override
        public void run() {
            ImageView imageView = getImageView();
            if (imageView == null) {
                return;
            }

            float t = interpolate();
            float scale = zoomStart + t * (zoomEnd - zoomStart);
            float deltaScale = scale / getScale();

            onScale(deltaScale, focalX, focalY);

            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(imageView, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - startTime) / ZOOM_DURATION;
            t = Math.min(1f, t);
            t = interpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {
        private final ScrollerProxy scroller;
        private int currentX, currentY;

        public FlingRunnable(Context context) {
            scroller = ScrollerProxy.getScroller(context);
        }

        public void cancelFling() {
            scroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final RectF rect = getDisplayRect();
            if (null == rect) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            currentX = startX;
            currentY = startY;

            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                scroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (scroller.isFinished()) {
                return; // remaining post that should not be handled
            }

            ImageView imageView = getImageView();
            if (null != imageView && scroller.computeScrollOffset()) {

                final int newX = scroller.getCurrX();
                final int newY = scroller.getCurrY();

                suppMatrix.postTranslate(currentX - newX, currentY - newY);
                setImageViewMatrix(getDrawMatrix());

                currentX = newX;
                currentY = newY;

                // Post On animation
                Compat.postOnAnimation(imageView, this);
            }
        }
    }
}