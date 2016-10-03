package com.elikaaccess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elikaaccess.adapter.LocalWifiAdapter;
import com.elikaaccess.model.Wifi;
import com.elikaaccess.utils.GIFView;
import com.elikaaccess.utils.Preferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class ActivitySearchLocalWifi extends Activity {
    private Context context = this;
    private List<Wifi> rowsWifi = new ArrayList<>();

    //private ArrayAdapter<String> adapter;
    private LocalWifiAdapter adapter;
    private LinearLayout searchingView, listingView, layerSuccess;
    private ImageView imgBack, imgRefresh;
    private TextView txtTitle;
    private ListView listView;

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // remove it and do last

        try {
            Intent intent = new Intent(context,ActivitySetupElika.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish(); // By haps
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_local);

        View view = findViewById(R.id.include);
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        imgRefresh = (ImageView) view.findViewById(R.id.imgHelp);
        imgRefresh.setImageResource(R.drawable.refresh_real);
        imgBack.setVisibility(View.GONE);
        imgBack.setImageResource(R.drawable.home);

        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(getResources().getString(R.string.searching_wifi));


        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgRefresh.setFocusable(false);
                imgRefresh.setImageResource(R.drawable.refresh_blank);
                new ParseUrl(Preferences.SERVER +"site_survey.html").execute();
            }
        });


        GIFView gifView = (GIFView) findViewById(R.id.imgSearch);
        gifView.setImageResource(R.drawable.wifi_loader);

        ((TextView) findViewById(R.id.txtProduct)).setText(Preferences.getInstance(context).getProductName());
        ((ImageView) findViewById(R.id.imgProduct)).setImageResource(Preferences.getInstance(context).getProductImg());

        searchingView = (LinearLayout) findViewById(R.id.layer_searching);
        listingView = (LinearLayout) findViewById(R.id.layer_listing);
        layerSuccess = (LinearLayout) findViewById(R.id.layer_success);
        listView = (ListView) findViewById(R.id.listWifi);
        //adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, rowsWifi);
        adapter = new LocalWifiAdapter(context, rowsWifi);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (rowsWifi.get(position).getEncryption().contains("WEP") ||
                        rowsWifi.get(position).getAuthentication().contains("WPA"))
                    askForPassword(rowsWifi.get(position));
                else
                    new ConnectWifi(rowsWifi.get(position)).execute(); // OPEN Network
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Wifi wifi = rowsWifi.get(position);
                String text = "Name:" + wifi.getSSID()
                        + "\nBSSID:" + wifi.getBSSID()
                        + "\nSignal:" + wifi.getRSSI()
                        + "\nAuth:" + wifi.getAuthentication()
                        + "\nEncryption:" + wifi.getEncryption()
                        + "\nNetwork:" + wifi.getNetworkType();

                Toast.makeText(context, text, Toast.LENGTH_LONG).show();

                return false;
            }
        });

        searchingView.setVisibility(View.VISIBLE);
        listingView.setVisibility(View.GONE);
        layerSuccess.setVisibility(View.GONE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ParseUrl(Preferences.SERVER +
                        "site_survey.html")
                        .execute();
            }
        }, 1000);
    }


    private class ParseUrl extends AsyncTask<Void, Void, String> {
        private String url;
        private List<Elements> elements = new ArrayList<>();


        public ParseUrl(String url) {
            this.url = url;
        }


        @Override
        protected String doInBackground(Void... params) {

            try {
                Document document = Jsoup.connect(url).timeout(20000).get();

                Log.e("Document data::", "" + document.text());

                elements.clear();

                Element table = document.select("table").get(0);
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    elements.add(tds);
                }

                return String.valueOf(elements.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        protected void onPostExecute(String s) {

            rowsWifi.clear();

            if (elements.size() > 3) // unwanted but added.
                for (int x = 3; x < elements.size() - 1; x++) {
                    Elements element = elements.get(x);
                    String SSID = element.get(1).text(); // Name
                    String BSSID = element.get(2).text(); // Mac Address
                    String RSSI = element.get(3).text(); // %age signals
                    String Channel = element.get(4).text(); // No of channels
                    String Encryption = element.get(5).text(); // Key authentication types (AES,WEP)
                    String Authentication = element.get(6).text(); // Wifi Auth Type (Ope, Shared, WPA)
                    String NetworkType = element.get(7).text(); // Infrastructure

                    /** For unique cases where we find no SSID name for any wifi **/
                    if (RSSI.startsWith("ABOVE")) {
                        RSSI = BSSID + "%";

                        String[] split = SSID.split(" ");
                        if (split.length > 0)
                            BSSID = split[0];
                        SSID = "- No name Wifi -";
                    }


                    Wifi wifi = new Wifi();
                    wifi.setSSID(SSID);
                    wifi.setBSSID(BSSID);
                    wifi.setRSSI(RSSI);
                    wifi.setChannel(Channel);
                    wifi.setAuthentication(Authentication);
                    wifi.setEncryption(Encryption);
                    wifi.setNetworkType(NetworkType);


                    if (!NetworkType.toLowerCase().equals("ad hoc")) {
                        rowsWifi.add(wifi);
                    }
                }

            TextView textView = new TextView(context);
            textView.setTextSize(15f);
            textView.setTextColor(Color.DKGRAY);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);


            if (s == null) {
                textView.setText("Error while communication with device, Please try again.");
                listView.setEmptyView(textView);
            } else if (elements.size() == 0) {
                textView.setText("No wifi found, Please try again.");
                listView.setEmptyView(textView);
            }

            if (adapter != null)
                adapter.notifyDataSetChanged();

            searchingView.setVisibility(View.GONE);
            listingView.setVisibility(View.VISIBLE);
            layerSuccess.setVisibility(View.GONE);

            imgBack.setVisibility(View.VISIBLE);
            imgRefresh.setVisibility(View.VISIBLE);

            imgRefresh.setFocusable(true);
            imgRefresh.setImageResource(R.drawable.refresh_real);
        }
    }


    private class ConnectWifi extends AsyncTask<Void, Void, String> {
        private Wifi wifi;

        public ConnectWifi(Wifi wifi) {
            this.wifi = wifi;
        }

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            imgRefresh.setVisibility(View.GONE);

            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Configuring device...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return performPostCall(Preferences.SERVER +"do_cmd.html",wifi);
        }


        public String performPostCall(String requestURL, Wifi wifi) {
            /**
             * [wifiDict setObject:[NSString stringWithFormat:@"81723904=%@", array[0] ] forKey:@"SET0"]; //ssid_name
             [wifiDict setObject:[NSString stringWithFormat:@"81068544=%@", array[1] ] forKey:@"SET1"]; //apcli_bssid
             [wifiDict setObject:[NSString stringWithFormat:@"81264896=%@", array[3] ] forKey:@"SET6"]; //Enty_Wep
             // [wifiDict setObject:array[3] forKey:@"Channel"];
             [wifiDict setObject:[NSString stringWithFormat:@"81199616=%@", array[4] ] forKey:@"SET2"]; //Encryption Type
             [wifiDict setObject:[NSString stringWithFormat:@"81134080=%@", array[5] ] forKey:@"SET3"]; //Security mode

             wifiDict setObject:@"LAN" forKey:@"CMD"];
             [wifiDict setObject:@"M2M%20Web%20Server.html" forKey:@"GO”];
             */

            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

              /*  Map<String, String> postDataParams = new HashMap<>();
                postDataParams.put("ssid_name", wifi.getSSID());
                postDataParams.put("apcli_bssid", wifi.getBSSID());
                postDataParams.put("S_SecurityMode", wifi.getNetworkType());
                postDataParams.put("S_EncryptionType", wifi.getEncryption());

                if (wifi.getEncryption().contains("WEP")) {
                    postDataParams.put("S_EnTy_KEY", wifi.getPassKey());
                    postDataParams.put("S_EnTyWEP", wifi.getAuthentication());
                } else if (wifi.getNetworkType().contains("WPA")) {
                    postDataParams.put("S_EnTy_Pass_P", wifi.getPassKey());
                }*/

                Map<String, String> postDataParams = new HashMap<>();

                /*NSMutableDictionary *wifiDict = [NSMutableDictionary new];
                [wifiDict setObject:[NSString stringWithFormat:@"81723904=%@", array[0] ] forKey:@"SET0"]; //ssid_name
                [wifiDict setObject:@"81068544=" forKey:@"SET5"]; //apcli_bssid
                [wifiDict setObject:[NSString stringWithFormat:@"81264896=%@", array[3] ] forKey:@"SET4"]; //Enty_Wep
                [wifiDict setObject:[NSString stringWithFormat:@"81199616=%@", array[4] ] forKey:@"SET1"]; //Encryption Type
                [wifiDict setObject:[NSString stringWithFormat:@"81134080=%@", array[5] ] forKey:@"SET2"]; //Security mode
                [wifiDict setObject:@"LAN" forKey:@"CMD"];
                [wifiDict setObject:@"M2M%20Web%20Server.html" forKey:@"GO"];

                [selectedWifiDict setObject:[NSString stringWithFormat:@"81330688="] forKey:@"SET3"];*/

                if (wifi != null) {
                    // For saving configurations
                    postDataParams.put("SET0", "81723904=" + wifi.getSSID());
                    postDataParams.put("SET1", "81199616=" + wifi.getEncryption());
                    postDataParams.put("SET2", "81134080=" + wifi.getAuthentication());
                    //postDataParams.put("SET3", "81330688=" + wifi.getPassKey()); // IF NONE, key = "";
                    postDataParams.put("SET4", "81264896=" + wifi.getChannel());
                    postDataParams.put("SET5", "81068544="); // + wifi.getBSSID());
                    postDataParams.put("CMD", "LAN");
                    postDataParams.put("GO", "M2M%20Web%20Server.html");


                    if (wifi.getEncryption().contains("NONE"))
                        postDataParams.put("SET3", "81330688=");
                    else if (wifi.getEncryption().contains("WEP"))
                        postDataParams.put("SET3", "81330688=" + wifi.getPassKey());
                    else if (wifi.getAuthentication().contains("WPA"))
                        postDataParams.put("SET3", "81658368=" + wifi.getPassKey());

                    Log.e("Saving data", "Encr: " + wifi.getEncryption() + " / Auth:" + wifi.getAuthentication());

                } else {
                    /*[restartDict setObject:@"SYS_CONF" forKey:@"CMD"];
                    [restartDict setObject:@"M2M%20Web%20Server.html" forKey:@"GO"];
                    [restartDict setObject:@"0" forKey:@"CCMD"];*/

                    // For Rebooting
                    postDataParams.put("CMD", "SYS_CONF");
                    postDataParams.put("GO", "M2M%20Web%20Server.html");
                    postDataParams.put("CCMD", "0");

                }

                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "Error:" + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(response);

            return response;
        }

        private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

            try {
                //JSONObject object = new JSONObject(s);
                // {"stutus":"Information logged"}
                if (s.contains("Set Successfully")) {
                    //Toast.makeText(context, object.getString("stutus"), Toast.LENGTH_SHORT).show();
                    searchingView.setVisibility(View.GONE);
                    listingView.setVisibility(View.GONE);
                    layerSuccess.setVisibility(View.VISIBLE);
                    imgRefresh.setVisibility(View.GONE);
                    txtTitle.setText(getResources().getString(R.string.configration));


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new RebootCall().execute();
                        }
                    }, 200);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class RebootCall extends AsyncTask<Void,Void,String> {
        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(context);
            dialog.setMessage("Rebooting device ...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return new ConnectWifi(null).performPostCall(Preferences.SERVER + "restart.html",null);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

            removeNetwork();

            // Rebooted device.
        }
    }

    @SuppressLint("SetTextI18n")
    private void askForPassword(final Wifi wifi) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle(wifi.getSSID());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ask_password);

        ((TextView) dialog.findViewById(R.id.txtSSID)).setText(wifi.getSSID());

        final EditText editText = (EditText) dialog.findViewById(R.id.edtPassword);
        ((Button) dialog.findViewById(R.id.btnSave)).setText("Confirm");
        dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setError(null);

                int length = editText.getText().length();


                if (length > 0) {

                    if (wifi.getEncryption().contains("WEP")) {
                        if (wifi.getAuthentication().equals("1") && !(length == 5 || length == 13)) {
                            Toast.makeText(context, "Please enter password of 5 or 13 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (wifi.getAuthentication().equals("0") && !(length == 10 || length == 26)) {
                            Toast.makeText(context, "Please enter password of 10 or 26 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (wifi.getNetworkType().contains("WPA")) {
                        if (length < 8 || length > 63) {
                            Toast.makeText(context, "Password too short. Please enter password minimum 8 to 63 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    wifi.setPassKey(editText.getText().toString());
                    dialog.dismiss();

                    new ConnectWifi(wifi).execute();
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


    @SuppressWarnings("unused")
    private boolean removeNetwork() {
        boolean isConnected = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration i : list) {
            Log.e("LSIT", " SSID :: " + i.SSID);
            if (i.SSID.contains("Elika")) {
                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();
                isConnected = true;
            }
        }
        return isConnected;
    }

}
