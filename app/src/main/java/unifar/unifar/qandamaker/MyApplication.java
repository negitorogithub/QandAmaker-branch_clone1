package unifar.unifar.qandamaker;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;


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
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
