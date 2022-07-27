package de.svenheins.objects;

 import java.awt.Image;

public class Sprite {

	private Image img;
	
	public Sprite(Image img){
		this.img = img;
	}

	public Image getImage(){
		return img;
	}
	
	public void setImg(Image img) {
		this.img = img;
	}
	
	public int getWidth() {
		return img.getWidth(null);
	}
	
	public int getHeight() {
		return img.getHeight(null);
	}
}
