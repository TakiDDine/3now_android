package de.threenow.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import de.threenow.Helper.ConnectionHelper;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.GlobalDataMethods;
import de.threenow.Utils.Utilities;
import de.threenow.chat.UserChatActivity;

import static de.threenow.IlyftApplication.trimMessage;


public class SplashScreen extends AppCompatActivity {

    String TAG = "SplashActivity";
    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    ConnectionHelper helper;
    Boolean isInternet;
    String device_token, device_UDID;
    Handler handleCheckStatus;
    int retryCount = 0;
    FirebaseAnalytics firebaseAnalytics;

    //    String Consumer_Key = "ojBHda1ppwq9Fdc8lTJ507dNQkfBWAG1";
//    String Consumer_Secret = "Ixy9QVRAnoDrmT1I";
    String keys = "ojBHda1ppwq9Fdc8lTJ507dNQkfBWAG1" + ":" + "Ixy9QVRAnoDrmT1I";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            firebaseAnalytics = FirebaseAnalytics.getInstance(SplashScreen.this);
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            helper = new ConnectionHelper(context);
            isInternet = helper.isConnectingToInternet();
            String base64Key = Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP);
            handleCheckStatus = new Handler();
            //check status every 3 sec
            SharedHelper.putKey(SplashScreen.this, "base64Key", base64Key);


            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase("true")) {

                if (getIntent().getExtras() != null) {
                    try {
                        String msgType = getIntent().getExtras().get("msg_type").toString();
                        String msg = getIntent().getExtras().get("msg").toString();
                        String requestId = getIntent().getExtras().get("request_id").toString();
                        String userName = getIntent().getExtras().get("user_name").toString();
                        Log.v(TAG, "msgType: " + msgType);
                        Log.v(TAG, "msg: " + msg);
                        if (msgType.equalsIgnoreCase("chat")) {
                            Intent intent = new Intent(SplashScreen.this, UserChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("message", msg);
                            intent.putExtra("request_id", requestId);
                            intent.putExtra("User_name", userName);
                            startActivity(intent);
                            finish();
                        } else if (msgType.equalsIgnoreCase("admin")) {
                            startActivity(new Intent(SplashScreen.this, NotificationTab.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    } catch (Exception e) {
                        GetToken();
                        getProfile();
                        e.printStackTrace();
                    }
                } else {
                    GetToken();
                    getProfile();
                }
            } else {
                GoToBeginActivity();

            }
        }, 1500);
        Log.e("printKeyHash", printKeyHash(SplashScreen.this) + "");

    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
//getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

//Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

// String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public void getProfile() {
        if (isInternet) {
            retryCount++;
            String tmp = Utilities.getUtilityInstance().getAppVersion(context);
            final String version = tmp.substring(tmp.indexOf(" ") + 1);
            String full_url = URLHelper.UserProfile + "?device_type=android" +
                    "&device_id=" + device_UDID +
                    "&device_token=" + device_token +
                    "&android_version=" + version;

            GlobalDataMethods.URLGetRate = full_url;

            Log.v("GetProfileAPI", full_url);

            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET, full_url,
                            object,
                            response -> {
                                Log.v("GetProfile", response.toString());
                                SharedHelper.putKey(context, "id", response.optString("id"));
                                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                                SharedHelper.putKey(context, "email", response.optString("email"));
                                SharedHelper.putKey(context, "rating", response.optString("rating").substring(0,4));

                                if (response.optString("picture").startsWith("http"))
                                    SharedHelper.putKey(context, "picture", response.optString("picture"));
                                else
                                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/app/public/" + response.optString("picture"));
                                SharedHelper.putKey(context, "gender", response.optString("gender"));
                                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                                SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                                SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                                if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                                    SharedHelper.putKey(context, "currency", response.optString("currency"));
                                else
                                    SharedHelper.putKey(context, "currency", "€");
                                SharedHelper.putKey(context, "sos", response.optString("sos"));
                                Log.e(TAG, "onResponse: Sos Call" + response.optString("sos"));
                                SharedHelper.putKey(context, "loggedIn", "true");

                                SharedHelper.putKey(context, "card", response.optString("card"));
                                SharedHelper.putKey(context, "paypal", response.optString("paypal"));
                                SharedHelper.putKey(context, "cash", response.optString("cash"));

                                String force_update = response.optString("force_update_version");
                                String last_version = response.optString("current_version");

                                SharedHelper.putKey(context, "force_update", force_update + "");
                                SharedHelper.putKey(context, "last_version", last_version);
                                SharedHelper.putKey(context, "current_version", version);

                                SharedHelper.putKey(context, "GOOGLE_KEY_MAPS", response.optString("google_keys"));
//                                SharedHelper.putKey(context, "GOOGLE_KEY_MAPS", "AIzaSyAVeNdSIO4ZfQ9mgp0nu5ul9wrLi-hjuz");

                                int current_version = Integer.parseInt(version.replace(".", ""));
                                int last_version_int = Integer.parseInt(last_version.replace(".", ""));

                                if (force_update.contains("true") && last_version_int>current_version)
                                    showUpdateDialog();
                                else
                                    getRunningTripList();

                            }, error -> {
                                Log.v("splash_error", error.toString() + "");
                                if (retryCount < 1) {
                                    getProfile();
                                } else {
                                    GoToBeginActivity();
                                }
                                String json = null;
                                String Message;
                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {

                                    try {
                                        JSONObject errorObj = new JSONObject(new String(response.data));
                                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                            try {
                                                displayMessage(errorObj.optString("message"));
                                            } catch (Exception e) {
                                                displayMessage(getString(R.string.something_went_wrong));
                                            }
                                        } else if (response.statusCode == 401) {
                                            refreshAccessToken();
                                        } else if (response.statusCode == 422) {

                                            json = trimMessage(new String(response.data));
                                            if (json != "" && json != null) {
                                                displayMessage(json);
                                            } else {
                                                displayMessage(getString(R.string.please_try_again));
                                            }

                                        } else if (response.statusCode == 503) {
                                            displayMessage(getString(R.string.server_down));
                                        }
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }

                                } else {
                                    if (error instanceof NoConnectionError) {
                                        displayMessage(getString(R.string.oops_connect_your_internet));
                                    } else if (error instanceof NetworkError) {
                                        displayMessage(getString(R.string.oops_connect_your_internet));
                                    } else if (error instanceof TimeoutError) {
                                        getProfile();
                                    }
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                            return headers;
                        }
                    };

            IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            //mProgressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
            builder.setMessage(R.string.check_your_internet).setCancelable(false);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(NetworkAction);

                }
            });
            builder.show();
        }

    }

    Dialog updateDialog;

    void showUpdateDialog() {
        updateDialog = new Dialog(context);
        updateDialog.setContentView(R.layout.update_layout);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.setCancelable(false);

        TextView tv_update = updateDialog.findViewById(R.id.tv_update);
        tv_update.setOnClickListener(view -> {
            final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        updateDialog.show();

    }


    private void getRunningTripList() {

        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URLHelper.CURRENT_TRIP,
                        response -> {
                            Log.e("getOnGoingTrip", response.toString());
                            if (response != null && response.length() > 0) {
                                Intent intent = new Intent(getApplicationContext(), TrackActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.e("Intent", "" + response.toString());
                                intent.putExtra("post_value", response.toString());
                                intent.putExtra("tag", "past_trips");
                                startActivity(intent);
                                activity.finish();
                            } else {
                                GoToMainActivity();
                            }
                        }, error -> {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                    } else {

                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getRunningTripList();
                        }

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context,
                                "token_type") + " " + SharedHelper.getKey(context,
                                "access_token"));
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    @Override
    protected void onDestroy() {
        // handleCheckStatus.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void refreshAccessToken() {
        JSONObject object = new JSONObject();
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.login,
                        object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.v("SignUpResponse", response.toString());
                                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                                getProfile();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;

                        if (response != null && response.data != null) {
                            SharedHelper.putKey(context, "loggedIn", "false");
                            GoToBeginActivity();
                        } else {
                            if (error instanceof NoConnectionError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof NetworkError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof TimeoutError) {
                                refreshAccessToken();
                            }
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    public void GetToken() {


        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") &&
                    SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreen.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        Log.e("newToken", newToken);
                        SharedHelper.putKey(getApplicationContext(), "device_token", "" + newToken);
                        device_token = newToken;

                    }
                });
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }


        try {
            device_UDID = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

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
//    private void showDialog()
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(getString(R.string.connect_to_network))
//                .setCancelable(false)
//                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                    }
//                })
//                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        finish();
//                    }
//                });
//        if(alert == null){
//            alert = builder.create();
//            alert.show();
//        }
//    }



}
