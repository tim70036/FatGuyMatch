package SuperClass;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import Client.ClientMain;
import processing.core.PApplet;
import processing.core.PImage;

public class Handler {
	private ArrayList<Character>	character;
	private ArrayList<Entity>	entity;
	private ArrayList<Tile> 	tile;
	private ArrayList<Skill>	skill;
	private ArrayList<Trail>	trail;
	private Random random = new Random();
	
	public Handler()
	{
		character = new ArrayList<Character>();
		entity = new ArrayList<Entity>();
		tile = new ArrayList<Tile>();
		skill = new ArrayList<Skill>();
		trail = new ArrayList<Trail>();
	}
	
	public void display(PApplet parent)
	{
		for(Character ch : character)
			ch.display(parent);
		for(Entity en : entity)
			en.display(parent);
		for(Tile t : tile)
			t.display(parent);
		for(Skill s : skill)
			s.display(parent);
		for(Trail t : trail)
			t.display(parent);
	}
	
	public void update()
	{
		for(Character ch : character)
			ch.update();
		for(Entity en : entity)
			en.update();
		for(Tile t : tile)
			t.update();
		for(Skill s : skill)
			s.update();
		for(Trail t : trail)
			t.update();
	}
	
	public void createLevel(BufferedImage level)
	{
		
		int width = level.getWidth();
		int height = level.getHeight();
		
		int index = 0;
		int index2=0;
		for(int y = 0 ; y < width ; y++)
		{
			for(int x = 0 ; x < height ; x++)
			{
				int pixel = level.getRGB(x, y);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel ) & 0xff;
				if(red==100&&green==50&&blue==0){
					Pipe.initPlace(x*32, y*32-300+31 , index2);
					index2++;
				}
				if(red == 0 && green == 0 && blue == 0)
					addTile(new Wall(x*32, y*32, 32, 32, Type.WALL, true , this));
				if(red == 0 && green == 0 && blue == 255){
					Tower.initPlace(x*32, y*32-303+31 , index);
					index++;
				
					//addEntity(new Tower(x*32,y*32-303+31,303,303,Type.TOWER,true,this));
				}
			}
		}
		//Random put Tower at first
		for(int x=0 ; x < 3; x++){
			int y = random.nextInt(20);
		
			if(Tower.isValid[y]==false){
				addEntity(new Tower(Tower.placeX[y],Tower.placeY[y],303,303,Type.TOWER,true,this,y));
				Tower.isValid[y] = true;
			}
			else 
				x--;
		}
		for(int x=0 ; x < 4; x++){
			int dy = random.nextInt(index2);
			if(Pipe.Valid[dy]==false){
				addEntity(new Pipe(Pipe.doorX[dy],Pipe.doorY[dy],300,300,Type.PIPE,true,this,false,dy));
				Pipe.Valid[dy] = true;
			}
			else 
				x--;
		}
	}
	
	public void addCharacter(Character ch)	{ character.add(ch); }
	public void addEntity(Entity en)	{	entity.add(en); }
	public void addTile(Tile t)	{ tile.add(t); }
	public void addSkill(Skill s){	skill.add(s); }
	public void addTrail(Trail t){	trail.add(t); }
	
	public void removeEntity(Entity en) { entity.remove(en); }
	public void removeTile(Tile t){ tile.remove(t); }
	public void removeSkill(Skill s){ skill.remove(s); }
	public void removeTrail(Trail t){ trail.remove(t); }
	
	public ArrayList<Character> getCharacter(){return character; } 
	public ArrayList<Entity> getEntity() { return entity; }
	public ArrayList<Tile>	getTile() { return tile; }
	public ArrayList<Skill>	getSkill() {return skill;}
	public ArrayList<Trail>	getTrail()	{	return trail; }
}
