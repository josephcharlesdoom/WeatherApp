package com.joe.joe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.joe.utility.SimpleGestureFilter;

public class NewLocationScreen extends Activity implements SimpleGestureFilter.SimpleGestureListener, ActionListener
{
	Button submit;
	EditText newPlace;
	TextView test;
	private ListView listLocations;
	public static String filename = "mySharedString";
	SharedPreferences prefs;
	private ArrayList<String> locations;

	private SimpleGestureFilter detector;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);
        
        View mainLayout = findViewById(R.id.newLoc);
		mainLayout.setBackgroundResource(R.drawable.happy);
	    
        

        detector = new SimpleGestureFilter(this,this);
        submit = (Button) findViewById(R.id.Bsubmit);
        newPlace = (EditText) findViewById(R.id.ETnewPlace);
        test = (TextView) findViewById(R.id.textView1);
        listLocations = (ListView) findViewById(R.id.listLocations);
        
        
        prefs = getSharedPreferences("profile", 0);
        locations = new ArrayList<String>(prefs.getStringSet("locations", Collections.<String> emptySet()));
        

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locations);
        listLocations.setAdapter(adapter);
		
        listLocations.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3) 
			{
				locations.remove(pos);
				adapter.notifyDataSetChanged();
				
				SharedPreferences.Editor editor = prefs.edit();
				editor.putStringSet("locations", new HashSet<String>(locations));
				editor.commit();
				
				MainActivity.resetLocations(locations);
				
                return true;
			}
        }); 
 
        
	}
	
	public void onAddLocation(View v) 
	{
		String location = newPlace.getText().toString();
		SharedPreferences.Editor editor = prefs.edit();
		locations.add(location);
		
		editor.putStringSet("locations", new HashSet<String>(locations));
		editor.commit();
		((ArrayAdapter<String>)listLocations.getAdapter()).notifyDataSetChanged();
		MainActivity.resetLocations(locations);

		Intent intent = new Intent(this, MainActivity.class);
		//intent.putExtra("location", locations[curLocationIndex]);
		intent.putExtra("location", location);
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);

	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
        
        
    }

	@Override
	public void onSwipe(int direction) 
	{
		switch(direction) 
		{
		
			
		case SimpleGestureFilter.SWIPE_UP:
			startActivity(new Intent(this, MainActivity.class));
			break;
			
		
		}
			
	}
	  
	@Override
	public void onDoubleTap() {
		
	}

	@Override
	public void onFailure(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		
	}
}
