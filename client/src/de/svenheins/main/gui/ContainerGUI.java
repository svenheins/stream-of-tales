package de.svenheins.main.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.PlayerManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;
import de.svenheins.objects.Entity;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.items.Container;
import de.svenheins.objects.items.Item;

public class ContainerGUI {
	public Container container;
	private String strValue;
	private int intValue;
//	private int xValue
	private int fontSize;
	private String name;
	private Space backgroundSpace;
	private int x;
	private int y;
	private boolean visible;
	
	public ContainerGUI(Container container, String name, int x, int y, String strValue, int intValue, Space backgroundSpace) {
		this.name = name;
		this.strValue = strValue;
		this.intValue = intValue;
		backgroundSpace.setAllXY(x, y);
		this.backgroundSpace = backgroundSpace;
		this.fontSize = GameStates.inventoryFontSize;
		this.container = container;
		this.setX(x);
		this.setY(y);
//		this.backgroundSpace.setPolyX(x);
//		this.backgroundSpace.setPolyY(y);
//		this.backgroundSpace.setAllXY(x, y);
		this.visible = true;
	}
	
	public ContainerGUI(Container container, String name, int x, int y, String strValue, int intValue) {
		this.name = name;
		this.strValue = strValue;
		this.intValue = intValue;
		this.backgroundSpace = null;
		this.fontSize = GameStates.inventoryFontSize;
		this.container = container;
		this.setX(x);
		this.setY(y);
//		this.backgroundSpace.setPolyX(x);
//		this.backgroundSpace.setPolyY(y);
//		this.backgroundSpace.setAllXY(x, y);
		this.visible = true;
	}
	
//	public void remove(BigInteger index) throws IllegalArgumentException {
//		try {
//			Button button = buttonList.get(index);
//			buttonList.remove(index);
//			idList.remove(index);
//			resort();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		}
//		
//	}
//	
//	public boolean add(Button button) {
//		if (buttonList.containsKey(button.getId())) {
//			return false;
//		} else {
//			buttonList.put(button.getId(), button);
//			idList.add(button.getId());
//			resort();
//			return true;
//		}
//	}
//	
//	/** resort the buttons, use if someone logs out/ logs in */
//	public void resort() {
//		int tempY = 0;
//		for (BigInteger id : idList) {
//			buttonList.get(id).setY(tempY);
//			tempY += buttonList.get(id).getHeight()+GameStates.contextMenuButtonDistYBetweenButtons+fontSize;
//		}
//	}
//	
//	public Button get(BigInteger index){
//		try {
//			return (Button) buttonList.get(index);
//		}
//		catch(IndexOutOfBoundsException e){
//			return null;
//		}
//		
//	}
//	
//	public boolean overwrite(Button button){
//		if (!buttonList.containsKey(button.getId())) {
//			/** do nothing if key is not yet set*/
//			return false;
//		} else {
//			buttonList.put(button.getId(), button);
//			idList.add(button.getId());
//			return true;
//		}
//	}
//	
//	public int size(){
//		return buttonList.size();
//	}
//	
//	public boolean contains(Button button) {
//		return buttonList.containsValue(button);
//	}
	
	public void mouseClick(Point p) {
		Item retItem = null;
		Point correctedPoint = (Point) p.clone();
		correctedPoint.setLocation(p.x - this.getX(), p.y-this.getY());
		
//		BigInteger takeItemID = BigInteger.valueOf(0);
		for (Item item : container.getItemList().values()) {
			if (item.getItemEntity().contains(correctedPoint)) {
				retItem = item;
			}
		}
//		for (int i =0; i < container.getHeight(); i++) {
//			for (int j = 0; j < container.getWidth(); j++) {
//				
//				if (container.getContainerArray()[i][j] != BigInteger.valueOf(-1) && container.getItemList().get(container.getContainerArray()[i][j]).getItemEntity().contains(correctedPoint)) {
//					
//				}
//			}
//		}
		
		if (retItem != null) {
			/** take the item */
			if (GamePanel.gp.getMouseItem() != null) {
				container.getItemList().remove(retItem.getId());
				container.getItemList().put(GamePanel.gp.getMouseItem().getId(), GamePanel.gp.getMouseItem());
				for (int i =0; i < container.getHeight(); i++) {
					for (int j = 0; j < container.getWidth(); j++) {
						if (container.getContainerArray()[i][j].equals(retItem.getId())) {
//							System.out.println("found it"+container.getContainerArray()[i][j]);
							GamePanel.gp.getMouseItem().getItemEntity().setX(GameStates.inventoryDistToFrameX + j*(2*GameStates.inventorySlotDistX+GameStates.inventoryItemTileWidth)+GameStates.inventorySlotDistX);
							GamePanel.gp.getMouseItem().getItemEntity().setY(GameStates.inventoryDistToFrameY + i*(2*GameStates.inventorySlotDistY+GameStates.inventoryFontDistanceY+GameStates.inventoryItemTileHeight)+GameStates.inventorySlotDistY);
							
							container.getContainerArray()[i][j] = GamePanel.gp.getMouseItem().getId();
//							System.out.println(container.getContainerArray()[i][j]);
						}
					}
				}
				
			} else {
//				container.addItem(retItem, retItem.getId());
				if(container.removeItem(retItem.getId())) {
					// OK
				}
				else
				{
					// not OK
				}
				
//				for (int i =0; i < container.getHeight(); i++) {
//					for (int j = 0; j < container.getWidth(); j++) {
//						if (container.getContainerArray()[i][j].equals(retItem.getId())) {
//							container.getContainerArray()[i][j] = BigInteger.valueOf(-1);
//						}
//					}
//				}
			}
			retItem.getItemEntity().setX(p.x);
			retItem.getItemEntity().setY(p.y);
			GamePanel.gp.setMouseItem(retItem);
		} else {
			if (GamePanel.gp.getMouseItem() != null) {
				/** retItem == null  so we did not click onto any valid item */
				Item tooManyItem = container.addItem(GamePanel.gp.getMouseItem()/*, GamePanel.gp.getMouseItem().getId()*/);
				if (tooManyItem != null) {
					if (tooManyItem.getCount() > 0) {
						/** first send to server for the itemList */
						GameWindow.gw.send(ClientMessages.addItem(tooManyItem.getId(), tooManyItem.getItemCode(), tooManyItem.getCount(), tooManyItem.getCapacity(), tooManyItem.getItemEntity().getX(), tooManyItem.getItemEntity().getY(), tooManyItem.getItemEntity().getMX(), tooManyItem.getItemEntity().getMY(), tooManyItem.getName(), tooManyItem.getItemEntity().getTileSet().getFileName(), tooManyItem.getItemEntity().getName()));
//						GameWindow.gw.send(ClientMessages.addItem(tooManyItem.getId()));
						for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
							ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
							try {
								channel.send(ClientMessages.addCompleteItem(ITEMCODE.WOOD, tooManyItem.getId(), "wood", GamePanel.gp.getPlayerEntity().getX(),GamePanel.gp.getPlayerEntity().getY(), tooManyItem.getCount(), new float[1]));
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}
						
		    		}
				}
				GamePanel.gp.setMouseItem(null);
			} // else there is no item in the inventory or in the mouseItem
		}
		
//		for (BigInteger id : idList) {
//			Button button = buttonList.get(id);
//			if (button.contains(p)) {
//				this.setIntValue(button.getIntValue());
//				this.setStrValue(button.getStrValue());
//				if (button.getId() == BigInteger.valueOf(0)) {
//					retEntity = GamePanel.gp.getPlayerEntity();
//				} else {
//					retEntity = PlayerManager.get(button.getId());	
//				}
//				this.setStrValue(retEntity.getName());
				
				/** now change the animation */
//				button.setActive();
//				deactivateOthers(button);
				
//				if (GameWindow.gw.isLoggedIn()) {
//					/** create map folder for the chosen player */
//					String playerName = this.getStrValue();
//				    boolean createMapFolderSccess = (new File(GameStates.standardMapFolder+playerName)).mkdirs();
//				    if (!createMapFolderSccess) {
//				         // Directory creation failed
//				    }
//				    /** delete only the Maps, not the changedPoints */
//				    GameWindow.gw.setGameMasterName(playerName);
//				    GameWindow.gw.initLocalMapFileList(playerName);
//				}
//			}
//		}
	}
//	
//	public void deactivateOthers(Button button) {
//		for(Button otherButton : buttonList.values()) {
//			if (otherButton != button) {
//				otherButton.setInactive();
//			}
//		}
//	}
	
	public void paint(Graphics2D g, ImageObserver iObserver) {
		/** paint backgroundSpace only if it exists */
		if (backgroundSpace != null) {
			g.setPaintMode();
			backgroundSpace.paint(g, 0, 0);
		}
		g.setPaintMode();
		/** loop through the container */
		for (int i = 0 ; i<container.getHeight(); i++) {
			for (int j = 0; j< container.getWidth(); j++){
				if (container.getItemList().keySet().contains(container.getContainerArray()[i][j])) {
//					System.out.print(" "+container.getContainerArray()[i][j] +" ");
					Item item = container.getItemList().get(container.getContainerArray()[i][j]);
//					Button button = get(itemID);
					
					Entity itemEntity = item.getItemEntity();
					g.setColor(new Color(250, 250, 250));
					g.setFont(new Font("Arial", Font.PLAIN , fontSize));
//					drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
					g.drawString(""+item.getCount()/*+" "+item.getName()*/,this.x+GameStates.inventoryFontDistanceX + (int)itemEntity.getX(), this.y+ GameStates.inventoryFontDistanceY +(int) itemEntity.getY() + itemEntity.getHeight() /*+ g.getFontMetrics().getHeight()*/);
					
					g.setPaintMode();
					g.drawImage(itemEntity.getSprite().getImage(), this.x + (int)itemEntity.getX(), this.y +(int) itemEntity.getY(), iObserver);
					
				} else {
					// dont draw anything into the field
//					System.out.println("item "+container.getContainerArray()[i][j] +" is not in container");
				}
			}
		}	
//		System.out.println(" ");
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Space getBackgroundSpace() {
		return backgroundSpace;
	}

	public void setBackgroundSpace(Space backgroundSpace) {
		this.backgroundSpace = backgroundSpace;
	}


	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
//		backgroundSpace.setAllXY(x, backgroundSpace.getY());
	}

	public int getY() {
		return y;
		
	}

	public void setY(int y) {
		this.y = y;
//		backgroundSpace.setAllXY(backgroundSpace.getX(), y);
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
