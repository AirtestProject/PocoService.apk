package com.netease.open.libpoco;

import android.annotation.SuppressLint;
import android.app.UiAutomation;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.test.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.netease.open.libpoco.sdk.AbstractNode;
import com.netease.open.libpoco.Node;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@SuppressLint("NewApi")
public class RootNode extends AbstractNode {
    /*
    因为安卓界面可能不止一层，曾经的默认方法只会拿到主界面的根节点，导致一些其他层级的view会获取不到节点
    例如一些输入法界面（讯飞输入法）、系统的一些虚拟按键等，都是另外一个层面上的内容，不在主界面上
    目前的修改：
    新增RootNode这一节点，用来表示一个虚构的根节点，它的getChildren会返回当前界面上所有根节点的列表
    相当于比之前的返回内容额外多了一个root层级，root节点的属性全是虚构的默认值
    可能会降低搜索速度，因为需要搜的内容可能更多了
    */

    public UiDevice device;
    protected int screenWidth_ = 0;
    protected int screenHeight_ = 0;

    public RootNode(int screenW, int screenH) {
        super();
        this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        this.screenWidth_ = screenW;
        this.screenHeight_ = screenH;

    }


    @Override
    public Iterable<AbstractNode> getChildren() {
        return Arrays.asList(this.getWindowRoots());
    }

    @Override
    public Object getAttr(String attrName) {
        Map<String, Object> defaultAttrs = new HashMap<>();
        defaultAttrs.put("name", "<Root>");
        defaultAttrs.put("type", "Root");
        defaultAttrs.put("visible", true);
        try {
            float[] defaultArr = new float[] {0, 0};
            defaultAttrs.put("pos", new JSONArray(defaultArr));
            defaultAttrs.put("size", new JSONArray(defaultArr));
            defaultAttrs.put("scale", new JSONArray(defaultArr));
            defaultAttrs.put("anchorPoint", new JSONArray(new float[] {0.5f, 0.5f}));
            JSONObject zOrders = new JSONObject();
            zOrders.put("global", 0);
            zOrders.put("local", 0);
            defaultAttrs.put("zOrders", zOrders);
        } catch (JSONException e) {}

        if (defaultAttrs.containsKey(attrName)) {
            return defaultAttrs.get(attrName);
        } else {
            return null;
        }
    }

    private AbstractNode[] getWindowRoots() {
        this.device.waitForIdle();
        Set<AccessibilityNodeInfo> roots = new HashSet();
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        AccessibilityNodeInfo activeRoot = uiAutomation.getRootInActiveWindow();
        if (activeRoot != null) {
            roots.add(activeRoot);
        }

        Iterator i$ = uiAutomation.getWindows().iterator();

        while (i$.hasNext()) {
            AccessibilityWindowInfo window = (AccessibilityWindowInfo) i$.next();
            AccessibilityNodeInfo root = window.getRoot();
            if (root == null) {
                Log.w("getWindowRoots", String.format("Skipping null root node for window: %s", window.toString()));
            } else {
                roots.add(root);
            }
        }
        int i = 0;
        AbstractNode[] ret = new AbstractNode[roots.size()];
        for (AccessibilityNodeInfo n : roots) {
            ret[i] = new Node(n, this.screenWidth_, this.screenHeight_);
            i++;
        }
        return ret;
    }
}
