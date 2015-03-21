package com.example.recipesforlife.util;

import android.content.SearchRecentSuggestionsProvider; 

/**
 * Class used to set up search for recent suggestions
 * @author Kari
 *
 */
public class SampleRecentSuggestionsProvider 
extends SearchRecentSuggestionsProvider { 

	public static final String AUTHORITY = 
			"com.example.recipesforlife.views.SampleRecentSuggestionsProvider";

	public static final int MODE = DATABASE_MODE_QUERIES; 

	public SampleRecentSuggestionsProvider() { 
		setupSuggestions(AUTHORITY, MODE); 
	} 
}