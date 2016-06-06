package SuperClass;

import java.util.Random;

import Client.*;
import processing.core.PApplet;

public class Character extends Entity
{
	
	public int playerID;
	public String playerName;
	public int characterID;
	public long dieTime = 0;
	
	private Random rand = new Random();
	
	// Shit
	public int shitNum = 8;
	
	// Trail effect
	public boolean inTrail = false;
	public long lastTime = 0;
	
	// Bigger effect
	public boolean canAttackssBoss = true;
	public int orgWidth, orgHeight;
	
	// Record
	public int characterKill;
	public int towerKill;
	public int diedNum;
	public int lastAttackID;
	
	public Character(int x, int y, int width, int height, Type type, boolean solid, Handler handler, int ID , int characterID, String name)
	{
		super(x, y, width, height, type, solid, handler);
		frame = 0;
		delay = 0;
		frameNum = 5-1;
		playerID = ID;
		playerName = name;
		this.characterID = characterID;
		
		orgWidth = width;
		orgHeight = height;
		this.life = 5000000;
	}

	public void display(PApplet parent) 
	{

		//draw character
		parent.fill(0);
		parent.image(Client.player[characterID][frame].getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
		
		//draw life bar
		parent.fill(123,15,3);
		parent.rect(this.getX(),this.getY()-20,this.getWidth(),10);
		parent.fill(13,155,23);
		parent.rect(this.getX(),this.getY()-20,(life*this.getWidth()/500),10);
		//draw name
		parent.fill(0, 0, 0);
		parent.textSize(16);
		parent.text(playerName, getX() + getWidth() * 0.3f, getY()-25);
	
	}

	public void update() 
	{	
		
		if(life>0)
		{
			// Bigger effect
//			if(canAttackssBoss)
//			{
//				this.setWidth(orgWidth * 2);
//				this.setHeight(orgHeight * 2);
//			}
//			else
//			{
//				this.setWidth(orgWidth);
//				this.setHeight(orgHeight);
//			}
			
			// Trail effect
			if(inTrail)
			{
				long nowTime = System.nanoTime();
				if(nowTime - lastTime > 10e6)
				{
					lastTime = nowTime;
					
					// Find an unuse Trail
					for(Trail t: this.getHandler().getTrail())
					{
						if(t.used == false)
						{
							// Show Trail
							t.setX(this.getX());
							t.setY(this.getY());
							t.setWidth(this.getWidth());
							t.setHeight(this.getHeight());
							t.setAlpha(255f);
							t.setCharacterID(this.characterID);
							t.setFrame(this.frame); // Now Image's frame
							t.used = true;
							break;
						}
					}
					
					
					
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
					gravity = 0.2;
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
							gravity = 0.2;
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
							this.lastAttackID = s.playerID;
							s.die();
							this.life -= 50;
						}
					}
					else if(s.getType() == Type.LAZERSKILL)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							this.lastAttackID = s.playerID;
							s.die();
							this.life -= 50;
						}
					}
					else if(s.getType() == Type.MISSILE && s.playerID != this.playerID)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							this.lastAttackID = s.playerID;
							s.die();
							this.life -= 50;
						}
					}
					else if(s.getType() == Type.DARKSKILL)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							this.lastAttackID = s.playerID;
							s.die();
							this.life = (this.life - 400 < 0) ? 0 : this.life - 400;
						}
					}
					
					else if(s.getType() == Type.THUNDERSKILL)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							this.lastAttackID = s.playerID;
							s.die();
							this.life = (this.life - 100 < 0) ? 0 : this.life - 100;
						}
					}
					else if(s.getType() == Type.SHIT)
					{
						if(this.getBound().intersects(s.getBound()))
						{
							this.shitNum += 1;
							
							int ran = rand.nextInt(100);
							
							if(ran <= 10)
							{
								this.life = 0;
							}
							else if(ran <= 50)
							{
								this.inTrail = true;
							}
							else
							{
								if(this.shitNum >= 10)	this.canAttackssBoss = true;
								else this.life = (this.life + 100) > 500 ? 500 : this.life + 500;
							}
							
							s.die();
						}	
					}
				}
			}
	
			// Jumping Falling
			if(jumping)
			{
				gravity -= 0.01;
				setVelY((float)-gravity);
				if(gravity <= 2.0)
				{
					jumping = false;
					gravity = 0.2;
					falling = true;
				}
			}
			if(falling)
			{
				gravity+=0.03;
				
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
					
					// Record score
					if(this.lastAttackID > -1)
					{
						this.getHandler().getCharacter().get(lastAttackID).characterKill += 1;
					}
					else if(this.lastAttackID == -1) // Tower kill
					{
						Tower.characterKill++;
					}
					else if(this.lastAttackID == -2) // Boss kill
					{
						Boss.characterKill++;
					}
					this.diedNum++;
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
					this.inTrail = false;
					this.canAttackssBoss = false;
					
					// Revive
					setX(rand.nextInt(3000));
					setY(rand.nextInt(3000));
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
