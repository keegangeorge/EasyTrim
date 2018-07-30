package com.kgeor.easytrim.Stepper;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kgeor.easytrim.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepperDetails extends Fragment implements Step {


    public StepperDetails() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_fragment_welcome, container, false);
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        // return null if the user can go to the next step,
        // create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        // update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        // handle error inside of fragment, e.g. show error on EditText
    }
}