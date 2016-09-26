package com.locservice.service;

import android.content.Intent;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.api.entities.OrderIdItem;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.manager.OrderManager;
import com.locservice.api.request.OrderStatusRequest;
import com.locservice.gcm.GCMUtils;
import com.locservice.utils.Logger;

import java.util.List;


/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetOrderStatusService extends CronJobsService {

    private static final String TAG = GetOrderStatusService.class .getSimpleName();

    public GetOrderStatusService() {
        super(TAG);
    }

    @Override
    protected boolean doCronWork(Intent intent) {

        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: GetOrderStatusService.doCronWork : Awake! Get order status : active_orders : " + CMApplication.getActiveOrders());

        if (CMApplication.getActiveOrders() != null && CMApplication.getActiveOrders().size() > 0) {
            try {
                OrderManager orderManager = new OrderManager();
                OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
                String orderIds = "";
                for (int i=0; i<CMApplication.getActiveOrders().size(); i++) {
                    OrderIdItem item = CMApplication.getActiveOrders().get(i);
                    orderIds += item.getIdOrder();
                    if(i < CMApplication.getActiveOrders().size() - 1)
                        orderIds += ",";
                }
                orderStatusRequest.setIdOrdersList(orderIds);
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GetOrderStatusService.doCronWork : orderIds : " + orderIds);

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GetOrderStatusService.doCronWork : " +
                        " : order track id : " + CMApplication.getTrackingOrderId() +
                        " : orderManager : " + orderManager +
                        " : orderStatusRequest : " + orderStatusRequest +
                        " : active_orders : " + CMApplication.getActiveOrders());

                List<OrderStatusData> orderStatusDatas = orderManager.GetOrderStatusList(getApplicationContext(), orderStatusRequest);

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GetOrderStatusService.doCronWork : order track id : " + CMApplication.getTrackingOrderId());
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GetOrderStatusService.doCronWork : order status list : " + orderStatusDatas);

                if(orderStatusDatas != null && orderStatusDatas.size() > 0) {
                    // set order status
                    setOrderStatus(orderStatusDatas);
                }

            } catch (final Exception e) {
                if(CMAppGlobals.DEBUG)Logger.e(TAG, ":: GetOrderStatusService.doCronWork : error : " + e);
            }
        } else {
            if(CMAppGlobals.DEBUG)Logger.w(TAG, ":: GetOrderStatusService.doCronWork : Stop! Return null order!");
            return false;
        }

        return true;
    }

    /**
     * Method for setting order status & canceling alarm
     * @param orderStatusDataList - list of order status data
     */
    private void setOrderStatus(List<OrderStatusData> orderStatusDataList) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GetOrderStatusService.setOrderStatus : list_orderStatusData : " + orderStatusDataList);

        if(orderStatusDataList != null && orderStatusDataList.size() > 0) {
            GCMUtils.updateOrderUI(this, orderStatusDataList);
        }

    } // end method setOrderStatus

}
