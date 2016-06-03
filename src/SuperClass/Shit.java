package SuperClass;

import processing.core.PApplet;
import Client.*;

public class Shit extends Skill {
	
	public ShitType shitType;
	
	

	public Shit(int x, int y, int width, int height, Type type, boolean solid, Handler handler, ShitType shit) {
		super(x, y, width, height, type, solid, handler);
		this.shitType = shit;
	}

	public void display(PApplet parent) 
	{
		if(used == true)
		{		
			parent.image(Client.shitImage, getX(), getY());
		}
	}

	@Override
	public void update() 
	{
		if(used == true)
		{
			if(jumping)
			{
				gravity-=0.0008;
				setVelY((float)-gravity);
				if(gravity<=0.90)
				{
					jumping = false;
					falling = true;
				}
			}
			else if(falling)
			{
				gravity+=0.003;
				setVelY((float)gravity);
				if(gravity >= 1.0)
				{
					jumping = true;
					falling = false;
				}
			}
		}
	}
}
