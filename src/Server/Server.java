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
import SuperClass.Skill;
import SuperClass.Tower;
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
		
		// Tell all Client to start
		broadCast("StartGame"); 
		
		// Init Data
		init();
		
		// Start GameThread
		isRunning = true;
		gameThread = new GameThread();
		gameThread.start();
	}
	
	public synchronized void init() // Init all object , and tell Clients to init the same thing
	{
		// Id
		setAllID();
		
		// Character
		for(int i=0 ; i < playerNum ; i++)
			handler.addEntity(new Character(100,100,100,100,Type.CHARACTER,true,handler, i));
		broadCast("Init");	broadCast("Characters");	broadCast(Integer.toString(playerNum));
		
		
		///SKILL BALL
		fireSkillNum = 50;
		for(int i=0 ; i < fireSkillNum ; i++)
			handler.addSkill(new FireSkill(3000,3000,50,50,Type.FIRESKILL,true,handler));
		broadCast("Init");	broadCast("FireSkill");	broadCast(Integer.toString(fireSkillNum));
		
		//init map,floor.   picture should be 16*16 32*32....
		try 
		{
			handler.createLevel(ImageIO.read(new File("level.png")));
		} catch (IOException e) {}
		
		// Tower
		handler.addEntity(new Tower(500 ,500,100,100, Type.TOWER, true, handler));
		broadCast("Init");	broadCast("Tower");	//broadCast();
		
		// Lazer Skill
		lazerSkillNum = 50;
		for(int i=0 ; i<lazerSkillNum ; i++)
			handler.addSkill(new LazerSkill(100,0,50,50,Type.LAZERSKILL, true, handler));
		broadCast("Init");	broadCast("LazerSkill"); broadCast(Integer.toString(lazerSkillNum));
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
					
					
					if(command.equals("Connect"))
					{
						
					}
					else if(command.equals("PlayerInput"))
					{
						command = reader.readLine();
						//System.out.println(command);
						
						if(command.equals("Press"))
						{
							command = reader.readLine();
							System.out.println(command);
							
							if(command.equals("W"))
							{
								//characters.getEntity().get(playerID).setVelY(-0.1f);
							    if(!handler.getEntity().get(playerID).jumping)
							    {
									handler.getEntity().get(playerID).jumping = true;
									handler.getEntity().get(playerID).falling = false;
									handler.getEntity().get(playerID).gravity = 1;
								}
							}
							if(command.equals("A"))
							{
								handler.getEntity().get(playerID).setVelX(-0.2f);
								handler.getEntity().get(playerID).move = true;
								handler.getEntity().get(playerID).face = 1;
							}
							/*else if(command.equals("S"))
								characters.getEntity().get(playerID).setVelY(0.1f);*/
							if(command.equals("D"))
							{
								handler.getEntity().get(playerID).setVelX(0.2f);
								handler.getEntity().get(playerID).move = true;
								handler.getEntity().get(playerID).face = 0;
							}
							if(command.equals("C"))
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
									fire.setX(handler.getEntity().get(playerID).getX());
									fire.setY(handler.getEntity().get(playerID).getY());
									fire.playerID  = playerID;
									fire.used = true;
								}
							}
							else;
						}
						if(command.equals("Release"))
						{
							command = reader.readLine();
							System.out.println(command);
							
							if(command.equals("A")){
								handler.getEntity().get(playerID).setVelX(0);
								handler.getEntity().get(playerID).move = false;
							}
							/*else if(command.equals("S"))
								characters.getEntity().get(playerID).setVelY(0);*/
							else if(command.equals("D")){
								handler.getEntity().get(playerID).setVelX(0);
								handler.getEntity().get(playerID).move = false;
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
