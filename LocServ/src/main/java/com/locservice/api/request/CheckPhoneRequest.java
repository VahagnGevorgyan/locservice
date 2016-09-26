package com.locservice.api.request;


/**
 * Created by Vahagn Gevorgyan
 * 18 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CheckPhoneRequest extends WebRequest {

    private String phone;
    private String devid;
    private String code;

    public CheckPhoneRequest(String phone, String devid, String code, String ver) {
        super("checkphone");
        this.phone = phone;
        this.devid = devid;
        this.code = code;
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
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code = code;
    }
}
