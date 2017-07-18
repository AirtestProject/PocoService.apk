package com.netease.open.poco.impl;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.netease.open.poco.sdk.IAttributor;
import com.netease.open.poco.sdk.NoSuchAttributeException;
import com.netease.open.poco.sdk.UnableToSetAttributeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adolli on 2017/7/12.
 */

public class Attributor implements IAttributor<AccessibilityNodeInfo> {
    private Map<String, IAttributeGetter> getters = new HashMap<>();
    private Map<String, IAttributeSetter> setters = new HashMap<>();

    public Attributor() {
        getters.put("visible", new AttributeVisibility());
        getters.put("text", new AttributeText());
        getters.put("type", new AttributeType());
        getters.put("enable", new AttributeEnable());
        getters.put("touchable", new AttributeTouchable());
        getters.put("screenPosition", new AttributeScreenPosition());
        getters.put("anchorPosition", new AttributeAnchorPosition());
        getters.put("size", new AttributeSize());
        getters.put("name", new AttributeName());

        setters.put("text", new AttributeText());
    }

    @Override
    public Object getAttr(AccessibilityNodeInfo[] nodes, String attrName) throws NoSuchAttributeException {
        AccessibilityNodeInfo node = nodes[0];
        return this.getAttr(node, attrName);
    }

    @Override
    public Object getAttr(AccessibilityNodeInfo node, String attrName) throws NoSuchAttributeException {
        IAttributeGetter getter = getters.get(attrName);
        if (getter != null) {
            return getter.get(node);
        }
        throw new NoSuchAttributeException(String.format("cannot evalute attribute \"%s\" of node \"%s\".", attrName, node.toString()));
    }

    @Override
    public void setAttr(AccessibilityNodeInfo[] nodes, String attrName, Object value) throws UnableToSetAttributeException {
        AccessibilityNodeInfo node = nodes[0];
        this.setAttr(node, attrName, value);
    }

    @Override
    public void setAttr(AccessibilityNodeInfo node, String attrName, Object value) throws UnableToSetAttributeException {
        IAttributeSetter setter = setters.get(attrName);
        if (setter != null) {
            setter.set(node, value);
        } else {
            throw new UnableToSetAttributeException(String.format("cannot set attribute \"%s\" of node \"%s\".", attrName, node.toString()));
        }
    }

    public interface IAttributeGetter {
        Object get(AccessibilityNodeInfo n);
    }

    public interface IAttributeSetter {
        void set(AccessibilityNodeInfo n, Object value);
    }

    public class AttributeVisibility implements IAttributeGetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            return n.isVisibleToUser();
        }
    }

    public class AttributeText implements IAttributeGetter, IAttributeSetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            CharSequence text = n.getText();
            if (text != null) {
                return text.toString();
            } else {
                return null;
            }
        }

        @Override
        public void set(AccessibilityNodeInfo n, Object value) {
            n.setText((String) value);
        }
    }

    public class AttributeType implements IAttributeGetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            return n.getClassName();
        }
    }

    public class AttributeEnable implements IAttributeGetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            return n.isEnabled();
        }
    }

    public class AttributeTouchable implements IAttributeGetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            return n.isClickable();
        }
    }

    public class AttributeScreenPosition implements IAttributeGetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            Rect rect = new Rect();
            n.getBoundsInScreen(rect);
            int x = rect.centerX();
            int y = rect.centerY();
            return new int[]{x, y};
        }
    }

    public class AttributeAnchorPosition extends AttributeScreenPosition {}

    public class AttributeSize implements IAttributeGetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            Rect rect = new Rect();
            n.getBoundsInScreen(rect);
            int w = rect.width();
            int h = rect.height();
            return new int[]{w, h};
        }
    }

    public class AttributeName implements IAttributeGetter {

        @Override
        public Object get(AccessibilityNodeInfo n) {
            String resid = n.getViewIdResourceName();
            if (resid != null) {
                return resid;
            } else {
                return n.getClassName().toString();
            }
        }
    }
}
