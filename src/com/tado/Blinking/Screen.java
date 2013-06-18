package com.tado.Blinking;

import android.app.Activity;
import android.hardware.Camera;
import android.text.Layout;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: neto
 * Date: 6/14/13
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class Screen {
    private static volatile long lastT = 0;
    private int state = 0;
    private boolean shouldStop = false;
    private Activity act;

    private void spitTime() {
        Log.e("TADO->", Long.toString(System.currentTimeMillis() - lastT));
        lastT = System.currentTimeMillis();
    }

    private void black(final LinearLayout ll) {
        act.runOnUiThread(new Thread() {
            public void run() {
                ll.setBackgroundResource(R.color.black);
            }
        });
    }
    private void white(final LinearLayout ll) {
        act.runOnUiThread(new Thread() {
            public void run() {
                ll.setBackgroundResource(R.color.white);
            }
        });
    }

    private void doState(int index, Integer[] sequence, LinearLayout ll) {
        int doMode = sequence[index];
        if (doMode != state) {
            switch(doMode) {
                case 1:
                    white(ll);
                    break;
                case 0:
                    black(ll);
            }
            state = doMode;
        }
        spitTime();
    }

    void start(final LinearLayout ll, Activity act, final int sleepTime, final Integer[] sequence) {
        shouldStop = false;
        this.act = act;

        (new Thread() {
            public void run() {
                int i = 0;
                shouldStop = false;
                try {
                    while(!shouldStop) {
                        if (i > sequence.length-1) i = 0;

                        doState(i, sequence, ll);
                        i++;

                        Thread.sleep(sleepTime);
                    }
                } catch(InterruptedException ex) {}
            }
        }).start();
    }

    void stop() {
        shouldStop = true;
    }


}
