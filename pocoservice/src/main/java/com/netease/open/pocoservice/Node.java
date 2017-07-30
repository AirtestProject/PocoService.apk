package com.netease.open.pocoservice;

import android.annotation.SuppressLint;
import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.netease.open.poco.sdk.AbstractNode;
import com.netease.open.poco.sdk.exceptions.NoSuchAttributeException;
import com.netease.open.poco.sdk.exceptions.UnableToSetAttributeException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by adolli on 2017/7/19.
 */

@SuppressLint("NewApi")
public class Node extends AbstractNode {
    public static String[] SecondaryAttributes = new String[] {
            "resourceId",
            "package",
            "desc",
            "text",
            "enabled",
            "checkable",
            "checked",
            "focusable",
            "focused",
            "editalbe",
            "selected",
            "touchable",
            "longClickable",
            "boundsInParent",
    };

    private Context context;
    private AccessibilityNodeInfo node;
    private int screenWidth_ = 0;
    private int screenHeight_ = 0;

    public Node(Context context, AccessibilityNodeInfo node, int screenW, int screenH) {
        super();
        this.context = context;
        this.node = node;
        this.screenWidth_ = screenW;
        this.screenHeight_ = screenH;
    }

    @Override
    public AbstractNode getParent() {
        return new Node(this.context, this.node.getParent(), this.screenWidth_, this.screenHeight_);
    }

    @Override
    public Iterable<AbstractNode> getChildren() {
        List<AbstractNode> ret = new LinkedList<>();
        int childCount = this.node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ret.add(new Node(this.context, node.getChild(i), this.screenWidth_, this.screenHeight_));
        }
        return ret;
    }

    @Override
    public void setAttr(String attrName, Object attrVal) throws UnableToSetAttributeException {
        switch (attrName) {
            case "text":
                this.node.setText((String) attrVal);
                break;
            default:
                throw new UnableToSetAttributeException(attrName, this.node);
        }
    }

    @Override
    public Object getAttr(String attrName) throws NoSuchAttributeException {
        Object ret = null;
        switch (attrName) {
            case "name":
                ret = node.getViewIdResourceName();
                if (ret == null) {
                    ret = node.getClassName();
                }
                if (ret != null) {
                    ret = ret.toString();
                } else {
                    ret = "<empty>";
                }
                break;
            case "type":
                ret = node.getClassName().toString();
                break;
            case "visible":
                ret = node.isVisibleToUser();
                break;
            case "pos":
                Rect bound = new Rect();
                node.getBoundsInScreen(bound);
                JSONArray pos = new JSONArray();
                try {
                    pos.put(1.0 * bound.centerX() / this.screenWidth_);
                    pos.put(1.0 * bound.centerY() / this.screenHeight_);
                } catch (JSONException e) {}
                ret = pos;
                break;
            case "size":
                Rect bound1 = new Rect();
                node.getBoundsInScreen(bound1);
                JSONArray size = new JSONArray();
                try {
                    size.put(1.0 * bound1.width() / this.screenWidth_);
                    size.put(1.0 * bound1.height() / this.screenHeight_);
                } catch (JSONException e) {}
                ret = size;
                break;
            case "boundsInParent":
                Rect boundP = new Rect();
                node.getBoundsInParent(boundP);
                JSONArray sizeP = new JSONArray();
                try {
                    sizeP.put(1.0 * boundP.width() / this.screenWidth_);
                    sizeP.put(1.0 * boundP.height() / this.screenHeight_);
                } catch (JSONException e) {}
                ret = sizeP;
                break;
            case "scale":
                JSONArray scale = new JSONArray();
                scale.put(1);
                scale.put(1);
                ret = scale;
                break;
            case "anchorPoint":
                JSONArray anchor = new JSONArray();
                try {
                    anchor.put(0.5);
                    anchor.put(0.5);
                } catch (JSONException e) {}
                ret = anchor;
                break;
            case "zOrders":
                JSONObject zOrders = new JSONObject();
                int localOrder = 0;
                try {
                    localOrder = node.getDrawingOrder();
                } catch (NoSuchMethodError e) { }
                try {
                    zOrders.put("global", 0);
                    zOrders.put("local", localOrder);
                } catch (JSONException e) {}
                ret = zOrders;
                break;
            default:
                ret = getSecondaryAttr(attrName);
        }
        return ret;
    }

    @Override
    public Map<String, Object> enumerateAttrs() {
        Map<String, Object> ret = new HashMap<>();
        for (String attr : RequiredAttributes) {
            ret.put(attr, this.getAttr(attr));
        }
        for (String attr : SecondaryAttributes) {
            ret.put(attr, this.getAttr(attr));
        }
        return ret;
    }

    private Object getSecondaryAttr(String attrName) {
        Object ret = null;
        switch (attrName) {
            case "resourceId":
                CharSequence resid = node.getViewIdResourceName();
                if (resid != null) {
                    ret = resid.toString();
                }
                break;
            case "package":
                CharSequence pkgName = node.getPackageName();
                if (pkgName != null) {
                    ret = pkgName.toString();
                }
                break;
            case "desc":
                CharSequence desc = node.getContentDescription();
                if (desc != null) {
                    ret = desc.toString();
                }
                break;
            case "text":
                CharSequence text = node.getText();
                if (text != null) {
                    ret = text.toString();
                }
                break;
            case "enabled":
                ret = node.isEnabled();
                break;
            case "checkable":
                ret = node.isCheckable();
                break;
            case "checked":
                ret = node.isChecked();
                break;
            case "focusable":
                ret = node.isFocusable();
                break;
            case "focused":
                ret = node.isFocused();
                break;
            case "editalbe":
                ret = node.isEditable();
                break;
            case "selected":
                ret = node.isSelected();
                break;
            case "touchable":
                ret = node.isClickable();
                break;
            case "longClickable":
                ret = node.isLongClickable();
                break;
            default:
                throw new NoSuchAttributeException(attrName, this.node);
        }
        return ret;
    }
}
