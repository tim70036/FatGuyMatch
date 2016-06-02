package SuperClass;

import java.util.Random;

import Client.*;
import processing.core.PApplet;

public class Character extends Entity
{
	
	public int playerID;
	public long dieTime = 0;
	private Random rand=new Random();
	public Character(int x, int y, int width, int height, Type type, boolean solid, Handler handler, int ID)
	{
		super(x, y, width, height, type, solid, handler);
		frame = 0;
		delay = 0;
		frameNum = 5-1;
		playerID = ID;
	}

	public void display(PApplet parent) 
	{

		//draw character
		parent.fill(0);
		parent.image(Client.player[frame].getImage(), this.getX(), this.getY());
		
		//draw life bar
		parent.fill(123,15,3);
		parent.rect(this.getX(),this.getY()-20,this.getWidth(),10);
		parent.fill(13,155,23);
		parent.rect(this.getX(),this.getY()-20,(life*this.getWidth()/500),10);
	
	}

	public void update() 
	{
		if(life>0){
			setX(getX() + getVelX());
			setY(getY() + getVelY());
			
			if(getX() <= 0)	setX(0);
			if(getY() <= 0)	
			{
				if(jumping)
				{
					jumping = false;
					setVelY(0);
					gravity = 0.0;
					falling = true;
				}
			}
			/*if(getX() + getWidth() >= ClientMain.windowWidth) 		setX(getX() + getVelX());
			if(getY() + getHeight() >= ClientMain.windowHeight)
			{
				setY(0);
			    if(falling) falling = false;
			}*/
			
	
			// Collision Detection
			for(Tile t : this.getHandler().getTile())
			{
				if(t.isSolid() == false)	continue;
				
				if(t.getType() == Type.WALL)
				{
					if(this.getBoundTop().intersects(t.getBound())) // Character's top collide
					{
						this.setVelY(0);
						/*this.setY(t.getY() + t.getHeight()); // Right on the bottom of t*/
						if(jumping)
						{
							jumping = false;
							setVelY(0);
							gravity = 0.0;
							falling = true;
						}
					}
					
					if(this.getBoundBottom().intersects(t.getBound())) // Character's bottom collide
					{
						this.setVelY(0);
						if(falling) 
							falling = false;
						
						this.setY(t.getY() - this.getHeight()); // Right On the top of t*/
					}
					else // Fall down if get out of platform ---> Bottom not collide
					{
					    if(!falling && !jumping) 
						{
							gravity = 0.0;
							falling = true;
						}
					}
					
					if(this.getBoundLeft().intersects(t.getBound())) // Character's left collide
					{
						this.setVelX(0);
						this.setX(t.getX() + t.getWidth()); // Right on the right of t
					}
					
					if(this.getBoundRight().intersects(t.getBound())) // Character's right collide
					{
						this.setVelX(0);
						this.setX(t.getX() - this.getWidth()); // Right on the left of t
					}
				}
			}
			for(Entity e : this.getHandler().getEntity())
			{
				if(e.isSolid() == false)	continue;
				
				if(e.getType() == Type.PIPE)
				{
					if(this.getBoundTop().intersects(e.getBound())) // Character's top collide
					{
						this.setVelY(0);
						/*this.setY(t.getY() + t.getHeight()); // Right on the bottom of t*/
						if(jumping)
						{
							jumping =false;
							this.setX(rand.nextInt(3010));
							this.setY(rand.nextInt(3010));
							gravity = 0.0;
							falling = true;
						}
					}
					
					/*if(this.getBoundBottom().intersects(e.getBound())) // Character's bottom collide
					{
						this.setVelY(0);
						if(falling) 
							falling = false;
						
						this.setY(e.getY() - this.getHeight()); // Right On the top of t
					}
					else // Fall down if get out of platform ---> Bottom not collide
					{
					    if(!falling && !jumping) 
						{
							gravity = 0.0;
							falling = true;
						}
					}*/
					
					/*if(this.getBoundLeft().intersects(e.getBound())) // Character's left collide
					{
						this.setVelX(0);
						this.setX(e.getX() + e.getWidth()); // Right on the right of t
					}
					
					if(this.getBoundRight().intersects(e.getBound())) // Character's right collide
					{
						this.setVelX(0);
						this.setX(e.getX() - this.getWidth()); // Right on the left of t
					}*/
				}
			}
			
			// Skill
			for(Skill s : this.getHandler().getSkill())
			{
				if(s.getType() == Type.FIRESKILL && s.playerID != this.playerID)
				{
					if(this.getBound().intersects(s.getBound()))
					{
						s.used = false;
						this.life -= 50;
					}
				}
				else if(s.getType() == Type.LAZERSKILL && s.playerID != this.playerID)
				{
					if(this.getBound().intersects(s.getBound()))
					{
						s.used = false;
						this.life -= 50;
					}
				}
				else if(s.getType() == Type.MISSILE && s.playerID != this.playerID)
				{
					if(this.getBound().intersects(s.getBound()))
					{
						s.used = false;
						this.life -= 50;
					}
				}
			}
	
			// Jumping Falling
			if(jumping)
			{
				gravity-=0.0002;
				setVelY((float)-gravity);
				if(gravity<=0.94)
				{
					jumping = false;
					falling = true;
				}
			}
			if(falling)
			{
				gravity+=0.0015;
				setVelY((float)gravity);			
			}
			
			///------animate--------------
			if(move==true){
				delay++;
				if(delay>20){
					if(face==0){
						if(frame==frameNum||frame>=5)frame=0;
						else if(frame>=5)frame=0;
						else frame++;
					}
					else if(face==1){
						if(frame==frameNum+5||frame<5)frame=5;
						else frame++;
					}
					delay = 0;
				}
			}
			else{
				delay = 0;
				frame = 0;
			}
		}
		else{
			if(dieTime==0)dieTime = System.nanoTime();
			else if( System.nanoTime() - dieTime  > (long)10e7*5){
				if(frame<10){
					frame=10;
					dieTime = 0;
				}
				else if(frame==14){
					frame = 0;
					life = 500;
					dieTime = 0;
					setX(200);
					setY(0);
				}
				else{
					dieTime = 0;
					frame++;
				}
			}
		}
		//////
	}
}
