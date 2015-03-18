package com.example.webbrowser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

public class Time extends Activity {

	protected TimePicker setTime;
	protected int minutes;
	protected int hours;
	protected String HR;
	protected String Min;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time);
		
		setTime = (TimePicker) findViewById(R.id.timePicker);
	}
	/*
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
		    new TimePickerDialog.OnTimeSetListener() {
		        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		            mHour = hourOfDay;
		            mMinute = minute;
		            updateDisplay();
		        }
		    };*/
	public void returnSettingsSubmit(View v)
	{
		setTime.clearFocus();
		//get the hours the user selects on the time picker
	    hours= setTime.getCurrentHour();
	    //convert the int number to string
	    HR = String.valueOf(hours);
	    //get the minutes the user selects on the time picker
	    minutes = setTime.getCurrentMinute();
	    //convert the int number to string
	    Min = String.valueOf(minutes);
	    //go back to the main activity
		Intent newAction = new Intent(getApplicationContext(), MainActivity.class);
		//pass in the the time the user sets to the main
		newAction.putExtra("passTimeHR", HR);
		newAction.putExtra("passTimeMin", Min);
		//start the main activity
		startActivity(newAction);
		//let the user know that the time was set successfully
		Toast.makeText(getApplicationContext(), "Time Set!", Toast.LENGTH_LONG).show();
	}
	
	public void returnSettingsCancel(View v)
	{
		//for this method the user has not set the time so we dont have to get the hour and minutes like the last method
		//go back to the main activity
		Intent newAction = new Intent(getApplicationContext(), MainActivity.class);
		//we also don't have to pass anthing back to the main so just start the main activity
		startActivity(newAction);
		//let the user know that the time was not set successfully per user request
		Toast.makeText(getApplicationContext(), "Time NOT Set!", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time, menu);
		return true;
	}

}
