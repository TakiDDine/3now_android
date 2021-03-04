package de.threenow.FCM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.threenow.R;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("msg_type") && intent.getStringExtra("msg_type").contains("chat")) {
            Log.e("onMR_chat_revived", "msg_type == chat");
            CustomMediaPlayer.RunRaining("message", context);

//            Log.e("intent_URI", intent.toUri(0));
//
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                for (String key : bundle.keySet()) {
//                    Log.e("onMR_chat_revived", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
//                }
//            }
//
//            String body = bundle.getString("gcm.notification.body");
//            Log.e("onMR_chat_body", body);
//
////             Get the custom layout view.
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            View toastView = inflater.inflate(R.layout.activity_toast_custom_view, null);
//            TextView customToastText = toastView.findViewById(R.id.customToastText);
//            customToastText.setText(body);
//            // Initiate the Toast instance.
//            Toast toast = new Toast(context);
//            // Set custom view in toast.
//            toast.setView(toastView);
//            toast.setDuration(Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.TOP, 0, 100);
//            toast.show();

//            Toast.makeText(context, body + " ..", Toast.LENGTH_SHORT).show();
        }
    }
}
