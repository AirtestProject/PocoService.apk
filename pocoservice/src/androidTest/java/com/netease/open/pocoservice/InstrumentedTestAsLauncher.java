package com.netease.open.pocoservice;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.netease.open.hrpc.backend.RpcServer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.LinkedList;

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
        UiAutomationConnection uiConn = new UiAutomationConnection(ins);
        Context context = InstrumentationRegistry.getTargetContext();

        new ServerForHierarchyViewer(InstrumentationRegistry.getTargetContext(), uiConn);

        RpcServer rpc = new RpcServer("0.0.0.0", 10081);
        rpc.export("poco-uiautomation-framework", new PocoUiautomation(context, uiConn));

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

}
