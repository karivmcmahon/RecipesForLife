package com.example.recipesforlife.views;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.*;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

public class MainActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main);
		
		Button logOff = (Button) findViewById(R.id.logOffButton);
		logOff.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  SharedPreferences sharedpreferences = getSharedPreferences
					      (SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
					      Editor editor = sharedpreferences.edit();
					      editor.clear();
					      editor.commit();
					     Intent i = new Intent(MainActivity.this, SignUpSignInActivity.class);
					     startActivity(i);
				
			}
			
		});
		
	}
	

	

}
