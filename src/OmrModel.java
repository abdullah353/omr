
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCircle;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvPointFrom32f;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import helper.Options;
import helper.Point;
import helper.Questions;
import helper.Rectangle;
import helper.Seq;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Blobs;
import org.bytedeco.javacv.CanvasFrame;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import config.Config;




public class OmrModel extends Config{
	/*
	 * Attributes
	 */
	private Logger logger;
	private FileHandler fh;
	String filename,path,nameonly,sheetid,aid,dkey;
	Rectangle mtl,mcl,mrr,qr;
	private CvPoint corner;
	Point mtlst,mtlend;
	Point orig;
	private int unitx,dpi;
	public int 	unit;
	BufferedImage image,debugimage;
	private IplImage imgx;
	double rot=0.0,uerr;
	public Questions questions;
	private JsonObject qrinfo;
	private Seq seq;
	/*
	 * Constructors
	 */
	public OmrModel(){
		logger = Logger.getLogger(OmrModel.class.getName());
		setLog("OmrModel",fh,logger);
		logger.log(Level.INFO, "Create Model");
		mtlst = new Point();
		mtlend = new Point();
		unit = 0;
		corner = cvPoint(0, 0);
	}
	
	/*
	 * Methods
	 */
	public void setseq(Seq in){
		seq = in;
	}
	public void setqrinfo(String qrstr){
		JsonParser parser = new JsonParser();
		qrinfo = (JsonObject)parser.parse(qrstr);
		setaid(qrinfo.get("id").getAsString());
		setdkey(getaid().substring(getaid().length() - 5));
	}
	public void setaid(String aid){
		this.aid = aid;
	}
	public void setdkey(String dkey){
		this.dkey = dkey;
	}
	public String getdkey(){
		return dkey;
	}
	public String getaid(){
		return aid;
	}
	public JsonObject getQSeq(){
		return seq.getqSeq();
	}
	public String getQnameat(int i){
		return seq.getqnameat(i);
	}
	public JsonObject getOptSeq(){
		return seq.getoptSeq();
	}
	public JsonObject getOptSeq(String queid){
		return (JsonObject) seq.getoptSeq().get(queid);
	}
	public int getOptCount(String queid){
		JsonObject ans = (JsonObject) seq.getoptSeq().get(queid);
		int count =0;
		if(ans.get("A")!=null) count++;
		if(ans.get("B")!=null) count++;
		if(ans.get("C")!=null) count++;
		if(ans.get("D")!=null) count++;
		if(ans.get("E")!=null) count++;
		if(ans.get("F")!=null) count++;
		return count;
	}
	public String getstudent(){
		return seq.getStudentName();
	}
	public int getQuestions(){
		JsonObject ques = getQSeq();
		int count =0;
		for (int i = 0; i <= 40; i++) {
			if(ques.get(String.valueOf(i)) != null){
				count++;
			}else{
				break;
			}
		}
		return count;
	}
	public int[] getoptions(){
		int opts[] = new int[getQuestions()];
		String label[] = {"A","B","C","D","E","F"};
		for (int i = 0; i < opts.length; i++) {
			int count = 0;
			for (int j = 0; j < 6; j++) {
				if(getOptSeq(getQnameat(i)).get(label[j]) != null){
					count++;
					opts[i] = count;
				}else{
					break;
				}
						//System.out.println(getOptSeq(getQnameat(i)));
			}
			
		}
		return opts;
	}
	/***
	 * Initializing Model
	 * @return boolean
	 */
	protected boolean init(){
		File file1 = new File(path+DR+filename);
		try {
			logger.log(Level.INFO, "Storing Image in Buffer");
			image = ImageIO.read(file1);
			imgx = cvLoadImage(path+DR+filename);
			cvSaveImage("debug/"+filename, imgx);
			setShapes();
			return true;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Can't Open/Store Image file");
			e.printStackTrace();
			return false;
		}
	}
	
	/***
	 * Setting Objects in Models
	 */
	public void setShapes(){
		logger.log(Level.INFO, "Creating 3 Markers and QR");
		mtl	 =	new Rectangle(image);
		mcl	 =	new Rectangle(image);
		mrr	 =	new Rectangle(image);
		qr	 =	new Rectangle(image);
	}
	
	/***
	 * Detecting if pixel is black
	 * @param x integer
	 * @param y integer
	 * @return boolean
	 */
	public boolean isblackp(int x,int y){
		Color color = new Color(image.getRGB( x,y));
		return (color.getRed() <= markth && color.getBlue() <= markth 
				&& color.getGreen() <= markth)?true :false;
	}
	/***
	 * Finding first Marker and set our unit accordingly
	 * @return boolean
	 */
	public boolean foundMarker(){
		logger.log(Level.INFO ,"Checking Whether We Found Marker");
		logger.log(Level.INFO,"Step 1 Checking Expected Width");
		int exp2U = 0,
			fix,
			hi = 0;
		exp2U = mtlst.gety();
		logger.log(Level.INFO ,"Checking Width expOrig y is "+exp2U);
		int yi = exp2U;
		int err = 0;
		while(isblackp(mtlst.getx(),yi)){
			hi++;
			yi++;
		}
		logger.log(Level.INFO ,"Setting end y position as "+(yi-err+1));
		if(err!=0){mtlend.sety(yi-err+1);}else{mtlend.sety(yi);}
		fix = (err!=0)?hi-1:hi-1;
		setUnitOrig(fix+1,mtlst.getx(),mtlst.gety());
		logger.log(Level.INFO ,"Arbitray start"+mtlst.getp()+" mtlend "+mtlend.getp() );
		return true;
	}
	
	/*
	 * Setter For unit
	 */
	public void setUnitOrig(int expu,int origx,int origy){
		orig = new Point(origx,origy);
		unit = expu;
		System.out.println("Adjusted Unit as "+unit+" orig as "+orig.getp());

	}
	/***
	 * Looking for reference rectangles
	 * 
	 */
	public boolean lookref(){
		List<Integer> units = new ArrayList<Integer>();
		double 	parea = imgx.height()*imgx.width(),
				s = imgx.height()/11.69;
    	
		int 	MinArea = (130 <= s && s <= 160)? 4000:20000,
				MaxArea = (130 <= s && s <= 160)? 7000:30000,
				sum = 0,
				avg = 0;
		dpi = (int) s;
		unitx =(130 <= dpi && dpi <= 160)? 28:58;
		System.out.println("dpi is "+s+" Min "+MinArea+" Max "+MaxArea);
		IplImage 	imgxd1 = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					imgxc1 = cvCreateImage(cvGetSize(imgx), imgx.depth(), imgx.nChannels());
		imgxc1 = imgx.clone();
		cvCvtColor(imgx, imgxd1, CV_BGR2GRAY);
		cvSmooth( imgxd1, imgxd1,CV_GAUSSIAN,9,9,2,2);
		units.addAll(regionchck(imgxd1,imgxc1, MinArea,MaxArea, "topleft"));
		//ShowImage(imgxc1, "WorkingImage", 512);
		for (int i = 0; i < units.size(); i++) {
			sum += units.get(i);
		}
		avg = sum/units.size();
		estimatecorner(avg,MinArea,MaxArea);
		cvSaveImage("debug/"+filename+"FIRST-imgxc1.jpg", imgxc1);
		return true;
	}
	private void estimatecorner(int avg,int MinArea,int MaxArea) {
		List<Integer> units = new ArrayList<Integer>();
		int sum =0,avg2=0;
		IplImage 	imgxd1 = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					imgxc1 = cvCreateImage(cvGetSize(imgx), imgx.depth(), imgx.nChannels());
		imgxc1 = imgx.clone(); 
        cvCvtColor(imgxc1, imgxd1, CV_BGR2GRAY);
        cvSmooth(imgxd1,imgxd1,CV_GAUSSIAN,9,9,2,2);
        cvCanny(imgxd1,imgxd1,0,300);
        cvThreshold(imgxd1,imgxd1, 127, 255, CV_THRESH_BINARY);
        //cvSaveImage(filename+"imgxc1canny.jpg", imgxd1);
        
        //ShowImage(imgxd1, "imgxd1",512);
        units.addAll(regionchck(imgxd1,imgxc1, MinArea,MaxArea, "topleft"));
		//ShowImage(imgxc1, "WorkingImage", 512);
		for (int i = 0; i < units.size(); i++) {
			sum += units.get(i);
		}
		avg2 = sum/units.size();
        
        //ShowImage(imgxc1, "imgsssss1",512);
        cvSaveImage("debug/"+filename+"SECOND-imgxc1.jpg", imgxc1);
		setUnitOrig(avg2,corner.x(),corner.y());
	}

	/***
	 * Detecting Anchors on page
	 * @return boolean
	 */
	public boolean checkAnchors(){
		setlayout(unit, orig.getx(), orig.gety());
		mtl.setCorn(new Point(layout[firstmark][x0],layout[firstmark][y0]),
					new Point(layout[firstmark][x1],layout[firstmark][y1]));
		mcl.setCorn(new Point(layout[secondmark][x0],layout[secondmark][y0]),
					new Point(layout[secondmark][x1],layout[secondmark][y1]));
		mrr.setCorn(new Point(layout[thirdmark][x0],layout[thirdmark][y0]),
					new Point(layout[thirdmark][x1],layout[thirdmark][y1]));
		/***
		 * BLOCK#1: DELETE THIS BLOCK ITS FOR CONSOLE
		 */
		
		if(mtl.isBlack()){
			System.out.println("First Anchor is valid");
		}
		if(mcl.isBlack()){
			System.out.println("Second Anchor is valid");
		}
		if(mrr.isBlack()){
			System.out.println("Third Anchor is cvalid");
		}
		
		//showQBlueprint(q1,unit,unit);
		
		/***
		 * END BLOCK#1
		 */
		return (mtl.isBlack() && mcl.isBlack() )?true:false;
	}
	public void fillimage(int x,int y,int w,int h){
		File file1 = new File("debug/"+nameonly+".jpg");
		try {
			BufferedImage ebugimage = ImageIO.read(file1);
			Graphics graph = ebugimage.createGraphics();
			graph.setColor(Color.red);
			graph.fillRect(x, y, h, w);
			graph.dispose();
			ImageIO.write(ebugimage, "jpg", new File("debug/"+nameonly+".jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * Reseting Model
	 */
	public void resetModel(){
		mtlst.empty();
		mtlend.empty();
		unit = 0;;
	}
	public boolean setQuestions(int count){
		if(count<=40){
			logger.log(Level.SEVERE,"Actual Quesitions in Quiz are"+count);
			questions = new Questions(count, unit, image,orig);
			return true;
		}
		logger.log(Level.SEVERE,"Total Questions found from Server exceeded the limit of 40");
		questions = new Questions(count, unit, image,orig);
		return true;
	}

	public void setpaths(String name, String directory) {
		filename = name;
		path = directory;
		nameonly = filename.substring(0,filename.lastIndexOf("."));
		logger.log(Level.INFO, "Selected Image "+path+DR+filename);
	}

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
    /***
     * Detecting references for specified region of page
     * @param imgxc1
     * @param MinArea
     * @param loc
     * @return
     */
    public List<Integer> regionchck(IplImage imgxd1,IplImage imgxc1,int MinArea,int MaxArea,String loc){
    	List<Integer> units = new ArrayList<Integer>();
    	Blobs 	Regions = new Blobs();
    	int stx = 0, sty = 0,
    		enx = 0, eny = 0;
    	if(loc.equals("topleft")){
    		stx = 0; sty = 0;
    		enx = imgxd1.width(); eny = imgxd1.height()/4;
    	}else if(loc.equals("topright")){
    		stx = 800; sty = 800;
    		enx =  imgxd1.width(); eny = imgxd1.height();    		
    	}
    	Regions.BlobAnalysis(
				imgxd1,		// image
                stx, sty,   // ROI start col, row
                enx,eny,	// ROI cols, rows                 
                1,          // border (0 = black; 1 = white)
                MinArea);   // minarea
		for(int i = 1; i <= Blobs.MaxLabel; i++)
        {
            double [] Region = Blobs.RegionData[i];
            if(!(Region[Blobs.BLOBAREA] > MaxArea)){
            int Parent = (int) Region[Blobs.BLOBPARENT];
            int Color = (int) Region[Blobs.BLOBCOLOR];
            int MinX = (int) Region[Blobs.BLOBMINX];
            int MaxX = (int) Region[Blobs.BLOBMAXX];
            int MinY = (int) Region[Blobs.BLOBMINY];
            int MaxY = (int) Region[Blobs.BLOBMAXY];
            if(i==1){
            	corner.x(MinX);
            	corner.y(MinY);
            }
            units.add(MaxY-MinY);
            System.out.println("Expected unit is "+(MaxY-MinY)+"Color is"+Color+" Parent "+Region[Blobs.BLOBSUMXX]);
            //System.out.println(MinX+","+MinY+"  "+MaxX+","+MaxY+" AREA + "+bArea+ " percentage"+perc*100+"diagl "+distance(p1, p2));
            Highlight(imgxc1,  MinX, MinY, MaxX, MaxY, 2);
            }else{
            	System.out.println("TOO LARGE AREA"+Region[Blobs.BLOBAREA]);
            }
        }
		return units;
    }

	public void circle() {
		int MinArea = 6;
		int ErodeCount =0;
		int DilateCount = 0;
		ErodeCount =2;
		DilateCount = 1;
		MinArea = 250;
		

		IplImage	imgxd1 = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					imgxc1 = cvCreateImage(cvGetSize(imgx), imgx.depth(), imgx.nChannels());
		imgxc1 = imgx.clone(); 
		imgxd1 = cvCreateImage(cvGetSize(imgxc1), IPL_DEPTH_8U, 1);

		cvCvtColor(imgxc1, imgxd1, CV_BGR2GRAY);
		//ShowImage(GrayImage, "CvtColor");
		cvSmooth(imgxd1,imgxd1,CV_GAUSSIAN,9,9,2,2);
		//ShowImage(GrayImage, "Smoothing");
		//ShowImage(GrayImage, "GrayImage", 512);
		//IplImage BWImage = cvCreateImage(cvGetSize(GrayImage), IPL_DEPTH_8U, 1); 
		cvThreshold(imgxd1, imgxd1, 200, 255, CV_THRESH_BINARY);
		//ShowImage(BWImage, "ThreshHolding");
		CvSeq contours = new CvSeq();;  //hold the pointer to a contour in the memory block
		CvSeq result = new CvSeq();;   //hold sequence of points of a contour
		CvMemStorage storage =CvMemStorage.create(); //storage area for all contours
		
		//cvFindContours(BWImage, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
		cvSmooth( imgxd1, imgxd1,CV_GAUSSIAN,9,9,2,2);
		cvCanny(imgxd1,imgxd1,0,300);
		cvSmooth( imgxd1, imgxd1,CV_GAUSSIAN,9,9,2,2);
		//cvCanny(arg0, arg1, arg2, arg3, arg4)
		//cvSobel(BWImage, BWImage, 1, 1);
		
		//ShowImage(BWImage, "Canny");
		//System.out.println(BWImage.height()*.05);
		int minradii = (int) (imgxd1.height()*.0085);
		int maxradii = (int) (imgxd1.height()*.015);
		int avg = minradii+maxradii/2;
		int cendist = (130 <= dpi && dpi <= 160)? 50:100,
			canth 	= (130 <= dpi && dpi <= 160)? 100:100,
			centh 	= (130 <= dpi && dpi <= 160)? 37:37,
			minr 	= (130 <= dpi && dpi <= 160)? 13:23,
			maxr 	= (130 <= dpi && dpi <= 160)? 28:56;
		//System.out.println(minradii+","+maxradii);
		CvSeq circles = cvHoughCircles(
				imgxd1, //Input image
				storage, //Memory Storage
				CV_HOUGH_GRADIENT, //Detection method
				1, //Inverse ratio 1
				cendist, //Minimum distance between the centers of the detected circles avg
				canth, //Higher threshold for canny edge detector 90
				centh, //Threshold at the center detection stage 39
				minr, //min radius 25
				maxr//max radius 100
				);
		int[] arx = new int[circles.total()];
		int[] ary = new int[circles.total()];
		List<JsonObject> points = new ArrayList<JsonObject>();

		for(int i = 0; i < circles.total(); i++){
			CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
			
			CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle));
			int radius = Math.round(circle.z());
			//REMOVE THIS TRICK PLEASE
			if(i<30){
				arx[i] = center.x();
				ary[i] = center.y();
				JsonObject point = new JsonObject();
				point.addProperty("x", center.x());
				point.addProperty("y", center.y());
				point.addProperty("r", radius);
				point.addProperty("s", 0);
				points.add(point);
			}
			if(radius<=80)
				cvCircle(imgxc1, center, radius, CvScalar.GREEN, 3, CV_AA, 0);
		}
		Options options = new Options(points, arx, ary);
		options.organise();
		//options.indexit(12, 28,5);
		/*
		System.out.println(Arrays.toString(ary));
		Arrays.sort(ary);
		System.out.println(Arrays.toString(ary));
		System.out.println(points.toString());
		List<JsonObject> ysort = sortby(points,removeDuplicates(ary),"y");
		addindex(ysort);*/
		//ShowImage(imgxc1, "Canny",512);
	}
	
	
	public String join(String[] ar,String sep){
		String ret = "";
		for (int i = 0; i < ar.length; i++) {
			ret += ar[i];
		}
		return ret;
	}
	public List<JsonObject> strnglist(String[] ar){
		List<JsonObject> json2 = new ArrayList<JsonObject>();
		for (int i = 0; i < ar.length; i++) {
			
		}
		return json2;
	}

}
