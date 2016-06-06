package Server;

public class ServerMain {
	
	public static Server server;
	
	public static void main(String[] args) 
	{
		play();
	}
	
	public static void play()
	{
		server = new Server(6667, 1);
	}
	
}
