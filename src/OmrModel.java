
import helper.Point;
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
	public int 	unit,twounit;
	BufferedImage image;
	double rot=0.0,uerr;
	
	/*
	 * Constructors
	 */
	public OmrModel(){
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
		//Do not look For Complete Height
		for (int y = 10; y < image.getHeight(); y++) {
			flag= false;
			for (int x = 0; x < image.getWidth(); x++) {
				if (isblackp(x,y))
					if(mtlst.isempty()){
						flag = true;
						mtlst.setP(x, y);
					}else mtlend.setP(x, y);
				else if(flag && !mtlst.isempty() && !mtlend.isempty()) 
					if(foundMarker()) break;
			}
		}
		return (twounit == 0)? false: true;
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
		for (int yi = exp2U; yi < 2*exp2U; yi++)
			if (isblackp(mtlst.getx(),yi))	hi++;
		//Error Correction Not Optimized at the moment
		if( exp2U - experr <= hi && hi <= exp2U + experr ){
			mtlend.sety(2*exp2U);
			setUnit(exp2U);
			return true;
		}
		/***
		 * BLOCK#5
		 */
		System.out.println("Can't verifyMarker");
		/***
		 * END BLOCK#5
		 */
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
		mtl.setCorn(new Point(2*twounit,twounit), new Point(10*twounit,2*twounit));
		mcl.setCorn(new Point(2*twounit,14*twounit), new Point(10*twounit,15*twounit));
		mrr.setCorn(new Point(29*twounit,56*twounit), new Point(37*twounit,57*twounit));
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
}

//verify Height and break
/*if(!mtlst.isempty() && !mtlend.isempty()){
	exp2U = mtlst.gety();
	System.out.println("Checking Width twou is"+exp2U);
	System.out.println("mtlst is"+mtlst.gety()+" 2twou is "+2*exp2U);
	int hi = 0;
	for (int yi = mtlst.gety(); yi < 2*exp2U; yi++) {
		int c = image1.getRGB( mtlst.getx(),yi);
		Color color = new Color(c);
		if (color.getRed() <= 89.25 && color.getBlue() <= 89.25 && color.getGreen() <= 89.25) {
			hi++;
		}
	}
	if( exp2U - experr <= hi && hi <= exp2U + experr ){
		mtlend.sety(2*exp2U);
		twounit = exp2U;
		unit = twounit/2;
	}
	System.out.println("Hi is "+hi);
}
*/
/*
System.out.println(image1.getHeight());

System.out.println("GOT OUT");
int d = image1.getRGB(299,59);
Color colord = new Color(d);
System.out.println("Actual end Color"+colord.getRed() + colord.getBlue() +colord.getGreen() );
System.out.println(mtlst.getp());
System.out.println(mtlend.getp());
System.out.println("Next Expected Mark (->x,^y)="+2*twouy+","+8*twouy+"End px="+ (10*twouy)+"py="+(15*twouy));
int q = image1.getRGB(2*twouy,8*twouy);
Color colorq = new Color(q);
int w = image1.getRGB(10*twouy-1,15*twouy-1);
Color colorw = new Color(w);
System.out.println("Colo Expected Mark (->x,^y)="+colorq.getRed() + colorq.getBlue() +colorq.getGreen()+"End px="+colorw.getRed() + colorw.getBlue() +colorw.getGreen());
System.out.println("Next Expected Mark (->x,^y)="+29*twouy+","+56*twouy+"End px="+ (37*twouy)+"py="+(57*twouy));
*/