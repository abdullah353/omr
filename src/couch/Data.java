package couch;

import java.util.Calendar;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Data {
	private JsonObject templ;
	public Data(){
		templ = new JsonObject();
	}
	
	public void addQuestion(String name, String value){
		templ.addProperty(name,value);
	}
	public void addcomp(String com,long end,Double speed,Double head,Double altia,Double acu,Double alt, Double lon, Double lat){
		templ.addProperty("comment", com);
		templ.addProperty("end_time", end);
		JsonObject gps = new JsonObject();
		gps.addProperty("speed", speed);
		gps.addProperty("heading", head);
		gps.addProperty("altitudeAccuracy", altia);
		gps.addProperty("accuracy", acu);
		gps.addProperty("altitude", alt);
		gps.addProperty("longitude", lon);
		gps.addProperty("latitude", lat);
		templ.add("gps", gps);
	}
	public void addtime(){
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String month = theMonth(now.get(Calendar.MONTH)); // Note: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		//int second = now.get(Calendar.SECOND);
		//int millis = now.get(Calendar.MILLISECOND);
		templ.addProperty("year", year);
		templ.addProperty("month", month);
		templ.addProperty("day", day);
		templ.addProperty("time", hour+":"+minute);
	}

	public void addsubname(String name){
		String str = "{\"labels\":[\"Name\"],\"location\":[\""+name+"\"]}";
		System.out.println(str);
		JsonParser parser = new JsonParser();
		templ = (JsonObject)parser.parse(str);
	}
	public JsonObject toJson(){
		return templ;
	}
	
	public JsonElement jsonel(String val){
		JsonObject json = new JsonObject();
		json.addProperty("string", val);
		return json.get("string");
	}
	public JsonElement jsonel(double val){
		JsonObject json = new JsonObject();
		json.addProperty("double", val);
		return json.get("double");
	}
	public JsonElement jsonel(int val){
		JsonObject json = new JsonObject();
		json.addProperty("int", val);
		return json.get("int");
	}
	public static String theMonth(int month){
		String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		return monthNames[month];
	}
}
