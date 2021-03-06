package de.threenow.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.StringRequest;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.threenow.Helper.ConnectionHelper;
import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.Utilities;

import static de.threenow.Activities.AgbActivity.setWindowFlag;
import static de.threenow.IlyftApplication.trimMessage;


public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    Dialog dialog;
    String TAG = "ForgetPassword";
    public Context context = ForgotPassword.this;
    ImageButton backArrow;
    TextInputLayout newPasswordLayout, confirmPasswordLayout, OtpLay;
    LinearLayout ll_resend;
    EditText newPassowrd, confirmPassword, OTP;
    EditText email;
    EditText mobile_no;
    CustomDialog customDialog;
    String validation = "",
            str_newPassword,
            str_confirmPassword,
            id,
            str_email = "",
            str_otp,
            server_opt,
            getemail,
            getmobile,
            str_number;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView note_txt;
    Boolean fromActivity = false;
    Button resend, nextIcon;

    Utilities utils = new Utilities();
    EditText number;

    public static int APP_REQUEST_CODE = 99;
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
    UIManager uiManager;
    String phoneNumberString;

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
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


//        etEmail=findViewById(R.id.etEmail);

    }

    private void findViewById() {

        mobile_no = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        backArrow=findViewById(R.id.imgBack);
        nextIcon =findViewById(R.id.nextIcon);

        note_txt =  findViewById(R.id.note);
        newPassowrd = findViewById(R.id.new_password);
        OTP = findViewById(R.id.otp);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordLayout =  findViewById(R.id.confirm_password_lay);
        OtpLay =  findViewById(R.id.otp_lay);
        newPasswordLayout =  findViewById(R.id.new_password_lay);
        resend =  findViewById(R.id.resend);
        ll_resend =  findViewById(R.id.ll_resend);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        str_email = SharedHelper.getKey(ForgotPassword.this, "email");
        email.setText(str_email);
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nextIcon.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextIcon:

                str_email = email.getText().toString();
                str_number = number.getText().toString();
                if (validation.equalsIgnoreCase("")) {
                    if (email.getText().toString().equals("")) {
                        displayMessage(getString(R.string.email_validation));
                    } else if (!Utilities.isValidEmail(email.getText().toString())) {
                        displayMessage(getString(R.string.not_valid_email));
                    }
                    else {
                        if (isInternet) {
                            forgetPassword();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }

                    }
                } else {
                    str_newPassword = newPassowrd.getText().toString();
                    str_confirmPassword = confirmPassword.getText().toString();
                    str_otp = OTP.getText().toString();
                    if (str_newPassword.equals("") || str_newPassword.equalsIgnoreCase(getString(R.string.new_password))) {
                        displayMessage(getString(R.string.password_validation));
                    } else if (str_newPassword.length() < 6) {
                        displayMessage(getString(R.string.password_size));
                    } else if (str_confirmPassword.equals("") || str_confirmPassword.equalsIgnoreCase(getString(R.string.confirm_password)) || !str_newPassword.equalsIgnoreCase(str_confirmPassword)) {
                        displayMessage(getString(R.string.confirm_password_validation));
                    } else if (str_confirmPassword.length() < 6) {
                        displayMessage(getString(R.string.password_size));
                    } else {
                        if (isInternet) {
                            resetpassword();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }
                    }
                }

                break;

            case R.id.imgBack:

                onBackPressed();
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utilities.hideKeyboard(ForgotPassword.this);

    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.mobile_no), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }


    private void forgetPassword() {
        str_number = number.getText().toString();
        customDialog = new CustomDialog(ForgotPassword.this);
        customDialog.setCancelable(false);
        customDialog.show();

        StringRequest jsonObjectRequest = new
                StringRequest(Request.Method.POST,
                        URLHelper.FORGET_PASSWORD,
                        response -> {
                            customDialog.dismiss();
                            Log.e("ForgetPasswordResponse", response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                JSONObject userObject = obj.getJSONObject("user");
                                if (userObject.getString("mobile") != null) {
                                    id = userObject.getString("id");
                                    getemail = userObject.getString("email");
                                    getmobile = userObject.getString("mobile");
//                                        if (getmobile == str_number) {
                                    Log.e("getmobile", getmobile + "");
                                    openphonelogin();

//                                        } else {
//                                            displayMessage("You have entered different mobile number");
//                                        }
                                } else {
                                    displayMessage("Mobile no is not exist with this email_id");
                                }


                            } catch (JSONException e) {
                                displayMessage("Mobile no is not exist with this email_id");
                                e.printStackTrace();
                            }

                        },
                        error -> {
                            Log.e("volleyerror", error.toString() + "");
                            customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;

                            if (response != null && response.data != null) {
                                Log.e("MyTest", "" + error);
                                Log.e("MyTestError", "" + error.networkResponse);
                                Log.e("MyTestError1", "" + response.statusCode);
                                try {

                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 ||
                                            response.statusCode == 500) {
                                        try {
                                            displayMessage(errorObj.optString("message"));
                                        } catch (Exception e) {
                                            displayMessage("Something went wrong.");
                                        }
                                    } else if (response.statusCode == 401) {
                                        try {
                                            if (errorObj.optString("message")
                                                    .equalsIgnoreCase("invalid_token")) {
                                                refreshAccessToken("FORGOT_PASSWORD");
                                            } else {
                                                displayMessage(errorObj.optString("message"));
                                            }
                                        } catch (Exception e) {
                                            displayMessage("Something went wrong.");
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
                                    //forgetPassword();
                                }
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", str_email);
                        Log.e(TAG, "params: " + params.toString());
                        return params;
                    }
                };

        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void resetpassword() {
        customDialog = new CustomDialog(ForgotPassword.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("password", str_newPassword);
            object.put("password_confirmation", str_confirmPassword);
            Log.e("ResetPassword", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.RESET_PASSWORD,
                        object,
                        response -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            Log.v("ResetPasswordResponse", response.toString());
                            try {
                                JSONObject object1 = new JSONObject(response.toString());
                                Toast.makeText(context, object1.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassword.this,
                                        Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    Log.e("MyTest", "" + error);
                    Log.e("MyTestError", "" + error.networkResponse);
                    Log.e("MyTestError1", "" + response.statusCode);
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message")
                                            .equalsIgnoreCase("invalid_token")) {
                                        refreshAccessToken("RESET_PASSWORD");
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
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
                            resetpassword();
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

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.login,
                        object,
                        response -> {

                            utils.print("SignUpResponse", response.toString());
                            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                            if (tag.equalsIgnoreCase("FORGOT_PASSWORD")) {
                                forgetPassword();
                            } else {
                                resetpassword();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json = "";
                        NetworkResponse response = error.networkResponse;

                        if (response != null && response.data != null) {
                            SharedHelper.putKey(context, "loggedIn", "false");
                            utils.GoToBeginActivity(ForgotPassword.this);
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
    private void openphonelogin() {

        dialog = new Dialog(ForgotPassword.this,R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            dialog.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        dialog.show();
        CountryCodePicker ccp=(CountryCodePicker)dialog.findViewById(R.id.ccp);
        Button nextIcon=dialog.findViewById(R.id.nextIcon);
        EditText mobile_no=dialog.findViewById(R.id.mobile_no);
        final String countryCode=ccp.getDefaultCountryCode();
        final String countryIso=ccp.getSelectedCountryNameCode();
        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumberString = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();

                SharedHelper.putKey(getApplicationContext(), "mobile_number", phoneNumberString);
                Log.v("Phonecode",phoneNumberString+" ");
                Intent intent =new Intent(ForgotPassword.this, OtpVerification.class);
                intent.putExtra("phonenumber",phoneNumberString);
                startActivityForResult(intent,APP_REQUEST_CODE);
                dialog.dismiss();
//                String phone=ccp.getDefaultCountryCode()+mobile_no.getText().toString();
//                PhoneNumber phoneNumber = new PhoneNumber(ccp.getSelectedCountryCode(),mobile_no.getText().toString(),ccp.getSelectedCountryNameCode());
//                phoneLogin(phoneNumber);


            }
        });

    }
    public void phoneLogin( PhoneNumber phoneNumber) {
        Log.e(TAG, "PhoneLogin");
        final Intent intent = new Intent(this, AccountKitActivity.class);
        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
                ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimary),
                R.drawable.bg,
                SkinManager.Tint.BLACK,
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
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (data != null) {
            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request




                if (getmobile != null) {
                    String[] separated = phoneNumberString.split("\\+");
                    String phoneSplit = separated[1];
                    if (getmobile.contains(phoneSplit)) {
                        email.setFocusable(false);
                        email.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        email.setClickable(false);

                        validation = "reset";
//                                    titleText.setText(R.string.reset_password);
                        newPasswordLayout.setVisibility(View.VISIBLE);
                        confirmPasswordLayout.setVisibility(View.VISIBLE);
                        OtpLay.setVisibility(View.GONE);
                        note_txt.setVisibility(View.GONE);
                        //OTP.performClick();
                        ll_resend.setVisibility(View.GONE);
                    } else {
                        displayMessage("Mobile no is not match with register emailid");
                    }
                }

                SharedHelper.putKey(ForgotPassword.this, "mobile", phoneNumberString);


//                AccountKitLoginResult loginResult = data
//                        .getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//
//                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                    @Override
//                    public void onSuccess(Account account) {
//                        Log.e(TAG, "onSuccess: Account Kit" + account.getId());
//                        Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
//                        if (AccountKit.getCurrentAccessToken().getToken() != null) {
//                            SharedHelper.putKey(ForgotPassword.this, "account_kit_token",
//                                    AccountKit.getCurrentAccessToken().getToken());
//                            //SharedHelper.putKey(RegisterActivity.this, "loggedIn", "true");
//                            // Get phone number
//                            dialog.dismiss();
//                            PhoneNumber phoneNumber = account.getPhoneNumber();
//                            String phoneNumberString = phoneNumber.toString();
//                            if (getmobile != null) {
//                                String[] separated = phoneNumberString.split("\\+");
//                                String phoneSplit = separated[1];
//                                if (getmobile.contains(phoneSplit)) {
//                                    email.setFocusable(false);
//                                    email.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
//                                    email.setClickable(false);
//
//                                    validation = "reset";
////                                    titleText.setText(R.string.reset_password);
//                                    newPasswordLayout.setVisibility(View.VISIBLE);
//                                    confirmPasswordLayout.setVisibility(View.VISIBLE);
//                                    OtpLay.setVisibility(View.GONE);
//                                    note_txt.setVisibility(View.GONE);
//                                    //OTP.performClick();
//                                    ll_resend.setVisibility(View.GONE);
//                                } else {
//                                    displayMessage("Mobile no is not match with register emailid");
//                                }
//                            }
//
//                            SharedHelper.putKey(ForgotPassword.this, "mobile", phoneNumberString);
//                            //registerAPI();
//                            Log.e(TAG, "Verified");
//                        } else {
//                            SharedHelper.putKey(ForgotPassword.this, "account_kit_token", "");
//                            SharedHelper.putKey(ForgotPassword.this, "loggedIn", "false");
//                            SharedHelper.putKey(context, "email", "");
//                            SharedHelper.putKey(context, "login_by", "");
//                            SharedHelper.putKey(ForgotPassword.this, "account_kit_token", "");
//                            Intent goToLogin = new Intent(ForgotPassword.this, Login.class);
//                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(goToLogin);
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onError(AccountKitError accountKitError) {
//                        Log.e(TAG, "onError: Account Kit" + accountKitError);
//                    }
//                });
//                if (loginResult != null) {
//                    SharedHelper.putKey(this, "account_kit", "true");
//                } else {
//                    SharedHelper.putKey(this, "account_kit", "false");
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
//                        SharedHelper.putKey(this, "account_kit", loginResult.getAccessToken().toString());
//                        toastMessage = "Welcome to Tranxit...";
//                    } else {
//                        SharedHelper.putKey(this, "account_kit", "");
//                        toastMessage = String.format(
//                                "Welcome to Tranxit...",
//                                loginResult.getAuthorizationCode().substring(0, 10));
//                    }
//                }
            }
        }
    }
}

//public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
//    Dialog dialog;
//    String TAG = "ForgetPassword";
//    public Context context = ForgotPassword.this;
//    ImageButton backArrow,nextIcon;
//    TextInputLayout newPasswordLayout, confirmPasswordLayout, OtpLay;
//    LinearLayout ll_resend;
//    EditText newPassowrd, confirmPassword, OTP;
//    EditText email;
//    EditText mobile_no;
//    CustomDialog customDialog;
//    String validation = "",
//            str_newPassword,
//            str_confirmPassword,
//            id,
//            str_email = "",
//            str_otp,
//            server_opt,
//            getemail,
//            getmobile,
//            str_number;
//    ConnectionHelper helper;
//    Boolean isInternet;
//    TextView note_txt;
//    Boolean fromActivity = false;
//    Button resend;
//
//    Utilities utils = new Utilities();
//    EditText number;
//
//    public static int APP_REQUEST_CODE = 99;
//    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
//    UIManager uiManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_forgot_password);
//        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        findViewById();
//
//        if (Build.VERSION.SDK_INT > 15) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//
//
////        etEmail=findViewById(R.id.etEmail);
//
//    }
//
//    private void findViewById() {
//
//        mobile_no = findViewById(R.id.mobile_no);
//        email = findViewById(R.id.email);
//        number = findViewById(R.id.number);
//        backArrow=findViewById(R.id.imgBack);
//        nextIcon =findViewById(R.id.nextIcon);
//
//        note_txt =  findViewById(R.id.note);
//        newPassowrd = findViewById(R.id.new_password);
//        OTP = findViewById(R.id.otp);
//        confirmPassword = findViewById(R.id.confirm_password);
//        confirmPasswordLayout =  findViewById(R.id.confirm_password_lay);
//        OtpLay =  findViewById(R.id.otp_lay);
//        newPasswordLayout =  findViewById(R.id.new_password_lay);
//        resend =  findViewById(R.id.resend);
//        ll_resend =  findViewById(R.id.ll_resend);
//        helper = new ConnectionHelper(context);
//        isInternet = helper.isConnectingToInternet();
//        str_email = SharedHelper.getKey(ForgotPassword.this, "email");
//        email.setText(str_email);
//        if (Build.VERSION.SDK_INT > 15) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//
//        nextIcon.setOnClickListener(this);
//        backArrow.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.nextIcon:
//
//                str_email = email.getText().toString();
//                str_number = number.getText().toString();
//                if (validation.equalsIgnoreCase("")) {
//                    if (email.getText().toString().equals("")) {
//                        displayMessage(getString(R.string.email_validation));
//                    } else if (!Utilities.isValidEmail(email.getText().toString())) {
//                        displayMessage(getString(R.string.not_valid_email));
//                    }
//                    else {
//                        if (isInternet) {
//                            forgetPassword();
//                        } else {
//                            displayMessage(getString(R.string.something_went_wrong_net));
//                        }
//
//                    }
//                } else {
//                    str_newPassword = newPassowrd.getText().toString();
//                    str_confirmPassword = confirmPassword.getText().toString();
//                    str_otp = OTP.getText().toString();
//                    if (str_newPassword.equals("") || str_newPassword.equalsIgnoreCase(getString(R.string.new_password))) {
//                        displayMessage(getString(R.string.password_validation));
//                    } else if (str_newPassword.length() < 6) {
//                        displayMessage(getString(R.string.password_size));
//                    } else if (str_confirmPassword.equals("") || str_confirmPassword.equalsIgnoreCase(getString(R.string.confirm_password)) || !str_newPassword.equalsIgnoreCase(str_confirmPassword)) {
//                        displayMessage(getString(R.string.confirm_password_validation));
//                    } else if (str_confirmPassword.length() < 6) {
//                        displayMessage(getString(R.string.password_size));
//                    } else {
//                        if (isInternet) {
//                            resetpassword();
//                        } else {
//                            displayMessage(getString(R.string.something_went_wrong_net));
//                        }
//                    }
//                }
//
//                break;
//
//            case R.id.imgBack:
//
//                onBackPressed();
//                finish();
//                break;
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Utilities.hideKeyboard(ForgotPassword.this);
//
//    }
//
//    public void displayMessage(String toastString) {
//        Log.e("displayMessage", "" + toastString);
//        Snackbar.make(findViewById(R.id.mobile_no), toastString, Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
//    }
//
//
//    private void forgetPassword() {
//        str_number = number.getText().toString();
//        customDialog = new CustomDialog(ForgotPassword.this);
//        customDialog.setCancelable(false);
//        customDialog.show();
//
//        StringRequest jsonObjectRequest = new
//                StringRequest(Request.Method.POST,
//                        URLHelper.FORGET_PASSWORD,
//                        response -> {
//                            customDialog.dismiss();
//                            Log.e("ForgetPasswordResponse", response);
//                            try {
//                                JSONObject obj = new JSONObject(response);
//
//                                JSONObject userObject = obj.getJSONObject("user");
//                                if (userObject.getString("mobile") != null) {
//                                    id = userObject.getString("id");
//                                    getemail = userObject.getString("email");
//                                    getmobile = userObject.getString("mobile");
////                                        if (getmobile == str_number) {
//                                    Log.e("getmobile", getmobile + "");
//                                    openphonelogin();
//
////                                        } else {
////                                            displayMessage("You have entered different mobile number");
////                                        }
//                                } else {
//                                    displayMessage("Mobile no is not exist with this email_id");
//                                }
//
//
//                            } catch (JSONException e) {
//                                displayMessage("Mobile no is not exist with this email_id");
//                                e.printStackTrace();
//                            }
//
//                        },
//                        error -> {
//                            Log.e("volleyerror", error.toString() + "");
//                            customDialog.dismiss();
//                            String json = null;
//                            String Message;
//                            NetworkResponse response = error.networkResponse;
//
//                            if (response != null && response.data != null) {
//                                Log.e("MyTest", "" + error);
//                                Log.e("MyTestError", "" + error.networkResponse);
//                                Log.e("MyTestError1", "" + response.statusCode);
//                                try {
//
//                                    JSONObject errorObj = new JSONObject(new String(response.data));
//
//                                    if (response.statusCode == 400 || response.statusCode == 405 ||
//                                            response.statusCode == 500) {
//                                        try {
//                                            displayMessage(errorObj.optString("message"));
//                                        } catch (Exception e) {
//                                            displayMessage("Something went wrong.");
//                                        }
//                                    } else if (response.statusCode == 401) {
//                                        try {
//                                            if (errorObj.optString("message")
//                                                    .equalsIgnoreCase("invalid_token")) {
//                                                refreshAccessToken("FORGOT_PASSWORD");
//                                            } else {
//                                                displayMessage(errorObj.optString("message"));
//                                            }
//                                        } catch (Exception e) {
//                                            displayMessage("Something went wrong.");
//                                        }
//                                    } else if (response.statusCode == 422) {
//                                        json = trimMessage(new String(response.data));
//                                        if (json != "" && json != null) {
//                                            displayMessage(json);
//                                        } else {
//                                            displayMessage("Please try again.");
//                                        }
//                                    } else {
//                                        displayMessage("Please try again.");
//                                    }
//
//                                } catch (Exception e) {
//                                    displayMessage("Something went wrong.");
//                                }
//
//                            } else {
//                                if (error instanceof NoConnectionError) {
//                                    displayMessage(getString(R.string.oops_connect_your_internet));
//                                } else if (error instanceof NetworkError) {
//                                    displayMessage(getString(R.string.oops_connect_your_internet));
//                                } else if (error instanceof TimeoutError) {
//                                    //forgetPassword();
//                                }
//                            }
//                        }) {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("X-Requested-With", "XMLHttpRequest");
//                        return headers;
//                    }
//
//                    @Override
//                    public Map<String, String> getParams() {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("email", str_email);
//                        Log.e(TAG, "params: " + params.toString());
//                        return params;
//                    }
//                };
//
//        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
//
//    }
//
//    private void resetpassword() {
//        customDialog = new CustomDialog(ForgotPassword.this);
//        customDialog.setCancelable(false);
//        if (customDialog != null)
//            customDialog.show();
//        JSONObject object = new JSONObject();
//        try {
//            object.put("id", id);
//            object.put("password", str_newPassword);
//            object.put("password_confirmation", str_confirmPassword);
//            Log.e("ResetPassword", "" + object);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new
//                JsonObjectRequest(Request.Method.POST,
//                        URLHelper.RESET_PASSWORD,
//                        object,
//                        response -> {
//                            if ((customDialog != null) && (customDialog.isShowing()))
//                                customDialog.dismiss();
//                            Log.v("ResetPasswordResponse", response.toString());
//                            try {
//                                JSONObject object1 = new JSONObject(response.toString());
//                                Toast.makeText(context, object1.optString("message"),
//                                        Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(ForgotPassword.this,
//                                        Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                                finish();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }, error -> {
//                    if ((customDialog != null) && (customDialog.isShowing()))
//                        customDialog.dismiss();
//                    String json = null;
//                    String Message;
//                    NetworkResponse response = error.networkResponse;
//                    Log.e("MyTest", "" + error);
//                    Log.e("MyTestError", "" + error.networkResponse);
//                    Log.e("MyTestError1", "" + response.statusCode);
//                    if (response != null && response.data != null) {
//                        try {
//                            JSONObject errorObj = new JSONObject(new String(response.data));
//
//                            if (response.statusCode == 400 || response.statusCode == 405 ||
//                                    response.statusCode == 500) {
//                                try {
//                                    displayMessage(errorObj.optString("message"));
//                                } catch (Exception e) {
//                                    displayMessage("Something went wrong.");
//                                }
//                            } else if (response.statusCode == 401) {
//                                try {
//                                    if (errorObj.optString("message")
//                                            .equalsIgnoreCase("invalid_token")) {
//                                        refreshAccessToken("RESET_PASSWORD");
//                                    } else {
//                                        displayMessage(errorObj.optString("message"));
//                                    }
//                                } catch (Exception e) {
//                                    displayMessage("Something went wrong.");
//                                }
//
//                            } else if (response.statusCode == 422) {
//
//                                json = trimMessage(new String(response.data));
//                                if (json != "" && json != null) {
//                                    displayMessage(json);
//                                } else {
//                                    displayMessage("Please try again.");
//                                }
//
//                            } else {
//                                displayMessage("Please try again.");
//                            }
//
//                        } catch (Exception e) {
//                            displayMessage("Something went wrong.");
//                        }
//
//
//                    } else {
//                        if (error instanceof NoConnectionError) {
//                            displayMessage(getString(R.string.oops_connect_your_internet));
//                        } else if (error instanceof NetworkError) {
//                            displayMessage(getString(R.string.oops_connect_your_internet));
//                        } else if (error instanceof TimeoutError) {
//                            resetpassword();
//                        }
//                    }
//
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("X-Requested-With", "XMLHttpRequest");
//                        return headers;
//                    }
//                };
//
//        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
//
//    }
//    private void refreshAccessToken(final String tag) {
//
//        JSONObject object = new JSONObject();
//        try {
//
//            object.put("grant_type", "refresh_token");
//            object.put("client_id", URLHelper.client_id);
//            object.put("client_secret", URLHelper.client_secret);
//            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
//            object.put("scope", "");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new
//                JsonObjectRequest(Request.Method.POST,
//                        URLHelper.login,
//                        object,
//                        response -> {
//
//                            utils.print("SignUpResponse", response.toString());
//                            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
//                            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
//                            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
//                            if (tag.equalsIgnoreCase("FORGOT_PASSWORD")) {
//                                forgetPassword();
//                            } else {
//                                resetpassword();
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        String json = "";
//                        NetworkResponse response = error.networkResponse;
//
//                        if (response != null && response.data != null) {
//                            SharedHelper.putKey(context, "loggedIn", "false");
//                            utils.GoToBeginActivity(ForgotPassword.this);
//                        } else {
//                            if (error instanceof NoConnectionError) {
//                                displayMessage(getString(R.string.oops_connect_your_internet));
//                            } else if (error instanceof NetworkError) {
//                                displayMessage(getString(R.string.oops_connect_your_internet));
//                            } else if (error instanceof TimeoutError) {
//                                refreshAccessToken(tag);
//                            }
//                        }
//                    }
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("X-Requested-With", "XMLHttpRequest");
//                        return headers;
//                    }
//                };
//
//        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
//    }
//    private void openphonelogin() {
//
//         dialog = new Dialog(ForgotPassword.this,R.style.AppTheme_NoActionBar);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT);
//        dialog.setContentView(R.layout.mobileverification);
//        dialog.setCancelable(false);
//        dialog.show();
//        CountryCodePicker ccp=(CountryCodePicker)dialog.findViewById(R.id.ccp);
//        ImageButton nextIcon=dialog.findViewById(R.id.nextIcon);
//        EditText mobile_no=dialog.findViewById(R.id.mobile_no);
//        final String countryCode=ccp.getDefaultCountryCode();
//        final String countryIso=ccp.getSelectedCountryNameCode();
//        nextIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String phone=ccp.getDefaultCountryCode()+mobile_no.getText().toString();
//                PhoneNumber phoneNumber = new PhoneNumber(ccp.getSelectedCountryCode(),mobile_no.getText().toString(),ccp.getSelectedCountryNameCode());
//                phoneLogin(phoneNumber);
//
//
//            }
//        });
//
//    }
//    public void phoneLogin( PhoneNumber phoneNumber) {
//        Log.e(TAG, "PhoneLogin");
//        final Intent intent = new Intent(this, AccountKitActivity.class);
//        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
//                ContextCompat.getColor(getApplicationContext(),
//                        R.color.colorPrimary),
//                R.drawable.bg,
//                SkinManager.Tint.BLACK,
//                0.001);
//        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
//                new AccountKitConfiguration.AccountKitConfigurationBuilder(
//                        LoginType.PHONE,
//                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
//        configurationBuilder.setUIManager(uiManager);
//        intent.putExtra(
//                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
//                configurationBuilder
//                        .setInitialPhoneNumber(phoneNumber)
//                        .build());
//
//        startActivityForResult(intent, APP_REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(
//            final int requestCode,
//            final int resultCode,
//            final Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "onActivityResult");
//        if (data != null) {
//            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
//                AccountKitLoginResult loginResult = data
//                        .getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//
//                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                    @Override
//                    public void onSuccess(Account account) {
//                        Log.e(TAG, "onSuccess: Account Kit" + account.getId());
//                        Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
//                        if (AccountKit.getCurrentAccessToken().getToken() != null) {
//                            SharedHelper.putKey(ForgotPassword.this, "account_kit_token",
//                                    AccountKit.getCurrentAccessToken().getToken());
//                            //SharedHelper.putKey(RegisterActivity.this, "loggedIn", "true");
//                            // Get phone number
//                            dialog.dismiss();
//                            PhoneNumber phoneNumber = account.getPhoneNumber();
//                            String phoneNumberString = phoneNumber.toString();
//                            if (getmobile != null) {
//                                String[] separated = phoneNumberString.split("\\+");
//                                String phoneSplit = separated[1];
//                                if (getmobile.contains(phoneSplit)) {
//                                    email.setFocusable(false);
//                                    email.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
//                                    email.setClickable(false);
//
//                                    validation = "reset";
////                                    titleText.setText(R.string.reset_password);
//                                    newPasswordLayout.setVisibility(View.VISIBLE);
//                                    confirmPasswordLayout.setVisibility(View.VISIBLE);
//                                    OtpLay.setVisibility(View.GONE);
//                                    note_txt.setVisibility(View.GONE);
//                                    //OTP.performClick();
//                                    ll_resend.setVisibility(View.GONE);
//                                } else {
//                                    displayMessage("Mobile no is not match with register emailid");
//                                }
//                            }
//
//                            SharedHelper.putKey(ForgotPassword.this, "mobile", phoneNumberString);
//                            //registerAPI();
//                            Log.e(TAG, "Verified");
//                        } else {
//                            SharedHelper.putKey(ForgotPassword.this, "account_kit_token", "");
//                            SharedHelper.putKey(ForgotPassword.this, "loggedIn", "false");
//                            SharedHelper.putKey(context, "email", "");
//                            SharedHelper.putKey(context, "login_by", "");
//                            SharedHelper.putKey(ForgotPassword.this, "account_kit_token", "");
//                            Intent goToLogin = new Intent(ForgotPassword.this, Login.class);
//                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(goToLogin);
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onError(AccountKitError accountKitError) {
//                        Log.e(TAG, "onError: Account Kit" + accountKitError);
//                    }
//                });
//                if (loginResult != null) {
//                    SharedHelper.putKey(this, "account_kit", "true");
//                } else {
//                    SharedHelper.putKey(this, "account_kit", "false");
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
//                        SharedHelper.putKey(this, "account_kit", loginResult.getAccessToken().toString());
//                        toastMessage = "Welcome to Tranxit...";
//                    } else {
//                        SharedHelper.putKey(this, "account_kit", "");
//                        toastMessage = String.format(
//                                "Welcome to Tranxit...",
//                                loginResult.getAuthorizationCode().substring(0, 10));
//                    }
//                }
//            }
//        }
//    }
//}
