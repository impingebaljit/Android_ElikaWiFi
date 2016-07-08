package com.elikaaccess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
/*
        try {
            Crittercism.initialize(
                    getApplicationContext(),
                    "0394cf3c2b8b4f80a11843750a04715800555300");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, ActivitySetupElika.class));
                finish();
            }
        }, 1500);
    }
}
