package com.kgeor.easytrim;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.kgeor.easytrim.MainActivity.boatTrim;
import static com.kgeor.easytrim.Speedometer.currentSpeedValue;

public class CalibrationTempActivity extends AppCompatActivity implements SensorEventListener, DataCommunication, View.OnClickListener {
    private Button submitTrim;
    static MyDatabase db;

    // SENSOR INFORMATION FOR DETECTING TRIM //
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16 ms
    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_temp);
        submitTrim = findViewById(R.id.set_trim);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        submitTrim.setOnClickListener(this);

        db = new MyDatabase(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRotationSensor == null) {
            Toast.makeText(this, "Rotation Sensor Unavailable", Toast.LENGTH_LONG).show();
        }

        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_MICROS);

    }



    @Override
    public void viewQueryResults(int speed) {
        // TODO method stub
    }

    public void addBoatSpecs() {
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            updateOrientation(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void updateOrientation(float[] rotationVector) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        // Transform rotation matrix into azimuth/pitch/roll (pitch == boatTrim)
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        // Convert radians to degrees
        boatTrim = orientation[1] * -57.295779513f;


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
