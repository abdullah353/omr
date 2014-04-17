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
	public Question(int number,int options,BufferedImage image,int twounit){
		nu 	= number;
		totOpt	= options;
		optA = new Rectangle(image);
		optB = new Rectangle(image);
		optC = new Rectangle(image);
		optD = new Rectangle(image);
		optE = new Rectangle(image);
		optF = new Rectangle(image);
		setOpt(twounit,twounit/2);
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
	
	public void setOpt(int twounit,int unit){
		optA.setCorn(ans[nu][A][x0]*twounit+unit, ans[nu][A][y0]*twounit, ans[nu][A][x1]*twounit, ans[nu][A][y1]*twounit-unit);
		optB.setCorn(ans[nu][B][x0]*twounit+unit, ans[nu][B][y0]*twounit, ans[nu][B][x1]*twounit, ans[nu][B][y1]*twounit-unit);
		optC.setCorn(ans[nu][C][x0]*twounit+unit, ans[nu][C][y0]*twounit, ans[nu][C][x1]*twounit, ans[nu][C][y1]*twounit-unit);
		optD.setCorn(ans[nu][D][x0]*twounit+unit, ans[nu][D][y0]*twounit, ans[nu][D][x1]*twounit, ans[nu][D][y1]*twounit-unit);
		optE.setCorn(ans[nu][E][x0]*twounit+unit, ans[nu][E][y0]*twounit, ans[nu][E][x1]*twounit, ans[nu][E][y1]*twounit-unit);
		optF.setCorn(ans[nu][F][x0]*twounit+unit, ans[nu][F][y0]*twounit, ans[nu][F][x1]*twounit, ans[nu][F][y1]*twounit-unit);
	}
	public void setOverview(int twounit,int unit){
		System.out.println((ans[nu][A][x0]*twounit+unit)+","+(ans[nu][A][y0]*twounit)+","+(ans[nu][A][x1]*twounit)+","+(ans[nu][A][y1]*twounit-unit));
		System.out.println((ans[nu][B][x0]*twounit+unit)+","+(ans[nu][B][y0]*twounit)+","+(ans[nu][B][x1]*twounit)+","+(ans[nu][B][y1]*twounit-unit));
		System.out.println((ans[nu][C][x0]*twounit+unit)+","+(ans[nu][C][y0]*twounit)+","+(ans[nu][C][x1]*twounit)+","+(ans[nu][C][y1]*twounit-unit));
		System.out.println((ans[nu][D][x0]*twounit+unit)+","+(ans[nu][D][y0]*twounit)+","+(ans[nu][D][x1]*twounit)+","+(ans[nu][D][y1]*twounit-unit));
		System.out.println((ans[nu][E][x0]*twounit+unit)+","+(ans[nu][E][y0]*twounit)+","+(ans[nu][E][x1]*twounit)+","+(ans[nu][E][y1]*twounit-unit));
		System.out.println((ans[nu][F][x0]*twounit+unit)+","+(ans[nu][F][y0]*twounit)+","+(ans[nu][F][x1]*twounit)+","+(ans[nu][F][y1]*twounit-unit));
	}
}
