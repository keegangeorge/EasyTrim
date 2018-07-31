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

/**
 * Activity responsible for submitting trim values based on the boat's speed to the database
 * which will be later viewed in the trim view.
 */
public class CalibrationActivity extends AppCompatActivity
        implements SensorEventListener, DataCommunication, View.OnClickListener {

    // FIELDS //
    boolean keepSearching;
    private int maxSearchSpeed = 100;
    static MyDatabase db;

    // GUI //
    private LottieAnimationView submitTrim;
    private TextView targetSpeed;

    // SENSOR INFORMATION FOR DETECTING TRIM //
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16 ms
    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;


    /**
     * Method responsible for initializing values for when Activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        keepSearching = true; // boolean for checking to continue calibration

        // LINK XML TO JAVA //
        submitTrim = findViewById(R.id.set_trim);
        targetSpeed = findViewById(R.id.calibration_target_speed);
        Button btnComplete = findViewById(R.id.calibration_complete);

        // SENSOR RELATED //
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // SET LISTENERS //
        submitTrim.setOnClickListener(this);
        btnComplete.setOnClickListener(this);

        // DATABASE INITIALIZATION //
        db = new MyDatabase(this);

    } // onCreate() method end

    @Override
    protected void onPause() {
        super.onPause();
        // release sensors early
        mSensorManager.unregisterListener(this);
    }

    /**
     * Method responsible for checking the current target speed to calibrate at
     * Also checks for the current units and represents them in short form
     */
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

        /*
         * Loops through target speeds in increments of 10 and stops at
         * the maximum speed the user set in the setup
         */
        if (keepSearching) {
            for (int i = 0; i < maxSearchSpeed; i += 10) {
                boolean isThere = db.hasObject(String.valueOf(i));
                if (!isThere) {
                    submitTrim.setVisibility(View.VISIBLE);
                    targetSpeed.setText(i + units);
                    keepSearching = false;
                    break;
                }
            } // for-loop end
        } // keepSearching if-statement end
    } // checkTargetSpeed() method end

    @Override
    protected void onResume() {
        super.onResume();
        // get the maximum speed based on the user input from the setup screen
        maxSearchSpeed = StepperDetails.getMaxSpeedInput();
        checkTargetSpeed();

        // attempt to acquire rotation sensor
        if (mRotationSensor == null) {
            Toast.makeText(this, "Rotation Sensor Unavailable", Toast.LENGTH_LONG).show();
        }
        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_MICROS);
    }


    @Override
    public void viewQueryResults(int speed) {
        // TODO method stub
    }

    /**
     * Method responsible for inserting the trim data relating to the current speed into the
     * database. After which a snack-bar will appear as feedback, along with an animated button
     * indicating whether it was added or not.
     */
    public void addBoatSpecs() {
        long id = db.insertData(currentSpeedValue, (int) boatTrim);

        // Snack-bar to appear when insertion in database fails.
        Snackbar failSnack = Snackbar.make(findViewById(R.id.calibration_linear),
                R.string.snack_fail, Snackbar.LENGTH_SHORT);
        failSnack.setAction(R.string.snack_fail_action, new MyTryAgainListener());

        if (id < 0) {
            // FAILED ENTRY //
            failSnack.show();
            submitTrim.setAnimation("BTN_FAIL.json");
            submitTrim.playAnimation();
        } else {
            // SUCCESSFUL ENTRY //
            Snackbar.make(findViewById(R.id.calibration_linear), "Speed: " + targetSpeed.getText() + " Calibrated", Snackbar.LENGTH_LONG).show();
            submitTrim.setAnimation("BTN_SUCCESS.json");
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
                Intent i = new Intent(CalibrationActivity.this, HomeActivity.class);
                startActivity(i);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Update the device orientation values when sensor event changes
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

    /**
     * Inner-class implementing a click listener for
     * the try-again button of the snack-bar
     */
    public class MyTryAgainListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            addBoatSpecs();
        }
    } // MyTryAgainListener inner class end
} // CalibrationActivity end
