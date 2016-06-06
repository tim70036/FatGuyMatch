package Client;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

import SuperClass.Character;
import SuperClass.Darkness;
import SuperClass.Entity;
import SuperClass.FireSkill;
import SuperClass.Handler;
import SuperClass.LazerSkill;
import SuperClass.Missile;
import SuperClass.Shit;
import SuperClass.Skill;
import SuperClass.Thunder;
import SuperClass.Trail;
import SuperClass.Type;
import controlP5.ControlFont;
import controlP5.ControlP5;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import de.looksgood.ani.Ani;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
public class Client extends PApplet{
	
	private static final long serialVersionUID = 1L;

	public static int width , height;
	
	private ControlP5 cp5;
	
	// Status
	private String menuStatus;
	private boolean isWaiting;
	private boolean isRunning;
	private boolean isGameOver;
	private boolean isInMenu;
	
	private String winner;
	private String bossScore;
	private String towerScore;
	private ArrayList<String>	playerAccount;
	private ArrayList<String> 	playerKill;
	private ArrayList<String> 	playerDied;
	private ArrayList<String> 	playerTowerKill;
	
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
	private PImage instrImg;
	private PImage[] menuPic = new PImage[3];
	private PImage[] author = new PImage[5];
	private PImage[] authorName = new PImage[5];
	private PImage[][] character = new PImage[4][3];
	
	// Mouse Detector
	private boolean[] isOnAuthor = new boolean[5];
	
	private int playerNum;
	private Handler handler;
	
	// Attributes for Network
	public int ID;
	public String playerName;
	private String IP;
	private int port;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private ClientThread clientThread;
	
	// cam
	public static Camera cam;
	
	// Level
	public BufferedImage levelImage;
	
	/// pic data
	public static PictureSheet sheet[];
	public static Picture player[][];
	
	public static PictureSheet boss_sheet;
	public static Picture boss[];
	
	// Shit
	public static PImage shitImage;
	
	// Lazer
	public static PImage lazerImage;
	
	// Normal attack
	public static PImage normalAttack[][];
	
	// Missile
	public static PImage traceAttack[][];
	
	//Boss attack
	public static PImage bossAttack [];
	
	// Door
	public static PImage doorImage;
	
	// SKill
	private boolean fireValid = true;
	private boolean missileValid = true;
	private int fireSkillNum;
	private int lazerSkillNum;
	private int missileNum;
	private int trailNum;
	private int shitNum;
	private int thunderNum;
	private int darkNum;
	// music
	private Minim minim;
	private AudioPlayer bgm;
	private boolean isPlay;
	
	private int fireSoundNum;
	private ArrayList<ArrayList<Sound>> fireSound;
	private int missileSoundNum;
	private ArrayList<ArrayList<Sound>> missileSound;
	
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
		this.isGameOver = false;
		this.isInMenu = true;
		
		this.isPlay = true;
		
		playerNum = 0;
		handler = new Handler();

		cam = new Camera();
	}
	
	public void setup() {
		
		// Record state
		this.pushMatrix();
		
		//	Sprite
		sheet = new PictureSheet[4];
		for(int i = 0; i < 4; i++){
			sheet[i] = new PictureSheet(loadImage("match"+ i + ".png"));
		}
		// Player
		player = new Picture[4][15];
		for(int i=0;i<4;i++)
			for(int tmp=0;tmp<5;tmp++){
				player[i][tmp] = new Picture(sheet[i],tmp,0,64);
				player[i][tmp].reSize(100, 100);
				player[i][tmp+5] = new Picture(sheet[i],tmp,1,64);
				player[i][tmp+5].reSize(100, 100);
				player[i][tmp+10] = new Picture(sheet[i],tmp,2,64);
				player[i][tmp+10].reSize(100, 100);
			}
		
		boss_sheet = new PictureSheet(loadImage("boss.png" ));
		boss = new Picture[15];
		boss[5] = new Picture(boss_sheet,0 , 0 ,200);
		boss[5].reSize(300, 300);
		for(int tmp=0;tmp<5;tmp++)
		{
			boss[tmp] = new Picture(boss_sheet,tmp,0,200);
			boss[tmp].reSize(300, 300);
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
		
		// Sound
		fireSoundNum = 10;
		missileSoundNum = 10;
		fireSound = new ArrayList<ArrayList<Sound>>();
		missileSound = new ArrayList<ArrayList<Sound>>();
		for(int i=0 ; i<4 ;i++)
		{
			this.fireSound.add(new ArrayList<Sound>());
			this.missileSound.add(new ArrayList<Sound>());
		}
		
		// Water 
		for(int i=0 ; i<fireSoundNum ; i++)
			fireSound.get(0).add(new Sound("water0.wav"));
		for(int i=0 ; i<missileNum ; i++)
			missileSound.get(0).add(new Sound("water1.wav"));
		// Fire
		for(int i=0 ; i<fireSoundNum ; i++)
			fireSound.get(1).add(new Sound("fire0.wav"));
		for(int i=0 ; i<missileNum ; i++)
			missileSound.get(1).add(new Sound("fire1.wav"));
		// Dark
		for(int i=0 ; i<fireSoundNum ; i++)
			fireSound.get(2).add(new Sound("dark0.wav"));
		for(int i=0 ; i<missileNum ; i++)
			missileSound.get(2).add(new Sound("dark1.wav"));
		// Love
		for(int i=0 ; i<fireSoundNum ; i++)
			fireSound.get(3).add(new Sound("love0.wav"));
		for(int i=0 ; i<missileNum ; i++)
			missileSound.get(3).add(new Sound("love1.wav"));
		
		
		
		// Menu picture & animation
		this.menuBg = this.loadImage("menuBg.png");
		this.titleImg = this.loadImage("title.png");
		this.instrImg = this.loadImage("instruction.png");
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
		
		// Shit
		shitImage = this.loadImage("shit.png");
		shitImage.resize(50, 50);
		
		// Lazer
		lazerImage = this.loadImage("lazer.png");
		lazerImage.resize(50, 50);
		
		// Normal attack
		normalAttack = new PImage[4][2];
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 2; j++){
				normalAttack[i][j] = this.loadImage("normalAttack"+ i + j +".png");
			}
		}
		
		// Missile
		traceAttack = new PImage[4][2];
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 2; j++){
				traceAttack[i][j] = this.loadImage("trace"+ i + j +".png");
			}
		}
		
		//Boss attack
		bossAttack= new PImage [2];
		for(int i=0;i<2;i++){
			bossAttack[i] = this.loadImage("bossAttack" + i + ".png");
			bossAttack[i].resize(50, 50);
		}
		
		// Door
		doorImage = this.loadImage("door.png");
		doorImage.resize(150, 150);
		
		Ani.to(this, (float)2, "titleY", 50);
		Ani.to(this, (float)2, "menu_bgX", 50);
		
	}
	
	public void loaddata(){}
	
	public void draw() {
		
		this.translate(cam.getX(), cam.getY());
		
		if(isInMenu)
		{
			if (this.menuStatus.equals("MainMenu")) {
				this.background(255);
				// MainMenu Picture
				this.image(this.menuBg, 0, 0, Client.width, Client.height);
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
				this.image(this.menuBg, 0, 0, Client.width, Client.height);
				this.image(this.instrImg, 50, 30 , Client.width-200, Client.height-200);
			}
			else if (this.menuStatus.equals("Information2")) {
				this.background(255);
				this.image(this.menuBg, 0, 0, Client.width, Client.height);
				
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
				this.image(this.menuBg, 0, 0, Client.width, Client.height);
				
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
							this.image(this.character[this.characterID][index], this.characterPicX, Client.height/2-330, 730, 550);
							delay++;
							if (delay == 20) {
								delay = 0;
								index = (index+1) % 3;
							}
						}
						break;
					case 1:
						if (delay < 20) {
							this.image(this.character[this.characterID][index], this.characterPicX, Client.height/2-330, 730, 550);
							delay++;
							if (delay == 20) {
								delay = 0;
								index = (index+1) % 3;
							}
						}
						break;
					case 2: 
						if (delay < 20) {
							this.image(this.character[this.characterID][index], this.characterPicX, Client.height/2-330, 730, 550);
							delay++;
							if (delay == 20) {
								delay = 0;
								index = (index+1) % 3;
							}
						}
						break;
					case 3:
						if (delay < 20) {
							this.image(this.character[this.characterID][index], this.characterPicX, Client.height/2-330, 730, 550);
							delay++;
							if (delay == 20) {
								delay = 0;
								index = (index+1) % 3;
							}
						}
						break;
				}
			}
		}
		else if(isRunning)
		{
			this.background(255);
			handler.display(this);
			
			int tag=0;
			for(Character en:	handler.getCharacter())
			{
				cam.tick(en);
				if(tag==ID)break;tag++;
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
		else if(isGameOver)
		{
			this.popMatrix();
			
			this.background(0);
			this.fill(255);
			this.stroke(255);
			
			changeBtnPos("ReplayBtn" , Client.width/2-this.btnWidth/2, 550);
			
			// Show text
			this.textSize(70);
			this.text("Winner : " + winner, Client.width/2-220 , 100);
			
			this.textSize(30);
			this.text("Boss  Kills  " + bossScore + "  Fat Guys", Client.width/2-150 , 150);
			this.text("Tower Kills  " + towerScore + "  Fat Guys", Client.width/2-150 , 200);
			
			this.textSize(35);
			this.text("PLAYER  P.KILL  T.KILL  DIED  KDA", Client.width/2-300, 250);
			this.line(345, 220, 345, 510);
			this.line(465, 220, 465, 510);
			this.line(580, 220, 580, 510);
			this.line(685, 220, 685, 510);
			
			this.textSize(30);
			for(int i=0 ; i<playerNum ; i++)
			{
				String s1 = playerAccount.get(i);
				String s2 = playerKill.get(i);
				String s3 = playerTowerKill.get(i);
				String s4 = playerDied.get(i);
				
				float k = Float.parseFloat(s2);
				float d = Float.parseFloat(s4);
				float a = Float.parseFloat(s3);
				float kda = (k + a) / d;
				 
				
				String s5 = String.format("%.1f", kda);
				
				this.line(180, 260+i*30, 780, 260+i*30);
				this.text(s1, 345-s1.length()*19  , 300+i*30);
				this.text(s2, 395, 300+i*30);
				this.text(s3, 520, 300+i*30);
				this.text(s4, 620, 300+i*30);
				this.text(s5, 705, 300+i*30);
			}
			
			this.stroke(0);
			this.pushMatrix();
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
		try 
		{
			socket = new Socket(IP , port);
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			clientThread = new ClientThread();
			clientThread.start();
			
		} catch (UnknownHostException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		
		//send CharacterID
		sendMessage("CharacterType");
		sendMessage(Integer.toString(this.characterID));
		
		// Send PlayerName
		sendMessage("Player Name");
		sendMessage(this.playerName);
		
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
					else if(command.equals("Game Over"))
					{
						command = reader.readLine();
						winner = command;
						
						// Reset score board
						playerAccount = new ArrayList<String>();
						playerKill = new ArrayList<String>();
						playerTowerKill = new ArrayList<String>();
						playerDied = new ArrayList<String>();
			
						for(int i=0 ; i<playerNum ; i++)
						{
							String player = reader.readLine();
							String characterKill = reader.readLine();
							String towerKill = reader.readLine();
							String diedNum = reader.readLine();
							
							playerAccount.add(player);
							playerKill.add(characterKill);
							playerTowerKill.add(towerKill);
							playerDied.add(diedNum);
							
						}
						
						bossScore = reader.readLine();
						towerScore = reader.readLine();
						
						// Reset state
						isRunning = false;
						isGameOver = true;
						
					}
					else if(command.equals("GameData"))
					{
						// Character's Data
						for(Character ch : handler.getCharacter())
						{
							String x = reader.readLine();
							String y = reader.readLine();
							String frame = reader.readLine();
							String life = reader.readLine();
							String w = reader.readLine();
							String h = reader.readLine();
							
							ch.setX(Float.parseFloat(x));
							ch.setY(Float.parseFloat(y));
							ch.setFrame(Integer.parseInt(frame));
							ch.life = Integer.parseInt(life);
							ch.setWidth(Integer.parseInt(w));
							ch.setHeight(Integer.parseInt(h));;
						}
						
						// Entity Data
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
							String n = reader.readLine();
							String m = reader.readLine();
							s.setX(Float.parseFloat(x));
							s.setY(Float.parseFloat(y));
							s.used = (used.equals("True")) ? true : false;
							s.face = Integer.parseInt(n);
							s.uniAttack = Integer.parseInt(m);
						}
						
						// Trail Data
						for(Trail t : handler.getTrail())
						{
							String x = reader.readLine();
							String y = reader.readLine();
							String used = reader.readLine();
							String frame = reader.readLine();
							String Alpha = reader.readLine();
							String Character = reader.readLine();
							String w = reader.readLine();
							String h = reader.readLine();
							
							t.setX(Float.parseFloat(x));
							t.setY(Float.parseFloat(y));;
							t.used = (used.equals("True")) ? true : false;
							t.setFrame(Integer.parseInt(frame));
							t.setAlpha(Float.parseFloat(Alpha));
							t.setCharacterID(Integer.parseInt(Character));
							t.setWidth(Integer.parseInt(w));
							t.setHeight(Integer.parseInt(h));;
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
							{
								command = reader.readLine();
								String name = reader.readLine();
								handler.addCharacter(new Character(100,100,100,100,Type.CHARACTER,true,handler, i,Integer.parseInt(command), name));
							}
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
						else if(command.equals("Thunder"))
						{
							command = reader.readLine();
							
							thunderNum = Integer.parseInt(command);
							for(int i=0 ; i<thunderNum; i++)
								handler.addSkill(new Thunder(3000,3000,50,50,Type.THUNDERSKILL, true, handler));
							
						}
						else if(command.equals("Darkness"))
						{
							command = reader.readLine();
							
							darkNum = Integer.parseInt(command);
							for(int i=0 ; i<darkNum; i++)
								handler.addSkill(new Darkness(3000,3000,50,50,Type.DARKSKILL, true, handler));
							
						}
						else if(command.equals("Trail"))
						{
							command = reader.readLine();
							
							trailNum = Integer.parseInt(command);
							for(int i=0 ; i<trailNum ; i++)
								handler.addTrail(new Trail(-100, 0, 100, 100, Type.TRAIL, false, handler));
						}
						else if(command.equals("Shit"))
						{
							command = reader.readLine();
							
							shitNum = Integer.parseInt(command);
							for(int i=0 ; i<shitNum ; i++)
								handler.addSkill(new Shit(-100,0,50,50,Type.SHIT, true, handler));
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
    	if(isRunning)
    	{
    		if(key == 'w' || key == 'a' || key == 's' || key == 'd' || key == 'o' || key == 'p')
        	{
        		sendMessage("PlayerInput");
        		sendMessage("Press");
        	}
        	if(key == 'w')	sendMessage("W");
        	else if(key == 'a')	sendMessage("A");
        	else if(key == 's')	sendMessage("S");
        	else if(key == 'd')	sendMessage("D");
        	else if(key == 'o') 
        	{
        		if(this.fireValid == true)
        		{
        			this.fireValid = false;
        			sendMessage("O");
        			
        			ArrayList<Sound> sound = fireSound.get(characterID);
        			for(Sound f : sound)
        			{
        				if(f.isPlaying() == false)
        				{
        					f.play();
        					break;
        				}
        			}
        		}
        	}
        	else if(key == 'p')
        	{
        		if(missileValid == true)
        		{
        			missileValid = false;
        			sendMessage("P");
        			
        			ArrayList<Sound> sound = missileSound.get(characterID);
        			for(Sound f : sound)
        			{
        				if(f.isPlaying() == false)
        				{
        					f.play();
        					break;
        				}
        			}
        		}
        	}
    	}
 	}
    
    public void keyReleased()
    {
    	if(isRunning)
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
        	else if(key == 'o') fireValid = true;
        	else if(key == 'p') missileValid = true;
    	}
    }
    
// ------------------------- ControlP5 Button ------------------------------- //
// -------------------------------------------------------------------------- //
    
    public void initButton() {
		// Button Label Font
		PFont pfont = createFont("Arial", 25, true); 
		ControlFont font = new ControlFont(pfont);
				
		// Button Controller
		cp5 = new ControlP5(this);
		
		cp5.addTextfield("EnterPlayerName")
	     .setPosition(-500,-500)
	     .setSize(200,40)
	     .setFont(font)
	     .setFocus(true)
	     .setColor(color(255,0,0))
	     ;
		
		cp5.addButton("ReplayBtn")
			.setLabel("Replay")
			.setSize(this.btnWidth, this.btnHeight)
			.setPosition(-500, -500)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("StartBtn")
			.setLabel("Let's Start Game")
			.setSize(this.btnWidth, this.btnHeight)
			.setPosition(Client.width/2-this.btnWidth/2, 400)
			.setColorForeground(color(0,255,0))
			.setColorBackground(color(30, 144, 255))
			.getCaptionLabel()
			.setFont(font)
			.toUpperCase(false);
		
		cp5.addButton("InfoBtn")
			.setLabel("Information")
			.setSize(this.btnWidth, this.btnHeight)
			.setPosition(Client.width/2-this.btnWidth/2, 500)
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
		
	}
    
    public void changeBtnPos(String s, float x, float y) {
    	cp5.getController(s)
    		.setPosition(x,  y);
    }
    
    public void EnterPlayerName(String theText)
    {
    	if (this.menuStatus.equals("Selecting")) 
    	{
    		Scanner s = new Scanner(theText);
    		String checkSpace = s.next();
    		if(checkSpace != null)
    		{
    			this.menuStatus = "Game";
        		this.changeBtnPos("EnterPlayerName", -500, -500);
        		this.changeBtnPos("PrevBtn", -500, -500);
        		this.changeBtnPos("NextBtn", -500, -500);
        		
        		playerName = theText;
        		// Connect to Server
        		this.isInMenu = false;
        		this.isWaiting = true;
        		this.connect();
    		}
    		s.close();
    	}
    }
    
    public void ReplayBtn()
    {
    	ClientMain.play();
    }
    
    public void StartBtn() {
    	if (this.menuStatus.equals("MainMenu")) {
    		this.menuStatus = "Selecting";
    		this.characterPicX = Client.width/2-370;
    		this.changeBtnPos("EnterPlayerName" ,Client.width/2-this.btnWidth/2, 580);
    		this.changeBtnPos("PrevBtn", 30, 300);
    		this.changeBtnPos("NextBtn", 870, 300);
    		this.changeBtnPos("StartBtn", -500, -500);
    		this.changeBtnPos("InfoBtn", -500, -500);
    		this.changeBtnPos("MuteBtn", -500, -500);
  
    	}
    }
    
    public void InfoBtn() {
    	if (this.menuStatus.equals("MainMenu")) {
    		this.menuStatus = "Information1";
    		this.changeBtnPos("StartBtn", -500, -500);
    		this.changeBtnPos("InfoBtn", -500, -500);
    		this.changeBtnPos("BackBtn", Client.width/2-this.btnWidth/2, 550);
    		this.changeBtnPos("NextBtn", 870, 300);
    	}
    }
    
    public void BackBtn() {
	    if (this.menuStatus.equals("Information1") || this.menuStatus.equals("Information2") || this.menuStatus.equals("Information3")) {
	    	this.menuStatus = "MainMenu";
	    	this.changeBtnPos("StartBtn", Client.width/2-this.btnWidth/2, 400);
    		this.changeBtnPos("InfoBtn", Client.width/2-this.btnWidth/2, 500);
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
    		Ani.to(this, (float)2, "characterPicX", Client.width/2-370);
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
    		Ani.to(this, (float)2, "characterPicX", Client.width/2-370);
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

