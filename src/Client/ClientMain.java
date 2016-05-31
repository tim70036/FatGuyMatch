package Client;

import javax.swing.JFrame;

public class ClientMain extends JFrame{

	private static final long serialVersionUID = 1L;
	// Screen ----> 14 : 10
	public final static int windowWidth = 1000, windowHeight = windowWidth/14*10; 
	
	public static void main(String [] args){
		
		Client applet = new Client( "140.114.86.236", 6667, windowWidth, windowHeight);
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
