package Client;

import javax.swing.JFrame;

public class ClientMain extends JFrame{

	private static final long serialVersionUID = 1L;
	
	public static Client applet;
	public static JFrame window;
	public static int port = 6667;
	// Screen ----> 14 : 10
	public final static int windowWidth = 1000, windowHeight = windowWidth/14*10; 
	
	public static void main(String [] args)
	{
		play();
	}
	public static void play()
	{
		if(window != null)	window.dispose();
		if(applet != null)	applet.destroy();
		applet = new Client(port, windowWidth, windowHeight);
		applet.init();
		applet.start();
		applet.setFocusable(true);
		
		window = new JFrame("FAT GUYYYYYYYY");
		window.setContentPane(applet);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(windowWidth, windowHeight);
		window.setVisible(true);
	}
}
