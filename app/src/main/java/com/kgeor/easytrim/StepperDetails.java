package com.kgeor.easytrim;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;


/**
 * Fragment responsible for taking in user input of necessary details for calculation
 * A simple {@link Fragment} subclass.
 */
public class StepperDetails extends Fragment
        implements Step, BlockingStep, AdapterView.OnItemSelectedListener {

    // FIELDS //
    public static EditText maxSpeedInput;
    private SharedPreferences sharedPref, sharedPreferences;
    public static String weatherCondition = "Sunny";

    // CONSTRUCTOR //
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

        // SETUP SPINNERS //
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

        // LINK EDIT TEXT //
        maxSpeedInput = getActivity().findViewById(R.id.max_speed_input);
    }

    /**
     * Method responsible for getting the maximum speed desired by the user
     * and then setting the maxSpeedValue from that input
     *
     * @return the max speed value the user set
     */
    public static int getMaxSpeedInput() {
        String value = maxSpeedInput.getText().toString();
        int maxSpeedValue = Integer.parseInt(value);
        System.out.println("MAX SPEED: " + maxSpeedValue);
        return maxSpeedValue;
    }

    /**
     * @return null so the user can go tot he next step
     */
    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {
    }

    /**
     * Method responsible for updating the measurement units based on what the user selects in the spinner
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (parent.getItemAtPosition(position).toString()) {
            case "Nautical Miles":
                editor.putString("units_list", "NM");
                break;
            case "Kilometers":
                editor.putString("units_list", "KM");
                break;
            case "Miles":
                editor.putString("units_list", "MI");
                break;
            case "Meters":
                editor.putString("units_list", "ME");
                break;
        }
        editor.apply();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor conditionsEditor = sharedPref.edit();

        switch (parent.getItemAtPosition(position).toString()) {
            case "Sunny":
                conditionsEditor.putString("CONDITION", "SUNNY");
                weatherCondition = "Sunny";
                // System.out.println("Sunny Condition Added");
                break;
            case "Light Wind":
                conditionsEditor.putString("CONDITION", "WIND_LIGHT");
                weatherCondition = "Light Wind";
                // System.out.println("Light Wind Condition Added");
                break;
            case "Strong Wind":
                conditionsEditor.putString("CONDITION", "WIND_STRONG");
                weatherCondition = "Strong Wind";
                // System.out.println("Strong Wind Condition Added");
                break;
            case "Storm":
                conditionsEditor.putString("CONDITION", "STORM");
                weatherCondition = "Storm";
                // System.out.println("Storm Condition Added");
                break;
        }
        conditionsEditor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        // Sets a requirement for a max speed to be set before the next step can be proceeded to
        if (TextUtils.isEmpty(maxSpeedInput.getText())) {
            Toast.makeText(this.getActivity(), "Max Speed Required!", Toast.LENGTH_SHORT).show();
        } else {
            getMaxSpeedInput();
            callback.goToNextStep();
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }
}
