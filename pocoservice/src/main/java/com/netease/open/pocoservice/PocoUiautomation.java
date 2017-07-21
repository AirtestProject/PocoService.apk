package com.netease.open.pocoservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

import com.netease.open.poco.sdk.IPocoUiautomation;
import com.netease.open.poco.sdk.IScreen;
import com.netease.open.poco.sdk.AbstractNode;
import com.netease.open.poco.sdk.simple.Attributor;
import com.netease.open.poco.sdk.simple.IDumper;
import com.netease.open.poco.sdk.IInputer;
import com.netease.open.poco.sdk.simple.ISelector;
import com.netease.open.poco.sdk.simple.Selector;

/**
 * Created by adolli on 2017/7/13.
 */

public class PocoUiautomation implements IPocoUiautomation {
    private Context context = null;
    private UiAutomation ui = null;

    public IDumper<AbstractNode> dumper = null;
    public ISelector<AbstractNode> selector = null;
    public Attributor attributor = null;
    public IInputer inputer = null;
    public IScreen screen = null;

    public PocoUiautomation(Context context, UiAutomation ui) {
        this.context = context;
        this.ui = ui;

        this.dumper = new Dumper(this.context, this.ui);
        this.selector = new Selector(this.dumper);
        this.attributor = new Attributor();
        this.inputer = new Inputer(this.context, this.ui);
        this.screen = new Screen(this.context, this.ui);

        AccessibilityServiceInfo accessibilityServiceInfo = this.ui.getServiceInfo();
        // 监听的事件类型
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        // 反馈方式
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_HAPTIC;
        accessibilityServiceInfo.notificationTimeout = 100;
        accessibilityServiceInfo.flags |=
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        this.ui.setServiceInfo(accessibilityServiceInfo);
    }
}
