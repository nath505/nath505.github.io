package com.naterod.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class PhoneActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mPhoneNumber;
    private Button mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Bundle bundle = getIntent().getExtras();
        String userName = bundle.getString("name");

        // display toolbar on top of screen
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Enter phone number");

        // set listener for confirm button
        mConfirmButton = findViewById(R.id.confirmButton);

        mConfirmButton.setOnClickListener(
                v -> {
                    UserDatabase database = new UserDatabase(getApplicationContext());
                    mPhoneNumber = findViewById(R.id.phoneNumber);

                    String phoneNumber = mPhoneNumber.getText().toString();

                    if (phoneNumber.isEmpty()) {
                        mPhoneNumber.getText().clear();
                        mPhoneNumber.setHint("Field cannot be empty");
                    }
                    else if (phoneNumber.length() != 10) {
                        mPhoneNumber.getText().clear();
                        mPhoneNumber.setHint("Must be exactly 10 digits");
                    }
                    else {
                        database.addPhoneNumber(userName, phoneNumber);
                        finish();
                    }
                }
        );
    }
}