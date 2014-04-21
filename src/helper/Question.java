package helper;

import java.awt.image.BufferedImage;

import config.Config;

public class Question extends Config{
	/*
	 * Attributes
	 */
	int nu,totOpt;
	Rectangle optA,optB,optC,optD,optE,optF;
	/*
	 * Constructor
	 */
	public Question(int number,int options,BufferedImage image,int unit){
		nu 	= number;
		totOpt	= options;
		optA = new Rectangle(image);
		optB = new Rectangle(image);
		optC = new Rectangle(image);
		optD = new Rectangle(image);
		optE = new Rectangle(image);
		optF = new Rectangle(image);
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
		boolean[] filled = {optA.isBlack(),optB.isBlack(),optC.isBlack(),
							optD.isBlack(),optE.isBlack(),optF.isBlack()};
		return filled;
	}
	
	public void setOpt(int unit){
		optA.setCorn((ans[nu][A][x0]+1/2)*unit, ans[nu][A][y0]*unit, ans[nu][A][x1]*unit, (ans[nu][A][y1]*-1/2)*unit);
		optB.setCorn((ans[nu][B][x0]+1/2)*unit, ans[nu][B][y0]*unit, ans[nu][B][x1]*unit, (ans[nu][B][y1]*-1/2)*unit);
		optC.setCorn((ans[nu][C][x0]+1/2)*unit, ans[nu][C][y0]*unit, ans[nu][C][x1]*unit, (ans[nu][C][y1]*-1/2)*unit);
		optD.setCorn((ans[nu][D][x0]+1/2)*unit, ans[nu][D][y0]*unit, ans[nu][D][x1]*unit, (ans[nu][D][y1]*-1/2)*unit);
		optE.setCorn((ans[nu][E][x0]+1/2)*unit, ans[nu][E][y0]*unit, ans[nu][E][x1]*unit, (ans[nu][E][y1]*-1/2)*unit);
		optF.setCorn((ans[nu][F][x0]+1/2)*unit, ans[nu][F][y0]*unit, ans[nu][F][x1]*unit, (ans[nu][F][y1]*-1/2)*unit);
	}
	public void setOverview(int unit){
		System.out.println((ans[nu][A][x0]+1/2)*unit+" , "+ ans[nu][A][y0]*unit+" , "+ ans[nu][A][x1]*unit+" , "+ (ans[nu][A][y1]*-1/2)*unit);
		System.out.println((ans[nu][B][x0]+1/2)*unit+" , "+ ans[nu][B][y0]*unit+" , "+ ans[nu][B][x1]*unit+" , "+ (ans[nu][B][y1]*-1/2)*unit);
		System.out.println((ans[nu][C][x0]+1/2)*unit+" , "+ ans[nu][C][y0]*unit+" , "+ ans[nu][C][x1]*unit+" , "+ (ans[nu][C][y1]*-1/2)*unit);
		System.out.println((ans[nu][D][x0]+1/2)*unit+" , "+ ans[nu][D][y0]*unit+" , "+ ans[nu][D][x1]*unit+" , "+ (ans[nu][D][y1]*-1/2)*unit);
		System.out.println((ans[nu][E][x0]+1/2)*unit+" , "+ ans[nu][E][y0]*unit+" , "+ ans[nu][E][x1]*unit+" , "+ (ans[nu][E][y1]*-1/2)*unit);
		System.out.println((ans[nu][F][x0]+1/2)*unit+" , "+ ans[nu][F][y0]*unit+" , "+ ans[nu][F][x1]*unit+" , "+ (ans[nu][F][y1]*-1/2)*unit);
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
}
