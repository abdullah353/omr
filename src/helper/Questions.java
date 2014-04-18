package helper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import config.Config;

public class Questions extends Config{
	public ArrayList<Question> questions;
	public int total,twounit;
	BufferedImage image;
	
	/*
	 * Constructors
	 */
	public Questions(int total,int twounit,BufferedImage image){
		this.total=total;
		this.twounit=twounit;
		this.image=image;
		questions = new ArrayList<Question>(); 
	}
	/*
	 * Methods
	 */
	public void addQuestion(int number,int options){
		Question q = new Question(number, options, image, twounit);
		questions.add(q);
	}
	public void addAllQuestions(){
		for (int i = 0; i < total; i++) {
			questions.add(new Question(i, 6, image, twounit));
		}
	}
	public Question getQuestion(int q){
		return questions.get(q);
	}
	public int[] getAllOptions(){
		int listsize = questions.size();
		int[] ret = new int[listsize];
		for(int i=0; i<listsize; i++){
           ret[i]=questions.get(i).getResult();
        }
      
		return ret;
	}
}
