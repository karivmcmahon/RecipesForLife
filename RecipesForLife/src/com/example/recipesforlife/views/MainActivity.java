package com.example.recipesforlife.views;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

public class MainActivity extends Activity {
	
	List<String> account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		account = new ArrayList<String>();
		Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
		TextView nameView = (TextView) findViewById(R.id.nameView1);
		nameView.setTypeface(typeFace);
		TextView secondNameView = (TextView) findViewById(R.id.secondNameView);
		secondNameView.setTypeface(typeFace);
		TextView bioView = (TextView) findViewById(R.id.bioTextView);
		bioView.setTypeface(typeFace);
		TextView countryView = (TextView) findViewById(R.id.countryText);
		countryView.setTypeface(typeFace);
		TextView cityView = (TextView) findViewById(R.id.cityTextView);
		cityView.setTypeface(typeFace);
		TextView interestView = (TextView) findViewById(R.id.interestTextView);
		interestView.setTypeface(typeFace);
		Button signUpButton = (Button) findViewById(R.id.signUpButton1);
		signUpButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				//Get text box
				EditText name = (EditText) findViewById(R.id.nameEditText1);
				EditText secondname = (EditText) findViewById(R.id.secondNameEditText);
				EditText country = (EditText) findViewById(R.id.countryEditText);
				EditText bio = (EditText) findViewById(R.id.bioEditText);
				EditText city = (EditText) findViewById(R.id.cityEditText);
				EditText interest = (EditText) findViewById(R.id.interestEditText);
				EditText email = (EditText) findViewById(R.id.emailEditText);
				EditText password = (EditText) findViewById(R.id.passwordEditText);
			
				//Retrieve data from textbox
				String nameEntered = name.getText().toString();
				String secondName = secondname.getText().toString();
				String countryEntered = country.getText().toString();
				String bioEntered = bio.getText().toString();
				String cityEntered = city.getText().toString();
				String interestEntered = interest.getText().toString();
				String emailEntered = email.getText().toString();
				String passwordEntered = password.getText().toString();
				
				//Add to list
				account.add(nameEntered);
				account.add(secondName);
				account.add(countryEntered);
				account.add(bioEntered);
				account.add(cityEntered);
				account.add(interestEntered);
				account.add(emailEntered);
				account.add(passwordEntered);
				
				//Insert to db
				Context t = getApplicationContext();
				accountModel accountmodel = new accountModel(t);
				accountmodel.insertAccount(account);
			}
		});
		
		//buildDatabase();
	}
	

	

}
