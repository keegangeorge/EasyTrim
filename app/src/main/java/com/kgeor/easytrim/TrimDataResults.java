package com.kgeor.easytrim;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import static com.kgeor.easytrim.StepperDetails.weatherCondition;

/**
 * Class responsible for displaying the trim data results from the database
 */
public class TrimDataResults extends AppCompatActivity {
    // FIELDS //
    static MyDatabase db;
    static MyDatabase dbWindLight, dbWindStrong, dbStorm, dbSunny;


    private TextView databaseResults;
    private String curUnits = "knots";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim_data_results);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        identifySpeedUnit();

        initDatabase();
        databaseResults = findViewById(R.id.database_values);
        viewDatabaseResults();
    }

    protected void identifySpeedUnit() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String unitsPref = sharedPref.getString("units_list", "NM");

        switch (unitsPref) {
            case "NM":
                curUnits = "knots";
                break;
            case "KM":
                curUnits = "km/h";
                break;
            case "MI":
                curUnits = "mph";
                break;
            case "ME":
                curUnits = "m/s";
                break;
        }
    }

    @SuppressWarnings("deprecation")
    public void viewDatabaseResults() {
        String data = db.getDataStylized(curUnits);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            databaseResults.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY));
        } else {
            databaseResults.setText(Html.fromHtml(data));
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
}
