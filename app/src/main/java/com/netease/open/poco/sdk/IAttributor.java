package com.netease.open.poco.sdk;

/**
 * Created by adolli on 2017/7/12.
 */

public interface IAttributor<NodeType> {
    Object getAttr(NodeType[] nodes, String attrName) throws NoSuchAttributeException;
    Object getAttr(NodeType node, String attrName) throws NoSuchAttributeException;

    void setAttr(NodeType[] nodes, String attrName, Object value) throws UnableToSetAttributeException;
    void setAttr(NodeType node, String attrName, Object value) throws UnableToSetAttributeException;
}
