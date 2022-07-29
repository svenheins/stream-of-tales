package de.svenheins.managers;

import java.util.ArrayList;

public class UploadTexture {
	private String uploadTextureName = "";
	private int bytesOfUploadTexture = 0;
	private int numberOfPacketsUploadTexture=0;
	
	private ArrayList<byte[]> uploadTexture = new ArrayList<byte[]>();
	
	private ArrayList<String> textureUploadList = new ArrayList<String>();

	/** constructor */
	public UploadTexture() {
		uploadTexture = new ArrayList<byte[]>();
		textureUploadList = new ArrayList<String>();
		uploadTextureName = "";
		bytesOfUploadTexture = 0;
		numberOfPacketsUploadTexture=0;
	}
	
	public void addPartOfTexture (byte[] texturePart){
		this.uploadTexture.add(texturePart);
	}
	
	public byte[] getPacket(int index) {
		return this.uploadTexture.get(index);
	}
	
	public int getSize() {
		return this.uploadTexture.size();
	}
	
	public String getUploadTextureName() {
		return uploadTextureName;
	}

	public void setUploadTextureName(String uploadTextureName) {
		this.uploadTextureName = uploadTextureName;
	}

	public int getBytesOfUploadTexture() {
		return bytesOfUploadTexture;
	}

	public void setBytesOfUploadTexture(int bytesOfUploadTexture) {
		this.bytesOfUploadTexture = bytesOfUploadTexture;
	}

	public int getNumberOfPacketsUploadTexture() {
		return numberOfPacketsUploadTexture;
	}

	public void setNumberOfPacketsUploadTexture(int numberOfPacketsUploadTexture) {
		this.numberOfPacketsUploadTexture = numberOfPacketsUploadTexture;
	}

	public ArrayList<byte[]> getUploadTexture() {
		return uploadTexture;
	}

	public void setUploadTexture(ArrayList<byte[]> uploadTexture) {
		this.uploadTexture = uploadTexture;
	}

	public ArrayList<String> getTextureUploadList() {
		return textureUploadList;
	}

	public void setTextureUploadList(ArrayList<String> textureUploadList) {
		this.textureUploadList = textureUploadList;
	}
	
	
	
	
}
