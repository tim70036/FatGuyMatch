package Client;
import processing.core.PImage;

public class Picture{
	private PImage img;
	public Picture(PImage Image) {
		img = Image;
	}
	public PImage getImage(){
		return  this.img;
	}
	public void reSize(int w,int h){
		this.img.resize(w, h);
	}
	
}
