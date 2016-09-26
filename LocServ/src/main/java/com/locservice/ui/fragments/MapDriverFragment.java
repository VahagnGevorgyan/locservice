package com.locservice.ui.fragments;

import com.locservice.api.entities.Driver;
import com.locservice.api.entities.DriverInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public abstract class MapDriverFragment extends MapBaseFragment {

	
	// TODO create other functionality
	
	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMe() {
		// TODO Auto-generated method stub
		
	}

	public void drawRoadDirections(List<LatLng> directions) {
		// TODO Auto-generated method stub

	}

	public abstract void setMyLocation(double latitude, double longitude);

	public abstract void setDriverUI(DriverInfo driverInfo, boolean show);

	public abstract void drawDriversOnMap(List<Driver> driversList);

	public abstract void moveMapPosition(double lat, double lng);

	public abstract void drawRoadDirections(List<String> directions, boolean isAddMarkers, boolean zoom, boolean drawRoad);

	public abstract void setDriverTime(int time);

}
