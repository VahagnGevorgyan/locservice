package com.locservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.entities.OrderIdItem;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.Tariff;
import com.locservice.application.LocServicePreferences;
import com.locservice.ui.SplashActivity;
import com.locservice.utils.Logger;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class CMApplication extends MultiDexApplication {

	private static final String TAG = CMApplication.class.getSimpleName();

	private static Context appContext = null;
	// tariff
	private static ArrayList<Tariff> tariffList = new ArrayList<Tariff>();
	private static String version_uuid = "";
	/** Name of the main database */
	public static final String DATABASE_NAME = "LocServiceDatabase.db";
	/** Version of the Database */
	public static final int DATABASE_VERSION = 2;
	/** Authentication token */
	public static String AUTH_TOKEN = "";

	/** Google Analytics PropertyId **/
	private static final String PROPERTY_ID = "UA-47137814-1";


	VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
		@Override
		public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
			if (newToken == null) {
				Intent intent = new Intent(CMApplication.this, SplashActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
	};


	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		Fabric.with(this, new Answers());

		CMApplication.appContext = getApplicationContext();

		// package signatures
		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.locservice", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException | NoSuchAlgorithmException e) {
			if(CMAppGlobals.DEBUG)Logger.e(TAG, ":: CMApplication.onCreate : error : " + e);
		}

		vkAccessTokenTracker.startTracking();
		VKSdk.initialize(this);

		// Load Polygons

	}

	private Activity mCurrentActivity = null;
	public Activity getCurrentActivity(){
		return mCurrentActivity;
	}
	public void setCurrentActivity(Activity mCurrentActivity){
		this.mCurrentActivity = mCurrentActivity;
	}



	@Override
	protected void attachBaseContext(Context base) {
		 super.attachBaseContext(base);
		 MultiDex.install(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * common getter for application context
	 * @return - application context
	 */
	public static Context getAppContext() {

		return CMApplication.appContext;
	}

	public static String getAuthToken() {
		return AUTH_TOKEN;
	}

	public static void setAuthToken(String authToken) {
		AUTH_TOKEN = authToken;
	}

	public static String getTrackingOrderId() {
		return LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_ORDER_ID.key(), "");
	}

	public static void setTrackingOrderId(String orderIDs) {
		LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.CURRENT_ORDER_ID, orderIDs);
	}

	public static int getTrackingOrderStatus() {
		return LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.CURRENT_ORDER_STATUS.key(), 0);
	}

	public static List<OrderIdItem> mActiveOrders = new ArrayList<>();

	public static List<OrderIdItem> getActiveOrders() {
		return mActiveOrders;
	}

	public static void setActiveOrders(List<OrderIdItem> activeOrders) {
		mActiveOrders = activeOrders;
	}

	public static void setActiveOrder(OrderIdItem orderIdItem, boolean check) {
		if(check && !isOrderItemExists(orderIdItem.getIdOrder()))
			mActiveOrders.add(orderIdItem);
	}

	public static void removeActiveOrder(String orderId) {
		for (int i=0; i<mActiveOrders.size(); i++) {
			OrderIdItem item = mActiveOrders.get(i);
			if(item.getIdOrder().equals(orderId)) {
				mActiveOrders.remove(item);
				return;
			}
		}
	}

	public static void updateActiveOrders(List<OrderStatusData> orders) {
		mActiveOrders.clear();
		for (OrderStatusData item :
				orders) {
			OrderIdItem idItem = new OrderIdItem();
			idItem.setStatus(item.getStatus());
			idItem.setIdOrder(item.getIdOrder());
			mActiveOrders.add(idItem);
		}
	}

	public static boolean isOrderItemExists(String orderId) {
		for (OrderIdItem itemId :
				mActiveOrders) {
			if(itemId.getIdOrder() != null
					&& itemId.getIdOrder().equals(orderId)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Method for checking is numeric string
	 *
	 * @param string - numeric string
	 * @return - return is numeric
	 */
	public static boolean isNumeric(String string) {
		try {
			double number = Double.parseDouble(string);

		} catch (Exception e) {
			return false;
		}
		return true;

	} // end method isNumeric


	public static void setTrackingOrderStatus(int status) {
		LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.CURRENT_ORDER_STATUS, status);
	}

	public static String getTrackingOrderBonus() {
		return LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_ORDER_BONUS.key(), "");
	}

	public static String getTrackingOrderPrice() {
		return LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_ORDER_PRICE.key(), "");
	}

	public static String getTrackingOrderTripTime() {
		return LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_ORDER_TRIP_TIME.key(), "");
	}

	/**
	 * Method for encoding bitmap to base64
	 * @param image
	 * @return
	 */
	public static String encodeBitmapToBase64(Bitmap image) {
		Bitmap img = image;
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.JPEG, 100, bas);
		byte[] b = bas.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

		return imageEncoded;

	} // end method encodeBitmapToBase64

	/**
	 * Method for decoding base64 to bitmap
	 *
	 * @param base64
	 * @return
	 */
	public static Bitmap decodeBase64ToBitmap(String base64) {
		byte[] decodedByte = Base64.decode(base64, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

	} // end method decodeBase64ToBitmap

	/**
	 * Method for checking network is available
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();

	} // end method isNetworkAvailable


	private static final int CAR_NUMBER_MIN_INDEX_SIZE = 2;
	private static final int CAR_NUMBER_MAX_INDEX_SIZE = 3;
	private static final int CAR_NUMBER_UNREAL_INDEX_SIZE = 4;

	/**
	 * Method for setting car number
	 *
	 * @param leftText
	 * @param rightText
	 * @param driverInfo
	 */
	public static void setCarNumber(TextView leftText, TextView rightText, DriverInfo driverInfo) {

		if (driverInfo != null) {
			String carNumber = driverInfo.getCarNumber();
			String left = "";
			String right = "";
			if (CMApplication.isNumeric(carNumber.substring(carNumber.length() - CAR_NUMBER_UNREAL_INDEX_SIZE))) {
				left = carNumber.substring(0, carNumber.length() - CAR_NUMBER_MIN_INDEX_SIZE);
				right = carNumber.substring(carNumber.length() - CAR_NUMBER_MIN_INDEX_SIZE);
			} else if (CMApplication.isNumeric(carNumber.substring(carNumber.length() - CAR_NUMBER_MAX_INDEX_SIZE))) {
				left = carNumber.substring(0, carNumber.length() - CAR_NUMBER_MAX_INDEX_SIZE);
				right = carNumber.substring(carNumber.length() - CAR_NUMBER_MAX_INDEX_SIZE);
			} else {
				left = carNumber.substring(0, carNumber.length() - CAR_NUMBER_MIN_INDEX_SIZE);
				right = carNumber.substring(carNumber.length() - CAR_NUMBER_MIN_INDEX_SIZE);
			}

			leftText.setText(left);
			rightText.setText(right);
		}

	} // end method setCarNumber

	/**
	 * Method for getting dp by pixel
	 * @param dp
	 * @return
	 */
	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

	} // end method dpToPx

	/**
	 * Method for getting px by dp
	 * @param px
	 * @return
	 */
	public static int pxToDp(int px) {
		return (int) (px / Resources.getSystem().getDisplayMetrics().density);

	} // end method pxToDp

	/**
	 * Method for calculate Bitmap Size
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;

	} // end method calculateInSampleSize

	/**
	 * Method for decoding Sample dBitmap
	 * @param context
	 * @param imageUri
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmap(Context context, Uri imageUri, int reqWidth, int reqHeight) {

		final InputStream imageStream;
		final InputStream imageStreamBitmap;
		Bitmap bitmap = null;
		try {
			imageStream = context.getContentResolver().openInputStream(imageUri);

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(imageStream, null, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;

			imageStreamBitmap = context.getContentResolver().openInputStream(imageUri);

			bitmap = BitmapFactory.decodeStream(imageStreamBitmap, null, options);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return bitmap;

	} // end method decodeSampledBitmap

	/**
	 * Method for checking has Navigation Bar
	 * @param context
	 * @return
     */
	public static boolean hasNavigationBar(Context context) {
		Point appUsableSize = getAppUsableScreenSize(context);
		Point realScreenSize = getRealScreenSize(context);

		// navigation bar on the right
		if (appUsableSize.x < realScreenSize.x) {
			return true;
//			return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
		}

		// navigation bar at the bottom
		if (appUsableSize.y < realScreenSize.y) {
			return true;
//			return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
		}

		// navigation bar is not present
//		return new Point();
		return false;
	}

	/**
	 * Method for getting App Usable Screen Size
	 * @param context
	 * @return
     */
	public static Point getAppUsableScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;

	} // end method getAppUsableScreenSize

	/**
	 * Method for getting Real Screen
	 * Size
	 * @param context
	 * @return
     */
	public static Point getRealScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();

		if (Build.VERSION.SDK_INT >= 17) {
			display.getRealSize(size);
		} else if (Build.VERSION.SDK_INT >= 14) {
			try {
				size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return size;
	} // end method getRealScreenSize

//	public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER // Tracker used by all ecommerce transactions from a company
	}

	/**
	 * Method for getting tracker by tracker name
//	 * @param trackerId - tracker name
	 * @return - tracker
     */
//	public synchronized Tracker getTracker(TrackerName trackerId) {
//		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CMApplication.getTracker : trackerId : " + trackerId);
//
//		if(!mTrackers.containsKey(trackerId)) {
//			GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this);
//
//			if(trackerId == TrackerName.APP_TRACKER) {
//				Tracker tracker = googleAnalytics.newTracker(PROPERTY_ID);
//				mTrackers.put(trackerId, tracker);
//			} else if(trackerId == TrackerName.GLOBAL_TRACKER) {
//				Tracker tracker = googleAnalytics.newTracker(R.xml.global_tracker);
//				mTrackers.put(trackerId, tracker);
//			}
//		}
//		return mTrackers.get(trackerId);
//
//	} // end method getTracker


	/**
	 * Method for saving client info
	 * @param clientInfo - Client info
     */
	public static void saveClientInfo(ClientInfo clientInfo) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CMApplication.saveProfileInfo : clientInfo : " + clientInfo);
		if (clientInfo != null) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CMApplication.saveProfileInfo : " +
						": name : " + clientInfo.getName() +
						": email : " + clientInfo.getEmail() +
						": bonus : " + clientInfo.getBonus() +
						": canUseBonus : " + clientInfo.getCanUseBonus() +
						": minBonusSum : " + clientInfo.getMinBonusSum() +
						": referral : " + clientInfo.getReferral() +
						": corporation.size : " + clientInfo.getCorporation().size() +
						": photoLink : " + clientInfo.getPhotoLink() +
						": smsNotification : " + clientInfo.getSmsNotification() +
						": sale : " + clientInfo.getSale() +
						": HasCards : " + clientInfo.getHasCards()
				);

			// Saving name
			if (clientInfo.getName() != null && !clientInfo.getName().isEmpty()) {
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_NAME, clientInfo.getName());
			}
			// Saving email
			if (clientInfo.getEmail() != null && !clientInfo.getEmail().isEmpty()) {
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_EMAIL, clientInfo.getEmail());
			}
			// Saving bonus
			if (clientInfo.getBonus() != null && !clientInfo.getBonus().isEmpty()) {
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BONUS, clientInfo.getBonus());
			}
			// Saving can use bonus
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_CAN_USE_BONUS, clientInfo.getCanUseBonus());
			// Saving min bonus sum
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_CAN_USE_BONUS, clientInfo.getCanUseBonus());
			// Saving referral
			if (clientInfo.getReferral() != null && !clientInfo.getReferral().isEmpty()) {
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_REFERAL, clientInfo.getReferral());
			}
			// Saving photo link
			if (clientInfo.getPhotoLink() != null && !clientInfo.getPhotoLink().isEmpty()) {
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_PHOTO_LINK, clientInfo.getPhotoLink());
			}
			// Saving SMS notification
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION, clientInfo.getSmsNotification());
			// Saving can use no cash
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_CAN_USE_NO_CASH, clientInfo.getCan_use_nocash());
			// Saving Sale
			if (clientInfo.getSale() != null && !clientInfo.getSale().isEmpty()) {
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_SALE, clientInfo.getSale());
			}
			// Saving has cards
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_HAS_CARDS, clientInfo.getHasCards());
		}

	} // end method saveClientInfo

	/**
	 * Method for getting listView height
	 * @param listView - List view
	 * @return - List View height
     */
	public static int getHeightOfListView(ListView listView) {

		ListAdapter mAdapter = listView.getAdapter();

		int totalHeight = 0;

		for (int i = 0; i < mAdapter.getCount(); i++) {
			View mView = mAdapter.getView(i, null, listView);

			mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

			totalHeight += mView.getMeasuredHeight();
		}

		return totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));

	} // end method getHeightOfListView

	/**
	 * Method for reading text files from raw folder
	 *
	 * @param ctx   - current context
	 * @param resId - resource id
	 * @return      - text
	 */
	public static String readRawTextFile(Context ctx, int resId)
	{
		InputStream inputStream = ctx.getResources().openRawResource(resId);

		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader buffReader = new BufferedReader(inputReader);
		String line;
		StringBuilder text = new StringBuilder();

		try {
			while (( line = buffReader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {
			return null;
		}
		return text.toString();

	} // end method readRawTextFile

}
