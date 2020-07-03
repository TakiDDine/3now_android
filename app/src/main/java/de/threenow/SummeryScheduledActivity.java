package de.threenow;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.threenow.Helper.CustomDialog;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;

public class SummeryScheduledActivity extends AppCompatActivity implements View.OnClickListener {

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
    String scheduledDate = "";
    String scheduledTime = "";
    String note;
    boolean nameschield;
    CheckBox nameschieldCheckbox;
    CustomDialog customDialog;
    String serviceId, service_type, typeCar, serviceCap, bagCap;
    int childSeat, babySeat;
    double totalPrice = 0;
    ImageView im_back;

    TextView service_car_type, serviceCapacity, AdresseFromD, AdresseToD,
            dateToD, dateToF, childSeatsPrice, babySeatPrice, nameTagPrice,
            detailNote, netlPrice, creditCardNbr, priceTrip, tvDistance,
            bagCapacity;


    Button submit_btn;
    LinearLayout ll_im_internal;


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
        setContentView(R.layout.activity_result_scheduled);

        s_latitude = getIntent().getStringExtra("s_latitude");
        s_longitude = getIntent().getStringExtra("s_longitude");
        d_latitude = getIntent().getStringExtra("d_latitude");
        d_longitude = getIntent().getStringExtra("d_longitude");
        s_address = getIntent().getStringExtra("s_address");
        d_address = getIntent().getStringExtra("d_address");
        distance = getIntent().getStringExtra("distance");
        use_wallet = getIntent().getStringExtra("use_wallet");
        payment_mode = getIntent().getStringExtra("payment_mode");
        card_id = getIntent().getStringExtra("card_id");
        serviceId = getIntent().getStringExtra("service_id");


        service_type = getIntent().getStringExtra("service_type");
        scheduledDate = getIntent().getStringExtra("schedule_date");
        scheduledTime = getIntent().getStringExtra("schedule_time");
        childSeat = getIntent().getIntExtra("kindersitz", 0);
        babySeat = getIntent().getIntExtra("babyschale", 0);
        nameschield = getIntent().getBooleanExtra("nameschield", false);
        note = getIntent().getStringExtra("note");
        double pr = Double.parseDouble(getIntent().getStringExtra("price").toString().trim());


//        if (serviceId.contains("19")) {
//            typeCar = "Economy Mercedes C/B Klasse";
//            serviceCap = "4";
//            bagCap = "3";
//        } else if (serviceId.contains("27") | serviceId.contains("32")) {
//            typeCar = "Mercedes Vito";
//            serviceCap = "8";
//            bagCap = "6";
//        }
//        else if (serviceId.contains("32")) {
//            typeCar = "Mercedes V-Klasse";
//            serviceCap = "7";
//            bagCap = "7";
//        }


        Log.e("123", toString());

        im_back = findViewById(R.id.im_back);
        service_car_type = findViewById(R.id.service_car_type);
        serviceCapacity = findViewById(R.id.serviceCapacity);
        bagCapacity = findViewById(R.id.bagCapacity);
        AdresseFromD = findViewById(R.id.AdresseFromD);
        AdresseToD = findViewById(R.id.AdresseToD);
        dateToD = findViewById(R.id.dateToD);
        dateToF = findViewById(R.id.dateToF);
        tvDistance = findViewById(R.id.tvDistance);
        childSeatsPrice = findViewById(R.id.childSeatsPrice);
        babySeatPrice = findViewById(R.id.babySeatPrice);
        nameTagPrice = findViewById(R.id.nameTagPrice);
        detailNote = findViewById(R.id.detailNote);
        netlPrice = findViewById(R.id.netlPrice);
        creditCardNbr = findViewById(R.id.creditCardNbr);
        priceTrip = findViewById(R.id.priceTrip);
        submit_btn = findViewById(R.id.submit_btn);
        ll_im_internal =  findViewById(R.id.ll_im_internal);


        im_back.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

        if (serviceId.contains("19")) {
            typeCar = "Mercedes Vito";
            serviceCap = "8";
            bagCap = "6";
            ll_im_internal.setBackgroundResource(R.drawable.im_eco_internal);
        } else if (serviceId.contains("27")) {
            typeCar = "Mercedes V-Klasse";
            serviceCap = "7";
            bagCap = "7";
            ll_im_internal.setBackgroundResource(R.drawable.im_vip_internal);
        }


        service_car_type.setText(typeCar.replace("Economy Mercedes C/B Klasse", "Economy\nMercedes C/B Klasse") + "");
        serviceCapacity.setText(serviceCap + " Maximal");
        bagCapacity.setText(bagCap + "");
        AdresseFromD.setText(s_address + "");
        AdresseToD.setText(d_address + "");
        dateToD.setText(scheduledDate + "");
        dateToF.setText(scheduledTime + "");
        childSeatsPrice.setText(0 + "€");
        babySeatPrice.setText(0 + "€");
        nameTagPrice.setText(0 + "€");
        detailNote.setText(" . . . ");

//        try {
            //  totalPrice += Long.parseLong(SharedHelper.getKey(SummeryScheduledActivity.this, "estimated_fare"));
//        } catch (Exception e) {
//
//        }


        if (childSeat > 1) {
            totalPrice += (childSeat - 1) * 5;
            childSeatsPrice.setText((5 * (childSeat - 1)) + "€");
        }


        if (babySeat > 1) {
            totalPrice += 10 * (babySeat - 1);
            babySeatPrice.setText((10 * (babySeat - 1)) + "€");
        }

        if (nameschield) {
            totalPrice += 15;
            nameTagPrice.setText(15 + "€");
        }

        if (note != null) {
            if (note.length() > 0)
                detailNote.setText(note + "");
        }

        priceTrip.setText(((double) (pr - totalPrice)) + "€");
        netlPrice.setText(pr + "€");

        if (distance != null && !distance.isEmpty() && distance.length() > 0 && !distance.contains("null"))
            tvDistance.setText(distance + " km");

        Log.e("payment_mode", payment_mode + "");

        if (payment_mode.contains("PAYPAL")) {
            creditCardNbr.setText("");
            creditCardNbr.setCompoundDrawablesWithIntrinsicBounds((R.drawable.cio_paypal_logo), 0, 0, 0);
        } else if (payment_mode.contains("CARD")) {
            creditCardNbr.setText("");
            creditCardNbr.setCompoundDrawablesWithIntrinsicBounds((R.drawable.visa), 0, 0, 0);
        } else {
            creditCardNbr.setText("");
            creditCardNbr.setCompoundDrawablesWithIntrinsicBounds((R.drawable.ic_cash_txt), 0, 0, 0);
        }


    }

    @Override
    public String toString() {
        return "SummeryScheduledActivity{" +
                "s_latitude='" + s_latitude + '\'' +
                ", s_longitude='" + s_longitude + '\'' +
                ", d_latitude='" + d_latitude + '\'' +
                ", d_longitude='" + d_longitude + '\'' +
                ", s_address='" + s_address + '\'' +
                ", d_address='" + d_address + '\'' +
                ", distance='" + distance + '\'' +
                ", use_wallet='" + use_wallet + '\'' +
                ", payment_mode='" + payment_mode + '\'' +
                ", card_id='" + card_id + '\'' +
                ", scheduledDate='" + scheduledDate + '\'' +
                ", scheduledTime='" + scheduledTime + '\'' +
                ", childSeat='" + childSeat + '\'' +
                ", babySeat='" + babySeat + '\'' +
                ", note='" + note + '\'' +
                ", nameschield='" + nameschield + '\'' +
                ", nameschieldCheckbox=" + nameschieldCheckbox +
                ", customDialog=" + customDialog +
                ", serviceId='" + serviceId + '\'' +
                ", service_type='" + service_type + '\'' +
                '}';
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_btn:
                setResult(RESULT_OK, new Intent());
                finish();
                break;

            case R.id.im_back:
                setResult(RESULT_CANCELED, new Intent());
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        setResult(RESULT_CANCELED, output);
        finish();
        super.onBackPressed();

    }

}
