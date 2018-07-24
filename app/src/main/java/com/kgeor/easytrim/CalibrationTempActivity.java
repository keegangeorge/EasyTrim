package com.kgeor.easytrim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.kgeor.easytrim.MainActivity.boatTrim;
import static com.kgeor.easytrim.Speedometer.currentSpeedValue;

public class CalibrationTempActivity extends AppCompatActivity implements DataCommunication, View.OnClickListener {
    // TODO add functionality to start calculating the trim values
    private Button submitTrim;
    static MyDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_temp);
        submitTrim = findViewById(R.id.set_trim);


        submitTrim.setOnClickListener(this);

        db = new MyDatabase(this);

    }


    @Override
    public void viewQueryResults(int speed) {
        // TODO method stub
    }

    public void addBoatSpecs() {
        // TODO FIX BUG! EXPLAINED BELOW:
        /*
         * When opening for first time and going straight to calibration view
         * (without ever clicking on TrimView yet) and when "setting trim"
         * the Trim Value is always stored in the database as '0'
         *
         * This is probably because the rotation sensor
         * is not being implemented yet
         */
        long id = db.insertData(currentSpeedValue, (int) boatTrim);

        if (id < 0) {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_trim:
                addBoatSpecs();
                break;
        }
    }

//    public void addBoatSpecs(View view) {
//        long id = db.insertData(main.finalSpeed, (int) main.boatTrim);
//
//        if (id < 0) {
//            Toast.makeText(this, "Fail: Duplicate Speed", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
//        }
//    }


}
