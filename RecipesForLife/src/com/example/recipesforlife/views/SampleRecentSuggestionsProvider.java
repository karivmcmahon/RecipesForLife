package com.example.recipesforlife.views;

import android.content.SearchRecentSuggestionsProvider; 

public class SampleRecentSuggestionsProvider 
      extends SearchRecentSuggestionsProvider { 
   
   public static final String AUTHORITY = 
		   "com.example.recipesforlife.views.SampleRecentSuggestionsProvider";

   public static final int MODE = DATABASE_MODE_QUERIES; 

   public SampleRecentSuggestionsProvider() { 
      setupSuggestions(AUTHORITY, MODE); 
   } 
}