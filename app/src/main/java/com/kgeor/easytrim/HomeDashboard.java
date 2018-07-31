package com.kgeor.easytrim;

import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

public class HomeDashboard {
    public static final String STORAGE = "dashboard";

    public static HomeDashboard get() {
        return new HomeDashboard();
    }

    private SharedPreferences storage;

    private HomeDashboard() {
    }

    public List<DataItem> getData() {
        return Arrays.asList(
                new DataItem(1, "Trim View", R.drawable.trim_card),
                new DataItem(2, "Calibration", R.drawable.calibration_card),
                new DataItem(3, "Stats", R.drawable.stats_card));
    }


}
