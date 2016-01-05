package com.kromer.foursquareexplorer;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefVenues {

    private static SharedPreferences cacheVenues;
    public static final String CACHEVENUES = "cacheVenues";
    public static final String DEFAULT_VALUE = "DEFAULT";
    public static final String KEY_DATA = "data";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_OAUTH_TOKEN = "oauth_token";

    private Context activity;

    public SharedPrefVenues(Context activity) {
        this.activity = activity;
    }

    public void setCachedVenues(String KEY, String VALUE) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(
                CACHEVENUES, 1).edit();

        editor.putString(KEY, VALUE);
        editor.commit();
    }

    public String getCachedVenues(String KEY) {
        String Value;
        cacheVenues = activity.getSharedPreferences(CACHEVENUES, 1);

        Value = cacheVenues.getString(KEY, DEFAULT_VALUE);
        return Value;
    }
}
