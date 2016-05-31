package SuperClass;

import Client.Client;
import processing.core.PApplet;

public class FireSkill extends Entity{

	public FireSkill(int x, int y, int width, int height, Type type, boolean solid, Handler handler) {
		super(x, y, width, height, type, solid, handler);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void display(PApplet parent) {
		// TODO Auto-generated method stub
		parent.fill(12,63,45);
		parent.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
		if(move==true){////fire or not
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
					move = false;
				}
			}
			else setVelX(0);
		}
		
	}
	
}
