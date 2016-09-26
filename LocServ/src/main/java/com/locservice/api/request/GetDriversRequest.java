package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetDriversRequest extends WebRequest {

    private double latitude;
    private double longitude;
    private float radius;
    private String IdLocality;
    private int tariff;

    private int NoSmoking;
    private int g_width;
    private int conditioner;
    private int animal;
    private int need_check;
    private int need_wifi;
    private int need_card;
    private int e_type;
    private int get_only_count; //returns only free drivers
    private float baby_seat;
    private int limit;

    public GetDriversRequest() {
        super("getdrivers");
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getIdLocality() {
        return IdLocality;
    }

    public void setIdLocality(String idLocality) {
        IdLocality = idLocality;
    }

    public int getTariff() {
        return tariff;
    }

    public void setTariff(int tariff) {
        this.tariff = tariff;
    }

    public int getNoSmoking() {
        return NoSmoking;
    }

    public void setNoSmoking(int noSmoking) {
        NoSmoking = noSmoking;
    }

    public int getG_width() {
        return g_width;
    }

    public void setG_width(int g_width) {
        this.g_width = g_width;
    }

    public int getConditioner() {
        return conditioner;
    }

    public void setConditioner(int conditioner) {
        this.conditioner = conditioner;
    }

    public int getAnimal() {
        return animal;
    }

    public void setAnimal(int animal) {
        this.animal = animal;
    }

    public int getNeed_check() {
        return need_check;
    }

    public void setNeed_check(int need_check) {
        this.need_check = need_check;
    }

    public int getNeed_wifi() {
        return need_wifi;
    }

    public void setNeed_wifi(int need_wifi) {
        this.need_wifi = need_wifi;
    }

    public int getNeed_card() {
        return need_card;
    }

    public void setNeed_card(int need_card) {
        this.need_card = need_card;
    }

    public int getE_type() {
        return e_type;
    }

    public void setE_type(int e_type) {
        this.e_type = e_type;
    }

    public int getGet_only_count() {
        return get_only_count;
    }

    public void setGet_only_count(int get_only_count) {
        this.get_only_count = get_only_count;
    }

    public float getBaby_seat() {
        return baby_seat;
    }

    public void setBaby_seat(float baby_seat) {
        this.baby_seat = baby_seat;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
