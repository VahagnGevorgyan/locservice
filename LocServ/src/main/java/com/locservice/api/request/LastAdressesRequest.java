package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 18 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LastAdressesRequest extends WebRequest {

    private String id; // last address id
    private int IdLocality;

    public LastAdressesRequest(String id, int IdLocality) {
        super("getlastaddresses");
        this.id = id;
        this.IdLocality = IdLocality;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdLocality() {
        return IdLocality;
    }

    public void setIdLocality(int idLocality) {
        IdLocality = idLocality;
    }
}
