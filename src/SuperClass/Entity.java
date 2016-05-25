package SuperClass;

import Server.Type;
import processing.core.PApplet;

public abstract class Entity {
	
	private float x,y;
	private int width, height;
	private float velX, velY;
	private Type type;
	private boolean solid;
	private Handler handler;
	
	public Entity(int x, int y, int width, int height,Type type, boolean solid, Handler handler)
	{
		this.x = x; this.y = y; this.width = width; this.height = height;
		this.type = type; this.solid = solid; this.handler = handler;
	}
	
	public abstract void display(PApplet parent);
	
	public abstract void update();
	
	public void die()
	{
		handler.removeEntity(this);
	}
	
	public float getX() {return x;}
	public void setX(float x) {this.x = x;}
	public float getY() {return y;}
	public void setY(float y) {this.y = y;}
	public int getWidth() {return width;}
	public void setWidth(int width) {this.width = width;}
	public int getHeight() {return height;}
	public void setHeight(int height) {this.height = height;}
	public float getVelX() {return velX;}
	public void setVelX(float velX) {this.velX = velX;}
	public float getVelY() {return velY;}
	public void setVelY(float velY) {this.velY = velY;}
	public Type getType() {return type;}
	public void setType(Type type) {this.type = type;}
	public boolean isSolid() {return solid;}
	public void setSolid(boolean solid) {this.solid = solid;}
	public Handler getHandler() {return handler;}
	public void setHandler(Handler handler) {this.handler = handler;}
}
