
import helper.Point;
import helper.Questions;
import helper.Rectangle;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import config.Config;




public class OmrModel extends Config{
	/*
	 * Attributes
	 */
	private Logger logger = Logger.getLogger(OmrModel.class.getName());
	private FileHandler fh;
	String filename,path;
	Rectangle mtl,mcl,mrr,qr;
	Point mtlst,mtlend;
	public int 	unit,twounit;
	BufferedImage image;
	double rot=0.0,uerr;
	public Questions questions;
	/*
	 * Constructors
	 */
	public OmrModel(){
		setLog("OmrModel",this.fh,this.logger);
		logger.log(Level.INFO, "Create Model");
		mtlst = new Point();
		mtlend = new Point();
		unit = 0;
		twounit = 0;
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
			logger.log(Level.INFO, "Storing Image in Buffer");
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
		logger.log(Level.INFO, "Creating 3 Markers and QR");
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
		logger.log(Level.INFO, "Searching for Unit Initiated");
		//Do not look For Complete Height
		for (int y = 10; y < image.getHeight(); y++) {
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
						logger.log(Level.INFO, "found flag true with non empty points");
						if(foundMarker()) {
							logger.log(Level.INFO, "Found Unit From Marker ");
							return true;
						}
					}
				}
			}
		}
		logger.log(Level.SEVERE, "Can't Found any Unit");
		return false;
	}
	
	/***
	 * Detecting if pixel is black
	 * @param x integer
	 * @param y integer
	 * @return boolean
	 */
	public boolean isblackp(int x,int y){
		Color color = new Color(image.getRGB( x,y));
		double thresh= 89.25;
		return (color.getRed() <= thresh && color.getBlue() <= thresh 
				&& color.getGreen() <= thresh)?true :false;
	}
	/***
	 * Finding first Marker and set our unit accordingly
	 * @return boolean
	 */
	public boolean foundMarker(){
		logger.log(Level.INFO ,"Checking Whether We Found Marker");
		//Expected unit
		int exp2U = 0,
			experr = 1,
			hi = 0;
		exp2U = mtlst.gety();
		
		logger.log(Level.INFO ,"Setting Expected twounit is "+exp2U);
		for (int yi = exp2U; yi < 2*exp2U; yi++)
			if (isblackp(mtlst.getx(),yi))	hi++;
		//Error Correction Not Optimized at the moment
		if( exp2U - experr <= hi && hi <= exp2U + experr ){
			mtlend.sety(2*exp2U);
			setUnit(exp2U);
			return true;
		}
		logger.log(Level.WARNING ,"Expected Marker is wrong");
		return false;
	}
	
	/*
	 * Setter For unit
	 */
	public void setUnit(int expu){
		twounit = expu;
		unit = twounit/2;
	}
	/***
	 * Detecting Anchors on page
	 * @return boolean
	 */
	public boolean checkAnchors(){
		mtl.setCorn(new Point(layout[firstmark][x0]*twounit,layout[firstmark][y0]*twounit),
					new Point(layout[firstmark][x1]*twounit,layout[firstmark][y1]*twounit));
		mcl.setCorn(new Point(layout[secondmark][x0]*twounit,layout[secondmark][y0]*twounit),
					new Point(layout[secondmark][x1]*twounit,layout[secondmark][y1]*twounit));
		mrr.setCorn(new Point(layout[thirdmark][x0]*twounit,layout[thirdmark][y0]*twounit),
					new Point(layout[thirdmark][x1]*twounit,layout[thirdmark][y1]*twounit));
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
		showQBlueprint(q1,twounit,unit);
		
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
		unit = 0;
		twounit = 0;
	}
	public boolean setQuestions(int count){
		if(count<=20){
			questions = new Questions(count, twounit, image);
			return true;
		}
		System.out.println("Can not Add More Than 20 Questions");
		return false;
	}
}