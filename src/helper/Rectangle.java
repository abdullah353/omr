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
		return (w >= b)?false:true;
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
}