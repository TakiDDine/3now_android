package de.threenow.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;

public class LegalActivity extends AppCompatActivity {

    private ImageView backArrow;
    private TextView
            termsConditionTextView,
            privacyPolicyTextView,
            copyrightTextView;

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
        setContentView(R.layout.activity_legal);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        backArrow =  findViewById(R.id.backArrow);
        termsConditionTextView =  findViewById(R.id.termsConditionTextView);
        privacyPolicyTextView =  findViewById(R.id.privacyPolicyTextView);
        copyrightTextView =  findViewById(R.id.copyrightTextView);

        backArrow.setOnClickListener(view -> onBackPressed());

        privacyPolicyTextView.setOnClickListener(v -> startActivity(new
                Intent(LegalActivity.this, PrivacyPolicyActivity.class)));

        termsConditionTextView.setOnClickListener(v -> startActivity(new
                Intent(LegalActivity.this, TermsOfUseActivity.class)));

        copyrightTextView.setOnClickListener(v -> Toast.makeText(
                LegalActivity.this,  getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show());


    }
}
