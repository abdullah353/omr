import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

class OmrController {
	OmrModel sheet;
	OmrView view;

	public OmrController(OmrModel sheet,OmrView view) {
		this.sheet=sheet;
		this.view=view;
		this.view.showExplorer();
		this.view.button.addActionListener(new OpenL());
	}
	
	
	/*
	 * Listening Events on Browse Click
	 */
	class OpenL implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JFileChooser c = new JFileChooser();
			int rVal = c.showOpenDialog(new JPanel());
			if (rVal == JFileChooser.APPROVE_OPTION) {
				view.filename.setText(c.getSelectedFile().getName());
				view.dir.setText(c.getCurrentDirectory().toString());
				view.hasfile = true;
			}
			if (rVal == JFileChooser.CANCEL_OPTION) {
				view.filename.setText("Operation Canceled");
				view.dir.setText("Operation Canceled");
				view.hasfile = false;
			}
		}
	}
}
