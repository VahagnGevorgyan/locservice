package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ChatAddCommentData;
import com.locservice.api.entities.ChatGetCommentsData;
import com.locservice.api.request.ChatAddCommentRequest;
import com.locservice.api.request.GetCommentsRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;


/**
 * Created by Vahagn Gevorgyan
 * 18 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChatManager extends WebManager {

    private static final String TAG = ChatManager.class.getSimpleName();

    public ChatManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for getting client info
     * @param id_comment - comment id
     */
    public void GetComments(long id_comment) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatManager.GetComments");

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getcomments : {\n"
                            + " id_comment : " + id_comment
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getChatModel().GetComments(new GetCommentsRequest(id_comment),
                new RequestCallback<ChatGetCommentsData>(mContext, RequestTypes.REQUEST_GET_COMMENTS));

    } // end method GetClientInfo

    /**
     * Method for adding order comment
     * @param comment - comment text
     */
    public void AddComment (String comment) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatManager.AddOrderComment : comment : " + comment);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : addcomment : {\n"
                            + " comment : " + comment
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getChatModel().AddComment(new ChatAddCommentRequest(comment),
                new RequestCallback<ChatAddCommentData>(mContext, RequestTypes.REQUEST_ADD_COMMENT));

    } // end method AddComment
}