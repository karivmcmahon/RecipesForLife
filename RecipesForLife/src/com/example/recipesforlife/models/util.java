package com.example.recipesforlife.models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.views.SignUpSignInActivity;

public class util  {

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
	@SuppressLint("SimpleDateFormat")
	public String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}

	public String getLastUpdated2()
	{
		Calendar cal = Calendar.getInstance(); // creates calendar		
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.SECOND, -3);
		Date today = cal.getTime();

		String lastUpdated = dateToString(today);
		return lastUpdated;
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

	public void setRowText(int resource, View rowview, int fontSize)
	{
		//Style for activity
		//typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
		TextView view = (TextView)	 rowview.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#FFFFFFFF"));
	}

	/**
	 * Set custom text for the dialog
	 * @param resource
	 * @param dialog
	 * @param fontSize
	 */
	public void setDialogTextString(int resource, Dialog dialog, String text)
	{
		//Style for activity
		//typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
		EditText view = (EditText)	 dialog.findViewById(resource);
		view.setText(text);
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
	public void setTextString(int resource,String text)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setText(text);

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

	public String getTextView(int resource )
	{
		TextView view = (TextView) activity.findViewById(resource);
		String text = view.getText().toString();
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

	public Button setButtonTextDialog(int resource, int fontSize, Dialog dialog)
	{
		Button  button = (Button) dialog.findViewById(resource);
		button.setTypeface(typeFace);
		button.setTextSize(fontSize);
		button.setTextColor(Color.parseColor("#FFFFFFFF"));
		return button;
	}

	public void setTimePickerFrag(Dialog dialog, int res, final Handler handler)
	{
		EditText editText2 = (EditText) dialog.findViewById(res);
		editText2.setOnTouchListener(new OnTouchListener(){
			//When clicked opens the timepicker fragment
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction()  == MotionEvent.ACTION_DOWN)
				{
					// Instantiating TimePickerDialogFragment 
					TimePickerFragment timePicker = new TimePickerFragment(handler);


					// Getting fragment manger for this activity 
					android.app.FragmentManager fm = activity.getFragmentManager();

					// Starting a fragment transaction 
					android.app.FragmentTransaction ft = fm.beginTransaction();

					// Adding the fragment object to the fragment transaction 
					ft.add(timePicker, "time_picker");

					// Opening the TimePicker fragment 
					ft.commit();

				}
				return false;
			}});
	}

	public void sync()
	{
		if(checkInternetConnection(context) == true)
		{
			sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
			syncModel sync = new syncModel(context);
			syncRecipeModel syncRecipe = new syncRecipeModel(context);
			syncCookbookModel syncCookbook = new syncCookbookModel(context);
			syncRecipeUpdateModel updates= new syncRecipeUpdateModel(context);
			syncContributersModel syncContributer = new syncContributersModel(context);
			try {
				sync.getJSONFromServer();
				Editor editor = sharedpreferences.edit();
				editor.putString("Account Date", getLastUpdated());
				editor.commit();

				syncCookbook.getJSONFromServer(false);
				editor.putString("Cookbook", getLastUpdated());
				editor.commit();

				syncRecipe.getJSONFromServer();
				editor.putString("Date", getLastUpdated());
				editor.commit();
				
				syncContributer.getJSONFromServer();
				editor.putString("Contributers", getLastUpdated());
				editor.commit();


				updates.getJSONFromServer();
				editor.putString("Change", getLastUpdated());
				editor.commit(); 
				
			    syncCookbook.getJSONFromServer(true);
				editor.putString("Cookbook Update", getLastUpdated());
				editor.commit(); 

				sync.getAndCreateAccountJSON();
				editor.putString("Account Date Server", getLastUpdated());
				editor.commit();

				syncCookbook.getAndCreateJSON(false);
				editor.putString("Cookbook Server", getLastUpdated());
				editor.commit();
			

				syncRecipe.getAndCreateJSON();
				editor.putString("Date Server", getLastUpdated());
				editor.commit();
				
				syncContributer.getAndCreateJSON();
				editor.putString("Contributers Server", getLastUpdated());
				editor.commit();

				updates.getAndCreateJSON();
				editor.putString("Change Server", getLastUpdated());
				editor.commit(); 
				
				syncCookbook.getAndCreateJSON(true);
				editor.putString("Cookbook Update Server", getLastUpdated());
				editor.commit(); 


				Log.v("LAST UPDATE", "LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
				Log.v("LAST UPDATE SERVER", "LAST UPDATE SERVER " + sharedpreferences.getString("Date Server", "DEFAULT"));


				Toast.makeText(context, 
						"App synced", Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context, 
						"App sync failed", Toast.LENGTH_LONG).show();

			} catch (IOException e) {
				// TODO Auto-generated catch block
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
