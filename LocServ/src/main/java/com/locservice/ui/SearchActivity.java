package com.locservice.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.adapters.SearchFragmentPagerAdapter;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.LastAddressData;
import com.locservice.api.entities.ResultData;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomEditTextView;
import com.locservice.ui.fragments.SearchFragment;
import com.locservice.ui.utils.SearchType;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;

import java.util.List;


/**
 * Created by Vahagn Gevorgyan
 * 03 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class SearchActivity extends LocationBaseActivity implements View.OnClickListener, ICallBack {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private Fragment currentFragment;
    private EditText editTextViewSearch;

    private Handler waitHandler;
    private Runnable waitRunnable;

    private SearchFragmentPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private boolean isFirst = true;
    private View dividerLine;
    private ImageView imageViewClear;
    private boolean isChangedFavoriteAddress;

    private SearchType searchType = SearchType.SEARCH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#00ffffff"));
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerSearch);
        pagerAdapter = new SearchFragmentPagerAdapter(getSupportFragmentManager(), SearchActivity.this);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(mOnPageChangeListener);
        }

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutSearch);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            // Iterate over all tabs and set the custom view
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(pagerAdapter.getTabView(i));
                }
            }
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null && tab.getCustomView() != null) {
                tab.getCustomView().setSelected(true);
            }
        }

        // Setting tab text color by position
        setTabTextColor(0);
        //Setting listeners
        setListeners();

        waitHandler = new Handler();

        // getting current fragment
        currentFragment = pagerAdapter.getFragmentList().get(0);
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: SearchActivity.onCreate : currentFragment : " + currentFragment);

        GooglePlace current_address = getIntent().getParcelableExtra(CMAppGlobals.EXTRA_SEARCH_ADDRESS);
        if (current_address != null) {
            editTextViewSearch.setText(current_address.getName());
            editTextViewSearch.setSelection(current_address.getName().length());
        }

    } // end method onCreate

    /**
     * View pager Page change listener
     */
    ViewPager.SimpleOnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: SearchActivity.onPageSelected : position : " + position);

            currentFragment = pagerAdapter.getFragmentList().get(position);
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: SearchActivity.onPageSelected : currentFragment : " + currentFragment);
            searchType = SearchType.values()[position];
            setTabTextColor(position);
            if (currentFragment != null) {
                switch (searchType) {
                    case SEARCH:
                        ((SearchFragment) currentFragment).showSearchPage();
                        editTextViewSearch.setText("");
                        hideEditableScreen(false, false);
                        break;
                    case AIRPORT:
                        showTabWhenChangingPager(true);
                        ((SearchFragment) currentFragment).showAirportPage();
                        editTextViewSearch.setText(getResources().getString(R.string.str_airports));
                        hideEditableScreen(true, true);
                        break;
                    case STATION:
                        showTabWhenChangingPager(true);
                        ((SearchFragment) currentFragment).showStationPage();
                        editTextViewSearch.setText(getResources().getString(R.string.str_stations));
                        hideEditableScreen(true, true);
                        break;
                }
            }
        }
    };

    /**
     * Method for hiding editable screen
     *
     * @param hide - hide if true else show
     * @param hideAddAddress - hide add address if true else show
     */
    public void hideEditableScreen(boolean hide, boolean hideAddAddress) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: SearchFragment.hideEditableScreen : hide : " + hide);
        if (hide) {
            imageViewClear.setVisibility(View.INVISIBLE);
            dividerLine.setVisibility(View.INVISIBLE);
            if (editTextViewSearch.getKeyListener() != null)
                editTextViewSearch.setTag(editTextViewSearch.getKeyListener());
            editTextViewSearch.setKeyListener(null);
            editTextViewSearch.setEnabled(false);
        } else {
            imageViewClear.setVisibility(View.VISIBLE);
            dividerLine.setVisibility(View.VISIBLE);
            if (editTextViewSearch.getTag() != null)
                editTextViewSearch.setKeyListener((KeyListener) editTextViewSearch.getTag());
            editTextViewSearch.setText("");
            editTextViewSearch.setEnabled(true);
        }
        if (hideAddAddress) {
            ((SearchFragment) currentFragment).showTextViewAdd(false);
        } else {
            ((SearchFragment) currentFragment).showTextViewAdd(true);
        }
    } // end method hideEditableScreen

    /**
     * Method for setting listeners
     */
    public void setListeners() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchActivity.setListeners");
        // ImageView back
        ImageView imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        if (imageViewBack != null) {
            imageViewBack.setOnClickListener(this);
        }
        // ImageView clear
        imageViewClear = (ImageView) findViewById(R.id.imageViewClear);
        if (imageViewClear != null) {
            imageViewClear.setOnClickListener(this);
        }
        // EditTextView Search
        editTextViewSearch = (EditText) findViewById(R.id.editTextViewSearch);
        editTextViewSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                final int currentLength = s.length();
                if (currentLength > 1) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchActivity.onTextChanged : search_text : s : " + s);
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchActivity.onTextChanged : search_text : str_stations : " + getResources().getString(R.string.str_stations));
                    if (currentFragment != null && ((SearchFragment) currentFragment).getTextViewNoResult() != null) {
                        ((SearchFragment) currentFragment).getTextViewNoResult().setVisibility(View.GONE);
                    }
                    if (searchType == SearchType.SEARCH) {
                        if (s != null && !s.toString().isEmpty()
                                && getGoogleApiClient().isConnected()
                                && currentFragment != null) {

                            // show progress bar
                            if (((SearchFragment) currentFragment).getProgressBarSearch() != null) {
                                ((SearchFragment) currentFragment).getProgressBarSearch().setVisibility(View.VISIBLE);
                            }

                            // POST_DELAYED for waiting autocomplete request
                            if (waitRunnable != null)
                                waitHandler.removeCallbacks(waitRunnable);
                            waitRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (currentFragment instanceof SearchFragment)
                                        ((SearchFragment) currentFragment).refreshAddressAdapterByText(s.toString());
                                }
                            };
                            waitHandler.postDelayed(waitRunnable, 1000);

                        } else if (!getGoogleApiClient().isConnected()) {
                            if (CMAppGlobals.DEBUG)
                                Logger.e(TAG, ":: SearchActivity.onTextChanged : GoogleApiClient is not connected");
                        }
                    }
                } else {

                    if (currentFragment != null) {
                        if (((SearchFragment) currentFragment).getTextViewNoResult() != null) {
                            ((SearchFragment) currentFragment).getTextViewNoResult().setVisibility(View.GONE);
                        }

                        if (((SearchFragment) currentFragment).getProgressBarSearch() != null)
                            ((SearchFragment) currentFragment).getProgressBarSearch().setVisibility(View.GONE);
                        // Show search page
                        ((SearchFragment) currentFragment).showSearchPage();

                        if (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "").isEmpty()) {
                            if (((SearchFragment) currentFragment).getExpandableListView() != null) {
                                ((SearchFragment) currentFragment).getExpandableListView().setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ((CustomEditTextView) editTextViewSearch).setBackPressedListener(new CustomEditTextView.BackPressedListener() {
            @Override
            public void onImeBack(CustomEditTextView editText) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: SearchActivity.onImeBack : keyBoard close");

                if (currentFragment != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tabLayout.setVisibility(View.VISIBLE);
                            if (((SearchFragment) currentFragment).getTextViewAdd() != null) {
                                ((SearchFragment) currentFragment).getTextViewAdd().setVisibility(View.VISIBLE);
                            }
                        }
                    }, 200);
                }
            }
        });

        editTextViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SearchActivity : editTextViewSearch : onClick : keyBoard open");

                tabLayout.setVisibility(View.GONE);
                if (currentFragment != null && ((SearchFragment) currentFragment).getTextViewAdd() != null) {
                    ((SearchFragment) currentFragment).getTextViewAdd().setVisibility(View.GONE);
                }
            }
        });

        // View top bar line
        dividerLine = findViewById(R.id.dividerLine);


    } // end method setListeners

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                // CLOSE KEYBOARD
                InputMethodManager immClose = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                immClose.hideSoftInputFromWindow(editTextViewSearch.getWindowToken(), 0);
                onBackPressed();
                break;
            case R.id.imageViewClear:
                editTextViewSearch.setText("");
                ((SearchFragment) currentFragment).showSearchPage();
                if (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "").isEmpty()) {
                    if (currentFragment != null
                            && ((SearchFragment) currentFragment).getExpandableListView() != null) {
                        ((SearchFragment) currentFragment).getExpandableListView().setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.textViewAdd:
                break;
        }
    } // end method onClick

    @Override
    public void onFailure(Throwable error, int requestType) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: SearchActivity.onFailure : requestType : " + requestType + " error : " + error);

        if (currentFragment != null && ((SearchFragment) currentFragment).getProgressBarSearch() != null)
            ((SearchFragment) currentFragment).getProgressBarSearch().setVisibility(View.GONE);
    } // end method onFailure

    @Override
    public void onSuccess(Object response, int requestType) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: SearchActivity.onSuccess : response : " + response + " : requestType : " + requestType);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_GET_FAVORITE:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchActivity.onSuccess : REQUEST_GET_FAVORITE : response : " + response);
                    if (!ErrorUtils.hasError(SearchActivity.this, response, "REQUEST_GET_FAVORITE")) {
                        //noinspection unchecked
                        List<GetFavoriteData> favorites = (List<GetFavoriteData>) response;
                        if (currentFragment instanceof SearchFragment) {
                            ((SearchFragment) currentFragment).setFavoriteList(favorites);
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_LAST_ADDRESSES:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchActivity.onSuccess : REQUEST_GET_LAST_ADDRESSES : response : " + response);
                    if (!ErrorUtils.hasError(SearchActivity.this, response, "REQUEST_GET_LAST_ADDRESSES")) {
                        if(response instanceof LastAddressData) {
                            LastAddressData lastAddressData = (LastAddressData) response;
                            if (currentFragment instanceof SearchFragment) {
                                ((SearchFragment) currentFragment).setLastAddressesList(lastAddressData);
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_REMOVE_FAVORITE:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchActivity.onSuccess : REQUEST_REMOVE_FAVORITE : response : " + response);
                    if (!ErrorUtils.hasError(SearchActivity.this, response, "REQUEST_REMOVE_FAVORITE")) {
                        if(response instanceof ResultData) {
                            ResultData resultData = (ResultData) response;
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchActivity.onSuccess : REQUEST_REMOVE_FAVORITE : result : " + resultData.getResult());
                            if (resultData.getResult().equals("1")) {
                                if (currentFragment instanceof SearchFragment) {
                                    setResult(Activity.RESULT_OK);
                                    isChangedFavoriteAddress = true;
                                    ((SearchFragment) currentFragment).removeAddressItem(((SearchFragment) currentFragment).getRemoveItemPosition());
                                }
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_REMOVE_ADDRESS:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: SearchActivity.onSuccess : REQUEST_REMOVE_ADDRESS : response : " + response);
                    if (!ErrorUtils.hasError(SearchActivity.this, response, "REQUEST_REMOVE_ADDRESS")) {
                        if(response instanceof ResultData) {
                            ResultData resultData = (ResultData) response;
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: SearchActivity.onSuccess : REQUEST_REMOVE_ADDRESS : result : " + resultData.getResult());
                            if (resultData.getResult().equals("1")) {
                                if (currentFragment instanceof SearchFragment) {
                                    ((SearchFragment) currentFragment).removeAddressItem(((SearchFragment) currentFragment).getRemoveItemPosition());
                                }
                            }
                        }
                    }
                    break;
            }
        }

    } // end method onSuccess

    /**
     * Method for getting EditTextViewSearch
     *
     * @return - edit text view Search
     */
    public EditText getEditTextViewSearch() {
        return editTextViewSearch;

    } // end method getEditTextViewSearch

    /**
     * Method for getting is first
     *
     * @return - is first
     */
    public boolean isFirst() {
        return isFirst;

    } // end method isFirst

    /**
     * Method for setting is first
     *
     * @param first - is first
     */
    public void setFirst(boolean first) {
        isFirst = first;

    } // end method setFirst

    /**
     * Method for setting tab layout text color by position
     *
     * @param selectedTabPosition - selected tab position
     */
    public void setTabTextColor(int selectedTabPosition) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.getCustomView() != null) {
                TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_name);
                if (selectedTabPosition == i) {
                    textView.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.orange_tab_selected));
                } else {
                    textView.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.gray_tab_unselected));
                }
            }
        }

    } // end method setTabTextColor

    /**
     * Method for showing tab
     *
     * @param show - show tab if true
     */
    public void showTabWhenChangingPager(boolean show) {
        if (show) {
            // CLOSE KEYBOARD
            InputMethodManager immClose = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            immClose.hideSoftInputFromWindow(editTextViewSearch.getWindowToken(), 0);

            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
        }

    } // end method showTabWhenChangingPager

    @Override
    public void onBackPressed() {
        if (isChangedFavoriteAddress) {
            Intent intent = new Intent();
            intent.putExtra(CMAppGlobals.EXTRA_CHANGED_FAVORITE_ADDRESS, isChangedFavoriteAddress);
            setResult(Activity.RESULT_OK, intent);
        }
        super.onBackPressed();

    } // end method onBackPressed

    /**
     * Method for setting change favorite address
     *
     * @param changedFavoriteAddress - change favorite address
     */
    public void setChangedFavoriteAddress(boolean changedFavoriteAddress) {
        isChangedFavoriteAddress = changedFavoriteAddress;

    } // end method setChangedFavoriteAddress
}