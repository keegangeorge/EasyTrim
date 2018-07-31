package com.kgeor.easytrim;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * StatsActivity class responsible for displaying statistics to the user
 */
public class StatsActivity extends AppCompatActivity implements View.OnClickListener {
    // GUI //
    TextView topSpeed, measurementUnit, condition;
    Button viewData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // LINK XML TO JAVA //
        topSpeed = findViewById(R.id.top_speed);
        measurementUnit = findViewById(R.id.unit_stat);
        condition = findViewById(R.id.condition_stat);
        viewData = findViewById(R.id.btnViewDataStat);

        // SET LISTENERS //
        viewData.setOnClickListener(this);

        // SET TEXT VALUES //
        topSpeed.setText(Integer.toString(Speedometer.topSpeed) + " " + Speedometer.curUnits);
        measurementUnit.setText(Speedometer.curUnits);
    }

    @Override
    public void onClick(View v) {
        // OPEN TRIM DATA RESULTS UPON BUTTON PRESS //
        Intent i = new Intent(StatsActivity.this, TrimDataResults.class);
        startActivity(i);
    }

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
}
