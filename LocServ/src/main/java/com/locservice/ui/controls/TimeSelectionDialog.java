package com.locservice.ui.controls;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.ui.fragments.ChooseTimeFragment;
import com.locservice.utils.Logger;

public class TimeSelectionDialog extends DialogFragment {

	private static final String TAG = TimeSelectionDialog.class.getSimpleName();

    public static final String ARG_PAGE = "arg_page";
	private FragmentActivity mContext;

    private TabLayout tabLayout;
    private ChooseTimeFragment chooseTimeTakeFragment;
//    private ChooseTimeFragment chooseTimeBringFragment;
    private View rootView;

    @SuppressLint("NewApi") @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.dialog_time_selection, container, false);
        
        mContext = getActivity();
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(0)));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(1)));

        if (chooseTimeTakeFragment == null) {
            if (CMAppGlobals.DEBUG) Logger.d(TAG, "chooseTimeTakeFragment = null");
            chooseTimeTakeFragment = ChooseTimeFragment.newInstance(0);
        }
//        if (chooseTimeBringFragment == null) {
//            if (CMAppGlobals.DEBUG) Logger.d(TAG, "chooseTimeBringFragment = null");
//            chooseTimeBringFragment = ChooseTimeFragment.newInstance(1);
//        }
        getChildFragmentManager().beginTransaction().add(R.id.container, chooseTimeTakeFragment).commit();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                switch (tab.getPosition()) {
                    case 0:
                        transaction.replace(R.id.container, chooseTimeTakeFragment).commit();
                        break;
//                    case 1:
//                        transaction.replace(R.id.container, chooseTimeBringFragment).commit();
//                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return rootView;
    }


    private int tabTitles[] = new int[] { R.string.str_time_of_filing
//            , R.string.test_bring
                };
    private int[] imageResId = { R.drawable.drawable_time_picker_take
//            , R.drawable.drawable_time_picker
                };

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.tab_name);
        tv.setText(tabTitles[position]);
        ImageView img = (ImageView) v.findViewById(R.id.tab_icon);
        img.setImageResource(imageResId[position]);
        return v;
    }
	
}
