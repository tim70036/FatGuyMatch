package SuperClass;

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
			parent.fill(12,63,45);
			parent.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(used == true)
		{
			delay++;
			if(delay>100){
				frame++;
				delay=0;
				
				setVelX((en.getX()-getX())/10);
				setVelY((en.getY()-getY())/10);
				setX(getX()+getVelX());
				setY(getY()+getVelY());
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
			playerID = -1;
			setVelX(0);
			setVelY(0);
			setX(100);
			setY(0);
		}
	}

}
