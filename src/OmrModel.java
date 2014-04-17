
import helper.Point;
import helper.Question;
import helper.Rectangle;

import java.awt.Color;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
		System.out.println("Looking for Unit");
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
						System.out.println("found flag true with non empty points");
						if(foundMarker()) {
							break;
						}
					}
				
					
				}
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
		System.out.println("Can't verifyMarker twounit = "+exp2U);
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
		Question q = new Question(0, 6, image, twounit);
		q.optOverview();
		boolean[] a = q.viewfilled();
		//System.out.print("b = "+Arrays.toString(a));
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