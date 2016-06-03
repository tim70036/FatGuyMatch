package SuperClass;

import processing.core.PApplet;
import Client.*;

public class Shit extends Skill {
	
	public ShitType shitType;
	
	private float alpha = 20f;

	public Shit(int x, int y, int width, int height, Type type, boolean solid, Handler handler, ShitType shit) {
		super(x, y, width, height, type, solid, handler);
		this.shitType = shit;
	}

	public void display(PApplet parent) 
	{
		if(used == true)
		{
			parent.image(Client.shitImage, getX(), getY());
		}
	}

	@Override
	public void update() 
	{
		if(used == true)
		{
			alpha = (alpha + 3f > 255f) ? 255f : alpha + 3f; // If alpha > 255 ----> error
			
			
			
			// Stop ? 
			if(alpha >= 255f)
			{
				//this.die();
			}
			else
			{
				this.setVelY(-1.5f);
				this.setY(getY() + getVelY());
			}
		}
	}
	
	public void setAlpha(float f){ alpha = f; }
}
