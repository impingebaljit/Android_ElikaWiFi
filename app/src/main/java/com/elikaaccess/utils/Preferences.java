package com.elikaaccess.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class Preferences {

    public static final String SERVER = "http://10.10.100.254/EN/";


    private String productName = "productName";
    private String productImg = "productImg";


    public String getProductName() {
        return preferences.getString(productName, "");
    }

    public void setProductName(String productName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(this.productName, productName);
        editor.apply();
    }

    public int getProductImg() {
        return preferences.getInt(productImg, 0);
    }

    public void setProductImg(int productImg) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(this.productImg, productImg);
        editor.apply();
    }

    public void setProductNameImg(String productName, int productImg) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(this.productName, productName);
        editor.putInt(this.productImg, productImg);
        editor.apply();
    }



    /** Prefs Base **/
    private static SharedPreferences preferences;
    private static Preferences ourInstance = new Preferences();
    public static Preferences getInstance(Context mContext) {
        context = mContext;
        preferences = context.getSharedPreferences("EkilaPrefs", Context.MODE_PRIVATE);
        return ourInstance;
    }
    private static Context context;
    private Preferences() {
        // Empty
    }
}
