package com.netease.open.poco.sdk;

import android.annotation.SuppressLint;

import com.netease.open.poco.sdk.simple.IDumper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by adolli on 2017/7/10.
 */

@SuppressLint("NewApi")
public abstract class AbstractDumper implements IDumper<AbstractNode> {
    public static String TAG = "AbstractDumper";

    public JSONObject dumpHierarchy() throws JSONException {
        return this.dumpHierarchyImpl(this.getRoot(), null, 0);
    }

    public JSONObject dumpHierarchyImpl(AbstractNode node, String depthStr, int childIndex) throws JSONException {
        if (node == null) {
            // return if still null
            return null;
        }

        if (depthStr == null) {
            depthStr = "";
        }

        JSONObject payload = new JSONObject();
        for (Map.Entry<String, Object> attr : node.enumerateAttrs().entrySet()) {
            payload.put(attr.getKey(), attr.getValue());
        }

        // depthstr 准备移除
        JSONObject zOrders = (JSONObject) node.getAttr("zOrders");
        String thisDepthStr = String.format("%s|%02x-%02x-%02x", depthStr, zOrders.getInt("global"), zOrders.getInt("local"), childIndex);
        payload.put("depthStr", thisDepthStr);

        JSONObject result = new JSONObject();

        JSONArray children = new JSONArray();
        int i = 0;
        for (AbstractNode child : node.getChildren()) {
            if ((boolean) child.getAttr("visible")) {
                children.put(this.dumpHierarchyImpl(child, thisDepthStr, i));
                i++;
            }
        }
        if (children.length() > 0) {
            result.put("children", children);
        }

        result.put("name", node.getAttr("name"));
        result.put("payload", payload);

        return result;
    }

}
