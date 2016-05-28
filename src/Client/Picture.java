package Client;
import processing.core.PImage;

public class Picture{
	private PictureSheet sheet;
	private PImage img;
	public Picture(PictureSheet sheet,int x,int y) {
		img = sheet.getPicture(x, y);
	}
	public PImage getImage(){
		return  this.img;
	}
	public void reSize(int w,int h){
		this.img.resize(w, h);
	}
	
}
