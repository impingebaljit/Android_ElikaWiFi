package com.elikaaccess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityWifiConnectStatus extends Activity {

    private Context context = this;

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connection_status);

        View view = findViewById(R.id.include);
        view.findViewById(R.id.imgBack).setVisibility(View.VISIBLE);
        view.findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setText(getResources().getString(R.string.searching_elika));

        view.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivitySetupWizard.wifiManager != null)
                    ActivitySetupWizard.wifiManager.disconnect();
                finish();
            }
        });

        final String status = this.getIntent().getStringExtra("status");

        TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
        Button btnNext = (Button) findViewById(R.id.btnNext);


        switch (status)
        {
            case "success":
                txtStatus.setTextColor(getResources().getColor(R.color.text_green));
                txtStatus.setText("Connected to Elika device successfully.Now search for available Wi-Fi networks");
                btnNext.setText(getResources().getString(R.string.next_step));
                txtTitle.setText("Elika Connected");
                break;

            case "failure":
                txtStatus.setTextColor(Color.RED);
                txtStatus.setText("Connection Fail\nPlease Enter Correct Password or Try to get closer to the unit");
                btnNext.setText(getResources().getString(R.string.try_again));
                txtTitle.setText("Elika Connection Failed");
                break;

            default:

                break;
        }


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equals("failure"))
                {
                    Intent intent = new Intent(context,ActivitySetupWizard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(context,ActivitySearchLocalWifi.class);
                    context.startActivity(intent);
                    finish();  // by Haps
                }

            }
        });

    }
}