package com.locservice.gcm;

import android.os.Bundle;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Vahagn Gevorgyan
 * 27 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GCMMessageHandler extends GcmListenerService {

    private static final String TAG = GCMMessageHandler.class.getSimpleName();

    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data != null) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GCMMessageHandler.onMessageReceived data : " + data);
            createNotification(from, data);
        } else {
            if (CMAppGlobals.DEBUG) Logger.e(TAG, ":: GCMMessageHandler.onMessageReceived error message received");
        }
    }

    /**
     * Creates notification based on title and body received
     * @param title
     * @param body
     */
    private void createNotification(String title, Bundle body) {
//        Context context = getBaseContext();
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
//                .setContentText(body)
//                ;
//        NotificationManager mNotificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());

    }// createNotification

}
