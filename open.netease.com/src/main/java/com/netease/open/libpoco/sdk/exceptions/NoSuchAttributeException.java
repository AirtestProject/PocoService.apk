package com.netease.open.libpoco.sdk.exceptions;

/**
 * Created by adolli on 2017/7/13.
 */

public class NoSuchAttributeException extends RuntimeException {
    public NoSuchAttributeException(String attrName, Object node) {
        super(String.format("cannot evalute attribute \"%s\" of node \"%s\".", attrName, node.toString()));
    }
}
