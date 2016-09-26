package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 29 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RepostSocialRequest extends WebRequest {

    private int social_id;

    public RepostSocialRequest(int social_id) {
        super("repostsocial");
        this.social_id = social_id;
    }

    public int getSocial_id() {
        return social_id;
    }

    public void setSocial_id(int social_id) {
        this.social_id = social_id;
    }
}
