package com.example.converterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    Spinner categorySpinner, sourceSpinner, destinationSpinner;
    EditText inputValue;
    Button convertButton;
    TextView resultView;

    Map<String, Double> lengthToCm = new HashMap<>();
    Map<String, Double> weightToKg = new HashMap<>();

    final String[] lengthUnits = {"inch", "foot", "yard", "mile", "cm", "km"};
    final String[] weightUnits = {"pound", "ounce", "ton", "kg", "g"};
    final String[] tempUnits = {"Celsius", "Fahrenheit", "Kelvin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner);
        sourceSpinner = findViewById(R.id.sourceSpinner);
        destinationSpinner = findViewById(R.id.destinationSpinner);
        inputValue = findViewById(R.id.inputValue);
        convertButton = findViewById(R.id.convertButton);
        resultView = findViewById(R.id.resultView);

        // Set up conversion maps
        setupConversionMaps();

        // Set up category spinner
        String[] categories = {"Select conversion type", "Length", "Weight", "Temperature"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(0); // hint default

        // Set category spinner listener
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(categories[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set convert button listener
        convertButton.setOnClickListener(v -> {
            String category = categorySpinner.getSelectedItem().toString();
            String sourceUnit = sourceSpinner.getSelectedItem().toString();
            String destUnit = destinationSpinner.getSelectedItem().toString();
            String input = inputValue.getText().toString();

            if (input.isEmpty()) {
                resultView.setText("Please enter a value.");
                return;
            }

            double value = Double.parseDouble(input);
            double result = convert(category, sourceUnit, destUnit, value);

            DecimalFormat df = new DecimalFormat("#.###");
            String formattedResult = df.format(result);
            resultView.setText(formattedResult + " " + destUnit + "s");
        });
    }

    // Update unit spinners based on selected category
    void updateUnitSpinners(String category) {
        ArrayAdapter<String> adapter;

        switch (category) {
            case "Length":
                adapter = new ArrayAdapter<>(this, R.layout.spinner_item, lengthUnits);
                break;
            case "Weight":
                adapter = new ArrayAdapter<>(this, R.layout.spinner_item, weightUnits);
                break;
            case "Temperature":
                adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempUnits);
                break;
            default:
                adapter = new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{});
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(adapter);
        destinationSpinner.setAdapter(adapter);
        sourceSpinner.setSelection(0);
        destinationSpinner.setSelection(0);
    }

    //Conversion maps for Length & Weight
    void setupConversionMaps() {
        lengthToCm.put("inch", 2.54);
        lengthToCm.put("foot", 30.48);
        lengthToCm.put("yard", 91.44);
        lengthToCm.put("mile", 160934.0);
        lengthToCm.put("cm", 1.0);
        lengthToCm.put("km", 100000.0);

        weightToKg.put("pound", 0.453592);
        weightToKg.put("ounce", 0.0283495);
        weightToKg.put("ton", 907.185);
        weightToKg.put("kg", 1.0);
        weightToKg.put("g", 0.001);
    }

    //Conversion logic for all the different categories
    double convert(String category, String source, String dest, double value) {
        switch (category) {
            case "Length":
                double cm = value * lengthToCm.get(source);
                return cm / lengthToCm.get(dest);

            case "Weight":
                double kg = value * weightToKg.get(source);
                return kg / weightToKg.get(dest);

            case "Temperature":
                return convertTemperature(source, dest, value);
        }
        return 0;
    }

    //Conversion logic temperature
    double convertTemperature(String source, String dest, double value) {
        if (source.equals(dest)) return value;

        switch (source) {
            case "Celsius":
                if (dest.equals("Fahrenheit")) return (value * 1.8) + 32;
                if (dest.equals("Kelvin")) return value + 273.15;
                break;
            case "Fahrenheit":
                if (dest.equals("Celsius")) return (value - 32) / 1.8;
                if (dest.equals("Kelvin")) return ((value - 32) / 1.8) + 273.15;
                break;
            case "Kelvin":
                if (dest.equals("Celsius")) return value - 273.15;
                if (dest.equals("Fahrenheit")) return ((value - 273.15) * 1.8) + 32;
                break;
        }

        return 0;
    }
}

