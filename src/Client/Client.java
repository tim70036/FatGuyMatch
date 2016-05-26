package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import SuperClass.Character;
import SuperClass.Entity;
import SuperClass.Handler;
import SuperClass.Type;
import SuperClass.Wall;
import de.looksgood.ani.Ani;
import processing.core.PApplet;
public class Client extends PApplet{
	
	public static int width , height;
	
	private boolean isRunning;
	private boolean isWaiting;
	private int playerNum;
	private int wallNum;
	private Handler handler;
	
	// Attributes for Network
	private String IP;
	private int port;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private ClientThread clientThread;
	
	public Client(String IP, int port, int width, int height)
	{
		this.IP = IP;
		this.port = port;
		Client.width = width;
		Client.height = height;
		System.out.println(Client.width);
		System.out.println(Client.height);
		
		this.isWaiting = true;
		this.isRunning = false;
		
		playerNum = 0;
		handler = new Handler();
	}
	
	public void setup() {

		size(width, height);
		smooth();
		Ani.init(this);
		
		// Connect to Server
		this.connect();
	}
	public void loaddata(){
		
		
	}

	
	public void draw() {
		if(isWaiting)
		{
			this.background(0);
		}
		else if(isRunning)
		{
			this.background(255);
			handler.display(this);
		}
	}
	
// ------------------------NetWork Part ----------------------------------- //
// ----------------------------------------------------------------------- //
	public void sendMessage(String message)
	{
		writer.println(message);
		writer.flush();
		System.out.println("Send message: " + message);
	}
	
	public void connect()
	{
		try {
			socket = new Socket(IP , port);
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			clientThread = new ClientThread();
			clientThread.start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
// ----------------------------------------------------------------------- //
// ----------------------------------------------------------------------- //
	class ClientThread extends Thread //Receiving Message from Server
	{
		public void run()
		{
			while(true)
			{
				try 
				{
					String command = reader.readLine();
					
					if(command.equals("StartGame"))
					{
						isRunning = true;
						isWaiting = false;
					}
					else if(command.equals("GameData"))
					{
						
						// Character's Data
						for(Entity ch : handler.getEntity())
						{
							String x = reader.readLine();
							String y = reader.readLine();
							ch.setX(Float.parseFloat(x));
							ch.setY(Float.parseFloat(y));
						}
					}
					else if(command.equals("Init"))
					{
						command = reader.readLine();

						if(command.equals("Characters"))
						{
							command = reader.readLine();
							
							playerNum = Integer.parseInt(command);
							for(int i=0 ; i < playerNum ; i++)
								handler.addEntity(new Character(100,100,100,100,Type.CHARACTER,true,handler));
						}
						else if(command.equals("Wall"))
						{
							command = reader.readLine();
							
							wallNum = Integer.parseInt(command);
							for(int i=0 ; i < wallNum ; i++)
								handler.addTile(new Wall(500,300,100,100,Type.WALL,true,handler));
						}
					}
					
					
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
// ----------------------Key Input Part ----------------------------------- //
// ----------------------------------------------------------------------- //
    public void keyPressed() 
	{
    	if(key == 'w' || key == 'a' || key == 's' || key == 'd')
    	{
    		sendMessage("PlayerInput");
    		sendMessage("Press");
    	}
    	if(key == 'w')	sendMessage("W");
    	else if(key == 'a')	sendMessage("A");
    	else if(key == 's')	sendMessage("S");
    	else if(key == 'd')	sendMessage("D");
	}
    
    public void keyReleased()
    {
    	if(key == 'w' || key == 'a' || key == 's' || key == 'd')
    	{
    		sendMessage("PlayerInput");
    		sendMessage("Release");
    	}
    	
    	if(key == 'w')	sendMessage("W");
    	else if(key == 'a')	sendMessage("A");
    	else if(key == 's')	sendMessage("S");
    	else if(key == 'd')	sendMessage("D");
    }
}
