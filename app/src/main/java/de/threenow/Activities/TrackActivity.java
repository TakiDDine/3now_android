package de.threenow.Activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import de.threenow.Helper.ConnectionHelper;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.DataParser;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.Models.CardInfo;
import de.threenow.Models.Driver;
import de.threenow.Models.GoogleDirectionModel;
import de.threenow.Models.PaymentRequest;
import de.threenow.Models.PaymentResponse;
import de.threenow.Models.RestInterface;
import de.threenow.Models.ServiceGenerator;
import de.threenow.R;
import de.threenow.Utils.GlobalDataMethods;
import de.threenow.Utils.ResponseListener;
import de.threenow.Utils.Utilities;
import de.threenow.Utils.Utils;
import de.threenow.chat.UserChatActivity;
import retrofit2.Call;
import retrofit2.Callback;

import static de.threenow.IlyftApplication.trimMessage;
import static de.threenow.Utils.GlobalDataMethods.coupon_discount_str;
import static de.threenow.Utils.GlobalDataMethods.coupon_gd_str;


public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResponseListener, GoogleMap.OnCameraMoveListener {

    private static final String TAG = "TrackActivity";
    private static final int REQUEST_LOCATION = 1450;
    private static final int PAY_NOW_AUTO_REQUEST_CODE = 667;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    public static double current_lngSend, current_latSend;
    public String PreviousStatus = "";
    public String CurrentStatus = "";

    @BindView(R.id.layoutdriverstatus)
    LinearLayout layoutdriverstatus;
    @BindView(R.id.driveraccepted)
    LinearLayout driveraccepted;
    @BindView(R.id.txtdriveraccpted)
    TextView txtdriveraccpted;
    @BindView(R.id.driverArrived)
    LinearLayout driverArrived;
    @BindView(R.id.txtdriverArrived)
    TextView txtdriverArrived;
    @BindView(R.id.imgarrived)
    ImageView imgarrived;
    @BindView(R.id.driverPicked)
    LinearLayout driverPicked;
    @BindView(R.id.txtdriverpicked)
    TextView txtdriverpicked;
    @BindView(R.id.imgPicked)
    ImageView imgPicked;
    @BindView(R.id.driverCompleted)
    LinearLayout driverCompleted;
    @BindView(R.id.txtdrivercompleted)
    TextView txtdrivercompleted;
    @BindView(R.id.imgDropped)
    ImageView imgDropped;

    @BindView(R.id.lnrFlow)
    LinearLayout lnrFlow;

    @BindView(R.id.promoLayout)
    LinearLayout promoLayout;
    @BindView(R.id.lblDistancePrice)
    TextView lblDistancePrice;

    @BindView(R.id.txtDiscount)
    TextView txtDiscount;
    TextView lblPaymentChange;
    String pickUpLocationName, dropLocationName;


    /// source dest layout
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgSos)
    ImageView imgSos;
    @BindView(R.id.imgShareRide)
    ImageView imgShareRide;
    @BindView(R.id.mapfocus)
    ImageView mapfocus;
    @BindView(R.id.shadowBack)
    ImageView shadowBack;
    @BindView(R.id.lnrWaitingForProviders)
    RelativeLayout lnrWaitingForProviders;
    @BindView(R.id.lblNoMatch)
    TextView lblNoMatch;
    @BindView(R.id.btnCancelRide)
    Button btnCancelRide;

    //// Waiting for provider layout
    @BindView(R.id.lnrProviderAccepted)
    LinearLayout lnrProviderAccepted;

    @BindView(R.id.AfterAcceptButtonLayout)
    LinearLayout AfterAcceptButtonLayout;
    @BindView(R.id.imgProvider)
    CircleImageView imgProvider;
    @BindView(R.id.imgServiceRequested)
    ImageView imgServiceRequested;

    /// Driver Accepted
    @BindView(R.id.lblProvider)
    TextView lblProvider;
    @BindView(R.id.tvServiceNumber)
    TextView tvServiceNumber;
    @BindView(R.id.tvServiceModel)
    TextView tvServiceModel;


    @BindView(R.id.lblSurgePrice)
    TextView lblSurgePrice;
    @BindView(R.id.lblServiceRequested)
    TextView lblServiceRequested;
    @BindView(R.id.lblModelNumber)
    TextView lblModelNumber;
    @BindView(R.id.ratingProvider)
    RatingBar ratingProvider;
    @BindView(R.id.btnCancelTrip)
    Button btnCancelTrip;

    /// Invoice layout
    @BindView(R.id.lnrInvoice)
    LinearLayout lnrInvoice;
    @BindView(R.id.lblBasePrice)
    TextView lblBasePrice;
    @BindView(R.id.lblExtraPrice)
    TextView lblExtraPrice;
    @BindView(R.id.lblTaxPrice)
    TextView lblTaxPrice;
    @BindView(R.id.lblTotalPrice)
    TextView lblTotalPrice;
    @BindView(R.id.lblPaymentTypeInvoice)
    TextView lblPaymentTypeInvoice;
    @BindView(R.id.imgPaymentTypeInvoice)
    ImageView imgPaymentTypeInvoice;
    @BindView(R.id.btnPayNow)
    Button btnPayNow;
    @BindView(R.id.btnPayNowCash)
    Button btnPayNowCash;

    @BindView(R.id.btn_trips_1)
    Button btn_trips_1;
    @BindView(R.id.btn_trips_2)
    Button btn_trips_2;
    @BindView(R.id.btn_trips_3)
    Button btn_trips_3;

    //// Rate provider
    @BindView(R.id.lnrRateProvider)
    LinearLayout lnrRateProvider;
    @BindView(R.id.lblProviderName)
    TextView lblProviderName;
    @BindView(R.id.imgProviderRate)
    CircleImageView imgProviderRate;
    @BindView(R.id.txtComments)
    EditText txtComments;
    @BindView(R.id.otherTips)
    EditText otherTips;
    @BindView(R.id.ratingProviderRate)
    RatingBar ratingProviderRate;
    @BindView(R.id.btnSubmitReview)
    Button btnSubmitReview;

    @BindView(R.id.rtlStaticMarker)
    RelativeLayout rtlStaticMarker;
    @BindView(R.id.imgDestination)
    ImageView imgDestination;
    @BindView(R.id.btnDone)
    Button btnDone;
    @BindView(R.id.booking_id)
    TextView booking_id;
    @BindView(R.id.tvPaymentLabel)
    TextView tvPaymentLabel;

    @BindView(R.id.btnCall)
    ImageButton btnCall;
    @BindView(R.id.btnChat)
    ImageButton btnChat;
    @BindView(R.id.new_message_tv)
    TextView new_message_tv;
    String current_chat_requestID = "";
    String currentProviderID = "";
    String userID = "";
    String providerFirstName = "";
    String providerLastName = "";


    // trinkeld
    @BindView(R.id.oneTrink)
    TextView oneTrink;
    @BindView(R.id.secondTrink)
    TextView secondTrink;
    @BindView(R.id.threedTrink)
    TextView threedTrink;
    @BindView(R.id.fiveTrink)
    TextView fiveTrink;
    @BindView(R.id.zeroTrink)
    TextView zeroTrink;

    String priceTrink = "";
    private String totalConfirmPayment;

    @butterknife.OnClick(R.id.oneTrink)
    void oneTrinkbtnCall() {
        changeColorButtonRadioClick(1);
        priceTrink = "1";
    }

    @butterknife.OnClick(R.id.secondTrink)
    void secondTrinkbtnCall() {
        changeColorButtonRadioClick(2);
        priceTrink = "2";
    }

    @butterknife.OnClick(R.id.threedTrink)
    void threedTrinkbtnCall() {
        changeColorButtonRadioClick(3);
        priceTrink = "3";
    }

    @butterknife.OnClick(R.id.fiveTrink)
    void fiveTrinkbtnCall() {
        changeColorButtonRadioClick(4);
        priceTrink = "5";
    }

    @butterknife.OnClick(R.id.zeroTrink)
    void zeroTrinkbtnCall() {
        changeColorButtonRadioClick(5);
        priceTrink = "0";
    }


    private void changeColorButtonRadioClick(int j) {
        TextView[] textViewArray = {oneTrink, secondTrink, threedTrink, fiveTrink, zeroTrink};

        for (int i = 0; i < textViewArray.length; i++) {
            if (i == j - 1) {
                textViewArray[i].setTextColor(getResources().getColor(R.color.white));
                textViewArray[i].setBackgroundResource(R.drawable.radio_click);
            } else {
                textViewArray[i].setTextColor(getResources().getColor(R.color.black));
                textViewArray[i].setBackgroundResource(R.drawable.radio_not_click);
            }
        }

    }

    @butterknife.OnClick(R.id.btnCall)
    void callbtnCall() {
        Intent intentCall = new Intent(Intent.ACTION_DIAL);
        intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
        startActivity(intentCall);

    }

    @butterknife.OnClick(R.id.btnChat)
    void callbtnChat() {
        new_message_tv.setVisibility(View.GONE);
        Intent intentChat = new Intent(context, UserChatActivity.class);
        intentChat.putExtra("requestId", current_chat_requestID);
        intentChat.putExtra("providerId", currentProviderID);
        intentChat.putExtra("userId", userID);
        intentChat.putExtra("userName", providerFirstName);
        context.startActivity(intentChat);
    }

    @butterknife.OnClick(R.id.lnrWaitingForProviders)
    void lnrWaitingForProvidersClick() {

    }

    @butterknife.OnClick(R.id.lnrRateProvider)
    void lnrRateProviderClick() {

    }

    @butterknife.OnClick(R.id.lnrInvoice)
    void lnrInvoiceClick() {

    }

    @butterknife.OnClick(R.id.lnrProviderAccepted)
    void lnrProviderAcceptedClick() {

    }

    @butterknife.OnClick(R.id.imgShareRide)
    void imgShareRideClick() {
        String url = "http://maps.google.com/maps?q=loc:";
        navigateToShareScreen(url);
    }

    @butterknife.OnClick(R.id.imgSos)
    void imgSosClick() {
        showSosPopUp();
    }

    @butterknife.OnClick(R.id.imgProviderRate)
    void imgProviderRateClick() {
        Intent intent4 = new Intent(activity, ShowProfile.class);
        intent4.putExtra("driver", driver);
        startActivity(intent4);
    }

    @butterknife.OnClick(R.id.imgProvider)
    void imgProviderClick() {
        Intent intent1 = new Intent(activity, ShowProfile.class);
        intent1.putExtra("driver", driver);
        startActivity(intent1);
    }

    @butterknife.OnClick(R.id.mapfocus)
    void mapfocusClick() {
        Double crtLat, crtLng;
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            crtLat = Double.parseDouble(current_lat);
            crtLng = Double.parseDouble(current_lng);

            if (crtLat != null && crtLng != null) {
                LatLng loc = new LatLng(crtLat, crtLng);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mapfocus.setVisibility(View.INVISIBLE);
            }
        }
    }

    @butterknife.OnClick(R.id.btnDone)
    void btnDoneClick() {

    }

    @butterknife.OnClick(R.id.btnSubmitReview)
    void btnSubmitReviewClick() {
        submitReviewCall();
    }


    @butterknife.OnClick(R.id.btn_trips_1)
    void btn_tips_1_Click() {
        if (btn_trips_1.getTextColors().getDefaultColor() == ContextCompat.getColor(context, R.color.white)) {
            Log.e("tips", "white");
            btn_trips_1.setTextColor(ContextCompat.getColor(context, R.color.quantum_googgreenA400));
            btn_trips_1.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge_green));

            btn_trips_2.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_2.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

            btn_trips_3.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_3.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

            previousTips = "0.0";
            otherTips.setText("");

            lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + (Double.parseDouble(totalConfirmPayment) + 1) + "");


        } else {
            Log.e("tips", "green");
            btn_trips_1.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_1.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));
            lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                    + totalConfirmPayment);
        }

        AddTips();
    }

    @butterknife.OnClick(R.id.btn_trips_2)
    void setBtn_trips_2_Click() {
        if (btn_trips_2.getTextColors().getDefaultColor() == ContextCompat.getColor(context, R.color.white)) {
            Log.e("tips", "white");
            btn_trips_2.setTextColor(ContextCompat.getColor(context, R.color.quantum_googgreenA400));
            btn_trips_2.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge_green));

            btn_trips_1.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_1.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

            btn_trips_3.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_3.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

            previousTips = "0.0";
            otherTips.setText("");

            lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + (Double.parseDouble(totalConfirmPayment) + 2) + "");
        } else {
            Log.e("tips", "green");
            btn_trips_2.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_2.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));
            lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                    + totalConfirmPayment);
        }

        AddTips();
    }

    @butterknife.OnClick(R.id.btn_trips_3)
    void btn_trips_3_Click() {
        if (btn_trips_3.getTextColors().getDefaultColor() == ContextCompat.getColor(context, R.color.white)) {
            Log.e("tips", "white");
            btn_trips_3.setTextColor(ContextCompat.getColor(context, R.color.quantum_googgreenA400));
            btn_trips_3.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge_green));

            btn_trips_2.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_2.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

            btn_trips_1.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_1.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

            previousTips = "0.0";
            otherTips.setText("");

            lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + (Double.parseDouble(totalConfirmPayment) + 3) + "");
        } else {
            Log.e("tips", "green");
            btn_trips_3.setTextColor(ContextCompat.getColor(context, R.color.white));
            btn_trips_3.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));
            lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                    + totalConfirmPayment);
        }

        AddTips();
    }

    String previousTips = "0.0", previousTipsSuccessIfConnectionFailed = "0.0";

    private void AddTips() {

        ProgressBar prgress_bar_add_tips = findViewById(R.id.prgress_bar_add_tips);
        ImageView im_is_tips_done = findViewById(R.id.im_is_tips_done);

        im_is_tips_done.setVisibility(View.GONE);
        prgress_bar_add_tips.setVisibility(View.VISIBLE);

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("tips", getTips());
            Log.e("AddTipsAPI", "" + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.ADD_TIPS, object, response -> {
            prgress_bar_add_tips.setVisibility(View.GONE);
            previousTipsSuccessIfConnectionFailed = previousTips = getTips();
            if (!previousTips.equals("0.0")) {
                im_is_tips_done.setImageResource(R.drawable.ic_checked);
                im_is_tips_done.setColorFilter(ContextCompat.getColor(context, R.color.quantum_googgreenA400), android.graphics.PorterDuff.Mode.SRC_IN);
                im_is_tips_done.setVisibility(View.VISIBLE);
            }

            Log.e("AddTips", response.toString());

        }, error -> {
            lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + getPreviouslblTotalPriceWithTips() + "");
            prgress_bar_add_tips.setVisibility(View.GONE);
            im_is_tips_done.setImageResource(R.drawable.ic_error_tips);
            im_is_tips_done.setColorFilter(ContextCompat.getColor(context, R.color.quantum_googred), android.graphics.PorterDuff.Mode.SRC_IN);
            im_is_tips_done.setVisibility(View.VISIBLE);

            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;
            Log.e("MyTest", "" + error);
            Log.e("MyTestError", "" + error.networkResponse);
            if (response != null && response.data != null) {
                Log.e("MyTestError1", "" + response.statusCode);
                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));
                    Log.e("ErrorAddTips", "" + errorObj.toString());

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("error"));
                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    } else if (response.statusCode == 401) {
                        refreshAccessToken("ADD_TIPS");
                    } else if (response.statusCode == 422) {
                        json = IlyftApplication.trimMessage(new String(response.data));
                        if (json != "" && json != null) {
                            displayMessage(json);
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }
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
                    AddTips();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private String getTips() {
        try {
            String TotalPriceTxt = lblTotalPrice.getText().toString();
            if (TotalPriceTxt.length() == 0)
                return "0.0";

            return (Double.parseDouble(TotalPriceTxt.replace("€", "")) - Double.parseDouble(totalConfirmPayment.replace("€", ""))) + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    private String getPreviouslblTotalPriceWithTips() {
        try {
            if (totalConfirmPayment.length() == 0)
                return "0.0";

            return (Double.parseDouble(totalConfirmPayment.replace("€", "")) + Double.parseDouble(previousTipsSuccessIfConnectionFailed)) + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    @butterknife.OnClick(R.id.btnPayNowCash)
    void btnPayNowCashClick() {


        if (customDialog == null) {
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
        }
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            URLEncoder.encode(URLHelper.COMPLETE_AS_CASH, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("222 object", object.toString());

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.COMPLETE_AS_CASH,
                        object,
                        response -> {
                            if (response != null) {
                                Log.e("222 response-", response.toString());

                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                                if (response.toString().contains("error")) {

                                    Toast.makeText(this, response.optString("error"), Toast.LENGTH_LONG).show();

                                } else if (response.optString("success").equals("transformed")) {
                                    btnPayNow.setVisibility(View.GONE);
                                    btnPayNowCash.setVisibility(View.GONE);

                                } else {
                                    Log.e("222 response", "error: " + response.toString());

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
                        headers.put("Authorization", "" + SharedHelper.getKey(TrackActivity.this, "token_type") + " " + SharedHelper.getKey(TrackActivity.this, "access_token"));
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    @butterknife.OnClick(R.id.btnPayNow)
    void btnPayNowClick() {

        btnPayNow.setEnabled(false);

        if (lblPaymentTypeInvoice.getText().toString().equalsIgnoreCase(getString(R.string.card))) {
            payNowCard();
        } else {
            Log.d(TAG, "btnPayNowClick: " + lblTotalPrice.getText().toString());

            PayPalPayment payment = new PayPalPayment(new BigDecimal(lblTotalPrice.getText().toString().replace("€", "").trim()), "EUR", " ",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(TrackActivity.this, PaymentActivity.class);
            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, GlobalDataMethods.config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, 0);
        }

    }

    private void init() {

        context = TrackActivity.this;
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        statusCheck();

        checkStatus();
        handleCheckStatus = new Handler();
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {

                    checkStatus();
                    utils.print("Handler", "Called");
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 5000);
            }
        }, 5000);


        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("flowValue")) {
                flowValue = intent.getIntExtra("flowValue", 0);
            }
        } else {
            flowValue = 0;
        }
        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_down);

        lblPaymentChange = findViewById(R.id.lblPaymentChanges);
        lblPaymentChange.setOnClickListener(v -> {
            Intent intent1 = new Intent(TrackActivity.this, CouponActivity.class);
            startActivity(intent1);
        });
    }


    @butterknife.OnClick(R.id.imgBack)
    void imgBackClick() {
        Log.e("done_aboood", "imgBackClick");

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.confirmation))
                .setMessage(getResources().getString(R.string.are_you_sure_you_want_to_close_flight_tracking))
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    boolean foreground = false;

    @Override
    public void onPause() {
        foreground = false;

        Log.e("lifcycle2", "onPause");
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        super.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        foreground = true;
        Log.e("lifcycle2", "onResume");
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this::onLocationChanged);
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.confirmation))
                .setMessage(getResources().getString((R.string.are_you_sure_you_want_to_close_flight_tracking)))
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> super.onBackPressed())
                .setNegativeButton(android.R.string.no, null).show();
    }


    @butterknife.OnClick(R.id.btnCancelRide)
    void btnCancelRideClick() {
        showCancelRideDialog();
    }

    @butterknife.OnClick(R.id.btnCancelTrip)
    void btnCancelTripClick() {
        if (btnCancelTrip.getText().toString().equals(getResources()
                .getString(R.string.cancel_trip)))
            showCancelRideDialog();
        else {
            String shareUrl = URLHelper.REDIRECT_SHARE_URL;
            navigateToShareScreen(shareUrl);
        }
    }

    private void showChooser() {
        Intent intent = new Intent(TrackActivity.this, Payment.class);
        startActivityForResult(intent, 5555);
    }

    RestInterface restInterface;
    Call<PaymentResponse> paymentResponseCall;
    Activity activity;
    Context context;
    String ETA;
    String isPaid = "", paymentMode = "";
    Utilities utils = new Utilities();
    int flowValue = 0;
    String reqStatus = "";
    String feedBackRating;
    Handler handleCheckStatus;
    String strTag = "";
    boolean once = true;
    Driver driver;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    Double latitude, longitude;
    String currentAddress;
    GoogleApiClient mGoogleApiClient;
    ImageView destinationBorderImg;

    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";
    //         <!--3. Waiting For Providers ...-->
    LocationRequest mLocationRequest;

    CameraPosition cmPosition;
    String current_lat = "", current_lng = "", current_address = "", source_lat = "", source_lng = "", source_address = "",
            dest_lat = "", dest_lng = "", dest_address = "";
    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    LatLng sourceLatLng;
    LatLng destLatLng;
    Marker sourceMarker;
    Marker destinationMarker;
    Marker providerMarker;
    AlertDialog alert;
    TextView lblPaymentType, txtpaiddriver;
    TextView lblDis, lblEta, lblApproxAmount, lblApproxAmountDiscount, lblCmfrmSourceAddress, lblCmfrmDestAddress;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    private boolean mIsShowing;
    private boolean mIsHiding;


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
        setContentView(R.layout.activity_track);

        context = TrackActivity.this;
        activity = TrackActivity.this;
        ButterKnife.bind(this);
        restInterface = ServiceGenerator.createService(RestInterface.class);
        SharedHelper.putKey(context, "AUTO_CARETAKER_REQ", "");

        init();
        //permission to access location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            initMap();
            MapsInitializer.initialize(getApplicationContext());
        }


        reqStatus = SharedHelper.getKey(context, "req_status");
        lblPaymentType = findViewById(R.id.lblPaymentType);
        txtpaiddriver = findViewById(R.id.txtpaiddriver);
        lblDis = findViewById(R.id.lblDis);
        lblEta = findViewById(R.id.lblEta);
        lblApproxAmount = findViewById(R.id.lblApproxAmount);
        lblApproxAmountDiscount = findViewById(R.id.lblApproxAmountDiscount);
        lblCmfrmSourceAddress = findViewById(R.id.lblCmfrmSourceAddress);
        lblCmfrmDestAddress = findViewById(R.id.lblCmfrmDestAddress);


        lblPaymentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooser();
            }
        });

        otherTips.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + "" +
                            (Double.parseDouble(charSequence.toString()) + Double.parseDouble(totalConfirmPayment)));

                    btn_trips_3.setTextColor(ContextCompat.getColor(context, R.color.white));
                    btn_trips_3.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

                    btn_trips_2.setTextColor(ContextCompat.getColor(context, R.color.white));
                    btn_trips_2.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));

                    btn_trips_1.setTextColor(ContextCompat.getColor(context, R.color.white));
                    btn_trips_1.setBackgroundDrawable(getDrawable(R.drawable.rectangle_edge));
                } else {
                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + "" +
                            (Double.parseDouble(totalConfirmPayment)));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("AddTips", "afterTextChanged");
                if (otherTips.getText().toString().length() != 0 || (!previousTips.equals("0.0")))
                    AddTips();
            }
        });

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.my.app.onMessageReceived.TrackActivity");
        receiver = new MyBroadcastReceiver();
    }

    private IntentFilter intentFilter;
    private MyBroadcastReceiver receiver;

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  Bundle extras = intent.getExtras();
            String message = intent.getExtras().getString("message");
            Log.e("messsgae", message + "messsage");
            if (message != null) {
                new_message_tv.setVisibility(View.VISIBLE);

                // Get the custom layout view.
//                View toastView = getLayoutInflater().inflate(R.layout.activity_toast_custom_view, null);
//                TextView customToastText = toastView.findViewById(R.id.customToastText);
//                customToastText.setText(message);
//                // Initiate the Toast instance.
//                Toast toast = new Toast(getApplicationContext());
//                // Set custom view in toast.
//                toast.setView(toastView);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0,0);
//                toast.show();

//                Toast.makeText(context, message + " ..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    void initMap() {
        if (mMap == null) {
            FragmentManager fm = getSupportFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }

        if (mMap != null) {
            setupMap();
        }

    }

    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }

    }

    public void statusCheck() {
        if (getApplicationContext() != null) {
            LocationManager manager = (LocationManager) TrackActivity.this.getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLoc();
            }
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(TrackActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Location error", "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(TrackActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.CANCELED:
                        showDialogForGPSIntent();
                        break;
                }
            }
        });
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(callGPSSettingIntent);
                            }
                        });
        builder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }


    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
        }
    }


    @Override
    public void getJSONArrayResult(String strTag, JSONArray arrayResponse) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (foreground) {

            if (URLHelper.syria_change_location)
                if ((location.getLongitude() + "").contains("36.") && (location.getLatitude() + "").contains("34.")) {
                    location.setLatitude(52.5230588);
                    location.setLongitude(13.4699208);
                }

            Log.e("onLocationChanged", "from here!");
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();

            if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {


                current_lat = "" + location.getLatitude();
                current_lng = "" + location.getLongitude();
                current_latSend = location.getLatitude();
                current_lngSend = location.getLongitude();
                if (source_lat.equalsIgnoreCase("") || source_lat.length() < 0) {
                    source_lat = current_lat;
                }
                if (source_lng.equalsIgnoreCase("") || source_lng.length() < 0) {
                    source_lng = current_lng;
                }

                if (value == 0) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.setPadding(0, 0, 0, 0);
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.e("getCompleteAdd1", "from here!");
                    currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
                    source_lat = "" + latitude;
                    source_lng = "" + longitude;
                    source_address = currentAddress;
                    current_address = currentAddress;
                    // frmSource.setText(currentAddress);
                    value++;

                }
            }
        }
    }

    @Override
    public void onCameraMove() {

        utils.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
        cmPosition = mMap.getCameraPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        String title = "";

        if (marker != null && marker.getTitle() != null) {
            title = marker.getTitle();

            if (sourceMarker != null && title.equalsIgnoreCase("Source")) {
                LatLng markerLocation = sourceMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses = null;

                Log.e("count_call_Geocoder2", URLHelper.count_call_Geocoder++ + "");
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                source_lat = markerLocation.latitude + "";
                source_lng = markerLocation.longitude + "";

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "source", "" + address + "," + city + "," + state);
                        source_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (destinationMarker != null && title.equalsIgnoreCase("Destination")) {
                LatLng markerLocation = destinationMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses;
                Log.e("count_call_Geocoder3", URLHelper.count_call_Geocoder++ + "");
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                Log.e("loc_trackPiT", "----from_here1: " + "onMarkerDragEnd");
                dest_lat = "" + markerLocation.latitude;
                dest_lng = "" + markerLocation.longitude;

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "destination", "" + address + "," + city + "," + state);
                        dest_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            mMap.clear();
            Log.e("setValuesForSAndD1", "from here!");
            setValuesForSourceAndDestination();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getApplicationContext(), R.raw.style_json));

            if (!success) {
                utils.print("Map:Style", "Style parsing failed.");
            } else {
                utils.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            utils.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        setupMap();

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TrackActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.location_permission_needed))
                        .setMessage(getString(R.string.please_accept_to_use_location_functionality))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TrackActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(TrackActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context, "first_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "GoCab-" + "Mr/Mrs." + name + " would like to share a trip with you at " +
                    shareUrl + current_lat + "," + current_lng);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getResources().getString(R.string.share_app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    // layout changes
    void layoutChanges() {
        try {

            if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            }

            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);


            shadowBack.setVisibility(View.GONE);
            txtComments.setText("");
            if (flowValue == 0) {


                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
            } else if (flowValue == 1) {

                destinationBorderImg.setVisibility(View.GONE);
                imgBack.setVisibility(View.VISIBLE);

                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }

            } else if (flowValue == 2) {

                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }

            } else if (flowValue == 3) {

                lnrWaitingForProviders.setVisibility(View.VISIBLE);

                //sourceAndDestinationLayout.setVisibility(View.GONE);
            } else if (flowValue == 4) {

                lnrProviderAccepted.startAnimation(slide_up);


                lnrProviderAccepted.setVisibility(View.VISIBLE);
                layoutdriverstatus.setVisibility(View.VISIBLE);
            } else if (flowValue == 5) {

                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
            } else if (flowValue == 6) {

                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);

                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(5.0f);
                feedBackRating = "5";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(5.0f);
                            feedBackRating = "5";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
            } else if (flowValue == 7) {
//
//                ScheduleLayout.startAnimation(slide_up);
//                ScheduleLayout.setVisibility(View.VISIBLE);
            } else if (flowValue == 8) {
                // clear all views
                shadowBack.setVisibility(View.GONE);
            } else if (flowValue == 9) {

                rtlStaticMarker.setVisibility(View.VISIBLE);
                shadowBack.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                } else {
                    Intent intentCall1 = new Intent(Intent.ACTION_DIAL);
                    intentCall1.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intentCall1);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    boolean rateDone = false, trinkDone = true;

    public void submitReviewCall() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtComments.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("rate", object.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SubmitRequestResponse", response.toString());
                utils.hideKeypad(context, activity.getCurrentFocus());

                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                SharedHelper.putKey(context, "service_type_Car_Ambulance", "");

                rateDone = true;
                goAfterRateAndTrink();


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
//                                utils.displayMessage(getCurrentFocus(), errorObj.optString("message"));
                            } catch (Exception e) {
//                                utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SUBMIT_REVIEW");
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
//                        utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getCurrentFocus(), getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type")
                        + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void goAfterRateAndTrink() {

        if (rateDone && trinkDone) {

            rateDone = false;
//            trinkDone = false;

            Intent goMain = new Intent(activity, MainActivity.class);
            goMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(goMain);
            stopHandler();

        }

    }


    public void payNowCard(String paymentType, String Price) {
        // --

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            Log.e("222", "paymentType: " + paymentType);

            object.put("request_id", SharedHelper.getKey(context, "request_id") + "");
//            object.put("user_id", SharedHelper.getKey(context, "id") + "");
            object.put("total_payment", Price.replace("€", ""));
            if (paymentType.contains("PAYPAL")) {
                object.put("payment_id", paymentId);
            }
//              object.put("payment_mode", SharedHelper.getKey(getApplicationContext(),"payment_mode"));
//              object.put("is_paid", isPaid);
            Log.e("222", "payNowCard: " + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_REQUEST_Later_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("222__onResponse", "PayNowRequestResponse: " + response.toString());

                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                trinkDone = true;
                goAfterRateAndTrink();


            }
        }, error -> {

            try {

                String msg = "statusCode: " + error.networkResponse.statusCode + "\n" +
                        URLHelper.PAY_REQUEST_Later_API + "\n\n" +
                        trimMessage(new String(error.networkResponse.data));

                utils.showAlert(context, msg);
            } catch (Exception e) {
                if (error.networkResponse != null)
                    utils.showAlert(context, "statusCode: " + error.networkResponse.statusCode + "\n" +
                            URLHelper.PAY_REQUEST_Later_API);
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
//                        refreshAccessToken("PAY_NOW_TRING");
                        Log.e("222", "refreshAccessToken(PAY_NOW_TRING)");
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
                utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.please_try_again));
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

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showreasonDialog();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    String cancaltype = "";

    private void showreasonDialog() {
        final int[] checkedIdInt = new int[1];
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        RadioGroup radioCancel = view.findViewById(R.id.radioCancel);
        radioCancel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedIdInt[0] = checkedId;

                if (checkedId == R.id.driver) {
                    reasonEtxt.setVisibility(View.GONE);
                    cancaltype = getResources().getString(R.string.plan_changed);
                }
                if (checkedId == R.id.vehicle) {
                    reasonEtxt.setVisibility(View.GONE);
                    cancaltype = getResources().getString(R.string.booked_another_cab);
                }

                if (checkedId == R.id.denied) {
                    reasonEtxt.setVisibility(View.GONE);
                    cancaltype = getResources().getString(R.string.driver_denied_to_come);
                }
                if (checkedId == R.id.moving) {
                    reasonEtxt.setVisibility(View.GONE);
                    cancaltype = getResources().getString(R.string.driver_is_not_moving);
                }

                if (checkedId == R.id.app) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getResources().getString(R.string.my_reason_is_not_listed);
                }

            }
        });
        builder.setView(view)
                .setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cancaltype.isEmpty()) {
                    Toast.makeText(TrackActivity.this, getResources().getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();

                } else {
                    cancalReason = reasonEtxt.getText().toString();

                    if (checkedIdInt[0] == R.id.app && cancalReason.isEmpty()) {
                        reasonEtxt.setError(getResources().getString(R.string.please_specify_reason));
                    } else if (cancalReason.isEmpty()) {
//                        reasonEtxt.setError(getResources().getString(R.string.please_specify_reason));
                        cancalReason = cancaltype;
                        cancelRequest();
                        dialog.dismiss();
                    } else {
                        cancelRequest();
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();

    }

    public void payNowBacked(String merchantReqId,
                             String checkOutReqId) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("m_id", merchantReqId);
            object.put("c_id", checkOutReqId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.PAY_NOW_API,
                        object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                utils.print("PayNowRequestResponse", response.toString());
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                SharedHelper.putKey(context, "total_amount", "");
                                flowValue = 6;
                                layoutChanges();
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
                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                    try {
//                                utils.displayMessage(getCurrentFocus(), errorObj.optString("message"));
                                    } catch (Exception e) {
//                                utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                                    }
                                } else if (response.statusCode == 401) {
                                    refreshAccessToken("PAY_NOW");
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

    void payNow() {
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

        String auth = "Bearer " + SharedHelper.getKey(TrackActivity.this,
                "paymentAccessToken");
        String requestWith = "XMLHttpRequest";
        String CONTENT_TYPE = "application/json";

        paymentResponseCall = restInterface.createSocialLogin(requestWith, CONTENT_TYPE,
                auth, paymentRequest);
        paymentResponseCall.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, retrofit2.Response<PaymentResponse> response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                if (response.code() == 200) {
                    String cId = response.body().getMerchantRequestID();
                    String mId = response.body().getCheckoutRequestID();
                    payNowBacked(mId, cId);
                } else {
                    Toast.makeText(TrackActivity.this, getResources().getString(R.string.not_responding), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Toast.makeText(TrackActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void cancelRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);

            utils.print("CancelRequest", object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                Toast.makeText(context, getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                SharedHelper.putKey(context, "request_id", "");
                SharedHelper.putKey(context, "service_type_Car_Ambulance", "");
                SharedHelper.putKey(context, "Insurance_id", "");
                SharedHelper.putKey(context, "insurance_price", "");
                SharedHelper.putKey(context, "insurance_name", "");

                PreviousStatus = "";
                Intent goMain = new Intent(activity, MainActivity.class);
                goMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(goMain);
                activity.finish();
                stopHandler();
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
                    flowValue = 4;
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
//                                utils.displayMessage(getCurrentFocus(), errorObj.optString("message"));
                                flowValue = 0;
                                PreviousStatus = "";
                                Intent goMain = new Intent(activity, MainActivity.class);
                                goMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(goMain);
                                activity.finish();
                                stopHandler();
                            } catch (Exception e) {
//                                utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                            }
                            layoutChanges();
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("CANCEL_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
//                                utils.displayMessage(getCurrentFocus(), json);
                            } else {
//                                utils.displayMessage(getCurrentFocus(), getString(R.string.please_try_again));
                            }
                            PreviousStatus = "";
                            Intent goMain = new Intent(activity, MainActivity.class);
                            goMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(goMain);
                            activity.finish();
                        } else if (response.statusCode == 503) {
//                            utils.displayMessage(getCurrentFocus(), getString(R.string.server_down));
                            layoutChanges();
                        } else {
//                            utils.displayMessage(getCurrentFocus(), getString(R.string.please_try_again));
                            layoutChanges();
                        }

                    } catch (Exception e) {
//                        utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                        layoutChanges();
                    }

                } else {
//                    utils.displayMessage(getCurrentFocus(), getString(R.string.please_try_again));
                    layoutChanges();
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

    private void refreshAccessToken(final String tag) {

        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");

            Log.i(TAG, "refreshAccessToken: " + object.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
//                    getApproximateFare();
                } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                    cancelRequest();
                } else if (tag.equalsIgnoreCase("SUBMIT_REVIEW")) {
                    submitReviewCall();
                } else if (tag.equalsIgnoreCase("PAY_NOW")) {
                    payNow();
                } else if (tag.equalsIgnoreCase("ADD_TIPS")) {
                    AddTips();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", "false");
                    utils.GoToBeginActivity(TrackActivity.this);
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

        Log.i(TAG, "refreshAccessToken: " + jsonObjectRequest.getUrl());
    }

    private void checkStatus() {
        try {

            utils.print("Handler", "Inside");
            if (isInternet) {

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.REQUEST_STATUS_CHECK_API, null, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedHelper.putKey(context, "req_status", "");
                        try {
                            if (customDialog != null && customDialog.isShowing()) {
                                customDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        reqStatus = "";
                        utils.print("Response-.", "" + response.toString());

                        if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                            utils.print("response", "not null");
                            try {
                                Log.i(TAG, "Check onResponse : " + response.toString(1));

                                JSONArray requestStatusCheck = response.optJSONArray("data");
                                JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                //Driver Detail
                                if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                    current_chat_requestID = requestStatusCheckObject.optString("id");
                                    currentProviderID = requestStatusCheckObject.optJSONObject("provider").optString("id");
                                    userID = requestStatusCheckObject.optJSONObject("user").optString("id");
                                    driver = new Driver();
                                    providerFirstName = requestStatusCheckObject.optJSONObject("provider").optString("first_name");
                                    driver.setFname(requestStatusCheckObject.optJSONObject("provider").optString("first_name"));
                                    // driver.setLname(requestStatusCheckObject.optJSONObject("provider").optString("last_name"));
                                    driver.setEmail(requestStatusCheckObject.optJSONObject("provider").optString("email"));
                                    driver.setMobile(requestStatusCheckObject.optJSONObject("provider").optString("mobile"));
                                    driver.setImg(requestStatusCheckObject.optJSONObject("provider").optString("avatar"));
                                    driver.setRating(requestStatusCheckObject.optJSONObject("provider").optString("rating"));
                                }
                                String status = requestStatusCheckObject.optString("status");
                                reqStatus = requestStatusCheckObject.optString("status");


                                SharedHelper.putKey(context, "req_status", requestStatusCheckObject.optString("status"));
                                String wallet = requestStatusCheckObject.optString("use_wallet");
                                if (status.equalsIgnoreCase("STARTED")
                                        || status.equalsIgnoreCase("ARRIVED")) {
                                    source_lat = requestStatusCheckObject.optJSONObject("provider").optString("latitude");
                                    source_lng = requestStatusCheckObject.optJSONObject("provider").optString("longitude");
                                    dest_lat = requestStatusCheckObject.optString("s_latitude");
                                    dest_lng = requestStatusCheckObject.optString("s_longitude");
                                    Log.e("loc_trackPiT", "----from_here2: " + "checkStatus");
                                    Log.e("setValuesForSAndD2", "from here!");
                                    setValuesForSourceAndDestination();

                                } else if (status.equalsIgnoreCase("PICKEDUP")) {
                                    source_lat = requestStatusCheckObject.optJSONObject("provider").optString("latitude");
                                    source_lng = requestStatusCheckObject.optJSONObject("provider").optString("longitude");
                                    dest_lat = requestStatusCheckObject.optString("d_latitude");
                                    dest_lng = requestStatusCheckObject.optString("d_longitude");
                                    Log.e("loc_trackPiT", "----from_here3: " + "checkStatus");
                                    Log.e("setValuesForSAndD3", "from here!");
                                    setValuesForSourceAndDestination();

                                } else {
                                    source_lat = requestStatusCheckObject.optString("s_latitude");
                                    source_lng = requestStatusCheckObject.optString("s_longitude");
                                    dest_lat = requestStatusCheckObject.optString("d_latitude");
                                    dest_lng = requestStatusCheckObject.optString("d_longitude");
                                    Log.e("loc_trackPiT", "----from_here4: " + "checkStatus");
                                    Log.e("setValuesForSAndD4", "from here!");
                                    setValuesForSourceAndDestination();

                                }
                                pickUpLocationName = requestStatusCheckObject.optString("s_address");
                                dropLocationName = requestStatusCheckObject.optString("d_address");

                                if (requestStatusCheckObject.optString("surge").equalsIgnoreCase("1")) {
                                    lblSurgePrice.setVisibility(View.VISIBLE);
                                } else {
                                    lblSurgePrice.setVisibility(View.GONE);
                                }

                                utils.print("PreviousStatus", "" + PreviousStatus);

                                if (!PreviousStatus.equals(status)) {
                                    mMap.clear();
                                    PreviousStatus = status;
                                    flowValue = 8;
                                    layoutChanges();
                                    SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                                    reCreateMap();
                                    utils.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                    switch (status) {
                                        case "SEARCHING":
                                            show(lnrWaitingForProviders);
                                            //rippleBackground.startRippleAnimation();
                                            strTag = "search_completed";
                                            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                                LatLng myLocation1 = new LatLng(Double.parseDouble(source_lat),
                                                        Double.parseDouble(source_lng));
                                                CameraPosition cameraPosition1 = new CameraPosition.Builder().target(myLocation1).zoom(16).build();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                                            }
                                            break;
                                        case "CANCELLED":
                                            strTag = "";
                                            imgSos.setVisibility(View.GONE);
                                            break;
                                        case "ACCEPTED":
                                            driveraccepted.setVisibility(View.VISIBLE);
                                            driverArrived.setVisibility(View.GONE);
                                            driverPicked.setVisibility(View.GONE);
                                            driverCompleted.setVisibility(View.GONE);
                                            txtdriveraccpted.setText(getString(R.string.arriving));
                                            imgarrived.setImageResource(R.drawable.arriveddisable);
                                            imgPicked.setImageResource(R.drawable.pickeddisable);
                                            imgDropped.setImageResource(R.drawable.complete);

                                            strTag = "ride_accepted";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");

//                                                getDurationForRoute(provider.optString("latitude"), provider.optString("longitude"));

                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                SharedHelper.putKey(context, "provider_first_name", "" + provider.optString("first_name"));
                                                lblProvider.setText(provider.optString("first_name"));
                                                tvServiceNumber.setText("" + provider_service.getString("service_number"));
                                                tvServiceModel.setText("" + provider_service.getString("service_model"));
//                                                imgProvider.getBackground().setAlpha(127);
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.get().load(service_type.optString("image"))
                                                        .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                                                        .into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));

                                                setPricesIfDiscount(service_type.optString("fixed"));


                                                lblCmfrmSourceAddress.setText(pickUpLocationName);
                                                lblCmfrmDestAddress.setText(dropLocationName);
                                                //lnrAfterAcceptedStatus.setVisibility(View.GONE);

                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                show(lnrProviderAccepted);
                                                flowValue = 9;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "STARTED":
                                            strTag = "ride_started";
                                            showStartedDialog(requestStatusCheckObject);
                                            getDurationForRoute();

                                            break;

                                        case "ARRIVED":
                                            if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                confirmDialog.dismiss();
                                            driveraccepted.setVisibility(View.GONE);
                                            driverArrived.setVisibility(View.VISIBLE);
                                            driverPicked.setVisibility(View.GONE);
                                            driverCompleted.setVisibility(View.GONE);
                                            txtdriverArrived.setText(getString(R.string.arrived));
                                            imgarrived.setImageResource(R.drawable.arriveddisable);
                                            imgPicked.setImageResource(R.drawable.pickeddisable);
                                            imgDropped.setImageResource(R.drawable.complete);

                                            once = true;
                                            strTag = "ride_arrived";
                                            utils.print("MyTest", "ARRIVED");
                                            try {
                                                utils.print("MyTest", "ARRIVED TRY");
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name"));
                                                tvServiceNumber.setText("" + provider_service.getString("service_number"));
                                                tvServiceModel.setText("" + provider_service.getString("service_model"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.get().load(URLHelper.base + service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));

                                                setPricesIfDiscount(service_type.optString("fixed"));

                                                lblCmfrmSourceAddress.setText(pickUpLocationName);
                                                lblCmfrmDestAddress.setText(dropLocationName);


                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                flowValue = 4;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                utils.print("MyTest", "ARRIVED CATCH");
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "PICKEDUP":
                                            if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                confirmDialog.dismiss();

                                            driveraccepted.setVisibility(View.GONE);
                                            driverArrived.setVisibility(View.GONE);
                                            driverPicked.setVisibility(View.VISIBLE);
                                            driverCompleted.setVisibility(View.GONE);
                                            txtdriverpicked.setText(getString(R.string.picked_up));
                                            imgarrived.setImageResource(R.drawable.arriveddisable);
                                            imgPicked.setImageResource(R.drawable.pickeddisable);
                                            imgDropped.setImageResource(R.drawable.complete);
                                            once = true;
                                            strTag = "ride_picked";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name"));
                                                tvServiceNumber.setText("" + provider_service.getString("service_number"));
                                                tvServiceModel.setText("" + provider_service.getString("service_model"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.get().load(URLHelper.base + service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));

                                                setPricesIfDiscount(service_type.optString("fixed"));
                                                lblCmfrmSourceAddress.setText(pickUpLocationName);
                                                lblCmfrmDestAddress.setText(dropLocationName);


                                                imgSos.setVisibility(View.VISIBLE);
                                                //imgShareRide.setVisibility(View.VISIBLE);

                                                btnCancelTrip.setText(getString(R.string.share));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                flowValue = 4;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "DROPPED":
                                            driveraccepted.setVisibility(View.GONE);
                                            driverArrived.setVisibility(View.GONE);
                                            driverPicked.setVisibility(View.GONE);
                                            driverCompleted.setVisibility(View.VISIBLE);
                                            txtdrivercompleted.setText(getString(R.string.trip_completed));
                                            imgarrived.setImageResource(R.drawable.arriveddisable);
                                            imgPicked.setImageResource(R.drawable.pickeddisable);
                                            imgDropped.setImageResource(R.drawable.complete);
                                            once = true;
                                            strTag = "";
                                            imgSos.setVisibility(View.VISIBLE);
                                            lblPaymentType.setEnabled(false);
                                            //imgShareRide.setVisibility(View.VISIBLE);
                                            try {
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("distance"));
                                                    if (payment.optString("discount") != null) {
                                                        promoLayout.setVisibility(View.VISIBLE);
                                                        txtDiscount.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("discount"));
                                                    }
                                                    //lblCommision.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("commision"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("total"));
                                                    totalConfirmPayment = payment.optString("total");
                                                    SharedHelper.putKey(TrackActivity.this, "total_price",
                                                            payment.optString("total"));
                                                }
                                                if (requestStatusCheckObject.optString("booking_id") != null &&
                                                        !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                    booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                    tvPaymentLabel.setText(SharedHelper.getKey(TrackActivity.this, "first_name").split(",")[0] + " owes");
                                                } else {
                                                    booking_id.setVisibility(View.GONE);
                                                }
                                                if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPayNowCash.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    lblPaymentType.setEnabled(false);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && wallet.equalsIgnoreCase("1")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPayNowCash.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    lblPaymentType.setEnabled(false);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getResources().getString(R.string.cash_and_wallet));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPayNowCash.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getResources().getString(R.string.card));
                                                    lblPaymentType.setEnabled(false);
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("PAYPAL")) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPayNowCash.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("PAYPAL");
                                                    lblPaymentType.setText("PAYPAL");
                                                    lblPaymentType.setEnabled(false);
                                                    txtpaiddriver.setVisibility(View.GONE);
                                                } else if (isPaid.equalsIgnoreCase("1")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPayNowCash.setVisibility(View.GONE);
                                                    lblProviderName.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name"));
                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    else
                                                        Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                confirmDialog.dismiss();
                                            break;

                                        case "COMPLETED":
                                            layoutdriverstatus.setVisibility(View.GONE);

                                            strTag = "";
                                            try {
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("distance"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("total"));
                                                    totalConfirmPayment = payment.optString("total");
                                                }
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                isPaid = requestStatusCheckObject.optString("paid");
                                                paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                imgSos.setVisibility(View.GONE);
                                                //imgShareRide.setVisibility(View.GONE);
                                                // lblCommision.setText(payment.optString("commision"));
                                                if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPayNowCash.setVisibility(View.GONE);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getResources().getString(R.string.card));
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    btnPayNowCash.setVisibility(View.VISIBLE);
                                                } else if (isPaid.equalsIgnoreCase("1")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPayNowCash.setVisibility(View.GONE);
                                                    lblProviderName.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name"));
                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    else
                                                        Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                    //imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    // lblPaymentTypeInvoice.setText("CARD");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                confirmDialog.dismiss();
                                            break;
                                    }
                                }

                                if ("ACCEPTED".equals(status) || "STARTED".equals(status) ||
                                        "ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status)) {
                                    Log.e("min_daa", "from here ||");

                                    liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                            requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
//                                utils.displayMessage(findViewById(R.id.imgBack), getString(R.string.something_went_wrong));
                            }
                        } else if (PreviousStatus.equalsIgnoreCase("SEARCHING")) {
                            SharedHelper.putKey(context, "current_status", "");
                            if (scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                    && !scheduledTime.equalsIgnoreCase("")) {

                            } else {

                                strTag = "";
                                PreviousStatus = "";
                                flowValue = 0;

                                if (GlobalDataMethods.newScheduleRequest) {
                                    GlobalDataMethods.newScheduleRequest = false;
                                    Intent output = new Intent();
                                    setResult(RESULT_OK, output);
                                    finish();
                                } else {
                                    Toast.makeText(context, getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    startActivity(intent);
                                    stopHandler();
                                }
                            }
                        } else if (PreviousStatus.equalsIgnoreCase("STARTED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            stopHandler();

                        } else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";

                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            stopHandler();
                        } else if (PreviousStatus.equalsIgnoreCase("SEARCHING") && response.optJSONObject("data") != null
                                && response.optJSONArray("data").length() > 0) {
                            Toast.makeText(context, getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;

                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            stopHandler();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());
                        try {
                            if (customDialog != null && customDialog.isShowing()) {
                                customDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        reqStatus = "";
                        SharedHelper.putKey(context, "req_status", "");
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");


                        headers.put("Authorization", "" +
                                SharedHelper.getKey(context, "token_type")
                                + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

                IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
                Log.i(TAG, "checkStatus api url : " + jsonObjectRequest.getUrl());

            } else {
                Log.e("nointerner", "nointernet");
//                utils.displayMessage(getCurrentFocus(), getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopHandler() {
        try {
            if (SharedHelper.getKey(context, "req_status").length() == 0 && handleCheckStatus != null) {
                handleCheckStatus.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPricesIfDiscount(String fixed) {

        lblApproxAmount.setText(SharedHelper.getKey(TrackActivity.this, "currency") + fixed);
        sendToServerCoupon();

    }


    private void sendToServerCoupon() {


        JSONObject object = new JSONObject();
        try {
            object.put("user_id", SharedHelper.getKey(context, "id"));
            object.put("coupon", GlobalDataMethods.coupon_gd_str);
            Log.e("coupon_from", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                URLHelper.COUPON_VERIFY,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utils.print("AddCouponRes", "" + response.toString());
                        try {

                            JSONObject jsonObject = response;

                            if (jsonObject.optString("success").equals("coupon available")) {
                                lblApproxAmount.setPaintFlags(lblApproxAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                double discount = Double.parseDouble(SharedHelper.getKey(context, "estimated_fare"))
                                        - (GlobalDataMethods.coupon_discount_str);

                                if (discount < 0) {
                                    discount = 0;
                                }
                                lblApproxAmountDiscount.setText(SharedHelper.getKey(context, "currency") + "" +
                                        String.format(Locale.ENGLISH, "%.2f", discount));
                                lblApproxAmountDiscount.setVisibility(View.VISIBLE);

                            } else {// coupoun used
                                coupon_gd_str = "";
                                coupon_discount_str = 0d;
                                lblApproxAmount.setPaintFlags(lblApproxAmount.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                                lblApproxAmountDiscount.setText("");
                                lblApproxAmountDiscount.setVisibility(View.GONE);
                            }


                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                Log.e(this.getClass().getName(), "Error_Favourite" + error.getMessage());

                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 ||
                                response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
//                                    refreshAccessToken();
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
                        sendToServerCoupon();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(getApplicationContext(), "token_type") + " "
                        + SharedHelper.getKey(getApplicationContext(), "access_token"));
                return headers;
            }
        };
        IlyftApplication.getInstance().addToRequestQueue(objectRequest);


    }


    boolean lockWhileGet = false;

    public void getDurationForRoute() {
        try {


            JSONObject object = new JSONObject();
            object.put("request_id", SharedHelper.getKey(context, "request_id"));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.get_current_Prodiver_Location_calc_distance, object,
                    response -> {

                        Log.e("get_current_Prodiver", "Location_calc_distance" + response.toString());

                        try {

                            JSONObject responseObject = new JSONObject(response.toString());

                            Double driver_lat = Double.parseDouble(responseObject.getJSONObject("provider").getString("latitude"));
                            Double driver_longi = Double.parseDouble(responseObject.getJSONObject("provider").getString("longitude"));

                            Log.e("done_aboood11", source_lat + " / " + source_lng);

                            GoogleDirection.withServerKey(SharedHelper.getKey(TrackActivity.this, "GOOGLE_KEY_MAPS"))
                                    .from(new LatLng(driver_lat, driver_longi))// driver loc
                                    .to(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)))
                                    .transportMode(TransportMode.DRIVING)
                                    .execute(new DirectionCallback() {


                                        @Override
                                        public void onDirectionSuccess(Direction direction, String rawBody) {
                                            Log.e("done_aboood", "onDirectionSuccess");
                                            if (direction.isOK()) {
                                                try {
                                                    trackPickToDest();
                                                    Log.v("rawBody2", rawBody + "");
                                                    Log.v("direction2", direction + "");

                                                    float totalDistance = 0;
                                                    int totalDuration = 0;
                                                    mMap.clear();
                                                    Route route = direction.getRouteList().get(0);
                                                    int legCount = route.getLegList().size();
                                                    for (int index = 0; index < legCount; index++) {
                                                        Leg leg = route.getLegList().get(index);

                                                        totalDistance = totalDistance + Float.parseFloat(leg.getDistance().getText()
                                                                .replace("km", "")
                                                                .replace("m", "")
                                                                .replace(",", ".")
                                                                .trim());

                                                        if (leg.getDuration().getText().contains("day")) {
                                                            Log.v("splitday", leg.getDuration().getText().split("day")[0] + " ");
                                                            totalDuration = totalDuration + 24 * 60 * Integer.parseInt(leg.getDuration().getText()
                                                                    .split("day")[0].trim());

                                                        } else if (leg.getDuration().getText().contains("hour")) {
                                                            Log.v("splithour", leg.getDuration().getText().split("hour")[0] + " ");
                                                            totalDuration = totalDuration + 60 * Integer.parseInt(leg.getDuration().getText()
                                                                    .split("hour")[0].trim());

                                                        } else if (leg.getDuration().getText().contains("hours")) {
                                                            totalDuration = totalDuration + 60 * Integer.parseInt(leg.getDuration().getText()
                                                                    .split("hours")[0].trim().replace("m", ""));
                                                        } else if (leg.getDuration().getText().contains("mins")) {
                                                            totalDuration = totalDuration + Integer.parseInt(leg.getDuration().getText()
                                                                    .replace("hour", "").replace("mins", "").replace("m", "").trim());
                                                        } else {
                                                            totalDuration = totalDuration + 0;
                                                        }
                                                    }

                                                    Log.e("min_km", "from getDurationForRoute");
                                                    lblCmfrmSourceAddress.setText(pickUpLocationName);
                                                    lblDis.setText(totalDistance + " km");
                                                    lblEta.setText(totalDuration + " Min");
                                                    GlobalDataMethods.GTotalDuration = totalDuration;

                                                    if (confirmDialog != null && confirmDialog.isShowing()) {

                                                        Log.e("Dialog", "if");

                                                        TextView tvDriverMsg = confirmDialog.findViewById(R.id.tvDriverMsg);
                                                        tvDriverMsg.setText(getResources().getString(R.string.driver_will_pickup_you_in) + " " + totalDuration + " " + getResources().getString(R.string.minutes));

                                                        ProgressBar pb_min_da = confirmDialog.findViewById(R.id.pb_min_da);
                                                        pb_min_da.setVisibility(View.GONE);

                                                        new Handler().postDelayed(() -> {
                                                            tvDone.performClick();
                                                        }, 3000);
                                                    } else {
                                                        Log.e("Dialog", "else");
                                                    }

                                                    Log.e("done_aboood2", totalDuration + "");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.e("done_aboood1", "error " + e.getMessage());

                                                }
                                            }
                                            lockWhileGet = false;
                                        }

                                        @Override
                                        public void onDirectionFailure(Throwable t) {
                                            lockWhileGet = false;
                                            // Do something
                                            Log.e("done_aboood", "Failure " + t.getMessage());
                                        }
                                    });


                        } catch (JSONException e) {
                            Log.e("GoogleDirection", "from here!");
                            e.printStackTrace();
                        }


                    }, error -> {
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
//                            refreshAccessToken();
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
                        e.printStackTrace();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getDurationForRoute();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                    Log.e("", "Access_Token" + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };
            IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    Dialog confirmDialog;
    TextView tvDone;

    private void showStartedDialog(JSONObject requestStatusCheckObject) {

        confirmDialog = new Dialog(TrackActivity.this);
        confirmDialog.setContentView(R.layout.confirm_ride_dialog);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        confirmDialog.show();
        TextView tvCancel = confirmDialog.findViewById(R.id.tvCancel);
        tvDone = confirmDialog.findViewById(R.id.tvDone);
        TextView tvDriverMsg = confirmDialog.findViewById(R.id.tvDriverMsg);
        tvDriverMsg.setText(getResources().getString(R.string.driver_will_pickup_you_in) + " " + "..." + " " + getResources().getString(R.string.minutes));

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                cancelRequest();
            }
        });


        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                layoutdriverstatus.setVisibility(View.VISIBLE);
                driveraccepted.setVisibility(View.VISIBLE);
                driverArrived.setVisibility(View.GONE);
                driverPicked.setVisibility(View.GONE);
                driverCompleted.setVisibility(View.GONE);
                txtdriveraccpted.setText(getString(R.string.arriving));
                imgarrived.setImageResource(R.drawable.arriveddisable);
                imgPicked.setImageResource(R.drawable.pickeddisable);
                imgDropped.setImageResource(R.drawable.complete);
                try {
                    JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                    JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                    JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                    SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                    SharedHelper.putKey(context, "provider_first_name", "" + provider.optString("first_name"));
                    lblProvider.setText(provider.optString("first_name"));
                    tvServiceNumber.setText("" + provider_service.getString("service_number"));
                    tvServiceModel.setText("" + provider_service.getString("service_model"));
                    if (provider.optString("avatar").startsWith("http"))
                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                    else
                        Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                    lblServiceRequested.setText(service_type.optString("name"));
                    lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                    Picasso.get().load(URLHelper.base + service_type.optString("image")).placeholder(R.drawable.car_select)
                            .error(R.drawable.car_select).into(imgServiceRequested);
                    ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));

                    setPricesIfDiscount(service_type.optString("fixed"));

                    lblCmfrmSourceAddress.setText(pickUpLocationName);
                    lblCmfrmDestAddress.setText(dropLocationName);

                    AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                    flowValue = 4;
                    layoutChanges();
                    if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                        SharedHelper.putKey(context, "current_status", "");
                        Intent intent = new Intent(TrackActivity.this, HistoryActivity.class);
                        intent.putExtra("tag", "upcoming");
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirmDialog.dismiss();

            }
        });


    }

    public void liveNavigation(String status, String lat, String lng) {
        Log.e("LiveNavigation", "ProLat" + lat + " ProLng" + lng);
        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")
        && !lat.equalsIgnoreCase("null") && !lng.equalsIgnoreCase("null")) {
            Double proLat = Double.parseDouble(lat);
            Double proLng = Double.parseDouble(lng);

            Float rotation = 0.0f;

            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.75f)
                    .position(new LatLng(proLat, proLng))
                    .rotation(rotation)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider)
                    );

            if (providerMarker != null) {

                rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                providerMarker.remove();
                providerMarker = mMap.addMarker(markerOptions);

            }


        }

    }

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }

    public void setValuesForSourceAndDestination() {
        mapfocus.setVisibility(View.GONE);
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
                    //  frmSource.setText(source_address);
                } else {
                    //  frmSource.setText(current_address);
                }
            } else {
                // frmSource.setText(current_address);
            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!dest_lat.equalsIgnoreCase("")) {
                // destination.setText(dest_address);
                // frmDestination.setVisibility(View.GONE);
                //  frmDest.setText(dest_address);

            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")
            && !source_lat.equalsIgnoreCase("null") && !source_lng.equalsIgnoreCase("null")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("") && dest_lat != null && dest_lng != null) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }

            if (sourceLatLng != null && destLatLng != null) {
                utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
                // String url = getDirectionsUrl(sourceLatLng, destLatLng);
               /* DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);*/
                pickUpLocationName = source_address;

                String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            }
        }
    }


    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    String paymentType = "";
    String paymentId = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        btnPayNow.setEnabled(true);

        Log.d(TAG, "onActivityResult: " + requestCode + " result code " + resultCode + " ");
        // closed car and ambulance

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("paymentExample", confirm.toJSONObject().toString(4));

                        paymentType = "PAYPAL";
                        paymentId = confirm.getProofOfPayment().getPaymentId();
                        payNowPaypal();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
        }
        if (requestCode == PAY_NOW_AUTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                // Toast.makeText(activity, "card profile", Toast.LENGTH_SHORT).show();
                String result = data.getStringExtra("paymentSuccessful");

                // String serviceRequsetName1 = SharedHelper.getKey(context, "service_type_Car_Ambulance");

                //  else {

                //  sendRequest();
                //}
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            if (requestCode == 5555) {
                if (resultCode == Activity.RESULT_OK) {
                    CardInfo cardInfo = data.getParcelableExtra("card_info");
                    getCardDetailsForPayment(cardInfo);
                }
            }
        }

        if (requestCode == 5555) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        }
        if (requestCode == REQUEST_LOCATION) {

        } else {

        }


        if (requestCode == 012) { // pay trink ok paypal
            if (resultCode == Activity.RESULT_OK) {


                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {

                        Log.e("222 paymentExample", confirm.toJSONObject().getJSONObject("response").toString());

// //                     TODO: send 'confirm' to your server for verification.
// //                     see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
// //                     for more detail
                        String paymentType = "PAYPAL";
                        paymentId = confirm.getProofOfPayment().getPaymentId();
                        payNowCard(paymentType, priceTrink);

                    } catch (JSONException e) {
                        Log.e("222 paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } // --
        }


    }

    private void payNowPaypal() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("total_payment", lblTotalPrice.getText().toString().replace("€", ""));

            Log.d(TAG, "2223 payNowCard: " + object.toString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_PAYPAL_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "2223 onResponse: " + response.toString());
                utils.print("2223 PayNowRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                SharedHelper.putKey(context, "total_amount", "");
                flowValue = 6;
                layoutChanges();
                btnPayNow.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnPayNow.setEnabled(true);
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
                                utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAY_NOW");
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
                        utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.something_went_wrong));
                    }
                } else {
                    utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.please_try_again));
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

    private void getCardDetailsForPayment(CardInfo cardInfo) {

        ImageView imgPaymentType = findViewById(R.id.imgPaymentType);
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            //  imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText(getResources().getString(R.string.cash));
            btnPayNow.setVisibility(View.GONE);
            btnPayNowCash.setVisibility(View.GONE);
        } else {
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.mipmap.ic_launcher);
            lblPaymentType.setText(cardInfo.getLastFour());
            btnPayNow.setVisibility(View.VISIBLE);
            btnPayNowCash.setVisibility(View.VISIBLE);
        }
    }


    public void displayMessage(String toastString) {
        try {

            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (NullPointerException e) {

        }
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);

            } catch (Exception e) {

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DataParser parser = new DataParser();


                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {

                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            if (reqStatus != null && !reqStatus.equalsIgnoreCase("")) {
                try {
                    trackPickToDest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String etaDur = "0";


    private void trackPickToDest() throws Exception {

        Log.v("rawBody_direction", "from: " + Double.parseDouble(source_lat) + " / " + Double.parseDouble(source_lng)
                + "\n" +
                "to: " + Double.parseDouble(dest_lat) + " / " + Double.parseDouble(dest_lng));

        if (GlobalDataMethods.googleDirectionModelHashMap.size() > 0) {
            GoogleDirectionModel googleDirectionModel = GlobalDataMethods.googleDirectionModelHashMap.get(Double.parseDouble(source_lat) + " / " + Double.parseDouble(source_lng)
                    + " // " + Double.parseDouble(dest_lat) + " / " + Double.parseDouble(dest_lng));

            if (googleDirectionModel != null) {
                Log.e("onDirectionSuccessOk", "from hash map");
                onDirectionSuccessOk(googleDirectionModel.getDirection(), googleDirectionModel.getRawBody());
                return;
            }
        }

        GoogleDirection.withServerKey(SharedHelper.getKey(TrackActivity.this, "GOOGLE_KEY_MAPS"))
                .from(new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng)))
                .to(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        Log.e("onDirectionSuccessOk", "from Google");
                        GoogleDirectionModel googleDirectionModel = new GoogleDirectionModel(direction, rawBody);

                        GlobalDataMethods.googleDirectionModelHashMap.put(Double.parseDouble(source_lat) + " / " + Double.parseDouble(source_lng)
                                + " // " + Double.parseDouble(dest_lat) + " / " + Double.parseDouble(dest_lng), googleDirectionModel);

                        onDirectionSuccessOk(direction, rawBody);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });

    }

    private void onDirectionSuccessOk(Direction direction, String rawBody) {
        if (direction.isOK()) {
            try {
                Log.v("rawBody", rawBody + "");
                Log.v("direction", direction + "");

                float totalDistance = 0;
                int totalDuration = 0;
                mMap.clear();
                Route route = direction.getRouteList().get(0);
                int legCount = route.getLegList().size();
                for (int index = 0; index < legCount; index++) {
                    Leg leg = route.getLegList().get(index);

                    totalDistance = totalDistance + Float.parseFloat(leg.getDistance().getText().replace("km", "").replace("m", "").trim());

//                                totalDistance =0;
                    if (leg.getDuration().getText().contains("hour")) {
                        Log.v("splithour", leg.getDuration().getText().split("hour")[0] + " ");
                        totalDuration = totalDuration + 60 * Integer.parseInt(leg.getDuration().getText()
                                .split("hour")[0].trim());

                    } else if (leg.getDuration().getText().contains("hours")) {
                        totalDuration = totalDuration + 60 * Integer.parseInt(leg.getDuration().getText()
                                .split("hours")[0].trim().replace("m", ""));
                    } else if (leg.getDuration().getText().contains("mins")) {
                        totalDuration = totalDuration + Integer.parseInt(leg.getDuration().getText()
                                .replace("hour", "").replace("mins", "").replace("m", "").trim());
                    } else {
                        totalDuration = totalDuration + 0;
                    }


                    if (reqStatus.equals("PICKEDUP") || reqStatus.equals("DROPPED")) {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.user_markers);
                        Bitmap icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.provider);
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(icon1))
                                .rotation(360)
                                .flat(true)
                                .anchor(0.5f, 0.5f)
                                .position(leg.getStartLocation().getCoordination()));
                        if (index == legCount - 1) {
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(leg.getEndLocation().getCoordination()));
                        }
                        List<Step> stepList = leg.getStepList();
                        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(TrackActivity.this, stepList, 3, Color.BLACK, 2, Color.GRAY);
                        for (PolylineOptions polylineOption : polylineOptionList) {
                            mMap.addPolyline(polylineOption);
                        }
                        if (pickUpLocationName != null) {
                        }

                        if (dest_address != null) {
                            View marker_view2 = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(com.gsanthosh91.decoderoutekey.R.layout.custom_marker, null);
                            TextView addressDes = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.addressTxt);
                            TextView etaTxt = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.etaTxt);
                            etaTxt.setVisibility(View.VISIBLE);
                            addressDes.setText(dropLocationName);
                            if (totalDuration > 60) {
                                etaTxt.setText(convertHours(totalDuration));
                            } else {
                                etaTxt.setText(totalDuration + " mins");
                            }
                            etaDur = totalDuration + "";
                            MarkerOptions marker_opt_des = new MarkerOptions().position(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)));
                            marker_opt_des.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view2))).anchor(0.00f, 0.20f);
                            destinationMarker = mMap.addMarker(marker_opt_des);
                        }
                    } else {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.destination_marker);
                        Bitmap icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.user_markers);
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon1)).position(leg.getStartLocation().getCoordination()));
                        if (index == legCount - 1) {
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(leg.getEndLocation().getCoordination()));
                        }
                        List<Step> stepList = leg.getStepList();
                        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(TrackActivity.this, stepList, 3, Color.BLACK, 2, Color.GRAY);
                        for (PolylineOptions polylineOption : polylineOptionList) {
                            mMap.addPolyline(polylineOption);
                        }
                        if (pickUpLocationName != null) {
                        }

                        if (dest_address != null) {
                            View marker_view2 = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(com.gsanthosh91.decoderoutekey.R.layout.custom_marker, null);
                            TextView addressDes = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.addressTxt);
                            TextView etaTxt = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.etaTxt);
                            etaTxt.setVisibility(View.VISIBLE);
                            addressDes.setText(pickUpLocationName);
                            if (totalDuration > 60) {
                                etaTxt.setText(convertHours(totalDuration));
                            } else {
                                etaTxt.setText(totalDuration + " mins");
                            }

                            etaDur = totalDuration + "";
                            MarkerOptions marker_opt_des = new MarkerOptions().position(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)));
                            marker_opt_des.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view2))).anchor(0.00f, 0.20f);
                            destinationMarker = mMap.addMarker(marker_opt_des);
                        }
                    }

                }

                mMap.setOnCameraIdleListener(() -> {
                    if (sourceMarker != null) {
                        String lat = String.valueOf(sourceLatLng.latitude);
                        String lng = String.valueOf(sourceLatLng.longitude);
                        if (((lat != null) && !lat.equals("") && !lat.isEmpty() && !lat.equalsIgnoreCase("0,0")) &&
                                ((lng != null) && !lng.equals("") && !lng.isEmpty() && !lng.equalsIgnoreCase("0,0"))) {
                            Point PickupPoint = mMap.getProjection().toScreenLocation(new LatLng(sourceLatLng.latitude, sourceLatLng.longitude));
                            sourceMarker.setAnchor(PickupPoint.x < dpToPx(context, 200) ? 0.00f : 1.00f, PickupPoint.y < dpToPx(context, 100) ? 0.20f : 1.20f);
                        }

                    }
                    if (destinationMarker != null) {
                        if (((dest_lat != null) && !dest_lat.equals("") && !dest_lat.isEmpty() && !dest_lat.equalsIgnoreCase("0,0")) &&
                                ((dest_lng != null) && !dest_lng.equals("") && !dest_lng.isEmpty() && !dest_lng.equalsIgnoreCase("0,0"))) {
                            Point PickupPoint = mMap.getProjection().toScreenLocation(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)));
                            destinationMarker.setAnchor(PickupPoint.x < dpToPx(context, 200) ? 0.00f : 1.00f, PickupPoint.y < dpToPx(context, 100) ? 0.20f : 1.20f);
                        }
                    }
                });

                Log.e("min_km", "from trackPickToDest");
                lblCmfrmSourceAddress.setText(pickUpLocationName);
                lblDis.setText(totalDistance + " km");
                lblEta.setText(totalDuration + " Min");
//                                GlobalDataMethods.GTotalDuration = totalDuration;
                setCameraWithCoordinationBounds(route);
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private int dpToPx(Context context, float dpValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dpValue * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public String convertHours(int runtime) {
        int hours = runtime / 60;
        int minutes = runtime % 60; // 5 in this case.
        return hours + "h " + minutes + " m";
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getUrl(double source_latitude, double source_longitude,
                          double dest_latitude, double dest_longitude) {

        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;


        // Sensor enabled
        String sensor = "sensor=false" + "&key=" + "AIzaSyCX6R-_OJ0vIApCQ-mFjVzd5Xn9h-xmlrI";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("url", url + "");
        return url;
    }


    public void payNowCard() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("total_payment", lblTotalPrice.getText().toString().replace("€", ""));
            if (paymentType.contains("PAYPAL")) {
                object.put("payment_id", paymentId);
            }
//              object.put("payment_mode", SharedHelper.getKey(getApplicationContext(),"payment_mode"));
//              object.put("is_paid", isPaid);
            Log.d(TAG, "2223 payNowCard: " + object.toString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "2223 onResponse: " + response.toString());
                utils.print("2223 PayNowRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                SharedHelper.putKey(context, "total_amount", "");
                flowValue = 6;
                layoutChanges();
                btnPayNow.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnPayNow.setEnabled(true);
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
                                utils.displayMessage(getCurrentFocus(), getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAY_NOW");
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
                        utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.something_went_wrong));
                    }
                } else {
                    utils.displayMessage(findViewById(R.id.lblTotalPrice), getString(R.string.please_try_again));
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
}
