package com.bgsltd.filecommande.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bgsltd.filecommande.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by marinaracu on 27.11.2017.
 */

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        startActivity(MainActivity.class);
    }

    private void startActivity(final Class<?> activity){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, activity);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
