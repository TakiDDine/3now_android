package de.threenow.FCM;

/**
 * Created by Ajay Tiwari on 26/08/18.
 */

import android.app.ActivityManager;
import android.app.Notification;
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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import de.threenow.Activities.MainActivity;
import de.threenow.Activities.TrackActivity;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;
import de.threenow.chat.UserChatActivity;

public class FCMService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String TAG = "MyFirebaseMsgService";
    String Tag = "FCMService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedHelper.putKey(getApplicationContext(), "device_token", "" + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(Tag, remoteMessage.getData().toString());
        Log.e(Tag + "Image", remoteMessage.getNotification().getImageUrl() + " ");

        String msg_type = remoteMessage.getNotification().getTitle();
        Log.e(Tag, msg_type);

        if (msg_type != null) {
            Log.e(Tag, "!=null");

            if (msg_type.equalsIgnoreCase("chat")) {

                Intent intente = new Intent("msg");    //action: "msg"
                intente.setPackage(getPackageName());
                intente.putExtra("message", remoteMessage.getNotification().getBody());
                getApplicationContext().sendBroadcast(intente);

//                MediaPlayer.create(this, R.raw.come_message).start();
//                startService(new Intent(this, SoundService.class));

                Log.e(Tag, "msg_type == chat");
                Log.e(Tag, getTopAppName() + "");
                if (getTopAppName().equals(UserChatActivity.class.getName())) {

                    Log.e(Tag, "if_UserChatActivity");
                    Intent intent = new Intent();
                    intent.putExtra("message", remoteMessage.getNotification().getBody());
                    intent.setAction("com.my.app.onMessageReceived");
                    sendBroadcast(intent);
                    Log.e(Tag + "message", remoteMessage.getNotification().getImageUrl() + " ");

                } else if (getTopAppName().equals(TrackActivity.class.getName())) {
                    Log.e(Tag, "if_TrackActivity");
                    Intent intent = new Intent();
                    intent.putExtra("message", remoteMessage.getNotification().getBody());
                    intent.setAction("com.my.app.onMessageReceived.TrackActivity");
                    sendBroadcast(intent);
                    Log.e(Tag + "message", remoteMessage.getNotification().getImageUrl() + " ");

                    handleNotification(remoteMessage);

                } else {
                    Log.e(Tag, "else");
                    handleNotification(remoteMessage);
                }

            } else if (msg_type.contains("admin")) {
                String title = remoteMessage.getNotification().getTitle();
                String message = remoteMessage.getNotification().getBody();
                String click_action = "com.quickridejadriver.providers.TARGETNOTIFICATION";
                Intent intent = new Intent(click_action);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                notifiBuilder.setContentTitle(title);
                notifiBuilder.setContentText(message);
                notifiBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notifiBuilder.setAutoCancel(true);
                notifiBuilder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notifiBuilder.build());
            } else if (msg_type.equalsIgnoreCase("Videochat")) {
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
                Log.e(TAG, "From: " + remoteMessage.getFrom());
                Log.e(TAG, "Notification Message Body: " + remoteMessage.getData());
                sendNotification(remoteMessage.getData().get("message"));
            } else {
                sendNotification("test");
                Log.e(TAG, "Notification Message Body: " + remoteMessage.getData());
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

        long[] mVibratePattern = new long[]{0, 400};
        final int[] mAmplitudes = new int[]{0, 128};

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, -1));
        } else { //deprecated in API 26
            vibrator.vibrate(mVibratePattern, -1);
        }

        Intent intent = new Intent(this, UserChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e(TAG, "Notification JSON " + requestId + userName + notificationBody);
        try {
            String title = notificationTitle;
            String message = notificationBody;
            intent.putExtra("message", message);
            intent.putExtra("request_id", requestId);
            intent.putExtra("User_name", userName);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* TripRequest code */, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_message_icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setFullScreenIntent(null, true)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                    .setGroup("Chat_Group_User")
                    .setGroupSummary(false);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE);
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // check for orio 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration(true);
//                notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
//                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);

            }

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
