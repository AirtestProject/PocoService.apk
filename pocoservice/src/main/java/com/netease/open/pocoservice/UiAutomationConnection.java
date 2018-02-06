package com.netease.open.pocoservice;

import android.app.Instrumentation;
import android.app.UiAutomation;

/**
 * Created by adolli on 2018/2/6.
 */

public class UiAutomationConnection implements IUiAutomationConnection {
    private Instrumentation instrumentation;

    public UiAutomationConnection(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public UiAutomation get() {
        return instrumentation.getUiAutomation();
    }
}
