package com.locservice.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class MapBaseFragment extends Fragment {

	public MapBaseFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRetainInstance(true);
	}

	public abstract void zoomIn();

	public abstract void zoomOut();

	public abstract void showMe();
	
	public abstract void showControls(View view);
	
	public abstract void hideControls(View view);
	
	public abstract void showDriver(View view);
	
	public abstract void hideDriver(View view);
}
