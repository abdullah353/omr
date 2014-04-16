package helper;

import java.awt.Color;
import java.awt.image.BufferedImage;


public class Global {
	protected BufferedImage image;
	/****
	 ** Constructor
	 ****/
	public Global(BufferedImage image){
		this.image = image;
	}
	/*
	 * Detecting if pixel is black
	 * @return boolean
	 */
	public boolean isblackp(int x,int y){
		Color color = new Color(image.getRGB( x,y));
		double thresh= 89.25;
		return (color.getRed() <= thresh && color.getBlue() <= thresh 
				&& color.getGreen() <= thresh)?true :false;
	}
}
