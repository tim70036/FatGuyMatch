package SuperClass;

import processing.core.PApplet;

public class LazerSkill extends Skill {
	

	public LazerSkill(int x, int y, int width, int height, Type type, boolean solid, Handler handler) 
	{
		super(x, y, width, height, type, solid, handler);
		
	}

	public void display(PApplet parent) 
	{
		if(used == true)
		{
			parent.strokeWeight(5);
			parent.stroke(255,127,80);
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
			if(delay>500){
				frame++;
				delay=0;
				setX(getX()+getVelX());
				setY(getY()+getVelY());
				if(frame>50){
					frame=0;
					setX(100);
					setY(0);
					used = false;
				}
			}
		}
		// Skill done, back to origin x , y
		else
		{
			setVelX(0);
			setVelY(0);
			setX(100);
			setY(0);
		}
	}

}
