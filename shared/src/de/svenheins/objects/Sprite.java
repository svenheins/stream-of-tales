/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
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
