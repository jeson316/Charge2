package com.example.jeson316.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.example.jeson316.app.core.BaseActivity;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity {

    @Override
    public int initLayoutID() {
        return R.layout.activity_main;
    }
}
