package de.threenow.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.Utilities;

import static de.threenow.IlyftApplication.trimMessage;

public class TripSchedulingActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    String s_latitude;
    String s_longitude;
    String d_latitude;
    String d_longitude;
    String s_address;
    String d_address;
    String distance;
    String use_wallet;
    String payment_mode;
    String card_id;
    String token_type;
    String scheduledDate = "";
    String scheduledTime = "";
    String access_token;
    String childSeat, babySeat, note;
    String nameschield;
    CheckBox nameschieldCheckbox;
    CustomDialog customDialog;
    String serviceId;
    Utilities utils;

    Button btnSheduleRideConfirm;
    EditText kindersitzEdittext, babyschaleEdittext, noteEditText;
    TextView pickup, to, btnDatePicker, btnTimePicker;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ImageView im_back, im_swap_location;
    Spinner baby_car_spinner, child_seat_spinner;


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.parseColor("#2B83D9"));
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_trip_scheduling);


        btnSheduleRideConfirm = findViewById(R.id.btnSheduleRideConfirm);
        nameschieldCheckbox = findViewById(R.id.nameschieldCheckbox);
        im_back = findViewById(R.id.im_back);
        im_swap_location = findViewById(R.id.im_swap_location);

        //        kindersitzEdittext = findViewById(R.id.kindersitzEdittext);
//        babyschaleEdittext = findViewById(R.id.babyschaleEdittext);
        btnDatePicker = findViewById(R.id.btn_date);
        btnTimePicker = findViewById(R.id.btn_time);
        pickup = findViewById(R.id.pickup);
        to = findViewById(R.id.to);
        noteEditText = findViewById(R.id.note);
        baby_car_spinner = findViewById(R.id.baby_car_spinner);
        child_seat_spinner = findViewById(R.id.child_seat_spinner);

        btnSheduleRideConfirm.setOnClickListener(this);
        im_back.setOnClickListener(this);
        im_swap_location.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        baby_car_spinner.setOnItemSelectedListener(this);
        child_seat_spinner.setOnItemSelectedListener(this);

        s_latitude = getIntent().getStringExtra("s_latitude");
        s_longitude = getIntent().getStringExtra("s_longitude");
        d_latitude = getIntent().getStringExtra("d_latitude");
        d_longitude = getIntent().getStringExtra("d_longitude");
        s_address = getIntent().getStringExtra("s_address");
        d_address = getIntent().getStringExtra("d_address");
        distance = getIntent().getStringExtra("distance");
        use_wallet = getIntent().getStringExtra("use_wallet");
        payment_mode = getIntent().getStringExtra("payment_mode");
        card_id = getIntent().getStringExtra("card_id");
        serviceId = getIntent().getStringExtra("service_id");


        utils = new Utilities();
        customDialog = new CustomDialog(this);


        btnDatePicker.setText(getString(R.string.sample_date));
        btnTimePicker.setText(getString(R.string.time));
        pickup.setText(s_address);
        to.setText(d_address);
        note = noteEditText.getText().toString();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        baby_car_spinner.setAdapter(adapter);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterChildSeat = ArrayAdapter.createFromResource(this,
                R.array.planets_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterChildSeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        child_seat_spinner.setAdapter(adapterChildSeat);

        childSeat = "0"; //kindersitzEdittext
        babySeat = "0"; //babyschaleEdittext

    }

    private void TimePickerDialogShow() {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(TripSchedulingActivity.this, new TimePickerDialog.OnTimeSetListener() {
            int callCount = 0;   //To track number of calls to onTimeSet()

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                if (callCount == 0) {
                    String choosedHour = "";
                    String choosedMinute = "";
                    String choosedTimeZone = "";
                    String choosedTime = "";

                    scheduledTime = selectedHour + ":" + selectedMinute;

                    if (selectedHour > 12) {
                        choosedTimeZone = "PM";
                        selectedHour = selectedHour - 12;
                        if (selectedHour < 10) {
                            choosedHour = "0" + selectedHour;
                        } else {
                            choosedHour = "" + selectedHour;
                        }
                    } else {
                        choosedTimeZone = "AM";
                        if (selectedHour < 10) {
                            choosedHour = "0" + selectedHour;
                        } else {
                            choosedHour = "" + selectedHour;
                        }
                    }

                    if (selectedMinute < 10) {
                        choosedMinute = "0" + selectedMinute;
                    } else {
                        choosedMinute = "" + selectedMinute;
                    }
                    choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                    if (scheduledDate != "" && scheduledTime != "") {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        if (!DateUtils.isToday(milliseconds)) {
                            btnTimePicker.setText(choosedTime);
                        } else {
                            if (utils.checktimings(scheduledTime)) {
                                btnTimePicker.setText(choosedTime);
                            } else {
                                Toast toast = new Toast(TripSchedulingActivity.this);
                                toast.makeText(TripSchedulingActivity.this, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(TripSchedulingActivity.this, R.string.date_firest_error, Toast.LENGTH_SHORT).show();
                    }
                }
                callCount++;
            }
        }, mHour, mMinute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void DatePickerDialogShow() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(TripSchedulingActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    // set day of month , month and year value in the edit text
                    String choosedMonth = "";
                    String choosedDate = "";
                    String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    scheduledDate = choosedDateFormat;
                    try {
                        choosedMonth = utils.getMonth(choosedDateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (dayOfMonth < 10) {
                        choosedDate = "0" + dayOfMonth;
                    } else {
                        choosedDate = "" + dayOfMonth;
                    }
                    btnDatePicker.setText(choosedDate + " " + choosedMonth + " " + year);
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
        datePickerDialog.show();
    }


    public void sendRequest() {
        note = noteEditText.getText().toString();
        if (nameschieldCheckbox.isChecked()) {
            nameschield = "true";
        } else {
            nameschield = "false";
        }

        JSONObject object = new JSONObject();
        try {
            object.put("s_latitude", Double.parseDouble(s_latitude));
            object.put("s_longitude", Double.parseDouble(s_longitude));
            object.put("d_latitude", Double.parseDouble(d_latitude));
            object.put("d_longitude", Double.parseDouble(d_longitude));
            object.put("s_address", s_address);
            object.put("d_address", d_address);
            object.put("service_type", Integer.parseInt(serviceId));
            object.put("distance", Integer.parseInt(distance));
            object.put("schedule_date", scheduledDate);
            object.put("schedule_time", scheduledTime);
            object.put("payment_mode", payment_mode);
            object.put("kindersitz ", Integer.parseInt(childSeat));
            object.put("babyschale", Integer.parseInt(babySeat));
            object.put("nameschield", Boolean.parseBoolean(nameschield));
            object.put("note", note);
            if (card_id != null) {
                object.put("card_id", card_id);
            }
            if (use_wallet != null) {
                object.put("use_wallet", use_wallet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            URLEncoder.encode(URLHelper.SEND_REQUEST_Later_API, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("111 object", object.toString());

        IlyftApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.SEND_REQUEST_Later_API,
                        object,
                        response -> {
                            if (response != null) {
                                utils.print("SendRequestResponse", response.toString());
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                                if (response.toString().contains("error")) {

                                    Toast.makeText(this, response.optString("error") , Toast.LENGTH_LONG).show();

                                } else if (response.optString("request_id").equals("")) {

                                    if (response.optString("request_id").length() == 0) {
                                        String msg = response.toString();

                                        if (msg.contains("No Drivers Found"))
                                            msg = getString(R.string.no_drivers_found);

                                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(this, response.optString("request_id"), Toast.LENGTH_LONG).show();

                                } else {
                                    SharedHelper.putKey(this, "current_status", "");
                                    SharedHelper.putKey(this, "request_id", "" + response.optString("request_id"));

                                    // flowValue = 3;
                                    //layoutChanges();


                                    Intent intent = new Intent(this, TrackActivity.class);
                                    intent.putExtra("flowValue", 3);
                                    startActivity(intent);
                                }
                            }
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    Log.e("sendrequestresponse", error.toString() + " ");
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showAlert(this, errorObj.optString("error"));
                                } catch (Exception e) {
                                    utils.showAlert(this, this.getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SEND_REQUEST");
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
                            utils.showAlert(this, this.getString(R.string.something_went_wrong));
                        }
                    } else {
                        utils.showAlert(this, this.getString(R.string.please_try_again));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(TripSchedulingActivity.this, "token_type") + " " + SharedHelper.getKey(TripSchedulingActivity.this, "access_token"));
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    private void refreshAccessToken(final String tag) {
        JSONObject object = new JSONObject();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedHelper.putKey(getApplicationContext(), "device_token", "" + refreshedToken);
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(this, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.login,
                        object,
                        response -> {
                            utils.print("SignUpResponse", response.toString());
                            SharedHelper.putKey(this, "access_token", response.optString("access_token"));
                            SharedHelper.putKey(this, "refresh_token", response.optString("refresh_token"));
                            SharedHelper.putKey(this, "token_type", response.optString("token_type"));
                            if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                                // getServiceList();
                            } else if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
                                // getApproximateFare();
                            } else if (tag.equalsIgnoreCase("SEND_REQUEST")) {
                                sendRequest();
                            } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                                // cancelRequest();
                            } else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                                // getProvidersList("");
                            } else if (tag.equalsIgnoreCase("SUBMIT_REVIEW")) {
                                // submitReviewCall();
                            } else if (tag.equalsIgnoreCase("PAY_NOW")) {
                                // payNow();
                            }
                        }, error -> {
                    String json = "";
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        Activity activity = this;
                        if (activity != null) {
                            SharedHelper.putKey(this, "loggedIn", "false");
                            utils.GoToBeginActivity(this);
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSheduleRideConfirm:
                if (btnTimePicker.getText().toString().matches("[a-zA-Z]+")) {
                    Toast.makeText(TripSchedulingActivity.this, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                } else {
                    sendRequest();
                }
                break;

            case R.id.btn_date:
                DatePickerDialogShow();
                break;

            case R.id.btn_time:
                TimePickerDialogShow();
                break;

            case R.id.im_swap_location:
                Toast.makeText(TripSchedulingActivity.this, "soon..", Toast.LENGTH_SHORT).show();
                break;

            case R.id.im_back:
                finish();
                break;
        }

    } // End onClick

    // spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Log.e("111 onItemSelected", R.id.baby_car_spinner + " "+ adapterView.getId() + " i: " + i + " l: " + l );
        switch (adapterView.getId()) {
            case R.id.baby_car_spinner:
                Log.e("111 babySeat", i + "");
                babySeat = i + ""; //babyschaleEdittext
                break;

            case R.id.child_seat_spinner:
                Log.e("111 childSeat", i + "");
                childSeat = i + "";//kindersitzEdittext
                break;

        } // End switch

    } // End onItemSelected

    // spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.e("111 onNothingSelected", adapterView.getId() + "");

//        switch(adapterView.getId()) {
//        }

    } // End onItemSelected

}