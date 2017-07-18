package com.netease.open.poco.impl;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.netease.open.poco.sdk.IInputer;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Created by adolli on 2017/7/13.
 */

public class Inputer implements IInputer {
    private UiAutomation ui = null;

    public Inputer(UiAutomation ui) {
        this.ui = ui;
    }

    @Override
    public void keyevent(int keycode) {
        // TODO： 待测试
        this.ui.injectInputEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode), true);
        this.ui.injectInputEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode), true);
    }

    @Override
    public void click(int x, int y) {
        long downTime = SystemClock.uptimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        MotionEvent up = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        down.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        up.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(down, true);
        this.ui.injectInputEvent(up, true);
        down.recycle();
        up.recycle();
    }

    @Override
    public void longClick(int x, int y) {
        MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        down.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(down, true);

        // 长 click sleep 3s
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        MotionEvent up = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        up.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(up, true);

        down.recycle();
        up.recycle();
    }

    @Override
    public void swipe(int x1, int y1, int x2, int y2, int durationInMillis) {
        final int interval = 25;
        int steps = durationInMillis / interval + 1;
        float dx = (x2 - x1) / steps;
        float dy = (y2 - y1) / steps;
        down(x1, y1);
        for (int step = 0; step < steps; step++) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
            }
            moveTo((int)(x1 + step * dx), (int)(y1 + step * dy));
        }
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
        }
        up(x2, y2);
    }

    private void down(int x, int y) {
        MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        evt.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(evt, true);
        evt.recycle();
    }

    private void moveTo(int x, int y) {
        MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
        evt.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(evt, true);
        evt.recycle();
    }

    private void up(int x, int y) {
        MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        evt.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(evt, true);
        evt.recycle();
    }
}
