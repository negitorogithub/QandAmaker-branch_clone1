package unifar.unifar.qandamaker;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;

import hotchemi.android.rate.AppRate;


public class MyApplication extends Application {
    public static Context context;
    public static Bundle bundle;
    public static int viewFlag;
    public static int adCount;
    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
        LeakCanary.install(this);
        bundle = new Bundle();
        bundle.putString("str_tag_name", "");
        bundle.putBoolean("isRecreated", false);
        bundle.putBoolean("isEditMode", false);
        adCount = 0;
        viewFlag = 0;
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(14) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .monitor();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
