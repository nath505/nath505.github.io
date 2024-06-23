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
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String mUserName;
    private SearchView mSearchView; // search box on top of page
    private ArrayList<String> mFilterList = null; // search filters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mUserName = bundle.getString("name");
        }

        // display toolbar on top of screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addItemButton = findViewById(R.id.addItemButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button filterButton = findViewById(R.id.filterButton);

        // set listener for add item button
        addItemButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                @SuppressWarnings("deprecation")
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivityForResult(intent, 0);
                }
        });

        // set listener for settings button
        settingsButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    intent.putExtra("name", mUserName);
                    startActivity(intent);
                }
        });

        // set listener for filter button
        filterButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                @SuppressWarnings("deprecation")
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                    intent.putStringArrayListExtra("filters", mFilterList); // add current filters
                    startActivityForResult(intent, 1);
                }
        });

        // enable search functionality
        mSearchView = findViewById(R.id.searchBox);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);

        // set listener for query that is typed into search box
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ItemDatabase itemDatabase = new ItemDatabase(getApplicationContext());
                String query = mSearchView.getQuery().toString();

                queryDatabase(itemDatabase, query, true);

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // user chose to sync database
        if (id == R.id.syncItem) {
            refreshList(true);
        // user chose to delete everything in database
        } else if (id == R.id.deleteAllItem) {
            DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
                if (which == BUTTON_POSITIVE) {
                    // user pressed ok
                    try (ItemDatabase database = new ItemDatabase(getApplicationContext())) {
                        database.deleteAll();

                        refreshList(false);
                    } catch (Exception e) {
                        e.printStackTrace();
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
        // user chose to log out
        } else if (id == R.id.logoutItem) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    refreshList(false);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    ItemDatabase itemDatabase = new ItemDatabase(getApplicationContext());
                    mFilterList = data.getStringArrayListExtra("filters");

                    String query = mSearchView.getQuery().toString();

                    queryDatabase(itemDatabase, query, true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // if the user performs a search
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            ItemDatabase itemDatabase = new ItemDatabase(getApplicationContext());
            // retrieve the query
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (!queryDatabase(itemDatabase, query, true)) {
                Toast.makeText(this, "No items were found", Toast.LENGTH_LONG).show();
            }

            mSearchView.clearFocus();
        }
    }

    public boolean queryDatabase(ItemDatabase itemDatabase, String query,
                                 boolean isPartial) {
        List<String> itemList = new ArrayList<>();
        boolean itemFound = false;

        // query the database
        Cursor cursor = itemDatabase.searchItemByName(query, isPartial);

        // if the query returned results, loop through all of them
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                String quantity = cursor.getString(2);

                itemList.add(name);
                itemList.add(quantity);
            } while (cursor.moveToNext());

            if (mFilterList != null) {
                // call function to apply filters to search
                itemList = applyFilters(itemList);
            }

            itemFound = true;
        }

        // call function to fill the scrollview
        populateList(itemList, itemDatabase, false);

        return itemFound;
    }

    public void setRowIconListeners(View view, ItemDatabase itemDatabase) {
        // set listener for row's edit icon
        view.findViewById(R.id.editIcon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void onClick(View v) {
                        String item = ((TextView) findViewById(view.getId())
                                .findViewById(R.id.listItemLabel)).getText().toString();
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);

                        // query database for editing item
                        Cursor cursor = itemDatabase.searchItemByName(item, false);

                        cursor.moveToFirst();

                        intent.putExtra("id", cursor.getInt(0));

                        startActivityForResult(intent, 0);
                    }
                }
        );

        // set listener for row's delete icon
        view.findViewById(R.id.deleteIcon).setOnClickListener(
                v -> {
                    String item = ((TextView) findViewById(view.getId())
                            .findViewById(R.id.listItemLabel)).getText().toString();

                    // delete from database
                    itemDatabase.deleteItem(item);
                    // remove from list
                    view.setVisibility(View.GONE);
                }
        );
    }

    public void checkIfLowStock(String itemName, String itemCount) {
        try (UserDatabase userDatabase = new UserDatabase(getApplicationContext())) {
            // when the list is manually refreshed, if the item's stock
            // is zero then send a new SMS to alert the user
            if (Integer.parseInt(itemCount) == 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    String phoneNum = userDatabase.searchUser(mUserName).get(2);

                    if (phoneNum != null) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNum, null,
                                "Currently out of stock: " + itemName, null, null);

                        Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateList(List <String> itemList, ItemDatabase itemDatabase,
                             boolean manualSync) {
        LinearLayout linearLayout = findViewById(R.id.itemList);

        // remove everything from view first
        linearLayout.removeAllViews();

        // show a greeting if there are no items to display
        if (itemList.size() > 0) {
            findViewById(R.id.emptyListText).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.emptyListText).setVisibility(View.VISIBLE);

            return;
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
            View view = inflater.inflate(R.layout.row_layout, linearLayout, false);

            ((TextView) view.findViewById(R.id.listItemLabel)).setText(itemName);
            ((TextView) view.findViewById(R.id.itemCountLabel)).setText(itemCount);

            if (manualSync) {
                checkIfLowStock(itemName, itemCount);
            }

            // each new row has a unique id
            view.setId(i);

            // call function to set listeners for edit and delete icons
            setRowIconListeners(view, itemDatabase);

            linearLayout.addView(view);
        }
    }

    public void refreshList(boolean manualSync) {
        ItemDatabase itemDatabase = new ItemDatabase(getApplicationContext());

        // read the current list of items from database
        // and apply filters
        List<String> itemList = itemDatabase.getListItems();
        if (mFilterList != null) {
            itemList = applyFilters(itemList);
        }

        // call function to fill the scrollview
        populateList(itemList, itemDatabase, manualSync);
    }

    public List<String> applyFilters(List<String> itemList) {
        // use filters to set sort type, order and range values
        String sortType = mFilterList.get(0);
        String sortOrder = mFilterList.get(1);
        String minValue = mFilterList.get(2);
        String maxValue = mFilterList.get(3);

        int size = itemList.size();

        // declare lists for sorting
        List<List<String>> sortedList = new ArrayList<>();
        List<String> temp;

        // divide list to be sorted into names and counts
        int entry = -1;

        for (int i = 0; i < size - 1; i+=2) {
            temp = new ArrayList<>();

            temp.add(itemList.get(++entry));
            temp.add(itemList.get(++entry));

            sortedList.add(temp);
        }

        /* perform any necessary sorting before displaying results */

        // sort by item name
        if (sortType.equals("Alphabetical")) {
            if (sortOrder.equals("Ascending")) {
                sortedList.sort(Comparator.comparing(x -> x.get(0)));
            }
            else if (sortOrder.equals("Descending")) {
                sortedList.sort(Comparator.comparing(x -> x.get(0), Collections.reverseOrder()));
            }
        // sort by item quantity
        } else if (sortType.equals("Quantity")) {
            if (sortOrder.equals("Ascending")) {
                sortedList.sort(Comparator.comparing(x -> Integer.parseInt(x.get(1))));
            }
            else if (sortOrder.equals("Descending")) {
                sortedList.sort(Comparator.comparing(x -> Integer.parseInt(x.get(1)), Collections.reverseOrder()));
            }
        }
        // get range values for sorting, if any
        if (!minValue.equals("")) {
            sortedList.removeIf(list -> Integer.parseInt(list.get(1)) < Integer.parseInt(minValue));
            size -= (size - (sortedList.size() * 2));
        }
        if (!maxValue.equals("")) {
            sortedList.removeIf(list -> Integer.parseInt(list.get(1)) > Integer.parseInt(maxValue));
            size -= (size - (sortedList.size() * 2));
        }

        // clear the original list for newly sorted values
        itemList.clear();
        // reset counter
        entry = 0;

        // rebuild the list with sorted values
        for (int i = 0; i < size - 1; i+=2) {
            itemList.add(sortedList.get(entry).get(0));
            itemList.add(sortedList.get(entry).get(1));

            ++entry;
        }

        // return the sorted list to display in scrollview
        return itemList;
    }
}