package com.example.recipesforlife.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TimePicker;

@SuppressLint("NewApi")
/**
 * A time picker dialog which enables the user to set hours and minutes 
 * Based on the Android documentation
 * Data is sent based on bundles
 * 
 * @author Kari
 *
 */
public class TimePickerFragment extends DialogFragment 
{

	Handler mHandler ;
	String hour;
	String minutes;
	String type;
	int h, m;

	public TimePickerFragment(Handler h, String type)
	{
		mHandler = h;
		this.type = type;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {		
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {		
				
				//Sends data once time has been sent
				hour = Integer.toString(hourOfDay);
				minutes = Integer.toString(minute);
				h = hourOfDay;
				m = minute;          
				Bundle b = new Bundle();
				b.putString("hour", hour);
				b.putString("minute", minutes);            
				Message m = new Message();
				m.setData(b);
				mHandler.sendMessage(m);

			}
		};

		
		// Create a new instance of TimePickerDialog and return
		TimePickerDialog tpd =new TimePickerDialog(getActivity(), listener, h,m,true);
		tpd.setTitle("Set time it will take to " + type + " recipe in hours:minutes");
		return tpd;
	}
}



