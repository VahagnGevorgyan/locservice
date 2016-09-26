package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CheckfilesChangesRequest extends WebRequest {

    private String tariffs_date;
    private String cities_date;
    private String landmarks_date;
    private int IdLocality;

    public CheckfilesChangesRequest(int id_locality,
                                    String locale,
                                    String tariff_date,
                                    String landmark_date,
                                    String cities_date) {
        super("checkfilesChanges", locale);
        this.IdLocality = id_locality;
        this.tariffs_date = tariff_date;
        this.landmarks_date = landmark_date;
        this.cities_date = cities_date;
    }

    public void setId_locality(int id_locality) {
        this.IdLocality = id_locality;
    }

    public int getId_locality() {
        return IdLocality;
    }

    public void setTariffs_date(String tariff_date) {
        this.tariffs_date = tariff_date;
    }

    public void setCities_date(String cities_date) {
        this.cities_date = cities_date;
    }

    public void setLandmarks_date(String landmark_date) {
        this.landmarks_date = landmark_date;
    }

    public String getTariffs_date() {
        return tariffs_date;
    }

    public String getCities_date() {
        return cities_date;
    }

    public String getLandmarks_date() {
        return landmarks_date;
    }
}
