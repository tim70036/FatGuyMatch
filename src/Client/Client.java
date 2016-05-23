package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


import de.looksgood.ani.Ani;
import processing.core.PApplet;
public class Client extends PApplet{
	
	private ArrayList<Character> character = new ArrayList<Character>();
	private final static int width = 1200, height = 650;
	
	// Attributes for Network
	private String IP;
	private int port;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private ClientThread clientThread;
	
	public void setup() {

		size(width, height);
		smooth();
		Ani.init(this);
		
		// Connect to Server
		this.connect();
	}
	public void loaddata(){
		
		
	}

	
	public void draw() {
		/*if(game wait){
		 *		
		 *	if(choose character)....
		 *
		 *  if(ready to play){
		 * 		loaddata();
		 *  }
		*/
		//else if(game play){....}
		
	}
// ----------------------------------------------------------------------- //
// ----------------------------------------------------------------------- //
	public void sendMessage(String message)
	{
		writer.println(message);
		writer.flush();
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
	class ClientThread extends Thread //持續接收來自server的訊息,根據訊息更新gui
	{
		public void run()
		{
			while(true)
			{
				try {
					String command = reader.readLine();
					
					if(command.equals(?????))
					{
						
					}
					else if(command.equals(????))
					{
						
					}
					
					
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
