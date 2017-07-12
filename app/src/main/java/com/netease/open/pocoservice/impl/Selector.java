package com.netease.open.pocoservice.impl;

import android.view.accessibility.AccessibilityNodeInfo;

import com.netease.open.pocoservice.impl.Dumper;
import com.netease.open.pocoservice.sdk.ISelector;

import org.json.JSONArray;

/**
 * Created by adolli on 2017/7/12.
 */

public class Selector implements ISelector<AccessibilityNodeInfo> {
    private Dumper dumper;

    public Selector(Dumper dumper) {
        this.dumper = dumper;
    }

    @Override
    public AccessibilityNodeInfo[] select(JSONArray cond, boolean multiple) {
        return this.selectImpl(cond, multiple, this.root(), null, 9999, true, true);
    }

    private AccessibilityNodeInfo[] selectImpl(JSONArray cond, boolean multiple, AccessibilityNodeInfo root, Object matcher, int maxDepth, boolean onlyVisibleNode, boolean includeRoot) {
        return new AccessibilityNodeInfo[0];
    }

    protected AccessibilityNodeInfo root() {
        return this.dumper.root();
    }
}
