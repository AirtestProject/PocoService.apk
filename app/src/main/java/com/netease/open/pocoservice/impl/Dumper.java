package com.netease.open.pocoservice.impl;

import android.annotation.SuppressLint;
import android.app.UiAutomation;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.netease.open.pocoservice.sdk.IDumper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by adolli on 2017/7/10.
 */

@SuppressLint("NewApi")
public class Dumper implements IDumper<AccessibilityNodeInfo> {
    public static String TAG = "Dumper";
    private UiAutomation ui;

    public Dumper(UiAutomation ui) {
        this.ui = ui;
    }

    @Override
    public AccessibilityNodeInfo root() {
        return this.ui.getRootInActiveWindow();
    }

    @Override
    public JSONObject dumpHierarchy(AccessibilityNodeInfo node, String depthStr, int childIndex) throws JSONException {
        if (node == null) {
            // return if still null
            return null;
        }

        if (depthStr == null) {
            depthStr = "";
        }

        JSONObject payload = new JSONObject();

        CharSequence clsName = node.getClassName();
        CharSequence resId = node.getViewIdResourceName();
        CharSequence name = clsName;
        if (resId != null) {
            name = resId;
        }
        payload.put("resourceId", resId);
        payload.put("package", node.getPackageName());
        payload.put("type", node.getClassName());
        payload.put("desc", convertToUtf8(node.getContentDescription()));
        payload.put("text", convertToUtf8(node.getText()));
        payload.put("enabled", node.isEnabled());
        payload.put("visible", node.isVisibleToUser());
        payload.put("checkable", node.isCheckable());
        payload.put("checked", node.isChecked());
        payload.put("focusable", node.isFocusable());
        payload.put("focused", node.isFocused());
        payload.put("editalbe", node.isEditable());
        payload.put("selected", node.isSelected());
        payload.put("longClickable", node.isLongClickable());

        Rect bound = new Rect();
        node.getBoundsInScreen(bound);

        // size
        JSONArray size = new JSONArray();
        size.put(bound.width());
        size.put(bound.height());
        payload.put("size", size);

        // position
        JSONArray pos = new JSONArray();
        pos.put(bound.centerX());
        pos.put(bound.centerY());
        payload.put("pos", pos);

        // anchor
        JSONArray anchor = new JSONArray();
        anchor.put(0.5);
        anchor.put(0.5);
        payload.put("anchorPoint", anchor);

        // scale
        JSONArray scale = new JSONArray();
        scale.put(1);
        scale.put(1);
        payload.put("scale", scale);

        // zOrders
        JSONObject zOrders = new JSONObject();
        int localOrder = 0;
        try {
            localOrder = node.getDrawingOrder();
        } catch (NoSuchMethodError e) { }
        zOrders.put("global", 0);
        zOrders.put("local", localOrder);
        payload.put("zOrders", zOrders);

        String thisDepthStr = String.format("%s|%02x-%02x-%02x", depthStr, zOrders.getInt("global"), zOrders.getInt("local"), childIndex);
        payload.put("depthStr", thisDepthStr);

        JSONObject result = new JSONObject();

        JSONArray children = new JSONArray();
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                children.put(dumpHierarchy(child, thisDepthStr, i));
            }
            result.put("children", children);
        }

        result.put("name", name);
        result.put("payload", payload);

        return result;
    }

    private static String convertToUtf8(CharSequence s) {
        if (s == null) {
            return null;
        }
        return new String(s.toString().getBytes(), Charset.forName("utf-8"));
    }
}
