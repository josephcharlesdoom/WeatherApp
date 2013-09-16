package com.joe.joe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.joe.utility.SimpleGestureFilter;
import com.joe.joe.weather.Conditions;
import com.joe.joe.weather.WeatherAPI;

public class MainActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener, LocationListener {
	
	private TextView TVlocation, TVcurrentCon, TVtemperature,TVtime,TVmessage, deg, highT, lowT, H, L;
	private WeatherAPI wapi;
	private String provider;
	private SimpleGestureFilter detector;
	private LocationManager locationManager;
	private double LA, LO;
	
	static private ArrayList<String> locs = new ArrayList<String>();
	private static int curLocationIndex= -1;
	
	private SharedPreferences prefs;
	
	public static void resetLocations(Collection<String> locations) {
		locs = new ArrayList<String>(locations);
		curLocationIndex = locs.size() - 1;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);	
        prefs = getSharedPreferences("profile", 0);
        
        if (locs.isEmpty()) {
        	
            resetLocations(prefs.getStringSet("locations", Collections.<String> emptySet()));
            curLocationIndex = -1;
        }
        
        
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        detector = new SimpleGestureFilter(this,this);
        
        TVlocation = (TextView) findViewById(R.id.textLocation);
        TVcurrentCon = (TextView) findViewById(R.id.textCurrentCondition);
        TVtemperature = (TextView) findViewById(R.id.textTemperature);
        TVtime = (TextView) findViewById(R.id.TVtime);
        TVmessage = (TextView) findViewById(R.id.TVmessage);
        deg = (TextView) findViewById(R.id.TVdeg);
        highT = (TextView) findViewById(R.id.TVhi);
        lowT = (TextView) findViewById(R.id.TVlo);
        H = (TextView) findViewById(R.id.TVh);
        L = (TextView) findViewById(R.id.TVl);
        
        
        
        
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        
        

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        if (intent.hasExtra("location"))
        {
        	Toast toast = Toast.makeText(this, "Location is " + extras.getString("location"), Toast.LENGTH_SHORT);
        	toast.show();
        	wapi = WeatherAPI.getAPI(extras.getString("location"));
        }
        else if (curLocationIndex >= 0)
        {
        	//Toast toast = Toast.makeText(this, "Location is " + locations[curLocationIndex], Toast.LENGTH_SHORT);
        	Toast toast = Toast.makeText(this, "Location is " + locs.get(curLocationIndex), Toast.LENGTH_SHORT);
        	toast.show();
        	
    		//wapi = WeatherAPI.getAPI(locations[curLocationIndex]);
        	wapi = WeatherAPI.getAPI(locs.get(curLocationIndex));
        }
        else
        {
        	Toast toast = Toast.makeText(this, "Location is gps", Toast.LENGTH_SHORT);
        	toast.show();
        	 provider = locationManager.getBestProvider(criteria, false);
             Location location = locationManager.getLastKnownLocation(provider);
             onLocationChanged(location);
             curLocationIndex = -1;
        }
        
        
        
        
        wapi.getMessage(new WeatherAPI.Callback() 
        {
			
			@Override
			public void onFinish(Object result) 
			{
				final Conditions currentConditions = (Conditions)result;
				
				runOnUiThread(new Runnable()
				{

					@Override
					public void run() 
					{
						TVmessage.setText(currentConditions.message);
						//highT.setText(currentConditions.temperature);
						//lowT.setText(currentConditions.lowTemperature);
					}
					
				});
			}
		});
        
        wapi.getCurrentConditions(new WeatherAPI.Callback() 
        {
			
			@Override
			public void onFinish(Object result) 
			{
				final Conditions currentConditions = (Conditions)result;
				
				runOnUiThread(new Runnable()
				{

					@Override
					public void run() 
					{
						TVlocation.setText(currentConditions.location.city + ", " + currentConditions.location.state);
						TVcurrentCon.setText(currentConditions.description);
						TVtemperature.setText(String.format("%2d", Math.round(Double.parseDouble(currentConditions.temperature))));
						TVtime.setText(currentConditions.updateTime);
						
						
						
						
						View mainLayout = findViewById(R.id.mainLayout);
						
						if(currentConditions.description.equals("Mostly Cloudy"))
						{
							mainLayout.setBackgroundResource(R.drawable.mostly_cloudy);
					    }
						else if(currentConditions.description.equals("Clear"))
						{
							mainLayout.setBackgroundResource(R.drawable.clear);
						}
						else if(currentConditions.description.equals("Rainy"))
						{
							mainLayout.setBackgroundResource(R.drawable.rainy);
						}
						else if(currentConditions.description.equals("Light Rain"))
						{
							mainLayout.setBackgroundResource(R.drawable.rainy);
						}
						else if(currentConditions.description.equals("Overcast"))
						{
							mainLayout.setBackgroundResource(R.drawable.clear);
							TVtemperature.setTextColor(Color.WHITE);
							TVtime.setTextColor(Color.WHITE);
							deg.setTextColor(Color.WHITE);
							highT.setTextColor(Color.WHITE);
							lowT.setTextColor(Color.WHITE);
							H.setTextColor(Color.WHITE);
							L.setTextColor(Color.WHITE);
							TVcurrentCon.setTextColor(Color.WHITE);
							TVlocation.setTextColor(Color.WHITE);
							
						}
						else if(currentConditions.description.equals("Scattered Clouds"))
						{
							mainLayout.setBackgroundResource(R.drawable.partly_cloudy);
							TVtemperature.setTextColor(Color.WHITE);
							deg.setTextColor(Color.WHITE);
						}
						else if(currentConditions.description.equals("Light Snow"))
						{
							mainLayout.setBackgroundResource(R.drawable.light_snow);
						}
						
						
						
					}
					
				});
			}
		});
        
        
        
        
        wapi.getForecastConditions(new WeatherAPI.Callback() 
        {
			
			@Override
			public void onFinish(Object result) 
			{
				final Conditions[] currentConditions = (Conditions[])result;
				
				runOnUiThread(new Runnable()
				{

					@Override
					public void run() 
					{
						lowT.setText(currentConditions[0].lowTemperature);
						highT.setText(currentConditions[0].temperature);
					}
				});
			}
        });
       
        
       
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
		case SimpleGestureFilter.SWIPE_RIGHT:
			startActivity(new Intent(this, ExtendedDayScreen.class));
			break;
			
		case SimpleGestureFilter.SWIPE_LEFT:
			startActivity(new Intent(this, RadarScreen.class));
			break;
			
		case SimpleGestureFilter.SWIPE_UP:
			
			
			if (curLocationIndex <= -1)
				break;
			else if (curLocationIndex == 0)
			{
				curLocationIndex--;
				startActivity(new Intent(this, MainActivity.class));
				finish();
			}
			else
			{
				curLocationIndex--;
				Intent intent = new Intent(this, MainActivity.class);
				//intent.putExtra("location", locations[curLocationIndex]);
				intent.putExtra("location", locs.get(curLocationIndex));
				startActivity(intent);
				finish();
			}

			break;
			
		case SimpleGestureFilter.SWIPE_DOWN:
			
			//if (curLocationIndex+1 >= locations.length)
			if (curLocationIndex+1 >= locs.size())
				startActivity(new Intent(this, NewLocationScreen.class));
			else
			{
				curLocationIndex++;
				Intent intent = new Intent(this, MainActivity.class);
				//intent.putExtra("location", locations[curLocationIndex]);
				intent.putExtra("location", locs.get(curLocationIndex));
				startActivity(intent);
				finish();
			}
			
			break;
		}
			
	}

//	 /* Request updates at startup */
//	  @Override
//	  protected void onResume() {
//	    super.onResume();
//	    if (curLocationIndex == -1)
//	    	locationManager.requestLocationUpdates(provider, 400, 1, this);
//	  }
	   
	  
	  /* Remove the locationlistener updates when Activity is paused */
	  @Override 
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }
	  
	  
	@Override
	public void onDoubleTap() {
		startActivity(new Intent(this, DetailsScreen.class));
		
	}

	@Override
	public void onLocationChanged(Location location) 
	{
		double latNum = (double) (location.getLatitude());
	    double lngNum = (double) (location.getLongitude());
	    
	    LA = latNum;
        LO = lngNum;
	    
        wapi = WeatherAPI.getAPI(LA + "," + LO);
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		   Toast.makeText(this, "Enabled new provider " + provider,
			        Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
			Toast.makeText(this, "Disabled provider " + provider,
					Toast.LENGTH_SHORT).show();
		
	}
}
