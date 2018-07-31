package com.kgeor.easytrim;


import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.anderson.dashboardview.view.DashboardView;

import static com.kgeor.easytrim.StepperDetails.weatherCondition;


/**
 * MainActivity class responsible for the primary activity - trim view, in which the user can
 * view a speedometer showcasing their current speed, as well as an indicator prompting the user
 * whether to increase or decrease the trim value
 *
 * @author Keegan George
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity
        implements DataCommunication, SensorEventListener {

    // FIELDS //
    private static final String TAG = MainActivity.class.getSimpleName();
    LottieAnimationView trimCorrectAnimation;
    protected static float boatTrim = 0;

    // GUI //
    protected static DashboardView speedGauge;

    // SENSORS & GPS //
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16 ms
    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;

    // DATABASE //
    static MyDatabase db;
    static MyDatabase dbWindLight, dbWindStrong, dbStorm, dbSunny;


    /**
     * Method responsible for what occurs when the settings items are clicked.
     */
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

    /**
     * Method responsible for inflating the settings menu
     *
     * @param menu the settings menu to be inflated
     * @return true if the menu is inflated
     */
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

        // Log.i(TAG, "onCreate: ");
        // GUI REFERENCES //
        WindowManager mWindowManager = this.getWindow().getWindowManager();
        trimCorrectAnimation = findViewById(R.id.trim_correct_animation);
        trimCorrectAnimation.setImageAssetsFolder("images/");
        speedGauge = findViewById(R.id.speed_gauge);

        // DATABASE //
        initDatabase();

        // SENSORS & GPS //
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    } // onCreate method end

    @Override
    protected void onPause() {
        super.onPause();
        // Release the sensor early
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        // Log.i(TAG, "onResume: ");
        super.onResume();

        // Attempts to register the rotation sensor if it is available on the device
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

    /**
     * Method responsible for getting the rotation values from the rotation sensors
     * and updating it to the current values. It also converts those values to degrees.
     */
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
     * Method responsible for comparing the current speed value with
     * the value in the database to check whether or not to increase
     * or decrease the boat's trim for that specific speed.
     *
     * @param speed the current speed the boat is at
     */
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

    /**
     * Method responsible for initializing database based on the current weather condition
     */
    public void initDatabase() {
        // DATABASE //
        dbWindLight = new MyDatabase(this);
        dbWindStrong = new MyDatabase(this);
        dbStorm = new MyDatabase(this);
        dbSunny = new MyDatabase(this);

        switch (weatherCondition) {
            case "Sunny":
                db = dbSunny;
                System.out.println("DB SET TO SUNNY");
                break;
            case "Light Wind":
                db = dbWindLight;
                System.out.println("DB SET TO LIGHT WIND");
                break;
            case "Strong Wind":
                db = dbWindStrong;
                System.out.println("DB SET TO STRONG WIND");
                break;
            case "Storm":
                db = dbStorm;
                System.out.println("DB SET TO STORM");
                break;
        }
    }


} // MainActivity class end








