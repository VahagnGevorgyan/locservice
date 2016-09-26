package com.locservice.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.Airport;
import com.locservice.api.entities.CardInfo;
import com.locservice.api.entities.CardsData;
import com.locservice.api.entities.CheckfilesData;
import com.locservice.api.entities.City;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.Driver;
import com.locservice.api.entities.DriverCoordinates;
import com.locservice.api.entities.DriverData;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.entities.DriverNearest;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.GetOrderInfoData;
import com.locservice.api.entities.GetOrdersData;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.LandmarkData;
import com.locservice.api.entities.LandmarkInfo;
import com.locservice.api.entities.NewOrderData;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderCancelData;
import com.locservice.api.entities.OrderIdItem;
import com.locservice.api.entities.OrderPriceData;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.PolygonData;
import com.locservice.api.entities.PolygonPoint;
import com.locservice.api.entities.PrepareCancelOrderData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.entities.Tariff;
import com.locservice.api.entities.TariffData;
import com.locservice.api.entities.TariffService;
import com.locservice.api.entities.Terminal;
import com.locservice.api.entities.google.GoogleDirectionsData;
import com.locservice.api.entities.google.GoogleDistance;
import com.locservice.api.entities.google.GoogleDistanceMatrixData;
import com.locservice.api.entities.google.GoogleDuration;
import com.locservice.api.entities.google.GoogleElement;
import com.locservice.api.entities.google.GoogleGeocode;
import com.locservice.api.entities.google.GoogleGeometry;
import com.locservice.api.entities.google.GoogleLeg;
import com.locservice.api.entities.google.GooglePolyline;
import com.locservice.api.entities.google.GoogleResult;
import com.locservice.api.entities.google.GoogleRoute;
import com.locservice.api.entities.google.GoogleRow;
import com.locservice.api.entities.google.GoogleStep;
import com.locservice.api.manager.ClientManager;
import com.locservice.api.manager.DriverManager;
import com.locservice.api.manager.FavoriteAddressManager;
import com.locservice.api.manager.GoogleManager;
import com.locservice.api.manager.LandmarkManager;
import com.locservice.api.manager.OrderManager;
import com.locservice.api.manager.PlaceManager;
import com.locservice.api.manager.TariffManager;
import com.locservice.api.request.GetDriversRequest;
import com.locservice.api.request.GetPriceRequest;
import com.locservice.application.LocServicePreferences;
import com.locservice.db.AirportDBManager;
import com.locservice.db.CityDBManager;
import com.locservice.db.CountryDBManager;
import com.locservice.db.LanguageDBManager;
import com.locservice.db.RailstationDBManager;
import com.locservice.db.TariffDBManager;
import com.locservice.db.TerminalDBManager;
import com.locservice.db.TranslationDBManager;
import com.locservice.gcm.GCMUtils;
import com.locservice.messages.WebViewDialogMessage;
import com.locservice.protocol.ICallBack;
import com.locservice.protocol.IMainActivity;
import com.locservice.service.GetOrderStatusService;
import com.locservice.service.OrderStatusReceiver;
import com.locservice.service.OrderStatusResultReceiver;
import com.locservice.ui.controls.SlidingUpPanelLayout;
import com.locservice.ui.fragments.MapGoogleFragment;
import com.locservice.ui.fragments.MapViewFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.helpers.MapHelper;
import com.locservice.ui.helpers.MenuHelper;
import com.locservice.ui.helpers.OrderState;
import com.locservice.ui.helpers.OrderStateHelper;
import com.locservice.ui.helpers.PolygonHelper;
import com.locservice.ui.helpers.PolygonItem;
import com.locservice.ui.helpers.PolygonType;
import com.locservice.ui.listeners.ActivityListener;
import com.locservice.ui.listeners.MenuDrawerListener;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.ui.utils.ScrollType;
import com.locservice.utils.DistanceHelper;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.PermissionUtils;
import com.locservice.utils.StringHelper;
import com.locservice.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.locservice.ui.helpers.OrderState.ASAP_A_BUSY;
import static com.locservice.ui.helpers.OrderState.ASAP_A_THIS;
import static com.locservice.ui.helpers.OrderState.CC;
import static com.locservice.ui.helpers.OrderState.CP;
import static com.locservice.ui.helpers.OrderState.LATER_A;
import static com.locservice.ui.helpers.OrderState.NC;
import static com.locservice.ui.helpers.OrderState.OW;


public class MainActivity extends LocationBaseActivity implements IMainActivity,
        MenuDrawerListener,
        ICallBack,
        OrderStatusResultReceiver.OnOrderStatusResultListener,
		ActivityListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected final OrderStatusResultReceiver orderStatusResultReceiver = new OrderStatusResultReceiver();
	private static final int DRIVER_COORD_INTERVAL = 1500;

	private boolean doubleBackToExitPressedOnce = false;

	private DrawerLayout mDrawerLayout;
	private MapViewFragment current_fragment;
	private FragmentTypes fragment_type = FragmentTypes.MAP;
	private FragmentHelper fragmentHelper;
	private CMApplication mCMApplication;
	private DriverManager driverManager;
	private boolean isNotification = false;
	private LatLngBounds polygonBounds;
	private List<LatLng> polygonPoints;
	private float outPolygonDistanceOfRoute;
	private float closestPointOfPolygonDistance;

	public DriverInfo getCurrent_driver_info() {
		return current_driver_info;
	}

	public void setCurrent_driver_info(DriverInfo current_driver_info) {
		this.current_driver_info = current_driver_info;
	}

	private DriverInfo current_driver_info;

	public List<PolygonItem> polygonItems = new ArrayList<>();

	private Handler updateDriverHandler = new Handler();
	private final Runnable updateDriverRunnable = new Runnable() {
		@Override
		public void run() {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.updateDriverRunnable.run ");
			refreshDrivers();
			MainActivity.this.updateDriverHandler.postDelayed(
					updateDriverRunnable, 30000);
		}
	};

	/**
	 * Method for getting update driver Handler
	 *
	 * @return - Handler for updating driver
     */
	public Handler getUpdateDriverHandler() {
		return updateDriverHandler;

	} // end method getUpdateDriverHandler

	/**
	 * Method for getting Update Driver Runnable
	 *
	 * @return - Runnable for updating driver
     */
	public Runnable getUpdateDriverRunnable() {
		return updateDriverRunnable;

	} // end method getUpdateDriverRunnable

	private OrderManager orderManager;
	private Bitmap avatarBitmap;
	private ClientManager clientManager;
	private GoogleManager googleManager;
	private Order currentOrder;
	private int[] driver_seconds;
	private int drivers_counter = 0;
	private CityDBManager cityDBManager;
	private AirportDBManager airportDBManager;
	private TerminalDBManager terminalDBManager;
	private RailstationDBManager railstationDBManager;
	private PlaceManager placeManager;
	private TariffManager tariffManager;
	private LandmarkManager landmarkManager;

	private TariffDBManager tariffDBManager;

	private OrderState currentOrderState;
	private DriverCoordinates driverCurrentCoordinates;

	private boolean isDrawDriverRoadToA = true;
	private boolean isCalledCollapsePane;
	private FavoriteAddressManager favoriteAddressManager;
	private ArrayList<String> points;

	private boolean fromMenu = false;
	private int orderPrice = 0;
	private int orderDuration = -1;
	private Bundle searchDataBundle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mCMApplication = (CMApplication)this.getApplicationContext();

		setContentView(com.locservice.R.layout.activity_main);

		String auth_token = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "");
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : auth_token : " + auth_token);

		placeManager = new PlaceManager(this);
		CountryDBManager countryDBManager = new CountryDBManager(this);
		countryDBManager.getAllCountries();
		cityDBManager = new CityDBManager(this);
		tariffManager = new TariffManager(this);
		tariffDBManager = new TariffDBManager(this);
		airportDBManager = new AirportDBManager(this);
		terminalDBManager = new TerminalDBManager(this);
		railstationDBManager = new RailstationDBManager(this);
		orderManager = new OrderManager(this);
		landmarkManager = new LandmarkManager(this);
		clientManager = new ClientManager(this);
		googleManager= new GoogleManager(this);
		favoriteAddressManager = new FavoriteAddressManager(this);

		// check network status
		if (Utils.checkNetworkStatus(this)) {

			if (getIntent().getBooleanExtra(CMAppGlobals.EXTRA_FROM_MENU, false)) {
				fromMenu = true;
			}
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : fromMenu : " + fromMenu);

			// Set Menu Listeners
			MenuHelper.getInstance().SetMenuListeners(this);
			// Set Map Fragment
			setCurrent_fragment(FragmentHelper.getInstance().openFragment(this, FragmentTypes.MAP, null));

			// Remove current driver info
			setCurrent_driver_info(null);

			// Remove Tracking Order Id
			CMApplication.setTrackingOrderId("");

			if(!LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "").equals("")) {

				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : EXTRA_ORDER_ID : " + getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID));
				if (getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID) != null) {
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : GetOrderStatusList");
					// Set Last Order Data
					orderManager.GetOrderStatusList(getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID));
				} else {
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : GetOrders");
					// Get Active Orders
					String phoneStr = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), "");
					orderManager.GetOrders(phoneStr, 1);
				}
				// Get client info
				clientManager.GetClientInfo();
			}

			// load all cities tariffs
			loadAllCitiesTariffs();

			// load all airport polygons
			loadAllAirportPolygons();

			// Request for getting favorite addresses
			favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);

			// get polygon intersection with path
			DistanceHelper.getPolygonAndPathIntersection();

			TranslationDBManager translationDBManager = new TranslationDBManager(this);
			List<TranslationDBManager.Translation> allTranslations = translationDBManager.getAllTranslations();
			for (TranslationDBManager.Translation item :
					allTranslations) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : TRANSLATION : id : " + item.getId()
									+ " : text : " + item.getText() + " : language_id : " + item.getLangId());

			}

			List<Tariff> allTariffs = tariffDBManager.getAllTariffs(4);
			for (Tariff item :
					allTariffs) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : TARIFF : id : " + item.getID()
						+ " : name : " + item.getName() + " : city_id : " + item.getCityId()
						+ " : short_name : " + item.getShortname() );
			}

			// driver manager
			driverManager = new DriverManager(MainActivity.this);
			waitHandler = new WaitHandler(driverManager);

		}

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onCreate : EXTRA_WEB_VIEW_MESSAGE : " + getIntent().getParcelableExtra(GCMUtils.EXTRA_WEB_VIEW_MESSAGE));
		if (getIntent().getParcelableExtra(GCMUtils.EXTRA_WEB_VIEW_MESSAGE) != null) {
			WebViewDialogMessage message = getIntent().getParcelableExtra(GCMUtils.EXTRA_WEB_VIEW_MESSAGE);
			onWebViewDialogEvent(message);
		}

		// Showing dialog if text's size is large
		showLargeTextDialog();

	}

//	/**
//	 * Called when the bus posts an event for successful result.
//	 *
//	 * Call onEvent: Called in the same thread (default)
//	 * Call onEventMainThread: Called in Android UI's main thread
//	 * Call onEventBackgroundThread: Called in the background thread
//	 * Call onEventAsync: Called in a separate thread
//	 *
//	 * @param event - api result event
//	 */
//	public void onEvent(ApiResultEvent event) {
//
//		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onEvent : event : " + event);
//
////		LoginResponse response = (LoginResponse) event.getResponse();
////		textView.setText("User Id: "+response.getUserLoginResult().getUserDetail()[0].getUserID());
//
//	}
//
//	/**
//	 * Called when the bus posts an event for service failure.
//	 *
//	 * @param event - api error event
//	 */
//	public void onEvent(ApiErrorEvent event) {
//
//		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onEvent : event : " + event);
//
//	}


	@Override
	protected void onDestroy() {
		clearCurrentActivity(this);
		super.onDestroy();
		// unregister BroadcastReceiver
		unregisterReceiver(this.orderStatusResultReceiver);
		this.waitHandler.removeMessages(RequestTypes.REQUEST_GET_DRIVER_COORDINATES);
		this.waitHandler.removeMessages(RequestTypes.REQUEST_GET_DRIVER_INFO);
		this.waitHandler.removeMessages(RequestTypes.REQUEST_GET_DRIVERS);

	}


	@Override
	public void onBackPressed() {

		if (current_fragment != null && current_fragment.ismConfirmVisible()) {
			if (current_fragment.isHasAction()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
				builder.setMessage(R.string.str_are_you_sure_unset);
				builder.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(!current_fragment.getOrderStateHelper().getTextViewConfirmOrder().isEnabled()) {
							current_fragment.getOrderStateHelper().getTextViewConfirmOrder().setEnabled(true);
						}
						current_fragment.getOrderStateHelper().cancelOrder(false);
						// unset service options
						current_fragment.setServiceOptions(true, null);
						// get current address for address option
						((MapGoogleFragment)current_fragment.getMapFragment()).getAddressByCameraPosition();
					}
				});
				builder.setNegativeButton(getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builder.create().show();
			} else {
				if(!current_fragment.getOrderStateHelper().getTextViewConfirmOrder().isEnabled()) {
					current_fragment.getOrderStateHelper().getTextViewConfirmOrder().setEnabled(true);
				}
				current_fragment.getOrderStateHelper().cancelOrder(false);
				// unset service options
				current_fragment.setServiceOptions(true, null);
				// get current address for address option
				((MapGoogleFragment)current_fragment.getMapFragment()).getAddressByCameraPosition();
			}
		}

		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);
	}

	@Override
	protected void onStart() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onStart ");
		super.onStart();
		// connect google API client

		PermissionUtils.ensurePermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION, PermissionUtils.ACCESS_FINE_LOCATION);
		PermissionUtils.ensurePermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION, PermissionUtils.ACCESS_COARSE_LOCATION);
		connect();
	}


	@Override
	public void onStop() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onStop ");
		super.onStop();
	}

	@Override
	public void onResume() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onResume ");
		super.onResume();
		// set current activity
		setCurrentActivity(this);
		if (CMApplication.getTrackingOrderId().isEmpty()) {
			// driver handler
			updateDriverHandler.postDelayed(updateDriverRunnable, 10000);
		}
		// set order status receiver
		this.orderStatusResultReceiver.setListener(this);
		// filter for BroadcastReceiver
		IntentFilter intentFilter = new IntentFilter(GCMUtils.UPDATE_ORDER_STATUS);
		// register BroadcastReceiver
		registerReceiver(this.orderStatusResultReceiver, intentFilter);

	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onActivityResult  "
				+ " : requestCode : " + requestCode
				+ " : resultCode : " + resultCode
				+ " : data : " + data);

		if(resultCode == RESULT_OK) {
			if(data != null) {
				if (requestCode == CMAppGlobals.REQUEST_CAPTURE_IMAGE) {
					try {
						Bundle extras = data.getExtras();
						Bitmap captureBitmap = (Bitmap) extras.get("data");
						ClientManager clientManager = new ClientManager(this);
						clientManager.UpdateClientInfo(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "Name"),
								LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), "Email"),
								LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
								CMApplication.encodeBitmapToBase64(captureBitmap), 0);
						avatarBitmap = captureBitmap;
					} catch (Exception e) {
						if(CMAppGlobals.DEBUG)Logger.e(TAG, ":: MainActivity.onActivityResult : Exception : " + e.getMessage());
					}
				}
				if(requestCode == CMAppGlobals.REQUEST_LOAD_FROM_GALLERY) {
					try {
						final Uri imageUri = data.getData();
						Bitmap imageBitmap = CMApplication.decodeSampledBitmap(MainActivity.this, imageUri, 100, 100);

						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onActivityResult : imageBitmap : " + imageBitmap);
						ClientManager clientManager = new ClientManager(this);
						clientManager.UpdateClientInfo(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "Name"),
								LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), "Email"),
								LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
								CMApplication.encodeBitmapToBase64(imageBitmap), 0);
						avatarBitmap = imageBitmap;
					} catch (Exception e) {
						if(CMAppGlobals.DEBUG)Logger.e(TAG, ":: MainActivity.onActivityResult : Exception : " + e.getMessage());
					}
				}
				if(requestCode == CMAppGlobals.REQUEST_SEARCH_DATA) {
					if(getCurrent_fragment() instanceof MapViewFragment) {

						searchDataBundle = data.getExtras();
						boolean isChangedFavoriteAddress = data.getBooleanExtra(CMAppGlobals.EXTRA_CHANGED_FAVORITE_ADDRESS, false);

						if (isChangedFavoriteAddress) {
							favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE_FROM_SEARCH);
						} else {
							GooglePlace address = data.getParcelableExtra(CMAppGlobals.EXTRA_SEARCH_ADDRESS);
							if (data.getIntExtra(CMAppGlobals.SCROLL_TYPE, -1) != -1 && address != null) {
								ScrollType scrollType = ScrollType.values()[data.getIntExtra(CMAppGlobals.SCROLL_TYPE, -1)];
								switch (scrollType) {
									case SO_FROM:
										((MapViewFragment) getCurrent_fragment()).refreshAddressByType(MainActivity.this, ScrollType.SO_FROM, address, true);
										break;
									case SO_WHERE:
										((MapViewFragment) getCurrent_fragment()).refreshAddressByType(MainActivity.this, ScrollType.SO_WHERE, address, false);
										break;
									default:
										break;
								}
								((MapViewFragment)getCurrent_fragment()).actionPanel(SlidingUpPanelLayout.SlideState.COLLAPSED);
							}
						}
					}
				}
				if(requestCode == CMAppGlobals.REQUEST_COMMENT) {
					if(getCurrent_fragment() instanceof MapViewFragment) {
						String comment = data.getStringExtra(CMAppGlobals.EXTRA_ORDER_COMMENT);
						String entrance = data.getStringExtra(CMAppGlobals.EXTRA_ORDER_ENTRANCE);

						if ((comment == null || comment.isEmpty()) && (entrance == null || entrance.isEmpty()))
							comment = getResources().getString(R.string.str_comment_title);

						((MapViewFragment)getCurrent_fragment()).setOrderComment(comment, entrance);
					}
				}
			}
		}
		if (requestCode == CMAppGlobals.REQUEST_ORDER_LIST) {
			if (resultCode == Activity.RESULT_OK) {
				// request if not register
				requestIfNotRegister();
			}

			if (data != null) {

				OrderStatusData orderStatusData = data.getParcelableExtra(CMAppGlobals.EXTRA_ACTIVE_ORDER_STATUS_DATA);
				if (orderStatusData != null) {
					MenuHelper.getInstance().activeOrdersOnItemClick(this, null, 0, orderStatusData);
				}
			}
		}
		if (requestCode == CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO) {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onActivityResult : REQUEST_CHANGE_PROFILE_INFO : ");
			if (resultCode == Activity.RESULT_OK) {
				// request if not register
				requestIfNotRegister();
			}
			// changing profile info
			changeProfileInfo();
			// get favorite addresses for current city
			favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);
			if (CMApplication.getTrackingOrderId().isEmpty()) {
				// get credit cards for updating payment list
				current_fragment.getCreditCards();
			}
		}
		if(requestCode == CMAppGlobals.REQUEST_REGISTER) {
			if (resultCode == Activity.RESULT_OK) {
				// request if not register
				requestIfNotRegister();
			}
			// get favorite addresses for current city
			favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);
		}
		if (requestCode == CMAppGlobals.REQUEST_ADD_FAVORITE_ADDRESS && resultCode == Activity.RESULT_OK) {
			// get favorite addresses for current city
			favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);
		}
	}

	@Override
	protected void onPause() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onPause ");
		// clear receiver
		this.orderStatusResultReceiver.clearListener();
		clearCurrentActivity(this);
		super.onPause();
		this.waitHandler.removeMessages(RequestTypes.REQUEST_GET_DRIVER_COORDINATES);
		this.waitHandler.removeMessages(RequestTypes.REQUEST_GET_DRIVER_INFO);
		updateDriverHandler.removeCallbacks(updateDriverRunnable);
	}

	@Override
	public void onConnected(Bundle bundle) {
		super.onConnected(bundle);

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onConnected ");

		Location location = getLocation();
		if (location != null) {
			double mLat;
			double mLng;

			mLat = location.getLatitude();
			mLng = location.getLongitude();
			LatLng currentLatLng = new LatLng(mLat, mLng);

			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onConnected : mLat ---- " + mLat);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onConnected : mLng ---- " + mLng);

			// set current location
			if(fragment_type == FragmentTypes.MAP) {
				MapViewFragment mapViewFragment = (MapViewFragment) getCurrent_fragment();
				if (mapViewFragment != null && mapViewFragment.isVisible()) {
					mapViewFragment.setCurrentLocation(mLat, mLng);
				}
			}

			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onConnected : isCurrentTariffSet : " + isCurrentTariffSet);
			if(!isCurrentTariffSet) {
				// get current CityId & Load tariffs
				getCurrentCityTariffs(currentLatLng);
			}
		} else {
			// set current location Moscow
			if(fragment_type == FragmentTypes.MAP) {
				MapViewFragment mapViewFragment = (MapViewFragment) getCurrent_fragment();
				if (mapViewFragment != null && mapViewFragment.isVisible()) {
					int languageID = new LanguageDBManager(this).getLanguageIdByLocale("ru");
					// get moscow address
					CityDBManager cityDBManager = new CityDBManager(this);
					City cItem = cityDBManager.getCityByID("2", languageID);
					if(cItem != null && cItem.getLatitude() != null && cItem.getLongitude() != null) {
						mapViewFragment.setCurrentLocation(Double.valueOf(cItem.getLatitude()), Double.valueOf(cItem.getLongitude()));
					}
				}
			}
		}
	}

	/**
	 * Method for loading all city airport polygons
	 */
	public void loadAllAirportPolygons() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.loadAllAirportPolygons ");

		// clear current polygons
		polygonItems.clear();

		int languageID = new LanguageDBManager(this).getLanguageIdByLocale("ru");
		String current_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
		AirportDBManager airportDBManager = new AirportDBManager(this);

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.loadAllAirportPolygons : current_city_id : " + current_city_id);

		List<Airport> allAirports;
		if(!current_city_id.equals("")) {
			allAirports = airportDBManager.getAllAirports(languageID, Integer.parseInt(current_city_id));
			if(allAirports.size() > 0) {
				PolygonHelper.loadPolygons(this, allAirports);
			}
		}

	} // end method loadAllAirportPolygons

	/**
	 * Method for loading all cities tariffs
	 */
	public void loadAllCitiesTariffs() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.loadAllCitiesTariffs : cityId : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), ""));

		String last_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
		int languageID = new LanguageDBManager(MainActivity.this).getLanguageIdByLocale("ru");
		List<City> allCities = cityDBManager.getAllCities(languageID);
		if(allCities.size() > 0) {
			for (City item :
					allCities) {
				String city_date = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CITIES_DATE.key() + "ru", "");
				// CHECK if DB have tariffs for CITY
				List<Tariff> tariff_list = tariffDBManager.getAllTariffs(languageID, Integer.parseInt(item.getId()));
				if(last_city_id.equals(item.getId())) {
					// always call for last city id
					placeManager.CheckFilesChanges(Integer.parseInt(item.getId()), "", "", city_date, "ru");
				} else if(tariff_list.size() == 0) {
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.loadAllCitiesTariffs : LOAD_TARIFF : city_id : " + item.getId());

					placeManager.CheckFilesChanges(Integer.parseInt(item.getId()), "", "", city_date, "ru");
				}
			}
		}

	} // end method getAllCitiesTariffs

	/**
	 * Method for getting current city tariffs
	 * @param currentLatLng - current lat lng coordinates
	 */
	public void getCurrentCityTariffs(LatLng currentLatLng) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getCurrentCityTariffs ");

		int languageID = new LanguageDBManager(MainActivity.this).getLanguageIdByLocale("ru");
		String current_city_id = DistanceHelper.getCityIdByCoordinates(MainActivity.this, currentLatLng);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getCurrentCityTariffs : current_city_id : " + current_city_id);
		String last_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getCurrentCityTariffs : last_city_id : " + last_city_id);

		// Setting Autocomplete bounds city id
		LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.BOUNDS_CITY_ID, current_city_id);

		if(!current_city_id.equals("") && (!last_city_id.equals(current_city_id) || !isCurrentTariffSet)) {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getCurrentCityTariffs : CALL CHECK_FILES_CHANGES : current_city_id : " + current_city_id);
		    // STORE city_id
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.CURRENT_CITY_ID, current_city_id);
			// CheckFilesChanges for city tariffs/landmarks
			String city_date = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CITIES_DATE.key() + "ru", "");
			// CHECK if DB have tariffs for CITY
			List<Tariff> tariff_list = tariffDBManager.getAllTariffs(languageID, Integer.parseInt(current_city_id));
			if(tariff_list != null && tariff_list.size() > 0) {
				// SET TARIFF DATA
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getCurrentCityTariffs : TARIFFS : EXISTS : current_city_id : " + current_city_id);

				isCurrentTariffSet = true;
				setTariffsData(tariff_list, false);

				// load all polygons
				loadAllAirportPolygons();
			} else {
				// GET TARIFF DATA
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getCurrentCityTariffs : TARIFFS : NOT EXISTS : current_city_id : " + current_city_id);

				placeManager.CheckFilesChanges(Integer.parseInt(current_city_id), "", "", city_date, "ru");
			}

			if (CMApplication.getTrackingOrderId().isEmpty()) {
				// get favorite addresses for current city
				favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);
			}
		}

	} // end method getCurrentCityTariffs

	@Override
	public void OpenMenuDrawer(FragmentActivity context) {
		MenuHelper.getInstance().openDrawer(context, Gravity.LEFT);
	}

	@Override
	public void CloseMenuDrawer(FragmentActivity context) {
		MenuHelper.getInstance().closeDrawer(context, Gravity.LEFT);
	}

	@Override
	public FragmentHelper getFragmentHelper() {
		return fragmentHelper;
	}

	@Override
	public void setFragmentHelper(FragmentHelper fragmentHelper) {
		this.fragmentHelper = fragmentHelper;
	}

	@Override
	public DrawerLayout getmDrawerLayout() {
		return mDrawerLayout;
	}

	@Override
	public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
		this.mDrawerLayout = mDrawerLayout;
	}

	@Override
	public void onFailure(Throwable error, int requestType) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.OnFailure : requestType : " + requestType + " : error : " + error);
		switch (requestType) {
			case RequestTypes.REQUEST_PREPARE_CANCEL_ORDER:
				current_fragment.getOrderStateHelper().getTextViewCancel().setEnabled(true);
				break;
		}
	}

	@Override
	public void onSuccess(Object response, int requestType) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.OnSuccess : requestType : " + requestType + " : object : " + response);
		if (response != null) {
			switch (requestType) {
				case RequestTypes.REQUEST_NEW_ORDER:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_NEW_ORDER")) {
						if(response instanceof NewOrderData) {
							NewOrderData order = (NewOrderData) response;
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_NEW_ORDER : orderExist : " + order.getOrderExist());
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_NEW_ORDER : order_id : " + order.getIdOrder());
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_NEW_ORDER : status : " + order.getStatus());
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_NEW_ORDER : title : " + order.getTitle());
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_NEW_ORDER : sub_title : " + order.getSub_title());

							if (OrderStateHelper.isOrderValid(order.getIdOrder())) {
								// ANIMATE TOP LAYOUT
								((MapViewFragment) getCurrent_fragment()).animateTopLayout();
								// STORE ORDER & START SERVICE
								CMApplication.setTrackingOrderId(order.getIdOrder());
								OrderIdItem orderIdItem = new OrderIdItem();
								orderIdItem.setIdOrder(order.getIdOrder());
								orderIdItem.setStatus(order.getStatus());
								CMApplication.setActiveOrder(orderIdItem, true);
								// update menu active orders
								OrderStatusData orderStatusData = new OrderStatusData();
								orderStatusData.setIdOrder(order.getIdOrder());
								orderStatusData.setStatus(order.getStatus());
								orderStatusData.setTitle(order.getTitle());
								orderStatusData.setSubTitle(order.getSub_title());
								MenuHelper.addActiveOrder(orderStatusData);
								MenuHelper.getInstance().addActiveOrders(MainActivity.this, MenuHelper.getActiveOrdersList());

								// set menu icon
								((MapViewFragment) getCurrent_fragment()).setMenuIcon(true);
								// set current order state
								current_order_state = OrderState.getStateByStatus(order.getStatus());
								currentOrderState = OrderState.getStateByStatus(order.getStatus());

								((MapGoogleFragment)current_fragment.getMapFragment()).setDirection_increment(0);

								// Set NewOrderData information orderId/status/currentPrice/bonus/trip_time
								setOrderState(order.getIdOrder(), OrderState.getStateByStatus(order.getStatus()), "", "", "");

								// set order UI
								current_fragment.getOrderStateHelper().updateUIByOrderStatusData(current_fragment.getRootView(), orderStatusData);

								// start service
								if (!isMyServiceRunning(GetOrderStatusService.class)) {
									if (CMAppGlobals.DEBUG)
										Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_NEW_ORDER : start service ");
									// Alarm Status Receiver
									OrderStatusReceiver.scheduleAlarms(getApplicationContext());
								}
							} else {
								if (order.getOrderExist() > 0) {
									Toast.makeText(MainActivity.this, getString(R.string.alert_order_exists), Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(MainActivity.this, getString(R.string.alert_order_not_created), Toast.LENGTH_SHORT).show();
								}
							}
						}
                    } else {
						current_fragment.setIsOrderCreated(false);
						current_fragment.getOrderStateHelper().getTextViewConfirmOrder().setEnabled(true);
					}
					break;
				case RequestTypes.REQUEST_GET_DRIVERS:
					if (CMAppGlobals.DEBUG)
						Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_GET_DRIVERS : AUTH_TOKEN : "
								+ LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_DRIVERS")) {
						if(response instanceof DriverData) {
							DriverData driverData = (DriverData) response;

							List<Driver> driversList = driverData.getDrivers();

							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVERS : driversList : " + driversList);

							// draw drivers
							((MapViewFragment) getCurrent_fragment()).drawDriversOnMap(driversList);
							if (currentOrder != null) {
								drawMapByOrderState(currentOrder, null, null);
							}

							// get driver time from
							DriverNearest nearest = driverData.getNearest();
							if(nearest != null) {

								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVERS : nearest.duration : " + nearest.getDuration());
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVERS : nearest.distance : " + nearest.getDistance());

								// drivers time
								if (!driversList.isEmpty()) {
									// Show Time on UI
									((MapViewFragment)getCurrent_fragment()).getMapFragment().setDriverTime(nearest.getDuration());
								} else {
									// Show Time on UI > 15 min
									((MapViewFragment)getCurrent_fragment()).getMapFragment().setDriverTime(1000);
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_DRIVER_INFO:

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_DRIVER_INFO")) {
						if(response instanceof DriverInfo) {
							current_driver_info = (DriverInfo) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_INFO : current_driver_info : " + current_driver_info);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_INFO : name : " + current_driver_info.getName());
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_INFO : id : " + current_driver_info.getId());

							// SHOW DRIVER BAR
							if (current_fragment != null) {
								// get fragment
								current_fragment.setDriverUI(current_driver_info, true);
								// set driver phone number
								current_fragment.getOrderStateHelper().setDriverPhoneNumber(current_driver_info.getPhone());
							}
							// remove update drivers
							updateDriverHandler.removeCallbacks(updateDriverRunnable);
							// Request Driver Coordinates
							MainActivity.this.waitHandler.sendEmptyMessageDelayed(RequestTypes.REQUEST_GET_DRIVER_COORDINATES, DRIVER_COORD_INTERVAL);
						}
					}
					break;
				case RequestTypes.REQUEST_GET_DRIVER_COORDINATES:
					if (CMAppGlobals.DEBUG)
						Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : response : " + response);

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_DRIVER_COORDINATES")) {
						if(response instanceof DriverCoordinates) {
							// Draw Driver on Map
							driverCurrentCoordinates = (DriverCoordinates) response;
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : driver id : " + driverCurrentCoordinates.getDriverID() +
										" : lat : " + driverCurrentCoordinates.getLatitude() + " : lng : " + driverCurrentCoordinates.getLongitude());

							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : driverCurrentCoordinates.getDirection : " + driverCurrentCoordinates.getDirection());

							if (driverCurrentCoordinates.getDriverID() != null) {
								// DRAW Driver on Map
								Driver driver = new Driver();
								driver.setId(driverCurrentCoordinates.getDriverID());
								driver.setLn(String.valueOf(driverCurrentCoordinates.getLongitude()));
								driver.setLt(String.valueOf(driverCurrentCoordinates.getLatitude()));

								// Draw road directions
								if (currentOrder != null) {
									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : currentOrderState : " + currentOrderState);

									if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : current_driver_info : " + current_driver_info);
									if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : current_driver_info.getId() : " + current_driver_info.getId());
									if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : driverCurrentCoordinates.getDriverID() : " + driverCurrentCoordinates.getDriverID());

									// Check if current driver information exists
									if(current_driver_info != null && current_driver_info.getId() != null &&
											!(current_driver_info.getId()).equals(driverCurrentCoordinates.getDriverID())) {
										// Get appointed driver info by orderID
										getDriverInfo(currentOrder.getIdOrder());
									}

									if(OrderState.getStateByStatus(currentOrder.getStatus()) == OrderState.OW) {
										if(currentOrder.getDelLongitude() != null
												&& !currentOrder.getDelLongitude().isEmpty()
												&& currentOrder.getDelLatitude() != null
												&& !currentOrder.getDelLatitude().isEmpty()) {
											googleManager.GetDirections(driverCurrentCoordinates.getLatitude() + "," + driverCurrentCoordinates.getLongitude(),
													currentOrder.getDelLatitude() + "," + currentOrder.getDelLongitude(),
													RequestTypes.REQUEST_GOOGLE_DIRECTIONS);
										} else {
											if(isDrawing(currentOrder.getIdOrder(), OrderState.getStateByStatus(currentOrder.getStatus()))) {
												drawMapByOrderState(currentOrder, driverCurrentCoordinates, null);
											}
										}
									} else if (OrderState.getStateByStatus(currentOrder.getStatus()) == OrderState.ASAP_A_THIS) {
										if(currentOrder.getLongitude() != null
												&& !currentOrder.getLongitude().isEmpty()
												&& currentOrder.getLatitude() != null
												&& !currentOrder.getLatitude().isEmpty()) {
											googleManager.GetDirections(driverCurrentCoordinates.getLatitude() + "," + driverCurrentCoordinates.getLongitude(),
													currentOrder.getLatitude() + "," + currentOrder.getLongitude(),
													RequestTypes.REQUEST_GOOGLE_DIRECTIONS);
										} else {
											if(isDrawing(currentOrder.getIdOrder(), OrderState.getStateByStatus(currentOrder.getStatus()))) {
												drawMapByOrderState(currentOrder, driverCurrentCoordinates, null);
											}
										}
									} else {
										if(isDrawing(currentOrder.getIdOrder(), OrderState.getStateByStatus(currentOrder.getStatus()))) {
											drawMapByOrderState(currentOrder, driverCurrentCoordinates, null);
										}
									}

									if (currentOrderState == OrderState.ASAP_A_THIS
											|| currentOrderState == OrderState.ASAP_A_BUSY
											) {
										if (CMAppGlobals.DEBUG)
											Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVER_COORDINATES : currentOrderState : " + currentOrderState);
										// calculate
										getDriverDistanceMatrix(driver);
									} else if (currentOrderState == OrderState.LATER_A) {
										String subTitle = getResources().getString(R.string.str_order_registered_content);
										subTitle = subTitle.replace("{0}", getResources().getString(R.string.str_minute_registered));
										current_fragment.getOrderStateHelper().setSubTitle(subTitle);
									}
								} else {
									orderManager.GetOrdersInfo("", CMApplication.getTrackingOrderId(), RequestTypes.REQUEST_GET_ORDERS_INFO);
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_CANCEL_ORDER:
					if (CMAppGlobals.DEBUG)
						Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_CANCEL_ORDER : response : " + response);

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_CANCEL_ORDER")) {
						if(response instanceof OrderCancelData) {

							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_CANCEL_ORDER : result : " + ((OrderCancelData) response).getResult());
							// stop driver coordinates handler
							MainActivity.this.waitHandler.removeMessages(RequestTypes.REQUEST_GET_DRIVER_COORDINATES);

							OrderStateHelper.canceled_order_Id = CMApplication.getTrackingOrderId();

							// remove from menu active orders
							MenuHelper.removeActiveOrder(CMApplication.getTrackingOrderId());
							MenuHelper.getInstance().addActiveOrders(MainActivity.this, MenuHelper.getActiveOrdersList());
							// remove order from active orders
							CMApplication.removeActiveOrder(CMApplication.getTrackingOrderId());
							CMApplication.setTrackingOrderId("");
							if(MenuHelper.getActiveOrdersList() != null && MenuHelper.getActiveOrdersList().size() > 0)  {
								// set menu active icon
								((MapViewFragment) getCurrent_fragment()).setMenuIcon(true);
							} else {
								// set menu icon
								((MapViewFragment) getCurrent_fragment()).setMenuIcon(false);
							}
							if (current_fragment != null) {
								current_fragment.getOrderStateHelper().cancelOrder(false);
							}

							// START ORDER STATUS SERVICE
							if(CMApplication.getActiveOrders().size() > 0) {
								// start service
								if (!isMyServiceRunning(GetOrderStatusService.class)) {
									if (CMAppGlobals.DEBUG)
										Logger.i(TAG, ":: MainActivity.OnSuccess : RequestTypes.REQUEST_CANCEL_ORDER : start service ");
									// Alarm Status Receiver
									OrderStatusReceiver.scheduleAlarms(getApplicationContext());
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_ORDER_STATUS_LIST_FROM_PUSH:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDER_STATUS_LIST_FROM_PUSH")) {
						//noinspection unchecked
						List<OrderStatusData> orderStatusDataList = (List<OrderStatusData>) response;
						if (CMAppGlobals.DEBUG)
							Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST_FROM_PUSH : orderStatusDataList : " + orderStatusDataList);

						if (!orderStatusDataList.isEmpty()) {
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST_FROM_PUSH : orderId : " + orderStatusDataList.get(0).getIdOrder());
							// set Order data
							setOrderListData(orderStatusDataList);
						}
					}
					break;
				case RequestTypes.REQUEST_GET_ORDER_STATUS_LIST:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDER_STATUS_LIST")) {
						//noinspection unchecked
						List<OrderStatusData> orderStatusDataList = (List<OrderStatusData>) response;
						if (CMAppGlobals.DEBUG)
							Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : orderStatusDataList : " + orderStatusDataList);

						if (!orderStatusDataList.isEmpty()) {
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : orderId : " + orderStatusDataList.get(0).getIdOrder());

							MenuHelper.setActiveOrdersList(orderStatusDataList);

							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : EXTRA_ORDER_ID : " + getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID));

							if (getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID) != null) {
								for (OrderStatusData item : orderStatusDataList) {
									if (item.getIdOrder().equals(getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID))) {
										CMApplication.setTrackingOrderId(item.getIdOrder());
										if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MainActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : 1");
										updateUIByOrderStatusData(item);
									}
								}
							} else {
								CMApplication.setTrackingOrderId(orderStatusDataList.get(0).getIdOrder());
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MainActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : 2");
								updateUIByOrderStatusData(orderStatusDataList.get(0));
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_ORDERS:
					if (CMAppGlobals.DEBUG)
						Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS : response : " + response);
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDERS")) {
						if(response instanceof GetOrdersData) {
							// set active orders
							GetOrdersData ordersData = (GetOrdersData) response;
							if (ordersData.getOrders().size() > 0) {
								if (CMAppGlobals.DEBUG)
									Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS : orders : " + ordersData.getOrders());

								CMApplication.setActiveOrders(ordersData.getOrders());

								// Check From Menu
								if(!fromMenu && !isNotification) {
									if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS : fromMenu : "
											+ false + " : isNotification : " + false);
									String orders = "";
									for (int i = 0; i < ordersData.getOrders().size(); i++) {
										orders += ordersData.getOrders().get(i).getIdOrder();
										if (i < ordersData.getOrders().size() - 1) {
											orders += ",";
										}
									}
									if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS : orders : " + orders);
									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MainActivity.onSuccess : REQUEST_GET_ORDERS : GetOrderStatusList");
									orderManager.GetOrderStatusList(orders);
								}
								// START SERVICE
								if (!isMyServiceRunning(GetOrderStatusService.class)) {
									if (CMAppGlobals.DEBUG)
										Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_GET_ORDERS : start service ");
									// Alarm Status Receiver
									OrderStatusReceiver.scheduleAlarms(getApplicationContext());
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_DRIVERS_FROM_MAP:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_DRIVERS_FROM_MAP")) {
						if(response instanceof DriverData) {
							DriverData driverData = (DriverData) response;
							List<Driver> driversList = driverData.getDrivers();

							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVERS_FROM_MAP : driversList : " + driversList);

							if (CMApplication.getTrackingOrderId().isEmpty()) {
								// draw drivers
								((MapViewFragment) getCurrent_fragment()).drawDriversOnMap(driversList);
							}

							// get driver time from
							DriverNearest nearest = driverData.getNearest();
							if(nearest != null) {

								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVERS_FROM_MAP : nearest.duration : " + nearest.getDuration());
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_DRIVERS_FROM_MAP : nearest.distance : " + nearest.getDistance());

								// drivers time
								if (!driversList.isEmpty()) {
									// Show Time on UI
									((MapViewFragment)getCurrent_fragment()).getMapFragment().setDriverTime(nearest.getDuration());
								} else {
									// Show Time on UI > 15 min
									((MapViewFragment)getCurrent_fragment()).getMapFragment().setDriverTime(1000);
								}
							}
							// Getting google distance matrix from A to B for getting order price
							getGoogleDistanceMatrixFromAToB();
						}
					}
					break;
				case RequestTypes.REQUEST_UPDATE_CLIENT_INFO:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_UPDATE_CLIENT_INFO")) {
						if(response instanceof ResultData) {
							ResultData resultData = (ResultData) response;
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_UPDATE_CLIENT_INFO : resultData : " + resultData);
							if (resultData.getResult().equals("1")) {
								MenuHelper.getInstance().SetProfileImage(avatarBitmap);
								LocServicePreferences.getAppSettings()
										.setSetting(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE, CMApplication.encodeBitmapToBase64(avatarBitmap));
							} else {
								Toast.makeText(MainActivity.this, R.string.alert_update_client_info, Toast.LENGTH_SHORT).show();
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_CLIENT_INFO:
					if(response instanceof ClientInfo) {
						ClientInfo clientInfo = (ClientInfo) response;
						if (CMAppGlobals.DEBUG)
							Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_CLIENT_INFO : " +
									": clientInfo : " + clientInfo +
									": name : " + clientInfo.getName() +
									": email : " + clientInfo.getEmail() +
									": bonus : " + clientInfo.getBonus() +
									": canUseBonus : " + clientInfo.getCanUseBonus() +
									": minBonusSum : " + clientInfo.getMinBonusSum() +
									": referral : " + clientInfo.getReferral() +
									": corporation.size : " + clientInfo.getCorporation().size() +
									": photoLink : " + clientInfo.getPhotoLink() +
									": smsNotification : " + clientInfo.getSmsNotification() +
									": sale : " + clientInfo.getSale()
							);

						// We already have user info
						LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.IS_USER_LOG_IN, true);

						CMApplication.saveClientInfo(clientInfo);

						if (clientInfo.getPhotoLink() != null && !clientInfo.getPhotoLink().isEmpty()) {
							ImageHelper.loadAvatarByLoader(this, clientInfo.getPhotoLink(), (RoundedImageView) findViewById(R.id.imageViewAvatar));
						}
						if (clientInfo.getName() != null) {
							MenuHelper.getInstance().setProfileName(MainActivity.this, clientInfo.getName());
						}
						if (clientInfo.getBonus() != null && !clientInfo.getBonus().isEmpty()) {
							MenuHelper.getInstance().setRewards(this, clientInfo.getBonus());
						}

						if (clientInfo.getSale() != null && !clientInfo.getSale().isEmpty()) {
							if (clientInfo.getSale().equals("0")) {
								MenuHelper.getInstance().showSale(false);
							} else {
								MenuHelper.getInstance().showSale(true);
								String sateText = clientInfo.getSale() + " " + getResources().getString(R.string.str_sale);
								MenuHelper.getInstance().setSaleText(sateText);
							}
						}
						// UPDATE PAYMENT TYPES
						((MapViewFragment)getCurrent_fragment()).addPaymentMethods(clientInfo);
					}

					break;
				case RequestTypes.REQUEST_GOOGLE_DIRECTIONS:
					if(response instanceof GoogleDirectionsData) {
						GoogleDirectionsData googleDirectionsData = (GoogleDirectionsData) response;
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DIRECTIONS : googleDirectionsData : " + googleDirectionsData);
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DIRECTIONS : status : " + googleDirectionsData.getStatus());

						points = new ArrayList<>();
						List<GoogleRoute> routes = googleDirectionsData.getRoutes();
						if (routes != null && !routes.isEmpty()) {
							List<GoogleLeg> legs = routes.get(0).getLegs();
							if (legs != null && !legs.isEmpty()) {
								List<GoogleStep> steps = legs.get(0).getSteps();
								if (steps != null && !steps.isEmpty()) {
									for (GoogleStep item : steps) {
										GooglePolyline point = item.getPolyline();
										points.add(point.getPoints());
									}
									// Draw Route
									drawMapByOrderState(currentOrder, driverCurrentCoordinates, points);
								}

								if(current_fragment.getOrderStateHelper().getSubTitle() != null
										&& currentOrderState != null
										&& currentOrderState == OW) {
									String sub_title = current_fragment.getOrderStateHelper().getSubTitle();
									if(legs.get(0).getDistance() != null && legs.get(0).getDuration() != null) {
										String strDistance = legs.get(0).getDistance().getText();
										if(strDistance.contains(",")) {
											strDistance = strDistance.replace(",", ".");
										}
										String strDuration = legs.get(0).getDuration().getText();
										if(strDuration.contains(",")) {
											strDuration = strDuration.replace(",", ".");
										}
										String strTime = ", ещё " + strDistance + " и " + strDuration;

										if(sub_title.contains(", ")) {
											String[] strArray = sub_title.split(", ");
											String str1 = strArray[0];
											str1 += strTime;

											current_fragment.getOrderStateHelper().setSubTitle(str1);
										} else {
											sub_title += strTime;

											current_fragment.getOrderStateHelper().setSubTitle(sub_title);
										}
									}
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GOOGLE_DIRECTIONS_FOR_PRICE_ROUTE:
					if(response instanceof GoogleDirectionsData) {
						GoogleDirectionsData googleDirections = (GoogleDirectionsData) response;

						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DIRECTIONS_FOR_PRICE_ROUTE : googleDirectionsData : " + googleDirections);
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DIRECTIONS_FOR_PRICE_ROUTE : status : " + googleDirections.getStatus());

						List<GoogleRoute> routeList = googleDirections.getRoutes();
						if (routeList != null && !routeList.isEmpty()) {
							List<GoogleLeg> legs = routeList.get(0).getLegs();
							if (legs != null && !legs.isEmpty()) {
								List<GoogleStep> steps = legs.get(0).getSteps();
								if (steps != null && !steps.isEmpty()) {
									List<String> pointList = new ArrayList<>();
									for (GoogleStep item : steps) {
										GooglePolyline point = item.getPolyline();
										pointList.add(point.getPoints());
									}

									if (polygonBounds == null || polygonPoints == null) {
										String strJsonOfPolygon = CMApplication.readRawTextFile(MainActivity.this, R.raw.moscow_polygon);
										polygonBounds = PolygonHelper.getPolygonBounds(strJsonOfPolygon);
										polygonPoints = PolygonHelper.getPolygonPoints(strJsonOfPolygon);
									}
									outPolygonDistanceOfRoute = 0;
									closestPointOfPolygonDistance = 0;
									List<Float> distancesOutAndInPolygon = PolygonHelper.getDistancesOutAndInPolygon(pointList, polygonBounds);
									float inPolygonDistanceOfRoute = distancesOutAndInPolygon.get(0);
									outPolygonDistanceOfRoute = distancesOutAndInPolygon.get(1);

									GooglePlace collAddress = current_fragment.getFromAddress();
									GooglePlace deliveryAddress = current_fragment.getToAddress();

									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MainActivity.onSuccess : REQUEST_GOOGLE_DIRECTIONS : inPolygonDistance : " + inPolygonDistanceOfRoute
											+ " : outPolygonDistanceOfRoute : " + outPolygonDistanceOfRoute);
									if (deliveryAddress != null && deliveryAddress.getLatLng() != null && collAddress != null && collAddress.getLatLng() != null) {
										if (deliveryAddress.getLatLng().latitude != 0 && deliveryAddress.getLatLng().longitude != 0) {
											if (!polygonBounds.contains(collAddress.getLatLng())) {
												LatLng closestPointInPolygonFromPoint = PolygonHelper.getClosestPointInPolygonFromPoint(collAddress.getLatLng(), polygonPoints);
												if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DIRECTIONS : closestPointInPolygonFromPoint : "
														+ " : lat : " + closestPointInPolygonFromPoint.latitude
														+ " : lng : " + closestPointInPolygonFromPoint.longitude
												);
												if (closestPointInPolygonFromPoint != null) {
//													googleManager.GetDirections(collAddress.getLatLng().latitude + "," + collAddress.getLatLng().longitude,
//															closestPointInPolygonFromPoint.latitude + "," + closestPointInPolygonFromPoint.longitude,
//															RequestTypes.REQUEST_GOOGLE_DIRECTIONS_FOR_PRICE_OUT_POLYGON);

													googleManager.GetDistanceMatrix(collAddress.getLatLng().latitude + "," + collAddress.getLatLng().longitude,
															closestPointInPolygonFromPoint.latitude + "," + closestPointInPolygonFromPoint.longitude, "now", RequestTypes.REQUEST_DISTANCE_MATRIX_FOR_PRICE_OUT_POLYGON);
												} else {
													googleManager.GetDistanceMatrix(collAddress.getLatLng().latitude + "," + collAddress.getLatLng().longitude,
															deliveryAddress.getLatLng().latitude + "," + deliveryAddress.getLatLng().longitude, "now", RequestTypes.REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B);
												}
											} else {
												googleManager.GetDistanceMatrix(collAddress.getLatLng().latitude + "," + collAddress.getLatLng().longitude,
														deliveryAddress.getLatLng().latitude + "," + deliveryAddress.getLatLng().longitude, "now", RequestTypes.REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B);
											}

										}
									}
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_ORDERS_INFO:
					if (CMAppGlobals.DEBUG)
						Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS_INFO : orderInfoData : " + response);

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDERS_INFO")) {
						if(response instanceof GetOrderInfoData) {
							GetOrderInfoData orderInfoData = (GetOrderInfoData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS_INFO : orderInfoData : " + orderInfoData);

							List<Order> orderList = orderInfoData.getOrders();
							if (orderList != null && !orderList.isEmpty()) {
								currentOrder = orderList.get(0);
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS_INFO : currentOrderState : " + currentOrderState);
								if (currentOrder != null) {
									if(isDrawing(currentOrder.getIdOrder(), OrderState.getStateByStatus(currentOrder.getStatus()))) {
										// Draw on Map
										drawMapByOrderState(currentOrder, driverCurrentCoordinates, points);
									}
									// START SERVICE
									if (!isMyServiceRunning(GetOrderStatusService.class)) {
										if (CMAppGlobals.DEBUG)
											Logger.i(TAG, ":: MainActivity.OnSuccess : REQUEST_GET_ORDERS_INFO : start service ");
										// Alarm Status Receiver
										OrderStatusReceiver.scheduleAlarms(getApplicationContext());
									}
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_ORDERS_INFO_MENU:
					if (CMAppGlobals.DEBUG)
						Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS_INFO_MENU : orderInfoData : " + response);

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDERS_INFO_MENU")) {
						if(response instanceof GetOrderInfoData) {
							GetOrderInfoData orderInfoData = (GetOrderInfoData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS_INFO_MENU : orderInfoData : " + orderInfoData);

							List<Order> orderList = orderInfoData.getOrders();
							if (orderList != null && !orderList.isEmpty()) {
								currentOrder = orderList.get(0);

								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS_INFO_MENU : currentOrderState : " + currentOrderState);
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDERS_INFO_MENU : driverCurrentCoordinates : " + driverCurrentCoordinates);

								// Setting Order Service Options
								current_fragment.setServiceOptions(false, currentOrder);
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GOOGLE_DISTANCE_MATRIX:
					if(response instanceof GoogleDistanceMatrixData) {
						GoogleDistanceMatrixData distanceMatrixData = (GoogleDistanceMatrixData) response;
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : distanceMatrixData : " + distanceMatrixData);
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : status : " + distanceMatrixData.getStatus());

						List<GoogleRow> rows = distanceMatrixData.getRows();
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : rows : " + rows);
						if (rows != null && !rows.isEmpty()) {
							GoogleRow row = rows.get(0);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : row : " + row);
							if (row != null) {
								List<GoogleElement> elements = row.getElements();
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : elements : " + elements);
								if (elements != null && !elements.isEmpty()) {
									GoogleElement element = elements.get(0);
									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : element : " + element);
									if (element != null) {
										// With out API KEY this object is null
										GoogleDuration duration = element.getDuration();
										if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : durationInTraffic : " + duration);
										if (duration != null) {
											int second = duration.getValue();
											drivers_counter++;
											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : drivers_counter : " + drivers_counter);
											if (drivers_counter > 0 && drivers_counter <= driver_seconds.length) {
												driver_seconds[drivers_counter - 1] = second;
												if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : second : " + second);
											}
											if (drivers_counter == driver_seconds.length) {
												int minSecond = driver_seconds[0];
												for (int i = 1; i < driver_seconds.length; i++) {
													if (driver_seconds[i] < minSecond) {
														minSecond = driver_seconds[i];
													}
												}
												if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : minSecond : " + minSecond);
												// Show Time on UI
												((MapViewFragment)getCurrent_fragment()).getMapFragment().setDriverTime(minSecond);
											}
										}
										GoogleDistance googleDistance = element.getDistance();
										if (googleDistance != null) {
											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : googleDistance.getValue : " + googleDistance.getValue());
										}
									}
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_DRIVER_DISTANCE_MATRIX:
					if(response instanceof GoogleDistanceMatrixData) {
						GoogleDistanceMatrixData dMatrixData = (GoogleDistanceMatrixData) response;
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : distanceMatrixData : " + dMatrixData);
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : status : " + dMatrixData.getStatus());

						List<GoogleRow> dRows = dMatrixData.getRows();
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : rows : " + dRows);
						if (dRows != null && !dRows.isEmpty()) {
							GoogleRow row = dRows.get(0);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : row : " + row);
							if (row != null) {
								List<GoogleElement> elements = row.getElements();
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : elements : " + elements);
								if (elements != null && !elements.isEmpty()) {
									GoogleElement element = elements.get(0);
									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : element : " + element);
									if (element != null) {
										// With out API KEY this object is null
										GoogleDuration duration = element.getDuration();
										if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : durationInTraffic : " + duration);
										if (duration != null) {
											int second = duration.getValue();
											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : second : " + second);

											// update order status time
											if (second/60 != 0) {
												String distanceStr = getResources().getString(R.string.str_driver_distance);
												String timeString = String.valueOf(second/60);
												distanceStr = distanceStr.replace("{0}", timeString);
												current_fragment.getOrderStateHelper().setSubTitle(distanceStr);
											} else {
												String distanceStr = getResources().getString(R.string.str_driver_near_sut_title);
												current_fragment.getOrderStateHelper().setSubTitle(distanceStr);
												String strTitle = getResources().getString(R.string.str_driver_near_title);
												current_fragment.getOrderStateHelper().setTitle(strTitle);
											}
										}
										GoogleDistance googleDistance = element.getDistance();
										if (googleDistance != null) {
											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : googleDistance.getValue : " + googleDistance.getValue());
										}
									}
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_DISTANCE_MATRIX_FOR_PRICE_OUT_POLYGON:
					if(response instanceof GoogleDistanceMatrixData) {
						GoogleDistanceMatrixData dMatrixData = (GoogleDistanceMatrixData) response;
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : distanceMatrixData : " + dMatrixData);
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : status : " + dMatrixData.getStatus());

						List<GoogleRow> dRows = dMatrixData.getRows();
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : rows : " + dRows);
						if (dRows != null && !dRows.isEmpty()) {
							GoogleRow row = dRows.get(0);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : row : " + row);
							if (row != null) {
								List<GoogleElement> elements = row.getElements();
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : elements : " + elements);
								if (elements != null && !elements.isEmpty()) {
									GoogleElement element = elements.get(0);
									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_DRIVER_DISTANCE_MATRIX : element : " + element);
									if (element != null) {

										GoogleDistance googleDistance = element.getDistance();
										if (googleDistance != null) {
											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX : googleDistance.getValue : " + googleDistance.getValue());
										}


										if (googleDistance != null) {
											closestPointOfPolygonDistance = googleDistance.getValue();
										}

										if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MainActivity.onSuccess : REQUEST_GOOGLE_DIRECTIONS_FOR_PRICE_OUT_POLYGON : closestPointOfPolygonDistance : " + closestPointOfPolygonDistance);
										GooglePlace collAddress = current_fragment.getFromAddress();
										GooglePlace deliveryAddress = current_fragment.getToAddress();
										if (deliveryAddress != null && deliveryAddress.getLatLng() != null && collAddress != null && collAddress.getLatLng() != null) {
											if (deliveryAddress.getLatLng().latitude != 0 && deliveryAddress.getLatLng().longitude != 0) {
												googleManager.GetDistanceMatrix(collAddress.getLatLng().latitude + "," + collAddress.getLatLng().longitude,
														deliveryAddress.getLatLng().latitude + "," + deliveryAddress.getLatLng().longitude, "now", RequestTypes.REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B);
											}
										}
									}
								}
							}
						}
					}
					break;
				case RequestTypes.CHECK_FILES_CHANGES:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : CHECK_FILES_CHANGES : response : " + response);
					if(response instanceof CheckfilesData) {
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : CHECK_FILES_CHANGES : locale : " + ((CheckfilesData)response).getLocale());
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : CHECK_FILES_CHANGES : landmarks link : " + ((CheckfilesData)response).getLandmarksLink());
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : CHECK_FILES_CHANGES : tariffs link : " + ((CheckfilesData)response).getTariffsLink());
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : CHECK_FILES_CHANGES : cities link : " + ((CheckfilesData)response).getCitiesLink());

						if(!ErrorUtils.hasError(getApplicationContext(), response, "CHECK_FILES_CHANGES")) {
							if(((CheckfilesData)response).getCitiesLink() != null) {
								// store Cities date
								LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.CITIES_DATE, ((CheckfilesData)response).getLocale(), String.valueOf(((CheckfilesData) response).getCitiesDate()));
								// CITIES LINK REQUEST
								placeManager.GetCountriesList(((CheckfilesData) response).getCitiesLink());
							}
							if(((CheckfilesData)response).getTariffsLink() != null) {
								// store Tariffs date
								LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.TARIFFS_DATE, ((CheckfilesData)response).getLocale(), String.valueOf(((CheckfilesData) response).getTariffsDate()));
								// TARIFF LINK REQUEST
								tariffManager.GetTariffList(((CheckfilesData) response).getTariffsLink());
							}
							if(((CheckfilesData)response).getLandmarksLink() != null) {
								// store Landmark date
								LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.LANDMARKS_DATE, ((CheckfilesData)response).getLocale(), String.valueOf(((CheckfilesData) response).getLandmarksDate()));
								// LANDMARK LINK REQUEST
								landmarkManager.GetLandmarkList(((CheckfilesData) response).getLandmarksLink());
							}
						}
					}
					break;
				case RequestTypes.GET_TARIFFS:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : GET_TARIFFS : response : " + response);
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : GET_TARIFFS : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));
					if(response instanceof TariffData) {
						TariffData tariffData = (TariffData)response;
						if(!ErrorUtils.hasError(getApplicationContext(), response, "GET_TARIFFS")) {
							int languageID = new LanguageDBManager(this).getLanguageIdByLocale(tariffData.getLocale());
							int city_id = Integer.parseInt(tariffData.getId_locality());
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : GET_TARIFFS : locale : " +
									tariffData.getLocale() + " : language_id : " + languageID + " : city_id : " + city_id);

							// get tariffs
							List<Tariff> tariffs = tariffData.getData();
							if(tariffs != null && tariffs.size() > 0) {
								// insert tariffs DB
								tariffDBManager.setTariffs(tariffs, LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0), languageID, city_id);
							}
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : GET_TARIFFS : END : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));

							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getCurrentActivity() : " + mCMApplication.getCurrentActivity());

							String last_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : GET_TARIFFS : END : last_city_id : " + last_city_id
												+ " : city_id : " + city_id);
							if(mCMApplication.getCurrentActivity() != null
									&& mCMApplication.getCurrentActivity() instanceof MainActivity
									&& last_city_id.equals(String.valueOf(city_id))) {
								isCurrentTariffSet = true;
								setTariffsData(tariffs, false);
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_LANDMARK_LIST:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : response : " + response);
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));

					if(response instanceof LandmarkData) {

						LandmarkData landmarkData = (LandmarkData)response;
						if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_LANDMARK_LIST")) {
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : locale : " + landmarkData.getLocale());
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : getIdLocality : " + landmarkData.getIdLocality());
							int languageID = new LanguageDBManager(this).getLanguageIdByLocale(landmarkData.getLocale());
							int city_id = landmarkData.getIdLocality();
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : locale : " +
									landmarkData.getLocale() + " : language_id : " + languageID + " : city_id : " + city_id);

							// get tariffs
							LandmarkInfo landmarkInfo = landmarkData.getData();
							if(landmarkInfo != null) {
								// insert airports DB
								airportDBManager.setAirports(landmarkInfo.getAirports(), LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0), languageID, city_id);
								// insert railstations DB
								railstationDBManager.setRailstations(landmarkInfo.getRailstations(), LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0), languageID, city_id);

								// Load ALL Airport Polygons
								loadAllAirportPolygons();
							}
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : END : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));
						}
					}
					break;
				case RequestTypes.REQUEST_GET_AIRPORT_POLYGON:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_AIRPORT_POLYGON : response : " + response);

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_AIRPORT_POLYGON")) {
						if(response instanceof PolygonData) {
							PolygonData polygonData = (PolygonData) response;

							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_AIRPORT_POLYGON : polygonData.getAirport() : " + polygonData.getAirport());
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_AIRPORT_POLYGON : polygonData.getTerminal() : " + polygonData.getTerminal());

							List<PolygonPoint> polygonPoints = polygonData.getData();
							int airportId = polygonData.getAirport();
							if(polygonPoints != null && polygonPoints.size() > 0) {
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_AIRPORT_POLYGON : polygonPoints : " + polygonPoints);

								PolygonItem polygonItem = new PolygonItem();

								int languageID = new LanguageDBManager(MainActivity.this).getLanguageIdByLocale("ru");
								// Airport Data from Database
								Airport airportByID = airportDBManager.getAirportByID(String.valueOf(airportId), languageID);
								if(polygonData.getTerminal() > 0) {
									int terminalId = polygonData.getTerminal();
									polygonItem.setId(String.valueOf(terminalId));
									// Terminal Data from Database
									Terminal terminalByID = terminalDBManager.getTerminalById(terminalId, languageID);
									if(terminalByID != null) {
										polygonItem.setName(airportByID.getName() + ", " + terminalByID.getName());
										polygonItem.setLatitude(airportByID.getLatitude());
										polygonItem.setLongitude(airportByID.getLongitude());
										polygonItem.setPolygonType(PolygonType.TERMINAL);
									}
								} else {
									polygonItem.setId(String.valueOf(airportId));
									if(airportByID != null) {
										polygonItem.setName(airportByID.getName());
										polygonItem.setLatitude(airportByID.getLatitude());
										polygonItem.setLongitude(airportByID.getLongitude());
										polygonItem.setPolygonType(PolygonType.AIRPORT);
									}
								}

								polygonItem.setPolygonBounds(PolygonHelper.getPolygonBounds(polygonPoints));

								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_AIRPORT_POLYGON : polygonItem : NAME : " + polygonItem.getName());

								// add polygon
								polygonItems.add(polygonItem);
							}
						}

					}
					break;
				case RequestTypes.REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B:
					if(response instanceof GoogleDistanceMatrixData) {

						GoogleDistanceMatrixData distanceFromAToB = (GoogleDistanceMatrixData) response;
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : distanceFromAToB : " + distanceFromAToB);
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : status : " + distanceFromAToB.getStatus());

						List<GoogleRow> rowsFromAToB = distanceFromAToB.getRows();
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : rows : " + rowsFromAToB);
						if (rowsFromAToB != null && !rowsFromAToB.isEmpty()) {
							GoogleRow row = rowsFromAToB.get(0);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : row : " + row);
							if (row != null) {
								List<GoogleElement> elements = row.getElements();
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : elements : " + elements);
								if (elements != null && !elements.isEmpty()) {
									GoogleElement element = elements.get(0);
									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : element : " + element);
									if (element != null) {
										GetPriceRequest getPriceRequest = new GetPriceRequest();
										getPriceRequest.setOs_version(Build.VERSION.RELEASE);
										current_fragment.setOrderPrice(getPriceRequest);
										GoogleDistance googleDistance = element.getDistance();
										if (googleDistance != null) {
											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : googleDistance.getValue : " + googleDistance.getValue());
											getPriceRequest.setDistance(googleDistance.getValue() / 1000f);
										}

										// If request not contains API key
										GoogleDuration googleDuration = element.getDuration();
										if (googleDuration != null) {
											orderDuration = googleDuration.getValue() / 60;
											getPriceRequest.setDuration(orderDuration);
											current_fragment.setDriverTimeText(orderDuration);
											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : googleDuration.getValue : " + googleDuration.getValue());
										}
										// TODO If request contains API key
//										GoogleDurationInTraffic durationInTraffic = element.getDurationInTraffic();
//										if (durationInTraffic != null) {
//											if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : durationInTraffic.getValue : " + durationInTraffic.getValue());
//											orderDuration = durationInTraffic.getValue() / 60;
//											getPriceRequest.setDuration(orderDuration);
//											current_fragment.setDriverTimeText(orderDuration);
//										}
										if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : outPolygonDistanceOfRoute : " + outPolygonDistanceOfRoute
												+ " : closestPointOfPolygonDistance : " + closestPointOfPolygonDistance);
										getPriceRequest.setService_way2(outPolygonDistanceOfRoute / 1000f);
										getPriceRequest.setService_way3(closestPointOfPolygonDistance / 1000f);
										if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B : request : " + new Gson().toJson(getPriceRequest));
										orderManager.GetOrderPrice(getPriceRequest);
									}
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_ORDER_PRICE:

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDER_PRICE")) {
						if(response instanceof OrderPriceData) {
							OrderPriceData priceData = (OrderPriceData) response;
							List<Integer> prices = priceData.getPrice();
							if (prices != null && !prices.isEmpty()) {
								int price = prices.get(0);
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDER_PRICE : currentPrice : " + price);
								orderPrice = price;
								if (current_fragment != null) {
									current_fragment.setOrderPriceText(price, false);
									if (current_fragment.ismConfirmVisible() && CMApplication.getTrackingOrderId().isEmpty()) {
										String subTitle = StringHelper.combineStrings(getResources().getString(R.string.str_price),
												" ",
												String.valueOf(price),
												" ",
												getResources().getString(R.string.str_ruble),
												" ",
												getResources().getString(R.string.str_behind),
												" ",
												String.valueOf(orderDuration),
												" ",
												getResources().getString(R.string.str_minute));
										if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_ORDER_PRICE : subTitle : " + subTitle);
										current_fragment.getOrderStateHelper().setSubTitle(subTitle);
									}
								}
							}
						}
					}
					break;
				case RequestTypes.REQUEST_CAME_OUT:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_CAME_OUT")) {
						if(response instanceof ResultData) {
							ResultData resultData = (ResultData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_CAME_OUT : resultData : " + resultData);

							if (resultData.getResult().equals("1")) {
								current_fragment.getOrderStateHelper().getTextViewGoToDriver().setVisibility(View.GONE);
								current_fragment.getOrderStateHelper().setIsCameOut(true);
							} else {
								Toast.makeText(MainActivity.this, R.string.alert_informing_driver_to_come, Toast.LENGTH_SHORT).show();
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_CARDS:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_CARDS")) {
						if(response instanceof CardsData) {
							CardsData cardsData = (CardsData) response;
							// DRAW CARDS
							if (cardsData.getCards() != null) {
								if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_CARDS : cardsData.getCards().size() : " + cardsData.getCards().size());

								for (CardInfo item :
										cardsData.getCards()) {
									if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_CARDS : CARD_ID : " + item.getId());
								}
								// set credit cards data
								current_fragment.setCreditCards(cardsData.getCards());
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_FAVORITE:
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_FAVORITE")) {
						//noinspection unchecked
						List<GetFavoriteData> favorites = (List<GetFavoriteData>) response;

						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_FAVORITE : favorites : " + favorites);

						// NOTIFY FROM/TO ADAPTER
						if (getCurrent_fragment() != null)
							((MapViewFragment) getCurrent_fragment()).addFavoriteAddresses(favorites);
					}
					break;
				case RequestTypes.REQUEST_GET_FAVORITE_FROM_SEARCH:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_FAVORITE_FROM_SEARCH : response : " + response);
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_FAVORITE_FROM_SEARCH")) {
						//noinspection unchecked
						List<GetFavoriteData> favorites = (List<GetFavoriteData>) response;

						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_FAVORITE_FROM_SEARCH : favorites : " + favorites);
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GET_FAVORITE_FROM_SEARCH : searchDataBundle : " + searchDataBundle);

						// NOTIFY FROM/TO ADAPTER
						if (getCurrent_fragment() != null)
							((MapViewFragment) getCurrent_fragment()).addFavoriteAddresses(favorites);
						if (searchDataBundle != null) {
							GooglePlace address = searchDataBundle.getParcelable(CMAppGlobals.EXTRA_SEARCH_ADDRESS);
							if (searchDataBundle.getInt(CMAppGlobals.SCROLL_TYPE, -1) != -1 && address != null) {
								ScrollType scrollType = ScrollType.values()[searchDataBundle.getInt(CMAppGlobals.SCROLL_TYPE, -1)];
								switch (scrollType) {
									case SO_FROM:
										((MapViewFragment) getCurrent_fragment()).refreshAddressByType(MainActivity.this, ScrollType.SO_FROM, address, true);
										break;
									case SO_WHERE:

										((MapViewFragment) getCurrent_fragment()).refreshAddressByType(MainActivity.this, ScrollType.SO_WHERE, address, false);
										break;
									default:
										break;
								}
								((MapViewFragment)getCurrent_fragment()).actionPanel(SlidingUpPanelLayout.SlideState.COLLAPSED);
							}
						}

					}
					break;
				case RequestTypes.REQUEST_GOOGLE_GEOCODE_FOR_COLL_ADDRESS:
					if(response instanceof GoogleGeocode) {
						GoogleGeocode googleGeocode = (GoogleGeocode) response;
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_GEOCODE_FOR_COLL_ADDRESS : googleGeocode : " + googleGeocode);
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_GEOCODE_FOR_COLL_ADDRESS : status : " + googleGeocode.getStatus());

						String collAddressGeoObject = getGeoDataJsonString(googleGeocode);
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_GEOCODE_FOR_COLL_ADDRESS : collAddressGeoObject : " + collAddressGeoObject);
						current_fragment.setCollAddressGeoObject(collAddressGeoObject);
					}
					break;
				case RequestTypes.REQUEST_GOOGLE_GEOCODE_FOR_DELIVERY_ADDRESS:
					if(response instanceof GoogleGeocode) {
						GoogleGeocode googleGeocodeDelivery = (GoogleGeocode) response;
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_GEOCODE_FOR_DELIVERY_ADDRESS : googleGeocode : " + googleGeocodeDelivery);
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_GEOCODE_FOR_DELIVERY_ADDRESS : status : " + googleGeocodeDelivery.getStatus());

						String deliveryAddressGeoObject = getGeoDataJsonString(googleGeocodeDelivery);
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_GOOGLE_GEOCODE_FOR_DELIVERY_ADDRESS : deliveryAddressGeoObject : " + deliveryAddressGeoObject);
						current_fragment.setDeliveryAddressGeoObject(deliveryAddressGeoObject);
					}
					break;
				case RequestTypes.REQUEST_PREPARE_CANCEL_ORDER:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_PREPARE_CANCEL_ORDER");

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_PREPARE_CANCEL_ORDER")) {
						if(response instanceof PrepareCancelOrderData) {
							PrepareCancelOrderData prepareCancelOrderData = (PrepareCancelOrderData) response;
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onSuccess : REQUEST_PREPARE_CANCEL_ORDER : prepareCancelOrderData : " + prepareCancelOrderData);

							if (prepareCancelOrderData.getIsPaid() == 1) {
								current_fragment.getOrderStateHelper().showPrepareCancelOrderDialog(prepareCancelOrderData.getCancelText());
							} else {
								current_fragment.getOrderStateHelper().showCancelOrderDialog();
							}
						}
					} else {
						current_fragment.getOrderStateHelper().showCancelOrderDialog();
					}
					break;
			}
		}
	}

	private boolean isCurrentTariffSet = false;

	/**
	 * Method for setting tariff adapter
	 *
	 * @param tariffs - tariff's list
	 */
	public void setTariffsData(List<Tariff> tariffs, boolean fromSplash) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setTariffsData : tariffs : " + tariffs);

		if(fromSplash && isCurrentTariffSet)
			return;
		// SET SLIDE PANEL TARIFF DATA
		Fragment current_fragment = getCurrent_fragment();
		if(current_fragment != null && current_fragment instanceof MapViewFragment && CMApplication.getTrackingOrderId().isEmpty()) {
			((MapViewFragment)current_fragment).setTariffsData(tariffs, fromSplash);
		}

	} // end method setTariffsData

	public Fragment getCurrent_fragment() {
		return current_fragment;
	}

	public void setCurrent_fragment(Fragment current_fragment) {
		this.current_fragment = (MapViewFragment) current_fragment;
	}

	/**
	 * Method for refreshing drivers
	 */
	public void refreshDrivers() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.refreshDrivers ");

		GetDriversRequest getDriversRequest = new GetDriversRequest();
		if(current_fragment != null) {

			if (!CMApplication.getTrackingOrderId().isEmpty()) {
				if (currentOrder != null) {
					if (currentOrder.getLatitude() != null && !currentOrder.getLatitude().isEmpty())
						getDriversRequest.setLatitude(Double.parseDouble(currentOrder.getLatitude()));
					if (currentOrder.getLongitude() != null && !currentOrder.getLongitude().isEmpty())
						getDriversRequest.setLongitude(Double.parseDouble(currentOrder.getLongitude()));

					if (currentOrder.getTariff() != null && !currentOrder.getTariff().isEmpty())
						getDriversRequest.setTariff(Integer.parseInt(currentOrder.getTariff()));

					List<Tariff> tariff_list;
					String current_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
					if (currentOrder.getIdLocality() != 0 && !current_city_id.isEmpty() && currentOrder.getIdLocality() != Integer.parseInt(current_city_id)) {
						// Getting tariff list
						TariffDBManager tariffDBManager = new TariffDBManager(MainActivity.this);
						int languageID = new LanguageDBManager(MainActivity.this).getLanguageIdByLocale("ru");
						tariff_list = (ArrayList<Tariff>) tariffDBManager.getAllTariffs(languageID, currentOrder.getIdLocality());
					} else {
						tariff_list = current_fragment.getTariff_list();
					}

					if (tariff_list != null && !tariff_list.isEmpty()
							&& currentOrder.getTariff() != null
							&& !currentOrder.getTariff().isEmpty()) {

						for (Tariff item : tariff_list) {
							if (item.getID().equals(currentOrder.getTariff())) {
								List<TariffService> tariffServices = item.getServices();
								if (tariffServices != null && !tariffServices.isEmpty()) {
									List<String> tariffServiceFields = MapViewFragment.getTariffServiceFields(currentOrder);
									List<TariffService> selectedTariffServices = new ArrayList<>();
									if (!tariffServiceFields.isEmpty()) {
										for (int i = 0; i < tariffServices.size(); i++) {
											if (tariffServiceFields.contains(tariffServices.get(i).getField())) {
												selectedTariffServices.add(tariffServices.get(i));
											}
										}

										current_fragment.setTariffServices(getDriversRequest, selectedTariffServices);
									}
									break;
								}
							}
						}
					}

				}
			} else {
				GooglePlace googlePlace = ((MapGoogleFragment)((MapViewFragment) getCurrent_fragment())
						.getMapFragment()).getCurrentGoogleAddress();
				if (googlePlace != null) {
					LatLng latLng = googlePlace.getLatLng();
					getDriversRequest.setLatitude(latLng.latitude);
					getDriversRequest.setLongitude(latLng.longitude);

					Tariff selectedTariff = ((MapViewFragment) getCurrent_fragment()).getSelectedTariff();
					if (selectedTariff != null) {
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity : selectedTariff Id : " +  selectedTariff.getID());
						getDriversRequest.setTariff(Integer.parseInt(selectedTariff.getID()));
					}

					List<TariffService> tariffServices = ((MapViewFragment) getCurrent_fragment()).getSelectedTariffServices();
					if (tariffServices != null && !tariffServices.isEmpty()) {
						current_fragment.setTariffServices(getDriversRequest, tariffServices);
					}
				}
			}

//			GooglePlace googlePlace = ((MapGoogleFragment)((MapViewFragment) getCurrent_fragment())
//					.getMapFragment()).getCurrentGoogleAddress();
//			if (googlePlace != null) {
//				LatLng latLng = googlePlace.getLatLng();
//				getDriversRequest.setLatitude(latLng.latitude);
//				getDriversRequest.setLongitude(latLng.longitude);
//			}
			getDriversRequest.setRadius(CMAppGlobals.NEAREST_DRIVERS_DEFAULT_RADIUS);
			getDriversRequest.setIdLocality("2");
//			Tariff selectedTariff = ((MapViewFragment) getCurrent_fragment()).getSelectedTariff();
//			if (selectedTariff != null) {
//				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity : selectedTariff Id : " +  selectedTariff.getID());
//				getDriversRequest.setTariff(Integer.parseInt(selectedTariff.getID()));
//			}
//			List<TariffService> tariffServices = ((MapViewFragment) getCurrent_fragment()).getSelectedTariffServices();
//			if (tariffServices != null && !tariffServices.isEmpty()) {
//				((MapViewFragment)getCurrent_fragment()).setTariffServices(getDriversRequest, tariffServices);
//			}


//			if(MapGoogleFragment.isLowMemory(MainActivity.this)) {
//				// set limit of drivers response
//				getDriversRequest.setLimit(5);
//			} else {
				// set limit of drivers response
				getDriversRequest.setLimit(10);
//			}

			// get drivers coordinates from server
			requestDriversCoord(MainActivity.this, getDriversRequest);
		}

	} // end method refreshDrivers


	private OrderState current_order_state = null;

	@Override
	public void onReceiveOrderStatusResult(Context context, Intent intent) {
		if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onReceiveOrderStatusResult : intent : " + intent);

		if(intent != null) {
			if(intent.hasExtra(GCMUtils.EXTRA_IS_GCM)) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onReceiveOrderStatusResult : IS_GCM ");

				String orderId = intent.getExtras().getString(GCMUtils.EXTRA_ORDER_ID);
				//final int status = intent.getExtras().getInt(GCMUtils.EXTRA_ORDER_STATE);

				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onReceiveOrderStatusResult : IS_GCM : orderId : " + orderId);

				// Request for getting order status list
				OrderManager orderManager = new OrderManager(this);
				List<OrderIdItem>  orderIdItems = CMApplication.getActiveOrders();
				for(OrderIdItem idItem :orderIdItems){
					if (orderId != null) {
						orderId = orderId.concat(",");
						orderId = orderId.concat(idItem.getIdOrder());
					}
				}
				orderManager.GetOrderStatusListFromPush(orderId);

			} else {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onReceiveOrderStatusResult : SERVICE ");

				List<OrderStatusData> orders = intent.getParcelableArrayListExtra(GCMUtils.EXTRA_ORDER_LIST);
				// set order data
				setOrderListData(orders);
			}
		}
	}

	/**
	 * Method for setting order list data getting
	 * from order list response
	 *
	 * @param orders - list of order status data
	 */
	public void setOrderListData(List<OrderStatusData> orders) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : orders : " + orders);

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : current_order_state : " + current_order_state);

		List<OrderStatusData> active_orders = new ArrayList<>();
		if(orders != null && orders.size() > 0) {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : orders : " + orders);
			for(int i=0; i< orders.size(); i++) {
				OrderStatusData item = orders.get(i);
				if(item.getIdOrder().equals(CMApplication.getTrackingOrderId())) {
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : orderId : " + item.getIdOrder()
							+ " : status : " + item.getStatus()
							+ " : trip_time : " + item.getTripTime()
							+ " : currentPrice : " + item.getPrice()
							+ " : bonus : " + item.getBonus()
							+ " : title : " + item.getTitle()
							+ " : subTitle : " + item.getSubTitle());
					if(currentOrderState != OrderState.getStateByStatus(item.getStatus())) {
						((MapGoogleFragment)current_fragment.getMapFragment()).setmMapIsTouched(false);
					}
					currentOrderState = OrderState.getStateByStatus(item.getStatus());
					if(currentOrder != null)
						currentOrder.setStatus(OrderState.getStatusStringByState(currentOrderState));
					if(OrderState.getStateByStatus(item.getStatus()) != CC && OrderState.getStateByStatus(item.getStatus()) != NC) {

						// Set NewOrderData information orderId/status/currentPrice/bonus/trip_time
						setOrderState(item.getIdOrder(), OrderState.getStateByStatus(item.getStatus()), item.getPrice(), item.getBonus(), item.getTripTime());

						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : isNotification : " + isNotification);

						if (isNotification) {
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : isNotification : " + isNotification);
							isNotification = false;
							if(current_order_state != null) {
								if(current_order_state == OrderState.getStateByStatus(item.getStatus())) {
									// set order UI
									current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), true,
											item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), null, false);
								} else {
									current_order_state = OrderState.getStateByStatus(item.getStatus());
									// set order UI
									current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), true,
											item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), item.getSubTitle(), true);
								}
							} else {
								current_order_state = OrderState.getStateByStatus(item.getStatus());
								// set order UI
								current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), true,
										item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), item.getSubTitle(), true);
							}
							orderManager.GetOrdersInfo("", item.getIdOrder(), RequestTypes.REQUEST_GET_ORDERS_INFO_MENU);
						} else {
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.onReceiveOrderStatusResult : current_order_state : " + current_order_state
									+ " : Status : " + item.getStatus()
									+ " : SubTitle : " + item.getSubTitle()
									+ " : Title : " + item.getTitle()
							);
							if(current_order_state != null) {
								if(current_order_state == OrderState.getStateByStatus(item.getStatus())
										&& current_order_state != OrderState.RC
										&& current_order_state != OrderState.OW) {
									// set order UI
									current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
											item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), null, null, false);
								} else {
									boolean isNewState = true;
									if(current_order_state == OrderState.getStateByStatus(item.getStatus())){
										isNewState = false;
									}
									current_order_state = OrderState.getStateByStatus(item.getStatus());
									if(current_order_state == ASAP_A_BUSY || current_order_state == ASAP_A_THIS) {
										// set order UI
										current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
												item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), getResources().getString(R.string.str_driver_time_calc), isNewState);
									}else if (current_order_state == LATER_A){
										String subTitle = getResources().getString(R.string.str_order_registered_content);
										subTitle = subTitle.replace("{0}", getResources().getString(R.string.str_minute_registered));
										// set order UI
										current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
												item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), subTitle, isNewState);
									}else {
										// set order UI
										current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
												item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), item.getSubTitle(), isNewState);
									}
									current_fragment.getOrderStateHelper().changeSlidePanelItems(OrderState.getStateByStatus(item.getStatus()));
								}
							} else {
								current_order_state = OrderState.getStateByStatus(item.getStatus());
								// set order UI
								current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
										item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), item.getSubTitle(), true);
							}
						}
						// Add Additional Fields And Services when order status is OW
						if (OrderState.getStateByStatus(item.getStatus()) == OrderState.OW) {
							current_fragment.getOrderStateHelper().addAdditionalFieldsAndServices(item);
						}

						if(OrderState.getStateByStatus(item.getStatus()) == OrderState.RC && item.getCameout() > 0) {
							current_fragment.getOrderStateHelper().setIsCameOut(true);
						}
					} else {
						// stop driver coordinates handler
						if (OrderState.getStateByStatus(item.getStatus()) == CP) {
							MainActivity.this.waitHandler.removeMessages(RequestTypes.REQUEST_GET_DRIVER_COORDINATES);
						}
						// remove order from active orders
						CMApplication.removeActiveOrder(item.getIdOrder());
						if(CMApplication.getActiveOrders().size() > 0)  {
							// set menu active icon
							((MapViewFragment) getCurrent_fragment()).setMenuIcon(true);
						} else {
							// set menu icon
							((MapViewFragment) getCurrent_fragment()).setMenuIcon(false);
						}
						current_order_state = OrderState.getStateByStatus(item.getStatus());
						// CC -- Order Canceled
						current_fragment.setDriverUI(null, false);
						current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
								item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), item.getSubTitle(), true);
						current_fragment.getOrderStateHelper().changeSlidePanelItems(OrderState.getStateByStatus(item.getStatus()));
					}
				}
				if(OrderState.getStateByStatus(item.getStatus()) != CP
						&& OrderState.getStateByStatus(item.getStatus()) != CC
						&& OrderState.getStateByStatus(item.getStatus()) != NC) {
					active_orders.add(item);
				} else {
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : status : " + item.getStatus());
					currentOrderState = OrderState.getStateByStatus(item.getStatus());
					if (OrderState.getStateByStatus(item.getStatus()) != CP) {
						if(current_order_state != null) {
							if(current_order_state == OrderState.getStateByStatus(item.getStatus())) {
								// set order UI
								current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
										item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), null, false);
							} else {
								current_order_state = OrderState.getStateByStatus(item.getStatus());
								// set order UI
								current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
										item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), item.getSubTitle(), true);
							}
						} else {
							current_order_state = OrderState.getStateByStatus(item.getStatus());
							// set order UI
							current_fragment.getOrderStateHelper().SetOrderState(current_fragment.getRootView(), OrderState.getStateByStatus(item.getStatus()), false,
									item.getIdOrder(), item.getPrice(), item.getBonus(), item.getTripTime(), item.getTitle(), item.getSubTitle(), true);
						}
					}
					MenuHelper.removeActiveOrder(item.getIdOrder());
					if (Utils.needCancelAlarm(item.getIdOrder())) {
						// STOP SERVICE
						OrderStatusReceiver.cancelAlarms(getApplicationContext());
					}
					currentOrderState = null;
				}
			}
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderListData : active_orders : " + active_orders);
			//update
			CMApplication.updateActiveOrders(active_orders);
			//  Active orders list refresh
			MenuHelper.getInstance().addActiveOrders(MainActivity.this, active_orders);
			if(CMApplication.getActiveOrders() != null
					&& CMApplication.getActiveOrders().size() > 0)  {
				if (current_fragment != null) {
					// set menu active icon
					current_fragment.setMenuIcon(true);
				} else {
					// set menu icon
					current_fragment.setMenuIcon(false);
				}
			}
		}

	} // end method setOrderListData


	public OrderState drawOrderState = null;
	public String drawOrderId = null;
	public boolean isCentering = false;

	/**
	 * Method for checking drawing
	 *
	 * @param orderId		- current order id
	 * @param orderState	- current order state
     * @return				- true ? draw : not draw
     */
	public boolean isDrawing(String orderId, OrderState orderState) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.isDrawing : orderId : "
				+ orderId + " : order_state : " + orderState
				+ " : drawOrderId : " + drawOrderId + " : drawOrderState : " + drawOrderState);

		if(drawOrderId != null && drawOrderState != null) {
			switch (orderState) {
				case R:
				case LATER_R:
				case ASAP_R:
					if(!drawOrderId.equals(orderId)) {
						drawOrderId = orderId;
						drawOrderState = orderState;
						return true;
					} else {
						if(drawOrderState != orderState) {
							drawOrderState = orderState;
							return true;
						}
					}
					break;
				case A: // DRIVER FOUND
				case LATER_A:
				case ASAP_A_THIS:
				case ASAP_A_BUSY:
					if(!drawOrderId.equals(orderId)) {
						drawOrderId = orderId;
						drawOrderState = orderState;
						isCentering = true;
					} else {
						if(drawOrderState != orderState) {
							drawOrderState = orderState;
							isCentering = true;
						} else {
							isCentering = false;
						}
					}
					return true;
				case RC:
				case OW:
					if(!drawOrderId.equals(orderId)) {
						drawOrderId = orderId;
						drawOrderState = orderState;
						isCentering = true;
					} else {
						if(drawOrderState != orderState) {
							drawOrderState = orderState;
							isCentering = true;
						} else {
							isCentering = false;
						}
					}
					return true;
			}
		} else {
			drawOrderId = orderId;
			drawOrderState = orderState;

			isCentering = true;

			return true;
		}

		return false;

	} // end method isDrawing

	/**
	 * Method for setting order state
	 *
	 * @param orderId - order id
	 * @param status - order status
	 * @param price - order price
	 * @param bonus - user bonus
	 * @param trip_time - order trip time
	 */
	public void setOrderState(String orderId, OrderState status, String price, String bonus, String trip_time) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderState : orderId : " + orderId +
						" : status : " + status + " : currentPrice : " + price + " : bonus : " + bonus + " : trip_time : " + trip_time);

		// SET ORDER to ACTIVE ORDERS
		if(!CMApplication.isOrderItemExists(orderId)) {
			OrderIdItem itemOrder = new OrderIdItem();
			itemOrder.setIdOrder(orderId);
			itemOrder.setStatus(OrderState.getStatusStringByState(status));
			CMApplication.setActiveOrder(itemOrder, false);
			if (current_fragment != null) {
				if(CMApplication.getActiveOrders().size() > 0)  {
					// set menu active icon
					current_fragment.setMenuIcon(true);
				} else {
					// set menu icon
					current_fragment.setMenuIcon(false);
				}
			}
		}

		switch (status) {
			case R:
			case LATER_R:
			case ASAP_R:
				setCurrent_driver_info(null);

				GooglePlace callAddress = ((MapViewFragment)getCurrent_fragment()).getFromAddress();

				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : callAddress : " + callAddress
						+ " : callAddress.getLatLng : " + callAddress.getLatLng());

				if (getCurrentOrder() != null) {
					if(isDrawing(orderId, status)) {
						if (current_fragment != null)
							current_fragment.setDriverUI(null, false);
						drawMapByOrderState(getCurrentOrder(), driverCurrentCoordinates, points);
					}
				} else {
					OrderManager orderManager = new OrderManager(this);
					orderManager.GetOrdersInfo("", orderId, RequestTypes.REQUEST_GET_ORDERS_INFO);
				}
				break;
			case A: // DRIVER FOUND
			case LATER_A:
			case ASAP_A_THIS:
			case ASAP_A_BUSY:
			{
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderState : STATUS : " + status + " : current_driver_info : " + current_driver_info);
				// remove callbacks for get drivers request
				if (updateDriverHandler != null)
					updateDriverHandler.removeCallbacks(updateDriverRunnable);
				// Check if current driver information exists
				if(current_driver_info == null) {
					// Get appointed driver info by orderID
					getDriverInfo(orderId);
				} else {
					// Get current Driver coordinates
					MainActivity.this.waitHandler.sendEmptyMessageDelayed(RequestTypes.REQUEST_GET_DRIVER_COORDINATES, DRIVER_COORD_INTERVAL);
				}
			}
				break;
			case RC: // DRIVER ARRIVED
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderState : STATUS : " + status + " : current_driver_info : " + current_driver_info);
				// Check if current driver information exists
				if(current_driver_info == null) {
					// Get appointed driver info by orderID
					getDriverInfo(orderId);
				} else {
					// Get current Driver coordinates
					MainActivity.this.waitHandler.sendEmptyMessageDelayed(RequestTypes.REQUEST_GET_DRIVER_COORDINATES, DRIVER_COORD_INTERVAL);
				}
				break;
			case OW: // ON THE WAY
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderState : OW : driverCurrentCoordinates : " + driverCurrentCoordinates);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setOrderState : OW : orderId : " + orderId);
				// remove callbacks for get drivers request
				if (updateDriverHandler != null)
					updateDriverHandler.removeCallbacks(updateDriverRunnable);

				if (driverCurrentCoordinates == null) {
					getDriverInfo(orderId);
				} else {
					MainActivity.this.waitHandler.sendEmptyMessageDelayed(RequestTypes.REQUEST_GET_DRIVER_COORDINATES, DRIVER_COORD_INTERVAL);
				}
				break;
			case CC: // ORDER CANCELED
				OrderStatusReceiver.cancelAlarms(getApplicationContext());
				break;
 			case CP: // ORDER COMPLETED
				OrderStatusReceiver.cancelAlarms(getApplicationContext());
				break;
			default:
				break;
		}

	} // end method setOrderState



	/**
	 * Method for getting driver information by orderId
	 *
	 * @param orderId - current order Id
	 */
	public void getDriverInfo(String orderId) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getDriverInfo : orderId : " + orderId);

		driverManager.GetDriverInfo(orderId, "");

	} // end method getDriverInfo

	/**
	 * This handler should be user for emulation purpose only
	 */
	private Handler waitHandler;

	private static class WaitHandler extends Handler {
		private final WeakReference<DriverManager> mDriverManager;
		WaitHandler(DriverManager driverManager) {
			this.mDriverManager = new WeakReference<>(driverManager);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
				case RequestTypes.REQUEST_GET_DRIVER_COORDINATES:
					DriverManager driverManager = mDriverManager.get();
					if (driverManager != null) {
						// get driver coordinates
						driverManager.GetDriverCoordinates();
					}
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Method for getting driver coordinates
	 */
	private void getDriverCoordinates() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.getDriverCoordinates : orderId : " + CMApplication.getTrackingOrderId());

		// get driver coordinates
		driverManager.GetDriverCoordinates();

	} // end method getDriverCoordinates

	/**
	 * Method for checking service state
	 * is running or not
	 * @param serviceClass - class of service
	 * @return - service running ? true : false
	 */
	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method for updating order status UI
	 *
	 * @param orderStatusData - order status data
	 */
	public void updateUIByOrderStatusData(OrderStatusData orderStatusData) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.updateUIByOrderStatusData : orderStatusData : " + orderStatusData);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.updateUIByOrderStatusData : orderStatusData : orderId : " + orderStatusData.getIdOrder());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.updateUIByOrderStatusData : orderStatusData : status : " + orderStatusData.getStatus());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.updateUIByOrderStatusData : orderStatusData : currentPrice : " + orderStatusData.getPrice());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.updateUIByOrderStatusData : orderStatusData : bonus : " + orderStatusData.getBonus());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.updateUIByOrderStatusData : orderStatusData : trip_time : " + orderStatusData.getTripTime());

		setDefaultValuesAfterCancelOrder();

		if(currentOrderState != OrderState.getStateByStatus(orderStatusData.getStatus())) {
			((MapGoogleFragment)current_fragment.getMapFragment()).setmMapIsTouched(false);
		}

		if (current_fragment.getSlidingUpPanelLayout().isExpanded() && !isCalledCollapsePane) {
			current_fragment.setActiveOrder(orderStatusData);
			current_fragment.getSlidingUpPanelLayout().collapsePane();
			isCalledCollapsePane = true;
		} else {

			if (!orderStatusData.getStatus().equals("CP")
					&& !orderStatusData.getStatus().equals("CC")
					&& !orderStatusData.getStatus().equals("NC")
					&& !orderStatusData.getStatus().equals("AR")) {

				((MapGoogleFragment)current_fragment.getMapFragment()).setDirection_increment(0);

				if(((MapGoogleFragment)current_fragment.getMapFragment()).getmMap() != null) {
					((MapGoogleFragment) current_fragment.getMapFragment()).getmMap().clear();
				}

				switch (OrderState.getStateByStatus(orderStatusData.getStatus())) {
					case R:
					case ASAP_R:
					case LATER_R:
						current_fragment.setDriverUI(null, false);
						orderManager.GetOrdersInfo("", orderStatusData.getIdOrder(), RequestTypes.REQUEST_GET_ORDERS_INFO);
						break;
					case A:
					case ASAP_A_THIS:
					case ASAP_A_BUSY:
					case LATER_A:
						// remove callbacks for get drivers request
						if (updateDriverHandler != null)
							updateDriverHandler.removeCallbacks(updateDriverRunnable);
						getDriverInfo(orderStatusData.getIdOrder());
						break;
					case RC:
						// remove callbacks for get drivers request
						if (updateDriverHandler != null)
							updateDriverHandler.removeCallbacks(updateDriverRunnable);
						getDriverInfo(orderStatusData.getIdOrder());
						break;
					case OW:
						// remove callbacks for get drivers request
						if (updateDriverHandler != null)
							updateDriverHandler.removeCallbacks(updateDriverRunnable);
//						current_fragment.setDriverUI(null, false);
						getDriverInfo(orderStatusData.getIdOrder());
						break;
				}

				CMApplication.setTrackingOrderId(orderStatusData.getIdOrder());
			}
			// Request get orders info for Setting Order Service Options
			orderManager.GetOrdersInfo("", orderStatusData.getIdOrder(), RequestTypes.REQUEST_GET_ORDERS_INFO_MENU);

			current_order_state = OrderState.getStateByStatus(orderStatusData.getStatus());
			current_fragment.getOrderStateHelper().updateUIByOrderStatusData(current_fragment.getRootView(), orderStatusData);
			currentOrderState = OrderState.getStateByStatus(orderStatusData.getStatus());
		}

	} // end method updateUIByOrderStatusData

	/**
	 * Method for getting currentOrder
	 *
	 * @return - current active order
	 */
	public Order getCurrentOrder() {
		return currentOrder;

	} // end method getCurrentOrder


	/**
	* Method fro getting Google Distance Matrix
	 *
	* @param drivers - list of drivers
	*/
	public void getGoogleDistanceMatrix(List<Driver> drivers) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrix : drivers : " + drivers);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrix : drivers.size : " + drivers.size());

		if (((MapViewFragment) getCurrent_fragment()).getCurrent_address() != null) {
			double latitude = ((MapViewFragment) getCurrent_fragment()).getCurrent_address().getLatLng().latitude;
			double longitude = ((MapViewFragment) getCurrent_fragment()).getCurrent_address().getLatLng().longitude;

			for (Driver item : drivers) {
				googleManager.GetDistanceMatrix(item.getLt() + "," + item.getLn(),
						latitude + "," + longitude, "now", RequestTypes.REQUEST_GOOGLE_DISTANCE_MATRIX);
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrix : current.getLt() : " + latitude + " : current.getLn() : " + longitude);
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrix : item.getLt() : " + item.getLt() + " : item.getLn() : " + item.getLn());
			}
		}

	} // end method getGoogleDistanceMatrix

	/**
	 * Method fro getting Driver Distance Matrix
	 *
	 * @param driver - driver info
	 */
	public void getDriverDistanceMatrix(Driver driver) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getDriverDistanceMatrix : driver : " + driver + " : drivers.getCarType() : " + driver.getCarType());

		if(currentOrder != null) {
			googleManager.GetDistanceMatrix(driver.getLt() + "," + driver.getLn(),
					currentOrder.getLatitude() + "," + currentOrder.getLongitude(), "now", RequestTypes.REQUEST_DRIVER_DISTANCE_MATRIX);
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getDriverDistanceMatrix : current.getLt() : " + currentOrder.getLatitude() + " : current.getLn() : " + currentOrder.getLongitude());
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getDriverDistanceMatrix : driver.getLt() : " + driver.getLt() + " : driver.getLn() : " + driver.getLn());
		}

	} // end method getDriverDistanceMatrix

	/**
	 * Method for getting google distance matrix from A to B
	 */
	public void getGoogleDistanceMatrixFromAToB() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB");

		GooglePlace deliveryAddress = current_fragment.getToAddress();
		GooglePlace collAddress = current_fragment.getFromAddress();
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB : collAddress : " + collAddress);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB : deliveryAddress : " + deliveryAddress);
		if (deliveryAddress != null && collAddress != null) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB : deliveryAddress : name : " + deliveryAddress.getName());
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB : deliveryAddress : address : " + deliveryAddress.getAddress());
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB : collAddress : name : " + collAddress.getName());
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB : collAddress : address : " + collAddress.getAddress());
		}

		if (deliveryAddress != null && deliveryAddress.getLatLng() != null && collAddress != null && collAddress.getLatLng() != null) {
			if (deliveryAddress.getLatLng().latitude != 0 && deliveryAddress.getLatLng().longitude != 0) {
//				googleManager.GetDistanceMatrixFromAToB(collAddress.getLatLng().latitude + "," + collAddress.getLatLng().longitude,
//						deliveryAddress.getLatLng().latitude + "," + deliveryAddress.getLatLng().longitude, "now");

				googleManager.GetDirections(collAddress.getLatLng().latitude + "," + collAddress.getLatLng().longitude,
						deliveryAddress.getLatLng().latitude + "," + deliveryAddress.getLatLng().longitude,
						RequestTypes.REQUEST_GOOGLE_DIRECTIONS_FOR_PRICE_ROUTE);
			}
		} else {
			orderPrice = 0;
			current_fragment.setOrderPriceText(0, true);
			orderDuration = -1;
			current_fragment.setDriverTimeText(-1);

			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.getGoogleDistanceMatrixFromAToB : CMApplication.getTrackingOrderId() : " + CMApplication.getTrackingOrderId());
			if (CMApplication.getTrackingOrderId().isEmpty()) {
				String subTitle = getResources().getString(R.string.os_order_confirm_subtitle);
				current_fragment.getOrderStateHelper().setSubTitle(subTitle);
			}
		}

	} // end methdo getGoogleDistanceMatrixFromAToB

    @Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		switch (requestCode) {
			case PermissionUtils.ACCESS_COARSE_LOCATION:
			case PermissionUtils.ACCESS_FINE_LOCATION:
				if (current_fragment != null) {
					current_fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
				}
				break;
			case PermissionUtils.PERMISSION_REQUEST_CAMERA:

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
						startActivityForResult(takePictureIntent, CMAppGlobals.REQUEST_CAPTURE_IMAGE);
					}
					return;
				}

				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						View view = findViewById(R.id.layout_main);
						if (view != null) {
							Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    })
                                    .show();
						}
						return;
					}
				}
				break;

			case PermissionUtils.READ_EXTERNAL_STORAGE:

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(intent, CMAppGlobals.REQUEST_LOAD_FROM_GALLERY);
					return;
				}

				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						View view = findViewById(R.id.layout_main);
						if (view != null) {
							Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    })
                                    .show();
						}
						return;
					}
				}
				break;
		}
	}

	/**
	 * Method for getting driverCurrentCoordinates
	 * @return - driver coordinates
	 */
	public DriverCoordinates getDriverCurrentCoordinates() {
		return driverCurrentCoordinates;

	} // end method getDriverCurrentCoordinates

	/**
	 * Method for getting current Order State
	 * @return - current order state
	 */
	public OrderState getCurrentOrderState() {
		return currentOrderState;

	} // end method getCurrentOrderState

	/**
	 * Method for change profile info
	 */
	public void  changeProfileInfo() {
		Bitmap imageBitmap = CMApplication
				.decodeBase64ToBitmap(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), ""));
		if (imageBitmap != null) {
			MenuHelper.getInstance().SetProfileImage(imageBitmap);
			avatarBitmap = imageBitmap;
		}
		String name = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "");
		if (!name.isEmpty()) {
			MenuHelper.getInstance().setProfileName(MainActivity.this, name);
		}
		String rewards = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
		if (!rewards.isEmpty()) {
			MenuHelper.getInstance().setRewards(MainActivity.this, rewards);
		}
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.changeProfileInfo : imageBitmap : " + imageBitmap + " : name : " + name);

	} // end method changeProfileInfo


	/**
	 *  Check is registration completed
	 *  and if not requesting for getting orders and  client info
	 */
	public void requestIfNotRegister() {
		boolean isUserLogIn = LocServicePreferences.getAppSettings().getBoolean(LocServicePreferences.Settings.IS_USER_LOG_IN.key(), false);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.requestIfNotRegister : isUserLogIn : " + isUserLogIn);

		if(!isUserLogIn) {
			String phoneStr = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), "");
			orderManager.GetOrders(phoneStr, 1);
			clientManager.GetClientInfo();
		}

	} // end method requestIfNotRegister

	/**
	 * Method or setting Default Values After Cancel Order
	 */
	public void setDefaultValuesAfterCancelOrder() {

		currentOrderState = null;
		currentOrder = null;
		isDrawDriverRoadToA = true;
		driverCurrentCoordinates = null;
		points = null;

	} // end method setDefaultValuesAfterCancelOrder

	/**
	 * Method for getting isDrawDriverRoadToA
	 * @return - draw driver road to point A
	 */
	public boolean isDrawDriverRoadToA() {
		return isDrawDriverRoadToA;

	} // end method isDrawDriverRoadToA

	public String getGeoDataJsonString(GoogleGeocode googleGeocode) {
		JSONObject jsonObject = new JSONObject();
		List<GoogleResult> googleResultList = googleGeocode.getResults();

		if (googleResultList != null && !googleResultList.isEmpty()) {
			try {
				GoogleResult googleResult = googleResultList.get(0);
				jsonObject.put("formatted_address", googleResult.getFormattedAddress());
				jsonObject.put("place_id", googleResult.getPlaceId());

				GoogleGeometry googleGeometry = googleResult.getGeometry();
				if (googleGeometry != null && googleGeometry.getLocation() != null) {
					JSONObject jsonLocation = new JSONObject();
					jsonLocation.put("lat", googleGeometry.getLocation().getLat());
					jsonLocation.put("lng", googleGeometry.getLocation().getLng());
					jsonObject.put("location", jsonLocation);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return jsonObject.toString();
	}


	@Override
	public void setCurrentActivity(Activity context) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.setCurrentActivity : context : " + context);

		// set current activity
		mCMApplication.setCurrentActivity(context);
	}

	@Override
	public void clearCurrentActivity(Activity context) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.clearCurrentActivity : context : " + context);

		Activity currActivity = mCMApplication.getCurrentActivity();
		if (this.equals(currActivity))
			mCMApplication.setCurrentActivity(null);
	}

	/**
	 * Method for getting points
	 *
	 * @return - list of points
     */
	public ArrayList<String> getPoints() {
		return points;

	} // end method getPoints

	/**
	 * Method for getting order price
	 * @return orderPrice
     */
	public int getOrderPrice() {
		return orderPrice;

	} // end method getOrderPrice

	/**
	 * Method for getting order duration
	 * @return orderDuration
	 */
	public int getOrderDuration() {
		return orderDuration;

	} // end method getOrderDuration


	/**
	 * Method for drawing on map by order state
	 *
 	 * @param order				- draw order
	 * @param driverCoordinates	- driver coordinates
	 * @param points			- route points
     */
	public void drawMapByOrderState(Order order, DriverCoordinates driverCoordinates, List<String> points) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.drawMapByOrderState : order_state : " + order.getStatus());

		if(((MapGoogleFragment) current_fragment.getMapFragment()).getmMap() != null && order != null) {
			switch (OrderState.getStateByStatus(order.getStatus())) {
				case R:
				case ASAP_R:
				case LATER_R:

//					((MapGoogleFragment)current_fragment.getMapFragment()).getmMap().clear();

					((MapGoogleFragment)current_fragment.getMapFragment()).removeMarkerA();

					((MapGoogleFragment)current_fragment.getMapFragment()).setDirection_increment(0);
					if(order.getLatitude() != null && !order.getLatitude().equals("")
						&& order.getLongitude() != null && !order.getLongitude().equals("")) {

						// set Map padding
						MapHelper.setMapPadding(MainActivity.this, current_fragment, false);

						// draw Marker A & Center it
						((MapGoogleFragment)current_fragment.getMapFragment()).drawMarkerA(Double.parseDouble(order.getLatitude()),
								Double.parseDouble(order.getLongitude()), true);

					}
					break;
				case LATER_A:
					((MapGoogleFragment)current_fragment.getMapFragment()).getmMap().clear();
					((MapGoogleFragment)current_fragment.getMapFragment()).setDirection_increment(0);
					if(order.getLatitude() != null && !order.getLatitude().equals("")
							&& order.getLongitude() != null && !order.getLongitude().equals("")) {

						// set Map padding
						MapHelper.setMapPadding(MainActivity.this, current_fragment, false);

						// draw Marker A & Center it
						((MapGoogleFragment)current_fragment.getMapFragment()).drawMarkerA(Double.parseDouble(order.getLatitude()),
								Double.parseDouble(order.getLongitude()), true);

					}
					break;
				case A:
				case ASAP_A_BUSY:
					((MapGoogleFragment)current_fragment.getMapFragment()).getmMap().clear();
					((MapGoogleFragment)current_fragment.getMapFragment()).setDirection_increment(0);
					if(order.getLatitude() != null && !order.getLatitude().equals("")
							&& order.getLongitude() != null && !order.getLongitude().equals("")
							&& driverCoordinates != null) {

						// set Map padding
						MapHelper.setMapPadding(MainActivity.this, current_fragment, false);

						// draw marker A
						((MapGoogleFragment)current_fragment.getMapFragment()).drawMarkerA(Double.parseDouble(order.getLatitude()),
								Double.parseDouble(order.getLongitude()), false);
						// draw marker Driver
						((MapGoogleFragment)current_fragment.getMapFragment()).drawDriver(driverCoordinates, false);
						// center map
						((MapGoogleFragment)current_fragment.getMapFragment()).centerMapByLocations(
								new LatLng(driverCoordinates.getLatitude(), driverCoordinates.getLongitude()),
								new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitude())));
					}
					break;
				case ASAP_A_THIS:
					// set Map padding
					MapHelper.setMapPadding(MainActivity.this, current_fragment, false);

					if(points != null) {
						// draw route
						((MapGoogleFragment)current_fragment.getMapFragment()).drawDirection(points, true);
					}
					if(order.getLatitude() != null && !order.getLatitude().equals("")
							&& order.getLongitude() != null && !order.getLongitude().equals("")) {
						((MapGoogleFragment)current_fragment.getMapFragment()).removeMarkerA();
						// draw marker A
						((MapGoogleFragment)current_fragment.getMapFragment()).drawMarkerA(Double.parseDouble(order.getLatitude()),
								Double.parseDouble(order.getLongitude()), false);
					}
					if(driverCoordinates != null) {
						// remove marker Driver
						((MapGoogleFragment)current_fragment.getMapFragment()).removeMarkerDriver();
						// draw marker Driver
						((MapGoogleFragment)current_fragment.getMapFragment()).drawDriver(driverCoordinates, false);
					}
					break;
				case RC:
					((MapGoogleFragment)current_fragment.getMapFragment()).getmMap().clear();
					((MapGoogleFragment)current_fragment.getMapFragment()).setDirection_increment(0);
					// set Map padding
					MapHelper.setMapPadding(MainActivity.this, current_fragment, false);
					// draw marker A
					((MapGoogleFragment)current_fragment.getMapFragment()).drawMarkerA(Double.parseDouble(order.getLatitude()),
							Double.parseDouble(order.getLongitude()), false);
					if(driverCoordinates != null) {
						((MapGoogleFragment)current_fragment.getMapFragment()).drawDriver(driverCoordinates, true);
					}
					break;
				case OW:
					// set Map padding
					MapHelper.setMapPadding(MainActivity.this, current_fragment, false);

					if(points != null) {
						// draw route
						((MapGoogleFragment)current_fragment.getMapFragment()).drawDirection(points, true);
					}
					if(order.getLatitude() != null && !order.getLatitude().equals("")
							&& order.getLongitude() != null && !order.getLongitude().equals("")) {
						((MapGoogleFragment)current_fragment.getMapFragment()).removeMarkerA();
						// draw marker A
						((MapGoogleFragment)current_fragment.getMapFragment()).drawMarkerA(Double.parseDouble(order.getLatitude()),
								Double.parseDouble(order.getLongitude()), false);
					}
					if(driverCoordinates != null) {
						if(order.getDelLatitude() != null
							&& !order.getDelLatitude().equals("")
							&& order.getDelLongitude() != null
							&& !order.getDelLongitude().equals("")) {
							((MapGoogleFragment)current_fragment.getMapFragment()).removeMarkerDriver();
							((MapGoogleFragment)current_fragment.getMapFragment()).removeMarkerB();
							// draw marker B
							((MapGoogleFragment)current_fragment.getMapFragment()).drawMarkerB(Double.parseDouble(order.getDelLatitude()),
									Double.parseDouble(order.getDelLongitude()));
							((MapGoogleFragment)current_fragment.getMapFragment()).drawDriver(driverCoordinates, false);

						} else {
							// remove marker Driver
							((MapGoogleFragment)current_fragment.getMapFragment()).removeMarkerDriver();
							// draw marker Driver
							((MapGoogleFragment)current_fragment.getMapFragment()).drawDriver(driverCoordinates, true);
						}
					}

					break;

			}
		}

	} // end method drawMapByOrderState

	/**
	 * Method for checking point in Polygon or not
	 * @param priorityPolygon - high priority polygon type
	 * @param currentLatLng - current lat lng
	 * @return - polygon item if point in polygon
     */
	public PolygonItem isPointInPolygon(PolygonType priorityPolygon, LatLng currentLatLng) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.isPointInPolygon : currentLatLng : " + currentLatLng);

		List<PolygonItem> findItems = new ArrayList<>();
		if(polygonItems != null && polygonItems.size() > 0) {
			for (PolygonItem item :
					polygonItems) {
				if (PolygonHelper.isPointInPolygon(currentLatLng, item.getPolygonBounds())) {
					findItems.add(item);
				}
			}
		}

		if(findItems.size() > 0) {
			for (PolygonItem item :
					findItems) {
				if(item.getPolygonType() == priorityPolygon)
					return item;
			}
			return findItems.get(0);
		}

		return null;

	} // end method isPointInPolygon

	/**
	 * Method for canceling order status service
	 */
	public void stopOrderStatusService() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MainActivity.stopOrderStatusService ");
		// STOP SERVICE
		OrderStatusReceiver.cancelAlarms(getApplicationContext());

	} // end method stopOrderStatusService


	/**
	 * Method for showing alert dialog
	 * if user changed text's size from settings
	 */
	public void showLargeTextDialog() {
		float fontScale = getResources().getConfiguration().fontScale;
		boolean isShownDialog = LocServicePreferences.getAppSettings().getBoolean(LocServicePreferences.Settings.IS_LARGE_TEXT_DIALOG_SHOWN.key(), false);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.showLargeTextDialog"
				+ " : fontScale : " + fontScale
				+ " : isShownDialog : " + isShownDialog
		);
		if (fontScale > 1 && !isShownDialog) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
			builder.setMessage(getString(R.string.alert_large_text));
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.IS_LARGE_TEXT_DIALOG_SHOWN, true);
					dialog.dismiss();
				}
			});
			builder.create().show();
		}

	} // showLargeTextDialog

}
