
class OmrController {
	OmrModel sheet;
	OmrView view;

	public OmrController(OmrModel sheet,OmrView view) {
		this.sheet=sheet;
		this.view=view;
		view.showExplorer();
	}
}
