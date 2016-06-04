package SuperClass;

import java.util.Random;

import Client.*;
import processing.core.PApplet;

public class Character extends Entity
{
	
	public int playerID;
	public int characterID;
	public long dieTime = 0;
	public long lastTime = 0,lastTime_dura = 0;
	private Random rand = new Random();
	
	// Trail effect
	public boolean inTrail = false;
	
	public Character(int x, int y, int width, int height, Type type, boolean solid, Handler handler, int ID , int characterID)
	{
		super(x, y, width, height, type, solid, handler);
		frame = 0;
		delay = 0;
		frameNum = 5-1;
		playerID = ID;
		this.characterID = characterID;
	}

	public void display(PApplet parent) 
	{

		//draw character
		parent.fill(0);
		parent.image(Client.player[characterID][frame].getImage(), this.getX(), this.getY());
		
		//draw life bar
		parent.fill(123,15,3);
		parent.rect(this.getX(),this.getY()-20,this.getWidth(),10);
		parent.fill(13,155,23);
		parent.rect(this.getX(),this.getY()-20,(life*this.getWidth()/500),10);
		//draw name
		parent.fill(0, 0, 0);
		parent.textSize(16);
		parent.text("Player "+this.playerID, getX() + getWidth() * 0.3f, getY()-25);
	
	}

	public void update() 
	{	
		if(life>0)
		{
			// Trail effect
			if(inTrail)
			{
				long durationTime = System.nanoTime();
				long nowTime = System.nanoTime();
				if(nowTime - lastTime > 5 * 10e6)
				{
					lastTime = nowTime;
					
					// Find an unuse Trail
					Trail trail = null;
					for(Trail t: this.getHandler().getTrail())
					{
						if(t.used == false)
						{
							trail = t;
							break;
						}
					}
					
					
					// Show Trail
					if(trail != null)
					{
						trail.setX(this.getX());
						trail.setY(this.getY());
						trail.setAlpha(255f);
						trail.setCharacterID(this.characterID);
						trail.setFrame(this.frame); // Now Image's frame
						trail.used = true;
					}
				}
				//duration time 
				if(durationTime - lastTime_dura > 10e10){
					durationTime = 0;
					lastTime_dura = 0;
					this.inTrail = false;
				}
			}
			
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
				}
			}
			
			// Skill
			for(Skill s : this.getHandler().getSkill())
			{
				if(s.used == true)
				{
					if(s.getType() == Type.FIRESKILL && s.playerID != this.playerID)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							s.die();
							this.life -= 50;
						}
					}
					else if(s.getType() == Type.LAZERSKILL)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							s.die();
							this.life -= 50;
						}
					}
					else if(s.getType() == Type.MISSILE && s.playerID != this.playerID)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							s.die();
							this.life -= 50;
						}
					}
					else if(s.getType() == Type.DARKSKILL)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							s.die();
							this.life = (this.life - 500 < 0) ? 0 : this.life - 500;
						}
					}
					
					else if(s.getType() == Type.THUNDERSKILL)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							s.die();
							this.life = (this.life - 2000 < 0) ? 0 : this.life - 2000;
						}
					}
					else if(s.getType() == Type.SHIT)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							switch (rand.nextInt(2))
							{
							case 0:
								this.inTrail = true;
								this.lastTime_dura = System.nanoTime();
								break;
							case 1:
								this.life = 0;
								break;
							}
							s.die();
						}	
					}
				}
			}
	
			// Jumping Falling
			if(jumping)
			{
				gravity-=0.0008;
				setVelY((float)-gravity);
				if(gravity<=0.90)
				{
					jumping = false;
					falling = true;
				}
			}
			if(falling)
			{
				gravity+=0.003;
				setVelY((float)gravity);			
			}
			
			///------animate--------------
			if(move==true){
				delay++;
				if(delay>20){
					if(face==0){
						if(frame==frameNum||frame>=5)frame=1;
						else frame++;
					}
					else if(face==1){
						if(frame==frameNum+5||frame<5)frame=6;
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
		// Died
		else
		{
			if(dieTime==0)dieTime = System.nanoTime();
			else if( System.nanoTime() - dieTime  > (long)10e7*5){
				// Start to die
				if(frame<10)
				{
					frame=10;
					dieTime = 0;
				}
				// Died ---> put shit
				else if(frame==14)
				{
					frame = 0;
					life = 500;
					dieTime = 0;
					
					// Put an unsused shit and put on it
					Shit.putShit(this.getX()+(this.getWidth()*0.4f) , this.getY()+(this.getHeight()*0.8f));
					
					// Reset Effect
					inTrail = false;
					
					// Revive
					setX(200);
					setY(0);
				}
				// Dying
				else
				{
					dieTime = 0;
					frame++;
				}
			}
		}
	}
}
