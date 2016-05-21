package Client;

import java.util.ArrayList;

import de.looksgood.ani.Ani;
import processing.core.PApplet;


public class CharacterDisplay extends PApplet{
	
	private ArrayList<Character> character = new ArrayList<Character>();
	
	private final static int width = 1200, height = 650;
	public void setup() {

		size(width, height);
		smooth();
		Ani.init(this);
		
		
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
	
}
