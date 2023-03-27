package com.cityone.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.activities.SplashActivity;
import com.cityone.shipping.ShippDetailsActivity;
import com.cityone.shipping.ShippStatusActivity;
import com.cityone.taxi.activities.TrackTaxiAct;
import com.cityone.utils.AppConstant;
import com.cityone.utils.SharedPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    Intent intent;
    SharedPref sharedPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // log the getting message from firebase
        // Log.d(TAG, "From: " + remoteMessage.getFrom());

        //  if remote message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            // Log.d(TAG, "Message data payload : " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            String jobType = data.get("type");

            /* Check the message contains data If needs to be processed by long running job
               so check if data needs to be processed by long running job */

            // Handle message within 10 seconds
            handleNow(data);

             /* if (jobType.equalsIgnoreCase(JobType.LONG.name())) {
                 // For long-running tasks (10 seconds or more) use WorkManager.
                 scheduleLongRunningJob();
            } else {} */

        }

        // if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            // Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // make a own server request here using your http client
    }

    private void handleNow(Map<String, String> data) {
        sendNotification(data.get("title"), data.get("message"));
    }

    private void sendNotification(String title, String messageBody) {

        sharedPref = SharedPref.getInstance(this);

        if(sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
            if(messageBody != null) {
                Log.e("hjasgfasfdsf","title = " + title);
                Log.e("hjasgfasfdsf","messageBody = " + messageBody);

                JSONObject jsonObject = null;
                String msg = "";
                String shipId = "";
                String status = "";
                String key = "";
                String request_id = "";

                try {
                    jsonObject = new JSONObject(messageBody);
                    msg = jsonObject.getString("message");
                    shipId = jsonObject.getString("shipping_id");
                } catch (JSONException e) {}

                try {
                    status = jsonObject.getString("status");
                    key = jsonObject.getString("key");
                } catch (Exception e) {}

                if("Pending".equals(status)) {
                    Intent intent = new Intent("shipdetail");
                    intent.putExtra("parcelid",shipId);
                    sendBroadcast(intent);
                } else if("Pickup".equals(status) || "Delivered".equals(status)) {
                    Intent intent = new Intent("sending");
                    sendBroadcast(intent);
                }

                if(key.equals("shipping")) {
                    callShippingWhenNotifyClicked(status,shipId,msg);
                }

                if(key.equalsIgnoreCase("taxi")) {
                    if(status.equalsIgnoreCase("Accept")) {
                        title = "New Booking Request";
                        try {
                            request_id = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("driver_accept_request");
                        Log.e("fsdfsfsdfdsfdsf", "below intent = " + jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Driver is on the way",request_id,"");
                    } else if(status.equalsIgnoreCase("Arrived")) {
                        title = "New Booking Request";
                        try {
                            request_id = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Driver Arrived",request_id,"");
                    } else if(status.equalsIgnoreCase("Start")) {
                        title = "New Booking Request";
                        try {
                            request_id = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Your trip has begin",request_id,"");
                    } else if(status.equalsIgnoreCase("Finish")) {
                        title = "New Booking Request";
                        try {
                            request_id = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Your trip is finished",request_id,"");
                    } else if(status.equalsIgnoreCase("Cancel_by_driver")) {
                        title = "New Booking Request";
                        try {
                            request_id = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Your trip is cancelled",request_id,"Cancel_by_driver");
                    }
                }
            }
        }
    }

    private void calltaxiStatusClicked(String title, String msg ,String requestId,String status) {

        if(status.equals("Cancel_by_driver")) {
            intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("request_id",requestId);
        } else {
            intent = new Intent(this, TrackTaxiAct.class);
            intent.putExtra("request_id",requestId);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(getNotificationId(), notificationBuilder.build());
    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

    private void callShippingWhenNotifyClicked(String status,String shipId,String msg) {

        if("Pending".equals(status)) {
            intent = new Intent(this, ShippDetailsActivity.class);
            intent.putExtra("parcelid",shipId);
        } else if("Pickup".equals(status) || "Delivered".equals(status)) {
            intent = new Intent(this, ShippStatusActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(getNotificationId(), notificationBuilder.build());
    }

    private boolean isAppOnForeground(Context context,String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                // Log.e("app",appPackageName);
                return true;
            }
        }
        return false;
    }

}

