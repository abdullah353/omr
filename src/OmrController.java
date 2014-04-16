import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

class OmrController {
	OmrModel sheet;
	OmrView view;

	public OmrController(OmrModel sheet,OmrView view) {
		this.sheet=sheet;
		this.view=view;
	}
	public boolean startApp(){
		this.view.subView(view.browsePanel());
		this.view.browse.addActionListener(new OpenL());
		this.view.scan.addActionListener(new Scan());
		return false;
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
				view.filename.setText(sheet.filename);
				view.dir.setText(sheet.path);
				view.subView(view.scanPanel());
			}
			if (rVal == JFileChooser.CANCEL_OPTION) {
				view.filename.setText("Operation Canceled");
				view.subView(view.scanPanel(), false);
			}
		}
	}
	
	class Scan implements ActionListener{
		public void actionPerformed(ActionEvent e){
			System.out.println("Start Scanning");
			sheet.init();
			if(sheet.searchUnit()){
				System.out.println("Unit Found TwoUnit="+sheet.twounit+" unit="+sheet.unit);
				if(sheet.checkAnchors())
					System.out.println("Anchors Checked");
				else
					System.out.println("Can't Validate Achors");
			}else{
				sheet.resetModel();
				System.out.println("Can't Found Unit");
			}
		}
	}
}
