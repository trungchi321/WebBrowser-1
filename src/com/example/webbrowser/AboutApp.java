package com.example.webbrowser;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AboutApp extends Activity {

	protected WebView aboutWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_app);
		
		ActionBar actionBar = getActionBar();
	    actionBar.setTitle("About CheetahWeb");
	    
	    aboutWebView = (WebView) findViewById(R.id.aboutVideo);
		aboutWebView.getSettings().setJavaScriptEnabled(true);
		aboutWebView.setWebChromeClient(new WebChromeClient());
		
		aboutWebView.setWebViewClient(new WebViewClient()       
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				return false;
			}
		});
		aboutWebView.loadUrl("http://www.youtube.com/watch?v=PJddS2sbL0k");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_app, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent aboutAction = new Intent(getApplicationContext(), Settings.class);
			aboutAction.putExtra("aboutKey", 1);
			startActivity(aboutAction);
			return true;
			
		case R.id.backActivity:
			Intent manageActivities = getIntent();
			
			Bundle extras = manageActivities.getExtras();
			if (extras == null)
			{
				//by default load the main activity if there is nothing to pass
				startActivity(new Intent(getApplicationContext(),
						com.example.webbrowser.MainActivity.class));
				Toast.makeText(getApplicationContext(), "returning to homepage", Toast.LENGTH_LONG).show();
			}
			if (extras != null)
			{
				if (extras.getInt("mainKey") == 1)
				{
					startActivity(new Intent(getApplicationContext(),
							com.example.webbrowser.MainActivity.class));
					Toast.makeText(this, "returning to homepage", Toast.LENGTH_LONG).show();
				}
				if (extras.getInt("bookKey") == 1)
				{
					startActivity(new Intent(getApplicationContext(),
							com.example.webbrowser.BookmarkActivity.class));
					Toast.makeText(this, "returning to bookmarks", Toast.LENGTH_LONG).show();
				}
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
