package helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import config.Config;

public class Questions extends Config{
	public ArrayList<Question> questions;
	public int total,unit;
	BufferedImage image;
	Point orig;
	/*
	 * Constructors
	 */
	public Questions(int total,int unit,BufferedImage image,Point orig){
		this.total=total;
		this.unit=unit;
		this.image=image;
		this.orig = orig;
		questions = new ArrayList<Question>(); 
	}
	/*
	 * Methods
	 */
	public void addQuestion(int number,int options){
		Question q = new Question(number, options, image, unit,orig);
		questions.add(q);
	}
	public void addAllQuestions(int[] options){
		for (int i = 0; i < total; i++) {
			System.out.println("REXER Questoin "+i +"Options "+options[i]);
			questions.add(new Question(i, options[i], image, unit,orig));
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
	public void genExcel(String excelname,String sheetName) throws IOException, WriteException{
		String[] selecOpt = getAllOptions();
		
		Question currque;
		WritableWorkbook workbook = Workbook.createWorkbook(new File(excelname+ext));
		WritableSheet sheets = workbook.createSheet(sheetName, 0);
		
		for (int i = 0; i < total; i++) {
			currque = getQuestion(i);
			Label label = new Label(i, 0, "Question "+currque.nu); 
			Label number = new Label(i, 1, selecOpt[i]); 
			sheets.addCell(label);
			sheets.addCell(number);
		}
		workbook.write(); 
		workbook.close();
	}
}
