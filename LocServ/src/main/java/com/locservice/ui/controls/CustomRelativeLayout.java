package com.locservice.ui.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;

public class CustomRelativeLayout extends RelativeLayout {
	
	private static final String TAG = CustomRelativeLayout.class.getSimpleName();

	private int mTouchSlop;
    private float mPrevX;
	
	public CustomRelativeLayout(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CustomRelativeLayout.onInterceptTouchEvent ");
		
	    switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	mPrevX = MotionEvent.obtain(ev).getX();
	            break;
	        case MotionEvent.ACTION_MOVE:
	        	final float eventX = ev.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                if (xDiff > mTouchSlop) {
                    return false;
                }
	    }
	    return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	
	
	

}
