package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import com.example.recipesforlife.R;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class util {
	
	Context context;
	Typeface typeFace;
	Activity activity;
	public static final String MyPREFERENCES = "MyPrefs";
	private SharedPreferences sharedpreferences;
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
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
	 * Get current date
	 */
	private String getLastUpdated()
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        String lastUpdated = dateToString(today);
        return lastUpdated;
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
	 * Get text
	 * @param resource
	 * @param fontSize
	 */
	public String getText(int resource )
	{
		EditText edit = (EditText) activity.findViewById(resource);
		String text = edit.getText().toString();
		return text;
	}
	
	/**
	 * Get text from Dialog
	 * @param resource
	 * @param fontSize
	 */
	public String getTextFromDialog(int resource, Dialog dialog )
	{
		EditText edit = (EditText) dialog.findViewById(resource);
		String text = edit.getText().toString();
		return text;
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
	
	public void sync()
	{
	 if(checkInternetConnection(context))
		{
			syncModel sync = new syncModel(context);
			syncRecipeModel syncRecipe = new syncRecipeModel(context);
			try {
				sync.getJSONFromServer();
				sync.getAndCreateAccountJSON();
				syncRecipe.getJSONFromServer();
				syncRecipe.getAndCreateJSON();
				
				sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
				 Log.v("LAST UPDATE", "LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
				
				Editor editor = sharedpreferences.edit();
		        editor.putString("Date", getLastUpdated());
		        editor.commit();
		        Toast.makeText(context, 
		        	    "App synced", Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				//uto-generated catch block
				e.printStackTrace();
				Toast.makeText(context, 
		        	    "App sync failed", Toast.LENGTH_LONG).show();
				
			}
		}
} 
	
	public Dialog createDialog(Activity activity, int resource)
	{
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(resource);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		return dialog;
	}
	

}
