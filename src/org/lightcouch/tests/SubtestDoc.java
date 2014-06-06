package org.lightcouch.tests;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

public class SubtestDoc {
	private String 	name, subtestId, prototype;
	private Logger logger = Logger.getLogger(SubtestDoc.class.getName());
	private Sum 	sum;
	private Data data;
	
	public SubtestDoc() {
		sum = new Sum(0,0,0,0);
		
	}
	/**
	 * Get Sub test Name
	 * @return String
	 */
	public String getname(){
		return name;
	}
	/**
	 * Get Sub Test Id 
	 * @return String
	 */
	public String getsubtestId(){
		return subtestId;
	}
	/**
	 * Get Sub Test Prototype
	 * @return String
	 */
	public String getprototype(){
		return prototype;
	}
	/**
	 * Get Sub sum info
	 * @return Sum
	 */
	public Sum getsum(){
		return sum;
	}
	/**
	 * Setting Sub Name
	 * @param name
	 */
	public void setname(String name){
		data = new Data();
		this.name = name;
	}
	public void setloc(String name){
		data.addsubname(name);
	}
	/**
	 * Setting Sub ID
	 * @param subId
	 */
	public void setsubtestId(String subId){
		this.subtestId = subId;
	}
	/**
	 * Setting Sub Prototype
	 * @param proto
	 */
	public void setprototype(String proto){
		this.prototype = proto;
	}
	/**
	 * Setting Sub sum Field
	 * @param cor
	 * @param inco
	 * @param miss
	 * @param tot
	 */
	public void setsum(int cor, int inco,int miss,int tot){
		this.sum.setcorrect(cor);
		this.sum.setincorrect(inco);
		this.sum.setmissing(miss);
		this.sum.settotal(tot);
	}
	/**
	 * Converting into Object
	 * @return JsonObject
	 */
	public JsonObject toJson(){
		JsonObject json = new JsonObject();
		if(name != null)
		json.addProperty("name",name);
		if(subtestId != null)
		json.addProperty("subtestId",subtestId);
		if(prototype != null)
		json.addProperty("prototype",prototype);
		if(sum != null)
		json.add("sum",sum.toJson());
		if(data != null)
		json.add("data", data.toJson());
		return json;
	}
	public void setdata(String prototype){
		if(prototype.equals("complete")){
			data = new Data();
			String com = "Omr Generated Results";
			long endtime = System.currentTimeMillis();
			data.addcomp(com, endtime, null, null,null,null,null,null,null);
		}
	}
	public void setdata(List<JsonObject> json, String reqproto, JsonObject quesorder, String[] results, JsonObject opt) {
		data = new Data();
		String col;
		for (int i = 0; i < json.size(); i++) {
			JsonObject el = json.get(i);
			col = el.get("collection").getAsString();
			if(reqproto.equals("survey") && col.equals("question") && el.get("subtestId").getAsString().equals(subtestId)){
				int QNu = -1;
				String 	cqid,
						mqid = el.get("_id").getAsString();
				
				for (int j =0;j < 40; j++) {
					if(quesorder.get(""+j) != null){
						cqid = quesorder.get(""+j).getAsString();
						if(cqid.equals(mqid)){
							QNu = j;
							break;
						}
					}else if(j != 0){
						break;
					}else{
						logger.log(Level.SEVERE, "Question Sequence Is Wrong");
						break;
					}
				}
				
				if(QNu != -1){
					if(results[QNu].equals("skip")){
						setsum(sum.getcorrect(), sum.getincorrect(), sum.getmissing()+1, sum.gettotal()+1);
						data.addQuestion( el.get("name").getAsString(), "0");
					}else{
						setsum(sum.getcorrect()+1, sum.getincorrect(), sum.getmissing(), sum.gettotal()+1);
						data.addQuestion(el.get("name").getAsString(),opt.get(mqid).getAsJsonObject()
										.get(results[QNu]).getAsString());
					}
					
				}else{
					setsum(sum.getcorrect(), sum.getincorrect(), sum.getmissing()+1, sum.gettotal()+1);
					data.addQuestion( el.get("name").getAsString(), "0");
				}
			}else if (reqproto.equals("datetime") && el.get("prototype") != null && el.get("prototype").getAsString().equals("datetime")) {
				data.addtime();
			}
		}
		
	}
}
