package com.kgeor.easytrim;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

/**
 * Class responsible for linking the Stepper (Tutorial Screen) to the associated data
 */
public class MyStepperAdapter extends AbstractFragmentStepAdapter {
    private static final String CURRENT_STEP_POSITION_KEY = "messageResourceId";

    // CONSTRUCTOR //
    public MyStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    /**
     * Method responsible for creating the steps of the tutorial view
     *
     * @param position the current position in the stepper
     */
    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                // FIRST FRAGMENT OF STEPPER: DETAILS //
                final StepperDetails step1 = new StepperDetails();
                Bundle b1 = new Bundle();
                b1.putInt(CURRENT_STEP_POSITION_KEY, position);
                step1.setArguments(b1);
                return step1;
            case 1:
                // SECOND FRAGMENT OF STEPPER: MOUNT DEVICE //
                final StepperMountDevice step2 = new StepperMountDevice();
                Bundle b2 = new Bundle();
                b2.putInt(CURRENT_STEP_POSITION_KEY, position);
                step2.setArguments(b2);
                return step2;
            case 2:
                // THIRD FRAGMENT OF STEPPER: CALIBRATION INFO //
                final StepperCalibration step3 = new StepperCalibration();
                Bundle b3 = new Bundle();
                b3.putInt(CURRENT_STEP_POSITION_KEY, position);
                step3.setArguments(b3);
                return step3;
        }
        return null;
    }

    /**
     * Method responsible for returning the number of steps in the stepper
     *
     * @return the number of steps in the stepper
     */
    @Override
    public int getCount() {
        return 3;
    }


    /**
     * Method responsible for setting title information in stepper view with tabs
     * Currently not being used, but kept for if switching to tab style
     *
     * @param position current position in stepper
     */
    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        switch (position) {
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("Information")
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Mount Device")
                        .create();
            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle("Calibration")
                        .create();
        }
        return null;
    }
}
