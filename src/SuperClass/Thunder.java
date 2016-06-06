package SuperClass;

import Client.Client;
import processing.core.PApplet;

public class Thunder extends Skill {
	

	public Thunder(int x, int y, int width, int height, Type type, boolean solid, Handler handler) 
	{
		super(x, y, width, height, type, solid, handler);
		
	}

	public void display(PApplet parent) 
	{
		if(used == true)
		{
			parent.image(Client.bossAttack[face+1], this.getX(), this.getY());
		}
	}

	public void update()
	{
		//Being used, fire or not
		if(used == true)
		{
			delay++;
			if(delay > 5){
				frame++;
				delay=0;
				setX(getX()+getVelX());
				setY(getY()+getVelY());
				if(frame>50){
					frame=0;
					this.die();
				}
			}
		}
	}

}
