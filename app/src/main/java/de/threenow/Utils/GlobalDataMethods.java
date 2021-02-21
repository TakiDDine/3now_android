package de.threenow.Utils;

import android.app.Activity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import de.threenow.Models.GoogleDirectionModel;

public class GlobalDataMethods {

    public static String URLGetRate = "";

    public static String lastConstructedURLSuccess;

    public static HashMap<String, GoogleDirectionModel> googleDirectionModelHashMap = new HashMap<>();

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static boolean newScheduleRequest = false;

    public static int GTotalDuration;

    public static String SourceTripeLat = "";
    public static String SourceTripeLong = "";

    public static String coupon_gd_str = "";
    public static Double coupon_discount = 0d;
    public static JSONObject coupon_response;


    public static Double getDiscountCoupon(Double total) {

        String discount_type = coupon_response.optString("discount_type");
        Double discount_value = Double.parseDouble(coupon_response.optString("discount_value"));
        Double maxDiscount = Double.parseDouble(coupon_response.optString("maxDiscount"));

        if (discount_type.contains("percent")) {
            coupon_discount = total * discount_value / 100;
        }

        coupon_discount = Math.min(coupon_discount, maxDiscount);

        if (total > coupon_discount) {
//            coupon_discount = total - coupon_discount;
        } else
            coupon_discount = 0d;

        return new Long(Math.round(coupon_discount * 10) / 10).doubleValue();

    }

    public static void registerBug(String ss){
        try {
        String s="Debug-infos:";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";

        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

        String apiToken = "595265935:AAHwZhYl8xgUECCthGV-5FUHvpqjXk_JOpg";//"my_bot_api_token";
        String chatId = "-568114865";//"@my_channel_name";
        String text = "" + ss;

        urlString = String.format(urlString, apiToken, chatId, s + "\n" +text);


        URL url = null;

            url = new URL(urlString.replace(" ", "%20").replace("\n","%0A%0A"));

            Log.e("registerBug", url.toString());

            URLConnection conn = url.openConnection();

            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
//            String response = sb.toString();
            // Do what you want with response

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean ShowAdditionalLog = false; // if (GlobalDataMethods.ShowAdditionalLog)


    public static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .acceptCreditCards(false)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId("AfoqsgwA-VGO3SuWQG3q4U63L-ggmL6zze-r30PBP_WkmFIQg8F0bE8FP9g5y410-YrfMce_Hg003s0o");


}
