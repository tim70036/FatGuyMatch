package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

import SuperClass.Handler;

public class Server {
	
	//ArrayList<Character> characters;//(依據playerID放入Character)
	ArrayList<ServerThread> threadPool;//(存放ServerThread, broadCast時可用)
	
	Handler characters;
	
	// Game Part
	GameThread gameThread;
	public boolean isRunning = false;
	
	// Network Part
	public int port;
	public int playerNum;
	ServerSocket serverSocket;
	
	public Server(int port, int playerNum)
	{
		//characters = new ArrayList<Character>();
		threadPool = new ArrayList<ServerThread>();
		characters = new Handler();
		
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
	
	public synchronized void init()
	{
		// Character
		for(int i=0 ; i < playerNum ; i++)
			characters.addEntity(new Character(100,100,100,100,Type.CHARACTER,true,characters));
		broadCast("Init");	broadCast("Characters");	broadCast(Integer.toString(playerNum));
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
	
	class GameThread extends Thread{ // 遊戲運行的Thread
		
		public void run()
		{
			System.out.println("Server is starting game thread.");
			while(isRunning)
			{
				
			}
			System.out.println("Server stops game thread");
		}
	}
	
// -------------------------NetWork Part ---------------------------------- //
// ----------------------------------------------------------------------- //

	public void broadCast(String message)
	{
		for(ServerThread i : threadPool)
			i.sendMessage(message);
	}
	
	class ServerThread extends Thread{  // 處理跟各個Client的訊息溝通
		
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
		
		public void run() // 持續接收來自client的訊息,  並根據訊息做該做的動作
		{
			while(true) // isRunning?
			{
				try 
				{
					String command = reader.readLine();
					
					if(command.equals("Connect"))
					{
						
					}
					else if(command.equals("PlayerInput"))
					{
						
					}
					
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		
		public void sendMessage(String message) // 送訊息給Client
		{
			writer.println(message);
			writer.flush();
		}
	}
}
