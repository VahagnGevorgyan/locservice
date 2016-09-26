package com.locservice.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.Statistics;
import com.locservice.api.manager.StatisticsManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.RoundedImageView;
import com.locservice.ui.helpers.RussianWordExtensionHelper;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;



public class StatisticsActivity extends BaseActivity implements View.OnClickListener, ICallBack {

    private static final String TAG = StatisticsActivity.class.getSimpleName();
    private CustomTextView textViewTrips;

    private CustomTextView textViewKm;
    private CustomTextView textViewMinute;

    private CustomTextView textViewBonusKm;
    private CustomTextView textViewBonusMinute;

    private CustomTextView textViewAverageKm;
    private CustomTextView textViewAverageMinute;

    private CustomTextView textViewSpeed;

    private CustomTextView textViewTripsTitle;
    private CustomTextView textViewKmTitle;
    private CustomTextView textViewMinuteTitle;
    private CustomTextView textViewBonusKmTitle;
    private CustomTextView textViewBonusMinuteTitle;
    private CustomTextView textViewAverageKmTitle;
    private CustomTextView textViewAverageMinuteTitle;
    private CustomTextView textViewSumAvgPrice;
    private CustomTextView textViewSumBonusPrice;
    private CustomTextView textViewSumPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Finding views
        findViews();

        // Setting event listeners
        setEventListeners();

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            // Setting profile info
            setProfileInfo();

            // Request for getting Statistics
            StatisticsManager statisticsManager = new StatisticsManager(this);
            statisticsManager.GetStatistics();
        }

    }

    /**
     * Method for finding view
     */
    public void findViews() {
        textViewTrips = (CustomTextView) findViewById(R.id.textViewTrips);

        textViewKm = (CustomTextView) findViewById(R.id.textViewKm);
        textViewMinute = (CustomTextView) findViewById(R.id.textViewMinute);

        textViewBonusKm = (CustomTextView) findViewById(R.id.textViewBonusKm);
        textViewBonusMinute = (CustomTextView) findViewById(R.id.textViewBonusMinute);

        textViewAverageKm = (CustomTextView) findViewById(R.id.textViewAverageKm);
        textViewAverageMinute = (CustomTextView) findViewById(R.id.textViewAverageMinute);

        textViewSpeed = (CustomTextView) findViewById(R.id.textViewSpeed);

        textViewTripsTitle = (CustomTextView) findViewById(R.id.textViewTripsTitle);
        textViewKmTitle = (CustomTextView) findViewById(R.id.textViewKmTitle);
        textViewMinuteTitle = (CustomTextView) findViewById(R.id.textViewMinuteTitle);
        textViewBonusKmTitle = (CustomTextView) findViewById(R.id.textViewBonusKmTitle);
        textViewBonusMinuteTitle = (CustomTextView) findViewById(R.id.textViewBonusMinuteTitle);
        textViewAverageKmTitle = (CustomTextView) findViewById(R.id.textViewAverageKmTitle);
        textViewAverageMinuteTitle = (CustomTextView) findViewById(R.id.textViewAverageMinuteTitle);

        textViewSumAvgPrice = (CustomTextView) findViewById(R.id.textViewSumAvgPrice);
        textViewSumBonusPrice = (CustomTextView) findViewById(R.id.textViewSumBonusPrice);
        textViewSumPrice = (CustomTextView) findViewById(R.id.textViewSumPrice);

        if(CMApplication.hasNavigationBar(StatisticsActivity.this)) {
            findViewById(R.id.container_statistics).setPadding(0, 0, 0, CMApplication.dpToPx(48));

        }

    } // end method findViews


    /**
     * Method for setting all event listeners
     */
    private void setEventListeners() {

        final LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnTouchListener(new View.OnTouchListener() {
            ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CommonHelper.setOnTouchImage(imageViewBack, event);
            }
        });
        layoutBack.setOnClickListener(this);

    } // end method setEventListeners

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                onBackPressed();
                break;
        }
    }

    /**
     * Method for setting profile info
     */
    public void setProfileInfo() {

        String base64Bitmap = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), "");
        RoundedImageView imageViewAvatar = (RoundedImageView) findViewById(R.id.imageViewAvatar);
        if (!base64Bitmap.isEmpty()) {
            imageViewAvatar.setImageBitmap(CMApplication.decodeBase64ToBitmap(base64Bitmap));
        }

        String name = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "");
        CustomTextView textViewFullName = (CustomTextView) findViewById(R.id.textViewFullName);
        if (!name.isEmpty()) {
            textViewFullName.setText(name);
        }

        String rewards = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
        CustomTextView textViewRewards = (CustomTextView) findViewById(R.id.textViewRewards);
        if (!rewards.isEmpty()) {
            String rewardsText = rewards + getResources().getString(R.string.str_menu_rewards);
            textViewRewards.setText(rewardsText);
        }

    } // end method setProfileInfo

    @Override
    public void onFailure(Throwable error, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: StatisticsActivity.onFailure : requestType : " + requestType + " : error : " + error.getMessage());
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: StatisticsActivity.OnSuccess : requestType : " + requestType + " : response : " + response);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_GET_STATISTICS:
                    if(response instanceof Statistics) {
                        Statistics statistics = (Statistics) response;
                        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: StatisticsActivity.OnSuccess : REQUEST_GET_STATISTICS : statistics : " + statistics);

                        String ordersCount = statistics.getOrdersCount() + "";
                        textViewTrips.setText(ordersCount);

                        String distance = statistics.getDistance() + "";
                        textViewKm.setText(distance);
                        String time = statistics.getTime() + "";
                        textViewMinute.setText(time);

                        String distanceBonus = statistics.getDistanceBonus() + "";
                        textViewBonusKm.setText(distanceBonus);
                        String timeBonus = statistics.getTimeBonus() + "";
                        textViewBonusMinute.setText(timeBonus);

                        String avgDistance = statistics.getAvgDistance() + "";
                        textViewAverageKm.setText(avgDistance);
                        String avgTime = statistics.getAvgTime() + "";
                        textViewAverageMinute.setText(avgTime);

                        String avgTimeSpeed = statistics.getAvgTimeSpeed() + "";
                        textViewSpeed.setText(avgTimeSpeed);

                        String sumPrice = statistics.getOrdersSum() + "";
                        textViewSumPrice.setText(sumPrice);

                        String sumBonusPrice = statistics.getSumBonus() + "";
                        textViewSumBonusPrice.setText(sumBonusPrice);

                        String sumAvgPrice = statistics.getAvgPrice() + "";
                        textViewSumAvgPrice.setText(sumAvgPrice);

                        // TODO in release version uncomment if(...)
                        // Setting right russian extensions
    //                    if (LocaleManager.getLocaleLang().equals("ru")) {
                            textViewTripsTitle.setText(RussianWordExtensionHelper.getTrip(StatisticsActivity.this, statistics.getOrdersCount()));
                            textViewKmTitle.setText(RussianWordExtensionHelper.getKilometer(StatisticsActivity.this, statistics.getDistance()));
                            textViewMinuteTitle.setText(RussianWordExtensionHelper.getMinute(StatisticsActivity.this, statistics.getTime()));
                            textViewBonusKmTitle.setText(RussianWordExtensionHelper.getKilometer(StatisticsActivity.this, statistics.getDistanceBonus()));
                            textViewBonusMinuteTitle.setText(RussianWordExtensionHelper.getMinute(StatisticsActivity.this, statistics.getTimeBonus()));
                            textViewAverageKmTitle.setText(RussianWordExtensionHelper.getKilometer(StatisticsActivity.this, statistics.getAvgDistance()));
                            textViewAverageMinuteTitle.setText(RussianWordExtensionHelper.getMinute(StatisticsActivity.this, statistics.getAvgTime()));
    //                    }
                    }
                    break;
            }
        }

    }
}
