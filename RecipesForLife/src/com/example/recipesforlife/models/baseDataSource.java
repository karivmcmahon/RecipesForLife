package com.example.recipesforlife.models;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Base class that relates to all model classes handling  database work
 * @author Kari
 *
 */
public abstract class baseDataSource {
    protected SQLiteDatabase database;
    public databaseConnection dbHelper;



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
