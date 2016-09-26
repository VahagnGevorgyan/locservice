package com.locservice.ui.helpers;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.ui.MainActivity;
import com.locservice.ui.fragments.MapGoogleFragment;
import com.locservice.ui.fragments.MapViewFragment;
import com.locservice.utils.Logger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 09 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class MapHelper {

    private static final String TAG = MapHelper.class.getSimpleName();

    /**
     * Method calculating map center
     *
     * @param mContext - current context
     * @param mapFragment - map fragment
     * @param layoutTop - top layout
     * @param mLat - marker latitude
     * @param mLng - marker longitude
     * @return - calculated map center
     */
    public static LatLng calcMapCenter(Context mContext, MapGoogleFragment mapFragment, RelativeLayout layoutTop, double mLat, double mLng) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.calcMapCenter : mLat : " + mLat + " : mLng : " + mLng);

        Point appUsableScreenSize = CMApplication.getAppUsableScreenSize(mContext);

        // BOUNDS

        // north - west
        int nwY = layoutTop.getHeight();
        int nwX = 0;
        Point nwPoint = new Point(nwX, nwY);

        // north - east
        int neY = layoutTop.getHeight();
        int neX = appUsableScreenSize.x;
        Point nePoint = new Point(neX, neY);

        // south - west
        float swY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.calcMapCenter : mapFragment.getLayoutSectionDriver() : " + mapFragment.getLayoutSectionDriver());
            LinearLayout driverRealSection = mapFragment.getDriverRealSection(mapFragment.getLayoutSectionDriver());
            float driver_margin_bottom = mContext.getResources().getDimension(R.dimen.dimen_driver_bottom_margin);
            swY = swY - (driverRealSection.getHeight() + driver_margin_bottom);
        }
        int swX = 0;
        Point swPoint = new Point(swX, (int) swY);

        // south - east
        float seY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            LinearLayout driverRealSection = mapFragment.getDriverRealSection(mapFragment.getLayoutSectionDriver());
            float driver_margin_bottom = mContext.getResources().getDimension(R.dimen.dimen_driver_bottom_margin);
            seY = seY - (driverRealSection.getHeight() + driver_margin_bottom);
        }
        int seX = appUsableScreenSize.x;
        Point sePoint = new Point(seX, (int) seY);

        Projection projection = mapFragment.getmMap().getProjection();

        // TEST
        LatLng nwLatLng = projection.fromScreenLocation(nwPoint);
        LatLng neLatLng = projection.fromScreenLocation(nePoint);
        LatLng swLatLng = projection.fromScreenLocation(swPoint);
        LatLng seLatLng = projection.fromScreenLocation(sePoint);


        List<LatLng> bounds = new ArrayList<>();
        bounds.add(nwLatLng);
        bounds.add(neLatLng);
        bounds.add(swLatLng);
        bounds.add(seLatLng);

        LatLngBounds latLngBounds = new LatLngBounds(swLatLng, neLatLng);

        LatLng center = latLngBounds.getCenter();


        Point centerBoundPoint = projection.toScreenLocation(center);

//        Point centerBoundPoint = new Point((int) ((swY - nwY)/2 + nwY), appUsableScreenSize.x/2);
//        LatLng centerLatLng = projection.fromScreenLocation(centerBoundPoint);

        // MARKER
        LatLng markerLatLng = new LatLng(mLat, mLng);

        Point markerPoint = projection.toScreenLocation(markerLatLng);

        Point newCenterPoint = new Point();
        if(centerBoundPoint.y > markerPoint.y) {
            // BOTTOM
            int dist = centerBoundPoint.y - markerPoint.y;
            // new point
            newCenterPoint.y = centerBoundPoint.y - 2 * dist;
            newCenterPoint.x = centerBoundPoint.x;
        } else {
            // TOP
            int dist = markerPoint.y - centerBoundPoint.y;
            // new point
            newCenterPoint.y = markerPoint.y + dist;
            newCenterPoint.x = markerPoint.x;
        }

        LatLng newLatLng = projection.fromScreenLocation(newCenterPoint);

        mapFragment.drawBounds(bounds, center, newLatLng);

        return projection.fromScreenLocation(newCenterPoint);

    } // end method calcMapCenter

    /**
     * Method for calculating map center
     * @param mContext - current context
     * @param mapFragment - map fragment
     * @param layoutTop - top layout
     * @return - calculated map center
     */
    public static LatLng calcMapCenter(Context mContext, MapGoogleFragment mapFragment, RelativeLayout layoutTop) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.calcMapCenter ");

        Point appUsableScreenSize = CMApplication.getAppUsableScreenSize(mContext);

        // north - west
        int nwY = layoutTop.getHeight();
        int nwX = 0;
        Point nwPoint = new Point(nwX, nwY);

        // north - east
        int neY = layoutTop.getHeight();
        int neX = appUsableScreenSize.x;
        Point nePoint = new Point(neX, neY);

        // south - west
        float swY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            LinearLayout driverRealSection = mapFragment.getDriverRealSection(mapFragment.getLayoutSectionDriver());
            float driver_margin_bottom = mContext.getResources().getDimension(R.dimen.dimen_driver_bottom_margin);
            swY = swY - (driverRealSection.getHeight() + driver_margin_bottom);
        }
        int swX = 0;
        Point swPoint = new Point(swX, (int) swY);

        // south - east
        float seY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            LinearLayout driverRealSection = mapFragment.getDriverRealSection(mapFragment.getLayoutSectionDriver());
            float driver_margin_bottom = mContext.getResources().getDimension(R.dimen.dimen_driver_bottom_margin);
            seY = seY - (driverRealSection.getHeight() + driver_margin_bottom);
        }
        int seX = appUsableScreenSize.x;
        Point sePoint = new Point(seX, (int) seY);

        LatLng centerMap = mapFragment.getmMap().getCameraPosition().target;

        Projection projection = mapFragment.getmMap().getProjection();

        LatLng nwLatLng = projection.fromScreenLocation(nwPoint);
        LatLng neLatLng = projection.fromScreenLocation(nePoint);
        LatLng swLatLng = projection.fromScreenLocation(swPoint);
        LatLng seLatLng = projection.fromScreenLocation(sePoint);

        List<LatLng> bounds = new ArrayList<>();
        bounds.add(nwLatLng);
        bounds.add(neLatLng);
        bounds.add(swLatLng);
        bounds.add(seLatLng);

        mapFragment.drawBounds(bounds, null, null);

        LatLngBounds latLngBounds = new LatLngBounds(swLatLng, neLatLng);
        LatLng center = latLngBounds.getCenter();


        Point centerBoundPoint = projection.toScreenLocation(center);


        Point centerMapPoint = projection.toScreenLocation(centerMap);


        Point newCenterPoint = new Point();
        if(centerBoundPoint.y > centerMapPoint.y) {
            // BOTTOM
            int dist = centerBoundPoint.y - centerMapPoint.y;
            // new point
            newCenterPoint.y = centerBoundPoint.y - 2 * dist;
            newCenterPoint.x = centerBoundPoint.x;
        } else {
            // TOP
            int dist = centerMapPoint.y - centerBoundPoint.y;
            // new point
            newCenterPoint.y = centerMapPoint.y + dist;
            newCenterPoint.x = centerMapPoint.x;
        }

        return projection.fromScreenLocation(newCenterPoint);

    } // end method calcMapCenter

    /**
     * Method for getting visible map points
     * @param mContext - current context
     * @param mapFragment - map fragment
     * @param layoutTop - top layout
     * @return - HashMap of visible bounds
     */
    public static HashMap<String, Point> getMapVisibleBounds(Context mContext, MapGoogleFragment mapFragment, RelativeLayout layoutTop) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.getMapVisibleBounds ");

        HashMap<String, Point> pointHashMap = new HashMap<String, Point>();

        Point appUsableScreenSize = CMApplication.getAppUsableScreenSize(mContext);

        // north - west
        int nwY = layoutTop.getHeight();
        int nwX = 0;
        Point nwPoint = new Point(nwX, nwY);
        pointHashMap.put("nw", nwPoint);

        // north - east
        int neY = layoutTop.getHeight();
        int neX = appUsableScreenSize.x;
        Point nePoint = new Point(neX, neY);
        pointHashMap.put("ne", nePoint);

        // south - west
        float swY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            LinearLayout driverRealSection = mapFragment.getDriverRealSection(mapFragment.getLayoutSectionDriver());
            float driver_margin_bottom = mContext.getResources().getDimension(R.dimen.dimen_driver_bottom_margin);
            swY = swY - (driverRealSection.getHeight() + driver_margin_bottom);
        }
        int swX = 0;
        Point swPoint = new Point(swX, (int) swY);
        pointHashMap.put("sw", swPoint);

        // south - east
        float seY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            LinearLayout driverRealSection = mapFragment.getDriverRealSection(mapFragment.getLayoutSectionDriver());
            float driver_margin_bottom = mContext.getResources().getDimension(R.dimen.dimen_driver_bottom_margin);
            seY = seY - (driverRealSection.getHeight() + driver_margin_bottom);
        }
        int seX = appUsableScreenSize.x;;
        Point sePoint = new Point(seX, (int) seY);
        pointHashMap.put("se", sePoint);

        return pointHashMap;

    } // end method getMapVisibleBounds


    public static void moveMapCenterToLocation(Context mContext,
                                               MapGoogleFragment mapGoogleFragment,
                                               double lat, double lng) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapHelper.moveMapCenterToLocation : lat : " + lat + " : lng : " + lng);

        // MOVE MAP TO NEW CENTER
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapHelper.moveMapCenterToLocation :: OLD_CENTER"
                + " : lat : " + mapGoogleFragment.getmMap().getCameraPosition().target.latitude
                + " : lng : " + mapGoogleFragment.getmMap().getCameraPosition().target.longitude);
        LatLng latLng = MapHelper.calcMapCenter(mContext,
                mapGoogleFragment,
                ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).getLayoutTop(),
                lat,
                lng);
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapHelper.moveMapCenterToLocation :: NEW_CENTER : lat : " + latLng.latitude + " : lng : " + latLng.longitude);

        CameraUpdate newCenterUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mapGoogleFragment.getmMap().getCameraPosition().zoom);
//        mapGoogleFragment.getmMap().animateCamera(newCenterUpdate);
        mapGoogleFragment.getmMap().moveCamera(newCenterUpdate);

        drawBounds(mContext, mapGoogleFragment, ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).getLayoutTop());


    }


    /**
     * Method drawing map bounds
     *
     * @param mContext      -   current context
     * @param mapFragment   -   map fragment
     * @param layoutTop     -   layout top
     */
    public static void drawBounds(Context mContext,
                            MapGoogleFragment mapFragment,
                            RelativeLayout layoutTop) {

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapHelper.drawBounds : mContext : " + mContext);

        Point appUsableScreenSize = CMApplication.getAppUsableScreenSize(mContext);

        // north - west
        int nwY = layoutTop.getHeight();
        int nwX = 0;
        Point nwPoint = new Point(nwX, nwY);

        // north - east
        int neY = layoutTop.getHeight();
        int neX = appUsableScreenSize.x;
        Point nePoint = new Point(neX, neY);

        // south - west
        float swY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.calcMapCenter : mapFragment.getLayoutSectionDriver() : " + mapFragment.getLayoutSectionDriver());
            swY = swY - mapFragment.getLayoutSectionDriver().getHeight();
        }
        int swX = 0;
        Point swPoint = new Point(swX, (int) swY);

        // south - east
        float seY = appUsableScreenSize.y - mContext.getResources().getDimension(R.dimen.slide_panel_height_one_field);
        if(mapFragment.getLayoutSectionDriver() != null
                && mapFragment.getLayoutSectionDriver().getVisibility() == View.VISIBLE) {
            seY = seY - mapFragment.getLayoutSectionDriver().getHeight();
        }
        int seX = appUsableScreenSize.x;
        Point sePoint = new Point(seX, (int) seY);

        Projection projection = mapFragment.getmMap().getProjection();

        // TEST
        LatLng nwLatLng = projection.fromScreenLocation(nwPoint);
        LatLng neLatLng = projection.fromScreenLocation(nePoint);
        LatLng swLatLng = projection.fromScreenLocation(swPoint);
        LatLng seLatLng = projection.fromScreenLocation(sePoint);


        List<LatLng> bounds = new ArrayList<>();
        bounds.add(nwLatLng);
        bounds.add(neLatLng);
        bounds.add(swLatLng);
        bounds.add(seLatLng);

        mapFragment.drawBounds(bounds, null, null);

    } // end method drawBounds

    /**
     * Method for setting map padding
     *
     * @param context           - current context
     * @param mapViewFragment   - map view fragment
     * @param revert            - revert padding
     */
    public static void setMapPadding(Context context, MapViewFragment mapViewFragment, boolean revert) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapHelper.setMapPadding : revert : " + revert);

        if(!revert) {
            Point appUsableScreenSize = CMApplication.getAppUsableScreenSize(context);
            int map_padding_top = mapViewFragment.getLayoutTop().getHeight();
            int map_padding_bottom = 0;
            if(((MapGoogleFragment)(mapViewFragment.getMapFragment())).getLayoutSectionDriver() != null
                    && ((MapGoogleFragment)(mapViewFragment.getMapFragment())).getLayoutSectionDriver().getVisibility() == View.VISIBLE) {

                if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapHelper.setMapPadding : getLayoutSectionDriver() : "
                        + ((MapGoogleFragment)(mapViewFragment.getMapFragment())).getLayoutSectionDriver());

                LinearLayout driverRealSection = ((MapGoogleFragment)(mapViewFragment.getMapFragment())).getDriverRealSection(((MapGoogleFragment)(mapViewFragment.getMapFragment())).getLayoutSectionDriver());
                float driver_margin_bottom = context.getResources().getDimension(R.dimen.dimen_driver_bottom_margin);
                map_padding_bottom += (driverRealSection.getHeight());
            }
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapHelper.setMapPadding : map_padding_top : " + map_padding_top
                    + " : map_padding_bottom : " + map_padding_bottom);
            // Set Map padding's
            if (((MapGoogleFragment)mapViewFragment.getMapFragment()).getmMap() != null)
                ((MapGoogleFragment)mapViewFragment.getMapFragment()).getmMap().setPadding(10, map_padding_top, 10, map_padding_bottom);
        } else {
            // set Map padding
            if (((MapGoogleFragment)mapViewFragment.getMapFragment()).getmMap() != null)
                ((MapGoogleFragment)mapViewFragment.getMapFragment()).getmMap().setPadding(10, 10, 10, 10);
        }

    } // end method setMapPadding

    /**
     * Method for calculating
     * @param lat1  -   start point latitude
     * @param lon1  -   start point longitude
     * @param lat2  -   end point latitude
     * @param lon2  -   end point longitude
     * @return      -   middle point
     */
    public static LatLng midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(lat3, lon3);

    } // end method midPoint

    /**
     * Method to decode polyline points
     *
     * @param encoded   - encoded string
     * @return          - list of points
     */
    public static List<LatLng> decodePolylinePoints(String encoded) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.decodePolylinePoints : encoded : " + encoded);

        List<LatLng> polylinePoints = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            polylinePoints.add(p);
        }

        return polylinePoints;

    } // end method decodePolylinePoints

}
