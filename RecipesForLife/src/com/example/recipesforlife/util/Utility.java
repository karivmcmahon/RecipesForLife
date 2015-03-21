package com.example.recipesforlife.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Utility class handling common methods for multiple classes
 * @author Kari
 *
 */
public class Utility  {

	public Utility()
	{


	}

	/**
	/**
	 * Convert date to string
	 * @param date
	 * @return string with date
	 */
	@SuppressLint("SimpleDateFormat")
	public String dateToString(Date date, boolean inappstring) {
		SimpleDateFormat formatter;

		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		String currentDate = formatter.format(date);
		return currentDate;
	}



	/**
	 * Get current date
	 */
	public String getLastUpdated(boolean appstring)
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		Date today = cal.getTime();
		String lastUpdated = dateToString(today, appstring);
		return lastUpdated;
	}



	/**
	 * Generates UUID then adds the name and type of table - to create a more detailed unique id
	 * @param addedBy
	 * @param table
	 * @return
	 */
	public String generateUUID(String addedBy, String table, SQLiteDatabase database ) {
		//   final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		final String uuid = UUID.randomUUID().toString();
		String uniqueid = addedBy + table + uuid;
		boolean exists = selectUUID(table, uniqueid,database);
		if(exists == true)
		{
			selectUUID(table, uniqueid,database);
		}
		return uniqueid;
	}

	/**
	 * Checks if unique id exists - if so create another one
	 * @param table
	 * @param uuid
	 * @return
	 */
	public boolean selectUUID(String table, String uuid, SQLiteDatabase database )
	{		
		Cursor cursor = database.rawQuery("SELECT uniqueid FROM " + table + " WHERE uniqueid=?", new String[] { uuid});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				return true;

			}
		}
		cursor.close();
		return false;
	}

}
