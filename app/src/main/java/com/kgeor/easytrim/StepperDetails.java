package com.kgeor.easytrim;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepperDetails extends Fragment implements Step, BlockingStep, AdapterView.OnItemSelectedListener {
    public static EditText maxSpeedInput;
    private SharedPreferences sharedPref;
    private String unitsPref;

    public StepperDetails() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stepper_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Spinner unitsSpinner = getActivity().findViewById(R.id.units_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.details_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(adapter);
        unitsSpinner.setOnItemSelectedListener(this);

        Spinner conditionsSpinner = getActivity().findViewById(R.id.weather_condition);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(), R.array.conditions, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionsSpinner.setAdapter(adapter1);
        conditionsSpinner.setOnItemSelectedListener(this);

        maxSpeedInput = getActivity().findViewById(R.id.max_speed_input);






    }

    public static int getMaxSpeedInput() {
        String value = maxSpeedInput.getText().toString();
        int maxSpeedValue = Integer.parseInt(value);
        System.out.println("MAX SPEED: " + maxSpeedValue);
        return maxSpeedValue;
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        if (parent.getItemAtPosition(position).toString().equals("Nautical Miles")) {
            editor.putString("units_list", "NM");
        } else if (parent.getItemAtPosition(position).toString().equals("Kilometers")) {
            editor.putString("units_list", "KM");
        } else if (parent.getItemAtPosition(position).toString().equals("Miles")) {
            editor.putString("units_list", "MI");
        } else if (parent.getItemAtPosition(position).toString().equals("Meters")) {
            editor.putString("units_list", "ME");
        }
        editor.apply();



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        getMaxSpeedInput();
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }
}
