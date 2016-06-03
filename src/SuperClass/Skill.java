package SuperClass;

import java.awt.Rectangle;

import processing.core.PApplet;

public abstract class Skill {

	private float x,y;
	private int width, height;
	private float velX, velY;
	private Type type;
	private boolean solid;
	public boolean jumping = false;
	public boolean falling = true;
	private Handler handler;
	
	public int playerID = -1;
	
	public double gravity = 0.0;
	
	//animate
	public int frame,delay,frameNum;
	public boolean used = false;
	public int face;
	
	public int life;
	
	public Skill(int x, int y, int width, int height,Type type, boolean solid, Handler handler)
	{
		this.x = x; this.y = y; this.width = width; this.height = height;
		this.type = type; this.solid = solid; this.handler = handler;
		frame = 0; delay = 0; used = false; face = 0;
		life = 500;
	}
	
	public abstract void display(PApplet parent);
	
	public abstract void update();
	
	public void die()
	{
		this.used = false;
		this.setX(-100);
		this.setY(0);
		this.playerID = -1;
	}
	
	// For Collision Detection
	public Rectangle getBound()
	{
		return new Rectangle((int)x , (int)y , width , height);
	}
	
	public Rectangle getBoundTop()
	{
		return new Rectangle((int)x + 10 , (int)y , width - 20 , 5);
	}
	
	public Rectangle getBoundBottom()
	{
		return new Rectangle((int)x + 10 , (int)y + height - 5 , width - 20 , 5);
	}
	
	public Rectangle getBoundLeft()
	{
		return new Rectangle((int)x , (int)y + 10, 5 , height - 20);
	}
	
	public Rectangle getBoundRight()
	{
		return new Rectangle((int)x + width - 5 , (int)y + 10, 5 , height - 20);
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
	public int getFrame(){ return this.frame;}
	public void setFrame(int frame){ this.frame = frame;}
}
