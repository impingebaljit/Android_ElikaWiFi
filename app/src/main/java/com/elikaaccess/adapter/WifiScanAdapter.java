package com.elikaaccess.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elikaaccess.R;

import java.util.ArrayList;
import java.util.List;


public class WifiScanAdapter extends BaseAdapter {

    @SuppressWarnings("unused")
    private Context context;
    private LayoutInflater inflater;
    private List list = new ArrayList();


    public WifiScanAdapter(Context context, List list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.row_scan_wifi, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        ScanResult result = (ScanResult) getItem(position);

        holder.name.setText(result.SSID);

        if (!result.capabilities.toUpperCase().contains("WPA")
                && !result.capabilities.toUpperCase().contains("WPA"))
            holder.lock.setVisibility(View.INVISIBLE);
        else
            holder.lock.setVisibility(View.VISIBLE);

        return convertView;
    }


    private class ViewHolder{
        ImageView wifi, lock, right;
        TextView name;

        public ViewHolder(View view)
        {
            name = (TextView) view.findViewById(R.id.txtWifiName);
            wifi = (ImageView) view.findViewById(R.id.icon_wifi);
            lock = (ImageView) view.findViewById(R.id.icon_lock);
            right = (ImageView) view.findViewById(R.id.icon_arrow);

        }
    }

}
