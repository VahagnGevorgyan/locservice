package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 28 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class UnBindCardRequest extends WebRequest {

    private String id;

    public UnBindCardRequest() {
        super("unbindcard");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
