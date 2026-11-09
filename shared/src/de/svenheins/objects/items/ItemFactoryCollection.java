/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items;

import java.math.BigInteger;
import java.util.HashMap;

import de.svenheins.main.GameStates;
import de.svenheins.main.TileSize;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.items.equipment.AmuletFactory;
import de.svenheins.objects.items.equipment.BodyArmorFactory;
import de.svenheins.objects.items.equipment.FeetFactory;
import de.svenheins.objects.items.equipment.GloveFactory;
import de.svenheins.objects.items.equipment.HelmetFactory;
import de.svenheins.objects.items.equipment.ShieldFactory;
import de.svenheins.objects.items.equipment.ShirtFactory;
import de.svenheins.objects.items.equipment.TrousersFactory;
import de.svenheins.objects.items.equipment.WeaponFactory;
import de.svenheins.objects.items.food.FoodFactory;
import de.svenheins.objects.items.materials.ArtifactStone;
import de.svenheins.objects.items.materials.Campfire;
import de.svenheins.objects.items.materials.MaterialFactory;
import de.svenheins.objects.items.materials.Shovel;
import de.svenheins.objects.items.materials.Soil;
import de.svenheins.objects.items.materials.Stone;
import de.svenheins.objects.items.materials.Wood;

public class ItemFactoryCollection {
//	private AmuletFactory amuletFactory;
//	private BodyArmorFactory bodyArmorFactor;
//	private FeetFactory feetFactory;
//	private GloveFactory gloveFactory;
//	private HelmetFactory helmetFactory;
//	private ShieldFactory shieldFactory;
//	private ShirtFactory shirtFactory;
//	private TrousersFactory trousersFactory;
//	private WeaponFactory weaponFactory;
//	private MaterialFactory materialFactory;
//	
//	public ItemFactoryCollection() {
//		this.setAmuletFactory(new AmuletFactory());
//		this.setBodyArmorFactor(new BodyArmorFactory());
//		this.setFeetFactory(new FeetFactory());
//		this.setGloveFactory(new GloveFactory());
//		this.setHelmetFactory(new HelmetFactory());
//		this.setShieldFactory(new ShieldFactory());
//		this.setShirtFactory(new ShirtFactory());
//		this.setTrousersFactory(new TrousersFactory());
//		this.setWeaponFactory(new WeaponFactory());
//		this.setMaterialFactory(new MaterialFactory());
//		
//	}
	private static AmuletFactory amuletFactory;
	private static BodyArmorFactory bodyArmorFactory;
	private static FeetFactory feetFactory;
	private static GloveFactory gloveFactory;
	private static HelmetFactory helmetFactory;
	private static ShieldFactory shieldFactory;
	private static ShirtFactory shirtFactory;
	private static TrousersFactory trousersFactory;
	private static WeaponFactory weaponFactory;
	private static MaterialFactory materialFactory;
	private static FoodFactory foodFactory;
	public static HashMap<String, ITEMCODE> itemCodeList;
	
//	public ItemFactoryCollection() {
//		this.setAmuletFactory(new AmuletFactory());
//		this.setBodyArmorFactor(new BodyArmorFactory());
//		this.setFeetFactory();
//		this.setGloveFactory();
//		this.setHelmetFactory();
//		this.setShieldFactory();
//		this.setShirtFactory();
//		this.setTrousersFactory(new TrousersFactory());
//		this.setWeaponFactory();
//		this.setMaterialFactory();
//		
//	}
	
	public static void init() {
		amuletFactory = new AmuletFactory();
		bodyArmorFactory = new BodyArmorFactory();
		feetFactory = new FeetFactory();
		gloveFactory = new GloveFactory();
		helmetFactory = new HelmetFactory();
		shieldFactory = new ShieldFactory();
		shirtFactory = new ShirtFactory();
		trousersFactory = new TrousersFactory();
		weaponFactory = new WeaponFactory();
		materialFactory = new MaterialFactory();
		foodFactory = new FoodFactory();
		itemCodeList = getItemCodes();
	}
	
	/** return the correct itemFactory depending on ItemCode */
	public static ItemFactory getItemFactory(ITEMCODE itemCode) {
		ItemFactory itemFactory = null;
		switch (itemCode) {
		case AMULET: {
			itemFactory = amuletFactory;
			break;
		}
		case BODY: {
			itemFactory = bodyArmorFactory;
			break;
		}
		case FEET: {
			itemFactory = feetFactory;
			break;
		}
		case FINGER: {
			itemFactory = null;
			break;
		}
		case GLOVE: {
			itemFactory = gloveFactory;
			break;
		}
		case HELMET: {
			itemFactory = helmetFactory;
			break;
		}
		case SHIELD: {
			itemFactory = shieldFactory;
			break;
		}
		case SHIRT: {
			itemFactory = shirtFactory;
			break;
		}
		case STONE: {
			itemFactory = materialFactory;
			break;
		}
		case ARTIFACTSTONE: {
			itemFactory = materialFactory;
			break;
		}
		case TROUSERS: {
			itemFactory = trousersFactory;
			break;
		}
		case WEAPON: {
			itemFactory = weaponFactory;
			break;
		}
		case WOOD: {
			itemFactory = materialFactory;
			break;
		}
		case CAMPFIRE: {
			itemFactory = materialFactory;
			break;
		}
		case SHOVEL: {
			itemFactory = materialFactory;
			break;
		}
		case SOIL: {
			itemFactory = materialFactory;
			break;
		}
		case MATERIAL: {
			itemFactory = materialFactory;
			break;
		}
		case FOOD: {
			itemFactory = foodFactory;
			break;
		}
		case APPLE: {
			itemFactory = foodFactory;
			break;
		}
		case SMALLPOTIONOFLIFE: {
			itemFactory = foodFactory;
			break;
		}
		default: {
			itemFactory = null;
			break;
		}
			
		}
		return itemFactory;
	}
	
	public static Item getItem(ITEMCODE itemCode, BigInteger id, String name, String comment, String room, int count, float x, float y, long creationTime, float[] states) {
		Item retItem = null;
		ItemFactory itemFactory = getItemFactory(itemCode);
		/** now get the item of the correct factory */
		if (itemFactory != null) {
			retItem = itemFactory.getItem(name);
			retItem.setComment(comment);
			retItem.setId(id);
			retItem.setRoom(room);
			retItem.setCount(count);
			retItem.setX(x);
			retItem.setY(y);
			retItem.getEntity().setX(x);
			retItem.getEntity().setY(y);
			retItem.setCreationTime(creationTime);
			retItem.setAttributes(states);
		}
		return retItem;
	}
	
	/** create a random Item, depending on level */
	public static Item createRandomItem(Integer level, BigInteger id, String room, float x, float y) {
		Item retItem = null;
		int randomDice = (int) (Math.random()*ITEMCODE.values().length);
		ITEMCODE itemCode = ITEMCODE.values()[randomDice];
		ItemFactory itemFactory = getItemFactory(itemCode);
		if (itemFactory != null) {
//			System.out.println("ItemFactory = "+itemCode);
			retItem = itemFactory.getRandom(level);
//			retItem.setCount(1);
			if (retItem != null) {
				retItem.setId(id);
				retItem.setRoom(room);
				retItem.setX(x);
				retItem.setY(y);
				retItem.getEntity().setX(x);
				retItem.getEntity().setY(y);
				retItem.rerollAttributes();
			}
		} else retItem = null;
		return retItem;
	}
	
	public static Item createItem(ITEMCODE itemCode, String room, TileSize tileSize, BigInteger id, float x, float y) {
		Item retItem = null;
		int count =1;
		switch (tileSize) {
		case ONE: count = 1; break;
		case TWO: count = 2; break;
		case THREE: count = 3;  break;
		case TEN: count = 10; break;
		case TINY: count = (int)(Math.random()*2 + 2); break;
		case SMALL: count = (int)(Math.random()*5 + 5); break;
		case MEDIUM: count = (int)(Math.random()*10 + 10); break;
		case BIG: count = (int)(Math.random()*20 + 20); break;
		case HUGE: count = (int)(Math.random()*20 + 30); break;
		case FULL: count = GameStates.itemMaterialCapacity;  break;
		default: break;
		}
		
		if (itemCode != null) {
		switch (itemCode) {
		case WOOD:
			retItem = new Wood(id, room, x, y);
			retItem.setCount(count);
			break;
		case STONE:
			retItem = new Stone(id, room, x, y);
			retItem.setCount(count);
			break;
		case ARTIFACTSTONE:
			retItem = new ArtifactStone(id, room, x, y);
			retItem.setCount(count);
			break;
		case SHOVEL:
			retItem = new Shovel(id, room, x, y);
			retItem.setCount(count);
			break;
		case SOIL:
			retItem = new Soil(id, room, x, y);
			retItem.setCount(count);
			break;
		case CAMPFIRE:
			retItem = new Campfire(id, room, x, y);
			retItem.setCount(count);
			break;
//		case FOOD:
//			retItem = new Campfire(id, room, x, y);
//			retItem.setCount(count);
//			break;
		default: ;
		}
		}
		return retItem;
	}

	public AmuletFactory getAmuletFactory() {
		return amuletFactory;
	}

//	public void setAmuletFactory(AmuletFactory amuletFactory) {
//		this.amuletFactory = amuletFactory;
//	}

	public BodyArmorFactory getBodyArmorFactor() {
		return bodyArmorFactory;
	}

//	public void setBodyArmorFactor(BodyArmorFactory bodyArmorFactor) {
//		this.bodyArmorFactor = bodyArmorFactor;
//	}

	public FeetFactory getFeetFactory() {
		return feetFactory;
	}

//	public void setFeetFactory(FeetFactory feetFactory) {
//		this.feetFactory = feetFactory;
//	}

	public GloveFactory getGloveFactory() {
		return gloveFactory;
	}

//	public void setGloveFactory(GloveFactory gloveFactory) {
//		this.gloveFactory = gloveFactory;
//	}

	public HelmetFactory getHelmetFactory() {
		return helmetFactory;
	}

//	public void setHelmetFactory(HelmetFactory helmetFactory) {
//		this.helmetFactory = helmetFactory;
//	}

	public ShieldFactory getShieldFactory() {
		return shieldFactory;
	}

//	public void setShieldFactory(ShieldFactory shieldFactory) {
//		this.shieldFactory = shieldFactory;
//	}

	public ShirtFactory getShirtFactory() {
		return shirtFactory;
	}

//	public void setShirtFactory(ShirtFactory shirtFactory) {
//		this.shirtFactory = shirtFactory;
//	}

	public TrousersFactory getTrousersFactory() {
		return trousersFactory;
	}

//	public void setTrousersFactory(TrousersFactory trousersFactory) {
//		this.trousersFactory = trousersFactory;
//	}

	public WeaponFactory getWeaponFactory() {
		return weaponFactory;
	}

//	public void setWeaponFactory(WeaponFactory weaponFactory) {
//		this.weaponFactory = weaponFactory;
//	}

	public MaterialFactory getMaterialFactory() {
		return materialFactory;
	}
	
	public static HashMap<String, ITEMCODE> getItemCodes() {
		HashMap<String, ITEMCODE> retList = new HashMap<String, ITEMCODE>();
		for (String itemName: amuletFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.AMULET);
		}
		for (String itemName: bodyArmorFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.BODY);
		}
		for (String itemName: feetFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.FEET);
		}
		for (String itemName: gloveFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.GLOVE);
		}
		for (String itemName: helmetFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.HELMET);
		}
		for (String itemName: shieldFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.SHIELD);
		}
		for (String itemName: shirtFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.SHIRT);
		}
		for (String itemName: trousersFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.TROUSERS);
		}
		for (String itemName: weaponFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.WEAPON);
		}
		for (String itemName: materialFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.MATERIAL);
		}
		for (String itemName: foodFactory.itemList.keySet()) {
			retList.put(itemName, ITEMCODE.FOOD);
		}
		return retList;
	}

	public static FoodFactory getFoodFactory() {
		return foodFactory;
	}

	public static void setFoodFactory(FoodFactory foodFactory) {
		ItemFactoryCollection.foodFactory = foodFactory;
	}

//	public void setMaterialFactory(MaterialFactory materialFactory) {
//		this.materialFactory = materialFactory;
//	}
}
