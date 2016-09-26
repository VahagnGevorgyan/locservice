package com.locservice.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.application.LocServicePreferences;
import com.locservice.messages.WebViewDialogMessage;
import com.locservice.service.DetachableResultsReceiver;
import com.locservice.ui.MainActivity;
import com.locservice.utils.JsonParser;
import com.locservice.utils.Logger;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Vahagn Gevorgyan
 * 29 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GCMIntentService extends IntentService {
    private static final String TAG = GCMIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    protected final DetachableResultsReceiver serviceReceiver = new DetachableResultsReceiver(new Handler());

    public GCMIntentService() {
        super("GcmIntentService" + GCMUtils.SENDER_ID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: GCMIntentService.onHandleIntent : intent : " + intent);

        if (intent != null) {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            // The getMessageType() intent parameter must be the intent you received
            // in your BroadcastReceiver.
            String messageType = gcm.getMessageType(intent);

            if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                    sendNotification("Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                    sendNotification("Deleted messages on server: "
                            + extras.toString());
                    // If it's a regular GCM message, do some work.
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {
                    // This loop represents the service doing some work.
                    if (extras.containsKey("newsURL")) {
                    }
                    sendNotification(extras);

                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GCMIntentService.onHandleIntent : Received : " + extras.toString());
                }
            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            LocServiceGCMBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    } // end method sendNotification

    /**
     * Method for sending notifications
     * @param extra
     */
    private void sendNotification(Bundle extra) {

        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: GCMIntentService.sendNotification : extra : " + extra);

//        int message = R.string.gcm_message;
        String message = extra.getString("message");
        String status = extra.getString("status");
        String orderId = extra.getString("order_id");

        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: GCMIntentService.sendNotification : message : " + message);
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: GCMIntentService.sendNotification : status : " + status);


        GCMMessages receivedStatus = GCMMessages.NONE;

        String type = extra.getString("type");
        if (type != null && type.equals("webview")){

            String subtitle = extra.getString("subtitle");
            String title = extra.getString("title");
            String url = extra.getString("url");
            if (url != null && !url.isEmpty())
                EventBus.getDefault().post(new WebViewDialogMessage(url));

            GCMUtils.GCMNotifyWebViewNews(this, title, subtitle, GCMMessages.WEB_VIEW_NEWS.ordinal(), new WebViewDialogMessage(url));
        }

        if (status != null) {
            if (TextUtils.equals(status, "AM")) {
                receivedStatus = GCMMessages.AM;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "R")) {
                receivedStatus = GCMMessages.R;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "A")) {
                receivedStatus = GCMMessages.A;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "CC")) {
                receivedStatus = GCMMessages.CC;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "NC")) {
                receivedStatus = GCMMessages.NC;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "SC")) {
                receivedStatus = GCMMessages.SC;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "BR")) {
                receivedStatus = GCMMessages.BR;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "RC")) {
                receivedStatus = GCMMessages.RC;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            } else if (TextUtils.equals(status, "OW")) {
                receivedStatus = GCMMessages.OW;
                GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                        orderId);
            }

            else {
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GCMIntentService : JSON CASE ");
                try {
                    JSONObject json = new JSONObject(status);
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GCMIntentService : json : " + json.toString());

                    if (json != null) {
                        String orderStatus = JsonParser.getString(json,
                                "status");
                        if (orderStatus != null
                                && TextUtils.equals(orderStatus, "CP")) {
                            receivedStatus = GCMMessages.CP;
                            String price = json.optString("price");
                            String bonus = json.optString("bonus");
                            String time = json.optString("time");

                            GCMUtils.GCMNotifyUIEx(this, message,
                                    receivedStatus.ordinal(), orderId, price,
                                    time, bonus);
                        } else if (TextUtils.equals(orderStatus, "AR")) {
                            return;
                        } else if (TextUtils.equals(orderStatus, "AM")) {
                            receivedStatus = GCMMessages.AM;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "R")) {
                            receivedStatus = GCMMessages.R;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "A")) {
                            receivedStatus = GCMMessages.A;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "CC")) {
                            receivedStatus = GCMMessages.CC;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "NC")) {
                            receivedStatus = GCMMessages.NC;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "SC")) {
                            receivedStatus = GCMMessages.SC;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "BR")) {
                            receivedStatus = GCMMessages.BR;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "RC")) {
                            receivedStatus = GCMMessages.RC;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        } else if (TextUtils.equals(orderStatus, "OW")) {
                            receivedStatus = GCMMessages.OW;
                            GCMUtils.GCMNotifyUI(this, message, receivedStatus.ordinal(),
                                    orderId);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // notifies user
        if (LocServicePreferences.getAppSettings().getString(
                LocServicePreferences.Settings.NEWS_WEBVIEW_MESSAGE.key(), "") != null
                && !LocServicePreferences.getAppSettings()
                .getString(LocServicePreferences.Settings.NEWS_WEBVIEW_MESSAGE.key(), "")
                .isEmpty()) {
            NotificationManager mNotificationManager = (NotificationManager) CMApplication
                    .getAppContext().getSystemService(
                            Context.NOTIFICATION_SERVICE);
            Class<?> startClass = MainActivity.class;
            Intent resultIntent = new Intent(CMApplication.getAppContext(), startClass);
            resultIntent.putExtra("WEBVIEW", "WEBVIEW");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(CMApplication.getAppContext());
            // Adds the back stack
            stackBuilder.addParentStack(startClass);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            // Gets a PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    CMApplication.getAppContext())
                    .setSmallIcon(R.drawable.ic_nf_small)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentTitle(
                            CMApplication.getAppContext().getString(
                                    R.string.app_name))
                    .setStyle(
                            new NotificationCompat.BigTextStyle()
                                    .bigText(LocServicePreferences
                                            .getAppSettings()
                                            .getString(
                                                    LocServicePreferences.Settings.NEWS_WEBVIEW_MESSAGE
                                                            .key(), "")
                                            .toString()))
                    .setContentText(
                            LocServicePreferences.getAppSettings().getString(
                                    LocServicePreferences.Settings.NEWS_WEBVIEW_MESSAGE.key(), ""));
            LocServicePreferences.getAppSettings().setSetting(
                    LocServicePreferences.Settings.NEWS_WEBVIEW_MESSAGE, "");

            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(0, mBuilder.build());

        } else if ((receivedStatus != GCMMessages.NONE)
                && TextUtils.isEmpty(orderId)) {
//            Utils.generateNotification(this, receivedStatus, orderId, -1);
        }

    } // end method sendNotification

//    protected class SetMarkpushAsyncTask extends AsyncTask<String, Void, JSONObject> {
//
//        @Override
//        protected JSONObject doInBackground(String... params) {
//            String status = params[0];
//            String orderId = params[1];
//            StringBuilder result = new StringBuilder();
//
//            Log.i(TAG, "::: GCM : SetMarkpushAsyncTask.doInBackground : status : " + status + " : orderId : " + orderId);
//
//            try {
//                URL url;
//                HttpURLConnection urlConn;
//                OutputStreamWriter printout;
//                url = new URL (Config.HOST_RELEASE);
//                urlConn = (HttpURLConnection) url.openConnection();
//                urlConn.setRequestMethod("POST");
//                urlConn.setDoInput (true);
//                urlConn.setDoOutput (true);
//                urlConn.setUseCaches (false);
//                urlConn.setRequestProperty("Content-Type","application/json");
//                urlConn.connect();
//
//                printout = new OutputStreamWriter(urlConn.getOutputStream ());
//                String jsonObject = composeEntityValues(status, orderId).toString();
//
//                printout.write(jsonObject);
//                printout.flush ();
//                printout.close ();
//
//                InputStream in = new BufferedInputStream(urlConn.getInputStream());
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//
//                urlConn.disconnect();
//            } catch( Exception e) {
//                e.printStackTrace();
//            }
//
//            Log.i(TAG, "::: GCM : SetMarkpushAsyncTask.doInBackground : response : " + result);
//
//            JSONObject jObject = null;
//            try {
//                jObject = new JSONObject(result.toString());
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return jObject;
//        }
//
//        protected void onPostExecute(JSONObject object) {
//            if (object != null) {
//                Log.i(TAG, "::: GCM : SetMarkpushAsyncTask.onPostExecute : json_object : " + object);
//            }
//        }
//    };
//    protected JSONObject composeEntityValues(String status, String orderId) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("index", "1");
//            jsonObject.put("devid", LocService.getDeviceId());
//            jsonObject.put("idhash", orderId);
//            jsonObject.put("status", status);
//            jsonObject.put("phone_os", "android");
//            if (LocServicePreferences.getAppSettings()
//                    .getString(Settings.LANGUAGE.key(), "").equals("hy")) {
//                jsonObject.put("locale", "am");
//            } else {
//                jsonObject.put("locale", LocService.getLocaleLang());
//            }
//            jsonObject.put("IdLocality", LocService.getCurrentPlaceId()
//                    .isEmpty() ? "0" : LocService.getCurrentPlaceId());
//            jsonObject.put("method", "markpush");
//            jsonObject.put("login", "V`qWHpL<Y9ZM");
//            jsonObject.put("ver", Config.SERVER_API_VERSION);
//            jsonObject.put("devtype", "1");
//            jsonObject.put("password", "rp&vDnhI<=rI");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("TAG", "Cannot obtain JSON");
//        }
//        return jsonObject;
//    }

}