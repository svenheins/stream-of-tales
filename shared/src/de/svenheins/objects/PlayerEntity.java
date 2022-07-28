package de.svenheins.objects;

import java.math.BigInteger;

public class PlayerEntity extends Entity {
	/** attributes */
	private String groupName;
	private long firstServerLogin;
	private int experience;
	private String country;
	
	
	/** TileSet-Constructor */
	public PlayerEntity(TileSet tileSet, String name, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
		this.groupName = "";
		this.country = "";
		this.firstServerLogin = System.currentTimeMillis();
		this.experience = 0;
	}


	public String getGroupName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public long getFirstServerLogin() {
		return firstServerLogin;
	}


	public void setFirstServerLogin(long firstServerLogin) {
		this.firstServerLogin = firstServerLogin;
	}


	public int getExperience() {
		return experience;
	}


	public void setExperience(int experience) {
		this.experience = experience;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}
	
	

}
