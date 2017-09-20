package Server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import SuperClass.Boss;
import SuperClass.Tower;

public class ServerMain extends JFrame{
	
	public static Server server;
	public static int port = 6667;
	public static int playerNum;
	
	public static ServerMain serverMain;
	public static JTextArea textArea;
	public static JTextField textField;
	
	public ServerMain()
	{
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Initialize textArea
		ServerMain.textArea = new JTextArea();
		ServerMain.textArea.setEditable(false);
		ServerMain.textArea.setPreferredSize(new Dimension(500,550));
		JScrollPane scrollPane = new JScrollPane(ServerMain.textArea);
	    this.add(scrollPane);
	    
	    // Initialize textField
	    ServerMain.textField = new JTextField();
	    ServerMain.textField.setPreferredSize(new Dimension(500,40));
	    ServerMain.textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				Scanner scan = new Scanner(ServerMain.textField.getText());
				ServerMain.textField.setText("");
				
				if(scan.hasNextInt() == true)
				{
					ServerMain.playerNum = scan.nextInt();
					ServerMain.addLine("Opening Server for " + playerNum + " players....");
					scan.close();
					
					ServerMain.textField.setEditable(false);
					
					// Game part
				    if(server != null)
					{
						server = null;
						Thread s = new Thread (new Runnable(){
							
							public void run() 
							{
								try 
								{
									Thread.sleep((long) (3000));
								} catch (InterruptedException e) {e.printStackTrace();}
							}
						});
						s.start();
						try 
						{
							s.join();
						} catch (InterruptedException a) {a.printStackTrace();}
					}
				    ServerMain.serverMain.setFocusable(true);
					server = new Server(port, playerNum);
				}
				else { ServerMain.addLine("Please enter player number.");}
			}
	    	
	    });
	    this.add(ServerMain.textField);
	    this.pack();
	    this.setVisible(true);
	    
	    // read playerNum
	    InetAddress IP = null;
		try {IP = InetAddress.getLocalHost();} catch (UnknownHostException e1) {e1.printStackTrace();}
	    ServerMain.addLine("IP of Server is := "+IP.getHostAddress());
		ServerMain.addLine("Please enter player number.");
	}
	
	public static void addLine(final String message) 
	{
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        ServerMain.textArea.append(message + "\n");
		    }
		});
	}
	
	public static void main(String[] args) 
	{
		replay();
	}
	
	public static void replay()
	{
		if(serverMain != null ) serverMain.dispose();
		serverMain = new ServerMain();
	}
	
}
