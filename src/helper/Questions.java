package helper;
import java.util.ArrayList;

import org.bytedeco.javacpp.opencv_core.IplImage;

import com.google.gson.JsonArray;

import config.Config;

public class Questions extends Config{
	public ArrayList<Question> questions;
	public int total,unit;
	private JsonArray cells,rows;
	IplImage image;
	Point orig;
	private int avgr;
	/*
	 * Constructors
	 */
	public Questions(int total,int unit,IplImage image,Point orig, JsonArray tcols, JsonArray trows,int avgr){
		this.total=total;
		this.unit=unit;
		this.image=image.clone();
		this.orig = orig;
		this.cells = tcols;
		this.rows = trows;
		this.avgr = avgr;
		questions = new ArrayList<Question>(); 
	}
	/*
	 * Methods
	 */
	public void addQuestion(int number,int options,String imgname){
		Question q = new Question(number, options, image,imgname, unit,orig,cells,rows,avgr);
		questions.add(q);
	}
	public void addAllQuestions(int[] options,String imgname){
		for (int i = 0; i < total; i++) {
			questions.add(new Question(i, options[i], image, imgname, unit, orig,cells,rows,avgr));
		}
	}
	public Question getQuestion(int q){
		return questions.get(q);
	}
	public String[] getAllOptions(){
		int listsize = questions.size();
		String[] ret = new String[listsize];
		for(int i=0; i<listsize; i++){
			ret[i]=questions.get(i).getResult();
		}
		return ret;
	}
	public IplImage drawQgrid(){
		int listsize = questions.size();
		for(int i=0; i<listsize; i++){
			questions.get(i).drawmaps();
		}
		return image;
	}
	
	public String[] getGrades(){
		return getAllOptions();
	}
	public IplImage getQgiven(){
		return image;
	}
}
