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
import java.util.Timer;
import java.util.TimerTask;

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
	
	// Status
	private boolean[] isInformation = new boolean[3];
	private boolean isMainMenu;
	private boolean isWaiting;
	private boolean isRunning;
	
	// Mouse Detect
	private boolean isOnNextBtn;
	private boolean isOnPrevBtn;
	private boolean isOnStartBtn;
	private boolean isOnInfoBtn;
	private boolean isOnBackBtn;
	private boolean isOnMuteBtn;
	
	// parameters
	private int muteWidth = 90;
	private int muteHeight = 40;
	private int btnWidth = 250;
	private int btnHeight = 80; 
	private int btnRadius = 40;
	
	// Menu Picture
	private PImage titleImg;
	private PImage[] menu_bg = new PImage[3];
	private PImage[] author = new PImage[5];
	private int titleX = 210, titleY = -200;
	private int menu_bgX = -200, menu_bgY = 300;
	private int delay = 0, index = 0;
	
	
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
	private boolean isPlay;
	
	public Client(String IP, int port, int width, int height)
	{
		this.IP = IP;
		this.port = port;
		Client.width = width;
		Client.height = height;
		
		for (int i = 0; i < 3; i++) 
			this.isInformation[i] = false;
		this.isMainMenu = true;
		this.isWaiting = false;
		this.isRunning = false;
		
		this.isOnNextBtn = false;
		this.isOnPrevBtn = false;
		this.isOnStartBtn = false;
		this.isOnInfoBtn = false;
		this.isOnBackBtn = false;
		this.isOnMuteBtn = false;
		this.isPlay = true;
		
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
		
		
		size(width, height);
		smooth();
		Ani.init(this);
		// Fps 120 is good , 60 is too low
		this.frameRate(60);
		
		// BGM
		minim = new Minim(this);
		bgm = minim.loadFile("battle.mp3");
		bgm.setGain((float)-20.0);
		bgm.loop();
		bgm.play();
		
		// Menu picture & animation
		this.titleImg = this.loadImage("title.png");
		for (int i = 0; i < 3; i++) 
			this.menu_bg[i] = this.loadImage("menu_bg"+i+".png");
		for (int i = 0; i < 5; i++)
			this.author[i] = this.loadImage("author"+i+".png");
		Ani.to(this, (float)2, "titleY", 100);
		Ani.to(this, (float)2, "menu_bgX", 50);
		
	}
	public void loaddata(){
		
		
	}

	
public void draw() {
		
		this.translate(cam.getX(), cam.getY());
		//this.scale((float)2.0);
		
		
		if (this.isMainMenu) {
			this.background(255);
			
			// MainMenu Picture
			this.image(this.titleImg, this.titleX, this.titleY);
	
			if (delay < 15) {
				this.image(this.menu_bg[index], this.menu_bgX, this.menu_bgY, 250, 300);
				delay++;
				if (delay == 15) {
					delay = 0;
					index = (index+1) % 3;
				}
			}
			
			// StartBtn
			if (this.isOnStartBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.rect(365, 400, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("Let's Start Game!", 370, 450);
			
			
			// InfoBtn
			if (this.isOnInfoBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.rect(365, 500, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("Information", 405, 550);
			
			// MuteBtn
			if(this.isOnMuteBtn) {
				this.fill(0, 255, 0, 70);
			} else {
				this.fill(30, 144, 255, 70);
			}
			this.rect(830, 590, this.muteWidth, this.muteHeight);
			if (!this.isPlay) {
				this.line(830, 590, 830+this.muteWidth, 590+this.muteHeight);
			} 
			this.fill(0);
			this.textSize(30);
			this.text("mute", 840, 621);
		}
		else if (this.isInformation[0]) {
			this.background(255);
			
			// BackBtn
			if (this.isOnBackBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.rect(365, 500, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("BackToMainMenu", 365, 550);
			
			// NextBtn
			if (this.isOnNextBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.ellipse(900, 300, 80, 80);
			this.fill(0);
			this.triangle(900, 280, 928, 300, 900, 320);
			this.rect(880, 290, 30, 20);
			
			// MuteBtn
			if(this.isOnMuteBtn) {
				this.fill(0, 255, 0, 70);
			} else {
				this.fill(30, 144, 255, 70);
			}
			this.rect(830, 590, this.muteWidth, this.muteHeight);
			if (!this.isPlay) {
				this.line(830, 590, 830+this.muteWidth, 590+this.muteHeight);
			} 
			this.fill(0);
			this.textSize(30);
			this.text("mute", 840, 621);
		}
		else if (this.isInformation[1]) {
			this.background(255);
			
			// Author's picture
			this.image(this.author[0], 150, 100, 250, 300);
			this.image(this.author[1], 600, 100, 250, 300);
			
			// BackBtn
			if (this.isOnBackBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.rect(365, 500, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("BackToMainMenu", 365, 550);
			
			// NextBtn
			if (this.isOnNextBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.ellipse(900, 300, 80, 80);
			this.fill(0);
			this.triangle(900, 280, 928, 300, 900, 320);
			this.rect(880, 290, 30, 20);
			
			// PreBtn
			if (this.isOnPrevBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.ellipse(80, 300, 80, 80);
			this.fill(0);
			this.triangle(80, 280, 52, 300, 80, 320);
			this.rect(70, 290, 33, 20);
			
			// MuteBtn
			if(this.isOnMuteBtn) {
				this.fill(0, 255, 0, 70);
			} else {
				this.fill(30, 144, 255, 70);
			}
			this.rect(830, 590, this.muteWidth, this.muteHeight);
			if (!this.isPlay) {
				this.line(830, 590, 830+this.muteWidth, 590+this.muteHeight);
			} 
			this.fill(0);
			this.textSize(30);
			this.text("mute", 840, 621);
		}
		else if (this.isInformation[2]) {
			this.background(255);
			
			// Author's picture
			this.image(this.author[2], 130, 100, 250, 300);
			this.image(this.author[3], 395, 180, 250, 300);
			this.image(this.author[4], 660, 100, 250, 300);
			
			// BackBtn
			if (this.isOnBackBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.rect(365, 500, this.btnWidth, this.btnHeight);
			this.fill(0);
			this.textSize(30);
			this.text("BackToMainMenu", 365, 550);
			
			// PreBtn
			if (this.isOnPrevBtn) {
				this.fill(0, 255, 0);
			} else {
				this.fill(30, 144, 255);
			}
			this.ellipse(80, 300, 80, 80);
			this.fill(0);
			this.triangle(80, 280, 52, 300, 80, 320);
			this.rect(70, 290, 33, 20);
			
			// MuteBtn
			if(this.isOnMuteBtn) {
				this.fill(0, 255, 0, 70);
			} else {
				this.fill(30, 144, 255, 70);
			}
			this.rect(830, 590, this.muteWidth, this.muteHeight);
			if (!this.isPlay) {
				this.line(830, 590, 830+this.muteWidth, 590+this.muteHeight);
			} 
			this.fill(0);
			this.textSize(30);
			this.text("mute", 840, 621);
		}
		else if(isWaiting)
		{
			this.background(255);
			this.fill(0);
			this.textSize(30);
			this.text("Waiting Other Players...", 300, 300);
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
						/*else if(command.equals("Tower"))
						{
							handler.addEntity(new Tower(250 ,250,303,303, Type.TOWER, true, handler));
						}*/
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
						else if(command.equals("Map"))
						{
							// Level,map floor  picture should be 16*16 32*32....
							try 
							{
								levelImage = ImageIO.read(new File("level.png"));
								handler.createLevel(levelImage);
							} catch (IOException e) {}
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
  		  // StartBtn
  		  if (this.isOnStartBtn) {
  			  this.isMainMenu = false;
  			  this.isWaiting = true;
  			  
  			  // Connect to Server
  			  this.connect();
  		  } 
  		  // InfoBtn
  		  else if (this.isOnInfoBtn) {
  			  this.isMainMenu = false;
  			  this.isInformation[0] = true;
  		  }
  		  // MuteBtn
  		  else if(this.isOnMuteBtn){
  			  if(this.isPlay){
  				  bgm.pause();
  				  this.isPlay = false;
  			  }
  			  else{
  				  bgm.play();
  				  this.isPlay = true;
  			  }
  		  }
  	  } 
  	  else if (this.isInformation[0]) {
  		  // BackBtn
  		  if (this.isOnBackBtn) {
  			  this.isMainMenu = true;
  			  this.isInformation[0] = false;
  		  } 
  		  
  		  // NextBtn
  		  else if (this.isOnNextBtn) {
  			  this.isInformation[0] = false;
  			  this.isInformation[1] = true;
  		  }
  		  
  		  // MuteBtn
  		  else if(this.isOnMuteBtn){
			  if(this.isPlay){
				  bgm.pause();
				  this.isPlay = false;
			  }
			  else{
				  bgm.play();
				  this.isPlay = true;
			  }
		  }
  	  }
  	  else if (this.isInformation[1]) {
		  // BackBtn
		  if (this.isOnBackBtn) {
			  this.isMainMenu = true;
			  this.isInformation[1] = false;
		  } 
		  
		  // NextBtn
		  else if (this.isOnNextBtn) {
			  this.isInformation[1] = false;
			  this.isInformation[2] = true;
		  }
		  
		  // PrevBtn
		  else if (this.isOnPrevBtn) {
			  this.isInformation[1] = false;
			  this.isInformation[0] = true;
		  }
		  
		  // MuteBtn
		  else if(this.isOnMuteBtn){
			  if(this.isPlay){
				  bgm.pause();
				  this.isPlay = false;
			  }
			  else{
				  bgm.play();
				  this.isPlay = true;
			  }
		  }
  	  }
  	  else if (this.isInformation[2]) {
		  // BackBtn
		  if (this.isOnBackBtn) {
			  this.isMainMenu = true;
			  this.isInformation[2] = false;
		  } 
		  
		  // PrevBtn
		  else if (this.isOnPrevBtn) {
			  this.isInformation[2] = false;
			  this.isInformation[1] = true;
		  }
		  
		  // MuteBtn
		  else if(this.isOnMuteBtn){
			  if(this.isPlay){
				  bgm.pause();
				  this.isPlay = false;
			  }
			  else{
				  bgm.play();
				  this.isPlay = true;
			  }
		  }
	  }
    }
    
    public void mouseMoved() {
  	  if (this.isMainMenu) {
  		  // StartBtn
  		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth
  				  && this.mouseY >= 400 && this.mouseY < 400+this.btnHeight) {
  			  this.isOnStartBtn = true;
  		  }
  		  // InfoBtn
  		  else if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
  				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
  			  this.isOnInfoBtn = true;
  		  }
  		  // MuteBtn
  		  else if(this.mouseX >= 830 && this.mouseX < 830+this.muteWidth
  				   && this.mouseY >= 590 && this.mouseY < 590+this.muteHeight){
  			  this.isOnMuteBtn = true;
  		  }
  		  else {
  			  this.isOnStartBtn = false;
  			  this.isOnInfoBtn = false;
  			  this.isOnBackBtn = false;
  			  this.isOnNextBtn = false;
  			  this.isOnPrevBtn = false;
  			  this.isOnMuteBtn = false;
  		  }
  	  } else if (this.isInformation[0]) {
  		  // BackBtn
  		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
  				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
  			  this.isOnBackBtn = true;
  		  }
  		  // NextBtn
  		  else if (this.mouseX >= 900-this.btnRadius && this.mouseX < 900+this.btnRadius
  				  && this.mouseY >= 300-this.btnRadius && this.mouseY < 300+this.btnRadius) {
  			  this.isOnNextBtn = true;
  		  }
  		  
  		  // MuteBtn
  		  else if(this.mouseX >= 830 && this.mouseX < 830+this.muteWidth
				   && this.mouseY >= 590 && this.mouseY < 590+this.muteHeight){
			  this.isOnMuteBtn = true;
		  }
  		  else {
  			  this.isOnStartBtn = false;
  			  this.isOnInfoBtn = false;
  			  this.isOnBackBtn = false;
  			  this.isOnNextBtn = false;
  			  this.isOnPrevBtn = false;
  			  this.isOnMuteBtn = false;
  		  }
  	  }
  	  else if (this.isInformation[1]) {
		  // BackBtn
		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
			  this.isOnBackBtn = true;
		  }
		  // NextBtn
		  else if (this.mouseX >= 900-this.btnRadius && this.mouseX < 900+this.btnRadius
				  && this.mouseY >= 300-this.btnRadius && this.mouseY < 300+this.btnRadius) {
			  this.isOnNextBtn = true;
		  }
		  
		  // PrevBtn
		  else if (this.mouseX >= 80-this.btnRadius && this.mouseX < 80+this.btnRadius
				  && this.mouseY >= 300-this.btnRadius && this.mouseY < 300+this.btnRadius) {
			  this.isOnPrevBtn = true;
		  }
		  
		  // MuteBtn
		  else if(this.mouseX >= 830 && this.mouseX < 830+this.muteWidth
				   && this.mouseY >= 590 && this.mouseY < 590+this.muteHeight){
			  this.isOnMuteBtn = true;
		  }
		  else {
			  this.isOnStartBtn = false;
			  this.isOnInfoBtn = false;
			  this.isOnBackBtn = false;
			  this.isOnNextBtn = false;
			  this.isOnPrevBtn = false;
			  this.isOnMuteBtn = false;
		  }
	  }
  	  else if (this.isInformation[2]) {
		  // BackBtn
		  if (this.mouseX >= 365 && this.mouseX < 365+this.btnWidth 
				  && this.mouseY >= 500 && this.mouseY < 500+this.btnHeight) {
			  this.isOnBackBtn = true;
		  }
		  
		  // PrevBtn
		  else if (this.mouseX >= 80-this.btnRadius && this.mouseX < 80+this.btnRadius
				  && this.mouseY >= 300-this.btnRadius && this.mouseY < 300+this.btnRadius) {
			  this.isOnPrevBtn = true;
		  }
		  
		  // MuteBtn
		  else if(this.mouseX >= 830 && this.mouseX < 830+this.muteWidth
				   && this.mouseY >= 590 && this.mouseY < 590+this.muteHeight){
			  this.isOnMuteBtn = true;
		  }
		  else {
			  this.isOnStartBtn = false;
			  this.isOnInfoBtn = false;
			  this.isOnBackBtn = false;
			  this.isOnNextBtn = false;
			  this.isOnPrevBtn = false;
			  this.isOnMuteBtn = false;
		  }
	  }
    }
}


//------------------------------------------

