package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 10 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RemoveAddressRequest extends WebRequest {
    private int id;

    public RemoveAddressRequest(int id) {
        super("removeAddress");
        this.id = id;
    }

    /**
     * Method for getting address id
     * @return - Address id
     */
    public int getId() {
        return id;

    } // end method getId

    /**
     * Method for setting address id
     * @param id - Address id
     */
    public void setId(int id) {
        this.id = id;

    } // end method setId
}
