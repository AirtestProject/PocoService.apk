package com.netease.open.pocoservice;

import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.netease.open.libpoco.sdk.IInput;

/**
 * Created by adolli on 2017/7/13.
 */

public class Input implements IInput {
    private Context context;
    private UiAutomation ui = null;

    public Input(Context context, UiAutomation ui) {
        this.context = context;
        this.ui = ui;
    }

    @Override
    public void keyevent(int keycode) {
        // TODO： 待测试
        this.ui.injectInputEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode), true);
        this.ui.injectInputEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode), true);
    }

    @Override
    public void click(double x, double y) {
        down(x, y);
        up(x, y);
    }

    @Override
    public void longClick(double x, double y) {
        longClick(x, y, 3000);
    }

    @Override
    public void longClick(double x, double y, double duration) {
        MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, (float) x, (float) y, 0);
        down.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(down, true);

        // long click sleep 3s
        try {
            Thread.sleep((int) (duration * 1000));
        } catch (InterruptedException e) {
        }

        MotionEvent up = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, (float) x, (float) y, 0);
        up.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(up, true);

        down.recycle();
        up.recycle();
    }


    @Override
    public void swipe(double x1, double y1, double x2, double y2, double duration) {
        final int interval = 25;
        int steps = (int) (duration * 1000 / interval + 1);
        double dx = (x2 - x1) / steps;
        double dy = (y2 - y1) / steps;
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

    private int[] getPortSize() {
        WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point dimm = new Point();
        display.getRealSize(dimm);
        return new int[] {dimm.x, dimm.y};
    }

    private void down(double x, double y) {
        int[] portSize = getPortSize();
        double fx = x * portSize[0];
        double fy = y * portSize[1];
        MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, ((float)fx), ((float)fy), 0);
        evt.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(evt, true);
        evt.recycle();
    }

    private void moveTo(double x, double y) {
        int[] portSize = getPortSize();
        double fx = x * portSize[0];
        double fy = y * portSize[1];
        MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, ((float)fx), ((float)fy), 0);
        evt.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(evt, true);
        evt.recycle();
    }

    private void up(double x, double y) {
        int[] portSize = getPortSize();
        double fx = x * portSize[0];
        double fy = y * portSize[1];
        MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, ((float)fx), ((float)fy), 0);
        evt.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        this.ui.injectInputEvent(evt, true);
        evt.recycle();
    }
}
