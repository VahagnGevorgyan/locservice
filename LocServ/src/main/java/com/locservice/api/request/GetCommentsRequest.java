package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 18 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetCommentsRequest extends WebRequest {

    private long id_comment;

    public GetCommentsRequest(long id_comment) {
        super("getcomments");
        this.id_comment = id_comment;
    }

    public long getId_comment() {
        return id_comment;
    }

    public void setId_comment(long id_comment) {
        this.id_comment = id_comment;
    }
}
