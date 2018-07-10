package com.kgeor.easytrim;

import android.Manifest;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hololo.tutorial.library.PermissionStep;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class CalibrateActivity extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_calibrate);

        // Slider 1
        addFragment(new Step.Builder().setTitle("This is header 1")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.default_placeholder) // int top drawable
                .setSummary("This is summary")
                .build());

        // Slider 2
        addFragment(new Step.Builder().setTitle("This is header 2")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.default_placeholder) // int top drawable
                .setSummary("This is summary")
                .build());

        // Slider 3
        addFragment(new Step.Builder().setTitle("This is header 3")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.default_placeholder) // int top drawable
                .setSummary("This is summary")
                .build());
    }

    @Override
    public void finishTutorial() {
        // Your implementation
    }
}
