package helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import config.Config;

public class Questions extends Config{
	public ArrayList<Question> questions;
	public int total,unit;
	BufferedImage image;
	
	/*
	 * Constructors
	 */
	public Questions(int total,int unit,BufferedImage image){
		this.total=total;
		this.unit=unit;
		this.image=image;
		questions = new ArrayList<Question>(); 
	}
	/*
	 * Methods
	 */
	public void addQuestion(int number,int options){
		Question q = new Question(number, options, image, unit);
		questions.add(q);
	}
	public void addAllQuestions(){
		for (int i = 0; i < total; i++) {
			questions.add(new Question(i, 6, image, unit));
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
	public void genExcel(String excelname,String sheetName) throws IOException, WriteException{
		int[] selecOpt = getAllOptions();
		Question currque;
		WritableWorkbook workbook = Workbook.createWorkbook(new File(excelname+ext));
		WritableSheet sheets = workbook.createSheet(sheetName, 0);
		
		for (int i = 0; i < total; i++) {
			currque = getQuestion(i);
			Label label = new Label(i, 0, "Question "+currque.nu); 
			Number number = new Number(i, 1, selecOpt[i]); 
			sheets.addCell(label);
			sheets.addCell(number);
		}
		workbook.write(); 
		workbook.close();
	}
}
