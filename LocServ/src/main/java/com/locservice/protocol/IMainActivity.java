package com.locservice.protocol;

import android.support.v4.widget.DrawerLayout;

import com.locservice.ui.helpers.FragmentHelper;

public interface IMainActivity {

	FragmentHelper getFragmentHelper();

	void setFragmentHelper(FragmentHelper fragmentHelper);

	DrawerLayout getmDrawerLayout();

	void setmDrawerLayout(DrawerLayout mDrawerLayout);
	  
}
