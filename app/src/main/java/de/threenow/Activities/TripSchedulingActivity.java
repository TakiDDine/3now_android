package de.threenow.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.Models.PaymentRequest;
import de.threenow.Models.PaymentResponse;
import de.threenow.Models.RestInterface;
import de.threenow.Models.ServiceGenerator;
import de.threenow.R;
import de.threenow.SummeryScheduledActivity;
import de.threenow.Utils.GlobalDataMethods;
import de.threenow.Utils.Utilities;
import de.threenow.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

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
    String scheduledDate = "";
    String scheduledTime = "";
    String childSeat, babySeat, note;
    String nameschield;
    CheckBox nameschieldCheckbox;
    CustomDialog customDialog;
    String serviceId;
    Utilities utils;

    Button btnSheduleRideConfirm;
    EditText noteEditText;
    TextView pickup, to, btnDatePicker, btnTimePicker;
    ImageView im_back, im_swap_location;
    Spinner baby_car_spinner, child_seat_spinner;
    Context context;
    private String paymentId, request_id, Price = "";
    private int mYear, mMonth, mDay, mHour, mMinute;

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

        context = TripSchedulingActivity.this;

        btnSheduleRideConfirm = findViewById(R.id.btnSheduleRideConfirm);
        nameschieldCheckbox = findViewById(R.id.nameschieldCheckbox);
        im_back = findViewById(R.id.im_back);
        im_swap_location = findViewById(R.id.im_swap_location);
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSheduleRideConfirm:
                if (btnTimePicker.getText().toString().matches("[a-zA-Z]+")) {
                    Toast.makeText(TripSchedulingActivity.this, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                } else {
                    customDialog.show();
//                    sendRequest();
                    sendRequestPrice();
//                    goToSummeryScheduledActivity();
                }
                break;

            case R.id.btn_date:
                DatePickerDialogShow();
                break;

            case R.id.btn_time:
//                TimePickerDialogShow();
                DatePickerDialogShow();
                break;

            case R.id.im_swap_location:
                Toast.makeText(TripSchedulingActivity.this, "soon..", Toast.LENGTH_SHORT).show();
                break;

            case R.id.im_back:
                finish();
                break;
        }

    } // End onClick

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TripSchedulingActivity.class.getName(), "222 onActivityResult: " + requestCode + " result code " + resultCode + " ");

        if (requestCode == 0) { // paypal done pay
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("222 paymentExample", confirm.toJSONObject().getJSONObject("response").toString());

 //                     TODO: send 'confirm' to your server for verification.
 //                     see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
 //                     for more detail
                        String paymentType = "PAYPAL";
                        paymentId = confirm.getProofOfPayment().getPaymentId();
                        payNowCard(paymentType);

                    } catch (JSONException e) {
                        Log.e("222 paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }

            // back from TrackActivity
        } else if (requestCode == 963) { // driver done found
            if (resultCode == Activity.RESULT_OK) {

                // pay after confirm trip then found driver
                payNowPaypalOrCard();
//                goToSummeryScheduledActivity();

            }
            // back from SummeryScheduledActivity
        } else if (requestCode == 93) { // summery done show and confirm
            if (resultCode == Activity.RESULT_OK) {
                // search driver after confirm trip
                sendRequest();
//                payNowPaypalOrCard();
            }
        }

    } // End on Activity results

    private void TimePickerDialogShow() {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(TripSchedulingActivity.this, new TimePickerDialog.OnTimeSetListener() {
            int callCount = 0;   //To track number of calls to onTimeSet()

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                if (callCount == 0) {
                    String choosedHour = "";
                    String choosedMinute = "";
                    String choosedTime = "";

                    if (selectedHour < 10) {
                        choosedHour = "0" + selectedHour;
                    } else {
                        choosedHour = "" + selectedHour;
                    }

                    if (selectedMinute < 10) {
                        choosedMinute = "0" + selectedMinute;
                    } else {
                        choosedMinute = "" + selectedMinute;
                    }


                    scheduledTime = choosedHour + ":" + choosedMinute;
                    choosedTime = scheduledTime;

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
                    String choosedDateFormat = dayOfMonth + "-" + ((int) (monthOfYear + 1)) + "-" + year;
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
                    btnDatePicker.setText(choosedDate + "-" + ((int) (monthOfYear + 1)) + "-" + year);
                    TimePickerDialogShow();
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//        datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
        datePickerDialog.show();
    }



    public void sendRequestPrice() {
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
            object.put("kindersitz", Integer.parseInt(childSeat));
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
            URLEncoder.encode(URLHelper.PAY_NOW_SCHEDUL_API, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("222 object", object.toString());

        IlyftApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.PAY_NOW_SCHEDUL_API,
                        object,
                        response -> {
                            if (response != null) {
                                Log.e("222 response", response.toString());

                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                                if (response.toString().contains("error")) {

                                    Toast.makeText(this, response.optString("error"), Toast.LENGTH_LONG).show();

                                } else if (response.optString("price").equals("")) {

                                    if (response.optString("price").length() == 0) {
                                        String msg = response.toString();

                                        if (msg.contains("No Drivers Found"))
                                            msg = getString(R.string.no_drivers_found);

                                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(this, response.optString("price"), Toast.LENGTH_LONG).show();

                                } else {
                                    Price = response.optString("price");
                                    Log.e("222 response", "price: " + Price);

//                                    showDialog();

                                    goToSummeryScheduledActivity();
                                }
                            }
                        }, error -> {
                    try {
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }


                        Log.e("222 error", "data: " + new JSONObject(new String(error.networkResponse.data)));

//                        String msg = "statusCode: " + error.networkResponse.statusCode + "\n" +
//                                URLHelper.PAY_NOW_SCHEDUL_API + "\n\n" +
//                                trimMessage(new String(error.networkResponse.data));
//
//
//                        utils.showAlert(this, msg);
                    } catch (Exception e) {
//                        if (error.networkResponse != null)
//                            utils.showAlert(this, "statusCode: " + error.networkResponse.statusCode + "\n" +
//                                    URLHelper.PAY_NOW_SCHEDUL_API);
                    }
                    if (error.networkResponse != null) {
                        Log.e("222 error", "networkTimeMs: " + error.networkResponse.networkTimeMs);
                        Log.e("222 error", "notModified: " + error.networkResponse.notModified);
                        Log.e("222 error", "statusCode: " + error.networkResponse.statusCode);
                    }


                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    Log.e("222 sendrequestresponse", error.toString() + " ");
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            Log.e("222 sendrequestresponse", new String(response.data) + " ");
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showAlert(this, errorObj.optString("error"));
                                } catch (Exception e) {
                                    Log.e("222 sendrequestresponse", e.getMessage());
//                                    utils.showAlert(this, this.getString(R.string.something_went_wrong));         ----------------------
                                }
                            } else if (response.statusCode == 401) {
                                Log.e("222 sendrequestresponse", "if (response.statusCode == 401)");
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
                        headers.put("Authorization", "" + SharedHelper.getKey(TripSchedulingActivity.this, "token_type") + " " + SharedHelper.getKey(TripSchedulingActivity.this, "access_token"));
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void sendRequest() {
        pdShow();

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
            object.put("kindersitz", Integer.parseInt(childSeat));
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
            URLEncoder.encode(URLHelper.SEND_REQUEST_API_SCHEDULE, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("222 object", object.toString());

        IlyftApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.SEND_REQUEST_API_SCHEDULE,
                        object,
                        response -> {
                            if (response != null) {
                                Log.e("222 response-", response.toString());

                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                                if (response.toString().contains("error")) {

                                    Toast.makeText(this, response.optString("error"), Toast.LENGTH_LONG).show();

                                } else if (response.optString("request_id").equals("")) {

                                    if (response.optString("request_id").length() == 0) {
                                        String msg = response.toString();

                                        if (msg.contains("No Drivers Found"))
                                            msg = getString(R.string.no_drivers_found);

                                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(this, response.optString("request_id"), Toast.LENGTH_LONG).show();

                                } else {
                                    request_id = response.optString("request_id");
                                    Log.e("222 response", "request_id: " + request_id);

                                    SharedHelper.putKey(this, "current_status", "");
                                    SharedHelper.putKey(this, "request_id", "" + request_id);

                                    // نجاح تسجيل الجددولة
                                    // سجل الدفع
                                    Price = response.optString("price") + "";

                                    GlobalDataMethods.newScheduleRequest = true;
                                    Intent intent = new Intent(context, TrackActivity.class);
                                    intent.putExtra("flowValue", 3);
                                    startActivityForResult(intent, 963);

                                }
                            }
                        }, error -> {
                    try {
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }


                        Log.e("222 error", "data: " + new JSONObject(new String(error.networkResponse.data)));


                    } catch (Exception e) {
                        e.printStackTrace();
//                        if (error.networkResponse != null)
//                            utils.showAlert(this, "statusCode: " + error.networkResponse.statusCode + "\n" +
//                                    URLHelper.SEND_REQUEST_Later_API);
                    }
                    if (error.networkResponse != null) {
                        Log.e("222 error", "networkTimeMs: " + error.networkResponse.networkTimeMs);
                        Log.e("222 error", "notModified: " + error.networkResponse.notModified);
                        Log.e("222 error", "statusCode: " + error.networkResponse.statusCode);
                    }


                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    Log.e("222 sendrequestresponse", error.toString() + " ");
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            Log.e("222 sendrequestresponse", new String(response.data) + " ");
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    String ms = errorObj.optString("error");
                                    if (ms.contains("Ride Scheduled")) {
                                        ms = getString(R.string.re_select_time_and_date);
                                    }

                                    utils.showAlert(this, ms);
                                } catch (Exception e) {
//                                    utils.showAlert(this, this.getString(R.string.something_went_wrong));         ----------------------
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
                            e.printStackTrace();
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
                        headers.put("Authorization", "" + SharedHelper.getKey(TripSchedulingActivity.this, "token_type") + " " + SharedHelper.getKey(TripSchedulingActivity.this, "access_token"));
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    void payNowPaypalOrCard() {
        if (payment_mode.equalsIgnoreCase("CARD")) {
            payNowCard("CARD");
        } else if (payment_mode.equalsIgnoreCase("cash")) {
            payNowCard("cash");
        } else {
            Log.e("222 payNowPaypal", "btnPayNowClick: " + Price);

            try {

                PayPalPayment payment = new PayPalPayment(new BigDecimal(Price.replace("€", "")), "EUR", " ",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(TripSchedulingActivity.this, PaymentActivity.class);
                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, GlobalDataMethods.config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                startActivityForResult(intent, 0);
            } catch (Exception e) {
                Log.e("222 payNowPaypal", e.getMessage());
            }
        }

    }

    Handler handleCheckStatus;

    private void goToSummeryScheduledActivity() {

        note = noteEditText.getText().toString();
        if (nameschieldCheckbox.isChecked()) {
            nameschield = "true";
        } else {
            nameschield = "false";
        }

        Intent i = new Intent(getApplicationContext(), SummeryScheduledActivity.class);
        i.putExtra("s_latitude", s_latitude);
        i.putExtra("s_longitude", s_longitude);
        i.putExtra("d_latitude", d_latitude);
        i.putExtra("d_longitude", d_longitude);
        i.putExtra("s_address", s_address);
        i.putExtra("d_address", d_address);
        i.putExtra("distance", distance);

        i.putExtra("payment_mode", payment_mode);
        i.putExtra("service_id", serviceId);

        if (use_wallet != null) {
            i.putExtra("use_wallet", use_wallet);
        }
        if (card_id != null) {
            i.putExtra("card_id", card_id);
        }

//        i.putExtra("service_type", Integer.parseInt(serviceId));
        i.putExtra("service_type", serviceId);
        i.putExtra("schedule_date", scheduledDate);
        i.putExtra("schedule_time", scheduledTime);
        i.putExtra("kindersitz", Integer.parseInt(childSeat));
        i.putExtra("babyschale", Integer.parseInt(babySeat));
        i.putExtra("nameschield", Boolean.parseBoolean(nameschield));
        i.putExtra("note", note);
        i.putExtra("price", Price);


        startActivityForResult(i, 93);
    }

    private static final int AUTO_DISMISS_MILLIS = 12000;

    public void payNowCard(String paymentType) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            Log.e("222", "paymentType: " + paymentType);

            object.put("request_id", request_id + "");
            object.put("total_payment", Price.replace("€", ""));
            object.put("distance", distance + "");
            object.put("payment_mode", paymentType + "");


            if (paymentType.contains("PAYPAL")) {
                object.put("payment_id", paymentId);
            }
//              object.put("payment_mode", SharedHelper.getKey(getApplicationContext(),"payment_mode"));
//              object.put("is_paid", isPaid);
            Log.e("222", "payNowCard: " + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_REQUEST_schedule_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("222 onResponse", "PayNowRequestResponse: " + response.toString());

                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                SharedHelper.putKey(context, "total_amount", "");

                AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(getString(R.string.booking_successful_msg))
                        .setTitle(context.getString(R.string.app_name))
                        .setCancelable(false)
                        .setIcon(R.mipmap.ic_launcher_round).create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {
                        new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                alertDialog.setMessage(getString(R.string.booking_successful_msg)
                                        + "\n\n\t\t"
                                        + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1));
                            }

                            @Override
                            public void onFinish() {
                                if (((AlertDialog) dialog).isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }.start();
                    }
                });

                alertDialog.show();

                handleCheckStatus = new Handler();
                //check status every 3 sec
                handleCheckStatus.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }, AUTO_DISMISS_MILLIS);


            }
        }, error -> {

            try {

                String msg = "statusCode: " + error.networkResponse.statusCode + "\n" +
                        URLHelper.PAY_REQUEST_schedule_API + "\n\n" +
                        trimMessage(new String(error.networkResponse.data));

                utils.showAlert(context, msg);
            } catch (Exception e) {
                if (error.networkResponse != null)
                    utils.showAlert(context, "statusCode: " + error.networkResponse.statusCode + "\n" +
                            URLHelper.PAY_REQUEST_schedule_API);
            }

            try {
                if (error.networkResponse != null)
                    Log.e("222 onResponse", "data: " + new JSONObject(new String(error.networkResponse.data)));
            } catch (Exception e) {

            }
            if (error.networkResponse != null) {
                Log.e("222 onResponse", "networkTimeMs: " + error.networkResponse.networkTimeMs);
                Log.e("222 onResponse", "notModified: " + error.networkResponse.notModified);
                Log.e("222 onResponse", "statusCode: " + error.networkResponse.statusCode);
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json = "";
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            utils.displayMessage(getCurrentFocus(), errorObj.optString("message"));
                        } catch (Exception e) {
//                                utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));    -----------
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken("PAY_NOW");
                        Log.e("222", "refreshAccessToken(PAY_NOW)");
                    } else if (response.statusCode == 422) {

                        json = trimMessage(new String(response.data));
                        if (json != "" && json != null) {
                            utils.displayMessage(getCurrentFocus(), json);
                        } else {
                            utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.please_try_again));
                        }
                    } else if (response.statusCode == 503) {
                        utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.server_down));
                    } else {
                        utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.please_try_again));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    View parentLayout = findViewById(android.R.id.content);
//                        utils.displayMessage(parentLayout, getString(R.string.something_went_wrong)); ------------
                }
            } else {
                try {
                    utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.please_try_again));
                } catch (Exception e) {
                    Toast.makeText(context, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
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

        Log.e("222", "refreshAccessToken" + object.toString());
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.login,
                        object,
                        response -> {
                            utils.print("SignUpResponse", response.toString());
                            Log.e("222", "refreshAccessToken response" + response.toString());

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
                                Log.e("222", "else if PAY_NOW");
                                payNow();
                            }
                        }, error -> {
                    String json = "";
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        Log.e("222", "error.networkResponse" + response.data);
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

    void payNow() {
        Log.e("222", " payNow()");

        String timestamp = Utils.getTimestamp();
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAccountReference("test");
        paymentRequest.setAmount("32");
        paymentRequest.setBusinessShortCode("174379");
        paymentRequest.setCallBackURL("https://spurquoteapp.ga/pusher/pusher.php?title=stk_push&message=result&push_type=individual&regId=null");
        paymentRequest.setPartyA("254700000000");
        paymentRequest.setPartyB("174379");
        paymentRequest.setPassword("MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMTkwMTAxMTkyODQz");
        paymentRequest.setPhoneNumber("254700000000");
        paymentRequest.setTimestamp("20190101192843");
        paymentRequest.setTransactionDesc("test");
        paymentRequest.setTransactionType("CustomerPayBillOnline");

        String auth = "Bearer " + SharedHelper.getKey(context,
                "paymentAccessToken");
        String requestWith = "XMLHttpRequest";
        String CONTENT_TYPE = "application/json";
        Call<PaymentResponse> paymentResponseCall;

        RestInterface restInterface = ServiceGenerator.createService(RestInterface.class);

        paymentResponseCall = restInterface.createSocialLogin(requestWith, CONTENT_TYPE,
                auth, paymentRequest);
        paymentResponseCall.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, retrofit2.Response<PaymentResponse> response) {
                Log.e("222", " payNow()" + response.body() + " " + response.message());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                if (response.code() == 200) {
                    String cId = response.body().getMerchantRequestID();
                    String mId = response.body().getCheckoutRequestID();

                    Log.e("222", " cId: " + cId + " mId: " + mId);


                    payNowBacked(mId, cId);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.not_responding), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Toast.makeText(context, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void payNowBacked(String merchantReqId,
                             String checkOutReqId) {
        Log.e("222", "payNowBacked");

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("m_id", merchantReqId);
            object.put("c_id", checkOutReqId);
//            object.put("total_payment", SharedHelper.getKey(context, "total_amount"));
            //object.put("payment_mode", paymentMode);
            // object.put("is_paid", isPaid);

            Log.e("222", "object" + object.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.PAY_REQUEST_schedule_API,
                        object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("222", "response.toString()" + response.toString());
                                utils.print("PayNowRequestResponse", response.toString());
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                SharedHelper.putKey(context, "total_amount", "");
                               /* flowValue = 6;
                                layoutChanges();*/
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        String json = "";
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                Log.e("222", "response.data " + response.data.toString());

                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                    try {
//                                utils.displayMessage(getCurrentFocus(), errorObj.optString("message"));
                                    } catch (Exception e) {
//                                utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                                    }
                                } else if (response.statusCode == 401) {
                                    refreshAccessToken("PAY_NOW");
                                    Log.e("222", "(response.statusCode == 401) " + " refreshAccessToken");
                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
//                                utils.displayMessage(getCurrentFocus(), json);
                                    } else {
//                                utils.displayMessage(getCurrentFocus(), getString(R.string.please_try_again));
                                    }
                                } else if (response.statusCode == 503) {
//                            utils.displayMessage(getCurrentFocus(), getString(R.string.server_down));
                                } else {
//                            utils.displayMessage(getCurrentFocus(), getString(R.string.please_try_again));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
//                        utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                            }

                        } else {
//                    utils.displayMessage(getCurrentFocus(), getString(R.string.please_try_again));
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type")
                                + " " + SharedHelper.getKey(context, "access_token"));
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


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

    @Override
    public void onDestroy() {
        try {
            handleCheckStatus.removeCallbacksAndMessages(null);
            if (customDialog != null && customDialog.isShowing()) {
                customDialog.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();

    }


    void pdShow() {

        if (!customDialog.isShowing()) {
            customDialog.show();
        }// End if

    }// End pdShow


    void pdHide() {

        if (customDialog.isShowing()) {
            customDialog.dismiss();
        }// End if

    }// End pdHide

}