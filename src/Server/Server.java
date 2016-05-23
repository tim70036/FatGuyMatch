package Server;

import java.net.Socket;
import java.util.*;

public class Server {
	
	ArrayList<Character> characters;//(依據playerID放入Character)
	ArrayList<ServerThread> threadPool;//(存放ServerThread, broadCast時可用)

	public boolean isRunning = false;
	
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
// ----------------------------------------------------------------------- //
// ----------------------------------------------------------------------- //
	class ServerThread extends Thread{ // 持續接收來自client的訊息,  並根據訊息做該做的動作
		
		private Socket socket;
		private int playerID;
		
		public Socket getSocket() {return socket;}
		public int getPlayerID() {return this.playerID;}
		public void setSocket(Socket socket) {this.socket = socket;}
		public void setPlayerID(int id) {this.playerID = id;}

		public void broadCast()
		{
			
		}
		
		public void run()
		{
			
		}
	}
}
