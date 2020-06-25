package de.threenow;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
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

import de.threenow.Activities.Login;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.Models.Driver;
import de.threenow.Utils.MyBoldTextView;

import static de.threenow.IlyftApplication.trimMessage;

public class ViewSummeryScheduledActivity extends AppCompatActivity implements View.OnClickListener {

    String s_latitude;
    String s_longitude;
    String d_latitude;
    String d_longitude;
    //    String distance;
    String card_id;
    String scheduledDate = "";
    String scheduledTime = "";
    String note;
    CustomDialog customDialog;
    String serviceId, service_type, typeCar, serviceCap, bagCap, nameschield;
    int childSeat, babySeat;
    double totalPrice = 0;
    ImageView im_back, tripProviderImg;

    TextView service_car_type, serviceCapacity, AdresseFromD, AdresseToD,
            dateToD, dateToF, childSeatsPrice, babySeatPrice, nameTagPrice,
            detailNote, netlPrice, creditCardNbr, priceTrip, tvDistance,
            bagCapacity, childSeats, babySeats;


    Button btnCancelRide, btnCall;

    Context context;
    String tag = "";
    public JSONObject jsonObject;
    Driver driver;
    String reason = "";
    RatingBar tripProviderRating;
    MyBoldTextView tripProviderName;
    double pr = 0d;

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
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//	View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view_summery_scheduled);

        context = ViewSummeryScheduledActivity.this;

        try {
            Intent intent = getIntent();
            String post_details = intent.getStringExtra("post_value");
            tag = intent.getStringExtra("tag");
            jsonObject = new JSONObject(post_details);
        } catch (Exception e) {
            jsonObject = null;
        }

        if (jsonObject != null) {
            //upcoming_trips
            getUpcomingDetails();
        }

        s_latitude = getIntent().getStringExtra("s_latitude");
        s_longitude = getIntent().getStringExtra("s_longitude");
        d_latitude = getIntent().getStringExtra("d_latitude");
        d_longitude = getIntent().getStringExtra("d_longitude");
        card_id = getIntent().getStringExtra("card_id");
//        serviceId = getIntent().getStringExtra("service_id");

        service_type = getIntent().getStringExtra("service_type");
        note = getIntent().getStringExtra("note");

//        if (serviceId.contains("19")) {
//            typeCar = "Economy Mercedes C/B Klasse";
//            serviceCap = "4";
//            bagCap = "3";
//        } else if (serviceId.contains("27") | serviceId.contains("32")) {
//            typeCar = "Mercedes Vito";
//            serviceCap = "8";
//            bagCap = "6";
//        }
//        else if (serviceId.contains("32")) {
//            typeCar = "Mercedes V-Klasse";
//            serviceCap = "7";
//            bagCap = "7";
//        }

        tripProviderRating = (RatingBar) findViewById(R.id.tripProviderRating);
        tripProviderName = (MyBoldTextView) findViewById(R.id.tripProviderName);
        tripProviderImg = findViewById(R.id.tripProviderImg);
        im_back = findViewById(R.id.im_back);
        service_car_type = findViewById(R.id.service_car_type);
        serviceCapacity = findViewById(R.id.serviceCapacity);
        bagCapacity = findViewById(R.id.bagCapacity);
        AdresseFromD = findViewById(R.id.AdresseFromD);
        AdresseToD = findViewById(R.id.AdresseToD);
        dateToD = findViewById(R.id.dateToD);
        dateToF = findViewById(R.id.dateToF);
        tvDistance = findViewById(R.id.tvDistance);
        childSeatsPrice = findViewById(R.id.childSeatsPrice);
        babySeatPrice = findViewById(R.id.babySeatPrice);
        nameTagPrice = findViewById(R.id.nameTagPrice);
        detailNote = findViewById(R.id.detailNote);
        netlPrice = findViewById(R.id.netlPrice);
        creditCardNbr = findViewById(R.id.creditCardNbr);
        priceTrip = findViewById(R.id.priceTrip);
        btnCancelRide = findViewById(R.id.btnCancelRide);
        btnCall = findViewById(R.id.btnCall);
        childSeats = findViewById(R.id.childSeats);
        babySeats = findViewById(R.id.babySeat);


        im_back.setOnClickListener(this);
        btnCancelRide.setOnClickListener(this);
        btnCall.setOnClickListener(this);

        childSeatsPrice.setText(0 + "€");
        babySeatPrice.setText(0 + "€");
        nameTagPrice.setText(0 + "€");
        detailNote.setText(" . . . ");

        priceTrip.setText(((double) (pr - totalPrice)) + "€");


    }

    private void showreasonDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);

        builder.setIcon(R.mipmap.ic_launcher_round)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);

        submitBtn.setOnClickListener(v -> {
            reason = reasonEtxt.getText().toString();
            cancelRequest();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getUpcomingDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject.optString("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.v("GetPaymentList", response.toString());
                if (response != null && response.length() > 0) {
                    if (response.optJSONObject(0) != null) {
//                        Picasso.get().load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
//                        paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                        String form = response.optJSONObject(0).optString("schedule_at");
                        JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
//                        if (response.optJSONObject(0).optString("booking_id") != null &&
//                                !response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
//                            booking_id.setText(response.optJSONObject(0).optString("booking_id"));
//                        }

                        serviceId = response.optJSONObject(0).optString("service_type_id");
                        if (serviceId.contains("19")) {
                            typeCar = "Mercedes Vito";
                            serviceCap = "8";
                            bagCap = "6";
                        } else if (serviceId.contains("27")) {
                            typeCar = "Mercedes V-Klasse";
                            serviceCap = "7";
                            bagCap = "7";
                        }

                        service_car_type.setText(typeCar.replace("Economy Mercedes C/B Klasse", "Economy\nMercedes C/B Klasse") + "");
                        serviceCapacity.setText(serviceCap + " Maximal");
                        bagCapacity.setText(bagCap + "");

                        tvDistance.setText(response.optJSONObject(0).optString("distance") + " km");

                        try {
                            childSeat = Integer.parseInt(response.optJSONObject(0).optString("kindersitz"));
                            babySeat = Integer.parseInt(response.optJSONObject(0).optString("babyschale"));
                            nameschield = response.optJSONObject(0).optString("nameschield");

                            if (childSeat > 1) {
                                totalPrice += (childSeat - 1) * 5;
                                childSeatsPrice.setText((5 * (childSeat - 1)) + "€");
                            }

                            childSeats.setText(getString(R.string.plus1) + " (" + childSeat + ")");
                            babySeats.setText(getString(R.string.plus2) + " (" + babySeat + ")");


                            if (babySeat > 1) {
                                totalPrice += 10 * (babySeat - 1);
                                babySeatPrice.setText((10 * (babySeat - 1)) + "€");
                            }

                            if (nameschield.contains("1")) {
                                totalPrice += 15;
                                nameTagPrice.setText(15 + "€");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (providerObj != null) {
                            driver = new Driver();
                            driver.setFname(providerObj.optString("first_name"));
                            driver.setLname(providerObj.optString("last_name"));
                            driver.setMobile(providerObj.optString("mobile"));
                            driver.setEmail(providerObj.optString("email"));
                            driver.setImg(providerObj.optString("avatar"));
                            driver.setRating(providerObj.optString("rating"));
                        }
                        try {
//                            tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
                            dateToD.setText(getDate(form) + "-" + getMonth(form) + "-" + getYear(form));
                            dateToF.setText(getTime(form));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                        if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
//                            paymentTypeImg.setImageResource(R.drawable.money_icon);
//                        } else {
//                            paymentTypeImg.setImageResource(R.drawable.visa);
//                        }

                        if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("PAYPAL")) {
                            creditCardNbr.setText("");
                            creditCardNbr.setCompoundDrawablesWithIntrinsicBounds((R.drawable.cio_paypal_logo), 0, 0, 0);
                        } else if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("card")) {
                            creditCardNbr.setText("");
                            creditCardNbr.setCompoundDrawablesWithIntrinsicBounds((R.drawable.visa), 0, 0, 0);
                        } else {
                            creditCardNbr.setText("");
                            creditCardNbr.setCompoundDrawablesWithIntrinsicBounds((R.drawable.ic_cash_txt), 0, 0, 0);
                        }

                        if (response.optJSONObject(0).optJSONObject("provider").optString("avatar") != null)
                            Picasso.get().load(URLHelper.base + "storage/app/public/" + response.optJSONObject(0).optJSONObject("provider").optString("avatar"))
                                    .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(tripProviderImg);

                        tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("provider").optString("rating")));
                        tripProviderName.setText(response.optJSONObject(0).optJSONObject("provider").optString("first_name"));
                        if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
//                            sourceAndDestinationLayout.setVisibility(View.GONE);
                            // viewLayout.setVisibility(View.GONE);
                        } else {
                            AdresseFromD.setText(response.optJSONObject(0).optString("s_address"));
                            AdresseToD.setText(response.optJSONObject(0).optString("d_address"));
                        }

                        note = response.optJSONObject(0).optString("note") + "";
                        Log.e("note_chk", note);
                        if (note != null && note.length() > 0 && !note.equalsIgnoreCase("null")) {
                            detailNote.setText(note);
                        }


                        try {
                            JSONObject serviceObj = response.optJSONObject(0).optJSONObject("payment");
                            if (serviceObj != null) {
//                            holder.car_name.setText(serviceObj.optString("name"));
//                                if (tag.equalsIgnoreCase("past_trips")) {
                                netlPrice.setText(serviceObj.optString("total") + "€");
                                pr = Double.parseDouble(serviceObj.optString("total"));
//                                } else {
//                                    netlPrice.setVisibility(View.GONE);
//                                }
//                                Picasso.get().load(serviceObj.optString("image"))
//                                        .placeholder(R.drawable.loading).error(R.drawable.loading)
//                                        .into(tripProviderImg);
                                priceTrip.setText(((double) (pr - totalPrice)) + "€");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
//                    parentLayout.setVisibility(View.VISIBLE);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
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
                            refreshAccessToken("UPCOMING_TRIPS");
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
                    displayMessage(getString(R.string.please_try_again));

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

        IlyftApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MM").format(cal.getTime());
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
        String timeName = new SimpleDateFormat("hh:mm").format(cal.getTime());
        return timeName;
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
//                if (tag.equalsIgnoreCase("PAST_TRIPS")) {
//                    getRequestDetails();
//                } else
                if (tag.equalsIgnoreCase("UPCOMING_TRIPS")) {
                    getUpcomingDetails();
                } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                    cancelRequest();
                }
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

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(ViewSummeryScheduledActivity.this, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        ViewSummeryScheduledActivity.this.finish();
    }

    public void cancelRequest() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", jsonObject.optString("id"));
            object.put("cancel_reason", reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("CancelRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
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
                            refreshAccessToken("CANCEL_REQUEST");
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
                    displayMessage(getString(R.string.please_try_again));
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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancelRide:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_launcher_round)
                        .setTitle(R.string.app_name)
                        .setMessage(getString(R.string.cencel_request))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                showreasonDialog();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.im_back:
                finish();
                break;

            case R.id.btnCall:
                Log.e("btn_call", "btn click");
                if (driver.getMobile() != null && !driver.getMobile().equalsIgnoreCase("null") && driver.getMobile().length() > 0) {
                    Log.e("btn_call", "first if ok");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.e("btn_call", "second if ok");
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                    } else {
                        Log.e("btn_call", "second if else");
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + driver.getMobile()));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Log.e("btn_call", "three if else");
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        Log.e("btn_call", "before startActivity");
                        startActivity(intentCall);
                    }
                } else {
                    displayMessage(getString(R.string.user_no_mobile));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted
            //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + driver.getMobile()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
