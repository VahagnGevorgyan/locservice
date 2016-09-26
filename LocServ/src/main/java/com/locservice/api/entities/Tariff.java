package com.locservice.api.entities;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Tariff {

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("is_default")
	@Expose
	private String isDefault;
	@SerializedName("car_models")
	@Expose
	private String carModels;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("short_name")
	@Expose
	private String shortname;
	@SerializedName("intervals")
	@Expose
	private List<TariffInterval> intervals = new ArrayList<TariffInterval>();
	@SerializedName("services")
	@Expose
	private List<TariffService> services = new ArrayList<TariffService>();
	@SerializedName("fixed_price_routes")
	@Expose
	private List<FixedPriceRoute> fixedPriceRoutes = new ArrayList<FixedPriceRoute>();
	@SerializedName("airport_min_price")
	@Expose
	private String airportMinPrice = "";

	private int cityId;

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	/**
	 *
	 * @return
	 * The id
	 */
	public String getID() {
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
	 * The isDefault
	 */
	public String getIsDefault() {
		return isDefault;
	}

	/**
	 *
	 * @param isDefault
	 * The is_default
	 */
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 *
	 * @return
	 * The carModels
	 */
	public String getCarModels() {
		return carModels;
	}

	/**
	 *
	 * @param carModels
	 * The car_models
	 */
	public void setCarModels(String carModels) {
		this.carModels = carModels;
	}

	/**
	 *
	 * @return
	 * The name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param name
	 * The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return
	 * The shortname
	 */
	public String getShortname() {
		return shortname;
	}

	/**
	 *
	 * @param shortname
	 * The shortname
	 */
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	/**
	 *
	 * @return
	 * The intervals
	 */
	public List<TariffInterval> getIntervals() {
		return intervals;
	}

	/**
	 *
	 * @param intervals
	 * The intervals
	 */
	public void setIntervals(List<TariffInterval> intervals) {
		this.intervals = intervals;
	}

	/**
	 *
	 * @return
	 * The services
	 */
	public List<TariffService> getServices() {
		return services;
	}

	/**
	 *
	 * @param services
	 * The services
	 */
	public void setServices(List<TariffService> services) {
		this.services = services;
	}

	/**
	 *
	 * @return
	 * The fixedPriceRoutes
	 */
	public List<FixedPriceRoute> getFixedPriceRoutes() {
		return fixedPriceRoutes;
	}

	/**
	 *
	 * @param fixedPriceRoutes
	 * The fixed_price_routes
	 */
	public void setFixedPriceRoutes(List<FixedPriceRoute> fixedPriceRoutes) {
		this.fixedPriceRoutes = fixedPriceRoutes;
	}

	/**
	 *
	 * @return
	 * The airportMinPrice
	 */
	public String getAirportMinPrice() {
		return airportMinPrice;
	}

	/**
	 *
	 * @param airportMinPrice
	 * The airport_min_price
	 */
	public void setAirportMinPrice(String airportMinPrice) {
		this.airportMinPrice = airportMinPrice;
	}


}