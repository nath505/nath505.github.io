package com.naterod.inventoryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.widget.Button;

import android.os.Bundle;
import android.widget.EditText;

import java.util.Objects;

public class AddActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mItemName;
    private EditText mItemQuantity;
    private Button mConfirmButton;
    private Button mGoBackButton;
    private boolean mAddedSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mItemName = findViewById(R.id.itemName);
        mItemQuantity = findViewById(R.id.itemQuantity);
        mConfirmButton = findViewById(R.id.confirmButton);
        mGoBackButton = findViewById(R.id.goBackButton);

        // display toolbar on top of screen
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add new item");

        mConfirmButton.setOnClickListener(view -> {
            ItemDatabase database = new ItemDatabase(getApplicationContext());

            String name = mItemName.getText().toString();
            String quantity = mItemQuantity.getText().toString();

            if (name.isEmpty() || quantity.isEmpty()) {
                if (name.isEmpty()) {
                    mItemName.setHint("Field cannot be empty");
                }
                if (quantity.isEmpty()) {
                    mItemQuantity.setHint("Field cannot be empty");
                }
            }
            else {
                try {
                    if (Integer.parseInt(quantity) < 1) {
                        mItemQuantity.getText().clear();
                        mItemQuantity.setHint("Minimum quantity is one");
                    }
                    else {
                        mAddedSuccess = database.addNewItem(name, Integer.parseInt(quantity));

                        if (!mAddedSuccess) {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // user pressed ok
                                }
                            };

                            new AlertDialog.Builder(AddActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Item already exists in inventory")
                                    .setPositiveButton("OK", onClickListener)
                                    .create()
                                    .show();
                        }
                        else {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                } catch (NumberFormatException e) {
                    mItemQuantity.getText().clear();
                    mItemQuantity.setHint("Number is too large");
                }
            }
        });

        mGoBackButton.setOnClickListener(view -> {
            finish();
        });
    }
}