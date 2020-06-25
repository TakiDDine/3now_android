package de.threenow.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;

import static de.threenow.IlyftApplication.trimMessage;

public class ActivityHelp extends AppCompatActivity implements View.OnClickListener {

    LinearLayout imgEmail;
    LinearLayout imgPhone;
    LinearLayout imgWeb;
    LinearLayout imgfaq;

    String phone = "";
    String email = "";

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
        setContentView(R.layout.activity_help);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        findviewById();
        setOnClickListener();

        ImageView backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> finish());

        getHelp();
    }

    private void findviewById() {
        imgEmail = findViewById(R.id.img_mail);
        imgPhone = findViewById(R.id.img_phone);
        imgWeb = findViewById(R.id.img_web);
        imgfaq = findViewById(R.id.img_faq);
    }

    private void setOnClickListener() {
        imgEmail.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        imgWeb.setOnClickListener(this);
        imgfaq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgEmail) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setType("text/html");
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + "-" + getString(R.string.help));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.hello_team));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
        } else if (v == imgPhone) {
            if (phone != null && !phone.equalsIgnoreCase("null") && !phone.equalsIgnoreCase("") && phone.length() > 0) {

                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" + phone));

                startActivity(intentCall);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setMessage(getString(R.string.sorry_for_inconvinent))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog alert1 = builder.create();
                alert1.show();
            }
        } else if (v == imgWeb) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.REDIRECT_URL));
            startActivity(browserIntent);
        } else if (v == imgfaq) {
            Intent intentqa = new Intent(this, QuestionandanswerActivity.class);
            startActivity(intentqa);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
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
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getHelp() {
        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.HELP, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.e("HELP", response.toString());
                phone = response.optString("contact_number");
                email = response.optString("contact_email");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                                e.printStackTrace();
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
                        e.printStackTrace();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getHelp();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                return headers;
            }
        };
        IlyftApplication.getInstance().addToRequestQueue(jsonObjectRequest);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(ActivityHelp.this, "access_token", response.optString("access_token"));
                SharedHelper.putKey(ActivityHelp.this, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(ActivityHelp.this, "token_type", response.optString("token_type"));
                getHelp();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(ActivityHelp.this, "loggedIn", "false");
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


    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            Toast.makeText(ActivityHelp.this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(this, "loggedIn", "false");
        Intent mainIntent = new Intent(this, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }
}
