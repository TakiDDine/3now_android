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

            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("AfoqsgwA-VGO3SuWQG3q4U63L-ggmL6zze-r30PBP_WkmFIQg8F0bE8FP9g5y410-YrfMce_Hg003s0o");



}
