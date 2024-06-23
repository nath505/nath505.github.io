package com.naterod.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private TextView mGreeting;
    private EditText mUserText;
    private EditText mPassText;
    private Button mLoginButton;
    private Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserText = findViewById(R.id.username);
        mPassText = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.loginButton);
        mSignupButton = findViewById(R.id.signupButton);

        // create new database for users if it doesn't exist,
        // otherwise get existing one
        UserDatabase database = new UserDatabase(getApplicationContext());

        // set listener for login button
        mLoginButton.setOnClickListener(view -> {
            boolean loginSuccess = false;
            String user = mUserText.getText().toString();
            String pass = mPassText.getText().toString();
            List<String> result;

            mGreeting = findViewById(R.id.textGreeting);

            if (user.isEmpty() || pass.isEmpty()) {
                mGreeting.setText(R.string.user_warning1);
                mGreeting.setTextColor(Color.RED);

                return;
            }

            result = database.searchUser(user);

            if (result != null && user.equals(result.get(0))
                    && pass.equals(result.get(1))) {
                loginSuccess = true;
            }
            else {
                mGreeting.setText(R.string.user_warning2);
                mGreeting.setTextColor(Color.RED);
            }

            if (loginSuccess) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("name", result.get(0));
                startActivity(intent);
            }
        });

        // set listener for signup button
        mSignupButton.setOnClickListener(view -> {
            String user = mUserText.getText().toString();
            String pass = mPassText.getText().toString();
            List<String> result;

            mGreeting = findViewById(R.id.textGreeting);

            if (user.isEmpty() || pass.isEmpty()) {
                mGreeting.setText(R.string.user_warning1);
                mGreeting.setTextColor(Color.RED);

                return;
            }

            result = database.searchUser(user);

            if (result != null && user.equals(result.get(0))) {
                mGreeting.setText(R.string.user_warning3);
                mGreeting.setTextColor(Color.RED);
            }
            else {
                database.addUser(user, pass);
                mGreeting.setText(R.string.user_added_text);
                mGreeting.setTextColor(Color.GREEN);
            }
        });
    }
}