
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCircle;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvPointFrom32f;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
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
	double [] marktl,marktr,markcl,markbl,markbr;
	Point orig;
	private int unit,dpi,avgradi;
	private Options options;
	double rot=0.0,uerr;
	private Questions questions;
	private JsonObject qrinfo;
	private Seq seq;
	private IplImage	imgx,imgxd1, imgxc1;
	/*
	 * Constructors
	 */
	public OmrModel(){
		logger = Logger.getLogger(OmrModel.class.getName());
		setLog("OmrModel",fh,logger);
		logger.log(Level.INFO, "Create Model");
		unit = 0;
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
			}
			
		}
		return opts;
	}
	/***
	 * Initializing Model
	 * @return boolean
	 */
	protected boolean init(){
		imgx = cvLoadImage(path+DR+filename);
		//cvSaveImage("debug/"+filename, imgx);
		imgxc1 = cvCreateImage(cvGetSize(imgx), imgx.depth(), imgx.nChannels());
		imgxc1 = imgx.clone();
		imgxd1 = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1);
		cvCvtColor(imgxc1, imgxd1, CV_BGR2GRAY);
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
		double 	s = imgx.height()/11.69;
		
		int 	MinArea = (130 <= s && s <= 160)? 4000:20000,
				MaxArea = (130 <= s && s <= 160)? 7000:30000;
		dpi = (int) s;
		unit =(130 <= dpi && dpi <= 160)? 28:58;
		System.out.println("dpi is "+s+" Min "+MinArea+" Max "+MaxArea);
		IplImage 	binary = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1),
					canny = cvCreateImage(cvGetSize(imgx), IPL_DEPTH_8U, 1);

		cvThreshold(imgxd1,binary, 127, 255, CV_THRESH_BINARY);
		cvSmooth(binary,binary,CV_GAUSSIAN,9,9,2,2);
		cvCanny(binary,canny,0,300);
		//cvSmooth(gray,gray,CV_GAUSSIAN,9,9,2,2);
		//cvThreshold(gray,gray, 127, 255, CV_THRESH_BINARY);
		regionchck(canny,MinArea,MaxArea, "top");
		regionchck(binary,MinArea,MaxArea, "bottom");
		//ShowImage(imgxc1, "WorkingImage", 512);
		//cvSaveImage("debug/"+filename+"FIRST-imgxc1.jpg", imgxc1);
		extractunit(imgxc1,false);
		return true;
	}
	/**
	 * Expecting Unit From All Markers ,Optionally Drawing Markers On input image for debugging purposes
	 * @param imgin
	 */
	private void extractunit(IplImage imgin,boolean debug) {
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
			if(debug)Highlight(imgin,  MinX, MinY, MaxX, MaxY, 2);
		}
		System.out.println("UNIT IS"+(avg/ms.size()));
		setUnitOrig((avg/ms.size()),(int) marktl[Blobs.BLOBMINX],(int) marktl[Blobs.BLOBMINY]);
		if(debug)cvSaveImage("debug/"+filename+"-markers.jpg", imgin);
	}
	/***
	 * Detecting Anchors on page
	 * @return boolean
	 */
	public boolean checkAnchors(){
		System.out.println("Marker Top Color "+marktl[Blobs.BLOBCOLOR]);
		System.out.println("Marker Top Color "+marktr[Blobs.BLOBCOLOR]);
		System.out.println("Marker Top Color "+markcl[Blobs.BLOBCOLOR]);
		System.out.println("Marker Top Color "+markbl[Blobs.BLOBCOLOR]);
		System.out.println("Marker Top Color "+markbr[Blobs.BLOBCOLOR]);
		return true;
	}

	/***
	 * Reseting Model
	 */
	public void resetModel(){
		
		unit = 0;
	}
	public boolean setQuestions(int count){
		if(count<=40){
			logger.log(Level.SEVERE,"Actual Quesitions in Quiz are"+count);
			questions = new Questions(count, unit, imgx,orig,getcols(),getrows(),avgr());
			return true;
		}
		logger.log(Level.SEVERE,"Total Questions found from Server exceeded the limit of 40");
		//questions = new Questions(count, unit, image,orig);
		return true;
	}
	public void fillQuestions(int[] optionscount){
		questions.addAllQuestions(optionscount,filename);
		ShowImage(questions.getQgiven(), "QUESTION SHEET", 512);
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
	public void regionchck(IplImage arg,int MinArea,int MaxArea,String loc){
		Blobs	Regions = new Blobs();
		int stx = 0, sty = 0,
		enx = 0, eny = 0,cust = 0;
		if(loc.equals("top")){
			stx = 0; sty = 0;
			enx = arg.width(); eny = arg.height()/4;
		}else if(loc.equals("bottom")){
			stx = 0;
			cust = arg.height() - (arg.height()/4);
			enx =  arg.width();
			eny = arg.height();
			CvRect r = new CvRect();
			r.x(stx);
			r.y(cust);
			r.height(arg.height());
			r.width(arg.width());
			//After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
			cvSetImageROI(arg, r);
		}
		
		Regions.BlobAnalysis(
			arg,		// image
			stx, sty,	// ROI start col, row
			enx,eny,	// ROI cols, rows
			1,			// border (0 = black; 1 = white)
			MinArea);	// minarea
		cvResetImageROI(arg);
		int j = 1;
		for(int i = 1; i <= Blobs.MaxLabel; i++){
			double [] Region = Blobs.RegionData[i];
			if(!(Region[Blobs.BLOBAREA] > MaxArea)){
				if(cust == 0){
					switch(j){
						case 1:
							marktl = Region.clone();
							//marktl[Blobs.BLOBMAXX]= marktl[Blobs.BLOBMAXX]-20;
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
						markbl[Blobs.BLOBMINY] = (int) Region[Blobs.BLOBMINY]+Math.abs(cust);
						markbl[Blobs.BLOBMAXY] = (int) Region[Blobs.BLOBMAXY]+Math.abs(cust);
						break;
					case 2:
						markbr = Region.clone();
						markbr[Blobs.BLOBMINY] = (int) Region[Blobs.BLOBMINY]+Math.abs(cust);
						markbr[Blobs.BLOBMAXY] = (int) Region[Blobs.BLOBMAXY]+Math.abs(cust);
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
	}

	public void circle() {
		CvRect r = new CvRect();
		r.x((int) markcl[Blobs.BLOBMINX]);
		r.y((int) markcl[Blobs.BLOBMAXY]);
		System.out.println("OFFSET IN Y"+(int) markcl[Blobs.BLOBMAXY]);
		r.height(-(int) markcl[Blobs.BLOBMAXY] + (int) markbl[Blobs.BLOBMINY]);
		r.width(-(int) markbl[Blobs.BLOBMINX] + (int) markbr[Blobs.BLOBMAXX]);
		//After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
		cvSetImageROI(imgxd1, r);
		//ShowImage(imgxd1, "QUESTIONS VIEW", 512);	
		
		//ShowImage(GrayImage, "CvtColor");
		cvSmooth(imgxd1,imgxd1,CV_GAUSSIAN,9,9,2,2);
		//ShowImage(GrayImage, "Smoothing");
		//ShowImage(GrayImage, "GrayImage", 512);
		//IplImage BWImage = cvCreateImage(cvGetSize(GrayImage), IPL_DEPTH_8U, 1); 
		cvThreshold(imgxd1, imgxd1, 200, 255, CV_THRESH_BINARY);
		//ShowImage(BWImage, "ThreshHolding");
		CvMemStorage storage =CvMemStorage.create(); //storage area for all contours
		
		//cvFindContours(BWImage, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
		cvSmooth( imgxd1, imgxd1,CV_GAUSSIAN,9,9,2,2);
		cvCanny(imgxd1,imgxd1,0,300);
		cvSmooth( imgxd1, imgxd1,CV_GAUSSIAN,9,9,2,2);
		//cvCanny(arg0, arg1, arg2, arg3, arg4)
		//cvSobel(BWImage, BWImage, 1, 1);
		
		//ShowImage(BWImage, "Canny");
		//System.out.println(BWImage.height()*.05);
		int cendist = (130 <= dpi && dpi <= 160)? 50:100,
			canth 	= (130 <= dpi && dpi <= 160)? 100:100,
			centh 	= (130 <= dpi && dpi <= 160)? 37:37,
			minr 	= (130 <= dpi && dpi <= 160)? 13:23,
			maxr 	= (130 <= dpi && dpi <= 160)? 28:56;
		avgradi = (minr+maxr)/2;

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
		cvResetImageROI(imgxd1);
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
		//ShowImage(imgxc1, "Canny",512);
		cvSaveImage("debug/"+filename+"-trans.jpg", imgxc1);
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

	public String[] getresults() {
		return questions.getAllOptions();
	}
	public void drawgrid(){
		cvSaveImage("debug/"+filename+"-rtQGrid.jpg",questions.drawQgrid());
	}
	public int[] getQrrect(){
		return new int[] {(int) marktl[Blobs.BLOBMINX],(int) marktl[Blobs.BLOBMAXY], (int) marktl[Blobs.BLOBMAXX]-(int) marktl[Blobs.BLOBMINX] ,(int) markcl[Blobs.BLOBMINY]-(int) marktl[Blobs.BLOBMINY]};
	}

	public IplImage getiplimg() {
		return imgxc1;
	}
}
