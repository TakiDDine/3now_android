package de.threenow.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.PayPalService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.threenow.Fragments.UserMapFragment;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.CustomTypefaceSpan;
import de.threenow.Utils.GlobalDataMethods;
import de.threenow.Utils.ResponseListener;
import de.threenow.Utils.Utilities;
import io.fabric.sdk.android.Fabric;

import static de.threenow.IlyftApplication.trimMessage;

public class MainActivity extends AppCompatActivity implements
        UserMapFragment.HomeFragmentListener,
        ResponseListener, View.OnClickListener {


    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final int REQUEST_LOCATION = 1450;
    public Context context = MainActivity.this;
    public Activity activity = MainActivity.this;
    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    CustomDialog customDialog;
    Utilities careUtilities = Utilities.getUtilityInstance();
    GoogleApiClient mGoogleApiClient;
    String keys;
    FirebaseAnalytics firebaseAnalytics;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtWebsite;
    private TextView txtName;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private Handler mHandler;
    private String notificationMsg;
    private TextView legal_id;
    private TextView footer_item_version;

    LinearLayout home_ll_id, prfile_header_menu, ll_payment, ll_track, ll_notification, ll_yourtrips, ll_wallet, ll_help, ll_contact;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        Log.e("language3", Locale.getDefault().getDisplayLanguage());
        setContentView(R.layout.activity_main);

        initPaypal();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        keys = getString(R.string.keyPaymetGetWayTokenServer);
        String base64Key = Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            notificationMsg = intent.getExtras().getString("Notification");

        firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        Crashlytics.getInstance();

        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        legal_id = findViewById(R.id.legal_id);
        footer_item_version = findViewById(R.id.footer_item_version);
        footer_item_version.setText(careUtilities.getAppVersion(context));

        legal_id.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LegalActivity.class)));

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = findViewById(R.id.usernameTxt);
        txtWebsite = findViewById(R.id.status_txt);
        imgProfile = findViewById(R.id.img_profile);
        home_ll_id = findViewById(R.id.home_ll_id);
        prfile_header_menu = findViewById(R.id.prfile_header_menu);
        ll_payment = findViewById(R.id.ll_payment);
        ll_track = findViewById(R.id.ll_track);
        ll_notification = findViewById(R.id.ll_notification);
        ll_yourtrips = findViewById(R.id.ll_yourtrips);
        ll_wallet = findViewById(R.id.ll_wallet);
        ll_help = findViewById(R.id.ll_help);
        ll_contact = findViewById(R.id.ll_contact);


        ll_payment.setOnClickListener(this);
        ll_track.setOnClickListener(this);
        ll_notification.setOnClickListener(this);
        ll_yourtrips.setOnClickListener(this);
        ll_wallet.setOnClickListener(this);
        ll_help.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        prfile_header_menu.setOnClickListener(this);
        home_ll_id.setOnClickListener(this);

//        navHeader.setOnClickListener(view -> startActivity(new Intent(activity, Profile.class)));

        getPaymetGetWayToken(base64Key);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void initPaypal() {
//        PayPalConfiguration config = new PayPalConfiguration()
//                // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
//                // or live (ENVIRONMENT_PRODUCTION)
//                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
//                .clientId(getString(R.string.client_id_paypal) +
//                        getString(R.string.paypal_secret) +
//                        getString(R.string.paypal_mode));

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, GlobalDataMethods.config);

        startService(intent);
    }


    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        txtName.setText(SharedHelper.getKey(context, "first_name"));
        txtWebsite.setText("5");
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "picture")
                .equalsIgnoreCase(null) && SharedHelper.getKey(context, "picture") != null) {
            Picasso.get().load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        } else {
            Picasso.get().load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        }

    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        SharedHelper.putKey(context, "current_status", "");
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            return;
        }
        Runnable mPendingRunnable = () -> {
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                UserMapFragment userMapFragment = UserMapFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("Notification", notificationMsg);
                userMapFragment.setArguments(bundle);
                return userMapFragment;
            default:
                return new UserMapFragment();
        }

    }

    private void setUpNavigationView() {
        // This method will trigger on item Click of navigation menu
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                //case R.id.nav_home:
                //navItemIndex = 0;
                //CURRENT_TAG = TAG_HOME;
                //break;
//                case R.id.nav_payment:
//                    drawer.closeDrawers();
//                    startActivity(new Intent(MainActivity.this, Payment.class));
////                    finish();
//
//                    break;
//                case R.id.nav_home:
//                    drawer.closeDrawers();
//                    startActivity(new Intent(MainActivity.this, MainActivity.class));
//                    finish();
//
//                    break;
//
//                case R.id.nav_track:
//                    drawer.closeDrawers();
//                    //navigateToShareScreen(URLHelper.APP_URL);
//                    startActivity(new Intent(MainActivity.this, RunningTrip.class));
//                    break;
//                case R.id.nav_profile:
//                    drawer.closeDrawers();
//                    startActivity(new Intent(MainActivity.this, EditProfile.class));
//
//                    break;
//                case R.id.nav_notification:
//                    drawer.closeDrawers();
//                    startActivity(new Intent(MainActivity.this, NotificationTab.class));
//
//                    break;
//
//                case R.id.nav_yourtrips:
//                    drawer.closeDrawers();
//                    SharedHelper.putKey(context, "current_status", "");
//                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
//                    intent.putExtra("tag", "past");
//                    startActivity(intent);
//                    return true;
//                // break;
//                case R.id.nav_coupon:
//                    drawer.closeDrawers();
//                    SharedHelper.putKey(context, "current_status", "");
//                    startActivity(new Intent(MainActivity.this, CouponActivity.class));
//                    return true;
//                case R.id.nav_wallet:
//                    drawer.closeDrawers();
//                    SharedHelper.putKey(context, "current_status", "");
//                    startActivity(new Intent(MainActivity.this, ActivityWallet.class));
//                    return true;
//                case R.id.nav_help:
//                    drawer.closeDrawers();
//                    SharedHelper.putKey(context, "current_status", "");
//                    startActivity(new Intent(MainActivity.this, ActivityHelp.class));
////                        finish();
//                    break;
//                case R.id.nav_sos:
//                    drawer.closeDrawers();
//                    startActivity(new Intent(MainActivity.this, SosCallActivity.class));
////                        finish();
//                    break;
////                case R.id.nav_share:
//                case R.id.nav_contact:
//                    // launch new intent instead of loading fragment
////                    //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
////                    navigateToShareScreen(URLHelper.APP_URL);
//
//                    // launch new intent instead of loading fragment
//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType("text/html");
//                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"Hassanalfakhre@gmail.com"});
//                    i.putExtra(Intent.EXTRA_SUBJECT, "Contact 3Now");
//                    startActivity(Intent.createChooser(i, "Send Email"));
//
//                    drawer.closeDrawers();
//                    return true;
//                case R.id.nav_logout:
//                    drawer.closeDrawers();
//                    // launch new intent instead of loading fragment
//                    //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                    showLogoutDialog();
//                    return true;
//
//
//                case R.id.nav_media:
//                    drawer.closeDrawers();
//                    startActivity(new Intent(MainActivity.this, MediaHome.class));
//                    break;
//                default:
//                    navItemIndex = 0;
            }
            loadHomeFragment();

            return true;
        });

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem);

        }

        ActionBarDrawerToggle actionBarDrawerToggle = new
                ActionBarDrawerToggle(this, drawer, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }
                };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                        if (status.isSuccess()) {
                            Log.d("MainAct", "Google User Logged out");
                           /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();*/
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    public void logout() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(this, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "logout: " + object);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();


                if (SharedHelper.getKey(context, "login_by").equals("facebook"))
                    LoginManager.getInstance().logOut();
                if (SharedHelper.getKey(context, "login_by").equals("google"))
                    signOut();
                if (!SharedHelper.getKey(MainActivity.this, "account_kit_token").equalsIgnoreCase("")) {
                    Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(MainActivity.this, "account_kit_token"));
                    AccountKit.logOut();
                    SharedHelper.putKey(MainActivity.this, "account_kit_token", "");
                }
                SharedHelper.clearSharedPreferences(context);

                if (Locale.getDefault().getDisplayLanguage().length() > 0)
                    if (Locale.getDefault().getDisplayLanguage().contains("De"))
                        SharedHelper.putKey(MainActivity.this, "lang", "de");
                    else
                        SharedHelper.putKey(MainActivity.this, "lang", "en");


                Intent goToLogin = new Intent(activity, Login.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goToLogin);
                finish();
            }
        }, error -> {
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
                            displayMessage(errorObj.getString("message"));
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
                    logout();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                Log.e("headers: Token", headers + " ");

                return headers;
            }
        };
        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                logout();


            }
        }, error -> {
            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", "false");
                GoToBeginActivity();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setTitle(context.getString(R.string.app_name))
                    .setIcon(R.drawable.ic_launcher_round)
                    .setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton(R.string.yes, (dialog, which) -> logout());
            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(arg -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            });
            dialog.show();
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Muli-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
//        finishAffinity();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notification, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.logout_user), Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.all_notifications_marked_as_read), Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.clear_all_notifications), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getResources().getString(R.string.share_app_not_found), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray arrayResponse) {

    }

    private void getPaymetGetWayToken(String baseKey) {
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                URLHelper.PAYMENT_TOKEN,
                jsonObject,
                response -> {
                    SharedHelper.putKey(MainActivity.this,
                            "paymentAccessToken",
                            response.optString("access_token"));
                    Log.e("paymentAccessToken", response.optString("access_token"));
                },
                error -> Log.e("errorPaymentToken", error + " ")) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Basic" + " " + baseKey);
                return headers;
            }
        };
        IlyftApplication.getInstance().addToRequestQueue(objectRequest);
    }


    public void logout(View view) {
        drawer.closeDrawers();
        // launch new intent instead of loading fragment
        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
        showLogoutDialog();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_ll_id:
                drawer.closeDrawers();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();

                break;

            case R.id.prfile_header_menu:
                drawer.closeDrawers();
                startActivity(new Intent(activity, Profile.class));
                break;


            case R.id.ll_payment:
                drawer.closeDrawers();
                startActivity(new Intent(MainActivity.this, Payment.class));
                break;


            case R.id.ll_track:
                drawer.closeDrawers();
                startActivity(new Intent(MainActivity.this, RunningTrip.class));
                break;

//            case R.id.ll_profile:
//                drawer.closeDrawers();
//                startActivity(new Intent(MainActivity.this, EditProfile.class));
//                break;

            case R.id.ll_notification:
                drawer.closeDrawers();
                startActivity(new Intent(MainActivity.this, NotificationTab.class));
                break;

            case R.id.ll_yourtrips:
                drawer.closeDrawers();
                SharedHelper.putKey(context, "current_status", "");
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("tag", "past");
                startActivity(intent);
                break;

//            case R.id.ll_coupon:
//                drawer.closeDrawers();
//                SharedHelper.putKey(context, "current_status", "");
//                startActivity(new Intent(MainActivity.this, CouponActivity.class));
//                break;

            case R.id.ll_wallet:
                drawer.closeDrawers();
                SharedHelper.putKey(context, "current_status", "");
                startActivity(new Intent(MainActivity.this, ActivityWallet.class));
                break;

            case R.id.ll_help:
                drawer.closeDrawers();
                SharedHelper.putKey(context, "current_status", "");
                startActivity(new Intent(MainActivity.this, ActivityHelp.class));
                break;

//            case R.id.ll_sos:
//                drawer.closeDrawers();
//                startActivity(new Intent(MainActivity.this, SosCallActivity.class));
//                break;

            case R.id.ll_contact:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/html");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"Hassanalfakhre@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Contact 3Now");
                startActivity(Intent.createChooser(i, "Send Email"));
                drawer.closeDrawers();
                break;

//            case R.id.ll_logout:
//                drawer.closeDrawers();
//                showLogoutDialog();
//                break;

//            case R.id.ll_media:
//                drawer.closeDrawers();
//                startActivity(new Intent(MainActivity.this, MediaHome.class));
//                break;
        }
    }
}