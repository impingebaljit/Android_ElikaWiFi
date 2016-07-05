package com.elikaaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.elikaaccess.utils.GIFView;
import com.elikaaccess.utils.Preferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class ActivitySearchLocalWifi extends Activity {
    private Context context = this;
    private List<String> rows = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private LinearLayout searchingView, listingView;
    private ImageView imgBack;

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(context, ActivitySetupElika.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_local);

        View view = findViewById(R.id.include);
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        imgBack.setVisibility(View.GONE);
        imgBack.setImageResource(R.drawable.home);

        view.findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.txtTitle)).setText(getResources().getString(R.string.searching_wifi));

        view.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        GIFView gifView = (GIFView) findViewById(R.id.imgSearch);
        gifView.setImageResource(R.drawable.wifi_loader);

        ((TextView) findViewById(R.id.txtProduct)).setText(Preferences.getInstance(context).getProductName());
        ((ImageView) findViewById(R.id.imgProduct)).setImageResource(Preferences.getInstance(context).getProductImg());

        searchingView = (LinearLayout) findViewById(R.id.layer_searching);
        listingView = (LinearLayout) findViewById(R.id.layer_listing);
        ListView listView = (ListView) findViewById(R.id.listWifi);
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, rows);
        listView.setAdapter(adapter);

        searchingView.setVisibility(View.VISIBLE);
        listingView.setVisibility(View.GONE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ParseUrl(
                        "http://urtestsite.com/projects/MobileApps/Pejman_%20Karimi/Elika/apis/site_survey.html")
                        .execute();
            }
        }, 1000);
    }


    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private class ParseUrl extends AsyncTask<Void, Void, Void>
    {
        private String url;
        private List<Elements> elements = new ArrayList<>();


        ParseUrl(String url)
        {
            this.url = url;
        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document document = Jsoup.connect(url).get();

                elements.clear();

                Element table = document.select("table").get(0);
                    for (Element row : table.select("tr")) {
                        Elements tds = row.select("td");
                        elements.add(tds);
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        protected void onPostExecute(Void aVoid) {

            rows.clear();

            int x;
            for (x = 2; x < elements.size() - 1; x++)
            {
                Elements element = elements.get(x);
                String response = element.get(1).text();

                /*for (Element elem : element)
                {
                    response += "/" + elem.text();
                }*/

                rows.add(response);
            }

            if (adapter != null)
                adapter.notifyDataSetChanged();

            searchingView.setVisibility(View.GONE);
            listingView.setVisibility(View.VISIBLE);
            imgBack.setVisibility(View.VISIBLE);


        }
    }
}
