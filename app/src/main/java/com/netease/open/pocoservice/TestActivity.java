package com.netease.open.pocoservice;

import android.app.UiAutomation;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//
//        UiDevice device = UiDevice.getInstance();
//        OutputStream out = new ByteArrayOutputStream();
//        try {
//            device.dumpWindowHierarchy(out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.print(out.toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("PocoService", "hahahahahah");
    }
}
