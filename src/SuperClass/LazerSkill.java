package SuperClass;

import processing.core.PApplet;

public class LazerSkill extends Skill {
	
	private Entity target;

	public LazerSkill(int x, int y, int width, int height, Type type, boolean solid, Handler handler) 
	{
		super(x, y, width, height, type, solid, handler);
		
	}

	public void display(PApplet parent) 
	{
		parent.strokeWeight(5);
		parent.fill(255,127,80);
		parent.line(getX(), getY(), target.getX(), target.getY());
		parent.strokeWeight(1);
	}

	public void update()
	{
		
	}

	public void setTarget(Entity t){	this.target = t;	}
	public Entity getTarget(){	return this.target;	}
}
