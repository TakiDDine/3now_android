package de.threenow.FCM;

/**
 * Created by Ajay Tiwari on 26/08/18.
 */

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import de.threenow.Activities.MainActivity;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;
import de.threenow.calender.Utils;
import de.threenow.chat.UserChatActivity;

public class FCMService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String TAG = "MyFirebaseMsgService";
    Utils utils = new Utils();
    String Tag = "FCMService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedHelper.putKey(getApplicationContext(), "device_token", "" + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(Tag, remoteMessage.getData().toString());
        Log.v(Tag+"Image", remoteMessage.getNotification().getImageUrl()+" ");
        String msg_type = remoteMessage.getNotification().getTitle();
        Log.d(Tag, msg_type);
        if(msg_type!=null) {
            Log.d(Tag, "!=null");
            if (msg_type.equalsIgnoreCase("chat")) {
                Log.d(Tag, "msg_type == chat");
                Log.d(Tag, getTopAppName() + "");
            if (getTopAppName().equals(UserChatActivity.class.getName())) {
                Log.d(Tag,  "if");
                Intent intent = new Intent();
                intent.putExtra("message", remoteMessage.getNotification().getBody());
                intent.setAction("com.my.app.onMessageReceived");
                sendBroadcast(intent);
                Log.v(Tag + "message", remoteMessage.getNotification().getImageUrl()+" ");

            } else {
                Log.d(Tag,  "else");
                handleNotification(remoteMessage);
            }
            }
            else if (msg_type.contains("admin"))
            {
                String title=remoteMessage.getNotification().getTitle();
                String message=remoteMessage.getNotification().getBody();
                String click_action = "com.quickridejadriver.providers.TARGETNOTIFICATION";
                Intent intent =new Intent(click_action);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                notifiBuilder.setContentTitle(title);
                notifiBuilder.setContentText(message);
                notifiBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notifiBuilder.setAutoCancel(true);
                notifiBuilder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0,notifiBuilder.build());
            }
            else if (msg_type.equalsIgnoreCase("Videochat")) {
                Log.e("callVideochat", "callVideochat");
                // handleNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getClickAction());
                if (getTopAppName().equals(MainActivity.class.getName())) {
                    Intent intent = new Intent();
                    intent.putExtra("roomId", remoteMessage.getData().get("request_id"));
                    intent.putExtra("videoCallEnabled", remoteMessage.getData().get("message"));
                    intent.setAction("com.my.app.onVideoCallReceived");
                    sendBroadcast(intent);
                    sendNotification(remoteMessage.getData().get("roomId"), "clicked");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("roomId", remoteMessage.getData().get("request_id"));
                    intent.putExtra("videoCallEnabled", remoteMessage.getData().get("message"));
                    intent.setAction("com.my.app.onVideoCallReceived");
                    sendBroadcast(intent);
                    sendNotification(remoteMessage.getData().get("roomId"), "clicked");
                }
            } else if (remoteMessage.getData() != null) {
                Log.v(TAG, "From: " + remoteMessage.getFrom());
                Log.v(TAG, "Notification Message Body: " + remoteMessage.getData());
                sendNotification(remoteMessage.getData().get("message"));
            } else {
                sendNotification("test");
                Log.v(TAG, "Notification Message Body: " + remoteMessage.getData());
            }
        }
    }

    private void sendNotification(String messageBody, String action) {
        Intent intent = new Intent(action);
        intent.putExtra("messageBody", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }





    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {

            intent.putExtra("message", messageBody);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // check for orio 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                // String channelId = context.getString(R.string.default_notification_channel_id);
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            }
            assert notificationManager != null;
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }


    }

    public String getTopAppName() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.i("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getShortClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        componentInfo.getPackageName();
        return taskInfo.get(0).topActivity.getClassName();
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        String requestId = remoteMessage.getData().get("request_id");
        sendNotification(getString(R.string.app_name), remoteMessage.getNotification().getBody(), requestId, remoteMessage.getData().get("user_name"));
    }

    private void sendNotification(String notificationTitle, String notificationBody, String requestId, String userName) {
        Intent intent = new Intent(this, UserChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e(TAG, "Notification JSON " + requestId+userName+ notificationBody);
        try {
            String title = notificationTitle;
            String message = notificationBody;
            intent.putExtra("message", message);
            intent.putExtra("request_id", requestId);
            intent.putExtra("User_name", userName);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* TripRequest code */, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // check for orio 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            assert notificationManager != null;
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            return R.drawable.notification_white;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

}
