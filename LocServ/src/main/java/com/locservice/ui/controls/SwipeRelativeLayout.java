package com.locservice.ui.controls;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 23 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class SwipeRelativeLayout extends RelativeLayout {

    private static final String TAG = SwipeRelativeLayout.class.getSimpleName();

    private int mTouchSlop;
    private boolean mIsSwiping;
    private float mDownY;
    private boolean mShown;
    private OnSwipeListener mOnSwipeListener;

    private float mStartY;
    private float mStartX;

    private float interceptDownY;

    private ViewGroup mScrollableView;
    private boolean isScrolled;

    /**
    * Constructor
    * @param context - Context
    */
    public SwipeRelativeLayout(Context context) {
        super(context);
        init();

    } // end constructor

    /**
     * Constructor
     * @param context - Context
     * @param attrs - Attribute Set
     */
    public SwipeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    } // end constructor

    /**
     * Constructor
     * @param context - Context
     * @param attrs - Attribute Set
     * @param defStyle - Def Style
     */
    public SwipeRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    } // end constructor

    /**
     * Method for initializing
     */
    private void init() {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();

    } // end method init

    /**
     * Method fot setting scrollable view
     * @param scrollableView - Scrollable view
     */
    public void setScrollableView(ViewGroup scrollableView) {
        mScrollableView = scrollableView;
        mScrollableView.post(new Runnable(){

            @Override
            public void run() {
                ViewTreeObserver observer = mScrollableView.getViewTreeObserver();
                observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){

                    @Override
                    public void onScrollChanged() {
                        int scrollX = mScrollableView.getScrollX();
                        int scrollY = mScrollableView.getScrollY();
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.setScrollableView : scrollX : " + scrollX + " : scrollY : " + scrollY);
                        isScrolled = scrollY != 0;

                    }});
            }});

    } // end method setScrollableView

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsSwiping = false;
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mShown = false;
                mDownY = ev.getRawY();
                interceptDownY = ev.getY();
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onInterceptTouchEvent : ddddd : ACTION_DOWN : " + ev.getY());
                return false;
            case MotionEvent.ACTION_MOVE:
//                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onInterceptTouchEvent : ACTION_MOVE : isDrag mIsSwiping : " + mIsSwiping);

                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onInterceptTouchEvent : ddddd : ACTION_MOVE " + ev.getY());
                float touchX = ev.getX();
                float touchY = ev.getY();

                // for scroll view
                if (mScrollableView != null
                        && mScrollableView.getVisibility() == View.VISIBLE
                        && canScroll(mScrollableView)) {

                    float scrollViewX = mScrollableView.getX();
                    float scrollViewY= getScrollViewTop(mScrollableView);

//                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : inScroll : leftTopX : " + scrollViewX);
//                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : inScroll : leftTopY : " + scrollViewY);
//                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : inScroll : rightBottomX : " + (scrollViewX + mScrollableView.getWidth()));
//                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : inScroll : rightBottomY : " + (scrollViewY + mScrollableView.getHeight()));
//
//
//                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : inScroll : touchX : " + touchX);
//                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : inScroll : touchY : " + touchY);
//
//                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : inScroll : returnValue : "
//                            + (touchY > scrollViewY
//                            && touchY < scrollViewY + mScrollableView.getHeight()
//                            && touchX > scrollViewX
//                            && touchX < scrollViewX + mScrollableView.getWidth()));

                    if (touchY > scrollViewY
                            && touchY < scrollViewY + mScrollableView.getHeight()
                            && touchX > scrollViewX
                            && touchX < scrollViewX + mScrollableView.getWidth()) {

                        if (mScrollableView instanceof CustomScrollView
                                &&((CustomScrollView)mScrollableView).isScrolledToEnd()
                                && interceptDownY - touchY > 0) {
                            return true;
                        }

                        return false;
                    }
                }

                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : mIsSwiping : " + mIsSwiping);
                if (mIsSwiping) {
                    return true;
                }

                final int yDiff = calculateDistanceY(ev);
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onInterceptTouchEvent : ACTION_MOVE : yDiff : " + yDiff);
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onInterceptTouchEvent : ACTION_MOVE : mTouchSlop : " + mTouchSlop);

//                boolean isDrag = yDiff > mTouchSlop;
                boolean isDrag = Math.abs(yDiff) > mTouchSlop;
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onInterceptTouchEvent : ACTION_MOVE : isDrag : " + isDrag);

//                if (yDiff > mTouchSlop) {
                if (yDiff != 0 && isDrag) {
                    mIsSwiping = true;
                    return true;
                }
                break;
        }

        return false;

    } // end method onInterceptTouchEvent

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            float mEndY = event.getY();
            float mEndX = event.getX();
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_UP : mStartY : " + mStartY + " : mEndY : " + mEndY);
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_UP : mStartX : " + mStartX + " : mEndX : " + mEndX);

            if (Math.abs(mStartY - mEndY) >= Math.abs(mStartX - mEndX) && Math.abs(mStartY - mEndY) > 30) { // 30 is min swipe size
                if (mStartY - mEndY < 0) {
                    if (mOnSwipeListener != null)
                        mOnSwipeListener.onSwipeDown();
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_UP : SWIPE_DOWN");
                } else {
                    if (mOnSwipeListener != null)
                        mOnSwipeListener.onSwipeUp();
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_UP : SWIPE_UP");
                }
            }
            mStartY = 0;
            mStartX = 0;
            mIsSwiping = false;
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mShown) {
                    mShown = true;
                }
                if (mStartY == 0)
                    mStartY = event.getY();

                if (mStartX == 0)
                    mStartX = event.getX();

                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SwipeRelativeLayout.onTouchEvent : ACTION_MOVE : mStartY : " + mStartY);

                return true;
        }
        return super.onTouchEvent(event);

    } // end method onTouchEvent

    /**
     * Method for getting scroll view top
     * @param view - Scroll view
     * @return - Top of Scroll view
     */
    private int getScrollViewTop(View view) {
        if (view.getParent() == view.getRootView())
            return view.getTop();
        else
            return view.getTop() + getScrollViewTop((View) view.getParent());

    } // end method getScrollViewTop

    /**
     * Method for calculating distance Y
     * @param ev - Motion event
     * @return - Distance Y
     */
    private int calculateDistanceY(MotionEvent ev) {
        return (int) (ev.getRawY() - mDownY);

    } // end method calculateDistanceY

    /**
     * Method for setting swipe listener
     * @param onSwipeListener - Swipe lestener
     */
    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;

    } // end method setOnSwipeListener

    /**
     * Interface for Swipe
     */
    public interface OnSwipeListener {
        void onSwipeDown();
        void onSwipeUp();

    } // end interface OnSwipeListener

    /**
     * @return Returns true this ScrollView can be scrolled
     */
    private boolean canScroll(ViewGroup scrollableView) {
        int childHeight = 0;
        for (int i = 0; i < scrollableView.getChildCount(); i++) {
            if (scrollableView.getChildAt(i) != null)
                childHeight += scrollableView.getChildAt(i).getHeight();
        }

        return scrollableView.getHeight() < childHeight + scrollableView.getPaddingTop() + scrollableView.getPaddingBottom();

    } // end method canScroll



}
