package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleRow {

    @SerializedName("elements")
    @Expose
    private List<GoogleElement> elements = new ArrayList<GoogleElement>();

    /**
     *
     * @return
     * The elements
     */
    public List<GoogleElement> getElements() {
        return elements;
    }

    /**
     *
     * @param elements
     * The elements
     */
    public void setElements(List<GoogleElement> elements) {
        this.elements = elements;
    }

}
