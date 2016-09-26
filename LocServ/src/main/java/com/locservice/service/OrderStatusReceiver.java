package com.locservice.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;

import java.util.Calendar;

/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderStatusReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = OrderStatusReceiver.class.getSimpleName();

    private static final int PERIOD = 10000; // 10 seconds
    private static final int INITIAL_DELAY = 1500; // 1.5 seconds

    @Override
    public void onReceive(Context ctxt, Intent i) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStatusReceiver.onReceive : i.getAction() : " + i.getAction());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            startWakefulService(ctxt, new Intent(ctxt,
                    GetOrderStatusService.class));
            scheduleAlarms(ctxt);
        } else {
            if (i.getAction() == null) {
                startWakefulService(ctxt, new Intent(ctxt,
                        GetOrderStatusService.class));
            } else {
                scheduleAlarms(ctxt);
            }
        }

    }

    public static void scheduleAlarms(Context ctxt) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStatusReceiver.scheduleAlarms : SystemClock.elapsedRealtime() : "
                + SystemClock.elapsedRealtime() + " : INITIAL_DELAY : " + INITIAL_DELAY + " : PERIOD : " + PERIOD);

        AlarmManager mgr = (AlarmManager) ctxt
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctxt, OrderStatusReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 10);
            mgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        } else {
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + INITIAL_DELAY, PERIOD, pi);
        }

    }

    public static void cancelAlarms(Context ctxt) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStatusReceiver.cancelAlarms : ctxt : " + ctxt);
        AlarmManager mgr = (AlarmManager) ctxt
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctxt, OrderStatusReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);

        mgr.cancel(pi);

    }

}

