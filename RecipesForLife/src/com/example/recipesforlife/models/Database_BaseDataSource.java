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
public abstract class Database_BaseDataSource {
	protected SQLiteDatabase database;
	public Database_DatabaseConnection dbHelper;



	protected Database_BaseDataSource(Context context) {
		dbHelper = new Database_DatabaseConnection(context);
	}

	public void close() {
		dbHelper.close();
	}

	protected int getIndex(String columnName, Cursor cursor) {
		int controlIndex = cursor.getColumnIndex(columnName);
		return controlIndex;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
}
