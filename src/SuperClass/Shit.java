package SuperClass;

import processing.core.PApplet;
import Client.*;

public class Shit extends Skill {
	
	public ShitType shitType;
	
	private int offset;
	
	private float alpha = 20f;

	public Shit(int x, int y, int width, int height, Type type, boolean solid, Handler handler, ShitType shit) {
		super(x, y, width, height, type, solid, handler);
		this.shitType = shit;
	}

	public void display(PApplet parent) 
	{
		if(used == true)
		{
			parent.tint(255,alpha);
			parent.image(Client.shitImage, getX(), getY());
			parent.noTint();
		}
	}

	@Override
	public void update() 
	{
		if(used == true)
		{
			alpha = (alpha + 3f >= 255f) ? 255f : alpha + 3f; // If alpha > 255 ----> error
			
			
			
			// Stop ? 
			if(alpha >= 255f)
			{
//				delay++;
//				if(delay > 80)
//				{
//					
//					this.setY(getY() + getVelY());
//					offset += getVelY();
//					
//					if(offset > 50)
//					{
//						setVelY(-1.5f);
//					}
//					else if(offset < 0)
//					{
//						setVelY(1.5f);
//					}
//					
//				}
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
