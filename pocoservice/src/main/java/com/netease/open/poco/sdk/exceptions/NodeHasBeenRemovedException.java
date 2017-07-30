package com.netease.open.poco.sdk.exceptions;

/**
 * Created by adolli on 2017/7/30.
 */

public class NodeHasBeenRemovedException extends RuntimeException {
    public NodeHasBeenRemovedException(String attrName, Object node) {
        super("Node was no longer alive. Please re-select.");
    }
}
