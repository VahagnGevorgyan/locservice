package com.locservice.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.GetOrderInfoData;
import com.locservice.api.entities.GetOrdersData;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderIdItem;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.ResultIntData;
import com.locservice.api.manager.OrderManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.fragments.OrderHistoryFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.StringHelper;
import com.locservice.utils.Utils;

import java.util.List;

public class OrderHistoryActivity extends BaseActivity implements ICallBack {

    private static final String TAG = OrderHistoryActivity.class.getSimpleName();
    private Fragment currentFragment;
    private OrderManager orderManager;

    private boolean isUpdatingList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            // set profile fragment
            currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.ORDER_HISTORY, null);

            orderManager = new OrderManager(this);
        }
    }

    @Override
    public void onFailure(Throwable error, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryActivity.onFailure : error : " + error + " : requestType : " + requestType);
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryActivity.onSuccess : response : " + response + " : requestType : " + requestType);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_GET_ORDERS:
                    if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDERS")) {
                        if(response instanceof GetOrdersData) {
                            // set active orders
                            GetOrdersData ordersData = (GetOrdersData)response;
                            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryActivity.onSuccess : REQUEST_GET_ORDERS : response : " + ordersData + " : requestType : " + requestType);
                            List<OrderIdItem> orderIdItems = ordersData.getOrders();
                            StringBuilder ids = new StringBuilder();
                            for (int i = 0; i < orderIdItems.size(); i++) {
                                if (i < orderIdItems.size() - 1) {
                                    ids.append(StringHelper.combineStrings(orderIdItems.get(i).getIdOrder(), ","));
                                } else {
                                    ids.append(orderIdItems.get(i).getIdOrder());
                                }
                            }
                            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryActivity.onSuccess : REQUEST_GET_ORDERS : response : ids : " + ids);
                            // get orders info by ids list
                            orderManager.GetOrdersInfo(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), ""), ids.toString(), RequestTypes.REQUEST_GET_ORDERS_INFO);
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_ORDERS_INFO:
                    if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDERS_INFO")) {
                        if(response instanceof GetOrderInfoData) {
                            GetOrderInfoData orderInfoData = (GetOrderInfoData) response;
                            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryActivity.onSuccess : REQUEST_GET_ORDERS_INFO : response : " + orderInfoData + " : requestType : " + requestType);

                            List<Order> orders = orderInfoData.getOrders();
                            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryActivity.onSuccess : REQUEST_GET_ORDERS_INFO : response : orders size : " + orders.size());

                            if (isUpdatingList) {
                                isUpdatingList = false;
                                if (!orders.isEmpty())
                                    ((OrderHistoryFragment)currentFragment).updateOrderListItem(orders.get(0));
                            } else {
                                //how orders history list
                                ((OrderHistoryFragment)currentFragment).showOrders(orders);
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_REMOVE_ORDER:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_REMOVE_ORDER")) {
                        if(response instanceof ResultIntData) {
                            ResultIntData resultData = (ResultIntData) response;
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderHistoryActivity.onSuccess : REQUEST_REMOVE_ORDER : resultData.getResult() : " + resultData.getResult());

                            if (resultData.getResult() > 0) {
                                removeOrderInfo();
                            } else {
                                Toast.makeText(OrderHistoryActivity.this, R.string.alert_remove_order, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_ORDER_STATUS_LIST:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDER_STATUS_LIST")) {
                        List<OrderStatusData> orderStatusDataList = (List<OrderStatusData>) response;
                        if (!orderStatusDataList.isEmpty()) {
                            ((OrderHistoryFragment)currentFragment).sendOrderStatusData(orderStatusDataList.get(0));
                        }
                    }
                    break;

            }
        }
    }

    /**
     * Method for removing order info from list
     */
    public void removeOrderInfo() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryActivity.removeOrderInfo ");

        ((OrderHistoryFragment)currentFragment).removeOrderInfo();

    } // end method removeOrderInfo

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CMAppGlobals.REQUEST_ORDER_INFO && resultCode == Activity.RESULT_OK) {
            isUpdatingList = true;
            OrderManager orderManager = new OrderManager(OrderHistoryActivity.this);
            orderManager.GetOrdersInfo("", ((OrderHistoryFragment)currentFragment).getCurrentOrderId(), RequestTypes.REQUEST_GET_ORDERS_INFO);
        }
    }
}
