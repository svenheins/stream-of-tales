package de.svenheins.objects;

import java.math.BigInteger;
import java.util.ArrayList;
import de.svenheins.functions.MyMath;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GameStates;
import de.svenheins.main.Priority;
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
	private float[] attributes;
	private Container inventory;
	private Container inventoryUse;
	private Container equipmentBody;
	private ArrayList<ItemInfluence> itemInfluenceList;
	private float[] totalItemInfluences = new float[AttributeType.values().length];
	private float[] totalAreaInfluences = new float[AttributeType.values().length];
	private float[] tempTotalAreaInfluences = new float[AttributeType.values().length];
	private float[] totalEquipmentInfluences = new float[AttributeType.values().length];
	private float[] totalAttributesInfluences = new float[AttributeType.values().length];
	private float[] totalBaseAttributes = new float[AttributeType.values().length];
	
	/** TileSet-Constructor */
	public PlayerEntity(TileSet tileSet, String name, BigInteger id, float x,
			float y, long animationDelay) {
		super(tileSet, name, id, x, y, animationDelay);
		this.groupName = "";
		this.country = "";
		this.firstServerLogin = System.currentTimeMillis();
		this.experience = 0;
		this.lastSeen = System.currentTimeMillis();
		this.setVisible(false);
		this.setAttributes(new float[AttributeType.values().length]);
		System.out.println("created "+name);
		this.inventory = new Container(GameStates.inventoryWidthPlayer, GameStates.inventoryHeightPlayer, OBJECTCODE.CONTAINER_MAIN, ITEMCODE.ALL);
		this.inventoryUse = new Container(GameStates.inventoryUseWidthPlayer, GameStates.inventoryUseHeightPlayer, OBJECTCODE.CONTAINER_USE, ITEMCODE.ALL);
		this.equipmentBody = new Container(1, 1, OBJECTCODE.CONTAINER_EQUIPMENT_BODY, ITEMCODE.BODY);
		this.setItemInfluenceList(new ArrayList<ItemInfluence>());
		
		this.setTotalAttributesInfluences(new float[AttributeType.values().length]);
		this.setTotalEquipmentInfluences(new float[AttributeType.values().length]);
		this.setTotalItemInfluences(new float[AttributeType.values().length]);
		this.setTempTotalAreaInfluences(new float[AttributeType.values().length]);
		this.setTotalAreaInfluences(new float[AttributeType.values().length]);
		this.setTotalBaseAttributes(new float[AttributeType.values().length]);
		
		totalBaseAttributes[AttributeType.MAXLIFE.ordinal()] = GameStates.playerStartAttributeMaxLife;
		attributes[AttributeType.MAXLIFE.ordinal()] = GameStates.playerStartAttributeMaxLife;
		attributes[AttributeType.LIFE.ordinal()] = GameStates.playerStartAttributeMaxLife;
		totalBaseAttributes[AttributeType.MAXMANA.ordinal()] = GameStates.playerStartAttributeMaxMana;
		attributes[AttributeType.MAXMANA.ordinal()] = GameStates.playerStartAttributeMaxMana;
		attributes[AttributeType.MANA.ordinal()] = GameStates.playerStartAttributeMaxMana;
		this.calculateTotalAttributeInfluence();
		this.calculateTotalEquipmentInfluence();
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
		itemInfluenceList.add(0, itemInfluence);
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
			/** apply influence to Life and Mana */
			tempAttributes[AttributeType.LIFE.ordinal()] += attributes[AttributeType.LIFE.ordinal()];
			tempAttributes[AttributeType.MANA.ordinal()] += attributes[AttributeType.MANA.ordinal()];
			/** regenerate life and mana */
			tempAttributes[AttributeType.LIFE.ordinal()] = regenerate(tempAttributes[AttributeType.LIFE.ordinal()], tempAttributes[AttributeType.LIFEREGENERATION.ordinal()], duration);
			tempAttributes[AttributeType.MANA.ordinal()] = regenerate(tempAttributes[AttributeType.MANA.ordinal()], tempAttributes[AttributeType.MANAREGENERATION.ordinal()], duration);
			/** normalize life and mana */
			tempAttributes[AttributeType.LIFE.ordinal()] = normalizeToMinMax(tempAttributes[AttributeType.LIFE.ordinal()], 0, tempAttributes[AttributeType.MAXLIFE.ordinal()]);
			tempAttributes[AttributeType.MANA.ordinal()] = normalizeToMinMax(tempAttributes[AttributeType.MANA.ordinal()], 0, tempAttributes[AttributeType.MAXMANA.ordinal()]);
			
			/** update attributes */
			this.setAttributes(tempAttributes);
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
			/** only apply itemInfluence if it is active! */
			/** first check if the influence has not ended yet and is no instant influence */
			if ( ((itemInfluence.getTimeBegin() <= timeNow) && (itemInfluence.getTimeEnd() >= timeNow)) || (itemInfluence.getTimeBegin() == itemInfluence.getTimeEnd())) {
				/** ACTIVE INFLUENCE: the influence is either continuous, has started and NOT yet ended OR the influence is instantaneous */
				System.out.println("active itemInfluence");
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
	public void calculateTotalEquipmentInfluence() {
		/** body */
		float[] tempTotalEquipmentInfluences = new float[AttributeType.values().length];
		for (Item item: this.getEquipmentBody().getItemList().values()) {
			if (tempTotalEquipmentInfluences.length == item.getAttributes().length) {
				tempTotalEquipmentInfluences = MyMath.addFloatArrays(tempTotalEquipmentInfluences, item.getAttributes());
			}
		}
		totalEquipmentInfluences = tempTotalEquipmentInfluences;
//		System.out.println("equipment finished");
		/** and so on */
	}
	
	/** calculate the actual total influence that is done by all equipment */ 
	public void calculateTotalAttributeInfluence() {
		/** which attributes effect others?
		 * MaxLife -> LifeRegeneration
		 * MaxMana -> ManaRegeneration
		 * */
		this.totalAttributesInfluences[AttributeType.LIFEREGENERATION.ordinal()] = this.attributes[AttributeType.MAXLIFE.ordinal()] * GameStates.influenceOfMaxLifeToLifeRegeneration;
		this.totalAttributesInfluences[AttributeType.MANAREGENERATION.ordinal()] = this.attributes[AttributeType.MAXMANA.ordinal()] * GameStates.influenceOfMaxManaToManaRegeneration;
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

}
