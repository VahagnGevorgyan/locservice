package com.locservice.ui.controls;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.utils.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ListView;


@SuppressLint("NewApi") public class CustomListView extends ListView {

	private static final String TAG = CustomListView.class.getSimpleName();
	
	private Bitmap background;
	private Context context;

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_rewards);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		
		
		Point size = new Point();
		WindowManager w = ((Activity)context).getWindowManager();
		w.getDefaultDisplay().getSize(size);
     	
		
		int count = getChildCount();
		int top = count > 0 ? getChildAt(0).getTop() : 0;
		int backgroundWidth = background.getWidth();
     	int backgroundHeight = background.getHeight();
     	
//     	backgroundHeight = size.y;
//     	backgroundWidth = size.x;
     	
     	int width = getWidth();
     	int height = getHeight();
     	
     	if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CustomListView.dispatchDraw : SIZE.X : " + size.x + " : SIZE.Y : " + size.y);

     	for (int y = top; y < height; y += backgroundHeight) {
     		for (int x = 0; x < width; x += backgroundWidth) {
     			canvas.drawBitmap(background, x, y, null);
     		}
     	}
		
//		Point size = new Point();
//		WindowManager w = ((Activity)context).getWindowManager();
//		w.getDefaultDisplay().getSize(size);
//     	canvas.drawBitmap(background, size.x, size.y, null);
//     	if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CustomListView.dispatchDraw : SIZE.X : " + size.x + " : SIZE.Y : " + size.y);
     	
     	super.dispatchDraw(canvas);
	}
}