package SuperClass;

import processing.core.PApplet;

public class Darkness extends Skill {
	

	public Darkness(int x, int y, int width, int height, Type type, boolean solid, Handler handler) 
	{
		super(x, y, width, height, type, solid, handler);
		
	}

	public void display(PApplet parent) 
	{
		if(used == true)
		{
			parent.strokeWeight(5);
			parent.stroke(255,0,0);
			parent.fill(255);
			parent.rect(getX(), getY(), getWidth(), getHeight());
			parent.stroke(0);
			parent.strokeWeight(1);
		}
	}

	public void update()
	{
		//Being used, fire or not
		if(used == true)
		{
			delay++;
			if(delay>100)
			{
				frame++;
				delay=0;
				setX(getX()+getVelX());
				setY(getY()+getVelY());
				if(frame>50)
				{
					frame=0;
					this.die();
				}
			}
		}
	}

}
