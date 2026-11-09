/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.main.PlayerProperty;
import de.svenheins.main.Priority;
import de.svenheins.managers.TileSetManager;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.items.Container;
import de.svenheins.objects.items.Item;

public class PlayerEntity extends Entity {
	/** attributes */
	private String groupName;
	private long firstServerLogin;
	private int experience;
	private String country;
	private long lastSeen;
	private boolean visible;
	private long lastAttack;
	private float[] attributes;
	private Container inventory;
	private Container inventoryUse;
	private Container equipmentBody;
	private Container equipmentWeapon;
	private Container equipmentHelmet;
	private Container equipmentFeet;
	private Container equipmentShield;
	private Container equipmentAmulet;
	private Container equipmentGlove;
	private Container equipmentShirt;
	private Container equipmentTrousers;
	private ArrayList<ItemInfluence> itemInfluenceList;
	private float[] totalItemInfluences = new float[AttributeType.values().length];
	private float[] totalAreaInfluences = new float[AttributeType.values().length];
	private float[] tempTotalAreaInfluences = new float[AttributeType.values().length];
	private float[] totalEquipmentInfluences = new float[AttributeType.values().length];
	private float[] totalAttributesInfluences = new float[AttributeType.values().length];
	private float[] totalBaseAttributes = new float[AttributeType.values().length];
	private Entity armorEntity;
	private Entity weaponEntity;
	private Entity helmetEntity;
	private Entity feetEntity;
	private Entity shieldEntity;
	private Entity amuletEntity;
	private Entity gloveEntity;
	private Entity shirtEntity;
	private Entity trousersEntity;
	private Entity hairEntity;
	private int collided;
	protected boolean alive;
	protected long deadSince;
	protected String sound;
	protected String damagedBy;
	protected String healedBy;
	
	
	
	

	/** TileSet-Constructor */
	public PlayerEntity(TileSet tileSet, String name, String room, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, room,  id, x, y, animationDelay);
		this.groupName = "";
		this.country = "";
		this.firstServerLogin = System.currentTimeMillis();
		this.experience = 0;
		this.lastSeen = System.currentTimeMillis();
		this.setVisible(true);
		this.setAttributes(new float[AttributeType.values().length]);
//		System.out.println("created "+name);
		this.inventory = new Container(GameStates.inventoryWidthPlayer, GameStates.inventoryHeightPlayer, OBJECTCODE.CONTAINER_MAIN, ITEMCODE.ALL);
		this.inventoryUse = new Container(GameStates.inventoryUseWidthPlayer, GameStates.inventoryUseHeightPlayer, OBJECTCODE.CONTAINER_USE, ITEMCODE.ALL);
		this.equipmentBody = new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_BODY, ITEMCODE.BODY);
		this.equipmentWeapon = new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_WEAPON, ITEMCODE.WEAPON);
		this.equipmentHelmet = new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_HELMET, ITEMCODE.HELMET);
		this.equipmentFeet = new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_FEET, ITEMCODE.FEET);
		this.equipmentShield = new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_SHIELD, ITEMCODE.SHIELD);
		this.setEquipmentAmulet(new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_AMULET, ITEMCODE.AMULET));
		this.setEquipmentGlove(new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_GLOVE, ITEMCODE.GLOVE));
		this.setEquipmentShirt(new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_SHIRT, ITEMCODE.SHIRT));
		this.setEquipmentTrousers(new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_TROUSERS, ITEMCODE.TROUSERS));
		this.setItemInfluenceList(new ArrayList<ItemInfluence>());
		
		this.setTotalAttributesInfluences(new float[AttributeType.values().length]);
		this.setTotalEquipmentInfluences(new float[AttributeType.values().length]);
		this.setTotalItemInfluences(new float[AttributeType.values().length]);
		this.setTempTotalAreaInfluences(new float[AttributeType.values().length]);
		this.setTotalAreaInfluences(new float[AttributeType.values().length]);
		this.setTotalBaseAttributes(new float[AttributeType.values().length]);
		
		String tileName = this.getTileSet().getName();
		totalBaseAttributes[AttributeType.MAXLIFE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXLIFE);
		attributes[AttributeType.MAXLIFE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXLIFE);
		attributes[AttributeType.LIFE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXLIFE);
		totalBaseAttributes[AttributeType.MAXMANA.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXMANA);
		attributes[AttributeType.MAXMANA.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXMANA);
		attributes[AttributeType.MANA.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXMANA);
		totalBaseAttributes[AttributeType.MAXENERGY.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXENERGY);
		attributes[AttributeType.MAXENERGY.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXENERGY);
		attributes[AttributeType.ENERGY.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXENERGY);
		totalBaseAttributes[AttributeType.MAXSPEED.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXSPEED);
		totalBaseAttributes[AttributeType.STRENGH.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.STRENGH);
		totalBaseAttributes[AttributeType.ARMOR.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.ARMOR);
		totalBaseAttributes[AttributeType.ATTACKSPEED.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.ATTACKSPERSECOND);
		totalBaseAttributes[AttributeType.ATTACKSQUARESIZE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.ATTACKSQUARESIZE);
		totalBaseAttributes[AttributeType.LIFELEECH.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.LIFELEECH);
		totalBaseAttributes[AttributeType.CRAFTLEVEL.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.CRAFTLEVEL);
		this.setArmorEntity(null);
		this.setWeaponEntity(null);
		this.setFeetEntity(null);
		this.setHelmetEntity(null);
		this.setShieldEntity(null);
		this.setAmuletEntity(null);
		this.setGloveEntity(null);
		this.setShirtEntity(null);
		this.setTrousersEntity(null);
		this.setHairEntity(null);
		this.calculateTotalAttributeInfluence();
		this.calculateTotalEquipmentInfluenceAndUpdateVisuals();
		
		this.setLastAttack(System.currentTimeMillis());
		this.setCollided(0);
		this.setAlive(true);
		this.setDeadSince(Long.MAX_VALUE);
		this.setSound("");
		this.setDamagedBy("");
		this.setHealedBy("");
	}
	
	public long getDeadSince() {
		return deadSince;
	}

	public void setDeadSince(long deadSince) {
		this.deadSince = deadSince;
	}
	
	public ArrayList<Container> getEquippedContainers() {
		ArrayList<Container> retList = new ArrayList<Container>();
		retList.add(equipmentAmulet);
		retList.add(equipmentBody);
		retList.add(equipmentFeet);
		retList.add(equipmentGlove);
		retList.add(equipmentHelmet);
		retList.add(equipmentShield);
		retList.add(equipmentShirt);
		retList.add(equipmentTrousers);
		retList.add(equipmentWeapon);
		return retList;
	}
	
	public void emptyEquipment() {
		this.setArmorEntity(null);
		this.setWeaponEntity(null);
		this.setFeetEntity(null);
		this.setHelmetEntity(null);
		this.setShieldEntity(null);
		this.setAmuletEntity(null);
		this.setGloveEntity(null);
		this.setShirtEntity(null);
		this.setTrousersEntity(null);
//		this.setHairEntity(null);
		this.calculateTotalEquipmentInfluenceAndUpdateVisuals();
		this.setChangedStates(true);
	}

	public void resetInfluences() {
		this.setItemInfluenceList(new ArrayList<ItemInfluence>());
		this.setTotalAttributesInfluences(new float[AttributeType.values().length]);
		this.setTotalEquipmentInfluences(new float[AttributeType.values().length]);
		this.setTotalItemInfluences(new float[AttributeType.values().length]);
		this.setTempTotalAreaInfluences(new float[AttributeType.values().length]);
		this.setTotalAreaInfluences(new float[AttributeType.values().length]);
		this.setTotalBaseAttributes(new float[AttributeType.values().length]);
	}
	
	public void resetAttributes() {
		String tileName = this.getTileSet().getName();
		totalBaseAttributes[AttributeType.MAXLIFE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXLIFE);
		attributes[AttributeType.MAXLIFE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXLIFE);
		attributes[AttributeType.LIFE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXLIFE);
		totalBaseAttributes[AttributeType.MAXMANA.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXMANA);
		attributes[AttributeType.MAXMANA.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXMANA);
		attributes[AttributeType.MANA.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXMANA);
		totalBaseAttributes[AttributeType.MAXENERGY.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXENERGY);
		attributes[AttributeType.MAXENERGY.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXENERGY);
		attributes[AttributeType.ENERGY.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXENERGY);
		totalBaseAttributes[AttributeType.MAXSPEED.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.MAXSPEED);
		totalBaseAttributes[AttributeType.STRENGH.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.STRENGH);
		totalBaseAttributes[AttributeType.ARMOR.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.ARMOR);
		totalBaseAttributes[AttributeType.ATTACKSPEED.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.ATTACKSPERSECOND);
		totalBaseAttributes[AttributeType.ATTACKSQUARESIZE.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.ATTACKSQUARESIZE);
		totalBaseAttributes[AttributeType.LIFELEECH.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.LIFELEECH);
		totalBaseAttributes[AttributeType.CRAFTLEVEL.ordinal()] = GameStates.getPlayerProperty(tileName, PlayerProperty.CRAFTLEVEL);
		
		this.calculateTotalAttributeInfluence();
	}

	public void move(long duration) {
//		super.move(duration);
		float movementX = duration * (this.getMX()+this.getAttributes()[AttributeType.MX.ordinal()])/1000;
		float movementY = duration * (this.getMY()+this.getAttributes()[AttributeType.MY.ordinal()])/1000;
		// Always update
		if(GameStates.getWidth()>0 && GameStates.getHeight()>0) {
			setX(this.getX()+movementX);
			setY(this.getY()+movementY);
		}
	}
	
	
	public void addItemInfluence(ItemInfluence itemInfluence) {
		switch (itemInfluence.getPriority()) {
		case LOW: 
			itemInfluenceList.add(itemInfluence);
			break;
		case MEDIUM:
			if (itemInfluenceList.size() > 0) {
				/** add right before the first HIGH priority-element*/
				int index = 0;
				for (int i = 0 ; i < itemInfluenceList.size(); i++) {
					index = i;
					if (itemInfluenceList.get(i).getPriority() == Priority.LOW)
						break;
					else 
						index = i+1;
				}
				itemInfluenceList.add(index, itemInfluence);
			} else {
				/** add the first element */
				itemInfluenceList.add(itemInfluence);
			}
			break;
		case HIGH:
			addFirstItemInfluence(itemInfluence);
			break;
		default: {
			System.out.println("ERROR: couldn't match Priority of the itemInfluence!");
		}
		}
	}
	
	public void addFirstItemInfluence(ItemInfluence itemInfluence) {
		if (itemInfluenceList != null) {
			itemInfluenceList.add(0, itemInfluence);
		}
	}
	
	public void removeItemInfluence(int index) {
		try {
			itemInfluenceList.remove(index);
		}
		catch(IndexOutOfBoundsException e) {
			System.out.println(e);
		}	
	}
	
	/** update attributes depending on influences */
	public void updateAttributes(long duration) {
		if ((totalBaseAttributes.length == totalAttributesInfluences.length) && (totalBaseAttributes.length == totalEquipmentInfluences.length) && (totalBaseAttributes.length == totalAreaInfluences.length) && (totalBaseAttributes.length == totalItemInfluences.length)){

			float[] tempAttributes = new float[attributes.length];
			tempAttributes = MyMath.add5FloatArrays(totalBaseAttributes, totalAttributesInfluences, totalEquipmentInfluences, totalAreaInfluences, totalItemInfluences);
			
			/** apply armor for lost life */
			if(tempAttributes[AttributeType.LIFE.ordinal()]<0 ) {
				tempAttributes[AttributeType.LIFE.ordinal()] = normalizeToMinMax(tempAttributes[AttributeType.LIFE.ordinal()]+tempAttributes[AttributeType.ARMOR.ordinal()], tempAttributes[AttributeType.LIFE.ordinal()], 0);
			}
			
//			/** apply hunger damage */
//			if (tempAttributes[AttributeType.ENERGY.ordinal()]<=0 ) {
//				tempAttributes[AttributeType.LIFEREGENERATION.ordinal()] = -0.05f;
//				if (this.isAlive() && this.getSingleState() != EntityStates.HURTING) {
//					this.setSingleState(EntityStates.HURTING);
//					this.determineAnimation();
//					this.setChangedStates(true);
//					this.setSound("hurting");
//				}
//			}
			
			/** check if playerEntity lost life */
			if(tempAttributes[AttributeType.LIFE.ordinal()]<0 ) {
				if (this.isAlive()) {
					this.setSingleState(EntityStates.HURTING);
					this.determineAnimation();
					this.setChangedStates(true);
					this.setSound("hurting");
				}
				
				/** check movements */
				if(tempAttributes[AttributeType.SENDMX.ordinal()]!= 0 || tempAttributes[AttributeType.SENDMY.ordinal()]!= 0) {
					if (totalAreaInfluences[AttributeType.LIFE.ordinal()] < 0) {
						float[] slideAttributes = new float[AttributeType.values().length];
						float slideFactorTemp = -1* GameStates.getPlayerProperty(this.getTileSet().getName(), PlayerProperty.SLIDEFACTOR)*totalAreaInfluences[AttributeType.LIFE.ordinal()]/ tempAttributes[AttributeType.MAXLIFE.ordinal()];
						float slideFactor = normalizeToMinMax(slideFactorTemp, GameStates.minSlideFactor, GameStates.maxSlideFactor);
//						System.out.println("SlideFactor = "+slideFactor);
						slideAttributes[AttributeType.MX.ordinal()] = slideFactor*(tempAttributes[AttributeType.SENDMX.ordinal()]);
						slideAttributes[AttributeType.MY.ordinal()] = slideFactor*(tempAttributes[AttributeType.SENDMY.ordinal()]);
						ItemInfluence slideInfluence = new ItemInfluence(System.currentTimeMillis(), System.currentTimeMillis()+((long) (slideFactor*this.getAnimation().getTimeBetweenAnimation())), slideAttributes, Priority.HIGH);
						this.addItemInfluence(slideInfluence);
						
						this.setLastAttack(System.currentTimeMillis());// STUNNED!
					}
				}
			}
			
			/** apply influence to Life and Mana */
			tempAttributes[AttributeType.LIFE.ordinal()] += attributes[AttributeType.LIFE.ordinal()];
			tempAttributes[AttributeType.MANA.ordinal()] += attributes[AttributeType.MANA.ordinal()];
			tempAttributes[AttributeType.ENERGY.ordinal()] += attributes[AttributeType.ENERGY.ordinal()];
			/** apply hunger damage */
			if (tempAttributes[AttributeType.ENERGY.ordinal()]<=0 ) {
				tempAttributes[AttributeType.LIFEREGENERATION.ordinal()] = -tempAttributes[AttributeType.MAXLIFE.ordinal()]/GameStates.timeTillStarving;
				if (this.isAlive() && this.getSingleState() != EntityStates.HURTING) {
					this.setSingleState(EntityStates.HURTING);
					this.determineAnimation();
					this.setChangedStates(true);
					this.setSound("hurting");
				}
			}
			
			/** regenerate life and mana */
			tempAttributes[AttributeType.LIFE.ordinal()] = regenerate(tempAttributes[AttributeType.LIFE.ordinal()], tempAttributes[AttributeType.LIFEREGENERATION.ordinal()], duration);
			tempAttributes[AttributeType.MANA.ordinal()] = regenerate(tempAttributes[AttributeType.MANA.ordinal()], tempAttributes[AttributeType.MANAREGENERATION.ordinal()], duration);
			tempAttributes[AttributeType.ENERGY.ordinal()] = regenerate(tempAttributes[AttributeType.ENERGY.ordinal()], tempAttributes[AttributeType.ENERGYREGENERATION.ordinal()], duration);
			/** normalize life and mana */
			tempAttributes[AttributeType.LIFE.ordinal()] = normalizeToMinMax(tempAttributes[AttributeType.LIFE.ordinal()], 0, tempAttributes[AttributeType.MAXLIFE.ordinal()]);
			tempAttributes[AttributeType.MANA.ordinal()] = normalizeToMinMax(tempAttributes[AttributeType.MANA.ordinal()], 0, tempAttributes[AttributeType.MAXMANA.ordinal()]);
			tempAttributes[AttributeType.ENERGY.ordinal()] = normalizeToMinMax(tempAttributes[AttributeType.ENERGY.ordinal()], 0, tempAttributes[AttributeType.MAXENERGY.ordinal()]);
			
			/** normalize ATTACKSPEED */
			tempAttributes[AttributeType.ATTACKSPEED.ordinal()] = normalizeToMinMax(tempAttributes[AttributeType.ATTACKSPEED.ordinal()], GameStates.getPlayerProperty(this.getTileSet().getName(), PlayerProperty.MINATTACKSPEED), GameStates.getPlayerProperty(this.getTileSet().getName(), PlayerProperty.MAXATTACKSPEED));
			
			totalAreaInfluences[AttributeType.LIFE.ordinal()] = 0.0f;
			totalAreaInfluences[AttributeType.MANA.ordinal()] = 0.0f;
			totalAreaInfluences[AttributeType.ENERGY.ordinal()] = 0.0f;
			totalItemInfluences[AttributeType.LIFE.ordinal()] = 0.0f;
			totalItemInfluences[AttributeType.MANA.ordinal()] = 0.0f;
			totalItemInfluences[AttributeType.ENERGY.ordinal()] = 0.0f;
			
			/** update attributes */
			this.setAttributes(tempAttributes);
			this.setMaxSpeed(this.getAttributes()[AttributeType.MAXSPEED.ordinal()]);
			
			
			
//			/** check if playerEntity is in a bad condition */
//			if(this.getAttributes()[AttributeType.LIFE.ordinal()]<this.getAttributes()[AttributeType.MAXLIFE.ordinal()]/5 ) {
//				this.setSingleState(EntityStates.HURTING);
//				this.determineAnimation();
//				this.setChangedStates(true);
//			}
		}
	}
	
	
	/** calculate the total influence of used items or similar influences (i.e. poisoned, slowed down,...) */
	public void calculateTotalItemInfluence() {
		/** add each itemInfluence to the total */
		float[] tempItemInfluence = new float[AttributeType.values().length];
		long timeNow = System.currentTimeMillis();
		int index = 0;
		int[] deleteIndex = new int[this.getItemInfluenceList().size()];
		ArrayList<ItemInfluence> tempItemInfluenceList = new ArrayList<ItemInfluence>(getItemInfluenceList());
		
		for (ItemInfluence itemInfluence: tempItemInfluenceList) {
			if (itemInfluence != null) {
				/** only apply itemInfluence if it is active! */
				/** first check if the influence has not ended yet and is no instant influence */
				if ( ((itemInfluence.getTimeBegin() <= timeNow) && (itemInfluence.getTimeEnd() >= timeNow)) || (itemInfluence.getTimeBegin() == itemInfluence.getTimeEnd())) {
					/** ACTIVE INFLUENCE: the influence is either continuous, has started and NOT yet ended OR the influence is instantaneous */
	//				System.out.println("active itemInfluence mx="+itemInfluence.getAttributes()[AttributeType.MX.ordinal()]+" my="+itemInfluence.getAttributes()[AttributeType.MY.ordinal()]);
					tempItemInfluence = MyMath.addFloatArrays(tempItemInfluence, itemInfluence.getAttributes());
				} else {
					/** the influence has not yet started, or has already ended and is not instantaneous
					 * -> do nothing, because the removal happens later */
				}
				
				/** finally remove the instantaneous influence (if applicable) or ended influence*/
				if (itemInfluence.getTimeBegin() == itemInfluence.getTimeEnd()) {
					/** instantaneous influence */
					deleteIndex[index] = 1;
				} else if (itemInfluence.getTimeEnd() < timeNow) {
					/** ended continuous influence */
					deleteIndex[index] = 1;
				}
			}
				index += 1; // index of the next iterator element 			
			
		}
		
		/** remove the invalid influences */
		for (int i = deleteIndex.length-1; i >=0; i--) {
			if ( deleteIndex[i] == 1) {
				tempItemInfluenceList.remove(i);
			}
		}
		/** update the itemInfluenceList */
		this.setItemInfluenceList(tempItemInfluenceList);
		this.setTotalItemInfluences(tempItemInfluence);
	}
	
	/** calculate the actual total influence that is done by all equipment */ 
	public void calculateTotalEquipmentInfluenceAndUpdateVisuals() {
		float[] tempTotalEquipmentInfluences = new float[AttributeType.values().length];
		
		/** body */
		boolean hasBodyEquipment = false;
		for (Item item: this.getEquipmentBody().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsArmor(item.getName());
			hasBodyEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasBodyEquipment) this.setArmorEntity(null);
		
		/** weapon */
		boolean hasWeaponEquipment = false;
		for (Item item: this.getEquipmentWeapon().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsWeapon(item.getName());
			hasWeaponEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasWeaponEquipment) this.setWeaponEntity(null);
		
		/** helmet */
		boolean hasHelmetEquipment = false;
		for (Item item: this.getEquipmentHelmet().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsHelmet(item.getName());
			hasHelmetEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasHelmetEquipment) this.setHelmetEntity(null);
		
		/** feet */
		boolean hasFeetEquipment = false;
		for (Item item: this.getEquipmentFeet().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsFeet(item.getName());
			hasFeetEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasFeetEquipment) this.setFeetEntity(null);
		
		/** shield */
		boolean hasShieldEquipment = false;
		for (Item item: this.getEquipmentShield().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsShield(item.getName());
			hasShieldEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasShieldEquipment) this.setShieldEntity(null);
		
		/** Amulet */
		boolean hasAmuletEquipment = false;
		for (Item item: this.getEquipmentAmulet().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsAmulet(item.getName());
			hasAmuletEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasAmuletEquipment) this.setAmuletEntity(null);
		
		/** Glove */
		boolean hasGloveEquipment = false;
		for (Item item: this.getEquipmentGlove().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsGlove(item.getName());
			hasGloveEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasGloveEquipment) this.setGloveEntity(null);
		
		/** Shirt */
		boolean hasShirtEquipment = false;
		for (Item item: this.getEquipmentShirt().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsShirt(item.getName());
			hasShirtEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasShirtEquipment) this.setShirtEntity(null);
		
		/** Trousers */
		boolean hasTrousersEquipment = false;
		for (Item item: this.getEquipmentTrousers().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
			/** update Visual representation of equipped item */
			this.updateVisualsTrousers(item.getName());
			hasTrousersEquipment = true;
		}
		/** delete armor if there is no */
		if (!hasTrousersEquipment) this.setTrousersEntity(null);
		
		totalEquipmentInfluences = tempTotalEquipmentInfluences;
//		System.out.println("equipment finished");
		/** and so on */
	}
	
	/** update animationDelay */
	public void updateAnimationDelay(long animationDelay) {
		this.getAnimation().setTimeBetweenAnimation(animationDelay);
		
		if (this.getWeaponEntity() != null) {
			this.getWeaponEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getArmorEntity() != null) {
			this.getArmorEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getShieldEntity() != null) {
			this.getShieldEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getHelmetEntity() != null) {
			this.getHelmetEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getFeetEntity() != null) {
			this.getFeetEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getAmuletEntity() != null) {
			this.getAmuletEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getGloveEntity() != null) {
			this.getGloveEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getShirtEntity() != null) {
			this.getShirtEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getTrousersEntity() != null) {
			this.getTrousersEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
		if (this.getHairEntity() != null) {
			this.getHairEntity().getAnimation().setTimeBetweenAnimation(animationDelay);
		}
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsArmor(String itemName) {
			/** update the armor Entity */
			Entity tempArmorEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),  BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setArmorEntity(tempArmorEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsWeapon(String itemName) {
			/** update the armor Entity */
			Entity tempWeaponEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setWeaponEntity(tempWeaponEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsHelmet(String itemName) {
			/** update the armor Entity */
			Entity tempHelmetEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setHelmetEntity(tempHelmetEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsFeet(String itemName) {
			/** update the armor Entity */
			Entity tempFeetEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setFeetEntity(tempFeetEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsShield(String itemName) {
			/** update the armor Entity */
			Entity tempShieldEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setShieldEntity(tempShieldEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsAmulet(String itemName) {
			/** update the armor Entity */
			Entity tempAmuletEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setAmuletEntity(tempAmuletEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsGlove(String itemName) {
			/** update the armor Entity */
			Entity tempGloveEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setGloveEntity(tempGloveEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsShirt(String itemName) {
			/** update the armor Entity */
			Entity tempShirtEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setShirtEntity(tempShirtEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsTrousers(String itemName) {
			/** update the armor Entity */
			Entity tempTrousersEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setTrousersEntity(tempTrousersEntity);
	}
	
	/** update visual representation of equipped item */
	public void updateVisualsHair(String itemName) {
			/** update the armor Entity */
			Entity tempHairEntity = new Entity(TileSetManager.manager.getTileSet(GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName), GameStates.itemEquipPrefix+this.getTileSet().getName()+itemName, this.getRoom(),BigInteger.valueOf(0), this.getX(), this.getY(), GameStates.animationDelay);
			this.setHairEntity(tempHairEntity);
	}
	
	/** calculate the actual total influence that is done by all equipment */ 
	public void calculateTotalAttributeInfluence() {
		/** which attributes effect others?
		 * MaxLife -> LifeRegeneration
		 * MaxMana -> ManaRegeneration
		 * */
		String tileName = this.getTileSet().getName();
		this.totalAttributesInfluences[AttributeType.LIFEREGENERATION.ordinal()] = this.attributes[AttributeType.MAXLIFE.ordinal()] * GameStates.getPlayerProperty(tileName, PlayerProperty.LIFEREGENERATION);
		this.totalAttributesInfluences[AttributeType.MANAREGENERATION.ordinal()] = this.attributes[AttributeType.MAXMANA.ordinal()] * GameStates.getPlayerProperty(tileName, PlayerProperty.MANAREGENERATION);
		this.totalAttributesInfluences[AttributeType.ENERGYREGENERATION.ordinal()] = this.attributes[AttributeType.MAXENERGY.ordinal()] * GameStates.getPlayerProperty(tileName, PlayerProperty.ENERGYREGERERATION);
	}
	
	/** regeneration process in relation to a second */
	public static float regenerate(float attributeValue, float regenerationPerSecond, long timeIntervallInMilliseconds) {
		return (attributeValue + ((float) timeIntervallInMilliseconds/1000)*regenerationPerSecond);
	}
	
	/** normalize an attribute (like life or mana) to a min and max value */
	public static float normalizeToMinMax(float attribute, float attributeMinimum, float attributeMaximum) {
		if (attribute > attributeMaximum) return attributeMaximum; else
		if (attribute < attributeMinimum) return attributeMinimum; else
			return attribute;
	}

	
	public ItemInfluence getItemInfluence(int index) {
		try {
			return itemInfluenceList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			System.out.println(e);
			return null;
		}
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


	public long getLastSeen() {
		return lastSeen;
	}


	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}


	public boolean isVisible() {
		return visible;
	}


	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	public Container getInventory() {
		return inventory;
	}


	public void setInventory(Container inventory) {
		this.inventory = inventory;
	}


	public Container getInventoryUse() {
		return inventoryUse;
	}


	public void setInventoryUse(Container inventoryUse) {
		this.inventoryUse = inventoryUse;
	}


	public Container getEquipmentBody() {
		return equipmentBody;
	}


	public void setEquipmentBody(Container equipmentBody) {
		this.equipmentBody = equipmentBody;
		
	}

	public ArrayList<ItemInfluence> getItemInfluenceList() {
		return itemInfluenceList;
	}

	public void setItemInfluenceList(ArrayList<ItemInfluence> itemInfluenceList) {
		this.itemInfluenceList = itemInfluenceList;
	}
	
	
	
	public int itemInfluenceSize() {
		return itemInfluenceList.size();
	}


	public float[] getAttributes() {
		return attributes;
	}


	public void setAttributes(float[] attributes) {
		this.attributes = attributes;
	}


	public float[] getTotalItemInfluences() {
		return totalItemInfluences;
	}


	public void setTotalItemInfluences(float[] totalItemInfluences) {
		this.totalItemInfluences = totalItemInfluences;
	}


	public float[] getTotalAreaInfluences() {
		return totalAreaInfluences;
	}


	public void setTotalAreaInfluences(float[] totalAreaInfluences) {
		this.totalAreaInfluences = totalAreaInfluences;
	}


	public float[] getTotalEquipmentInfluences() {
		return totalEquipmentInfluences;
	}


	public void setTotalEquipmentInfluences(float[] totalEquipmentInfluences) {
		this.totalEquipmentInfluences = totalEquipmentInfluences;
	}


	public float[] getTotalAttributesInfluences() {
		return totalAttributesInfluences;
	}


	public void setTotalAttributesInfluences(float[] totalAttributesInfluences) {
		this.totalAttributesInfluences = totalAttributesInfluences;
	}


	public float[] getTempTotalAreaInfluences() {
		return tempTotalAreaInfluences;
	}


	public void setTempTotalAreaInfluences(float[] tempTotalAreaInfluences) {
		this.tempTotalAreaInfluences = tempTotalAreaInfluences;
	}


	protected float[] getTotalBaseAttributes() {
		return totalBaseAttributes;
	}


	protected void setTotalBaseAttributes(float[] totalBaseAttributes) {
		this.totalBaseAttributes = totalBaseAttributes;
	}

	public Entity getArmorEntity() {
		return armorEntity;
	}

	public void setArmorEntity(Entity armorEntity) {
		this.armorEntity = armorEntity;
	}

	public Entity getWeaponEntity() {
		return weaponEntity;
	}

	public void setWeaponEntity(Entity weaponEntity) {
		this.weaponEntity = weaponEntity;
	}

	public Entity getHelmetEntity() {
		return helmetEntity;
	}

	public void setHelmetEntity(Entity helmetEntity) {
		this.helmetEntity = helmetEntity;
	}

	public Entity getFeetEntity() {
		return feetEntity;
	}

	public void setFeetEntity(Entity feetEntity) {
		this.feetEntity = feetEntity;
	}
	
	
	
	/** ****************************************************
	 * Override stuff for animation of equipment, 
	 * that goes together with the normal Sprite animation
	 * *****************************************************
	 */
	
	@Override
	public void setWaitingForSingleAnimation(boolean waitingForSingleAnimation) {
		super.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.armorEntity != null) this.armorEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.weaponEntity != null) this.weaponEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.helmetEntity != null) this.helmetEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.feetEntity != null) this.feetEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.shieldEntity != null) this.shieldEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.amuletEntity != null) this.amuletEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.gloveEntity != null) this.gloveEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.shirtEntity != null) this.shirtEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.trousersEntity != null) this.trousersEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		if (this.hairEntity != null) this.hairEntity.setWaitingForSingleAnimation(waitingForSingleAnimation);
		
	}
	
	@Override
	public void setOrientation(EntityStates orientation) {
		super.setOrientation(orientation);
		if (this.armorEntity != null) this.armorEntity.setOrientation(orientation);
		if (this.weaponEntity != null) this.weaponEntity.setOrientation(orientation);
		if (this.helmetEntity != null) this.helmetEntity.setOrientation(orientation);
		if (this.feetEntity != null) this.feetEntity.setOrientation(orientation);
		if (this.shieldEntity != null) this.shieldEntity.setOrientation(orientation);
		if (this.amuletEntity != null) this.amuletEntity.setOrientation(orientation);
		if (this.gloveEntity != null) this.gloveEntity.setOrientation(orientation);
		if (this.shirtEntity != null) this.shirtEntity.setOrientation(orientation);
		if (this.trousersEntity != null) this.trousersEntity.setOrientation(orientation);
		if (this.hairEntity != null) this.hairEntity.setOrientation(orientation);
	}
	
	@Override
	public void setSingleState(EntityStates state) {
		super.setSingleState(state);
		if (this.armorEntity != null) this.armorEntity.setSingleState(state);
		if (this.weaponEntity != null) this.weaponEntity.setSingleState(state);
		if (this.helmetEntity != null) this.helmetEntity.setSingleState(state);
		if (this.feetEntity != null) this.feetEntity.setSingleState(state);
		if (this.shieldEntity != null) this.shieldEntity.setSingleState(state);
		if (this.amuletEntity != null) this.amuletEntity.setSingleState(state);
		if (this.gloveEntity != null) this.gloveEntity.setSingleState(state);
		if (this.shirtEntity != null) this.shirtEntity.setSingleState(state);
		if (this.trousersEntity != null) this.trousersEntity.setSingleState(state);
		if (this.hairEntity != null) this.hairEntity.setSingleState(state);
	}
	
	@Override
	public void updateSprite() {
		super.updateSprite();
		if (this.armorEntity != null) this.armorEntity.updateSprite();
		if (this.weaponEntity != null) this.weaponEntity.updateSprite();
		if (this.helmetEntity != null) this.helmetEntity.updateSprite();
		if (this.feetEntity != null) this.feetEntity.updateSprite();
		if (this.shieldEntity != null) this.shieldEntity.updateSprite();
		if (this.amuletEntity != null) this.amuletEntity.updateSprite();
		if (this.gloveEntity != null) this.gloveEntity.updateSprite();
		if (this.shirtEntity != null) this.shirtEntity.updateSprite();
		if (this.trousersEntity != null) this.trousersEntity.updateSprite();
		if (this.hairEntity != null) this.hairEntity.updateSprite();
	}
	
	@Override
	public void setChangedStates(boolean b) {
		super.setChangedStates(b);
		if (this.armorEntity != null) this.armorEntity.setChangedStates(b);
		if (this.weaponEntity != null) this.weaponEntity.setChangedStates(b);
		if (this.helmetEntity != null) this.helmetEntity.setChangedStates(b);
		if (this.feetEntity != null) this.feetEntity.setChangedStates(b);
		if (this.shieldEntity != null) this.shieldEntity.setChangedStates(b);
		if (this.amuletEntity != null) this.amuletEntity.setChangedStates(b);
		if (this.gloveEntity != null) this.gloveEntity.setChangedStates(b);
		if (this.shirtEntity != null) this.shirtEntity.setChangedStates(b);
		if (this.trousersEntity != null) this.trousersEntity.setChangedStates(b);
		if (this.hairEntity != null) this.hairEntity.setChangedStates(b);
	}
	
	@Override
	public void setContinuousState(EntityStates continuousState) {
		super.setContinuousState(continuousState);
		if (this.armorEntity != null) this.armorEntity.setContinuousState(continuousState);
		if (this.weaponEntity != null) this.weaponEntity.setContinuousState(continuousState);
		if (this.helmetEntity != null) this.helmetEntity.setContinuousState(continuousState);
		if (this.feetEntity != null) this.feetEntity.setContinuousState(continuousState);
		if (this.shieldEntity != null) this.shieldEntity.setContinuousState(continuousState);
		if (this.amuletEntity != null) this.amuletEntity.setContinuousState(continuousState);
		if (this.gloveEntity != null) this.gloveEntity.setContinuousState(continuousState);
		if (this.shirtEntity != null) this.shirtEntity.setContinuousState(continuousState);
		if (this.trousersEntity != null) this.trousersEntity.setContinuousState(continuousState);
		if (this.hairEntity != null) this.hairEntity.setContinuousState(continuousState);
	}
	
	@Override
	public void determineOrientation(Point p) {
		super.determineOrientation(p);
		if (this.armorEntity != null) this.armorEntity.setOrientation(this.getOrientation());
		if (this.weaponEntity != null) this.weaponEntity.setOrientation(this.getOrientation());
		if (this.helmetEntity != null) this.helmetEntity.setOrientation(this.getOrientation());
		if (this.feetEntity != null) this.feetEntity.setOrientation(this.getOrientation());
		if (this.shieldEntity != null) this.shieldEntity.setOrientation(this.getOrientation());
		if (this.amuletEntity != null) this.amuletEntity.setOrientation(this.getOrientation());
		if (this.gloveEntity != null) this.gloveEntity.setOrientation(this.getOrientation());
		if (this.shirtEntity != null) this.shirtEntity.setOrientation(this.getOrientation());
		if (this.trousersEntity != null) this.trousersEntity.setOrientation(this.getOrientation());
		if (this.hairEntity != null) this.hairEntity.setOrientation(this.getOrientation());
	}
	
	@Override
	public void startAnimation() {
		super.startAnimation();
		if (this.armorEntity != null) this.armorEntity.startAnimation();
		if (this.weaponEntity != null) this.weaponEntity.startAnimation();
		if (this.helmetEntity != null) this.helmetEntity.startAnimation();
		if (this.feetEntity != null) this.feetEntity.startAnimation();
		if (this.shieldEntity != null) this.shieldEntity.startAnimation();
		if (this.amuletEntity != null) this.amuletEntity.startAnimation();
		if (this.gloveEntity != null) this.gloveEntity.startAnimation();
		if (this.shirtEntity != null) this.shirtEntity.startAnimation();
		if (this.trousersEntity != null) this.trousersEntity.startAnimation();
		if (this.hairEntity != null) this.hairEntity.startAnimation();
	}
	
	public void endAnimation() {
		super.endAnimation();
		if (this.armorEntity != null) this.armorEntity.endAnimation();
		if (this.weaponEntity != null) this.weaponEntity.endAnimation();
		if (this.helmetEntity != null) this.helmetEntity.endAnimation();
		if (this.feetEntity != null) this.feetEntity.endAnimation();
		if (this.shieldEntity != null) this.shieldEntity.endAnimation();
		if (this.amuletEntity != null) this.amuletEntity.endAnimation();
		if (this.gloveEntity != null) this.gloveEntity.endAnimation();
		if (this.shirtEntity != null) this.shirtEntity.endAnimation();
		if (this.trousersEntity != null) this.trousersEntity.endAnimation();
		if (this.hairEntity != null) this.hairEntity.endAnimation();
	}

	@Override
	public void determineAnimation() {
		super.determineAnimation();
		if (this.armorEntity != null) this.armorEntity.determineAnimation();
		if (this.weaponEntity != null) this.weaponEntity.determineAnimation();
		if (this.helmetEntity != null) this.helmetEntity.determineAnimation();
		if (this.feetEntity != null) this.feetEntity.determineAnimation();
		if (this.shieldEntity != null) this.shieldEntity.determineAnimation();
		if (this.amuletEntity != null) this.amuletEntity.determineAnimation();
		if (this.gloveEntity != null) this.gloveEntity.determineAnimation();
		if (this.shirtEntity != null) this.shirtEntity.determineAnimation();
		if (this.trousersEntity != null) this.trousersEntity.determineAnimation();
		if (this.hairEntity != null) this.hairEntity.determineAnimation();
	}
	
	@Override
	public void setStandingAnimation() {
		super.setStandingAnimation();
		if (this.armorEntity != null) this.armorEntity.setStandingAnimation();
		if (this.weaponEntity != null) this.weaponEntity.setStandingAnimation();
		if (this.helmetEntity != null) this.helmetEntity.setStandingAnimation();
		if (this.feetEntity != null) this.feetEntity.setStandingAnimation();
		if (this.shieldEntity != null) this.shieldEntity.setStandingAnimation();
		if (this.amuletEntity != null) this.amuletEntity.setStandingAnimation();
		if (this.gloveEntity != null) this.gloveEntity.setStandingAnimation();
		if (this.shirtEntity != null) this.shirtEntity.setStandingAnimation();
		if (this.trousersEntity != null) this.trousersEntity.setStandingAnimation();
		if (this.hairEntity != null) this.hairEntity.setStandingAnimation();
	}
	
	@Override
	public void setWalkingAnimation() {
		super.setWalkingAnimation();
		if (this.armorEntity != null) this.armorEntity.setWalkingAnimation();
		if (this.weaponEntity != null) this.weaponEntity.setWalkingAnimation();
		if (this.helmetEntity != null) this.helmetEntity.setWalkingAnimation();
		if (this.feetEntity != null) this.feetEntity.setWalkingAnimation();
		if (this.shieldEntity != null) this.shieldEntity.setWalkingAnimation();
		if (this.amuletEntity != null) this.amuletEntity.setWalkingAnimation();
		if (this.gloveEntity != null) this.gloveEntity.setWalkingAnimation();
		if (this.shirtEntity != null) this.shirtEntity.setWalkingAnimation();
		if (this.trousersEntity != null) this.trousersEntity.setWalkingAnimation();
		if (this.hairEntity != null) this.hairEntity.setWalkingAnimation();
	}

	@Override
	protected void determineAttackingAnimation() {
		super.determineAttackingAnimation();
		if (this.armorEntity != null) this.armorEntity.determineAttackingAnimation();
		if (this.weaponEntity != null) this.weaponEntity.determineAttackingAnimation();
		if (this.helmetEntity != null) this.helmetEntity.determineAttackingAnimation();
		if (this.feetEntity != null) this.feetEntity.determineAttackingAnimation();
		if (this.shieldEntity != null) this.shieldEntity.determineAttackingAnimation();
		if (this.amuletEntity != null) this.amuletEntity.determineAttackingAnimation();
		if (this.gloveEntity != null) this.gloveEntity.determineAttackingAnimation();
		if (this.shirtEntity != null) this.shirtEntity.determineAttackingAnimation();
		if (this.trousersEntity != null) this.trousersEntity.determineAttackingAnimation();
		if (this.hairEntity != null) this.hairEntity.determineAttackingAnimation();
	}
	
	@Override
	protected void determineHurtingAnimation() {
		super.determineHurtingAnimation();
		if (this.armorEntity != null) this.armorEntity.determineHurtingAnimation();
		if (this.weaponEntity != null) this.weaponEntity.determineHurtingAnimation();
		if (this.helmetEntity != null) this.helmetEntity.determineHurtingAnimation();
		if (this.feetEntity != null) this.feetEntity.determineHurtingAnimation();
		if (this.shieldEntity != null) this.shieldEntity.determineHurtingAnimation();
		if (this.amuletEntity != null) this.amuletEntity.determineHurtingAnimation();
		if (this.gloveEntity != null) this.gloveEntity.determineHurtingAnimation();
		if (this.shirtEntity != null) this.shirtEntity.determineHurtingAnimation();
		if (this.trousersEntity != null) this.trousersEntity.determineHurtingAnimation();
		if (this.hairEntity != null) this.hairEntity.determineHurtingAnimation();
	}
	
	@Override
	protected void determineWorkingAnimation() {
		super.determineWorkingAnimation();
		if (this.armorEntity != null) this.armorEntity.determineWorkingAnimation();
		if (this.weaponEntity != null) this.weaponEntity.determineWorkingAnimation();
		if (this.helmetEntity != null) this.helmetEntity.determineWorkingAnimation();
		if (this.feetEntity != null) this.feetEntity.determineWorkingAnimation();
		if (this.shieldEntity != null) this.shieldEntity.determineWorkingAnimation();
		if (this.amuletEntity != null) this.amuletEntity.determineWorkingAnimation();
		if (this.gloveEntity != null) this.gloveEntity.determineWorkingAnimation();
		if (this.shirtEntity != null) this.shirtEntity.determineWorkingAnimation();
		if (this.trousersEntity != null) this.trousersEntity.determineWorkingAnimation();
		if (this.hairEntity != null) this.hairEntity.determineWorkingAnimation();
	}

	public Entity getShieldEntity() {
		return shieldEntity;
	}

	public void setShieldEntity(Entity shieldEntity) {
		this.shieldEntity = shieldEntity;
	}

	public Container getEquipmentWeapon() {
		return equipmentWeapon;
	}

	public void setEquipmentWeapon(Container equipmentWeapon) {
		this.equipmentWeapon = equipmentWeapon;
	}

	public Container getEquipmentHelmet() {
		return equipmentHelmet;
	}

	public void setEquipmentHelmet(Container equipmentHelmet) {
		this.equipmentHelmet = equipmentHelmet;
	}

	public Container getEquipmentFeet() {
		return equipmentFeet;
	}

	public void setEquipmentFeet(Container equipmentFeet) {
		this.equipmentFeet = equipmentFeet;
	}

	public Container getEquipmentShield() {
		return equipmentShield;
	}

	public void setEquipmentShield(Container equipmentShield) {
		this.equipmentShield = equipmentShield;
	}
	

	public long getLastAttack() {
		return lastAttack;
	}

	public void setLastAttack(long lastAttack) {
		this.lastAttack = lastAttack;
	}
	
	/** calculate the level based on some fighting relevant attributes */
	public int getLevel() {
//		int level =1 + (int)( (this.getAttributes()[AttributeType.MAXLIFE.ordinal()]/50+ this.getAttributes()[AttributeType.MAXMANA.ordinal()]/10 +(this.getAttributes()[AttributeType.ATTACKSPEED.ordinal()]-1.0)*5  +this.getAttributes()[AttributeType.ARMOR.ordinal()]/2+this.getAttributes()[AttributeType.STRENGH.ordinal()]/8+this.getAttributes()[AttributeType.MAXSPEED.ordinal()]/75) /6);
		int level = (int) ( this.getAttributes()[AttributeType.MAXLIFE.ordinal()]/20);
		return level;	
	}

	public int getCollided() {
		return collided;
	}

	public void setCollided(int collided) {
		this.collided = collided;
	}

	public Container getEquipmentAmulet() {
		return equipmentAmulet;
	}

	public void setEquipmentAmulet(Container equipmentAmulet) {
		this.equipmentAmulet = equipmentAmulet;
	}

	public Container getEquipmentGlove() {
		return equipmentGlove;
	}

	public void setEquipmentGlove(Container equipmentGlove) {
		this.equipmentGlove = equipmentGlove;
	}

	public Container getEquipmentShirt() {
		return equipmentShirt;
	}

	public void setEquipmentShirt(Container equipmentShirt) {
		this.equipmentShirt = equipmentShirt;
	}

	public Container getEquipmentTrousers() {
		return equipmentTrousers;
	}

	public void setEquipmentTrousers(Container equipmentTrousers) {
		this.equipmentTrousers = equipmentTrousers;
	}

	public Entity getAmuletEntity() {
		return amuletEntity;
	}

	public void setAmuletEntity(Entity amuletEntity) {
		this.amuletEntity = amuletEntity;
	}

	public Entity getGloveEntity() {
		return gloveEntity;
	}

	public void setGloveEntity(Entity gloveEntity) {
		this.gloveEntity = gloveEntity;
	}

	public Entity getShirtEntity() {
		return shirtEntity;
	}

	public void setShirtEntity(Entity shirtEntity) {
		this.shirtEntity = shirtEntity;
	}

	public Entity getTrousersEntity() {
		return trousersEntity;
	}

	public void setTrousersEntity(Entity trousersEntity) {
		this.trousersEntity = trousersEntity;
	}

	public Entity getHairEntity() {
		return hairEntity;
	}

	public void setHairEntity(Entity hairEntity) {
		this.hairEntity = hairEntity;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}
	
	public String getDamagedBy() {
		return damagedBy;
	}
	
	public void setDamagedBy(String damagedBy) {
		this.damagedBy = damagedBy;
	}
	
	public String getHealedBy() {
		return healedBy;
	}
	
	public void setHealedBy(String healedBy) {
		this.healedBy = healedBy;
	}
}
