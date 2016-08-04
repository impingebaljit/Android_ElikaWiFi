package com.elikaaccess.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elikaaccess.R;
import com.elikaaccess.model.Wifi;


import java.util.List;

public class LocalWifiAdapter extends BaseAdapter {

    private List list;
    private LayoutInflater inflater;

    public LocalWifiAdapter(Context context, List list)
    {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.row_local_wifi, null);
            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Wifi wifi = (Wifi)getItem(position);

        //if (wifi.)

        holder.name.setText(wifi.getSSID());
        holder.signal.setText(wifi.getRSSI());

        if (wifi.getEncryption().toLowerCase().contains("none"))
            holder.img.setVisibility(View.GONE);


        return convertView;
    }


    private class ViewHolder
    {
        TextView name, signal;
        ImageView img;

        public ViewHolder(View v)
        {
            img = (ImageView) v.findViewById(R.id.imgSecure);
            name = (TextView) v.findViewById(R.id.txtWifiName);
            signal = (TextView) v.findViewById(R.id.txtWifiSignal);
        }
    }
}
