package com.locservice.api.entities;


/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Language {

    private int id;

    private String long_name;

    private String short_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLong_name() {
        return long_name;
    }

    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

}
