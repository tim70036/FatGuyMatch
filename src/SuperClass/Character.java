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
		if(getY() <= 0)	setY(0);
		if(getX() + getWidth() >= ClientMain.windowWidth) 		setX(ClientMain.windowWidth - getWidth());
		if(getY() + getHeight() >= ClientMain.windowHeight)	setY(ClientMain.windowHeight - getHeight());
	}

}
