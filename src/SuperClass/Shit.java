package SuperClass;

import processing.core.PApplet;
import Client.*;

public class Shit extends Skill {
	
	public Shit(int x, int y, int width, int height, Type type, boolean solid, Handler handler) {
		super(x, y, width, height, type, solid, handler);
	}
	
	public static void putShit(float targetX , float targetY)
	{
		// Put an unsused shit and put on it
		Shit shit = null;
		for(Skill s : Skill.getHandler().getSkill())
		{
			if(s.getType() == Type.SHIT && s.used == false)
			{
				shit = (Shit) s;
				break;
			}
		}
		if(shit != null)
		{
			shit.setX(targetX);
			shit.setY(targetY);
			shit.gravity = 0.5;
			shit.jumping = true;
			shit.used = true;
		}
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
				gravity-=0.0004;
				setVelY((float)-gravity);
				if(gravity<=0.48)
				{
					jumping = false;
					falling = true;
				}
			}
			else if(falling)
			{
				gravity+=0.0004;
				setVelY((float)gravity);
				if(gravity >= 0.5)
				{
					jumping = true;
					falling = false;
				}
			}
			setY(getY() + getVelY());
		}
	}
}
