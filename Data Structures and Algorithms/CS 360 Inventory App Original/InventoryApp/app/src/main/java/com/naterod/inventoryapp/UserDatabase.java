package com.naterod.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int VERSION = 1;

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "username";
        private static final String COL_PASS = "password";
        private static final String COL_PHONE = "phone_num";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserDatabase.UserTable.COL_ID + " integer primary key autoincrement, " +
                UserDatabase.UserTable.COL_NAME + " text, " +
                UserDatabase.UserTable.COL_PASS + " text, " +
                UserDatabase.UserTable.COL_PHONE + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        onCreate(db);
    }

    @SuppressLint("Range")
    public List<String> searchUser(String user) {
        SQLiteDatabase db = getReadableDatabase();
        List<String> result = null;

        Cursor cursor = db.query(
                "users",
                new String[] {"username, password, phone_num"},
                "username like ?",
                new String[] {user},
                null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            result = new ArrayList<>();

            result.add(cursor.getString(cursor.getColumnIndex("username")));
            result.add(cursor.getString(cursor.getColumnIndex("password")));
            result.add(cursor.getString(cursor.getColumnIndex("phone_num")));

            cursor.close();
        }

        return result;
    }

    public void addUser(String user, String pass) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_NAME, user);
        values.put(UserTable.COL_PASS, pass);

        db.insert(UserTable.TABLE, null, values);
    }

    public void addPhoneNumber(String userName, String userPhone) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_PHONE, userPhone);

        String[] name = new String[]{userName};
        db.update(UserTable.TABLE, values, "username = ?", name);
    }
}
