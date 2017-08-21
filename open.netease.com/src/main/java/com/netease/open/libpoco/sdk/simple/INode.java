package com.netease.open.libpoco.sdk.simple;

import com.netease.open.libpoco.sdk.exceptions.NoSuchAttributeException;
import com.netease.open.libpoco.sdk.exceptions.NodeHasBeenRemovedException;
import com.netease.open.libpoco.sdk.exceptions.UnableToSetAttributeException;

/**
 * Created by adolli on 2017/7/19.
 */

public interface INode {
    Object getAttr(String attrName) throws NoSuchAttributeException, NodeHasBeenRemovedException;
    void setAttr(String attrName, Object value) throws UnableToSetAttributeException, NodeHasBeenRemovedException;
}
