package com.elikaaccess.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Wifi implements Parcelable {

    private String SSID, BSSID, RSSI, encryption, authentication, channel, passKey, networkType;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String encryption) {
        this.channel = channel;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public Wifi(){}

    /** Parcel Data Models **/
    private Wifi(@SuppressWarnings("unused") Parcel in) {
    }

    public static final Creator<Wifi> CREATOR = new Creator<Wifi>() {
        @Override
        public Wifi createFromParcel(Parcel in) {
            return new Wifi(in);
        }

        @Override
        public Wifi[] newArray(int size) {
            return new Wifi[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
