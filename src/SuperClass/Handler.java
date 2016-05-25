package SuperClass;

import java.util.ArrayList;

import processing.core.PApplet;

public class Handler {
	private ArrayList<Entity>	entity;
	private ArrayList<Tile> tile;
	
	public Handler()
	{
		entity = new ArrayList<Entity>();
		tile = new ArrayList<Tile>();
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
	
	public void addEntity(Entity en)	{	entity.add(en); }
	public void addTile(Tile t)	{ tile.add(t); }
	public void removeEntity(Entity en) { entity.remove(en); }
	public void removeTile(Tile t){ tile.remove(t); }
	public ArrayList<Entity> getEntity() { return entity; }
	public ArrayList<Tile>	getTile() { return tile; }
}
