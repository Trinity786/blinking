package com.tado.Blinking;

import android.hardware.Camera;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: neto
 * Date: 6/14/13
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class Flashlight {
    private static Camera camera = null;
    private static volatile long lastT = 0;
    private int state = 0;
    private Timer t;

    private void on() {
        Camera camera = Camera.open();
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

        camera.setParameters(p);

        camera.startPreview();

        Flashlight.camera = camera;

    }

    private void light() {
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

        camera.setParameters(p);
        spitTime();
    }

    private void off() {
        if (camera != null) {
            Camera.Parameters p = camera.getParameters();

            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            spitTime();
        }
    }

    private void spitTime() {
        Log.e("TADO->", Long.toString(System.currentTimeMillis() - lastT));
        lastT = System.currentTimeMillis();
    }

    void stopBlink() {
        t.cancel();
        off();
    }

    private void doState(int index, Integer[] sequence) {
        int doMode = sequence[index];
        if (doMode != state) {
            switch(doMode) {
                case 1:
                    light();
                    break;
                case 0:
                    off();
            }
            state = doMode;
        }
    }

    void blink(int sleep) {
        blink(sleep, new Integer[] {0,1});
    }

    void blink(final int sleep, final Integer[] sequence) {
        int i = 0;
        on();
        while (true) {
            if (i > sequence.length-1) i = 0;

            doState(i, sequence);
            i++;
        }
    }

    private void doStateBrightness(int index, Integer[] sequence, WindowManager.LayoutParams layout, Window window) {
        int doMode = sequence[index];
        if (doMode != state) {
            switch(doMode) {
                case 1:
                    layout.screenBrightness = 1F;
                    window.setAttributes(layout);
                    break;
                case 0:
                    layout.screenBrightness = 0F;
                    window.setAttributes(layout);
            }
            state = doMode;
        }
    }

}
