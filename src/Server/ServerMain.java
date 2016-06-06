package Server;

public class ServerMain {
	
	public static Server server;
	
	public static void main(String[] args) 
	{
		play();
	}
	
	public static void play()
	{
		if(server != null)
		{
			server = null;
			Thread s = new Thread (new Runnable(){
				
				public void run() 
				{
					try 
					{
						Thread.sleep((long) (3000));
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			});
			
			s.start();
			try 
			{
				s.join();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		server = new Server(6667, 1);
	}
	
}
