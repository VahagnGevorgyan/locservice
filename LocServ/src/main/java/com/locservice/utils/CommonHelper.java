package com.locservice.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.locservice.CMAppGlobals;
import com.locservice.R;

@SuppressLint("NewApi") public class CommonHelper {

	private static final String TAG = CommonHelper.class.getSimpleName();

	/**
	 * Method for setting touch listener to layout
	 * Changing layout background after touch
	 * @param view
	 * @param event
	 * @param drawable
	 * @param selectDrawable
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean setOnTouchLayout(View view, MotionEvent event, Drawable drawable, Drawable selectDrawable) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            		view.setBackgroundDrawable(selectDrawable);
            	} else {
            		view.setBackground(selectDrawable);
            	}
            	break;
            case MotionEvent.ACTION_MOVE:	
            	break;
            case MotionEvent.ACTION_UP:
				if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					view.setBackgroundDrawable(drawable);
				} else {
					view.setBackground(drawable);
				}
            	break;
            default:
				if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					view.setBackgroundDrawable(drawable);
				} else {
					view.setBackground(drawable);
				}
            	break;
        }
        return false;
	}
	
	/**
	 * Method for setting touch listener to layout
	 * Changing layout background after touch
	 * @param view
	 * @param event
	 * @param drawable
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean setOnTouchLayout(View view, MotionEvent event, Drawable drawable) {
		Drawable mDrawable = drawable;
		mDrawable.setAlpha(10);
		int sdk = android.os.Build.VERSION.SDK_INT;
		switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            		view.setBackgroundDrawable(mDrawable);
            	} else {
            		view.setBackground(mDrawable);
            	}
            	break;
            case MotionEvent.ACTION_MOVE:
            	break;
            case MotionEvent.ACTION_UP:
            	view.performClick();
				if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					view.setBackgroundDrawable(drawable);
				} else {
					view.setBackground(drawable);
				}
            	break;
            default:
            	if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					view.setBackgroundDrawable(drawable);
				} else {
					view.setBackground(drawable);
				}
            	break;
        }
        return false;
	}
	
	/**
	 * Method for setting touch listener to layout
	 * Changing layout background after touch
	 * @param view
	 * @param event
	 * @return
	 */
	public static boolean setOnTouchLayoutColor(Context context, View view, MotionEvent event) {
		switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	view.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_info));
            	break;
            case MotionEvent.ACTION_MOVE:	
            	break;
            case MotionEvent.ACTION_UP:
            	view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            	break;
            default:
            	view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            	break;
        }
        return false;
	}
	
	/*
	 * Setting touch image
	 */
	public static boolean setOnTouchImage(ImageButton view, MotionEvent event) {
		switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	view.setColorFilter(Color.argb(50, 0, 0, 0));
            	break;
            case MotionEvent.ACTION_MOVE:	
            	break;
            case MotionEvent.ACTION_UP:
            	view.setColorFilter(Color.argb(0, 0, 0, 0));
            	break;
            default:
            	view.setColorFilter(Color.argb(0, 0, 0, 0));
            	break;
        }
        return false;
	}
	
	/*
	 * Setting touch image
	 */
	public static boolean setOnTouchImage(ImageView view, MotionEvent event) {
		switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	view.setColorFilter(Color.argb(50, 0, 0, 0));
            	break;
            case MotionEvent.ACTION_MOVE:
            	break;
            case MotionEvent.ACTION_UP:
            	view.setColorFilter(Color.argb(0, 0, 0, 0));
            	break;
            default:
            	view.setColorFilter(Color.argb(0, 0, 0, 0));
            	break;
        }
        return false;
	}

	/**
	 * Method for calculating time in minutes
	 *
	 * @param time
	 * @return
	 */
	public static String calculateTimeMin(int time) {
		String minString = String.valueOf(time);

		int minute = time / 60;
		if (minute <= 3) {
			minString = "3";
		}
		if (minute > 3 && minute <= 15) {
			minString = String.valueOf(minute);
		}
		if (minute > 15) {
			minString = ">";
			minString += 15;
		}

		return minString;

	} // end method calculateTimeMin


	/**
	 * Method for getting device id
	 * @param context - current context
	 */
	public static String getDeviceId(Activity context) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonHelper.getDeviceId : context : " + context);
		// get device id
		if (!PermissionUtils.ensurePermission(context, android.Manifest.permission.READ_PHONE_STATE, PermissionUtils.READ_PHONE_STATE)) {
			return null;
		}
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return (telephonyManager.getDeviceId() == null) ? Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
				: telephonyManager.getDeviceId();

	} // end method getDeviceId

	/**
	 * Method for getting formatted phone
	 * @param phone
	 * @return
	 */
	public static String getFormattedPhone(String phone) {
		String formattedPhone = "";
		for (int i = 0; i < phone.length(); i++) {
			if (i == 4) {
				formattedPhone += " ";
			}
			if (i == 7 || i == 9) {
				formattedPhone += "-";
			}
			formattedPhone += phone.charAt(i);
		}

		return formattedPhone;

	} // end method getFormattedPhone


}
