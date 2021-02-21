package de.threenow;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;

//import io.fabric.sdk.android.Fabric;
//import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by jayakumar on 29/01/17.
 */

public class IlyftApplication extends Application {

    public static final String TAG = IlyftApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static IlyftApplication mInstance;


    @Override
    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
        if (SharedHelper.getKey(base, "lang") != null)
            super.attachBaseContext(LocaleManager.setNewLocale(base, SharedHelper.getKey(base, "lang")));
        else
            super.attachBaseContext(LocaleManager.setNewLocale(base, "de"));


        MultiDex.install(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
}

    public void setLocale(String lang) {
        Log.e("language", Locale.getDefault().getDisplayLanguage());
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

//        SharedPrefrence.setLanguage(Profile.this,lang);
        SharedHelper.putKey(this, "lang", lang);

//        Intent refresh = new Intent(this, MainActivity.class);
//        finish();
//        startActivity(refresh);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setLangFirstTime();
        mInstance = this;
        EmojiManager.install(new IosEmojiProvider());

//        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
//            //Catch your exception
//            // Without System.exit() this will not work.
////                System.exit(2);
//
//            Log.e("IlyftApplication", paramThrowable.getMessage());
//        });

//        initCalligraphyConfig();
//        Fabric fabric = new Fabric.Builder(IlyftApplication.this)
//                .kits(new Crashlytics())
//                .debuggable(BuildConfig.DEBUG)
//                .build();
//
//        Fabric.with(fabric);

    }

    private void setLangFirstTime() {
        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            setLocale("de");

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }
    }

//    private void initCalligraphyConfig() {
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath(getResources().getString(R.string.bariol))
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );
//    }

    public static synchronized IlyftApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void cancelRequestInQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public static String trimMessage(String json) {
        String trimmedString = "";

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = jsonObject.getJSONArray(key);
                    for (int i = 0, size = value.length(); i < size; i++) {
                        Log.e("Errors in Form", "" + value.getString(i));
                        trimmedString += value.getString(i);
                        if (i < size - 1) {
                            trimmedString += '\n';
                        }
                    }
                } catch (JSONException e) {

                    trimmedString += jsonObject.optString(key);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        Log.e("Trimmed", "" + trimmedString);

        return trimmedString;
    }


}
