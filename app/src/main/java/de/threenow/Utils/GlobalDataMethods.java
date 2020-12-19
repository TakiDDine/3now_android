package de.threenow.Utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import org.json.JSONObject;

import java.util.HashMap;

import de.threenow.Models.GoogleDirectionModel;

public class GlobalDataMethods {

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


    public static Double getDiscountCoupon(Double total){

        String discount_type = coupon_response.optString("discount_type");
        Double discount_value = Double.parseDouble(coupon_response.optString("discount_value"));
        Double maxDiscount = Double.parseDouble(coupon_response.optString("maxDiscount"));

        if (discount_type.contains("percent")){
            coupon_discount = total * discount_value /100;
        }

        coupon_discount = Math.min(coupon_discount, maxDiscount);

        if (total > coupon_discount) {
//            coupon_discount = total - coupon_discount;
        }else
            coupon_discount = total;

        return new Long(Math.round(coupon_discount * 10) / 10).doubleValue();

    }

    public static boolean ShowAdditionalLog = false; // if (GlobalDataMethods.ShowAdditionalLog)


    public static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .acceptCreditCards(false)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId("AfoqsgwA-VGO3SuWQG3q4U63L-ggmL6zze-r30PBP_WkmFIQg8F0bE8FP9g5y410-YrfMce_Hg003s0o");


}
