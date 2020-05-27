package de.threenow;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import de.threenow.Activities.MainActivity;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.Utils.Utilities;

import static de.threenow.IlyftApplication.trimMessage;

public class SendEmailActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView im_back;
    Button submit_btn;
    TextView placeName_tv, email_tv, placeAdresse_tv;

    String name, email, message;

    CustomDialog customDialog;
    Utilities utils;

    @Override
    protected void attachBaseContext(Context base) {
        if (SharedHelper.getKey(base, "lang") != null)
            super.attachBaseContext(LocaleManager.setNewLocale(base, SharedHelper.getKey(base, "lang")));
        else
            super.attachBaseContext(LocaleManager.setNewLocale(base, "de"));


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
//        }
//        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//        if (Build.VERSION.SDK_INT >= 19) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_send_email);

        im_back = findViewById(R.id.im_back);
        submit_btn = findViewById(R.id.submit_btn);

        placeName_tv = findViewById(R.id.placeName);
        email_tv = findViewById(R.id.email);
        placeAdresse_tv = findViewById(R.id.placeAdresse);


        im_back.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

        customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);

        utils = new Utilities();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.im_back:
                finish();
                break;

            case R.id.submit_btn:
//                Toast.makeText(SendEmailActivity.this, "soon..", Toast.LENGTH_SHORT).show();

                name = placeName_tv.getText().toString();
                email = email_tv.getText().toString();
                message = placeAdresse_tv.getText().toString();

                if (name.equals("")) {
                    placeName_tv.setError(getString(R.string.empty_field));
                } else if (email.equals("")) {
                    email_tv.setError(getString(R.string.empty_field));
                } else if (message.equals("")) {
                    placeAdresse_tv.setError(getString(R.string.empty_field));
                } else {
                    sendEmail();
                }

                break;

        } // End switch
    }

    private void sendEmail() {
        customDialog.show();

        JSONObject object = new JSONObject();

        try {

            object.put("name", name);
            object.put("email", email);
            object.put("message", message);
        } catch (Exception e) {
            Log.e("322 object", e.getMessage());
            e.printStackTrace();
        }

        try {
            URLEncoder.encode(URLHelper.SEND_EMAIL_API_NEW, "UTF-8");

        } catch (Exception e) {
            Log.e("322 object", e.getMessage());
            e.printStackTrace();
        }

        Log.e("322 object", object.toString());

        IlyftApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.SEND_EMAIL_API_NEW,
                        object,
                        response -> {
                            if (response != null) {
                                Log.e("322 response", response.toString());

                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                                if (response.toString().contains("error")) {

                                    Toast.makeText(this, response.optString("error"), Toast.LENGTH_LONG).show();

                                } else {
                                    Log.e("322 response else", response.toString());
//                                    Intent intent = new Intent(SendEmailActivity.this, TrackActivity.class);
//                                    intent.putExtra("flowValue", 3);
////                                    startActivity(intent);
//                                    startActivityForResult(intent, 963);

                                    Toast.makeText(this, getString(R.string.sent_email_successfully), Toast.LENGTH_LONG).show();

                                    Intent goMain = new Intent(SendEmailActivity.this, MainActivity.class);
                                    goMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(goMain);
                                    finish();


                                }
                            }
                        }, error -> {
                    try {
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.e("322 log error", errorString);
                        }


                        Log.e("322 error", "data: " + new JSONObject(new String(error.networkResponse.data)));

//                        String msg = "statusCode: " + error.networkResponse.statusCode + "\n" +
//                                URLHelper.SEND_EMAIL_API_NEW + "\n\n" +
//                                trimMessage(new String(error.networkResponse.data));
//
//
//                        utils.showAlert(this, msg);
                    } catch (Exception e) {
//                        if (error.networkResponse != null)
//                            utils.showAlert(this, "statusCode: " + error.networkResponse.statusCode + "\n" +
//                                    URLHelper.SEND_EMAIL_API_NEW);
                    }
                    if (error.networkResponse != null) {
                        Log.e("322 error", "networkTimeMs: " + error.networkResponse.networkTimeMs);
                        Log.e("322 error", "notModified: " + error.networkResponse.notModified);
                        Log.e("322 error", "statusCode: " + error.networkResponse.statusCode);
                    }


                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    Log.e("322 sendrequestresponse", error.toString() + " ");

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            Log.e("322 sendrequestresponse", new String(response.data) + " ");
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showAlert(this, errorObj.optString("error"));
                                } catch (Exception e) {
//                                    utils.showAlert(this, this.getString(R.string.something_went_wrong));         ----------------------
                                }
                            } else if (response.statusCode == 401) {
//                                refreshAccessToken("SEND_REQUEST");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.showAlert(this, json);
                                } else {
                                    utils.showAlert(this, this.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(this, this.getString(R.string.server_down));
                            } else {
                                utils.showAlert(this, this.getString(R.string.please_try_again));
                            }
                        } catch (Exception e) {
//                            utils.showAlert(this, this.getString(R.string.something_went_wrong));         ----------------
                        }
                    } else {
                        utils.showAlert(this, this.getString(R.string.please_try_again));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(SendEmailActivity.this, "token_type") + " " + SharedHelper.getKey(SendEmailActivity.this, "access_token"));
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}