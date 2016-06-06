package SuperClass;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import Client.Client;
import processing.core.PApplet;
import processing.core.PImage;

public class Boss extends Entity {

	Character target;
	public int lastAttackPlayerID = -1;
	
	long lastTime ;
	long nowTime;
	PImage img;
	private Random random = new Random();
	public int skill_type;
	//remember the place that tower can revival
	//public static int[] placeX = new int[20]; 
	//public static int[] placeY = new int[20];
	public static boolean[] isValid = new boolean[20];
	
	public boolean isAiming = false;
	
	public static int characterKill = 0;
	
	public int spell;
	
	public Boss(int x, int y, int width, int height, Type type, boolean solid, Handler handler) 
	{
		super(x, y, width, height, type, solid, handler);
		life = 5000;
		//which place it is located
	}
	
	public void display(PApplet parent) 
	{
		parent.fill(0);
		parent.image(Client.boss[frame].getImage(), this.getX(), this.getY());
		parent.fill(123,15,3);
		parent.rect(this.getX(),this.getY()-20,this.getWidth(),20);
		parent.fill(13,155,23);
		parent.rect(this.getX(),this.getY()-20,(life*this.getWidth()/5000f),20);
		
	}
	
	
	public void update() 
	{
		// Character Detection
		if(life>0)
		{
			// Attack Character
			for(Character e : this.getHandler().getCharacter())
			{			
				// If in the area of Tower
				if(this.getBound().intersects(e.getBound()))
				{
					// Not aiming ---> get a character 
					if(isAiming == false)
					{
						isAiming = true;
						target = e;
					}
					
					// Emit a unused Lazer to it
					nowTime = System.nanoTime();
					if(nowTime - lastTime > (long)10e8 * 2) // Fire Rate
					{
						for(Skill s : this.getHandler().getSkill())
						{	
							if(s.getType() == Type.THUNDERSKILL && s.used == false)
							{
								Thunder thunder = (Thunder) s;
								if(e.getX()>this.getX())face=1;
								else face=0;
								
								thunder.used = true;
								thunder.playerID = -2;
								thunder.setX(this.getX());
								thunder.setY(this.getY());
								thunder.setVelX( (e.getX() - this.getX() - 50) / 15 );
								thunder.setVelY( (e.getY() - this.getY()) / 15 );
								
								lastTime = nowTime;
								break;
							}
						}
					}
				}
			}
			
			// Aiming
			if(isAiming)
			{
				// Animation
				delay++;
				if(delay>60)
				{
					if(frame == frameNum || frame>=5)
					{
						frame=1;
						spell=1;
					}
					else 
					{
						frame++;
						spell=0;
					}
					delay = 0;
				}
				
				// Animation is done ----> Launch Skill
				if(spell == 1)
				{
					for(Skill s : this.getHandler().getSkill())
					{	
						if(s.getType() == Type.DARKSKILL && s.used == false && spell==1)
						{
							Darkness dark = (Darkness) s;
							dark.used = true;
							dark.playerID = -2;
							dark.setX(this.getX());
							dark.setY(this.getY());
							dark.setVelX( (target.getX() - this.getX() - 50) / 15 );
							dark.setVelY( (target.getY() - this.getY()) / 15 );
							
							break;
						}
					}
					
					// Reset
					isAiming = false;
					spell = 0;
				}
			}
			
			// Player's Attack
			for(Skill s : this.getHandler().getSkill())
			{
				if(s.getType() == Type.FIRESKILL )
				{
					///need to reset the bound size
					if(super.getBound().intersects(s.getBound()))
					{
						
						
						// Is this player's attack valid ?
						if(this.getHandler().getCharacter().get(s.playerID).canAttackssBoss == true)
						{
							// Record who last attack boss
							this.lastAttackPlayerID = s.playerID;
							
							this.life -= 100;
						}
						s.die();
						
					}
				}
			}
		}
		// Boss died ----> Game over
		else 
		{
			Server.Server.isRunning = false;
		}
	}
		
	// Override, Bigger bound
	public Rectangle getBound()
	{
		return new Rectangle((int)getX() -200 , (int)getY() -200 , getWidth() + 400, getHeight() + 400);
	}

}
