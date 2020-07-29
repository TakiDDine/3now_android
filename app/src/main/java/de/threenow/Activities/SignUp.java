package de.threenow.Activities;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.threenow.Helper.ConnectionHelper;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.Utilities;

import static de.threenow.IlyftApplication.trimMessage;

public class SignUp extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    String TAG = "SignUp";
    TextView txtSignIn, txtAgbBtn;
    EditText etName, etLastName, etEmail, etPassword;
    Button btnSignUp;
//    private MaterialSpinner spRegister;

    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utilities utils = new Utilities();
    String device_token, device_UDID;
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
    UIManager uiManager;

    Button btnFb, btnGoogle;

    /*----------Facebook Login---------*/
    CallbackManager callbackManager;
    ImageView backArrow;
    AccessTokenTracker accessTokenTracker;
    String UserName, UserEmail, result, FBUserID, FBImageURLString;
    JSONObject json;
    private static final int REQ_SIGN_IN_REQUIRED = 100;
    /*----------Google Login---------------*/
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 100;
    public static int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        txtSignIn = findViewById(R.id.txtSignIn);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtAgbBtn = findViewById(R.id.txt_agb_btn);
        // btnFb=findViewById(R.id.btnFb);
        // btnGoogle=findViewById(R.id.btnGoogle);
        txtSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        //btnFb.setOnClickListener(this);
        //btnGoogle.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        SpannableString content = new SpannableString(getResources().getString(R.string.conditions));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtAgbBtn.setText(content + " ");

        txtAgbBtn.setOnClickListener(this);

        getToken();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtSignIn) {
            startActivity(new Intent(SignUp.this, Login.class));
            finish();
        }
        if (v.getId() == R.id.txt_agb_btn){
            startActivity(new Intent(SignUp.this, AgbActivity.class));
        }
        if (v.getId() == R.id.btnSignUp) {

            Pattern ps = Pattern.compile(".*[0-9].*");
            Matcher firstName = ps.matcher(etName.getText().toString());


            if (etName.getText().toString().equals("") ||
                    etName.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                displayMessage(getString(R.string.first_name_empty));
            } else if (etLastName.getText().toString().equals("")||
                    etLastName.getText().toString().equalsIgnoreCase(getString(R.string.last_name))) {
                displayMessage(getString(R.string.last_name_empty));
            } else if (firstName.matches()) {
                displayMessage(getString(R.string.first_name_no_number));
            } else if (etEmail.getText().toString().equals("") ||
                    etEmail.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                displayMessage(getString(R.string.email_validation));
            } else if (!Utilities.isValidEmail(etEmail.getText().toString())) {
                displayMessage(getString(R.string.not_valid_email));
            } else if (etPassword.getText().toString().equals("") ||
                    etPassword.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                displayMessage(getString(R.string.password_validation));
            } else if (etPassword.length() < 6) {
                displayMessage(getString(R.string.password_size));
            } else {
                if (isInternet) {
                    openphonelogin();
                } else {
                    displayMessage(getString(R.string.something_went_wrong_net));
                }
            }
        }
       /* if (v.getId()==R.id.btnFb)
        {
            logInType("fb");
        }
        if(v.getId()==R.id.btnGoogle)
        {
            logInType("google");
        }*/
    }

    public void logInType(String loginType) {
        Intent intent = new Intent(SignUp.this, Login.class);
        intent.putExtra("loginTypeSignUP", loginType);
        startActivity(intent);

    }

    private void googleLogIn() {
        Log.e(TAG, "Google signin");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.e(TAG, "RC_SIGN_IN: " + RC_SIGN_IN);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void facebookLogin() {
        if (isInternet) {
            LoginManager.getInstance().logInWithReadPermissions(SignUp.this,
                    Arrays.asList("public_profile", "email"));


            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {


                        public void onSuccess(final LoginResult loginResult) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {

                                            @Override
                                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                                try {
                                                    Log.e(TAG, "id" + user.optString("id"));
                                                    Log.e(TAG, "name" + user.optString("first_name"));

                                                    String profileUrl = "https://graph.facebook.com/v2.8/" + user.optString("id") + "/picture?width=1920";


                                                    final JsonObject json = new JsonObject();
                                                    json.addProperty("device_type", "android");
                                                    json.addProperty("device_token", device_token);
                                                    json.addProperty("accessToken", loginResult.getAccessToken().getToken());
                                                    json.addProperty("device_id", device_UDID);
                                                    json.addProperty("login_by", "facebook");
                                                    json.addProperty("first_name", user.optString("first_name"));
                                                    json.addProperty("last_name", user.optString("last_name"));
                                                    json.addProperty("id", user.optString("id"));
                                                    json.addProperty("email", user.optString("email"));
                                                    json.addProperty("avatar", profileUrl);

                                                    login(json, URLHelper.FACEBOOK_LOGIN, "facebook");

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.d("facebookExp", e.getMessage());
                                                }
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,first_name,last_name,email");
                                request.setParameters(parameters);
                                request.executeAsync();
                                Log.e("getAccessToken", "" + loginResult.getAccessToken().getToken());
                                SharedHelper.putKey(SignUp.this, "accessToken", loginResult.getAccessToken().getToken());
//                                        login(loginResult.getAccessToken().getToken(), URLHelper.FACEBOOK_LOGIN, "facebook");
                            }

                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e("exceptionfacebook", exception.toString());
                            // App code
                        }
                    });
        } else {
            //mProgressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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

    public void login(final JsonObject json, final String URL, final String Loginby) {
        customDialog = new CustomDialog(SignUp.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        Log.e("url", URL + "");
        Log.e(TAG, "login: Facebook" + json);
        Ion.with(SignUp.this)
                .load(URL)
                .addHeader("X-Requested-With", "XMLHttpRequest")
//                .addHeader("Authorization",""+SharedHelper.getKey(context, "token_type")+" "+SharedHelper.getKey(context, "access_token"))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.e("result_data", result + "");
                        // do stuff with the result or error
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (e instanceof TimeoutException) {
                                login(json, URL, Loginby);
                            }
                            return;
                        }
                        if (result != null) {
                            Log.e(Loginby + "_Response", result.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(result.toString());
                                String status = jsonObject.optString("status");
//                                if (status.equalsIgnoreCase("true")) {
                                SharedHelper.putKey(SignUp.this, "token_type", jsonObject.optString("token_type"));
                                SharedHelper.putKey(SignUp.this, "access_token", jsonObject.optString("access_token"));
                                if (Loginby.equalsIgnoreCase("facebook"))
                                    SharedHelper.putKey(SignUp.this, "login_by", "facebook");
                                if (Loginby.equalsIgnoreCase("google"))
                                    SharedHelper.putKey(SignUp.this, "login_by", "google");
                                //  getProfile();
                                openphonelogin();
//                                } else {
//                                    startActivity(new Intent(ActivitySocialLogin.this, MainActivity.class));
//                                    finish();
//                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                        // onBackPressed();
                    }
                });
    }

    Dialog dialog;

    private void openphonelogin() {

        dialog = new Dialog(SignUp.this, R.style.AppTheme_NoActionBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(true);
        dialog.show();
        CountryCodePicker ccp = (CountryCodePicker) dialog.findViewById(R.id.ccp);
        Button nextIcon = dialog.findViewById(R.id.nextIcon);
        ImageView imgBack = dialog.findViewById(R.id.imgBack);
        EditText mobile_no = dialog.findViewById(R.id.mobile_no);
        final String countryCode = ccp.getDefaultCountryCode();
        final String countryIso = ccp.getSelectedCountryNameCode();

        imgBack.setOnClickListener(v -> dialog.dismiss());

        nextIcon.setOnClickListener(v -> {
            String phone = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();
            SharedHelper.putKey(getApplicationContext(), "mobile_number", phone);
            Log.v("Phonecode", phone + " ");
            Intent intent = new Intent(SignUp.this, OtpVerification.class);
            intent.putExtra("phonenumber", phone);
            startActivityForResult(intent, APP_REQUEST_CODE);
            dialog.dismiss();
        });

    }

    public void phoneLogin(PhoneNumber phoneNumber) {

        final Intent intent = new Intent(this, AccountKitActivity.class);
        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
                ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimary),
                R.color.colorPrimary,
                SkinManager.Tint.WHITE,
                0.001);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        configurationBuilder.setUIManager(uiManager);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder
                        .setInitialPhoneNumber(phoneNumber)
                        .build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (data != null) {

            if (requestCode == RC_SIGN_IN) {

//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//                Log.e(TAG,"If: "+result);
//                handleSignInResult(result);
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                Log.e(TAG, "If: " + result.toString());

            }
            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request

                dialog.dismiss();
//                SharedHelper.putKey(getApplicationContext(), "mobile", countryCodePicker.getSelectedCountryCodeWithPlus()+etMobile.getText().toString());
                registerAPI();
            }
//            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
//                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//
//                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                    @Override
//                    public void onSuccess(Account account) {
//                        Log.e(TAG, "onSuccess: Account Kit" + account.getId());
//                        Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
//                        if (AccountKit.getCurrentAccessToken().getToken() != null) {
//                            SharedHelper.putKey(getApplicationContext(), "account_kit_token",
//                                    AccountKit.getCurrentAccessToken().getToken());
//                            //SharedHelper.putKey(RegisterActivity.this, "loggedIn", "true");
//                            // Get phone number
//                            PhoneNumber phoneNumber = account.getPhoneNumber();
//                            String phoneNumberString = phoneNumber.toString();
//                            SharedHelper.putKey(getApplicationContext(), "mobile", phoneNumberString);
//                            registerAPI();
//                        } else {
//                            SharedHelper.putKey(getApplicationContext(), "account_kit_token", "");
//                            SharedHelper.putKey(getApplicationContext(), "loggedIn", "false");
//                            SharedHelper.putKey(getApplicationContext(), "email", "");
//                            SharedHelper.putKey(getApplicationContext(), "login_by", "");
//                            SharedHelper.putKey(getApplicationContext(), "account_kit_token", "");
//                            Intent goToLogin = new Intent(getApplicationContext(), Login.class);
//                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(goToLogin);
//                           finish();
//                        }
//                    }
//
//                    @Override
//                    public void onError(AccountKitError accountKitError) {
//                        Log.e(TAG, "onError: Account Kit" + accountKitError);
//                    }
//                });
//                if (loginResult != null) {
//                    SharedHelper.putKey(getApplicationContext(), "account_kit", "true");
//                } else {
//                    SharedHelper.putKey(getApplicationContext(), "account_kit", "false");
//                }
//                String toastMessage;
//                if (loginResult.getError() != null) {
//                    toastMessage = loginResult.getError().getErrorType().getMessage();
//                    // showErrorActivity(loginResult.getError());
//                } else if (loginResult.wasCancelled()) {
//                    toastMessage = "Login Cancelled";
//                } else {
//                    if (loginResult.getAccessToken() != null) {
//                        Log.e(TAG, "onActivityResult: Account Kit" + loginResult.getAccessToken().toString());
//                        SharedHelper.putKey(getApplicationContext(), "account_kit", loginResult.getAccessToken().toString());
//                        toastMessage = "Welcome to Tranxit...";
//                    } else {
//                        SharedHelper.putKey(getApplicationContext(), "account_kit", "");
//                        toastMessage = String.format(
//                                "Welcome to Tranxit...",
//                                loginResult.getAuthorizationCode().substring(0, 10));
//                    }
//                }
//            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Beginscreen", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("Google", "display_name:" + acct.getDisplayName());
            Log.d("Google", "mail:" + acct.getEmail());
            Log.d("Google", "photo:" + acct.getPhotoUrl());

            new RetrieveTokenTask().execute(acct.getEmail());
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String accessToken) {
            super.onPostExecute(accessToken);
            Log.e("Token", accessToken);
            final JsonObject json = new JsonObject();
            json.addProperty("device_type", "android");
            json.addProperty("device_token", device_token);
            json.addProperty("accessToken", accessToken);
            json.addProperty("device_id", device_UDID);
            json.addProperty("login_by", "google");

            login(json, URLHelper.GOOGLE_LOGIN, "google");

        }
    }

    private void registerAPI() {
        customDialog = new CustomDialog(SignUp.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");
            object.put("first_name", etName.getText().toString());
            object.put("last_name", etLastName.getText().toString());
            object.put("email", etEmail.getText().toString());
            object.put("password", etPassword.getText().toString());
            object.put("mobile", SharedHelper.getKey(getApplicationContext(), "mobile_number"));
            object.put("picture", "");
            object.put("social_unique_id", "");

            utils.print("InputToRegisterAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.register,
                        object,
                        response -> {
                            dialog.dismiss();
                            try {
                                displayMessage(response.getString("msg"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            utils.print("SignInResponse", response.toString());
                            SharedHelper.putKey(getApplicationContext(), "email", etEmail.getText().toString());
                            SharedHelper.putKey(getApplicationContext(), "password", etPassword.getText().toString());
                            signIn();
                        },
                        error -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;

                            if (response != null && response.data != null) {
                                utils.print("MyTest", "" + error);
                                utils.print("MyTestError", "" + error.networkResponse);
                                utils.print("MyTestError1", "" + response.statusCode);
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
                                        try {
                                            if (errorObj.optString("message")
                                                    .equalsIgnoreCase("invalid_token")) {
                                                //   Refresh token
                                            } else {
//                                                displayMessage(getString(R.string.email_exist));
                                            }
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }

                                    } else if (response.statusCode == 422) {
                                        Snackbar.make(getCurrentFocus(),
                                                R.string.email_exist, Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                        json = trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
                                            if (json.startsWith("The email")) {

                                            }
                                            //displayMessage(json);
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
                                    registerAPI();
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

    public void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(SignUp.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("grant_type", "password");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("username", SharedHelper.getKey(getApplicationContext(), "email"));
                object.put("password", SharedHelper.getKey(getApplicationContext(), "password"));
                object.put("scope", "");
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                utils.print("InputToLoginAPI", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.POST,
                            URLHelper.login,
                            object,
                            response -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                utils.print("SignUpResponse", response.toString());
                                SharedHelper.putKey(getApplicationContext(), "access_token",
                                        response.optString("access_token"));
                                SharedHelper.putKey(getApplicationContext(), "refresh_token",
                                        response.optString("refresh_token"));
                                SharedHelper.putKey(getApplicationContext(), "token_type",
                                        response.optString("token_type"));
                                getProfile();
                            },
                            error -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                String json = null;
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
                                            try {
                                                if (errorObj.optString("message")
                                                        .equalsIgnoreCase("invalid_token")) {
                                                    //Call Refresh token
                                                } else {
                                                    displayMessage(errorObj.optString("message"));
                                                }
                                            } catch (Exception e) {
                                                displayMessage(getString(R.string.something_went_wrong));
                                            }

                                        } else if (response.statusCode == 422) {

                                            json = trimMessage(new String(response.data));
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
                                        signIn();
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
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {
        if (isInternet) {
            customDialog = new CustomDialog(SignUp.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            URLHelper.UserProfile,
                            object,
                            response -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    //customDialog.dismiss();
                                    utils.print("GetProfile", response.toString());
                                SharedHelper.putKey(getApplicationContext(), "id",
                                        response.optString("id"));
                                SharedHelper.putKey(getApplicationContext(), "first_name",
                                        response.optString("first_name"));
                                SharedHelper.putKey(getApplicationContext(), "last_name",
                                        response.optString("last_name"));
                                SharedHelper.putKey(getApplicationContext(), "email",
                                        response.optString("email"));
                                SharedHelper.putKey(getApplicationContext(), "picture",
                                        URLHelper.base + "storage/app/public/" +
                                                response.optString("picture"));
                                SharedHelper.putKey(getApplicationContext(), "gender",
                                        response.optString("gender"));
                                SharedHelper.putKey(getApplicationContext(), "mobile",
                                        response.optString("mobile"));
                                SharedHelper.putKey(getApplicationContext(), "wallet_balance",
                                        response.optString("wallet_balance"));
                                SharedHelper.putKey(getApplicationContext(), "payment_mode",
                                        response.optString("payment_mode"));
                                if (!response.optString("currency")
                                        .equalsIgnoreCase("") &&
                                        response.optString("currency") != null)
                                    SharedHelper.putKey(getApplicationContext(), "currency",
                                            response.optString("currency"));
                                else
                                    SharedHelper.putKey(getApplicationContext(), "currency", "€");
                                SharedHelper.putKey(getApplicationContext(), "sos",
                                        response.optString("sos"));
                                SharedHelper.putKey(getApplicationContext(), "loggedIn",
                                        "true");
                                GoToMainActivity();
                            },
                            error -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
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
                                            try {
                                                if (errorObj.optString("message")
                                                        .equalsIgnoreCase("invalid_token")) {
                                                    refreshAccessToken();
                                                } else {
                                                    displayMessage(errorObj.optString("message"));
                                                }
                                            } catch (Exception e) {
                                                displayMessage(getString(R.string.something_went_wrong));
                                            }

                                        } else if (response.statusCode == 422) {

                                            json = trimMessage(new String(response.data));
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
                                        getProfile();
                                    }
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "" + SharedHelper.getKey(getApplicationContext(), "token_type")
                                    + " " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                            return headers;
                        }
                    };

            IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    public void getToken() {
        try {
            if (!SharedHelper.getKey(SignUp.this, "device_token").equals("") &&
                    SharedHelper.getKey(SignUp.this, "device_token") != null) {
                device_token = SharedHelper.getKey(SignUp.this, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SignUp.this, new OnSuccessListener<InstanceIdResult>() {
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
            device_UDID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    private void refreshAccessToken() {
        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(getApplicationContext(), "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.login,
                        object,
                        response -> {
                            Log.v("SignUpResponse", response.toString());
                            SharedHelper.putKey(getApplicationContext(), "access_token",
                                    response.optString("access_token"));
                            SharedHelper.putKey(getApplicationContext(), "refresh_token",
                                    response.optString("refresh_token"));
                            SharedHelper.putKey(getApplicationContext(), "token_type",
                                    response.optString("token_type"));
                            getProfile();


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;

                        if (response != null && response.data != null) {
                            SharedHelper.putKey(getApplicationContext(), "loggedIn", "false");
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

    private void GoToBeginActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(getApplicationContext(), SplashScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void GoToMainActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.etName), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
}
