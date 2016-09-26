package com.locservice.api.service;

import com.locservice.api.entities.GetOrderInfoData;
import com.locservice.api.entities.GetOrderTrackData;
import com.locservice.api.entities.GetOrdersData;
import com.locservice.api.entities.NewOrderData;
import com.locservice.api.entities.OrderCancelData;
import com.locservice.api.entities.OrderPriceData;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.PrepareCancelOrderData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.entities.ResultIntData;
import com.locservice.api.request.CameOutRequest;
import com.locservice.api.request.CheckOrderPay;
import com.locservice.api.request.GetOrdersRequest;
import com.locservice.api.request.GetPriceRequest;
import com.locservice.api.request.OrderCancelRequest;
import com.locservice.api.request.OrderRatingRequest;
import com.locservice.api.request.OrderRemoveRequest;
import com.locservice.api.request.OrderRequest;
import com.locservice.api.request.OrderStatusRequest;
import com.locservice.api.request.OrderTrackRequest;
import com.locservice.api.request.PayOrderRequest;
import com.locservice.api.request.PrepareCancelOrderRequest;
import com.locservice.api.request.RequestGetOrdersInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 18 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface OrderService {

    @POST("/")
    Call<NewOrderData> NewOrder(@Body OrderRequest body);

    @POST("/")
    Call<ResultIntData> RemoveOrder(@Body OrderRemoveRequest body);

    @POST("/")
    Call<List<OrderStatusData>> GetOrderStatusList(@Body OrderStatusRequest body);

    @POST("/")
    Call<GetOrdersData> GetOrders(@Body GetOrdersRequest body);

    @POST("/")
    Call<GetOrderInfoData> GetOrdersInfo(@Body RequestGetOrdersInfo body);

    @POST("/")
    Call<OrderCancelData> CancelOrder(@Body OrderCancelRequest body);

    @POST("/")
    Call<ResultData> SetOrderRating(@Body OrderRatingRequest body);

    @POST("/")
    Call<OrderPriceData> GetOrderPrice(@Body GetPriceRequest body);

    @POST("/")
    Call<ResultData> CameOut(@Body CameOutRequest body);

    @POST("/")
    Call<GetOrderTrackData> GetOrderTrack(@Body OrderTrackRequest body);

    @POST("/")
    Call<PrepareCancelOrderData> PrepareCancelOrder(@Body PrepareCancelOrderRequest body);

    @POST("/")
    Call<ResultData> PayOrder(@Body PayOrderRequest body);

    @POST("/")
    Call<ResultData> CheckOrderPay(@Body CheckOrderPay body);

}
