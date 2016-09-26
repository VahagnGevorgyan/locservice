package com.locservice.ui.helpers;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Vahagn Gevorgyan
 * 28 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PolygonItem {

    private String id;
    private String name;
    private String latitude;
    private String longitude;
    private PolygonType polygonType;

    private LatLngBounds polygonBounds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public LatLngBounds getPolygonBounds() {
        return polygonBounds;
    }

    public void setPolygonBounds(LatLngBounds polygonBounds) {
        this.polygonBounds = polygonBounds;
    }

    public PolygonType getPolygonType() {
        return polygonType;
    }

    public void setPolygonType(PolygonType polygonType) {
        this.polygonType = polygonType;
    }
}
