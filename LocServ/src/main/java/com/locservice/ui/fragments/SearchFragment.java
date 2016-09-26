package com.locservice.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.adapters.AddressSearchExpandableAdapter;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.Airport;
import com.locservice.api.entities.City;
import com.locservice.api.entities.Country;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.GooglePrediction;
import com.locservice.api.entities.LastAddress;
import com.locservice.api.entities.LastAddressData;
import com.locservice.api.entities.Railstation;
import com.locservice.api.manager.AddressManager;
import com.locservice.api.manager.FavoriteAddressManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.db.AirportDBManager;
import com.locservice.db.CountryDBManager;
import com.locservice.db.LanguageDBManager;
import com.locservice.db.RailstationDBManager;
import com.locservice.ui.AddFavoriteAddressActivity;
import com.locservice.ui.SearchActivity;
import com.locservice.utils.Logger;
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

/**
 * Created by Vahagn Gevorgyan
 * 3 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    public static final double ONE_LAT_LNG_BY_KM = 110.574; // Km
    public static final int TYPE_FAVORITE = 123456;
    public static final int TYPE_LAST_ADDRESS = TYPE_FAVORITE + 1;

    private SearchActivity mContext;
    private View rootView;

    private AddressSearchExpandableAdapter mAddressSearchExpandableAdapter;
    private ExpandableListView mExpandableListView;
    private AutocompleteFilter mAutocompleteFilter;
    private LatLngBounds mLatLngBounds;
    private int scrollType;
    private String current_place_id;

    private List<GetFavoriteData> mFavoriteList;
    private LastAddressData mLastAddressData;
    private TextView textViewAdd;
    private List<Airport> allAirports;
    private List<Railstation> allRailstations;

    private ProgressBar progressBarSearch;
    private TextView textViewNoResult;

    private int removeItemPosition;


    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Method for getting Search fragment
     * @param page - Page index
     * @return - Search fragment new
     */
    public static SearchFragment newInstance(int page) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.newInstance : page : " + page);

        Bundle args = new Bundle();
        args.putInt(CMAppGlobals.ARGUMENT_SEARCH_FRAGMENT_TYPE, page);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;

    } // end method newInstance

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search_fragment_new, container, false);
        mContext = (SearchActivity) getActivity();

        scrollType = mContext.getIntent().getIntExtra(CMAppGlobals.SCROLL_TYPE, 0);

        progressBarSearch = (ProgressBar) rootView.findViewById(R.id.progressBarSearch);
        textViewNoResult = (TextView) rootView.findViewById(R.id.textViewNoResult);
        // Creating list view
        createListView();

        if (getArguments() != null) {
            int page = getArguments().getInt(CMAppGlobals.ARGUMENT_SEARCH_FRAGMENT_TYPE);
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.onCreateView : page : " + page);
        }

        if (mContext.isFirst()) {
            mContext.setFirst(false);
            showSearchPage();
        }

        textViewAdd = (TextView) rootView.findViewById(R.id.textViewAdd);
        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddFavoriteAddressActivity.class);
                if (mContext.getEditTextViewSearch().getText().toString().length() > 2) {
                    GetFavoriteData getFavoriteData = new GetFavoriteData();
                    getFavoriteData.setAddress(mContext.getEditTextViewSearch().getText().toString());
                    intent.putExtra(CMAppGlobals.EXTRA_FAVORITE_ADDRESS, getFavoriteData);
                    intent.putExtra(CMAppGlobals.EXTRA_NO_PLACE_ID, true);
                }
                startActivityForResult(intent, CMAppGlobals.REQUEST_ADD_FAVORITE_ADDRESS);
            }
        });

        return rootView ;
    }

    /**
     * Method for converting center and radius to bounds
     *
     * @param center - center point
     * @param radius - radius from center
     * @return       - bounds
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

    /**
     * Method for creating list
     */
    private void createListView() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.createListView");

        // Creating Autocomplete Filter
        AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder();
        mAutocompleteFilter = builder.build();

        mLatLngBounds = null;

        CountryDBManager countryDBManager = new CountryDBManager(mContext);
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

        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.recyclerViewSearchAddress);
        mAddressSearchExpandableAdapter = new AddressSearchExpandableAdapter(getContext(),
                new ArrayList<AddressSearchExpandableAdapter.SearchAddressItem>(), mExpandableListView);
        mExpandableListView.setAdapter(mAddressSearchExpandableAdapter);

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // TODO this for not clickable airport item if it has terminals
//                ArrayList<Integer> types = (ArrayList<Integer>) mAddressSearchExpandableAdapter.getSearchAddressList().get(groupPosition).getGooglePrediction().getTypes();
//                if (types.contains(Place.TYPE_AIRPORT)) {
//                    if (mAddressSearchExpandableAdapter.getSearchAddressList().get(groupPosition).getTerminalList() != null &&
//                            mAddressSearchExpandableAdapter.getSearchAddressList().get(groupPosition).getTerminalList().size() > 0) {
//                        return false;
//                    }
//                }
                onItemClick(mAddressSearchExpandableAdapter.getSearchAddressList().get(groupPosition), -1);
                return false;
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                onItemClick(mAddressSearchExpandableAdapter.getSearchAddressList().get(groupPosition), childPosition);
                return false;
            }
        });

        mExpandableListView.setVisibility(View.VISIBLE);

        mExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.onItemLongClick"
                        + " : position : " + position
                        + " : id : " + id
                );

                if (mAddressSearchExpandableAdapter != null
                        && mAddressSearchExpandableAdapter.getSearchAddressList() != null
                        && !mAddressSearchExpandableAdapter.getSearchAddressList().isEmpty()
                        && position < mAddressSearchExpandableAdapter.getSearchAddressList().size()) {

                    AddressSearchExpandableAdapter.SearchAddressItem searchAddressItem = mAddressSearchExpandableAdapter.getSearchAddressList().get(position);
                    if (searchAddressItem != null
                            && searchAddressItem.getGooglePrediction() != null
                            && searchAddressItem.getGooglePrediction().getTypes() != null
                            && !searchAddressItem.getGooglePrediction().getTypes().isEmpty()) {

                        if (searchAddressItem.getGooglePrediction().getTypes().contains(TYPE_FAVORITE)) {
                            String strMessage = mContext.getResources().getString(R.string.alert_favorite_address_delete);
                            removeItemPosition = position;
                            showRemoveAddressDialog(position,searchAddressItem.getGooglePrediction(), strMessage, true);
                            return true;
                        } else if (searchAddressItem.getGooglePrediction().getTypes().contains(TYPE_LAST_ADDRESS)){
                            String strMessage = mContext.getResources().getString(R.string.alert_last_address_delete);
                            removeItemPosition = position;
                            showRemoveAddressDialog(position, searchAddressItem.getGooglePrediction(), strMessage, false);
                            return true;
                        }
                    }
                }

                return false;
            }
        });


    } // end method createListView

    /**
     * Method for showing remove address dialog
     * @param position - item position
     * @param message - dialog message
     * @param isFavorite - if true favorite els last address
     */
    public void showRemoveAddressDialog(final int position, final GooglePrediction googlePrediction,
                                        final String message, final boolean isFavorite) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.showRemoveAddressDialog"
                + " : position : " + position
                + " : googlePrediction : " + googlePrediction
                + " : message : " + message
                + " : isFavorite : " + isFavorite);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.showRemoveAddressDialog : Types :"
                        +  mAddressSearchExpandableAdapter.getSearchAddressList().get(position).getGooglePrediction().getTypes());
                if (isFavorite) {
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.showRemoveAddressDialog : Id : " + googlePrediction.getId());
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.showRemoveAddressDialog : PlaceId : " + googlePrediction.getPlaceId());
                    FavoriteAddressManager favoriteAddressManager = new FavoriteAddressManager(mContext);
                    favoriteAddressManager.RemoveFavorite(googlePrediction.getId());
                } else {
                    if (googlePrediction.getId() != null && !googlePrediction.getId().isEmpty()) {
                        AddressManager addressManager = new AddressManager(mContext);
                        addressManager.RemoveAddress(Integer.parseInt(googlePrediction.getId()));
                    }
                }

                dialog.dismiss();
            }
        });
        builder.create().show();

    } // end method showRemoveAddressDialog

    /**
     * Method for removing address item
     * @param position - address item position
     */
    public void removeAddressItem(int position) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.removeAddressItem : position : " + position);

        if (mAddressSearchExpandableAdapter != null
                && mAddressSearchExpandableAdapter.getSearchAddressList() != null
                && !mAddressSearchExpandableAdapter.getSearchAddressList().isEmpty()) {
            mAddressSearchExpandableAdapter.getSearchAddressList().remove(position);
            mAddressSearchExpandableAdapter.notifyDataSetChanged();
        }

    } // end method removeAddressItem

    /**
     * Method for showing search page ui
     */
    public void showSearchPage() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.showSearchPage ");

        String auth_token = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "");
        if (!auth_token.isEmpty()) {
            if (progressBarSearch != null)
                progressBarSearch.setVisibility(View.VISIBLE);

            if (mFavoriteList == null) {
                FavoriteAddressManager favoriteAddressManager = new FavoriteAddressManager(mContext);
                favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);
            } else {
                setFavoriteList(mFavoriteList);
            }
        } else {
            if (progressBarSearch != null) {progressBarSearch.setVisibility(View.GONE);}
        }

    } // end method showSearchPage

    /**
     * Method for showing search page ui
     */
    public void showAirportPage() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.showAirportPage");
        if (progressBarSearch != null)
            progressBarSearch.setVisibility(View.VISIBLE);
        setAirportsList();

    } // end method showSearchPage

    /**
     * Method for showing search page ui
     */
    public void showStationPage() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.showAirportPage");

        if (progressBarSearch != null)
            progressBarSearch.setVisibility(View.VISIBLE);
        setRailstationList();

    } // end method showSearchPage

    /**
     * Item Click listener
     *
     * @param item - selected item
     * @param terminalPosition - terminal position
     */
    private void onItemClick(final AddressSearchExpandableAdapter.SearchAddressItem item, int terminalPosition) {

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.activeOrdersOnItemClick : getGooglePrediction().getId() : " + item.getGooglePrediction().getId());
        if(item.getGooglePlace() != null)
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.activeOrdersOnItemClick : getGooglePlace().getId() : " + item.getGooglePlace().getId());

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.activeOrdersOnItemClick : terminalPosition : " + terminalPosition);

        // check network status
        if (Utils.checkNetworkStatus(mContext)) {

            // CLOSE KEYBOARD
            InputMethodManager immClose = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            immClose.hideSoftInputFromWindow(mContext.getEditTextViewSearch().getWindowToken(), 0);

            if (item.getGooglePrediction().getTypes().contains(Place.TYPE_STREET_ADDRESS)
                    || item.getGooglePrediction().getTypes().contains(Place.TYPE_ESTABLISHMENT)
                    || item.getGooglePrediction().getTypes().contains(Place.TYPE_PREMISE)) {
                // get google place & redirect
                getPlaceBySearchAddressItem(item);
            } else {
                // start search
                Intent intent = new Intent();
                // TYPE -- STREET
                if (item.getGooglePrediction().getTypes().contains(TYPE_FAVORITE)
                        || item.getGooglePrediction().getTypes().contains(TYPE_LAST_ADDRESS)
                        || (item.getGooglePlace() != null && item.getGooglePlace().isAirport())
                        || (item.getGooglePlace() != null && item.getGooglePlace().isRailwayStation())) {

                    if (item.getGooglePrediction().getTypes().contains(TYPE_FAVORITE)) {
                        item.getGooglePlace().setIsFavorite(true);
                        intent.putExtra(CMAppGlobals.EXTRA_CHANGED_FAVORITE_ADDRESS, true);
                    }
                    // Setting terminal id
                    if (item.getTerminalList() != null && !item.getTerminalList().isEmpty() && terminalPosition >= 0) {
                        String airportAddress = item.getGooglePrediction().getDescription() + ", " + item.getTerminalList().get(terminalPosition).getName();
                        item.getGooglePlace().setName(airportAddress);
                        item.getGooglePlace().setAddress(airportAddress);
                        item.getGooglePlace().setAirportTerminalId(item.getTerminalList().get(terminalPosition).getId());
                        if(item.getTerminalList().get(terminalPosition).getLatitude() != null && !item.getTerminalList().get(terminalPosition).getLatitude().equals("")
                                && item.getTerminalList().get(terminalPosition).getLongitude() != null && !item.getTerminalList().get(terminalPosition).getLongitude().equals("")) {
                            item.getGooglePlace().setLatLng(new LatLng(
                                    Double.valueOf(item.getTerminalList().get(terminalPosition).getLatitude()),
                                    Double.valueOf(item.getTerminalList().get(terminalPosition).getLongitude())));
                        }
                    }

                    intent.putExtra(CMAppGlobals.EXTRA_SEARCH_ADDRESS, item.getGooglePlace());
                    intent.putExtra(CMAppGlobals.SCROLL_TYPE, scrollType);
                    mContext.setResult(Activity.RESULT_OK, intent);
                    mContext.finish();
                } else {
                    if (current_place_id != null && current_place_id.equals(item.getGooglePrediction().getPlaceId())) {
                        // get google place & redirect
                        getPlaceBySearchAddressItem(item);
                    } else {
                        current_place_id = item.getGooglePrediction().getPlaceId();
                        String strDesc = item.getGooglePrediction().getDescription() + " ";
                        mContext.getEditTextViewSearch().setText(strDesc);
                        mContext.getEditTextViewSearch().setSelection(mContext.getEditTextViewSearch().getText().length());
                        // OPEN KEYBOARD
                        InputMethodManager immOpen = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        immOpen.showSoftInput(mContext.getEditTextViewSearch(), InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }

    } // end listener onItemClick

    /**
     * Method for refreshing search adapter by text
     *
     * @param search_text - searching text
     */
    public void refreshAddressAdapterByText(String search_text) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : search_text : " + search_text);

        if(isAdded()) {

            if (search_text.equals(getResources().getString(R.string.str_stations))) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : search_stations : " + search_text);
                // set railstations list to adapter
                setRailstationList();
            } else if (search_text.equals(getResources().getString(R.string.str_airports))) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : search_airports : " + search_text);
                // set airports list to adapter
                setAirportsList();
            } else if (search_text.equals(getResources().getString(R.string.str_favorites))) {
                FavoriteAddressManager favoriteAddressManager = new FavoriteAddressManager(mContext);
                favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);
            } else if (search_text.equals(getResources().getString(R.string.str_latest))) {
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : search_last_addresses : " + search_text);
                AddressManager addressManager = new AddressManager(mContext);
                addressManager.GetLastAddresses(mContext, "", 2);
            } else {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : search_airport : " + search_text);
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : mLatLngBounds : " + mLatLngBounds);
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : mAutocompleteFilter : " + mAutocompleteFilter);
                // Call AutoComplete Request
                PendingResult<AutocompletePredictionBuffer> result =
                        Places.GeoDataApi.getAutocompletePredictions(mContext.getGoogleApiClient(), search_text, mLatLngBounds, mAutocompleteFilter);
                result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {

                        final Status status = autocompletePredictions.getStatus();
                        if (!status.isSuccess()) {
                            if (CMAppGlobals.DEBUG)
                                Logger.e(TAG, ":: SearchFragment.getAutocomplete : Error getting autocomplete API call");
                            autocompletePredictions.release();
                            return;
                        }

                        Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                        ArrayList<GooglePrediction> addresses = new ArrayList<>(autocompletePredictions.getCount());
                        if (autocompletePredictions.getCount() > 0) {
                            mExpandableListView.setVisibility(View.VISIBLE);
                        }
                        while (iterator.hasNext()) {
                            AutocompletePrediction prediction = iterator.next();

                            CharacterStyle cs = new CharacterStyle() {
                                @Override
                                public void updateDrawState(TextPaint tp) {
                                }
                            };
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchFragment.getAutocomplete : FullText : " + prediction.getFullText(cs));
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchFragment.getAutocomplete : PrimaryText : " + prediction.getPrimaryText(cs));
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchFragment.getAutocomplete : SecondaryText : " + prediction.getSecondaryText(cs));

                            String desc = prediction.getPrimaryText(cs).toString();

                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchFragment.getAutocomplete : DESC : " + desc);

                            List<Integer> placeTypes = prediction.getPlaceTypes();
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchFragment.getAutocomplete : placeTypes : " + placeTypes);

                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchFragment.getAutocomplete : place_id : " + prediction.getPlaceId());

                            GooglePrediction googlePrediction = new GooglePrediction();
                            googlePrediction.setDescription(desc);
                            googlePrediction.setPlaceId(prediction.getPlaceId());
                            List<Integer> predictionList = new ArrayList<>();
                            if (placeTypes != null) {
                                predictionList.addAll(placeTypes);
                            }
                            googlePrediction.setTypes(predictionList);
                            googlePrediction.setAddress_secondary(prediction.getSecondaryText(cs).toString());

                            addresses.add(googlePrediction);
                        }
                        autocompletePredictions.release();

                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SearchFragment.refreshAddressAdapterByText : addresses : " + addresses);

                        // clear adapter list items
                        mAddressSearchExpandableAdapter.clearItems();

                        // addresses
                        if (addresses.size() > 0) {

                            for (final GooglePrediction item : addresses) {

                                AddressSearchExpandableAdapter.SearchAddressItem addressItem = new AddressSearchExpandableAdapter.SearchAddressItem();
                                addressItem.setGooglePrediction(item);

                                // ADD TO ADAPTER List & Notify
                                mAddressSearchExpandableAdapter.getSearchAddressList().add(addressItem);
                                if(mAddressSearchExpandableAdapter.getSearchAddressList().size() > 0)
                                    mAddressSearchExpandableAdapter.notifyDataSetChanged();

                                textViewNoResult.setVisibility(View.GONE);
                                // hide progress bar
                                progressBarSearch.setVisibility(View.GONE);
                            }
                        } else {
                            mExpandableListView.setVisibility(View.INVISIBLE);
                            textViewNoResult.setVisibility(View.VISIBLE);
                            // hide progress bar
                            progressBarSearch.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }

    } // end method refreshAddressAdapterByText

    /**
     * Method for setting railway station list to adapter
     */
    public void setRailstationList() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setRailstationList ");

        int languageID = new LanguageDBManager(mContext).getLanguageIdByLocale("ru");
        RailstationDBManager railstationDBManager = new RailstationDBManager(mContext);
        if (allRailstations == null) {
            // TODO this for multi county rail stations
//            String current_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
//            if(!current_city_id.equals("")) {
//                allRailstations = railstationDBManager.getAllRailstations(languageID, Integer.parseInt(current_city_id));
//            }
            // load only rail stations of Moscow
            allRailstations = railstationDBManager.getAllRailstations(languageID, 2);
        }
        if (allRailstations != null) {
            // clear adapter list items
            mAddressSearchExpandableAdapter.clearItems();
            for (Railstation item : allRailstations) {
                if (item != null) {
                    // set data
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setRailstationList : item_name : " + item.getName());

                    // get places
                    GooglePlace googlePlace = new GooglePlace();
                    googlePlace.setId(item.getId());
                    googlePlace.setAddress(item.getName());
                    LatLng latLng = new LatLng(Double.valueOf(item.getLatitude()), Double.valueOf(item.getLongitude()));
                    googlePlace.setLatLng(latLng);
                    googlePlace.setName(item.getName());
                    googlePlace.setIsRailwayStation(true);

                    // CHECK if item is in bounds
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setRailstationList : searchAdapter.getLatLngBounds() : " + mLatLngBounds);
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setRailstationList : googlePlace.setLatLng : " + googlePlace.getLatLng() + " : place name : " + googlePlace.getName());

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setRailstationList : searchAdapter.refresh adapter : lat : " + googlePlace.getLatLng().latitude
                                + " : lng : " + googlePlace.getLatLng().longitude);
                    AddressSearchExpandableAdapter.SearchAddressItem addressItem = new AddressSearchExpandableAdapter.SearchAddressItem();
                    addressItem.setGooglePlace(googlePlace);
                    GooglePrediction googlePrediction = new GooglePrediction();
                    googlePrediction.setId(item.getId());
                    googlePrediction.setDescription(item.getName());
                    List<Integer> predictionList = new ArrayList<>();
                    predictionList.add(Place.TYPE_TRAIN_STATION);
                    googlePrediction.setTypes(predictionList);
                    addressItem.setGooglePrediction(googlePrediction);

                    // ADD TO ADAPTER List & Notify
                    mAddressSearchExpandableAdapter.getSearchAddressList().add(addressItem);
                    if(mAddressSearchExpandableAdapter.getSearchAddressList().size() > 0) {
                        mAddressSearchExpandableAdapter.notifyDataSetChanged();
                    }
                    textViewNoResult.setVisibility(View.GONE);
                }

            }
        }
        // hide progress bar
        progressBarSearch.setVisibility(View.GONE);

    } // end method setRailstationList

    /**
     * Method for setting airports list to adapter
     */
    public void setAirportsList() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setAirportsList ");

        int languageID = new LanguageDBManager(mContext).getLanguageIdByLocale("ru");
        AirportDBManager airportDBManager = new AirportDBManager(mContext);
        if (allAirports == null) {
            // TODO this for multi country airports
//            String current_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
//            if(!current_city_id.equals("")) {
//                allAirports = airportDBManager.getAllAirports(languageID, Integer.parseInt(current_city_id));
//            }
            // load only airports of Moscow
            allAirports = airportDBManager.getAllAirports(languageID, 2);
        }

        if (allAirports != null) {
            // clear adapter list items
            mAddressSearchExpandableAdapter.clearItems();
            for (Airport item : allAirports) {
                if (item != null) {
                    // set data
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setAirportsList : item_name : " + item.getName());

                    // get places
                    GooglePlace googlePlace = new GooglePlace();
                    googlePlace.setId(item.getId());
                    googlePlace.setAddress(item.getName());
                    LatLng latLng = new LatLng(Double.valueOf(item.getLatitude()), Double.valueOf(item.getLongitude()));
                    googlePlace.setLatLng(latLng);
                    googlePlace.setName(item.getName());
                    googlePlace.setIsAirport(true);

                    // CHECK if item is in bounds
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setAirportsList : searchAdapter.getLatLngBounds() : " + mLatLngBounds);
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setAirportsList : googlePlace.setLatLng : " + googlePlace.getLatLng() + " : place name : " + googlePlace.getName());

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchFragment.setAirportsList : searchAdapter.refresh adapter : lat : " + googlePlace.getLatLng().latitude
                                + " : lng : " + googlePlace.getLatLng().longitude);

                    AddressSearchExpandableAdapter.SearchAddressItem addressItem = new AddressSearchExpandableAdapter.SearchAddressItem();
                    addressItem.setGooglePlace(googlePlace);
                    addressItem.setTerminalList(item.getTerminals());
                    GooglePrediction googlePrediction = new GooglePrediction();
                    googlePrediction.setId(item.getId());
                    googlePrediction.setDescription(item.getName());
                    List<Integer> predictionList = new ArrayList<>();
                    predictionList.add(Place.TYPE_AIRPORT);
                    googlePrediction.setTypes(predictionList);
                    addressItem.setGooglePrediction(googlePrediction);

                    // ADD TO ADAPTER List & Notify
                    mAddressSearchExpandableAdapter.getSearchAddressList().add(addressItem);
                    if(mAddressSearchExpandableAdapter.getSearchAddressList().size() > 0)
                        mAddressSearchExpandableAdapter.notifyDataSetChanged();
                    textViewNoResult.setVisibility(View.GONE);
                }
            }
        }
        // hide progress bar
        progressBarSearch.setVisibility(View.GONE);

    } // end method setAirportsList

    /**
     * Method for getting google place
     *  by search address item
     *
     * @param item - search address
     */
    public void getPlaceBySearchAddressItem(final AddressSearchExpandableAdapter.SearchAddressItem item) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : item : " + item);

        // set google place Id
        Places.GeoDataApi.getPlaceById(mContext.getGoogleApiClient(), item.getGooglePrediction().getPlaceId())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            try {

                                final Place myPlace = places.get(0);
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : GooglePlace : place name : " + myPlace.getName());
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : GooglePlace : place Address : " + myPlace.getAddress());
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : GooglePlace : secondary Address : " + item.getGooglePrediction().getAddress_secondary());
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : GooglePlace : Description Address : " + item.getGooglePrediction().getDescription());

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
                                String secondaryAddress = item.getGooglePrediction().getAddress_secondary();
                                if (secondaryAddress != null && !secondaryAddress.isEmpty()) {
                                    String[] separatedAddress = secondaryAddress.split(",");
                                    if (separatedAddress.length > 0)
                                        googlePlace.setArea(separatedAddress[0]);
                                }

                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : searchAdapter.getLatLngBounds() : " + mLatLngBounds);
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : googlePlace.setLatLng : " + googlePlace.getLatLng() + " : place name : " + googlePlace.getName());

                                // start search
                                Intent intent = new Intent();
                                intent.putExtra(CMAppGlobals.EXTRA_SEARCH_ADDRESS, googlePlace);
                                intent.putExtra(CMAppGlobals.SCROLL_TYPE, scrollType);
                                mContext.setResult(Activity.RESULT_OK, intent);
                                mContext.finish();

                            } catch (IllegalStateException e) {
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : ERROR : " + e);
                            }
                        } else {
                            if (CMAppGlobals.DEBUG)
                                Logger.e(TAG, ":: SearchFragment.getPlaceBySearchAddressItem : Error places status");
                        }
                        places.release();
                    }
                });

    } // end method getPlaceBySearchAddressItem

    /**
     * Method for setting favorite list to adapter
     */
    public void setFavoriteList(List<GetFavoriteData> favoriteList) {

        mFavoriteList = favoriteList;

        if (mAddressSearchExpandableAdapter != null) {
            // clear adapter list items
            mAddressSearchExpandableAdapter.clearItems();
            if (favoriteList.size() > 0) {

                for (GetFavoriteData item : favoriteList) {
                    if (item != null) {
                        // set data
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: SearchFragment.setAirportsList : item_name : " + item.getName());

                        // get places
                        GooglePlace googlePlace = new GooglePlace();
                        googlePlace.setId(item.getId());
                        googlePlace.setAddress(item.getAddress());
                        LatLng latLng = new LatLng(Double.valueOf(item.getLatitude()), Double.valueOf(item.getLongitude()));
                        googlePlace.setLatLng(latLng);
                        googlePlace.setName(item.getName());
                        googlePlace.setIsAirport(false);
                        googlePlace.setIsFavorite(true);

                        AddressSearchExpandableAdapter.SearchAddressItem addressItem = new AddressSearchExpandableAdapter.SearchAddressItem();
                        addressItem.setGooglePlace(googlePlace);
                        GooglePrediction googlePrediction = new GooglePrediction();
                        googlePrediction.setId(item.getId());
                        googlePrediction.setDescription(item.getName());
                        googlePrediction.setAddress_secondary(item.getAddress());
                        List<Integer> placeTypeList =new ArrayList<>();
                        placeTypeList.add(TYPE_FAVORITE);
                        googlePrediction.setTypes(placeTypeList);
                        addressItem.setGooglePrediction(googlePrediction);

                        // ADD TO ADAPTER List & Notify
                        mAddressSearchExpandableAdapter.getSearchAddressList().add(addressItem);
                        if(mAddressSearchExpandableAdapter.getSearchAddressList().size() > 0) {
                            mAddressSearchExpandableAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        if (mLastAddressData == null) {
            String currentCityID = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
            if (!currentCityID.isEmpty()) {
                // Request for getting last addresses
                AddressManager addressManager = new AddressManager(mContext);
                addressManager.GetLastAddresses(mContext, "", Integer.parseInt(currentCityID));
            }
        } else {
            setLastAddressesList(mLastAddressData);
        }

    } // end method setFavoriteList

    /**
     * Method for setting last addresses list to adapter
     */
    public void setLastAddressesList(LastAddressData lastAddressData) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SearchFragment.setLastAddressesList : lastAddressData.getIsLast() : " + lastAddressData.getIsLast());
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SearchFragment.setLastAddressesList : lastAddressData.getAddresses() : " + lastAddressData.getAddresses());

        mLastAddressData = lastAddressData;
//        // clear adapter list items
//        mAddressSearchExpandableAdapter.clearItems();
        if (lastAddressData.getAddresses() != null && lastAddressData.getAddresses().size() > 0) {

            List<LastAddress> addresses = lastAddressData.getAddresses();
            for (LastAddress item : addresses) {
                if (item != null) {
                    // set data
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setLastAddressesList : item.getAddress() : " + item.getAddress());
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setLastAddressesList : item.getId() : " + item.getId());
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setLastAddressesList : item.getIdType() : " + item.getIdType());
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setLastAddressesList : item.getLat() : " + item.getLat());
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setLastAddressesList : item.getLng() : " + item.getLng());

                    // get places
                    GooglePlace googlePlace = new GooglePlace();
                    googlePlace.setId(String.valueOf(item.getId()));
                    googlePlace.setAddress(item.getAddress());
                    LatLng latLng = new LatLng(Double.valueOf(item.getLat()), Double.valueOf(item.getLng()));
                    googlePlace.setLatLng(latLng);
                    googlePlace.setName(item.getAddress());
                    googlePlace.setIsAirport(false);
                    googlePlace.setIsFavorite(false);
                    googlePlace.setIsLastAddress(true);

                    AddressSearchExpandableAdapter.SearchAddressItem addressItem = new AddressSearchExpandableAdapter.SearchAddressItem();
                    addressItem.setGooglePlace(googlePlace);
                    GooglePrediction googlePrediction = new GooglePrediction();
                    googlePrediction.setId(String.valueOf(item.getId()));
                    googlePrediction.setDescription(item.getAddress());
                    List<Integer> placeTypeList =new ArrayList<>();
                    placeTypeList.add(TYPE_LAST_ADDRESS);
                    googlePrediction.setTypes(placeTypeList);
                    addressItem.setGooglePrediction(googlePrediction);

                    // ADD TO ADAPTER List & Notify
                    mAddressSearchExpandableAdapter.getSearchAddressList().add(addressItem);
                    if(mAddressSearchExpandableAdapter.getSearchAddressList().size() > 0) {
                        mAddressSearchExpandableAdapter.notifyDataSetChanged();
                    }
                    // hide progress bar
                    progressBarSearch.setVisibility(View.GONE);
                    textViewNoResult.setVisibility(View.GONE);
                }
            }
        } else {
            progressBarSearch.setVisibility(View.GONE);
            textViewNoResult.setVisibility(View.VISIBLE);
        }

    } // end method setLastAddressesList

    /**
     * Method for showing text view add
     * @param isShow - if true show
     */
    public void showTextViewAdd(boolean isShow) {
        if (isShow) {
            textViewAdd.setVisibility(View.VISIBLE);
        } else {
            textViewAdd.setVisibility(View.GONE);
        }

    } // end method showTextViewAdd

    /**
     * Method for getting progress par
     * @return - progress bar
     */
    public ProgressBar getProgressBarSearch() {
        return progressBarSearch;

    } // end method getProgressBarSearch

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchFragment.setLastAddressesList"
                + " : requestCode : " + requestCode
                + " : resultCode : " + resultCode
                + " : data : " + data
        );
        switch (requestCode) {
            case CMAppGlobals.REQUEST_ADD_FAVORITE_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    FavoriteAddressManager favoriteAddressManager = new FavoriteAddressManager(mContext);
                    favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);
                    mContext.setResult(Activity.RESULT_OK);
                    mContext.setChangedFavoriteAddress(true);
                }
                break;
        }
    }

    /**
     * Method for getting text view add
     * @return - text view add
     */
    public TextView getTextViewAdd() {
        return textViewAdd;

    } // end method textViewAdd

    /**
     * Method for  getting text view no result
     * @return - Text view no result
     */
    public TextView getTextViewNoResult() {
        return textViewNoResult;

    } // end method textViewNoResult

    /**
     * Method for getting Expandable List View
     * @return - Expandable List View
     */
    public ExpandableListView getExpandableListView() {
        return mExpandableListView;

    } // end method getExpandableListView

    /**
     * Method for getting removing item position
     * @return - removing item position
     */
    public int getRemoveItemPosition() {
        return removeItemPosition;

    } // end method getRemoveItemPosition
}
