package de.svenheins.objects;

import java.math.BigInteger;

public class ServerPlayer extends ServerEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** attributes */
	private String groupName;
	private long firstServerLogin;
	private int experience;
	private String country;
	
	public ServerPlayer(ServerSprite sprite, BigInteger id, float x, float y,
			float mx, float my) {
		super(sprite, id, x, y, mx, my);
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
