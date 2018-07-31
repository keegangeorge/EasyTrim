package com.kgeor.easytrim;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.List;

/**
 * Activity responsible for the main menu which showcases a swipe-able card view
 */
public class HomeActivity extends AppCompatActivity implements
        DiscreteScrollView.OnItemChangedListener<DashboardAdapter.ViewHolder>,
        DiscreteScrollView.ScrollStateChangeListener<DashboardAdapter.ViewHolder>,
        View.OnClickListener {

    // FIELDS //
    private List<DataItem> data;
    private HomeDashboard dashboard;
    protected static String cardItemState;
    private TextView currentItemName;
    private Button btnView;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);
        firstAppLaunch();

        cardItemState = "Trim View";

        // LINK XML TO JAVA //
        currentItemName = findViewById(R.id.item_name);
        btnView = findViewById(R.id.btn_card_view);
        itemPicker = findViewById(R.id.item_picker);


        // SET LISTENERS //
        currentItemName.setOnClickListener(this);
        btnView.setOnClickListener(this);
        itemPicker.setOnClickListener(this);

        // DATA //
        dashboard = HomeDashboard.get();
        data = dashboard.getData();

        // DISCRETE SCROLL VIEW //
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        itemPicker.addScrollStateChangeListener(this);

        infiniteAdapter = InfiniteScrollAdapter.wrap(new DashboardAdapter(data));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransitionTimeMillis(150);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        onItemChanged(data.get(0));
    } // onCreate method end

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_card_view:
                int realPosition = infiniteAdapter.getRealPosition(itemPicker.getCurrentItem());
                DataItem current = data.get(realPosition);
                if (realPosition == 0) {
                    // TRIM VIEW CARD //
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(i);
                } else if (realPosition == 1) {
                    // CALIBRATION CARD //
                    Intent i = new Intent(HomeActivity.this, WelcomeActivity.class);
                    startActivity(i);
                } else if (realPosition == 2) {
                    // STATS CARD //
                    Intent i = new Intent(HomeActivity.this, StatsActivity.class);
                    startActivity(i);
                }
                break;
        }
    }

    /**
     * Method responsible for what occurs on a first launch of the application
     * Will cause the WelcomeActivity to be opened, prompting user for setup.
     */
    private void firstAppLaunch() {
        // Get reference to shared preference if this is the user's first run of the app
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            // If it is the first run, open the WelcomeActivity
            startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
        }

        // Make the first run boolean false
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
    }

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

    private void onItemChanged(DataItem item) {
        currentItemName.setText(item.getName());
    }

    @Override
    public void onScrollStart(@NonNull DashboardAdapter.ViewHolder currentItemHolder, int adapterPosition) {
        // When scrolling begins, scale down and hide the button
        btnView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setDuration(100);
    }

    @Override
    public void onScrollEnd(@NonNull DashboardAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable DashboardAdapter.ViewHolder currentHolder, @Nullable DashboardAdapter.ViewHolder newCurrent) {
    }

    @Override
    public void onCurrentItemChanged(@Nullable DashboardAdapter.ViewHolder viewHolder, int adapterPosition) {
        final int positionInDataSet = infiniteAdapter.getRealPosition(adapterPosition);
        onItemChanged(data.get(positionInDataSet));

        if (viewHolder != null) {
            // When scrolling stops, scale the button back up and un-hide it
            btnView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(150);
        }
    }

} // HomeActivity end
