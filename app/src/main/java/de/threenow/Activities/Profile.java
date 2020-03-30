package de.threenow.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;

import static de.threenow.Helper.LocaleManager.getLanguage;
import static de.threenow.IlyftApplication.trimMessage;


public class Profile extends AppCompatActivity implements View.OnClickListener {
    ImageView backArrow;
    TextView txtuserName, txtEdituser, txtVehiclename, txtvehicleEdit, txtPassword, txtLanguage;
    CircleImageView img_profile;
    ImageView img_car;
    Button btnLogout;
    GoogleApiClient mGoogleApiClient;
    Locale myLocale;
    TextView txtReview;
    String currentLanguage = "de", currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("language5", Locale.getDefault().getDisplayLanguage());
        setContentView(R.layout.activity_profile);
        currentLanguage = getIntent().getStringExtra(currentLang);
        Log.e("language6", Locale.getDefault().getDisplayLanguage());

        findview();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void findview() {
        img_profile = findViewById(R.id.img_profile);
        img_car = findViewById(R.id.img_car);
        backArrow = findViewById(R.id.backArrow);
        txtuserName = findViewById(R.id.txtuserName);
        txtEdituser = findViewById(R.id.txtEdituser);
        txtVehiclename = findViewById(R.id.txtVehiclename);
        txtvehicleEdit = findViewById(R.id.txtvehicleEdit);
        txtPassword = findViewById(R.id.txtPassword);
        txtLanguage = findViewById(R.id.txtLanguage);

        btnLogout = findViewById(R.id.btnLogout);

        txtReview = findViewById(R.id.txtReview);
        txtVehiclename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        txtuserName.setText(SharedHelper.getKey(Profile.this, "first_name"));

        Picasso.get().load(SharedHelper.getKey(Profile.this, "picture"))
                .placeholder(R.drawable.ic_dummy_user)
                .error(R.drawable.ic_dummy_user)
                .into(img_profile);
        if (!SharedHelper.getKey(Profile.this, "service_image").isEmpty()) {
            Picasso.get().load(SharedHelper.getKey(Profile.this, "service_image"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(img_car);
        }
        txtEdituser.setOnClickListener(this);
        txtPassword.setOnClickListener(this);
        txtLanguage.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        txtReview.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtEdituser) {
            startActivity(new Intent(Profile.this, EditProfile.class));
        }
        if (v.getId() == R.id.txtPassword) {
            startActivity(new Intent(Profile.this, ChangePassword.class));
        }

        if (v.getId() == R.id.txtReview) {
            startActivity(new Intent(Profile.this, UserReview.class));
        }
        if (v.getId() == R.id.btnLogout) {
            showLogoutDialog();
        }
        if (v.getId() == R.id.txtLanguage) {
            showRadioButtonDialog();
        }


    }

    private void showRadioButtonDialog() {
        int checked;
        String langNow = getLanguage(Profile.this);

        if (langNow.contains("en")) {
            checked = 0;
        } else {
            checked = 1;
        }

        // custom dialog
        final CharSequence[] items = {getResources().getString(R.string.English), getResources().getString(R.string.German)};

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle(getResources().getString(R.string.select_your_language));
        builder.setIcon(R.drawable.ic_lang);
        builder.setSingleChoiceItems(items, checked, (dialog, item) -> {
            Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
//            setLocale("de");
//            changeLanguage(Profile.this,"en");
            dialog.dismiss();
            if (item != checked) {
                if (getLanguage(Profile.this).contains("en")) {
                    setLocale("de");
                } else {
                    setLocale("en");
                }
            }
//            else {

//            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void changeLanguage(Context context, String language) {
        Log.e("language1", Locale.getDefault().getDisplayLanguage());

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        context.createConfigurationContext(config);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        Log.e("language2", Locale.getDefault().getDisplayLanguage());

        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }

    public void setLocale(String lang) {
        Log.e("language", Locale.getDefault().getDisplayLanguage());
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

//        SharedPrefrence.setLanguage(Profile.this,lang);
        SharedHelper.putKey(this,"lang",lang);

        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.action_logout));
            builder.setMessage(getString(R.string.logout_alert));

            builder.setPositiveButton(R.string.yes,
                    (dialog, which) -> logout());

            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();

            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(arg -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            });
            dialog.show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void logout() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.LOGOUT,
                        object,
                        response -> {


                            if (SharedHelper.getKey(getApplicationContext(), "login_by").equals("facebook"))
                                LoginManager.getInstance().logOut();
                            if (SharedHelper.getKey(getApplicationContext(), "login_by").equals("google"))
                                signOut();
                            if (!SharedHelper.getKey(Profile.this, "account_kit_token").equalsIgnoreCase("")) {
                                Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(Profile.this, "account_kit_token"));
                                AccountKit.logOut();
                                SharedHelper.putKey(Profile.this, "account_kit_token", "");
                            }


                            //SharedHelper.putKey(context, "email", "");
                            SharedHelper.clearSharedPreferences(Profile.this);
                            Intent goToLogin = new Intent(Profile.this, Login.class);
                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(goToLogin);
                            finish();
                        }, error -> {
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
                                /*refreshAccessToken();*/
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
                    public java.util.Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        Log.e("getHeaders: Token", SharedHelper.getKey(getApplicationContext(), "access_token") + SharedHelper.getKey(getApplicationContext(), "token_type"));
                        headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(getApplicationContext(), "access_token"));
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

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
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

}
