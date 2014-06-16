package helper;

import static org.bytedeco.javacpp.opencv_core.cvGet2D;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;

import config.Config;

public class Rectangle extends Config{
	private IplImage in;
	public Point tl,br;
	public Rectangle(IplImage in){
		this.in = in;
	}

	public boolean isBlack(double fact){
		double b=0,w=0;
		for (int y = tl.gety(); y < br.gety(); y++) {
			for (int x = tl.getx(); x < br.getx(); x++) {
				if (isblackp(x,y)) b++;
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
	/*
	 * Detecting if pixel is black
	 * @return boolean
	 */
	public boolean isblackp(int x,int y){
		CvScalar s=cvGet2D(in,y,x);
		//System.out.println( "B:"+ s.val(0) + " G:" + s.val(1) + " R:" + s.val(2));//Print values
		return (s.val(2) <= markth && s.val(0) <= markth
				&& s.val(1) <= markth)?true :false;
	}
	public boolean isblackp(int x,int y,double fact){
		CvScalar s=cvGet2D(in,y,x);
		//System.out.println( "B:"+ s.val(0) + " G:" + s.val(1) + " R:" + s.val(2));//Print values
		return (s.val(2) <= fact && s.val(0) <= fact
				&& s.val(1) <= fact)?true :false;
	}
}