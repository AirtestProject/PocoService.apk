package com.netease.open.poco.sdk;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by adolli on 2017/7/13.
 */

public interface IMatcher<NodeType> {
    boolean match(JSONArray cond, NodeType node) throws JSONException;
}
