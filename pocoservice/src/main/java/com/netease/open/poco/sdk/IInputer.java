package com.netease.open.poco.sdk;

/**
 * Created by adolli on 2017/7/13.
 */

public interface IInputer {
    void keyevent(int keycode);

    // java里默认会把浮点当做double，默认都定义成double型参数，反射时的多态匹配优先匹配double型
    void click(double x, double y);

    void longClick(double x, double y);
    void longClick(double x, double y, double duration);

    void swipe(double x1, double y1, double x2, double y2, double duration);

}
