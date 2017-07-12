package com.netease.open.pocoservice.sdk;

/**
 * Created by adolli on 2017/7/12.
 */

public interface IAttributor<NodeType> {
    Object getAttr(NodeType node, String attrName);

    void setAttr(NodeType node, String attrName, Object value);
}
