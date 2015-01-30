package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class utility {
	
	public utility()
	{
		
	}
	
	/**
	 * Convert date to string
	 * @param date
	 * @return string with date
	 */
	public String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	
	/**
	 * Get current date
	 */
	public String getLastUpdated()
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        String lastUpdated = dateToString(today);
        return lastUpdated;
	}

}
