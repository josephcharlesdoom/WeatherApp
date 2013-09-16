package com.joe.joe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.joe.utility.SimpleGestureFilter;
import com.joe.joe.weather.Conditions;
import com.joe.joe.weather.WeatherAPI;
import com.joe.joe.weather.WeatherAPI.Callback;

@SuppressLint("NewApi")
/**
 * 
 * This class is used to show the weather condition of a city in the future 10 days.
 * 
 * @author Patrick Brown, Liudong Zuo
 * @date 11-26-2012
 *
 */
public class ExtendedDayScreen extends ListActivity implements SimpleGestureFilter.SimpleGestureListener {

	private SimpleGestureFilter detector;
	private WeatherAPI wapi;

	public void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.extended_day);

		detector = new SimpleGestureFilter(this, this);
		wapi = WeatherAPI.getAPI();

		final Context context = this;

		wapi.getForecastConditions(new Callback() {

			@Override
			public void onFinish(Object result) {
				Conditions[] conditions = (Conditions[]) result;
				final ConditionsAdapter adapter = new ConditionsAdapter(
						context, conditions);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setListAdapter(adapter);
						getListView().setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
				});
			}

		});
	}

	private class ConditionsAdapter extends ArrayAdapter<Conditions> {

		private Context context;
		private Conditions[] forecastdays;

		public ConditionsAdapter(Context context, Conditions[] forecastdays) {
			super(context, R.layout.extended_day_screen_item, forecastdays);
			this.forecastdays = forecastdays;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			View rowView = inflater.inflate(R.layout.extended_day_screen_item,
					parent, false);
			
			ImageView icon = (ImageView) rowView.findViewById(R.id.imageWeather);
			TextView textDate = (TextView) rowView.findViewById(R.id.textDate);
			TextView textConditions = (TextView) rowView.findViewById(R.id.textConditions);
			TextView textTemperature = (TextView) rowView.findViewById(R.id.textTemperature);
			

			Conditions conditions = forecastdays[position];
			icon.setImageBitmap(conditions.icon);
			textDate.setText(conditions.date);
			Integer chance = Integer.parseInt(conditions.percentage);
			String chanceStr = (chance == 0 ? "" : " ( Chance of Precip: " + conditions.percentage + "%)");
			textConditions.setText(conditions.description +chanceStr);
			textTemperature.setText("Low:" + conditions.lowTemperature + "  High: " + conditions.temperature);
			
			return rowView;
		}

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		// Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;

	}

	@Override
	public void onSwipe(int direction) {
		switch (direction) {
//		case SimpleGestureFilter.SWIPE_RIGHT:
//			startActivity(new Intent(this, ExtendedDayScreen.class));
//			break;

		case SimpleGestureFilter.SWIPE_LEFT:
			startActivity(new Intent(this, MainActivity.class));
			break;

//		case SimpleGestureFilter.SWIPE_DOWN:
//			startActivity(new Intent(this, LocationScreen.class));
//			break;
//
//		case SimpleGestureFilter.SWIPE_UP:
//			startActivity(new Intent(this, DetailsScreen.class));
//			break;
		}

	}

	@Override
	public void onDoubleTap() {

	}
}
