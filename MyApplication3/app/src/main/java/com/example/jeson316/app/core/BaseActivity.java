package com.example.jeson316.app.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by jeson316 on 2017/12/8.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(initLayoutID());
    }

}
