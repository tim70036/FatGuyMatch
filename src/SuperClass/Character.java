package SuperClass;

import Client.*;
import processing.core.PApplet;

public class Character extends Entity
{
	
	public Character(int x, int y, int width, int height, Type type, boolean solid, Handler handler)
	{
		super(x, y, width, height, type, solid, handler);
		frame = 0;
		delay = 0;
		frameNum = 4-1;
	}

	public void display(PApplet parent) 
	{
		parent.fill(0);
		parent.image(Client.player[frame].getImage(), this.getX(), this.getY());
		//parent.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
		parent.fill(255);
	}

	public void update() 
	{
		setX(getX() + getVelX());
		setY(getY() + getVelY());
		
		if(getX() <= 0)	setX(0);
		if(getY() <= 0)	
		{
			if(jumping)
			{
				jumping = false;
				setVelY(0);
				gravity = 0.0;
				falling = true;
			}
		}
		/*if(getX() + getWidth() >= ClientMain.windowWidth) 		setX(getX() + getVelX());
		if(getY() + getHeight() >= ClientMain.windowHeight)
		{
			setY(0);
		    if(falling) falling = false;
		}*/
		

		// Collision Detection
		for(Tile t : this.getHandler().getTile())
		{
			if(t.isSolid() == false)	continue;
			
			if(t.getType() == Type.WALL)
			{
				if(this.getBoundTop().intersects(t.getBound())) // Character's top collide
				{
					this.setVelY(0);
					/*this.setY(t.getY() + t.getHeight()); // Right on the bottom of t*/
					if(jumping)
					{
						jumping = false;
						setVelY(0);
						gravity = 0.0;
						falling = true;
					}
				}
				
				if(this.getBoundBottom().intersects(t.getBound())) // Character's bottom collide
				{
					this.setVelY(0);
					if(falling) 
						falling = false;
					
					this.setY(t.getY() - this.getHeight()); // Right On the top of t*/
				}
				else // Fall down if get out of platform ---> Bottom not collide
				{
				    if(!falling && !jumping) 
					{
						gravity = 0.0;
						falling = true;
					}
				}
				
				if(this.getBoundLeft().intersects(t.getBound())) // Character's left collide
				{
					this.setVelX(0);
					this.setX(t.getX() + t.getWidth()); // Right on the right of t
				}
				
				if(this.getBoundRight().intersects(t.getBound())) // Character's right collide
				{
					this.setVelX(0);
					this.setX(t.getX() - this.getWidth()); // Right on the left of t
				}
			}
		}

		// Jumping Falling
		if(jumping)
		{
			gravity-=0.00002;
			setVelY((float)-gravity);
			if(gravity<=0.0)
			{
				jumping = false;
				falling = true;
			}
		}
		if(falling)
		{
			gravity+=0.00015;
			setVelY((float)gravity);			
		}
		
		///------animate--------------
		if(move==true){
			delay++;
			if(delay>2000){
				if(frame==frameNum)frame=0;
				else frame++;
				delay = 0;
			}
		}
		else{
			delay = 0;
			frame = 0;
		}
		//////
	}
}
