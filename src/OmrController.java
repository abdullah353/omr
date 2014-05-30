import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import jxl.write.WriteException;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import config.Config;

class OmrController extends Config{
	OmrModel sheet;
	OmrView view;
	GetDocs docs;
	private Logger logger = Logger.getLogger(OmrModel.class.getName());
	private FileHandler fh;
	
	public OmrController(OmrModel sheet,OmrView view) {
		setLog("OmrController",this.fh,this.logger);
		logger.log(Level.INFO, "Initializing Controller");
		this.sheet=sheet;
		this.view=view;
	}
	public boolean startApp(){
		logger.log(Level.INFO, "Starting Applicaton");
		this.view.subView(view.browsePanel());
		this.view.browse.addActionListener(new OpenL());
		this.view.scan.addActionListener(new Scan());
		return false;
	}
	public static String readQRCode(String filePath, String charset)
		throws FileNotFoundException, IOException, NotFoundException {
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
		new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
		Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
		return qrCodeResult.getText();
	}
	/*
	 * Listening Events on Browse Click
	 */
	class OpenL implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JFileChooser c = new JFileChooser();
			FileFilter ft = new FileNameExtensionFilter("Image Files", "png","jpg");
			c.setFileFilter(ft);
			sheet.resetModel();
			int rVal = c.showOpenDialog(new JPanel());
			if (rVal == JFileChooser.APPROVE_OPTION) {
				
				sheet.filename = c.getSelectedFile().getName();
				sheet.path = c.getCurrentDirectory().toString();
				
				logger.log(Level.INFO, "Selected Image "+sheet.path+DR+sheet.filename);
				
				view.filename.setText(sheet.filename);
				view.dir.setText(sheet.path);
				view.subView(view.scanPanel());
			}
			if (rVal == JFileChooser.CANCEL_OPTION) {
				logger.log(Level.INFO, "User Canceled Explorer No Image Selected");
				view.filename.setText("Operation Canceled");
				view.subView(view.scanPanel(), false);
			}
		}
	}
	
	class Scan implements ActionListener{
		public void actionPerformed(ActionEvent e){
			logger.log(Level.INFO, "Initiated Scanning");
			sheet.init();
			if(sheet.searchUnit()){
				System.out.println("Unit Found Unit="+sheet.unit);
				if(sheet.checkAnchors()){
					System.out.println("Anchors Checked");
					//setting up all questions
					initDocs();
					try {
						initQuestions(sheet.getQuestions(),sheet.getoptions());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					String[] results = sheet.questions.getAllOptions();
					System.out.print("b = "+Arrays.toString(results));
					sheet.questions.addAllQuestions(sheet.getoptions());
					genrslt(docs,sheet.getstudent(),"OMR", results);
					view.filename.setText("Ready To Submit");
					docs.push();
					try {
						sheet.questions.genExcel("test2", "Sheets");
					} catch (WriteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					System.out.println("Can't Validate Achors");
					sheet.resetModel();
				}
			}else{
				sheet.resetModel();
				System.out.println("Can't Found Unit");
			}
		}
	}
	public void initDocs(){
		docs = new GetDocs(sheet.path+DR+sheet.filename);
		sheet.setqrinfo(docs.getqrstr());
		sheet.setseq(docs.getMap());
		logger.log(Level.INFO,"Student Name "+sheet.getstudent());
		logger.log(Level.INFO,"Questions Details "+sheet.getQSeq().toString());
		logger.log(Level.INFO,"Total Questions "+sheet.getQuestions());
		logger.log(Level.INFO,"Options Details "+sheet.getOptSeq());
		
	}
	public void initQuestions(int total,int[] options ) throws IOException{
		sheet.setQuestions(total);
		sheet.questions.addAllQuestions(options);
		sheet.questions.getQuestion(0).setOverview(sheet.unit);
	}
	public void genrslt(GetDocs docs,String name,String enu, String[] results){
		if(docs.subtst(enu,name,results)){
			System.out.println("Subtest Ready");
		}else{
			System.out.println("Error To DB");
		}
	}
}
