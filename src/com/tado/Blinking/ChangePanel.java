package com.tado.Blinking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.*;

import java.util.ArrayList;
import java.util.List;

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
    private DrawingDiffs drawClass;

    private List<GestureDetector.SimpleOnGestureListener> listeners = new ArrayList<GestureDetector.SimpleOnGestureListener>();

    class DoubleTapDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("TADO", "double tap");
            thread = thread.toggle();

            for(GestureDetector.SimpleOnGestureListener gl : listeners) {
                gl.onDoubleTap(e);
            }

            return true;
        }
    }

    public void setListenerOnDoubleTap(GestureDetector.SimpleOnGestureListener listener) {
        listeners.add(listener);
    }

    final public static int SQUARES = 1;
    final public static int PULSES = 2;
    public ChangePanel(Context context, long sleepTime, Integer[] sequence, int drawingType) {
        super(context);

        this.sequence = sequence;
        this.sleepTime = sleepTime;

        getHolder().addCallback(this);

        this.setFocusable(true);

        switch (drawingType) {
            case SQUARES:
                drawClass = new Squares();
                break;
            case PULSES:
                drawClass = new Pulses();
                break;
        }
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


    interface DrawingDiffs {
        public void run(SurfaceHolder sh, ChangePanel panel);
        public void onDraw(Canvas canvas);
    }

    class Squares implements DrawingDiffs {
        public void run(SurfaceHolder sh, ChangePanel panel) {
            Canvas c = null;
            try {
                c = sh.lockCanvas(null);
                panel.onDraw(c); //draw canvas
            } finally {
                if (c != null) {
                    sh.unlockCanvasAndPost(c);  //show canvas
                }
            }
        }

        private int getColor() {
            long timeNow = System.currentTimeMillis();

            if (timeNow - lastT >= sleepTime) {
                index++;
                lastT = timeNow;

                if (index > sequence.length-1) index = 0;
            }
            int doMode = sequence[index];
            int res = 0;

            switch(doMode) {
                case 1:
                    res = WHITE;
                    break;
                default:
                    res = BLACK;
            }
            spitTime(doMode);

            return res;
        }

        public void onDraw(Canvas canvas) {
            int color = getColor();
            if (previous != color || true) {
                canvas.drawColor(color);
                previous = color;
            }
        }
    }
    class Pulses implements DrawingDiffs {
        boolean pulse = false;

        public void run(SurfaceHolder sh, ChangePanel panel) {
            Canvas c = null;
            // Draw twice for pulse
            try {
                c = sh.lockCanvas(null);
                panel.onDraw(c); //draw canvas
            } finally {
                if (c != null) {
                    sh.unlockCanvasAndPost(c);  //show canvas
                }
            }
        }

        private int getColor() {
            long timeNow = System.currentTimeMillis();
            // always set pulse to false, let the if bellow figure out
            pulse = false;

            if (timeNow - lastT >= sleepTime) {
                index++;
                lastT = timeNow;

                pulse = true;

                if (index > sequence.length-1) index = 0;
            }
            int doMode = sequence[index];
            int res = 0;

            switch(doMode) {
                case 1:
                    if (pulse) {
                        res = WHITE;
                    } else
                        res = BLACK;
                    break;
                default:
                    res = BLACK;
            }
            spitTime(doMode);

            return res;
        }

        public void onDraw(Canvas canvas) {
            int color = getColor();

            canvas.drawColor(color);
        }
    }


    class CanvasThread extends Thread {
        Canvas c;
        private SurfaceHolder sh;
        private ChangePanel panel;
        private volatile boolean shouldStop = false;

        public CanvasThread(ChangePanel panel, SurfaceHolder sh) {
            this.sh = sh;
            this.panel = panel;

            this.setPriority(Thread.MAX_PRIORITY);
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
                drawClass.run(sh, panel);
            }
        }
    }

    int previous = -1;
    @Override
    public void onDraw(Canvas canvas) {
        drawClass.onDraw(canvas);
    }
}
