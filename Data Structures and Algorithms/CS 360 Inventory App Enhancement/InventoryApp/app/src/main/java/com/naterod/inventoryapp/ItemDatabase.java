package com.naterod.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "items.db";
    private static final int VERSION = 1;
    private final Context mContext;

    public ItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

        this.mContext = context;
    }

    private static final class ItemTable {
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_QUANT = "quantity";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ItemDatabase.ItemTable.TABLE + " (" +
                ItemDatabase.ItemTable.COL_ID + " integer primary key autoincrement, " +
                ItemDatabase.ItemTable.COL_NAME + " text, " +
                ItemDatabase.ItemTable.COL_QUANT + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ItemDatabase.ItemTable.TABLE);
        onCreate(db);
    }

    public void deleteAll() {
        mContext.deleteDatabase(DATABASE_NAME);
    }

    public Cursor searchItemByName(String itemName, boolean isPartial) {
        SQLiteDatabase db = getReadableDatabase();

        if (isPartial) {
            itemName = "%"+itemName+"%";
        }

        return db.query(
                "items",
                new String[] {"_id, name, quantity"},
                "name like ?",
                new String[] {itemName},
                null, null, null, null
        );
    }

    public Cursor searchItemById(int itemId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "items",
                new String[] {"name, quantity"},
                "_id like ?",
                new String[] {itemId+""},
                null, null, null, null
        );
    }

    @SuppressLint("Range")
    public List<String> getListItems() {
        SQLiteDatabase db = getReadableDatabase();

        List<String> itemList = new ArrayList<>();

        Cursor cursor = db.query(
                "items",
                new String[] {"name, quantity"},
                null, null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                itemList.add(cursor.getString(cursor.getColumnIndex("name")));
                itemList.add(cursor.getString(cursor.getColumnIndex("quantity")));
            } while(cursor.moveToNext());

            cursor.close();
        }

        return itemList;
    }

    public boolean addNewItem(String itemName, int itemQuantity) {
        SQLiteDatabase db = getWritableDatabase();

        boolean addedSuccess = false;

        ContentValues values = new ContentValues();
        values.put("name", itemName);
        values.put("quantity", itemQuantity);

        Cursor cursor = searchItemByName(itemName, false);

        // check if item already exists
        // if it doesn't then add it
        if (cursor == null || cursor.getCount() == 0) {
            db.insert("items", null, values);
            addedSuccess = true;
        }

        return addedSuccess;
    }

    @SuppressLint("Range")
    public void addAmount(String itemName, int itemAmount) {
        SQLiteDatabase db = getWritableDatabase();
        int newAmount = 0;

        Cursor cursor = searchItemByName(itemName, false);

        if (cursor != null && cursor.moveToFirst()) {
            newAmount = cursor.getInt(cursor.getColumnIndex("quantity")) + itemAmount;
        }

        ContentValues values = new ContentValues();
        values.put("quantity", newAmount);

        String[] name = new String[] {itemName};
        db.update("items", values, "name = ?", name);
    }

    @SuppressLint("Range")
    public boolean removeAmount(String itemName, int itemAmount) {
        SQLiteDatabase db = getWritableDatabase();
        int newAmount = 0;
        boolean removeSuccess = false;

        Cursor cursor = searchItemByName(itemName, false);

        if (cursor != null && cursor.moveToFirst()) {
            newAmount = cursor.getInt(cursor.getColumnIndex("quantity")) - itemAmount;
        }

        // check if item count is not less than zero
        // otherwise the remove operation will fail
        if (newAmount >= 0) {
            ContentValues values = new ContentValues();
            values.put("quantity", newAmount);

            String[] name = new String[]{itemName};
            db.update("items", values, "name = ?", name);

            removeSuccess = true;
        }

        return removeSuccess;
    }

    public void deleteItem(String itemName) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = searchItemByName(itemName, false);

        String[] name = new String[] {itemName};
        if (cursor != null && cursor.moveToFirst()) {
            db.delete("items", "name = ?", name);
        }
    }
}
