
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCircle;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
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
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_legacy.CvImage;
import org.bytedeco.javacv.Blobs;
import org.bytedeco.javacv.CanvasFrame;

import com.google.gson.JsonArray;
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
	private CvPoint corner,Qstp,Qsbr,Qrtp,Qrbr;
	Point mtlst,mtlend;
	double [] marktl,marktr,markcl,markbl,markbr;
	Point orig;
	private int unitx,dpi,avgradi;
	private Options options;
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
		Qstp = cvPoint(0, 0);
		Qsbr = cvPoint(0, 0);
		Qrtp  = cvPoint(0, 0);
		Qrbr  = cvPoint(0, 0);
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
		IplImage 	gray = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					binary = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					smooth = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					canny = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					imgxc1 = cvCreateImage(cvGetSize(imgx), imgx.depth(), imgx.nChannels());
		imgxc1 = imgx.clone(); 
		cvCvtColor(imgxc1, gray, CV_BGR2GRAY);
		cvThreshold(gray,gray, 127, 255, CV_THRESH_BINARY);
		cvSmooth(gray,gray,CV_GAUSSIAN,9,9,2,2);
		cvCanny(gray,canny,0,300);
		//cvSmooth(gray,gray,CV_GAUSSIAN,9,9,2,2);
		//cvThreshold(gray,gray, 127, 255, CV_THRESH_BINARY);
		regionchck(canny,imgxc1, MinArea,MaxArea, "top");
		regionchck(gray,imgxc1, MinArea,MaxArea, "bottom");
		ShowImage(imgxc1, "WorkingImage", 512);
		cvSaveImage("debug/"+filename+"FIRST-imgxc1.jpg", imgxc1);
		drawmarkers(imgxc1);
		return true;
	}

	private void drawmarkers(IplImage imgin) {
		IplImage imgxc1 = cvCreateImage(cvGetSize(imgx), imgx.depth(), imgx.nChannels());
		imgxc1 = imgin.clone();
		int avg = 0;
		List<double[]> ms = new ArrayList<double[]>();
		ms.add(marktl);
		ms.add(marktr);
		ms.add(markcl);
		ms.add(markbl);
		ms.add(markbr);
		for (int i = 0; i < ms.size(); i++) {
			int MinX = (int) ms.get(i)[Blobs.BLOBMINX],
				MinY = (int) ms.get(i)[Blobs.BLOBMINY],
				MaxX = (int) ms.get(i)[Blobs.BLOBMAXX],
				MaxY = (int) ms.get(i)[Blobs.BLOBMAXY];
				avg += (MaxY-MinY);
				System.out.println("UNIT IS"+(MaxY-MinY));
			Highlight(imgin,  MinX, MinY, MaxX, MaxY, 2);
		}
		System.out.println("UNIT IS"+(avg/ms.size()));
		setUnitOrig((avg/ms.size()),(int) marktl[Blobs.BLOBMINX],(int) marktl[Blobs.BLOBMINY]);
		cvSaveImage("debug/1FIRST-imgxc1.jpg", imgin);
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
			questions = new Questions(count, unit, image,orig,getcols(),getrows(),avgr(),Qstp,Qsbr);
			return true;
		}
		logger.log(Level.SEVERE,"Total Questions found from Server exceeded the limit of 40");
		//questions = new Questions(count, unit, image,orig);
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
		Blobs	Regions = new Blobs();
		int stx = 0, sty = 0,
		enx = 0, eny = 0,cust = 0;
		if(loc.equals("top")){
			stx = 0; sty = 0;
			enx = imgxd1.width(); eny = imgxd1.height()/4;
		}else if(loc.equals("bottom")){
			stx = 0;
			cust = imgxd1.height() - (imgxd1.height()/4);
			enx =  imgxd1.width();
			eny = imgxd1.height();
			CvRect r = new CvRect();
			r.x(stx);
			r.y(cust);
			r.height(imgxd1.height());
			r.width(imgxd1.width());
			//After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
			cvSetImageROI(imgxd1, r);
		}
		
		Regions.BlobAnalysis(
			imgxd1,		// image
			stx, sty,	// ROI start col, row
			enx,eny,	// ROI cols, rows
			1,			// border (0 = black; 1 = white)
			MinArea);	// minarea
		int j = 1;
		for(int i = 1; i <= Blobs.MaxLabel; i++){
			double [] Region = Blobs.RegionData[i];
			if(!(Region[Blobs.BLOBAREA] > MaxArea)){
				int Color = (int) Region[Blobs.BLOBCOLOR];
				int MinX = (int) Region[Blobs.BLOBMINX];
				int MaxX = (int) Region[Blobs.BLOBMAXX];
				int MinY = (int) Region[Blobs.BLOBMINY];
				int MaxY = (int) Region[Blobs.BLOBMAXY];
				System.out.println("J="+j+" ("+MinX+","+(MinY+Math.abs(cust))+")");
				if(cust == 0){
					switch(j){
						case 1:
							marktl = Region.clone();
							break;
						case 2:
							marktr = Region.clone();
							break;
						case 3:
							markcl = Region.clone();
							break;
						default:
							logger.log(Level.SEVERE,"Found More Than Three Anchors");
							break;
					}
				}else{
					switch(j){
					case 1:
						markbl = Region.clone();
						markbl[Blobs.BLOBMINY] = MinY+Math.abs(cust);
						markbl[Blobs.BLOBMAXY] = MaxY+Math.abs(cust);
						break;
					case 2:
						markbr = Region.clone();
						markbr[Blobs.BLOBMINY] = MinY+Math.abs(cust);
						markbr[Blobs.BLOBMAXY] = MaxY+Math.abs(cust);
						break;
					default:
						logger.log(Level.SEVERE,"Found More Than Two Bottom Anchors");
						break;
					}
				}
				j++;
				//Highlight(imgxc1,  MinX, MinY+Math.abs(cust), MaxX, MaxY+Math.abs(cust), 2);
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
		CvRect r = new CvRect();
		r.x((int) markcl[Blobs.BLOBMINX]);
		r.y((int) markcl[Blobs.BLOBMAXY]);
		System.out.println("OFFSET IN Y"+(int) markcl[Blobs.BLOBMAXY]);
		r.height(-(int) markcl[Blobs.BLOBMAXY] + (int) markbl[Blobs.BLOBMINY]);
		r.width(-(int) markbl[Blobs.BLOBMINX] + (int) markbr[Blobs.BLOBMAXX]);
		//After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
		cvSetImageROI(imgxd1, r);
		ShowImage(imgxd1, "QUESTIONS VIEW", 512);
		
		
		
		//ShowImage(GrayImage, "CvtColor");
		cvSmooth(imgxd1,imgxd1,CV_GAUSSIAN,9,9,2,2);
		//ShowImage(GrayImage, "Smoothing");
		//ShowImage(GrayImage, "GrayImage", 512);
		//IplImage BWImage = cvCreateImage(cvGetSize(GrayImage), IPL_DEPTH_8U, 1); 
		cvThreshold(imgxd1, imgxd1, 200, 255, CV_THRESH_BINARY);
		//ShowImage(BWImage, "ThreshHolding");
		CvSeq contours = new CvSeq();	//hold the pointer to a contour in the memory block
		CvSeq result = new CvSeq();		//hold sequence of points of a contour
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
		avgradi = (minr+maxr)/2;
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

			if(radius<=80){
				center.x(center.x()+(int) markcl[Blobs.BLOBMINX]);
				center.y(center.y()+(int) markcl[Blobs.BLOBMAXY]);
				arx[i] = center.x();
				ary[i] = center.y();
				JsonObject point = new JsonObject();
				point.addProperty("x", center.x());
				point.addProperty("y", center.y());
				point.addProperty("r", radius);
				point.addProperty("s", 0);
				points.add(point);
				cvCircle(imgxc1, center, radius, CvScalar.GREEN, 3, CV_AA, 0);
			}
		}
		options = new Options(points, arx, ary,65,45,65,45,320,300);
		options.organise();

		//options.indexit(12, 28,5);
		/*
		System.out.println(Arrays.toString(ary));
		Arrays.sort(ary);
		System.out.println(Arrays.toString(ary));
		System.out.println(points.toString());
		List<JsonObject> ysort = sortby(points,removeDuplicates(ary),"y");
		addindex(ysort);*/
		ShowImage(imgxc1, "Canny",512);
		cvSaveImage("debug/2323232332FIRST-imgxc1.jpg", imgxc1);
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
	public JsonArray getcols(){
		return options.getcols();
	}
	public JsonArray getrows(){
		return options.getrows();
	}
	public int avgr(){
		return avgradi;
	}
	
	public IplImage Cropper(IplImage orig,CvRect r){
		r = new CvRect();
		r.position(0);
		cvSetImageROI(orig, r);
		IplImage cropped = cvCreateImage(cvGetSize(orig), orig.depth(), orig.nChannels());
		cvCopy(orig, cropped);
		return cropped;
	}

	public void slice() {
		System.out.println("Slicing Images for QrCode Area, Question Area");
		System.out.println("QRCODE TPL ("+marktl[Blobs.BLOBMINX]+","+marktl[Blobs.BLOBMAXY]+") BR ("+markcl[Blobs.BLOBMAXX]+","+markcl[Blobs.BLOBMINY]+")");
	}
}
