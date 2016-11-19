package com.example.fs.cy_demo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class GuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        final Intent intent=new Intent(GuideActivity.this,MainAct.class);
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(task,1000*2);
    }
}
