package couch;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.lightcouch.CouchDbClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

//Based on http://www.lightcouch.org/lightcouch-guide.html#dependencies

public class GetDocs {
	private String serial,aid,dkey,student,qrstr;
	private Seq seqdoc;
	private CouchDbClient dbtang;
	private JsonObject 	rslt;
	private IplImage in;
	private Logger logger = Logger.getLogger(GetDocs.class.getName());
	public void setKey(String aid){
		this.aid = aid;
		this.dkey = this.aid.substring(this.aid.length() - 5);
	}
	
	public GetDocs(IplImage in,int[] rect){
		this.in = in;
		CvRect r = new CvRect();
		r.x(rect[0]);
		r.y(rect[1]);
		r.height(rect[3]);
		r.width(rect[2]);
		cvSetImageROI(in, r);
		qrstr = readQRCode();
		cvResetImageROI(in);
		//System.out.println("Data read from QR Code: "+ qrstr);
		JsonParser parser = new JsonParser();
		JsonObject qrcode = (JsonObject)parser.parse(qrstr);
		if( qrcode.get("id").getAsString() != null && qrcode.get("StudentName") != null){
			serial = qrcode.get("id").getAsString();
			student = qrcode.get("StudentName").getAsString();
			logger.log(Level.INFO, "Sheets Issued For "+ student +" , Serial Number "+ serial);
		}else{
			logger.log(Level.SEVERE, "No Information in QrRCode");
		}
	}
	public String getqrstr(){
		return qrstr;
	}
	public boolean subtst(String enu,String name, String[] results){
		dbtang = new CouchDbClient("couchdb.properties");
		List<JsonObject> json = dbtang.view("tangerine/byDKey")
										.includeDocs(true)
										.key(dkey)
										.query(JsonObject.class);
		List<JsonObject> json2 = new ArrayList<JsonObject>();
		rslt = new JsonObject();
		String col ="";
		JsonArray colls = new JsonArray();
		for (int i = 0; i < json.size(); i++) {
			JsonObject el = json.get(i);
			SubtestDoc subDoc = new SubtestDoc();
			if(el.get("collection") != null){
				col = el.get("collection").getAsString();
				switch (col){
					case "assessment":
						rslt.add("assessmentId", el.get("assessmentId"));
						rslt.add("assessmentName", el.get("name"));
						rslt.addProperty("start_time", System.currentTimeMillis());
						rslt.addProperty("enumerator", enu);
						rslt.addProperty("collection", "result");
						break;
					case "subtest":
						//System.out.println(el.get("name").getAsString());
						subDoc.setname(el.get("name").getAsString());
						subDoc.setprototype(el.get("prototype").getAsString());
						subDoc.setsubtestId(el.get("_id").getAsString());
						if (el.get("prototype").getAsString().equals("location") || el.get("prototype").getAsString().equals("datetime")){
							subDoc.setsum(1, 0, 0, 1);
						}else{
							subDoc.setsum(0, 0, 0, 0);
						}
						if(el.get("prototype").getAsString().equals("location")){
							subDoc.setloc(name);
						}else{
							subDoc.setdata(json,el.get("prototype").getAsString(),seqdoc.getqSeq(),results,seqdoc.getoptSeq());
						}
						
						colls.add(subDoc.toJson());
						break;
					default:
						break;
				}
			}
		}
		SubtestDoc comp = new SubtestDoc();
		comp.setname("Assessment complete");
		comp.setprototype("complete");
		comp.setsubtestId("result");
		comp.setsum(1, 0, 0, 1);
		comp.setdata("complete");
		colls.add(comp.toJson());
		json2.add(comp.toJson());
		rslt.add("subtestData", colls);
		//System.out.println(rslt);
		dbtang.shutdown();
		return true;
	}
	public String readQRCode(){
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
		new BufferedImageLuminanceSource(in.getBufferedImage())));
		Result qrCodeResult;
		try {
			qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
			return qrCodeResult.getText();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "false";
		}
	public Seq getMap(){
		CouchDbClient dbClient = new CouchDbClient("maping",false,"http","m.teletaaleem.com",5984,"admin","t2demo");
		logger.log(Level.WARNING, "Getting Sheet Sequences");
		seqdoc = dbClient.find(Seq.class, serial);
		setKey(seqdoc.getqid());
		return seqdoc;
	}
	public void push(){
		dbtang = new CouchDbClient("couchdb.properties");
		dbtang.save(rslt);
		dbtang.shutdown();
	}
}
/*
 * 
 * 	// dbClient.save(foo);   // or save and ignore response 
		// dbClient.batch(foo);  // saves as batch
		//Response resp2 = dbClient.update(object);

		
		/*Map<String, Object> map = new HashMap<String, Object>();
		map.put("_id", "some-id");
		map.put("a-list", Collections.EMPTY_LIST);
		dbClient.save(map);

		JsonObject json = new JsonObject();
		json.addProperty("_id", "some-id-2");
		json.add("an-array", new JsonArray());
		dbClient.save(json); 
*/		
