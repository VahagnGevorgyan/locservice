package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 26 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class BindCardInfo {
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("MD")
    @Expose
    private String MD;
    @SerializedName("PaReq")
    @Expose
    private String paReq;
    @SerializedName("TermUrl")
    @Expose
    private String TermUrl;
    @SerializedName("bind_idhash")
    @Expose
    private String bind_idhash;


    /**
     *
     * @return
     * The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     * The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return
     * The MD
     */
    public String getMD() {
        return MD;
    }

    /**
     *
     * @param MD
     * The MD
     */
    public void setMD(String MD) {
        this.MD = MD;
    }

    /**
     *
     * @return
     * The paReq
     */
    public String getPaReq() {
        return paReq;
    }

    /**
     *
     * @param paReq
     * The paReq
     */
    public void setPaReq(String paReq) {
        this.paReq = paReq;
    }

    /**
     *
     * @return
     * The TermUrl
     */
    public String getTermUrl() {
        return TermUrl;
    }

    /**
     *
     * @param TermUrl
     * The TermUrl
     */
    public void setTermUrl(String TermUrl) {
        this.TermUrl = TermUrl;
    }

    /**
     *
     * @return
     * The bind_idhash
     */
    public String getBind_idhash() {
        return bind_idhash;
    }

    /**
     *
     * @param bind_idhash
     * The bind_idhash
     */
    public void setBind_idhash(String bind_idhash) {
        this.bind_idhash = bind_idhash;
    }

}