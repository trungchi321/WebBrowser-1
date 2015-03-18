package com.example.webbrowser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class BookmarkActivity extends Activity {
	
	private SQLiteDatabase bookmarkDatabase;
	@SuppressWarnings("rawtypes")
	protected ArrayAdapter adapter;
	ArrayList<String> list;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		
		ActionBar actionBar = getActionBar();
	    actionBar.setTitle("Bookmarks");
	    
		//Initialize the database
		initializeDatabase();
		//update the database when bookmarks added or deleted
		updateBookmarkList();
		
		Intent manageBookmarks = getIntent();
	    String savedBkmrk = manageBookmarks.getStringExtra("savedURL");
	    
	    if (savedBkmrk != null)
	    {
	    	ContentValues cv = new ContentValues();
	    	cv.put("bookMarkValue", savedBkmrk);
	    	bookmarkDatabase.insert("tbl_bkmrks", null, cv);
			updateBookmarkList();
		}
		
		final ListView listContent = (ListView)findViewById(R.id.DatabaseContent);
		
		Toast bkmrkHelp = Toast.makeText(getApplicationContext(), "You can delete an individual bookmark by tapping and holding the desired url", Toast.LENGTH_LONG);
		bkmrkHelp.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, 0);
		bkmrkHelp.show();
		
		listContent.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				String passURL;
				passURL = listContent.getItemAtPosition(position).toString();
				Intent loadUrl = new Intent(getApplicationContext(), MainActivity.class);
				loadUrl.putExtra("EnteredUrl", passURL);
				startActivity(loadUrl);
			}
		});
		
		listContent.setOnItemLongClickListener(new OnItemLongClickListener() {
		    @Override
		    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long id) {
		    		  String str = listContent.getItemAtPosition(pos).toString();
		    		  bookmarkDatabase.delete("tbl_bkmrks", "bookMarkValue=?", new String[] {str});
		    		  updateBookmarkList();
		              Toast.makeText(getApplicationContext(), str + " deleted", Toast.LENGTH_LONG).show();

		        return true;
		    }

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bookmark, menu);
		return true;
	}
	
	public void insertBookmarks(View v) {
		final EditText bookmark = (EditText) findViewById(R.id.typedBookmark);
		
		ContentValues cv = new ContentValues();
		String enteredURL = bookmark.getText().toString();
		if (!enteredURL.equals(""))
		{
			cv.put("bookMarkValue", bookmark.getText().toString());
			bookmarkDatabase.insert("tbl_bkmrks", null, cv);
			updateBookmarkList();
		}
		Toast.makeText(getApplicationContext(), "Type a bookmark to add or bookmark already added", Toast.LENGTH_LONG).show();
	}
	
/********** Private method to display contents of db in TextView 
 * @return 
 * @return ***********/
	
	private void updateBookmarkList() {
		
		try {
			Cursor c = bookmarkDatabase.query("tbl_bkmrks", null, null, null, null, null, null);
			
			SharedPreferences prefs = getPreferences(MODE_PRIVATE);
			TreeSet<String> treeSet = new TreeSet<String>();
			list = new ArrayList<String>(prefs.getStringSet("list", treeSet));

			String insertBookmarkDatabase = "";
			c.moveToFirst();
			while (c.isAfterLast() == false) {
				for (int i = 1; i < c.getColumnCount(); i++)
				{
					insertBookmarkDatabase = c.getString(i);
					if (!list.contains(insertBookmarkDatabase))
					{
						list.add(insertBookmarkDatabase);
					}
				}
				c.moveToNext();
			}
			
			ListView bookmarkView = (ListView) findViewById(R.id.DatabaseContent);
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, list);
			bookmarkView.setAdapter(arrayAdapter);
			
			SharedPreferences orderBkmrks = PreferenceManager.getDefaultSharedPreferences(this);
			boolean bkmrkSequence = orderBkmrks.getBoolean("controlBookmarkViewSeq", true);
			if (bkmrkSequence == true)
			{
				arrayAdapter.sort(new Comparator<String>(){
					public int compare(String object1, String object2){
						return object1.compareToIgnoreCase(object2);
					};
				});
			}
			else
			{
				
			}
			c.close();
			
		} catch (Exception e) {
		}
	}
	
	/********** Public methods to retrieve SQLiteDatabase object **********/
	
	public SQLiteDatabase initializeDatabase() {
		bookmarkDatabase = openOrCreateDatabase(
				"bookmarks.db",
				SQLiteDatabase.CREATE_IF_NECESSARY,
				null);
		
		final String CREATE_TREK_TABLE = 
		"CREATE TABLE tbl_bkmrks (id INTEGER PRIMARY KEY AUTOINCREMENT, bookMarkValue TEXT);";
		
		try {
			bookmarkDatabase.execSQL(CREATE_TREK_TABLE);
		} catch (Exception e) {
			Log.d("SQLiteDemo", "tbl_bkmrks table already exists");
		}

		return bookmarkDatabase;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    case R.id.return_button:
			startActivity(new Intent(getApplicationContext(),
					com.example.webbrowser.MainActivity.class));
			Toast.makeText(this, "returning to homepage", Toast.LENGTH_LONG).show();
			return true;
			
	    case R.id.settings:
			Intent bkmrkAction = new Intent(getApplicationContext(), Settings.class);
			bkmrkAction.putExtra("bkmrkKey", 1);
			startActivity(bkmrkAction);
			return true;
			
	    case R.id.deleteURLs:
	    	bookmarkDatabase.delete("tbl_bkmrks", null, null);
			Toast.makeText(getApplicationContext(), "Bookmarks Deleted", Toast.LENGTH_LONG).show();
			updateBookmarkList();
			return true;

	    case R.id.about_app:
			Intent newAction = new Intent(getApplicationContext(), AboutApp.class);
			newAction.putExtra("bookKey", 1);
			startActivity(newAction);
			return true;
			
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
