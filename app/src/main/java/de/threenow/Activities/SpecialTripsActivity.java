package de.threenow.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;

public class SpecialTripsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView im_back;
    Button ic_phone, ic_mail;

    @Override
    protected void attachBaseContext(Context base) {
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.BLACK);
        }
        setContentView(R.layout.activity_special_trips);

        im_back = findViewById(R.id.im_back);
        ic_phone = findViewById(R.id.ic_phone);
        ic_mail = findViewById(R.id.ic_mail);


        im_back.setOnClickListener(this);
        ic_phone.setOnClickListener(this);
        ic_mail.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ic_phone:
                Toast.makeText(SpecialTripsActivity.this, "soon..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ic_mail:
                Toast.makeText(SpecialTripsActivity.this, "soon...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.im_back:
                finish();
                break;

        } // End switch

    } // End onClick
}