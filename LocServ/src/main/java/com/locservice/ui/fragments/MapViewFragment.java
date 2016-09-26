package com.locservice.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.AdapterActionListener;
import com.locservice.adapters.AddressRVAdapter;
import com.locservice.adapters.ChildSeatRVAdapter;
import com.locservice.adapters.DateRVAdapter;
import com.locservice.adapters.PaymentRVAdapter;
import com.locservice.adapters.TariffRVAdapter;
import com.locservice.adapters.TariffServiceRVAdapter;
import com.locservice.api.entities.CardInfo;
import com.locservice.api.entities.ChildSeat;
import com.locservice.api.entities.ChildSeatData;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.Corporation;
import com.locservice.api.entities.DateData;
import com.locservice.api.entities.Driver;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.PaymentInfo;
import com.locservice.api.entities.PaymentTypes;
import com.locservice.api.entities.Tariff;
import com.locservice.api.entities.TariffInterval;
import com.locservice.api.entities.TariffService;
import com.locservice.api.manager.CreditCardManager;
import com.locservice.api.manager.GoogleManager;
import com.locservice.api.manager.OrderManager;
import com.locservice.api.request.GetDriversRequest;
import com.locservice.api.request.GetPriceRequest;
import com.locservice.api.request.OrderRequest;
import com.locservice.application.LocServicePreferences;
import com.locservice.db.LanguageDBManager;
import com.locservice.db.TariffDBManager;
import com.locservice.gcm.GCMUtils;
import com.locservice.protocol.ICallBack;
import com.locservice.protocol.IMapViewFragment;
import com.locservice.ui.AddCreditCardsActivity;
import com.locservice.ui.AddFavoriteAddressActivity;
import com.locservice.ui.ChatActivity;
import com.locservice.ui.CommentActivity;
import com.locservice.ui.CreditCardsActivity;
import com.locservice.ui.MainActivity;
import com.locservice.ui.RegisterActivity;
import com.locservice.ui.RewardsActivity;
import com.locservice.ui.SearchActivity;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.SlidingUpPanelLayout;
import com.locservice.ui.controls.SlidingUpPanelLayout.SlideState;
import com.locservice.ui.helpers.DateHelper;
import com.locservice.ui.helpers.OrderState;
import com.locservice.ui.helpers.OrderStateHelper;
import com.locservice.ui.helpers.SlidePanelHelper;
import com.locservice.ui.utils.ActivityTypes;
import com.locservice.ui.utils.ScrollType;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.GetAddress;
import com.locservice.utils.LocaleManager;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MapViewFragment extends Fragment implements IMapViewFragment,
        SlidingUpPanelLayout.PanelSlideListener,
        GetAddress,
        AdapterActionListener,
        DateRVAdapter.DateOnItemClickListener,
        PaymentRVAdapter.PaymentOnItemClickListener,
        ChildSeatRVAdapter.ChildSeatOnItemClickListener,
        TariffServiceRVAdapter.TariffServiceOnItemClickListener,
        TariffRVAdapter.TariffOnItemClickListener,
        AddressRVAdapter.AddressOnItemClickListener {

    private static final String TAG = MapViewFragment.class.getSimpleName();
    public static final int EXPAND_PANEL = 0x101;
    public static final int COLLAPSE_PANEL = 0x111;

    private View rootView;
    private MapDriverFragment mapFragment = null;
    private Double mLat;
    private Double mLng;

    private Date selectedDate;

    private LinearLayout layoutTopMenu;

    public boolean isSlidePanelOpened = false;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private RelativeLayout layoutSectionTariff;
    private LinearLayout layoutItemForScroll;

    public RelativeLayout getLayoutTop() {
        return layoutTop;
    }

    private RelativeLayout layoutTop;

    private OrderStateHelper orderHelper;

    private FragmentActivity mContext;

    protected OrderManager orderManager;

    private RelativeLayout layoutMapScreen;
    private boolean isFromAnimateTopLayout;
    private ScrollView scrollViewSlidePanelContainer;
    private AddressRVAdapter mAdapterFrom;
    private AddressRVAdapter mAdapterTo;
    private GradientDrawable blurGradient;
    private List<Tariff> tariff_list = new ArrayList<>();
    private ArrayList<TariffService> tariff_services = new ArrayList<>();
    private PaymentRVAdapter mAdapterPayment;
    private TariffRVAdapter mAdapterTariff;
    private TariffServiceRVAdapter mAdapterService;
    private ChildSeatRVAdapter mAdapterChild;
    private List<ChildSeat> child_seat_list = new ArrayList<>();
    private ArrayList<GooglePlace> fromAddressList = new ArrayList<>();
    private ArrayList<GooglePlace> toAddressList = new ArrayList<>();
    private RecyclerView scrollViewFrom;
    private RecyclerView scrollViewTo;
    private List<DateData> dateList = new ArrayList<>();
    private DateRVAdapter mAdapterDate;
    private RecyclerView recyclerViewDate;
    private String order_comment;
    private String order_entrance;

    private ClientInfo mClientInfo;
    private boolean mConfirmVisible;
    private boolean mMenuActive;
    private boolean mFavoriteAddressClicked;
    private GooglePlace mCurrentFavoriteAddress;

    private List<CardInfo> mCardInfoList = new ArrayList<>();

    private int panelItemsCount = 7;
    private LinearLayout layoutItemFrom;
    private LinearLayout layoutItemTo;
    private LinearLayout layoutItemDate;
    private LinearLayout layoutItemTariff;
    private LinearLayout layoutItemService;
    private LinearLayout layoutItemChild;
    private LinearLayoutManager layoutManagerDate;
    private RecyclerView recyclerViewPayment;
    private LinearLayoutManager layoutManagerPayment;
    private List<PaymentInfo> paymentList = new ArrayList<>();
    private RecyclerView recyclerViewChild;
    private LinearLayoutManager layoutManagerChildSeat;
    private RecyclerView recyclerViewService;
    private LinearLayoutManager layoutManagerTariffService;
    private RecyclerView recyclerViewTariff;
    private LinearLayoutManager layoutManagerTariff;
    private TextView textViewOWInfo;
    private RelativeLayout layoutComment;
    private TextView textViewComment;
    private ImageView imageViewComment;
    private OrderStatusData activeOrder = null;
    private ImageView imageViewMapMenu;
    private LinearLayoutManager layoutManagerFrom;
    private LinearLayoutManager layoutManagerTo;
    private String collAddressGeoObject;
    private String deliveryAddressGeoObject;

    private boolean isOrderCreated = false;
    private boolean hasAction = false;

    private View layoutForInScreenTools;
    private Handler orderConfirmHandler;


    public void setPanelItemsCount(int panelItemsCount) {
        this.panelItemsCount = panelItemsCount;
    }

    public LinearLayout getLayoutItemFrom() {
        return layoutItemFrom;
    }

    public LinearLayout getLayoutItemTo() {
        return layoutItemTo;
    }

    public LinearLayout getLayoutItemDate() {
        return layoutItemDate;
    }

    public LinearLayout getLayoutItemTariff() {
        return layoutItemTariff;
    }

    public LinearLayout getLayoutItemService() {
        return layoutItemService;
    }

    public LinearLayout getLayoutItemChild() {
        return layoutItemChild;
    }

    public void setIsOrderCreated(boolean isOrderCreated) {
        this.isOrderCreated = isOrderCreated;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_map_driver, container, false);
        mContext = getActivity();

        float fontScale = mContext.getResources().getConfiguration().fontScale;

        CustomTextView textViewTariffTitle = ((CustomTextView) rootView.findViewById(R.id.textViewTariffTitle));
        textViewTariffTitle.setText(mContext.getResources().getString(R.string.onward_text));
        textViewTariffTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                mContext.getResources().getInteger(R.integer.int_tariff_title_text_size) / fontScale);

        CustomTextView textViewTariffPrice = (CustomTextView) rootView.findViewById(R.id.textViewTariffPrice);
        textViewTariffPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                mContext.getResources().getInteger(R.integer.int_tariff_price_text_size) / fontScale);


        isOrderCreated = false;

        blurGradient = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0x00FFFFFF,  //start color of gradient
                0xFFFFFFFF}); //end color of gradient
        blurGradient.setGradientType(GradientDrawable.RADIAL_GRADIENT); // making it circular gradient
        blurGradient.setGradientRadius(20);  // radius of the circle
        blurGradient.setGradientCenter(1, 0.53f); // center of gradient

        // CREATE MAP FRAGMENT
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        mapFragment = (MapDriverFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.onCreateView ");
        if (mapFragment == null) {
            mapFragment = new MapGoogleFragment();

            fragmentTransaction.replace(R.id.fragment_container, mapFragment);
            fragmentTransaction.commit();
        }

        // map buttons
        mapFragment.hideControls(rootView);

        layoutItemFrom = (LinearLayout) rootView.findViewById(R.id.layoutItemFrom);
        layoutItemTo = (LinearLayout) rootView.findViewById(R.id.layoutItemTo);
        layoutItemDate = (LinearLayout) rootView.findViewById(R.id.layoutItemDate);
        layoutItemTariff = (LinearLayout) rootView.findViewById(R.id.layoutItemTariff);
        layoutItemService = (LinearLayout) rootView.findViewById(R.id.layoutItemService);
        layoutItemChild = (LinearLayout) rootView.findViewById(R.id.layoutItemChild);

        // set service options
        setServiceOptions(true, null);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        scrollViewSlidePanelContainer = (ScrollView) rootView.findViewById(R.id.scrollViewSlidePanelContainer);
        RelativeLayout mapContainer = (RelativeLayout) rootView.findViewById(R.id.fragment_container);

        mSlidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.slide_panel_height));
        mSlidingUpPanelLayout.setScrollableView(scrollViewSlidePanelContainer, 0);
        mSlidingUpPanelLayout.setPanelSlideListener(this);
        mSlidingUpPanelLayout.setPanelState(SlideState.EXPANDED);

        orderConfirmHandler = new OrderConfirmHandler(mSlidingUpPanelLayout);

        layoutItemForScroll = (LinearLayout) rootView.findViewById(R.id.layoutItemForScroll);

        layoutTop = (RelativeLayout) rootView.findViewById(R.id.layoutTop);
        layoutSectionTariff = (RelativeLayout) rootView.findViewById(R.id.layoutSectionTariff);

        layoutSectionTariff.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CommonHelper.setOnTouchLayout(layoutSectionTariff, event,
                        ContextCompat.getDrawable(getContext(), R.drawable.bg_tariff_sec),
                        ContextCompat.getDrawable(getContext(), R.drawable.gradient_order_taxi_selected));

            }
        });
        layoutSectionTariff.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // create request for new order
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : " + Utils.isUserRegistered(mContext));

                // check network status
                if (Utils.checkNetworkStatus(mContext)) {
                    if (layoutMapScreen.getVisibility() == View.VISIBLE) {
                        layoutMapScreen.setVisibility(View.GONE);
                    }

                    if (!Utils.isUserRegistered(mContext)) {
                        mContext.startActivityForResult(new Intent(mContext, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
                    } else {
                        if (!isOrderCreated) {
                            // get address from Google APIs
                            getGoogleApiAddress();
                            isOrderCreated = true;
                            // show confirmation page
                            showOrderConfirmPage();
                        }
                    }
                }
            }
        });
        layoutTopMenu = (LinearLayout) rootView.findViewById(R.id.layoutTopMenu);
        layoutTopMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.Menu : Top");
                ((MainActivity) mContext).OpenMenuDrawer(mContext);
            }
        });

        imageViewMapMenu = (ImageView) rootView.findViewById(R.id.imageViewMapMenu);
        imageViewMapMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.Menu : Map : mConfirmVisible : " + mConfirmVisible);
                // OPEN MENU DRAWER
                ((MainActivity) mContext).OpenMenuDrawer(mContext);
            }
        });

        // timer
        this.sliderHandler.postDelayed(this.updateTimeRunnable, 1500);

        orderHelper = new OrderStateHelper(mContext, this, rootView, layoutTop, layoutSectionTariff, mSlidingUpPanelLayout, mapContainer, mapFragment, OrderState.UNKNOWN);
        orderHelper.setTopBarItems(rootView, OrderState.UNKNOWN, null, "");


        layoutMapScreen = (RelativeLayout) rootView.findViewById(R.id.layoutMapScreen);
        layoutMapScreen.setVisibility(View.GONE);
        layoutMapScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment : layoutMapScreen.setOnClickListener onClick");
                // collapse pane
                mSlidingUpPanelLayout.collapsePane();
            }
        });

        textViewOWInfo = (TextView) rootView.findViewById(R.id.textViewOWInfo);
        textViewOWInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ChatActivity.class));
            }
        });

        textViewComment = (TextView) rootView.findViewById(R.id.textViewComment);
        textViewComment.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                mContext.getResources().getInteger(R.integer.int_slide_panel_items_unselected_text_size) / fontScale);
        imageViewComment = (ImageView) rootView.findViewById(R.id.imageViewComment);
        layoutComment = (RelativeLayout) rootView.findViewById(R.id.layoutItemComment);
        layoutComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.onCreateView : COMMENT_CLICK ");

                hasAction = true;
                // OPEN COMMENT PAGE
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra(CMAppGlobals.EXTRA_ORDER_COMMENT, order_comment);
                intent.putExtra(CMAppGlobals.EXTRA_ORDER_ENTRANCE, order_entrance);
                mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_COMMENT);
            }
        });

        layoutForInScreenTools = rootView.findViewById(R.id.layoutForInScreenTools);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment : Screen : hasNavigationBar : " +  CMApplication.hasNavigationBar(mContext));

        if (CMApplication.hasNavigationBar(mContext)) {
            layoutForInScreenTools.setVisibility(View.VISIBLE);
        }

        if(CMApplication.hasNavigationBar(mContext)) {
            layoutSectionTariff.setPadding(0,CMApplication.dpToPx(10), 0, CMApplication.dpToPx(58));
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Method for create new order
     */
    public void createNewOrder() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.createNewOrder : isOrderCreated : " + isOrderCreated);

        if (!isOrderCreated) {

            isOrderCreated = true;

            scrollViewSlidePanelContainer.fullScroll(View.FOCUS_UP);

            // Change Panel Bar height
            int oneFieldHeight = mContext.getResources().getDimensionPixelSize(R.dimen.slide_panel_height_one_field);
            mSlidingUpPanelLayout.setPanelHeight(oneFieldHeight);

            // enable slid panel
            mSlidingUpPanelLayout.setSlidingEnabled(true);

            // collapse panel
            Message message = new Message();
            message.what = COLLAPSE_PANEL;
            orderConfirmHandler.sendMessage(message);

            // CREATE ORDER
            CMApplication.setTrackingOrderId("");

            orderManager = new OrderManager((ICallBack) mContext);
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setOs_version(Build.VERSION.RELEASE);
            orderRequest.setClientPhone_text(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), ""));

            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : phone_number : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), ""));

            DateData selectedDate = getSelectedDate();
            if (selectedDate != null) {
                if (selectedDate.getDate() != null) {
                    // SELECTED DATE FORMAT
                    SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd-MM-yyyy", new Locale(LocaleManager.getLocaleLang()));
                    SimpleDateFormat dateFormatTime = new SimpleDateFormat("HHmm", new Locale(LocaleManager.getLocaleLang()));
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment.setOrderPrice : order_date : " + dateFormatDay.format(selectedDate.getDate()) + " :: " + dateFormatTime.format(selectedDate.getDate()));
                    orderRequest.setCollDate(dateFormatDay.format(selectedDate.getDate()));
                    orderRequest.setCollTime(dateFormatTime.format(selectedDate.getDate()));
                }
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setOrderPrice : isHurry : " + selectedDate.isHurry());
                if (selectedDate.isHurry()) {
                    // set hurry true
                    orderRequest.setHurry("Y");
                } else {
                    // set hurry false
                    orderRequest.setHurry("N");
                }
            }

            orderRequest.setIdLocality(2);
            // for push notifications
            orderRequest.setDevice_token(GCMUtils.getRegistrationId());

            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.createNewOrder : order duration : "
                    + ((MainActivity)mContext).getOrderDuration());
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.createNewOrder : order price : "
                    + ((MainActivity)mContext).getOrderPrice());

            // set Driver price & duration
            if(((MainActivity)mContext).getOrderDuration() > -1) {
                orderRequest.setEta(String.valueOf(((MainActivity)mContext).getOrderDuration()));
            }
            if(((MainActivity)mContext).getOrderPrice() > 0) {
                orderRequest.setPrice_prediction(String.valueOf(((MainActivity)mContext).getOrderPrice()));
            }

            GooglePlace callAddress = getFromAddress();
            if (callAddress != null && callAddress.getLatLng() != null) {
                if (callAddress.isAirport()) {
                    orderRequest.setCollAddrTypeMenu(3);
                    if (callAddress.getId() != null && !callAddress.getId().isEmpty())
                        orderRequest.setCollLandmark(Integer.parseInt(callAddress.getId()));
                    if (callAddress.getAirportTerminalId() != null && !callAddress.getAirportTerminalId().isEmpty())
                        orderRequest.setCollTerminal(Integer.parseInt(callAddress.getAirportTerminalId()));

                } else if (callAddress.isRailwayStation()) {
                    orderRequest.setCollAddrTypeMenu(2);
                    if (callAddress.getId() != null && !callAddress.getId().isEmpty())
                        orderRequest.setCollLandmark(Integer.parseInt(callAddress.getId()));

                } else if (callAddress.isHasPlaceId()) {
                    orderRequest.setCollAddrTypeMenu(1);
                    // create geo object JSON
                    collAddressGeoObject = getGeoDataJsonObject(callAddress);
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : collAddressGeoObject : " + collAddressGeoObject);
                    if (collAddressGeoObject != null)
                        orderRequest.setCollGeoObject(collAddressGeoObject);

                } else {
                    orderRequest.setCollAddrTypeMenu(1);

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : collAddressGeoObject : " + collAddressGeoObject);
                    if (collAddressGeoObject != null)
                        orderRequest.setCollGeoObject(collAddressGeoObject);
                }

                if(callAddress.isFavorite()) {
                    orderRequest.setCollAddressText(callAddress.getAddress());
                } else if (callAddress.getName() != null && !callAddress.getName().equals("")) {
                    orderRequest.setCollAddressText(callAddress.getName());
                }

                if (callAddress.getLatLng() != null) {
                    orderRequest.setLatitude(callAddress.getLatLng().latitude + "");
                    orderRequest.setLongitude(callAddress.getLatLng().longitude + "");
                }

                if (callAddress.getId() != null && !callAddress.getId().equals("")
                        && (callAddress.isFavorite() || callAddress.isLastAddress())) {
                    orderRequest.setCollAddressId(callAddress.getId());
                }

                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : CALLADDRESS : deliveryAddress.getComment() : " + callAddress.getComment());
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : CALLADDRESS : deliveryAddress.getEntrance() : " + callAddress.getEntrance());

                if (callAddress.getComment() != null && !callAddress.getComment().equals("")) {
                    orderRequest.setCollComment(callAddress.getComment());
                }

                if (order_entrance != null && !order_entrance.isEmpty()) {
                    // Setting order entrance
                    orderRequest.setCollPodjed(order_entrance);
                } else if (callAddress.getEntrance() != null && !callAddress.getEntrance().equals("")) {
                    orderRequest.setCollPodjed(callAddress.getEntrance());
                }

                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : CollGeoObject : " + new Gson().toJson(callAddress));
            } else {
                Toast.makeText(mContext, R.string.alert_select_address, Toast.LENGTH_SHORT).show();
                isOrderCreated = false;
                getOrderStateHelper().getTextViewConfirmOrder().setEnabled(true);
                return;
            }

            GooglePlace deliveryAddress = getToAddress();
            if (deliveryAddress != null) {
                if (deliveryAddress.isAirport()) {
                    orderRequest.setDeliveryAddrTypeMenu(3);
                    if (deliveryAddress.getId() != null && !deliveryAddress.getId().isEmpty())
                        orderRequest.setDeliveryLandmark(Integer.parseInt(deliveryAddress.getId()));
                    if (deliveryAddress.getAirportTerminalId() != null && !deliveryAddress.getAirportTerminalId().isEmpty())
                        orderRequest.setDeliveryTerminal(Integer.parseInt(deliveryAddress.getAirportTerminalId()));

                } else if (deliveryAddress.isRailwayStation()) {
                    orderRequest.setDeliveryAddrTypeMenu(2);
                    if (deliveryAddress.getId() != null && !deliveryAddress.getId().isEmpty())
                        orderRequest.setDeliveryLandmark(Integer.parseInt(deliveryAddress.getId()));

                } else if (deliveryAddress.isHasPlaceId()) {
                    orderRequest.setDeliveryAddrTypeMenu(1);
                    // create geo object JSON
                    deliveryAddressGeoObject = getGeoDataJsonObject(deliveryAddress);
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : deliveryAddressGeoObject : " + deliveryAddressGeoObject);
                    if (deliveryAddressGeoObject != null)
                        orderRequest.setDeliveryGeoObject(deliveryAddressGeoObject);

                } else {
                    orderRequest.setDeliveryAddrTypeMenu(1);

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : deliveryAddressGeoObject : " + deliveryAddressGeoObject);
                    if (deliveryAddressGeoObject != null)
                        orderRequest.setDeliveryGeoObject(deliveryAddressGeoObject);

                }

                if(deliveryAddress.isFavorite()) {
                    orderRequest.setDeliveryAddressText(deliveryAddress.getAddress());
                } else if (deliveryAddress.getName() != null && !deliveryAddress.getName().isEmpty())
                    orderRequest.setDeliveryAddressText(deliveryAddress.getName());

                if (deliveryAddress.getLatLng() != null) {
                    orderRequest.setDel_latitude(deliveryAddress.getLatLng().latitude + "");
                    orderRequest.setDel_longitude(deliveryAddress.getLatLng().longitude + "");
                }

                if (deliveryAddress.getId() != null && !deliveryAddress.getId().equals("")
                        && (deliveryAddress.isFavorite() || deliveryAddress.isLastAddress())) {
                    orderRequest.setDeliveryAddressId(deliveryAddress.getId());
                }

                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : DELIVERY : deliveryAddress.getComment() : " + deliveryAddress.getComment());
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : DELIVERY : deliveryAddress.getEntrance() : " + deliveryAddress.getEntrance());

                if (deliveryAddress.getComment() != null && !deliveryAddress.getComment().equals("")) {
                    orderRequest.setDeliveryComment(deliveryAddress.getComment());
                }

                if (deliveryAddress.getEntrance() != null && !deliveryAddress.getEntrance().equals("")) {
                    orderRequest.setDeliveryPodjed(deliveryAddress.getEntrance());
                }
            }

            // PAYMENT TYPES
            PaymentInfo currentPaymentInfo = getSelectedPayment();
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : currentPaymentInfo : " + currentPaymentInfo.getPayment_name());
            switch (currentPaymentInfo.getPaymentType()) {
                case CASH:
                    orderRequest.setNoCashPayment(0);
                    orderRequest.setOnlinePayment(0);
                    break;
                case BONUS:
                    orderRequest.setNoCashPayment(0);
                    orderRequest.setOnlinePayment(0);
                    orderRequest.setUseBonus(1);
                    break;
                case CREDIT_CARD:
                    orderRequest.setNeed_card(1);
                    orderRequest.setOnlinePayment(1);
                    orderRequest.setNoCashPayment(0);
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : CARD_ID : " + currentPaymentInfo.getCardInfo().getId());
                    orderRequest.setId_card(Integer.parseInt(currentPaymentInfo.getCardInfo().getId()));
                    break;
                case CORPORATION:
                    orderRequest.setNoCashPayment(1);
                    orderRequest.setOnlinePayment(0);
                    orderRequest.setId_corporation(Integer.parseInt(currentPaymentInfo.getCorporationId()));
                    break;
                default:
                    orderRequest.setNoCashPayment(0);
                    orderRequest.setOnlinePayment(0);
                    break;
            }

            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : order_comment : " + order_comment);
            // Setting order comment
            orderRequest.setOrderComment(order_comment);

            Tariff currentTariff = getSelectedTariff();
            if (currentTariff != null) {
                orderRequest.setTariff(currentTariff.getID());
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : current_tariff : " + currentTariff.getName());
            }

            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : slidePanelAdapter.getSelectedTariffServices() : " + getSelectedTariffServices());

            setTariffServices(orderRequest, getSelectedTariffServices());

            String s = new Gson().toJson(orderRequest);
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : NEW_ORDER : JSON REQUEST : " + s);

            // Create New NewOrderData
            orderManager.NewOrder(orderRequest);
        }
    } // end method createNewOrder

    /**
     * Method for getting address from Google Apis
     */
    private void getGoogleApiAddress() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.getGoogleApiAddress ");
        // Request for getting coll Address Geo Object
        GoogleManager googleManager = new GoogleManager((ICallBack) mContext);
        GooglePlace callAddress = getFromAddress();

        if (callAddress != null) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : callAddress : hasPlaceId : " + callAddress.isHasPlaceId());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : callAddress : isFavorite : " + callAddress.isFavorite());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : callAddress : isLastAddress : " + callAddress.isLastAddress());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : callAddress : isAirport : " + callAddress.isAirport());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : callAddress : isRailwayStation : " + callAddress.isRailwayStation());

            if (callAddress.getLatLng() != null
                    && collAddressGeoObject == null
                    && !callAddress.isAirport()
                    && !callAddress.isRailwayStation()
                    && !callAddress.isHasPlaceId()
                    && !callAddress.isFavorite()
                    && !callAddress.isLastAddress()) {
                String latLng = callAddress.getLatLng().latitude + "," + callAddress.getLatLng().longitude;
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment : layoutSectionTariff.OnClick : callAddress : latLng : " + latLng);
                googleManager.GetGeocodeForCollAddress(latLng);
            }
        }

        // Request for getting delivery Address Geo Object
        GooglePlace deliveryAddress = getToAddress();

        if (deliveryAddress != null) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : deliveryAddress : hasPlaceId : " + deliveryAddress.isHasPlaceId());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : deliveryAddress : isFavorite : " + deliveryAddress.isFavorite());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : deliveryAddress : isAirport : " + deliveryAddress.isAirport());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment : PLACE : deliveryAddress : isRailwayStation : " + deliveryAddress.isRailwayStation());

            if (deliveryAddress.getLatLng() != null
                    && deliveryAddressGeoObject == null
                    && !deliveryAddress.isAirport()
                    && !deliveryAddress.isRailwayStation()
                    && !deliveryAddress.isHasPlaceId()
                    && !deliveryAddress.isFavorite()) {
                String latLng = deliveryAddress.getLatLng().latitude + "," + deliveryAddress.getLatLng().longitude;
                googleManager.GetGeocodeForDeliveryAddress(latLng);
            }
        }


    } // end method getGoogleApiAddress

    /**
     * Handler for sending message to slide up panel
     */
    static class OrderConfirmHandler extends Handler {
        private final WeakReference<SlidingUpPanelLayout> slidingUpPanelLayout;


        public OrderConfirmHandler(SlidingUpPanelLayout slidingUpPanelLayout) {
            this.slidingUpPanelLayout = new WeakReference<>(slidingUpPanelLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SlidingUpPanelLayout slidingUpPanelLayout = this.slidingUpPanelLayout.get();
            if (slidingUpPanelLayout != null) {
                switch (msg.what) {
                    case EXPAND_PANEL:
                        slidingUpPanelLayout.expandPane();
                        break;
                    case COLLAPSE_PANEL:
                        slidingUpPanelLayout.collapsePane();
                        break;
                }
            }
        }
    }

    /**
     * Method for showing or hiding layout item for scroll
     * @param hide - If true hide else show
     */
    public void showOrHideLayoutItemForScroll(boolean hide) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.showOrHideLayoutItemForScroll : hide : " + hide);

        if (hide) {
            layoutItemForScroll.setVisibility(View.GONE);
        } else {
            layoutItemForScroll.setVisibility(View.VISIBLE);
        }

    } // end method showOrHideLayoutItemForScroll


    /**
     * Method for showing confirmation state
     */
    public void showOrderConfirmPage() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.showOrderConfirmPage ");

        // Hiding layout for scroll
        layoutItemForScroll.setVisibility(View.GONE);

        // CHANGE SLIDE PANEL HEIGHT
        changeSlidePanelContentHeight(false);

        if (!mSlidingUpPanelLayout.isExpanded()) {
            // Sending message to slide panel
            Message message = new Message();
            message.what = EXPAND_PANEL;
            orderConfirmHandler.sendMessage(message);
        }

        // map buttons
        mapFragment.hideControls(rootView);

        isSlidePanelOpened = mSlidingUpPanelLayout.isExpanded();

        // enable slide panel
        mSlidingUpPanelLayout.setSlidingEnabled(false);

        orderHelper.setPanelState(isSlidePanelOpened);
        // creating subtitle
        String subTitle = mContext.getResources().getString(R.string.os_order_confirm_subtitle);

        GooglePlace toAddress = getToAddress();
        if (toAddress != null && toAddress.getLatLng() != null) {
            subTitle = mContext.getResources().getString(R.string.str_price) + " " + ((MainActivity)mContext).getOrderPrice() + " " + mContext.getResources().getString(R.string.str_ruble)
                    + " " + getResources().getString(R.string.str_behind) + " " + ((MainActivity)mContext).getOrderDuration() + " " + getResources().getString(R.string.str_minute);
        }
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment :: subTitle : " + subTitle);

        // order register state
        orderHelper.SetOrderState(rootView, OrderState.ORDER_CONFIRM, false, "", "", "", "",
            mContext.getResources().getString(R.string.os_order_confirm),
            subTitle, true);

        mConfirmVisible = true;


    } // end method showOrderConfirmPage

    /**
     * Method for changing slide panel height
     */
    public void changeSlidePanelContentHeight(boolean revert) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.changeSlidePanelContentHeight : revert : " + revert);

        if(!revert) {
            Point appUsableScreenSize = CMApplication.getAppUsableScreenSize(mContext);
            Point realScreenSize = CMApplication.getRealScreenSize(mContext);
            int topHeight = layoutTop.getHeight();

            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.changeSlidePanelContentHeight : appUsableScreenSize.y : " + appUsableScreenSize.y);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.changeSlidePanelContentHeight : realScreenSize.y : " + realScreenSize.y);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.changeSlidePanelContentHeight : topHeight : " + topHeight);

            int newHeight = appUsableScreenSize.y - topHeight;

            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.changeSlidePanelContentHeight : newHeight : " + newHeight);

            ViewGroup.LayoutParams layoutParams = scrollViewSlidePanelContainer.getLayoutParams();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                layoutParams.height = newHeight-CMApplication.dpToPx(24);
            } else {
                layoutParams.height = newHeight;
            }
            scrollViewSlidePanelContainer.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = scrollViewSlidePanelContainer.getLayoutParams();
            layoutParams.height = (int) mContext.getResources().getDimension(R.dimen.slide_panel_content_height);
            scrollViewSlidePanelContainer.setLayoutParams(layoutParams);
        }

    } // end method changeSlidePanelContentHeight

    /**
     * Method for hiding confirmation state
     */
    public void hideOrderConfirmPage() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.hideOrderConfirmPage ");

        mConfirmVisible = false;

        CustomTextView textView = ((CustomTextView) rootView.findViewById(R.id.textViewTariffTitle));
        textView.setText(mContext.getResources().getString(R.string.onward_text));
        float fontScale = mContext.getResources().getConfiguration().fontScale;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                mContext.getResources().getInteger(R.integer.int_tariff_title_text_size) / fontScale);

        changeSlidePanelContentHeight(true);

    } // end method hideOrderConfirmPage

    public void actionPanel(SlideState state) {
        mSlidingUpPanelLayout.setPanelState(state);
    }


    /**
     * Method for setting tariff services
     *
     * @param orderRequest - request object of new order
     * @param services     - list of services
     */
    public void setTariffServices(OrderRequest orderRequest, List<TariffService> services) {
        if (orderRequest != null && services != null) {
            for (int i = 0; i < services.size(); i++) {
                String orderField = services.get(i).getField();
                if (orderField != null) {
                    switch (orderField) {
                        case "g_width":
                            orderRequest.setG_width(1);
                            break;
                        case "need_wifi":
                            orderRequest.setNeed_wifi(1);
                            break;
                        case "need_check":
                            orderRequest.setNeed_check(1);
                            break;
                        case "conditioner":
                            orderRequest.setConditioner(1);
                            break;
                        case "NoSmoking":
                            orderRequest.setNoSmoking(1);
                            break;
                        case "child_seats":
                            if (SlidePanelHelper.getChildrenSeats(mAdapterChild) != null) {
                                orderRequest.setChild_seats(SlidePanelHelper.getChildrenSeats(mAdapterChild));
                            }
                            break;
                        case "e_type":
                            orderRequest.setE_type(1);
                            break;
                        case "meeting":
                            orderRequest.setMeeting(1);
                            break;
                        case "yellow_reg_num":
                            orderRequest.setYellow_reg_num(1);
                            break;
                        case "animal":
                            orderRequest.setAnimal(1);
                            break;
                        case "luggage":
                            orderRequest.setLuggage(1);
                            break;
                    }
                }

            }
        }

    } // end method setTariffServices

    /**
     * Method for setting tariff services
     *
     * @param orderRequest - request object of new order
     * @param services     - list of services
     */
    public void setTariffServices(GetPriceRequest orderRequest, List<TariffService> services) {
        if (services != null && orderRequest != null) {
            for (int i = 0; i < services.size(); i++) {
                String orderField = services.get(i).getField();
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setTariffServices : orderField : " + orderField);
                if (orderField != null) {
                    switch (orderField) {
                        case "g_width":
                            orderRequest.setG_width(1);
                            break;
                        case "need_wifi":
                            orderRequest.setNeed_wifi(1);
                            break;
                        case "need_check":
                            orderRequest.setNeed_check(1);
                            break;
                        case "conditioner":
                            orderRequest.setConditioner(1);
                            break;
                        case "NoSmoking":
                            orderRequest.setNoSmoking(1);
                            break;
                        case "child_seats":
                            if (SlidePanelHelper.getChildrenSeats(mAdapterChild) != null) {
                                orderRequest.setChild_seats(SlidePanelHelper.getChildrenSeats(mAdapterChild));
                            }
                            break;
                        case "e_type":
                            orderRequest.setE_type(1);
                            break;
                        case "meeting":
                            orderRequest.setMeeting(1);
                            break;
                        case "yellow_reg_num":
                            orderRequest.setYellow_reg_num(1);
                            break;
                        case "animal":
                            orderRequest.setAnimal(1);
                            break;
                        case "luggage":
                            orderRequest.setLuggage(1);
                            break;
                    }
                }

            }
        }

    } // end method setTariffServices

    /**
     * Method for setting tariff services
     *
     * @param getDriversRequest - request object get drivers
     * @param services          - list of services
     */
    public void setTariffServices(GetDriversRequest getDriversRequest, List<TariffService> services) {
        if (services != null && getDriversRequest != null) {
            for (int i = 0; i < services.size(); i++) {
                String orderField = services.get(i).getField();
                if (orderField != null) {
                    switch (orderField) {
                        case "g_width":
                            getDriversRequest.setG_width(1);
                            break;
                        case "need_wifi":
                            getDriversRequest.setNeed_wifi(1);
                            break;
                        case "need_check":
                            getDriversRequest.setNeed_check(1);
                            break;
                        case "conditioner":
                            getDriversRequest.setConditioner(1);
                            break;
                        case "NoSmoking":
                            getDriversRequest.setNoSmoking(1);
                            break;
                        case "e_type":
                            getDriversRequest.setE_type(1);
                            break;
                        case "animal":
                            getDriversRequest.setAnimal(1);
                            break;
                        case "child_seats":
                            getDriversRequest.setBaby_seat(getDriversRequest.getBaby_seat());
                            break;
                    }
                }
            }
        }

    } // end method setTariffServices

    private Handler sliderHandler = new Handler();

    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            mSlidingUpPanelLayout.CollapsePanelFirstTime(SlideState.COLLAPSED);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        if (mLat != null && mLng != null) {
            mapFragment.setMyLocation(mLat, mLng);
        }
        // set current order state UI
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.onResume : orderID : " + CMApplication.getTrackingOrderId() +
                    " : status : " + CMApplication.getTrackingOrderStatus() +
                    " : price : " + CMApplication.getTrackingOrderPrice() +
                    " : trip time : " + CMApplication.getTrackingOrderTripTime() +
                    " : bonus : " + CMApplication.getTrackingOrderBonus());

    }

    /**
     * Method for setting current location on map.
     *
     * @param mLng - location longitude
     * @param mLat - location latitude
     */
    public void setCurrentLocation(double mLat, double mLng) {
        if (this.mapFragment != null) {
            ((MapGoogleFragment) this.mapFragment).setMyLocation(mLat, mLng);
        }

    } // end method setCurrentLocation

    /**
     * Method for setting driver time on bottom bar
     *
     * @param timeMinute - time in minutes
     */
    public void setDriverTimeText(int timeMinute) {
        if (isAdded()) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setDriverTimeText : timeMinute : " + timeMinute);

            TextView textViewTariffTime = (TextView) rootView.findViewById(R.id.textViewTariffTime);
            if (timeMinute == -1) {
                textViewTariffTime.setText("");
            } else {
                String timeString = getResources().getString(R.string.str_behind) + " " + timeMinute + " " + getResources().getString(R.string.str_minute);
                textViewTariffTime.setText(timeString);
            }
        }

    } // end method setDriverTimeText

    /**
     * Method for setting order price on bottom bar
     *
     * @param price - order price
     */
    public void setOrderPriceText(int price, boolean isDefault) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setOrderPriceText : setOrderPriceText : " + price);
        CustomTextView textViewTariffPrice = (CustomTextView) rootView.findViewById(R.id.textViewTariffPrice);
        float fontScale = mContext.getResources().getConfiguration().fontScale;
        textViewTariffPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                mContext.getResources().getInteger(R.integer.int_tariff_price_text_size) / fontScale);
        String tariffPriceText;
        if (isDefault) {
            String mPrice = "";
            Tariff currentTariff = getSelectedTariff();
            if (currentTariff != null) {
                List<TariffInterval> intervals = currentTariff.getIntervals();
                if (intervals != null && !intervals.isEmpty()) {
                    Calendar calendarNow = Calendar.getInstance();
                    Date dateNow = calendarNow.getTime();

                    List<Date> dayTimes = DateHelper.getDayTimes();

                    if (intervals.size() > 1) {
                        if (dateNow.after(dayTimes.get(0)) && dateNow.before(dayTimes.get(1))) {
                            // 09:00 - 21:00 day
                            for (TariffInterval interval : intervals) {
                                if (interval.getType_by_date() != null && interval.getType_by_date().equals("2")) {
                                    mPrice = interval.getPrice();
                                    break;
                                }
                            }
                        } else {
                            // 21:01 - 08:59 night
                            for (TariffInterval interval : intervals) {
                                if (interval.getType_by_date() != null && interval.getType_by_date().equals("1")) {
                                    mPrice = interval.getPrice();
                                    break;
                                }
                            }
                        }
                    } else {
                        for (TariffInterval interval : intervals) {
                            if (interval.getType_by_date() != null && interval.getType_by_date().equals("0")) {
                                mPrice = interval.getPrice();
                                break;
                            }
                        }
                    }
                }
            }
            tariffPriceText = mPrice + " " + mContext.getResources().getString(R.string.str_tariff_test);
            textViewTariffPrice.setText(tariffPriceText);
        } else {
            tariffPriceText = mContext.getResources().getString(R.string.str_price) + " " + price + " " + mContext.getResources().getString(R.string.str_ruble);
            textViewTariffPrice.setText(tariffPriceText);
        }

    } // end method setOrderPriceText

    /**
     * Method for setting service options
     */
    @Override
    public void setServiceOptions(boolean unset, Order order) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.setServiceOptions : unset : " + unset);

        if(unset) {
            // set panel data
            setSlidePanelData();

            // set adapters
            addSlidePanelItem(rootView, ScrollType.SO_FROM, blurGradient);
            addSlidePanelItem(rootView, ScrollType.SO_WHERE, blurGradient);
            addSlidePanelItem(rootView, ScrollType.SO_WHEN, blurGradient);
            if (tariff_list != null && tariff_list.size() > 0)
                addSlidePanelItem(rootView, ScrollType.SO_CAR, blurGradient);
            if (tariff_services != null && tariff_services.size() > 0)
                addSlidePanelItem(rootView, ScrollType.SO_WHISHES, blurGradient);
            // CHILD_SEAT
            if (child_seat_list != null && child_seat_list.size() > 0)
                addSlidePanelItem(rootView, ScrollType.SO_CHILD_SEAT, blurGradient);
            // PAYMENT
            addSlidePanelItem(rootView, ScrollType.SO_PAYMENT, blurGradient);
            // hide child seat
            if(layoutItemChild != null && layoutItemChild.getVisibility() == View.VISIBLE) {
                layoutItemChild.setVisibility(View.GONE);
            }
        } else {
            if (order != null) {
                // UPDATING FROM ADDRESS LIST
                int selectedFromPosition = 0;
                if (order.getCollAddressText() != null && !order.getCollAddressText().isEmpty()) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment.setServiceOptions : CollAddressText : " + order.getCollAddressText());

                    if (order.getCollAddressId() != null) {
                        for (int i = 0; i < fromAddressList.size(); i++) {
                            if (fromAddressList.get(i).getId() != null && fromAddressList.get(i).getId().equals(order.getCollAddressId())) {
                                selectedFromPosition = i;
                                GooglePlace fromAddress = new GooglePlace();
                                fromAddress.setLatLng(new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitude())));
                                fromAddress.setName(order.getCollAddressText());
                                if (!fromAddressList.isEmpty()) {
                                    fromAddressList.set(0, fromAddress);
                                }
                                break;
                            }
                        }
                    }

                    if (order.getLatitude() != null && !order.getLatitude().isEmpty()
                            && order.getLongitude() != null && !order.getLongitude().isEmpty()
                            && selectedFromPosition == 0) {
                        GooglePlace fromAddress = new GooglePlace();
                        fromAddress.setLatLng(new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitude())));
                        fromAddress.setName(order.getCollAddressText());
                        if (!fromAddressList.isEmpty()) {
                            fromAddressList.set(0, fromAddress);
                        }
                    }
                }
                mAdapterFrom = new AddressRVAdapter(mContext, fromAddressList, ScrollType.SO_FROM, selectedFromPosition, fromAddressList.size() > 2);
                mAdapterFrom.setOnItemClickListener(this);
                scrollViewFrom.setAdapter(mAdapterFrom);
                layoutManagerFrom.scrollToPosition(mAdapterFrom.MIDDLE + selectedFromPosition);

                // UPDATING TO ADDRESS LIST
                int selectedToPosition = 0;
                if (order.getDeliveryAddressText() != null && !order.getDeliveryAddressText().isEmpty()) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: MapViewFragment.setServiceOptions : DeliveryAddressText : " + order.getDeliveryAddressText());

                    if (order.getDeliveryAddressId() != null) {
                        for (int i = 0; i < toAddressList.size(); i++) {
                            if (toAddressList.get(i).getId() != null && toAddressList.get(i).getId().equals(order.getDeliveryAddressId())) {
                                selectedToPosition = i;
                                break;
                            }
                        }
                    }
                    if (order.getDelLatitude() != null && !order.getDelLatitude().isEmpty()
                            && order.getDelLongitude() != null && !order.getDelLongitude().isEmpty()
                            && selectedToPosition == 0) {
                        GooglePlace toAddress = new GooglePlace();
                        toAddress.setLatLng(new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitude())));
                        toAddress.setName(order.getDeliveryAddressText());
                        if (!toAddressList.isEmpty()) {
                            if (mAdapterTo.isInfinite()) {
                                toAddressList.set(toAddressList.size() - 1, toAddress);
                                selectedToPosition = toAddressList.size() - 1;
                            } else {
                                GooglePlace firstPlace = toAddressList.get(0);
                                toAddressList.set(0, toAddress);
                                toAddressList.set(1, firstPlace);
                                selectedToPosition = 0;
                            }
                        }
                    }
                }

                mAdapterTo = new AddressRVAdapter(mContext, toAddressList, ScrollType.SO_WHERE, selectedToPosition, toAddressList.size() > 2);
                mAdapterTo.setOnItemClickListener(this);
                scrollViewTo.setAdapter(mAdapterTo);
                layoutManagerTo.scrollToPosition(mAdapterTo.MIDDLE + selectedToPosition);

                // UPDATING DATE LIST
                int selectedDatePosition;
                if (order.getHurry() == 1) {
                    selectedDatePosition = 0;
                } else {

                    DateHelper dateHelper = new DateHelper(mContext);
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.setServiceOptions : CollDate : " + order.getCollDate()
                            + " : CollTime : " + order.getCollTime());
                    Date itemDate = DateHelper.getDateByStringDateFormat(order.getCollDate() + " " + order.getCollTime(), "dd-MM-yyyy HHmm");
                    DateData dateData = new DateData();
                    dateData.setDate(itemDate);
                    dateData.setTitle(dateHelper.getStringDateByUI(itemDate));

                    if (mAdapterDate.isInfinite()) {
                        selectedDatePosition = 1;
                        dateList.set(selectedDatePosition, dateData);
                    } else {
                        selectedDatePosition = 0;
                        DateData firstDateData = dateList.get(0);
                        dateList.set(0, dateData);
                        dateList.set(1, firstDateData);
                    }

                }
                mAdapterDate = new DateRVAdapter(mContext, dateList, selectedDatePosition, dateList.size() > 2);
                mAdapterDate.setOnItemClickListener(this);
                recyclerViewDate.setAdapter(mAdapterDate);
                layoutManagerDate.scrollToPosition(mAdapterDate.MIDDLE + selectedDatePosition);

                // UPDATING TARIFF LIST
                String current_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setServiceOptions : IdLocality : " + order.getIdLocality());
                if (order.getIdLocality() != 0 && !current_city_id.isEmpty() && order.getIdLocality() != Integer.parseInt(current_city_id)) {
                    // Getting tariff list
                    TariffDBManager tariffDBManager = new TariffDBManager(mContext);
                    int languageID = new LanguageDBManager(mContext).getLanguageIdByLocale("ru");
                    tariff_list = tariffDBManager.getAllTariffs(languageID, order.getIdLocality());
                }
                if (tariff_list != null && !tariff_list.isEmpty()) {
                    int selectedTariffPosition = 0;
                    if (order.getTariff() != null) {
                        for (int i = 0; i < tariff_list.size(); i++) {
                            if (tariff_list.get(i).getID().equals(order.getTariff())) {
                                if (mAdapterTariff.isInfinite()) {
                                    selectedTariffPosition = i;
                                } else {
                                    if (i != 0) {
                                        Tariff firstTariff = tariff_list.get(0);
                                        tariff_list.set(0, tariff_list.get(i));
                                        tariff_list.set(1, firstTariff);
                                        selectedTariffPosition = 0;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    mAdapterTariff = new TariffRVAdapter(mContext, tariff_list, selectedTariffPosition, tariff_list.size() > 2);
                    mAdapterTariff.setFirst(false);
                    mAdapterTariff.setOnItemClickListener(this);
                    recyclerViewTariff.setAdapter(mAdapterTariff);
                    layoutManagerTariff.scrollToPosition(mAdapterTariff.MIDDLE + selectedTariffPosition);

                    // UPDATING SERVICE LIST
                    tariff_services = (ArrayList<TariffService>) tariff_list.get(selectedTariffPosition).getServices();
                    if (tariff_services != null && !tariff_services.isEmpty()) {
                        List<TariffService> selectedTariffServices = new ArrayList<>();
                        int tariffServiceFirstSelectedPosition = 0;
                        List<String> tariffServiceFields = getTariffServiceFields(order);
                        if (!tariffServiceFields.isEmpty()) {
                            boolean isFirst = true;
                            for (int i = 0; i < tariff_services.size(); i++) {
                                if (tariffServiceFields.contains(tariff_services.get(i).getField())) {
                                    selectedTariffServices.add(tariff_services.get(i));
                                    if (isFirst) {
                                        tariffServiceFirstSelectedPosition = i;
                                        isFirst = false;
                                    }
                                }
                            }
                        }
                        mAdapterService = new TariffServiceRVAdapter(mContext, tariff_services, tariff_services.size() > 2);
                        mAdapterService.setOnItemClickListener(this);
                        if (!selectedTariffServices.isEmpty())
                            mAdapterService.setSelectedTariffServices(selectedTariffServices);
                        recyclerViewService.setAdapter(mAdapterService);
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: MapViewFragment.setServiceOptions : tariffServiceFirstSelectedPosition : " + tariffServiceFirstSelectedPosition);
                        layoutManagerTariffService.scrollToPosition(mAdapterService.MIDDLE + tariffServiceFirstSelectedPosition);
                    }
                }

                // UPDATING CHILD_SEAT LIST
                if (order.getChildSeats() != null && !order.getChildSeats().isEmpty()) {
                    int firstSelectedChildSeatPosition = 0;
                    List<ChildSeat> selectedChildSeats = new ArrayList<>();
                    List<ChildSeatData> childSeatDataList = order.getChildSeats();
                    boolean isFirst = true;
                    for (int i = 0; i < childSeatDataList.size(); i++) {
                        if (childSeatDataList.get(i).getWeight() != null && !childSeatDataList.get(i).getWeight().isEmpty()) {
                            int childSeatPosition = Integer.parseInt(childSeatDataList.get(i).getWeight()) - 1;
                            if (childSeatPosition >= 0) {
                                if (isFirst) {
                                    isFirst = false;
                                    firstSelectedChildSeatPosition = childSeatPosition;
                                }
                                ChildSeat childSeat = child_seat_list.get(childSeatPosition);
                                selectedChildSeats.add(childSeat);
                            }
                        }
                    }
                    getLayoutItemChild().setVisibility(View.VISIBLE);
                    mAdapterChild = new ChildSeatRVAdapter(mContext, child_seat_list);
                    mAdapterChild.setOnItemClickListener(this);
                    mAdapterChild.setSelectedChildSeats(selectedChildSeats);
                    recyclerViewChild.setAdapter(mAdapterChild);
                    layoutManagerChildSeat.scrollToPosition(mAdapterChild.MIDDLE + firstSelectedChildSeatPosition);
                } else {
                    getLayoutItemChild().setVisibility(View.GONE);
                }

                // UPDATING PAYMENT LIST
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setServiceOptions : NoCashPayment : " + order.getNoCashPayment()
                            + " : OnlinePayment : " + order.getOnlinePayment()
                            + " : UseBonus : " + order.getUseBonus()
                            + " : NeedCard : " + order.getNeedCard()
                    );
                PaymentTypes selectedPaymentType = null;
                if (order.getNoCashPayment() == 0
                        && order.getOnlinePayment() != null && order.getOnlinePayment().equals("0")
                        && order.getUseBonus() == 1) {
                    // payment type is bonus
                    selectedPaymentType = PaymentTypes.BONUS;
                } else if (order.getNoCashPayment() == 0
                        && order.getOnlinePayment() != null && order.getOnlinePayment().equals("1")) {
                    // payment type is card
                    selectedPaymentType = PaymentTypes.CREDIT_CARD;
                } else if (order.getNoCashPayment() == 1
                        && order.getOnlinePayment() != null && order.getOnlinePayment().equals("0")) {
                    // payment type is corporation
                    selectedPaymentType = PaymentTypes.CORPORATION;
                } else if (order.getNoCashPayment() == 0
                        && order.getOnlinePayment() != null && order.getOnlinePayment().equals("0")) {
                    // payment type is cash
                    selectedPaymentType = PaymentTypes.CASH;
                }
                int selectedPaymentPosition = 0;
                if (selectedPaymentType != null) {
                    for (int i = 0; i < paymentList.size(); i++) {
                        if (selectedPaymentType == paymentList.get(i).getPaymentType()) {
                            if (selectedPaymentType != PaymentTypes.CREDIT_CARD) {
                                if (mAdapterPayment.isInfinite()) {
                                    selectedPaymentPosition = i;
                                } else {
                                    if (i != 0) {
                                        PaymentInfo firstInfo = paymentList.get(0);
                                        PaymentInfo secondInfo = paymentList.get(1);
                                        paymentList.set(0, secondInfo);
                                        paymentList.set(1, firstInfo);
                                    }
                                }
                                break;
                            } else {
                                CardInfo cardInfo = paymentList.get(i).getCardInfo();
                                if (cardInfo != null)
                                    if (CMAppGlobals.DEBUG)
                                        Logger.i(TAG, ":: MapViewFragment.setOrderServiceOptions : currentOrder.getId_card : " + order.getId_card()
                                                + " : cardInfo.getId : " + cardInfo.getId());

                                String id_card = order.getId_card() + "";
                                if (cardInfo != null && cardInfo.getId().equals(id_card)) {
                                    if (mAdapterPayment.isInfinite()) {
                                        selectedPaymentPosition = i;
                                    } else {
                                        if (i != 0) {
                                            PaymentInfo firstInfo = paymentList.get(0);
                                            PaymentInfo secondInfo = paymentList.get(1);
                                            paymentList.set(0, secondInfo);
                                            paymentList.set(1, firstInfo);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                mAdapterPayment = new PaymentRVAdapter(mContext, paymentList, selectedPaymentPosition, paymentList.size() > 2);
                mAdapterPayment.setOnItemClickListener(this);
                recyclerViewPayment.setAdapter(mAdapterPayment);
                layoutManagerPayment.scrollToPosition(mAdapterPayment.MIDDLE + selectedPaymentPosition);

                String strComment = "";
                if (order.getCollPodjed() != null && !order.getCollPodjed().isEmpty()) {
                    strComment = mContext.getResources().getString(R.string.str_entrance) + " " + order.getCollPodjed() + ". ";
                }
                if (order.getOrderComment() != null && !order.getOrderComment().isEmpty()) {
                    strComment += order.getOrderComment();
                }
                if (!strComment.isEmpty())
                    textViewComment.setText(strComment);

                setSlidePanelItemClickEnabled(false);
            }
        }

    } // end method setServiceOptions

    /**
     * Method for adding payment types
     *
     * @param clientInfo - client information
     */
    public void addPaymentMethods(ClientInfo clientInfo) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.addPaymentMethod : client_name : " + clientInfo.getName());
        mClientInfo = clientInfo;

        // REQUEST CREDIT CARDS
        getCreditCards();

        // PAYMENT
        addSlidePanelItem(rootView, ScrollType.SO_PAYMENT, blurGradient);

    } // end method addPaymentMethod

    /**
     * Method for setting card info list
     *
     * @param cardInfos - list of card info's
     */
    public void setCreditCards(List<CardInfo> cardInfos) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setCreditCards : cardInfos : " + cardInfos);

        mCardInfoList.clear();
        mCardInfoList.addAll(cardInfos);

        // Removing cards item
        for (int i = paymentList.size() - 1; i >= 0; i--) {
            if ((paymentList.get(i).getCardInfo() != null && paymentList.get(i).getCardInfo().getId() != null)
                    || paymentList.get(i).isNoCard()) {
                paymentList.remove(i);
            }
        }

        // CREDIT CARDS
        String payment_card;
        if (mCardInfoList != null && mCardInfoList.size() > 0) {
            for (int i = 0; i < mCardInfoList.size(); i++) {
                if (mCardInfoList.get(i).getName() != null &&!mCardInfoList.get(i).getName().equals(""))
                    payment_card = mCardInfoList.get(i).getName();
                else
                    payment_card = mCardInfoList.get(i).getId();

                PaymentInfo cardInfo = new PaymentInfo(payment_card, PaymentTypes.CREDIT_CARD);
                cardInfo.setCardInfo(mCardInfoList.get(i));
                if (paymentList.size() > 1) {
                    paymentList.add(paymentList.size() - 1, cardInfo);
                } else {
                    paymentList.add(cardInfo);
                }
            }
        } else {
            payment_card = mContext.getResources().getString(R.string.str_attach_card);
            PaymentInfo paymentInfo = new PaymentInfo(payment_card, PaymentTypes.CREDIT_CARD);
            paymentInfo.setIsNoCard(true);
            if (paymentList.size() > 1)
                paymentList.add(paymentList.size() - 1, paymentInfo);
            else
                paymentList.add(paymentInfo);
        }

        recyclerViewPayment = (RecyclerView) rootView.findViewById(R.id.recyclerViewPayment);
        layoutManagerPayment = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        int paymentSelectedPosition = 0;
        if (mAdapterPayment != null)
            paymentSelectedPosition = mAdapterPayment.getCurrentPosition();
        mAdapterPayment = new PaymentRVAdapter(mContext, paymentList, paymentSelectedPosition, paymentList.size() > 2);
        mAdapterPayment.setOnItemClickListener(this);
        recyclerViewPayment.setLayoutManager(layoutManagerPayment);
        recyclerViewPayment.setAdapter(mAdapterPayment);
        layoutManagerPayment.scrollToPosition(mAdapterPayment.MIDDLE + paymentSelectedPosition);

    } // end method setCreditCards

    /**
     * Method for getting credit cards
     */
    public void getCreditCards() {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.getCreditCards : mClientInfo : " + mClientInfo);

        if (mClientInfo != null) {
            CreditCardManager creditCardManager = new CreditCardManager((ICallBack) mContext);
            creditCardManager.GetCreditCards();
        }

    } // end method getCreditCards


    /**
     * Method for adding slide panel item by scroll type
     *
     * @param rootView - root view layout
     * @param scrollType - scroll type
     */
    public void addSlidePanelItem(View rootView, ScrollType scrollType, GradientDrawable blurGradient) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.addPanelListItems : scrollType : " + scrollType);

        switch (scrollType) {
            case SO_FROM: {
                ImageView imageViewBlurFrom = (ImageView) rootView.findViewById(R.id.imageViewBlurFrom);
                imageViewBlurFrom.setBackgroundDrawable(blurGradient);
                if (fromAddressList.isEmpty())
                    fromAddressList = (ArrayList<GooglePlace>) AddressRVAdapter.getListByScrollType(mContext, ScrollType.SO_FROM);
                // set adapter
                scrollViewFrom = (RecyclerView) rootView.findViewById(R.id.recyclerViewFrom);
                layoutManagerFrom = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                scrollViewFrom.setLayoutManager(layoutManagerFrom);
                mAdapterFrom = new AddressRVAdapter(mContext, fromAddressList, ScrollType.SO_FROM, 0, fromAddressList.size() > 2);
                mAdapterFrom.setOnItemClickListener(this);
                scrollViewFrom.setAdapter(mAdapterFrom);
                layoutManagerFrom.scrollToPosition(mAdapterFrom.MIDDLE);
            }
            break;
            case SO_WHERE: {
                ImageView imageViewBlurTo = (ImageView) rootView.findViewById(R.id.imageViewBlurTo);
                imageViewBlurTo.setBackgroundDrawable(blurGradient);

                ArrayList<GooglePlace> defaultFromAddressList = (ArrayList<GooglePlace>) AddressRVAdapter.getListByScrollType(mContext, ScrollType.SO_WHERE);
                // set adapter
                if (toAddressList.isEmpty()) {
                    toAddressList = defaultFromAddressList;
                } else {
                    for (GooglePlace item : toAddressList) {
                        if (!item.isFavorite()
                                && !item.getName().equals(defaultFromAddressList.get(0).getName())
                                && !item.getName().equals(defaultFromAddressList.get(defaultFromAddressList.size() -1).getName())) {
                            toAddressList.remove(item);
                            toAddressList.add(defaultFromAddressList.get(defaultFromAddressList.size() -1));
                            break;
                        }
                    }
                }

                if (scrollViewTo == null) {
                    scrollViewTo = (RecyclerView) rootView.findViewById(R.id.recyclerViewTo);
                    layoutManagerTo = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    scrollViewTo.setLayoutManager(layoutManagerTo);
                }
                mAdapterTo = new AddressRVAdapter(mContext, toAddressList, ScrollType.SO_WHERE, 0, toAddressList.size() > 2);
                mAdapterTo.setOnItemClickListener(this);
                scrollViewTo.setAdapter(mAdapterTo);
                layoutManagerTo.scrollToPosition(mAdapterTo.MIDDLE);
            }
            break;
            case SO_WHEN: {
                ImageView imageViewBlurDate = (ImageView) rootView.findViewById(R.id.imageViewBlurDate);
                imageViewBlurDate.setBackgroundDrawable(blurGradient);

                dateList = DateRVAdapter.getDateList(mContext);
                recyclerViewDate = (RecyclerView) rootView.findViewById(R.id.recyclerViewDate);
                layoutManagerDate = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                mAdapterDate = new DateRVAdapter(mContext, dateList, 0, dateList.size() > 2);
                mAdapterDate.setOnItemClickListener(this);
                layoutManagerDate.scrollToPosition(mAdapterDate.MIDDLE);
                recyclerViewDate.setLayoutManager(layoutManagerDate);
                recyclerViewDate.setAdapter(mAdapterDate);
            }
            break;
            case SO_CAR: {
                ImageView imageViewBlurTariff = (ImageView) rootView.findViewById(R.id.imageViewBlurTariff);
                imageViewBlurTariff.setBackgroundDrawable(blurGradient);
                recyclerViewTariff = (RecyclerView) rootView.findViewById(R.id.recyclerViewTariff);

                layoutManagerTariff = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                mAdapterTariff = new TariffRVAdapter(mContext, tariff_list, 0, tariff_list.size() > 2);
                mAdapterTariff.setOnItemClickListener(this);
                int tariffDefaultPosition = 0;
                for (int i = 0; i < tariff_list.size(); i++) {
                    if (tariff_list.get(i).getIsDefault().equals("1")) {
                        tariffDefaultPosition = i;
                        break;
                    }
                }
                layoutManagerTariff.scrollToPosition(mAdapterTariff.MIDDLE + tariffDefaultPosition);
                recyclerViewTariff.setLayoutManager(layoutManagerTariff);
                recyclerViewTariff.setAdapter(mAdapterTariff);
            }
            break;
            case SO_WHISHES: {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.addSlidePanelItem : SO_WHISHES : rootView :  " + rootView
                            + " : size : " + tariff_list.size());
                ImageView imageViewBlurService = (ImageView) rootView.findViewById(R.id.imageViewBlurService);
                imageViewBlurService.setBackgroundDrawable(blurGradient);
                recyclerViewService = (RecyclerView) rootView.findViewById(R.id.recyclerViewService);
                layoutManagerTariffService = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                mAdapterService = new TariffServiceRVAdapter(mContext, tariff_services, tariff_services.size() > 2);
                mAdapterService.setOnItemClickListener(this);
                layoutManagerTariffService.scrollToPosition(mAdapterService.MIDDLE);
                recyclerViewService.setLayoutManager(layoutManagerTariffService);
                recyclerViewService.setAdapter(mAdapterService);
            }
            break;
            case SO_PAYMENT: {
                ImageView imageViewBlurPayment = (ImageView) rootView.findViewById(R.id.imageViewBlurPayment);
                imageViewBlurPayment.setBackgroundDrawable(blurGradient);

                // check if client info is NULL
                if (mClientInfo != null) {
                    // BONUS
                    if (mClientInfo.getCanUseBonus() > 0) {
                        String payment_bonus = mContext.getResources().getString(R.string.payment_bonus);
                        PaymentInfo bonusInfo = new PaymentInfo(payment_bonus, PaymentTypes.BONUS);
                        boolean isBonusAdded = false;
                        for (PaymentInfo item : paymentList) {
                            if (item.getPayment_name().equals(payment_bonus)) {
                                isBonusAdded = true;
                            }
                        }
                        if (!isBonusAdded) {
                            if (paymentList.size() > 1) {
                                paymentList.add(1, bonusInfo);
                            } else {
                                paymentList.add(bonusInfo);
                            }
                        }
                    }
                    // CORPORATION
                    if (mClientInfo.getCorporation().size() > 0) {
                        for (Corporation item : mClientInfo.getCorporation()) {
                            boolean isCorporationAdded = false;
                            for (PaymentInfo paymentInfoItem : paymentList) {
                                if (paymentInfoItem.getPayment_name().equals(item.getName())) {
                                    isCorporationAdded = true;
                                }
                            }
                            if (!isCorporationAdded) {
                                PaymentInfo corpInfo = new PaymentInfo(item.getName(), PaymentTypes.CORPORATION);
                                corpInfo.setCorporationId(item.getId());
                                corpInfo.setCorporationStatus(item.getCorporationStatus());
                                if (paymentList.size() > 2) {
                                    paymentList.add(2, corpInfo);
                                } else {
                                    paymentList.add(corpInfo);
                                }
                            }
                        }
                    }
                } else {
                    // CASH
                    String[] payment_items = mContext.getResources().getStringArray(R.array.so_payment_items);
                    for (int i = 0; i < payment_items.length; i++) {
                        PaymentInfo itemInfo;
                        if (i == 0) {
                            itemInfo = new PaymentInfo(payment_items[i], PaymentTypes.CASH);
                        } else if (payment_items.length > 1 && i == payment_items.length - 1) {
                            itemInfo = new PaymentInfo(payment_items[i], PaymentTypes.BONUS);
                            itemInfo.setPromoCode(true);
                        } else {
                            itemInfo = new PaymentInfo(payment_items[i], PaymentTypes.CASH);
                        }

                        paymentList.add(itemInfo);

                    }
                }

                recyclerViewPayment = (RecyclerView) rootView.findViewById(R.id.recyclerViewPayment);
                layoutManagerPayment = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                mAdapterPayment = new PaymentRVAdapter(mContext, paymentList, 0, paymentList.size() > 2);
                mAdapterPayment.setOnItemClickListener(this);
                layoutManagerPayment.scrollToPosition(mAdapterPayment.MIDDLE);
                recyclerViewPayment.setLayoutManager(layoutManagerPayment);
                recyclerViewPayment.setAdapter(mAdapterPayment);
            }
            break;
            case SO_CHILD_SEAT: {
                ImageView imageViewBlurChild = (ImageView) rootView.findViewById(R.id.imageViewBlurChild);
                imageViewBlurChild.setBackgroundDrawable(blurGradient);
                recyclerViewChild = (RecyclerView) rootView.findViewById(R.id.recyclerViewChild);
                layoutManagerChildSeat = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

                mAdapterChild = new ChildSeatRVAdapter(mContext, child_seat_list);
                mAdapterChild.setOnItemClickListener(this);
                layoutManagerChildSeat.scrollToPosition(mAdapterChild.MIDDLE);
                recyclerViewChild.setLayoutManager(layoutManagerChildSeat);
                recyclerViewChild.setAdapter(mAdapterChild);
            }
            break;
            default:
                break;
        }

    } // end method addPanelListItems


    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.onPanelSlide : slideOffset : " + slideOffset);

    }

    @Override
    public void onPanelCollapsed(View panel) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setPanelSlideListener : onPanelCollapsed : layoutMapScreen : " + layoutMapScreen +
                    " : isFromAnimateTopLayout : " + isFromAnimateTopLayout);

        layoutMapScreen.setVisibility(View.GONE);

        if (isFromAnimateTopLayout) {
            isFromAnimateTopLayout = false;
            orderHelper.setPanelState(isSlidePanelOpened);
            // order register state
            orderHelper.SetOrderState(rootView, OrderState.UNKNOWN, false, "", "", "", "", "", null, true);
        }

        if (activeOrder != null) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MapViewFragment.setPanelSlideListener : REQUEST_GET_ORDER_STATUS_LIST : 2");
            ((MainActivity) mContext).updateUIByOrderStatusData(activeOrder);
            activeOrder = null;
        }
    }

    @Override
    public void onPanelExpanded(View panel) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setPanelSlideListener : onPanelExpanded : layoutMapScreen : " + layoutMapScreen);

        layoutMapScreen.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public GooglePlace getAddress() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment getAddress ");
        if (mapFragment != null) {
            GooglePlace googlePlace = ((MapGoogleFragment) mapFragment).getCurrentGoogleAddress();
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment getAddress googlePlace : " + googlePlace);
            return googlePlace;
        }
        return null;
    }

    @Override
    public void setAddress(GooglePlace address) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setAddress : address : " + address);
        if (mapFragment != null) {
            final LatLng latLng = address.getLatLng();
            ((MapGoogleFragment) mapFragment).showOnMap(latLng, true);
        }
    }

    /**
     * Method for drawing drivers on map
     *
     * @param driversList - drivers current list
     */
    public void drawDriversOnMap(List<Driver> driversList) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment drawDrivers driversList : " + driversList);

        if (mapFragment != null)
            ((MapGoogleFragment) mapFragment).drawDriversOnMap(driversList);

    } // end method drawDriversOnMap

    /**
     * Method for getting map fragment
     *
     * @return - map fragment
     */
    public MapDriverFragment getMapFragment() {
        return mapFragment;

    } // end method getMapFragment


    /**
     * Method for getting order state helper
     *
     * @return orderHelper
     */
    public OrderStateHelper getOrderStateHelper() {
        return orderHelper;
    }

    /**
     * Method for getting fragment root view
     *
     * @return rootView
     */
    public View getRootView() {
        return rootView;
    }

    /**
     * Method for setting current driver information
     *
     * @param driverInfo - driver information
     * @param show - show driver information
     */
    public void setDriverUI(DriverInfo driverInfo, boolean show) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setDriverUI : " + driverInfo + " : show : " + show);
        if (mapFragment != null)
            mapFragment.setDriverUI(driverInfo, show);

    } // end method setDriverUI

    /**
     * Method for drawing directions
     * @param directions - list of directions
     */
    public void drawRoadDirections(List<String> directions, boolean isAddMarkers, boolean zoom, boolean drawRoad) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.drawRoadDirections : directions : " + directions);

        if (mapFragment != null) {
            mapFragment.drawRoadDirections(directions, isAddMarkers, zoom, drawRoad);
        }

    } // end method drawRoadDirections

    /**
     * Method for start top layout animation
     */
    public void animateTopLayout() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.animateTopLayout ");

        // map buttons
        mapFragment.hideControls(rootView);
        // set items
        orderHelper.setTopBarItems(rootView, OrderState.UNKNOWN, null, "");

    } // end method animateTopLayout

    public void setSlidePanelData() {
        TariffDBManager tariffDBManager = new TariffDBManager(mContext);
        int languageID = new LanguageDBManager(mContext).getLanguageIdByLocale("ru");

        String last_city_id = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setSlidePanelData : last_city_id : " + last_city_id);

        if (last_city_id.equals(""))
            tariff_list = tariffDBManager.getAllTariffs(languageID, 2);
        else
            tariff_list = tariffDBManager.getAllTariffs(languageID, Integer.parseInt(last_city_id));

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setListData : languageID : " + languageID);
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setListData : tariffs : " + tariff_list);
        if (tariff_list.size() > 0)
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setListData : tariff_services : " + tariff_list.get(0).getServices());

        child_seat_list = ChildSeatRVAdapter.getChildSeatList(mContext);

        if (tariff_list != null && tariff_list.size() > 0)
            tariff_services = (ArrayList<TariffService>) tariff_list.get(0).getServices();

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, String.valueOf(":: SlidePanelAdapter.setListData : tariff_list size : " + tariff_list != null ? tariff_list.size() : "0"));
    }

    /**
     * Method for setting tariff data to adapter
     *
     * @param tariffList - list of tariffs
     * @param fromSplash - call from splash activity
     */
    public synchronized void setTariffsData(List<Tariff> tariffList, boolean fromSplash) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: SlidePanelAdapter.setTariffsData : tariffList : " + tariffList);
        if (tariffList != null && tariffList.size() > 0) {
            tariff_list.clear();
            tariff_list.addAll(tariffList);
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: SlidePanelAdapter.setListData : tariffs : " + tariff_list);

            if (tariff_list != null && tariff_list.size() > 0) {
                tariff_services.clear();
                tariff_services.addAll((ArrayList<TariffService>) tariff_list.get(0).getServices());
            }
        }
        if (tariff_list != null && tariff_list.size() > 0) {
            int defaultPosition = 0;
            if (tariffList != null) {
                for (int i = 0; i < tariffList.size(); i++) {
                    if (this.tariff_list.get(i).getIsDefault().equals("1")) {
                        defaultPosition = i;
                    }
                }
            }

            if (tariff_list != null) {
                mAdapterTariff = new TariffRVAdapter(mContext, tariff_list, 0, tariff_list.size() > 2);
                mAdapterTariff.setOnItemClickListener(this);
                if (recyclerViewTariff == null) {
                    recyclerViewTariff = (RecyclerView) rootView.findViewById(R.id.recyclerViewTariff);
                    layoutManagerTariff = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewTariff.setLayoutManager(layoutManagerTariff);
                }
                recyclerViewTariff.setAdapter(mAdapterTariff);
                layoutManagerTariff.scrollToPosition(mAdapterTariff.MIDDLE + defaultPosition);
            }

            if (tariff_services != null) {
                mAdapterService = new TariffServiceRVAdapter(mContext, tariff_services, tariff_services.size() > 2);
                mAdapterService.setOnItemClickListener(this);
                if (recyclerViewService == null) {
                    layoutManagerTariffService = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewService = (RecyclerView) rootView.findViewById(R.id.recyclerViewService);
                    recyclerViewService.setLayoutManager(layoutManagerTariffService);
                }
                recyclerViewService.setAdapter(mAdapterService);
                layoutManagerTariffService.scrollToPosition(mAdapterService.MIDDLE);
            }
        }

    } // end method setTariffsData

    public void setCurrent_address(GooglePlace current_address) {
        this.current_address = current_address;
    }

    /**
     * Method for setting child seat list
     *
     * @param show - show/hide state
     */
    public void setChildSeatState(boolean show) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setChild_getChildSeatList : show : " + show);

        if (show) {
            layoutItemChild.setVisibility(View.VISIBLE);
        } else {
            layoutItemChild.setVisibility(View.GONE);
        }

    } // end method setChildSeatState

    private GooglePlace current_address;

    public GooglePlace getCurrent_address() {
        return current_address;
    }

    /**
     * Method for removing favorite addresses from list
     *
     * @param mList - favorite address list
     */
    public void removeFavoriteAddresses(ArrayList<GooglePlace> mList) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.removeFavoriteAddresses : mList : " + mList);
        if (mList != null && mList.size() > 0) {
            ArrayList<GooglePlace> address_list = new ArrayList<>();
            address_list.addAll(mList);
            mList.clear();
            for (GooglePlace place : address_list) {
                if (!place.isFavorite()) {

                    mList.add(place);
                }
            }
        }

    } // end method removeFavoriteAddresses

    /**
     * Method for adding favorite addresses to address adapter
     *
     * @param getFavoriteDatas - favorite address list
     */
    public void addFavoriteAddresses(List<GetFavoriteData> getFavoriteDatas) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.addFavoriteAddresses : getFavoriteDatas : " + getFavoriteDatas);

        removeFavoriteAddresses(fromAddressList);
        removeFavoriteAddresses(toAddressList);

        int posToAddress = mAdapterTo.getCurrentPosition();
        if (getFavoriteDatas != null && getFavoriteDatas.size() > 0) {

            for (GetFavoriteData item : getFavoriteDatas) {
                if (item != null) {
                    GooglePlace place = new GooglePlace();
                    place.setAddress(item.getAddress());
                    place.setId(item.getId());
                    place.setName(item.getName());
                    LatLng mLatLng = new LatLng(Double.valueOf(item.getLatitude()), Double.valueOf(item.getLongitude()));
                    place.setLatLng(mLatLng);
                    place.setIsFavorite(true);
                    place.setEntrance(item.getEntrance());
                    place.setComment(item.getComment());

                    // FROM
                    if (fromAddressList.size() > 1)
                        fromAddressList.add(fromAddressList.size() - 1 ,place);

                    ArrayList<GooglePlace> defaultToAddressList = (ArrayList<GooglePlace>) AddressRVAdapter.getListByScrollType(mContext, ScrollType.SO_WHERE);
                    // TO
                    if (toAddressList.size() > 1) {
                        if (toAddressList.get(toAddressList.size() - 1).getName().equals(defaultToAddressList.get(defaultToAddressList.size() - 1).getName())){
                            toAddressList.add(toAddressList.size() - 1, place);
                        } else {
                            toAddressList.add(place);
                        }
                    }

                }
            }
        } else {
            posToAddress = 0;
        }
        // FROM ADAPTER
        int fromAddressPosition = mAdapterFrom.getCurrentPosition();
        if (fromAddressPosition < 0)
            fromAddressPosition = 0;
        mAdapterFrom = new AddressRVAdapter(mContext, fromAddressList, ScrollType.SO_FROM, fromAddressPosition, fromAddressList.size() > 2);
        mAdapterFrom.setOnItemClickListener(this);
        scrollViewFrom.setLayoutManager(layoutManagerFrom);
        scrollViewFrom.setAdapter(mAdapterFrom);
        layoutManagerFrom.scrollToPosition(mAdapterFrom.MIDDLE + fromAddressPosition);

        // TO ADAPTER
        mAdapterTo = new AddressRVAdapter(mContext, toAddressList, ScrollType.SO_WHERE, posToAddress, toAddressList.size() > 2);
        mAdapterTo.setOnItemClickListener(this);
        scrollViewTo.setLayoutManager(layoutManagerTo);
        scrollViewTo.setAdapter(mAdapterTo);
        layoutManagerTo.scrollToPosition(mAdapterTo.MIDDLE + posToAddress);

        if (!CMApplication.getTrackingOrderId().isEmpty()) {
            setSlidePanelItemClickEnabled(false);
        }

    } // end method addFavoriteAddresses

    /**
     * Method for refreshing address list (FROM/TO)
     *
     * @param context       - current context
     * @param scrollType    - scroll type
     * @param address       - address
     */
    public void refreshAddressByType(Context context, ScrollType scrollType, GooglePlace address, boolean isShowMap) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.refreshAddressByType : scrollType : "
                    + scrollType + " : address : " + address.getName());
        switch (scrollType) {
            case SO_FROM:
                collAddressGeoObject = null;

//                if (address.isFavorite() && mCurrentFavoriteAddress != null) {
//                    setCurrent_address(mCurrentFavoriteAddress);
//                    fromAddressList.get(0).setName(mCurrentFavoriteAddress.getAddress());
//                    mAdapterFrom.setCurrentPosition(fromAddressList.indexOf(mCurrentFavoriteAddress));
//                }
                Logger.i(TAG, ":: MapViewFragment.refreshAddressByType : isFavorite : " + address.isFavorite() + " : mCurrentFavoriteAddress : " + mCurrentFavoriteAddress);
                if (address.isFavorite()) {
                    setCurrent_address(address);
                    if (mCurrentFavoriteAddress != null) {
                        fromAddressList.get(0).setName(mCurrentFavoriteAddress.getAddress());
                    } else {
                        fromAddressList.get(0).setName(address.getAddress());
                    }
                    for (int i = 0; i < fromAddressList.size(); i++) {
                        if (fromAddressList.get(i).isFavorite() && fromAddressList.get(i).getId().equals(address.getId())) {
                            mAdapterFrom.setCurrentPosition(i);
                            break;
                        }
                    }
                    mFavoriteAddressClicked = false;
//                    mAdapterFrom.setCurrentPosition(fromAddressList.indexOf(address));
                }else {
                    setCurrent_address(address);
                    fromAddressList.set(0, address);
                    mAdapterFrom.setCurrentPosition(0);
                }

                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.refreshAddressByType : fromAddressList : element(0) : " + fromAddressList.get(0).getName());
                if (isShowMap) {
                    setAddress(address);
                }

                // FROM ADAPTER
                int curPos = mAdapterFrom.getCurrentPosition();
                mAdapterFrom = new AddressRVAdapter(mContext, fromAddressList, ScrollType.SO_FROM, curPos, fromAddressList.size() > 2);
                mAdapterFrom.setOnItemClickListener(this);
                scrollViewFrom.setLayoutManager(layoutManagerFrom);
                scrollViewFrom.setAdapter(mAdapterFrom);
                layoutManagerFrom.scrollToPosition(mAdapterFrom.MIDDLE + curPos);

                // Getting google distance matrix from A to B for getting order price
                ((MainActivity) mContext).getGoogleDistanceMatrixFromAToB();
                break;
            case SO_WHERE:
                deliveryAddressGeoObject = null;

                int posToAddress = 1;
                if (address.isFavorite()) {
                    for (GooglePlace place : toAddressList) {
                        if (place.getName().equals(address.getName())) {
                            address = place;
                            posToAddress = toAddressList.indexOf(place);
                        }
                    }
                } else {
                    String[] defaultToStrings = context.getResources().getStringArray(R.array.so_where_items);

                    if (toAddressList.size() > 1
                            && !toAddressList.get(1).isFavorite()
                            && !toAddressList.get(1).getName().equals(defaultToStrings[defaultToStrings.length - 1])) {
                        if (mAdapterTo.isInfinite()) {
                            toAddressList.set(1, address);
                        } else {
                            posToAddress = 0;
                            toAddressList.set(0, address);
                        }
                    } else {
                        if (mAdapterTo.isInfinite()) {
                            toAddressList.add(1, address);
                            toAddressList.remove(toAddressList.size() - 1);
                        } else {
                            posToAddress = 0;
                            GooglePlace firstPlace = toAddressList.get(0);
                            toAddressList.set(0, address);
                            toAddressList.set(1, firstPlace);
                        }
                    }

                }

                mAdapterTo = new AddressRVAdapter(mContext, toAddressList, ScrollType.SO_WHERE, posToAddress, toAddressList.size() > 2);
                mAdapterTo.setOnItemClickListener(this);
                layoutManagerTo.scrollToPosition(mAdapterTo.MIDDLE + posToAddress);
                scrollViewTo.setLayoutManager(layoutManagerTo);
                scrollViewTo.setAdapter(mAdapterTo);

                // Getting google distance matrix from A to B for getting order price
                ((MainActivity) mContext).getGoogleDistanceMatrixFromAToB();
                break;
            default:
                break;
        }

    } // end method refreshAddressByType

    /**
     * Method for refreshing address list (WHEN)
     *
     * @param context       - current context
     * @param scrollType    - scroll type
     * @param dateString    - date for refresh
     */
    public void refreshTimeByType(Context context, ScrollType scrollType, Date date, String dateString) {

        if (date != null) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.refreshTimeByType : stringDate : " + dateString);
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.refreshTimeByType : dateList.size : " + dateList.size());
            selectedDate = date;
            DateData dateData = new DateData();
            dateData.setDate(date);
            dateData.setTitle(dateString);

            int posDate = mAdapterDate.getCurrentPosition();
            if (mAdapterDate.isInfinite()) {
                dateList.set(1, dateData);
                posDate = 1;
            } else {

                if (dateList.get(0).isHurry()) {
                    DateData firstDate = dateList.get(0);
                    dateList.set(0, dateData);
                    dateList.set(1, firstDate);
                } else {
                    dateList.set(0, dateData);
                }
            }
            mAdapterDate = new DateRVAdapter(mContext, dateList, posDate, dateList.size() > 2);
            mAdapterDate.setOnItemClickListener(this);
            layoutManagerDate.scrollToPosition(mAdapterDate.MIDDLE + posDate);
            recyclerViewDate.setLayoutManager(layoutManagerDate);
            recyclerViewDate.setAdapter(mAdapterDate);
        }

    } // end method refreshTimeByType

    @Override
    public void updateAdapterByScrollType(ScrollType scrollType, int position) {

        switch (scrollType) {
            case SO_CAR:
                tariff_services = (ArrayList<TariffService>) tariff_list.get(position).getServices();
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.updateAdapterByScrollType : SO_CAR : tariff_services :  " + tariff_services
                            + " : size : " + tariff_list.size());
                if (tariff_services != null && tariff_services.size() > 0) {
                    mAdapterService.setSelectedTariffServices(new ArrayList<TariffService>());
                    setChildSeatState(false);
                    mAdapterService.setIsChildSeatAdded(false);
                    if (tariff_services.size() > 2) {
                        mAdapterService.setIsInfinite(true);
                    } else {
                        mAdapterService.setIsInfinite(false);
                    }
                    mAdapterService.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * Method for getting selected date
     *
     * @return - selected date
     */
    public DateData getSelectedDate() {
        int datePosition = mAdapterDate.getCurrentPosition();
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.getSelectedDate : datePosition : " + datePosition);

        DateData dateData = dateList.get(datePosition);
        if (dateData.getDate() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));
            calendar.add(Calendar.MINUTE, 15);
            dateData.setDate(calendar.getTime());
        }

        return dateList.get(datePosition);

    } // and method getSelectedDate

    /**
     * Method for getting selected from address from address adapter
     *
     * @return - return from address
     */
    public GooglePlace getFromAddress() {
        int position = mAdapterFrom.getCurrentPosition();
        if (position >= 0 && position <= fromAddressList.size() - 1) {
            return fromAddressList.get(position);
        }
        return null;

    } // end method getFromAddress

    /**
     * Method for getting selected to address from address adapter
     *
     * @return - return to address
     */
    public GooglePlace getToAddress() {
        int position = mAdapterTo.getCurrentPosition();
        if (position >= 0 && (position < toAddressList.size())) {
            return toAddressList.get(position);
        }
        return null;

    } // end method getToAddress

    /**
     * Method for getting selected tariff from tariff adapter
     *
     * @return - return selected tariff
     */
    public Tariff getSelectedTariff() {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.getSelectedTariff : tariff_list : " + tariff_list);
        if (tariff_list != null && tariff_list.size() > 0
                && mAdapterTariff != null
                && tariff_list.size() > mAdapterTariff.getCurrentPosition()) {
            return tariff_list.get(mAdapterTariff.getCurrentPosition());
        }
        return null;

    } // end method getSelectedTariff

    /**
     * Method for getting selected payment from payment adapter
     *
     * @return - return selected payment
     */
    public PaymentInfo getSelectedPayment() {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.getSelectedPayment : paymentList : " + paymentList);
        if (paymentList != null
                && mAdapterPayment != null
                && paymentList.size() > 0) {
            return paymentList.get(mAdapterPayment.getCurrentPosition());
        }
        return null;

    } // end method getSelectedTariff

    /**
     * Method for getting selected tariff service from service adapter
     *
     * @return - return selected tariff service list
     */
    public ArrayList<TariffService> getSelectedTariffServices() {
        if (mAdapterService != null)
            return (ArrayList<TariffService>) mAdapterService.getSelectedTariffServices();
        return null;

    } // end method getSelectedTariffServices

    /**
     * Method for enabling/disabling horizontal
     * scroll view
     *
     * @param isEnable - is Slide Panel items clickable
     */
    public void setSlidePanelItemClickEnabled(boolean isEnable) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setSlidePanelItemClickEnabled : isEnable : " + isEnable);

        if (mAdapterTariff != null)
            mAdapterTariff.setItemClickState(isEnable);
        if (mAdapterFrom != null)
            mAdapterFrom.setItemClickState(isEnable);
        if (mAdapterTo != null)
            mAdapterTo.setItemClickState(isEnable);
        if (mAdapterDate != null)
            mAdapterDate.setItemClickState(isEnable);
        if (mAdapterService != null)
            mAdapterService.setItemClickState(isEnable);
        if (mAdapterChild != null)
            mAdapterChild.setItemClickState(isEnable);
        if (mAdapterPayment != null)
            mAdapterPayment.setItemClickState(isEnable);
        if (layoutComment != null) {
            layoutComment.setEnabled(isEnable);
        }

    } // end method setSlidePanelItemClickEnabled

    public boolean isFavoriteAddressClicked() {
        return mFavoriteAddressClicked;
    }

    @Override
    public void dateOnItemClick(View itemView, int infinitePosition, DateData item, TextView currentTextView) {
        int realPosition = infinitePosition % dateList.size();
        Log.i(TAG, ":: MapViewFragment.dateOnItemClick : realPosition : " + realPosition + " : infinitePosition : " + infinitePosition);
        Log.i(TAG, ":: MapViewFragment.dateOnItemClick : mAdapterDate.getCurrentPosition() : " + mAdapterDate.getCurrentPosition());

        if (mAdapterDate.isClickEnable()) {
            hasAction = true;
            List<String> dates = Arrays.asList(mContext.getResources().getStringArray(R.array.so_when_items));

            boolean isThereSetTime = dates.get(1).equals(item.getTitle());

            if (!item.isHurry()) {
                if (isThereSetTime)
                     mAdapterDate.openTimeSelectionDialog();
                else if (realPosition == mAdapterDate.getCurrentPosition())
                    mAdapterDate.openTimeSelectionDialog();
            }

            if (realPosition != mAdapterDate.getCurrentPosition()) {
                CustomTextView textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);

                if (!isThereSetTime) {
                    if (mAdapterDate.isInfinite()) {
                        textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                        textViewItem.setPadding(0, 0
                                , 0, 0);
                        if (currentTextView != null) {
                            currentTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                            currentTextView.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);
                        }
                        mAdapterDate.setCurrentTextView(textViewItem);
                        mAdapterDate.setCurrentPosition(realPosition);
                        layoutManagerDate.scrollToPosition(infinitePosition - dateList.size());
                    } else {
                        DateData firstDate = dateList.get(0);
                        DateData secondDate = dateList.get(1);
                        dateList.set(0, secondDate);
                        dateList.set(1, firstDate);
                        mAdapterDate.setCurrentPosition(0);
                        layoutManagerDate.scrollToPosition(0);
                    }

                    mAdapterDate.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void addressOnItemClick(View itemView, int position, GooglePlace item, TextView currentTextView, ScrollType scrollType) {
        Log.i(TAG, ":: MapViewFragment.addressOnItemClick : scrollType : " + scrollType);
        int realPosition;
        switch (scrollType) {
            case SO_FROM:
                collAddressGeoObject = null;
                if (mAdapterFrom.isInfinite()) {
                    realPosition = position % fromAddressList.size();
                } else {
                    realPosition = position;
                }

                Log.i(TAG, ":: MapViewFragment.addressOnItemClick : realPosition : " + realPosition + " : position : " + position);

                if (mAdapterFrom.isClickEnable()) {
                    hasAction = true;
                    boolean isFirstItemSelected = false;
                    if (mAdapterFrom.getCurrentPosition() == 0) {
                        isFirstItemSelected = true;
                    }

                    if (realPosition != mAdapterFrom.getCurrentPosition() && realPosition != fromAddressList.size() - 1) {
                        CustomTextView textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);
                        textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                        textViewItem.setPadding(0, 0, 0, 0);
                        if (currentTextView != null) {
                            currentTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                            currentTextView.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);
                        }
                        mAdapterFrom.setCurrentTextView(textViewItem);
                        mAdapterFrom.setCurrentPosition(realPosition);
                        if (mAdapterFrom.isInfinite()) {
                            layoutManagerFrom.scrollToPosition(position - fromAddressList.size());
                        } else {
                            layoutManagerFrom.scrollToPosition(position);
                        }
                        mAdapterFrom.notifyDataSetChanged();
                    }

                    mCurrentFavoriteAddress = null;
                    mFavoriteAddressClicked = false;
                    Log.i(TAG, ":: MapViewFragment.addressOnItemClick : isFavorite : " + item.isFavorite());
                    if (item.isFavorite()) {
                        mFavoriteAddressClicked = true;
                        mCurrentFavoriteAddress = item;
                        mapFragment.moveMapPosition(item.getLatLng().latitude, item.getLatLng().longitude);
                    } else if (realPosition == fromAddressList.size() - 1 && mAdapterFrom.getCurrentPosition() >= 0) {
                        GooglePlace selectedGooglePlace = fromAddressList.get(mAdapterFrom.getCurrentPosition());
                        if (selectedGooglePlace != null && selectedGooglePlace.getLatLng() != null) {
                            GetFavoriteData favoriteData = new GetFavoriteData();
                            favoriteData.setId("");
                            favoriteData.setIdLocality("2");
                            favoriteData.setLatitude(String.valueOf(selectedGooglePlace.getLatLng().latitude));
                            favoriteData.setLongitude(String.valueOf(selectedGooglePlace.getLatLng().longitude));
                            if (!selectedGooglePlace.isFavorite())
                                favoriteData.setAddress(selectedGooglePlace.getName());
                            else
                                favoriteData.setAddress(selectedGooglePlace.getAddress());

                            Intent intent = new Intent(mContext, AddFavoriteAddressActivity.class);

                            if (selectedGooglePlace.isFavorite() || selectedGooglePlace.isLastAddress()) {
                                favoriteData.setId(selectedGooglePlace.getId());
                            } else {
                                intent.putExtra(CMAppGlobals.EXTRA_NO_PLACE_ID, true);
                            }
                            intent.putExtra(CMAppGlobals.EXTRA_FAVORITE_ADDRESS, favoriteData);
                            mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_ADD_FAVORITE_ADDRESS);

                        }
                    } else {
                        if (isFirstItemSelected) {
                            // Open Search Page
//                            Intent intent = new Intent(mContext, CommonActivity.class);
                            Intent intent = new Intent(mContext, SearchActivity.class);
                            intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.SEARCH);
                            intent.putExtra(CMAppGlobals.SCROLL_TYPE, scrollType.ordinal());
                            intent.putExtra(CMAppGlobals.EXTRA_SEARCH_ADDRESS, item);
                            mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_SEARCH_DATA);
                        }
                    }
                }

                    break;
                case SO_WHERE:
                    deliveryAddressGeoObject = null;
                    if (mAdapterTo.isInfinite()) {
                        realPosition = position % toAddressList.size();
                    } else {
                        realPosition = position;
                    }
                    Log.i(TAG, ":: MapViewFragment.addressOnItemClick : realPosition : " + realPosition + " : position : " + position);
                    if (mAdapterTo.isClickEnable()) {
                        hasAction = true;
                        String[] defaultToAddressStrings = getResources().getStringArray(R.array.so_where_items);

                        boolean isByAddress = false;
                        if (toAddressList.get(toAddressList.size() - 1).getName().equals(defaultToAddressStrings[defaultToAddressStrings.length - 1])) {
                            isByAddress = true;
                        }

                        if (!item.isFavorite()) {
                            if (toAddressList.size() > 1
                                    && !item.getName().equals(defaultToAddressStrings[0])) {
                                if (isByAddress) {
                                    // Open Search Page
//                                    Intent intent = new Intent(mContext, CommonActivity.class);
                                    Intent intent = new Intent(mContext, SearchActivity.class);
                                    intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.SEARCH);
                                    intent.putExtra(CMAppGlobals.SCROLL_TYPE, scrollType.ordinal());
                                    mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_SEARCH_DATA);

                                } else if (toAddressList.get(mAdapterTo.getCurrentPosition()).getName().equals(item.getName())) {
                                    // Open Search Page
//                                    Intent intent = new Intent(mContext, CommonActivity.class);
                                    Intent intent = new Intent(mContext, SearchActivity.class);
                                    intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.SEARCH);
                                    intent.putExtra(CMAppGlobals.SCROLL_TYPE, scrollType.ordinal());
                                    intent.putExtra(CMAppGlobals.EXTRA_SEARCH_ADDRESS, item);
                                    mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_SEARCH_DATA);
                                }
                            }
                        }

                        if (realPosition != mAdapterTo.getCurrentPosition()
                                && !toAddressList.get(realPosition).getName().equals(defaultToAddressStrings[defaultToAddressStrings.length - 1])) {

                            CustomTextView textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);
                            textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                            if (currentTextView != null) {
                                currentTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                            }
                            if (mAdapterTo.isInfinite()) {
                                mAdapterTo.setCurrentTextView(textViewItem);
                                mAdapterTo.setCurrentPosition(realPosition);
                                layoutManagerTo.scrollToPosition(position - toAddressList.size());
                            } else {
                                GooglePlace firstPlace = toAddressList.get(0);
                                GooglePlace secondPlace = toAddressList.get(1);
                                toAddressList.set(0, secondPlace);
                                toAddressList.set(1, firstPlace);
                                mAdapterTo.setCurrentPosition(0);
                                layoutManagerTo.scrollToPosition(0);
                            }
                            mAdapterTo.notifyDataSetChanged();
                        }

                        // Getting google distance matrix from A to B for getting order price
                        ((MainActivity) mContext).getGoogleDistanceMatrixFromAToB();

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CMAppGlobals.DEBUG)
            Log.i(TAG, ":: MapViewFragment.onActivityResult : requestCode : " + requestCode
                    + " : resultCode : " + resultCode
                    + " : data : " + data);

        if (requestCode == CMAppGlobals.REQUEST_CARD_ADD) {
            getCreditCards();
        }
    }

    @Override
    public void paymentOnItemClick(View itemView, int position, PaymentInfo item, TextView currentTextView) {
        int realPosition;
        if (mAdapterPayment.isInfinite()) {
            realPosition = position % paymentList.size();
        } else {
            realPosition = position;
        }
        if (CMAppGlobals.DEBUG)
            Log.i(TAG, ":: MapViewFragment.paymentOnItemClick : realPosition : " + realPosition + " : position : " + position);

        if (mAdapterPayment.isClickEnable()) {
            hasAction = true;
            if (realPosition != mAdapterPayment.getCurrentPosition()) {
                if (item.isNoCard()) {
                    Intent intent = new Intent(mContext, AddCreditCardsActivity.class);
                    startActivityForResult(intent, CMAppGlobals.REQUEST_CARD_ADD);

                } else if(item.isPromoCode()) {
                    // REWARDS
                    Intent intent = new Intent(mContext, RewardsActivity.class);
                    mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO);
                } else  {
                    CustomTextView textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);
                    textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    textViewItem.setPadding(0, 0, 0, 0);
                    if (currentTextView != null) {
                        currentTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                        currentTextView.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);
                    }
                    if (mAdapterPayment.isInfinite()) {
                        mAdapterPayment.setCurrentTextView(textViewItem);
                        mAdapterPayment.setCurrentPosition(realPosition);
                        layoutManagerPayment.scrollToPosition(position - paymentList.size());
                    } else {
                        PaymentInfo firstInfo = paymentList.get(0);
                        PaymentInfo secondInfo = paymentList.get(1);
                        paymentList.set(0, secondInfo);
                        paymentList.set(1, firstInfo);
                        mAdapterPayment.setCurrentPosition(0);
                        layoutManagerPayment.scrollToPosition(0);
                    }

                    mAdapterPayment.notifyDataSetChanged();
                }

            } else if (item.getPaymentType() == PaymentTypes.CREDIT_CARD) {
                Intent intent = new Intent(mContext, CreditCardsActivity.class);
                startActivityForResult(intent, CMAppGlobals.REQUEST_CARD_ADD);
            }
        }
    }

    @Override
    public void childSeatOnItemClick(View itemView, int infinitePosition, ChildSeat item) {

        int realPosition = infinitePosition % child_seat_list.size();
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.childSeatOnItemClick : realPosition : " + realPosition + " : infinitePosition : " + infinitePosition);

        if (mAdapterChild.isClickEnable()) {
            hasAction = true;
            CustomTextView textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);
            if (!mAdapterChild.getSelectedChildSeats().contains(item)) {
                if (mAdapterChild.getSelectedChildSeats().size() == 2) {
                    Toast.makeText(mContext, R.string.alert_max_child_seat, Toast.LENGTH_SHORT).show();
                    return;
                }
                textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                textViewItem.setPadding(0, 0, 0, 0);
                mAdapterChild.getSelectedChildSeats().add(item);
                layoutManagerChildSeat.scrollToPosition(infinitePosition - child_seat_list.size());
            } else {
                textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                textViewItem.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);
                mAdapterChild.getSelectedChildSeats().remove(item);
            }
            mAdapterChild.notifyDataSetChanged();
            // Getting google distance matrix from A to B for getting order price
            ((MainActivity) mContext).getGoogleDistanceMatrixFromAToB();
        }

    }

    @Override
    public void tariffServiceOnItemClick(View itemView, int position, TariffService item) {

        int realPosition;
        if (mAdapterService.isInfinite()) {
            realPosition = position % tariff_services.size();
        } else {
            realPosition = position;
        }
        Log.i(TAG, ":: MapViewFragment.tariffServiceOnItemClick : realPosition : " + realPosition + " : position : " + position);

        if (mAdapterService.isClickEnable()) {
            hasAction = true;
            CustomTextView textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);
            if (!mAdapterService.getSelectedTariffServices().contains(item)) {
                textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                textViewItem.setPadding(0, 0, 0, 0);
                mAdapterService.getSelectedTariffServices().add(item);
                if (mAdapterService.isInfinite()) {
                    layoutManagerTariffService.scrollToPosition(position - tariff_services.size());
                } else {
                    layoutManagerTariffService.scrollToPosition(position);
                }
            } else {
                textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                mAdapterService.getSelectedTariffServices().remove(item);
            }
            mAdapterService.notifyDataSetChanged();

            if (item != null && item.getField() != null && item.getField().equals("child_seats")) {
                if (mAdapterService.isChildSeatAdded()) {
                    mAdapterService.setIsChildSeatAdded(false);
                    ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).setChildSeatState(false);
                } else {
                    mAdapterService.setIsChildSeatAdded(true);
                    ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).setChildSeatState(true);
                }
            }
            // Getting google distance matrix from A to B for getting order price
            ((MainActivity) mContext).getGoogleDistanceMatrixFromAToB();
        }

    }

    @Override
    public void tariffOnItemClick(View itemView, int position, Tariff item, TextView currentTextView) {

        int realPosition = 0;
        if (mAdapterTariff.isInfinite()) {
            if(tariff_list != null && tariff_list.size() > 0)
                realPosition = position % tariff_list.size();
        } else {
            realPosition = position;
        }
        Log.i(TAG, ":: MapViewFragment.tariffServiceOnItemClick : realPosition : " + realPosition + " : position : " + position);

        if (mAdapterTariff.isClickEnable()) {
            hasAction = true;
            if (realPosition != mAdapterTariff.getCurrentPosition()) {
                CustomTextView textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);

                textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                textViewItem.setPadding(0, 0, 0, 0);
                if (currentTextView != null) {
                    currentTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                    currentTextView.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);
                }
                if (mAdapterTariff.isInfinite()) {
                    mAdapterTariff.setCurrentTextView(textViewItem);
                    mAdapterTariff.setCurrentPosition(realPosition);
                    layoutManagerTariff.scrollToPosition(position - tariff_list.size());
                } else if (tariff_list.size() > 1){
                    Tariff firstTariff = tariff_list.get(0);
                    Tariff secondTariff = tariff_list.get(1);
                    tariff_list.set(0, secondTariff);
                    tariff_list.set(1, firstTariff);
                    mAdapterTariff.setCurrentPosition(0);
                    layoutManagerTariff.scrollToPosition(0);
                }

                mAdapterTariff.notifyDataSetChanged();

                if (mAdapterTariff.isInfinite()) {
                    // Updating tariff service list
                    updateAdapterByScrollType(ScrollType.SO_CAR, realPosition);
                } else {
                    // Updating tariff service list
                    updateAdapterByScrollType(ScrollType.SO_CAR, 0);
                }
                // Getting google distance matrix from A to B for getting order price
                ((MainActivity) mContext).getGoogleDistanceMatrixFromAToB();

                // Request for nearest driver
                if (mapFragment instanceof MapGoogleFragment) {
                    ((MapGoogleFragment)mapFragment).getProgressBarMarker().setVisibility(View.VISIBLE);
                    ((MapGoogleFragment)mapFragment).getTextViewLocationMarker().setText("");
                }
                ((MainActivity)mContext).refreshDrivers();

            }
        }

    }

    /**
     * Method for seting order price
     *
     * @param getPriceRequest - Get Price Request
     */
    public void setOrderPrice(GetPriceRequest getPriceRequest) {

        getPriceRequest.setOs_version(Build.VERSION.RELEASE);
        getPriceRequest.setClientPhone_text(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), ""));

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setOrderPrice : phone_number : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), ""));

        DateData selectedDate = getSelectedDate();
        if (selectedDate != null) {
            if (selectedDate.getDate() != null) {
                // SELECTED DATE FORMAT
                SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd-MM-yyyy", new Locale(LocaleManager.getLocaleLang()));
                SimpleDateFormat dateFormatTime = new SimpleDateFormat("HHmm", new Locale(LocaleManager.getLocaleLang()));
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setOrderPrice : order_date : " + dateFormatDay.format(selectedDate.getDate()) + " :: " + dateFormatTime.format(selectedDate.getDate()));
                getPriceRequest.setCollDate(dateFormatDay.format(selectedDate.getDate()));
                getPriceRequest.setCollTime(dateFormatTime.format(selectedDate.getDate()));
            }
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setOrderPrice : isHurry : " + selectedDate.isHurry());
            if (selectedDate.isHurry()) {
                // set hurry true
                getPriceRequest.setHurry("Y");
            } else {
                // set hurry false
                getPriceRequest.setHurry("N");
            }
        }

        getPriceRequest.setIdLocality(2);
        // for push notifications
        getPriceRequest.setDevice_token(GCMUtils.getRegistrationId());

        GooglePlace callAddress = getFromAddress();
        if (callAddress != null && callAddress.getLatLng() != null) {
            if (callAddress.isAirport()) {
                getPriceRequest.setCollAddrTypeMenu(3);
                if (callAddress.getId() != null && !callAddress.getId().isEmpty())
                    getPriceRequest.setCollLandmark(Integer.parseInt(callAddress.getId()));
                if (callAddress.getAirportTerminalId() != null && !callAddress.getAirportTerminalId().isEmpty())
                    getPriceRequest.setCollTerminal(Integer.parseInt(callAddress.getAirportTerminalId()));

            } else if (callAddress.isRailwayStation()) {
                getPriceRequest.setCollAddrTypeMenu(2);
                if (callAddress.getId() != null && !callAddress.getId().isEmpty())
                    getPriceRequest.setCollLandmark(Integer.parseInt(callAddress.getId()));

            } else if (callAddress.isHasPlaceId()) {
                getPriceRequest.setCollAddrTypeMenu(1);
                // create geo object JSON
                collAddressGeoObject = getGeoDataJsonObject(callAddress);
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setOrderPrice : collAddressGeoObject : " + collAddressGeoObject);
                if (collAddressGeoObject != null)
                    getPriceRequest.setCollGeoObject(collAddressGeoObject);

            } else {
                getPriceRequest.setCollAddrTypeMenu(1);

                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setOrderPrice : collAddressGeoObject : " + collAddressGeoObject);
                if (collAddressGeoObject != null)
                    getPriceRequest.setCollGeoObject(collAddressGeoObject);
            }

            if(callAddress.isFavorite()) {
                getPriceRequest.setCollAddressText(callAddress.getAddress());
            } else if (callAddress.getName() != null && !callAddress.getName().equals("")) {
                getPriceRequest.setCollAddressText(callAddress.getName());
            }

            if (callAddress.getLatLng() != null) {
                getPriceRequest.setLatitude(callAddress.getLatLng().latitude + "");
                getPriceRequest.setLongitude(callAddress.getLatLng().longitude + "");
            }

            if (callAddress.getId() != null && !callAddress.getId().equals("")
                    && (callAddress.isFavorite() || callAddress.isLastAddress())) {
                getPriceRequest.setCollAddressId(callAddress.getId());
            }


            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setOrderPrice : CALLADDRESS : deliveryAddress.getComment() : " + callAddress.getComment());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setOrderPrice : CALLADDRESS : deliveryAddress.getEntrance() : " + callAddress.getEntrance());


            if (callAddress.getComment() != null && !callAddress.getComment().equals("")) {
                getPriceRequest.setCollComment(callAddress.getComment());
            }

            if (callAddress.getEntrance() != null && !callAddress.getEntrance().equals("")) {
                getPriceRequest.setCollPodjed(callAddress.getEntrance());
            }


            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setOrderPrice : CollGeoObject : " + new Gson().toJson(callAddress));
        } else {
            Toast.makeText(mContext, R.string.alert_select_address, Toast.LENGTH_SHORT).show();
            return;
        }

        GooglePlace deliveryAddress = getToAddress();
        if (deliveryAddress != null) {
            if (deliveryAddress.isAirport()) {
                getPriceRequest.setDeliveryAddrTypeMenu(3);
                if (deliveryAddress.getId() != null && !deliveryAddress.getId().isEmpty())
                    getPriceRequest.setDeliveryLandmark(Integer.parseInt(deliveryAddress.getId()));
                if (deliveryAddress.getAirportTerminalId() != null && !deliveryAddress.getAirportTerminalId().isEmpty())
                    getPriceRequest.setDeliveryTerminal(Integer.parseInt(deliveryAddress.getAirportTerminalId()));

            } else if (deliveryAddress.isRailwayStation()) {
                getPriceRequest.setDeliveryAddrTypeMenu(2);
                if (deliveryAddress.getId() != null && !deliveryAddress.getId().isEmpty())
                    getPriceRequest.setDeliveryLandmark(Integer.parseInt(deliveryAddress.getId()));

            } else if (deliveryAddress.isHasPlaceId()) {
                getPriceRequest.setDeliveryAddrTypeMenu(1);
                // create geo object JSON
                deliveryAddressGeoObject = getGeoDataJsonObject(deliveryAddress);
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setOrderPrice : deliveryAddressGeoObject : " + deliveryAddressGeoObject);
                if (deliveryAddressGeoObject != null)
                    getPriceRequest.setDeliveryGeoObject(deliveryAddressGeoObject);

            } else {
                getPriceRequest.setDeliveryAddrTypeMenu(1);

                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setOrderPrice : deliveryAddressGeoObject : " + deliveryAddressGeoObject);
                if (deliveryAddressGeoObject != null)
                    getPriceRequest.setDeliveryGeoObject(deliveryAddressGeoObject);

            }

            if(deliveryAddress.isFavorite()) {
                getPriceRequest.setDeliveryAddressText(deliveryAddress.getAddress());
            } else if (deliveryAddress.getName() != null && !deliveryAddress.getName().isEmpty())
                getPriceRequest.setDeliveryAddressText(deliveryAddress.getName());

            if (deliveryAddress.getLatLng() != null) {
                getPriceRequest.setDel_latitude(deliveryAddress.getLatLng().latitude + "");
                getPriceRequest.setDel_longitude(deliveryAddress.getLatLng().longitude + "");
            }

            if (deliveryAddress.getId() != null && !deliveryAddress.getId().equals("")
                    && (deliveryAddress.isFavorite() || deliveryAddress.isLastAddress())) {
                getPriceRequest.setDeliveryAddressId(deliveryAddress.getId());
            }

            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setOrderPrice : DELIVERY : deliveryAddress.getComment() : " + deliveryAddress.getComment());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setOrderPrice : DELIVERY : deliveryAddress.getEntrance() : " + deliveryAddress.getEntrance());

            if (deliveryAddress.getComment() != null && !deliveryAddress.getComment().equals("")) {
                getPriceRequest.setDeliveryComment(deliveryAddress.getComment());
            }

            if (deliveryAddress.getEntrance() != null && !deliveryAddress.getEntrance().equals("")) {
                getPriceRequest.setDeliveryPodjed(deliveryAddress.getEntrance());
            }
        }

        // PAYMENT TYPES
        PaymentInfo currentPaymentInfo = getSelectedPayment();
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setOrderPrice : currentPaymentInfo : " + currentPaymentInfo.getPayment_name());
        switch (currentPaymentInfo.getPaymentType()) {
            case CASH:
                getPriceRequest.setNoCashPayment(0);
                getPriceRequest.setOnlinePayment(0);
                break;
            case BONUS:
                getPriceRequest.setNoCashPayment(0);
                getPriceRequest.setOnlinePayment(0);
                getPriceRequest.setUseBonus(1);
                break;
            case CREDIT_CARD:
                getPriceRequest.setNeed_card(1);
                getPriceRequest.setOnlinePayment(1);
                getPriceRequest.setNoCashPayment(0);
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: MapViewFragment.setOrderPrice : CARD_ID : " + currentPaymentInfo.getCardInfo().getId());
                getPriceRequest.setId_card(Integer.parseInt(currentPaymentInfo.getCardInfo().getId()));
                break;
            case CORPORATION:
                getPriceRequest.setNoCashPayment(1);
                getPriceRequest.setOnlinePayment(0);
                getPriceRequest.setId_corporation(Integer.parseInt(currentPaymentInfo.getCorporationId()));
                break;
            default:
                getPriceRequest.setNoCashPayment(0);
                getPriceRequest.setOnlinePayment(0);
                break;
        }

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setOrderPrice : order_comment : " + order_comment);
        getPriceRequest.setOrderComment(order_comment);

        Tariff currentTariff = getSelectedTariff();
        if (currentTariff != null) {
            getPriceRequest.setTariff(currentTariff.getID());
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: MapViewFragment.setOrderPrice : current_tariff : " + currentTariff.getName());
        }

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setOrderPrice : slidePanelAdapter.getSelectedTariffServices() : " + getSelectedTariffServices());

        setTariffServices(getPriceRequest, getSelectedTariffServices());

        String s = new Gson().toJson(getPriceRequest);
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setOrderPrice : NEW_ORDER : JSON REQUEST : " + s);

    } // end method setOrderPrice

    /**
     * Method for setting from address field visible/invisible
     *
     * @param isVisible - check visibility
     */
    public void setFromItemVisible(boolean isVisible) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setFromItemVisible : isVisible : " + isVisible);

        if (isVisible) {
            this.setPanelItemsCount(7);
            this.getLayoutItemFrom().setVisibility(View.VISIBLE);
        } else {
            this.setPanelItemsCount(6);
            this.getLayoutItemFrom().setVisibility(View.GONE);
        }

    } // end method setFromItemVisible

    /**
     * Method for requesting permissions
     *
     * @param requestCode   - request code
     * @param permissions   - permissions list
     * @param grantResults  - grand results list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mapFragment != null) {
            mapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    } // end method onRequestPermissionsResult

    /**
     * Method for setting order comment
     *
     * @param comment - comment for showing
     */
    public void setOrderComment(String comment, String entrance) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setOrderComment : comment : " + comment);

        order_comment = comment;
        order_entrance = entrance;
        if (entrance != null && !entrance.isEmpty()) {
            String ord_comment = getResources().getString(R.string.str_entrance) + " " + entrance + ".";
            if (order_comment != null && !order_comment.isEmpty())
                ord_comment += " " + comment;
            textViewComment.setText(ord_comment);
        } else {
            textViewComment.setText(comment);
        }
        imageViewComment.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.comment_cloud_orange));

    } // end method setOrderComment

    /**
     * Method for getting SlidingUpPanelLayout
     *
     * @return - mSlidingUpPanelLayout
     */
    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return mSlidingUpPanelLayout;

    } // end method getSlidingUpPanelLayout

    /**
     * Method for setting activeOrder
     *
     * @param orderStatusData - order status data
     */
    public void setActiveOrder(OrderStatusData orderStatusData) {
        this.activeOrder = orderStatusData;

    } // end method setActiveOrderPosition

    /**
     * Method for setting menu image icon
     *
     * @param hasActive - has active orders
     */
    public void setMenuIcon(boolean hasActive) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setMenuIcon : hasActive : " + hasActive);
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setMenuIcon : imageViewMapMenu : " + imageViewMapMenu);
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.setMenuIcon : mContext : " + mContext);
        mMenuActive = hasActive;

//        if (hasActive)
//            imageViewMapMenu.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_menu_black_active));
//        else
//            imageViewMapMenu.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_menu_black));

    }// end method setMenuIcon


    /**
     * Method for setting collAddressGeoObject
     *
     * @param collAddressGeoObject - call address geo object
     */
    public void setCollAddressGeoObject(String collAddressGeoObject) {
        this.collAddressGeoObject = collAddressGeoObject;

    } // end method setCollAddressGeoObject

    /**
     * Method for setting deliveryAddressGeoObject
     *
     * @param deliveryAddressGeoObject - delivery address geo object
     */
    public void setDeliveryAddressGeoObject(String deliveryAddressGeoObject) {
        this.deliveryAddressGeoObject = deliveryAddressGeoObject;

    } // end method setDeliveryAddressGeoObject

    /**
     * Method for getting tariff services fields
     *
     * @param order - order object
     */
    public static List<String> getTariffServiceFields(Order order) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.getTariffServiceFields : order : " + order);

        List<String> serviceFields = new ArrayList<>();
        if (order.getGWidth() != null && order.getGWidth().equals("1")) {
            serviceFields.add("g_width");
        }
        if (order.getNeedWifi() != null && order.getNeedWifi().equals("1")) {
            serviceFields.add("need_wifi");
        }
        if (order.getNeedCheck() != null && order.getNeedCheck().equals("1")) {
            serviceFields.add("need_check");
        }
        if (order.getConditioner() != null && order.getConditioner().equals("1")) {
            serviceFields.add("conditioner");
        }
        if (order.getNoSmoking() == 1) {
            serviceFields.add("NoSmoking");
        }
        if (order.getEType() != null && order.getEType().equals("1")) {
            serviceFields.add("e_type");
        }
        if (order.getMeeting() != null && order.getMeeting().equals("1")) {
            serviceFields.add("meeting");
        }
        if (order.getYellow_reg_num().equals("1")) {
            serviceFields.add("yellow_reg_num");
        }
        if (order.getAnimal() != null && order.getAnimal().equals("1")) {
            serviceFields.add("animal");
        }
        if (order.getLuggage() != null && order.getLuggage().equals("1")) {
            serviceFields.add("luggage");
        }
        if (order.getChildSeats() != null && !order.getChildSeats().isEmpty()) {
            serviceFields.add("child_seats");

        }

        return serviceFields;

    } // end method getTariffServiceFields

    /**
     * Method for creating geo JSON object
     *
     * @param googlePlace - google place object
     * @return - geo JSON object
     */
    public String getGeoDataJsonObject(GooglePlace googlePlace) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: MapViewFragment.getGeoDataJsonObject : googlePlace : " + googlePlace);
        JSONObject jsonObject = new JSONObject();

        if (googlePlace != null) {
            try {
                jsonObject.put("formatted_address", googlePlace.getName());
                jsonObject.put("place_id", googlePlace.getId());

                JSONObject jsonLocation = new JSONObject();
                jsonLocation.put("lat", googlePlace.getLatLng().latitude);
                jsonLocation.put("lng", googlePlace.getLatLng().longitude);
                jsonObject.put("location", jsonLocation);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject.toString();
    }

    public boolean ismConfirmVisible() {
        return mConfirmVisible;
    }

    public void setmConfirmVisible(boolean mConfirmVisible) {
        this.mConfirmVisible = mConfirmVisible;
    }

    /**
     * Method for getting layout Top Menu
     * @return layoutTopMenu
     */
    public LinearLayout getLayoutTopMenu() {
        return layoutTopMenu;

    } // end method getLayoutTopMenu

    /**
     * Method for checking is has action
     *
     * @return - has acton
     */
    public boolean isHasAction() {
        return hasAction;

    } // end method isHasAction

    /**
     * Method for setting has action
     *
     * @param hasAction - has action
     */
    public void setHasAction(boolean hasAction) {
        this.hasAction = hasAction;

    } // end method setHasAction

    /**
     * Method for getting tariff lest
     * @return  - tariff list
     */
    public List<Tariff> getTariff_list() {
        return tariff_list;

    } // end metod getTariff_list

    /**
     * Method for getting from address list
     * @return - from address list
     */
    public ArrayList<GooglePlace> getFromAddressList() {
        return fromAddressList;

    } // end method getFromAddressList

    /**
     * Method for getting to address list
     * @return - to address list
     */
    public ArrayList<GooglePlace> getToAddressList() {
        return toAddressList;

    } // end method getToAddressList

    /**
     * Method for getting from adapter
     * @return - from adapter
     */
    public AddressRVAdapter getAdapterFrom() {
        return mAdapterFrom;

    } // end method getAdapterFrom

    /**
     * Method for getting to adapter
     * @return - from adapter
     */
    public AddressRVAdapter getAdapterTo() {
        return mAdapterTo;

    } // end method getAdapterTo

    /**
     * Method for getting layout item for scroll
     * @return - Layout item for scroll
     */
    public LinearLayout getLayoutItemForScroll() {
        return layoutItemForScroll;

    } // end method getLayoutItemForScroll
}
