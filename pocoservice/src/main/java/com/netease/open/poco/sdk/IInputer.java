package com.netease.open.poco.sdk;

/**
 * Created by adolli on 2017/7/13.
 */

public interface IInputer {
    void keyevent(int keycode);

    void click(int x, int y);

    void longClick(int x, int y);

    void swipe(int x1, int y1, int x2, int y2, int durationInMillis);
}
