package SuperClass;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Client.ClientMain;
import processing.core.PApplet;
import processing.core.PImage;

public class Handler {
	private ArrayList<Entity>	entity;
	private ArrayList<Tile> tile;
	
	public Handler()
	{
		entity = new ArrayList<Entity>();
		tile = new ArrayList<Tile>();
		
		// Create floor 
		//createLevel();
	}
	
	public void display(PApplet parent)
	{
		for(Entity en : entity)
			en.display(parent);
		for(Tile t : tile)
			t.display(parent);
	}
	
	public void update()
	{
		for(Entity en : entity)
			en.update();
		for(Tile t : tile)
			t.update();
	}
	
	public void createLevel(BufferedImage level)
	{
		
		int width = level.getWidth();
		int height = level.getHeight();
		
		for(int y = 0 ; y < width ; y++)
		{
			for(int x = 0 ; x < height ; x++)
			{
				int pixel = level.getRGB(x, y);
				
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel ) & 0xff;
				
				if(red == 0 && green == 0 && blue == 0)
					addTile(new Wall(x*64, y*64, 64, 64, Type.WALL, true , this));
			}
		}
	}
	
	public void addEntity(Entity en)	{	entity.add(en); }
	public void addTile(Tile t)	{ tile.add(t); }
	public void removeEntity(Entity en) { entity.remove(en); }
	public void removeTile(Tile t){ tile.remove(t); }
	public ArrayList<Entity> getEntity() { return entity; }
	public ArrayList<Tile>	getTile() { return tile; }
}
