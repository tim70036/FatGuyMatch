package SuperClass;

import Client.Client;
import processing.core.PApplet;

public class FireSkill extends Skill{
	
	public FireSkill(int x, int y, int width, int height, Type type, boolean solid, Handler handler) {
		super(x, y, width, height, type, solid, handler);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void display(PApplet parent) {
		// TODO Auto-generated method stub
		if(used == true)
		{
			parent.image(Client.normalAttack[uniAttack][face], this.getX(), this.getY());
		}
	}

	@Override
	public void update() {
		
		//Being used, fire or not
		if(used == true)
		{
			delay++;
			
			if(delay>8)
			{
				frame++;
				delay=0;
				if(face==0)	setVelX(20);
				else setVelX(-20);
				setX(getX()+getVelX());
			}
		
			if(frame>40)
			{
				frame=0;
				this.die();
			}
		}
		
	}
	
}
