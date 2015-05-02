package com.example.recipesforlife.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.SyncModel_AccountModel;
import com.example.recipesforlife.models.SyncModel_ContributersModel;
import com.example.recipesforlife.models.SyncModel_CookbookModel;
import com.example.recipesforlife.models.SyncModel_RecipeDetailsModel;
import com.example.recipesforlife.models.SyncModel_RecipeModel;
import com.example.recipesforlife.models.SyncModel_ReviewModel;
import com.example.recipesforlife.views.Account_SignUpSignInView;

/**
 * Utility class handling common methods for multiple classes
 * @author Kari
 *
 */
public class Util  {

	private Context context;
	private Typeface typeFace;
	private Activity activity;
	private Editor editor;
	private SharedPreferences sharedpreferences;
	 
	 
	private SyncModel_AccountModel sync;
	private SyncModel_RecipeModel syncRecipe;
	private SyncModel_CookbookModel syncCookbook;
	private SyncModel_ContributersModel syncContributer;
	private SyncModel_ReviewModel syncReview;
	private SyncModel_RecipeDetailsModel syncRecipeDetails;


	public Util(Context context, Activity activity)
	{
		this.activity = activity;
		this.context = context;
		typeFace=Typeface.createFromAsset(context.getAssets(),"fonts/elsie.ttf");
	}
	/**
	 * Checks if there is internet connection
	 * 
	 * @param context	Activity context
	 * @return boolean	if there is return true else return false
	 */
	private boolean checkInternetConnection(Context context) {
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
	 * 
	 * @param date		Date
	 * @return String 	Date as string
	 */
	@SuppressLint("SimpleDateFormat")
	private String dateToString(Date date, boolean inappstring) {
		SimpleDateFormat formatter;

		TimeZone tz = TimeZone.getTimeZone("Europe/London");   
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		formatter.setTimeZone(tz);
		Calendar cal = Calendar.getInstance(); // creates calendar
		String currentDate = formatter.format(cal.getTime());
		return currentDate;
	}



	/**
	 * Get current date
	 * 
	 * @param appstring  If request coming from app
	 * @return String	current date time
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
	 * 
	 * @param resource		Resource that the custom text will be set for
	 * @param dialog		Dialog the resource is contained in
	 * @param fontSize		Fontsize for text
	 */
	public void setDialogText(int resource, Dialog dialog, int fontSize)
	{
		//Style for activity
		TextView view = (TextView)	 dialog.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#FFFFFFFF"));
	}

	/**
	 * Set text for a row in a listview
	 * 
	 * @param resource		Resource that the custom text will be set for
	 * @param rowview		Row view in a list
	 * @param fontSize		Fontsize for text
	 */
	public void setRowText(int resource, View rowview, int fontSize)
	{
		//Style for activity
		TextView view = (TextView)	 rowview.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#FFFFFFFF"));
	}

	/**
	 * Set custom text for the dialog
	 * 
	 * @param resource		Resource that the custom text will be set for
	 * @param dialog		Dialog the resource is contained 
	 * @param fontSize		Fontsize for text
	 */
	public void setDialogTextString(int resource, Dialog dialog, String text)
	{
		//Style for activity
		EditText view = (EditText)	 dialog.findViewById(resource);
		view.setText(text);
	}

	/**
	 * Set custom text
	 * 
	 * @param resource	Resource that the custom text will be set for
	 * @param fontSize	Fontsize for text
	 */
	public void setText(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#FFFFFFFF"));
	}

	/**
	 * Set text to black and italic
	 * 
	 * @param resource		Resource that the custom text will be set for
	 * @param fontSize		Fontsize for text
	 */
	public void setTextBlackItalic(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTextSize(fontSize);
		view.setTypeface(typeFace, Typeface.ITALIC);

	}

	/**
	 * Set custom text
	 * 
	 * @param resource	Resource that the  text will be set for
	 * @param text 		Text to be set to textview
	 */
	public void setTextString(int resource,String text)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setText(text);
	}

	/**
	 * Get text from edit text
	 * 
	 * @param resource  Resource that the text will be retrieved from
	 * @return String 	Retrieved text from edit text
	 */
	public String getText(int resource )
	{
		EditText edit = (EditText) activity.findViewById(resource);
		String text = edit.getText().toString();
		return text;
	}

	/**
	 * Gets text from textview
	 * 
	 * @param resource	Resource that the text will be retrieved from
	 * @return String	Text retrieved from textview
	 */
	public String getTextView(int resource )
	{
		TextView view = (TextView) activity.findViewById(resource);
		String text = view.getText().toString();
		return text;
	}

	/**
	 * Get text from Dialog
	 * 
	 * @param resource	Resource that the text will be retrieved from
	 * @param dialog 	Dialog the text is being retrieved from
	 * @return String 	Text retrieved from dialog
	 */
	public String getTextFromDialog(int resource, Dialog dialog )
	{
		EditText edit = (EditText) dialog.findViewById(resource);
		String text = edit.getText().toString();
		return text;
	}

	/**
	 * Set custom text with pink color
	 * 
	 * @param resource	Resource that the text will be set for
	 * @param fontSize	Font size for text
	 */
	public void setTextPink(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
		view.setTextColor(Color.parseColor("#F3216C"));
	}

	/**
	 * Set custom text with black color
	 * 
	 * @param resource	Resource that the text will be set for
	 * @param fontSize	Font size for text
	 */
	public void setTextBlack(int resource,int fontSize)
	{
		TextView view = (TextView) activity.findViewById(resource);
		view.setTypeface(typeFace);
		view.setTextSize(fontSize);
	}



	/**
	 * Set custom text for button
	 * 
	 * @param resource	Resource that the text will be set for
	 * @param fontSize	Font size for text
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
	 * 
	 * @param resource	Resource that the button text will be set for
	 * @param fontSize	Font size for text
	 * @param dialog	Dialog which contains the button
	 * @return Button	Button with text
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
	 * 
	 * @param dialog		Dialog which should contain the time picker
	 * @param res			Resource for the time picker
	 * @param handler
	 * @param type			Time picker type
	 */
	public void setTimePickerFrag(Dialog dialog, int res, final Handler handler, final String type)
	{
		EditText editText2 = (EditText) dialog.findViewById(res);
		editText2.setOnTouchListener(new OnTouchListener(){
			//When clicked opens the timepicker fragment
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction()  == MotionEvent.ACTION_DOWN)
				{
					// Instantiating TimePickerDialogFragment 
					TimePickerFragment timePicker = new TimePickerFragment(handler, type);


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
			sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
			sync = new SyncModel_AccountModel(context);
			syncRecipe = new SyncModel_RecipeModel(context);
			syncCookbook = new SyncModel_CookbookModel(context);
			syncContributer = new SyncModel_ContributersModel(context);
			syncReview = new SyncModel_ReviewModel(context);
			syncRecipeDetails = new SyncModel_RecipeDetailsModel(context);
			try {
				//Sync
				editor = sharedpreferences.edit();
				
				//Handles inserts
				accountInsertsSync();
				cookbookInsertsSync();
				recipeInsertsSync();
				recipeDetailsInsertsSync();
				contributorsInsertsSync();
				reviewInsertsSync();
			
				//Handles updates
				recipeUpdatesSync();
				cookbookUpdatesSync();
				contributorUpdatesSync();
			
				//Update timestamp and reset stage
				editor.putString("Date", getLastUpdated(true));
				editor.commit();
				editor.putString("Stage", "1");
				editor.commit();

				Log.v("LAST UPDATE", "LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
				return "success";
			} catch (JSONException e) {
				Log.v("LAST UPDATE", "ERROR LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
				e.printStackTrace();
				return "fail";

			} catch (IOException e) {
				Log.v("LAST UPDATE", "ERROR LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
				e.printStackTrace();
				return "fail";

			}
			catch (SQLiteException e) {
				Log.v("LAST UPDATE", "ERROR LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
				e.printStackTrace();
				return "fail";
			}
		}
		return "fail";

	} 

	/**
	 * Handles the retrieving and sending of account information to be inserted to the databases
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void accountInsertsSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("1"))
				sync.getJSONFromServer();
				editor.putString("Stage", "2");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("2"))
				sync.getAndCreateAccountJSON();
				editor.putString("Stage", "3");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Handles the retrieving and sending of cookbook information to be inserted to the databases
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void cookbookInsertsSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("3"))
				syncCookbook.getJSONFromServer(false);
				editor.putString("Stage", "4");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("4"))
				syncCookbook.getAndCreateJSON(false);
				editor.putString("Stage", "5");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Handles the retrieving and sending of recipe information to be inserted to the databases
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void recipeInsertsSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("5"))   
				syncRecipe.getJSONFromServer(false);
				editor.putString("Stage", "6");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("6"))   
				syncRecipe.getAndCreateJSON(false); 
				editor.putString("Stage", "7");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Handles the retrieving and sending of recipe details information to be inserted to the databases
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void recipeDetailsInsertsSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("7"))
				syncRecipeDetails.getJSONFromServer();
				editor.putString("Stage", "8");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("8"))   
				syncRecipeDetails.getAndCreateJSON(false);
				editor.putString("Stage", "10");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}

	}

	/**
	 * Handles the retrieving and sending of contributors information to be inserted to the databases
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void contributorsInsertsSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("10"))
				syncContributer.getJSONFromServer(false);
				editor.putString("Stage", "11");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("11"))
				syncContributer.getAndCreateJSON(false);
				editor.putString("Stage", "12");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Handles the retrieving and sending of review information to be inserted to the databases
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void reviewInsertsSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("12"))
				syncReview.getJSONFromServer();
				editor.putString("Stage", "13");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("13"))
				syncReview.getAndCreateJSON();
				editor.putString("Stage", "14");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Handles the retrieving and sending of recipe information to be updated in the database
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void recipeUpdatesSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("14"))
				syncRecipe.getJSONFromServer(true);
				editor.putString("Stage", "15");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("15"))
				syncRecipe.getAndCreateJSON(true);
				editor.putString("Stage", "16");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Handles the retrieving and sending of cookbook information to be updated in the database
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void cookbookUpdatesSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("16"))
				syncCookbook.getJSONFromServer(true);
				editor.putString("Stage", "17");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("17"))
				syncCookbook.getAndCreateJSON(true);
				editor.putString("Stage", "18");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Handles the retrieving and sending of contrib information to be updated in the database
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLiteException
	 */
	private void contributorUpdatesSync() throws JSONException, IOException, SQLiteException
	{
		try
		{
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("18"))
				syncContributer.getJSONFromServer(true);
				editor.putString("Stage", "19");
				editor.commit();
			if(sharedpreferences.getString("Stage", "DEFAULT").equals("19"))
				syncContributer.getAndCreateJSON(true);
				editor.putString("Stage", "20");
				editor.commit();
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		catch (SQLiteException e) {
			throw e;
		}
	}

	/**
	 * Creates a dialog
	 * 
	 * @param activity	
	 * @param resource	Resource for dialog view to be set to
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
	 *
	 * @param spinner		Spinner
	 * @param myString		String item to get index for
	 * @return int 			the index
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

	/**
	 * Gets image path based on URI - used to show path when user selects image in browse
	 * 
	 * @param uri		Uri
	 * @return String	Image path
	 */
	public String getRealPathFromURI(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * Decodes image and finds the correct scale value for sample size
	 * 
	 * @param selectedImage		Image URI
	 * @return Bitmap			Bitmap image
	 * 
	 * @throws FileNotFoundException
	 */
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

	/**
	 * Rotates image based on ExifInterface - particularly useful for camera photos
	 * 
	 * @param bitmap		Image bitmap
	 * @param filePath		Image file path
	 * @return Bitmap		Correctly rotated image
	 */
	public Bitmap rotateImage(Bitmap bitmap, String filePath)
	{
		Bitmap resultBitmap = bitmap;

		try
		{
			ExifInterface exifInterface = new ExifInterface(filePath);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			Matrix matrix = new Matrix();

			if (orientation == 6) {
				matrix.postRotate(90);
			}
			else if (orientation == 3) {
				matrix.postRotate(180);
			}
			else if (orientation == 8) {
				matrix.postRotate(270);
			}

			// Rotate the bitmap
			resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		catch (Exception exception)
		{
			Log.v("Could not rotate the image", "Could not rotate image " + exception);
		}
		return resultBitmap;
	}

	/** 
	 * Get image intent for selecting photos from gallery or camera
	 * 
	 * @return Intent	For selecting image
	 */
	public Intent getImageIntent()
	{
		Intent pickIntent = new Intent();
		pickIntent.setType("image/*");
		pickIntent.setAction(Intent.ACTION_GET_CONTENT);
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
		Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
		chooserIntent.putExtra
		(
				Intent.EXTRA_INITIAL_INTENTS, 
				new Intent[] { takePhotoIntent }
				); 
		return chooserIntent;
	}

	/**
	 * Sets up the search details
	 * 
	 * @param menu	Menu
	 */
	public void setUpSearch(Menu menu)
	{
		SearchManager searchManager =
				(SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		android.support.v7.widget.SearchView searchView =
				(android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(activity.getComponentName()));
	}



}