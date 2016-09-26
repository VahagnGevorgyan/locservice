package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 15 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Statistics {

    @SerializedName("orders_count")
    @Expose
    private int ordersCount;
    @SerializedName("orders_sum")
    @Expose
    private int ordersSum;
    @SerializedName("distance")
    @Expose
    private int distance;
    @SerializedName("time")
    @Expose
    private int time;
    @SerializedName("distance_bonus")
    @Expose
    private int distanceBonus;
    @SerializedName("avg_distance")
    @Expose
    private int avgDistance;
    @SerializedName("time_bonus")
    @Expose
    private int timeBonus;
    @SerializedName("sum_bonus")
    @Expose
    private int sumBonus;
    @SerializedName("avg_time")
    @Expose
    private int avgTime;
    @SerializedName("avg_time_speed")
    @Expose
    private int avgTimeSpeed;
    @SerializedName("avg_price")
    @Expose
    private int avgPrice;

    /**
     *
     * @return
     * The ordersCount
     */
    public int getOrdersCount() {
        return ordersCount;
    }

    /**
     *
     * @param ordersCount
     * The orders_count
     */
    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    /**
     *
     * @return
     * The ordersSum
     */
    public int getOrdersSum() {
        return ordersSum;
    }

    /**
     *
     * @param ordersSum
     * The orders_sum
     */
    public void setOrdersSum(int ordersSum) {
        this.ordersSum = ordersSum;
    }

    /**
     *
     * @return
     * The distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The time
     */
    public int getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     *
     * @return
     * The distanceBonus
     */
    public int getDistanceBonus() {
        return distanceBonus;
    }

    /**
     *
     * @param distanceBonus
     * The distance_bonus
     */
    public void setDistanceBonus(int distanceBonus) {
        this.distanceBonus = distanceBonus;
    }

    /**
     *
     * @return
     * The avgDistance
     */
    public int getAvgDistance() {
        return avgDistance;
    }

    /**
     *
     * @param avgDistance
     * The avg_distance
     */
    public void setAvgDistance(int avgDistance) {
        this.avgDistance = avgDistance;
    }

    /**
     *
     * @return
     * The timeBonus
     */
    public int getTimeBonus() {
        return timeBonus;
    }

    /**
     *
     * @param timeBonus
     * The time_bonus
     */
    public void setTimeBonus(int timeBonus) {
        this.timeBonus = timeBonus;
    }

    /**
     *
     * @return
     * The sumBonus
     */
    public int getSumBonus() {
        return sumBonus;
    }

    /**
     *
     * @param sumBonus
     * The sum_bonus
     */
    public void setSumBonus(int sumBonus) {
        this.sumBonus = sumBonus;
    }

    /**
     *
     * @return
     * The avgTime
     */
    public int getAvgTime() {
        return avgTime;
    }

    /**
     *
     * @param avgTime
     * The avg_time
     */
    public void setAvgTime(int avgTime) {
        this.avgTime = avgTime;
    }

    /**
     *
     * @return
     * The avgTimeSpeed
     */
    public int getAvgTimeSpeed() {
        return avgTimeSpeed;
    }

    /**
     *
     * @param avgTimeSpeed
     * The avg_time_speed
     */
    public void setAvgTimeSpeed(int avgTimeSpeed) {
        this.avgTimeSpeed = avgTimeSpeed;
    }

    /**
     *
     * @return
     * The avgPrice
     */
    public int getAvgPrice() {
        return avgPrice;
    }

    /**
     *
     * @param avgPrice
     * The avg_price
     */
    public void setAvgPrice(int avgPrice) {
        this.avgPrice = avgPrice;
    }

}