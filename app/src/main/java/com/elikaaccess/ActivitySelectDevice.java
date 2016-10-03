package com.elikaaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.elikaaccess.utils.Preferences;

public class ActivitySelectDevice extends Activity implements View.OnClickListener {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);


        View view = findViewById(R.id.include);
        view.findViewById(R.id.imgBack).setVisibility(View.VISIBLE);
        view.findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.txtTitle)).setText(getResources().getString(R.string.wifi_setup_wizard));

        view.findViewById(R.id.imgBack).setOnClickListener(this);

        findViewById(R.id.product_1).setOnClickListener(this);
       // findViewById(R.id.product_2).setOnClickListener(this);
        //findViewById(R.id.product_3).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {

        final Intent intent = new Intent(context, ActivitySetupWizard.class);

        v.setBackgroundColor(Color.parseColor("#AAC2C2C2"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (v.getId()) {
                    case R.id.product_1:
                        Preferences.getInstance(context)
                                .setProductNameImg(
                                        getResources()
                                                .getString(R.string.elika_460), R.drawable.elika_460);

                        intent.putExtra("product", 1);
                        startActivity(intent);
                        v.setBackgroundResource(R.drawable.bg_green_border);
                        break;

                  /*  case R.id.product_2:

                        Preferences.getInstance(context)
                                .setProductNameImg(
                                        getResources()
                                                .getString(R.string.elika_92), R.drawable.elika_92);

                        intent.putExtra("product", 2);
                        startActivity(intent);
                        v.setBackgroundResource(R.drawable.bg_green_border);
                        break;
*/
                 /*   case R.id.product_3:
                      Preferences.getInstance(context)
                                .setProductNameImg(
                                        getResources()
                                                .getString(R.string.elika_76), R.drawable.elika_76);

                        intent.putExtra("product", 3);
                        startActivity(intent);
                        v.setBackgroundResource(R.drawable.bg_green_border);
                        break;*/

                    case R.id.imgBack:
                        finish();
                        v.setBackgroundColor(Color.TRANSPARENT);
                        break;
                }


            }
        }, 150);

    }
}
