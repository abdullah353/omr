package helper;

import java.awt.image.BufferedImage;

public class Rectangle extends Global{
	public Point tl,br;
	/**
	 * Constructor
	 */
	public Rectangle(BufferedImage image){
		super(image);
		
	};
	/***
	 * Checking if rectangle has excess of black pixels
	 * @return boolean
	 */
	public boolean isBlack(){
		int b=0,w=0;
		for (int y = tl.gety(); y < br.gety(); y++) {
			for (int x = tl.getx(); x < br.getx(); x++) {
				if (isblackp(x,y)) b++;
				else w++;
			}
		}
		System.out.println("tl ="+tl.getp()+","+br.getp()+" White is"+w+" And Black is "+b);
		return (w*.50 >= b)?false:true;
	}
	public boolean isBlack(double fact){
		double b=0,w=0;
		for (int y = tl.gety(); y < br.gety(); y++) {
			for (int x = tl.getx(); x < br.getx(); x++) {
				if (isblackp(x,y,fact)) b++;
				else w++;
			}
		}
		double per = (b/w)*100 ;
		//System.out.println("tl ="+tl.getp()+","+br.getp()+" White is"+w+" And Black is "+b+"Percent "+ per);
		return (per <= fact)?false:true;
	}
	/***
	 * Setting Edges points
	 * @param p1 top-left corner
	 * @param p2 bottom-right corner
	 */
	public void setCorn(Point p1, Point p2) {
		tl = p1;
		br = p2;
	}
	public void setCorn(int x0,int y0,int x1,int y1){
		//System.out.println("Setting points x0= "+x0+",y0="+y0+",x1= "+x1+",y1="+y1);
		setCorn(new Point(x0, y0), new Point(x1,y1));
	}
	public String displayCorners(){
		return "TopLeft "+tl.getp()+" BottomRight "+br.getp();
	}
	public double getheight(){
		return br.getx() - tl.getx();
	}
	public double getwidth(){
		return br.gety() - tl.gety();
	}
}