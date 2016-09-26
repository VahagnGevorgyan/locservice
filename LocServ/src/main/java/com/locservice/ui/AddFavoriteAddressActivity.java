package com.locservice.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.AddressSearchAdapter;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.City;
import com.locservice.api.entities.Country;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.GooglePrediction;
import com.locservice.api.entities.SetFavoriteData;
import com.locservice.api.manager.FavoriteAddressManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.db.CountryDBManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.listeners.ActivityListener;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.StringHelper;
import com.locservice.utils.Utils;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AddFavoriteAddressActivity extends LocationBaseActivity implements View.OnClickListener, ICallBack, ActivityListener {

    private static final String TAG = AddFavoriteAddressActivity.class.getSimpleName();

    public static final double ONE_LAT_LNG_BY_KM = 110.574; // Km

    private LinearLayout layoutBack;
    private EditText editTextName;
    private EditText editTextAddress;
    private EditText editTextEntrance;
    private EditText editTextComment;
    private TextView editTextNameDescription;
    private TextView editTextSave;
    private Runnable waitRunnable;
    private Handler waitHandler;

    private CMApplication mCMApplication;

    private RecyclerView recyclerViewSearchAddress;
    private LinearLayoutManager linearLayoutManager;
    private AddressSearchAdapter searchAdapter;


    private ProgressBar progressBarSearch;
    private boolean isUpdate = false;
    private GetFavoriteData getFavoriteData;

    private String current_place_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite_address);

        mCMApplication = (CMApplication)this.getApplicationContext();

        //change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, com.locservice.R.color.color_status_bar));
        }

        // check network status
        if (Utils.checkNetworkStatus(this)) {

            waitHandler = new Handler();

            // Setting event listeners
            setEventListeners();
            // Creating RecyclerView
            createRecyclerView();

            getFavoriteData = getIntent().getParcelableExtra(CMAppGlobals.EXTRA_FAVORITE_ADDRESS);
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onCreate : getFavoriteData : " + getFavoriteData);
            if (getFavoriteData != null) {
                completeAddressFields(getFavoriteData);
                isUpdate = true;
            }
        }
    }

    public void completeAddressFields (GetFavoriteData favoriteData) {
        if (favoriteData.getName() != null)
            editTextName.setText(favoriteData.getName());
        if (favoriteData.getAddress() != null)
            editTextAddress.setText(favoriteData.getAddress());
        if (favoriteData.getEntrance() != null)
            editTextEntrance.setText(favoriteData.getEntrance());
        if (favoriteData.getComment() != null) {
            editTextComment.setText(favoriteData.getComment());
        }

        if (favoriteData.getLatitude() != null && !favoriteData.getLatitude().isEmpty()
                && favoriteData.getLongitude() != null && !favoriteData.getLongitude().isEmpty()) {
            currentGooglePlace = new GooglePlace();
            currentGooglePlace.setLatLng(new LatLng(Double.parseDouble(favoriteData.getLatitude()), Double.parseDouble(favoriteData.getLongitude())));
        }
    }


    /**
     * Method for setting event listeners
     */
    public void setEventListeners() {
        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnTouchListener(new View.OnTouchListener() {
            ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CommonHelper.setOnTouchImage(imageViewBack, event);
            }
        });
        layoutBack.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                final int currentLength = s.length();
                if (currentLength > 1) {
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onTextChanged : search_text : " + s);

                    if (s != null && !s.toString().isEmpty() && getGoogleApiClient().isConnected()) {
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onTextChanged : s.length : " + s.length());

                        // POST_DELAYED for waiting autocomplete request
                        if (waitRunnable != null)
                            waitHandler.removeCallbacks(waitRunnable);
                        waitRunnable = new Runnable() {
                            @Override
                            public void run() {
                                refreshAddressAdapterByText(s.toString());
                            }
                        };
                        currentGooglePlace = null;
                        progressBarSearch.setVisibility(View.VISIBLE);
                        waitHandler.postDelayed(waitRunnable, 1000);
                    }
                } else {
                    searchAdapter.getAddresses().clear();
                    searchAdapter.notifyDataSetChanged();
                    progressBarSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editTextEntrance = (EditText) findViewById(R.id.editTextEntrance);
        editTextNameDescription = (TextView) findViewById(R.id.editTextNameDescription);
        editTextComment = (EditText) findViewById( R.id.editTextComment);

        editTextSave = (TextView) findViewById(R.id.editTextSave);
        editTextSave.setOnClickListener(this);

        progressBarSearch = (ProgressBar) findViewById(R.id.progressBarSearch);


    } // end method setEventListeners

    /**
     * Method for creating recycler
     */
    public void createRecyclerView() {
        // CREATE RECYCLER VIEW
        recyclerViewSearchAddress = (RecyclerView) findViewById(R.id.recyclerViewSearchAddress);
        linearLayoutManager = new LinearLayoutManager(AddFavoriteAddressActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchAddress.setLayoutManager(linearLayoutManager);

        AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder();
//        builder.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS);
        AutocompleteFilter autocompleteFilter = builder.build();

        LatLngBounds mLatLngBounds = null;

        CountryDBManager countryDBManager = new CountryDBManager(AddFavoriteAddressActivity.this);
        List<Country> countryList = countryDBManager.getAllCountries();

        String current_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.BOUNDS_CITY_ID.key(), "");
        if (countryList != null && !countryList.isEmpty() &&!current_city_id.isEmpty()) {
            for (Country country : countryList) {
                List<City> cityList = country.getCities();
                if (cityList != null && !cityList.isEmpty()) {
                    for (City city : cityList) {
                        if (city.getId() != null && !city.getId().isEmpty() && city.getId().equals(current_city_id)) {
                            // Current lat and lng
                            LatLng atLng = new LatLng(Double.parseDouble(city.getLatitude()), Double.parseDouble(city.getLongitude()));
                            // Bounds
                            mLatLngBounds = convertCenterAndRadiusToBounds(atLng, Double.parseDouble(city.getSearchRadius()));
                        }
                    }
                }
            }
        }

        // CREATE ADAPTER
        searchAdapter = new AddressSearchAdapter(this, R.layout.list_item_favorite_autocomplate, new ArrayList<AddressSearchAdapter.SearchAddressItem>(), getGoogleApiClient(), mLatLngBounds, autocompleteFilter);
        searchAdapter.setOnItemClickListener(onItemClickListener);
        recyclerViewSearchAddress.setAdapter(searchAdapter);
    }

    /**
     * Method for converting center and radius to bounds
     *
     * @param center
     * @param radius
     * @return
     */
    public LatLngBounds convertCenterAndRadiusToBounds(LatLng center, double radius) {
        double radiusByLatLng = radius / ONE_LAT_LNG_BY_KM;
        double southwestLat = center.latitude - radiusByLatLng;
        double southwestLng = center.longitude - radiusByLatLng;
        double northeastLat = center.latitude + radiusByLatLng;
        double northeastLng = center.longitude + radiusByLatLng;

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, "southwestLat " + southwestLat + " southwestLng : " + southwestLng
                    + " northeastLat : " + northeastLat + " northeastLng : " + northeastLng);

        return new LatLngBounds(new LatLng(southwestLat, southwestLng), new LatLng(northeastLat, northeastLng));

    } // end method convertCenterAndRadiusToBounds


    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(this)) {

            switch (v.getId()) {
                case R.id.layoutBack:
                    onBackPressed();
                    break;
                case R.id.editTextSave:
                    String name = editTextName.getText().toString();
                    name = name.trim();
                    String address = editTextAddress.getText().toString();
                    address = address.trim();
                    String id = "";
                    if (currentGooglePlace != null) {
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: AddFavoriteAddressActivity.AddFavorite : name : "
                                    + name + " : address : " + address + " : currentGooglePlace : "
                                    + currentGooglePlace + " : currentGooglePlace.getLatLng" + currentGooglePlace.getLatLng()
                                    + " : isUpdate : " + isUpdate);
                    }
                    if (isUpdate) {
                        id = getFavoriteData.getId();
                    }
                    if (!name.isEmpty() && !address.isEmpty() && currentGooglePlace != null && currentGooglePlace.getLatLng() != null) {
                        String lat = currentGooglePlace.getLatLng().latitude + "";
                        String lng = currentGooglePlace.getLatLng().longitude + "";
                        FavoriteAddressManager favoriteAddressManager = new FavoriteAddressManager(this);
                        favoriteAddressManager.SetFavorite(editTextAddress.getText().toString(),
                                lat,
                                lng,
                                editTextEntrance.getText().toString(),
                                editTextComment.getText().toString(),
                                editTextName.getText().toString(),
                                id
                        );
                    } else if (name.isEmpty()) {
                        editTextName.setError(getString(R.string.alert_empty_name));
                    } else if (address.isEmpty()) {
                        editTextAddress.setError(getString(R.string.alert_empty_address));
                    } else {
                        Toast.makeText(AddFavoriteAddressActivity.this, R.string.alert_wrong_address, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private GooglePlace currentGooglePlace;
    private AddressSearchAdapter.OnItemClickListener onItemClickListener = new AddressSearchAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View view, int position, final AddressSearchAdapter.SearchAddressItem address) {
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity.activeOrdersOnItemClick : placeId : " + address.getGooglePrediction().getPlaceId());

            // check network status
            if (Utils.checkNetworkStatus(AddFavoriteAddressActivity.this)) {

                // CLOSE KEYBOARD
                InputMethodManager immClose = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                immClose.hideSoftInputFromWindow(editTextAddress.getWindowToken(), 0);

                if (address.getGooglePrediction().getTypes().contains(Place.TYPE_STREET_ADDRESS)
                        || address.getGooglePrediction().getTypes().contains(Place.TYPE_ESTABLISHMENT)
                        || address.getGooglePrediction().getTypes().contains(Place.TYPE_PREMISE)) {
                    // get google place & redirect
                    getPlaceBySearchAddressItem(address);
                } else {
                    // TYPE -- STREET
                    if (current_place_id != null && current_place_id.equals(address.getGooglePrediction().getPlaceId())) {
                        // get google place & redirect
                        getPlaceBySearchAddressItem(address);
                    } else {
                        current_place_id = address.getGooglePrediction().getPlaceId();
                        String strDesc = StringHelper.combineStrings(address.getGooglePrediction().getDescription(), " ");
                        editTextAddress.setText(strDesc);
                        editTextAddress.setSelection(editTextAddress.getText().length());
                        // OPEN KEYBOARD
                        InputMethodManager immOpen = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        immOpen.showSoftInput(editTextAddress, InputMethodManager.SHOW_IMPLICIT);
                    }
                }

            }
        }
    };

    /**
     * Method for getting google place
     *  by search address item
     *
     * @param item - search address
     */
    public void getPlaceBySearchAddressItem(final AddressSearchAdapter.SearchAddressItem item) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity.getPlaceBySearchAddressItem : item : " + item);

        // set google place Id
        Places.GeoDataApi.getPlaceById(getGoogleApiClient(), item.getGooglePrediction().getPlaceId())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            try {

                                final Place myPlace = places.get(0);
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: AddFavoriteAddressActivity.getPlaceBySearchAddressItem.getPlaceById : GooglePlace : place name : " + myPlace.getName());

                                // get places
                                GooglePlace googlePlace = new GooglePlace();
                                googlePlace.setId(myPlace.getId());
                                googlePlace.setAddress((String) myPlace.getAddress());
                                googlePlace.setLatLng(myPlace.getLatLng());
                                googlePlace.setLatLngBounds(myPlace.getViewport());
                                googlePlace.setName(item.getGooglePrediction().getDescription());
                                googlePlace.setPhoneNumber((String) myPlace.getPhoneNumber());
                                googlePlace.setLocale(myPlace.getLocale());
                                googlePlace.setPlaceTypes(myPlace.getPlaceTypes());
                                googlePlace.setPriceLevel(myPlace.getPriceLevel());
                                googlePlace.setRating(myPlace.getRating());
                                googlePlace.setWebsiteUri(myPlace.getWebsiteUri());
                                googlePlace.setHasPlaceId(true);

                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: AddFavoriteAddressActivity.getPlaceById : googlePlace.setLatLng : " + googlePlace.getLatLng() + " : place name : " + googlePlace.getName());

//                                // start search
//                                Intent intent = new Intent();
//                                intent.putExtra(CMAppGlobals.EXTRA_SEARCH_ADDRESS, googlePlace);
//                                intent.putExtra(CMAppGlobals.SCROLL_TYPE, scrollType);
//                                mContext.setResult(Activity.RESULT_OK, intent);
//                                mContext.finish();

                                currentGooglePlace = googlePlace;
                                searchAdapter.getAddresses().clear();
                                searchAdapter.notifyDataSetChanged();
                                progressBarSearch.setVisibility(View.GONE);
                                recyclerViewSearchAddress.setVisibility(View.GONE);

                            } catch (IllegalStateException e) {
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity : ERROR : " + e);
                            }
                        } else {
                            if (CMAppGlobals.DEBUG)
                                Logger.e(TAG, ":: AddFavoriteAddressActivity.getPlaceById : Error places status");
                        }
                        places.release();
                    }
                });

    } // end method getPlaceBySearchAddressItem

    /**
     * Method for refreshing search adapter by text
     *
     * @param search_text
     */
    public void refreshAddressAdapterByText(String search_text) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.refreshAddressAdapterByText : search_text : " + search_text);

        // Call AutoComplete Request
        PendingResult<AutocompletePredictionBuffer> result =
                Places.GeoDataApi.getAutocompletePredictions(getGoogleApiClient(), search_text, searchAdapter.getmLatLngBounds(), searchAdapter.getmAutocompleteFilter());
        result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
            @Override
            public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {

                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    if (CMAppGlobals.DEBUG)
                        Logger.e(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : Error getting autocomplete API call");
                    autocompletePredictions.release();
                    return;
                }

                Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                ArrayList<GooglePrediction> addresses = new ArrayList<>(autocompletePredictions.getCount());
                while (iterator.hasNext()) {
                    AutocompletePrediction prediction = iterator.next();

                    CharacterStyle cs = new CharacterStyle() {
                        @Override
                        public void updateDrawState(TextPaint tp) {
                        }
                    };
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : FullText : " + prediction.getFullText(cs));
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : PrimaryText : " + prediction.getPrimaryText(cs));
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : SecondaryText : " + prediction.getSecondaryText(cs));

                    String desc = prediction.getPrimaryText(cs).toString();

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : DESC : " + desc);

                    List<Integer> placeTypes = prediction.getPlaceTypes();
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : placeTypes : " + placeTypes);

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : place_id : " + prediction.getPlaceId());

                    GooglePrediction googlePrediction = new GooglePrediction();
                    googlePrediction.setDescription(desc);
                    googlePrediction.setPlaceId(prediction.getPlaceId());
                    List<Integer> predictionList = new ArrayList<>();
                    predictionList.addAll(placeTypes);
                    googlePrediction.setTypes(predictionList);
                    googlePrediction.setAddress_secondary(prediction.getSecondaryText(cs).toString());

                    addresses.add(googlePrediction);
                }
                autocompletePredictions.release();

                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : addresses.size : " + addresses.size());
                // addresses
                if (addresses.size() > 0) {
                    // clear adapter list items
                    searchAdapter.clearItems();

                    for (final GooglePrediction item : addresses) {
                        recyclerViewSearchAddress.setVisibility(View.VISIBLE);
                        AddressSearchAdapter.SearchAddressItem addressItem = new AddressSearchAdapter.SearchAddressItem();
                        addressItem.setGooglePrediction(item);

                        // ADD TO ADAPTER List & Notify
                        searchAdapter.getAddresses().add(addressItem);
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.getAutocomplete : searchAdapter.getAddresses.size : " + searchAdapter.getAddresses().size());
                        if(searchAdapter.getAddresses().size() > 0)
                            searchAdapter.notifyDataSetChanged();
                        // hide progress bar
                        progressBarSearch.setVisibility(View.GONE);
                    }
                } else {
                    progressBarSearch.setVisibility(View.GONE);
                }
            }
        });

    } // end method refreshAddressAdapterByText

    @Override
    public void onFailure(Throwable error, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onFailure : requestType : " + requestType + " : error : " + error);
    }

    @Override
    public void onResume() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity.onResume ");
        super.onResume();
        // set current activity
        setCurrentActivity(this);

        if (getIntent().getBooleanExtra(CMAppGlobals.EXTRA_NO_PLACE_ID, false)) {
            if (getFavoriteData != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editTextAddress.setText(getFavoriteData.getAddress());
                    }
                }, 500);

            }
        }
    }

    @Override
    protected void onPause() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity.onPause ");
        clearCurrentActivity(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity.onDestroy ");
        clearCurrentActivity(this);
        super.onDestroy();
    }


    @Override
    public void onSuccess(Object response, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onSuccess : requestType : " + requestType + " : response : " + response);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_SET_FAVORITE:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_SET_FAVORITE")) {
                        if (response instanceof SetFavoriteData) {

                            SetFavoriteData favoriteData = (SetFavoriteData) response;

                            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onSuccess : REQUEST_SET_FAVORITE : favoriteData : " + favoriteData);
                            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onSuccess : REQUEST_SET_FAVORITE : result : " + favoriteData.getResult());
                            if (favoriteData.getResult() == 1) {
                                GetFavoriteData getFavoriteData = new GetFavoriteData();
                                getFavoriteData.setAddress(editTextAddress.getText().toString());
                                getFavoriteData.setName(editTextName.getText().toString());
                                String lat = currentGooglePlace.getLatLng().latitude + "";
                                getFavoriteData.setLatitude(lat);
                                String lng = currentGooglePlace.getLatLng().longitude + "";
                                getFavoriteData.setLongitude(lng);
                                getFavoriteData.setEntrance(editTextEntrance.getText().toString());
                                getFavoriteData.setComment(editTextComment.getText().toString());
                                String old_id = favoriteData.getIdOld() + "";
                                getFavoriteData.setId(old_id);

                                if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onSuccess : REQUEST_SET_FAVORITE : address : " + editTextAddress.getText().toString() +
                                        " : lat : " + currentGooglePlace.getLatLng().latitude + " : lng : " + currentGooglePlace.getLatLng().longitude);

                                if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddFavoriteAddressActivity.onSuccess : REQUEST_SET_FAVORITE : old_id : " + old_id);


                                Intent intent = new Intent();
                                intent.putExtra(CMAppGlobals.EXTRA_FAVORITE_ADDRESS, getFavoriteData);
                                if (favoriteData.getIdFavorite() != null)
                                    intent.putExtra(CMAppGlobals.EXTRA_FAVORITE_ID, favoriteData.getIdFavorite());

                                if(getApplicationContext() != null &&
                                        ((CMApplication)getApplicationContext()).getCurrentActivity() instanceof AddFavoriteAddressActivity) {
                                    setResult(Activity.RESULT_OK, intent);
                                    // closing keyboard
                                    View view = this.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                    finish();
                                }
                            }
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public void setCurrentActivity(Activity context) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity.setCurrentActivity : context : " + context);

        // set current activity
        mCMApplication.setCurrentActivity(context);
    }

    @Override
    public void clearCurrentActivity(Activity context) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddFavoriteAddressActivity.clearCurrentActivity : context : " + context);

        Activity currActivity = mCMApplication.getCurrentActivity();
        if (this.equals(currActivity))
            mCMApplication.setCurrentActivity(null);
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onBackPressed();
    }
}
