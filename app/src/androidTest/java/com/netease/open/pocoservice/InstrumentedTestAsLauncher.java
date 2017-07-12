package com.netease.open.pocoservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.netease.open.pocoservice.impl.Dumper;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import fi.iki.elonen.NanoHTTPD;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTestAsLauncher {
    private static final String TAG = "InstrumentedTest";

    @Test
    public void launch() throws Exception {
        Instrumentation ins = InstrumentationRegistry.getInstrumentation();
        UiAutomation ui = ins.getUiAutomation();

        new App(InstrumentationRegistry.getTargetContext(), ui);

        RpcServer rpc = new RpcServer("0.0.0.0", 10081);
        rpc.export("rpc-object-test", 123);
        rpc.export("uiautomation", ui);
        rpc.export("dumper", new Dumper(ui));

        class test {
            public String val = "123";
            public int ival = 233;
            public String[] array1 = new String[10];
            public LinkedList<String> array2 = new LinkedList<>();
            public HashMap<String, String> array3 = new HashMap<>();
            public test() {
                array2.add("123312231");
            }
        }

        rpc.export("test", new test());

        Thread.sleep(24 * 60 * 60 * 1000);  // 1 天
    }

    public class App extends NanoHTTPD {
        UiAutomation ui;
        Dumper dumper;
        Context context;

        public App(Context context, UiAutomation ui) throws IOException {
            super("0.0.0.0", 10080);  // 将端口forward出来访问
            Log.i(TAG, "server listening on 127.0.0.1:10080");
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
            Log.i(TAG, "server started");

            this.context = context;
            this.ui = ui;
            this.dumper = new Dumper(this.ui);

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

            switch (path) {
                case "/hierarchy":
                    try {
                        ret = this.dumper.dumpHierarchy(this.ui.getRootInActiveWindow(), null, 0).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/screen_size":
                    WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    Point dimm = new Point();
                    display.getRealSize(dimm);

                    JSONArray size = new JSONArray();
                    size.put(dimm.x);
                    size.put(dimm.y);
                    ret = size.toString();
                    break;
                case "/screen":
                    String paramWidth = session.getParms().get("width");
                    int width = 720;
                    if (paramWidth != null) {
                        width = Integer.parseInt(paramWidth);
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap screen = this.ui.takeScreenshot();
                    int height = width * screen.getHeight() / screen.getWidth();
                    screen = Bitmap.createScaledBitmap(screen, width, height, true);
                    screen.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    ret = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                    break;
            }

            return newFixedLengthResponse(ret);
        }
    }
}
