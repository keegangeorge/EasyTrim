package com.kgeor.easytrim;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import static com.kgeor.easytrim.MainActivity.boatTrim;
import static com.kgeor.easytrim.Speedometer.currentSpeedValue;

public class CalibrationTempActivity extends AppCompatActivity implements SensorEventListener, DataCommunication, View.OnClickListener {
    private LottieAnimationView submitTrim;
    static MyDatabase db;
    boolean keepSearching;

    // SENSOR INFORMATION FOR DETECTING TRIM //
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16 ms
    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;


    // GUI //
    private TextView targetSpeed;
    private int maxSearchSpeed = 100;
    private Button btnComplete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_temp);

        keepSearching = true;
        submitTrim = findViewById(R.id.set_trim);
        targetSpeed = findViewById(R.id.calibration_target_speed);
        btnComplete = findViewById(R.id.calibration_complete);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        submitTrim.setOnClickListener(this);
        btnComplete.setOnClickListener(this);

        db = new MyDatabase(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void checkTargetSpeed() {
        String units;
        switch (Speedometer.curUnits) {
            case "knots":
                units = " knots";
                break;
            case "kilometersPerHour":
                units = " km/h";
                break;
            case "milesPerHour":
                units = " mph";
                break;
            case "metersPerSecond":
                units = " m/s";
                break;
            default:
                units = " knots";
                break;
        }
        if (keepSearching) {
            // TODO change i+= 5 to i+= 10 (5 for testing purposes)
            for (int i = 0; i < maxSearchSpeed; i += 5) {
                System.out.println("For-loop is called");
                boolean isThere = db.hasObject(String.valueOf(i));

                if (!isThere) {
                    submitTrim.setVisibility(View.VISIBLE); // TODO change to based on speed
                    targetSpeed.setText(i + units);
                    keepSearching = false;
                    break;
                }
                System.out.println("HAS? " + isThere);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("CALIBRATION onRESUME() CALLED");
        maxSearchSpeed = StepperDetails.getMaxSpeedInput();
        checkTargetSpeed();


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
        Snackbar failSnack = Snackbar.make(findViewById(R.id.calibration_linear),
                R.string.snack_fail, Snackbar.LENGTH_SHORT);
        failSnack.setAction(R.string.snack_fail_action, new MyTryAgainListener());

        if (id < 0) {
            failSnack.show();
            submitTrim.setAnimation("done_button.json");
            submitTrim.playAnimation();
        } else {
            Snackbar.make(findViewById(R.id.calibration_linear), "Speed: " + targetSpeed.getText() + " Calibrated", Snackbar.LENGTH_LONG).show();
            submitTrim.setAnimation("done_button.json");
            submitTrim.playAnimation();
            keepSearching = true;
            checkTargetSpeed();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_trim:
                addBoatSpecs();
                break;
            case R.id.calibration_complete:
                Intent i = new Intent(CalibrationTempActivity.this, HomeActivity.class);
                startActivity(i);
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

    public class MyTryAgainListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            addBoatSpecs();
        }
    }


}
