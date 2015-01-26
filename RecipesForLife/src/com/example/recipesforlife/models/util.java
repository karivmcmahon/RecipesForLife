package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class util {
	
	Context context;
	Typeface typeFace;
	Activity activity;
	public util(Context context, Activity activity)
	{
		this.activity = activity;
		// TODO Auto-generated constructor st
		this.context = context;
		typeFace=Typeface.createFromAsset(context.getAssets(),"fonts/elsie.ttf");
	}
	/**
	 * Checks if there is internet connection
	 * @param context
	 * @return if there is return true else return false
	 */
	public boolean checkInternetConnection(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
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
	 * Set custom text for the dialog
	 * @param resource
	 * @param dialog
	 * @param fontSize
	 */
	public void setDialogText(int resource, Dialog dialog, int fontSize)
	{
		//Style for activity
		//typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
		TextView view = (TextView)	 dialog.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#FFFFFFFF"));
	}
	
	/**
	 * Set custom text
	 * @param resource
	 * @param fontSize
	 */
	public void setText(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#FFFFFFFF"));
	}
	
	/**
	 * Set custom text
	 * @param resource
	 * @param fontSize
	 */
	public void setTextPink(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#F3216C"));
	}
	
	public void setTextBlack(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		//view.setTextColor(Color.parseColor("#F3216C"));
	}
	
	public void setTextBlackItalic(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		//view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTypeface(typeFace, Typeface.ITALIC);
		//view.setTextColor(Color.parseColor("#F3216C"));
	}
	
	/**
	 * Set custom text for button
	 * @param resource
	 * @param fontSize
	 */
	public void setButtonText(int resource, int fontSize)
	{
		Button  button = (Button) activity.findViewById(resource);
		button.setTypeface(typeFace);
		button.setTextSize(fontSize);
		button.setTextColor(Color.parseColor("#FFFFFFFF"));
	}
	
	

}
