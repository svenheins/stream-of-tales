/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.math.BigInteger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.main.GameStates;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;

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
	private ManagedReference<ServerContainer> inventory;
	private ManagedReference<ServerContainer> inventoryUse;
	private ManagedReference<ServerContainer> inventoryEqBody;
	private ManagedReference<ServerContainer> inventoryEqWeapon;
	private ManagedReference<ServerContainer> inventoryEqHelmet;
	private ManagedReference<ServerContainer> inventoryEqFeet;
	private ManagedReference<ServerContainer> inventoryEqShield;
	private ManagedReference<ServerContainer> inventoryEqAmulet;
	private ManagedReference<ServerContainer> inventoryEqGlove;
	private ManagedReference<ServerContainer> inventoryEqShirt;
	private ManagedReference<ServerContainer> inventoryEqTrousers;
	private String armorTileSetName;
	private String helmetTileSetName;
	private String feetTileSetName;
	private String weaponTileSetName;
	private String shieldTileSetName;
	private String amuletTileSetName;
	private String gloveTileSetName;
	private String shirtTileSetName;
	private String trousersTileSetName;
	private String hairName;
	
	
	public ServerPlayer(ServerSprite sprite, String name, String room, BigInteger id, String groupName, String tileSetName, String tileSetPathName, float x, float y,
			float mx, float my) {
		super(sprite, name, room, id, tileSetName, tileSetPathName, x, y, mx, my);
		this.groupName = groupName;
		this.country = "";
		this.firstServerLogin = System.currentTimeMillis();
		this.experience = 0;
		ServerContainer inventory = new ServerContainer(GameStates.inventoryWidthPlayer, GameStates.inventoryHeightPlayer, OBJECTCODE.CONTAINER_MAIN, ITEMCODE.ALL);
		this.inventory = AppContext.getDataManager().createReference(inventory);
		
		ServerContainer inventoryUseRef= new ServerContainer(GameStates.inventoryUseWidthPlayer, GameStates.inventoryUseHeightPlayer, OBJECTCODE.CONTAINER_USE, ITEMCODE.ALL);
		this.inventoryUse = AppContext.getDataManager().createReference(inventoryUseRef);
		
		ServerContainer inventoryEqBodyRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_BODY, ITEMCODE.BODY);
		this.inventoryEqBody = AppContext.getDataManager().createReference(inventoryEqBodyRef);
		ServerContainer inventoryEqWeaponRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_WEAPON, ITEMCODE.WEAPON);
		this.inventoryEqWeapon = AppContext.getDataManager().createReference(inventoryEqWeaponRef);
		ServerContainer inventoryEqHelmetRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_HELMET, ITEMCODE.HELMET);
		this.inventoryEqHelmet = AppContext.getDataManager().createReference(inventoryEqHelmetRef);
		ServerContainer inventoryEqFeetRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_FEET, ITEMCODE.FEET);
		this.inventoryEqFeet = AppContext.getDataManager().createReference(inventoryEqFeetRef);
		ServerContainer inventoryEqShieldRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_SHIELD, ITEMCODE.SHIELD);
		this.inventoryEqShield = AppContext.getDataManager().createReference(inventoryEqShieldRef);
		ServerContainer inventoryEqAmuletRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_AMULET, ITEMCODE.AMULET);
		this.setInventoryEqAmulet(AppContext.getDataManager().createReference(inventoryEqAmuletRef));
		ServerContainer inventoryEqGloveRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_GLOVE, ITEMCODE.GLOVE);
		this.setInventoryEqGlove(AppContext.getDataManager().createReference(inventoryEqGloveRef));
		ServerContainer inventoryEqShirtRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_SHIRT, ITEMCODE.SHIRT);
		this.setInventoryEqShirt(AppContext.getDataManager().createReference(inventoryEqShirtRef));
		ServerContainer inventoryEqTrousersRef= new ServerContainer(1,1 , OBJECTCODE.CONTAINER_EQUIPMENT_TROUSERS, ITEMCODE.TROUSERS);
		this.setInventoryEqTrousers(AppContext.getDataManager().createReference(inventoryEqTrousersRef));
		
		
		this.setArmorTileSetName("");
		this.setFeetTileSetName("");
		this.setHelmetTileSetName("");
		this.setWeaponTileSetName("");
		this.setShieldTileSetName("");
		this.setAmuletTileSetName("");
		this.setGloveTileSetName("");
		this.setShirtTileSetName("");
		this.setTrousersTileSetName("");
		this.setHairName("");
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

	public ManagedReference<ServerContainer> getInventory() {
		return inventory;
	}

	public void setInventory(ManagedReference<ServerContainer> inventory) {
		this.inventory = inventory;
	}

	public ManagedReference<ServerContainer> getInventoryUse() {
		return inventoryUse;
	}

	public void setInventoryUse(ManagedReference<ServerContainer> inventoryUse) {
		this.inventoryUse = inventoryUse;
	}

	public ManagedReference<ServerContainer> getInventoryEqBody() {
		return inventoryEqBody;
	}

	public void setInventoryEqBody(ManagedReference<ServerContainer> inventoryEqBody) {
		this.inventoryEqBody = inventoryEqBody;
	}

	public String getArmorTileSetName() {
		return armorTileSetName;
	}

	public void setArmorTileSetName(String armorTileSetName) {
		this.armorTileSetName = armorTileSetName;
	}

	public String getHelmetTileSetName() {
		return helmetTileSetName;
	}

	public void setHelmetTileSetName(String helmetTileSetName) {
		this.helmetTileSetName = helmetTileSetName;
	}

	public String getFeetTileSetName() {
		return feetTileSetName;
	}

	public void setFeetTileSetName(String feetTileSetName) {
		this.feetTileSetName = feetTileSetName;
	}

	public String getWeaponTileSetName() {
		return weaponTileSetName;
	}

	public void setWeaponTileSetName(String weaponTileSetName) {
		this.weaponTileSetName = weaponTileSetName;
	}

	public ManagedReference<ServerContainer> getInventoryEqWeapon() {
		return inventoryEqWeapon;
	}

	public void setInventoryEqWeapon(ManagedReference<ServerContainer> inventoryEqWeapon) {
		this.inventoryEqWeapon = inventoryEqWeapon;
	}

	public ManagedReference<ServerContainer> getInventoryEqHelmet() {
		return inventoryEqHelmet;
	}

	public void setInventoryEqHelmet(ManagedReference<ServerContainer> inventoryEqHelmet) {
		this.inventoryEqHelmet = inventoryEqHelmet;
	}
	
	public ManagedReference<ServerContainer> getInventoryEqFeet() {
		return inventoryEqFeet;
	}

	public void setInventoryEqFeet(ManagedReference<ServerContainer> inventoryEqFeet) {
		this.inventoryEqFeet = inventoryEqFeet;
	}

	public ManagedReference<ServerContainer> getInventoryEqShield() {
		return inventoryEqShield;
	}

	public void setInventoryEqShield(ManagedReference<ServerContainer> inventoryEqShield) {
		this.inventoryEqShield = inventoryEqShield;
	}


	public String getShieldTileSetName() {
		return shieldTileSetName;
	}

	public void setShieldTileSetName(String shieldTileSetName) {
		this.shieldTileSetName = shieldTileSetName;
	}

	public ManagedReference<ServerContainer> getInventoryEqAmulet() {
		return inventoryEqAmulet;
	}

	public void setInventoryEqAmulet(ManagedReference<ServerContainer> inventoryEqAmulet) {
		this.inventoryEqAmulet = inventoryEqAmulet;
	}

	public ManagedReference<ServerContainer> getInventoryEqGlove() {
		return inventoryEqGlove;
	}

	public void setInventoryEqGlove(ManagedReference<ServerContainer> inventoryEqGlove) {
		this.inventoryEqGlove = inventoryEqGlove;
	}

	public ManagedReference<ServerContainer> getInventoryEqShirt() {
		return inventoryEqShirt;
	}

	public void setInventoryEqShirt(ManagedReference<ServerContainer> inventoryEqShirt) {
		this.inventoryEqShirt = inventoryEqShirt;
	}

	public ManagedReference<ServerContainer> getInventoryEqTrousers() {
		return inventoryEqTrousers;
	}

	public void setInventoryEqTrousers(ManagedReference<ServerContainer> inventoryEqTrousers) {
		this.inventoryEqTrousers = inventoryEqTrousers;
	}

	public String getAmuletTileSetName() {
		return amuletTileSetName;
	}

	public void setAmuletTileSetName(String amuletTileSetName) {
		this.amuletTileSetName = amuletTileSetName;
	}

	public String getGloveTileSetName() {
		return gloveTileSetName;
	}

	public void setGloveTileSetName(String gloveTileSetName) {
		this.gloveTileSetName = gloveTileSetName;
	}

	public String getShirtTileSetName() {
		return shirtTileSetName;
	}

	public void setShirtTileSetName(String shirtTileSetName) {
		this.shirtTileSetName = shirtTileSetName;
	}

	public String getTrousersTileSetName() {
		return trousersTileSetName;
	}

	public void setTrousersTileSetName(String trousersTileSetName) {
		this.trousersTileSetName = trousersTileSetName;
	}

	public String getHairName() {
		return hairName;
	}

	public void setHairName(String hairName) {
		this.hairName = hairName;
	}

	
	
}
