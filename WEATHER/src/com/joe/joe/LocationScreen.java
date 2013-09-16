package com.joe.joe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.joe.joe.utility.SimpleGestureFilter;

public class LocationScreen extends Activity implements SimpleGestureFilter.SimpleGestureListener
{

	private SimpleGestureFilter detector;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        detector = new SimpleGestureFilter(this, this);
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
		case SimpleGestureFilter.SWIPE_UP:
			startActivity(new Intent(this, MainActivity.class));
			break;
		}
		
	}

	@Override
	public void onDoubleTap() {
		// TODO Auto-generated method stub
		
	}

}
