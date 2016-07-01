package com.elikaaccess;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ActivityConnectingWifi extends Activity {

    private Context context = this;
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);

        ImageView imageView = (ImageView) findViewById(R.id.imgProduct);
        TextView textView = (TextView) findViewById(R.id.txtSSID);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        final ScanResult result = (ScanResult) getIntent().getExtras().get("scanResult");
        if (result != null) {
            textView.setText(result.SSID);
        }

        switch (getIntent().getExtras().getInt("product")) {
            case 1:
                imageView.setImageResource(R.drawable.elika_460);
                break;

            case 2:
                imageView.setImageResource(R.drawable.elika_92);
                break;

            case 3:
                imageView.setImageResource(R.drawable.elika_76);
                break;
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectWiFi(result);
            }
        }, 500);

    }


    private void connectWiFi(ScanResult scanResult) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

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

        new Handler().postDelayed(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isAvailable())
                    if (mWifi.isConnected()) {
                        Toast.makeText(context, "Connected to \"" + networkSSID + "\" successfully", Toast.LENGTH_SHORT).show();
                        txtStatus.setText("Connected to \"" + networkSSID + "\" successfully");
                    }
                    else if (mWifi.isFailover()) {
                        Toast.makeText(context, "Failed to connect to \"" + networkSSID + "\", Please try again.", Toast.LENGTH_SHORT).show();
                        txtStatus.setText("Failed to connect to \"" + networkSSID + "\", Please try again");
                    }
                    else
                        connectStatus(networkSSID);
                else {
                    Toast.makeText(context, "It seems Wifi is turned off, Please try again", Toast.LENGTH_SHORT).show();
                    txtStatus.setText("It seems Wifi is turned off, Please try again");
                }
            }
        }, 1000);


    }

}
