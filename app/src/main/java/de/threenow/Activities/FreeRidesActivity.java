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

    String code = "", i_price;

    TextView tv_invite_price, tv_how_it_work, tv_info_invite;

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
        tv_invite_price = findViewById(R.id.invite_price);
        tv_how_it_work = findViewById(R.id.tv_how_it_work);
        tv_info_invite = findViewById(R.id.tv_info_invite);

        backArrow.setOnClickListener(view -> finish());

        tv_how_it_work.setOnClickListener(view -> startActivity(new Intent(context, HowItWorkActivity.class).putExtra("invite_price", i_price)));

        code = (new DecimalFormat("#").format(Double.parseDouble(SharedHelper.getKey(context, "id") + "") + 1000000) + "");

        btn_invite.setText(code);

        btn_invite.setOnClickListener(view -> {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey,\n" +
                    "hilf mir einen " + tv_invite_price.getText().toString() +
                    " Gutschein bei 3Now zu erhalten, dafür musst du aber meinen Code nützen :" + code +
                    "\n\nAndroid:\n" +
                    "https://ply.gl/de.threenow\n" +
                    "\nIOS\n" +
                    "https://apps.apple.com/nz/app/3now/id1524167394\n");
            startActivity(Intent.createChooser(intent, code));
        });

        getInvitePrice();
    }

    private void getInvitePrice() {
        CustomDialog customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.INVITE_PRICE, object, response -> {

            Log.e("INVITE_PRICE", response.toString());

//            i_price = String.format("%,f", Double.parseDouble(response.optString("price"))).replace(",", ".") + "";
//            i_price = i_price.substring(0, i_price.indexOf(".") + 2);

            i_price = response.optString("price");

            tv_invite_price.setText(i_price + "€");
            tv_info_invite.setText(String.format(getString(R.string.info_invite), i_price));

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