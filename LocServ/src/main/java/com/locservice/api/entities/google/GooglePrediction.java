package com.locservice.api.entities.google;

import com.locservice.ui.utils.SearchType;
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
public class GooglePrediction {

    // Google types
    public static final String TYPE_AIRPORT = "airport";
    public static final String TYPE_TRAIN_STATION = "train_station";
    public static final String TYPE_SUBWAY_STATION = "subway_station";
    public static final String TYPE_STREET_ADDRESS = "street_address";
    public static final String TYPE_ESTABLISHMENT = "establishment";
    public static final String TYPE_PREMISE = "premise";
    // Not google(LocService) types
    public static final String TYPE_FAVORITE = "favorite";
    public static final String TYPE_LAST_ADDRESS = "last_address";


    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("matched_substrings")
    @Expose
    private List<GoogleMatchedSubstring> matchedSubstrings = new ArrayList<GoogleMatchedSubstring>();
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("terms")
    @Expose
    private List<GoogleTerm> terms = new ArrayList<GoogleTerm>();
    @SerializedName("types")
    @Expose
    private List<String> types = new ArrayList<String>();

    private SearchType searchType;

    private String address_secondary;

    public String getAddress_secondary() {
        return address_secondary;
    }

    public void setAddress_secondary(String address_secondary) {
        this.address_secondary = address_secondary;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The matchedSubstrings
     */
    public List<GoogleMatchedSubstring> getMatchedSubstrings() {
        return matchedSubstrings;
    }

    /**
     *
     * @param matchedSubstrings
     * The matched_substrings
     */
    public void setMatchedSubstrings(List<GoogleMatchedSubstring> matchedSubstrings) {
        this.matchedSubstrings = matchedSubstrings;
    }

    /**
     *
     * @return
     * The placeId
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     *
     * @param placeId
     * The place_id
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    /**
     *
     * @return
     * The reference
     */
    public String getReference() {
        return reference;
    }

    /**
     *
     * @param reference
     * The reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     *
     * @return
     * The terms
     */
    public List<GoogleTerm> getTerms() {
        return terms;
    }

    /**
     *
     * @param terms
     * The terms
     */
    public void setTerms(List<GoogleTerm> terms) {
        this.terms = terms;
    }

    /**
     *
     * @return
     * The types
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     *
     * @param types
     * The types
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

}
