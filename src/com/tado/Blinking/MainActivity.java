package com.tado.Blinking;

import android.app.Activity;
import android.os.*;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    final Activity act = this;
    private ChangePanel p;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindowManager();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);

        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (p != null)
            p.setRunning(false);
    }
    @Override
    public void onResume() {
        super.onResume();
        //p.setRunning(true);
    }

    private void init() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);

        final Button b = (Button) findViewById(R.id.button);

        // attach an OnClickListener
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final EditText sleepText = (EditText)findViewById(R.id.editText);
                final EditText sequenceText = (EditText)findViewById(R.id.sequenceText);
                final RadioButton squaresRb = (RadioButton)findViewById(R.id.rbSquares);
                int sleepTime = Integer.parseInt(sleepText.getText().toString());
                String[] sSeq = sequenceText.getText().toString().split(",");
                List<Integer> iSeq = new ArrayList<Integer>();
                for (String s : sSeq) {
                    iSeq.add(Integer.parseInt(s));
                }

                final int mode = squaresRb.isChecked() ? ChangePanel.SQUARES : ChangePanel.PULSES;

                p = new ChangePanel(act, sleepTime, iSeq.toArray(new Integer[]{}), mode);
                p.setListenerOnDoubleTap(new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        act.setContentView(R.layout.main);
                        init();
                        return true;
                    }
                });

                act.setContentView(p);
            }
        });
    }
}
