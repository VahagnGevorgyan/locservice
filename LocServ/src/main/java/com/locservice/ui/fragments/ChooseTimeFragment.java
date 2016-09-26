package com.locservice.ui.fragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.ui.MainActivity;
import com.locservice.ui.controls.CustomNumberPicker;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.TimeSelectionDialog;
import com.locservice.ui.helpers.DateHelper;
import com.locservice.ui.utils.ScrollType;
import com.locservice.utils.LocaleManager;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Vahagn Gevorgyan
 * 07 December 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChooseTimeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ChooseTimeFragment.class.getSimpleName();
    public static final int MONTH_DAYS_COUNT = 30;
    public static final int DAY_HOURS_COUNT = 24;
    public static final int HOUR_MINUTES_COUNT = 12;
    private FragmentActivity mContext;

    private CustomNumberPicker numberPickerDay;
    private CustomNumberPicker numberPickerHour;
    private CustomNumberPicker numberPickerMinute;
    private int mPage;

    private String[] arrayDays;
    private String[] arrayHours;
    private String[] arrayHoursCurrent;
    private String[] arrayMinutes;
    private String[] arrayMinutesCurrent;

    private Calendar[] calendarArray = new Calendar[MONTH_DAYS_COUNT];
    private View rootView;

    private static int[] arrayTake;
    private static int[] arrayBring;
    private boolean isChosen = false;
    private int mintHour;
    private int minMinute;

    public static ChooseTimeFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(TimeSelectionDialog.ARG_PAGE, page);
        ChooseTimeFragment fragment = new ChooseTimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15);
        SimpleDateFormat hour = new SimpleDateFormat("HH", new Locale(LocaleManager.getLocaleLang()));
        SimpleDateFormat minute = new SimpleDateFormat("mm", new Locale(LocaleManager.getLocaleLang()));
        mintHour = Integer.parseInt(hour.format(calendar.getTime()));
        minMinute = Integer.parseInt(minute.format(calendar.getTime()));
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment.onCreate : minMinute : " + minMinute
                + " : mintHour : " + mintHour);
        // binding arrays
        arrayHoursCurrent = bindHours(mintHour);
        arrayHours = bindHours(0);
        arrayMinutesCurrent = bindMinutes(minMinute);
        arrayMinutes = bindMinutes(0);
        arrayDays = bindArrayDays();
        if (getArguments() != null) mPage = getArguments().getInt(TimeSelectionDialog.ARG_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_choose_time, container, false);
        mContext = getActivity();

        CustomTextView textViewChoose = (CustomTextView) rootView.findViewById(R.id.textViewChoose);
        CustomTextView textViewCancel = (CustomTextView) rootView.findViewById(R.id.textViewCancel);
        CustomTextView textViewMessage = (CustomTextView) rootView.findViewById(R.id.textViewMessage);
        numberPickerDay = (CustomNumberPicker) rootView.findViewById(R.id.numberPickerDay);
        numberPickerHour = (CustomNumberPicker) rootView.findViewById(R.id.numberPickerHour);
        numberPickerMinute = (CustomNumberPicker) rootView.findViewById(R.id.numberPickerMinute);

        if (mPage == 0) {
            textViewMessage.setText(R.string.str_choose_time_info);
        } else if (mPage == 1) {
            textViewMessage.setText(R.string.str_time_tab_take_message);
        }

        int picker_color = ContextCompat.getColor(getContext(), R.color.time_dialog);
        setDividerColor(numberPickerDay, picker_color);
        setDividerColor(numberPickerHour, picker_color);
        setDividerColor(numberPickerMinute, picker_color);

        textViewChoose.setOnClickListener(this);
        textViewCancel.setOnClickListener(this);

        numberPickerHour.setMaxValue(DAY_HOURS_COUNT - 1);
        numberPickerHour.setMinValue(mintHour);
        numberPickerHour.setWrapSelectorWheel(false);
        numberPickerHour.setDisplayedValues(arrayHoursCurrent);

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment.onCreateView : arrayMinutesCurrent.length : " + arrayMinutesCurrent.length);
        for (int i = 0; i < arrayMinutesCurrent.length; i++) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment.onCreateView : test : i : " + i + " : arrayMinutesCurrent : " + arrayMinutesCurrent[i]);

        }
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment.onCreateView : hourMinutesCount : " + HOUR_MINUTES_COUNT
                + " : arrayMinutesCurrent.length : " + arrayMinutesCurrent.length
        );
        numberPickerMinute.setMaxValue(HOUR_MINUTES_COUNT - 1);
        numberPickerMinute.setMinValue(HOUR_MINUTES_COUNT - arrayMinutesCurrent.length);
        numberPickerMinute.setWrapSelectorWheel(false);
        numberPickerMinute.setDisplayedValues(arrayMinutesCurrent);


        numberPickerDay.setMaxValue(MONTH_DAYS_COUNT - 1);
        numberPickerDay.setMinValue(0);
        numberPickerDay.setWrapSelectorWheel(false);
        numberPickerDay.setDisplayedValues(arrayDays);
        numberPickerDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        numberPickerDay.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: onScrollStateChange scrollState : " + scrollState + " position : " + view.getValue());
                if (view.getValue() == 0) {

                    numberPickerHour.setMaxValue(DAY_HOURS_COUNT - 1);
                    numberPickerHour.setMinValue(mintHour);
                    numberPickerHour.setWrapSelectorWheel(false);
                    numberPickerHour.setDisplayedValues(bindHours(mintHour));

                    numberPickerMinute.setMaxValue(HOUR_MINUTES_COUNT - 1);
                    numberPickerMinute.setMinValue(HOUR_MINUTES_COUNT - arrayMinutesCurrent.length);
                    numberPickerMinute.setWrapSelectorWheel(false);
                    numberPickerMinute.setDisplayedValues(bindMinutes(minMinute));
                } else {
                    numberPickerHour.setDisplayedValues(arrayHours);
                    numberPickerHour.setMaxValue(DAY_HOURS_COUNT - 1);
                    numberPickerHour.setMinValue(0);
                    numberPickerHour.setWrapSelectorWheel(false);

                    numberPickerMinute.setDisplayedValues(arrayMinutes);
                    numberPickerMinute.setMaxValue(HOUR_MINUTES_COUNT - 1);
                    numberPickerMinute.setMinValue(0);
                    numberPickerMinute.setWrapSelectorWheel(false);

                }
            }
        });

        numberPickerHour.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: ChooseTimeFragment numberPickerHour : onScrollStateChange :"
                        + scrollState + " position : " + view.getValue());
                if (view.getValue() == Integer.parseInt(arrayHoursCurrent[0])) {

                    numberPickerMinute.setMaxValue(HOUR_MINUTES_COUNT - 1);
                    numberPickerMinute.setMinValue(HOUR_MINUTES_COUNT - arrayMinutesCurrent.length);
                    numberPickerMinute.setWrapSelectorWheel(false);
                    numberPickerMinute.setDisplayedValues(bindMinutes(minMinute));
                    if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: ChooseTimeFragment numberPickerHour : onScrollStateChange : minMinute : " + minMinute);
                } else {
                    numberPickerMinute.setDisplayedValues(arrayMinutes);
                    numberPickerMinute.setMaxValue(HOUR_MINUTES_COUNT - 1);
                    numberPickerMinute.setMinValue(0);
                    numberPickerMinute.setWrapSelectorWheel(false);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPage == 0 && !isChosen) {
            arrayTake = new int[] { numberPickerDay.getValue(), numberPickerHour.getValue(), numberPickerMinute.getValue() };
        } else if (mPage == 1 && !isChosen){
            arrayBring = new int[] { numberPickerDay.getValue(), numberPickerHour.getValue(), numberPickerMinute.getValue() };
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        if (mPage == 0) {
            if (arrayTake != null) {
                numberPickerDay.setValue(arrayTake[0]);
                numberPickerHour.setValue(arrayTake[1]);
                numberPickerMinute.setValue(arrayTake[2]);
            }
        } else if (mPage == 1){
            if (arrayBring != null) {
                numberPickerDay.setValue(arrayBring[0]);
                numberPickerHour.setValue(arrayBring[1]);
                numberPickerMinute.setValue(arrayBring[2]);
            }
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(mContext)) {

            TimeSelectionDialog dialog = (TimeSelectionDialog) mContext.getSupportFragmentManager().findFragmentByTag("OrderTimeDealogFragment");
            switch (v.getId()) {
                case R.id.textViewChoose:
                    String time = arrayDays[numberPickerDay.getValue()] + " " +
                            arrayHours[numberPickerHour.getValue()] + ":" +
                            arrayMinutes[numberPickerMinute.getValue()];

                    Calendar calendarBring = calendarArray[numberPickerDay.getValue()];
                    calendarBring.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrayHours[numberPickerHour.getValue()]));
                    calendarBring.set(Calendar.MINUTE, Integer.parseInt(arrayMinutes[numberPickerMinute.getValue()]));
                    Date dateBring = calendarBring.getTime();

                    if (mContext != null && mContext instanceof MainActivity) {
                        if (((MainActivity) mContext).getCurrent_fragment() instanceof MapViewFragment) {
                            ((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment())
                                    .refreshTimeByType(mContext, ScrollType.SO_WHEN, dateBring, time);
                        }
                    }
                    arrayTake = null;
                    arrayBring = null;
                    isChosen = true;
                    dialog.dismiss();
                    break;
                case R.id.textViewCancel:
                    arrayTake = null;
                    arrayBring = null;
                    isChosen = true;
                    dialog.dismiss();
                    break;
            }
        }
    }

    /**
     * Method for bind array of number picker
     * @param minHour - min hour
     *
     * @return - hours by UI
     */
    public String[] bindHours(int minHour) {
        String[] hours = new String[24 - minHour];
        for (int i = minHour; i < 24; i++) {
            String current;
            if (i < 10) {
                current = "0" + i;
            } else {
                current = "" + i;
            }
            Log.d(TAG, ":: bindHours current : " + current);
            hours[i- minHour] = current;
        }
        return hours;

    } // end method bindHour

    /**
     * Method for getting minimum minute (5 step)
     * @param minMinute - min minute
     *
     * @return - min minute by UI
     */
    public int getMinMinute(int minMinute) {
        for (int i = 0; i < 5; i++) {
            if (minMinute % 5 == 0){
                break;
            }
            minMinute++;
        }
        if (minMinute == 60) {
            minMinute = 0;
        }
        return minMinute;

    } // end method getMinMinute

    /**
     * Method for bind array of number picker
     * @param minMinute - min minute
     *
     * @return - minutes by UI
     */
    public String[] bindMinutes(int minMinute) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment.bindMinutes : minMinute : " + minMinute);
        minMinute = getMinMinute(minMinute);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment.bindMinutes : minMinute : 5 : point " + minMinute);

        List<String> listMinutes = new ArrayList<>();

        for (int i = minMinute; i < 60; i += 5) {
            String current;
            if (i < 10) {
                current = "0" + i;
            } else {
                current = "" + i;
            }
            Log.d(TAG, ":: bindMinutes current : " + current);
            listMinutes.add(current);
        }
        String[] minutes = new String[listMinutes.size()];
        for (int i = 0; i < listMinutes.size(); i++) {
            minutes[i] = listMinutes.get(i);
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment.bindMinutes : minute : " + listMinutes.get(i) + " : i : " + i);
        }

        return minutes;

    } // end method bindMinutes

    /**
     * Method for binding days
     */
    public String[] bindArrayDays() {
        String[] weekDays = DateHelper.getWeekDays(LocaleManager.getLocaleLang(), true);
        String[] months = DateHelper.getMonths(LocaleManager.getLocaleLang(), true);
        String[] days = new String[MONTH_DAYS_COUNT];
        for (int i = 0; i < MONTH_DAYS_COUNT; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            calendarArray[i] = calendar;
            String dayName;
            if (i == 0) {
                dayName = getResources().getString(R.string.str_today);
            } else {
                dayName = weekDays[calendar.get(Calendar.DAY_OF_WEEK)] + " "
                        + calendar.get(Calendar.DAY_OF_MONTH) + " "
                        + months[calendar.get(Calendar.MONTH)];
            }
            days[i] = dayName;

        }

        return days;

    } // end method bindArrayDays

    /**
     * Method for setting NumberPicker divider color
     * @param picker - Number picer
     * @param color - color
     */
    @SuppressLint("NewApi")
    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    } // end method setDividerColor
}
