package SuperClass;

import java.awt.Rectangle;

import processing.core.PApplet;

public class Tower extends Entity {

	LazerSkill lazer;
	
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
			// If in the area of Tower
			if(this.getBound().intersects(e.getBound()))
			{
				// Emit Lazer to it
				lazer = new LazerSkill((int)getX() ,(int)getY() , 0 , 0 , Type.LAZERSKILL, false, this.getHandler());
				this.getHandler().addSkill(lazer);
				lazer.setTarget(e);
			}
		}
		
		// Remove the skill, if it's target is out of Tower's area
		if(lazer != null)
		{
			if(this.getBound().intersects(lazer.getTarget().getBound()) == false)
			{
				lazer.die();
				lazer = null;
			}
		}
	}
	
	// Override, Bigger bound
	public Rectangle getBound()
	{
		return new Rectangle((int)getX() , (int)getY() , getWidth() + 100, getHeight() + 100);
	}

}
