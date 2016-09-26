package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 27 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CardsData {

    @SerializedName("cards")
    @Expose
    private List<CardInfo> cards = new ArrayList<CardInfo>();

    /**
     *
     * @return
     * The cards
     */
    public List<CardInfo> getCards() {
        return cards;
    }

    /**
     *
     * @param cards
     * The cards
     */
    public void setCards(List<CardInfo> cards) {
        this.cards = cards;
    }

}
