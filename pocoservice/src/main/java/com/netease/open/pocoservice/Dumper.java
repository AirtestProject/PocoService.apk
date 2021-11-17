package com.netease.open.pocoservice;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.netease.open.libpoco.RootNode;
import com.netease.open.libpoco.sdk.AbstractNode;
import com.netease.open.libpoco.sdk.AbstractDumper;

/**
 * Created by adolli on 2017/7/19.
 */

public class Dumper extends AbstractDumper {
    private Context context;
    private UiAutomationConnection ui;

    public Dumper(Context context, UiAutomationConnection ui) {
        super();
        this.context = context;
        this.ui = ui;
    }

    @Override
    public AbstractNode getRoot() {
        int[] portSize = getPortSize();
        return new RootNode(portSize[0], portSize[1]);
    }

    private int[] getPortSize() {
        WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point dimm = new Point();
        display.getRealSize(dimm);
        return new int[] {dimm.x, dimm.y};
    }
}
