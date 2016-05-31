package Client;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import SuperClass.Character;
import SuperClass.Entity;
import SuperClass.FireSkill;
import SuperClass.Handler;
import SuperClass.Skill;
import SuperClass.Type;
import SuperClass.Wall;
import de.looksgood.ani.Ani;
import processing.core.PApplet;
import processing.core.PImage;
public class Client extends PApplet{
	
	public static int width , height;
	
	private boolean isRunning;
	private boolean isWaiting;
	private int playerNum;
	private int wallNum;
	private Handler handler;
	
	// Attributes for Network
	public int ID;
	private String IP;
	private int port;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private ClientThread clientThread;
	
	/// cam
	public static Camera cam;
	
	// Level
	public BufferedImage levelImage;
	
	/// pic data
	public static PictureSheet sheet;
	public static Picture player[];
	
	public Client(String IP, int port, int width, int height)
	{
		this.IP = IP;
		this.port = port;
		Client.width = width;
		Client.height = height;
		
		this.isWaiting = true;
		this.isRunning = false;
		
		playerNum = 0;
		handler = new Handler();

		cam = new Camera();
	}
	
	public void setup() {
		
		// init picture
		//player = new Picture(loadImage("match.png"));
		//player.reSize(100, 100);// Change be careful server init
		
		//	Sprite
		sheet = new PictureSheet(loadImage("match.png"));
		
		// Player
		player = new Picture[10];
		for(int tmp=0;tmp<5;tmp++){
			player[tmp] = new Picture(sheet,tmp,0);
			player[tmp].reSize(100, 100);
			player[tmp+5] = new Picture(sheet,tmp,1);
			player[tmp+5].reSize(100, 100);
		}
		
		// Level,map floor  picture should be 16*16 32*32....
		try 
		{
			levelImage = ImageIO.read(new File("level.png"));
			handler.createLevel(levelImage);
		} catch (IOException e) {}
		
		
		size(width, height);
		smooth();
		Ani.init(this);
		// Fps 120 is good , 60 is too low
		this.frameRate(60);
		
		// Connect to Server
		this.connect();
	}
	public void loaddata(){
		
		
	}

	
	public void draw() {
		
		
		this.translate(cam.getX(), cam.getY());
		//this.scale((float)2.0);
		
		if(isWaiting)
		{
			this.background(0);
		}
		else if(isRunning)
		{
			this.background(255);
			handler.display(this);
			
			int tag=0;
			for(Entity en:handler.getEntity()){
				if(en.getType() == Type.CHARACTER){
					
					cam.tick(en);
					if(tag==ID)break;tag++;
				}
			}
		}
	}
	
// ------------------------NetWork Part ----------------------------------- //
// ----------------------------------------------------------------------- //
	public void sendMessage(String message)
	{
		writer.println(message);
		writer.flush();
		//System.out.println("Send message: " + message);
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
						
						// Client ID
						command  = reader.readLine();
						ID = Integer.parseInt(command);
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
							String frame = reader.readLine();
							ch.setX(Float.parseFloat(x));
							ch.setY(Float.parseFloat(y));
							ch.setFrame(Integer.parseInt(frame));
						}
						
						// Skill Data
						for(Skill s : handler.getSkill())
						{
							String x = reader.readLine();
							String y = reader.readLine();
							s.setX(Float.parseFloat(x));
							s.setY(Float.parseFloat(y));
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
						else if(command.equals("FireSkill")){
							command = reader.readLine();
							for(int i=0 ; i < 20 ; i++)
								handler.addSkill(new FireSkill(0,0,50,50,Type.FIRESKILL,true,handler));
						}
						
						else if(command.equals("Wall"))
						{
							command = reader.readLine();
							
							wallNum = Integer.parseInt(command);
							for(int i=0 ; i < wallNum ; i++)
								handler.addTile(new Wall(500,300,500,50,Type.WALL,true,handler));
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
    	if(key == 'w' || key == 'a' || key == 's' || key == 'd' || key == 'c')
    	{
    		sendMessage("PlayerInput");
    		sendMessage("Press");
    	}
    	if(key == 'w')	sendMessage("W");
    	else if(key == 'a')	sendMessage("A");
    	else if(key == 's')	sendMessage("S");
    	else if(key == 'd')	sendMessage("D");
    	else if(key == 'c') sendMessage("C");
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


//------------------------------------------

