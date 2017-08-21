package com.netease.open.libpoco.sdk;

import com.netease.open.libpoco.sdk.simple.INode;

import java.util.Map;

/**
 * Created by adolli on 2017/7/19.
 */

public abstract class AbstractNode implements INode {
    // 必须实现的基础属性，用于dump
    public static String[] RequiredAttributes = new String[] {
            "name",
            "type",
            "visible",
            "pos",
            "size",
            "scale",
            "anchorPoint",
            "zOrders",
    };

    // tree node interface
    public abstract AbstractNode getParent();
    public abstract Iterable<AbstractNode> getChildren();

    // node interface
    public abstract void setAttr(String attrName, Object attrVal);
    public abstract Object getAttr(String attrName);

    // method for dumper only
    public abstract Map<String, Object> enumerateAttrs();
}
