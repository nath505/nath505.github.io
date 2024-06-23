package com.naterod.inventoryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class EditActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mItemNameText;
    private TextView mCountText;
    private EditText mAmountText;
    private Button mAddButton;
    private Button mRemoveButton;
    private String mItemName;
    private String mItemCount;
    private String mAmount;
    private boolean mRemoveSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ItemDatabase database = new ItemDatabase(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");

        // display toolbar on top of screen
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update item count");
        // add up button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Cursor cursor = database.searchItemById(id);

        cursor.moveToFirst();

        mItemName = cursor.getString(0);
        mItemCount = String.valueOf(cursor.getInt(1));

        // display the selected item name
        mItemNameText = findViewById(R.id.itemNameText);
        mItemNameText.setText(mItemName);

        // display current item count
        mCountText = findViewById(R.id.countText);
        mCountText.setText(mItemCount);

        // set listener for buttons
        mAddButton = findViewById(R.id.addButton);
        mRemoveButton = findViewById(R.id.removeButton);

        // user wants to add to current item count
        mAddButton.setOnClickListener(view -> {
            mAmountText = findViewById(R.id.amountText);
            mAmount = mAmountText.getText().toString();

            if (mAmount.isEmpty()) {
                mAmountText.setHint("Please enter an amount");
            }
            else {
                try {
                    database.addAmount(mItemName, Integer.parseInt(mAmount));

                    setResult(RESULT_OK);
                    finish();
                } catch (NumberFormatException e) {
                    mAmountText.getText().clear();
                    mAmountText.setHint("Number is too large");
                }
            }
        });

        // user wants to remove from current item count
        mRemoveButton.setOnClickListener(view -> {
            mAmountText = findViewById(R.id.amountText);
            mAmount = mAmountText.getText().toString();

            if (mAmount.isEmpty()) {
                mAmountText.setHint("Please enter an amount");
            }
            else {
                try {
                    mRemoveSuccess = database.removeAmount(mItemName, Integer.parseInt(mAmount));

                    if (!mRemoveSuccess) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // user pressed ok
                            }
                        };

                        new AlertDialog.Builder(EditActivity.this)
                                .setTitle("Error")
                                .setMessage("Item count cannot be less than zero")
                                .setPositiveButton("OK", onClickListener)
                                .create()
                                .show();
                    }
                    else {
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (NumberFormatException e) {
                    mAmountText.getText().clear();
                    mAmountText.setHint("Number is too large");
                }
            }
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