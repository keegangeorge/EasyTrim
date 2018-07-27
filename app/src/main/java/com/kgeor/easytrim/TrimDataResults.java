package com.kgeor.easytrim;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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

    @SuppressWarnings("deprecation")
    public void viewDatabaseResults() {
        String data = db.getDataStylized(curUnits);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            databaseResults.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY));
        } else {
            databaseResults.setText(Html.fromHtml(data));
        }
    }

}
