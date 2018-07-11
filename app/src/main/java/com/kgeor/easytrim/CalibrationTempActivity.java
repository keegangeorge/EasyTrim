package com.kgeor.easytrim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CalibrationTempActivity extends AppCompatActivity implements View.OnClickListener {
    // TODO add functionality to start calculating the trim values
    private Button submitTrim;
    static MyDatabase db;
    MainActivity main;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_temp);
        submitTrim = findViewById(R.id.set_trim);


        submitTrim.setOnClickListener(this);

        db = new MyDatabase(this);
        main.speedGauge = findViewById(R.id.speed_gauge_calibration);




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_trim:
                addBoatSpecs(submitTrim);
                break;
        }
    }

    public void addBoatSpecs(View view) {
        long id = db.insertData(main.finalSpeed, (int) main.boatTrim);

        if (id < 0) {
            Toast.makeText(this, "Fail: Duplicate Speed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }
    }


}
