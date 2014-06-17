package couch;

import com.google.gson.JsonObject;

public class Sum {
	int correct,incorrect,missing,total;
	public Sum(){
		super();
	}
	public Sum(int cor, int inco,int miss,int tot){
		correct = cor;
		incorrect = inco;
		missing = miss;
		total = tot;
	}
	/**
	 * Getting Correct Value
	 * @return integer
	 */
	public int getcorrect(){
		return correct;
	}
	/**
	 * Getting incorrect Value
	 * @return integer
	 */
	public int getincorrect(){
		return incorrect;
	}
	/**
	 * Getting Missing Value
	 * @return integer
	 */
	public int getmissing(){
		return missing;
	}
	/**
	 * Getting total Value
	 * @return integer
	 */
	public int gettotal(){
		return total;
	}
	/**
	 * Setting Correct Value
	 * @param cor
	 */
	public void setcorrect(int cor){
		correct = cor;
	}
	/**
	 * Setting Incorrect Value
	 * @param inco
	 */
	public void setincorrect(int inco){
		incorrect = inco;
	}
	/**
	 * Setting Missing Value
	 * @param miss
	 */
	public void setmissing(int miss){
		missing = miss;
	}
	/**
	 * Setting Total Value
	 * @param tot
	 */
	public void settotal(int tot){
		total = tot;
	}
	/**
	 * Converting To JsonObject
	 * @return
	 */
	public JsonObject toJson(){
		JsonObject json = new JsonObject();
		json.addProperty("correct", correct);
		json.addProperty("incorrect", incorrect);
		json.addProperty("missing", missing);
		json.addProperty("total", total);
		return json;
	}
}
