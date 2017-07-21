package com.netease.open.poco.sdk;

/**
 * Created by adolli on 2017/7/13.
 */

public interface IInputer {
    void keyevent(int keycode);

    void click(float x, float y);

    void longClick(float x, float y);
    void longClick(float x, float y, float duration);

    void swipe(float x1, float y1, float x2, float y2, float duration);
}
