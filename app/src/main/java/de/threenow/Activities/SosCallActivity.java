package de.threenow.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;

public class SosCallActivity extends AppCompatActivity {

    ImageView ivBack, ivCall;
    TextView tvName, tvNumber;

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
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_call);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String sosNumber = SharedHelper.getKey(this, "sos");
        String sosFirstName = SharedHelper.getKey(this, "first_name");
        String sosLastNmae = SharedHelper.getKey(this, "last_name");
        ivBack = findViewById(R.id.ivBack);
        ivCall = findViewById(R.id.ivCall);
        tvName = findViewById(R.id.tvName);
        tvNumber = findViewById(R.id.tvNumber);

        tvName.setText(sosFirstName + " " + sosLastNmae);
        tvNumber.setText(sosNumber);
        ivBack.setOnClickListener(view -> onBackPressed());
        ivCall.setOnClickListener(view -> {
            if (tvNumber.getText().toString() != null) {
                sosCall();
            }
        });
    }

    private void sosCall() {
        String number = tvNumber.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
        } else {
            Intent intentCall = new Intent(Intent.ACTION_CALL);
            intentCall.setData(Uri.parse("tel:" + number));
            startActivity(intentCall);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
