package SuperClass;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;

public class Pipe extends Entity {
	public boolean open_close=false;
	PImage img;
	public static int[] doorX = new int[12]; 
	public static int[] doorY = new int[12];
	private Random random = new Random();
	private int index;
	public static boolean[] Valid = new boolean[12];
	public Pipe(int x, int y, int width, int height, Type type, boolean solid, Handler handler,boolean state,int index) {
		super(x, y, width, height, type, solid, handler);
		// TODO Auto-generated constructor stub
		this.open_close=state;
		this.index=index;
	}
	public static void initPlace(int x ,int y , int index){
		doorX[index] = x;
		doorY[index] = y;
		Valid[index] = false;
	}

	@Override
	public void display(PApplet parent) {
		// TODO Auto-generated method stub
		img = parent.loadImage("door.png");
		img.resize(300,300);
		parent.image(img, getX(), getY(),getWidth(),getHeight());
		
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
}
