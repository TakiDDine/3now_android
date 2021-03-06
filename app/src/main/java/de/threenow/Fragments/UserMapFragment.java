package de.threenow.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import de.threenow.Activities.ChoseServiceActivity;
import de.threenow.Activities.CouponActivity;
import de.threenow.Activities.CustomGooglePlacesSearch;
import de.threenow.Activities.MainActivity;
import de.threenow.Activities.Payment;
import de.threenow.Activities.TrackActivity;
import de.threenow.Helper.ConnectionHelper;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.DataParser;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.Models.CardInfo;
import de.threenow.Models.Driver;
import de.threenow.Models.GetUserRate;
import de.threenow.Models.PlacePredictions;
import de.threenow.Models.PostUserRate;
import de.threenow.Models.RestInterface;
import de.threenow.Models.ServiceGenerator;
import de.threenow.R;
import de.threenow.Utils.GlobalDataMethods;
import de.threenow.Utils.MapAnimator;
import de.threenow.Utils.MapRipple;
import de.threenow.Utils.MyTextView;
import de.threenow.Utils.ResponseListener;
import de.threenow.Utils.Utilities;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;
import static de.threenow.IlyftApplication.trimMessage;
import static de.threenow.Utils.GlobalDataMethods.coupon_discount;
import static de.threenow.Utils.GlobalDataMethods.coupon_gd_str;


public class UserMapFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResponseListener, GoogleMap.OnCameraMoveListener {

    private static final String TAG = "UserMapFragment";
    private static final int REQUEST_LOCATION = 1450;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private final int ADD_CARD_CODE = 435;
    public String PreviousStatus = "";
    Activity activity;
    Context context;
    View rootView;
    HomeFragmentListener listener;
    double wallet_balance;
    TextView txtSelectedAddressSource;
    Utilities utils = new Utilities();
    int flowValue = 0;
    DrawerLayout drawer;
    int NAV_DRAWER = 0;
    String reqStatus = "";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    String feedBackRating;
    double height;
    double width;
    String strPickLocation = "", strPickType = "";
    int click = 1;
    boolean pick_first = true;
    Driver driver;
    //        <!-- Map frame -->
    LinearLayout mapLayout;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    Marker marker;
    Double latitude, longitude;
    String currentAddress;
    GoogleApiClient mGoogleApiClient;
    //        <!-- Source and Destination Layout-->
    LinearLayout sourceAndDestinationLayout;
    ImageView imgMenu, redMenu, mapfocus, imgBack;
    TextView frmSource, frmDest;
    LinearLayout sourceDestLayout;
    LinearLayout lnrRequestProviders;
    RecyclerView rcvServiceTypes;
    ImageView imgPaymentType;


    //       <!--1. Request to providers -->
    ImageView imgSos;
    TextView lblPaymentType, lblPromo, booking_id;
    //    Button btnRequestRides;
    TextView btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";
    RelativeLayout lnrSearchAnimation;

    LinearLayout lnrApproximate;
    Button btnRequestRideConfirm;
    TextView tvPickUpAddres, tvDropAddres;
    LinearLayout layoutSrcDest;

    //         <!--2. Approximate Rate ...-->
    CheckBox chkWallet;
    TextView lblEta, lblDis;
    TextView lblType;
    TextView lblApproxAmount, lblApproxAmountDiscount, surgeDiscount, surgeTxt;
    View lineView;
    LocationRequest mLocationRequest;
    TextView lblNoMatch;

    //         <!--3. Waiting For Providers ...-->
    Button btnCancelRide;
    RippleBackground rippleBackground;

    //         <!--4. Driver Accepted ...-->
    TextView lblBasePrice, lblExtraPrice, lblDistancePrice,
            lblTaxPrice, lblTotalPrice, lblPaymentTypeInvoice;
    ImageView imgPaymentTypeInvoice;

    //          <!--6. Rate provider Layout ...-->
    RelativeLayout rtlStaticMarker;
    ImageView imgDestination;
    TextView btnDone;
    CameraPosition cmPosition;
    String current_lat = "", current_lng = "", current_address = "", source_lat = "",
            source_lng = "", source_address = "",
            dest_lat = "", dest_lng = "", dest_address = "";
    //Internet
    ConnectionHelper helper;

    //            <!-- Static marker-->
    Boolean isInternet;
    //RecylerView
    int currentPostion = 0;
    CustomDialog customDialog;
    TextView tvZoneMsg;
    //MArkers
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();
    AlertDialog alert;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;
    ParserTask parserTask;
    String notificationTxt;
    boolean scheduleTrip = false;
    MapRipple mapRipple;
    TextView schedule_ride;
    //  MY INITIALIZATION
    MyTextView lblCmfrmSourceAddress, lblCmfrmDestAddress;
    ImageView ImgConfrmCabType;
    ImageView ivTopFav;
    @BindView(R.id.llFlow)
    FrameLayout llFlow;
    boolean isRequestProviderScreen;
    CharSequence pickUpLocationName = "";
    CharSequence dropLocationName = "";
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    private boolean mIsShowing;
    private boolean mIsHiding;
    private LatLng sourceLatLng;
    private LatLng destLatLng;

    private Marker sourceMarker;
    private Marker destinationMarker;
    private boolean isDragging;
    private boolean ClickFiretInTime = true;
    private boolean ClickFiretInTime2 = true;

    public static UserMapFragment newInstance() {
        return new UserMapFragment();
    }

    Call<ResponseBody> responseBodyCall;
    RestInterface restInterface;
    Call<GetUserRate> getUserRateCall;

    @Override
    public void onCameraMove() {
        if (GlobalDataMethods.ShowAdditionalLog)
            utils.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
        cmPosition = mMap.getCameraPosition();
        if (marker != null) {
            if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(marker.getPosition())) {
                if (GlobalDataMethods.ShowAdditionalLog)
                    utils.print("Current marker", "Current Marker is not visible");
                if (mapfocus.getVisibility() == View.INVISIBLE) {
                    mapfocus.setVisibility(View.VISIBLE);
                }
            } else {
                utils.print("Current marker", "Current Marker is visible");
                if (mapfocus.getVisibility() == View.VISIBLE) {
                    mapfocus.setVisibility(View.INVISIBLE);
                }
                if (mMap.getCameraPosition().zoom < 16.0f) {
                    if (mapfocus.getVisibility() == View.INVISIBLE) {
                        mapfocus.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            notificationTxt = bundle.getString("Notification");

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            ButterKnife.bind(this, rootView);
        }


        restInterface = ServiceGenerator.createService(RestInterface.class);
        customDialog = new CustomDialog(getActivity());
        if (activity != null && isAdded()) {
            if (customDialog != null) {
                customDialog.show();
//                new Handler().postDelayed(() -> {
                init(rootView);
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Log.e("if_permissions", "not grant");
                } else {
                    Log.e("else_permissions", "grant");
                    MapsInitializer.initialize(getActivity());
                    initMap();
                    customDialog.dismiss();
                    customDialog.cancel();
                }
//                }, 500);
            }
        }

        reqStatus = SharedHelper.getKey(context, "req_status");

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        String current_version_str = SharedHelper.getKey(activity, "current_version");
        if (current_version_str.length() > 0) {
            int current_version = Integer.parseInt(current_version_str.replace(".", ""));
            int last_version = Integer.parseInt(SharedHelper.getKey(activity, "last_version").replace(".", ""));
            Log.e("last_current_ver", last_version + " " + current_version);
            if (last_version > current_version)
                showUpdateDialog();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        try {
            listener = (HomeFragmentListener) context;


        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void init(View rootView) {

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        statusCheck();

//        <!-- Map frame -->

        schedule_ride = rootView.findViewById(R.id.schedule_ride);

        mapLayout = rootView.findViewById(R.id.mapLayout);
        drawer = activity.findViewById(R.id.drawer_layout);

//        <!-- Source and Destination Layout-->

        sourceAndDestinationLayout = rootView.findViewById(R.id.sourceAndDestinationLayout);
        sourceDestLayout = rootView.findViewById(R.id.sourceDestLayout);
        frmSource = rootView.findViewById(R.id.frmSource);
        frmDest = rootView.findViewById(R.id.frmDest);
        imgMenu = rootView.findViewById(R.id.imgMenu);
        redMenu = rootView.findViewById(R.id.redMenu);
        imgSos = rootView.findViewById(R.id.imgSos);
        mapfocus = rootView.findViewById(R.id.mapfocus);
        imgBack = rootView.findViewById(R.id.imgBack);
        txtSelectedAddressSource = rootView.findViewById(R.id.txtSelectedAddressSource);
        ivTopFav = rootView.findViewById(R.id.ivTopFav);

//        <!-- Request to providers-->

        lnrRequestProviders = rootView.findViewById(R.id.lnrRequestProviders);
        rcvServiceTypes = rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = rootView.findViewById(R.id.imgPaymentType);
        lblPaymentType = rootView.findViewById(R.id.lblPaymentType);
        lblPromo = rootView.findViewById(R.id.lblPromo);
        booking_id = rootView.findViewById(R.id.booking_id);
        btnRequestRides = rootView.findViewById(R.id.btnRequestRides);

//        <!--  Driver and service type Details-->

        lnrSearchAnimation = rootView.findViewById(R.id.lnrSearch);

//         <!--2. Approximate Rate ...-->
        lnrApproximate = rootView.findViewById(R.id.lnrApproximate);
        tvPickUpAddres = rootView.findViewById(R.id.tvSourcePoint);
        tvDropAddres = rootView.findViewById(R.id.tvDroppoint);
        layoutSrcDest = rootView.findViewById(R.id.layoutSrcDest);
        chkWallet = rootView.findViewById(R.id.chkWallet);
        lblEta = rootView.findViewById(R.id.lblEta);
        lblDis = rootView.findViewById(R.id.lblDis);
        lblType = rootView.findViewById(R.id.lblType);
        lblApproxAmount = rootView.findViewById(R.id.lblApproxAmount);
        lblApproxAmountDiscount = rootView.findViewById(R.id.lblApproxAmountDiscount);
        surgeDiscount = rootView.findViewById(R.id.surgeDiscount);
        surgeTxt = rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = rootView.findViewById(R.id.btnRequestRideConfirm);
        lineView = rootView.findViewById(R.id.lineView);

//         <!--3. Waiting For Providers ...-->
        lblNoMatch = rootView.findViewById(R.id.lblNoMatch);
        btnCancelRide = rootView.findViewById(R.id.btnCancelRide);
        rippleBackground = rootView.findViewById(R.id.content);

//           <!--5. Invoice Layout ...-->

        lblBasePrice = rootView.findViewById(R.id.lblBasePrice);
        lblExtraPrice = rootView.findViewById(R.id.lblExtraPrice);
        lblDistancePrice = rootView.findViewById(R.id.lblDistancePrice);
        lblTaxPrice = rootView.findViewById(R.id.lblTaxPrice);
        lblTotalPrice = rootView.findViewById(R.id.lblTotalPrice);
        lblPaymentTypeInvoice = rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = rootView.findViewById(R.id.imgPaymentTypeInvoice);

//            <!--Static marker-->

        rtlStaticMarker = rootView.findViewById(R.id.rtlStaticMarker);
        imgDestination = rootView.findViewById(R.id.imgDestination);
        btnDone = rootView.findViewById(R.id.btnDone);

        /* MY INITIALIZATION*/

        lblCmfrmSourceAddress = rootView.findViewById(R.id.lblCmfrmSourceAddress);
        lblCmfrmDestAddress = rootView.findViewById(R.id.lblCmfrmDestAddress);
        ImgConfrmCabType = rootView.findViewById(R.id.ImgConfrmCabType);
        tvZoneMsg = rootView.findViewById(R.id.tvZoneMsg);


//        getCards
        CardInfo cardInfo = new CardInfo();
        cardInfo.setCardId("CASH");
        cardInfo.setCardType("CASH");
        cardInfo.setLastFour("CASH");
        cardInfoArrayList.add(cardInfo);

        schedule_ride.setOnClickListener(new OnClick());
        btnRequestRides.setOnClickListener(new OnClick());
        btnRequestRideConfirm.setOnClickListener(new OnClick());
        btnCancelRide.setOnClickListener(new OnClick());
        btnDone.setOnClickListener(new OnClick());
        sourceDestLayout.setOnClickListener(new OnClick());
        frmDest.setOnClickListener(new OnClick());
        lblPaymentType.setOnClickListener(new OnClick());
        imgPaymentType.setOnClickListener(new OnClick());
        frmSource.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        mapfocus.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        imgSos.setOnClickListener(new OnClick());
        lblPromo.setOnClickListener(new OnClick());
        lnrRequestProviders.setOnClickListener(new OnClick());
        lnrApproximate.setOnClickListener(new OnClick());

        ivTopFav.setOnClickListener(view -> saveAddressDialog());

        flowValue = 0;
        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return true;

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (!reqStatus.equalsIgnoreCase("SEARCHING")) {
                    utils.print("", "Back key pressed!");
                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {

                        if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {


                            if (!current_lat.equalsIgnoreCase("") &&
                                    !current_lng.equalsIgnoreCase("")) {
                                LatLng myLocation = new LatLng(Double.parseDouble(current_lat),
                                        Double.parseDouble(current_lng));
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(myLocation).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                flowValue = 0;
                            }
                        } else {

                            exitConfirmation();

                        }
                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        try {


                            mMap.setPadding(50, 50, 50, 50);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder.build();
                            int padding = 0; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                            mMap.moveCamera(cu);
                            flowValue = 1;
                        } catch (Exception e) {
                            Log.e("exption", e.getMessage());
                        }
                    } else {
                        exitConfirmation();
                    }
                    layoutChanges();
                    return true;
                }
            }
            return false;
        });


        try {
            chkWallet.setChecked(false);

            String p_m = SharedHelper.getKey(context, "payment_mode").toString();
            if (p_m.equalsIgnoreCase("card") && cardInfo.getLastFour().length() > 0) {
                if (cardInfo.getLastFour().equals("CASH") && SharedHelper.getKey(context, "last_four").length() > 0) {
                    lblPaymentType.setText("xxxx" + SharedHelper.getKey(context, "last_four"));
                    cardInfo.setLastFour(SharedHelper.getKey(context, "last_four"));
                } else if (cardInfo.getLastFour().equals("WALLET") && SharedHelper.getKey(context, "last_four").length() > 0) {
                    chkWallet.setChecked(true);
                    lblPaymentType.setText("xxxx" + SharedHelper.getKey(context, "last_four"));
                    cardInfo.setLastFour(SharedHelper.getKey(context, "last_four"));
                } else {
                    lblPaymentType.setText("xxxx" + cardInfo.getLastFour());
                }
            } else if (p_m.length() > 0 && p_m.equalsIgnoreCase("cash")) {
                lblPaymentType.setText(getString(R.string.cash));
            } else if (p_m.length() > 0 && p_m.equalsIgnoreCase("WALLET")) {
                lblPaymentType.setText(getString(R.string.wallet));
                chkWallet.setChecked(true);
            } else if (p_m.length() > 0) {
                lblPaymentType.setText(p_m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void exitConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.confirmation))
                .setMessage(getResources().getString(R.string.do_you_really_want_to_exit_3now))
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> getActivity().finishAffinity())
                .setNegativeButton(android.R.string.no, null).show();
    }

    @SuppressWarnings("MissingPermission")
    void initMap() {
        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.provider_map);
            Log.d(TAG, "onMapReady");
            mapFragment.getMapAsync(this);

            new Handler().postDelayed(() -> getServiceList(), 500);
        } else {
            getServiceList();
        }
        setupMap();
    }

    @SuppressWarnings("MissingPermission")
    void setupMap() {

        if (mMap != null) {
            Log.e("setupMap", "if");
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(false);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }

//        else {
//            Log.e("setupMap","else");
//            Toast.makeText(activity, getResources().getString(R.string.no_map), Toast.LENGTH_SHORT).show();
//        }


    }

    @Override
    public void onLocationChanged(Location location) {
        if (foreground) {
            if (GlobalDataMethods.ShowAdditionalLog)
                Log.e("onLocationChanged", "from here!2");

            if (URLHelper.syria_change_location && (location.getLongitude() + "").contains("."))
                if ((location.getLongitude() + "").contains("32.") && (location.getLatitude() + "").contains("15.")) {//abood
                    location.setLatitude(supportChangeLocation(52.5230588, location.getLatitude()));
                    location.setLongitude(supportChangeLocation(13.4699208, location.getLongitude()));
                }

            if (marker != null) {
                marker.remove();
            }
            if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {

                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.75f)
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_currentlocation));
                marker = mMap.addMarker(markerOptions);


                current_lat = "" + location.getLatitude();
                current_lng = "" + location.getLongitude();

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
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.e("getCompleteAdd7", "from here! latitude: " + latitude + " longitude: " + longitude);
                    currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
                    source_lat = "" + latitude;
                    source_lng = "" + longitude;
                    source_address = currentAddress;
                    current_address = currentAddress;
                    frmSource.setText(currentAddress);
                    getProvidersList("");
                    value++;
                    try {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                updateLocationToAdmin(location.getLatitude() + "", location.getLongitude() + "");
            }
        }
    }

    private double supportChangeLocation(double real, double sy) {
        String realStr = Double.valueOf(real).toString();
        realStr = realStr.substring(0, realStr.indexOf('.'));

        String syStr = Double.valueOf(sy).toString();
        syStr = syStr.substring(syStr.indexOf('.'));

        return Double.parseDouble(realStr + syStr);

    }

    private void updateLocationToAdmin(String latitude, String longitude) {
        JSONObject object = new JSONObject();
        try {
            object.put("latitude", latitude);
            object.put("longitude", longitude);
//            utils.print("SendRequestUpdate", "" + object.toString());// ---------- abood
        } catch (Exception e) {
            e.printStackTrace();
        }

        IlyftApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.base + "api/user/update/location",
                        object,
                        response -> {
//                            Log.v("uploadRes", response + " ");//---------------------------------- abood
                        }, error -> {

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

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage(getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {

            Intent intentCall = new Intent(Intent.ACTION_DIAL);
            intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
            startActivity(intentCall);

        });
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> showreasonDialog());
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    String cancaltype = "";

    private void showreasonDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        RadioGroup radioCancel = view.findViewById(R.id.radioCancel);
        radioCancel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.driver) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getActivity().getResources().getString(R.string.plan_changed);
                }
                if (checkedId == R.id.vehicle) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getActivity().getResources().getString(R.string.booked_another_cab);
                }
                if (checkedId == R.id.app) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getActivity().getResources().getString(R.string.my_reason_is_not_listed);
                }
                if (checkedId == R.id.denied) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getActivity().getResources().getString(R.string.driver_denied_to_come);
                }
                if (checkedId == R.id.moving) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getActivity().getResources().getString(R.string.driver_is_not_moving);
                }
            }
        });
        builder.setView(view).setCancelable(true);
        final AlertDialog dialog = builder.create();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cancaltype.isEmpty()) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();

                } else {
                    cancalReason = reasonEtxt.getText().toString();
                    if (cancalReason.isEmpty()) {
                        reasonEtxt.setError(getActivity().getResources().getString(R.string.please_specify_reason));
                    } else {
                        cancelRequest();
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    void layoutChanges() {
        try {
            utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());
            if (lnrApproximate.getVisibility() == View.VISIBLE) {
                lnrApproximate.startAnimation(slide_down);
            }
            lnrRequestProviders.setVisibility(View.GONE);
            lnrApproximate.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);
            sourceDestLayout.setVisibility(View.GONE);
            imgMenu.setVisibility(View.GONE);
            redMenu.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            layoutSrcDest.setVisibility(View.GONE);
            if (flowValue == 0) {
                if (imgMenu.getVisibility() == View.GONE) {
                    redMenu.setVisibility(View.GONE);
                    lnrRequestProviders.setVisibility(View.VISIBLE);
                    frmSource.setOnClickListener(new OnClick());
                    frmDest.setOnClickListener(new OnClick());
                    sourceDestLayout.setOnClickListener(null);
                    if (mMap != null) {
                        mMap.clear();
                        stopAnim();
                        setupMap();
                    }
                    sourceDestLayout.setVisibility(View.GONE);
                }

                sourceDestLayout.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.VISIBLE);
                showIf();
                sourceAndDestinationLayout.setVisibility(View.VISIBLE);

            } else if (flowValue == 1) {
                frmSource.setVisibility(View.VISIBLE);
                sourceDestLayout.setVisibility(View.GONE);
                imgBack.setVisibility(View.VISIBLE);
                layoutSrcDest.setVisibility(View.GONE);
                lnrRequestProviders.startAnimation(slide_up);
                lnrRequestProviders.setVisibility(View.VISIBLE);
                Log.e("wallet_balance_log1", " " + wallet_balance);
                if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                    if (lineView != null && chkWallet != null) {
//                        lineView.setVisibility(View.VISIBLE);
//                        chkWallet.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (lineView != null && chkWallet != null) {
                        lineView.setVisibility(View.GONE);
                        chkWallet.setVisibility(View.GONE);
                    }
                }
                chkWallet.setChecked(false);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }
            } else if (flowValue == 2) {

                lnrRequestProviders.setVisibility(View.GONE);
//                frmDestination.setVisibility(View.GONE);
                sourceDestLayout.setVisibility(View.GONE);
                imgBack.setVisibility(View.VISIBLE);
                layoutSrcDest.setVisibility(View.GONE);
                chkWallet.setChecked(false);
                lnrApproximate.startAnimation(slide_up);
                lnrApproximate.setVisibility(View.VISIBLE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mMap.setPadding(0, 0, 0, lnrApproximate.getHeight());
                    }
                });
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 3) {
                lnrRequestProviders.setVisibility(View.GONE);
                imgBack.setVisibility(View.VISIBLE);
                layoutSrcDest.setVisibility(View.GONE);
                sourceDestLayout.setVisibility(View.GONE);
                //sourceAndDestinationLayout.setVisibility(View.GONE);
            } else if (flowValue == 4) {
                lnrRequestProviders.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                showIf();
                sourceDestLayout.setVisibility(View.GONE);


            } else if (flowValue == 5) {
                sourceDestLayout.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                showIf();
            } else if (flowValue == 6) {
                imgMenu.setVisibility(View.VISIBLE);
                showIf();
                sourceDestLayout.setVisibility(View.GONE);
                feedBackRating = "1";
            } else if (flowValue == 9) {
                sourceDestLayout.setVisibility(View.GONE);
                rtlStaticMarker.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showIf() {
        if (redMenu != null)
            if (existTrip && imgMenu.getVisibility() == View.VISIBLE) {
                redMenu.setVisibility(View.VISIBLE);
                MainActivity.redDrawer.setVisibility(View.VISIBLE);
            } else {
                redMenu.setVisibility(View.GONE);
                MainActivity.redDrawer.setVisibility(View.GONE);
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));

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
        googleMap.setOnCameraChangeListener(cameraPosition -> {
            LatLng target = cameraPosition.target;
            Log.e("1_map", "setOnCameraChangeListener");
            if (pick_first) {
                updateLocation(target);
                Log.e("1_map", "if");
            } else {
                Log.e("2_map", "else");
            }
        });
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                Log.e("3_map", "if");
            } else {
                //Request Location Permission
                checkLocationPermission();
                Log.e("4_map", "else");
            }
        } else {
            Log.e("5_map", "else");
            buildGoogleApiClient();
        }
    }

    private void updateLocation(LatLng centerLatLng) {
        if (GlobalDataMethods.ShowAdditionalLog)
            Log.e("count_call_Geocoder4", URLHelper.count_call_Geocoder++ + "");

        if (centerLatLng != null) {
            Geocoder geocoder = new Geocoder(context,
                    Locale.getDefault());

            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {

                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;
                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;
                }
                if (completeAddress != null) {
                    //mLocationTextView.setText(completeAddress);
                    txtSelectedAddressSource.setText(completeAddress);
                }
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.location_permission_needed)
                        .setMessage(R.string.please_accept_to_use_location_functionality)
                        .setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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

                Log.e("count_call_Geocoder5", URLHelper.count_call_Geocoder++ + "");
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

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

                Log.e("count_call_Geocoder6", URLHelper.count_call_Geocoder++ + "");
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

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
            setValuesForSourceAndDestination();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    MapsInitializer.initialize(getActivity());
                    initMap();
                    Log.e("if_permissions_2", "grant");
                } else {
//                    showPermissionReqDialog();
                    Log.e("else_permissions_2", "not grant");
                }
                break;
            case 2:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                startActivity(intent);
                break;
            case 3:
                Intent intentDial = new Intent(Intent.ACTION_DIAL);
                intentDial.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                startActivity(intentDial);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(callGPSSettingIntent);
                        });
        builder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    PlacePredictions placePredictions;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode + " Result Code " + resultCode);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST) {
            if (parserTask != null) {
                parserTask = null;
            }
            if (resultCode == Activity.RESULT_OK) {
                if (marker != null) {
                    marker.remove();
                }

                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                strPickLocation = data.getExtras().getString("pick_location");
                strPickType = data.getExtras().getString("type");


                if (strPickLocation.equalsIgnoreCase("yes")) {
                    pick_first = true;
                    mMap.clear();
                    flowValue = 9;
                    layoutChanges();
                    float zoomLevel = 16.0f; //This goes up to 21
                    stopAnim();
                } else {
                    if (placePredictions != null) {
                        if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                            source_lat = "" + placePredictions.strSourceLatitude;
                            source_lng = "" + placePredictions.strSourceLongitude;
                            source_address = placePredictions.strSourceAddress;

                            if (!placePredictions.strSourceLatitude.equalsIgnoreCase("")
                                    && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) {
                                double latitude = Double.parseDouble(placePredictions.strSourceLatitude);
                                double longitude = Double.parseDouble(placePredictions.strSourceLongitude);
                                LatLng location = new LatLng(latitude, longitude);

                                //mMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .anchor(0.5f, 0.75f)
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                                marker = mMap.addMarker(markerOptions);
                                sourceMarker = mMap.addMarker(markerOptions);
                               /* CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                            }

                        }
                        if (!placePredictions.strDestAddress.equalsIgnoreCase("")) {
                            dest_lat = "" + placePredictions.strDestLatitude;
                            dest_lng = "" + placePredictions.strDestLongitude;
                            dest_address = placePredictions.strDestAddress;
                            dropLocationName = dest_address;

                            SharedHelper.putKey(context, "current_status", "2");
                            if (source_lat != null && source_lng != null && !source_lng.equalsIgnoreCase("")
                                    && !source_lat.equalsIgnoreCase("")) {
                                String url = getUrl(Double.parseDouble(source_lat), Double.parseDouble(source_lng)
                                        , Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));

                                current_lat = source_lat;
                                current_lng = source_lng;
                                //  getNewApproximateFare("1");
                                //  getNewApproximateFare2("2");
                                FetchUrl fetchUrl = new FetchUrl();
                                fetchUrl.execute(url);
                                LatLng location = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));


                                //mMap.clear();
                                if (sourceMarker != null)
                                    sourceMarker.remove();
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .anchor(0.5f, 0.75f)
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                                marker = mMap.addMarker(markerOptions);
                                sourceMarker = mMap.addMarker(markerOptions);
                               /* CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(14).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                            }
                            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                                if (destinationMarker != null)
                                    destinationMarker.remove();
                                MarkerOptions destMarker = new MarkerOptions()
                                        .position(destLatLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                                destinationMarker = mMap.addMarker(destMarker);
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(sourceMarker.getPosition());
                                builder.include(destinationMarker.getPosition());
                                LatLngBounds bounds = builder.build();
                                int padding = 200; // offset from edges of the map in pixels
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.moveCamera(cu);

                                /*LatLng myLocation = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                            }
                        }

                        if (dest_address.equalsIgnoreCase("")) {
                            Log.e("if_dest_address: ", dest_address);
                            frmSource.setText(source_address);
                        } else {
                            Log.e("else_dest_address: ", dest_address);
                            if (cardInfoArrayList.size() > 0) {
                                getCardDetailsForPayment(cardInfoArrayList.get(0));
                                sourceDestLayout.setVisibility(View.GONE);
                            }
                        }
                        flowValue = 1;
                        getValidZone();
                        layoutChanges();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        } else if (requestCode == 0000) {
            if (resultCode == Activity.RESULT_OK) {
                lblPromo.setText(getResources().getString(R.string.promo_code_applied));
                getServiceList();
            }
        } else if (requestCode == 5555) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        } else if (requestCode == 10) {
            String result = data.getStringExtra("paymentSuccessful");
        }
    }

    public void setValuesForApproximateLayout() {
        if (isInternet) {
            String surge = SharedHelper.getKey(context, "surge");
            if (surge.equalsIgnoreCase("1")) {
                surgeDiscount.setVisibility(View.VISIBLE);
                surgeTxt.setVisibility(View.VISIBLE);
                surgeDiscount.setText(SharedHelper.getKey(context, "surge_value"));
            } else {
                surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.GONE);
            }


            lblCmfrmSourceAddress.setText(source_address);
            lblCmfrmDestAddress.setText(dest_address);
            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));

            if (coupon_gd_str != null && !coupon_gd_str.equals("") && coupon_gd_str.length() > 0) {

//                if (jsonObject.optString("success").equals("coupon available")) {
                lblApproxAmount.setPaintFlags(lblApproxAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                double total = Double.parseDouble(SharedHelper.getKey(context, "estimated_fare"));

                GlobalDataMethods.coupon_discount = GlobalDataMethods.getDiscountCoupon(total);

                double discount = total - (GlobalDataMethods.coupon_discount);

                if (discount < 0) {
                    discount = 0;
                }
                lblApproxAmountDiscount.setText(SharedHelper.getKey(context, "currency") + "" +
                        String.format(Locale.ENGLISH, "%.2f", discount));
                lblApproxAmountDiscount.setVisibility(View.VISIBLE);

            } else {// coupoun used
                coupon_gd_str = "";
                coupon_discount = 0d;
                lblApproxAmount.setPaintFlags(lblApproxAmount.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                lblApproxAmountDiscount.setText("");
                lblApproxAmountDiscount.setVisibility(View.GONE);
            }


//                Log.e("copoun", "onActivityCreated " +  "if ok..");
//                sendToServerCoupon();
//            }

            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + SharedHelper.getKey(context, "estimated_fare"));


            lblEta.setText(SharedHelper.getKey(context, "eta_time") + "");
            lblDis.setText(SharedHelper.getKey(context, "distance") + "km");
            if (!SharedHelper.getKey(context, "name").equalsIgnoreCase("")
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase(null)
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase("null")) {
                lblType.setText(SharedHelper.getKey(context, "name"));
            } else {
                lblType.setText("" + getResources().getString(R.string.sedan));
            }

            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
        }
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

        Log.e("coupon_from", "UserMapFragment");

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                URLHelper.COUPON_VERIFY,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utils.print("AddCouponRes", "" + response.toString());
                        try {
                            JSONObject jsonObject = response;
                            GlobalDataMethods.coupon_response = response;

                            if (jsonObject.optString("success").equals("coupon available")) {
                                ((TextView) rootView.findViewById(R.id.lblPromo)).setText(getResources().getString(R.string.promo_code_applied));

                                lblApproxAmount.setPaintFlags(lblApproxAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                double total = Double.parseDouble(SharedHelper.getKey(context, "estimated_fare"));

                                GlobalDataMethods.coupon_discount = GlobalDataMethods.getDiscountCoupon(total);

                                double discount = total - (GlobalDataMethods.coupon_discount);

                                if (discount < 0) {
                                    discount = 0;
                                }
                                lblApproxAmountDiscount.setText(SharedHelper.getKey(context, "currency") + "" +
                                        String.format(Locale.ENGLISH, "%.2f", discount));
                                lblApproxAmountDiscount.setVisibility(View.VISIBLE);


                            } else {// coupoun used
                                coupon_gd_str = "";
                                coupon_discount = 0d;
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


    private void getCards() {


        JSONObject object = new JSONObject();
        try {
            object.put("user_id", SharedHelper.getKey(context, "id"));
            object.put("coupon", GlobalDataMethods.coupon_gd_str);
            Log.e("coupon_from", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                URLHelper.CARD_PAYMENT_LIST,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utils.print("getCards", "" + response.toString());
//                        try {
//                                try {
//                                    JSONArray jsonArray = response;
//                                    if (jsonArray.length() > 0) {
//                                        CardInfo cardInfo = new CardInfo();
//                                        cardInfo.setCardId("CASH");
//                                        cardInfo.setCardType("CASH");
//                                        cardInfo.setLastFour("CASH");
//                                        cardInfoArrayList.add(cardInfo);
//                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                            JSONObject cardObj = jsonArray.getJSONObject(i);
//                                            cardInfo = new CardInfo();
//                                            cardInfo.setCardId(cardObj.optString("card_id"));
//                                            cardInfo.setCardType(cardObj.optString("brand"));
//                                            cardInfo.setLastFour(cardObj.optString("last_four"));
//                                            cardInfoArrayList.add(cardInfo);
//                                        }
//                                    }
//
//                                } catch (JSONException e1) {
//                                    e1.printStackTrace();
//                                }
//
//                        } catch (Exception e2) {
//                            e2.printStackTrace();
//                            CardInfo cardInfo = new CardInfo();
//                            cardInfo.setCardId("CASH");
//                            cardInfo.setCardType("CASH");
//                            cardInfo.setLastFour("CASH");
//                            cardInfoArrayList.add(cardInfo);
//                        }

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


    public void getServiceList() {
        try {
            JSONArray response = new JSONArray("[{\"id\":19,\"name\":\"Economy Mercedes C\\/B Klasse\",\"provider_name\":null,\"image\":\"public\\/uploads\\/mercedes-benz-c-klasse-t-modell-2018-schwarz.png\",\"capacity\":\"4\",\"fixed\":\"2.1\",\"price\":0,\"minute\":0,\"distance\":1,\"calculator\":\"MIN\",\"description\":\"Affordable, rides\",\"status\":0,\"bags\":\"3\"},{\"id\":27,\"name\":\"Mercedes Vito\",\"provider_name\":null,\"image\":\"public\\/uploads\\/vw-touran@2x.ac9bb5a8.png\",\"capacity\":\"8\",\"fixed\":\"7.1\",\"price\":0,\"minute\":0,\"distance\":1,\"calculator\":\"MIN\",\"description\":\"xl 8 person\",\"status\":0,\"bags\":\"5\"},{\"id\":32,\"name\":\"Mercedes V-Klasse\",\"provider_name\":null,\"image\":\"public\\/uploads\\/mb_v-klasse-1.png\",\"capacity\":\"7\",\"fixed\":\"17\",\"price\":0,\"minute\":0,\"distance\":1,\"calculator\":\"MIN\",\"description\":\"vip \",\"status\":0,\"bags\":\"7\"}]");
            currentPostion = 0;
            Log.e("ServiceListAdapter1", "from here");
            getESTIMATED_FARE_ALL_API("19", response);
            getProvidersList(SharedHelper.getKey(context, "service_type"));
            if (mMap != null)
                mMap.clear();
            setValuesForSourceAndDestination();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getApproximateFare() {

        GlobalDataMethods.SourceTripeLat = source_lat;
        GlobalDataMethods.SourceTripeLong = source_lng;


        if (dest_lat != null && dest_lat.length() > 0) {

            String constructedURL = URLHelper.ESTIMATED_FARE_ALL_API + "" +
                    "?s_latitude=" + source_lat
                    + "&s_longitude=" + source_lng
                    + "&d_latitude=" + dest_lat
                    + "&d_longitude=" + dest_lng;

            Log.e("ESTIMATED_FARE5", GlobalDataMethods.lastConstructedURLSuccess
                    .equals(constructedURL) + " / " +
                    responseSameOld);

            if (GlobalDataMethods.lastConstructedURLSuccess.equals(constructedURL) && responseSameOld != null) {// same

                try {

                    JSONObject response = responseSameOld;
                    utils.print("ApproximateResponse same", response.toString());
                    SharedHelper.putKey(context, "estimated_fare_19", responseSameOld.getJSONObject("19").optString("fare_price"));
                    SharedHelper.putKey(context, "estimated_fare_27", responseSameOld.getJSONObject("27").optString("fare_price"));
                    SharedHelper.putKey(context, "estimated_fare_32", responseSameOld.getJSONObject("32").optString("fare_price"));

                    switch (SharedHelper.getKey(context, "service_type")) {
                        case "19":
                            SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("19").optString("fare_price"));
                            break;
                        case "27":
                            SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("27").optString("fare_price"));
                            break;
                        case "32":
                            SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("32").optString("fare_price"));
                            break;
                    }

                    SharedHelper.putKey(context, "distance", response.optString("distance"));
                    SharedHelper.putKey(context, "eta_time", " " + response.optString("time").replace("mins", "Min ").replace("hours", "Stunden").replace("hour", "Stunden"));

                    setValuesForApproximateLayout();
//                    double wallet_balance = response.optDouble("wallet_balance");
//                    SharedHelper.putKey(context, "wallet_balance", "" + response.optDouble("wallet_balance"));

                    Log.e("wallet_balance_log2", " " + wallet_balance);
                    if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
//                        lineView.setVisibility(View.VISIBLE);
//                        chkWallet.setVisibility(View.VISIBLE);
                    } else {
                        lineView.setVisibility(View.GONE);
                        chkWallet.setVisibility(View.GONE);
                    }
                    flowValue = 2;
                    layoutChanges();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {// new

                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();
                JSONObject object = new JSONObject();


                Log.e("ESTIMATED_FARE1", constructedURL);
//            System.out.println("getNewApproximateFare getNewApproximateFare " + constructedURL);
                JsonObjectRequest jsonObjectRequest = new
                        JsonObjectRequest(Request.Method.GET,
                                constructedURL + "&service_type=" + SharedHelper.getKey(context, "service_type"),
                                object,
                                response -> {
                                    if (response != null) {

                                        try {
                                            if ((customDialog != null) && (customDialog.isShowing()))
                                                customDialog.dismiss();
                                            if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                                                utils.print("ApproximateResponse", response.toString());
                                                SharedHelper.putKey(context, "estimated_fare_19", responseSameOld.getJSONObject("19").optString("fare_price"));
                                                SharedHelper.putKey(context, "estimated_fare_27", responseSameOld.getJSONObject("27").optString("fare_price"));
                                                SharedHelper.putKey(context, "estimated_fare_32", responseSameOld.getJSONObject("32").optString("fare_price"));

                                                switch (SharedHelper.getKey(context, "service_type")) {
                                                    case "19":
                                                        SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("19").optString("fare_price"));
                                                        break;
                                                    case "27":
                                                        SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("27").optString("fare_price"));
                                                        break;
                                                    case "32":
                                                        SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("32").optString("fare_price"));
                                                        break;
                                                }
                                                SharedHelper.putKey(context, "distance", response.optString("distance"));
                                                SharedHelper.putKey(context, "eta_time", " " + response.optString("time").replace("mins", "Min ").replace("hours", "Stunden").replace("hour", "Stunden"));


                                                setValuesForApproximateLayout();
                                                double wallet_balance = response.optDouble("wallet_balance");
                                                SharedHelper.putKey(context, "wallet_balance", "" + response.optDouble("wallet_balance"));

                                                Log.e("wallet_balance_log3", " " + wallet_balance);
                                                if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
//                                                    lineView.setVisibility(View.VISIBLE);
//                                                    chkWallet.setVisibility(View.VISIBLE);
                                                } else {
                                                    lineView.setVisibility(View.GONE);
                                                    chkWallet.setVisibility(View.GONE);
                                                }
                                                flowValue = 2;
                                                layoutChanges();

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("approxerrorcode", error.toString() + " " + error.getMessage());
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
                                                utils.showAlert(context, errorObj.optString("message"));
                                            } catch (Exception e) {
                                                utils.showAlert(context, context.getString(R.string.something_went_wrong));
                                            }
                                        } else if (response.statusCode == 401) {
                                            refreshAccessToken("APPROXIMATE_RATE");
                                        } else if (response.statusCode == 422) {
                                            json = trimMessage(new String(response.data));
                                            if (json != "" && json != null) {
                                                utils.showAlert(context, json);
                                            } else {
                                                utils.showAlert(context, context.getString(R.string.please_try_again));
                                            }
                                        } else if (response.statusCode == 503) {
                                            utils.showAlert(context, context.getString(R.string.server_down));
                                        } else {
                                            utils.showAlert(context, context.getString(R.string.please_try_again));
                                        }

                                    } catch (Exception e) {
                                        utils.showAlert(context, context.getString(R.string.something_went_wrong));
                                    }

                                } else {
                                    utils.showAlert(context, context.getString(R.string.please_try_again));
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
            } // End else

        }

    }

    void getProvidersList(String strTag) {

        if (current_lat.length() > 0) {
            String providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                    "latitude=" + current_lat +
                    "&longitude=" + current_lng +
                    "&service=" + strTag;
            utils.print("Get all providers", "" + providers_request);
            utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));

            for (int i = 0; i < lstProviderMarkers.size(); i++) {
                lstProviderMarkers.get(i).remove();
            }

            JsonArrayRequest jsonArrayRequest = new
                    JsonArrayRequest(providers_request,
                            response -> {
                                utils.print("GetProvidersList", response.toString());
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject jsonObj = response.getJSONObject(i);
                                        utils.print("GetProvidersList", jsonObj.getString("latitude")
                                                + "," + jsonObj.getString("longitude"));
                                        if (!jsonObj.getString("latitude").equalsIgnoreCase("")
                                                && !jsonObj.getString("longitude").equalsIgnoreCase("")) {

                                            Double proLat = Double.parseDouble(jsonObj.getString("latitude"));
                                            Double proLng = Double.parseDouble(jsonObj.getString("longitude"));

                                            Float rotation = 0.0f;

                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .anchor(0.5f, 0.75f)
                                                    .position(new LatLng(proLat, proLng))
                                                    .rotation(rotation)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_provider_marker_icon));

                                            lstProviderMarkers.add(mMap.addMarker(markerOptions));

                                            builder.include(new LatLng(proLat, proLng));
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {

                                try {
                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                        try {
//                                utils.showAlert(context, errorObj.optString("message"));
                                        } catch (Exception e) {
//                                utils.showAlert(context, getString(R.string.something_went_wrong));

                                        }

                                    } else if (response.statusCode == 401) {
                                        refreshAccessToken("PROVIDERS_LIST");
                                    } else if (response.statusCode == 422) {
                                        json = trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
//                                utils.showAlert(context, json);
                                        } else {
//                                utils.showAlert(context, context.getString(R.string.please_try_again));
                                        }
                                    } else if (response.statusCode == 503) {
//                            utils.showAlert(context, context.getString(R.string.please_try_again));
                                    } else {
//                            utils.showAlert(context, context.getString(R.string.please_try_again));
                                    }

                                } catch (Exception e) {
//                        utils.showAlert(context, context.getString(R.string.something_went_wrong));

                                }

                            } else {
//                    utils.showAlert(context, context.getString(R.string.no_drivers_found));
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

            IlyftApplication.getInstance().addToRequestQueue(jsonArrayRequest);
        }
    }

    public void sendRequest() {
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("s_latitude", source_lat);
            object.put("s_longitude", source_lng);
            object.put("d_latitude", dest_lat);
            object.put("d_longitude", dest_lng);
            object.put("s_address", source_address);
            object.put("d_address", dest_address);
            object.put("service_type", SharedHelper.getKey(context, "service_type"));
            object.put("distance", SharedHelper.getKey(context, "distance"));

            object.put("schedule_date", scheduledDate);
            object.put("schedule_time", scheduledTime);

            chkWallet.setChecked(false);

            if (SharedHelper.getKey(context, "payment_mode").equals("WALLET")) {
                chkWallet.setChecked(true);
                object.put("use_wallet", 1);
                object.put("payment_mode", "WALLET");
            } else {
                chkWallet.setChecked(false);
                object.put("use_wallet", 0);
                if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                    object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                } else {
                    object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                    object.put("card_id", SharedHelper.getKey(context, "card_id"));
                }
            }


            if (coupon_gd_str.length() > 0) {
                object.put("promo_code", coupon_gd_str);
            }

            utils.print("SendRequestInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        IlyftApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.SEND_REQUEST_API,
                        object,
                        response -> {
                            btnRequestRideConfirm.setEnabled(true);
                            if (response != null) {
                                utils.print("SendRequestResponse.", response.toString());
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();

                                if (response.toString().contains("error")) {
                                    String msg = response.optString("error");
                                    if (msg.contains("Sorry in this Source location,We ")) {
                                        msg = getString(R.string.cannot_provide_service_location);
                                    }

                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                                } else if (response.optString("request_id", "").equals("")) {
                                    utils.displayMessage(getView(), response.optString("message").replace("No Drivers Found", getString(R.string.no_drivers_found)));
                                } else {
                                    SharedHelper.putKey(context, "current_status", "");
                                    SharedHelper.putKey(context, "request_id", "" + response.optString("request_id"));
                                    if (!scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase(""))
                                        scheduleTrip = true;
                                    else
                                        scheduleTrip = false;

                                    flowValue = 0;
                                    layoutChanges();

                                    Intent intent = new Intent(getActivity(), TrackActivity.class);
                                    intent.putExtra("flowValue", 3);
                                    startActivity(intent);
                                    activity.finish();
                                }
                            }
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    btnRequestRideConfirm.setEnabled(true);
                    String json = null;
                    Log.v("sendrequestresponse..", error.toString() + " ");
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        btnRequestRideConfirm.setEnabled(true);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showAlert(context, errorObj.optString("error").replace("Already Request in Progress", "Anfrage bereits in Bearbeitung")
                                            .replace("The card id field is required when payment mode is CARD.", "Das Feld Karten-ID ist erforderlich, wenn der Zahlungsmodus Karte ist."));
                                } catch (Exception e) {
                                    utils.showAlert(context, context.getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SEND_REQUEST");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.showAlert(context, json.replace("Already Request in Progress", "Anfrage bereits in Bearbeitung")
                                            .replace("The card id field is required when payment mode is CARD.", "Das Feld Karten-ID ist erforderlich, wenn der Zahlungsmodus Karte ist."));
                                } else {
                                    utils.showAlert(context, context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(context, context.getString(R.string.server_down));
                            } else {
                                utils.showAlert(context, context.getString(R.string.please_try_again));
                            }
                        } catch (Exception e) {
                            utils.showAlert(context, context.getString(R.string.something_went_wrong));
                        }
                    } else {
                        utils.showAlert(context, context.getString(R.string.please_try_again));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        Log.e("headers", headers.toString());
                        return headers;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void cancelRequest() {

        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.CANCEL_REQUEST_API,
                        object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        utils.print("CancelRequestResponse", response.toString());
                        Toast.makeText(context, getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        mapClear();
                        SharedHelper.putKey(context, "request_id", "");
                        flowValue = 0;
                        PreviousStatus = "";
                        layoutChanges();
                        setupMap();
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
                                        utils.displayMessage(getView(), errorObj.optString("message"));
                                    } catch (Exception e) {
                                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                                    }
                                    layoutChanges();
                                } else if (response.statusCode == 401) {
                                    refreshAccessToken("CANCEL_REQUEST");
                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        utils.displayMessage(getView(), json);
                                    } else {
                                        utils.displayMessage(getView(), getString(R.string.please_try_again));
                                    }
                                    layoutChanges();
                                } else if (response.statusCode == 503) {
                                    utils.displayMessage(getView(), getString(R.string.server_down));
                                    layoutChanges();
                                } else {
                                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                                    layoutChanges();
                                }

                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                                layoutChanges();
                            }

                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
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

    public void setValuesForSourceAndDestination() {
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
                    frmSource.setText(source_address);
                } else {
                    frmSource.setText(current_address);
                }
            } else {
                frmSource.setText(current_address);
            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!dest_lat.equalsIgnoreCase("")) {
                frmDest.setText(dest_address);
            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
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

    private void refreshAccessToken(final String tag) {
        JSONObject object = new JSONObject();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedHelper.putKey(getApplicationContext(), "device_token", "" + refreshedToken);
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(getActivity(), "refresh_token"));
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
                            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                            if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                                getServiceList();
                            } else if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
                                getApproximateFare();
                            } else if (tag.equalsIgnoreCase("SEND_REQUEST")) {
                                sendRequest();
                            } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                                cancelRequest();
                            } else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                                getProvidersList("");
                            }
                        }, error -> {
                    String json = "";
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        Activity activity = getActivity();
                        if (activity != null && isAdded()) {
                            SharedHelper.putKey(context, "loggedIn", "false");
                            utils.GoToBeginActivity(getActivity());
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

    private void showChooser() {
        Intent intent = new Intent(getActivity(), Payment.class);
        startActivityForResult(intent, 5555);
    }

    private void getCardDetailsForPayment(CardInfo cardInfo) {
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            //   imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText(getResources().getString(R.string.cash));
        } else if (cardInfo.getLastFour().equals("WALLET")) {
            SharedHelper.putKey(context, "payment_mode", "WALLET");
            //   imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText(getResources().getString(R.string.wallet));
        } else if (cardInfo.getLastFour().equals("PAYPAL")) {
            SharedHelper.putKey(context, "payment_mode", "PAYPAL");
            //   imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText("PAYPAL");
        } else {
            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
//            SharedHelper.putKey(context, "payment_mode", "M-Pesa");
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.mipmap.ic_launcher_round);
            lblPaymentType.setText("xxxx" + cardInfo.getLastFour());
        }
    }

    private void mapClear() {
        if (parserTask != null)
            parserTask.cancel(true);
        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    public void statusCheck() {
        final LocationManager manager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                .addOnConnectionFailedListener(connectionResult -> Log.d("Location error", "Location error " + connectionResult.getErrorCode())).build();
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
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;

                case LocationSettingsStatusCodes.CANCELED:
                    showDialogForGPSIntent();
                    break;
            }
        });
//	        }

    }


    String constructedURLOld = "";
    JSONObject responseSameOld;

    public void getNewApproximateFare(String service_type1, final MyTextView view, MyTextView serviceItemPriceCoupon) {

        if (dest_lat != null && dest_lat.length() > 0) {

            scheduledDate = "";
            scheduledTime = "";

            String constructedURL = URLHelper.ESTIMATED_FARE_ALL_API + "" +
                    "?s_latitude=" + source_lat
                    + "&s_longitude=" + source_lng
                    + "&d_latitude=" + dest_lat
                    + "&d_longitude=" + dest_lng;

            if (constructedURLOld.length() > 0 && constructedURLOld.equals(constructedURL) && responseSameOld != null) { // same
                Log.e("ESTIMATED_FARE2", "same");
                try {
                    SharedHelper.putKey(context, "distance", responseSameOld.optString("distance"));
                    SharedHelper.putKey(context, "eta_time", " " + responseSameOld.optString("time").replace("mins", "Min ").replace("hours", "Stunden").replace("hour", "Stunden"));
                    SharedHelper.putKey(context, "currency", responseSameOld.optString("currency"));

                    SharedHelper.putKey(context, "estimated_fare_19", responseSameOld.getJSONObject("19").optString("fare_price"));
                    SharedHelper.putKey(context, "estimated_fare_27", responseSameOld.getJSONObject("27").optString("fare_price"));
                    SharedHelper.putKey(context, "estimated_fare_32", responseSameOld.getJSONObject("32").optString("fare_price"));

                    switch (service_type1) {
                        case "19":
                            SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("19").optString("fare_price"));
                            break;
                        case "27":
                            SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("27").optString("fare_price"));
                            break;
                        case "32":
                            SharedHelper.putKey(context, "estimated_fare", responseSameOld.getJSONObject("32").optString("fare_price"));
                            break;
                    }
                    view.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));
                    if (coupon_gd_str != null && !coupon_gd_str.equals("") && coupon_gd_str.length() > 0) {
                        view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        double total = Double.parseDouble(SharedHelper.getKey(context, "estimated_fare"));

                        GlobalDataMethods.coupon_discount = GlobalDataMethods.getDiscountCoupon(total);

                        double discount = total - (GlobalDataMethods.coupon_discount);

                        if (discount < 0) {
                            discount = 0;
                        }
                        serviceItemPriceCoupon.setText(SharedHelper.getKey(context, "currency") + "" +
                                String.format(Locale.ENGLISH, "%.2f", discount));
                    } else {
                        view.setPaintFlags(view.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        serviceItemPriceCoupon.setText("");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void getESTIMATED_FARE_ALL_API(String service_type1, JSONArray responseArr) {

        if (dest_lat != null && dest_lat.length() > 0) {
            Log.e("getESTIMATED_ALL_API", "if");

            scheduledDate = "";
            scheduledTime = "";

            String constructedURL = URLHelper.ESTIMATED_FARE_ALL_API + "" +
                    "?s_latitude=" + source_lat
                    + "&s_longitude=" + source_lng
                    + "&d_latitude=" + dest_lat
                    + "&d_longitude=" + dest_lng;


            constructedURLOld = constructedURL;
            Log.e("ESTIMATED_FARE2", constructedURL);
            JSONObject object = new JSONObject();
//            System.out.println("getNewApproximateFare getNewApproximateFare " + constructedURL);
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            constructedURL + "&service_type=" + service_type1,
                            object,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (response != null) {
                                        if (!response.optString("time").equalsIgnoreCase("")) {
                                            try {

                                                GlobalDataMethods.lastConstructedURLSuccess = constructedURL;


                                                responseSameOld = response;

                                                utils.print("NewApproximateResponse", response.toString());

                                                SharedHelper.putKey(context, "distance", response.optString("distance"));
                                                SharedHelper.putKey(context, "eta_time", " " + response.optString("time").replace("mins", "Min ").replace("hours", "Stunden").replace("hour", "Stunden"));
                                                SharedHelper.putKey(context, "currency", response.optString("currency"));

//                                                SharedHelper.putKey(context, "estimated_fare", "25.5");

                                                SharedHelper.putKey(context, "estimated_fare_19", response.getJSONObject("19").optString("fare_price"));
                                                SharedHelper.putKey(context, "estimated_fare_27", response.getJSONObject("27").optString("fare_price"));
                                                SharedHelper.putKey(context, "estimated_fare_32", response.getJSONObject("32").optString("fare_price"));

                                                switch (service_type1) {
                                                    case "19":
                                                        SharedHelper.putKey(context, "estimated_fare", response.getJSONObject("19").optString("fare_price"));
                                                        break;
                                                    case "27":
                                                        SharedHelper.putKey(context, "estimated_fare", response.getJSONObject("27").optString("fare_price"));
                                                        break;
                                                    case "32":
                                                        SharedHelper.putKey(context, "estimated_fare", response.getJSONObject("32").optString("fare_price"));
                                                        break;
                                                }

                                                ServiceListAdapter serviceListAdapter = new ServiceListAdapter(responseArr);
                                                rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity,
                                                        LinearLayoutManager.VERTICAL, false));
                                                rcvServiceTypes.setAdapter(serviceListAdapter);
//                                                getProvidersList(SharedHelper.getKey(context, "service_type"));


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
//                            Log.i(TAG, "getHeaders param : " + headers.toString());
                            return headers;
                        }
                    };

            IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            Log.e("getESTIMATED_ALL_API", "else");
            ServiceListAdapter serviceListAdapter = new ServiceListAdapter(responseArr);
            rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity,
                    LinearLayoutManager.VERTICAL, false));
            rcvServiceTypes.setAdapter(serviceListAdapter);
//            getProvidersList(SharedHelper.getKey(context, "service_type"));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {

            if (coupon_gd_str != null && !coupon_gd_str.equals("") && coupon_gd_str.length() > 0) {
                sendToServerCoupon();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // handleCheckStatus.removeCallbacksAndMessages(null);
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
        super.onDestroy();
    }

    private void stopAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().stopAnim();
        } else {
            Toast.makeText(context, getResources().getString(R.string.map_not_ready), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3 * 1000); // time seconds
        mLocationRequest.setFastestInterval(3 * 1000);//
        mLocationRequest.setSmallestDisplacement(3f);// low changes 3 meters
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            Log.d("downloadUrl", data.toString());
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
        String sensor = "sensor=false" + "&key=" + SharedHelper.getKey(context, "GOOGLE_KEY_MAPS");

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("url", url + "");
        return url;
    }


    boolean foreground = false;

    @Override
    public void onPause() {
        foreground = false;
        Log.e("lifcycle1", "onPause");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        super.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        foreground = true;

        try {
            if (mGoogleApiClient != null)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this::onLocationChanged);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("lifcycle1", "onResume");
        super.onResume();
        if (!SharedHelper.getKey(context, "wallet_balance").equalsIgnoreCase("")) {
            wallet_balance = Double.parseDouble(SharedHelper.getKey(context, "wallet_balance"));
        }

        Log.e("wallet_balance_log4", " " + wallet_balance);
        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
            if (lineView != null && chkWallet != null) {
//                lineView.setVisibility(View.VISIBLE);
//                chkWallet.setVisibility(View.VISIBLE);
            }
        } else {
            if (lineView != null && chkWallet != null) {
                lineView.setVisibility(View.GONE);
                chkWallet.setVisibility(View.GONE);
            }
        }

        getPastTripRate();
        getRunningTripList();
    }

    boolean existTrip = false;

    private void getRunningTripList() {

        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URLHelper.CURRENT_TRIP,
                        response -> {
                            utils.print("getOnGoingTrip", response.toString());
                            if (response != null && response.length() > 0) {
                                existTrip = true;
                                showIf();
                                Intent intent = new Intent(context, TrackActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.e("Intent", "" + response.toString());
                                intent.putExtra("post_value", response.toString());
                                intent.putExtra("tag", "past_trips");
                                startActivity(intent);
                                activity.finish();
                            } else {
                                existTrip = false;
                                showIf();
                            }
                        }, error -> {
                    existTrip = false;
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

    String requestWith = "XMLHttpRequest";

    void getPastTripRate() {
        Log.e("Method", "getPastTripRate");

        String auth = "Bearer " + SharedHelper.getKey(context, "access_token");
        getUserRateCall = restInterface.getUserRate(requestWith, auth);
        getUserRateCall.enqueue(new Callback<GetUserRate>() {
            @Override
            public void onResponse(Call<GetUserRate> call,
                                   retrofit2.Response<GetUserRate> response) {
                try {

                    Log.e("2225", response.code() + "");

                    if (response.message() != null)
                        Log.e("2225", response.message() + "");

                    if (response.body().getPaid() != null)
                        Log.e("2225", response.body().getPaid() + "");

                    if (response.body().getRequest_id() != null)
                        Log.e("2225", response.body().getRequest_id() + "");

                    if (response.body().getUser_rated() != null)
                        Log.e("2225", response.body().getUser_rated() + "");

//                    Log.e("2225", response.code() + " " + response.message() + " getPaid: " + response.body().getPaid() + " getRequest_id: " + response.body().getRequest_id() + " getUser_rated: " + response.body().getUser_rated());
                } catch (Exception e) {
                    Log.e(UserMapFragment.class.getName(), e.getMessage());
                    e.printStackTrace();

                }
                if (response.code() == 200) {
                    String requestId = response.body().getRequest_id();
                    String providerImage = response.body().getProvider_picture();
                    String providerName = response.body().getProvider_name();
                    if (response.body().getPaid().equals("1")) {
                        if (response.body().getUser_rated().equals("0")) {
                            showTripRateDialog(requestId, providerName, providerImage);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserRate> call, Throwable t) {

            }
        });
    }

    Dialog userRateDialog;
    Dialog updateDialog;

    void showUpdateDialog() {
        updateDialog = new Dialog(getActivity());
        updateDialog.setContentView(R.layout.update_layout);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imCross = updateDialog.findViewById(R.id.imCross);
        imCross.setVisibility(View.VISIBLE);
        imCross.setOnClickListener(view -> updateDialog.dismiss());

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

    void showTripRateDialog(String requestId, String proName, String proImage) {
        Log.e("Method", "showTripRateDialog");
        userRateDialog = new Dialog(context);
        userRateDialog.setContentView(R.layout.user_rate_dailog);

        CircleImageView ivProviderImg;
        TextView tvProviderName, btnRate;
        RatingBar rbProvider;
        EditText etComment;


        userRateDialog.show();
        userRateDialog.setCancelable(false);

        ivProviderImg = userRateDialog.findViewById(R.id.ivProviderImg);
        tvProviderName = userRateDialog.findViewById(R.id.tvProviderName);
        btnRate = userRateDialog.findViewById(R.id.btnRate);
        rbProvider = userRateDialog.findViewById(R.id.rbProvider);
        etComment = userRateDialog.findViewById(R.id.etComment);

        rbProvider.setRating(Float.parseFloat("5"));

        if (proImage != null && !proImage.equalsIgnoreCase("null")) {
            Picasso.get().load(URLHelper.image_url_signature + proImage).error(R.drawable.ic_dummy_user).placeholder(R.drawable.ic_dummy_user)
                    .resize(100, 100)
                    .into(ivProviderImg);
        }

        tvProviderName.setText(context.getString(R.string.rate_your_trip_with) + " " + proName);

        btnRate.setOnClickListener(v -> {
            int rate = (int) rbProvider.getRating();
            String com = etComment.getText().toString();
            if (rbProvider.getRating() > 1) {
                postPastTripRate(requestId, rate, com);
            } else {
                postPastTripRate(requestId, 1, com);
            }

        });

    }


    void postPastTripRate(String requestId, int rating, String comment) {
        Log.e("Method", "postPastTripRate");
        customDialog.show();
        customDialog.setCancelable(false);
        String auth = "Bearer " + SharedHelper.getKey(context, "access_token");
        PostUserRate postUserRate = new PostUserRate();
        postUserRate.setRequest_id(requestId);
        postUserRate.setRating(rating);
        postUserRate.setComment(comment);
        responseBodyCall = restInterface.postUserRate(requestWith, auth, postUserRate);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("s_click_onResponse", response + "");
                if (response.code() == 200) {
                    if (customDialog != null && customDialog.isShowing()) {
                        customDialog.dismiss();
                    }
                    if (userRateDialog != null && userRateDialog.isShowing())
                        userRateDialog.dismiss();
                    if (mMap != null)
                        mMap.clear();
                    flowValue = 0;
                    layoutChanges();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("f_click_onResponse", t + "");
                customDialog.dismiss();
            }
        });
    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray response) {
        if (strTag.equalsIgnoreCase("Get Services")) {
            utils.print("GetServices", response.toString());
            if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                SharedHelper.putKey(context, "service_type", "" +
                        response.optJSONObject(0).optString("id"));
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            if (response.length() > 0) {
                currentPostion = 0;
                Log.e("ServiceListAdapter2", "from here");
                ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity,
                        LinearLayoutManager.VERTICAL, false));
                rcvServiceTypes.setAdapter(serviceListAdapter);
            } else {
                utils.displayMessage(getView(), getString(R.string.no_service));
            }
            mMap.clear();
            setValuesForSourceAndDestination();
        }
    }

    private void saveAddressDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.location_storage_home_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioGroup rgLocationChooser;
        RadioButton rbHome, rbWork, rbOther;
        TextView tvAddressLocation, tvCancelDialog, tvSaveAddressLocation;

        rgLocationChooser = dialog.findViewById(R.id.rgLocationChooser);
        rbHome = dialog.findViewById(R.id.rbHome);
        rbWork = dialog.findViewById(R.id.rbWork);
        rbOther = dialog.findViewById(R.id.rbOther);

        rgLocationChooser.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == R.id.rbHome) {

            } else if (checkedId == R.id.rbWork) {

            } else {

            }
        });

        tvAddressLocation = dialog.findViewById(R.id.tvAddressLocation);
        tvCancelDialog = dialog.findViewById(R.id.tvCancelDialog);
        tvSaveAddressLocation = dialog.findViewById(R.id.tvSaveAddressLocation);

        tvAddressLocation.setText(source_address + "");
        tvCancelDialog.setOnClickListener(view -> dialog.dismiss());

        tvSaveAddressLocation.setOnClickListener(view -> {
            int selectedId = rgLocationChooser.getCheckedRadioButtonId();
            String locationType = "";
            if (selectedId == rbHome.getId()) {
                locationType = "Home";
            }
            if (selectedId == rbWork.getId()) {
                locationType = "Work";
            }
            if (selectedId == rbOther.getId()) {
                locationType = "Other";
            }
            saveLocationAddress(locationType);
            new Handler().postDelayed(() -> dialog.dismiss(), 300);
        });

        dialog.show();
    }

    private void saveLocationAddress(String locationType) {
        String addressLat = current_lat;
        String addressLng = current_lng;
        String addressLocation = current_address;
        String type = locationType;
        String id = SharedHelper.getKey(getActivity(), "id");

        if (isInternet) {
            customDialog = new CustomDialog(getContext());
            if (customDialog != null)
                customDialog.show();

            JSONObject object = new JSONObject();
            try {
                object.put("location_type", type);
                object.put("user_id", id);
                object.put("address", addressLocation);
                object.put("longitude", addressLat);
                object.put("latitude", addressLng);
                utils.print("Save_Location", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                    URLHelper.SAVE_LOCATION,
                    object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if ((customDialog != null) && customDialog.isShowing())
                                customDialog.dismiss();
                            utils.print("Save_Location Response", response.toString());
                            SharedHelper.putKey(context, type + "_address", addressLocation);
                            // callSuccess();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    Log.e("Save_Location" + this.getClass().getName(), "Error_Favourite" + error.getMessage());

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
//                            getProfile();
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

    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public interface HomeFragmentListener {
    }

    private long mLastClickTime = 0;

    class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgPaymentType:
                    showChooser();
                    break;
                case R.id.frmSource:
                    Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent.putExtra("cursor", "source");
                    intent.putExtra("s_address", frmSource.getText().toString());
                    intent.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;

                case R.id.sourceDestLayout:
                case R.id.frmDest:
                    // mis-clicking prevention, using threshold of 1000 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    // do your magic here
                    Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent2.putExtra("cursor", "destination");
                    intent2.putExtra("s_address", frmSource.getText().toString());
                    intent2.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.lblPaymentType:
//                    lblPaymentType
                    showChooser();
                    break;
                case R.id.lblPromo:
//                    lblPaymentType
                    Intent intentCouponActivity = new Intent(getActivity(), CouponActivity.class);
                    startActivityForResult(intentCouponActivity, 0000);

                    break;
                case R.id.btnRequestRides:
                    if (ClickFiretInTime2) {
                        scheduledDate = "";
                        scheduledTime = "";
                        if (!frmSource.getText().toString().equalsIgnoreCase("") &&
                                !frmDest.getText().toString().equalsIgnoreCase("")) {
                            getApproximateFare();
                            sourceDestLayout.setOnClickListener(new OnClick());
                        } else {
                            Toast.makeText(context, getResources().getString(R.string.please_enter_both_pickup_and_drop_locations), Toast.LENGTH_SHORT).show();
                        }
                    }
                    ClickFiretInTime2 = false;

                    new Handler().postDelayed(() -> ClickFiretInTime2 = true, 1000);

                    break;
                case R.id.schedule_ride:
                    if (!frmSource.getText().toString().equalsIgnoreCase("") &&
                            !frmDest.getText().toString().equalsIgnoreCase("")) {

                        Intent i = new Intent(getApplicationContext(), ChoseServiceActivity.class);

                        chkWallet.setChecked(false);

                        if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                            i.putExtra("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                        } else if (SharedHelper.getKey(context, "payment_mode").equals("WALLET")) {
                            i.putExtra("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                            chkWallet.setChecked(true);
                        } else {
                            i.putExtra("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                            i.putExtra("card_id", SharedHelper.getKey(context, "card_id"));
                        }

                        if (chkWallet.isChecked()) {
                            i.putExtra("use_wallet", 1);
                        } else {
                            i.putExtra("use_wallet", 0);
                        }

                        i.putExtra("s_latitude", source_lat);
                        i.putExtra("s_longitude", source_lng);
                        i.putExtra("d_latitude", dest_lat);
                        i.putExtra("d_longitude", dest_lng);
                        i.putExtra("s_address", source_address);
                        i.putExtra("d_address", dest_address);
                        i.putExtra("distance", SharedHelper.getKey(context, "distance"));


                        startActivity(i);
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.please_enter_both_pickup_and_drop_locations),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnRequestRideConfirm:
                    frmDest.setOnClickListener(null);
                    frmSource.setOnClickListener(null);
//                    sourceDestLayout.setClickable(false);
                    SharedHelper.putKey(context, "name", "");
                    scheduledDate = "";
                    scheduledTime = "";
                    btnRequestRideConfirm.setEnabled(false);

                    if (ClickFiretInTime) {
                        sendRequest();
                    }
                    ClickFiretInTime = false;

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            ClickFiretInTime = true;
                        }
                    }, 1000);
                    break;
                case R.id.btnCancelRide:
                    showCancelRideDialog();
                    break;
                case R.id.imgSos:
                    showSosPopUp();
                    break;
                case R.id.btnDone:
                    pick_first = true;
                    try {
                        utils.print("centerLat", cmPosition.target.latitude + "");
                        utils.print("centerLong", cmPosition.target.longitude + "");

                        Geocoder geocoder = null;
                        List<Address> addresses;
                        Log.e("count_call_Geocoder7", URLHelper.count_call_Geocoder++ + "");
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        String city = "", state = "", address = "";

                        try {
                            addresses = geocoder.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (strPickType.equalsIgnoreCase("source")) {
                            source_address = "" + address + "," + city + "," + state;
                            source_lat = "" + cmPosition.target.latitude;
                            source_lng = "" + cmPosition.target.longitude;
                            if (dest_lat.equalsIgnoreCase("")) {
                                Toast.makeText(context, getResources().getString(R.string.select_destination), Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", source_address);
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {

                                source_lat = "" + cmPosition.target.latitude;
                                source_lng = "" + cmPosition.target.longitude;

                                mMap.clear();
//                            setValuesForSourceAndDestination();
                                flowValue = 1;
                                layoutChanges();
                                strPickLocation = "";
                                strPickType = "";
                                getServiceList();

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);

                            }
                        } else {
                            dest_lat = "" + cmPosition.target.latitude;
                            if (dest_lat.equalsIgnoreCase(source_lat)) {
                                Toast.makeText(context, getResources().getString(R.string.both_source_and_destination_are_same), Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", frmSource.getText().toString());
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {
                                if (placePredictions != null) {
                                    if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                                        source_lat = "" + placePredictions.strSourceLatitude;
                                        source_lng = "" + placePredictions.strSourceLongitude;
                                        source_address = placePredictions.strSourceAddress;
                                    }
                                }
                                dest_address = "" + address + "," + city + "," + state;
                                dest_lat = "" + cmPosition.target.latitude;
                                dest_lng = "" + cmPosition.target.longitude;
                                dropLocationName = dest_address;
                                mMap.clear();
//                            setValuesForSourceAndDestination();
                                flowValue = 1;
                                layoutChanges();
                                strPickLocation = "";
                                strPickType = "";
                                getServiceList();

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.cant_get_address_try_again), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.imgBack:
                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 0;
                        isRequestProviderScreen = false;
                        sourceDestLayout.setVisibility(View.VISIBLE);
                        getProvidersList("");
                        frmSource.setOnClickListener(new OnClick());
                        frmDest.setOnClickListener(new OnClick());
                        sourceDestLayout.setOnClickListener(null);
                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            sourceDestLayout.setVisibility(View.VISIBLE);
                        }
                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        isRequestProviderScreen = true;
                        frmSource.setOnClickListener(new OnClick());
                        frmDest.setOnClickListener(new OnClick());
                        sourceDestLayout.setOnClickListener(null);
                        flowValue = 1;
                    }
                    layoutChanges();
                    break;
                case R.id.imgMenu:
                    if (NAV_DRAWER == 0) {
                        if (drawer != null)
                            drawer.openDrawer(Gravity.LEFT);
                    } else {
                        NAV_DRAWER = 0;
                        if (drawer != null)
                            drawer.closeDrawers();
                    }
                    break;
                case R.id.mapfocus:
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
                    break;

            }
        }
    }


    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.service_type_list_item, null);

            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            utils.print("Title: ", "" +
                    jsonArray.optJSONObject(position).optString("name")
                    + " Image: " + jsonArray.optJSONObject(position).optString("image")
                    + " Grey_Image:" + jsonArray.optJSONObject(position).optString("grey_image"));

            holder.serviceItem.setText(jsonArray.optJSONObject(position).optString("name").replace("Economy Mercedes C/B Klasse", "Economy\nMercedes C/B Klasse"));
            System.out.println("POSITION IS CALLEDD " + position);


            if (position == 0) {
                holder.serviceImg.setImageDrawable(getResources().getDrawable(R.drawable.car1));
                holder.bagCapacity.setText("3");
            } else if (position == 1) {
                holder.serviceImg.setImageDrawable(getResources().getDrawable(R.drawable.car23));
                holder.bagCapacity.setText("5");
            } else if (position == 2) {
                holder.serviceImg.setImageDrawable(getResources().getDrawable(R.drawable.car23));
                holder.bagCapacity.setText("7");
            }

            getNewApproximateFare(jsonArray.optJSONObject(position)
                    .optString("id"), holder.serviceItemPrice, holder.serviceItemPriceCoupon);


            if (position == currentPostion) {
                SharedHelper.putKey(context, "service_type", "" +
                        jsonArray.optJSONObject(position).optString("id"));

                holder.selector_background.setBackgroundResource(R.drawable.selected_service_item);
                holder.serviceItem.setTextColor(getResources().getColor(R.color.text_color_white));
                holder.serviceCapacity.setText(jsonArray.optJSONObject(position).optString("capacity"));

            } else {
                holder.selector_background.setBackgroundResource(R.drawable.normal_service_item);
                holder.serviceItem.setTextColor(getResources().getColor(R.color.black));
                holder.serviceCapacity.setText(jsonArray.optJSONObject(position).optString("capacity"));
            }


            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(view -> {
                currentPostion = Integer.parseInt(view.getTag().toString());
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(currentPostion).optString("id"));
                SharedHelper.putKey(context, "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                notifyDataSetChanged();

                utils.print("service_typeCurrentPosition", "" + SharedHelper.getKey(context, "service_type"));
                utils.print("Service name", "" + SharedHelper.getKey(context, "name"));
//                getProvidersList(SharedHelper.getKey(context, "service_type"));
            });
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView serviceItem, serviceCapacity, bagCapacity;
            MyTextView serviceItemPrice, serviceItemPriceCoupon;
            ImageView serviceImg;
            LinearLayout linearLayoutOfList;
            FrameLayout selector_background;

            public MyViewHolder(View itemView) {
                super(itemView);
                serviceItem = itemView.findViewById(R.id.service_car_type);
                serviceCapacity = itemView.findViewById(R.id.serviceCapacity);
                bagCapacity = itemView.findViewById(R.id.bagCapacity);
                serviceImg = itemView.findViewById(R.id.serviceImg);
                linearLayoutOfList = itemView.findViewById(R.id.LinearLayoutOfList);
                selector_background = itemView.findViewById(R.id.selector_background);
                serviceItemPrice = itemView.findViewById(R.id.serviceItemPrice);
                serviceItemPriceCoupon = itemView.findViewById(R.id.serviceItemPriceCoupon);
                height = itemView.getHeight();
                width = itemView.getWidth();
            }
        }
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
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

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                if (jObject.toString().contains("error_message"))
                GlobalDataMethods.registerBug(Arrays.toString(Thread.currentThread().getStackTrace()) + "\r\n" + jsonData[0].toString());
                Log.d("ParserTask", jsonData[0].toString());

                saveLastTripInSH(jObject);

                DataParser parser = new DataParser();
                Log.d("1_ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("2_ParserTask", "Executing routes");
                Log.d("3_ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("4_ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            String distance = "";
            String duration = "";
            isDragging = false;
            // Traversing through all the routes
            if (result != null)
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) {
                            duration = (String) point.get("duration");
                            continue;
                        }


                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                        builder.include(position);
                    }

                    if (!source_lat.equalsIgnoreCase("") &&
                            !source_lng.equalsIgnoreCase("")) {
                        LatLng location = new LatLng(Double.parseDouble(source_lat),
                                Double.parseDouble(source_lng));
                        //mMap.clear();
                        if (sourceMarker != null)
                            sourceMarker.remove();
                        MarkerOptions markerOptions = new MarkerOptions()
                                .anchor(0.5f, 0.75f)
                                .position(location).draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.round_source));
                        marker = mMap.addMarker(markerOptions);
                        sourceMarker = mMap.addMarker(markerOptions);

                    }
                    if (!dest_lat.equalsIgnoreCase("") &&
                            !dest_lng.equalsIgnoreCase("")) {
                        destLatLng = new LatLng(Double.parseDouble(dest_lat),
                                Double.parseDouble(dest_lng));
                        if (destinationMarker != null)
                            destinationMarker.remove();
                        MarkerOptions destMarker = new MarkerOptions()
                                .position(destLatLng).draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.square_desti));
                        destinationMarker = mMap.addMarker(destMarker);

                        mMap.setPadding(20, 20, 20, 20);

                        builder.include(sourceMarker.getPosition());
                        builder.include(destinationMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        int padding = 0; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                        mMap.moveCamera(cu);


                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLACK);


                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                }
            tvPickUpAddres.setText(source_address);
            tvDropAddres.setText(dest_address);
            if (lineOptions != null && points != null) {
                mMap.addPolyline(lineOptions);
//                startAnim(points);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private void saveLastTripInSH(JSONObject jObject) {
        try {
            String DESTINATION_ADDRESS, SOURCE_ADDRESS;

            DESTINATION_ADDRESS = jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address").toString();
            SOURCE_ADDRESS = jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address").toString();


            SharedHelper.putKey(context, "DESTINATION_ADDRESS", DESTINATION_ADDRESS);
            SharedHelper.putKey(context, "SOURCE_ADDRESS", SOURCE_ADDRESS);


            Log.d("ParsTask end_address", jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address").toString());
            Log.d("ParsTask start_address", jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address").toString());
        } catch (Exception e) {

        }
    }


    public void getValidZone() {

        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("s_latitude", source_lat);
            object.put("s_longitude", source_lng);
            object.put("d_latitude", dest_lat);
            object.put("d_longitude", dest_lng);

            utils.print("ValidZoneInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        IlyftApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.VALIDZONE,
                        object,
                        response -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            if (response != null) {

                                utils.print("ValidZoneResponse", response.toString());
                                if (response.optString("status").equalsIgnoreCase("1")) {

                                    rcvServiceTypes.setVisibility(View.VISIBLE);
                                    tvZoneMsg.setVisibility(View.GONE);
                                    getServiceList();
                                } else {
                                    rcvServiceTypes.setVisibility(View.GONE);
                                    tvZoneMsg.setVisibility(View.VISIBLE);
                                    tvZoneMsg.setText(response.optString("error"));
                                }
                            }
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    Log.v("sendrequestresponse...", error.toString() + " ");
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showAlert(context, errorObj.optString("error"));
                                } catch (Exception e) {
                                    utils.showAlert(context, context.getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SEND_REQUEST");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.showAlert(context, json);
                                } else {
                                    utils.showAlert(context, context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(context, context.getString(R.string.server_down));
                            } else {
                                utils.showAlert(context, context.getString(R.string.please_try_again));
                            }
                        } catch (Exception e) {
                            utils.showAlert(context, context.getString(R.string.something_went_wrong));
                        }
                    } else {
                        utils.showAlert(context, context.getString(R.string.please_try_again));
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

}


