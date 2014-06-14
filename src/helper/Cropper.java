package helper;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class Cropper {
	private IplImage imgorig,imgret;
	public IplImage Cropper(IplImage imgin,CvPoint tp,CvPoint br) {
		imgorig = cvCreateImage(cvGetSize(imgin), imgin.depth(), imgin.nChannels());
		imgorig = imgin.clone();
		CvRect r = new CvRect(tp);
		r.height(tp.y()-br.y());
		r.width(tp.x()-br.x());
		//After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
		cvSetImageROI(imgorig, r);
		IplImage cropped = cvCreateImage(cvGetSize(imgorig), imgorig.depth(), imgorig.nChannels());
		//Copy original image (only ROI) to the cropped image
		cvCopy(imgorig, imgret);
		cvSaveImage("cropped.png", imgret);
		return imgret;
	}
}