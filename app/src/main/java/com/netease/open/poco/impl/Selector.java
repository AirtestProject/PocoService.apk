package com.netease.open.poco.impl;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;
import com.netease.open.poco.sdk.DefaultMatcher;
import com.netease.open.poco.sdk.IAttributor;
import com.netease.open.poco.sdk.IDumper;
import com.netease.open.poco.sdk.IMatcher;
import com.netease.open.poco.sdk.ISelector;
import com.netease.open.pocoservice.RpcServer;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by adolli on 2017/7/12.
 */

public class Selector implements ISelector<AccessibilityNodeInfo> {
    private IDumper<AccessibilityNodeInfo> dumper;
    private IAttributor<AccessibilityNodeInfo> attributor;
    private IMatcher<AccessibilityNodeInfo> matcher;

    public Selector(IDumper<AccessibilityNodeInfo> dumper, IAttributor<AccessibilityNodeInfo> attributor) {
        this.dumper = dumper;
        this.attributor = attributor;
        this.matcher = new DefaultMatcher<>(this.attributor);
    }

    @Override
    public AccessibilityNodeInfo[] select(JSONArray cond) throws JSONException {
        return this.select(cond, true);
    }

    @Override
    public AccessibilityNodeInfo[] select(JSONArray cond, boolean multiple) throws JSONException {
        List<AccessibilityNodeInfo> result = this.selectImpl(cond, multiple, this.root(), this.matcher, 9999, true, true);
        return makeArray(result);
    }

    private List<AccessibilityNodeInfo> selectImpl(JSONArray cond, boolean multiple, AccessibilityNodeInfo root, IMatcher<AccessibilityNodeInfo> matcher, int maxDepth, boolean onlyVisibleNode, boolean includeRoot) throws JSONException {
        List<AccessibilityNodeInfo> result = new LinkedList<>();
        String op = cond.getString(0);
        JSONArray args = cond.getJSONArray(1);

        if (op.equals(">") || op.equals("/")) {
            // 相对选择
            List<AccessibilityNodeInfo> parents = new LinkedList<>();
            parents.add(root);
            for (int i = 0; i < args.length(); i++) {
                JSONArray arg = args.getJSONArray(i);
                List<AccessibilityNodeInfo> midResult = new LinkedList<>();
                for (AccessibilityNodeInfo parent : parents) {
                    int _maxDepth = maxDepth;
                    if (op.equals("/") && i != 0) {
                        _maxDepth = 1;
                    }
                    midResult.addAll(this.selectImpl(arg, true, parent, matcher, _maxDepth, onlyVisibleNode, false));
                }
                parents = midResult;
            }
            result = parents;
        } else if (op.equals("-")) {
            // 兄弟节点选择
            JSONArray query1 = args.getJSONArray(0);
            JSONArray query2 = args.getJSONArray(1);
            List<AccessibilityNodeInfo> result1 = this.selectImpl(query1, multiple, root, matcher, maxDepth, onlyVisibleNode, includeRoot);
            for (AccessibilityNodeInfo n : result1) {
                result.addAll(this.selectImpl(query2, multiple, n.getParent(), matcher, 1, onlyVisibleNode, includeRoot));
            }
        } else if (op.equals("index")) {
            JSONArray cond1 = args.getJSONArray(0);
            int i = args.getInt(1);
            result = new LinkedList<>();
            result.add(this.selectImpl(cond1, multiple, root, matcher, maxDepth, onlyVisibleNode, includeRoot).get(i));
        } else {
            this.selectTraverse(cond, root, matcher, result, multiple, maxDepth, onlyVisibleNode, includeRoot);
        }

        return result;
    }

    private boolean selectTraverse(JSONArray cond, AccessibilityNodeInfo node, IMatcher<AccessibilityNodeInfo> matcher, List<AccessibilityNodeInfo> outResult, boolean multiple, int maxDepth, boolean onlyVisibleNode, boolean includeRoot) throws JSONException {
        // 剪掉不可见节点branch
        if (onlyVisibleNode && !node.isVisibleToUser()) {
            return false;
        }

        if (matcher.match(cond, node)) {
            if (includeRoot) {
                outResult.add(node);
                if (!multiple) {
                    return true;
                }
            }
        }

        // 最大搜索深度耗尽并不表示遍历结束，其余child节点仍需遍历
        if (maxDepth == 0) {
            return false;
        }
        maxDepth -= 1;

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            boolean finished = this.selectTraverse(cond, child, matcher, outResult, multiple, maxDepth, onlyVisibleNode, true);
            if (finished) {
                return true;
            }
        }

        return false;
    }

    public AccessibilityNodeInfo root() {
        return this.dumper.root();
    }

    @Override
    public AccessibilityNodeInfo[] make_selection(AccessibilityNodeInfo n) {
        AccessibilityNodeInfo[] ret = new AccessibilityNodeInfo[1];
        ret[0] = n;
        return ret;
    }

    private static AccessibilityNodeInfo[] makeArray(List<AccessibilityNodeInfo> list) {
        int i = 0;
        AccessibilityNodeInfo[] ret = new AccessibilityNodeInfo[list.size()];
        for (AccessibilityNodeInfo n : list) {
            ret[i] = n;
            i++;
        }
        return ret;
    }
}
