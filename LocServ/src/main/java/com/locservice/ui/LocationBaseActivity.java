package com.locservice.ui;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.manager.DriverManager;
import com.locservice.api.request.GetDriversRequest;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.Places;


public class LocationBaseActivity extends BaseActivity implements
		ConnectionCallbacks,
		OnConnectionFailedListener,
		LocationListener {

	private static final String TAG = LocationBaseActivity.class.getSimpleName();

	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/*
	 * Constants for location update parameters
	 */
	// Milliseconds per second
	public static final int MILLISECONDS_PER_SECOND = 1000;

	// The update interval
	public static final int UPDATE_INTERVAL_IN_SECONDS = 60;

	// A fast interval ceiling
	public static final int FAST_CEILING_IN_SECONDS = 1;

	// Update interval in milliseconds
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;

	// A fast ceiling of update intervals, used when the app is visible
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
			* FAST_CEILING_IN_SECONDS;

	private Location mLocation;

	// A request to connect to Location Services
	private LocationRequest mLocationRequest;

	// Stores the current instantiation of the location client in this object
	private GoogleApiClient mGoogleApiClient;

	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	boolean mUpdatesRequested = true;

	/*
	 * Initialize the Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
				.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Note that location updates are off until the user turns them on
		mUpdatesRequested = false;

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mGoogleApiClient = new GoogleApiClient.Builder(CMApplication.getAppContext())
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.enableAutoManage(LocationBaseActivity.this, this)
				.build();
	}

	/*
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	/*
	 * Called when the Activity is going into the background. Parts of the UI
	 * may be visible, but the Activity is inactive.
	 */

	/*
	 * Called when the Activity is restarted, even before it becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/*
	 * Called when the system detects that this Activity is now visible.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LocationBaseActivity.onResume ");
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LocationBaseActivity.onPause ");
		// If the client is connected
		if (mGoogleApiClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onPause();
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 *
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			if (CMAppGlobals.DEBUG)
				Logger.i(TAG, ":: LocationBaseActivity.servicesConnected : " + getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			showErrorDialog(resultCode);
			return false;
		}
	}

	/**
	 * Invoked by the "Start Updates" button Sends a request to start location
	 * updates
	 *
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void startUpdates() {
		mUpdatesRequested = true;

		if (servicesConnected()) {
			startPeriodicUpdates();
		}
	}

	/**
	 * Invoked by the "Stop Updates" button Sends a request to remove location
	 * updates request them.
	 *
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void stopUpdates() {
		mUpdatesRequested = false;

		if (servicesConnected()) {
			stopPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {

		if (mUpdatesRequested) {
			startPeriodicUpdates();
		}
	}

	/**
	 * Method for requesting nearest drivers from server
	 * @param getDriversRequest - request object
	 */
	public void requestDriversCoord(ICallBack context, GetDriversRequest getDriversRequest) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LocationBaseActivity.requestDriversCoord : getDriversRequest : " + getDriversRequest);

		DriverManager driverManager = new DriverManager(context);
		driverManager.GetDrivers(getDriversRequest);

	} //  end method requestDriversCoord

	/**
	 * Method for requesting nearest drivers from map
	 * @param getDriversRequest - request object
	 */
	public void requestDriversCoordFromMap(ICallBack context, GetDriversRequest getDriversRequest) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LocationBaseActivity.requestDriversCoordFromMap : getDriversRequest : " + getDriversRequest);

		DriverManager driverManager = new DriverManager(context);
		driverManager.GetDriversFromMap(getDriversRequest);

	} //  end method requestDriversCoordFromMap

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {
				if (CMAppGlobals.DEBUG)
					Logger.e(TAG, ":: LocationBaseActivity : Error : " + e.toString());
				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/**
	 * Report location updates to the UI.
	 *
	 * @param location
	 *            The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (mLocation != null && mLocation.distanceTo(location) > 10) {
			mLocation = location;
		}
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (location == null) {
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 *
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

	}

	public Location getLocation() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			// Get the current location
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return null;
			}
			this.mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		}

		return this.mLocation;
	}

	protected boolean isLocationEnabled() {
		LocationManager lm = null;
		boolean result = false;
		if (lm == null)
			lm = (LocationManager) getApplicationContext().getSystemService(
					Context.LOCATION_SERVICE);
		try {
			result = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			result = result
					|| lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		return result;
	}

	protected void connect() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}

