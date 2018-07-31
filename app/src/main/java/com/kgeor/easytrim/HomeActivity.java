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

public class HomeActivity extends AppCompatActivity implements
        DiscreteScrollView.OnItemChangedListener<DashboardAdapter.ViewHolder>,
        DiscreteScrollView.ScrollStateChangeListener<DashboardAdapter.ViewHolder>,
        View.OnClickListener {

    private List<DataItem> data;
    private HomeDashboard dashboard;
    protected static String cardItemState;
    private TextView currentItemName;
    private Button button2;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardItemState = "Trim View";
        setContentView(R.layout.activity_home_activity);

        currentItemName = findViewById(R.id.item_name);

        currentItemName.setOnClickListener(this);

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(this);

        dashboard = HomeDashboard.get();
        data = dashboard.getData();

        itemPicker = findViewById(R.id.item_picker);
        itemPicker.setOnClickListener(this); // TODO testing?

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

//        if (itemPicker.getScrollState() == SCROLL_STATE_DRAGGING) {
//            button2.setVisibility(View.INVISIBLE);
//        } else {
//            button2.setVisibility(View.VISIBLE);
//        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
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
                    Intent i = new Intent(HomeActivity.this, WelcomeActivity.class);
                    startActivity(i);
                }
                break;
        }


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
        System.out.println("onItemChanged() called");

    }

    @Override
    public void onScrollStart(@NonNull DashboardAdapter.ViewHolder currentItemHolder, int adapterPosition) {
//        button2.animate().alpha(0.0f).setDuration(200);
        button2.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setDuration(100);
//        button2.setVisibility(View.INVISIBLE);
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
//            button2.animate().alpha(1.0f).setDuration(200);
//            button2.setVisibility(View.VISIBLE);
            button2.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(150);

        }
    }
}
