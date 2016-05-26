package SuperClass;

import java.awt.Rectangle;

import processing.core.PApplet;

public abstract class Tile {
	private int x,y;
	private int width, height;
	private int velX, velY;
	private Type type;
	private boolean solid;
	private Handler handler;
	
	public Tile(int x, int y, int width, int height,Type type, boolean solid, Handler handler)
	{
		this.x = x; this.y = y; this.width = width; this.height = height;
		this.type = type; this.solid = solid; this.handler = handler;
	}
	
	public abstract void display(PApplet parent);
	
	public abstract void update();
	
	public void die()
	{
		handler.removeTile(this);
	}
	
	// For Collision Detection
	public Rectangle getBound()
	{
		return new Rectangle((int)x , (int)y , width , height);
	}
	
	public int getX() {return x;}
	public void setX(int x) {this.x = x;}
	public int getY() {return y;}
	public void setY(int y) {this.y = y;}
	public int getWidth() {return width;}
	public void setWidth(int width) {this.width = width;}
	public int getHeight() {return height;}
	public void setHeight(int height) {this.height = height;}
	public int getVelX() {return velX;}
	public void setVelX(int velX) {this.velX = velX;}
	public int getVelY() {return velY;}
	public void setVelY(int velY) {this.velY = velY;}
	public Type getType() {return type;}
	public void setType(Type type) {this.type = type;}
	public boolean isSolid() {return solid;}
	public void setSolid(boolean solid) {this.solid = solid;}
	public Handler getHandler() {return handler;}
	public void setHandler(Handler handler) {this.handler = handler;}
}
