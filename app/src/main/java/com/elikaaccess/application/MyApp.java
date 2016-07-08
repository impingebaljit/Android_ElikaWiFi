package com.elikaaccess.application;

import android.app.Application;

import com.elikaaccess.utils.OverrideFont;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        OverrideFont.setDefaultFont(this, "SANS_SERIF", "HelveticaNeue.ttf");
    }
}
