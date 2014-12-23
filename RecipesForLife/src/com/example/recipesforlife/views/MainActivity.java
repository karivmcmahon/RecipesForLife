package com.example.recipesforlife.views;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

public class MainActivity extends Activity {
	
	List<String> account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		account = new ArrayList<String>();
		Button signUpButton = (Button) findViewById(R.id.signUpButton1);
		signUpButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
				EditText name = (EditText) findViewById(R.id.nameEditText1);
				EditText secondname = (EditText) findViewById(R.id.secondNameEditText);
				EditText country = (EditText) findViewById(R.id.countryEditText);
				EditText bio = (EditText) findViewById(R.id.bioEditText);
				EditText city = (EditText) findViewById(R.id.cityEditText);
				EditText interest = (EditText) findViewById(R.id.interestEditText);
				String nameEntered = name.getText().toString();
				String secondName = secondname.getText().toString();
				String countryEntered = country.getText().toString();
				String bioEntered = bio.getText().toString();
				String cityEntered = city.getText().toString();
				String interestEntered = interest.getText().toString();
				Log.v("NAME", "NAME " + nameEntered);
				account.add(nameEntered);
				account.add(secondName);
				account.add(countryEntered);
				account.add(bioEntered);
				account.add(cityEntered);
				account.add(interestEntered);
				Context t = getApplicationContext();
				accountModel accountmodel = new accountModel(t);
				Log.v("NAME", "NAME " + nameEntered);
				accountmodel.insertAccount(account);
				Log.v("NAME", "NAME " + nameEntered);
			}
		});
		
		buildDatabase();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// building the database.
		public void buildDatabase() {
			databaseConnection dbConnection = new databaseConnection(this);
			//myDbHelper.deleteDatabase();
			try {
				dbConnection.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}
		}

}
