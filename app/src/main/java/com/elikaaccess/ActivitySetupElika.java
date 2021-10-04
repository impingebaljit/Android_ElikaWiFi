package com.elikaaccess;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
        TextView textView = (TextView) view.findViewById(R.id.txtTitle);
        textView.setText(getResources().getString(R.string.home));

        txtSetup = (TextView) findViewById(R.id.textSetup);

        findViewById(R.id.textSetup).setOnClickListener(this);
        findViewById(R.id.imgSetup).setOnClickListener(this);

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasInternetPermission = checkSelfPermission(android.Manifest.permission.INTERNET);
            int hasCoarsePermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasFinePermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasWifiPermission = checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
            int hasNEtworkPermission = checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE);
            int hasChangeWifiPermission = checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE);

            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.INTERNET);
            }
            if (hasCoarsePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (hasFinePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (hasWifiPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (hasNEtworkPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.CHANGE_NETWORK_STATE);
            }
            if (hasChangeWifiPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.CHANGE_WIFI_STATE);
            }

            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), 2);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    tryToReadSSID();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission is required to acess the App", Toast.LENGTH_LONG).show();
                    finish();

                }

                break;
        }
    }

    private void tryToReadSSID() {
        //If requested permission isn't Granted yet
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {//Permission already granted
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                String ssid = wifiInfo.getSSID();//Here you can access your SSID
                System.out.println(ssid);
            }
        }
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
