import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.View;


public class OmrView extends JFrame{
	OmrModel sheet;
	protected JTextField filename = new JTextField(30),
						 dir = new JTextField(80);
	protected boolean hasfile = false;
	protected JButton button = new JButton("Browse");


	public OmrView(OmrModel sheet)
	{
	  this.sheet=sheet;
	}
	
	public boolean showExplorer(){
		JPanel dc = new DrawComponent();
	    setSize(420, 150);
	    setContentPane(dc);
		setVisible(true);
		return false;
	}
	
	/*
	 *  Drawing Components on Frame
	 */
	class DrawComponent extends JPanel{
		public DrawComponent(){
			this.add(button);
			this.add(filename);
			
			filename.setEditable(false);
		    filename.setText("Nothing Selected! Click Browse and Choose Image");
		    
		    //button.addActionListener(new OpenL(this));
		}
	}//Draw Components
	
	
}