package de.threenow.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;

public class FullImage extends AppCompatActivity {
    ImageView imgFull;
    ImageView imgback;
    TextView txtTitle;
    String title;
    String imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        title = getIntent().getStringExtra("title");
        imgUrl = getIntent().getStringExtra("url");
        imgFull=findViewById(R.id.imgFull);
        imgback = findViewById(R.id.imgback);
        txtTitle =findViewById(R.id.txtTitle);
        Picasso.get().load(imgUrl).into(imgFull);
        txtTitle.setText(title);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
