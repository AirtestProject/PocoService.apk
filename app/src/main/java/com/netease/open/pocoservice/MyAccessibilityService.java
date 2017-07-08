package com.netease.open.pocoservice;

import java.io.IOException;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import fi.iki.elonen.NanoHTTPD;


@SuppressLint("NewApi")
public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "PocoHierarchyService";

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        // 监听的事件类型
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        // 反馈方式
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_HAPTIC;
        accessibilityServiceInfo.notificationTimeout = 100;
        accessibilityServiceInfo.flags |=
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        setServiceInfo(accessibilityServiceInfo);

        try {
            new App();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.d(TAG, "hahahaha");
//        try {
//            JSONObject hierarchy = dumpHierarchy(null);
//            viewHierarchy = hierarchy;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private String convertToUtf8(CharSequence s) {
        if (s == null) {
            return null;
        }
        return new String(s.toString().getBytes(), Charset.forName("utf-8"));
    }

    protected JSONObject dumpHierarchy(AccessibilityNodeInfo node, String depthStr, int childIndex) throws JSONException {
        if (node == null) {
            node = getRootInActiveWindow();
        }
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

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return true;
    }

    @Override
    public void onInterrupt() {
    }


    public class App extends NanoHTTPD {
        public App() throws IOException {
            super("0.0.0.0", 10080);
            Log.i(TAG, "server listening on 127.0.0.1:10080");
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
            Log.i(TAG, "server started");
        }

        @Override
        public Response serve(IHTTPSession session) {
            String path = session.getUri();
            String ret = "- empty -";

            switch (path) {
                case "/hierarchy":
                    try {
                        ret = MyAccessibilityService.this.dumpHierarchy(null, null, 0).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/screen_size":
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    Point dimm = new Point();
                    display.getRealSize(dimm);

                    JSONArray size = new JSONArray();
                    size.put(dimm.x);
                    size.put(dimm.y);
                    ret = size.toString();
                    break;
                case "/screen":
                    break;
            }

            return newFixedLengthResponse(ret);
        }
    }
}