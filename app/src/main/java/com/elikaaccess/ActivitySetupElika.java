package com.elikaaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class ActivitySetupElika extends Activity implements View.OnClickListener {

    private Context context = this;
    private TextView txtSetup;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_elika);

        View view = findViewById(R.id.include);
        view.findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
        TextView textView = (TextView)view.findViewById(R.id.txtTitle);
        textView.setText(getResources().getString(R.string.home));

        txtSetup = (TextView) findViewById(R.id.textSetup);

        findViewById(R.id.textSetup).setOnClickListener(this);
        findViewById(R.id.imgSetup).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {

        txtSetup.setTextColor(Color.GRAY);

        new Handler().postDelayed(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.textSetup:
                        startActivity(new Intent(context, ActivitySelectDevice.class)); //ActivitySelectDevice
                        break;

                    case R.id.imgSetup:
                        startActivity(new Intent(context, ActivitySelectDevice.class)); //ActivityLocalWifi
                        break;

                }
                txtSetup.setTextColor(getResources().getColor(R.color.white));
            }
        }, 150);

    }
}
