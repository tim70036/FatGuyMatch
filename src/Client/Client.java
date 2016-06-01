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
import SuperClass.LazerSkill;
import SuperClass.Missile;
import SuperClass.Skill;
import SuperClass.Tower;
import SuperClass.Type;
import SuperClass.Wall;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import de.looksgood.ani.Ani;
import processing.core.PApplet;
import processing.core.PImage;
public class Client extends PApplet{
	
	public static int width , height;
	
	private boolean isMainMenu;
	private boolean isInformation;
	private boolean isWaiting;
	private boolean isRunning;
	
	private boolean isOnStartBtn;
	private boolean isOnInfoBtn;
	private boolean isOnBackBtn;
	private int btnWidth = 250;
	private int btnHeight = 80; 
	
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
	
	// SKill
	private int fireSkillNum;
	private int lazerSkillNum;
	private int missileNum;
	
	// music
	private Minim minim;
	private AudioPlayer bgm;
	private AudioPlayer fire;
	
	public Client(String IP, int port, int width, int height)
	{
		this.IP = IP;
		this.port = port;
		Client.width = width;
		Client.height = height;
		
		this.isMainMenu = true;
		this.isInformation = false;
		this.isWaiting = false;
		this.isRunning = false;
		
		this.isOnStartBtn = false;
		this.isOnInfoBtn = false;
		this.isOnBackBtn = false;
		
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
		
		minim = new Minim(this);
		bgm = minim.loadFile("battle.mp3");
		bgm.loop();
		bgm.play();
	}
	public void loaddata(){
		
		
	}

	
public void draw() {
		
		this.translate(cam.getX(), cam.getY());
		//this.scale((float)2.0);
		
		if (this.isMainMenu) {
			this.background(255);
			
			// StartBtn
			if (this.isOnStartBtn) {
				this.fill(255, 255, 0);
			} else {
				this.fill(255, 0, 0);
			}
			this.rect(365, 400, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("Let's Start Game!", 370, 450);
			
			
			// InfoBtn
			if (this.isOnInfoBtn) {
				this.fill(255, 255, 0);
			} else {
				this.fill(255, 0, 0);
			}
			this.rect(365, 500, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("Information", 405, 550);
		}
		else if (this.isInformation) {
			this.background(255);
			
			// BackBtn
			if (this.isOnBackBtn) {
				this.fill(255, 255, 0);
			} else {
				this.fill(255, 0, 0);
			}
			this.rect(365, 500, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("BackToMainMenu", 365, 550);
		}
		else if(isWaiting)
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
							String life = reader.readLine();
							ch.setX(Float.parseFloat(x));
							ch.setY(Float.parseFloat(y));
							ch.setFrame(Integer.parseInt(frame));
							ch.life = Integer.parseInt(life);
						}
						
						// Skill Data
						for(Skill s : handler.getSkill())
						{
							String x = reader.readLine();
							String y = reader.readLine();
							String used = reader.readLine();
							s.setX(Float.parseFloat(x));
							s.setY(Float.parseFloat(y));
							s.used = (used.equals("True")) ? true : false;
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
								handler.addEntity(new Character(100,100,100,100,Type.CHARACTER,true,handler, i));
						}
						else if(command.equals("FireSkill"))
						{
							command = reader.readLine();
							
							fireSkillNum = Integer.parseInt(command);
							for(int i=0 ; i < fireSkillNum ; i++)
								handler.addSkill(new FireSkill(3000,3000,50,50,Type.FIRESKILL,true,handler));
						}
						else if(command.equals("Tower"))
						{
							handler.addEntity(new Tower(250 ,250,303,303, Type.TOWER, true, handler));
						}
						else if(command.equals("LazerSkill"))
						{
							command = reader.readLine();
							
							lazerSkillNum = Integer.parseInt(command);
							for(int i=0 ; i<lazerSkillNum ; i++)
								handler.addSkill(new LazerSkill(3000,3000,50,50,Type.LAZERSKILL, true, handler));
						}
						else if(command.equals("MissileSkill"))
						{
							command = reader.readLine();
							
							missileNum = Integer.parseInt(command);
							for(int i=0 ; i<missileNum; i++)
								handler.addSkill(new Missile(3000,3000,50,50,Type.MISSILE, true, handler));
							
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
    	if(key == 'w' || key == 'a' || key == 's' || key == 'd' || key == 'c' || key == 'f')
    	{
    		sendMessage("PlayerInput");
    		sendMessage("Press");
    	}
    	if(key == 'w')	sendMessage("W");
    	else if(key == 'a')	sendMessage("A");
    	else if(key == 's')	sendMessage("S");
    	else if(key == 'd')	sendMessage("D");
    	else if(key == 'c') {
    		sendMessage("C");
    		fire = minim.loadFile("bomb.mp3");
    		fire.play();
    	}
    	else if(key == 'f') sendMessage("F");
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
    
    // ------------------------- Mouse Input Part ------------------------------- //
    // -------------------------------------------------------------------------- //
    public void  mouseClicked() {
  	  if (this.isMainMenu) {
  		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth
  				  && this.mouseY >= 400 && this.mouseY < 400+this.btnHeight) {
  			  this.isMainMenu = false;
  			  this.isWaiting = true;
  			  
  			  // Connect to Server
  			  this.connect();
  		  } 
  		  else if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
  				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
  			  this.isMainMenu = false;
  			  this.isInformation = true;
  		  }
  	  } 
  	  else if (this.isInformation) {
  		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
  				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
  			  this.isMainMenu = true;
  			  this.isInformation = false;
  		  } 
  	  }
    }
    
    public void mouseMoved() {
  	  if (this.isMainMenu) {
  		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth
  				  && this.mouseY >= 400 && this.mouseY < 400+this.btnHeight) {
  			  this.isOnStartBtn = true;
  		  }
  		  else if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
  				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
  			  this.isOnInfoBtn = true;
  		  }
  		  else {
  			  this.isOnStartBtn = false;
  			  this.isOnInfoBtn = false;
  			  this.isOnBackBtn = false;
  		  }
  	  } else if (this.isInformation) {
  		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
  				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
  			  this.isOnBackBtn = true;
  		  } 
  		  else {
  			  this.isOnStartBtn = false;
  			  this.isOnInfoBtn = false;
  			  this.isOnBackBtn = false;
  		  }
  	  }
    }
}


//------------------------------------------

