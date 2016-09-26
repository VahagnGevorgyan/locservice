package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 27 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CheckCardBindRequest extends WebRequest {

    private String bind_idhash;

    public CheckCardBindRequest() {
        super("checkCardBind");
    }

    public String getBind_idhash() {
        return bind_idhash;
    }

    public void setBind_idhash(String bind_idhash) {
        this.bind_idhash = bind_idhash;
    }
}
