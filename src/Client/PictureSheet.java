package Client;

import processing.core.PImage;

public class PictureSheet {
	private PImage img;
	
	public PictureSheet(PImage pic)
	{
		try
		{
			this.img = pic;
		}catch(Exception e){e.printStackTrace();}
	}
	
	public PImage getPicture(int x,int y ){
		return img.get(x*64,y*64 , 64, 64);
	}
}
