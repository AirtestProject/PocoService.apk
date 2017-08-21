package com.netease.open.pocoservice;

import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import com.netease.open.libpoco.sdk.IScreen;

import java.io.ByteArrayOutputStream;

/**
 * Created by adolli on 2017/7/19.
 */

public class Screen implements IScreen {
    private Context context;
    private UiAutomation ui;

    public Screen(Context context, UiAutomation ui) {
        this.context = context;
        this.ui = ui;
    }

    @Override
    public int[] getPortSize() {
        WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point dimm = new Point();
        display.getRealSize(dimm);
        return new int[] {dimm.x, dimm.y};
    }

    @Override
    public Object getScreen(int width) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap screen = this.ui.takeScreenshot();
        int height = width * screen.getHeight() / screen.getWidth();
        screen = Bitmap.createScaledBitmap(screen, width, height, true);
        screen.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        String ret = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        return ret;
    }
}
