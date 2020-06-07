package de.threenow.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;
import de.threenow.Utils.GlobalDataMethods;

public class TrinkgeldActivity extends AppCompatActivity implements View.OnClickListener {

    TextView oneTrink, secondTrink, threedTrink;
    Button BtFertig;
    String price;

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
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            GlobalDataMethods.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        setContentView(R.layout.activity_trinkgeld);

        oneTrink = findViewById(R.id.oneTrink);
        secondTrink = findViewById(R.id.secondTrink);
        threedTrink = findViewById(R.id.threedTrink);
        BtFertig = findViewById(R.id.BtFertig);

        oneTrink.setOnClickListener(this);
        secondTrink.setOnClickListener(this);
        threedTrink.setOnClickListener(this);
        BtFertig.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TrinkgeldActivity.class.getName(), "222 onActivityResult: " + requestCode + " result code " + resultCode + " ");

        if (requestCode == 0) { // paypal done pay
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("222 paymentExample", confirm.toJSONObject().getJSONObject("response").toString());

                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/

                        String paymentType = "PAYPAL";
//                        paymentId = confirm.getProofOfPayment().getPaymentId();
//                        payNowCard(paymentType);
                        finish();
                    } catch (JSONException e) {
                        Log.e("222 paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.oneTrink:
                price = "1";
                oneTrink.setTextColor(getResources().getColor(R.color.white));
                oneTrink.setBackgroundResource(R.drawable.radio_click);

                secondTrink.setTextColor(getResources().getColor(R.color.black));
                secondTrink.setBackgroundResource(R.drawable.radio_not_click);

                threedTrink.setTextColor(getResources().getColor(R.color.black));
                threedTrink.setBackgroundResource(R.drawable.radio_not_click);
                break;


            case R.id.secondTrink:
                price = "2";
                oneTrink.setTextColor(getResources().getColor(R.color.black));
                oneTrink.setBackgroundResource(R.drawable.radio_not_click);

                secondTrink.setTextColor(getResources().getColor(R.color.white));
                secondTrink.setBackgroundResource(R.drawable.radio_click);

                threedTrink.setTextColor(getResources().getColor(R.color.black));
                threedTrink.setBackgroundResource(R.drawable.radio_not_click);
                break;


            case R.id.threedTrink:
                price = "3";
                oneTrink.setTextColor(getResources().getColor(R.color.black));
                oneTrink.setBackgroundResource(R.drawable.radio_not_click);

                secondTrink.setTextColor(getResources().getColor(R.color.black));
                secondTrink.setBackgroundResource(R.drawable.radio_not_click);

                threedTrink.setTextColor(getResources().getColor(R.color.white));
                threedTrink.setBackgroundResource(R.drawable.radio_click);
                break;

            case R.id.BtFertig:
                PayPalPayment payment = new PayPalPayment(new BigDecimal(price), "EUR", " ",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(TrinkgeldActivity.this, PaymentActivity.class);
                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, GlobalDataMethods.config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                startActivityForResult(intent, 0);
                break;
        }
    }
}