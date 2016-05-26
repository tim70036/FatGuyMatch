package SuperClass;

import processing.core.PApplet;

public class Wall extends Tile {

	public Wall(int x, int y, int width, int height, Type type, boolean solid, Handler handler) {
		super(x, y, width, height, type, solid, handler);
	}

	@Override
	public void display(PApplet parent) 
	{
		parent.fill(255,0,0);
		parent.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight(),80);
		parent.fill(255);
	}

	@Override
	public void update() 
	{
		
	}

}
