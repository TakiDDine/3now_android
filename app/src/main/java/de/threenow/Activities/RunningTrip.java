package de.threenow.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.snackbar.Snackbar;
import de.threenow.Helper.ConnectionHelper;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.MyBoldTextView;
import de.threenow.Utils.MyTextView;
import de.threenow.Utils.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static de.threenow.IlyftApplication.trimMessage;

public class RunningTrip extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RelativeLayout errorLayout;
    private ConnectionHelper helper;
    private boolean isInternet;
    private CustomDialog customDialog;
    private RunningTripAdapter runningTripAdapter;

    @Override
    protected void attachBaseContext(Context base) {
//        if (SharedPrefrence.getLanguage(base) != null)
//            super.attachBaseContext(LocaleManager.setNewLocale(base, SharedPrefrence.getLanguage(base)));
//        else

        if (SharedHelper.getKey(base, "lang") != null)
            super.attachBaseContext(LocaleManager.setNewLocale(base, SharedHelper.getKey(base, "lang")));
        else
            super.attachBaseContext(LocaleManager.setNewLocale(base, "de"));
        Log.e("language4", Locale.getDefault().getDisplayLanguage());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
//        newConfig.setLayoutDirection(Locale.ENGLISH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_trip);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(getString(R.string.running_track));
        findViewByIdAndInitialize();

        if (isInternet) {
            getRunningTripList();
        } else {
            displayMessage("No Internet Connection");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            //  onBackPressed();
            startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public void findViewByIdAndInitialize() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        errorLayout = (RelativeLayout) findViewById(R.id.errorLayout);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(RunningTrip.this);
        isInternet = helper.isConnectingToInternet();
    }


    Utilities utils = new Utilities();

    private void getRunningTripList() {

        customDialog = new CustomDialog(RunningTrip.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URLHelper.CURRENT_TRIP,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                utils.print("getOnGoingTrip", response.toString());
                                if (response != null && response.length() > 0) {
                                    runningTripAdapter = new RunningTripAdapter(response);
                                    //  recyclerView.setHasFixedSize(true);
                                    RecyclerView.LayoutManager mLayoutManager = new
                                            LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    if (runningTripAdapter != null && runningTripAdapter.getItemCount() > 0) {
                                        errorLayout.setVisibility(View.GONE);
                                        recyclerView.setAdapter(runningTripAdapter);
                                    } else {
                                        errorLayout.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.VISIBLE);
                                }
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        String json = null;
                        String Message;
                        JSONObject errorObj = null;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 || response.statusCode == 405
                                        || response.statusCode == 500) {
                                    try {
                                        displayMessage(errorObj.optString("message"));
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }

                                } else if (response.statusCode == 401) {
                                    refreshAccessToken("CURRENT_TRIPS");
                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        displayMessage(json);
                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }
                                } else if (response.statusCode == 503) {
                                    displayMessage(getString(R.string.server_down));

                                } else {
                                    displayMessage(getString(R.string.please_try_again));

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
                                getRunningTripList();
                            }
                   /* else {
                        displayMessage(errorObj.optString("error"));
                    }*/
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(RunningTrip.this,
                                "token_type") + " " + SharedHelper.getKey(RunningTrip.this,
                                "access_token"));
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    private void refreshAccessToken(final String tag) {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(RunningTrip.this, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(RunningTrip.this, "access_token", response.optString("access_token"));
                SharedHelper.putKey(RunningTrip.this, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(RunningTrip.this, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("PAST_TRIPS")) {
                    getRunningTripList();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
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


    private class RunningTripAdapter extends RecyclerView.Adapter<RunningTripAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public RunningTripAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public RunningTripAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_item, parent, false);
            return new RunningTripAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RunningTripAdapter.MyViewHolder holder, int position) {
            try {
                if (jsonArray.optJSONObject(position) != null) {
                    Picasso.get().load(jsonArray.optJSONObject(position).optString("static_map"))
                            .into(holder.tripImg);
                }

                if (jsonArray.optJSONObject(position).optJSONObject("payment") != null) {
                    holder.tripAmount.setText(SharedHelper.getKey(getApplicationContext(), "currency") + "" + jsonArray.optJSONObject(position).optJSONObject("payment").optString("total"));
                }
                if (jsonArray.optJSONObject(position).optString("booking_id") != null &&
                        !jsonArray.optJSONObject(position).optString("booking_id").equalsIgnoreCase("")) {
                    holder.booking_id.setText(getResources().getString(R.string.booking_id) + ":" + jsonArray.optJSONObject(position).optString("booking_id"));
                }
                if (!jsonArray.optJSONObject(position).optString("assigned_at", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("assigned_at");
                    try {
                        holder.tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + getResources().getString(R.string.at) + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject serviceObj = jsonArray.getJSONObject(position).optJSONObject("service_type");
                if (serviceObj != null) {
                    holder.car_name.setText(serviceObj.optString("name"));
                    Picasso.get()
                            .load(serviceObj.optString("image"))
                            .into(holder.driver_image);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView tripTime, car_name, booking_id;
            MyBoldTextView tripDate, tripAmount;
            ImageView tripImg, driver_image;

            public MyViewHolder(View itemView) {
                super(itemView);
                tripDate = (MyBoldTextView) itemView.findViewById(R.id.tripDate);
                tripTime = (MyTextView) itemView.findViewById(R.id.tripTime);
                tripAmount = (MyBoldTextView) itemView.findViewById(R.id.tripAmount);
                tripImg = itemView.findViewById(R.id.tripImg);
                car_name = (MyTextView) itemView.findViewById(R.id.car_name);
                booking_id = (MyTextView) itemView.findViewById(R.id.booking_id);
                driver_image = itemView.findViewById(R.id.driver_image);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), TrackActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Log.e("Intent", "" + jsonArray.optJSONObject(getAdapterPosition()).toString());
                        intent.putExtra("post_value", jsonArray.optJSONObject(getAdapterPosition()).toString());
                        intent.putExtra("tag", "past_trips");
                        startActivity(intent);
                    }
                });

            }
        }
    }


    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(RunningTrip.this, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.recyclerView), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }


}
