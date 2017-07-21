package com.netease.open.poco.sdk.exceptions;

/**
 * Created by adolli on 2017/7/13.
 */

public class UnableToSetAttributeException extends RuntimeException {
    public UnableToSetAttributeException(String attrName, Object node) {
        super(String.format("Unable to set attribute \"%s\" of node \"%s\"", attrName, node.toString()));
    }
}
