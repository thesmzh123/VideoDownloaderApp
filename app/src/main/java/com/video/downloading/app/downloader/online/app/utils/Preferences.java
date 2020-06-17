package com.video.downloading.app.downloader.online.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String MY_PREF = "MyPreferences";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Preferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(MY_PREF,0);
        this.editor = this.sharedPreferences.edit();
    }

    public void set(String key,String value) {
        this.editor.putString(key,value);
        this.editor.apply();
    }
  public void setLong(String key, long value) {
        this.editor.putLong(key,value);
        this.editor.apply();
    }


    public String get(String key) {
        return this.sharedPreferences.getString(key,"");
    }

    public long getLong(String key) {
        return this.sharedPreferences.getLong(key,0);
    }

    public void clear(String key) {
        this.editor.remove(key);
        this.editor.apply();
    }


    public void clear() {
        this.editor.clear();
        this.editor.apply();
    }
}