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
public class MainActivity extends AppCompatActivity implements DataCommunication, SensorEventListener, View.OnClickListener {
    // FIELDS //
//    public SpeedTask speedTask = new SpeedTask();
    LottieAnimationView trimCorrectAnimation;


    // GUI //
//    private Button button;
    //    private Button calibrateButton;
    private Button submitTrim;
    //    protected Button btnSpeed;
    private TextView textView, trimTextView, trimStat, tempInfo;
    protected static DashboardView speedGauge;

    // SENSORS & GPS //
//    private LocationManager locationManager;
//    private LocationListener locationListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16 ms
    protected static float boatTrim = 0;
//    protected static int finalSpeed = 0;

    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;
    float multiplier;

    // DATABASE //
    static MyDatabase db;

    // SHARED PREFERENCES //
//    private String unitsPref;
//    private SharedPreferences sharedPref;

    // UNITS //
//    private String curUnits = "knots";


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
//        button = findViewById(R.id.button);
        trimCorrectAnimation = findViewById(R.id.trim_correct_animation);
        tempInfo = findViewById(R.id.information);
        trimCorrectAnimation.setImageAssetsFolder("images/");
//        btnSpeed = findViewById(R.id.btnSpeed);
//        calibrateButton = findViewById(R.id.calibrate_button);
//        submitTrim = findViewById(R.id.submit_trim);
//        textView = findViewById(R.id.textView);
        trimTextView = findViewById(R.id.pitch);
        speedGauge = findViewById(R.id.speed_gauge);
        trimStat = findViewById(R.id.trim_up_or_down);

//        calibrateButton.setOnClickListener(this);
//        submitTrim.setOnClickListener(this);
//        btnSpeed.setOnClickListener(this);

        // SETTINGS //
        // default values set in XML, this ensures SharedPreferences is initialized with default values
//        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
//        detectUnits();


        // DATABASE //
        db = new MyDatabase(this);

        // SENSORS & GPS //
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // PERMISSIONS //
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                /*
//                 * Occurs on first launch of application and permissions are asked
//                 */
//                Log.i(TAG, "HERE_3");
//                requestPermissions(new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.INTERNET
//                }, 10);
//                return;
//            }
//        }


    } // onCreate() method end

//    public void startSpeedCalc(View view) {
//        SpeedTask speedTask = new SpeedTask();
//        speedTask.execute();
//    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }


    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
//        detectUnits();

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

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(0);
        trimTextView.setText("CURRENT TRIM: " + (int) boatTrim);
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
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    public void viewDatabaseResults(View view) {
        String data = db.getData();
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }


    @Override
    public void viewQueryResults(int speed) {
        int convertedTrim = (int) boatTrim;
        int queryResults = db.getCloseToData(speed);

        int trimResults = db.getTrimData(speed);
        System.out.println("Current Trim: " + boatTrim);
        System.out.println("Trim for Speed: " + trimResults);
        System.out.println("Database Speed: " + queryResults);
        System.out.println("Actual Speed: " + speed);
        tempInfo.setText("TRIM: " + convertedTrim + " TRIM FOR SPEED: " +
                trimResults + " DB SPEED: " + queryResults + " REAL SPEED: " +
                speed);

//        if (queryResults == speed) {
//            System.out.println("Boat's Actual Trim: " + convertedTrim);
//            System.out.println("Boat's Desired Trim: " + trimResults);
//            if (convertedTrim > trimResults) {
//                trimCorrectAnimation.setAnimation("trim-down.json");
//                trimCorrectAnimation.playAnimation();
//                trimStat.setText("Need less trim!");
//            } else if (convertedTrim < trimResults) {
//                trimCorrectAnimation.setAnimation("trim-up.json");
//                trimCorrectAnimation.playAnimation();
//                trimStat.setText("Need more trim!");
//
//            } else if (convertedTrim == trimResults) {
//                trimStat.setText("Trim is correct!");
//                trimCorrectAnimation.setAnimation("steady.json");
//                trimCorrectAnimation.playAnimation();
//            }
//        } else if(queryResults > speed) {
//            trimStat.setText("DB GREATER THAT REAL");
//        } else if (queryResults < speed) {
//            trimStat.setText("DB LESS THAN REAL");
//        } else {
//            trimCorrectAnimation.pauseAnimation();
//            trimStat.setText("Need calibration");
//        }

        if (convertedTrim > trimResults) {
//            trimCorrectAnimation.setAnimation("trim-down.json");
            trimCorrectAnimation.setAnimation("DECREASE_TRIM.json");
            trimCorrectAnimation.playAnimation();
            trimStat.setText("Need less trim!");
        } else if (convertedTrim < trimResults) {
            trimCorrectAnimation.setAnimation("INCREASE_TRIM.json");
            trimCorrectAnimation.playAnimation();
            trimStat.setText("Need more trim!");

        } else if (convertedTrim == trimResults) {
            trimStat.setText("Trim is correct!");
            trimCorrectAnimation.setAnimation("NORMAL_TRIM.json");
            trimCorrectAnimation.playAnimation();
        } else {
            trimCorrectAnimation.pauseAnimation();
        }


    }


} // MainActivity class end








