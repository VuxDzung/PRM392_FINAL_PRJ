package com.example.prm392_final_prj.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "online_tour_booking_app_session";
    private static final String KEY_TOKEN = "jwt_token";

    private static final String KEY_ROLE = "key_role";

    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void saveRole(String role) {
        sharedPreferences.edit().putString(KEY_ROLE, role).apply();
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }




}
