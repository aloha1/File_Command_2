package com.bgsltd.file_command.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jili on 7/25/17.
 * this class use for common methods of activity
 */

public abstract class BaseActivity extends Activity {
    //load layout , init UI , and Ui event
    protected abstract void initView(Bundle savedInstanceState);

    // init variables , include intent data and activity internal data;
    protected abstract void initData();

    // CALL APIs
    protected abstract void loadData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("BaseActivity",getClass().getSimpleName());

        initView(savedInstanceState);

        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
