package com.netease.open.pocoservice;

import android.app.Notification;
import android.app.UiAutomation;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashSet;

/**
 * Used to skip apk auto install && permission popups
 * Called in method: setPermissionPatterns
 * <p>
 * Created by hzsunshx on 2018/3/7.
 */

public class AccessibilityEventListener implements UiAutomation.OnAccessibilityEventListener {
    public String toastMessage;
    public long toastTime;

    private static AccessibilityEventListener instance;

    public AccessibilityEventListener() {
        AccessibilityEventListener.instance = this;
    }

    public static AccessibilityEventListener getInstance() {
        if (instance == null) {
            throw new RuntimeException(); // Must be init first.
        }
        return instance;
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (event.getPackageName() == null) {
            return;
        } else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable parcelable = event.getParcelableData();
            if (!(parcelable instanceof Notification)) { // without Notification is Toast
                String packageName = event.getPackageName().toString();
                if (event.getText().size() > 0) {
                    this.toastTime = System.currentTimeMillis();
                    this.toastMessage = "" + event.getText().get(0);
                    System.out.println("Toast:" + toastMessage + " Pkg:" + packageName + " Time:" + toastTime);
                }
            }
        }
    }
}