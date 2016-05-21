package Client;
import javax.swing.JFrame;
public class Client extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static int windowWidth = 1200, windowHeight = 670;
	
	public static void main(String [] args){
		
		CharacterDisplay applet = new CharacterDisplay();
		applet.init();
		applet.start();
		applet.setFocusable(true);
		
		JFrame window = new JFrame("FAT GUYYYYYYYY");
		window.setContentPane(applet);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(windowWidth, windowHeight);
		window.setVisible(true);
		
		
	}
}
