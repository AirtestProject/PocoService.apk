package com.netease.open.pocoservice.impl;

import android.view.accessibility.AccessibilityNodeInfo;

import com.netease.open.pocoservice.sdk.IAttributor;

/**
 * Created by adolli on 2017/7/12.
 */

public class Attributor implements IAttributor<AccessibilityNodeInfo> {
    @Override
    public Object getAttr(AccessibilityNodeInfo node, String attrName) {
        return null;
    }

    @Override
    public void setAttr(AccessibilityNodeInfo node, String attrName, Object value) {

    }
}
