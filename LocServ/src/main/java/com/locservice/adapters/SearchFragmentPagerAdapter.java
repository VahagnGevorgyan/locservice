package com.locservice.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.ui.SearchActivity;
import com.locservice.ui.fragments.SearchFragment;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vahagn Gevorgyan
 * 29 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class SearchFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = SearchFragmentPagerAdapter.class.getSimpleName();

    private SearchActivity mContext;
    // Tab count
    public static final int TAB_COUNT = 3;
    // View pager fragments list
    private List<Fragment> fragmentList = new ArrayList<>();

    /**
     * Constructor
     * @param fm - Support Fragment Magager
     * @param context - Activity context
     */
    public SearchFragmentPagerAdapter(FragmentManager fm, SearchActivity context) {
        super(fm);
        this.mContext = context;
        init();

    } // end constructor

    /**
     * Method for initialization
     */
    private void init() {
        for (int i = 0; i < TAB_COUNT; i++) {
            Fragment currentFragment = SearchFragment.newInstance(i);
            fragmentList.add(currentFragment);
        }

    } // end method init

    @Override
    public Fragment getItem(int position) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: SearchFragmentPagerAdapter.getItem : position : " + position);

        return fragmentList.get(position);
    }

    /**
     * Method for getting view pager fragments list
     * @return  - View pager fragments list
     */
    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    // Tab titles
    private int tabTitles[] = new int[]{R.string.str_search, R.string.str_airports,
            R.string.str_stations
    };

    // Tab images resources
    private int[] imageResId = { R.drawable.search_selector,
            R.drawable.airport_selector,
            R.drawable.station_selector
    };

    /**
     * Method for getting custom tab view
     * @param position - Tab position
     * @return - custom tab view
     */
    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_search_custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.tab_name);
        tv.setText(tabTitles[position]);
        ImageView img = (ImageView) v.findViewById(R.id.tab_icon);
        img.setImageResource(imageResId[position]);
        return v;

    } // end methos getTabView
}
