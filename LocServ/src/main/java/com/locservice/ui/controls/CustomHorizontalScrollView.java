package com.locservice.ui.controls;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;

import com.locservice.exceptions.ZeroChildException;

public class CustomHorizontalScrollView extends HorizontalScrollView {

	public interface CustomHorizontalScrollViewListener {
		public abstract void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	CustomHorizontalScrollViewListener mCustomHorizontalScrollViewListener;

	public void setCustomHorizontalScrollViewListener(CustomHorizontalScrollViewListener listener){
		mCustomHorizontalScrollViewListener = listener;
	}


	Context context;
	int prevIndex = 0;
	
	/**
	 * Constructor
	 * @param context
	 * @param attrs
	 */
	public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setSmoothScrollingEnabled(true);
	}

	public <T> void setAdapter(Context context, ArrayAdapter<T> mAdapter) {

		try {
			fillViewWithAdapter(mAdapter);
		} catch (ZeroChildException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * Method for filling view with adapter
	 * @param <T>
	 * @param mAdapter
	 * @throws ZeroChildException
	 */
	private <T> void fillViewWithAdapter(ArrayAdapter<T> mAdapter)
			throws ZeroChildException {
		if (getChildCount() == 0) {
			throw new ZeroChildException(
					"CenterLockHorizontalScrollView must have one child");
		}
		if (getChildCount() == 0 || mAdapter == null)
			return;

		ViewGroup parent = (ViewGroup) getChildAt(0);

		parent.removeAllViews();

		for (int i = 0; i < mAdapter.getCount(); i++) {
			parent.addView(mAdapter.getView(i, null, parent));
		}
	}

	public void setCenter(int index) {

		ViewGroup parent = (ViewGroup) getChildAt(0);

		View view = parent.getChildAt(index);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenWidth = displaymetrics.widthPixels;

		int scrollX = (view.getLeft() - (screenWidth / 2))
				+ (view.getWidth() / 2);
		this.smoothScrollTo(scrollX, 0);
		prevIndex = index;
	}
	
	public void setScrollEnabled(boolean enable) {
		if(!enable) {
			this.setOnTouchListener(new OnTouch());
			setHorizontalFadingEdgeEnabled(true);
			setVerticalFadingEdgeEnabled(true);
		} else {
			this.setOnTouchListener(null);
			setHorizontalFadingEdgeEnabled(false);
			setVerticalFadingEdgeEnabled(false);
		}
	}
	
	private class OnTouch implements OnTouchListener
	{
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
	    	return true;
	    }
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if(mCustomHorizontalScrollViewListener != null){
			mCustomHorizontalScrollViewListener.onScrollChanged(l, t, oldl, oldt);
		}
	}
}
