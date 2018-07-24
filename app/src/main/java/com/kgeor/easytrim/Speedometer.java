package com.kgeor.easytrim;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.anderson.dashboardview.view.DashboardView;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Speedometer extends Fragment implements View.OnClickListener {
    // FIELDS //
    public static final String TAG = Speedometer.class.getSimpleName();
    protected int finalSpeed = 0;
    protected static int currentSpeedValue; // TODO remove this is temporary for testing

    // GUI //
    protected Button btnSpeed;
    protected DashboardView speedGauge;
    float multiplier;

    // GPS & SENSOR RELATED //
    private LocationManager locationManager;
    private LocationListener locationListener;
//    public static final int SENSOR_DELAY_MICROS = 16 & 1000; // 16 ms

    // SHARED PREFERENCES //
    private String unitsPref;
    private SharedPreferences sharedPref;
    private String curUnits = "knots";

    DataCommunication dataCommunication;

    public Speedometer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speedometer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataCommunication = (DataCommunication)getActivity(); // init interface

        // GUI REFERENCES //
        btnSpeed = getActivity().findViewById(R.id.btnSpeed);
        speedGauge = getActivity().findViewById(R.id.speed_gauge);

        // LISTENERS //
        btnSpeed.setOnClickListener(this);

        PreferenceManager.setDefaultValues(this.getActivity(), R.xml.pref_general, false);
        detectUnits();

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        // PERMISSIONS //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    }

    public void startSpeedCalc(View view) {
        SpeedTask speedTask = new SpeedTask();
        speedTask.execute();
    }

    protected void detectUnits() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        unitsPref = sharedPref.getString("units_list", "NM");
        Toast.makeText(this.getActivity(), unitsPref, Toast.LENGTH_SHORT).show();
        System.out.println("Units PreF: " + unitsPref);
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
    public void onResume() {
        super.onResume();
        detectUnits();
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
                if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSpeed:
                startSpeedCalc(btnSpeed);
                Toast.makeText(this.getActivity(), "Speed Calculating...", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public class SpeedTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            locationListener = new LocationListener() {
                float initSpeed = 0.0f;
                float convertedSpeed;
                float filteredSpeed;

                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG, "onLocationChanged: Method Start");
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

                    convertedSpeed = initSpeed * multiplier;
                    filteredSpeed = filter(finalSpeed, convertedSpeed, 2);
                    finalSpeed = (int) filteredSpeed;
                    speedGauge.setPercent(finalSpeed);
                    // viewQueryResults(finalSpeed); // TODO is working without implementation?
                    dataCommunication.viewQueryResults(finalSpeed);
                    currentSpeedValue = finalSpeed;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
//                    textView.setText(R.string.standby);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
//                    textView.setText(R.string.unavailable_service);
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
//            textView.setText(R.string.detecting_speed);
        }

        @Override
        protected void onPostExecute(Integer value) {
            configureLocationUpdates();
            speedGauge.setPercent(value);

        }
    } // end Async inner class




}
