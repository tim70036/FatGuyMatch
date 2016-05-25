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

import Server.Character;
import Server.Type;
import SuperClass.Handler;
import de.looksgood.ani.Ani;
import processing.core.PApplet;
public class Client extends PApplet{
	
	private boolean isRunning;
	private boolean isWaiting;
	private int playerNum;
	private Handler characters;
	private final static int width = 1200, height = 650;
	
	// Attributes for Network
	private String IP;
	private int port;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private ClientThread clientThread;
	
	public Client(String IP, int port)
	{
		this.IP = IP;
		this.port = port;
		this.isWaiting = true;
		this.isRunning = false;
		
		playerNum = 0;
		characters = new Handler();
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
			characters.display(this);
		}
	}
	
// ------------------------NetWork Part ----------------------------------- //
// ----------------------------------------------------------------------- //
	public void sendMessage(String message)
	{
		writer.println(message);
		writer.flush();
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
	class ClientThread extends Thread //持續接收來自server的訊息,根據訊息更新gui
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
					else if(command.equals("MoveCharacter"))
					{
						
					}
					else if(command.equals("Init"))
					{
						command = reader.readLine();

						if(command.equals("Characters"))
						{
							command = reader.readLine();
							playerNum = Integer.parseInt(command);
							for(int i=0 ; i < playerNum ; i++)
								characters.addEntity(new Character(100,100,100,100,Type.CHARACTER,true,characters));
						}
					}
					
					
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
}
