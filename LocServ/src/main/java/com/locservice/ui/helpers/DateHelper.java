package com.locservice.ui.helpers;

import android.content.Context;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.utils.LocaleManager;
import com.locservice.utils.Logger;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Vahagn Gevorgyan
 * 12 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DateHelper {

    private static final String TAG = DateHelper.class.getSimpleName();

    private Context mContext;
    private Calendar currentDate;
    private Calendar dayAgoDate;
    private Calendar weekAgoDate;
    private Calendar monthAgoDate;
    private Calendar dayAfterDate;

    public DateHelper(Context context) {
        this.mContext = context;

        // Initialization dates
        initDates();
    }

    /**
     * Method for initialization Dates
     */
    public void initDates() {

        currentDate = getDefaultDayCalendar();
        dayAgoDate = getDefaultDayCalendar();
        dayAgoDate.add(Calendar.DATE, -1);
        weekAgoDate = getDefaultDayCalendar();
        weekAgoDate.add(Calendar.DATE, -7);
        monthAgoDate = getDefaultDayCalendar();
        monthAgoDate.add(Calendar.DATE, -30);
        dayAfterDate = getDefaultDayCalendar();
        dayAfterDate.add(Calendar.DATE, 1);

    } // end method initDates

    /**
     * Method for getting Default Day Calendar
     *
     * @return - Current calendar day at 00:00 time
     */
    public static Calendar getDefaultDayCalendar () {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        return calendar;

    } // end method getDefaultDayCalendar

    /**
     * Method for getting Date by string date format
     * @param simpleDateFormat - simple date format
     * @return - Date object
     */
    public static Date getDateByStringDateFormat(String stringDate, String simpleDateFormat) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(simpleDateFormat, new Locale(LocaleManager.getLocaleLang()));
        try {
            date = sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    } // end method getDateByStringDateFormat

    /**
     * Method for getting date by timestamp
     * @param timestamp - timestamp
     *
     * @return - Date object
     */
    public static Date getDateByTimeStamp (long timestamp) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getDateByTimeStamp : timestamp : " + timestamp);

        return new Date(timestamp * 1000);

    } // end method timestamp

    /**
     * Method for getting timestamp (seconds) By Date
     * @param date - date object
     * @return - date by timestamp in seconds
     */
    public static long getTimestampByDate(Date date) {
        return (date.getTime() / 1000);

    } // end method getTimestampByDate


    /**
     * Method for getting date
     * @param date - Date
     * @return - string date by UI
     */
    public String getStringDateByUI(Date date) {
        String strDate;
        Locale locale = new Locale(LocaleManager.getLocaleLang());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm", java.util.Locale.getDefault());
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getStringDateByUI : date : " + simpleDateFormat.format(date));
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getStringDateByUI : currentDate : " +  simpleDateFormat.format(currentDate.getTime()));
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getStringDateByUI : dayAgoDate : " +  simpleDateFormat.format(dayAgoDate.getTime()));
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getStringDateByUI : weekAgoDate : " +  simpleDateFormat.format(weekAgoDate.getTime()));
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getStringDateByUI : monthAgoDate : " +  simpleDateFormat.format(monthAgoDate.getTime()));
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getStringDateByUI : dayAfterDate : " +  simpleDateFormat.format(dayAfterDate.getTime()));

        if (date.after(currentDate.getTime())) {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            if (date.before(dayAfterDate.getTime())) {
                strDate = mContext.getString(R.string.str_today) + " " +  sdf.format(date.getTime());
            } else {
                Calendar currentDate = Calendar.getInstance();
                currentDate.setTime(date);
                Map<String, Integer> months = currentDate.getDisplayNames(Calendar.MONTH, Calendar.LONG, locale);
                SimpleDateFormat sdfDay = new SimpleDateFormat("dd", java.util.Locale.getDefault());
                SimpleDateFormat sdfMonth = new SimpleDateFormat("MM", java.util.Locale.getDefault());
                String day = sdfDay.format(currentDate.getTime());
                String month = sdfMonth.format(currentDate.getTime());

                int week = currentDate.get(Calendar.DAY_OF_WEEK);
                String strWeek = getWeekDay(week, true);
                strDate = strWeek + " " + day + " " + getKeyByValue(months, Integer.parseInt(month) - 1) + " " + sdf.format(date.getTime());
            }
        } else if (date.after(dayAgoDate.getTime())) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            strDate = mContext.getString(R.string.str_yesterday) + " " +  sdf.format(date.getTime());
        } else if (date.after(weekAgoDate.getTime())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            strDate = getWeekDay(week, false);
        } else if (date.after(monthAgoDate.getTime())) {
            Map<String, Integer> months = monthAgoDate.getDisplayNames(Calendar.MONTH, Calendar.LONG, locale);
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd", java.util.Locale.getDefault());
            SimpleDateFormat sdfMonth = new SimpleDateFormat("MM", java.util.Locale.getDefault());
            String day = sdfDay.format(monthAgoDate.getTime());
            String month = sdfMonth.format(monthAgoDate.getTime());

            strDate = day + " " + getKeyByValue(months, Integer.parseInt(month) - 1);
        } else {
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd", new Locale(LocaleManager.getLocaleLang()));
            SimpleDateFormat sdfMonth = new SimpleDateFormat("MM", new Locale(LocaleManager.getLocaleLang()));
            SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", new Locale(LocaleManager.getLocaleLang()));
            String day = sdfDay.format(date.getTime());
            String month = sdfMonth.format(date.getTime());
            String year = sdfYear.format(date.getTime());

            strDate = day + " " + getMonths(LocaleManager.getLocaleLang(), false)[Integer.parseInt(month) - 1] + " " + year;
        }

        return strDate;

    } // end method getStringDateByUI.

    /**
     * Method for getting week day
     * @param day - Calendar DAY OF WEEK
     * @param isShort - if true week sort name else week long name
     *
     * @return - week name
     */
    public static String getWeekDay(int day, boolean isShort) {
        String strDate = "";
        Locale locale = new Locale(LocaleManager.getLocaleLang());
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] weekDays;
        if (isShort) {
            weekDays = symbols.getShortWeekdays();
        } else {
            weekDays = symbols.getWeekdays();
        }
        switch (day) {
            case Calendar.SUNDAY:
                strDate = weekDays[1];
                break;
            case Calendar.MONDAY:
                strDate = weekDays[2];
                break;
            case Calendar.TUESDAY:
                strDate = weekDays[3];
                break;
            case Calendar.WEDNESDAY:
                strDate = weekDays[4];
                break;
            case Calendar.THURSDAY:
                strDate = weekDays[5];
                break;
            case Calendar.FRIDAY:
                strDate = weekDays[6];
                break;
            case Calendar.SATURDAY:
                strDate = weekDays[7];
                break;

        }
        return strDate;

    } // end method getWeekDay

    /**
     * Method for getting week days
     * @param strLocale - string locale
     * @param isShort - if true week short name else weed long name
     *
     * @return - week days
     */
    public static String[] getWeekDays(String strLocale, boolean isShort) {
        Locale locale = new Locale(strLocale);
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] weekDays;
        if (isShort) {
            weekDays = symbols.getShortWeekdays();
        } else {
            weekDays = symbols.getWeekdays();
        }

        return weekDays;

    } // end method getWeekDays

    /**
     * Method for getting months days
     * @param locale - locale
     *
     * @return - months days
     */
    public static String[] getMonths (String locale, boolean isShort) {

        Calendar calendar = Calendar.getInstance();
        Locale localeCountry = new Locale(locale);
        Map<String, Integer> months;
        if (isShort) {
            months = calendar.getDisplayNames(Calendar.MONTH, Calendar.SHORT, localeCountry);
        } else {
            months = calendar.getDisplayNames(Calendar.MONTH, Calendar.LONG, localeCountry);
        }
        String[] monthsNames = new String[months.size()];
        for (int i = 0; i < months.size(); i++) {
            monthsNames[i] = getKeyByValue(months, i);
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChooseTimeFragment arrayMonths.length : " + months.size() + " : item : " + getKeyByValue(months, i));
        }

        return monthsNames;

    } // end method getMonthsDays

    /**
     * Method for getting Map kay by value
     * @param map - map
     * @param value - value of map
     *
     * @return - kay of map
     */
    public static  String getKeyByValue(Map<String, Integer> map, Integer value) {
        for (String item : map.keySet()) {
            if (map.get(item).equals(value)) {
                return item;
            }
        }
        return null;

    } // end method getKeyByValue

    /**
     * Method for getting Day times for tariff price
     * @return - working and no working times
     */
    public static List<Date> getDayTimes() {
        Calendar calendarDayFirstTime = getDefaultDayCalendar();
        Calendar calendarDaySecondTime = getDefaultDayCalendar();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm", java.util.Locale.getDefault());
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getDayDate : default_date : " + simpleDateFormat.format(getDefaultDayCalendar().getTime()));

        calendarDayFirstTime.add(Calendar.HOUR, 9);
        calendarDaySecondTime.add(Calendar.HOUR, 21);

        List<Date> dayTimes = new ArrayList<>();
        dayTimes.add(calendarDayFirstTime.getTime());
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getDayDate : calendarDayFirstTime : " + simpleDateFormat.format(calendarDayFirstTime.getTime()));
        dayTimes.add(calendarDaySecondTime.getTime());
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getDayDate : calendarDaySecondTime : " + simpleDateFormat.format(calendarDaySecondTime.getTime()));

        return dayTimes;

    } // end method getDayTimes

    public static String getDateString (Date date) {
        Locale locale = new Locale(LocaleManager.getLocaleLang());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateHelper.getStringDateByUI : date : " + simpleDateFormat.format(date));

        return simpleDateFormat.format(date);
    }
}
