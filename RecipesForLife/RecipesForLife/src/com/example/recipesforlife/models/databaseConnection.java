package com.example.recipesforlife.models;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class databaseConnection extends SQLiteOpenHelper {
	
  private static String DB_PATH;
  private static String DB_NAME = "dv.sqlite";
  private SQLiteDatabase myDataBase;
  private Context myContext;
  private Resources resources;
  
  public databaseConnection(Context context) 
  {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/";
        resources = myContext.getResources();
  }
  
  public void createDataBase() throws IOException
  {
        if (databaseFileExists()) 
        {
            if (checkDataBase()) 
            {
                Log.v("Database Debug", "Database has already been created.");
	        } 
	        else 
	        {
	            this.getReadableDatabase();
	            this.close();
	            try 
	            {
	            	
	                copyDataBase();
	                Log.v("Database Debug", "Copied database successfully");
	            } 
	            catch (IOException e) 
	            {
	                Log.v("Database Debug", "Error copying the database");
	                e.printStackTrace();
	                throw new Error("Error copying database");
	            }
        }
	    } 
        else 
        {
	        Log.v("Database Debug", "Database file does not exist in assets folder.");
	    }
  	}

	private boolean databaseFileExists() {
	    AssetManager mg = resources.getAssets();
	    try 
	    {
	        mg.open("databases/dv.sqlite");
	        Log.v("Database Debug", DB_NAME + " does exist.");
	        return true;
	    } 
	    catch (IOException ex) 
	    {
	        ex.printStackTrace();
	        Log.v("Database Debug", DB_NAME + " does not exist.");
	        return false;
	    }
	}
	
	private boolean checkDataBase() 
	{
	    SQLiteDatabase checkDB = null;
	    try 
	    {
	        String myPath = DB_PATH + DB_NAME;
	        checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	        Log.v("Database Debug", "Database does exist");
	
	    } 
	    catch (SQLiteException e) 
	    {
	        Log.v("Database Debug", "Database doesn't exist yet.");
	    }
	
	    if (checkDB != null) 
	    {
	        checkDB.close();
	        SQLiteDatabase.releaseMemory();
	    }
	    return checkDB != null ? true : false;
	}
	
	private void copyDataBase() throws IOException 
	{
	    //Open your local db as the input stream
	    AssetManager mg = resources.getAssets();
	    InputStream myInput = mg.open("databases/dv.sqlite");
	
	    // Path to the just created empty db
	    String outFileName = DB_PATH + DB_NAME;
	
	    //Open the empty db as the output stream
	    OutputStream myOutput = new FileOutputStream(outFileName);
	
	    //Transfer bytes from the inputfile to the outputfile
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = myInput.read(buffer)) > 0) 
	    {
	        myOutput.write(buffer, 0, length);
	    }
	    //Close the streams
	    myOutput.flush();
	    myOutput.close();
	    myInput.close();
	}
	
	public SQLiteDatabase openDataBase() throws SQLException 
	{
	    //Open the database
	    String myPath = DB_PATH + DB_NAME;
	    myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    myDataBase.close();
	    SQLiteDatabase.releaseMemory();
	    Log.v("Database Debug", "Database opened successfully.");
	    close();
	    return myDataBase;
	}
	
	public void deleteDatabase() throws SQLException 
	{
	    myContext.deleteDatabase(DB_NAME);
	}
	
	@Override
	public synchronized void close() 
	{
	    if (myDataBase != null)
	        myDataBase.close();
	    super.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
