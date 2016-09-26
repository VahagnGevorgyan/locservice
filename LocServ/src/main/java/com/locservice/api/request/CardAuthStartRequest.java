package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 26 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CardAuthStartRequest {

    private String MD;
    private String PaReq;
    private String TermUrl;
    private String AUTH_URL;

    public String getAUTH_URL() {
        return AUTH_URL;
    }

    public void setAUTH_URL(String AUTH_URL) {
        this.AUTH_URL = AUTH_URL;
    }

    public String getMD() {
        return MD;
    }

    public void setMD(String MD) {
        this.MD = MD;
    }

    public String getPaReq() {
        return PaReq;
    }

    public void setPaReq(String paReq) {
        PaReq = paReq;
    }

    public String getTermUrl() {
        return TermUrl;
    }

    public void setTermUrl(String termUrl) {
        TermUrl = termUrl;
    }
}
