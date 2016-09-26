package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
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
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 18 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderManager extends WebManager {

    private static final String TAG = OrderManager.class.getSimpleName();

    public OrderManager() {

    }

    public OrderManager(ICallBack context) {
        super(context);
    }


    /**
     * Method for creating new order
     *
     * @param orderRequest - order request
     */
    public void NewOrder(OrderRequest orderRequest) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.NewOrder : orderRequest : " + orderRequest);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : neworder : {\n"
                            + " ClientPhone_text : "   + orderRequest.getClientPhone_text()
                            + ",\n CollDate : "     + orderRequest.getCollDate()
                            + ",\n CollTime : "     + orderRequest.getCollTime()
                            + ",\n CollTimeOffset : "     + orderRequest.getCollTimeOffset()
                            + ",\n tariff : "     + orderRequest.getTariff()
                            + ",\n IdLocality : "     + orderRequest.getIdLocality()
                            + ",\n CollAddrTypeMenu : "     + orderRequest.getCollAddrTypeMenu()
                            + ",\n CollLandmark : "     + orderRequest.getCollLandmark()
                            + ",\n CollAddressText : "     + orderRequest.getCollAddressText()
                            + ",\n DeliveryAddrTypeMenu : "     + orderRequest.getDeliveryAddrTypeMenu()
                            + ",\n DeliveryLandmark : "     + orderRequest.getDeliveryLandmark()
                            + ",\n DeliveryAddressText : "     + orderRequest.getDeliveryAddressText()
                            + ",\n OnlinePayment : "     + orderRequest.getOnlinePayment()
                            + ",\n CollTerminal : "     + orderRequest.getCollTerminal()
                            + ",\n DeliveryTerminal : "     + orderRequest.getDeliveryTerminal()
                            + ",\n ClientFullName : "     + orderRequest.getClientFullName()
                            + ",\n IdOrder : "     + orderRequest.getIdOrder()
                            + ",\n CallerPhone : "     + orderRequest.getCallerPhone()
                            + ",\n PassengerName : "     + orderRequest.getPassengerName()
                            + ",\n PassengerPhone : "     + orderRequest.getPassengerPhone()
                            + ",\n NoSmoking : "     + orderRequest.getNoSmoking()
                            + ",\n useBonus : "     + orderRequest.getUseBonus()
                            + ",\n hurry : "     + orderRequest.getHurry()
                            + ",\n CityFrom : "     + orderRequest.getCityFrom()
                            + ",\n Flight : "     + orderRequest.getFlight()
                            + ",\n MetPhone : "     + orderRequest.getMetPhone()
                            + ",\n Wagon : "     + orderRequest.getWagon()
                            + ",\n Train : "     + orderRequest.getTrain()
                            + ",\n latitude : "     + orderRequest.getLatitude()
                            + ",\n longitude : "     + orderRequest.getLongitude()
                            + ",\n del_latitude : "     + orderRequest.getDel_latitude()
                            + ",\n del_longitude : "     + orderRequest.getDel_longitude()
                            + ",\n CollComment : "     + orderRequest.getCollComment()
                            + ",\n DeliveryComment : "     + orderRequest.getDeliveryComment()
                            + ",\n CollPodjed : "     + orderRequest.getCollPodjed()
                            + ",\n DeliveryPodjed : "     + orderRequest.getDeliveryPodjed()
                            + ",\n OrderComment : "     + orderRequest.getOrderComment()
                            + ",\n device_token : "     + orderRequest.getDevice_token()
                            + ",\n CollSuburb : "     + orderRequest.getCollSuburb()
                            + ",\n DeliverySuburb : "     + orderRequest.getDeliverySuburb()
                            + ",\n g_type : "     + orderRequest.getG_type()
                            + ",\n conditioner : "     + orderRequest.getConditioner()
                            + ",\n animal : "     + orderRequest.getAnimal()
                            + ",\n need_check : "     + orderRequest.getNeed_check()
                            + ",\n need_wifi : "     + orderRequest.getNeed_wifi()
                            + ",\n need_card : "     + orderRequest.getNeed_card()
                            + ",\n yellow_reg_num : "     + orderRequest.getYellow_reg_num()
                            + ",\n luggage : "     + orderRequest.getLuggage()
                            + ",\n meeting : "     + orderRequest.getMeeting()
                            + ",\n g_width : "     + orderRequest.getG_width()
                            + ",\n e_type : "     + orderRequest.getE_type()
                            + ",\n is_exist : "     + orderRequest.getIs_exist()
                            + ",\n CollAddressId : "     + orderRequest.getCollAddressId()
                            + ",\n DeliveryAddressId : "     + orderRequest.getDeliveryAddressId()
                            + ",\n NoCashPayment : "     + orderRequest.getNoCashPayment()
                            + ",\n id_corporation : "     + orderRequest.getId_corporation()
                            + ",\n child_seats : "     + orderRequest.getChild_seats()
                            + ",\n CollGeoObject : "     + orderRequest.getCollGeoObject()
                            + ",\n DeliveryGeoObject : "     + orderRequest.getDeliveryGeoObject()
                            + ",\n id_card : "     + orderRequest.getId_card()
                            + ",\n os_version : "     + orderRequest.getOs_version()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().NewOrder(orderRequest,
                new RequestCallback<NewOrderData>(mContext, RequestTypes.REQUEST_NEW_ORDER));

    } // end method NewOrder

    /**
     * Method for removing order by id
     * @param orderId - id of order
     */
    public void RemoveOrder(String orderId) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RemoveOrder.RemoveOrder : orderId : " + orderId);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : removeOrderFromHistory : {\n"
                            + " orderId : "   + orderId
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        OrderRemoveRequest orderRemoveRequest = new OrderRemoveRequest();
        orderRemoveRequest.setIdOrder(orderId);
        ServiceLocator.getOrderModel().RemoveOrder(orderRemoveRequest,
                new RequestCallback<ResultIntData>(mContext, RequestTypes.REQUEST_REMOVE_ORDER));

    } // end method RemoveOrder

    /**
     * Method for getting order status list
     *
     * @param context - current context
     * @param orderStatusRequest - order status request body
     */
    public List<OrderStatusData> GetOrderStatusList(Context context, OrderStatusRequest orderStatusRequest) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.GetOrderStatusList : orderStatusRequest : " + orderStatusRequest);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText(context, "Request : getliststatus : {\n"
                            + " IdOrdersList : "   + orderStatusRequest.getIdOrdersList()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        return ServiceLocator.getOrderModel().GetOrderStatusList(orderStatusRequest);

    } // end method GetOrderStatusList

    public  void GetOrderStatusList(String IdOrdersList) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.GetOrderStatusList : IdOrdersList : " + IdOrdersList);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getliststatus : {\n"
                            + " IdOrdersList : "   + IdOrdersList
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().GetOrderStatusList(new OrderStatusRequest("getliststatus", IdOrdersList),
                new RequestCallback<List<OrderStatusData>>(mContext, RequestTypes.REQUEST_GET_ORDER_STATUS_LIST));

    } // end method GetOrderStatusList

    public  void GetOrderStatusListFromPush(String IdOrdersList) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.GetOrderStatusListFromPush : IdOrdersList : " + IdOrdersList);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getliststatus : {\n"
                            + " IdOrdersList : "   + IdOrdersList
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().GetOrderStatusList(new OrderStatusRequest("getliststatus", IdOrdersList),
                new RequestCallback<List<OrderStatusData>>(mContext, RequestTypes.REQUEST_GET_ORDER_STATUS_LIST_FROM_PUSH));

    } // end method GetOrderStatusListFromPush

    /**
     * Method for getting orders ids list
     * @param phone - phone number
     */
    public void GetOrders (String phone, int only_active) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.GetOrders : phone : " + phone + " : only_active : " + only_active);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getorders : {\n"
                            + " phone : "   + phone
                            + ",\n only_active : "     + only_active
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().GetOrders(new GetOrdersRequest(phone, only_active),
                new RequestCallback<GetOrdersData>(mContext, RequestTypes.REQUEST_GET_ORDERS));

    } // end method GetOrders

    /**
     * Method for canceling order
     * @param orderId - order id
     */
    public void CancelOrder(String orderId) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.CancelOrder : orderId : " + orderId);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : cancelorder : {\n"
                            + " orderId : "   + orderId
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().CancelOrder(new OrderCancelRequest(orderId),
                new RequestCallback<OrderCancelData>(mContext, RequestTypes.REQUEST_CANCEL_ORDER));

    } // end method CancelOrder


    /**
     * Method for getting orders info list
     * @param phone - phone number
     * @param IdOrdersList - order id list
     */
    public void GetOrdersInfo (String phone, String IdOrdersList, int requestTypes) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.GetOrdersInfo : phone : " + phone + " : requestTypes : " + requestTypes);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getordersinfo : {\n"
                            + " phone : "   + phone
                            + ",\n IdOrdersList : "     + IdOrdersList
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().GetOrdersInfo(new RequestGetOrdersInfo("getordersinfo", phone, IdOrdersList),
                new RequestCallback<GetOrderInfoData>(mContext, requestTypes));

    } // end method GetOrdersInfo

    /**
     * Method for getting order track
     * @param orderId - order id
     */

    public void GetOrderTrack (String orderId) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.GetOrderTrack : orderId : " + orderId);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getordertrack : {\n"
                            + " orderId : "   + orderId
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().GetOrderTrack(new OrderTrackRequest("getordertrack", orderId),
                new RequestCallback<GetOrderTrackData>(mContext, RequestTypes.REQUEST_GET_ORDER_TRACK));

    } // end method GetOrderTrack

    /**
     * Method for setting order rate
     * @param IdOrder - order id
     * @param rating - rating
     */
    public void SetOrderRating (String IdOrder, int rating, String comment) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.SetOrderRating : IdOrder : " + IdOrder + " : rating : " + rating + " : comment : " + comment);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : orderrating : {\n"
                            + " IdOrder : "   + IdOrder
                            + ",\n rating : "     + rating
                            + ",\n comment : "     + comment
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().SetOrderRating(new OrderRatingRequest(IdOrder, rating, comment),
                new RequestCallback<ResultData>(mContext, RequestTypes.REQUEST_SET_ORDER_RATE));

    } // end method SetOrderRating

    /**
     * Method for getting order price
     * @param getPriceRequest - order price request body
     */
    public void GetOrderPrice (GetPriceRequest getPriceRequest) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.GetOrderPrice : getPriceRequest : " + getPriceRequest);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getprice : {\n"
                            + " ClientPhone_text : "   + getPriceRequest.getClientPhone_text()
                            + ",\n CollDate : "     + getPriceRequest.getCollDate()
                            + ",\n CollTime : "     + getPriceRequest.getCollTime()
                            + ",\n CollTimeOffset : "     + getPriceRequest.getCollTimeOffset()
                            + ",\n tariff : "     + getPriceRequest.getTariff()
                            + ",\n IdLocality : "     + getPriceRequest.getIdLocality()
                            + ",\n CollAddrTypeMenu : "     + getPriceRequest.getCollAddrTypeMenu()
                            + ",\n CollLandmark : "     + getPriceRequest.getCollLandmark()
                            + ",\n CollAddressText : "     + getPriceRequest.getCollAddressText()
                            + ",\n DeliveryAddrTypeMenu : "     + getPriceRequest.getDeliveryAddrTypeMenu()
                            + ",\n DeliveryLandmark : "     + getPriceRequest.getDeliveryLandmark()
                            + ",\n DeliveryAddressText : "     + getPriceRequest.getDeliveryAddressText()
                            + ",\n OnlinePayment : "     + getPriceRequest.getOnlinePayment()
                            + ",\n CollTerminal : "     + getPriceRequest.getCollTerminal()
                            + ",\n DeliveryTerminal : "     + getPriceRequest.getDeliveryTerminal()
                            + ",\n ClientFullName : "     + getPriceRequest.getClientFullName()
                            + ",\n IdOrder : "     + getPriceRequest.getIdOrder()
                            + ",\n CallerPhone : "     + getPriceRequest.getCallerPhone()
                            + ",\n PassengerName : "     + getPriceRequest.getPassengerName()
                            + ",\n PassengerPhone : "     + getPriceRequest.getPassengerPhone()
                            + ",\n NoSmoking : "     + getPriceRequest.getNoSmoking()
                            + ",\n useBonus : "     + getPriceRequest.getUseBonus()
                            + ",\n hurry : "     + getPriceRequest.getHurry()
                            + ",\n CityFrom : "     + getPriceRequest.getCityFrom()
                            + ",\n Flight : "     + getPriceRequest.getFlight()
                            + ",\n MetPhone : "     + getPriceRequest.getMetPhone()
                            + ",\n Wagon : "     + getPriceRequest.getWagon()
                            + ",\n Train : "     + getPriceRequest.getTrain()
                            + ",\n latitude : "     + getPriceRequest.getLatitude()
                            + ",\n longitude : "     + getPriceRequest.getLongitude()
                            + ",\n del_latitude : "     + getPriceRequest.getDel_latitude()
                            + ",\n del_longitude : "     + getPriceRequest.getDel_longitude()
                            + ",\n CollComment : "     + getPriceRequest.getCollComment()
                            + ",\n DeliveryComment : "     + getPriceRequest.getDeliveryComment()
                            + ",\n CollPodjed : "     + getPriceRequest.getCollPodjed()
                            + ",\n DeliveryPodjed : "     + getPriceRequest.getDeliveryPodjed()
                            + ",\n OrderComment : "     + getPriceRequest.getOrderComment()
                            + ",\n device_token : "     + getPriceRequest.getDevice_token()
                            + ",\n CollSuburb : "     + getPriceRequest.getCollSuburb()
                            + ",\n DeliverySuburb : "     + getPriceRequest.getDeliverySuburb()
                            + ",\n g_type : "     + getPriceRequest.getG_type()
                            + ",\n conditioner : "     + getPriceRequest.getConditioner()
                            + ",\n animal : "     + getPriceRequest.getAnimal()
                            + ",\n need_check : "     + getPriceRequest.getNeed_check()
                            + ",\n need_wifi : "     + getPriceRequest.getNeed_wifi()
                            + ",\n need_card : "     + getPriceRequest.getNeed_card()
                            + ",\n yellow_reg_num : "     + getPriceRequest.getYellow_reg_num()
                            + ",\n luggage : "     + getPriceRequest.getLuggage()
                            + ",\n meeting : "     + getPriceRequest.getMeeting()
                            + ",\n g_width : "     + getPriceRequest.getG_width()
                            + ",\n e_type : "     + getPriceRequest.getE_type()
                            + ",\n is_exist : "     + getPriceRequest.getIs_exist()
                            + ",\n CollAddressId : "     + getPriceRequest.getCollAddressId()
                            + ",\n DeliveryAddressId : "     + getPriceRequest.getDeliveryAddressId()
                            + ",\n NoCashPayment : "     + getPriceRequest.getNoCashPayment()
                            + ",\n id_corporation : "     + getPriceRequest.getId_corporation()
                            + ",\n child_seats : "     + getPriceRequest.getChild_seats()
                            + ",\n CollGeoObject : "     + getPriceRequest.getCollGeoObject()
                            + ",\n DeliveryGeoObject : "     + getPriceRequest.getDeliveryGeoObject()
                            + ",\n id_card : "     + getPriceRequest.getId_card()
                            + ",\n os_version : "     + getPriceRequest.getOs_version()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().GetOrderPrice(getPriceRequest,
                new RequestCallback<OrderPriceData>(mContext, RequestTypes.REQUEST_GET_ORDER_PRICE));

    } // end method GetOrderPrice

    /**
     * Method for informing driver that client came out
     */
    public void CameOut (String IdOrder) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.CameOut");

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : cameout : {\n"
                            + " IdOrder : "   + IdOrder
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().CameOut(new CameOutRequest(IdOrder),
                new RequestCallback<ResultData>(mContext, RequestTypes.REQUEST_CAME_OUT));

    } // end method CameOut

    /**
     *  Method for informing driver that client came out
     * @param IdOrder - order id
     */
    public void PrepareCancelOrder (String IdOrder) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.PrepareCancelOrder : IdOrder : " + IdOrder);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : prepareCancelOrder : {\n"
                            + " IdOrder : "   + IdOrder
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().PrepareCancelOrder(new PrepareCancelOrderRequest(IdOrder),
                new RequestCallback<PrepareCancelOrderData>(mContext, RequestTypes.REQUEST_PREPARE_CANCEL_ORDER));

    } // end method PrepareCancelOrder

    /**
     * Method for paying order
     *
     * @param IdOrder - order id
     * @param id_card - credit card id
     */
    public void PayOrder (String IdOrder, int id_card) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.PayOrder : IdOrder : " + IdOrder
                + " : id_card : " + id_card
        );

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : PayOrder : {\n"
                            + " IdOrder : "   + IdOrder
                            + ",\n id_card : " + id_card
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().PayOrder(new PayOrderRequest(IdOrder, id_card),
                new RequestCallback<ResultData>(mContext, RequestTypes.REQUEST_PAY_ORDER));

    } // end method PayOrder

    /**
     * Method for paying order
     *
     * @param IdOrder - order id
     */
    public void CheckOrderPay (String IdOrder) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderManager.CheckOrderPay : IdOrder : " + IdOrder);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : CheckOrderPay : {\n"
                            + " IdOrder : "   + IdOrder
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getOrderModel().CheckOrderPay(new CheckOrderPay(IdOrder),
                new RequestCallback<ResultData>(mContext, RequestTypes.REQUEST_CHECK_ORDER_PAY));

    } // end method CheckOrderPay
}
