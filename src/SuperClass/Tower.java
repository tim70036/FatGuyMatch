package SuperClass;

import java.awt.Rectangle;

import processing.core.PApplet;

public class Tower extends Entity {

	Character target;
	long lastTime ;
	long nowTime;
	
	public Tower(int x, int y, int width, int height, Type type, boolean solid, Handler handler) {
		super(x, y, width, height, type, solid, handler);
	}

	public void display(PApplet parent) 
	{
		parent.fill(192,192,192);
		parent.rect(getX(), getY(), getWidth(), getHeight());
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
								lazer.setVelX( (e.getX() - this.getX()) / 15 );
								lazer.setVelY( (e.getY() - this.getY()) / 15 );
								lastTime = nowTime;
								break;
							}
						}
					}
					}
					
//					if(lazer != null)
//					{
//						
//						lazer = null;
//						System.out.println("shoot");
//					}
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
