package com.tado.Blinking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.*;

/**
 * Created with IntelliJ IDEA.
 * User: neto
 * Date: 6/17/13
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChangePanel extends SurfaceView implements SurfaceHolder.Callback {

    private CanvasThread thread;
    public volatile long lastT = 0;
    private Integer[] sequence;
    private long sleepTime;

    class DoubleTapDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("TADO", "double tap");
            thread = thread.toggle();
            return true;
        }
    }




    public ChangePanel(Context context, long sleepTime, Integer[] sequence) {
        super(context);

        this.sequence = sequence;
        this.sleepTime = sleepTime;

        getHolder().addCallback(this);

        this.setFocusable(true);
    }


    @Override

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }


    @Override

    public void surfaceCreated(SurfaceHolder holder) {
        thread = new CanvasThread(this, getHolder());

        thread.setRunning(true);
        thread.start();
    }


    @Override

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }


        thread = null;
    }

    private void spitTime(int seq) {
        //Log.e("TADO->", Long.toString(System.currentTimeMillis() - lastT) + " | " + Integer.toString(seq));
    }

    private int index = 0;
    final private int BLACK = getResources().getColor(R.color.black);
    final private int WHITE = getResources().getColor(R.color.white);

    private int getColor() {
        long timeNow = System.currentTimeMillis();

        if (timeNow - lastT >= this.sleepTime) {
            index++;
            lastT = timeNow;

            if (index > sequence.length-1) index = 0;
        }
        int doMode = sequence[index];
        int res = 0;

        switch(doMode) {
            case 1:
                res = BLACK;
                break;
            default:
                res = WHITE;
        }
        spitTime(doMode);

        return res;
    }

    public void setRunning(boolean run) {
        if (run && thread == null) {
            this.surfaceCreated(getHolder());
        }
        else if (!run) {
            thread.setRunning(false);
        }
    }


    final GestureDetector doubleTap = new GestureDetector(this.getContext(), new DoubleTapDetector());


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        doubleTap.onTouchEvent(event);

        return true; //super.onTouchEvent(event);
    }


    class CanvasThread extends Thread {
        Canvas c;
        private SurfaceHolder sh;
        private ChangePanel panel;
        private volatile boolean shouldStop = false;

        public CanvasThread(ChangePanel panel, SurfaceHolder sh) {
            this.sh = sh;
            this.panel = panel;
        }

        public void setRunning(boolean run) {
            this.shouldStop = !run;
        }

        public CanvasThread toggle() {
            if (this.shouldStop) {
                CanvasThread ct = new CanvasThread(panel, sh);
                ct.start();
                return ct;
            }
            else {
                this.shouldStop = true;
            }

            return this;
        }

        public void run() {
            shouldStop = false;

            while(!shouldStop) {
                try {
                    c = sh.lockCanvas(null);
                    panel.onDraw(c); //draw canvas
                } finally {
                    if (c != null) {
                        sh.unlockCanvasAndPost(c);  //show canvas
                    }
                }
            }
        }
    }

    int previous = -1;
    @Override
    public void onDraw(Canvas canvas) {
        int color = getColor();
        if (previous != color) {
            canvas.drawColor(color);
            previous = color;
        }
    }
}
