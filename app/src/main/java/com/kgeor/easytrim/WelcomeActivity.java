package com.kgeor.easytrim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kgeor.easytrim.R;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btnSetup = findViewById(R.id.btnWelcomeSetup);
        btnSetup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(WelcomeActivity.this, Tutorial.class);
        startActivity(i);
    }
}
