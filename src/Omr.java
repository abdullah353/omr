
public class Omr {
	
	public static void main(String[] args) {
		// TODO code application logic here
		
		OmrModel sheet=new OmrModel();
		OmrView view=new OmrView(sheet);
		OmrController control=new OmrController(sheet, view);
		control.startApp();
	}
}
