package com.locservice.api.service;

import com.locservice.api.entities.ChatAddCommentData;
import com.locservice.api.entities.ChatGetCommentsData;
import com.locservice.api.request.ChatAddCommentRequest;
import com.locservice.api.request.GetCommentsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Hayk Yegoryan
 * 18 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface ChatService {

    @POST("/")
    Call<ChatGetCommentsData> GetComments(@Body GetCommentsRequest body);

    @POST("/")
    Call<ChatAddCommentData> AddComment(@Body ChatAddCommentRequest body);

}
