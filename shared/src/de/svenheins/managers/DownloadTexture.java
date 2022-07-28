package de.svenheins.managers;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadTexture {
	private String downloadTextureName = "";
	private int bytesOfDownloadTexture = 0;
	private int numberOfPacketsDownloadTexture=0;
	
	private ArrayList<byte[]> downloadTexture = new ArrayList<byte[]>();
	
	private ArrayList<String> textureDownloadList = new ArrayList<String>();

	/** constructor */
	public DownloadTexture() {
		downloadTexture = new ArrayList<byte[]>();
		textureDownloadList = new ArrayList<String>();
		downloadTextureName = "";
		bytesOfDownloadTexture = 0;
		numberOfPacketsDownloadTexture=0;
	}
	
	public void addPartOfTexture (byte[] texturePart){
		this.downloadTexture.add(texturePart);
	}
	
	public byte[] getPacket(int index) {
		return this.downloadTexture.get(index);
	}
	
	public int getSize() {
		return this.downloadTexture.size();
	}
	
	public String getDownloadTextureName() {
		return downloadTextureName;
	}

	public void setDownloadTextureName(String downloadTextureName) {
		this.downloadTextureName = downloadTextureName;
	}

	public int getBytesOfDownloadTexture() {
		return bytesOfDownloadTexture;
	}

	public void setBytesOfDownloadTexture(int bytesOfDownloadTexture) {
		this.bytesOfDownloadTexture = bytesOfDownloadTexture;
	}

	public int getNumberOfPacketsDownloadTexture() {
		return numberOfPacketsDownloadTexture;
	}

	public void setNumberOfPacketsDownloadTexture(int numberOfPacketsDownloadTexture) {
		this.numberOfPacketsDownloadTexture = numberOfPacketsDownloadTexture;
	}

	public ArrayList<byte[]> getDownloadTexture() {
		return downloadTexture;
	}

	public void setDownloadTexture(ArrayList<byte[]> downloadTexture) {
		this.downloadTexture = downloadTexture;
	}

	public ArrayList<String> getTextureDownloadList() {
		return textureDownloadList;
	}

	public void setTextureDownloadList(ArrayList<String> textureDownloadList) {
		this.textureDownloadList = textureDownloadList;
	}
	
	
	
	
}
