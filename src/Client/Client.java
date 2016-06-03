package Client;
import java.awt.Color;
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
import controlP5.ControlEvent;
import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.ControlWindow;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import de.looksgood.ani.Ani;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
public class Client extends PApplet{
	
	public static int width , height;
	
	private ControlP5 cp5;
	
	// Status
	private String menuStatus;
	private boolean isWaiting;
	private boolean isRunning;
	
	// parameters
	private int muteWidth = 90;
	private int muteHeight = 40;
	private int btnWidth = 250;
	private int btnHeight = 80; 
	private int titleX = 210, titleY = -200;
	private int menu_bgX = -200, menu_bgY = 300;
	private int delay , index;
	private int characterID;
	private int characterPicX;
	private int characterPicXL = -500, characterPicXR = 1500;
	
	// Menu Picture
	private PImage titleImg;
	private PImage menuBg;
	private PImage[] menuPic = new PImage[3];
	private PImage[] author = new PImage[5];
	private PImage[] authorName = new PImage[5];
	private PImage[][] character = new PImage[4][3];
	
	// Mouse Detector
	private boolean[] isOnAuthor = new boolean[5];
	
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
		
		this.menuStatus = "MainMenu";
		
		for (int i = 0; i < 5; i++)
			this.isOnAuthor[i] = false;
		
		this.isWaiting = false;
		this.isRunning = false;
		
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
		player = new Picture[15];
		for(int tmp=0;tmp<5;tmp++){
			player[tmp] = new Picture(sheet,tmp,0);
			player[tmp].reSize(100, 100);
			player[tmp+5] = new Picture(sheet,tmp,1);
			player[tmp+5].reSize(100, 100);
			player[tmp+10] = new Picture(sheet,tmp,2);
			player[tmp+10].reSize(100, 100);
		}
		
		
		size(width, height);
		smooth();
		this.initButton();
		Ani.init(this);
		// FPS 120 is good , 60 is too low
		this.frameRate(60);
		
		// BGM
		minim = new Minim(this);
		bgm = minim.loadFile("battle.mp3");
		bgm.setGain((float)-20.0);
		bgm.loop();
		bgm.play();
		
		// Menu picture & animation
		this.menuBg = this.loadImage("menuBg.png");
		this.titleImg = this.loadImage("title.png");
		for (int i = 0; i < 3; i++) 
			this.menuPic[i] = this.loadImage("menu_bg"+i+".png");
		for (int i = 0; i < 5; i++) {
			this.author[i] = this.loadImage("author"+i+".png");
			this.authorName[i] = this.loadImage("authorName"+i+".png");
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				this.character[i][j] = this.loadImage("character"+i+j+".png");
			}
		}
		Ani.to(this, (float)2, "titleY", 50);
		Ani.to(this, (float)2, "menu_bgX", 50);
		
	}
	
	public void loaddata(){}
	
	public void draw() {
		
		this.translate(cam.getX(), cam.getY());
		//this.scale((float)2.0);
		
		if (this.menuStatus.equals("MainMenu")) {
			this.background(255);
			// MainMenu Picture
			this.image(this.menuBg, 0, 0, this.width, this.height);
			this.image(this.titleImg, this.titleX, this.titleY);
			
			if (delay < 20) {
				this.image(this.menuPic[index], this.menu_bgX, this.menu_bgY, 250, 300);
				delay++;
				if (delay == 20) {
					delay = 0;
					index = (index+1) % 3;
				}
			}
		}
		else if (this.menuStatus.equals("Information1")) {
			this.background(255);
			this.image(this.menuBg, 0, 0, this.width, this.height);
		}
		else if (this.menuStatus.equals("Information2")) {
			this.background(255);
			this.image(this.menuBg, 0, 0, this.width, this.height);
			
			// Authors' name & picture
			this.image(this.author[0], 200, 100, 250, 300);
			this.image(this.author[1], 550, 100, 250, 300);
			if (this.isOnAuthor[0]) 
				this.image(this.authorName[0], this.mouseX, this.mouseY, 150, 100);
			else if (this.isOnAuthor[1]) 
				this.image(this.authorName[1], this.mouseX, this.mouseY, 150, 100);
		}
		else if (this.menuStatus.equals("Information3")) {
			this.background(255);
			this.image(this.menuBg, 0, 0, this.width, this.height);
			
			// Authors' name & picture
			this.image(this.author[2], 130, 100, 250, 300);
			this.image(this.author[3], 395, 100, 250, 300);
			this.image(this.author[4], 660, 100, 250, 300);
			if (this.isOnAuthor[2]) 
				this.image(this.authorName[2], this.mouseX, this.mouseY, 150, 100);
			else if (this.isOnAuthor[3]) 
				this.image(this.authorName[3], this.mouseX, this.mouseY, 150, 100);
			else if (this.isOnAuthor[4]) 
				this.image(this.authorName[4], this.mouseX, this.mouseY, 150, 100);
		
		}
		else if (this.menuStatus.equals("Selecting")) {
			this.background(0);
			switch (this.characterID) {
				case 0:	
					if (delay < 20) {
						this.image(this.character[this.characterID][index], this.characterPicX, this.height/2-330, 730, 550);
						delay++;
						if (delay == 20) {
							delay = 0;
							index = (index+1) % 3;
						}
					}
					break;
				case 1:
					if (delay < 20) {
						this.image(this.character[this.characterID][index], this.characterPicX, this.height/2-330, 730, 550);
						delay++;
						if (delay == 20) {
							delay = 0;
							index = (index+1) % 3;
						}
					}
					break;
				case 2: 
					if (delay < 20) {
						this.image(this.character[this.characterID][index], this.characterPicX, this.height/2-330, 730, 550);
						delay++;
						if (delay == 20) {
							delay = 0;
							index = (index+1) % 3;
						}
					}
					break;
				case 3:
					if (delay < 20) {
						this.image(this.character[this.characterID][index], this.characterPicX, this.height/2-330, 730, 550);
						delay++;
						if (delay == 20) {
							delay = 0;
							index = (index+1) % 3;
						}
					}
					break;
			}
		}
		else if(isWaiting)
		{
			this.background(0);
			
			// waiting line
			this.fill(255);
			this.textSize(30);
			if (delay < 90) {
				delay++;
				if (delay < 30) {
					this.text("Now Loading.", 380, 350);
				} else if (delay >= 30 && delay < 60) {
					this.text("Now Loading..", 380, 350);
				} else if (delay >= 60 && delay < 90){
					this.text("Now Loading...", 380, 350);
				} else {
					this.text("Now Loading...", 380, 350);
					delay = 0;
				}
			}
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
							
							// Level,map floor  picture should be 16*16 32*32....
							try 
							{
								levelImage = ImageIO.read(new File("level.png"));
								handler.createLevel(levelImage);
								
							} catch (IOException e) {}
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
    
// ------------------------- ControlP5 Button ------------------------------- //
// -------------------------------------------------------------------------- //
    
    public void initButton() {
		// Button Label Font
		PFont pfont = createFont("Arial", 25, true); 
		ControlFont font = new ControlFont(pfont);
				
		// Button Controller
		cp5 = new ControlP5(this);
		
		cp5.addButton("StartBtn")
			.setLabel("Let's Start Game")
			.setSize(this.btnWidth, this.btnHeight)
			.setPosition(this.width/2-this.btnWidth/2, 400)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("InfoBtn")
			.setLabel("Information")
			.setSize(this.btnWidth, this.btnHeight)
			.setPosition(this.width/2-this.btnWidth/2, 500)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("MuteBtn")
			.setLabel("Mute")
			.setSize(this.muteWidth, this.muteHeight)
			.setPosition(880, 620)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("BackBtn")
			.setLabel("BackToMainMenu")
			.setSize(this.btnWidth, this.btnHeight)
			.setPosition(-500, -500)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("NextBtn")
			.setLabel("Next")
			.setSize(this.muteWidth, this.muteHeight)
			.setPosition(-500, -500)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("PrevBtn")
			.setLabel("Prev")
			.setSize(this.muteWidth, this.muteHeight)
			.setPosition(-500, -500)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("SelectBtn")
			.setLabel("Select")
			.setSize(this.btnWidth, this.btnHeight)
			.setPosition(-500, -500)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
	}
    
    public void changeBtnPos(String s, float x, float y) {
    	cp5.getController(s)
    		.setPosition(x,  y);
    }
    
    public void StartBtn() {
    	if (this.menuStatus.equals("MainMenu")) {
    		this.menuStatus = "Selecting";
    		this.characterPicX = this.width/2-370;
    		this.changeBtnPos("SelectBtn", this.width/2-this.btnWidth/2, 580);
    		this.changeBtnPos("PrevBtn", 30, 300);
    		this.changeBtnPos("NextBtn", 870, 300);
    		this.changeBtnPos("StartBtn", -500, -500);
    		this.changeBtnPos("InfoBtn", -500, -500);
    	}
    }
    
    public void InfoBtn() {
    	if (this.menuStatus.equals("MainMenu")) {
    		this.menuStatus = "Information1";
    		this.changeBtnPos("StartBtn", -500, -500);
    		this.changeBtnPos("InfoBtn", -500, -500);
    		this.changeBtnPos("BackBtn", this.width/2-this.btnWidth/2, 500);
    		this.changeBtnPos("NextBtn", 870, 300);
    	}
    }
    
    public void BackBtn() {
	    if (this.menuStatus.equals("Information1") || this.menuStatus.equals("Information2") || this.menuStatus.equals("Information3")) {
	    	this.menuStatus = "MainMenu";
	    	this.changeBtnPos("StartBtn", this.width/2-this.btnWidth/2, 400);
    		this.changeBtnPos("InfoBtn", this.width/2-this.btnWidth/2, 500);
    		this.changeBtnPos("BackBtn", -500, -500);
    		this.changeBtnPos("NextBtn", -500, -500);
    		this.changeBtnPos("PrevBtn", -500, -500);
	    }
    }
    
    public void NextBtn() {
    	if (this.menuStatus.equals("Information1")) {
    		this.menuStatus = "Information2";
    		this.changeBtnPos("PrevBtn", 30, 300);
    	} else if (this.menuStatus.equals("Information2")) {
    		this.menuStatus = "Information3";
    		this.changeBtnPos("NextBtn", -500, -500);
    	} else if (this.menuStatus.equals("Selecting")) {
    		this.characterID = (this.characterID+1) % 4;
    		this.characterPicX = this.characterPicXL;
    		Ani.to(this, (float)2, "characterPicX", this.width/2-370);
    	}
    }
    
    public void PrevBtn() {
    	if (this.menuStatus.equals("Information2")) {
    		this.menuStatus = "Information1";
    		this.changeBtnPos("PrevBtn", -500, -500);
    	} else if (this.menuStatus.equals("Information3")) {
    		this.menuStatus = "Information2";
    		this.changeBtnPos("NextBtn", 870, 300);
    	} else if (this.menuStatus.equals("Selecting")) {
    		this.characterID = (this.characterID==0) ? 3 : this.characterID-1;
    		this.characterPicX = this.characterPicXR;
    		Ani.to(this, (float)2, "characterPicX", this.width/2-370);
    	}
    }
    
    public void MuteBtn() {
    	if (this.isPlay) {
    		cp5.getController("MuteBtn")
    			.setColorBackground(color(255, 0, 0));
    		this.isPlay = false;
    		this.bgm.pause();
    	} else {
    		cp5.getController("MuteBtn")
				.setColorBackground(color(30, 144, 255));
    		this.isPlay = true;
    		this.bgm.play();
    	}
    }

    public void SelectBtn() {
    	if (this.menuStatus.equals("Selecting")) {
    		this.menuStatus = "Game";
    		this.changeBtnPos("SelectBtn", -500, -500);
    		this.changeBtnPos("PrevBtn", -500, -500);
    		this.changeBtnPos("NextBtn", -500, -500);
    		
    		// Connect to Server
    		this.isWaiting = true;
    		this.connect();
    	}
    }

 // ------------------------- Mouse Input Part ------------------------------- //
 // -------------------------------------------------------------------------- //
    
    public void mouseMoved() {
    	if (this.menuStatus.equals("Information2")) {
    		if (mouseX >= 200 && mouseX < 450 && mouseY >= 100 && mouseY < 400)
    			this.isOnAuthor[0] = true;
    		else if (mouseX >= 550 && mouseX < 800 && mouseY >= 100 && mouseY < 400)
    			this.isOnAuthor[1] = true;
    		else {
    			for (int i = 0; i < 5; i++)
    				this.isOnAuthor[i] = false;
    		}
    	} else if (this.menuStatus.equals("Information3")) {
    		if (mouseX >= 130 && mouseX < 380 && mouseY >= 100 && mouseY < 400)
    			this.isOnAuthor[2] = true;
    		else if (mouseX >= 395 && mouseX < 645 && mouseY >= 100 && mouseY < 400)
    			this.isOnAuthor[3] = true;
    		else if (mouseX >= 660 && mouseX < 910 && mouseY >= 100 && mouseY < 400)
    			this.isOnAuthor[4] = true;
    		else {
    			for (int i = 0; i < 5; i++)
    				this.isOnAuthor[i] = false;
    		}
    	}
    }
    
}

//------------------------------------------

