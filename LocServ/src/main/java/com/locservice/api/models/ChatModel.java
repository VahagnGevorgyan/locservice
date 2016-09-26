package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.ChatAddCommentData;
import com.locservice.api.entities.ChatGetCommentsData;
import com.locservice.api.request.ChatAddCommentRequest;
import com.locservice.api.request.GetCommentsRequest;
import com.locservice.api.service.ChatService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by Vahagn Gevorgyan
 * 18 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChatModel {

    private static final String TAG = ChatModel.class.getSimpleName();

    private ChatService commentService;

    public ChatModel() {
        this.commentService = ServiceGenerator.createService(ChatService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for getting comments data
     *
     * @param body - request body
     * @param cb - response callback
     */
    public void GetComments(GetCommentsRequest body, Callback<ChatGetCommentsData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatModel.GetComments : body : " + body + " cb : " + cb);

        Call<ChatGetCommentsData> chatGetCommentsDataCall = commentService.GetComments(body);
        chatGetCommentsDataCall.enqueue(cb);

    } //end method GetComments

    /**
     * Method for adding comment
     *
     * @param body - request body
     * @param cb - response callback
     */
    public void AddComment (ChatAddCommentRequest body, Callback<ChatAddCommentData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatModel.AddComment body : " + body + " cb : " + cb);

        Call<ChatAddCommentData> chatAddCommentDataCall = commentService.AddComment(body);
        chatAddCommentDataCall.enqueue(cb);

    } // end method AddComment
}
