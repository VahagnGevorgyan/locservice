package com.locservice.ui.fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.entities.Driver;
import com.locservice.api.entities.DriverCoordinates;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.Tariff;
import com.locservice.api.entities.TariffService;
import com.locservice.api.request.GetDriversRequest;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.service.GoogleAddressIntentService;
import com.locservice.service.GoogleAddressResultReceiver;
import com.locservice.ui.CommonActivity;
import com.locservice.ui.LocationBaseActivity;
import com.locservice.ui.MainActivity;
import com.locservice.ui.OrderInfoActivity;
import com.locservice.ui.controls.AutoResizeTextView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.helpers.MapHelper;
import com.locservice.ui.helpers.OrderState;
import com.locservice.ui.helpers.PolygonHelper;
import com.locservice.ui.helpers.PolygonItem;
import com.locservice.ui.helpers.PolygonType;
import com.locservice.ui.utils.ActivityTypes;
import com.locservice.ui.utils.ScrollType;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.PermissionUtils;
import com.locservice.utils.Utils;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.directions.route.Segment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MapGoogleFragment extends MapDriverFragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraChangeListener,
        View.OnClickListener,
        GoogleAddressResultReceiver.OnReceiveResultListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapGoogleFragment.class.getSimpleName();
    private Marker markerA;
    private Marker markerB;
    private Marker markerDriver;
    private LatLng currentLatLng;
    private TouchableWrapper mTouchView;
    private View touchHelperView;

    public GoogleMap getmMap() {
        return mMap;
    }

    private GoogleMap mMap;
    private FragmentActivity mContext;
    private LatLng myLatLng;
    private ImageButton imageButtonLocation;
    private GoogleAddressResultReceiver mResultReceiver = new GoogleAddressResultReceiver(new Handler());
    private GooglePlace currentGoogleAddress;

    public RelativeLayout getLayoutSectionDriver() {
        return layoutSectionDriver;
    }

    public LinearLayout getDriverRealSection(RelativeLayout layoutDriver) {
        return (LinearLayout) layoutDriver.findViewById(R.id.layoutSectionDriverInfo);
    }

    private RelativeLayout layoutSectionDriver;
    private ImageView imageViewLocationMarker;

    private boolean from_address = false;
    private View rootView;
    private TextView textViewLocationMarker;
    private String minTextShort = "";
    private ProgressBar progressBarMarker;
    private Handler waitHandler;
    public Runnable waitRunnable;
    private AlertDialog mLocationErrorDialog;
    private boolean  isAnimateDriverPosition = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapGoogleFragment.onCreateView ");

        rootView = inflater.inflate(R.layout.fragment_map_google, container, false);
        mContext = getActivity();

        imageButtonLocation = (ImageButton) rootView.findViewById(R.id.imageButtonLocation);

        minTextShort = mContext.getResources().getString(R.string.str_minute_short);
        waitHandler = new Handler();

        imageViewLocationMarker = (ImageView) rootView.findViewById(R.id.imageViewLocationMarker);
        progressBarMarker = (ProgressBar) rootView.findViewById(R.id.progressBarMarker);
        textViewLocationMarker = (TextView) rootView.findViewById(R.id.textViewLocationMarker);

        float fontScale = mContext.getResources().getConfiguration().fontScale;
        textViewLocationMarker.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                mContext.getResources().getInteger(R.integer.int_location_marker_text_size) / fontScale);

        ImageButton imageButtonZoom = (ImageButton) rootView.findViewById(R.id.imageButtonZoom);
        imageButtonLocation.setOnClickListener(this);
        imageButtonZoom.setOnClickListener(this);

        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(rootView);



        return mTouchView;

    } // end method onCreateView

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapGoogleFragment.onActivityResult ");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    } // end method onActivityCreated

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapGoogleFragment.onMapReady ");
        mMap = googleMap;
        if (mMap != null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);

            mMap.setOnMapClickListener(this);
            mMap.setOnCameraChangeListener(this);
            mMap.setOnMarkerClickListener(this);

            if (!PermissionUtils.ensurePermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION, PermissionUtils.ACCESS_FINE_LOCATION)
                    && !PermissionUtils.ensurePermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION, PermissionUtils.ACCESS_COARSE_LOCATION)) {
                return;
            }

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (!isLocationServiceEnabled()
                    && LocServicePreferences.getAppSettings().getBoolean(LocServicePreferences.Settings.IS_SHOW_ENABLE_LOCATION.key(), true)) {
                showEnableLocation();
            }
            mMap.setMyLocationEnabled(true);

            if (mContext instanceof OrderInfoActivity) {
                if(mContext != null && ((OrderInfoActivity) mContext).getCurrent_fragment() != null) {
                    ((OrderInfoFragment) ((OrderInfoActivity) mContext).getCurrent_fragment()).showPinsOnMap();
                    showOrHideLocationIcon(false);
                    imageButtonLocation.setVisibility(View.INVISIBLE);
                }
            }

            // PINCH ZOOMING on Map
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            final View mMapView = mapFragment.getView();
            touchHelperView = rootView.findViewById(R.id.touchHelperView);
            touchHelperView.setOnTouchListener(new View.OnTouchListener() {
                float scaleFactor = 1f;

                @Override
                public boolean onTouch(final View view, final MotionEvent motionEvent) {
                    if (simpleGestureDetector.onTouchEvent(motionEvent)) { // Double tap
                        mMap.animateCamera(CameraUpdateFactory.zoomIn()); // Fixed zoom in
                    } else if (motionEvent.getPointerCount() == 1) { // Single tap
                        if (mMapView != null) {
                            mMapView.dispatchTouchEvent(motionEvent); // Propagate the event to the map (Pan)
                        }
                    } else if (scaleGestureDetector.onTouchEvent(motionEvent)) { // Pinch zoom
                        mMap.moveCamera(CameraUpdateFactory.zoomBy( // Zoom the map without panning it
                                (mMap.getCameraPosition().zoom * scaleFactor
                                        - mMap.getCameraPosition().zoom) / 5));
                    }

                    return true; // Consume all the gestures
                }

                // Gesture detector to manage double tap gestures
                private GestureDetector simpleGestureDetector = new GestureDetector(
                        mContext, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        return true;
                    }
                });

                // Gesture detector to manage scale gestures
                private ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(
                        mContext, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scaleFactor = detector.getScaleFactor();
                        return true;
                    }
                });
            });
        }

    } // end method onMapReady

    @Nullable
    @Override
    public View getView() {
        return rootView;
    }

    private boolean mMapIsTouched = false;

    public void setmMapIsTouched(boolean mMapIsTouched) {
        this.mMapIsTouched = mMapIsTouched;
    }

    private class TouchableWrapper extends RelativeLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment : TouchableWrapper.dispatchTouchEvent : ACTION_DOWN : mMapIsTouched : " + mMapIsTouched);

                    if(mContext instanceof MainActivity
                            && ((MainActivity) getActivity()).getCurrentOrder() != null
                            && ((MainActivity) getActivity()).getCurrentOrder().getStatus() != null
                            && (OrderState.getStateByStatus(((MainActivity) getActivity()).getCurrentOrder().getStatus()) == OrderState.A
                            || OrderState.getStateByStatus(((MainActivity) getActivity()).getCurrentOrder().getStatus()) == OrderState.ASAP_A_THIS
                            || OrderState.getStateByStatus(((MainActivity) getActivity()).getCurrentOrder().getStatus()) == OrderState.ASAP_A_BUSY
                            || OrderState.getStateByStatus(((MainActivity) getActivity()).getCurrentOrder().getStatus()) == OrderState.LATER_A
                            || OrderState.getStateByStatus(((MainActivity) getActivity()).getCurrentOrder().getStatus()) == OrderState.RC
                            || OrderState.getStateByStatus(((MainActivity) getActivity()).getCurrentOrder().getStatus()) == OrderState.OW)) {
                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment : TouchableWrapper.dispatchTouchEvent : ACTION_DOWN : status : " + ((MainActivity) getActivity()).getCurrentOrder().getStatus());
                        mMapIsTouched = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment : TouchableWrapper.dispatchTouchEvent : ACTION_UP : mMapIsTouched : " + mMapIsTouched);
                    break;
            }

            return super.dispatchTouchEvent(ev);

        }

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (Utils.checkNetworkStatus(mContext)) {

            currentLatLng = cameraPosition.target;
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapGoogleFragment.onCameraChange : currentLatLng : lat : "
                        + currentLatLng.latitude + " : lng : " + currentLatLng.longitude);

            if (CMAppGlobals.DEBUG && mContext instanceof MainActivity)
                Logger.i(TAG, ":: MapGoogleFragment.onCameraChange : currentOrderState : " + ((MainActivity) mContext).getCurrentOrderState());

            if (mContext instanceof MainActivity) {

                if (((MainActivity) mContext).getDriverCurrentCoordinates() != null) {
                    isAnimateDriverPosition = false;
                }

                if (((MainActivity) mContext).getCurrentOrderState() == null) {
                    // POST_DELAYED for waiting GOOGLE matrix request
                    if (waitRunnable != null)
                        waitHandler.removeCallbacks(waitRunnable);
                    waitRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (progressBarMarker.getVisibility() == View.GONE)
                                progressBarMarker.setVisibility(View.VISIBLE);
                            if (textViewLocationMarker != null)
                                textViewLocationMarker.setText("");
                            // get current CITY TARIFFS
                            ((MainActivity) mContext).getCurrentCityTariffs(currentLatLng);
                            // CHECK IF POINT IN AIRPORT
                            PolygonItem polygonItem = ((MainActivity) mContext).isPointInPolygon(PolygonType.TERMINAL, currentLatLng);
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.onCameraChange : polygonItem : " + polygonItem);
                            if(polygonItem != null) {
                                setCurrentGoogleAddress(polygonItem, currentLatLng);
                            } else {
                                startGoogleAddressIntentService(currentLatLng);
                            }
                        }
                    };
                    waitHandler.postDelayed(waitRunnable, 700);
                }
            }
        }

    } // end method onCameraChange

    /**
     * Method for getting current address
     *    by camera target position.
     */
    public void getAddressByCameraPosition() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.getAddressByCameraPosition : currentLatLng : " + currentLatLng);

        startGoogleAddressIntentService(currentLatLng);

    } // end method getAddressByCameraPosition

    @Override
    public void setMyLocation(double latitude, double longitude) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.setMyLocation : lat : " + latitude + " : lng : " + longitude);

        if (myLatLng == null) {
            myLatLng = new LatLng(latitude, longitude);
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, CMAppGlobals.DEFAULT_ZOOM_LEVEL));
            }
        } else {
            myLatLng = new LatLng(latitude, longitude);
        }

    } // end method setMyLocation

    @Override
    public void setDriverUI(final DriverInfo driverInfo, boolean show) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.setDriverUI : " + driverInfo + " : show : " + show);

        // driver section bar
        layoutSectionDriver = (RelativeLayout) mContext.findViewById(R.id.layoutSectionDriver);
        if (layoutSectionDriver != null) {
            if (show) {
                layoutSectionDriver.setVisibility(View.VISIBLE);
                // set driver information
                if (driverInfo.getPhotoLink() != null && !driverInfo.getPhotoLink().isEmpty()) {
                    ImageView imageViewDriver = (ImageView) layoutSectionDriver.findViewById(R.id.imageViewDriver);
                    ImageHelper.loadImagesByLoader(mContext, driverInfo.getPhotoLink(), imageViewDriver);
                }
                if (driverInfo.getName() != null && !driverInfo.getName().isEmpty()) {
                    CustomTextView textViewDriver = (CustomTextView) layoutSectionDriver.findViewById(R.id.textViewDriver);
                    textViewDriver.setText(driverInfo.getName());
                }
                String car = "";
                AutoResizeTextView textViewCar = (AutoResizeTextView) layoutSectionDriver.findViewById(R.id.textViewCar);
                if (driverInfo.getCarMark() != null && !driverInfo.getCarMark().isEmpty()) {
                    car = driverInfo.getCarMark() + " ";
                    textViewCar.setText(car);
                }
                if (driverInfo.getCarModel() != null && !driverInfo.getCarModel().isEmpty()) {
                    car += driverInfo.getCarModel();
                    textViewCar.setText(car);
                }
                CustomTextView textViewDriverRate = (CustomTextView) layoutSectionDriver.findViewById(R.id.textViewDriverRate);
                String driverRate = driverInfo.getRate() + "";
                textViewDriverRate.setText(driverRate);
                String carColor = "";
                if (driverInfo.getCarColor() != null && !driverInfo.getCarColor().isEmpty()) {
                    AutoResizeTextView textViewCarColor = (AutoResizeTextView) layoutSectionDriver.findViewById(R.id.textViewCarColor);
                    carColor = driverInfo.getCarColor();
                    textViewCarColor.setText(carColor);
                }

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.setDriverUI : getCarColorCode : " + driverInfo.getCarColorCode());
                if(driverInfo.getCarColorCode() != null && !driverInfo.getCarColorCode().isEmpty()) {
                    ImageView imageViewDriverCar = (ImageView) layoutSectionDriver.findViewById(R.id.imageViewDriverCar);

                    String imgStr = getDriverIconNameForProfile(driverInfo.getCarType(), driverInfo.getCarColorCode());


                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.setDriverUI : DRIVER_IMAGE : " + imgStr);
                    int drawableResourceId = mContext.getResources().getIdentifier(imgStr, "drawable", mContext.getPackageName());
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.setDriverUI : drawableResourceId : " + drawableResourceId);
                    if(drawableResourceId > 0) {
                        imageViewDriverCar.setImageDrawable(ContextCompat.getDrawable(mContext, drawableResourceId));
                    } else {
                        imageViewDriverCar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sedan_000000_driver_row));
                    }
                }

                if (driverInfo.getCarNumber() != null && !driverInfo.getCarNumber().isEmpty()) {
                    CustomTextView textViewCarNumber = (CustomTextView) layoutSectionDriver.findViewById(R.id.textViewCarNumber);
                    CustomTextView textViewCarNumberReg = (CustomTextView) layoutSectionDriver.findViewById(R.id.textViewCarNumberReg);
                    CMApplication.setCarNumber(textViewCarNumber, textViewCarNumberReg, driverInfo);
                }
            } else {
                layoutSectionDriver.setVisibility(View.INVISIBLE);
            }
            layoutSectionDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check network status
                    if (Utils.checkNetworkStatus(mContext)) {
                        Intent intent = new Intent(mContext, CommonActivity.class);
                        intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.DRIVER);
                        intent.putExtra(CMAppGlobals.EXTRA_DRIVER_INFO, driverInfo);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    /**
     * Method for showing on map
     *
     * @param latLng - location for showing on map
     */
    public void showOnMap(LatLng latLng, boolean from_address) {
        if (latLng != null)
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapGoogleFragment.showOnMap : lat : " + latLng.latitude + " : lng : " + latLng.longitude);
        if (mMap != null && latLng != null) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapGoogleFragment.showOnMap : mMap : " + mMap + " : from_address : " + from_address);
            float zoomLevel = mMap.getCameraPosition().zoom;
            this.from_address = from_address;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CMAppGlobals.DEFAULT_ZOOM_LEVEL));
        }

    } // end method showOnMap

    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(mContext)) {
            switch (v.getId()) {
                case R.id.imageButtonLocation:
                    if (!isLocationServiceEnabled()) {
                        showEnableLocation();
                    } else {
                        // current location from Google Client
                        if (mContext instanceof LocationBaseActivity && ((LocationBaseActivity) mContext).getLocation() != null) {
                            double latitude = ((LocationBaseActivity) getActivity()).getLocation().getLatitude();
                            double longitude = ((LocationBaseActivity) getActivity()).getLocation().getLongitude();
                            myLatLng = new LatLng(latitude, longitude);
                        }
                        showOnMap(myLatLng, false);
                    }
                    break;
                case R.id.imageButtonZoom:
                    if (mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(CMAppGlobals.DEFAULT_ZOOM_LEVEL));
                    }
                    break;
            }
        }

    } // end method onClick

    @Override
    public void onStart() {
        super.onStart();
        // set receiver
        this.mResultReceiver.setListener(this);
    }

    @Override
    public void onStop() {
        // clear receiver
        this.mResultReceiver.clearListener();
        super.onStop();

    }

    @Override
    public void showControls(View view) {

    }

    @Override
    public void hideControls(View view) {

    }

    @Override
    public void showDriver(View view) {
        RelativeLayout layoutSectionDriver = (RelativeLayout) view.findViewById(R.id.layoutSectionDriver);
        if (layoutSectionDriver != null && layoutSectionDriver.getVisibility() == View.INVISIBLE) {
            layoutSectionDriver.setVisibility(View.VISIBLE);
            layoutSectionDriver.setEnabled(true);
        }
    }

    @Override
    public void hideDriver(View view) {
        RelativeLayout layoutSectionDriver = (RelativeLayout) view.findViewById(R.id.layoutSectionDriver);
        if (layoutSectionDriver != null && layoutSectionDriver.getVisibility() == View.VISIBLE) {
            layoutSectionDriver.setVisibility(View.INVISIBLE);
            layoutSectionDriver.setEnabled(false);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    /**
     * Method for starting google address service
     *
     * @param currentLatLng - current location
     */
    protected void startGoogleAddressIntentService(LatLng currentLatLng) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.startGoogleAddressIntentService : currentLatLng.lat : "
                    + currentLatLng.latitude + " : currentLatLng.lng : " + currentLatLng.longitude);
        // start google intent service
        Intent intent = new Intent(mContext, GoogleAddressIntentService.class);
        intent.putExtra(CMAppGlobals.RECEIVER, mResultReceiver);
        intent.putExtra(CMAppGlobals.LAT_LNG_DATA_EXTRA, currentLatLng);
        mContext.startService(intent);

    } // end method startGoogleAddressIntentService

    /**
     * Method for setting current google address
     * @param polygonItem  - polygon item
     * @param currentLatLng - current lat lng
     */
    public void setCurrentGoogleAddress(PolygonItem polygonItem, LatLng currentLatLng) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, "MapGoogleFragment.setCurrentGoogleAddress : polygonItem : " + polygonItem + " : currentLatLng : " + currentLatLng);

        currentGoogleAddress = new GooglePlace();
        currentGoogleAddress.setIsFavorite(((MapViewFragment) this.getParentFragment()).isFavoriteAddressClicked());
        currentGoogleAddress.setName(polygonItem.getName());
        currentGoogleAddress.setLatLng(currentLatLng);

        if (from_address) {
            from_address = false;
        } else {
            if (CMApplication.getTrackingOrderId().isEmpty()) {
                // Set Address to SlidePanelAdapter
                ((MapViewFragment) this.getParentFragment()).refreshAddressByType(getActivity(), ScrollType.SO_FROM, currentGoogleAddress, false);
            }
        }

        // Request for nearest driver
        GetDriversRequest getDriversRequest = new GetDriversRequest();
        getDriversRequest.setLatitude(currentGoogleAddress.getLatLng().latitude);
        getDriversRequest.setLongitude(currentGoogleAddress.getLatLng().longitude);
        getDriversRequest.setRadius(CMAppGlobals.NEAREST_DRIVERS_DEFAULT_RADIUS);
        getDriversRequest.setIdLocality("2");
        Tariff selectedTariff = ((MapViewFragment) this.getParentFragment()).getSelectedTariff();
        if (selectedTariff != null) {
            getDriversRequest.setTariff(Integer.parseInt(selectedTariff.getID()));
        }
        List<TariffService> tariffServices = ((MapViewFragment) this.getParentFragment()).getSelectedTariffServices();
        if (tariffServices != null && !tariffServices.isEmpty()) {
            ((MapViewFragment) this.getParentFragment()).setTariffServices(getDriversRequest, tariffServices);
        }

        if (isLowMemory(mContext)) {
            // set limit of drivers response
            getDriversRequest.setLimit(5);
        } else {
            // set limit of drivers response
            getDriversRequest.setLimit(10);
        }

        ((MainActivity) mContext).requestDriversCoordFromMap((ICallBack) mContext, getDriversRequest);

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == CMAppGlobals.SUCCESS_RESULT) {
            if (resultData != null) {
                Address address = resultData.getParcelable(CMAppGlobals.RESULT_DATA_KEY);
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapGoogleFragment.onReceiveResult : address : " + address);
                if (address != null) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapGoogleFragment.onReceiveResult : address.getAdminArea() : " + address.getAdminArea());
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapGoogleFragment.onReceiveResult : address.getLocality() : " + address.getLocality());
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapGoogleFragment.onReceiveResult : address.getSubAdminArea() : " + address.getSubAdminArea());

                    currentGoogleAddress = new GooglePlace();

                    if(address.getLocality() != null && !address.getLocality().equals("")) {
                        currentGoogleAddress.setArea(address.getLocality());
                    } else if(address.getSubAdminArea() != null && !address.getSubAdminArea().equals("")) {
                        currentGoogleAddress.setArea(address.getSubAdminArea());
                    } else if(address.getAdminArea() != null && !address.getAdminArea().equals("")) {
                        currentGoogleAddress.setArea(address.getAdminArea());
                    }

                    currentGoogleAddress.setIsFavorite(((MapViewFragment) this.getParentFragment()).isFavoriteAddressClicked());
                    if (address.getFeatureName() != null && address.getThoroughfare() != null && address.getFeatureName().equals(address.getThoroughfare()) || address.getThoroughfare() == null) {
                        currentGoogleAddress.setName(address.getFeatureName());
                    } else {
                        currentGoogleAddress.setName(address.getThoroughfare() + ", " + address.getFeatureName());
                    }

                    if (address.getUrl() != null) {
                        currentGoogleAddress.setWebsiteUri(Uri.parse(address.getUrl()));
                    }
                    currentGoogleAddress.setPhoneNumber(address.getPhone());
                    if (address.hasLatitude() && address.hasLongitude()) {
                        currentGoogleAddress.setLatLng(new LatLng(address.getLatitude(), address.getLongitude()));
                    }

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapGoogleFragment.onReceiveResult : from_address : " + from_address);
                    if (from_address) {
                        from_address = false;
                    } else {
                        if (CMApplication.getTrackingOrderId().isEmpty()) {
                            // Set Address to SlidePanelAdapter
                            ((MapViewFragment) this.getParentFragment()).refreshAddressByType(getActivity(), ScrollType.SO_FROM, currentGoogleAddress, false);
                        }
                    }

                    // Request for nearest driver
                    GetDriversRequest getDriversRequest = new GetDriversRequest();
                    getDriversRequest.setLatitude(currentGoogleAddress.getLatLng().latitude);
                    getDriversRequest.setLongitude(currentGoogleAddress.getLatLng().longitude);
                    getDriversRequest.setRadius(CMAppGlobals.NEAREST_DRIVERS_DEFAULT_RADIUS);
                    getDriversRequest.setIdLocality("2");
                    Tariff selectedTariff = ((MapViewFragment) this.getParentFragment()).getSelectedTariff();
                    if (selectedTariff != null) {
                        getDriversRequest.setTariff(Integer.parseInt(selectedTariff.getID()));
                    }
                    List<TariffService> tariffServices = ((MapViewFragment) this.getParentFragment()).getSelectedTariffServices();
                    if (tariffServices != null && !tariffServices.isEmpty()) {
                        ((MapViewFragment) this.getParentFragment()).setTariffServices(getDriversRequest, tariffServices);
                    }

//                    if(isLowMemory(mContext)) {
//                        // set limit of drivers response
//                        getDriversRequest.setLimit(5);
//                    } else {
                        // set limit of drivers response
                        getDriversRequest.setLimit(10);
//                    }

                    ((MainActivity) mContext).requestDriversCoordFromMap((ICallBack) mContext, getDriversRequest);

                }
            } else {
                String message = resultData.getString(CMAppGlobals.RESULT_MESSAGE_KEY);
                if (CMAppGlobals.DEBUG)
                    Logger.e(TAG, ":: MapGoogleFragment.onReceiveResult : Message : " + message);
            }
        } else if (resultCode == CMAppGlobals.FAILURE_RESULT) {
            if (CMAppGlobals.DEBUG)
                Logger.e(TAG, ":: MapGoogleFragment.onReceiveResult : FAILURE_RESULT");
        }
//        ((MapViewFragment) this.getParentFragment()).setFavoriteAddressClicked(false);

    } // end method onReceiveResult

    /**
     * Method for checking memory usage
     * @return - is low memory
     */
    public static boolean isLowMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.isLowMemory : availableMegs : " + availableMegs);
        return availableMegs < 140;

    } // end method isLowMemory

    /**
     * Method for getting current address
     *
     * @return - current google address
     */
    public GooglePlace getCurrentGoogleAddress() {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.getCurrentGoogleAddress : currentGoogleAddress : " + currentGoogleAddress);
        return currentGoogleAddress;

    } // end method getCurrentGoogleAddress

    public void drawBounds(List<LatLng> bounds, LatLng center, LatLng centerLatLng) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.drawBounds : bounds : " + bounds);

        PolylineOptions options = new PolylineOptions();
        options.color(Color.parseColor("#CC0000FF"));
        options.width(5);
        options.visible(true);
        options.addAll(bounds);

        mMap.addPolyline(options);

        if(center != null && centerLatLng != null) {
            MarkerOptions icon = new MarkerOptions()
                    .position(new LatLng(center.latitude, center.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_point_b));
            mMap.addMarker(icon);

            MarkerOptions iconNew = new MarkerOptions()
                    .position(new LatLng(centerLatLng.latitude, centerLatLng.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_point_end));
            mMap.addMarker(iconNew);
        }
    }

    /**
     * Method for drawing drivers on map
     *
     * @param driversList - list of drivers
     */
    @Override
    public void drawDriversOnMap(List<Driver> driversList) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.drawDriversOnMap : driversList : " + driversList);

        if (mMap != null) {
            mMap.clear();
            for (final Driver driver : driversList) {
                Handler handler = new Handler();
                double lat = 0;
                double lng = 0;
                if (driver.getLt() != null && !driver.getLt().isEmpty()) {
                    lat = Double.parseDouble(driver.getLt());
                }
                if (driver.getLn() != null && !driver.getLn().isEmpty()) {
                    lng = Double.parseDouble(driver.getLn());
                }
                if (lat != 0 && lng != 0 && driver.getId() != null) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapGoogleFragment.drawDriversOnMap : driver_id : " + driver.getId() + " : color : " + driver.getCarColorCode());

                    final double finalLat = lat;
                    final double finalLng = lng;
                    if(driver.getCarColorCode() != null
                            && !driver.getCarColorCode().equals("")) {

                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDriversOnMap"
                                + " : driver.getCarType() : " + driver.getCarType()
                                + " : driver.getCarColorCode() : " + driver.getCarColorCode()
                        );
                        String imgStr = getDriverIconNameForMap(driver.getCarType(), driver.getCarColorCode());

                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDriversOnMap : DRIVER_IMAGE : " + imgStr);
                        final int drawableResourceId = mContext.getResources().getIdentifier(imgStr, "drawable", mContext.getPackageName());
                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDriversOnMap : drawableResourceId : " + drawableResourceId);

                        if(drawableResourceId > 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // draw driver marker
                                    markerDriver = drawMarkerOnMap(new LatLng(finalLat, finalLng), drawableResourceId,
                                            45 * (driver.getDirection() > 0 ? driver.getDirection() - 1 : 0));
                                }
                            });
                        } else {
                            markerDriver = drawMarkerOnMap(new LatLng(lat, lng), R.drawable.sedan_000000_map,
                                    45 * (driver.getDirection() > 0 ? driver.getDirection() - 1 : 0));
                        }
                    } else {
                        markerDriver = drawMarkerOnMap(new LatLng(lat, lng), R.drawable.sedan_000000_map,
                                45 * (driver.getDirection() > 0 ? driver.getDirection() - 1 : 0));
                    }
                }
            }
        }

    } // end method drawDriversOnMap

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    /**
     * Method for moving map to position
     *
     * @param lat - latitude of position
     * @param lng -
     */
    @Override
    public void moveMapPosition(double lat, double lng) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.moveMapPosition : lat : " + lat + " : lng : " + lng);

        if (mMap != null) {
            LatLng latLng = new LatLng(lat, lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CMAppGlobals.DEFAULT_ZOOM_LEVEL));
        }

    } // end method moveMapPosition


    public void setDirection_increment(int direction_increment) {
        this.direction_increment = direction_increment;
    }

    public int direction_increment = 0;

    /**
     * Method for drawing direction using points
     *
     * @param points - route points
     */
    public void drawDirection(List<String> points, boolean zoom) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDirection : zoom : " + zoom + " : points : " + points);

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDirection : direction_increment : " + direction_increment);
        if(direction_increment == 0) {
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDirection : direction_increment : " + direction_increment);
            direction_increment = 6;
            if (mMap != null) {
                mMap.clear();
                if (points != null && points.size() > 1 && !CMApplication.getTrackingOrderId().isEmpty()) {

                    PolylineOptions options = new PolylineOptions();
                    options.color(Color.parseColor("#CC0000FF"));
                    options.width(5);
                    options.visible(true);
                    List<LatLng> latLngList = new ArrayList<>();
                    for (String item : points) {
                        List<LatLng> latLngs = PolygonHelper.decodePolylinePoints(item);
                        latLngList.addAll(latLngs);
                        options.addAll(latLngs);
                    }

                    mMap.addPolyline(options);

                    if (zoom) {
                        // Update camera for showing road on map
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(latLngList.get(0));
                        builder.include(latLngList.get(latLngList.size() - 1));
                        LatLngBounds bounds = builder.build();
                        int padding = CMApplication.dpToPx(50); // offset from edges of the map in pixels
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.moveCamera(cameraUpdate);
                    }
                }
            }
        } else {
            direction_increment--;
        }

    } // end method drawDirection


    /**
     * Method for drawing driver marker on map
     *
     * @param driverCoordinates - driver coordinates
     * @param isAnimateDriverPosition - animate driver position
     */
    public void drawDriver(DriverCoordinates driverCoordinates, boolean isAnimateDriverPosition) {
        if (driverCoordinates != null) {
            if(((MainActivity) mContext).getCurrent_driver_info() != null
                    && ((MainActivity) mContext).getCurrent_driver_info().getCarColorCode() != null
                    && !(((MainActivity) mContext).getCurrent_driver_info().getCarColorCode()).equals("")) {


//                String lowerStr = ((MainActivity) mContext).getCurrent_driver_info().getCarColorCode().toLowerCase();
//                String imgStr = "c_" + lowerStr + "_map";

                DriverInfo current_driver_info = ((MainActivity) mContext).getCurrent_driver_info();
                String imgStr = getDriverIconNameForMap(current_driver_info.getCarType(), current_driver_info.getCarColorCode());

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDriver : DRIVER_IMAGE : " + imgStr);
                int drawableResourceId = mContext.getResources().getIdentifier(imgStr, "drawable", mContext.getPackageName());
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawDriver : drawableResourceId : " + drawableResourceId);
                if(drawableResourceId > 0) {
                    // draw driver marker
                    markerDriver = drawMarkerOnMap(new LatLng(driverCoordinates.getLatitude(), driverCoordinates.getLongitude()), drawableResourceId,
                            45 * (driverCoordinates.getDirection() > 0 ? driverCoordinates.getDirection() - 1 : 0));
                } else {
                    markerDriver = drawMarkerOnMap(new LatLng(driverCoordinates.getLatitude(), driverCoordinates.getLongitude()), R.drawable.sedan_000000_map,
                            45 * (driverCoordinates.getDirection() > 0 ? driverCoordinates.getDirection() - 1 : 0));
                }
            } else {
                markerDriver = drawMarkerOnMap(new LatLng(driverCoordinates.getLatitude(), driverCoordinates.getLongitude()), R.drawable.sedan_000000_map,
                        45 * (driverCoordinates.getDirection() > 0 ? driverCoordinates.getDirection() - 1 : 0));
            }
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapGoogleFragment.drawDriver : isAnimateDriverPosition : " + isAnimateDriverPosition);

            if(CMAppGlobals.DEBUG)Logger.i(TAG, "::MapGoogleFragment.drawDriver : mMapIsTouched : " + mMapIsTouched);
            // animate
            if (isAnimateDriverPosition && !mMapIsTouched) {
                float zoomLevel = mMap.getCameraPosition().zoom;
                LatLng latLng = new LatLng(driverCoordinates.getLatitude(), driverCoordinates.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            }
        }
    } // end method drawDriver

    /**
     * Method for drawing marker A
     *
     * @param lat - marker latitude
     * @param lng - marker longitude
     * @param isAnimate - is animate to marker
     */
    public void drawMarkerA(double lat, double lng, boolean isAnimate) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawMarkerA : lat : "
                + lat + " : lng : " + lng + " : isAnimate : " + isAnimate);

        // draw marker A
        markerA = drawMarkerOnMap(new LatLng(lat, lng), R.drawable.ic_loc, 0);

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawMarkerA : mMapIsTouched : " + mMapIsTouched);
        // animate
        if(isAnimate && !mMapIsTouched) {
            // move
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), CMAppGlobals.DEFAULT_ZOOM_LEVEL));
        }

    } // end method drawMarkerA

    public void removeMarkerA() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.removeMarkerA ");
        if(markerA != null)
            markerA.remove();
    }

    public void removeMarkerDriver() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.removeMarkerDriver ");
        if(markerDriver != null)
            markerDriver.remove();
    }

    public void removeMarkerB() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.removeMarkerB ");
        if(markerB != null)
            markerB.remove();
    }

    /**
     * Method for drawing marker B
     * @param lat - marker latitude
     * @param lng - marker longitude
     */
    public void drawMarkerB(double lat, double lng) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.drawMarkerB : lat : " + lat + " : lng : " + lng);

        markerB = drawMarkerOnMap(new LatLng(lat, lng), R.drawable.ic_point_b, 0);

    } // end method drawMarkerB

    /**
     * Method for centering Map by start & end points
     *
     * @param startLatLng   - start point
     * @param endLatLng     - end point
     */
    public void centerMapByLocations(LatLng startLatLng, LatLng endLatLng) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.centerMapByLocations : "
                + "start_lat : " + startLatLng.latitude + " : start_lng : " + startLatLng.longitude
                + "end_lat : " + endLatLng.latitude + " : end_lng : " + endLatLng.longitude);
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.centerMapByLocations : mMapIsTouched : " + mMapIsTouched);

        if(!mMapIsTouched) {
            // set bounds
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startLatLng);
            builder.include(endLatLng);
            LatLngBounds bounds = builder.build();
            int padding = CMApplication.dpToPx(50); // offset from edges of the map in pixels
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cameraUpdate);
        }

    } // end method centerMapByLocations


    @Override
    public void drawRoadDirections(List<String> points, boolean isAddMarkers, boolean zoom, boolean drawRoad) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.drawRoadDirections : directions : " + points);
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.drawRoadDirections : mMap : " + mMap);

        if (mMap != null) {

            mMap.clear();

            if (points != null && points.size() > 1 && !CMApplication.getTrackingOrderId().isEmpty()) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapGoogleFragment.drawRoadDirections : size : " + points.size());

                PolylineOptions options = new PolylineOptions();
                options.color(Color.parseColor("#CC0000FF"));
                options.width(5);
                options.visible(true);
                List<LatLng> latLngList = new ArrayList<>();
                for (String item : points) {
                    List<LatLng> latLngs = PolygonHelper.decodePolylinePoints(item);
                    latLngList.addAll(latLngs);
                    options.addAll(latLngs);
                }

                if(drawRoad)
                    mMap.addPolyline(options);

                if (zoom) {

                    // Update camera for showing road on map
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(latLngList.get(0));
                    builder.include(latLngList.get(latLngList.size() - 1));
                    LatLngBounds bounds = builder.build();
                    int padding = CMApplication.dpToPx(50); // offset from edges of the map in pixels
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cameraUpdate);

                    // MOVE MAP TO NEW CENTER
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment :: OLD_CENTER : lat : "
                            + mMap.getCameraPosition().target.latitude + " : lng : " + mMap.getCameraPosition().target.longitude);
                    LatLng latLng = MapHelper.calcMapCenter(mContext,
                            MapGoogleFragment.this,
                            ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).getLayoutTop());
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment :: NEW_CENTER : lat : " + latLng.latitude + " : lng : " + latLng.longitude);

                    CameraUpdate newCenterUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom);
                    mMap.moveCamera(newCenterUpdate);

                    // get map visibility bounds
                    HashMap<String, Point> mapVisibleBounds = MapHelper.getMapVisibleBounds(mContext,
                            MapGoogleFragment.this,
                            ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).getLayoutTop());

                    Projection projection = mMap.getProjection();

                    // Returns the geographic location that corresponds to a screen location
                    LatLng neLatLng = projection.fromScreenLocation(mapVisibleBounds.get("ne"));
                    LatLng swLatLng = projection.fromScreenLocation(mapVisibleBounds.get("sw"));

                    final LatLngBounds vLatLngBounds = new LatLngBounds(swLatLng, neLatLng);

                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment : visibleLatLngBounds "
                            + " : vLatLngBounds.northeast.latitude : " + vLatLngBounds.northeast.latitude
                            + " : vLatLngBounds.northeast.longitude : " + vLatLngBounds.northeast.longitude
                            + " : vLatLngBounds.southwest.latitude : " + vLatLngBounds.southwest.latitude
                            + " : vLatLngBounds.southwest.longitude : " + vLatLngBounds.southwest.longitude);

                    float current_zoom = mMap.getCameraPosition().zoom;
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment : latLngList.size() : " + latLngList.size());
                    LatLng startLatLng = latLngList.get(0);
                    LatLng endLatLng = latLngList.get(latLngList.size() - 1);

                    int count = 0;
                    if(!vLatLngBounds.contains(startLatLng) && !vLatLngBounds.contains(endLatLng)) {

                        zoomMapRouteVisibility(vLatLngBounds, startLatLng, endLatLng, count, current_zoom);

                    }
                }
            }

            if (isAddMarkers && mContext instanceof MainActivity) {
                Order order = ((MainActivity) mContext).getCurrentOrder();
                DriverCoordinates driverCoordinates = ((MainActivity) mContext).getDriverCurrentCoordinates();
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapGoogleFragment.drawRoadDirections : MainActivity : order : " + order + " : driverCoordinates : " + driverCoordinates);

                if (driverCoordinates != null) {
                    drawMarkerOnMap(new LatLng(driverCoordinates.getLatitude(), driverCoordinates.getLongitude()), R.drawable.ic_car_red, 45 * (driverCoordinates.getDirection() > 0 ? driverCoordinates.getDirection() - 1 : 0));
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapGoogleFragment.drawRoadDirections : isAnimateDriverPosition : " + isAnimateDriverPosition);
                }

                if (order != null) {
                    if (order.getLatitude() != null && !order.getLatitude().isEmpty()
                            && order.getLongitude() != null && !order.getLongitude().isEmpty()) {
                        drawMarkerOnMap(new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitude())), R.drawable.ic_loc, 0);

                        // move to marker
                        if (driverCoordinates == null && zoom) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(order.getLatitude()),
                                    Double.parseDouble(order.getLongitude())), CMAppGlobals.DEFAULT_ZOOM_LEVEL));
                        }
                        if(zoom) {
                            // move map center
                            if(order.getStatus().equals(OrderState.getStatusStringByState(OrderState.R))
                                    || order.getStatus().equals(OrderState.getStatusStringByState(OrderState.ASAP_R))
                                    || order.getStatus().equals(OrderState.getStatusStringByState(OrderState.LATER_R))) {
                                // change MAP CENTER to POINT A
                                MapHelper.moveMapCenterToLocation(mContext, MapGoogleFragment.this, Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitude()));
                            }
                        }
                    }
                    if (order.getDelLatitude() != null && !order.getDelLatitude().isEmpty()
                            && order.getDelLongitude() != null && !order.getDelLongitude().isEmpty()
                            && !((MainActivity) mContext).isDrawDriverRoadToA()) {
                        drawMarkerOnMap(new LatLng(Double.parseDouble(order.getDelLatitude()), Double.parseDouble(order.getDelLongitude())), R.drawable.ic_point_b, 0);
                    }
                }

            }
            if (isAddMarkers && mContext instanceof OrderInfoActivity) {
                Order order = ((OrderInfoActivity) mContext).getOrder();
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapGoogleFragment.drawRoadDirections : OrderInfoActivity : order : " + order);
                if (order != null) {
                    if (order.getLatitude() != null && !order.getLatitude().isEmpty()
                            && order.getLongitude() != null && !order.getLongitude().isEmpty()) {
                        drawMarkerOnMap(new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitude())), R.drawable.ic_loc, 0);
                        if (points == null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(order.getLatitude()),
                                    Double.parseDouble(order.getLongitude())), CMAppGlobals.DEFAULT_ZOOM_LEVEL));
                        }
                    }
                    if (order.getDelLatitude() != null && !order.getDelLatitude().isEmpty()
                            && order.getDelLongitude() != null && !order.getDelLongitude().isEmpty()) {
                        drawMarkerOnMap(new LatLng(Double.parseDouble(order.getDelLatitude()), Double.parseDouble(order.getDelLongitude())), R.drawable.ic_point_b, 0);
                    }
                }
            }
        }

    } // end method drawRoadDirections

    /**
     * Method for zooming route on map
     *
     * @param latLngBounds  - bounds
     * @param startLatLng   - start point
     * @param endLatLng     - end point
     * @param count         - count iteration
     * @param current_zoom  - current zoom
     */
    public void zoomMapRouteVisibility(LatLngBounds latLngBounds, final LatLng startLatLng, final LatLng endLatLng, final int count, float current_zoom) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment :: zoomMapRouteVisibility : "
                + " count : " + count + " : current_zoom : " + current_zoom);

        if(!latLngBounds.contains(startLatLng)
                || !latLngBounds.contains(endLatLng)) {

            current_zoom -= 0.01f;

            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment : ZOOM_LEVEL : " + current_zoom);
            mMap.moveCamera(CameraUpdateFactory.zoomTo(current_zoom));

            final float finalCurrent_zoom = current_zoom;
            waitRunnable = new Runnable() {
                @Override
                public void run() {

                    Projection projection = mMap.getProjection();
                    // get bounds
                    HashMap<String, Point> mapVisibleBounds1 = MapHelper.getMapVisibleBounds(mContext,
                            MapGoogleFragment.this,
                            ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).getLayoutTop());
                    LatLng neLatLng1 = projection.fromScreenLocation(mapVisibleBounds1.get("ne"));
                    LatLng swLatLng1 = projection.fromScreenLocation(mapVisibleBounds1.get("sw"));

                    LatLngBounds vLatLngBounds = new LatLngBounds(swLatLng1, neLatLng1);

                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment :: visibleLatLngBounds "
                            + " : vLatLngBounds.northeast.latitude : " + vLatLngBounds.northeast.latitude
                            + " : vLatLngBounds.northeast.longitude : " + vLatLngBounds.northeast.longitude
                            + " : vLatLngBounds.southwest.latitude : " + vLatLngBounds.southwest.latitude
                            + " : vLatLngBounds.southwest.longitude : " + vLatLngBounds.southwest.longitude);

                    int iterator = count + 1;

                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment :: iterator : " + iterator);

                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment :: START "
                            + ": lat : " + startLatLng.latitude + " : lng : " + startLatLng.longitude +
                            " : END : lat : " + endLatLng.latitude + " : lng : " + endLatLng.longitude);

                    // recursive
                    zoomMapRouteVisibility(vLatLngBounds, startLatLng, endLatLng, iterator, finalCurrent_zoom);
                }
            };
            waitHandler.postDelayed(waitRunnable, 15);
        }
        else {

            LatLng centerLatLng = calcMapCenter(startLatLng, endLatLng, latLngBounds);

            CameraUpdate newCenterUpdate = CameraUpdateFactory.newLatLngZoom(centerLatLng, mMap.getCameraPosition().zoom);
            mMap.animateCamera(newCenterUpdate);
        }

    } // end method zoomMapRouteVisibility

    public LatLng calcMapCenter(LatLng startLatLng, LatLng endLatLng, LatLngBounds bounds) {
        Projection projection = mMap.getProjection();

        Point startPoint = projection.toScreenLocation(startLatLng);
        Point endPoint = projection.toScreenLocation(endLatLng);

        int mid_x = Math.abs(startPoint.x + endPoint.x)/2;
        int mid_y = Math.abs(startPoint.y + endPoint.y)/2;

        Point mid_point = new Point(mid_x, mid_y);

        Point boundsPoint = projection.toScreenLocation(bounds.getCenter());

        Point mapCenterPoint = projection.toScreenLocation(mMap.getCameraPosition().target);

        int dist = Math.abs(mid_point.y - boundsPoint.y);


        int k = CMApplication.dpToPx(0);

        Point centerPoint = new Point();

        if(mapCenterPoint.y > mid_point.y) {
            centerPoint.y = mapCenterPoint.y + (dist + k);
        } else {
            centerPoint.y = mapCenterPoint.y - (dist - k);
        }
        centerPoint.x = mapCenterPoint.x;

        return projection.fromScreenLocation(centerPoint);
    }

    @Override
    public void drawRoadDirections(final List<LatLng> track) {
        super.drawRoadDirections(track);
        if (mMap != null) {
            mMap.clear();

            if (track != null && track.size() > 1) {

                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                        .withListener(new RoutingListener() {
                            @Override
                            public void onRoutingFailure(RouteException e) {
                            }

                            @Override
                            public void onRoutingStart() {
                            }

                            @Override
                            public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                                PolylineOptions options = new PolylineOptions();
                                options.color(Color.parseColor("#CC0000FF"));
                                options.width(5);
                                options.visible(true);

                                for (Route route : arrayList) {
                                    for (Segment segment : route.getSegments()) {
                                        options.add(segment.startPoint());
                                    }
                                }

                                options.add(track.get(track.size() - 1));
                                mMap.addPolyline(options);

                                double firstLng = track.get(0).longitude;
                                double lastLng = track.get(track.size() - 1).longitude;
                                double firstLat = track.get(0).latitude;
                                double lastLat = track.get(track.size() - 1).latitude;

                                double topPaddingByLng = 0.05;
                                if (firstLng > lastLng) {
                                    firstLng = firstLng + topPaddingByLng;
                                    firstLat = firstLat + topPaddingByLng;
                                } else {
                                    lastLng = lastLng + topPaddingByLng;
                                    lastLat = lastLat + topPaddingByLng;
                                }

                                // Update camera for showing road on map
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(new LatLng(firstLat, firstLng));
                                builder.include(new LatLng(lastLat, lastLng));
                                LatLngBounds bounds = builder.build();
                                int padding = CMApplication.dpToPx(50); // offset from edges of the map in pixels
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.moveCamera(cameraUpdate);

                                drawMarkerOnMap(track.get(0), R.drawable.ic_loc, 0);
                                drawMarkerOnMap(track.get(track.size() - 1), R.drawable.ic_point_b, 0);
                            }

                            @Override
                            public void onRoutingCancelled() {

                            }
                        })
                        .waypoints(track)
                        .build();
                routing.execute();
            } else if (track != null && track.size() == 1) {
                drawMarkerOnMap(track.get(0), R.drawable.ic_loc, 0);
            }
        }
    }

    @Override
    public void setDriverTime(int time) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.setDriverTime : time : " + time);
        textViewLocationMarker = (TextView) rootView.findViewById(R.id.textViewLocationMarker);
        progressBarMarker = (ProgressBar) rootView.findViewById(R.id.progressBarMarker);
        progressBarMarker.setVisibility(View.GONE);
        // get time in minutes
        String timeString = CommonHelper.calculateTimeMin(time);
        String timeStr = timeString + "\n" + minTextShort;
        textViewLocationMarker.setText(timeStr);

    } // end method setDriverTime

    /**
     * Method for drawing marker on map
     *
     * @param latLng - coordinate of marker
     * @param drawable - drawable resource id
     */
    public Marker drawMarkerOnMap(LatLng latLng, int drawable, int rotation) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.drawMarkerOnMap : latLng : " + latLng + " : drawable : " + drawable);

        Marker marker = null;
        if (mMap != null && latLng != null) {
            double lat = latLng.latitude;
            double lng = latLng.longitude;
            marker =  mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.fromResource(drawable)));
            marker.setRotation(rotation);
        }
        return marker;

    } // end method drawMarkerOnMap

    /**
     * Method for showing location icon
     *
     * @param show - show/hide location icon
     */
    public void showOrHideLocationIcon(boolean show) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.showLocationIcon : show : " + show);

        if (show) {
            imageViewLocationMarker.setVisibility(View.VISIBLE);
            textViewLocationMarker.setVisibility(View.VISIBLE);
            imageButtonLocation.setVisibility(View.VISIBLE);
        } else {
            imageViewLocationMarker.setVisibility(View.GONE);
            progressBarMarker.setVisibility(View.GONE);
            textViewLocationMarker.setVisibility(View.GONE);
            imageButtonLocation.setVisibility(View.GONE);
        }

    } // end method showLocationIcon

    /**
     * Method for clearing map
     */
    public void clearMap() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapGoogleFragment.clearMap ");

        if (mMap != null) {
            mMap.clear();
        }

    } // end method clearMap

    /**
     * Method for requesting permission result
     * @param requestCode - request code
     * @param permissions - permissions array
     * @param grantResults - grant result array
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.onRequestPermissionsResult : requestCode : " + requestCode
                + " : permissions : " + Arrays.toString(permissions) + " : grantResults : " + Arrays.toString(grantResults));

        switch (requestCode) {
            case PermissionUtils.ACCESS_COARSE_LOCATION:
            case PermissionUtils.ACCESS_FINE_LOCATION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                    }
                    return;
                }

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED && getView() != null) {
                        View view = getView().findViewById(R.id.container_main_map);
                        Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.settings), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // check network status
                                        if (Utils.checkNetworkStatus(mContext)) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    }
                                })
                                .show();
                        return;
                    }
                }
                break;
        }

    } // end method onRequestPermissionsResult

    /**
     * Method for checking is location service enabled
     *
     * @return - is location enabled
     */
    public boolean isLocationServiceEnabled() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapGoogleFragment.isLocationServiceEnabled ");
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        return gpsEnabled || networkEnabled;

    } // end method isLocationServiceEnabled

    /**
     * Method for showing enable
     */
    private void showEnableLocation() {
        if (mLocationErrorDialog != null && mLocationErrorDialog.isShowing()) {
            mLocationErrorDialog.dismiss();
        }
        mLocationErrorDialog = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle).create();
        mLocationErrorDialog.setCanceledOnTouchOutside(false);
        mLocationErrorDialog.setCancelable(false);
        mLocationErrorDialog.setMessage(getString(R.string.location_dialog_message));
        mLocationErrorDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.settings),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
        mLocationErrorDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.IS_SHOW_ENABLE_LOCATION, false);
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        if (mMap != null)
                            mMap.setMyLocationEnabled(false);
                    }
                });
        mLocationErrorDialog.show();
    }

    /**
     * Method for getting progressBarMarker
     *
     * @return - progress bar marker
     */
    public ProgressBar getProgressBarMarker() {
        return progressBarMarker;

    } // end method getProgressBarMarker

    /**
     * Method for getting textViewLocationMarker
     *
     * @return - location marker text view
     */
    public TextView getTextViewLocationMarker() {
        return textViewLocationMarker;

    } // end method getTextViewLocationMarker

    /**
     * Method for getting driver icon name
     * @param driverType - Driver car type
     * @param colorCode - Driver car color
     * @return - Driver icon name
     */
    public String getDriverIconNameForMap(String driverType, String colorCode) {
        String lowerStr = colorCode.toLowerCase();
        String imgStr = "";
        if (driverType != null) {
            switch (driverType) {
                case "sedan":
                    imgStr = "sedan_" + lowerStr + "_map";
                    break;
                case "minivan":
                    imgStr = "minivan_" + lowerStr + "_map";
                    break;
                case "business":
                    imgStr = "business_" + lowerStr + "_map";
                    break;
                default:
                    imgStr = "sedan_" + lowerStr + "_map";
                    break;
            }
        }

        return imgStr;

    } // end method getDriverIconNameForMap

    /**
     * Method for getting driver icon name
     * @param driverType - Driver car type
     * @param colorCode - Driver car color
     * @return - Driver icon name
     */
    public String getDriverIconNameForProfile(String driverType, String colorCode) {
        String lowerStr = colorCode.toLowerCase();
        String imgStr = "";
        if (driverType != null) {
            switch (driverType) {
                case "sedan":
                    imgStr = "sedan_" + lowerStr + "_driver_row";
                    break;
                case "minivan":
                    imgStr = "minivan_" + lowerStr + "_driver_row";
                    break;
                case "business":
                    imgStr = "business_" + lowerStr + "_driver_row";
                    break;
                default:
                    imgStr = "sedan_" + lowerStr + "_driver_row";
                    break;
            }
        }

        return imgStr;

    } // end method getDriverIconNameForProfile

}
