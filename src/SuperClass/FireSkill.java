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
			parent.fill(12,63,45);
			parent.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
		}
		
	}

	@Override
	public void update() {
		
		//Being used, fire or not
		if(used == true)
		{
			delay++;
			if(delay>500){
				frame++;
				delay=0;
				setVelX(15);
				setX(getX()+getVelX());
				if(frame>50){
					frame=0;
					setX(100);
					setY(0);
					used = false;
				}
			}
		}
		// Skill done, back to origin x , y
		else
		{
			setVelX(0);
			setVelY(0);
			setX(100);
			setY(0);
		}
		
	}
	
}
