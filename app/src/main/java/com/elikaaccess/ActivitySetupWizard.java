package com.elikaaccess;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elikaaccess.adapter.WifiScanAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivitySetupWizard extends Activity implements View.OnClickListener {

    private Context context = this;
    public static WifiManager wifiManager;
    private LocationManager locationManager;
    private ListView listViewWifi;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 101;
    private ProgressDialog pDiaog = null;
    private List<ScanResult> listAvailableWifi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wizard);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.pDialog();

        View view = findViewById(R.id.include);
        view.findViewById(R.id.imgBack).setVisibility(View.VISIBLE);
        view.findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.txtTitle)).setText(getResources().getString(R.string.setup_wizard));
        view.findViewById(R.id.imgBack).setOnClickListener(this);

        listViewWifi = (ListView) findViewById(R.id.listWifi);

        /** register a receiver to get all wifi results **/
        registerReceiver(receiveWifiResults, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    void pDialog()
    {
        pDiaog = new ProgressDialog(context);
        pDiaog.setMessage("Laoding ...");
        pDiaog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.wifiStateChanges();

        //updateList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            Log.e("LOG", "Update Wifi list - GRANTED PERMISSION");
            //updateList();
            requestToEnableGPS();
        }
    }

    /**
     * Module to enable device wifi is Wifi is at OFF state
     **/
    private void wifiStateChanges() {
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        wifiManager.startScan();
        if (pDiaog != null)
            pDiaog.show();
    }

    private BroadcastReceiver receiveWifiResults = new BroadcastReceiver() {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context c, Intent intent) {
            /** Update list once scan is complete **/
            Log.e("LOG", "Wifi scanning complete");

            if (pDiaog != null && pDiaog.isShowing())
                pDiaog.dismiss();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
                else
                    requestToEnableGPS();
            }
            else {
                updateList();
                Log.e("LOG", "Update Wifi list - PreCode");
            }

        }
    };

    private void updateList() {
        listAvailableWifi = wifiManager.getScanResults();

        getElikaWifiList(); // re-arrange list

        WifiScanAdapter scanAdapter = new WifiScanAdapter(context, listAvailableWifi);
        listViewWifi.setAdapter(scanAdapter);
        scanAdapter.notifyDataSetChanged();

        listViewWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ScanResult result = listAvailableWifi.get(position);

                //System.out.println("Remove:" + removeNetwork(result));

                if (!result.capabilities.toUpperCase().contains("WPA")
                        && !result.capabilities.toUpperCase().contains("WEP"))
                    callNextScreen(result, ""); // Open network
                else
                    askForPassword(result); // Password protected network
            }
        });

        Log.e("LOG", "Total wifi : " + listAvailableWifi.size());
    }

    private void getElikaWifiList() {

        List<ScanResult> listReturn = new ArrayList<>();
        for (ScanResult result : this.listAvailableWifi)
            if (result.SSID.toLowerCase().startsWith("elika"))
                listReturn.add(result);

        this.listAvailableWifi = listReturn;


    }

    @SuppressWarnings("unused")
    private boolean removeNetwork(ScanResult result)
    {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        for( WifiConfiguration i : list ) {
            Log.e("SSID:" + result.SSID, " with :: " + i.SSID);
            if (i.SSID.equals("\"" + result.SSID + "\"")) {
                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        if (receiveWifiResults != null)
            unregisterReceiver(receiveWifiResults);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
        }
    }

    private void askForPassword(final ScanResult scanResult) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle(scanResult.SSID);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ask_password);

        ((TextView) dialog.findViewById(R.id.txtSSID)).setText(scanResult.SSID);

        final EditText editText = (EditText) dialog.findViewById(R.id.edtPassword);
        dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setError(null);
                int length = editText.getText().length();
                if ( length > 0) {
                    if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                        if (!(length == 5 || length == 13 ||length == 10 || length == 26)) {
                            Toast.makeText(context, "Please enter password of 5, 10, 13 or 26 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    else if (scanResult.capabilities.toUpperCase().contains("WPA"))
                    if (length < 8 || length > 63) {
                        Toast.makeText(context, "Password too short. Please enter password minimum 8 to 63 characters.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    callNextScreen(scanResult, editText.getText().toString());

                    dialog.dismiss();
                } else {
                    editText.requestFocus();
                    editText.setError("Enter password");
                }
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void callNextScreen(ScanResult scanResult, String password)
    {
        Intent intent = new Intent(context, ActivityConnectingWifi.class);
        intent.putExtra("password", password);
        intent.putExtra("scanResult", scanResult);
        startActivity(intent);
    }

    private void requestToEnableGPS()
    {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.getProvider("gps") != null)
            updateList();
        else {
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(context, "Please turn on GPS to avail our services", Toast.LENGTH_LONG).show();
        }
    }
}