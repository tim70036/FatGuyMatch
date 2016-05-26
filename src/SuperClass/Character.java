package SuperClass;

import Client.*;
import processing.core.PApplet;

public class Character extends Entity{

	public Character(int x, int y, int width, int height, Type type, boolean solid, Handler handler)
	{
		super(x, y, width, height, type, solid, handler);
	}

	public void display(PApplet parent) 
	{
		parent.fill(0);
		parent.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight(),80);
		parent.fill(255);
	}

	public void update() 
	{
		setX(getX() + getVelX());
		setY(getY() + getVelY());
		
		if(getX() <= 0)	setX(0);
		if(getY() <= 0)	setY(0);
		if(getX() + getWidth() >= ClientMain.windowWidth) 		setX(ClientMain.windowWidth - getWidth());
		if(getY() + getHeight() >= ClientMain.windowHeight)	setY(ClientMain.windowHeight - getHeight());
		
		// Collision Detection
		for(Tile t : this.getHandler().getTile())
		{
			if(t.isSolid() == false)	continue;
			
			if(t.getType() == Type.WALL)
			{
				if(this.getBoundTop().intersects(t.getBound())) // Character's top collide
				{
					this.setVelY(0);
					this.setY(t.getY() + t.getHeight()); // Right on the bottom of t
				}
				if(this.getBoundBottom().intersects(t.getBound())) // Character's bottom collide
				{
					this.setVelY(0);
					this.setY(t.getY() - this.getHeight()); // Right On the top of t
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
	}

}
