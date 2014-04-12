import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class OmrView extends JFrame{
	OmrModel sheet;
	JPanel main;
	protected JTextField filename = new JTextField(30),
						 dir = new JTextField(80);
	protected boolean hasfile = false;
	protected JButton browse = new JButton("Browse"),
						scan = new JButton("Scan");


	public OmrView(OmrModel sheet){
		this.sheet=sheet;
		centreWindow(this);
		setSize(600, 150);
		main = new DrawComponent();
	}

	/*
	 * Center Windows Frame
	 */
	private static void centreWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x=(int)((dimension.getWidth() - 600)/2);
		int y=(int)((dimension.getHeight() - 150)/2);
		frame.setLocation(x, y);
	}
	
	


	protected void cleanView(){
		main.removeAll();
	}
	protected void subView(JPanel panel,boolean state){
		if(state) main.add(panel);
		else main.remove(panel);
		setContentPane(main);
		if(main.isVisible())setVisible(true);
	}
	protected JPanel browsePanel(){
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20,0, 0, 0));
		panel.setLayout(new FlowLayout());
		panel.add(browse);
		filename.setEditable(false);
		filename.setText("Nothing Selected! Click Browse and Choose Image");
		panel.add(filename);
		return panel;
	}
	public JPanel scanPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(scan);
		return panel;
	}
	
	/*
	 *  Drawing Components on Frame
	 */
	class DrawComponent extends JPanel{
		public DrawComponent(){
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}
		
	}
	protected void toggleSubView(JPanel panel,boolean state){
		subView(panel,state);
	}
}