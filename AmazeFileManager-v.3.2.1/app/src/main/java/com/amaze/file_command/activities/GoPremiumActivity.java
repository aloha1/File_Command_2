package com.amaze.file_command.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.amaze.file_command.R;

/**
 * Created by Yunwen on 11/6/2017.
 */

public class GoPremiumActivity extends Activity {
    private String TAG = "GoPremiumActivity";
    private Toolbar toolbar;
    private TextView toolbarTitle, textTitle, textContent;
    private ImageView imageEnglish, imageArabic,imageGerman,imageSpanish,imageFrench,imageChinese, imageJapanese;
    private Configuration config;
    private Resources resources;
    private DisplayMetrics dm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_premium);
        initView();
    }

    protected void initView(){

    }

}
