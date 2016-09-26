package com.locservice.utils;

import android.content.Context;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.City;
import com.locservice.db.CityDBManager;
import com.locservice.db.LanguageDBManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 06 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DistanceHelper {

    public static final String TAG = DistanceHelper.class.getSimpleName();


    /**
     * Method for getting CityId by currentLatLng
     * @param mLatLng
     * @return
     */
    public static String getCityIdByCoordinates(Context context, LatLng mLatLng) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DistanceHelper.getCityIdByCoordinates : lat : "
                + mLatLng.latitude + " : lng : " + mLatLng.longitude);

        CityDBManager cityDBManager = new CityDBManager(context);
        int languageID = new LanguageDBManager(context).getLanguageIdByLocale("ru");
        List<City> allCities = cityDBManager.getAllCities(languageID);
        if(allCities != null && allCities.size() > 0) {
            for (City item : allCities) {
                LatLng cityLatLng = new LatLng(Double.valueOf(item.getLatitude()), Double.valueOf(item.getLongitude()));
                if(isPointInCity(mLatLng, cityLatLng)) {
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DistanceHelper.distance : city_name : " + item.getName());
                    return item.getId();
                }
            }
        }
        return "";

    } // end method getCityIdByCoordinates


    /**
     * Method for checking point in city
     * @param pLatLng
     * @param cityLatLng
     * @return
     */
    public static boolean isPointInCity(LatLng pLatLng, LatLng cityLatLng) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DistanceHelper.isPointInCity : pLatLng.latitude : "
                + pLatLng.latitude + " : pLatLng.longitude : " + pLatLng.longitude
                + " : cityLatLng.latitude : " + cityLatLng.latitude
                + " : cityLatLng.longitude : " + cityLatLng.longitude);

        double disKilometers = distance(pLatLng, cityLatLng, "K");
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DistanceHelper.isPointInCity : disKilometers : " + disKilometers);

        return disKilometers < 40;

    } // end method isPointInCity

    public static double distance(LatLng fLatLng, LatLng sLatLng, String unit) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DistanceHelper.distance : fLatLng.latitude : " + fLatLng.latitude + " : fLatLng.longitude : " + fLatLng.longitude
                + " : sLatLng.latitude : " + sLatLng.latitude + " : sLatLng.longitude : " + sLatLng.longitude);

        double theta = fLatLng.longitude - sLatLng.longitude;
        double dist = Math.sin(deg2rad(fLatLng.latitude)) * Math.sin(deg2rad(sLatLng.latitude))
                + Math.cos(deg2rad(fLatLng.latitude)) * Math.cos(deg2rad(sLatLng.latitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);

    } // end method distance

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    /**
     * Method for getting polygon and path intersection
     */
    public static void getPolygonAndPathIntersection() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DistanceHelper.getPolygonAndPathIntersection ");

//        Polygon polygon;
//        polygon.setPoints(new ArrayList<LatLng>());

    } // end method getPolygonAndPathIntersection

    public List<LatLng> getPolygonPoints() {
        ArrayList<LatLng> points = new ArrayList<>();


        return points;
    }

    public void getpointsJsonArray() {

    }

}
