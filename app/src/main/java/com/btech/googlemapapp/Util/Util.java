package com.btech.googlemapapp.Util;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.btech.googlemapapp.R;

/**
 * Created by danielromero on 23/5/17.
 */

public class Util {

    public static void saveOnSharedPreferences(String key, String value, Context context){
        SharedPreferences preferencesAccount = context.getSharedPreferences("PreferencesAccount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesAccount.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getFromSharedPreferences(String key, Context context){
        SharedPreferences preferencesAccount = context.getSharedPreferences("PreferencesAccount", Context.MODE_PRIVATE);
        String value = preferencesAccount.getString(key, null);
        return value;
    }

}
