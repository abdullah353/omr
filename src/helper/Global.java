package helper;

import static org.bytedeco.javacpp.opencv_core.cvGet2D;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;

import config.Config;


public class Global extends Config{
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
		return (color.getRed() <= markth && color.getBlue() <=  markth
				&& color.getGreen() <= markth)?true :false;
	}
	public boolean isblackp(IplImage in,int x,int y){
		CvScalar s=cvGet2D(in,y,x);
		System.out.println( "B:"+ s.val(0) + " G:" + s.val(1) + " R:" + s.val(2));//Print values
		return (s.val(2) <= 245 && s.val(0) <= 245
				&& s.val(1) <= 245)?true :false;
	}
	public boolean isblackp(int x,int y,double fact){
		Color color = new Color(image.getRGB( x,y));
		return (color.getRed() <= fact && color.getBlue() <=  fact
				&& color.getGreen() <= fact)?true :false;
	}
}
