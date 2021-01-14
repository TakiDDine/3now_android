package de.threenow.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import de.threenow.R;

public class HowItWorkActivity extends AppCompatActivity {

    ImageView backArrow;

    TextView tv1_how_it_work,tv2_how_it_work,tv3_how_it_work,tv4_how_it_work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Newtheme);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_how_it_work);

        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(view -> finish());

        String i_price =  getIntent().getStringExtra("invite_price");

        tv1_how_it_work = findViewById(R.id.tv1_how_it_work);
        tv2_how_it_work = findViewById(R.id.tv2_how_it_work);
        tv3_how_it_work = findViewById(R.id.tv3_how_it_work);
        tv4_how_it_work = findViewById(R.id.tv4_how_it_work);

        tv1_how_it_work.setText(String.format(getString(R.string.tv1_how_it_work), i_price));
        tv2_how_it_work.setText(String.format(getString(R.string.tv2_how_it_work), i_price));
        tv3_how_it_work.setText(String.format(getString(R.string.tv3_how_it_work), i_price));
        tv4_how_it_work.setText(String.format(getString(R.string.tv4_how_it_work), i_price));



    }
}