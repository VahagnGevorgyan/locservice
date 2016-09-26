package com.locservice.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.application.LocServicePreferences;
import com.locservice.gcm.GCMIntentService;
import com.locservice.gcm.GCMMessages;
import com.locservice.gcm.GCMUtils;
import com.locservice.messages.Message;
import com.locservice.ui.ChatActivity;
import com.locservice.ui.MainActivity;
import com.locservice.ui.OrderInfoActivity;
import com.locservice.ui.helpers.OrderState;

import java.util.List;

public class Utils {

	/** For logging */
	private static final String TAG = Utils.class.getSimpleName();


	/**
	 * 	ConnectivityManager for checking network status.
	 */
	private static ConnectivityManager connectivityManager = null;

	/**
	 * Check network status.
	 */
	public static boolean checkNetworkStatus(Context context) {
		boolean status = false;

		if (null == connectivityManager) {
			connectivityManager = (ConnectivityManager) CMApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		}

		if (null != connectivityManager) {
			final NetworkInfo net = connectivityManager.getActiveNetworkInfo();

			if (null != net) {
				final int networkType = net.getType();

				if (networkType == ConnectivityManager.TYPE_WIFI
						|| networkType == ConnectivityManager.TYPE_MOBILE) {
					status = true;
				}
			}
		}
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.checkNetworkStatus : Network Status : " + status);
		return status;

	} // end method checkNetworkStatus

	/**
	 * Check network status.
	 */
	public static boolean checkNetworkStatus(Activity context) {
		boolean status = false;

		if (null == connectivityManager) {
			connectivityManager = (ConnectivityManager) CMApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		}

		if (null != connectivityManager) {
			final NetworkInfo net = connectivityManager.getActiveNetworkInfo();

			if (null != net) {
				final int networkType = net.getType();

				if (networkType == ConnectivityManager.TYPE_WIFI
						|| networkType == ConnectivityManager.TYPE_MOBILE) {
					status = true;
				}
			}
		}
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.checkNetworkStatus : Network Status : " + status);

		if(!status) {
			showNoConnectionDialog(context);
		}
		return status;

	} // end method checkNetworkStatus

	/**
	 * Method for showing network state popup
	 * @param context - current context
	 */
	public static void showNoConnectionDialog(final Activity context) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.showNoConnectionDialog ");

		final Activity ctx = context;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AppCompatAlertDialogStyle);
		builder.setCancelable(false);
		builder.setMessage(R.string.str_network_state_check);
		builder.setPositiveButton(R.string.str_network_state_update, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// restart activity
				restartActivity(context);
			}
		});
		builder.show();

	} // end method showNoConnectionDialog

	/**
	 * Method for restarting activity
	 * @param mActivity
	 */
	public static void restartActivity(Activity mActivity) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.restartActivity : mActivity : " + mActivity);

		Intent intent = mActivity.getIntent();
		mActivity.finish();
		mActivity.startActivity(intent);

	} // end method restartActivity

	/**
	 * Check if user registered.
	 */
	public static boolean isUserRegistered(Context context) {

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.isUserRegistered : AUTH_TOKEN : " + CMApplication.getAuthToken());
		if(!CMApplication.getAuthToken().equals("")) return true;

		String auth_token = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "");
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.isUserRegistered : PREF_AUTH_TOKEN : " + auth_token);
		if(!auth_token.equals("")) {
			CMApplication.setAuthToken(auth_token);
			return true;
		}
		return false;

	} // end method isUserRegistered

	/**
	 * Method for generating notifications
	 * @param context
	 * @param message
	 */
	public static void generateNotification(Context context, String message, int type) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.generateNotification : " +
				" : message : " + message + " : order_state : " + type);

		if(!checkApp(context)) {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.generateNotification : SHOW NOTIFICATION ");
			NotificationManager mNotificationManager = (NotificationManager) CMApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

			Class<?> startClass = ChatActivity.class;
			int number = 0;
			int notification_id = GCMIntentService.NOTIFICATION_ID;

			Intent resultIntent = new Intent(context, startClass);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack
			stackBuilder.addParentStack(startClass);
			// Adds the Intent to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			// Gets a PendingIntent containing the entire back stack
			PendingIntent resultPendingIntent = stackBuilder
					.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
							| PendingIntent.FLAG_ONE_SHOT);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.ic_nf_small)
					.setDefaults(Notification.DEFAULT_SOUND)
					.setAutoCancel(true)
					.setContentTitle(context.getString(R.string.app_name))
					.setStyle(
							new NotificationCompat.BigTextStyle().bigText(message))
					.setContentText(message).setNumber(number);

			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_nf_larg);
			mBuilder.setLargeIcon(bm);

			mBuilder.setContentIntent(resultPendingIntent);
			mNotificationManager.notify(notification_id, mBuilder.build());
		}


	} // end method generateNotification

	/**
	 * Method for generating notifications
	 * @param context
	 * @param message
	 * @param orderId
	 */
	public static void generateNotification(Context context, GCMMessages message, String orderId, int type) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.generateNotification : orderId : " + orderId +
				" : message : " + message + " : order_state : " + type);

		if(!checkApp(context)) {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.generateNotification : SHOW NOTIFICATION ");
			NotificationManager mNotificationManager = (NotificationManager) CMApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

			Class<?> startClass = getActivityClassByOrderState(type);
			int number = 0;
			int notification_id = GCMIntentService.NOTIFICATION_ID;

			Intent resultIntent = new Intent(context, startClass);
			resultIntent.putExtra(CMAppGlobals.EXTRA_ORDER_ID, orderId);
			resultIntent.putExtra(CMAppGlobals.EXTRA_ORDER_STATUS, OrderState.getStatusStringByState(GCMMessages.convertToOrderState(type)));
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack
			stackBuilder.addParentStack(startClass);
			// Adds the Intent to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			// Gets a PendingIntent containing the entire back stack
			PendingIntent resultPendingIntent = stackBuilder
					.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
							| PendingIntent.FLAG_ONE_SHOT);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.ic_nf_small)
					.setDefaults(Notification.DEFAULT_SOUND)
					.setAutoCancel(true)
					.setContentTitle(context.getString(R.string.app_name))
					.setStyle(
							new NotificationCompat.BigTextStyle().bigText(message
									.toString()))
					.setContentText(message.toString()).setNumber(number);

			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_nf_larg);
			mBuilder.setLargeIcon(bm);

			mBuilder.setContentIntent(resultPendingIntent);
			mNotificationManager.notify(notification_id, mBuilder.build());
		}


	} // end method generateNotification

	/**
	 * Method for generating notifications
	 * @param context
	 * @param subTitle
	 */
	public static void generateWebViewNotification(Context context, String title, String subTitle, Message webMessage) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.generateNotificationForVewView : " +
				" : subTitle : " + subTitle);

		if(!checkApp(context)) {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.generateNotificationForVewView : SHOW NOTIFICATION ");
			NotificationManager mNotificationManager = (NotificationManager) CMApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

			Class<?> startClass = MainActivity.class;
			int number = 0;
			int notification_id = GCMIntentService.NOTIFICATION_ID;

			Intent resultIntent = new Intent(context, startClass);
			resultIntent.putExtra(GCMUtils.EXTRA_WEB_VIEW_MESSAGE, webMessage);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack
			stackBuilder.addParentStack(startClass);
			// Adds the Intent to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			// Gets a PendingIntent containing the entire back stack
			PendingIntent resultPendingIntent = stackBuilder
					.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
							| PendingIntent.FLAG_ONE_SHOT);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.ic_nf_small)
					.setDefaults(Notification.DEFAULT_SOUND)
					.setAutoCancel(true)
					.setContentTitle(title)
					.setStyle(
							new NotificationCompat.BigTextStyle().bigText(subTitle))
					.setContentText(subTitle).setNumber(number);

			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_nf_larg);
			mBuilder.setLargeIcon(bm);

			mBuilder.setContentIntent(resultPendingIntent);
			mNotificationManager.notify(notification_id, mBuilder.build());
		}


	} // end method generateNotification

	/**
	 * Method for getting activity class by order state
	 * @param type
	 * @return
	 */
	public static Class<?> getActivityClassByOrderState(int type) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.getActivityClassByOrderState : type : " + type);

		Class<?> startClass = MainActivity.class;
		OrderState orderState = GCMMessages.convertToOrderState(type);
		if(orderState == OrderState.CC) {
			startClass = OrderInfoActivity.class;
		}

		return startClass;

	} // end method getActivityClassByOrderState

	/**
	 * Method for checking alarm for closing
	 * @param orderId
	 * @return
	 */
	public static boolean needCancelAlarm(String orderId) {
		CMApplication.removeActiveOrder(orderId);
		return CMApplication.getActiveOrders().size() == 0;

	} // end method needCancelAlarm

	/**
	 * Method for checking application foreground
	 * @param context
	 * @return
	 */
	public static boolean checkApp(Context context) {

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: Utils.checkApp ");

		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		ComponentName componentInfo = taskInfo.get(0).topActivity;
		if (componentInfo.getPackageName().equalsIgnoreCase("com.locservice")) {
			return true;
		} else {
			return false;
		}
	}

}
