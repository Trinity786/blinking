package com.tado.Blinking;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

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
        p.setRunning(false);
    }
    @Override
    public void onResume() {
        super.onResume();
        //p.setRunning(true);
    }

    private void init() {
        final ToggleButton b = (ToggleButton) findViewById(R.id.toggleButton);
        //final Flashlight flashlight = new Flashlight();
        final LinearLayout llayout = (LinearLayout) findViewById(R.id.layout);

        final Screen s = new Screen();

        // attach an OnClickListener
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final EditText sleepText = (EditText)findViewById(R.id.editText);
                final EditText sequenceText = (EditText)findViewById(R.id.sequenceText);
                int sleepTime = Integer.parseInt(sleepText.getText().toString());
                String[] sSeq = sequenceText.getText().toString().split(",");
                List<Integer> iSeq = new ArrayList<Integer>();
                for (String s : sSeq) {
                    iSeq.add(Integer.parseInt(s));
                }

                if (b.isChecked()) {
                    //flashlight.blink(sleepTime, iSeq.toArray(new Integer[]{}));
                    //s.start(llayout, );

                    s.start(llayout, act, sleepTime, iSeq.toArray(new Integer[]{}));
                }
                else {
                    //flashlight.stopBlink();
                    s.stop();
                }
            }
        });
    }
}
