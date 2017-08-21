package com.netease.open.libpoco.sdk.simple;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adolli on 2017/7/12.
 */

public interface IDumper <NodeType> {

    NodeType getRoot();

    JSONObject dumpHierarchy() throws JSONException;

    /**
     * 以渲染树坐标系为准的视口尺寸
     *
     * @return [width, height] in pixels
     */
    int[] getPortSize();
}
