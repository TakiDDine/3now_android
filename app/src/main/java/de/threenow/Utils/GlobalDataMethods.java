package de.threenow.Utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.paypal.android.sdk.payments.PayPalConfiguration;

public class GlobalDataMethods {

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

    public static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)

            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId("AdThF4H9jvgJOz5hde4Mj_0SjJmsgAa9DaPmRBH7ZAqlKvmu5XNTX4b2Z7wFtzeX33frjAUROiXJh_lr");



}
