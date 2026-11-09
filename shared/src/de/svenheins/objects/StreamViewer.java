/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

public class StreamViewer {
	private String name;
	private String comment;
	private int experience;
	private int gold;
	private int level;
	private int countPro;
	private int countContra;
	private int countKilledEnemies;
	private int countKilledPlayers;
	private int countIngameMessages;
	private int countDroppedItems;
	private long lastUpdateTime;
	private int red;
	private int green;
	private int blue;
	
	
	public StreamViewer(String name) {
		this.setName(name);
		this.setComment("no comment");
		this.setExperience(0);
		this.setGold(10);
		this.setLevel(1);
		this.setCountPro(0);
		this.setCountContra(0);
		this.setCountKilledEnemies(0);
		this.setCountKilledPlayers(0);
		this.setCountIngameMessages(0);
		this.setCountDroppedItems(0);
		this.setLastUpdateTime(System.currentTimeMillis());
		this.setRed(250);
		this.setGreen(250);
		this.setBlue(250);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCountPro() {
		return countPro;
	}
	public void setCountPro(int countPro) {
		this.countPro = countPro;
	}
	public int getCountContra() {
		return countContra;
	}
	public void setCountContra(int countContra) {
		this.countContra = countContra;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public int getCountKilledEnemies() {
		return countKilledEnemies;
	}

	public void setCountKilledEnemies(int countKilledEnemies) {
		this.countKilledEnemies = countKilledEnemies;
	}

	public int getCountIngameMessages() {
		return countIngameMessages;
	}

	public void setCountIngameMessages(int countIngameMessages) {
		this.countIngameMessages = countIngameMessages;
	}

	public int getCountDroppedItems() {
		return countDroppedItems;
	}

	public void setCountDroppedItems(int countDroppedItems) {
		this.countDroppedItems = countDroppedItems;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/** update Level depending on Experience */
	public void updateLevelByExperience() {
		int step = 100;
		int tempExp = this.getExperience()-step;
		int tempLevel = 1;
		while(tempExp >= 0) {
			tempLevel += 1; 
			step += 100;
			tempExp = tempExp-step;
		}
		this.setLevel(tempLevel);
	}
	
	public void addExp(int exp) {
		this.setExperience(this.getExperience()+exp);
	}
	
	public void addGold(int gold) {
		this.setGold(this.getGold()+gold);
	}
	
	public void removeGold(int gold) {
		this.setGold(this.getGold()-gold);
	}
	
	public void addCountPro(int count) {
		this.setCountPro(this.getCountPro()+count);
	}
	
	public void addCountContra(int count) {
		this.setCountContra(this.getCountContra()+count);
	}
	
	public void addCountIngameMessages(int count) {
		this.setCountIngameMessages(this.getCountIngameMessages()+count);
	}
	
	public void addCountDroppedItems(int count) {
		this.setCountDroppedItems(this.getCountDroppedItems()+count);
	}
	
	public void addCountKilledEnemies(int count) {
		this.setCountKilledEnemies(this.getCountKilledEnemies()+count);
	}
	
	public void addCountKilledPlayers(int count) {
		this.setCountKilledPlayers(this.getCountKilledPlayers()+count);
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getCountKilledPlayers() {
		return countKilledPlayers;
	}

	public void setCountKilledPlayers(int countKilledPlayers) {
		this.countKilledPlayers = countKilledPlayers;
	}
}
