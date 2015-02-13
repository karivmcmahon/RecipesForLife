package com.example.recipesforlife.models;

import java.util.ArrayList;
import java.util.UUID;

import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;

public class cookbookModel extends baseDataSource {
	
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	SharedPreferences sharedpreferences;
	Context context;
	utility utils;
	
	public cookbookModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		utils = new utility();
	}
	
	public void insertBook(cookbookBean book, boolean server)
	{
		
		open();
	    ContentValues values = new ContentValues();
	    values.put("name", book.getName()); 
	    values.put("updateTime", utils.getLastUpdated()); 
	    values.put("changeTime", "2015-01-01 12:00:00.000"); 
	    values.put("creator", book.getCreator()); 
	    values.put("privacyOption", book.getPrivacy()); 
	    values.put("contributerid", 1);
	    values.put("description", book.getDescription());
	    if(server == true)
	    {
	    	values.put("uniqueid", book.getUniqueid());
	    }
	    else
	    {
	    	 values.put("uniqueid", generateUUID(book.getCreator(), "Cookbook"));
	    }
	    database.beginTransaction();
        try
        {
        	database.insertOrThrow("Cookbook", null, values);
        	database.setTransactionSuccessful();
        	database.endTransaction(); 
        	close();
        }catch(SQLException e)
        {
        	database.endTransaction();
        	close();
        } 
    	close();
    	
    	
	}
	
	public ArrayList<cookbookBean> selectCookbooksByUser(String user)
	{
		ArrayList<cookbookBean> cbList = new ArrayList<cookbookBean>();
	    open();
        Cursor cursor = database.rawQuery("SELECT * FROM Cookbook WHERE creator=?", new String[] { user });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                cbList.add(cursorToCookbook(cursor));
                
                
            }
        }
        cursor.close();
        close();
        return cbList;	
	}
	
	public int selectCookbooksID(String name, String user)
	{
		int id = 0;
	  open();
		 Cursor cursor = database.rawQuery("SELECT * FROM Cookbook WHERE creator=? AND name=?", new String[] { user , name });
      if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                id = cursor.getInt(getIndex("id",cursor));
                
                
            }
        }
        cursor.close(); 
        close();
        return id;	
	}
	
	public String selectCookbooksUniqueID(int recipeid)
	{
	 String id = "";
	  open();
        Cursor cursor = database.rawQuery("SELECT uniqueid FROM Cookbook INNER JOIN CookbookRecipe WHERE CookbookRecipe.Recipeid=? AND  Cookbook.id=CookbookRecipe.Cookbookid", new String[] { Integer.toString(recipeid) });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                id = cursor.getString(getIndex("uniqueid",cursor));
                
                
            }
        }
        cursor.close();
        close();
        return id;	
	}
	
	public String selectCookbooksNameByID(String uniqueid)
	{
	 String name = "";
	  open();
        Cursor cursor = database.rawQuery("SELECT name FROM Cookbook WHERE uniqueid=?", new String[] { uniqueid });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                name = cursor.getString(getIndex("uniqueid",cursor));
                
                
            }
        }
        cursor.close();
        close();
        return name;	
	}
	
	public ArrayList<String> selectRecipesByCookbook(String name, String user)
	{
		open();
		ArrayList<String> names = new ArrayList<String>();
		Cursor cursor = database.rawQuery("SELECT *, Recipe.name AS recipename FROM Recipe INNER JOIN CookbookRecipe INNER JOIN Cookbook ON CookbookRecipe.Recipeid=Recipe.id WHERE Cookbook.name = ? AND Cookbook.creator = ?", new String[] { name, user });
		  if (cursor != null && cursor.getCount() > 0) {
	            for (int i = 0; i < cursor.getCount(); i++) {
	                cursor.moveToPosition(i);
	                names.add(cursor.getString(getIndex("recipename",cursor)));
	                
	                
	            }
	        }
	    cursor.close();
		close();
		return names;
	}
	
	public cookbookBean cursorToCookbook(Cursor cursor) {
        cookbookBean cb = new cookbookBean();
        cb.setName(cursor.getString(getIndex("name",cursor)));
        cb.setDescription(cursor.getString(getIndex("description",cursor)));
        cb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
        cb.setPrivacy(cursor.getString(getIndex("privacyOption",cursor)));
        cb.setCreator(cursor.getString(getIndex("creator",cursor)));
        cb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
        cb.setChangeTime(cursor.getString(getIndex("changeTime", cursor)));
        return cb;
    }
	
	 /**
	  * Generates UUID then adds the name and type of table - to create a more detailed unique id
	  * @param addedBy
	  * @param table
	  * @return
	  */
	 public String generateUUID(String addedBy, String table ) {
		 //   final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		 final String uuid = UUID.randomUUID().toString();
		 String uniqueid = addedBy + table + uuid;
		 boolean exists = selectUUID(table, uniqueid);
		 if(exists == true)
		 {
			 selectUUID(table, uniqueid);
		 }
		 return uniqueid;
		}
	 
	 /**
	  * Checks if unique id exists - if so create another one
	  * @param table
	  * @param uuid
	  * @return
	  */
	 public boolean selectUUID(String table, String uuid )
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
