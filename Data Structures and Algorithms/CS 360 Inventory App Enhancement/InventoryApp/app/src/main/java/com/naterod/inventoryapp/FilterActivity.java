package com.naterod.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // display toolbar on top of screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Search by filter");
        // add up button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // populate each spinner with an array of values
        Spinner spinner1 = findViewById(R.id.sortTypeSpinner);
        Spinner spinner2 = findViewById(R.id.sortOrderSpinner);
        // get values from edit text boxes
        EditText minEdit = findViewById(R.id.minEdit);
        EditText maxEdit = findViewById(R.id.maxEdit);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.sortTypeArray,
                android.R.layout.simple_spinner_item
        );
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.sortOrderArray,
                android.R.layout.simple_spinner_item
        );

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);

        // set the selected spinner values based on saved user filters
        List<String> filters = getIntent().getStringArrayListExtra("filters");

        if (filters != null) {
            if (filters.get(0).equals("Alphabetical")) {
                spinner1.setSelection(1);
            } else if (filters.get(0).equals("Quantity")) {
                spinner1.setSelection(2);
            }
            if (filters.get(1).equals("Ascending")) {
                spinner2.setSelection(1);
            } else if (filters.get(1).equals("Descending")) {
                spinner2.setSelection(2);
            }
            if (!filters.get(2).equals("")) {
                minEdit.setText(filters.get(2));
            }
            if (!filters.get(3).equals("")) {
                maxEdit.setText(filters.get(3));
            }
        }

        Button saveButton = findViewById(R.id.saveButton);
        Button resetButton = findViewById(R.id.resetButton);

        // set listener for save button
        saveButton.setOnClickListener(v -> {
            // get values from the filters
            String spinner1Value = spinner1.getSelectedItem().toString();
            String spinner2Value = spinner2.getSelectedItem().toString();
            String minValue = minEdit.getText().toString();
            String maxValue = maxEdit.getText().toString();

            ArrayList<String> filterList = new ArrayList<>();

            // go through all the filters and store them in list
            filterList.add(spinner1Value);
            filterList.add(spinner2Value);
            filterList.add(minValue);
            filterList.add(maxValue);

            // send the list back to MainActivity
            Intent intent = new Intent(FilterActivity.this, MainActivity.class);
            intent.putStringArrayListExtra("filters", filterList);

            setResult(RESULT_OK, intent);
            finish();
        });

        // set listener for reset button
        resetButton.setOnClickListener(v -> {
            // reset all the filter values to default
            spinner1.setSelection(0);
            spinner2.setSelection(0);
            minEdit.getText().clear();
            maxEdit.getText().clear();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}