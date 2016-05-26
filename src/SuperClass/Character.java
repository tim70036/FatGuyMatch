package SuperClass;

import Client.*;
import Server.Type;
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
		if(getY() <= 0)	{
			setY(0);
		    if(falling) falling = true;
		}
		if(getX() + getWidth() >= ClientMain.windowWidth) 		setX(ClientMain.windowWidth - getWidth());
		if(getY() + getHeight() >= ClientMain.windowHeight)	setY(ClientMain.windowHeight - getHeight());
		
		if(jumping){
			gravity-=0.003;
			setVelY((int)-gravity);
			if(gravity<=0.0){
				jumping = false;
				falling = true;
			}
		}
		if(falling){
			gravity+=0.003;
			setVelY((int)gravity);
			if(gravity>=5.0){
				jumping = false;
				falling = true;
			}
			
		}
	}

}
