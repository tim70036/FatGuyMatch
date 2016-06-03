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

import SuperClass.Character;
import SuperClass.Entity;
import SuperClass.Handler;
import SuperClass.LazerSkill;
import SuperClass.Missile;
import SuperClass.Skill;
import SuperClass.Tower;
import SuperClass.Trail;
import SuperClass.Type;
import SuperClass.Wall;
import SuperClass.FireSkill;

public class Server {
	
	//ArrayList<Character> handler;
	ArrayList<ServerThread> threadPool;// Store the connection to all Client
	
	Handler handler;
	
	// Game Part
	GameThread gameThread;
	public boolean isRunning = false;
	public int wallNum;
	
	// Network Part
	public int port;
	public int playerNum;
	ServerSocket serverSocket;
	
	// Skill
	public int fireSkillNum;
	public int lazerSkillNum;
	public int missileNum;
	public int trailNum;
	
	public Server(int port, int playerNum)
	{
		//handler = new ArrayList<Character>();
		threadPool = new ArrayList<ServerThread>();
		handler = new Handler();
		
		try 
		{
			this.port = port;
			this.playerNum = playerNum;
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {e.printStackTrace();}
		
		isRunning = false;
		start();
	}
	
	public synchronized void start()
	{
		if(isRunning)	return; 
		
		// Accepting clients until every player is connected
		System.out.println("Server starts waiting for client.");
		while(true)
		{
			try 
			{
				Socket client = serverSocket.accept();
				System.out.println("\nAccpet player " + threadPool.size() + "\nFrom address: " + client.getInetAddress() + "\n");
				
				ServerThread connection = new ServerThread(client, threadPool.size()); // Make a ServerThread
				connection.start(); // Start it
				threadPool.add(connection); // Add into threadPool
				
				// Everyone is connected ?
				if(threadPool.size() >= playerNum)	break;
				
			} catch (IOException e) {e.printStackTrace();}
		}
		System.out.println("Server stops waiting for client.");
		
		// Init Data
		init();
		
		// Tell all Client to start
		broadCast("StartGame"); 
		
		// Id
		setAllID();
				
		
		// Start GameThread
		isRunning = true;
		gameThread = new GameThread();
		gameThread.start();
	}
	
	public synchronized void init() // Init all object , and tell Clients to init the same thing
	{
		// Character
		for(int i=0 ; i < playerNum ; i++)
			handler.addCharacter(new Character(100,100,100,100,Type.CHARACTER,true,handler, i));
		broadCast("Init");	broadCast("Characters");	broadCast(Integer.toString(playerNum));
		
		
		///SKILL BALL
		fireSkillNum = 50;
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
		
		//Trail 
		trailNum = 200;
		for(int i=0 ; i<trailNum ; i++)
			handler.addTrail(new Trail(-100,0,100,100,Type.TRAIL , false, handler));
		broadCast("Init"); broadCast("Trail");	broadCast(Integer.toString(trailNum));
	
		//init map,floor.   picture should be 16*16 32*32....
		try 
		{
			handler.createLevel(ImageIO.read(new File("level.png")));
		} catch (IOException e) {}
	}
	
	public synchronized void stop()
	{
		if(!isRunning)	return;
		
		isRunning = false;
		try 
		{
			gameThread.join();
		} catch (InterruptedException e) {e.printStackTrace();}
	}
// -------------------------Game Thread ----------------------------------- //
// ----------------------------------------------------------------------- //
	
	class GameThread extends Thread{ // The Main Game Thread
		
		public void run()
		{
			System.out.println("Server is starting game thread.");
			
			// Sever too overload ?? Need FPS 60 ???? ---> The best is same FPS as CLient ---> FPS 120
			long lastTime = System.nanoTime();
			double delta = 0.0;
			double ns = 100000000.0/60.0;
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
			
			
			System.out.println("Server stops game thread");
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
			broadCast(x);
			broadCast(y);
			broadCast(frame);
			broadCast(life);
			
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
			broadCast(x);
			broadCast(y);
			broadCast(u);
		}
		
		// Trail Data
		for(Trail t : handler.getTrail())
		{
			String x = Float.toString(t.getX());
			String y = Float.toString(t.getY());
			String u = (t.used == true) ? "True" : "False";
			String frame = Integer.toString(t.getFrame());
			String Alpha = Float.toString(t.getAlpha());
			broadCast(x);
			broadCast(y);
			broadCast(u);
			broadCast(frame);
			broadCast(Alpha);
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
		private int playerID; // ??? useful???
		
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
					//System.out.println("\nFrom client " + playerID);
					//System.out.println(command);
					
					
					if(command.equals("Ready to Start"))
					{
						
					}
					else if(command.equals("PlayerInput"))
					{
						command = reader.readLine();
						//System.out.println(command);
						
						if(command.equals("Press"))
						{
							command = reader.readLine();
							//System.out.println(command);
							
							if(command.equals("W"))
							{
							    if(!handler.getCharacter().get(playerID).jumping)
							    {
									handler.getCharacter().get(playerID).jumping = true;
									handler.getCharacter().get(playerID).falling = false;
									handler.getCharacter().get(playerID).gravity = 1;
								}
							}
							if(command.equals("A"))
							{
								handler.getCharacter().get(playerID).setVelX(-0.4f);
								handler.getCharacter().get(playerID).move = true;
								handler.getCharacter().get(playerID).face = 1;
							}
							/*else if(command.equals("S"))
								characters.getEntity().get(playerID).setVelY(0.1f);*/
							if(command.equals("D"))
							{
								handler.getCharacter().get(playerID).setVelX(0.4f);
								handler.getCharacter().get(playerID).move = true;
								handler.getCharacter().get(playerID).face = 0;
							}
							// Fire Skill
							if(command.equals("C"))
							{
								if(handler.getCharacter().get(playerID).life > 0)
								{
									// Find an unused FireSkill
									FireSkill fire = null;
									for(Skill s : handler.getSkill())
									{
										if(s.getType() == Type.FIRESKILL && s.used == false)
										{
											fire = (FireSkill) s;
											break;
										}
									}
									
									// Launch fire
									if(fire != null)
									{
										fire.setX(handler.getCharacter().get(playerID).getX());
										fire.setY(handler.getCharacter().get(playerID).getY());
										fire.face = handler.getCharacter().get(playerID).face;
										fire.playerID  = playerID;
										fire.used = true;
									}
								}
							}
							// Missile Skill
							else if(command.equals("F"))
							{
								if(handler.getCharacter().get(playerID).life > 0)
								{
									// Find an unused Missile Skill
									Missile missile = null;
									for(Skill s : handler.getSkill())
									{
										if(s.getType() == Type.MISSILE && s.used == false)
										{
											missile = (Missile) s;
											break;
										}
									}
									
									// Launch Missle
									if(missile != null)
									{
										missile.setX(handler.getCharacter().get(playerID).getX());
										missile.setY(handler.getCharacter().get(playerID).getY());
										if(playerNum==1)
											missile.en = handler.getCharacter().get(0);
										else
											missile.en = handler.getCharacter().get((playerID+1)%(playerNum));
										missile.playerID  = playerID;
										missile.used = true;
									}
								}	
							}
							else;
						}
						if(command.equals("Release"))
						{
							command = reader.readLine();
							System.out.println(command);
							
							if(command.equals("A")){
								handler.getCharacter().get(playerID).setVelX(0);
								handler.getCharacter().get(playerID).move = false;
							}
							/*else if(command.equals("S"))
								characters.getEntity().get(playerID).setVelY(0);*/
							else if(command.equals("D")){
								handler.getCharacter().get(playerID).setVelX(0);
								handler.getCharacter().get(playerID).move = false;
							}
							else;
						}
					}
					
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		
		public void sendMessage(String message) // Send Message to Client
		{
			writer.println(message);
			writer.flush();
		}
	}
}
