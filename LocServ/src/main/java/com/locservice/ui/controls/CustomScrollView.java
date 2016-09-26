package com.locservice.ui.controls;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 18 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CustomScrollView extends ScrollView {

    private static final String TAG = CustomScrollView.class.getSimpleName();
    private boolean isScrolledToEnd;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CustomScrollView.onScrollChanged");

        // We take the last son in the scrollview
        View view = (View) getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));

        isScrolledToEnd = false;

        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            // do stuff
            isScrolledToEnd = true;
        }

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CustomScrollView.onScrollChanged : isScrolledToEnd : " + isScrolledToEnd);
    }

    public boolean isScrolledToEnd() {
        return isScrolledToEnd;
    }
}
