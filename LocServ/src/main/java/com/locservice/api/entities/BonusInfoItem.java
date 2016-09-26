package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 01 September 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class BonusInfoItem {
    @SerializedName("value_title")
    @Expose
    private String valueTitle;
    @SerializedName("value_description")
    @Expose
    private String valueDescription;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("can_get_bonus")
    @Expose
    private int canGetBonus;

    /**
     *
     * @return
     * The valueTitle
     */
    public String getValueTitle() {
        return valueTitle;
    }

    /**
     *
     * @param valueTitle
     * The value_title
     */
    public void setValueTitle(String valueTitle) {
        this.valueTitle = valueTitle;
    }

    /**
     *
     * @return
     * The valueDescription
     */
    public String getValueDescription() {
        return valueDescription;
    }

    /**
     *
     * @param valueDescription
     * The value_description
     */
    public void setValueDescription(String valueDescription) {
        this.valueDescription = valueDescription;
    }

    /**
     *
     * @return
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     * The canGetBonus
     */
    public int getCanGetBonus() {
        return canGetBonus;
    }

    /**
     *
     * @param canGetBonus
     * The can_get_bonus
     */
    public void setCanGetBonus(int canGetBonus) {
        this.canGetBonus = canGetBonus;
    }

}