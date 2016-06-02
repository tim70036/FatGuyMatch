package SuperClass;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;

public class Tower extends Entity {

	Character target;
	long lastTime ;
	long nowTime;
	PImage img;
	private Random random = new Random();
	private int index;
	//remember the place that tower can revival
	public static int[] placeX = new int[20]; 
	public static int[] placeY = new int[20];
	public static boolean[] isValid = new boolean[20];
	
	
	
	public Tower(int x, int y, int width, int height, Type type, boolean solid, Handler handler,int index) {
		super(x, y, width, height, type, solid, handler);
		life = 5000;
		//which place it is located
		this.index = index;
	}
	// To initial place in the handler
	public static void initPlace(int x ,int y , int index){
		placeX[index] = x;
		placeY[index] = y;
		isValid[index] = false;
	}
	
	public void display(PApplet parent) 
	{
		/*parent.fill(192,192,192);
		parent.rect(getX(), getY(), getWidth(), getHeight());*/
		img = parent.loadImage("tower.png");
		parent.image(img, getX(), getY(),getWidth(),getHeight());
		parent.fill(123,15,3);
		parent.rect(this.getX(),this.getY()-20,this.getWidth(),20);
		parent.fill(13,155,23);
		parent.rect(this.getX(),this.getY()-20,(life*this.getWidth()/5000),20);
		
	}
	
	
	public void update() 
	{
		// Character Detection
		for(Entity e : this.getHandler().getEntity())
		{
			if(e.getType() == Type.CHARACTER)
			{
				// If in the area of Tower
				if(this.getBound().intersects(e.getBound()))
				{
					nowTime = System.nanoTime();
					// Emit a unused Lazer to it
					if(nowTime - lastTime > (long)10e8 * 2) // Fire Rate
					{
						for(Skill s : this.getHandler().getSkill())
						{
							if(s.getType() == Type.LAZERSKILL && s.used == false)
							{
								LazerSkill lazer = (LazerSkill) s;
								lazer.used = true;
								lazer.setX(this.getX());
								lazer.setY(this.getY());
								lazer.setVelX( (e.getX() - this.getX()) / 5 );
								lazer.setVelY( (e.getY() - this.getY()) / 5 );
								lastTime = nowTime;
								break;
							}
						}
					}
				}
					
//					if(lazer != null){lazer = null;System.out.println("shoot");}
			}
			
			for(Skill s : this.getHandler().getSkill())
			{
				if(s.getType() == Type.FIRESKILL )
				{
					///need to reset the bound size
					if(super.getBound().intersects(s.getBound()))
					{
						s.used = false;
						this.life -= 100;
					}
				}
			}
			// Tower die and random revival
			if(life<0){
				isValid[index] = false;
				int tag;
				do
				{
					tag = random.nextInt(20);
					this.setX(placeX[tag]);
					this.setY(placeY[tag]);
				}while(isValid[tag]==false);
				this.life = 5000;
			}
		}
	}
		
		// Remove the skill, if it's target is out of Tower's area
//		if(lazer != null && target != null)
//		{
//			if(this.getBound().intersects(target.getBound()) == false)
//			{
//				lazer.used = false;
//				lazer = null;
//				target = null;
//			}
//		}
	
	
	// Override, Bigger bound
	public Rectangle getBound()
	{
		return new Rectangle((int)getX() -200 , (int)getY() -200 , getWidth() + 400, getHeight() + 400);
	}

}
