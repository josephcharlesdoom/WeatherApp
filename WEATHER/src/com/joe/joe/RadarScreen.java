package com.joe.joe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.joe.joe.utility.SimpleGestureFilter;
import com.joe.joe.weather.WeatherAPI;

public class RadarScreen extends Activity implements SimpleGestureFilter.SimpleGestureListener
{
	private WeatherAPI wapi;
	private SimpleGestureFilter detector;
	private ImageView radarImage;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radar);
        detector = new SimpleGestureFilter(this, this);
        
        radarImage = (ImageView) findViewById(R.id.IVradar);
        
        wapi = WeatherAPI.getAPI();
        
        wapi.getAnimatedRadar(new WeatherAPI.Callback() {
			
			@Override
			public void onFinish(Object result) {
				final Bitmap bitmap = (Bitmap) result;
				
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						radarImage.setImageBitmap(bitmap);
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
		case SimpleGestureFilter.SWIPE_RIGHT:
			startActivity(new Intent(this, MainActivity.class));
			break;
		}
		
	}

	@Override
	public void onDoubleTap() 
	{
		
		
	}

}
