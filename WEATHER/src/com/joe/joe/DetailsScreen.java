package com.joe.joe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.joe.joe.utility.SimpleGestureFilter;
import com.joe.joe.weather.Conditions;
import com.joe.joe.weather.WeatherAPI;

public class DetailsScreen extends Activity implements SimpleGestureFilter.SimpleGestureListener
{

	private SimpleGestureFilter detector;
	private WeatherAPI wapi;
	private TextView location, currentCon, temperature, feels,
			wind, humid, dewP, windChill;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        wapi = WeatherAPI.getAPI();
        
        detector = new SimpleGestureFilter(this, this);
        location = (TextView) findViewById(R.id.textLocation);
        currentCon = (TextView) findViewById(R.id.textCurrentCondition);
        temperature = (TextView) findViewById(R.id.textTemperature);
        feels = (TextView) findViewById(R.id.textFeelsLike);
        wind = (TextView) findViewById(R.id.textWind);
        humid = (TextView) findViewById(R.id.textHumidity);
        dewP = (TextView) findViewById(R.id.textDewPoint);
        windChill = (TextView) findViewById(R.id.textWindChill);
        
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
						location.setText(currentConditions.location.city + ", " + currentConditions.location.state);
						currentCon.setText(currentConditions.description);
						temperature.setText(String.format("%2d", Math.round(Double.parseDouble(currentConditions.temperature))));
						feels.setText("Feels like: " + currentConditions.feelsLike + " degrees");
						wind.setText("Wind Gust: " + currentConditions.wind + " mph");
						humid.setText("Humidity: " + currentConditions.humidity );
						dewP.setText("Dew Point: " + currentConditions.dewPoint + " degrees");
						windChill.setText("Wind Chill: " + currentConditions.windChill);
						
					}
					
				});
			}
        });
    }

    
	@Override
	public boolean dispatchTouchEvent(MotionEvent me) 
	{
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);

	}
	
	@Override
	public void onSwipe(int direction) 
	{
		switch(direction)
		{
		case SimpleGestureFilter.SWIPE_DOWN:
			break;
		}
		
	}

	@Override
	public void onDoubleTap() {
		startActivity(new Intent(this, MainActivity.class));
		
	}

}