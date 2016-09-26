package com.locservice.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.CardsData;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.entities.GetOrderInfoData;
import com.locservice.api.entities.GetOrderTrackData;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.manager.OrderManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.fragments.OrderInfoFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.listeners.ActivityListener;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 20 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderInfoActivity extends BaseActivity implements ICallBack, ActivityListener {

    private static final String TAG = OrderInfoActivity.class.getSimpleName();

    private Fragment current_fragment;
    private Order mOrder;
    private CMApplication mCMApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);

        mCMApplication = (CMApplication)this.getApplicationContext();

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoActivity.onCreate ");

        //change status and navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setStatusBarColor(Color.parseColor("#ddffffff"));
            window.setNavigationBarColor(Color.parseColor("#ddffffff"));
        }

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            setOrderInfoFragment();
        }

    }

    /**
     * Method for setting order info fragment
     */
    public void setOrderInfoFragment() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoActivity.setOrderInfoFragment ");

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            current_fragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.ORDER_INFO, extras);

            String idOrder = extras.getString(CMAppGlobals.EXTRA_ORDER_ID);
            // Request for getting order status list
            OrderManager orderManager = new OrderManager(this);
            orderManager.GetOrderStatusList(idOrder);
            orderManager.GetOrderTrack(idOrder);

            if (extras.containsKey(CMAppGlobals.EXTRA_ORDER)) {
                mOrder = extras.getParcelable(CMAppGlobals.EXTRA_ORDER);

                if (CMAppGlobals.DEBUG)
                    if (mOrder != null) {
                        Logger.i(TAG, ":: OrderInfoActivity.setOrderInfoFragment : status : " + mOrder.getStatus());
                    }
            }
        }

    } // end method setOrderInfoFragment

    /**
     * Method for getting order
     *
     * @return - current order
     */
    public Order getOrder() {
        return mOrder;

    } // end method getOrder

    /**
     * Method for setting order
     *
     * @param mOrder - order
     */
    public void setOrder(Order mOrder) {
        this.mOrder = mOrder;

    } // end method setOrder

    @Override
    public void onFailure(Throwable error, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoActivity.OnFailure : error : " + error);

    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: OrderInfoActivity.OnSuccess : response : " + response);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_GET_DRIVER_INFO:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_DRIVER_INFO : response : " + response);

                    if(response instanceof DriverInfo) {
                        // get driver information
                        DriverInfo driverInfo = (DriverInfo) response;
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_DRIVER_INFO : driverInfo : " + driverInfo);
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_DRIVER_INFO : name : " + driverInfo.getName());
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_DRIVER_INFO : id : " + driverInfo.getId());
                        if (driverInfo.getId() != null && !driverInfo.getId().isEmpty()) {
                            // SHOW DRIVER BAR
                            if (current_fragment != null) {
                                // set driver UI
                                ((OrderInfoFragment) current_fragment).setDriverUI(driverInfo, true);
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_ORDER_STATUS_LIST:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : response : " + response);

                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDER_STATUS_LIST")) {
                        List<OrderStatusData> orderStatusDataList = (List<OrderStatusData>) response;
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : orderStatusDataList : " + orderStatusDataList);
                        if (!orderStatusDataList.isEmpty()) {
                            ((OrderInfoFragment) current_fragment).addAdditionalFieldsAndServices(orderStatusDataList.get(0));
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_ORDERS_INFO:
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_ORDERS_INFO : response : " + response);

                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDERS_INFO")) {
                        if(response instanceof GetOrderInfoData) {
                            GetOrderInfoData orderInfoData = (GetOrderInfoData) response;
                            List<Order> orderList = orderInfoData.getOrders();

                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_ORDERS_INFO : orderList : " + orderList);

                            if (orderList != null && !orderList.isEmpty()) {
                                mOrder = orderInfoData.getOrders().get(0);
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_ORDERS_INFO : mOrder : " + mOrder);
                                // set service options
                                ((OrderInfoFragment) current_fragment).setServiceOptions(mOrder);

                                if (mOrder.getFeedback() != null)
                                    ((OrderInfoFragment)current_fragment).setFeedbackContent(mOrder);
                                ((OrderInfoFragment) current_fragment).setEnableRatingBar();
                            }
                            ((OrderInfoFragment) current_fragment).getMapFragment().drawRoadDirections(null, true, true, false);
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_ORDER_TRACK:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDER_TRACK")) {
                        if(response instanceof GetOrderTrackData) {
                            GetOrderTrackData orderTrackData = (GetOrderTrackData) response;
                            ((OrderInfoFragment) current_fragment).getMapFragment().drawRoadDirections(null, true, true, false);
                            if (current_fragment != null) {
                                List<LatLng> track = new ArrayList<>();

                                if (orderTrackData.getTrack() != null && !orderTrackData.getTrack().isEmpty()) {
                                    for(List<String> coordinate : orderTrackData.getTrack()){
                                        if(coordinate.size()==2){
                                            if(!coordinate.get(0).equals("") && !coordinate.get(1).equals("")){
                                                track.add(new LatLng(Double.parseDouble(coordinate.get(0)), Double.parseDouble(coordinate.get(1))));
                                            }
                                        }
                                    }
                                    ((OrderInfoFragment) current_fragment).drawTrack(track);
                                }
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_SET_ORDER_RATE:
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_SET_ORDER_RATE : response : " + response);

                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_SET_ORDER_RATE")) {
                        if(response instanceof ResultData) {
                            ResultData resultData = (ResultData) response;
                            if (resultData.getResult().equals("1")) {
                                if (((OrderInfoFragment) current_fragment).getLayoutFeedback().getVisibility() == View.VISIBLE)
                                    ((OrderInfoFragment) current_fragment).sandFeedbackAction();

                                ((OrderInfoFragment) current_fragment).setEnableRatingBar();
                                setResult(Activity.RESULT_OK);
                            }
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_CLIENT_INFO:
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_CLIENT_INFO : response : " + response);

                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_CLIENT_INFO")) {
                        if(response instanceof ClientInfo) {
                            ClientInfo clientInfo = (ClientInfo) response;
                            ((OrderInfoFragment) current_fragment).setPaymentType(mOrder, clientInfo);
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_CARDS:
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoActivity.onSuccess : REQUEST_GET_CARDS : response : " + response);

                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_CLIENT_INFO")) {
                        if(response instanceof CardsData) {
                            CardsData cardsData = (CardsData) response;
                            ((OrderInfoFragment) current_fragment).setPaymentType(mOrder, cardsData);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Method for getting current_fragment
     *
     * @return current_fragment
     */
    public Fragment getCurrent_fragment() {
        return current_fragment;

    } // end method getCurrent_fragment

    @Override
    public void onResume() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoActivity.onResume ");
        super.onResume();
        // set current activity
        setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoActivity.onPause ");
        clearCurrentActivity(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoActivity.onDestroy ");
        clearCurrentActivity(this);
        super.onDestroy();
    }

    @Override
    public void setCurrentActivity(Activity context) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoActivity.setCurrentActivity : context : " + context);

        // set current activity
        mCMApplication.setCurrentActivity(context);
    }

    @Override
    public void clearCurrentActivity(Activity context) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoActivity.clearCurrentActivity : context : " + context);

        Activity currActivity = mCMApplication.getCurrentActivity();
        if (this.equals(currActivity))
            mCMApplication.setCurrentActivity(null);
    }
}
