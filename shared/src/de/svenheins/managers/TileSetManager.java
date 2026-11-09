/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.util.ArrayList;
import java.util.HashMap;
import de.svenheins.main.GameStates;
import de.svenheins.main.ItemProperty;
import de.svenheins.main.PlayerProperty;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.ItemFactory;
import de.svenheins.objects.items.ItemFactoryCollection;

public class TileSetManager {
	public static TileSetManager manager = new TileSetManager();
	private HashMap<String, TileSet> map = new HashMap<String, TileSet>();
	
	public void add(String name, TileSet tileSet) {
		if (tileSet != null) {
			map.put(name, tileSet);
		}
	}
	
	public TileSet getTileSet(String src) {
		if(map.get(src) != null) return map.get(src);
		else {
//			BufferedImage img;
//			try {
//				img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
			TileSet s = new TileSet(src, src, (int)GameStates.getPlayerProperty("standardPlayer", PlayerProperty.WIDTH),(int) GameStates.getPlayerProperty("standardPlayer", PlayerProperty.HEIGHT));
			map.put(src, s);
			return s;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
			
		}
	}
	
	public TileSet getTileSetByPath(String src, String path) {
		if(map.get(src) != null) return map.get(src);
		else {
//			BufferedImage img;
//			try {
//				img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
			TileSet s = new TileSet(path, src, (int)GameStates.getPlayerProperty("standardPlayer", PlayerProperty.WIDTH),(int) GameStates.getPlayerProperty("standardPlayer", PlayerProperty.HEIGHT));
			map.put(src, s);
			return s;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
			
		}
	}
	
	public TileSet getTileSet(String src, int width, int height) {
		if(map.get(src) != null) return map.get(src);
		else {
//			BufferedImage img;
//			try {
//				img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
			TileSet s = new TileSet(src, src, width, height);
//			System.out.println(GameStates.resourcePath+"images/"+src);
			map.put(src, s);
			return s;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
			
		}
	}
	
	
	public TileSet getTileSet(TileSet tile) {
		String idString = tile.getName();
		if(map.get(idString) != null) return map.get(idString);
		else {
			map.put(idString, tile);
//			System.out.println(idString);
			return map.get(idString);
		}
	}
	
	public void init() {
		TileSet tileSetStandardPlayer = new TileSet(GameStates.tileNameStandardPlayer,GameStates.nameStandardPlayer, (int)GameStates.getPlayerProperty(GameStates.nameStandardPlayer, PlayerProperty.WIDTH), (int)GameStates.getPlayerProperty(GameStates.nameStandardPlayer, PlayerProperty.HEIGHT));
		TileSet tileSetDwarf = new TileSet(GameStates.tileNameDwarf,GameStates.nameDwarf, (int)GameStates.getPlayerProperty(GameStates.nameDwarf, PlayerProperty.WIDTH), (int)GameStates.getPlayerProperty(GameStates.nameDwarf, PlayerProperty.HEIGHT));
		TileSetManager.manager.add(GameStates.nameStandardPlayer, tileSetStandardPlayer);
		TileSetManager.manager.add(GameStates.nameDwarf, tileSetDwarf);
		
		TileSet tileSetRabbit = new TileSet(GameStates.tileNameRabbit,GameStates.nameRabbit, (int)GameStates.getPlayerProperty(GameStates.nameRabbit, PlayerProperty.WIDTH), (int)GameStates.getPlayerProperty(GameStates.nameRabbit, PlayerProperty.HEIGHT));
		TileSetManager.manager.add(GameStates.nameRabbit, tileSetRabbit);
		
		TileSet lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName00,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName00, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName01,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName01, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName02,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName02, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName03,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName03, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName04,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName04, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName05,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName05, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName06,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName06, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName07,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName07, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName08,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName08, lightTile);
		 lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName09,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName09, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName10,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName10, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName11,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName11, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName12,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName12, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName13,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName13, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName14,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName14, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName15,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName15, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName16,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName16, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName17,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName17, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName18,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName18, lightTile);
		lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName19,GameStates.lightTileWidth, GameStates.lightTileHeight);
		TileSetManager.manager.add(GameStates.lightTileSetName19, lightTile);
		
		TileSet fireTile = TileSetManager.manager.getTileSet(GameStates.fireTileSetName,GameStates.fireTileWidth, GameStates.fireTileHeight);
		TileSetManager.manager.add(GameStates.fireTileSetName, fireTile);
//		int wolfWidthFront = 32;
//		int wolfGeneralHeight = 32;
//		int[] x = new int[20*4];
//		int[] y = new int[20*4];
//		int[] wolfWidth = new int[20*4];
//		int[] wolfHeight = new int[20*4];
//		int xPos = 0;
//		int xAdd = 0;
//		int yAdd = 32;
//		int index = 0;
//		for (int i = 0; i < 20; i++) {
//			for (int j = 0 ; j < 4; j++) {
//				x[index] = xPos;
//				y[index] = j*(wolfGeneralHeight+yAdd);
//				wolfWidth[index] = wolfWidthFront + xAdd; 
//				wolfHeight[index] = wolfGeneralHeight +yAdd; 
////				System.out.println("index = "+index+" x = "+x[index]+" y = "+y[index]+" width = "+wolfWidth[index]+" height = "+wolfHeight[index]);
//				index++;
//			}
//			xPos +=32+xAdd;
//			if (i%4 == 1 || i% 4 == 2) {
//				xAdd=32;
//				yAdd = 0;
//			} else {
//				xAdd = 0;
//				yAdd = 32;
//			}
//			
//		}
		TileSet tileSetWolf = getWolfTileSet(GameStates.tileNameWolf, GameStates.nameWolf);
		TileSetManager.manager.add(GameStates.nameWolf, tileSetWolf);
		TileSet tileSetGreyWolf = getWolfTileSet(GameStates.tileNameGreyWolf, GameStates.nameGreyWolf);
		TileSetManager.manager.add(GameStates.nameGreyWolf, tileSetGreyWolf);
		TileSet tileSetDarkWolf = getWolfTileSet(GameStates.tileNameDarkWolf, GameStates.nameDarkWolf);
		TileSetManager.manager.add(GameStates.nameDarkWolf, tileSetDarkWolf);
		
		TileSet tileSetWolfBig = getBigWolfTileSet(GameStates.tileNameWolfBig, GameStates.nameWolfBig);
		TileSetManager.manager.add(GameStates.nameWolfBig, tileSetWolfBig);
		TileSet tileSetGreyWolfBig = getBigWolfTileSet(GameStates.tileNameGreyWolfBig, GameStates.nameGreyWolfBig);
		TileSetManager.manager.add(GameStates.nameGreyWolfBig, tileSetGreyWolfBig);
		TileSet tileSetDarkWolfBig = getBigWolfTileSet(GameStates.tileNameDarkWolfBig, GameStates.nameDarkWolfBig);
		TileSetManager.manager.add(GameStates.nameDarkWolfBig, tileSetDarkWolfBig);
		
		ItemFactory itemFactory;
		String namePlayer = GameStates.nameStandardPlayer;
		for (ITEMCODE itemCode : ITEMCODE.values()) {
			itemFactory = ItemFactoryCollection.getItemFactory(itemCode);
			ItemProperty itemPropertyWidth = GameStates.getItemPropertyType(itemCode, "width");
			ItemProperty itemPropertyHeight = GameStates.getItemPropertyType(itemCode, "height");
			if (itemPropertyWidth != null && itemPropertyHeight != null){
				for (String name: itemFactory.getItemList().keySet()) {
					TileSet tileSetEquipStandardPlayer = new TileSet( GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ namePlayer + name+".png", GameStates.itemEquipPrefix + namePlayer+name, GameStates.getItemProperty(namePlayer, itemPropertyWidth), GameStates.getItemProperty(namePlayer, itemPropertyHeight));
					TileSetManager.manager.add(tileSetEquipStandardPlayer.getName(), tileSetEquipStandardPlayer);
				}
			}
		}
		
		/** do this for every playerEntity (dwarf, elve, child and so on */
		/** namePlayer = GameStates.nameDwarf ... */

		
		TileSet tileSetInfoIcon = new TileSet(GameStates.agentInfoIconTileSetName,GameStates.agentInfoIconName, GameStates.agentInfoIconTileSetWidth,GameStates.agentInfoIconTileSetHeight);
		TileSetManager.manager.add(GameStates.agentInfoIconName, tileSetInfoIcon);
		TileSet tileSetClouds = new TileSet(GameStates.tileNameCloud,GameStates.cloudName, GameStates.cloudWidth,GameStates.cloudHeight);
		TileSetManager.manager.add(GameStates.cloudName, tileSetClouds);
		TileSet tileSetCloudsOverlay = new TileSet(GameStates.tileNameCloudOverlay,GameStates.cloudNameOverlay, GameStates.cloudWidth,GameStates.cloudHeight);
		TileSetManager.manager.add(GameStates.cloudNameOverlay, tileSetCloudsOverlay);
		
		TileSet tileSetStarBlue = new TileSet(GameStates.starBlueTileName,GameStates.starBlueName, GameStates.starBlueWidth,GameStates.starBlueHeight);
		TileSetManager.manager.add(GameStates.starBlueName, tileSetStarBlue);
		TileSet tileSetStarBlue3 = new TileSet(GameStates.starBlue3TileName,GameStates.starBlue3Name, GameStates.starBlueWidth,GameStates.starBlueHeight);
		TileSetManager.manager.add(GameStates.starBlue3Name, tileSetStarBlue3);
		TileSet tileSetStarBlue4 = new TileSet(GameStates.starBlue4TileName,GameStates.starBlue4Name, GameStates.starBlueWidth,GameStates.starBlueHeight);
		TileSetManager.manager.add(GameStates.starBlue4Name, tileSetStarBlue4);
		TileSet tileSetStarBlue5 = new TileSet(GameStates.starBlue5TileName,GameStates.starBlue5Name, GameStates.starBlueWidth,GameStates.starBlueHeight);
		TileSetManager.manager.add(GameStates.starBlue5Name, tileSetStarBlue5);
		
		TileSet tileSetStarBlueBig = new TileSet(GameStates.starBlueBigTileName,GameStates.starBlueBigName, GameStates.starBlueBigWidth,GameStates.starBlueBigHeight);
		TileSetManager.manager.add(GameStates.starBlueBigName, tileSetStarBlueBig);
		TileSet tileSetStarBlueBig2 = new TileSet(GameStates.starBlueBig2TileName,GameStates.starBlueBig2Name, GameStates.starBlueBigWidth,GameStates.starBlueBigHeight);
		TileSetManager.manager.add(GameStates.starBlueBig2Name, tileSetStarBlueBig2);
		TileSet tileSetStarBlueBig3 = new TileSet(GameStates.starBlueBig3TileName,GameStates.starBlueBig3Name, GameStates.starBlueBigWidth,GameStates.starBlueBigHeight);
		TileSetManager.manager.add(GameStates.starBlueBig3Name, tileSetStarBlueBig3);
		TileSet tileSetStarBlueBig4 = new TileSet(GameStates.starBlueBig4TileName,GameStates.starBlueBig4Name, GameStates.starBlueBigWidth,GameStates.starBlueBigHeight);
		TileSetManager.manager.add(GameStates.starBlueBig4Name, tileSetStarBlueBig4);
		
		TileSet tileSetSky = new TileSet(GameStates.tileNameSky,GameStates.skyName, GameStates.skyWidth,GameStates.skyHeight);
		TileSetManager.manager.add(GameStates.skyName, tileSetSky);
		
//		/** add itemEquipGraphics  STANDARDPLAYER*/
//		/** Cloak */
//		TileSet tileSetEquipStandardPlayerCloak = new TileSet( GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer + "Cloak.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+"Cloak", GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHARMOR), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTARMOR));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerCloak.getName(), tileSetEquipStandardPlayerCloak);
//		/** Shoe */
//		TileSet tileSetEquipStandardPlayerShoe = new TileSet(	GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "Shoe.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "Shoe",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHFEET), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTFEET));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerShoe.getName(),tileSetEquipStandardPlayerShoe);
//		/** Leatherpants */
//		TileSet tileSetEquipStandardPlayerLeatherpants = new TileSet(	GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "Leatherpants.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "Leatherpants",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHTROUSERS), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTTROUSERS));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerLeatherpants.getName(),tileSetEquipStandardPlayerLeatherpants);
//		/** Greenshirt */
//		TileSet tileSetEquipStandardPlayerGreenshirt = new TileSet(	GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "Greenshirt.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "Greenshirt",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHSHIRT), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTSHIRT));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerGreenshirt.getName(),tileSetEquipStandardPlayerGreenshirt);
//		/** LeatherGlove */
//		TileSet tileSetEquipStandardPlayerLeatherGlove = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "LeatherGlove.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "LeatherGlove",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHGLOVE), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTGLOVE));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerLeatherGlove.getName(),tileSetEquipStandardPlayerLeatherGlove);
//		/** BlondHair */
//		TileSet tileSetEquipStandardPlayerBlondHair = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "BlondHair.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "BlondHair",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHHELMET), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTHELMET));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerBlondHair.getName(),tileSetEquipStandardPlayerBlondHair);
//		/** ShortBlondHair */
//		TileSet tileSetEquipStandardPlayerShortBlondHair = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "ShortBlondHair.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "ShortBlondHair",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHHELMET), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTHELMET));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerShortBlondHair.getName(),tileSetEquipStandardPlayerShortBlondHair);
//		
//		/** LeatherHelmet */
//		TileSet tileSetEquipStandardPlayerLeatherHelmet = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "LeatherHelmet.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "LeatherHelmet",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHHELMET), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTHELMET));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerLeatherHelmet.getName(),tileSetEquipStandardPlayerLeatherHelmet);
//		/** WoodenSword */
//		TileSet tileSetEquipStandardPlayerWoodenSword = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "WoodenSword.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "WoodenSword",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHWEAPON), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTWEAPON));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerWoodenSword.getName(),tileSetEquipStandardPlayerWoodenSword);
//		/** IronSword */
//		TileSet tileSetEquipStandardPlayerIronSword = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "IronSword.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "IronSword",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHWEAPON), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTWEAPON));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerIronSword.getName(),tileSetEquipStandardPlayerIronSword);
//		/** LeatherShield */
//		TileSet tileSetEquipStandardPlayerLeatherShield = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "LeatherShield.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "LeatherShield",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHSHIELD), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTSHIELD));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerLeatherShield.getName(),tileSetEquipStandardPlayerLeatherShield);
//		/** GoldenAmulet */
//		TileSet tileSetEquipStandardPlayerGoldenAmulet = new TileSet(GameStates.standardTilePathItems + GameStates.itemEquipPrefix+ GameStates.nameStandardPlayer+ "GoldenAmulet.png", GameStates.itemEquipPrefix + GameStates.nameStandardPlayer+ "GoldenAmulet",GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.WIDTHAMULET), GameStates.getItemProperty(GameStates.nameStandardPlayer, ItemProperty.HEIGHTAMULET));
//		TileSetManager.manager.add(tileSetEquipStandardPlayerGoldenAmulet.getName(),tileSetEquipStandardPlayerGoldenAmulet);
//		
//		
//		
//		/** add itemEquipGraphics DWARF*/
//		TileSet tileSetEquipDwarfCloak = new TileSet(GameStates.standardTilePathItems+GameStates.itemEquipPrefix+GameStates.nameDwarf+"Cloak.png", GameStates.itemEquipPrefix+GameStates.nameDwarf+"Cloak", GameStates.getItemProperty(GameStates.nameDwarf, ItemProperty.WIDTHARMOR), GameStates.getItemProperty(GameStates.nameDwarf, ItemProperty.HEIGHTARMOR));
//		TileSetManager.manager.add(tileSetEquipDwarfCloak.getName(), tileSetEquipDwarfCloak);
//		/** Shoe */
//		TileSet tileSetEquipDwarfShoe = new TileSet(GameStates.standardTilePathItems+GameStates.itemEquipPrefix+GameStates.nameDwarf+"Shoe.png", GameStates.itemEquipPrefix+GameStates.nameDwarf+"Shoe", GameStates.getItemProperty(GameStates.nameDwarf, ItemProperty.WIDTHFEET), GameStates.getItemProperty(GameStates.nameDwarf, ItemProperty.HEIGHTFEET));
//		TileSetManager.manager.add(tileSetEquipDwarfShoe.getName(), tileSetEquipDwarfShoe);
//		
	}
	
	public TileSet getWolfTileSet(String tileNameWolf, String nameWolf) {
		int wolfWidthFront = 18; // 46
		int wolfGeneralHeight = 34; // 52
		int[] x = new int[21*4];
		int[] y = new int[21*4];
		int[] wolfWidth = new int[21*4];
		int[] wolfHeight = new int[21*4];
		int xPos = 0;
		int xAdd = 0;
		int yAdd = 18;
		int index = 0;
		for (int i = 0; i < 21; i++) {
			for (int j = 0 ; j < 4; j++) {
				x[index] = xPos;
				y[index] = j*(wolfGeneralHeight+yAdd);
				wolfWidth[index] = wolfWidthFront + xAdd; 
				wolfHeight[index] = wolfGeneralHeight +yAdd; 
//				System.out.println("index = "+index+" x = "+x[index]+" y = "+y[index]+" width = "+wolfWidth[index]+" height = "+wolfHeight[index]);
				index++;
			}
			xPos +=wolfWidthFront+xAdd;
			if (i%4 == 1 || i% 4 == 2) {
				xAdd=28;
				yAdd = 0;
			} else {
				xAdd = 0;
				yAdd = 18;
			}
			
		}
		TileSet tileSetWolf = new TileSet(tileNameWolf,nameWolf,x, y, wolfWidth, wolfHeight); 
		return tileSetWolf;
	}
	
	public TileSet getBigWolfTileSet(String tileNameWolf, String nameWolf) {
		int wolfWidthFront = 36; // 46
		int wolfGeneralHeight = 68; // 52
		int[] x = new int[21*4];
		int[] y = new int[21*4];
		int[] wolfWidth = new int[21*4];
		int[] wolfHeight = new int[21*4];
		int xPos = 0;
		int xAdd = 0;
		int yAdd = 36;
		int index = 0;
		for (int i = 0; i < 21; i++) {
			for (int j = 0 ; j < 4; j++) {
				x[index] = xPos;
				y[index] = j*(wolfGeneralHeight+yAdd);
				wolfWidth[index] = wolfWidthFront + xAdd; 
				wolfHeight[index] = wolfGeneralHeight +yAdd; 
//				System.out.println("index = "+index+" x = "+x[index]+" y = "+y[index]+" width = "+wolfWidth[index]+" height = "+wolfHeight[index]);
				index++;
			}
			xPos +=wolfWidthFront+xAdd;
			if (i%4 == 1 || i% 4 == 2) {
				xAdd=56;
				yAdd = 0;
			} else {
				xAdd = 0;
				yAdd = 36;
			}
			
		}
		TileSet tileSetWolf = new TileSet(tileNameWolf,nameWolf,x, y, wolfWidth, wolfHeight); 
		return tileSetWolf;
	}
	
	/** return all tileSets as ArrayList<String> */
	public ArrayList<String> getAllTileSetFileNames() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (TileSet tile: map.values()) {
			returnList.add(tile.getFileName());
		}
		return returnList;
	}
	
	/** return all tileSets as ArrayList<String> */
	public ArrayList<String> getAllTileSetNames() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (String name: map.keySet()) {
			returnList.add(name);
		}
		return returnList;
	}

	public TileSet getTileSetByPath(String path) {
		TileSet returnTile = null;
		for (TileSet tile: map.values()) {
			if (tile.getFileName().equals(path)) {
				returnTile = tile;
			}
		}
		return returnTile;
	}
	
	
	public int getSize() {
		return map.size();
	}

}
