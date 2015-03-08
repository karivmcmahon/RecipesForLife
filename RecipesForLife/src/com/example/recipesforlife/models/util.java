package com.example.recipesforlife.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Utility class handling common methods for multiple classes
 * @author Kari
 *
 */
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
	public String dateToString(Date date, boolean inappstring) {
		SimpleDateFormat formatter;


		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		String currentDate = formatter.format(date);
		return currentDate;
	}



	/**
	 * Get current date
	 */
	private String getLastUpdated(boolean appstring)
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		Date today = cal.getTime();
		String lastUpdated = dateToString(today, appstring);
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
	 * Set text for a row in a listview
	 * @param resource
	 * @param rowview
	 * @param fontSize
	 */
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
	 * Get text from edit text
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
	 * 
	 * @param resource
	 * @return
	 */
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

	/**
	 * Set text to black
	 * @param resource
	 * @param fontSize
	 */
	public void setTextBlack(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		//view.setTextColor(Color.parseColor("#F3216C"));
	}

	/**
	 * Set text to black and italic
	 * @param resource
	 * @param fontSize
	 */
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

	/**
	 * Set button text for a dialog
	 * @param resource
	 * @param fontSize
	 * @param dialog
	 * @return Button
	 */
	public Button setButtonTextDialog(int resource, int fontSize, Dialog dialog)
	{
		Button  button = (Button) dialog.findViewById(resource);
		button.setTypeface(typeFace);
		button.setTextSize(fontSize);
		button.setTextColor(Color.parseColor("#FFFFFFFF"));
		return button;
	}

	/**
	 * Set up the time picker
	 * @param dialog
	 * @param res
	 * @param handler
	 */
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

	/**
	 * Code to sync the databases
	 */
	public String sync()
	{
		if(checkInternetConnection(context) == true)
		{
			sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
			syncModel sync = new syncModel(context);
			syncRecipeModel syncRecipe = new syncRecipeModel(context);
			syncCookbookModel syncCookbook = new syncCookbookModel(context);
			syncContributersModel syncContributer = new syncContributersModel(context);
			try {

				//Get json from server for inserts
				
				Editor editor = sharedpreferences.edit();
				
				//INSERTS SYNC 
				sync.getJSONFromServer();
				sync.getAndCreateAccountJSON();
				editor.putString("Account Date", getLastUpdated(true));
				editor.commit();

				syncCookbook.getJSONFromServer(false);
				syncCookbook.getAndCreateJSON(false);
				editor.putString("Cookbook", getLastUpdated(true));
				editor.commit();

				syncRecipe.getJSONFromServer(false);
				syncRecipe.getAndCreateJSON(false);
				editor.putString("Date", getLastUpdated(true));
				editor.commit();

				syncContributer.getJSONFromServer(false);
				syncContributer.getAndCreateJSON(false);
				editor.putString("Contributers", getLastUpdated(true));
				editor.commit();

				//UPDATES SYNC

				syncRecipe.getJSONFromServer(true);
				syncRecipe.getAndCreateJSON(true);
				editor.putString("Change", getLastUpdated(true));
				editor.commit(); 

				syncCookbook.getJSONFromServer(true);
				syncCookbook.getAndCreateJSON(true);
				editor.putString("Cookbook Update", getLastUpdated(true));
				editor.commit();
				
				

				syncContributer.getJSONFromServer(true);
				syncContributer.getAndCreateJSON(true);
				editor.putString("Contributers Update", getLastUpdated(true));
				editor.commit();

			

				Log.v("LAST UPDATE", "LAST UPDATE " + sharedpreferences.getString("Cookbook", "DEFAULT"));
				
				return "success";

				/*Toast.makeText(context, 
						"App synced", Toast.LENGTH_LONG).show();*/
			} catch (JSONException e) {
				Log.v("LAST UPDATE", "ERROR LAST UPDATE " + sharedpreferences.getString("Cookbook Date", "DEFAULT"));
				
				e.printStackTrace();
				return "fail";
				/*Toast.makeText(context, 
						"App sync failed", Toast.LENGTH_LONG).show();*/

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.v("LAST UPDATE", "ERROR LAST UPDATE " + sharedpreferences.getString("Cookbook Date", "DEFAULT"));
				
				e.printStackTrace();
				return "fail";
				/*Toast.makeText(context, 
						"App sync failed", Toast.LENGTH_LONG).show();*/

			}
			catch (SQLException e) {
				Log.v("LAST UPDATE", "ERROR LAST UPDATE " + sharedpreferences.getString("Cookbook Date", "DEFAULT"));
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "fail";
				/*Toast.makeText(context, 
					"App sync failed", Toast.LENGTH_LONG).show();*/

			}
		}
		return "fail";

	} 

	/**
	 * Creates a dialog
	 * @param activity
	 * @param resource
	 * @return Dialog
	 */
	public Dialog createDialog(Activity activity, int resource)
	{
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(resource);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		return dialog;
	}


	/**
	 * Gets spinner index  - found online http://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position
	 * @param spinner
	 * @param myString
	 * @return
	 */
	public int getIndex(Spinner spinner, String myString)
	{
		int index = 0;	
		for (int i=0;i<spinner.getCount();i++)
		{
			if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString))
			{
				index = i;
				i=spinner.getCount();//will stop the loop, kind of break, by making condition false
			}
		}
		return index;
	} 

	public String getRealPathFromURI(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 140;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE
					|| height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(selectedImage), null, o2);

	}



}
