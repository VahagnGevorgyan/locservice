package com.locservice.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.DriverRateAdapter;
import com.locservice.adapters.DriverReviewAdapter;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.entities.DriverInfoFeedback;
import com.locservice.api.entities.DriverInfoOrder;
import com.locservice.api.manager.DriverManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.OrderInfoActivity;
import com.locservice.ui.controls.AutoResizeTextView;
import com.locservice.ui.controls.CustomHorizontalScrollView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.RoundedImageView;
import com.locservice.ui.controls.SlidingUpPanelLayout;
import com.locservice.ui.controls.SlidingUpPanelLayout.SlideState;
import com.locservice.ui.helpers.DateHelper;
import com.locservice.ui.utils.ActivityTypes;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DriverFragment extends Fragment implements SlidingUpPanelLayout.PanelSlideListener {

    private static final String TAG = DriverFragment.class.getSimpleName();

    private View rootView;
    private FragmentActivity mContext;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private CustomHorizontalScrollView scrollViewDriverRate;

	private DriverRateAdapter driverRateAdapter;

    private ListView listViewDriverReview;

    private DriverInfo mDriverInfo;
    private View layoutForInScreenTools;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_driver_info, container, false);
        mContext = getActivity();

        if (mContext.getIntent().getParcelableExtra(CMAppGlobals.EXTRA_DRIVER_INFO) != null) {
            mDriverInfo = mContext.getIntent().getParcelableExtra(CMAppGlobals.EXTRA_DRIVER_INFO);
            setDriverInfo(mDriverInfo);
        }

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: DriverFragment.onCreateView  mDriverInfo :" + mDriverInfo);

        // set events
        setEventListeners();

        // set slide panel parameters
        setSlidePanelParameters();


        return rootView;
    }


    /**
     * Method for setting slide panel
     */
    public void setSlidePanelParameters() {
        if (isAdded()) {
            listViewDriverReview = (ListView) rootView.findViewById(R.id.listViewDriverReview);
            scrollViewDriverRate = (CustomHorizontalScrollView) rootView.findViewById(R.id.scrollViewDriverRate);

            mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
            mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

            int mapHeight = getResources().getDimensionPixelSize(R.dimen.slide_panel_height_driver);
            mSlidingUpPanelLayout.setPanelHeight(mapHeight);
            mSlidingUpPanelLayout.setScrollableView(listViewDriverReview, 0);
            mSlidingUpPanelLayout.setPanelSlideListener(this);

            // set rate list
            setRateList(rootView);

            // set review list
            setReviewList(mContext, rootView);
        }

    } // end method setSlidePanelParameters

    /**
     * Method for setting review list
     *
     * @param view
     */
    private void setReviewList(Activity context, View view) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverFragment.setReviewList");

        if (mDriverInfo != null) {
            List<DriverInfoFeedback> reviewList = mDriverInfo.getFeedbacks();
            if (reviewList != null) {
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverFragment.setReviewList : reviews size : " + reviewList.size());
                ArrayAdapter<DriverInfoFeedback> mAdapter = new DriverReviewAdapter(mContext, reviewList);

                try {
                    createOrdersList(context, listViewDriverReview, mAdapter);
                } catch (Exception ex) {
                    if(CMAppGlobals.DEBUG)Logger.e(TAG, ":: DriverFragment.setReviewList : error : " + ex.getMessage());
                }
            }
        }


    } // end method setReviewList

	/**
	 * TEST - Method for creating list view header
     *
	 * @param context
	 * @param listView
	 * @param adapter
	 */
    public void createOrdersList(Activity context, ListView listView, ArrayAdapter<DriverInfoFeedback> adapter) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverFragment.createOrdersList");
    	// add list of orders
        if (mDriverInfo != null) {
            List<DriverInfoOrder> orderList = mDriverInfo.getOrders();
            if (orderList != null) {
                DateHelper dateHelper = new DateHelper(mContext);
                for(int i=0; i<orderList.size(); i++) {
                    final DriverInfoOrder item = orderList.get(i);
                    if(item != null) {
                        View header_view = context.getLayoutInflater().inflate(R.layout.header_driver_review, listView, false);
                        TextView textViewPrice = (TextView) header_view.findViewById(R.id.textViewPrice);
                        textViewPrice.setVisibility(View.VISIBLE);
                        textViewPrice.setText(orderList.get(i).getPrice() + context.getResources().getString(R.string.str_ruble));
                        RatingBar ratingBarOrder = (RatingBar) header_view.findViewById(R.id.ratingBarOrder);
                        ratingBarOrder.setRating((float) orderList.get(i).getRate());
                        AutoResizeTextView textViewName = (AutoResizeTextView) header_view.findViewById(R.id.textViewName);
                        textViewName.setText(orderList.get(i).getCollAddressText());
                        TextView textViewDescription = (TextView) header_view.findViewById(R.id.textViewDescription);
                        Date itemDate = dateHelper.getDateByStringDateFormat(item.getCollDate(), "dd-MM-yyyy HH:mm");
                        String description = dateHelper.getDateString(itemDate) + " " + item.getTariffName();
                        textViewDescription.setText(description);
                        textViewDescription.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                        View dividerLine = (View) header_view.findViewById(R.id.dividerLine);
                        View dividerLineLarge = (View) header_view.findViewById(R.id.dividerLineLarge);
                        TextView textViewReviewTitle = (TextView) header_view.findViewById(R.id.textViewReviewTitle);
                        if(i < orderList.size() - 1) {
                            dividerLine.setVisibility(View.VISIBLE);
                            dividerLineLarge.setVisibility(View.GONE);
                            textViewReviewTitle.setVisibility(View.GONE);
                        } else {
                            dividerLine.setVisibility(View.GONE);
                            dividerLineLarge.setVisibility(View.VISIBLE);
                            textViewReviewTitle.setVisibility(View.VISIBLE);
                            if (mDriverInfo.getFeedbacks() == null || mDriverInfo.getFeedbacks().isEmpty()) {
                                textViewReviewTitle.setText(R.string.str_no_feedbacks);
                            }
                        }
                        header_view.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // check network status
                                if (Utils.checkNetworkStatus(mContext)) {
                                    Intent intent = new Intent(mContext, OrderInfoActivity.class);
                                    intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.ORDER_INFO);
                                    intent.putExtra(CMAppGlobals.EXTRA_ORDER_ID, item.getIdOrder());
                                    intent.putExtra(CMAppGlobals.EXTRA_ORDER_RATE, item.getRate());
                                    startActivityForResult(intent, CMAppGlobals.REQUEST_ORDER_INFO);
                                }
                            }
                        }

                        );
                            listView.addHeaderView(header_view);
                        }
                    }
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverFragment.onActivityResult : requestCode : " + requestCode + " : resultCode : " + resultCode + " : data : " + data);
        if (requestCode == CMAppGlobals.REQUEST_ORDER_INFO && resultCode == Activity.RESULT_OK) {
            if (mDriverInfo != null && mDriverInfo.getId() != null && !mDriverInfo.getId().isEmpty()) {
                DriverManager driverManager = new DriverManager((ICallBack) mContext);
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverFragment.onActivityResult : mDriverInfo.getId : " + mDriverInfo.getId());
                driverManager.GetDriverInfo("", mDriverInfo.getId());
            }
        }
    }

    /**
     * TEST - Method for creating list view footer
     *
     * @param text
     * @return
     */
    public View createFooter(Activity context, String text) {
        View v = context.getLayoutInflater().inflate(R.layout.footer_driver_review, null);
        ((TextView) v.findViewById(R.id.tvText)).setText(text);
        return v;
    }


    /**
     * Method for setting rate list
     *
     * @param view
     */
    private void setRateList(View view) {
		// set data
		driverRateAdapter = new DriverRateAdapter(mContext,
										   R.layout.list_item_driver_rate,
										   getRateList(),
										   scrollViewDriverRate);
		scrollViewDriverRate.setAdapter(mContext, driverRateAdapter);

    } // end method setRateList

    private Handler sliderHandler = new Handler();

    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            mSlidingUpPanelLayout.CollapsePanelFirstTime(SlideState.COLLAPSED);
        }
    };


    /**
     * Method for setting all event listeners
     */
    private void setEventListeners() {
        layoutForInScreenTools = rootView.findViewById(R.id.layoutForInScreenTools);
        if(CMApplication.hasNavigationBar(mContext)) {
            layoutForInScreenTools.setVisibility(View.VISIBLE);
        }
        final LinearLayout layoutBack = (LinearLayout) rootView.findViewById(R.id.layoutBack);
        layoutBack.setOnTouchListener(new OnTouchListener() {
            ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CommonHelper.setOnTouchImage(imageViewBack, event);
            }
        });
        layoutBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mContext != null) {
                    // check network status
                    if (Utils.checkNetworkStatus(mContext)) {
                        mContext.onBackPressed();
                    }
                }
            }
        });
        final LinearLayout layoutPhone = (LinearLayout) rootView.findViewById(R.id.layoutPhone);
        layoutPhone.setOnTouchListener(new OnTouchListener() {
            ImageView imageViewPhone = (ImageView) layoutPhone.findViewById(R.id.imageViewPhone);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CommonHelper.setOnTouchImage(imageViewPhone, event);
            }
        });
        layoutPhone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // check network status
                if (Utils.checkNetworkStatus(mContext)) {

                    if (mDriverInfo != null) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + mDriverInfo.getPhone()));
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Problem with driver's phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    } // end method setEventListeners


    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onPanelCollapsed(View panel) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onPanelExpanded(View panel) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onPanelAnchored(View panel) {
        // TODO Auto-generated method stub

    }

    /**
     * driver information section
     */
    public static class DriverRate {

        private String name;
        private String value;
        private boolean isNumberVisible;


        public DriverRate() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isNumberVisible() {
            return isNumberVisible;
        }

        public void setIsNumberVisible(boolean isNumberVisible) {
            this.isNumberVisible = isNumberVisible;
        }
    }

    /**
     * Method for setting driver info
     * @param driverInfo
     */
    public void setDriverInfo(DriverInfo driverInfo) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverFragment setDriverInfo : driverInfo : " + driverInfo);

        if (driverInfo != null) {
            mDriverInfo = driverInfo;

            LayoutInflater inflater = LayoutInflater.from(mContext);
            rootView = inflater.inflate(R.layout.fragment_driver_info, null);
            CustomTextView textViewName = (CustomTextView) rootView.findViewById(R.id.textViewName);
            if (driverInfo.getName() != null && !driverInfo.getName().isEmpty()) {
                textViewName.setText(driverInfo.getName());
            }
            ImageView imageViewCar = (ImageView) rootView.findViewById(R.id.imageViewCar);
            // SET CAR IMAGE
            if(driverInfo.getCarColorCode() != null
                    && !driverInfo.getCarColorCode().equals("")) {

                String lowerStr = driverInfo.getCarColorCode().toLowerCase();
                String imgStr = "d_" + lowerStr;
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverFragment setDriverInfo : DRIVER_IMAGE : " + imgStr);
                int drawableResourceId = this.getResources().getIdentifier(imgStr, "drawable", mContext.getPackageName());
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverFragment setDriverInfo : drawableResourceId : " + drawableResourceId);
                if(drawableResourceId > 0) {
                    imageViewCar.setImageDrawable(ContextCompat.getDrawable(mContext, drawableResourceId));
                }
            }

            CustomTextView textViewCarColor = (CustomTextView) rootView.findViewById(R.id.textViewCarColor);
            if (driverInfo.getCarColor() != null && !driverInfo.getCarColor().isEmpty()) {
                textViewCarColor.setText(driverInfo.getCarColor());
            }
            CustomTextView textViewCarName = (CustomTextView) rootView.findViewById(R.id.textViewCarName);
            if (driverInfo.getCarModel() != null && driverInfo.getCarMark() != null) {
                textViewCarName.setText(driverInfo.getCarMark() + " " + driverInfo.getCarModel());
            }
            CustomTextView textViewCarNumber = (CustomTextView) rootView.findViewById(R.id.textViewCarNumber);
            CustomTextView textViewCarNumberReg = (CustomTextView) rootView.findViewById(R.id.textViewCarNumberReg);
            if (driverInfo.getCarNumber() != null && !driverInfo.getCarNumber().isEmpty()) {
                CMApplication.setCarNumber(textViewCarNumber, textViewCarNumberReg, driverInfo);
            }
            CustomTextView textViewDriverRate = (CustomTextView) rootView.findViewById(R.id.textViewDriverRate);
            textViewDriverRate.setText(driverInfo.getRate() + "");
            RoundedImageView imageViewAvatar = (RoundedImageView) rootView.findViewById(R.id.imageViewAvatar);
            if (driverInfo.getPhotoLink() != null && !driverInfo.getPhotoLink().isEmpty()) {
                ImageHelper.loadImagesByLoader(mContext, driverInfo.getPhotoLink(), imageViewAvatar);
            }
        }

    } // end method setDriverInfo

    /**
     * Method for getting rating list
     * @return
     */
    public ArrayList<DriverRate> getRateList() {
        ArrayList<DriverRate> list = new ArrayList<DriverRate>();
        DriverRate item = new DriverRate();
        item.setName(mContext.getString(R.string.str_rate));
        if (mDriverInfo != null)
            item.setValue(mDriverInfo.getRate() + "");
        list.add(item);
//        item = new DriverRate();
//        item.setName(mContext.getString(R.string.str_your_rate));
//        if (mDriverInfo != null)
//            item.setValue(mDriverInfo.getRateByClient() + "");
//        list.add(item);
//        item = new DriverRate();
//        item.setName(mContext.getString(R.string.str_trip));
//        if (mDriverInfo != null)
//            item.setValue(mDriverInfo.getOrdersCount() + "");
//        list.add(item);
//        item = new DriverRate();
//        item.setName(mContext.getString(R.string.str_in_top));
//        if (mDriverInfo != null)
//            item.setValue(mDriverInfo.getRatePlace() + "");
//        item.setIsNumberVisible(true);
//        list.add(item);
        return list;
    }

    /**
     * Method for getting  Driver Info
     * @return
     */
    public DriverInfo getmDriverInfo() {
        return mDriverInfo;

    } // end method getmDriverInfo

    /**
     * Method for setting mDriverInfo
     * @param mDriverInfo
     */
    public void setmDriverInfo(DriverInfo mDriverInfo) {
        this.mDriverInfo = mDriverInfo;

    } // end method setmDriverInfo

    /**
     * Method for getting driver icon name
     * @param driverType - Driver car type
     * @param colorCode - Driver car color
     * @return - Driver icon name
     */
    public String getDriverIconNameForProfile(String driverType, String colorCode) {
        String lowerStr = colorCode.toLowerCase();
        String imgStr = "";
        if (driverType != null) {
            switch (driverType) {
                case "sedan":
                    imgStr = "sedan_" + lowerStr + "_driver_row";
                    break;
                case "minivan":
                    imgStr = "minivan_" + lowerStr + "_driver_row";
                    break;
                case "business":
                    imgStr = "business_" + lowerStr + "_driver_row";
                    break;
                default:
                    imgStr = "sedan_" + lowerStr + "_driver_row";
                    break;
            }
        }

        return imgStr;

    } // end method getDriverIconNameForProfile
}
