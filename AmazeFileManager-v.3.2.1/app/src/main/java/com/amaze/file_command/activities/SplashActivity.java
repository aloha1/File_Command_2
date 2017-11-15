package com.amaze.file_command.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.amaze.file_command.R;
import com.amaze.file_command.base.BaseActivity;


public class SplashActivity extends Activity {

    private ImageView imgSplash;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private SharedPreferences sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imgSplash = (ImageView)findViewById(R.id.img_splash);
        sharedPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}