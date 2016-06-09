package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import SuperClass.*;
import SuperClass.Character;

public class Server {
	
	//ArrayList<Character> handler;
	ArrayList<ServerThread> threadPool;// Store the connection to all Client
	ArrayList<Integer> CharacterID;// Store which Character
	
	Handler handler;
	
	// Game Part
	GameThread gameThread;
	public static boolean isRunning = false;
	public int wallNum;
	
	// Network Part
	public int port;
	public int playerNum;
	public int replayNum;
	ServerSocket serverSocket;
	
	// Skill
	public int fireSkillNum;
	public int lazerSkillNum;
	public int missileNum;
	public int trailNum;
	public int shitNum;
	public int darkNum;
	public int thunderNum;
	
	//Character init place
	public int initPlace[][];

	
	public Server(int port, int playerNum)
	{
		Boss.characterKill = 0;
		Tower.characterKill = 0;
		
		this.port = port;
		this.playerNum = playerNum;
		
		// init character's first place
		initPlace = new int[][]{
			{100,100},
			{100,2500},
			{2500,100},
			{2500,2500},
			{1000,1000},
			{1500,2000}
		};
		
		isRunning = false;
		start();
	}
	
	public synchronized void start()
	{
		if(isRunning)	return; 
		
		threadPool = new ArrayList<ServerThread>();
		CharacterID = new ArrayList<Integer>();
		try 
		{
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {e.printStackTrace();}
		
		// Need another thread for while loop, otherwise EDT will GG
		Thread thread = new Thread (new Runnable(){
			
			public void run()
			{
				// Accepting clients until every player is connected
				ServerMain.addLine("Server starts waiting for client.");
				while(true)
				{
					try 
					{
						Socket client = serverSocket.accept();
						ServerMain.addLine("\nAccpet player " + threadPool.size() + "\nFrom address: " + client.getInetAddress() + "\n");
						
						ServerThread connection = new ServerThread(client, threadPool.size()); // Make a ServerThread
						connection.start(); // Start it
						threadPool.add(connection); // Add into threadPool
						
						// Everyone is connected ?
						if(threadPool.size() >= playerNum)	break;
						
					} catch (IOException e) {e.printStackTrace();}
				}
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ServerMain.addLine("Server stops waiting for client.");
				
				
				// wait characterID
				while(CharacterID.size() < playerNum)
				{
					ServerMain.addLine("");
				}
				
				// Init Data
				init();
			}
		}
		);
		thread.start();
	}
	
	public synchronized void init() // Init all object , and tell Clients to init the same thing
	{
		handler = new Handler();
		
		// Character
		for(int i=0 ; i < playerNum ; i++)
			handler.addCharacter(new Character(initPlace[i][0],initPlace[i][1],100,100,Type.CHARACTER,true,handler, i,CharacterID.get(i)
					, threadPool.get(i).playerName));
		broadCast("Init");	broadCast("Characters");	broadCast(Integer.toString(playerNum));
		for(int i=0 ; i<playerNum ; i++)
		{
			broadCast(Integer.toString(CharacterID.get(i)));
			broadCast(threadPool.get(i).playerName);
		}
		
		///SKILL BALL
		fireSkillNum = 80;
		for(int i=0 ; i < fireSkillNum ; i++)
			handler.addSkill(new FireSkill(3000,3000,50,50,Type.FIRESKILL,true,handler));
		broadCast("Init");	broadCast("FireSkill");	broadCast(Integer.toString(fireSkillNum));
		
		// Lazer Skill
		lazerSkillNum = 50;
		for(int i=0 ; i<lazerSkillNum ; i++)
			handler.addSkill(new LazerSkill(3000,3000,50,50,Type.LAZERSKILL, true, handler));
		broadCast("Init");	broadCast("LazerSkill"); broadCast(Integer.toString(lazerSkillNum));
	
		//Missile Skill
		missileNum = 20;
		for(int i=0 ; i<missileNum; i++)
			handler.addSkill(new Missile(3000,3000,50,50,Type.MISSILE, true, handler));
		broadCast("Init");	broadCast("MissileSkill"); broadCast(Integer.toString(missileNum));
		//darkNUm
		darkNum = 20;
		for(int i=0 ; i<darkNum ; i++)
			handler.addSkill(new Darkness(3000,3000,100,100,Type.DARKSKILL, true, handler));
		broadCast("Init");	broadCast("Darkness"); broadCast(Integer.toString(darkNum));
		//thunder
		thunderNum = 30;
		for(int i=0 ; i<thunderNum ; i++)
			handler.addSkill(new Thunder(3000,3000,50,50,Type.THUNDERSKILL, true, handler));
		broadCast("Init");	broadCast("Thunder"); broadCast(Integer.toString(thunderNum));
		//Trail 
		trailNum = 200;
		for(int i=0 ; i<trailNum ; i++)
			handler.addTrail(new Trail(-100,0,100,100,Type.TRAIL , false, handler));
		broadCast("Init"); broadCast("Trail");	broadCast(Integer.toString(trailNum));
		
		// Shit
		shitNum = 30;
		for(int i=0 ; i<shitNum ; i++)
			handler.addSkill(new Shit(-100,0,50,50,Type.SHIT, true, handler));
		broadCast("Init"); broadCast("Shit");	broadCast(Integer.toString(shitNum));
		
		//init map,floor.   picture should be 16*16 32*32....
		try 
		{
			handler.createLevel(ImageIO.read(new File("level.png")));
		} catch (IOException e) {}
		
		
		
		// Tell all Client to start
		broadCast("StartGame"); 
		
		// Id
		setAllID();
				
		
		// Start GameThread
		isRunning = true;
		gameThread = new GameThread();
		gameThread.start();
	}
	
// -------------------------Game Thread ----------------------------------- //
// ----------------------------------------------------------------------- //
	
	class GameThread extends Thread{ // The Main Game Thread
		
		public void run()
		{
			ServerMain.addLine("Server is starting game thread.");
			
			// Sever too overload ?? Need FPS 60 ???? ---> The best is same FPS as CLient ---> FPS 120
			long lastTime = System.nanoTime();
			double delta = 0.0;
			double ns = 600000000.0/60.0;
			while(isRunning)
			{
				long nowTime = System.nanoTime();
				delta += (nowTime - lastTime) / ns;
				lastTime = nowTime;
				while(delta >= 1)
				{
					delta--;
					update();	
					sendData();	
				}
					
			}
			ServerMain.addLine("Server stops game loop.");
			
			// Who kill the boss? 
			int lastAttackPlayerID = -1;
			for(Entity e : handler.getEntity())
				if(e.getType() == Type.BOSS)
					lastAttackPlayerID = ((Boss)e).lastAttackPlayerID;
			
			broadCast("Game Over");
			
			// Send Score
			String winner = threadPool.get(lastAttackPlayerID).playerName;
			broadCast(winner);
			
			for(int i=0 ; i<playerNum ; i++)
			{
				// Player Name , Data
				int characterKill = handler.getCharacter().get(i).characterKill;
				int towerKill = handler.getCharacter().get(i).towerKill;
				int diedNum = handler.getCharacter().get(i).diedNum;
				
				broadCast(threadPool.get(i).playerName);
				broadCast(Integer.toString(characterKill));
				broadCast(Integer.toString(towerKill));
				broadCast(Integer.toString(diedNum));
			}
			
			int bossScore = Boss.characterKill;
			int towerScore = Tower.characterKill;
			broadCast(Integer.toString(bossScore));
			broadCast(Integer.toString(towerScore));
			
			
			ServerMain.addLine("Server stops game thread. ");
			
			// Replay
			ServerMain.replay();
		}
	}
	
	public void update()
	{
		handler.update();
	}
	
	public void sendData()
	{
		broadCast("GameData");
		
		// Character's Data
		for(Character ch : handler.getCharacter())
		{
			String x = Float.toString(ch.getX());
			String y = Float.toString(ch.getY());
			String frame = Integer.toString(ch.getFrame());
			String life = Integer.toString(ch.life);
			String w = Integer.toString(ch.getWidth());
			String h = Integer.toString(ch.getHeight());
			broadCast(x);
			broadCast(y);
			broadCast(frame);
			broadCast(life);
			broadCast(w);
			broadCast(h);
			
		}
		
		for(Entity ch : handler.getEntity())
		{
			String x = Float.toString(ch.getX());
			String y = Float.toString(ch.getY());
			String frame = Integer.toString(ch.getFrame());
			String life = Integer.toString(ch.life);
			broadCast(x);
			broadCast(y);
			broadCast(frame);
			broadCast(life);
			
		}
		
		// Skill Data
		for(Skill s : handler.getSkill())
		{
			String x = Float.toString(s.getX());
			String y = Float.toString(s.getY());
			String u = (s.used == true) ? "True" : "False";
			String n = Integer.toString(s.face);
			String m = Integer.toString(s.uniAttack);
			broadCast(x);
			broadCast(y);
			broadCast(u);
			broadCast(n);
			broadCast(m);
		}
		
		// Trail Data
		for(Trail t : handler.getTrail())
		{
			String x = Float.toString(t.getX());
			String y = Float.toString(t.getY());
			String u = (t.used == true) ? "True" : "False";
			String frame = Integer.toString(t.getFrame());
			String Alpha = Float.toString(t.getAlpha());
			String Character = Integer.toString(t.getCharacterID());
			String w = Integer.toString(t.getWidth());
			String h = Integer.toString(t.getHeight());
			broadCast(x);
			broadCast(y);
			broadCast(u);
			broadCast(frame);
			broadCast(Alpha);
			broadCast(Character);
			broadCast(w);
			broadCast(h);
		}
	}
// -------------------------NetWork Part ---------------------------------- //
// ----------------------------------------------------------------------- //

	public void broadCast(String message)
	{
		for(ServerThread i : threadPool)
			i.sendMessage(message);
	}
	public void setAllID()
	{
		int ID = 0;
		for(ServerThread i : threadPool){
			i.sendMessage(Integer.toString(ID));
			ID++;
		}
	}
	class ServerThread extends Thread{  // Deal with the message from Client
		
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
		private int playerID;
		public String playerName;
		private Character ch;
		
		public Socket getSocket() {return socket;}
		public int getPlayerID() {return this.playerID;}
		public void setSocket(Socket socket) {this.socket = socket;}
		public void setPlayerID(int id) {this.playerID = id;}

		public ServerThread(Socket client, int ID)
		{
			try 
			{
				playerID = ID;
				socket = client;
				writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			} catch (IOException e) {e.printStackTrace();}
		}
		
		public void run() // Receiving Message from Client , player input
		{
			while(true) // isRunning?
			{
				try 
				{
					String command = reader.readLine();
					//ServerMain.addLine("\nFrom client " + playerID);
					//ServerMain.addLine(command);
					
					
					if(command.equals("PlayerInput"))
					{
						command = reader.readLine();
						//ServerMain.addLine(command);
						
						ch = handler.getCharacter().get(playerID);
						if(command.equals("Press"))
						{
							command = reader.readLine();
							//ServerMain.addLine(command);
							
							if(command.equals("W"))
							{
							    if(!ch.jumping)
							    {
									ch.jumping = true;
									ch.falling = false;
									ch.gravity = 4;
								}
							}
							if(command.equals("A"))
							{
								if(ch.inTrail)	ch.setVelX(-4.1f);
								else ch.setVelX(-2.7f);
								ch.move = true;
								ch.face = 1;
							}
							if(command.equals("D"))
							{
								if(ch.inTrail)	ch.setVelX(4.1f);
								else ch.setVelX(2.7f);
								ch.move = true;
								ch.face = 0;
							}
							// Fire Skill
							if(command.equals("O"))
							{
								if(ch.life > 0)
								{
									// Find an unused FireSkill
									FireSkill fire = null;
									for(Skill s : handler.getSkill())
									{
										if(s.getType() == Type.FIRESKILL && s.used == false)
										{
											fire = (FireSkill) s;
											fire.setX(ch.getX());
											fire.setY(ch.getY());
											fire.face = ch.face;
											fire.playerID  = playerID;
											fire.used = true;
											fire.uniAttack = ch.characterID;
											break;
										}
									}
								}
							}
							// Missile Skill
							else if(command.equals("P"))
							{
								if(ch.life > 0)
								{
									for(Character e : handler.getCharacter())
									{
										if(e.playerID != ch.playerID)
										{
											if(ch.getBiggerBound().intersects(e.getBound()))
											{
												// Find an unused Missile Skill
												Missile missile = null;
												for(Skill s : handler.getSkill())
												{
													if(s.getType() == Type.MISSILE && s.used == false)
													{
														missile = (Missile) s;
														missile.setX(ch.getX());
														missile.setY(ch.getY());
														missile.en = e;
														missile.face = ch.face;
														missile.playerID  = playerID;
														missile.used = true;
														missile.uniAttack = ch.characterID;
														break;
													}
												}
											}
										}
									}
								}	
							}
							else;
						}
						if(command.equals("Release"))
						{
							command = reader.readLine();
							//ServerMain.addLine(command);
							
							if(command.equals("A"))
							{
								ch.setVelX(0);
								ch.move = false;
							}
							else if(command.equals("D"))
							{
								ch.setVelX(0);
								ch.move = false;
							}
						}
					}
					else if(command.equals("CharacterType"))
					{
						command = reader.readLine();
						int num = Integer.parseInt(command);
						CharacterID.add(num);
					}
					else if(command.equals("Player Name"))
					{
						command = reader.readLine();
						playerName = command;
					}
					
				} catch (Exception e) 
				{
					break;
				}
			}
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void sendMessage(String message) // Send Message to Client
		{
			writer.println(message);
			writer.flush();
		}
	}
}
