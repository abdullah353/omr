package helper;

import java.awt.Color;
import java.awt.image.BufferedImage;

import config.Config;

public class Question extends Config{
	/*
	 * Attributes
	 */
	int nu,totOpt;
	Rectangle optA,optB,optC,optD,optE,optF;
	Point orig;
	BufferedImage image;
	/*
	 * Constructor
	 */
	public Question(int number,int options,BufferedImage image,int unit,Point orig){
		nu 	= number;
		totOpt	= options;
		this.image = image;
		optA = new Rectangle(image);
		optB = new Rectangle(image);
		optC = new Rectangle(image);
		optD = new Rectangle(image);
		optE = new Rectangle(image);
		optF = new Rectangle(image);
		this.orig = orig;
		setOpt(unit);
		//setOverview(twounit,twounit/2);
	}
	/*
	 * Methods
	 */
	public boolean isCorrect(){
		return false;
	}
	public void optOverview(){
		System.out.println("Overview of Choices For Question Q"+nu);
		System.out.println("OptA isfilled "+optA.isBlack()+" Position"+optA.tl.getp()+optA.br.getp());
		System.out.println("OptB isfilled "+optB.isBlack()+" Position"+optB.tl.getp()+optB.br.getp());
		System.out.println("OptC isfilled "+optC.isBlack()+" Position"+optC.tl.getp()+optC.br.getp());
		System.out.println("OptD isfilled "+optD.isBlack()+" Position"+optD.tl.getp()+optD.br.getp());
		System.out.println("OptE isfilled "+optE.isBlack()+" Position"+optE.tl.getp()+optE.br.getp());
		System.out.println("OptF isfilled "+optF.isBlack()+" Position"+optF.tl.getp()+optF.br.getp());
	}
	public boolean isfilled(){
		return false;
	}
	public boolean[] viewfilled(){
		boolean[] filled = {optA.isBlack(quth),optB.isBlack(quth),optC.isBlack(quth),
							optD.isBlack(quth),optE.isBlack(quth),optF.isBlack(quth)};
		return filled;
	}
	
	public void setOpt(int unit){
		optA.setCorn((int)( (double)(ans[nu][A][x0]+0.5)*unit+orig.getx()), (ans[nu][A][y0]*unit+orig.gety()),(ans[nu][A][x1]*unit+orig.getx()), (int) ((double) (ans[nu][A][y1]-0.5)*unit+orig.gety()));
		optB.setCorn((int)( (double)(ans[nu][B][x0]+0.5)*unit+orig.getx()), (ans[nu][B][y0]*unit+orig.gety()),(ans[nu][B][x1]*unit+orig.getx()), (int) ((double) (ans[nu][B][y1]-0.5)*unit+orig.gety()));
		optC.setCorn((int)( (double)(ans[nu][C][x0]+0.5)*unit+orig.getx()), (ans[nu][C][y0]*unit+orig.gety()),(ans[nu][C][x1]*unit+orig.getx()), (int) ((double) (ans[nu][C][y1]-0.5)*unit+orig.gety()));
		optD.setCorn((int)( (double)(ans[nu][D][x0]+0.5)*unit+orig.getx()+1), (ans[nu][D][y0]*unit+orig.gety()),(ans[nu][D][x1]*unit+orig.getx()), (int) ((double) (ans[nu][D][y1]-0.5)*unit+orig.gety()));
		optE.setCorn((int)( (double)(ans[nu][E][x0]+0.5)*unit+orig.getx()+1), (ans[nu][E][y0]*unit+orig.gety()),(ans[nu][E][x1]*unit+orig.getx()), (int) ((double) (ans[nu][E][y1]-0.5)*unit+orig.gety()));
		optF.setCorn((int)( (double)(ans[nu][F][x0]+0.5)*unit+orig.getx()+2), (ans[nu][F][y0]*unit+orig.gety()),(ans[nu][F][x1]*unit+orig.getx()), (int) ((double) (ans[nu][F][y1]-0.5)*unit+orig.gety()));
	}
	public void setOverview(int unit){
		System.out.println("optA "+optA.displayCorners());
		System.out.println("optB "+optB.displayCorners());
		System.out.println("optC "+optC.displayCorners());
		System.out.println("optD "+optD.displayCorners());
		System.out.println("optE "+optE.displayCorners());
		System.out.println("optF "+optF.displayCorners());
	}
	public int getResult(){
		boolean[] allinfo = viewfilled();
		for (int i = 0; i < allinfo.length; i++) {
			if(allinfo[i]){
				return i+1;
			}
		}
		return -1;
	}
	public boolean isblackp(int x,int y){
		Color color = new Color(image.getRGB( x,y));
		return (color.getRed() <= 245 && color.getBlue() <= 245
				&& color.getGreen() <= 245)?true :false;
	}
}
