package com.example.edcan3vacationproject;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

public class UserCache {
    private static SharedPreferences getShared(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static void setUser(Context context, UserModel model){
        Gson gson = new Gson();
        String json = gson.toJson(model);
        SharedPreferences.Editor editor = getShared(context).edit();
        editor.putString("user_data", json).apply();
    }
    public static UserModel getUser(Context context){
        Gson gson = new Gson();
        return gson.fromJson(getShared(context).getString("user_data", ""), UserModel.class);
    }
    public static void  clear(Context context){
        SharedPreferences.Editor editor = getShared(context).edit();
        editor.putString("user_data", null).apply();
    }
}
