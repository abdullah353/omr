package helper;

import static org.bytedeco.javacpp.opencv_core.cvGet2D;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;

import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;

import com.google.gson.JsonArray;

import config.Config;

public class Question extends Config{
	/*
	 * Attributes
	 */
	int nu,totOpt,avgr;
	File file;
	Rectangle optA,optB,optC,optD,optE,optF;
	Point orig;
	String imgname;
	private JsonArray cells,rows;
	private IplImage tmpimgx;
	/*
	 * Constructor
	 */
	public Question(int number,int options,IplImage in,String imgname,int unit,Point orig, JsonArray cells, JsonArray rows, int avgr){
		nu 	= number;
		this.imgname = imgname;
		totOpt	= options;
		this.cells = cells;
		this.rows = rows;
		this.avgr = avgr;
		optA = new Rectangle(in);
		optB = new Rectangle(in);
		optC = new Rectangle(in);
		optD = new Rectangle(in);
		optE = new Rectangle(in);
		optF = new Rectangle(in);
		this.orig = orig;
		tmpimgx = in;
		setoptloc();
	}
	private void setoptloc() {
		optA.setCorn(cell(nu,"A","x")-avgr,row(nu,"A","y")-avgr,cell(nu,"A","x")+avgr,row(nu,"A","y")+avgr);
		optB.setCorn(cell(nu,"B","x")-avgr,row(nu,"B","y")-avgr,cell(nu,"B","x")+avgr,row(nu,"B","y")+avgr);
		optC.setCorn(cell(nu,"C","x")-avgr,row(nu,"C","y")-avgr,cell(nu,"C","x")+avgr,row(nu,"C","y")+avgr);
		optD.setCorn(cell(nu,"D","x")-avgr,row(nu,"D","y")-avgr,cell(nu,"D","x")+avgr,row(nu,"D","y")+avgr);
		optE.setCorn(cell(nu,"E","x")-avgr,row(nu,"E","y")-avgr,cell(nu,"E","x")+avgr,row(nu,"E","y")+avgr);
		optF.setCorn(cell(nu,"F","x")-avgr,row(nu,"F","y")-avgr,cell(nu,"F","x")+avgr,row(nu,"F","y")+avgr);
	}
	public int cell(int nu,String opt,String axis){
		return cells.get(getind(nu,opt,axis)).getAsInt();
	}
	public int row(int nu,String opt,String axis){
		return rows.get(getind(nu,opt,axis)).getAsInt();
	}
	/*
	 * Methods
	 */
	public boolean isCorrect(){
		return false;
	}
	public void optOverview(){
		System.out.println("Overview of Choices For Question Q"+nu);
		System.out.println("OptA isfilled "+optA.isBlack(quth)+" Position"+optA.tl.getp()+optA.br.getp());
		System.out.println("OptB isfilled "+optB.isBlack(quth)+" Position"+optB.tl.getp()+optB.br.getp());
		System.out.println("OptC isfilled "+optC.isBlack(quth)+" Position"+optC.tl.getp()+optC.br.getp());
		System.out.println("OptD isfilled "+optD.isBlack(quth)+" Position"+optD.tl.getp()+optD.br.getp());
		System.out.println("OptE isfilled "+optE.isBlack(quth)+" Position"+optE.tl.getp()+optE.br.getp());
		System.out.println("OptF isfilled "+optF.isBlack(quth)+" Position"+optF.tl.getp()+optF.br.getp());
	}
	public boolean isfilled(){
		return false;
	}
	public boolean[] viewfilled(){
		switch(totOpt){
		case 1:
			return new boolean[] {optA.isBlack(quth)};
		case 2:
			return new boolean[] {optA.isBlack(quth),optB.isBlack(quth)};
		case 3:
			return new boolean[] {optA.isBlack(quth),optB.isBlack(quth),optC.isBlack(quth)};
		case 4:
			return new boolean[] {optA.isBlack(quth),optB.isBlack(quth),optC.isBlack(quth),
					optD.isBlack(quth)};
		case 5:
			return new boolean[] {optA.isBlack(quth),optB.isBlack(quth),optC.isBlack(quth),
					optD.isBlack(quth),optE.isBlack(quth)};
		default:
			return new boolean[] {optA.isBlack(quth),optB.isBlack(quth),optC.isBlack(quth),
					optD.isBlack(quth),optE.isBlack(quth),optF.isBlack(quth)};
		}

	}

	public void setOverview(int unit) throws IOException{
		System.out.println("optA "+optA.displayCorners());
		System.out.println("optB "+optB.displayCorners());
		System.out.println("optC "+optC.displayCorners());
		System.out.println("optD "+optD.displayCorners());
		System.out.println("optE "+optE.displayCorners());
		System.out.println("optF "+optF.displayCorners());
	}
	public String getResult(){
		boolean[] allinfo = viewfilled();
		for (int i = 0; i < allinfo.length; i++) {
			if(allinfo[i]){
				return ((i+1) == 1)? "A":((i+1) == 2)? "B":((i+1) == 3)? "C":((i+1) == 4)? "D":((i+1) == 5)? "E":((i+1) == 6)?"F":"skip";
			}
		}
		return "skip";
	}
	public boolean isblackp(int x,int y){
		CvScalar s=cvGet2D(tmpimgx,y,x);
		System.out.println( "B:"+ s.val(0) + " G:" + s.val(1) + " R:" + s.val(2));//Print values
		return (s.val(2) <= 245 && s.val(0) <= 245
				&& s.val(1) <= 245)?true :false;
	}
	public void drawmaps(){
		fillimage((int) optA.tl.getx(),(int) optA.tl.gety(),(int) optA.br.getx(),(int) optA.br.gety());
		fillimage((int) optB.tl.getx(),(int) optB.tl.gety(),(int) optB.br.getx(),(int) optB.br.gety());
		fillimage((int) optC.tl.getx(),(int) optC.tl.gety(),(int) optC.br.getx(),(int) optC.br.gety());
		fillimage((int) optD.tl.getx(),(int) optD.tl.gety(),(int) optD.br.getx(),(int) optD.br.gety());
		fillimage((int) optE.tl.getx(),(int) optE.tl.gety(),(int) optE.br.getx(),(int) optE.br.gety());
		fillimage((int) optF.tl.getx(),(int) optF.tl.gety(),(int) optF.br.getx(),(int) optF.br.gety());
	}

	public void fillimage(int xMin,int yMin,int w,int h){
		Highlight(tmpimgx,xMin,yMin, w,  h, 4);
	}
	public void savefilled(){
		cvSaveImage("debug/rt-"+imgname,tmpimgx);
	}
	/*
	public void savefilled(){
		try {
			ImageIO.write(tmpimg, "jpg", file = new File("debug/"+imgname+".jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	/***
	 * Draw rectangle on image
	 * @param image
	 * @param xMin
	 * @param yMin
	 * @param xMax
	 * @param yMax
	 * @param Thick
	 */
	public static void Highlight(IplImage image, int xMin, int yMin, int xMax, int yMax, int Thick){
		CvPoint pt1 = cvPoint(xMin,yMin);
		CvPoint pt2 = cvPoint(xMax,yMax);
		CvScalar color = cvScalar(255,0,0,0);     // blue [green] [red]
		cvRectangle(image, pt1, pt2, color, Thick, 4, 0);
	}
	/***
	 * Overloaded Show Image
	 * @param image
	 * @param caption
	 * @param size
	 */
	public static void ShowImage(IplImage image, String caption, int size){
		if(size < 128) size = 128;
		CvMat mat = image.asCvMat();
		int width = mat.cols(); if(width < 1) width = 1;
		int height = mat.rows(); if(height < 1) height = 1;
		double aspect = 1.0 * width / height;
		if(height != size) { height = size; width = (int) ( height * aspect ); }
		if(width != size) width = size;
		height = (int) ( width / aspect );
		ShowImage(image, caption, width, height);
	}
	/***
	 * Show Image
	 * @param image
	 * @param caption
	 * @param width
	 * @param height
	 */
	public static void ShowImage(IplImage image, String caption, int width, int height){
		CanvasFrame canvas = new CanvasFrame(caption, 1);   // gamma=1
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		canvas.setCanvasSize(width, height);
		canvas.showImage(image);
	}
}
