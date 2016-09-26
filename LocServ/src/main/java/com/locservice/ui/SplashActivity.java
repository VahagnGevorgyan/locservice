package com.locservice.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.AuthData;
import com.locservice.api.entities.CheckfilesData;
import com.locservice.api.entities.City;
import com.locservice.api.entities.Country;
import com.locservice.api.entities.CountryData;
import com.locservice.api.entities.LandmarkData;
import com.locservice.api.entities.LandmarkInfo;
import com.locservice.api.entities.Tariff;
import com.locservice.api.entities.TariffData;
import com.locservice.api.manager.LandmarkManager;
import com.locservice.api.manager.PlaceManager;
import com.locservice.api.manager.RegisterManager;
import com.locservice.api.manager.TariffManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.db.AirportDBManager;
import com.locservice.db.CountryDBManager;
import com.locservice.db.LanguageDBManager;
import com.locservice.db.RailstationDBManager;
import com.locservice.db.TariffDBManager;
import com.locservice.gcm.GCMUtils;
import com.locservice.protocol.ICallBack;
import com.locservice.protocol.IWsCallBack;
import com.locservice.ui.helpers.PolygonHelper;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.locservice.ws.manager.AuthWsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;


public class SplashActivity extends LocationBaseActivity implements ICallBack, IWsCallBack {

    private static final String TAG = SplashActivity.class.getSimpleName();

    public static final String TESTFAIRY_API_TOKEN = "1635fcf98e96a0d6d3280ec6e9036d1ce1455976";

    private Class<?> mClassActivityToStart = MainActivity.class;
    private boolean noConnected = false;

    private final Handler mHandler = new Handler();
    private TariffManager tariffManager;
    private LandmarkManager landmarkManager;
    private AuthWsManager authWsManager;
    private PlaceManager placeManager;
    private CountryDBManager countryDBManager;
    private LanguageDBManager languageDBManager;
    private TariffDBManager tariffDBManager;
    private AirportDBManager airportDBManager;
    private RailstationDBManager railstationDBManager;
    private CMApplication mCMApplication;
    private RegisterManager registerManager;
    private ImageView imageViewLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCMApplication = (CMApplication)this.getApplicationContext();

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        // WS Connect
//		WSManager.getInstance().con(this);

        // SET TEST-FAIRY
//		TestFairy.begin(this, TESTFAIRY_API_TOKEN);

        // TODO SET CRASHLYTICS

        // set content view
        setContentView(R.layout.activity_splash);

        imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);

        // check network status
        if (Utils.checkNetworkStatus(this)) {

            // Check device for Play Services APK. If check succeeds, proceed
            // with GCM registration.
            if (GCMUtils.checkPlayServices(this)) {
                String regid = GCMUtils.getRegistrationId();

                if (regid.isEmpty()) {
                    GCMUtils.registerInBackground();
                }
            } else {
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onCreate : No valid Google Play Services APK found.");
            }

            // Check tariffs, cities, landmarks file last update
            languageDBManager = new LanguageDBManager(this);
            countryDBManager = new CountryDBManager(this);
            tariffManager = new TariffManager(this);
            landmarkManager = new LandmarkManager(this);
            tariffDBManager = new TariffDBManager(this);
            airportDBManager = new AirportDBManager(this);
            railstationDBManager = new RailstationDBManager(this);
            placeManager = new PlaceManager(this);

            registerManager = new RegisterManager(this);


            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                // Check user authentication
                String deviceId = CommonHelper.getDeviceId(this);
                String phone_number = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_PHONE_NUMBER.key(), "");
                if (deviceId != null && !deviceId.equals("")
                        && !phone_number.equals("")) {
                    registerManager.CheckPhone(phone_number, "updateVersion", deviceId);
                }
            }

            // download locale -- ru
            String locale = "ru";
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onCreate : locale : " + locale + " : city_date : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CITIES_DATE.key()+locale, ""));
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onCreate : locale : " + locale + " : tariffs_date : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.TARIFFS_DATE.key()+locale, ""));
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onCreate : locale : " + locale + " : landmarks_date : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.LANDMARKS_DATE.key()+locale, ""));

            if(CMApplication.DATABASE_VERSION >
                    LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.DB_VERSION.key(), 0)) {
                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.DB_VERSION, CMApplication.DATABASE_VERSION);

                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.TARIFFS_DATE, locale, "");
                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.LANDMARKS_DATE, locale, "");
                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.CITIES_DATE, locale, "");

            }

            placeManager.CheckFilesChanges(0, LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.TARIFFS_DATE.key()+locale, ""),
                    LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.LANDMARKS_DATE.key()+locale, ""),
                    LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CITIES_DATE.key()+locale, ""), locale);

            if (!LocServicePreferences.getAppSettings().getBoolean(LocServicePreferences.Settings.IS_VIDEO_LOADED.key(), false)) {
                startSubsequentActivity(3000);
            } else {
                startSubsequentActivity(500);
            }
        }
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();

    }

    /**
     * Method for clearing static activity from
     * application file
     */
    private void clearReferences(){
        Activity currActivity = mCMApplication.getCurrentActivity();
        if (this.equals(currActivity))
            mCMApplication.setCurrentActivity(null);
    }


    @Override
    public void onResume() {
        super.onResume();
        mCMApplication.setCurrentActivity(this);

        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.IS_VIDEO_LOADED, true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(noConnected) {
            super.onBackPressed();
        }
    }

    /**
     * Method for starting subsequent activity
     * @param timeOut - time delay
     */
    private void startSubsequentActivity(int timeOut) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // check if user registered
                Intent intent = new Intent(SplashActivity.this, mClassActivityToStart);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
            }
        }, timeOut);
    }

    @Override
    public void onFailure(Throwable error, int requestType) {
        Logger.e(TAG, ":: SplashActivity.onFailure : requestType : " + requestType);
        Logger.e(TAG, ":: SplashActivity.onFailure : error : " + error.getMessage());
        if(CMAppGlobals.DEBUG) Logger.e(TAG, ":: SplashActivity.onFailure : Error : " + error != null ? error.getMessage() : "");
        Toast.makeText(getApplicationContext(), "Error : " + error != null ? error.getMessage() : "", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if(response != null) {
            switch (requestType) {
                case RequestTypes.GET_COUNTRIES:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : GET_COUNTRIES : response : " + response);

                    if(!ErrorUtils.hasError(getApplicationContext(), response, "GET_COUNTRIES")) {
                        if(response instanceof CountryData) {

                            CountryData countryData = (CountryData)response;
                            int languageID = new LanguageDBManager(this).getLanguageIdByLocale(countryData.getLocale());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : GET_COUNTRIES : locale : " + countryData.getLocale() + " : language_id : " + languageID);

                            // get countries
                            List<Country> countries = countryData.getData();
                            if(countries != null && countries.size() > 0) {

                                countryDBManager.setCountries(countries,
                                        LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0), languageID);

                                String city_date = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CITIES_DATE.key()+countryData.getLocale(), "");
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : city_date : " + city_date + " : locale : " + countryData.getLocale());
                                if(!city_date.equals("")) {
                                    // request tariffs
                                    for (Country country :
                                            countries) {
                                        if(country != null && country.getCities() != null
                                                && country.getCities().size() > 0) {
                                            List<City> cities = country.getCities();
                                            for (City city :
                                                    cities) {
                                                if (city != null && city.getId().equals("2")) {
                                                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : city_date : " + city_date + " : city_id : " + city.getId());
                                                    // CheckFilesChanges for city tariffs/landmarks
                                                    placeManager.CheckFilesChanges(Integer.parseInt(city.getId()), "", "", city_date, countryData.getLocale());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));

                    break;
                case RequestTypes.CHECK_FILES_CHANGES:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : CHECK_FILES_CHANGES : response : " + response);

                    if(!ErrorUtils.hasError(getApplicationContext(), response, "CHECK_FILES_CHANGES")) {
                        if(response instanceof CheckfilesData) {
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : CHECK_FILES_CHANGES : locale : " + ((CheckfilesData)response).getLocale());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : CHECK_FILES_CHANGES : landmarks link : " + ((CheckfilesData)response).getLandmarksLink());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : CHECK_FILES_CHANGES : tariffs link : " + ((CheckfilesData)response).getTariffsLink());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : CHECK_FILES_CHANGES : cities link : " + ((CheckfilesData)response).getCitiesLink());

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
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : CHECK_FILES_CHANGES : ((CheckfilesData)response).getLandmarksLink() : " + ((CheckfilesData)response).getLandmarksLink());
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : CHECK_FILES_CHANGES : LandmarksDate : " + String.valueOf(((CheckfilesData) response).getLandmarksDate()));
                                // store Landmark date
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.LANDMARKS_DATE, ((CheckfilesData)response).getLocale(), String.valueOf(((CheckfilesData) response).getLandmarksDate()));
                                // LANDMARK LINK REQUEST
                                landmarkManager.GetLandmarkList(((CheckfilesData) response).getLandmarksLink());
                            }
                        }
                    }
                    break;
                case RequestTypes.GET_TARIFFS:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : GET_TARIFFS : response : " + response);
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : GET_TARIFFS : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));

                    if(!ErrorUtils.hasError(getApplicationContext(), response, "GET_TARIFFS")) {
                        if(response instanceof TariffData) {
                            TariffData tariffData = (TariffData)response;
                            int languageID = new LanguageDBManager(this).getLanguageIdByLocale(tariffData.getLocale());
                            int city_id = Integer.parseInt(tariffData.getId_locality());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : GET_TARIFFS : locale : " +
                                    tariffData.getLocale() + " : language_id : " + languageID + " : city_id : " + city_id);

                            // get tariffs
                            List<Tariff> tariffs = tariffData.getData();
                            if(tariffs != null && tariffs.size() > 0) {
                                // insert tariffs DB
                                tariffDBManager.setTariffs(tariffs, LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0), languageID, city_id);
                            }
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : GET_TARIFFS : END : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));

                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: mCMApplication.getCurrentActivity() : " + mCMApplication.getCurrentActivity());
                            if(mCMApplication.getCurrentActivity() != null
                                    && mCMApplication.getCurrentActivity() instanceof MainActivity) {
                                if(city_id == 2 && tariffData.getLocale().equals("ru"))
                                    ((MainActivity)mCMApplication.getCurrentActivity()).setTariffsData(tariffs, true);
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_LANDMARK_LIST:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : response : " + response);
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));

                    if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_LANDMARK_LIST")) {
                        if(response instanceof LandmarkData) {
                            LandmarkData landmarkData = (LandmarkData)response;
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : locale : " + landmarkData.getLocale());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : getIdLocality : " + landmarkData.getIdLocality());
                            int languageID = new LanguageDBManager(this).getLanguageIdByLocale(landmarkData.getLocale());
                            int city_id = landmarkData.getIdLocality();
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : locale : " +
                                    landmarkData.getLocale() + " : language_id : " + languageID + " : city_id : " + city_id);

                            // get tariffs
                            LandmarkInfo landmarkInfo = landmarkData.getData();
                            if(landmarkInfo != null) {
                                // insert airports DB
                                airportDBManager.setAirports(landmarkInfo.getAirports(), LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0), languageID, city_id);
                                // insert railstations DB
                                railstationDBManager.setRailstations(landmarkInfo.getRailstations(), LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0), languageID, city_id);

                                // Get all Airport Polygons
                                PolygonHelper.loadPolygons(SplashActivity.this, landmarkInfo.getAirports());
                            }
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_GET_LANDMARK_LIST : END : last_tr_id : " + LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.LAST_TR_ID.key(), 0));
                        }
                    }
                    break;
                case RequestTypes.REQUEST_CHECK_PHONE:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_CHECK_PHONE : response : " + response);
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_CHECK_PHONE")) {
                        if(response instanceof AuthData) {
                            AuthData authData = (AuthData) response;
                            int result = authData.getResult();
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_CHECK_PHONE : result : " + result);
                            if (authData.getResult() > 0) {

                                CMApplication.setAuthToken(authData.getAuth_token());

                                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_CHECK_PHONE : AUTH_TOKEN : " + authData.getAuth_token());
                                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SplashActivity.onSuccess : REQUEST_CHECK_PHONE : PHONE_NUMBER : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_PHONE_NUMBER.key(), ""));

                                // STORE AUTH_TOKEN in SharedPreferences
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.AUTH_TOKEN, authData.getAuth_token());
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PHONE_NUMBER, LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_PHONE_NUMBER.key(), ""));

                                // get current activity
                                if(getApplicationContext() != null &&
                                        ((CMApplication)getApplicationContext()).getCurrentActivity() instanceof MainActivity) {
                                    ((MainActivity)((CMApplication)getApplicationContext()).getCurrentActivity()).requestIfNotRegister();
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onWsFailure(Exception error) {
        if(CMAppGlobals.DEBUG)Logger.e(TAG, ":: SplashActivity.onWsFailure : error : " + error.getMessage());
    }

    @Override
    public void onWsSuccess(Object response) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onWsSuccess : response : " + response);
    }

    @Override
    public void onWsOpen() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onWsOpen ");

        // WS::Authenticate
//		authWsManager = new AuthWsManager(this);
//		authWsManager.authenticate();
    }

    @Override
    public void onWsClose(int code, String reason) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.onWsClose ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        connect();
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: SplashActivity.checkPlayServices : This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


}

	
