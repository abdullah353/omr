import java.io.IOException;


public class Omr {
	
	public static void main(String[] args) throws IOException {
		// TODO code application logic here
		OmrModel sheet=new OmrModel();
		OmrView view=new OmrView(sheet);
		OmrController control=new OmrController(sheet, view);
		control.startApp();
	}
}
