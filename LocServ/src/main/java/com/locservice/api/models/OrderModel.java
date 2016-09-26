package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
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
import com.locservice.api.service.OrderService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by Vahagn Gevorgyan
 * 18 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderModel {

    private static final String TAG = OrderModel.class.getSimpleName();

    private OrderService orderService;

    public OrderModel() {
        orderService = ServiceGenerator.createService(OrderService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method fo creating new order
     * @param body - request body
     * @param cb - response callback
     */
    public void NewOrder(OrderRequest body, Callback<NewOrderData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.NewOrder body : " + body + " cb : " + cb);

        Call<NewOrderData> newOrderDataCall = orderService.NewOrder(body);
        newOrderDataCall.enqueue(cb);

    } // and method NewOrder

    /**
     * Method fo removing order
     * @param body - request body
     * @param cb - response callback
     */
    public void RemoveOrder(OrderRemoveRequest body, Callback<ResultIntData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.RemoveOrder body : " + body + " cb : " + cb);

        Call<ResultIntData> newOrderDataCall = orderService.RemoveOrder(body);
        newOrderDataCall.enqueue(cb);

    } // and method RemoveOrder

    /**
     * Method for getting order status list
     * @param orderStatusRequest - order status requesy body
     * @return - list of order status data
     */
    public List<OrderStatusData> GetOrderStatusList(OrderStatusRequest orderStatusRequest) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.GetOrderStatusList orderStatusRequest : " + orderStatusRequest);

        Call<List<OrderStatusData>> statusListCall = orderService.GetOrderStatusList(orderStatusRequest);
        try {
            return statusListCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    } // end method GetOrderStatusList

    /**
     * Method for getting order status list
     * @param body - request body
     * @param cb - response callback
     */
    public void GetOrderStatusList(OrderStatusRequest body, Callback<List<OrderStatusData>> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.GetOrders body : " + body + " cb : " + cb);

        Call<List<OrderStatusData>> orderStatusCall = orderService.GetOrderStatusList(body);
        orderStatusCall.enqueue(cb);

    } // end method GetOrderStatusList

    /**
     * Method for getting orders ids list
     * @param body - request body
     * @param cb - response callback
     */
    public void GetOrders (GetOrdersRequest body, Callback<GetOrdersData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.GetOrders body : " + body + " cb : " + cb);

        Call<GetOrdersData> getOrdersDataCall = orderService.GetOrders(body);
        getOrdersDataCall.enqueue(cb);

    } // end method GetOrders

    /**
     * Method for getting order track
     * @param body - request body
     * @param cb - response callback
     */
    public void GetOrderTrack (OrderTrackRequest body, Callback<GetOrderTrackData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.GetOrders body : " + body + " cb : " + cb);

        Call<GetOrderTrackData> getOrderTrackDataCall = orderService.GetOrderTrack(body);
        getOrderTrackDataCall.enqueue(cb);

    } // end method GetOrderTrack

    /**
     * Method fo canceling new client
     * @param body - request body
     * @param cb - response callback
     */
    public void CancelOrder(OrderCancelRequest body, Callback<OrderCancelData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.CancelOrder body : " + body + " cb : " + cb);

        Call<OrderCancelData> orderCancelDataCall = orderService.CancelOrder(body);
        orderCancelDataCall.enqueue(cb);

    } // and method CancelOrder

    /**
     * Method for getting orders info list
     * @param body - request body
     * @param cb - response callback
     */
    public void GetOrdersInfo (RequestGetOrdersInfo body, Callback<GetOrderInfoData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.GetOrdersInfo body : " + body + " cb : " + cb);

        Call<GetOrderInfoData> getOrderInfoDataCall = orderService.GetOrdersInfo(body);
        getOrderInfoDataCall.enqueue(cb);

    } // end method GetOrdersInfo

    /**
     * Method for setting order rate
     * @param body - request body
     * @param cb - response callback
     */
    public void SetOrderRating (OrderRatingRequest body, Callback<ResultData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.SetOrderRating body : " + body + " cb : " + cb);

        Call<ResultData> resultDataCall = orderService.SetOrderRating(body);
        resultDataCall.enqueue(cb);

    } // end method SetOrderRating

    /**
     * Method for getting order price
     * @param body - request body
     * @param cb - response callback
     */
    public void GetOrderPrice (GetPriceRequest body, Callback<OrderPriceData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.GetOrderPrice body : " + body + " cb : " + cb);

        Call<OrderPriceData> orderPriceDataCall = orderService.GetOrderPrice(body);
        orderPriceDataCall.enqueue(cb);

    } // end method GetOrderPrice

    /**
     * Method for informing driver that client came out
     * @param body - request body
     * @param cb - response callback
     */
    public void CameOut (CameOutRequest body, Callback<ResultData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.CameOut body : " + body + " cb : " + cb);

        Call<ResultData> resultDataCall = orderService.CameOut(body);
        resultDataCall.enqueue(cb);

    } // end method CameOut

    /**
     * Method for prepare cancel order
     * @param body - request body
     * @param cb - response callback
     */
    public void PrepareCancelOrder (PrepareCancelOrderRequest body, Callback<PrepareCancelOrderData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.PrepareCancelOrder body : " + body + " cb : " + cb);

        Call<PrepareCancelOrderData> resultDataCall = orderService.PrepareCancelOrder(body);
        resultDataCall.enqueue(cb);

    } // end method PrepareCancelOrder

    /**
     * Method for paying order
     * @param body - request body
     * @param cb - response callback
     */
    public void PayOrder (PayOrderRequest body, Callback<ResultData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.PayOrder body : " + body + " cb : " + cb);

        Call<ResultData> resultDataCall = orderService.PayOrder(body);
        resultDataCall.enqueue(cb);

    } // end method PayOrder

    /**
     * Method for checking order pay
     * @param body - request body
     * @param cb - response callback
     */
    public void CheckOrderPay (CheckOrderPay body, Callback<ResultData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderModel.CheckOrderPay body : " + body + " cb : " + cb);

        Call<ResultData> resultDataCall = orderService.CheckOrderPay(body);
        resultDataCall.enqueue(cb);

    } // end method CheckOrderPay
}
