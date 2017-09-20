package SuperClass;

import Client.Client;
import processing.core.PApplet;

public class Missile extends Skill{
	
	//An attack can follow enemy
	public Character en;
	
	public Missile(int x, int y, int width, int height, Type type, boolean solid, Handler handler) {
		super(x, y, width, height, type, solid, handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void display(PApplet parent) {
		// TODO Auto-generated method stub
		if(used == true){
			parent.image(Client.traceAttack[uniAttack][face], this.getX(), this.getY());
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(used == true)
		{
			delay++;
			if(delay>20){
				frame++;
				delay=0;
				
				setVelX((en.getX()-getX())/15);
				setVelY((en.getY()-getY())/15);
				setX(getX()+getVelX());
				setY(getY()+getVelY());
				if(frame>50)
				{
					frame=0;
					// Skill done, back to origin x , y
					this.die();
				}
			}
		}
		

	}

}
