package com.locservice.ui.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class CustomAnimationListener implements AnimationListener {
	View mView;
	int mModifier;
	Context mContext;

	public CustomAnimationListener(View v, int modifier, Context c){
	    mView=v;
	    mModifier=modifier;
	    mContext=c;
	}
	public void onAnimationEnd(Animation animation) {
	    int[] pos={mView.getLeft(),mView.getTop()+mModifier,mView.getRight(),mView.getBottom()+mModifier};
	    mView.layout(pos[0],pos[1],pos[2],pos[3]);
	}

	public void onAnimationRepeat(Animation animation) {}

	public void onAnimationStart(Animation animation) {}

}