package com.netease.open.poco.sdk.simple;

import com.netease.open.poco.sdk.AbstractNode;

/**
 * Created by adolli on 2017/7/19.
 */

public class Attributor {
    public Attributor() {}

    public Object getAttr(AbstractNode[] nodes, String attrName) {
        return getAttr(nodes[0], attrName);
    }

    public Object getAttr(AbstractNode node, String attrName) {
        return node.getAttr(attrName);
    }

    public void setAttr(AbstractNode[] nodes, String attrName, Object attrVal) {
        setAttr(nodes[0], attrName, attrVal);
    }

    public void setAttr(AbstractNode node, String attrName, Object attrVal) {
        node.setAttr(attrName, attrVal);
    }
}
