package com.netease.open.pocoservice;

import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.netease.open.libpoco.sdk.IInput;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by adolli on 2017/7/13.
 */

public class Input implements IInput {
    private static final String TAG = Input.class.getName();

    private Context context;
    private UiAutomationConnection uiConnn = null;

    public Input(Context context, UiAutomationConnection uiConnn) {
        this.context = context;
        this.uiConnn = uiConnn;
    }

    @Override
    public void keyevent(int keycode) {
        UiAutomation uiauto = this.uiConnn.get();
        uiauto.injectInputEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode), true);
        uiauto.injectInputEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode), true);
    }

    @Override
    public void click(double x, double y) {
        down(x, y);
        up(x, y);
    }

    @Override
    public void longClick(double x, double y) {
        longClick(x, y, 3);
    }

    @Override
    public void longClick(double x, double y, double duration) {
        // duration: 单位秒
        down(x, y);
        SystemClock.sleep((long) (duration * 1000));
        up(x, y);
    }


    @Override
    public void swipe(double x1, double y1, double x2, double y2, double duration) {
        final int interval = 25;
        int steps = (int) (duration * 1000 / interval + 1);
        double dx = (x2 - x1) / steps;
        double dy = (y2 - y1) / steps;
        down(x1, y1);
        for (int step = 0; step < steps; step++) {
            SystemClock.sleep(interval);
            moveTo(x1 + step * dx, y1 + step * dy);
        }
        SystemClock.sleep(interval);
        up(x2, y2);
    }

    @Override
    public void applyMotionEvents(JSONArray events) throws JSONException {
        long downTime = SystemClock.uptimeMillis();
        int[] size = this.getPortSize();
        int w = size[0];
        int h = size[1];

        SparseArray<MotionEvent.PointerProperties> pps = new SparseArray<>(5);
        SparseArray<MotionEvent.PointerCoords> pcs = new SparseArray<>(5);

        for (int i = 0; i < events.length(); ++i) {
            int eventAction = -1;
            int removeContactId = -1;
            JSONArray event = events.getJSONArray(i);
            String type = event.getString(0);
            switch (type) {
                case "d":
                    int contactId = event.getInt(2);
                    if (pps.indexOfKey(contactId) < 0) {
                        pps.put(contactId, makeMPP(contactId));
                    }
                    if (pps.size() >= 2) {
                        eventAction = getPointerAction(MotionEvent.ACTION_POINTER_DOWN, pps.size() - 1);
                    } else {
                        eventAction = MotionEvent.ACTION_DOWN;
                    }

                    JSONArray pos = event.getJSONArray(1);
                    pcs.put(contactId, makeMPC((float) pos.getDouble(0) * w, (float) pos.getDouble(1) * h));
                    break;
                case "m":
                    // 仅当下一个action不是move才需执行本次的move event执行，否则等下个一起
                    if (i + 1 < events.length()) {
                        JSONArray nextEvent = events.getJSONArray(i + 1);
                        if (!nextEvent.getString(0).equals("m")) {
                            eventAction = MotionEvent.ACTION_MOVE;
                        }
                    } else {
                        // 如果最后一个是move的话，当然这种情况一般不可能
                        eventAction = MotionEvent.ACTION_MOVE;
                    }
                    JSONArray pos1 = event.getJSONArray(1);
                    float fx = (float) pos1.getDouble(0) * w;
                    float fy = (float) pos1.getDouble(1) * h;
                    int contactId1 = event.getInt(2);
                    MotionEvent.PointerCoords coord = pcs.get(contactId1);
                    if (fx == coord.x && fy == coord.y) {
                        // 坐标没有变化的话就不用本次move了
                        eventAction = -1;
                    } else {
                        coord.x = fx;
                        coord.y = fy;
                    }
                    break;
                case "u":
                    removeContactId = event.getInt(1);
                    if (pps.size() == 1) {
                        eventAction = MotionEvent.ACTION_UP;
                    } else {
                        eventAction = getPointerAction(MotionEvent.ACTION_POINTER_UP, pps.size() - 1);
                    }
                    break;
                case "s":
                    // 每个move事件本身要消耗25ms
                    long interval = (long) (event.getDouble(1) * 1000) - 25;
                    if (interval < 0) {
                        interval = 0;
                    }
                    SystemClock.sleep(interval);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown event type \"%s\"", type));
            }

            // Note: 注意以下坑点
            // MotionEvent里的properties和coords一定要按照pointerId从小到大的顺序排
            // 多个move事件最好合批后再inject，没有发生坐标变化的事件就不要进行move了
            // TODO： pointerId 为0的点必须最早下去，最迟起来，可在一开始重新分配一个contactId序列
            if (eventAction != -1) {
                MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[pps.size()];
                for (int j = 0; j < pps.size(); ++j) {
                    properties[j] = pps.valueAt(j);
                }

                MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[pcs.size()];
                for (int j = 0; j < pcs.size(); ++j) {
                    coords[j] = pcs.valueAt(j);
                }

                MotionEvent mevt = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                        eventAction, properties.length, properties, coords,
                        0, 0, 1, 1, 1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);

                injectEvent(mevt);

                // 执行完action up事件后才移除这个点
                if (removeContactId != -1) {
                    pcs.remove(removeContactId);
                    pps.remove(removeContactId);
                }
            }
        }
    }

    // 测试用
    public void testMultiGesture() throws InterruptedException {
        Log.d(TAG, "testMultiGesture");
        MotionEvent.PointerCoords[] pcs1 = makeMPCs(new float[][]{
                {100, 100},
                {200, 200},
                {300, 300},
        });
        MotionEvent.PointerCoords[] pcs2 = makeMPCs(new float[][]{
                {700, 700},
                {600, 600},
                {500, 500},
        });
//        MotionEvent.PointerCoords[] pcs3 = makeMPCs(new float[][]{
//                {500, 700},
//                {456, 600},
//                {999, 200},
//                {800, 250},
//                {700, 240},
//        });
        performMultiPointerGesture(pcs1, pcs2);
        Log.d(TAG, "testMultiGesture - end");
    }

    private int[] getPortSize() {
        WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point dimm = new Point();
        display.getRealSize(dimm);
        return new int[] {dimm.x, dimm.y};
    }

    private MotionEvent.PointerProperties makeMPP(int contactId) {
        MotionEvent.PointerProperties mpp = new MotionEvent.PointerProperties();
        mpp.id = contactId;
        mpp.toolType = MotionEvent.TOOL_TYPE_FINGER;
        return mpp;
    }
    private MotionEvent.PointerCoords makeMPC(float fx, float fy) {
        MotionEvent.PointerCoords mpc = new MotionEvent.PointerCoords();
        mpc.x = fx;
        mpc.y = fy;
        mpc.pressure = 0.2f;
        mpc.size = 0.02f;
        return mpc;
    }
    private MotionEvent.PointerCoords[] makeMPCs(float[][] coords) {
        MotionEvent.PointerCoords[] mpcs = new MotionEvent.PointerCoords[coords.length];
        for (int i = 0; i < coords.length; ++i) {
            float[] p = coords[i];
            MotionEvent.PointerCoords mpc = makeMPC(p[0], p[1]);
            mpcs[i] = mpc;
        }
        return mpcs;
    }
    private MotionEvent.PointerProperties[] makeMPPs(int[] contactIds) {
        MotionEvent.PointerProperties[] mpps = new MotionEvent.PointerProperties[contactIds.length];
        for (int i = 0; i< contactIds.length; ++i) {
            MotionEvent.PointerProperties mpp = makeMPP(contactIds[i]);
            mpps[i] = mpp;
        }
        return mpps;
    }

    private void down(double x, double y) {
        int[] portSize = getPortSize();
        down(x, y, portSize[0], portSize[1], 0, true);
    }
    private void down(double x, double y, int w, int h, int contactId, boolean sync) {
        long now = SystemClock.uptimeMillis();
        double fx = x * w;
        double fy = y * h;
        MotionEvent.PointerProperties[] mpps = makeMPPs(new int[] {contactId});
        MotionEvent.PointerCoords[] mpcs = makeMPCs(new float[][]{{(float) fx, (float) fy}});
        MotionEvent evt = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 1, mpps, mpcs, 0, 0, 1.0f, 1.0f, 1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        injectEvent(evt);
    }

    private void moveTo(double x, double y) {
        int[] portSize = getPortSize();
        moveTo(x, y, portSize[0], portSize[1], 0, true);
    }
    private void moveTo(double x, double y, int w, int h, int contactId, boolean sync) {
        long now = SystemClock.uptimeMillis();
        double fx = x * w;
        double fy = y * h;
        MotionEvent.PointerProperties[] mpps = makeMPPs(new int[] {contactId});
        MotionEvent.PointerCoords[] mpcs = makeMPCs(new float[][]{{(float) fx, (float) fy}});
        MotionEvent evt = MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, mpps.length, mpps, mpcs, 0, 0, 1.0f, 1.0f, 1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        injectEvent(evt, sync);
    }

    private void up() {
        up(0, 0, 1, 1, 0, true);
    }
    private void up(double x, double y) {
        int[] portSise = this.getPortSize();
        int w = portSise[0];
        int h = portSise[1];
        up(x, y, w, h, 0, true);
    }
    private void up(double x, double y, int w, int h, int contactId, boolean sync) {
        long now = SystemClock.uptimeMillis();
        double fx = x * w;
        double fy = y * h;
        MotionEvent.PointerProperties[] mpps = makeMPPs(new int[] {contactId});
        MotionEvent.PointerCoords[] mpcs = makeMPCs(new float[][]{{(float)fx, (float)fy}});
        MotionEvent evt = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, 1, mpps, mpcs, 0, 0, 1.0f, 1.0f, 1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        injectEvent(evt, sync);
    }


    private int getPointerAction(int motionEnvent, int index) {
        return motionEnvent + (index << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
    }

    private boolean injectEvent(MotionEvent evt, boolean sync) {
        boolean result = this.uiConnn.get().injectInputEvent(evt, sync);
        return result;
    }
    private boolean injectEvent(MotionEvent evt) {
        return injectEvent(evt, true);
    }

    private boolean performMultiPointerGesture(MotionEvent.PointerCoords[] ... touches) {
        boolean ret = true;
        if (touches.length < 2) {
            throw new IllegalArgumentException("Must provide coordinates for at least 2 pointers");
        }

        // Get the pointer with the max steps to inject.
        int maxSteps = 0;
        for (int x = 0; x < touches.length; x++)
            maxSteps = (maxSteps < touches[x].length) ? touches[x].length : maxSteps;

        // specify the properties for each pointer as finger touch
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[touches.length];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[touches.length];
        for (int x = 0; x < touches.length; x++) {
            MotionEvent.PointerProperties prop = new MotionEvent.PointerProperties();
            prop.id = x;
            prop.toolType = MotionEvent.TOOL_TYPE_FINGER;
            properties[x] = prop;

            // for each pointer set the first coordinates for touch down
            pointerCoords[x] = touches[x][0];
        }

        // Touch down all pointers
        long downTime = SystemClock.uptimeMillis();
        MotionEvent event;
        event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 1,
                properties, pointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        ret &= injectEvent(event);

        for (int x = 1; x < touches.length; x++) {
            event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                    getPointerAction(MotionEvent.ACTION_POINTER_DOWN, x), x + 1, properties,
                    pointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
            ret &= injectEvent(event);
        }

        // Move all pointers
        for (int i = 1; i < maxSteps - 1; i++) {
            // for each pointer
            for (int x = 0; x < touches.length; x++) {
                // check if it has coordinates to move
                if (touches[x].length > i)
                    pointerCoords[x] = touches[x][i];
                else
                    pointerCoords[x] = touches[x][touches[x].length - 1];
            }

            event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_MOVE, touches.length, properties, pointerCoords, 0, 0, 1, 1,
                    0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);

            ret &= injectEvent(event);
            SystemClock.sleep(5);
        }

        // For each pointer get the last coordinates
        for (int x = 0; x < touches.length; x++)
            pointerCoords[x] = touches[x][touches[x].length - 1];

        // touch up
        for (int x = 1; x < touches.length; x++) {
            event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                    getPointerAction(MotionEvent.ACTION_POINTER_UP, x), x + 1, properties,
                    pointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
            ret &= injectEvent(event);
        }

        Log.i(TAG, "x " + pointerCoords[0].x);
        // first to touch down is last up
        event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 1,
                properties, pointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        ret &= injectEvent(event);
        return ret;
    }
}
