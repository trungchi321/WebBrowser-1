package com.example.webbrowser;


import java.util.Calendar;
import java.util.Timer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.ActionBar;

public class MainActivity extends Activity{
	
	protected WebView myWebView;
	protected Button goUrl;
	protected ImageButton cancelPage;
	protected EditText webAddress;
	protected String url;
	@SuppressWarnings("rawtypes")
	protected ArrayAdapter bkmrks;
	protected ProgressBar progressBar;
    protected ProgressBar horizontalLoad;
    final Activity activity = this;
    //these variables used to calculate alarm time
    protected String[] setTime;
    protected int systemHours;
    protected int systemMinutes;
    protected int calculateHours;
    protected int calculateMinutes;
    protected int alarmHours;
    protected int alarmMinutes;
    protected long alarmSetHours;
	protected long alarmSetMinutes;
	protected long alarmSetSeconds;
	protected long alarmTime;
	protected long alarmSetTime;
    //these variables used to notify the user
    private static final int NOTIFY_6 = 0x1006;
    private NotificationManager notifier = null;
    private static int count = 0;
    //these variables used to setup the alarm manager
	PendingIntent pi;
	BroadcastReceiver br;
	AlarmManager am;
	Timer timer;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionBar = getActionBar();
	    actionBar.setTitle("CheetahWeb");
        
	myWebView = (WebView) findViewById(R.id.webPage);
	myWebView.getSettings().setJavaScriptEnabled(true);
	
    goUrl = (Button) findViewById(R.id.go_button);
    cancelPage = (ImageButton) findViewById(R.id.cancelWeb);
    webAddress = (EditText) findViewById(R.id.web_site);
    myWebView.setWebChromeClient(new WebChromeClient());
    
    //read in whether or not to load the home page on app startup
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean bookmarkCheckBox = sharedPrefs.getBoolean("loadHomePage", true);
    
    //get the home page url the user has defined in the settings
    SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
	String homePage = myPreference.getString("home_page", "google.com");
	
	//initialize the loading bar and get the user choice of what kind of loading bar they want
	progressBar = (ProgressBar) findViewById(R.id.loadingProgress);
	horizontalLoad = (ProgressBar) findViewById(R.id.horizontalprogressBar);
	SharedPreferences loadingPresent = PreferenceManager.getDefaultSharedPreferences(this);
	final String loadingOption = loadingPresent.getString("prefloadingBar", "circle");
    
    //run this webview if the loadhome page on app start is checked in settings
    if (bookmarkCheckBox == true)
    {
		myWebView.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress)
			{
				activity.setProgress(progress * 1000);
				if (loadingOption.equals("horizontal"))
				{
					if(progress < 100 && horizontalLoad.getVisibility() == ProgressBar.GONE){
	                       horizontalLoad.setVisibility(ProgressBar.VISIBLE);
					}
					horizontalLoad.setProgress(progress);
					if(progress == 100) {
						horizontalLoad.setVisibility(ProgressBar.GONE);
					}
				}
				if (loadingOption.equals("circle"))
				{
					if (progress == 100)
					{
						progressBar.setVisibility(View.GONE);
						cancelPage.setVisibility(View.GONE);
					}
				}
			}
		});
		
		myWebView.setWebViewClient(new WebViewClient()       
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				return false;
			}
		});
		myWebView.loadUrl("http://" + homePage);
		cancelPage.setVisibility(View.VISIBLE);
		if (loadingOption.equals("circle"))
		{
			progressBar.setVisibility(View.VISIBLE);
		}
    }  
    
    Intent webbrowsing = getIntent();
    String bkmrkUrl = webbrowsing.getStringExtra("EnteredUrl");
    
   //run the method that will start the alarm service to notify the user of their event
    setup();
    
    if (bkmrkUrl == null)
    {
	    //run the method that will calculate the time the user has set so that it can be passed to the alarm manager
		calculateAlarmTime();
    }
    
	//run this webview if notification icon clicked or loading from a bookmark
    if (bkmrkUrl != null)
    {
		myWebView.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress)
			{
				activity.setProgress(progress * 1000);
				if (loadingOption.equals("horizontal"))
				{
					if(progress < 100 && horizontalLoad.getVisibility() == ProgressBar.GONE){
	                       horizontalLoad.setVisibility(ProgressBar.VISIBLE);
					}
					horizontalLoad.setProgress(progress);
					if(progress == 100) {
						horizontalLoad.setVisibility(ProgressBar.GONE);
					}
				}
				if (loadingOption.equals("circle"))
				{
					if (progress == 100)
					{
						progressBar.setVisibility(View.GONE);
						cancelPage.setVisibility(View.GONE);
					}
				}
			}
		});
		
		myWebView.setWebViewClient(new WebViewClient()       
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				return false;
			}
		});
		myWebView.loadUrl("http://" + bkmrkUrl);
		cancelPage.setVisibility(View.VISIBLE);
		if (loadingOption.equals("circle"))
		{
			progressBar.setVisibility(View.VISIBLE);
		} 
    }
  
	horizontalLoad.setVisibility(View.GONE);
	url = webAddress.getText().toString();
	
	if (url.length() == 0)
	{
		progressBar.setVisibility(View.GONE);
		cancelPage.setVisibility(View.GONE);
	}
	}
	
	//start class methods
	public void calculateAlarmTime()
	{
		 //this code block controls when a notification is displayed and calls the notifyUser method to display
	    //notification when the time the user has set has been reached
	    //get the current system time
	    Calendar c = Calendar.getInstance(); 
	    systemHours = c.get(Calendar.HOUR);
	    systemMinutes = c.get(Calendar.MINUTE);
	    int systemSeconds = c.get(Calendar.SECOND);
	    
		//get the hours the user has set to remind
	    SharedPreferences userSetTime = PreferenceManager.getDefaultSharedPreferences(this);
	   	String remindTime = userSetTime.getString("setReminder", null);
	   	
	   	if (remindTime != null && remindTime != "")
	   	{
	   		//split the time into hours and minutes
	   		setTime = remindTime.split(":");
	   		
	   		//convert the hours and minutes to int to calculate
	   		calculateHours = Integer.parseInt(setTime[0]);
	   		calculateMinutes = Integer.parseInt(setTime[1]);
	   		
	   		//Calculate the number of hours to set the alarm
	   		if (calculateHours < systemHours)
	   		{
	   			alarmHours = systemHours - calculateHours;
	   		}
	   		else
	   		{
	   			alarmHours = calculateHours - systemHours;
	   		}
	   		
	   		//calculate the number of minutes to set the alarm
	   		if (calculateMinutes < systemMinutes)
	   		{
	   			alarmMinutes = systemMinutes - calculateMinutes;
	   		}
	   		else
	   		{
	   			alarmMinutes = calculateMinutes - systemMinutes;
	   		}
	   		
	   		//convert to milliseconds to pass to alarm
	   		alarmSetHours = alarmHours * 3600000;
	   		alarmSetMinutes = alarmMinutes * 60000;
	   		alarmSetSeconds = systemSeconds * 1000;
	   		
	   		//calculate the difference in the actual time and the user set time
	   	    alarmTime = alarmSetHours + alarmSetMinutes;
	   	    alarmSetTime = alarmTime - alarmSetSeconds;
	   		
	   	   	//set the alarm
	   	    am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + alarmSetTime, pi );
	   	}
	}

	//this method will setup the alarm manager to display the user notification at the user set time
	private void setup() {
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent i) {
				//call method to display a notification in the notification bar
				notifyUser();
			}
		};
		
		registerReceiver(br, new IntentFilter("com.authorwjf.wakeywakey") );
		pi = PendingIntent.getBroadcast( this, 0, new Intent("com.authorwjf.wakeywakey"),
				0 );
		am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
	}
	
    public void displayWebPage(View V)
    {
    		progressBar = (ProgressBar) findViewById(R.id.loadingProgress);
    		horizontalLoad = (ProgressBar) findViewById(R.id.horizontalprogressBar);
    		horizontalLoad.setVisibility(View.GONE);
    		
			SharedPreferences loadingPresent = PreferenceManager.getDefaultSharedPreferences(this);
			final String loadingOption = loadingPresent.getString("prefloadingBar", null);
    		
    		url = webAddress.getText().toString();
    		if (url.length() == 0)
    		{
    			Toast.makeText(getApplicationContext(), "You must enter a url", Toast.LENGTH_LONG).show();
    		}
    		else
    		{
    			myWebView.setWebChromeClient(new WebChromeClient(){
    				public void onProgressChanged(WebView view, int progress)
    				{
    					if (loadingOption.equals("horizontal"))
	    				{
	    					if(progress < 100 && horizontalLoad.getVisibility() == ProgressBar.GONE){
	    		                       horizontalLoad.setVisibility(ProgressBar.VISIBLE);
	    		            }
	    		            horizontalLoad.setProgress(progress);
	    		            if(progress == 100) {
	    		            	horizontalLoad.setVisibility(ProgressBar.GONE);
	    		            }
	    				}
    					if (loadingOption.equals("circle"))
    					{
    						activity.setProgress(progress * 1000);
	    					
	    					if (progress == 100)
	    					{
	    						progressBar.setVisibility(View.GONE);
	    						cancelPage.setVisibility(View.GONE);
	    					}
    					}
    				}
	    			});
    			
    			myWebView.setWebViewClient(new WebViewClient()       
    			{
    				@Override
    				public boolean shouldOverrideUrlLoading(WebView view, String url) 
    				{
    					return false;
    				}
    			});
    			myWebView.loadUrl("http://" + url);
    			cancelPage.setVisibility(View.VISIBLE);
    			
    			if (loadingOption.equals("circle"))
    			{
    				progressBar.setVisibility(View.VISIBLE);
    			}
    		}
    }
    
	public void cancelWebPage(View V)
    {
		progressBar = (ProgressBar) findViewById(R.id.loadingProgress);
    	Toast.makeText(getApplicationContext(), "stopping", Toast.LENGTH_LONG).show();
    	myWebView.stopLoading();
    	cancelPage.setVisibility(View.GONE);
    	progressBar.setVisibility(View.GONE);
    	
    }
	
	public void go_home(View v) {
		SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
		String homePage = myPreference.getString("home_page", "google.com");
		progressBar = (ProgressBar) findViewById(R.id.loadingProgress);
		horizontalLoad = (ProgressBar) findViewById(R.id.horizontalprogressBar);
		SharedPreferences loadingPresent = PreferenceManager.getDefaultSharedPreferences(this);
		final String loadingOption = loadingPresent.getString("prefloadingBar", "circle");
		
		myWebView.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress)
			{
				activity.setProgress(progress * 1000);
				if (loadingOption.equals("horizontal"))
				{
					if(progress < 100 && horizontalLoad.getVisibility() == ProgressBar.GONE){
	                       horizontalLoad.setVisibility(ProgressBar.VISIBLE);
					}
					horizontalLoad.setProgress(progress);
					if(progress == 100) {
						horizontalLoad.setVisibility(ProgressBar.GONE);
					}
				}
				if (loadingOption.equals("circle"))
				{
					if (progress == 100)
					{
						progressBar.setVisibility(View.GONE);
						cancelPage.setVisibility(View.GONE);
					}
				}
			}
		});
		
		myWebView.setWebViewClient(new WebViewClient()       
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				return false;
			}
		});
		Toast.makeText(getApplicationContext(), "going to homepage", Toast.LENGTH_LONG).show();
		myWebView.loadUrl("http://" + homePage);
		cancelPage.setVisibility(View.VISIBLE);
		if (loadingOption.equals("circle"))
		{
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	
	//this method will show a notification
	//this method will only run when the time the user sets matches the user time
	@SuppressWarnings("deprecation")
	public void notifyUser() {
        	
		  
        	//get the name of the event to display when the notification is launched
        	SharedPreferences eventNamePref = PreferenceManager.getDefaultSharedPreferences(this);
    		String userEventName = eventNamePref.getString("eventName", "Cheetah Time!");
    		
    		//get the url to launch when the notification is launched
    		SharedPreferences eventUrlPref = PreferenceManager.getDefaultSharedPreferences(this);
    		String userNotifyUrl = eventUrlPref.getString("eventURL", null);
    		
    		//restart the notifications when app is closed and reopened
    		Notification notify = null;
    		
            
            /*** STEP 1: Get a NotificationManager ***/
            notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            
            //count the number of notifications received
            count++;
            
            /*** STEP 2: Create a Notification object ***/
            
            if (notify == null)
            {
            	notify = new Notification(R.drawable.ic_launcher_notify,
                            "1 New Event", System.currentTimeMillis());
            	notify.flags |= Notification.FLAG_AUTO_CANCEL;	
            	notify.number = count;
            	//22222222222222notify.number = notify.number - 1;
            }
            
            
            //play a ringtone when notification received
            //get what ringtone the user has selected
            SharedPreferences ringtone = PreferenceManager.getDefaultSharedPreferences(this);
        	final String setRingtone = ringtone.getString("audioRingTone", "future");
        	//set and play the ringtone
        	if (setRingtone.equals("future"))
        	{
        		notify.audioStreamType = AudioManager.STREAM_NOTIFICATION;
                notify.sound = Uri.parse("android.resource://com.example.webbrowser/" + R.raw.perfect_notification);
        	}
        	if (setRingtone.equals("droid"))
        	{
        		notify.audioStreamType = AudioManager.STREAM_NOTIFICATION;
                notify.sound = Uri.parse("android.resource://com.example.webbrowser/" + R.raw.droid2);
        	}
        	if (setRingtone.equals("galaxy"))
        	{
        		notify.audioStreamType = AudioManager.STREAM_NOTIFICATION;
                notify.sound = Uri.parse("android.resource://com.example.webbrowser/" + R.raw.notification);
        	}
        	if (setRingtone.equals("piano"))
        	{
        		notify.audioStreamType = AudioManager.STREAM_NOTIFICATION;
                notify.sound = Uri.parse("android.resource://com.example.webbrowser/" + R.raw.instrumental);
        	}
            	
	        //vibrate when notification received
	        notify.vibrate = new long[] { 200, 600, 200, 200, 600,};
	            
	        /*** STEP 3: Create a PendingIntent ***/
	        Intent toLaunch = new Intent(MainActivity.this, MainActivity.class);
	        
	        //pass in the URL to the MainActivity to be loaded when the notification is clicked on
	        toLaunch.putExtra("EnteredUrl", userNotifyUrl);
	        
	        //start MainActivity
	        PendingIntent intentBack = PendingIntent.getActivity(MainActivity.this, 0, toLaunch, PendingIntent.FLAG_CANCEL_CURRENT);
	            
	        /*** STEP 4: Register the intent with the notification ***/
	        notify.setLatestEventInfo(MainActivity.this, userEventName, "Tap to load the webpage for your event", intentBack);
	            
	        /*** STEP 5: Notify the user ***/
	        notifier.notify(NOTIFY_6, notify);
            
        } 
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent mainAction = new Intent(getApplicationContext(), Settings.class);
			mainAction.putExtra("mainKey", 1);
			startActivity(mainAction);
			return true;
			
		case R.id.back_button:
			if(myWebView.canGoBack())
			{
				myWebView.goBack();
				cancelPage.setVisibility(View.VISIBLE);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "no previous webpage!", Toast.LENGTH_LONG).show();
			}
			return true;
			
		case R.id.forward_button:
			if(myWebView.canGoForward())
			{
				myWebView.goForward();
				cancelPage.setVisibility(View.VISIBLE);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "already at most recent page!", Toast.LENGTH_LONG).show();
			}
			return true;
			
		case R.id.bookmarks_button:
			Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
			url = webAddress.getText().toString();
			
			if (url.length() == 0)
			{
				Toast.makeText(getApplicationContext(), "Cannot save url, you must enter a url to save", Toast.LENGTH_LONG).show();
			}
			else
			{
				intent.putExtra("savedURL", url);
				startActivity(intent);
				Toast.makeText(this, "saved " + url, Toast.LENGTH_LONG).show();
			}
		    return true;
		
		case R.id.about_app:
			Intent newAction = new Intent(getApplicationContext(), AboutApp.class);
			newAction.putExtra("mainKey", 1);
			startActivity(newAction);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}