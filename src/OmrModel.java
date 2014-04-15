import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class OmrModel {
	String filename,path,DR = "/";
	Rectangle mtl,mcl,mrr,qr;
	Point mtlst,mtlend;
	public int unit,
		twounit;
	public double uerr;
	double rot=0.0;
	public boolean verifyMarker(int x,int y,BufferedImage image1){
		int exp2U = 0,
			experr = 1;
		exp2U = mtlst.gety();
		System.out.println("Checking Width twou is"+exp2U);
		System.out.println("mtlst is"+mtlst.gety()+" 2twou is "+2*exp2U);
		int hi = 0;
		for (int yi = mtlst.gety(); yi < 2*exp2U; yi++) {
			Color color = new Color(image1.getRGB( mtlst.getx(),yi));
			if (color.getRed() <= 89.25 && color.getBlue() <= 89.25 && color.getGreen() <= 89.25) {
				hi++;
			}
		}
		if( exp2U - experr <= hi && hi <= exp2U + experr ){
			mtlend.sety(2*exp2U);
			twounit = exp2U;
			unit = twounit/2;
			return true;
		}
		System.out.println("Can't verifyMarker");
		return false;
	}
	public boolean findUnit(BufferedImage image1){
		boolean flag;
		for (int y = 10; y < image1.getHeight(); y++) {
			flag= false;
			for (int x = 0; x < image1.getWidth(); x++) {
				Color color = new Color(image1.getRGB(x,y));
				if (color.getRed() <= 89.25 && color.getBlue() <= 89.25 && color.getGreen() <= 89.25) {
					if(mtlst.isempty()){
						flag = true;
						mtlst.setP(x, y);
						System.out.println("Found First Black at"+mtlst.getp());
					}else{
						mtlend.setP(x, y);
					}
				}else if(flag && !mtlst.isempty() && !mtlend.isempty()){
					if(verifyMarker(x,y,image1)){
						break;
					}
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
		}
		System.out.println("");
		return (twounit ==0)?false:true;
	}
	public OmrModel(){
		mtlst = new Point();
		mtlend = new Point();
		unit = 0;
		twounit = 0;
	}
	public void resetModel(){
		mtlst.empty();
		mtlend.empty();
		unit = 0;
		twounit = 0;
	}
	protected boolean validMarkers(){
		System.out.println("Checking Markers!");
		System.out.println("file location "+path+DR+filename);
		File file1 = new File(path+DR+filename);
		try {
			BufferedImage image1 = ImageIO.read(file1);
			if(findUnit(image1)){
				System.out.println("Unit Found TwoUnit="+twounit+" unit="+unit);
			}else{
				resetModel();
				System.out.println("Can't Found Unit");
			}
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
	}
	/*
	 * Defining Shappes Classes
	 */
	//Point
	public class Point {
		private int x = 0;
		private int y = 0;
		//constructor
		public Point(int a, int b) {
			x = a;
			y = b;
		}
		public int getx() {
			return x;
		}
		public int gety() {
			return y;
		}
		public void setx(int x) {
			this.x=x;
		}
		public void sety(int y) {
			this.y=y;
		}
		public boolean isempty() {
			return (x == 0 && y==0)?true:false;
		}
		public Point(){
			x=0;y=0;
		}
		public void empty(){
			x=0;y=0;
		}
		public void setP(int x,int y){
			this.x = x;
			this.y = y;
		}
		public String getp(){
			return "(x , y) = ("+x+","+y+")";
		}
	}
	public class Unit{
		int pix;
		public Unit(int mp){
			
		}
	}
	//Circle 
	public class Circle{
		Point origin;
		int circumference;
		int radius = 0;
		
		protected boolean isfilled(){
			return false;
		}
	}
	//Rectangle
	public class Rectangle{
		public	int width = 0,
					height = 0;
		public Point tl,tr,bl,br;
		//public Rectangle(int a){};
		public boolean isMarker(){
			return (height == width / 4) ?true:false;
		}
		public boolean isQr(){
			return false;
		}
	}
	public class Marker extends Rectangle{
		Point strtp,endp;
		int ratio = 4;
		double err = 0.5;
		double inac;
		public Marker(Point strt, Point end){
			strtp = strt;
			endp = end;
		}
		protected boolean isValid(){
			int l = strtp.x - endp.x;
			int h = strtp.y - endp.y;
			inac = l/h - ratio;
			return (ratio-err <= l/h || l/h <= ratio+err)?true:false;
		}
	}
}
