package Client;

import java.awt.image.BufferedImage;

import Server.Type;
import SuperClass.Entity;
import SuperClass.Handler;
import processing.core.PApplet;

public class Character extends Entity{

	public Character(int x, int y, int width, int height, Type type, boolean solid, Handler handler)
	{
		super(x, y, width, height, type, solid, handler);
	}

	public void display(PApplet parent) 
	{
		parent.fill(0);
		parent.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
		parent.fill(255);
	}
}
