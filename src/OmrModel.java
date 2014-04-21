
import helper.Point;
import helper.Questions;
import helper.Rectangle;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import config.Config;




public class OmrModel extends Config{
	/*
	 * Attributes
	 */
	String filename,path;
	Rectangle mtl,mcl,mrr,qr;
	Point mtlst,mtlend;
	Point orig;
	public int 	unit;
	BufferedImage image;
	double rot=0.0,uerr;
	public Questions questions;
	/*
	 * Constructors
	 */
	public OmrModel(){
		mtlst = new Point();
		mtlend = new Point();
		
		unit = 0;
	}
	
	/*
	 * Methods
	 */
	
	/***
	 * Initializing Model
	 * @return boolean
	 */
	protected boolean init(){
		/***
		 * BLOCK#2
		 */
		System.out.println("Checking Markers!");
		System.out.println("file location "+path+DR+filename);
		/***
		 * END BLOCK#2
		 */
		File file1 = new File(path+DR+filename);
		try {
			this.image = ImageIO.read(file1);
			setShapes();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return false;
	}
	
	/***
	 * Setting Objects in Models
	 */
	public void setShapes(){
		mtl	 =	new Rectangle(image);
		mcl	 =	new Rectangle(image);
		mrr	 =	new Rectangle(image);
		qr	 =	new Rectangle(image);
	}
	
	/***
	 * Searching and Setting Unit
	 * @return boolean
	 */
	public boolean searchUnit(){
		boolean flag;
		System.out.println("Looking for Unit");
		//Do not look For Complete Height
		for (int y = 1; y < image.getHeight(); y++) {
			flag= false;
			for (int x = 0; x < image.getWidth(); x++) {
				if (isblackp(x,y)){
					if(mtlst.isempty()){
						flag = true;
						System.out.println("First black at"+x+","+y);
						mtlst.setP(x, y);
					}else {
						System.out.println("Next black at"+x+","+y);
						mtlend.setP(x, y);
					}
				}else{
					if(flag && !mtlst.isempty() && !mtlend.isempty()){
						System.out.println("found flag true with non empty points");
						if(foundMarker()) {
							return true;
						}
					}
				
					
				}
			}
		}
		return (unit == 0)? false: true;
	}
	
	/***
	 * Detecting if pixel is black
	 * @param x integer
	 * @param y integer
	 * @return boolean
	 */
	public boolean isblackp(int x,int y){
		Color color = new Color(image.getRGB( x,y));
		return (color.getRed() <= thresh && color.getBlue() <= thresh 
				&& color.getGreen() <= thresh)?true :false;
	}
	/***
	 * Finding first Marker and set our unit accordingly
	 * @return boolean
	 */
	public boolean foundMarker(){
		//Expected unit
		int exp2U = 0,
			experr = 1,
			hi = 0;
		exp2U = mtlst.gety();
		/***
		 * BLOCK#3 
		 */
		System.out.println("Checking Width twou is "+exp2U);
		System.out.println("mtlst is"+mtlst.gety()+" 2twou is "+2*exp2U);
		/***
		 * END BLOCK#3
		 */
		//for (int yi = exp2U; yi < 2*exp2U; yi++)
		//	if (isblackp(mtlst.getx(),yi))	hi++;
		int yi = exp2U;
		int err = 0;
		while(err <= 3){
			if(!isblackp(mtlst.getx(),yi)){
				err++;	
			}
			hi++;
			yi++;
		}
		
		//Error Correction Not Optimized at the moment
		//if( exp2U - experr <= hi && hi <= exp2U + experr ){
		System.out.println("Setting end y position as "+(yi-err+1));
			mtlend.sety(yi-err+1);
			System.out.println("Setting Unit as "+(hi-err)+" orig as "+mtlst.getp());
			setUnitOrig((hi-err),mtlst.getx(),mtlst.gety());
			//return true;
		//}
		/***
		 * BLOCK#5
		 */
		System.out.println("Arbitray start"+mtlst.getp()+" mtlend "+mtlend.getp() );
		/***
		 * END BLOCK#5
		 */
		return true;
	}
	
	/*
	 * Setter For unit
	 */
	public void setUnitOrig(int expu,int origx,int origy){
		orig = new Point(origx,origy);
		unit = expu;
	}
	/***
	 * Detecting Anchors on page
	 * @return boolean
	 */
	public boolean checkAnchors(){
		setlayout(unit, orig.getx(), orig.gety());
		mtl.setCorn(new Point(layout[firstmark][x0],layout[firstmark][y0]),
					new Point(layout[firstmark][x1],layout[firstmark][y1]));
		mcl.setCorn(new Point(layout[secondmark][x0],layout[secondmark][y0]),
					new Point(layout[secondmark][x1],layout[secondmark][y1]));
		mrr.setCorn(new Point(layout[thirdmark][x0],layout[thirdmark][y0]),
					new Point(layout[thirdmark][x1],layout[thirdmark][y1]));
		/***
		 * BLOCK#1: DELETE THIS BLOCK ITS FOR CONSOLE
		 */
		if(mtl.isBlack()){
			System.out.println("First Anchor is valid");
		}
		if(mcl.isBlack()){
			System.out.println("Second Anchor is valid");
		}
		if(mrr.isBlack()){
			System.out.println("Third Anchor is valid");
		}
		//showQBlueprint(q1,unit,unit);
		
		/***
		 * END BLOCK#1
		 */
		return (mtl.isBlack() && mcl.isBlack() && mrr.isBlack())?true:false;
	}

	/***
	 * Reseting Model
	 */
	public void resetModel(){
		mtlst.empty();
		mtlend.empty();
		unit = 0;;
	}
	public boolean setQuestions(int count){
		if(count<=20){
			questions = new Questions(count, unit, image);
			questions.getQuestion(0).setOverview(unit);
			return true;
		}
		System.out.println("Can not Add More Than 20 Questions");
		return false;
	}
}
