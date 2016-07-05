package com.elikaaccess.utils;

import android.app.Application;


public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //OverrideFont.setDefaultFont(this, "DEFAULT", "HelveticaNeue.ttf");
        OverrideFont.setDefaultFont(this, "SANS_SERIF", "HelveticaNeue.ttf");
        //OverrideFont.setDefaultFont(this, "SANS_SERIF", "HelveticaNeue.ttf");
        //OverrideFont.setDefaultFont(this, "SERIF", "HelveticaNeue.ttf");
        //OverrideFont.setDefaultFont(this, "MONOSPACE", "HelveticaNeue.ttf");
    }
}
