package com.locservice.api.entities;

/**
 * Created by Vahagn Gevorgyan
 * 23 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChildSeat {

    private String age;
    private String weight;
    private int position;

    public ChildSeat(String age, String weight, int position) {
        this.age = age;
        this.weight = weight;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
