package SuperClass;

import processing.core.PApplet;

public class Wall extends Tile {

	public Wall(int x, int y, int width, int height, Type type, boolean solid, Handler handler) {
		super(x, y, width, height, type, solid, handler);
	}

	@Override
	public void display(PApplet parent) 
	{
		parent.image(Client.Client.wallImg, this.getX(), this.getY());
	}

	@Override
	public void update() 
	{
		
	}

}
