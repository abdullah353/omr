package helper;

import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
	BufferedImage image,tmpimg;
	private IplImage tmpimgx;
	/*
	 * Constructor
	 */
	public Question(int number,int options,BufferedImage image,String imgname,int unit,Point orig, JsonArray cells, JsonArray rows, int avgr){
		nu 	= number;
		this.imgname = imgname;
		totOpt	= options;
		this.image = image;
		this.cells = cells;
		this.rows = rows;
		this.avgr = avgr;
		optA = new Rectangle(image);
		optB = new Rectangle(image);
		optC = new Rectangle(image);
		optD = new Rectangle(image);
		optE = new Rectangle(image);
		optF = new Rectangle(image);
		this.orig = orig;
		//setOpt(unit);
		setoptloc();
		//setOverview(twounit,twounit/2);
	}
	private void setoptloc() {
		optA.setCorn(cell(nu,"A","x")-avgr,row(nu,"A","y")-avgr,cell(nu,"A","x")+avgr,row(nu,"A","y")+avgr);
		optB.setCorn(cell(nu,"B","x")-avgr,row(nu,"B","y")-avgr,cell(nu,"B","x")+avgr,row(nu,"B","y")+avgr);
		optC.setCorn(cell(nu,"C","x")-avgr,row(nu,"C","y")-avgr,cell(nu,"C","x")+avgr,row(nu,"C","y")+avgr);
		optD.setCorn(cell(nu,"D","x")-avgr,row(nu,"D","y")-avgr,cell(nu,"D","x")+avgr,row(nu,"D","y")+avgr);
		optE.setCorn(cell(nu,"E","x")-avgr,row(nu,"E","y")-avgr,cell(nu,"E","x")+avgr,row(nu,"E","y")+avgr);
		optF.setCorn(cell(nu,"F","x")-avgr,row(nu,"F","y")-avgr,cell(nu,"F","x")+avgr,row(nu,"F","y")+avgr);
		drawmaps();
		//optOverview();
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
	
	public void setOpt(int unit){
		optA.setCorn((int)( (double)(ans[nu][A][x0])*unit+orig.getx()), (ans[nu][A][y0]*unit+orig.gety()),(ans[nu][A][x1]*unit+orig.getx()), (int) ((double) (ans[nu][A][y1])*unit+orig.gety()));
		optB.setCorn((int)( (double)(ans[nu][B][x0])*unit+orig.getx()), (ans[nu][B][y0]*unit+orig.gety()),(ans[nu][B][x1]*unit+orig.getx()), (int) ((double) (ans[nu][B][y1])*unit+orig.gety()));
		optC.setCorn((int)( (double)(ans[nu][C][x0])*unit+orig.getx()), (ans[nu][C][y0]*unit+orig.gety()),(ans[nu][C][x1]*unit+orig.getx()), (int) ((double) (ans[nu][C][y1])*unit+orig.gety()));
		optD.setCorn((int)( (double)(ans[nu][D][x0])*unit+orig.getx()), (ans[nu][D][y0]*unit+orig.gety()),(ans[nu][D][x1]*unit+orig.getx()), (int) ((double) (ans[nu][D][y1])*unit+orig.gety()));
		optE.setCorn((int)( (double)(ans[nu][E][x0])*unit+orig.getx()), (ans[nu][E][y0]*unit+orig.gety()),(ans[nu][E][x1]*unit+orig.getx()), (int) ((double) (ans[nu][E][y1])*unit+orig.gety()));
		optF.setCorn((int)( (double)(ans[nu][F][x0])*unit+orig.getx()), (ans[nu][F][y0]*unit+orig.gety()),(ans[nu][F][x1]*unit+orig.getx()), (int) ((double) (ans[nu][F][y1])*unit+orig.gety()));
		//drawmaps();
		//
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
		Color color = new Color(image.getRGB( x,y));
		return (color.getRed() <= 245 && color.getBlue() <= 245
				&& color.getGreen() <= 245)?true :false;
	}
	public void drawmaps(){
		tmpimgx = cvLoadImage("debug/"+imgname);
		fillimage((int) optA.tl.getx(),(int) optA.tl.gety(),(int) optA.br.getx(),(int) optA.br.gety());
		fillimage((int) optB.tl.getx(),(int) optB.tl.gety(),(int) optB.br.getx(),(int) optB.br.gety());
		fillimage((int) optC.tl.getx(),(int) optC.tl.gety(),(int) optC.br.getx(),(int) optC.br.gety());
		fillimage((int) optD.tl.getx(),(int) optD.tl.gety(),(int) optD.br.getx(),(int) optD.br.gety());
		fillimage((int) optE.tl.getx(),(int) optE.tl.gety(),(int) optE.br.getx(),(int) optE.br.gety());
		fillimage((int) optF.tl.getx(),(int) optF.tl.gety(),(int) optF.br.getx(),(int) optF.br.gety());
		savefilled();
	}
/*
	public void drawmaps(){
		try {
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+imgname);
			file = new File("debug/"+imgname);
			tmpimg = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("Cant read Image debug/"+imgname);
			e.printStackTrace();
		}
		fillimage((int) optA.tl.getx(),(int) optA.tl.gety(),(int) optA.getwidth(),(int) optA.getheight());
		fillimage((int) optB.tl.getx(),(int) optB.tl.gety(),(int) optB.getwidth(),(int) optB.getheight());
		fillimage((int) optC.tl.getx(),(int) optC.tl.gety(),(int) optC.getwidth(),(int) optC.getheight());
		fillimage((int) optD.tl.getx(),(int) optD.tl.gety(),(int) optD.getwidth(),(int) optD.getheight());
		fillimage((int) optE.tl.getx(),(int) optE.tl.gety(),(int) optE.getwidth(),(int) optE.getheight());
		fillimage((int) optF.tl.getx(),(int) optF.tl.gety(),(int) optF.getwidth(),(int) optF.getheight());
		savefilled();
	}
*/
	public void fillimage(int xMin,int yMin,int w,int h){
		/*Graphics graph = tmpimg.createGraphics();
		graph.setColor(Color.red);
		graph.fillRect(x, y, w, h);
		graph.dispose();
		*/
		Highlight(tmpimgx,xMin,yMin, w,  h, 4);
	}
	public void savefilled(){
		cvSaveImage("debug/"+imgname,tmpimgx);
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
        CvScalar color = cvScalar(255,0,0,0);       // blue [green] [red]
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
    public static void ShowImage(IplImage image, String caption, int width, int height)
    {
        CanvasFrame canvas = new CanvasFrame(caption, 1);   // gamma=1
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas.setCanvasSize(width, height);
        canvas.showImage(image);
    }
}
