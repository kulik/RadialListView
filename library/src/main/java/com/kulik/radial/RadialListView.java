package com.kulik.radial;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RadialListView extends AdapterView<BaseAdapter> {
    private static final String TAG = RadialListView.class.getSimpleName();

    private static final int DEFAULT_ITEMS_OFFSET = 2;

    private static final int[] ATTR_ARRAY = new int[] { android.R.attr.layout_width, // 0
                    android.R.attr.layout_height, // 1
                    R.attr.offset_items, // 2
                    R.attr.delemiter, // 3
                    R.attr.delemiter_width, // 4
                    R.attr.delay_per_item_anim // 5
    };
    public static final int DEFAULT_DELAY_PER_ITEM = 0;

    private int mMaxVisibleItemsQuantity = 4;
    /**
     * A list of cached (re-usable) item views
     */
    private final LinkedList<View> mCachedItemViews = new LinkedList<View>();
    private final List<View> mDelimitersViews = new ArrayList<View>(mMaxVisibleItemsQuantity);

    private int mChildIndexLeftOffset = 0;
    private int mChildIndexRightOffset = mMaxVisibleItemsQuantity - 1;

    private int mDelayAnimPerItem = DEFAULT_DELAY_PER_ITEM;

    private int mRadius;
    /**
     * angular element size
     */
    private double mDPhi;
    /**
     * The adapter with all the data
     */
    private BaseAdapter mAdapter;

    /**
     * Adapter data change observer
     */
    private DataSetObserver mDataSetObserver;
    private int mItemWidth;

    private int mLayoutHeight;
    private int mLayoutWidth;
    private int mOffsetItems;
    private Drawable mDelimiter;
    private int mDelimiterWidth;

    private TwoAnimationsWrapper mLeftAnimator = new TwoAnimationsWrapper();
    private TwoAnimationsWrapper mRightAnimator = new TwoAnimationsWrapper();
    private ObjectAnimator mScroller = new ObjectAnimator();

    private Context mContext;

    //scroling fields

    /**
     * Current rotation offset angle
     */
    private double mDTheta;

    /**
     * Scrolling start angle
     */
    private double mStartTheta = 0;

    /**
     * describe current state of scroling
     */
    private boolean mIsManualScrolling = false;

    public RadialListView(Context context) {
        super(context);
        init(context, null);
    }

    public RadialListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadialListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        retriveXMLLayoutParams(attrs);

        mDPhi = Math.PI / 4 / (mMaxVisibleItemsQuantity - mOffsetItems);
        mRadius = mLayoutHeight;
        mItemWidth = getChordWidth();
        initScroller();
    }

    private void initScroller() {
        mScroller.setTarget(this);
        mScroller.setPropertyName("ScrollAngle");
    }

    private void retriveXMLLayoutParams(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, ATTR_ARRAY);
        mLayoutWidth = ta.getDimensionPixelSize(0, LayoutParams.FILL_PARENT);
        mLayoutHeight = ta.getDimensionPixelSize(1, LayoutParams.FILL_PARENT);
        mOffsetItems = ta.getInt(2, DEFAULT_ITEMS_OFFSET);
        mDelimiter = ta.getDrawable(3);
        mDelimiterWidth = ta.getDimensionPixelSize(4, 1);
        mDelayAnimPerItem = ta.getInt(5, DEFAULT_DELAY_PER_ITEM);
        ta.recycle();
    }

    @Override
    public void setAdapter(final BaseAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;
        initAdapter();

        if (mDataSetObserver == null) {
            mDataSetObserver = new DataSetObserver() {
                @Override
                public void onChanged() {
                    initScroller();
                    onDataChange();
                }

                @Override
                public void onInvalidated() {
                    onDataInvalidated();
                }
            };
        }
        mAdapter.registerDataSetObserver(mDataSetObserver);
    }

    @SuppressWarnings("unused")
    private void setScrollAngle(float angle) {
        Log.d(TAG, "Scrolling: " + angle);
        scrollToAngle(angle);

    }

    protected void initAdapter() {
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.v(TAG, "onLayout" + changed);
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter == null) {
            return;
        }

        if (changed) {
            mAdapter.notifyDataSetChanged();
            fillList();
            layoutsItems();
        }
    }

    private void fillList() {
        Log.i(TAG, "fillList()");
        View newChild;
        removeAllViewsInLayout();

        int needAdd = Math.min(mAdapter.getCount(), mMaxVisibleItemsQuantity);
        int index = 0;
        while (index < needAdd) {

            newChild = (mMaxVisibleItemsQuantity < mCachedItemViews.size()) ?
                    getCachedView() : null;

            newChild = mAdapter.getView(index + mChildIndexLeftOffset, newChild, this);
            mCachedItemViews.add(newChild);

            addChildInLayout(newChild);
            measureChild(newChild);

            //add delimiter
            View d = getDelimiterView(index);
            addChildInLayout(d);
            d.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            index++;
        }

//        mChildIndexRightOffset = mCachedItemViews.size() - 1;
    }

    private void layoutsItems() {
        Log.v(TAG, "layoutsItems()");
        for (int i = 0, a = mCachedItemViews.size(); i < a; i++) {

            final View child = mCachedItemViews.get(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            child.layout(-width / 2 , 0, width / 2, height);
            ViewHelper.setPivotX(child, width / 2);
            ViewHelper.setPivotY(child, mRadius);
            rotateItem(i, child, false, false);

            final View d = getDelimiterView(i);
            d.layout(0, 0, d.getMeasuredWidth(), d.getMeasuredHeight());
            ViewHelper.setPivotY(d, mRadius);
            rotateDelimiter(i, d, false, false);
        }
    }

    private void rotateDelimiter(int i, View d, boolean shouldAnimated, boolean fromOppositeSide) {
        float currentPosition = (float) (((mDPhi * (mMaxVisibleItemsQuantity - i - 1) + mDTheta) / Math.PI * 180f));
        if (shouldAnimated) {
            ObjectAnimator objectAnimator = (fromOppositeSide) ? mLeftAnimator.delimiterAnimator : mRightAnimator.delimiterAnimator;
            objectAnimator.cancel();
            objectAnimator.setTarget(d);
            objectAnimator.setFloatValues((fromOppositeSide) ? 90f : (float) (-(mDPhi / 2) / Math.PI * 180f), currentPosition);
            objectAnimator.start();
        } else {
            if (mLeftAnimator.delimiterAnimator.isRunning() && d.equals(mLeftAnimator.getDelimiter())) {
                mLeftAnimator.delimiterAnimator.setFloatValues(90f, currentPosition);
            } else if (mRightAnimator.delimiterAnimator.isRunning() && d.equals(mRightAnimator.getDelimiter())) {
                mRightAnimator.delimiterAnimator.setFloatValues((float) (-(mDPhi / 2) / Math.PI * 180f), currentPosition);
            } else {
                ViewHelper.setRotation(d, currentPosition);
            }
        }
    }

    private void rotateItem(int i, View child, boolean shouldAnimated, boolean fromOppositeSide) {
        float currentPosition = (float) (((mDPhi * (mMaxVisibleItemsQuantity - i - 0.5) + mDTheta) / Math.PI * 180f));
        if (shouldAnimated) {
            ObjectAnimator objectAnimator = (fromOppositeSide) ? mLeftAnimator.objectAnimator : mRightAnimator.objectAnimator;
            objectAnimator.cancel();
            objectAnimator.setTarget(child);
            objectAnimator.setFloatValues((fromOppositeSide) ? (float) (90 + mDPhi / 2 / Math.PI * 180f) : 0, currentPosition);
            objectAnimator.start();
        } else {
            //update end values if needed
            if (mLeftAnimator.objectAnimator.isRunning() && child.equals(mLeftAnimator.getMain())) {
                mLeftAnimator.objectAnimator.setFloatValues((float) (90 + mDPhi / 2 / Math.PI * 180f), currentPosition);
            } else if (mRightAnimator.objectAnimator.isRunning() && child.equals(mRightAnimator.getMain())) {
                mRightAnimator.objectAnimator.setFloatValues(0, currentPosition);
            } else {
                ViewHelper.setRotation(child, currentPosition);
            }
        }
    }

    private void addChildInLayout(final View child) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }
        addViewInLayout(child, 0, params);
    }

    private void measureChild(final View child) {
        Log.v(TAG, "measureChild()");

        int widthMeashure = MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY);
        int heightMeashure = MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY);
        if (child != null) {
            child.measure(widthMeashure, heightMeashure);
        }
    }

    private View getCachedView() {
        if (mCachedItemViews.size() != 0) {
            return mCachedItemViews.removeFirst();
        }
        return null;
    }

    private View getDelimiterView(int index) {
        View dv;
        if (index < mDelimitersViews.size()) {
            dv = mDelimitersViews.get(index);
        } else {
            dv = new View(mContext);
            dv.setBackgroundDrawable(mDelimiter);
            mDelimitersViews.add(dv);
        }
        return dv;
    }

    private void onDataInvalidated() {
        Log.v(TAG, "onDataInvalidated()");
        removeAllViewsInLayout();

        mCachedItemViews.clear();
        requestLayout();
    }

    private void onDataChange() {
        Log.v(TAG, "onDataChange()");
        requestLayout();
    }

    @Override
    public void setSelection(int position) {
        if (position < 0 || position >= mAdapter.getCount()) {
            throw new IndexOutOfBoundsException("position can't be more then items quantity of Adapter content");
        }
        int relatedPosition = 0;
        if (position > mChildIndexLeftOffset) {
            relatedPosition = position - mChildIndexLeftOffset;
        } else if (position < mChildIndexLeftOffset) {
            relatedPosition = mAdapter.getCount() - mChildIndexLeftOffset + position;
        } else {
            return;
        }
        mIsManualScrolling = false;
        mScroller.setFloatValues((float) mDTheta, (float) mDPhi * relatedPosition + 0.05f);
        mStartTheta = 0;
        mScroller.setDuration(mDelayAnimPerItem * relatedPosition);
        mScroller.start();
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        double radius = Math.sqrt(Math.pow(x, 2) + Math.pow(mLayoutHeight - y, 2));
        Log.d(TAG, "onInterceptTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (radius > mRadius - mItemWidth && radius < mRadius) {
                    mStartTheta = -mDTheta + Math.atan2(x, mLayoutHeight - y);
                    mIsManualScrolling = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                double theta = -mDTheta + Math.atan2(x, mLayoutHeight - y);
                if (Math.abs(mStartTheta - theta) >= 0.1) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsManualScrolling = false;
                break;
        }

        return false;
    }

    private void updateViewRotation(int animateIndex, boolean shouldAnimate, boolean animateFromAnotherSide) {
        for (int index = 0, a = mCachedItemViews.size(); index < a; index++) {
            final View child = mCachedItemViews.get(index);
            rotateItem(index, child, (shouldAnimate && animateIndex == index), animateFromAnotherSide);
            final View d = getDelimiterView(index);
            rotateDelimiter(index, d, (shouldAnimate && animateIndex == index), animateFromAnotherSide);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsManualScrolling && event.getAction() == MotionEvent.ACTION_MOVE) {
            mScroller.cancel();
            double currentTheta = Math.atan2(event.getX(), mLayoutHeight - event.getY());
            scrollToAngle(currentTheta);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return false;
    }

    protected void scrollToAngle(double currentTheta) {
        mDTheta = currentTheta - mStartTheta;

        // Scrolled more one element size. Adding new item
        double sign = 0;
        if (Math.abs(mDTheta) >= mDPhi) {
            Log.d(TAG, "mDTheta" + String.valueOf(mDTheta));
            sign = Math.signum(mDTheta);
            Log.d(TAG, "sign" + String.valueOf(sign));
            mChildIndexLeftOffset += sign;
            mChildIndexRightOffset += sign;

            if (mChildIndexLeftOffset < 0) {
                mChildIndexLeftOffset = mAdapter.getCount() - 1;
            } else if (mChildIndexLeftOffset >= mAdapter.getCount()) {
                mChildIndexLeftOffset = 0;
            }
            if (mChildIndexRightOffset < 0) {
                mChildIndexRightOffset = mAdapter.getCount() - 1;
            } else if (mChildIndexRightOffset >= mAdapter.getCount()) {
                mChildIndexRightOffset = 0;
            }

            View v;
            if (sign > 0) {   //clockwise
                v = mCachedItemViews.removeFirst();
                v = mAdapter.getView(mChildIndexRightOffset, v, null);
                mCachedItemViews.addLast(v);

            } else {
                v = mCachedItemViews.removeLast();
                v = mAdapter.getView(mChildIndexLeftOffset, v, null);
                mCachedItemViews.addFirst(v);
            }
//                }
            mDTheta = 0;
            mStartTheta = currentTheta;
        }
        //defining side for animation, right or left...;
        boolean animateFromOppositeSide = (sign < 0);
        //last visible items
        int lastIndex = Math.min(mMaxVisibleItemsQuantity, mAdapter.getCount()) - 1;
        //first or last item depending on direction
        int animateIndex = animateFromOppositeSide ? 0 : lastIndex;
        updateViewRotation(animateIndex, sign != 0, animateFromOppositeSide);
        invalidate();
    }

    private int getChordWidth() {
        double chord = 2 * mRadius * Math.sin(mDPhi / 2);
        return Math.round((float) chord);
    }
}