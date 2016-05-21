package Client;

import java.awt.image.BufferedImage;

public class Character {
	
	private int x,y;
	private int which_role;

	public Character(int x,int y,int which_role){
		this.setX(x);
		this.setY(y);
		this.setWhich_role(which_role);
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public int getWhich_role() {
		return which_role;
	}

	public void setWhich_role(int which_role) {
		this.which_role = which_role;
	}
}
