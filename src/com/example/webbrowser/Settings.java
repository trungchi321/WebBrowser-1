package com.example.webbrowser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Settings extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) { 
            // Activity was brought to front and not created, 
            // Thus finishing this will get us to the last viewed activity 
            finish(); 
            return; 
        } 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.goBackWindow:
			Intent manageBookmarks = getIntent();
			
			Bundle extras = manageBookmarks.getExtras();
			if (extras != null)
			{
				if (extras.getInt("mainKey") == 1)
				{
					startActivity(new Intent(getApplicationContext(),
							com.example.webbrowser.MainActivity.class));
					Toast.makeText(this, "going back to browsing", Toast.LENGTH_LONG).show();
				}
				if (extras.getInt("bkmrkKey") == 1)
				{
					startActivity(new Intent(getApplicationContext(),
							com.example.webbrowser.BookmarkActivity.class));
					Toast.makeText(this, "returning to bookmarks", Toast.LENGTH_LONG).show();
				}
				if (extras.getInt("aboutKey") == 1)
				{
					startActivity(new Intent(getApplicationContext(),
							com.example.webbrowser.AboutApp.class));
					Toast.makeText(this, "returning to about app", Toast.LENGTH_LONG).show();
				}
			}
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
