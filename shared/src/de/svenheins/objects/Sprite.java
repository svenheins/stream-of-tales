package de.svenheins.objects;

import java.awt.image.BufferedImage;

// import java.awt.Image;

public class Sprite {

	private BufferedImage img;
	
	public Sprite(BufferedImage img){
		this.img = img;
	}

	public BufferedImage getImage(){
		return img;
	}
	
	public void setImg(BufferedImage img) {
		this.img = img;
	}
	
	public int getWidth() {
		return img.getWidth(null);
	}
	
	public int getHeight() {
		return img.getHeight(null);
	}
}
