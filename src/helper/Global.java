package helper;

import java.awt.Color;
import java.awt.image.BufferedImage;

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
	public boolean isblackp(int x,int y,double fact){
		Color color = new Color(image.getRGB( x,y));
		return (color.getRed() <= fact && color.getBlue() <=  fact
				&& color.getGreen() <= fact)?true :false;
	}
}
