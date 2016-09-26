package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 29 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RepostSocialData {

    @SerializedName("result")
    @Expose
    private int result;
    @SerializedName("repost_bonus")
    @Expose
    private int repostBonus;
    @SerializedName("client_bonus")
    @Expose
    private int clientBonus;
    @SerializedName("is_can_repeat")
    @Expose
    private int isCanRepeat;

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
     * The repostBonus
     */
    public int getRepostBonus() {
        return repostBonus;
    }

    /**
     *
     * @param repostBonus
     * The repost_bonus
     */
    public void setRepostBonus(int repostBonus) {
        this.repostBonus = repostBonus;
    }

    /**
     *
     * @return
     * The clientBonus
     */
    public int getClientBonus() {
        return clientBonus;
    }

    /**
     *
     * @param clientBonus
     * The client_bonus
     */
    public void setClientBonus(int clientBonus) {
        this.clientBonus = clientBonus;
    }

    /**
     *
     * @return
     * The isCanRepeat
     */
    public int getIsCanRepeat() {
        return isCanRepeat;
    }

    /**
     *
     * @param isCanRepeat
     * The is_can_repeat
     */
    public void setIsCanRepeat(int isCanRepeat) {
        this.isCanRepeat = isCanRepeat;
    }

}