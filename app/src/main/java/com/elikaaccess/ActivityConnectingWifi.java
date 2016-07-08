package com.elikaaccess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elikaaccess.utils.GIFView;
import com.elikaaccess.utils.Preferences;

import java.util.List;

public class ActivityConnectingWifi extends Activity {

    private final int RETRY_ATTEMPTS = 20;
    private Context context = this;
    private int tryWifi = 0;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);


        View view = findViewById(R.id.include);
        view.findViewById(R.id.imgBack).setVisibility(View.GONE);
        view.findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.txtTitle)).setText(getResources().getString(R.string.searching_elika));
        view.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivitySetupWizard.wifiManager != null)
                    ActivitySetupWizard.wifiManager.disconnect();
                finish();
            }
        });


        GIFView gifView = (GIFView) findViewById(R.id.gifView);
        gifView.setImageResource(R.drawable.wifi_loader);



        ImageView imageView = (ImageView) findViewById(R.id.imgProduct);
        TextView textView = (TextView) findViewById(R.id.txtSSID);

        final ScanResult result = (ScanResult) getIntent().getExtras().get("scanResult");
        if (result != null) {
            textView.setText(result.SSID);
        }

        imageView.setImageResource(Preferences.getInstance(context).getProductImg());
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectWiFi(result);
            }
        }, 500);


    }


    private void connectWiFi(ScanResult scanResult) {
        try {


            Log.e("LOG", "Item clicked, SSID " + scanResult.SSID + ", Security : " + scanResult.capabilities);

            String networkSSID = scanResult.SSID;
            String networkPass = getIntent().getExtras().getString("password");

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;

            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass != null) {
                    if (networkPass.matches("^[0-9a-fA-F]+$")) {
                        conf.wepKeys[0] = networkPass;
                    } else {
                        conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                    }
                }

                conf.wepTxKeyIndex = 0;

            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";

            } else {
                Log.v("rht", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            wifiManager.disconnect(); // Disconnect from current Wifi point first.

            System.out.println("Removing Network ::" + wifiManager.getConnectionInfo().getNetworkId());
            System.out.println("Network removed::" + wifiManager.removeNetwork(wifiManager.getConnectionInfo().getNetworkId())); // Remove the Account first.

            wifiManager.removeNetwork(wifiManager.getConnectionInfo().getNetworkId());
            System.out.println("Saving removed::" + wifiManager.saveConfiguration());

            int networkId = wifiManager.addNetwork(conf);

            Log.v("rht", "Add result " + networkId);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    Log.v("rht", "WifiConfiguration SSID " + i.SSID);

                    boolean isDisconnected = wifiManager.disconnect();
                    Log.v("rht", "isDisconnected : " + isDisconnected);

                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    Log.v("rht", "isEnabled : " + isEnabled);

                    boolean isReconnected = wifiManager.reconnect();
                    Log.v("rht", "isReconnected : " + isReconnected);

                    Log.v("rht", "+++++++++++NEXT++++++++++++ == " + i);


                    if (isReconnected) {
                        connectStatus(networkSSID);
                    }
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectStatus(final String networkSSID) {

        final Intent intent = new Intent(context, ActivityWifiConnectStatus.class);
        intent.putExtra("SSID", networkSSID);

        new Handler().postDelayed(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isAvailable())
                    if (mWifi.isConnected()
                            &&
                            (wifiManager.getConnectionInfo().getSSID().equals(networkSSID)
                            ||
                            wifiManager.getConnectionInfo().getSSID().equals("\"" + networkSSID + "\""))) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            showMAlert(intent);
                        else
                        {
                            intent.putExtra("status", "success");
                            startActivity(intent);
                            finish();
                        }
                    }
                    else if (mWifi.isFailover()) {
                        intent.putExtra("status", "failure");
                        startActivity(intent);
                        finish();
                    }
                    else if (tryWifi >= RETRY_ATTEMPTS)
                    {

                        if (ActivitySetupWizard.wifiManager != null && ActivitySetupWizard.wifiManager.isWifiEnabled()) {
                            ActivitySetupWizard.wifiManager.disconnect();
                        }
                        intent.putExtra("status", "failure");
                        startActivity(intent);
                        finish();
                    }
                else
                        connectStatus(networkSSID);
                else {
                    intent.putExtra("status", "failure");
                    startActivity(intent);
                    finish();
                }

                tryWifi++;
            }
        }, 1000);


    }

    private void showMAlert(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Connected to device");
        builder.setMessage("If you are connected to wrong wifi device. " +
                "Then please try to reconnect after removing old enabled wifi connection manually from Settings.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                finish();
            }
        });

        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                intent.putExtra("status", "success");
                startActivity(intent);
                finish();
            }
        });

        builder.show();
    }

}
