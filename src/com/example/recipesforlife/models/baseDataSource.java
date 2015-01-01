package com.example.recipesforlife.models;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public abstract class baseDataSource {
    protected SQLiteDatabase database;
    protected databaseConnection dbHelper;



    protected baseDataSource(Context context) {
        dbHelper = new databaseConnection(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    protected int getIndex(String columnName, Cursor cursor) {
        int controlIndex = cursor.getColumnIndex(columnName);
        return controlIndex;
    }
}
