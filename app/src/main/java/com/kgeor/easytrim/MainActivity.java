package com.kgeor.easytrim;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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


import com.anderson.dashboardview.view.DashboardView;

import java.text.NumberFormat;


/**
 * @author Keegan George
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    // FIELDS //
//    public SpeedTask speedTask = new SpeedTask();


    // GUI //
    private Button button;
    //    private Button calibrateButton;
    private Button submitTrim;
    protected Button btnSpeed;
    private TextView textView, trimTextView, trimStat;
    protected static DashboardView speedGauge;

    // SENSORS & GPS //
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16 ms
    protected static float boatTrim = 0;
    protected static int finalSpeed = 0;

    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;
    float multiplier;

    // DATABASE //
    static MyDatabase db;

    // SHARED PREFERENCES //
    private String unitsPref;
    private SharedPreferences sharedPref;

    // UNITS //
    private String curUnits = "knots";


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
        button = findViewById(R.id.button);
        btnSpeed = findViewById(R.id.btnSpeed);
//        calibrateButton = findViewById(R.id.calibrate_button);
//        submitTrim = findViewById(R.id.submit_trim);
//        textView = findViewById(R.id.textView);
        trimTextView = findViewById(R.id.pitch);
        speedGauge = findViewById(R.id.speed_gauge);
        trimStat = findViewById(R.id.trim_up_or_down);

//        calibrateButton.setOnClickListener(this);
//        submitTrim.setOnClickListener(this);
        btnSpeed.setOnClickListener(this);

        // SETTINGS //
        // default values set in XML, this ensures SharedPreferences is initialized with default values
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        detectUnits();


        // DATABASE //
        db = new MyDatabase(this);

        // SENSORS & GPS //
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // PERMISSIONS //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                /*
                 * Occurs on first launch of application and permissions are asked
                 */
                Log.i(TAG, "HERE_3");
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }


    } // onCreate() method end

    public void startSpeedCalc(View view) {
        SpeedTask speedTask = new SpeedTask();
        speedTask.execute();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    protected void detectUnits() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        unitsPref = sharedPref.getString("units_list", "NM");
        Toast.makeText(this, unitsPref, Toast.LENGTH_SHORT).show();
        System.out.println("Units Pref: " + unitsPref);
        System.out.println("Cur Pref: " + curUnits);

        switch (unitsPref) {
            case "NM":
                curUnits = "knots";
                break;
            case "KM":
                curUnits = "kilometersPerHour";
                break;
            case "MI":
                curUnits = "milesPerHour";
                break;
            case "ME":
                System.out.println("When am I called?");
                curUnits = "metersPerSecond";
                break;
        }


    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        detectUnits();

        if (mRotationSensor == null) {
            Toast.makeText(this, "Rotation Sensor Unavailable", Toast.LENGTH_LONG).show();
        }

        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_MICROS);
    }


    /**
     * Recursive filter method for smoothing changes in speed
     * Retrieved from:
     * https://stackoverflow.com/questions/11900097/location-getspeed-update#answer-12152234
     *
     * @param prev Previous value of filter
     * @param curr New input value into filter
     * @return New filtered value
     */
    private float filter(final float prev, final float curr, final int ratio) {
        // If first time through, initialise digital filter with current values
        if (Float.isNaN(prev))
            return curr;
        // If current value is invalid, return previous filtered value
        if (Float.isNaN(curr))
            return prev;
        // Calculate new filtered value
        return (float) (curr / ratio + prev * (1.0 - 1.0 / ratio));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*
                     * Occurs when permissions are granted by the user
                     */
                    Log.i(TAG, "onRequestPermissionsResult: PermissionGranted");
                    return;
                }
        }
    }

    private void configureLocationUpdates() {
        Log.i(TAG, "configureLocationUpdates: requestLocationUpdate");
        // params:   provider, minTime(ms), minDistance, locationListener
        try {
            Log.i(TAG, "HERE_1");
            locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
        } catch (SecurityException e) {

            /*
             * Occurs when permissions are denied on first app launch and then when user attempts to
             * use a feature that requires the permissions, this causes the permissions to pop up again
             */

            Log.i(TAG, "HERE_2");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET
                    }, 10);
                    return;
                }
            }
        }
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
            case R.id.btnSpeed:
                startSpeedCalc(btnSpeed);
                Toast.makeText(this, "Speed Calculating...", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.calibrate_button:
//                Intent i = new Intent(MainActivity.this, CalibrateActivity.class);
//                startActivity(i);
//                break;
//            case R.id.submit_trim:
//                addBoatSpecs(button);
//                break;

//            case R.id.match_speed:
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(intent);
//                break;
        }
    }

    public void addBoatSpecs(View view) {
        long id = db.insertData(finalSpeed, (int) boatTrim);

        if (id < 0) {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }


    }

    public void viewDatabaseResults(View view) {
        String data = db.getData();
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public class SpeedTask extends AsyncTask<Void, Integer, Integer> {


        @Override
        protected Integer doInBackground(Void... params) {
            locationListener = new LocationListener() {
                float initSpeed = 0.0f;
                float convertedSpeed;
                float filteredSpeed;

                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG, "onLocationChanged: Method start");

                    initSpeed = location.getSpeed();


                    switch (curUnits) {
                        case "knots":
                            multiplier = 1.94384f;
                            speedGauge.setUnit(" knots");
                            break;
                        case "kilometersPerHour":
                            multiplier = 3.6f;
                            speedGauge.setUnit(" km/h");
                            break;
                        case "milesPerHour":
                            multiplier = 2.23694f;
                            speedGauge.setUnit(" mph");
                            break;
                        case "metersPerSecond":
                            multiplier = 1f;
                            speedGauge.setUnit(" m/s");
                            break;
                        default:
                            multiplier = 1.94384f;
                            speedGauge.setUnit(" knots");
                            break;
                    }


                    System.out.println("Current Unit: " + curUnits + "  | Multiplier: " + multiplier);

                    convertedSpeed = initSpeed * multiplier;
                    filteredSpeed = filter(finalSpeed, convertedSpeed, 2);
                    finalSpeed = (int) filteredSpeed;
                    speedGauge.setPercent(finalSpeed);
                    viewQueryResults();


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    textView.setText(R.string.standby);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    textView.setText(R.string.unavailable_service);
                }
            };

            return finalSpeed;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            textView.setText(R.string.detecting_speed);
        }

        @Override
        protected void onPostExecute(Integer value) {
            configureLocationUpdates();
            speedGauge.setPercent(value);
        }
    } // end Async inner class

    public void viewQueryResults() {
        int convertedTrim = (int) boatTrim;
        int queryResults = db.getSelectedData(finalSpeed);
        int trimResults = db.getTrimData(finalSpeed);

        if (queryResults == finalSpeed) {
            System.out.println("Boat's Actual Trim: " + convertedTrim);
            System.out.println("Boat's Desired Trim: " + trimResults);
            if (convertedTrim > trimResults) {
                trimStat.setText("Need less trim!");
            } else if (convertedTrim < trimResults) {
                trimStat.setText("Need more trim!");
            } else if (convertedTrim == trimResults) {
                trimStat.setText("Trim is correct!");
            }
        } else {
            trimStat.setText("Need calibration");
        }
    }

} // MainActivity class end








