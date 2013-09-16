package com.joe.joe.weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WeatherAPI {
	
	final private static String BASE_URL = "http://api.wunderground.com/api/%s/%s/q/%s" ;
	
	private String apikey = "e33803bca24b5d11";
	private String query;
	

	
	private static HashMap<String,WeatherAPI> keyhash = new HashMap();
	private static WeatherAPI recentAPI;

	private WeatherAPI(String query) 
	{
		this.query = query.replaceAll(" ", "%20");
	}
	
	public static WeatherAPI getAPI(String query)
	{
		if(! keyhash.containsKey(query))
		{
			keyhash.put(query, new WeatherAPI(query));
		}
		recentAPI = keyhash.get(query);
		
		return recentAPI;
	
	}
	
	public static WeatherAPI getAPI()
	{
		return recentAPI;
	}
	
	private Document request(String action)
	{
		
		String url = String.format(BASE_URL + ".xml", apikey, action, query);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(url);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
		
	}
	
	
	
	private Bitmap requestAsImage(String action)
	{
		String url = String.format(BASE_URL + ".gif?%s", apikey, action, query, "radius=200&width=480&height=800");
		
		URL newurl;
		try {
			newurl = new URL(url);
			return BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
		} catch (MalformedURLException e) {

		} catch (IOException e) {
		
		}
		
		return null;
	}
	
	public void getAnimatedRadar(Callback cb)
	{
		final Callback fcb = cb;
		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				Bitmap bitmap = requestAsImage("animatedradar");
				fcb.onFinish(bitmap);
			}
			
		});
		
		t.start();
	}

	
	public void getCurrentConditions(Callback cb)
	{
		final Callback fcb = cb;
		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				Document doc = request("conditions");
				Conditions conditions = parseConditions(doc);
				conditions.location = parseLocation(doc);
				fcb.onFinish(conditions);
			}
			
		});
		
		t.start();
	}
	
	public void getMessage(Callback cb)
	{
		final Callback fcb = cb;
		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				Document doc = request("message");
				Conditions message = parseMessage(doc);
				fcb.onFinish(message);
			}
			
		});
		
		t.start();
	}
	
	public void getForecastConditions(Callback cb) {
		final Callback fcb = cb;

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Document doc = request("forecast10day");
				Conditions[] conditions = parseForecast(doc);
				fcb.onFinish(conditions);
			}

		});

		t.start();
	}
	
	
	private Location parseLocation(Document doc)
	{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		Location location = new Location();
		try {
			XPathExpression expr = xpath.compile("/response/current_observation/display_location/*");
			NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for(int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
				
				if (node.getNodeName().equals("city"))
					location.city = node.getTextContent();
				else if (node.getNodeName().equals("state"))
					location.state = node.getTextContent();
				else if (node.getNodeName().equals("zip"))
					location.zip = node.getTextContent();
				else if (node.getNodeName().equals("longitute"))
					location.longitude = node.getTextContent();
				else if (node.getNodeName().equals("latitude"))
					location.latitude = node.getTextContent();
				else if (node.getNodeName().equals("country"))
					location.country = node.getTextContent();
	
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return location;
	}
	
	
	
	private Conditions parseConditions(Document doc)
	{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		Conditions conditions = new Conditions();
		try {
			XPathExpression expr = xpath.compile("/response/current_observation/*");
			NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for(int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
				
				if (node.getNodeName().equals("weather"))
					conditions.description = node.getTextContent();
				else if (node.getNodeName().equals("temp_f"))
					conditions.temperature = node.getTextContent();
				else if (node.getNodeName().equals("feelslike_f"))
					conditions.feelsLike = node.getTextContent();
				else if (node.getNodeName().equals("wind_gust_mph"))
					conditions.wind = node.getTextContent();
				else if (node.getNodeName().equals("relative_humidity"))
					conditions.humidity = node.getTextContent();
				else if (node.getNodeName().equals("dewpoint_f"))
					conditions.dewPoint = node.getTextContent();
				else if (node.getNodeName().equals("windchill_f"))
					conditions.windChill = node.getTextContent();
				else if (node.getNodeName().equals("temp_low"))
					conditions.low = node.getTextContent();
				else if (node.getNodeName().equals("observation_time"))
					conditions.updateTime = node.getTextContent();
			
	
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conditions;
	}
	
	private Conditions[] parseForecast(Document doc) {
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		ArrayList<Conditions> conditionsList = new ArrayList<Conditions>(10);
		try {
			XPathExpression expr = xpath
					.compile("/response/forecast/simpleforecast/forecastdays/*");
			NodeList forecastList = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);

			for (int k = 0; k < forecastList.getLength(); k++) {
				Conditions conditions = new Conditions();
				conditionsList.add(conditions);
				Node forecast = forecastList.item(k);
				NodeList list = forecast.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					Node node = list.item(i);

					if (node.getNodeName().equals("date")) 
					{
						NodeList childrenList = node.getChildNodes();
						for (int j = 0; j < childrenList.getLength(); j++) 
						{
							Node child = childrenList.item(j);
							if (child.getNodeName().equals("pretty"))
								conditions.date = child.getTextContent();
						}
					
					
				
						
					} else if (node.getNodeName().equals("conditions")) 
					{
						conditions.description = node.getTextContent();
					} else if (node.getNodeName().equals("icon_url")) 
					{
						conditions.urlIcon = node.getTextContent();

						URL newurl;
						try {
							newurl = new URL(conditions.urlIcon);
							conditions.icon = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
						} catch (MalformedURLException e) {
						} catch (IOException e) {
						}

					} else if (node.getNodeName().equals("low")) {
						NodeList childrenList = node.getChildNodes();
						for (int j = 0; j < childrenList.getLength(); j++) {
							Node child = childrenList.item(j);
							if (child.getNodeName().equals("fahrenheit"))
								conditions.lowTemperature = child.getTextContent();
						}
					}else if (node.getNodeName().equals("high")) {
						NodeList childrenList = node.getChildNodes();
						for (int j = 0; j < childrenList.getLength(); j++) {
							Node child = childrenList.item(j);
							if (child.getNodeName().equals("fahrenheit"))
								conditions.temperature = child.getTextContent();
						}
					} else if (node.getNodeName().equals("pop")) {
						conditions.percentage = node.getTextContent();
					}
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return conditionsList.toArray(new Conditions[0]);
	}

	private Conditions parseMessage(Document doc)
	{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		Conditions condition = new Conditions();
		try {
			XPathExpression expr = xpath.compile("/response/forecast/txt_forecast/forecastdays/forecastday/*");
			NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for(int i=0; i<list.getLength(); i++) 
			{
				Node node = list.item(i);
				
				if (node.getNodeName().equals("fcttext"))
					condition.message = node.getTextContent();
				
	
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return condition;
	}
	
	public interface Callback {
		public void onFinish(Object result); 
	}
	
}
