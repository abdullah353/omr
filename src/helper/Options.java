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
			grabedopts,ysorted,xsorted;
	private int[] ar,arx,ary;
	private int ymaxoff,yminoff,xmaxoff,xminoff,xmaxgap,xmingap;
	private JsonArray rows,cols;
	private JsonObject objopts,optsbyline,optsbygroup;
	public JsonArray getrows(){
		return rows;
	}
	public JsonArray getcols(){
		return cols;
	}
	public Options(List<JsonObject> gotopts,int[] arx,int[] ary,int ymaxoff,int yminoff,int xmaxoff,int xminoff,int xmaxgap,int xmingap ){
		grabedopts = new ArrayList<JsonObject>();
		ysorted = new ArrayList<JsonObject>();
		xsorted = new ArrayList<JsonObject>();
		options = new ArrayList<JsonObject>();
		objopts = new JsonObject();
		this.arx = arx.clone();
		this.ary = ary.clone();
		grabedopts.addAll(gotopts);
		 this.ymaxoff=ymaxoff;
		 this.yminoff=yminoff;
		 this.xmaxoff=xmaxoff;
		 this.xminoff=xminoff;
		 this.xmaxgap=xmaxgap;
		 this.xmingap=xmingap;
		
	}
	public void organise(){
		//sorting By y point
		ysorted = sortby(grabedopts,removeDuplicates(ary),"y");
		xsorted = sortby(grabedopts,removeDuplicates(arx),"x");
		//System.out.println(xsorted.toString());
		//Detecting Number of lines 
		rows = indexes(ysorted, "y");
		cols = indexes(xsorted, "x");
		System.out.println("incRows ind "+rows.size()+" data "+rows.toString());
		System.out.println("incCols ind "+cols.size()+" data "+cols.toString());
		//Removing Lines that are expected wrong
		rows = fxindexes(rows, ymaxoff, yminoff);
		cols = fxindexes(cols, xmaxoff, xminoff,xmaxgap,xmingap);
		System.out.println("Fixed index "+rows.size()+" data "+rows.toString());
		System.out.println("Fixed index "+cols.size()+" data "+cols.toString());
		//Organizing all options circle according to line number
		//optsbyline = byline();
		//System.out.println(optsbyline.toString());
		//Fix Missing y circles here then go further
		
		//expecting all y circles now move on to divide on groups
		//optsbygroup = bygroup();
		
	}
	private JsonObject bygroup() {
		JsonObject ret = new JsonObject();
		List<JsonObject> tmp =  new ArrayList<JsonObject>();
		for (int i = 0; i < rows.size(); i++) {
			for (int j = 0; j < optsbyline.get(""+i).getAsJsonArray().size(); j++) {
				tmp.add(optsbyline.get(""+i).getAsJsonArray().get(j).getAsJsonObject());
			}
		}
		return ret;
	}
	private JsonObject bygroup(JsonArray line) {
		JsonObject ret = new JsonObject();
		System.out.println(sortby(grabedopts,removeDuplicates(arx),"x").toString());
		for (int i = 0; i < line.size(); i++) {
			if(i+1 != line.size()){
				int sub = ysorted.get(i+1).get("x").getAsInt() -ysorted.get(i).get("x").getAsInt();
				//System.out.println();
			}
		}
		System.out.println(ret.toString());
		return ret;
	}
	private JsonArray fxindexes(JsonArray in,int maxlim,int minlim) {
		return fxindexes(in,maxlim,minlim,0,0);
	}
	private JsonArray fxindexes(JsonArray in,int maxlim,int minlim,int gapmax,int gapmin) {
		JsonArray ret = new JsonArray();
		int prv=0,nxt=0;
		boolean falsy = false;
		for (int i = 0; i < in.size(); i++) {
			if(i+1 != in.size()){
				if(!falsy){
					prv	= in.get(i).getAsInt();
					nxt	= in.get(i+1).getAsInt();
				}else{
					nxt	= in.get(i+1).getAsInt();
					if(i==1){
						if(nxt-in.get(i-1).getAsInt() <= maxlim && nxt-in.get(i-1).getAsInt() >= minlim  ){
							
						}else if(nxt-in.get(i).getAsInt() <= maxlim && nxt-in.get(i).getAsInt() >= minlim  ){
							prv	= in.get(i).getAsInt();
						}
					}
					
				}
				int sub = nxt-prv;
				//System.out.println(nxt +"-"+prv+" = "+sub);
				if(sub <=maxlim && sub >=minlim || sub <=gapmax && sub >=gapmin){
					falsy = false;
					//System.out.println("ADDED"+sub);
					if(ret.size() == 0){
						ret.add(in.get(i));
						ret.add(in.get(i+1));
					}else{
						ret.add(in.get(i+1));
					}
				}else{
					falsy = true;
				}
			}
		}
		return ret;
	}
	private JsonObject byline() {
		JsonObject ret = new JsonObject();
		//System.out.println("Organizing Options for line "+lines.size());
		for (int i = 0; i < rows.size(); i++) {
			JsonArray subl = new JsonArray();
			int curline = rows.get(i).getAsInt();
			//System.out.println("Organizing Options for line "+curline);
			for (int j = 0; j < ysorted.size(); j++) {
				//System.out.println(Math.abs(curline - ysorted.get(j).get("y").getAsInt()));
				if(Math.abs(curline - ysorted.get(j).get("y").getAsInt()) <= 12){
					subl.add(ysorted.get(j));
				}
			}
			System.out.println(subl.toString());
			ret.add(""+i, subl);
		}
		return ret;
	}


	public void indexit(int minradi, int maxradi,int ques) {
		minradi =12;
		maxradi = 28;
		ques = 5;
		int 	stc = ysorted.get(0).get("y").getAsInt(),
				stn = ysorted.get(1).get("y").getAsInt();
		
		//System.out.println("First two t points are"+stc+" "+stn);
		JsonObject qsopts = new JsonObject();
		for (int i = 0; i < ques; i++) {
			//System.out.println("Making Question Num"+i);
			JsonObject qopts = new JsonObject();
			JsonObject opts  = new JsonObject();
			int optc = 0;
			for (int j = 0; j < ysorted.size(); j++) {
				//System.out.println("Checking Seq to"+stc);	
				
				JsonObject str = ysorted.get(j);
				
				int min = str.get("y").getAsInt() - stc;
				//System.out.println("if "+min+" <= "+minradi);
				if( min<= minradi && min >=0){
					
					qopts.add(""+optc,str);
					//System.out.println(qopts.toString());
					optc++;
				}else{
				}
				
				if(optc == 6) {
					if(j+1 < ysorted.size()){
						stc = ysorted.get(j+1).get("y").getAsInt();
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
	private JsonArray indexes(List<JsonObject> in,String axis) {
		//System.out.println("Indexes With axis as "+axis);
		JsonArray l = new JsonArray();
		for (int i = 0; i < in.size(); i++) {
			if(i+1 != in.size()){
				int sub = in.get(i+1).get(axis).getAsInt() -in.get(i).get(axis).getAsInt();
				//System.out.println(in.get(i+1).get(axis).getAsInt() +"-"+in.get(i).get(axis).getAsInt()+" = "+sub);
				if(sub >=12){
					//System.out.println("ADDED"+sub);
					if(l.size() == 0){
						l.add(in.get(i).get(axis));
						l.add(in.get(i+1).get(axis));
					}else{
						l.add(in.get(i+1).get(axis));
					}
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
