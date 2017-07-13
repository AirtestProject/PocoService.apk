package com.netease.open.poco.impl;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.netease.open.poco.sdk.IAttributor;
import com.netease.open.poco.sdk.IDumper;
import com.netease.open.poco.sdk.IInputer;
import com.netease.open.poco.sdk.ISelector;

/**
 * Created by adolli on 2017/7/13.
 */

public class PocoUiautomation {
    private Context context = null;
    private UiAutomation ui = null;
    public IDumper<AccessibilityNodeInfo> dumper = null;
    public ISelector<AccessibilityNodeInfo> selector = null;
    public IAttributor<AccessibilityNodeInfo> attributor = null;
    public IInputer inputer = null;

    public PocoUiautomation(Context context, UiAutomation ui) {
        this.context = context;
        this.ui = ui;
        this.dumper = new Dumper(ui);
        this.attributor = new Attributor();
        this.selector = new Selector(this.dumper, this.attributor);
        this.inputer = new Inputer(ui);
    }

    public int[] get_screen_size() {
        int[] ret = new int[2];
        WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point dimm = new Point();
        display.getRealSize(dimm);
        return new int[] {dimm.x, dimm.y};
    }
}
