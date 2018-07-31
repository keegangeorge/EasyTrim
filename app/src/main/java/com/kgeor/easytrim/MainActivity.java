package com.kgeor.easytrim;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.anderson.dashboardview.view.DashboardView;

import java.text.NumberFormat;


/**
 * @author Keegan George
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements DataCommunication, SensorEventListener {
    // FIELDS //
    LottieAnimationView trimCorrectAnimation;


    // GUI //
    protected static DashboardView speedGauge;

    // SENSORS & GPS //
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16 ms
    protected static float boatTrim = 0;

    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;

    // DATABASE //
    static MyDatabase db;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_item:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: ");
        WindowManager mWindowManager = this.getWindow().getWindowManager();
        // GUI REFERENCES //
        trimCorrectAnimation = findViewById(R.id.trim_correct_animation);
        trimCorrectAnimation.setImageAssetsFolder("images/");
        speedGauge = findViewById(R.id.speed_gauge);

        // DATABASE //
        db = new MyDatabase(this);

        // SENSORS & GPS //
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();

        if (mRotationSensor == null) {
            Toast.makeText(this, "Rotation Sensor Unavailable", Toast.LENGTH_LONG).show();
        }

        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_MICROS);
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

    @Override
    public void viewQueryResults(int speed) {
        int convertedTrim = (int) boatTrim;
        int queryResults = db.getCloseToData(speed);
        int trimResults = db.getTrimData(speed);
        // System.out.println("Current Trim: " + boatTrim);
        // System.out.println("Trim for Speed: " + trimResults);
        // System.out.println("Database Speed: " + queryResults);
        // System.out.println("Actual Speed: " + speed);

        if (convertedTrim > trimResults) {
            trimCorrectAnimation.setAnimation("DECREASE_TRIM.json");
            trimCorrectAnimation.playAnimation();
        } else if (convertedTrim < trimResults) {
            trimCorrectAnimation.setAnimation("INCREASE_TRIM.json");
            trimCorrectAnimation.playAnimation();
        } else if (convertedTrim == trimResults) {
            trimCorrectAnimation.setAnimation("NORMAL_TRIM.json");
            trimCorrectAnimation.playAnimation();
        } else {
            trimCorrectAnimation.pauseAnimation();
        }
    }
} // MainActivity class end








