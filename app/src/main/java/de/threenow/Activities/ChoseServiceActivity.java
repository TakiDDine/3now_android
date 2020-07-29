package de.threenow.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;

public class ChoseServiceActivity extends AppCompatActivity implements View.OnClickListener {

    String s_latitude;
    String s_longitude;
    String d_latitude;
    String d_longitude;
    String s_address;
    String d_address;
    String distance;
    String use_wallet;
    String payment_mode;
    String card_id;
    TextView economybtn, vipbtn, specialbtn;
    int serviceId;
    TextView t1;

    ImageView im_back;

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
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//	View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getSupportActionBar().hide();
        setTheme(R.style.TranslucentStatusBar);

        setContentView(R.layout.activity_chose_service);


        im_back = findViewById(R.id.im_back);
        im_back.setOnClickListener(this);

        s_latitude = getIntent().getStringExtra("s_latitude");
        s_longitude = getIntent().getStringExtra("s_longitude");
        d_latitude = getIntent().getStringExtra("d_latitude");
        d_longitude = getIntent().getStringExtra("d_longitude");
        s_address = getIntent().getStringExtra("s_address");
        d_address = getIntent().getStringExtra("d_address");
        distance = getIntent().getStringExtra("distance");
        use_wallet = "" + getIntent().getIntExtra("use_wallet", 0);
        payment_mode = getIntent().getStringExtra("payment_mode");
        card_id = getIntent().getStringExtra("card_id");
        economybtn = findViewById(R.id.economybtn);
        vipbtn = findViewById(R.id.vipbtn);
        specialbtn = findViewById(R.id.specialbtn);
        t1 = findViewById(R.id.textView);
        t1.setText(use_wallet);


        economybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceId = 19;
                Intent i = new Intent(getApplicationContext(), TripSchedulingActivity.class);
                i.putExtra("s_latitude", s_latitude);
                i.putExtra("s_longitude", s_longitude);
                i.putExtra("d_latitude", d_latitude);
                i.putExtra("d_longitude", d_longitude);
                i.putExtra("s_address", s_address);
                i.putExtra("d_address", d_address);
                i.putExtra("distance", distance);

                i.putExtra("payment_mode", payment_mode);
                i.putExtra("service_id", "19");
                if (use_wallet != null) {
                    i.putExtra("use_wallet", use_wallet);
                }
                if (card_id != null) {
                    i.putExtra("card_id", card_id);
                }
                startActivity(i);
            }
        });
        vipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceId = 27;
                Intent i = new Intent(getApplicationContext(), TripSchedulingActivity.class);
                i.putExtra("s_latitude", s_latitude);
                i.putExtra("s_longitude", s_longitude);
                i.putExtra("d_latitude", d_latitude);
                i.putExtra("d_longitude", d_longitude);
                i.putExtra("s_address", s_address);
                i.putExtra("d_address", d_address);
                i.putExtra("distance", distance);
                i.putExtra("payment_mode", payment_mode);
                i.putExtra("service_id", "27");
                if (use_wallet != null) {
                    i.putExtra("use_wallet", use_wallet);
                }
                if (card_id != null) {
                    i.putExtra("card_id", card_id);
                }
                startActivity(i);
            }
        });
        specialbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceId = 32;
                Intent i = new Intent(getApplicationContext(), SpecialTripsActivity.class);
//                i.putExtra("s_latitude", s_latitude);
//                i.putExtra("s_longitude", s_longitude);
//                i.putExtra("d_latitude", d_latitude);
//                i.putExtra("d_longitude", d_longitude);
//                i.putExtra("s_address", s_address);
//                i.putExtra("d_address", d_address);
//                i.putExtra("distance", distance);
//                i.putExtra("payment_mode", payment_mode);
//                i.putExtra("service_id", "27");
//                if (use_wallet != null) {
//                    i.putExtra("use_wallet", use_wallet);
//                }
//                if (card_id != null) {
//                    i.putExtra("card_id", card_id);
//                }
                startActivity(i);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.im_back:
                finish();
                break;
        }

    } // End onClick
}