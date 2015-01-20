package com.example.recipesforlife.models;

import java.util.Calendar;





import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

@SuppressLint("NewApi")
public class TimePickerFragment extends DialogFragment {

	Handler mHandler ;
    String hour;
    String minutes;
    int h, m;
 
    public TimePickerFragment(Handler h){
        /** Getting the reference to the message handler instantiated in MainActivity class */
        mHandler = h;
    }
@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current time as the default values for the picker
	

    TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			hour = Integer.toString(hourOfDay);
            minutes = Integer.toString(minute);
            h = hourOfDay;
            m = minute;

             /** Creating a bundle object to pass currently set date to the fragment */
             Bundle b = new Bundle();

             /** Adding currently set day to bundle object */
             b.putString("hour", hour);

             /** Adding currently set month to bundle object */
             b.putString("minute", minutes);

          

           

             /** Creating an instance of Message */
             Message m = new Message();

             /** Setting bundle object on the message object m */
             m.setData(b);

             /** Message m is sending using the message handler instantiated in MainActivity class */
             mHandler.sendMessage(m);
		}
	};
// Create a new instance of TimePickerDialog and return
	return new TimePickerDialog(getActivity(), listener, h,m,
true);
}
}



