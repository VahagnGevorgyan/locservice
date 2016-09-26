package com.locservice.utils;

import com.locservice.api.entities.GooglePlace;

/**
 * Created by Vahagn Gevorgyan
 * 02 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface GetAddress {

    public GooglePlace getAddress();

    public void setAddress(GooglePlace address);
}
