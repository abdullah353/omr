
public class OmrView {
	OmrModel sheet;
	public OmrView(OmrModel sheet)
	{
	  this.sheet=sheet;
	}
	
	public boolean showExplorer(){
		System.out.println("Showing Explorer");
		return false;
	}

}