package com.btech.googlemapapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        goToMain(true);
    }

    public void goToMain(boolean withDelay){
        final Intent mainIntent;
        mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
            }
        }, withDelay?3000:500);
    }

}
