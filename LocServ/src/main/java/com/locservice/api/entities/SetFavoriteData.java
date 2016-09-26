package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class SetFavoriteData extends APIError {


    @SerializedName("result")
    @Expose
    private int result;
    @SerializedName("id_favorite")
    @Expose
    private String idFavorite;
    @SerializedName("id_old")
    @Expose
    private String idOld;

    /**
     *
     * @return
     * The result
     */
    public int getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The idFavorite
     */
    public String getIdFavorite() {
        return idFavorite;
    }

    /**
     *
     * @param idFavorite
     * The id_favorite
     */
    public void setIdFavorite(String idFavorite) {
        this.idFavorite = idFavorite;
    }

    /**
     *
     * @return
     * The idOld
     */
    public String getIdOld() {
        return idOld;
    }

    /**
     *
     * @param idOld
     * The id_old
     */
    public void setIdOld(String idOld) {
        this.idOld = idOld;
    }

}
