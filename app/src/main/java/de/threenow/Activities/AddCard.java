package de.threenow.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.material.snackbar.Snackbar;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;
import de.threenow.Utils.MyButton;
import de.threenow.Utils.Utilities;

import static de.threenow.IlyftApplication.trimMessage;

public class AddCard extends AppCompatActivity {

    static final Pattern CODE_PATTERN = Pattern
            .compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    Activity activity;
    Context context;
    ImageView backArrow, help_month_and_year, help_cvv;
    MyButton addCard;
    //EditText cardNumber, cvv, month_and_year;
    CardForm cardForm;
    String Card_Token = "";
    CustomDialog customDialog;
    Utilities utils = new Utilities();

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
        setTheme(R.style.Newtheme);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        }


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue_pay));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue_pay));
        }

        setContentView(R.layout.activity_add_card);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewByIdAndInitialize();

//        cardNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() > 0 && !CODE_PATTERN.matcher(s).matches()) {
//                    String input = s.toString();
//                    String numbersOnly = keepNumbersOnly(input);
//                    String code = formatNumbersAsCode(numbersOnly);
//                    cardNumber.removeTextChangedListener(this);
//                    cardNumber.setText(code);
//                    cardNumber.setSelection(code.length());
//                    cardNumber.addTextChangedListener(this);
//                }
//            }
//
//            private String keepNumbersOnly(CharSequence s) {
//                return s.toString().replaceAll("[^0-9]", "");
//            }
//
//            private String formatNumbersAsCode(CharSequence s) {
//                int groupDigits = 0;
//                String tmp = "";
//                for (int i = 0; i < s.length(); ++i) {
//                    tmp += s.charAt(i);
//                    ++groupDigits;
//                    if (groupDigits == 4) {
//                        tmp += "-";
//                        groupDigits = 0;
//                    }
//                }
//                return tmp;
//            }
//        });
//
//
//        month_and_year.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                if (s.length() > 0 && !CODE_PATTERN.matcher(s).matches()) {
//                    String input = s.toString();
//                    String numbersOnly = keepNumbersOnly(input);
//                    String code = formatNumbersAsCode(numbersOnly);
//                    cardNumber.removeTextChangedListener(this);
//                    cardNumber.setText(code);
//                    cardNumber.setSelection(code.length());
//                    cardNumber.addTextChangedListener(this);
//                }
//
//            }
//
//            private String keepNumbersOnly(CharSequence s) {
//                return s.toString().replaceAll("[^0-9]", "");
//            }
//
//            private String formatNumbersAsCode(CharSequence s) {
//                int groupDigits = 0;
//                String tmp = "";
//                for (int i = 0; i < s.length(); ++i) {
//                    tmp += s.charAt(i);
//                    ++groupDigits;
//                    if (groupDigits == 4) {
//                        tmp += "-";
//                        groupDigits = 0;
//                    }
//                }
//                return tmp;
//            }
//
//
//        });

        backArrow.setOnClickListener(view -> onBackPressed());

        addCard.setOnClickListener(view -> {
            customDialog = new CustomDialog(AddCard.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            if (cardForm.getCardNumber() == null || cardForm.getExpirationMonth() == null || cardForm.getExpirationYear() == null || cardForm.getCvv() == null) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                displayMessage(getString(R.string.enter_card_details));
            } else {
                if (cardForm.getCardNumber().equals("") || cardForm.getExpirationMonth().equals("") || cardForm.getExpirationYear().equals("") || cardForm.getCvv().equals("")) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    displayMessage(getString(R.string.enter_card_details));
                } else {
                    String cardNumber = cardForm.getCardNumber();
                    int month = Integer.parseInt(cardForm.getExpirationMonth());
                    int year = Integer.parseInt(cardForm.getExpirationYear());
                    String cvv = cardForm.getCvv();
                    utils.print("MyTest", "CardDetails Number: " + cardNumber + "Month: " + month + " Year: " + year);


                    Card card = new Card(cardNumber, month, year, cvv);
                    Stripe stripe = new Stripe(AddCard.this,
                            URLHelper.STRIPE_TOKEN);
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    utils.print("CardToken:", " " + token.getId());
                                    utils.print("CardToken:", " " + token.getCard().getLast4());
                                    Card_Token = token.getId();

                                    addCardToAccount(Card_Token);
                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    displayMessage(getString(R.string.enter_card_details));
                                    if ((customDialog != null) && (customDialog.isShowing()))
                                        customDialog.dismiss();
                                }
                            }
                    );
                }

            }
        });

    }


    public void findViewByIdAndInitialize() {
        backArrow = findViewById(R.id.backArrow);
//        help_month_and_year = findViewById(R.id.help_month_and_year);
//        help_cvv = findViewById(R.id.help_cvv);
        addCard = (MyButton) findViewById(R.id.addCard);
//        cardNumber =  findViewById(R.id.cardNumber);
//        cvv =  findViewById(R.id.cvv);
//        month_and_year =  findViewById(R.id.monthAndyear);
        context = AddCard.this;
        activity = AddCard.this;
        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Add CardDetails")
                .setup(AddCard.this);
    }


    public void addCardToAccount(final String cardToken) {

        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("stripe_token", cardToken);
            Log.e("addCardToAccount", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                URLHelper.ADD_CARD_TO_ACCOUNT_API,
                object,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("AddCouponRes", "" + response.toString());
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();

                        if (response != null) {
                                try {
                                    utils.print("SendRequestResponse", response.toString());

                                    JSONObject jsonObject =  (response);
                                    Toast.makeText(AddCard.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                    // onBackPressed();
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("isAdded", true);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                customDialog.dismiss();
                            }
                    }
                }, new com.android.volley.Response.ErrorListener() {
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
                        addCardToAccount(cardToken);
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





    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                addCardToAccount(Card_Token);
            }
        }, error -> {
            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", "false");
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
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
