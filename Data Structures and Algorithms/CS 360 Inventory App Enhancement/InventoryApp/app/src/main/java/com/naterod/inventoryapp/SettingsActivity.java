package com.naterod.inventoryapp;

import static android.content.DialogInterface.BUTTON_POSITIVE;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private String mUserName;

    private final int REQUEST_SEND_SMS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle bundle = getIntent().getExtras();
        mUserName = bundle.getString("name");

        // display toolbar on top of screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        // add up button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Button smsButton = findViewById(R.id.smsButton);
        Button deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // set listener for SMS button
        smsButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smsButtonClick(v);
                }
        });

        // set listener for delete account button
        deleteAccountButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
                        if (which == BUTTON_POSITIVE) {
                            // user pressed ok
                            UserDatabase userDatabase = new UserDatabase(getApplicationContext());
                            userDatabase.deleteUser(mUserName);

                            Toast.makeText(SettingsActivity.this, "Account deleted successfully",
                                    Toast.LENGTH_LONG).show();

                            // clear stack and go back to login screen
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    };

                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Delete your account")
                            .setMessage("This will delete your account with the app!")
                            .setNegativeButton("CANCEL", onClickListener)
                            .setPositiveButton("OK", onClickListener)
                            .create()
                            .show();
                }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                Toast.makeText(this, "SMS permission has been granted", Toast.LENGTH_LONG).show();

                // go to phone number screen
                Intent intent = new Intent(SettingsActivity.this, PhoneActivity.class);
                intent.putExtra("name", mUserName);
                startActivity(intent);
            }
        }
    }

    public void smsButtonClick(View view) {
        // check if SMS send permission is granted
        // if not, user will be given option to
        if (Permissions.hasPermissions(this, Manifest.permission.SEND_SMS,
                R.string.send_sms_rationale, REQUEST_SEND_SMS)) {
            Toast.makeText(this, "SMS permission already granted", Toast.LENGTH_LONG).show();
        }
    }
}