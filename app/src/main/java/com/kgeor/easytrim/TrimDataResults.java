package com.kgeor.easytrim;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TrimDataResults extends AppCompatActivity {
    static MyDatabase db;
    private TextView databaseResults;
    private String curUnits = "knots";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim_data_results);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        identifySpeedUnit();


        db = new MyDatabase(this);
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

    public void viewDatabaseResults() {
        String data = db.getDataStylized(curUnits);
        databaseResults.setText(Html.fromHtml(data));
    }
}
