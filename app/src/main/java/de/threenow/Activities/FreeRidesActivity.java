package de.threenow.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.Helper.URLHelper;
import de.threenow.IlyftApplication;
import de.threenow.R;

public class FreeRidesActivity extends AppCompatActivity {

    ImageView backArrow;

    Button btn_invite;

    String code = "";

    TextView invite_price;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Newtheme);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.parseColor("#008FFF"));
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_free_rides);

        context = FreeRidesActivity.this;

        backArrow = findViewById(R.id.backArrow);
        btn_invite = findViewById(R.id.btn_invite);
        invite_price = findViewById(R.id.invite_price);

        backArrow.setOnClickListener(view -> finish());

        code = (new DecimalFormat("#").format(Double.parseDouble(SharedHelper.getKey(context, "id") + "") + 1000000) + "");

        btn_invite.setText(code);

        btn_invite.setOnClickListener(view -> {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, code);
            startActivity(Intent.createChooser(intent, code));
        });

        getInvitePrice();
    }

    private void getInvitePrice() {
        CustomDialog customDialog = new CustomDialog(context);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.INVITE_PRICE, object, response -> {

            Log.e("INVITE_PRICE", response.toString());
            invite_price.setText(response.optString("price") + "€");
            SharedHelper.putKey(context, "GOOGLE_KEY_MAPS", response.optString("key"));
            customDialog.dismiss();

        }, error -> {
            customDialog.dismiss();
            Log.e("URL_GOOGLE_KEY_MAPS", error.toString());
            if (error instanceof TimeoutError) {
                Log.e("INVITE_PRICE", "time out reCall");
                getInvitePrice();
            } else {
                Toast.makeText(context, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
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