package com.locservice.ui.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.utils.Logger;

public class CounterDigitAnimation {
	
	private final static String TAG = CounterDigitAnimation.class.getSimpleName(); 
	
	private Context mContext;
	private Timer mTimer = null;

	private int index_timer_0;
	private int index_timer_1;
	private int index_timer_2;
	private int index_timer_3;
	private int index_timer_4;

	public CounterDigitAnimation(Context context) {
		this.mContext = context;
	}
	
	/**
	 * Method for single digit animation
	 * @param id
	 * @param value
	 */
	public void animateDigit(final int id, final long value, final DigitAnimateNumber type) {
		final View v = ((Activity)mContext).findViewById(id);
		if (v == null) {
			return;
		}
		float fontScale = mContext.getResources().getConfiguration().fontScale;
		final TextView text1 = (TextView) v.findViewById(R.id.text1);
		text1.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_counter_text_size) / fontScale);
		final TextView text2 = (TextView) v.findViewById(R.id.text2);
		text2.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_counter_text_size) / fontScale);

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CounterDigitAnimation.animateDigit : text1 : " + text1.getText().toString());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CounterDigitAnimation.animateDigit : text2 : " + text2.getText().toString());
		boolean running = false;
		if (text1.getAnimation() != null)
			running = !text1.getAnimation().hasEnded();
		if (text1.getText().toString().equals(String.valueOf(value)) || running) return;
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.counter_slide_out);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				text1.setText("" + value);
			}
		});
		text1.startAnimation(animation);
		animation = AnimationUtils.loadAnimation (mContext, R.anim.counter_slide_in);
		text2.startAnimation(animation);
		text2.setText("" + value);
	}
	
	/**
	 * Method for starting digit animation with timer
	 */
	public void startAnimation() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CounterDigitAnimation.startAnimation : index : " + index_timer_0);

		mTimer = new Timer();
		index_timer_0 = 9;
		index_timer_1 = 9;
		index_timer_2 = 1;

		final Handler uiHandler = new Handler();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {

				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						if (CMAppGlobals.DEBUG)
							Logger.i(TAG, ":: CounterDigitAnimation.Timer : index : " + index_timer_0);
						index_timer_0++;
						// animate
						if (index_timer_0 > 9) {
							index_timer_0 = 0;
							index_timer_1++;
							if (index_timer_1 > 9) {
								index_timer_2++;
								animateDigit(R.id.layoutDigit3, index_timer_2, DigitAnimateNumber.DIGIT_2);
								index_timer_1 = 0;
							}
							animateDigit(R.id.layoutDigit2, index_timer_1, DigitAnimateNumber.DIGIT_1);
						}
						animateDigit(R.id.layoutDigit1, index_timer_0, DigitAnimateNumber.DIGIT_0);
					}
				});
			}

			;
		}, 0L, 2000); 
		
	} // end method startAnimation
	
	/**
	 * Method for stopping digit animation timer
	 */
	public void StopAnimation() {
		if(mTimer != null) mTimer.cancel();
		
	} // end method StopOrderTimer

	private int current_index_timer_0 = -1;
	private int current_index_timer_1 = -1;
	private int current_index_timer_2 = -1;
	private int current_index_timer_3 = -1;
	private int current_index_timer_4 = -1;

	/**
	 * Method for starting digit animation with timer
	 * @param index_timer_0
	 * @param index_timer_1
	 * @param index_timer_2
	 * @param index_timer_3
	 * @param index_timer_4
	 */
	public void startAnimation(int index_timer_0, int index_timer_1, int index_timer_2, int index_timer_3, int index_timer_4) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CounterDigitAnimation.startAnimation : index0 : " + index_timer_0
								+ " : index1 : " + index_timer_1
								+ " : index2 : " + index_timer_2
								+ " : index3 : " + index_timer_3
								+ " : index4 : " + index_timer_4);

		mTimer = new Timer();
		this.index_timer_0 = index_timer_0;
		this.index_timer_1 = index_timer_1;
		this.index_timer_2 = index_timer_2;
		this.index_timer_3 = index_timer_3;
		this.index_timer_4 = index_timer_4;
		final Handler uiHandler = new Handler();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {

				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CounterDigitAnimation.Timer : index : " + CounterDigitAnimation.this.index_timer_0);

						if (CounterDigitAnimation.this.index_timer_0 != CounterDigitAnimation.this.current_index_timer_0) {
							animateDigit(R.id.layoutDigit1, CounterDigitAnimation.this.index_timer_0, DigitAnimateNumber.DIGIT_0);
							CounterDigitAnimation.this.current_index_timer_0 = CounterDigitAnimation.this.index_timer_0;
						}

						if (CounterDigitAnimation.this.index_timer_1 != CounterDigitAnimation.this.current_index_timer_1) {
							animateDigit(R.id.layoutDigit2, CounterDigitAnimation.this.index_timer_1, DigitAnimateNumber.DIGIT_1);
							CounterDigitAnimation.this.current_index_timer_1 = CounterDigitAnimation.this.index_timer_1;
						}

						if (CounterDigitAnimation.this.index_timer_2 != CounterDigitAnimation.this.current_index_timer_2) {
							animateDigit(R.id.layoutDigit3, CounterDigitAnimation.this.index_timer_2, DigitAnimateNumber.DIGIT_2);
							CounterDigitAnimation.this.current_index_timer_2 = CounterDigitAnimation.this.index_timer_2;
						}

						if (CounterDigitAnimation.this.index_timer_3 != CounterDigitAnimation.this.current_index_timer_3) {
							animateDigit(R.id.layoutDigit4, CounterDigitAnimation.this.index_timer_3, DigitAnimateNumber.DIGIT_3);
							CounterDigitAnimation.this.current_index_timer_3 = CounterDigitAnimation.this.index_timer_3;
						}

						if (CounterDigitAnimation.this.index_timer_4 != CounterDigitAnimation.this.current_index_timer_4) {
							animateDigit(R.id.layoutDigit5, CounterDigitAnimation.this.index_timer_4, DigitAnimateNumber.DIGIT_4);
							CounterDigitAnimation.this.current_index_timer_4 = CounterDigitAnimation.this.index_timer_4;
						}

					}
				});
			};
		}, 0L, 2000);

	} // end method startAnimation

}
