/*
 * Nathaniel Rodriguez
 * Southern New Hampshire University
 * CS-360: Mobile Architecture and Programming
 * December 15, 2023
 */

package com.naterod.inventoryapp;

import static android.content.DialogInterface.BUTTON_POSITIVE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mLinearLayout;
    private Toolbar mToolbar;
    private ImageView mDeleteAllIcon;
    private ImageView mSyncIcon;
    private Button mSmsButton;
    private Button mAddItemButton;
    private String mUserName;
    private final int REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        mUserName = bundle.getString("name");

        // display toolbar on top of screen
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // set listener for delete all icon
        mDeleteAllIcon = findViewById(R.id.deleteAllIcon);

        mDeleteAllIcon.setOnClickListener(view -> {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == BUTTON_POSITIVE) {
                        // user pressed ok
                        ItemDatabase database = new ItemDatabase(getApplicationContext());
                        database.deleteAll();

                        refreshList(false);
                    }
                }
            };

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete all items")
                    .setMessage("This will delete everything stored in the inventory!")
                    .setNegativeButton("CANCEL", onClickListener)
                    .setPositiveButton("OK", onClickListener)
                    .create()
                    .show();
        });

        // set listener for sync icon
        mSyncIcon = findViewById(R.id.syncIcon);

        mSyncIcon.setOnClickListener(view -> {
            refreshList(true);
        });

        mAddItemButton = findViewById(R.id.addItemButton);
        mSmsButton = findViewById(R.id.smsButton);

        // set listener for add item button
        mAddItemButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                @SuppressWarnings("deprecation")
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivityForResult(intent, 0);
                }
        });

        // set listener for SMS button
        mSmsButton.setOnClickListener(view -> {
            smsButtonClick(view);
        });
    }

    public void smsButtonClick(View view) {
        // check if SMS send permission is granted
        // if not, user will be given option to
        if (Permissions.hasPermissions(this, Manifest.permission.SEND_SMS,
                R.string.send_sms_rationale, REQUEST_SEND_SMS)) {
            Toast.makeText(this, "SMS permission already granted", Toast.LENGTH_LONG).show();
        }
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
                Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                intent.putExtra("name", mUserName);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            refreshList(false);
        }
    }

    public void refreshList(boolean manualSync) {
        ItemDatabase itemDatabase = new ItemDatabase(getApplicationContext());
        UserDatabase userDatabase = new UserDatabase(getApplicationContext());

        mLinearLayout = findViewById(R.id.itemList);
        // read the current list of items from database
        List<String> itemList = itemDatabase.getListItems();

        // remove everything from view first
        mLinearLayout.removeAllViews();

        // show a greeting if there are no items to display
        if (itemList.size() > 0) {
            findViewById(R.id.emptyListText).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.emptyListText).setVisibility(View.VISIBLE);
        }

        /* iterate through list of items to display in scrollview */

        // each item has its own row with name and count, as well as
        // edit and delete icons
        String itemName;
        String itemCount;

        int entry = -1;

        for (int i = 0; i < itemList.size() - 1; i+=2) {
            itemName = itemList.get(++entry);
            itemCount = itemList.get(++entry);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.row_layout, mLinearLayout, false);

            ((TextView) view.findViewById(R.id.listItemLabel)).setText(itemName);
            ((TextView) view.findViewById(R.id.itemCountLabel)).setText(itemCount);

            // when the list is manually refreshed, if the item's stock
            // is zero then send a new SMS to alert the user
            if (Integer.parseInt(itemCount) == 0 && manualSync) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    String phoneNum = userDatabase.searchUser(mUserName).get(2);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNum, null,
                            "Currently out of stock: " + itemName, null, null);

                    Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();
                }
            }

            // each new row has a unique id
            view.setId(i);

            // set listener for row's edit icon
            view.findViewById(R.id.editIcon).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        @SuppressWarnings("deprecation")
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, EditActivity.class);

                            Cursor cursor = itemDatabase.searchItem(((TextView) findViewById(view.getId())
                                    .findViewById(R.id.listItemLabel)).getText().toString());

                            cursor.moveToFirst();

                            intent.putExtra("id", cursor.getInt(0));

                            startActivityForResult(intent, 1);
                        }
                }
            );

            // set listener for row's delete icon
            view.findViewById(R.id.deleteIcon).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemDatabase.deleteItem(((TextView) findViewById(view.getId())
                                    .findViewById(R.id.listItemLabel)).getText().toString());

                            view.setVisibility(View.GONE);
                        }
                    }
            );

            mLinearLayout.addView(view);
        }
    }
}