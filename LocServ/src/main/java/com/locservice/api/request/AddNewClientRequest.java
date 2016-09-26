package com.locservice.api.request;

import com.locservice.BuildConfig;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AddNewClientRequest {

    private String method;
    private String name;
    private String phone;
    private int company_id;
    private String devid;
    private String ver;
    private String brand;
    private String model;
    private String os_version;
    private String phone_os;

    public AddNewClientRequest(String phone, int company_id, String devid, String brand, String model, String os_version) {
        this.method = "addnewclient";
        this.name = "";
        this.phone = phone;
        this.company_id = company_id;
        this.devid = devid;
        this.ver = BuildConfig.VERSION_NAME;
        this.brand = brand;
        this.model = model;
        this.os_version = os_version;
        this.phone_os = "android";
    }

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The company_id
     */
    public int getCompany_id() {
        return company_id;
    }

    /**
     *
     * @param company_id
     * The company_id
     */
    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    /**
     *
     * @return
     * The devid
     */
    public String getDevid() {
        return devid;
    }

    /**
     *
     * @param devid
     * The devid
     */
    public void setDevid(String devid) {
        this.devid = devid;
    }


    /**
     *
     * @return
     * The method
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @param method
     * The method
     */
    public void setMethod(String method) {
        this.method = method;
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
     * The ver
     */
    public String getVer() {
        return ver;
    }

    /**
     *
     * @param ver
     * The ver
     */
    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     *
     * @return
     * The brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     *
     * @param brand
     * The brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     *
     * @return
     * The model
     */
    public String getModel() {
        return model;
    }

    /**
     *
     * @param model
     * The model
     */
    public void setModel(String model) {
        this.model = model;
    }


    /**
     *
     * @return
     * The os_version
     */
    public String getOs_version() {
        return os_version;
    }

    /**
     *
     * @param os_version
     * The os_version
     */
    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public String getPhone_os() {
        return phone_os;
    }

    public void setPhone_os(String phone_os) {
        this.phone_os = phone_os;
    }
}
