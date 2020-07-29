package de.threenow.Activities;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import de.threenow.Constants.CouponListAdapter;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.GlobalDataMethods;
import de.threenow.Utils.Utilities;

public class CouponActivity extends AppCompatActivity {

    private EditText coupon_et;
    private Button apply_button;
    private String session_token;
    Context context;
    LinearLayout couponListCardView;
    ListView coupon_list_view;
    ArrayList<JSONObject> listItems;
    ListAdapter couponAdapter;
    CustomDialog customDialog;
    Utilities utils = new Utilities();

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
        getWindow().setBackgroundDrawableResource(R.drawable.coupon_bg);
        setContentView(R.layout.activity_coupon);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = CouponActivity.this;
        session_token = SharedHelper.getKey(this, "access_token");
        couponListCardView =  findViewById(R.id.cardListViewLayout);
        coupon_list_view = (ListView) findViewById(R.id.coupon_list_view);
        coupon_et =  findViewById(R.id.coupon_et);
        apply_button =  findViewById(R.id.apply_button);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coupon_et.getText().toString().isEmpty()) {
                    Toast.makeText(CouponActivity.this, getResources().getString(R.string.enter_a_coupon), Toast.LENGTH_SHORT).show();
                } else {
                    sendToServerCoupon();
                }
            }
        });

//        getCoupon();
    }

    private void sendToServerCoupon() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("user_id", SharedHelper.getKey(CouponActivity.this,"id"));
        json.addProperty("coupon", coupon_et.getText().toString());

        Ion.with(this)
                .load(URLHelper.COUPON_VERIFY)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        try {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            // response contains both the headers and the string result
                            if (e != null) {
                                if (e instanceof NetworkErrorException) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                }
                                if (e instanceof TimeoutException) {
                                    sendToServerCoupon();
                                }
                                return;
                            }
                            if (response.getHeaders().code() == 200) {
                                utils.print("AddCouponRes", "" + response.getResult());
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());

                                    if (jsonObject.optString("success").equals("coupon available")) {
                                        GlobalDataMethods.coupon_gd_str = coupon_et.getText().toString();
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);

                                        Toast.makeText(CouponActivity.this, getString(R.string.coupon_added), Toast.LENGTH_SHORT).show();
                                        finish();
//                                        couponListCardView.setVisibility(View.GONE);
//                                        getCoupon();
                                    }

//                                    else if (jsonObject.optString("code").equals("promocode_expired")) {
//                                        Toast.makeText(CouponActivity.this, getString(R.string.expired_coupon), Toast.LENGTH_SHORT).show();
//                                    } else if (jsonObject.optString("code").equals("promocode_already_in_use")) {
//                                        Toast.makeText(CouponActivity.this, getString(R.string.already_in_use_coupon), Toast.LENGTH_SHORT).show();
//                                    }

                                    else {
                                        Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                utils.print("AddCouponErr", "" + response.getResult());
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("SEND_TO_SERVER");
                                } else
                                    Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }

    private void sendToServer() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("promocode", coupon_et.getText().toString());
        Ion.with(this)
                .load(URLHelper.ADD_COUPON_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        try {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            // response contains both the headers and the string result
                            if (e != null) {
                                if (e instanceof NetworkErrorException) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                }
                                if (e instanceof TimeoutException) {
                                    sendToServer();
                                }
                                return;
                            }
                            if (response.getHeaders().code() == 200) {
                                utils.print("AddCouponRes", "" + response.getResult());
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    if (jsonObject.optString("code").equals("promocode_applied")) {
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
//                                        finish();
                                        Toast.makeText(CouponActivity.this, getString(R.string.coupon_added), Toast.LENGTH_SHORT).show();
                                        couponListCardView.setVisibility(View.GONE);
                                        getCoupon();
                                    } else if (jsonObject.optString("code").equals("promocode_expired")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.expired_coupon), Toast.LENGTH_SHORT).show();
                                    } else if (jsonObject.optString("code").equals("promocode_already_in_use")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.already_in_use_coupon), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                utils.print("AddCouponErr", "" + response.getResult());
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("SEND_TO_SERVER");
                                } else
                                Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void refreshAccessToken(final String tag) {

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("SEND_TO_SERVER")) {
                    sendToServerCoupon();
                } else {
//                    getCoupon();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", "false");
                    utils.GoToBeginActivity(CouponActivity.this);
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken(tag);
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

    private void getCoupon() {
        couponListCardView.setVisibility(View.GONE);
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.COUPON_LIST_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            }
                            if (e instanceof TimeoutException) {
                                getCoupon();
                            }
                        } else {
                            if (response != null) {
                                if (response.getHeaders().code() == 200) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response.getResult());
                                        if (jsonArray != null && jsonArray.length() > 0) {
                                            utils.print("CouponActivity", "" + jsonArray.toString());
                                            listItems = getArrayListFromJSONArray(jsonArray);
                                            couponAdapter = new CouponListAdapter(context, R.layout.coupon_list_item, listItems);
                                            coupon_list_view.setAdapter(couponAdapter);
                                            couponListCardView.setVisibility(View.VISIBLE);
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    if ((customDialog != null) && (customDialog.isShowing()))
                                        customDialog.dismiss();
                                    if (response.getHeaders().code() == 401) {
                                        refreshAccessToken("GET_COUPON");
                                    }
                                }
                            } else {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {

        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();

        try {
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    aList.add(jsonArray.getJSONObject(i));

                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return aList;

    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
