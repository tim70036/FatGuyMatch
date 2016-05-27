package Client;

import SuperClass.Entity;

public class Camera {
	
	public float x,y;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void tick(Entity player){
		setX(- player.getX()+Client.width/2 );
		setY( - player.getY()+Client.height/2);
	}

	
	
}
