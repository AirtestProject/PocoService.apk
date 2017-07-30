package com.netease.open.pocoservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.UiAutomation;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.netease.open.poco.sdk.AbstractNode;
import com.netease.open.poco.sdk.IScreen;
import com.netease.open.poco.sdk.simple.IDumper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by adolli on 2017/7/19.
 */

@SuppressLint("NewApi")
public class ServerForHierarchyViewer extends NanoHTTPD {
    private static final String TAG = ServerForHierarchyViewer.class.getName();

    private Context context;
    private UiAutomation ui;

    private IDumper<AbstractNode> dumper;
    private IScreen screen;


    public ServerForHierarchyViewer(Context context, UiAutomation ui) throws IOException {
        super("0.0.0.0", 10080);  // 将端口forward出来访问
        Log.i(TAG, "server listening on 127.0.0.1:10080");
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
        Log.i(TAG, "server started");

        this.context = context;
        this.ui = ui;

        this.dumper = new Dumper(this.context, this.ui);
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

    @Override
    public Response serve(IHTTPSession session) {
        String path = session.getUri();
        String ret = "- empty -";
        String mimeType = NanoHTTPD.MIME_PLAINTEXT;

        switch (path) {
            case "/hierarchy":
                try {
                    ret = this.dumper.dumpHierarchy().toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mimeType = "application/json";
                break;
            case "/screen_size":
                int[] size = this.screen.getPortSize();
                JSONArray jsize = new JSONArray();
                jsize.put(size[0]);
                jsize.put(size[1]);
                ret = jsize.toString();
                mimeType = "application/json";
                break;
            case "/screen":
                String paramWidth = session.getParms().get("width");
                int width = 720;
                if (paramWidth != null) {
                    width = Integer.parseInt(paramWidth);
                }
                ret = (String) this.screen.getScreen(width);
                break;
            case "/hierarchy_size":
                int[] size2 = this.dumper.getPortSize();
                JSONArray jsize2 = new JSONArray();
                jsize2.put(size2[0]);
                jsize2.put(size2[1]);
                ret = jsize2.toString();
                mimeType = "application/json";
                break;
        }
        return newFixedLengthResponse(Response.Status.OK, mimeType, ret);
    }
}
