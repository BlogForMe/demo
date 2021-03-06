package com.chs.androiddailytext.slideview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.blankj.utilcode.util.SizeUtils;
import com.chs.androiddailytext.R;


/**
 * <b>Project:</b> SlideDetailsLayout<br>
 * <b>Create Date:</b> 16/1/22<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Pull up to open panel, pull down to close panel.
 * <br>
 */
@SuppressWarnings("unused")
public class SlideDetailsLayout extends ViewGroup {

    /**
     * Callback for panel OPEN-CLOSE status changed.
     */
    public interface OnSlideDetailsListener {
        /**
         * Called after status changed.
         *
         * @param status {@link Status}
         */
        void onStatucChanged(Status status);
    }

    public enum Status {
        /** Panel is closed */
        CLOSE,
        /** Panel is opened */
        OPEN;

        public static Status valueOf(int stats) {
            if (0 == stats) {
                return CLOSE;
            } else if (1 == stats) {
                return OPEN;
            } else {
                return CLOSE;
            }
        }
    }

    private static final float DEFAULT_PERCENT = 0.2f;
    private static final int DEFAULT_DURATION = 300;
    private static final float DEFAULT_MAX_VELOCITY = 2500f;

    private View mFrontView;
    private View mBehindView;

    private float mTouchSlop;
    private float mInitMotionY;
    private float mInitMotionX;


    private View mTarget;
    private float mSlideOffset;
    private Status mStatus = Status.OPEN;
    private boolean isFirstShowBehindView = true;
    private float mPercent = DEFAULT_PERCENT;
    private long mDuration = DEFAULT_DURATION;
    private int mDefaultPanel = 0;
    private VelocityTracker mVelocityTracker;

    private OnSlideDetailsListener mOnSlideDetailsListener;

    /**
     * ???????????????????????????????????????????????????1/3
     */
    private int mFirstHeight;
    private boolean isFirstIn = false;
    private OnFinishListener mOnFinishListener;
    public SlideDetailsLayout(Context context) {
        this(context, null);
    }

    public SlideDetailsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDetailsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideDetailsLayout, defStyleAttr, 0);
        mPercent = a.getFloat(R.styleable.SlideDetailsLayout_percent, DEFAULT_PERCENT);
        mDuration = a.getInt(R.styleable.SlideDetailsLayout_duration, DEFAULT_DURATION);
        mDefaultPanel = a.getInt(R.styleable.SlideDetailsLayout_default_panel, 0);
        a.recycle();

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        mFirstHeight = screenHeight*2/3;
    }

    public void setFirstIn(boolean firstIn) {
        isFirstIn = firstIn;
    }

    /**
     * Set the callback of panel OPEN-CLOSE status.
     *
     * @param listener {@link OnSlideDetailsListener}
     */
    public void setOnSlideDetailsListener(OnSlideDetailsListener listener) {
        this.mOnSlideDetailsListener = listener;
    }

    /**
     * Open pannel smoothly.
     *
     * @param smooth true, smoothly. false otherwise.
     */
    public void smoothOpen(boolean smooth) {
        if (mStatus != Status.OPEN) {
            mStatus = Status.OPEN;
            final float height = -getMeasuredHeight();
            if(isFirstIn){
                animatorSwitch(-(float) mFirstHeight, height, true, smooth ? mDuration : 0);
            }else {
                animatorSwitch(0, height, true, smooth ? mDuration : 0);
            }
        }
    }

    /**
     * Close pannel smoothly.
     *
     * @param smooth true, smoothly. false otherwise.
     */
    public void smoothClose(boolean smooth) {
        if (mStatus != Status.CLOSE) {
            mStatus = Status.OPEN;
            final float height = -getMeasuredHeight();
            animatorSwitch(height, 0, true, smooth ? mDuration : 0);
        }
    }

    /**
     * Set the float value for indicate the moment of switch panel
     *
     * @param percent (0.0, 1.0)
     */
    public void setPercent(float percent) {
        this.mPercent = percent;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept childs more than 1!!");
        }

        mFrontView = getChildAt(0);
        mBehindView = getChildAt(1);

        // set behindview's visibility to GONE before show.
//        mBehindView.setVisibility(GONE);
        if (mDefaultPanel == 1) {
            post(new Runnable() {
                @Override
                public void run() {
                    smoothOpen(false);
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int pWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int pHeight = MeasureSpec.getSize(heightMeasureSpec);

        final int childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(pHeight, MeasureSpec.EXACTLY);

        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            // skip measure if gone
            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
        }

        setMeasuredDimension(pWidth, pHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int left = l;
        final int right = r;
        int top;
        int bottom;

        final int offset = (int) mSlideOffset;

        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);

            // skip layout
            if (child.getVisibility() == GONE) {
                continue;
            }

            if (child == mBehindView) {
                top = b + offset;
                bottom = top + b - t;
            } else {
                top = t + offset;
                bottom = b + offset;
            }

            child.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (null == mTarget) {
            return false;
        }

        if (!isEnabled()) {
            return false;
        }

        final int aciton = MotionEventCompat.getActionMasked(ev);

        boolean shouldIntercept = false;
        switch (aciton) {
            case MotionEvent.ACTION_DOWN: {
                if (null == mVelocityTracker) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(ev);

                mInitMotionX = ev.getX();
                if(isFirstIn){
                    mInitMotionY = ev.getY()+mFirstHeight;
                }else {
                    mInitMotionY = ev.getY();
                }
                shouldIntercept = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();

                final float xDiff = x - mInitMotionX;
                final float yDiff = y - mInitMotionY;

                if (canChildScrollVertically((int) yDiff)) {
                    shouldIntercept = false;
//                    if (DEBUG) {
//                        Log.d(TAG, "intercept, child can scroll vertically, do not intercept");
//                    }
                } else {
                    final float xDiffabs = Math.abs(xDiff);
                    final float yDiffabs = Math.abs(yDiff);

                    // intercept rules???
                    // 1. The vertical displacement is larger than the horizontal displacement;
                    // 2. Panel stauts is CLOSE???slide up
                    // 3. Panel status is OPEN???slide down
                    if (yDiffabs > mTouchSlop && yDiffabs >= xDiffabs
                        && !(mStatus == Status.CLOSE && yDiff > 0
                             || mStatus == Status.OPEN && yDiff < 0)) {
                        shouldIntercept = true;
//                        if (DEBUG) {
//                            Log.d(TAG, "intercept, intercept events");
//                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                shouldIntercept = false;
                break;
            }
            default:

        }

        return shouldIntercept;
    }

    private void recycleVelocityTracker(){
        if(null != mVelocityTracker){
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (null == mTarget) {
            return false;
        }

        if (!isEnabled()) {
            return false;
        }


        boolean wantTouch = true;
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // if target is a view, we want the DOWN action.
                if (mTarget instanceof View) {
                    wantTouch = true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000);
                final float y = ev.getY();
                final float yDiff = y - mInitMotionY;
                Log.i("yDiff",yDiff+"");
                if (canChildScrollVertically(((int) yDiff))) {
                    wantTouch = false;
                } else {
                    processTouchEvent(yDiff);
                    wantTouch = true;
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                finishTouchEvent();
                recycleVelocityTracker();
                wantTouch = false;
                break;
            }
            default:
        }
        return wantTouch;
    }

    /**
     * @param offset Displacement in vertically.
     */
    public void processTouchEvent(final float offset) {
        if (Math.abs(offset) < mTouchSlop) {
            return;
        }

        final float oldOffset = mSlideOffset;
        // pull up to open
        if (mStatus == Status.CLOSE) {
            // reset if pull down
            if (offset >= 0) {
                mSlideOffset = 0;
            } else {
                mSlideOffset = offset;
            }

            if (mSlideOffset == oldOffset) {
                return;
            }

            // pull down to close
        } else if (mStatus == Status.OPEN) {
            final float pHeight = -getMeasuredHeight();
            // reset if pull up
            if (offset <= 0) {
                mSlideOffset = pHeight;
            } else {
                final float newOffset = pHeight + offset;
                mSlideOffset = newOffset;
            }

            if (mSlideOffset == oldOffset) {
                return;
            }
        }

//        if (SlideDebug.DEBUG) {
//            Log.v("slide", "process, offset: " + mSlideOffset);
//        }
        // relayout
        requestLayout();
    }

    /**
     * Called after gesture is ending.
     */
    private void finishTouchEvent() {
        final int pHeight = getMeasuredHeight();
        final int percent = (int) (pHeight * mPercent);
        final float offset = mSlideOffset;

        boolean changed = false;
        final float yVelocity = mVelocityTracker.getYVelocity();

//        if (DEBUG) {
//            Log.v(TAG, "finish, offset: " + offset + ", percent: " + percent + ", yVelocity: " + yVelocity);
//        }

        if (Status.CLOSE == mStatus) {
            if (offset <= -percent || yVelocity <= -DEFAULT_MAX_VELOCITY) {
                mSlideOffset = -pHeight;
                mStatus = Status.OPEN;
                changed = true;
            } else {
                // keep panel closed
                mSlideOffset = 0;
            }
        } else if (Status.OPEN == mStatus) {
            if ((offset + pHeight) >= percent || yVelocity >= DEFAULT_MAX_VELOCITY) {
                mSlideOffset = 0;
                mStatus = Status.CLOSE;
                changed = true;
            } else {
                // keep panel opened
                mSlideOffset = -pHeight;
            }
        }

        animatorSwitch(offset, mSlideOffset, changed);
        isFirstIn = false;
        if(mOnFinishListener!=null){
            mOnFinishListener.finish();
        }
    }

    private void animatorSwitch(final float start, final float end) {
        animatorSwitch(start, end, true, mDuration);
    }

    private void animatorSwitch(final float start, final float end, final long duration) {
        animatorSwitch(start, end, true, duration);
    }

    private void animatorSwitch(final float start, final float end, final boolean changed) {
        animatorSwitch(start, end, changed, mDuration);
    }

    private void animatorSwitch(final float start,
                                final float end,
                                final boolean changed,
                                final long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSlideOffset = (float) animation.getAnimatedValue();
                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (changed) {
                    if (mStatus == Status.OPEN) {
                        checkAndFirstOpenPanel();
                    }

                    if (null != mOnSlideDetailsListener) {
                        mOnSlideDetailsListener.onStatucChanged(mStatus);
                    }
                }
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * Whether the closed pannel is opened at first time.
     * If open first, we should set the behind view's visibility as VISIBLE.
     */
    private void checkAndFirstOpenPanel() {
        if (isFirstShowBehindView) {
            isFirstShowBehindView = false;
            mBehindView.setVisibility(VISIBLE);
        }
    }

    /**
     * When pulling, target view changed by the panel status. If panel opened, the target is behind view.
     * Front view is for otherwise.
     */
    private void ensureTarget() {
        if (mStatus == Status.CLOSE) {
            mTarget = mFrontView;
        } else {
            mTarget = mBehindView;
        }
    }

    /**
     * Check child view can srcollable in vertical direction.
     *
     * @param direction Negative to check scrolling up, positive to check scrolling down.
     *
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    protected boolean canChildScrollVertically(int direction) {
        return innerCanChildScrollVertically(mTarget, -direction);
    }

    private boolean innerCanChildScrollVertically(View view, int direction) {
        if (view instanceof ViewGroup) {

            if(mStatus == Status.OPEN&&mInitMotionY< SizeUtils.dp2px(50)){
                return false;
            }else if(mStatus == Status.OPEN&&mInitMotionY> SizeUtils.dp2px(50)){
                return true;
            }

            final ViewGroup vGroup = (ViewGroup) view;
            View child;
            boolean result;
            for (int i = 0; i < vGroup.getChildCount(); i++) {
                child = vGroup.getChildAt(i);

                if (child instanceof View) {
                    result = ViewCompat.canScrollVertically(child, direction);
                } else {
                    result = innerCanChildScrollVertically(child, direction);
                }

                if (result) {
                    return true;
                }
            }
        }

        return ViewCompat.canScrollVertically(view, direction);
    }

    protected boolean canListViewSroll(AbsListView absListView) {
        if (mStatus == Status.OPEN) {
            return absListView.getChildCount() > 0
                   && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                                                                               .getTop() <
                                                                    absListView.getPaddingTop());
        } else {
            final int count = absListView.getChildCount();
            return count > 0
                   && (absListView.getLastVisiblePosition() < count - 1
                       || absListView.getChildAt(count - 1)
                                     .getBottom() > absListView.getMeasuredHeight());
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.offset = mSlideOffset;
        ss.status = mStatus.ordinal();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mSlideOffset = ss.offset;
        mStatus = Status.valueOf(ss.status);

        if (mStatus == Status.OPEN) {
            mBehindView.setVisibility(VISIBLE);
        }

        requestLayout();
    }

    static class SavedState extends BaseSavedState {

        private float offset;
        private int status;

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source
         */
        public SavedState(Parcel source) {
            super(source);
            offset = source.readFloat();
            status = source.readInt();
        }

        /**
         * Constructor called by derived classes when creating their SavedState objects
         *
         * @param superState The state of the superclass of this view
         */
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(offset);
            out.writeInt(status);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    interface  OnFinishListener{
        /**
         * ????????????
         */
        void finish();
    }

}
