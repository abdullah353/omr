package helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Options {
	private List<JsonObject> options,
			grabedopts,sorted;
	private int[] ar,arx,ary;
	private JsonArray lines;
	private JsonObject objopts,optsbyline;
	public Options(List<JsonObject> gotopts,int[] arx,int[] ary){
		grabedopts = new ArrayList<JsonObject>();
		sorted = new ArrayList<JsonObject>();
		options = new ArrayList<JsonObject>();
		objopts = new JsonObject();
		this.arx = arx.clone();
		this.ary = ary.clone();
		grabedopts.addAll(gotopts);
	}
	public void organise(){
		//sorting By y point
		sorted = sortby(grabedopts,removeDuplicates(ary),"y");
		System.out.println(sorted.toString());
		//Detecting Number of lines 
		lines = linesy();
		System.out.println(lines.toString());
		//Removing Lines that are expected wrong
		lines = fxlinesy();
		System.out.println(lines.toString());
		//Organizing all options circle according to line number
		optsbyline = byline();
		System.out.println(optsbyline.toString());
		//Fix Missing y circles here then go further
		
		
	
	}
	private JsonArray fxlinesy() {
		JsonArray ret = new JsonArray();
		for (int i = 0; i < lines.size(); i++) {
			if(i+1 != lines.size()){
				int sub = lines.get(i+1).getAsInt() -lines.get(i).getAsInt();
				//System.out.println(lines.get(i+1).getAsInt() +"-"+lines.get(i).getAsInt()+" = "+sub);
				if(sub <=65 && sub >=45){
					//System.out.println("ADDED"+sub);
					//ret.add(lines.get(i+1));
					//ret.add(lines.get(i));
					if(ret.size() == 0){
						ret.add(lines.get(i));
						ret.add(lines.get(i+1));
					}else{
						ret.add(lines.get(i+1));
					}
				}
			}
		}
		//return removeDuplicates(ret);
		return ret;
	}
	private JsonArray removeDuplicates(JsonArray in) {
		JsonArray ret = new JsonArray();
		int[] tmp = new int[in.size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = in.get(i).getAsInt();
		}
		tmp = removeDuplicates(tmp);
		JsonObject tmp2 = new JsonObject();
		for (int i = 0; i < tmp.length; i++) {
			tmp2.addProperty(""+i,tmp[i] );
			ret.add(tmp2.get(""+i));
		}
		return ret;
	}
	private JsonObject byline() {
		JsonObject ret = new JsonObject();
		//System.out.println("Organizing Options for line "+lines.size());
		for (int i = 0; i < lines.size(); i++) {
			JsonArray subl = new JsonArray();
			int curline = lines.get(i).getAsInt();
			//System.out.println("Organizing Options for line "+curline);
			for (int j = 0; j < sorted.size(); j++) {
				//System.out.println(Math.abs(curline - sorted.get(j).get("y").getAsInt()));
				if(Math.abs(curline - sorted.get(j).get("y").getAsInt()) <= 12){
					subl.add(sorted.get(j));
				}
			}
			//System.out.println(subl.toString());
			ret.add(""+i, subl);
		}
		return ret;
	}
	public void indexit(int minradi, int maxradi,int ques) {
		minradi =12;
		maxradi = 28;
		ques = 5;
		int 	stc = sorted.get(0).get("y").getAsInt(),
				stn = sorted.get(1).get("y").getAsInt();
		
		//System.out.println("First two t points are"+stc+" "+stn);
		JsonObject qsopts = new JsonObject();
		for (int i = 0; i < ques; i++) {
			//System.out.println("Making Question Num"+i);
			JsonObject qopts = new JsonObject();
			JsonObject opts  = new JsonObject();
			int optc = 0;
			for (int j = 0; j < sorted.size(); j++) {
				//System.out.println("Checking Seq to"+stc);	
				
				JsonObject str = sorted.get(j);
				
				int min = str.get("y").getAsInt() - stc;
				//System.out.println("if "+min+" <= "+minradi);
				if( min<= minradi && min >=0){
					
					qopts.add(""+optc,str);
					//System.out.println(qopts.toString());
					optc++;
				}else{
				}
				
				if(optc == 6) {
					if(j+1 < sorted.size()){
						stc = sorted.get(j+1).get("y").getAsInt();
						JsonObject cach = new JsonObject();
						cach = qopts;
						//System.out.println("ADDING TO DUMP"+qopts.toString());
						objopts.add(""+i,qopts);
						qopts = new JsonObject();
						//System.out.println(cach.toString());
						//System.out.println(qopts.toString());
						//System.out.println("Changing Seq to"+stc);
					}
				}
				
			}
			//System.out.println("Question Num"+i+ " opts are"+qopts.toString());
		}
		
	}
	private JsonArray linesy() {
		JsonArray l = new JsonArray();
		l.add(sorted.get(0).get("y"));
		for (int i = 0; i < sorted.size(); i++) {
			if(i+1 != sorted.size()){
				int sub = sorted.get(i+1).get("y").getAsInt() -sorted.get(i).get("y").getAsInt();
				//System.out.println(sorted.get(i+1).get("y").getAsInt() +"-"+sorted.get(i).get("y").getAsInt());
				if(sub >=12){
					//System.out.println("ADDED"+sub);
					l.add(sorted.get(i+1).get("y"));
				}
			}
		}
		return l;
	}
	public void addoption(JsonObject opt){
		options.add(opt);
	}
	private List<JsonObject> sortby(List<JsonObject> points, int[] ar,String index) {
		List<JsonObject> ret = new ArrayList<JsonObject>();
		for (int i = 0; i < ar.length; i++) {
			if(ar[i]!=0){
				for (int j = 0; j < points.size(); j++) {
					if(points.get(j).get(index).getAsInt() == ar[i]){
						ret.add(points.get(j));
					}
				}
			}
		}
		return ret;
	}
	public static int[] removeDuplicates(int[] arr) {
		Set<Integer> alreadyPresent = new HashSet<Integer>();
		int[] whitelist = new int[0];
		for (int nextElem : arr) {
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		Arrays.sort(whitelist);
		return whitelist;
	}
}
