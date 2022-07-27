package de.svenheins.objects;

import java.io.Serializable;

import com.sun.sgs.app.ManagedObject;
 


public class ServerSprite implements Serializable, ManagedObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String strImg;
	private int height, width;
	
//	public Sprite(String strImg){
//		this.strImg = strImg;
//	}
	
	public ServerSprite(String strImg, int height, int width){
		this.strImg = strImg;
		this.height = height;
		this.width = width;
	}

	public String getStrImg(){
		return strImg;
	}
	
	public void setStrImg(String strImg) {
		this.strImg = strImg;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
