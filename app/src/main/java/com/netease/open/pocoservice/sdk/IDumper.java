package com.netease.open.pocoservice.sdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adolli on 2017/7/12.
 */

public interface IDumper <NodeType> {

    NodeType root();

    JSONObject dumpHierarchy(NodeType root, String depthStr, int childIndex) throws JSONException;
}
