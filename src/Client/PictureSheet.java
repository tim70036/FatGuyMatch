package Client;

import processing.core.PImage;

public class PictureSheet {
	private PImage img;
	
	public PictureSheet(PImage pic){
		try{
			this.img = pic;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public PImage getPicture(int x,int y ,int r){
		return img.get(x*r,y*r , r, r);
	}
}
