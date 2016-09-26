package com.locservice.ui.helpers;

import android.location.Location;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.Airport;
import com.locservice.api.entities.PolygonPoint;
import com.locservice.api.entities.Terminal;
import com.locservice.api.manager.LandmarkManager;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vahagn Gevorgyan
 * 8 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PolygonHelper {

    private static final String TAG = PolygonHelper.class.getSimpleName();


    /**
     * Method for checking is ray cross segment
     * @param point - point
     * @param a - first point
     * @param b - second point
     * @return - is crosses segment
     */
    public boolean rayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        // Ray Casting algorithm checks, for each segment, if the point is 1) to the left of the segment and 2) not above nor below the segment. If these two conditions are met, it returns true
        double px = point.longitude,
                py = point.latitude,
                ax = a.longitude,
                ay = a.latitude,
                bx = b.longitude,
                by = b.latitude;
        if (ay > by) {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0 || ax <0 || bx <0) { px += 360; ax+=360; bx+=360; }
        // if the point has the same latitude as a or b, increase slightly py
        if (py == ay || py == by) py += 0.00000001;


        // if the point is above, below or to the right of the segment, it returns false
        if ((py > by || py < ay) || (px > Math.max(ax, bx))){
            return false;
        }
        // if the point is not above, below or to the right and is to the left, return true
        else if (px < Math.min(ax, bx)){
            return true;
        }
        // if the two above conditions are not met, you have to compare the slope of segment [a,b] (the red one here) and segment [a,p] (the blue one here) to see if your point is to the left of segment [a,b] or not
        else {
            double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
            double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
            return (blue >= red);
        }

    } // end method rayCrossesSegment


    public static class Line {
        LatLng pointFrom;
        LatLng pointTo;

        public Line(LatLng pointFrom, LatLng pointTo) {
            this.pointFrom = pointFrom;
            this.pointTo = pointTo;
        }
    }

    /**
     * Method for checking is point in polygon
     *
     * @param point - point for checking
     * @param bound - polygon bound
     * @return  - true : in polygon - false : not in polygon
     */
    public static boolean isPointInPolygon(LatLng point, LatLngBounds bound) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.isPointInPolygon : point : lat : " + point.latitude + " : lng : " + point.longitude);
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.isPointInPolygon : bound : " + bound);

        return bound.contains(point);

    } // end method isPointInPolygon


    /**
     * Method for loading all polygons
     */
    public static void loadPolygons(ICallBack context, List<Airport> airports) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.loadPolygons : airports : " + airports);

        LandmarkManager landmarkManager = new LandmarkManager(context);

        if(airports != null && airports.size() >= 0) {
            // Load Polygons
            for (Airport airport : airports) {
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.loadPolygons : airport.getId() : " + airport.getId());
                landmarkManager.GetAirportPolygon(airport.getId(), "");
                if(airport.getTerminals() != null && airport.getTerminals().size() > 0) {
                    for (Terminal terminal :
                            airport.getTerminals()) {
                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.loadPolygons : airport.getId() : " + airport.getId() + " : terminal.getId() : " + terminal.getId());
                        landmarkManager.GetAirportPolygon(airport.getId(), terminal.getId());
                    }
                }
            }
        }

    } // end method loadPolygons

    /**
     * Method for getting polygon bounds by polygon JSON string
     * @param jsonStr - polygon json string
     * @return - polygon bounds
     */
    public static LatLngBounds getPolygonBounds(String jsonStr) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.getPolygonBounds : jsonStr : " + jsonStr);

        LatLngBounds polygonBounds = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.getPolygonBounds : jsonArray : " + jsonArray);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(int i = 0; i<jsonArray.length(); i++) {
                JSONObject itemObject = (JSONObject) jsonArray.get(i);
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.getPolygonBounds : itemObject : " + itemObject);
                if(itemObject != null) {
                    double lat = Double.valueOf(itemObject.getString("latitude"));
                    double lng = Double.valueOf(itemObject.getString("longitude"));
                    builder.include(new LatLng (lat, lng));
                }
            }
            polygonBounds = builder.build();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return polygonBounds;

    } // end method getPolygonBounds

    /**
     * Method for getting polygon points by polygon JSON string
     * @param jsonStr - polygon json string
     * @return - polygon bounds
     */
    public static List<LatLng> getPolygonPoints(String jsonStr) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.getPolygonBounds : jsonStr : " + jsonStr);

        List<LatLng> latLngList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.getPolygonBounds : jsonArray : " + jsonArray);
            for(int i = 0; i<jsonArray.length(); i++) {
                JSONObject itemObject = (JSONObject) jsonArray.get(i);
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.getPolygonBounds : itemObject : " + itemObject);
                if(itemObject != null) {
                    double lat = Double.valueOf(itemObject.getString("latitude"));
                    double lng = Double.valueOf(itemObject.getString("longitude"));
                    latLngList.add(new LatLng(lat, lng));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return latLngList;

    } // end method getPolygonBounds

    /**
     * Method for getting polygon bounds by polygon points
     *
     * @param polygonPoints - polygon points
     * @return - polygon bounds
     */
    public static LatLngBounds getPolygonBounds(List<PolygonPoint> polygonPoints) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: PolygonHelper.getPolygonBounds : polygonPoints : " + polygonPoints);

        LatLngBounds polygonBounds = null;
        if(polygonPoints != null && polygonPoints.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (PolygonPoint polygonPoint :
                    polygonPoints) {
                builder.include(new LatLng (polygonPoint.getLatitude(), polygonPoint.getLongitude()));
            }
            polygonBounds = builder.build();
        }

        return polygonBounds;

    } // end method getPolygonBounds

    /**
     * Method for getting distance by meters
     * @param first - First point LatLng
     * @param second - Second point LatLng
     * @return - Distance by meters
     */
    public static double getTwoPointsDistanceInMeters(LatLng first, LatLng second) {
        Location locationFirst = new Location("First");
        locationFirst.setLatitude(first.latitude);
        locationFirst.setLongitude(first.longitude);

        Location locationSecond = new Location("Second");
        locationSecond.setLatitude(second.latitude);
        locationSecond.setLongitude(second.longitude);

        return locationFirst.distanceTo(locationSecond);

    } // end method getTwoPointsDistanceInMeters

    /**
     * Method to decode polyline points
     * @param encoded - encoded string of points
     * @return - list of polygons
     */
    public static List<LatLng> decodePolylinePoints(String encoded) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapGoogleFragment.decodePolylinePoints : encoded : " + encoded);

        List<LatLng> polylinePoints = new ArrayList<>();
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

    /**
     * Method for getting distances out and in polygon
     * @param routePointList - Route points list
     * @param polygonBounds - Polygon bounds
     * @return - List of distance in and out polygon
     */
    public static List<Float> getDistancesOutAndInPolygon(List<String> routePointList, LatLngBounds polygonBounds) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: PolygonHelper.getDistancesOutAndInPolygon : routePointList : " + routePointList
                + " : polygonBounds : " + polygonBounds);

        float inPolygonDistance = 0f;
        float outPolygonDistance = 0f;
        List<LatLng> routeList = new ArrayList<>();
        for (String pointItem : routePointList) {
            routeList.addAll(decodePolylinePoints(pointItem));
        }

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: PolygonHelper.getDistancesOutAndInPolygon : routeList : size : " + routeList.size());
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: PolygonHelper.getDistancesOutAndInPolygon : routeList : json : " + (new Gson().toJson(routeList)));

        boolean isPreviousInPolygon = false;
        for (int i = 0; i < routeList.size(); i++) {
            boolean isCurrentInPolygon = polygonBounds.contains(routeList.get(i));

            if (i > 0) {
                if (isCurrentInPolygon && isPreviousInPolygon) {
                    inPolygonDistance += getTwoPointsDistanceInMeters(routeList.get(i - 1), routeList.get(i));
                } else if (!isCurrentInPolygon && isPreviousInPolygon) {
                    inPolygonDistance += getTwoPointsDistanceInMeters(routeList.get(i - 1), routeList.get(i));
                } else if (isCurrentInPolygon && !isPreviousInPolygon) {
                    inPolygonDistance += getTwoPointsDistanceInMeters(routeList.get(i - 1), routeList.get(i));
                } else {
                    outPolygonDistance += getTwoPointsDistanceInMeters(routeList.get(i - 1), routeList.get(i));
                }
            }

            isPreviousInPolygon = isCurrentInPolygon;
        }

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: PolygonHelper.getDistancesOutAndInPolygon : inPolygonDistance : " + inPolygonDistance
                + " : outPolygonDistance : " + outPolygonDistance);

        List<Float> distanceLest = new ArrayList<>();
        distanceLest.add(inPolygonDistance);
        distanceLest.add(outPolygonDistance);

        return distanceLest;

    } // end method getDistancesOutAndInPolygon

    /**
     * Method for getting closest point in polygon from point
     * @param point - Point out polygon
     * @param latLngPolygonList - List of polygon bounds latLng
     * @return - Closest point in polygon from point
     */
    public static LatLng getClosestPointInPolygonFromPoint(LatLng point, List<LatLng> latLngPolygonList) {
        if (point != null && latLngPolygonList != null) {
            double minDistance = getTwoPointsDistanceInMeters(point, latLngPolygonList.get(0));
            LatLng closestLatLng = latLngPolygonList.get(0);
            for (int i = 1; i < latLngPolygonList.size(); i++)  {
                double distance = getTwoPointsDistanceInMeters(point, latLngPolygonList.get(i));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestLatLng = latLngPolygonList.get(i);
                }
            }

            return closestLatLng;
        }

        return null;

    } // end method getClosestPointInPolygonFromPoint




}
