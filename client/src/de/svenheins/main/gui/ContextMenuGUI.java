package de.svenheins.main.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.ItemManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.Space;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.items.equipment.bodyarmors.Cloak;

public class ContextMenuGUI {
	public HashMap<BigInteger, Button> buttonList = new HashMap<BigInteger, Button>();
	public List<BigInteger> idList = new ArrayList<BigInteger>();
	private Space backgroundSpace;
	private LocalObject localObject;
	private boolean visible;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public ContextMenuGUI() {
		setVisible(false);
	}
	
	/** show the contextMenu for the corresponding entity */
	public void create(LocalObject localObject, int x, int y) {
//		int countButtons = 0;
		this.setX(x);
		this.setY(y);
		ArrayList<Polygon> contextMenuSpacePolygon = new ArrayList<Polygon>();
		
		if (localObject != this.localObject) {	
			buttonList.clear();
			idList.clear();
			int guiHeight = GameStates.contextMenuButtonDistY;
			if (localObject instanceof PlayerEntity) {
				/** add one button */
//				System.out.println(((PlayerEntity) localObject).getName());
				Button gmButton = standardButton(BigInteger.valueOf(0), localObject.getName(), 0, GameStates.contextMenuButtonDistX, guiHeight, "Gamemaster");
				if (this.add(gmButton)) ; else System.out.println("couldn't add");
				guiHeight += gmButton.getHeight() + GameStates.contextMenuButtonDistY;
//				/** add next button */
//				Button secondButton = standardButton(BigInteger.valueOf(1), "abc", 0, GameStates.contextMenuButtonDistX, guiHeight, "Button2");
//				if (this.add(secondButton)) ; else System.out.println("couldn't add");
//				guiHeight += secondButton.getHeight() + GameStates.contextMenuButtonDistY;
				
				/** if its me */
				/** add next button */
				Button animationButton = standardButton(BigInteger.valueOf(20), "abc", 0, GameStates.contextMenuButtonDistX, guiHeight, "go to bed");
				if (this.add(animationButton)) ; else System.out.println("couldn't add");
				guiHeight += animationButton.getHeight() + GameStates.contextMenuButtonDistY;
				
				/** add next button */
				Button cloakButton = standardButton(BigInteger.valueOf(21), "abc", 0, GameStates.contextMenuButtonDistX, guiHeight, "drop cloak");
				if (this.add(cloakButton)) ; else System.out.println("couldn't add");
				guiHeight += cloakButton.getHeight() + GameStates.contextMenuButtonDistY;
				
			}
			this.setLocalObject(localObject);
			contextMenuSpacePolygon.add((new Polygon(new int[]{0 , 0 , GameStates.contextMenuButtonWidth + GameStates.contextMenuButtonDistX *2, GameStates.contextMenuButtonWidth+ GameStates.contextMenuButtonDistX *2}, new int[]{0 ,  guiHeight, guiHeight, 0}, 4) ));
			backgroundSpace = new Space(contextMenuSpacePolygon, 0, 0, "backgroundGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
		}
		this.setVisible(true);
	}
	
	/** remove a button from the list */
	public void remove(BigInteger index) throws IllegalArgumentException {
		try {
			buttonList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean add(Button button) {
		if (buttonList.containsKey(button.getId())) {
			return false;
		} else {
			buttonList.put(button.getId(), button);
			idList.add(button.getId());
			return true;
		}
	}
	
	public Button get(BigInteger index){
		try {
			return (Button) buttonList.get(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	public boolean overwrite(Button button){
		if (!buttonList.containsKey(button.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			buttonList.put(button.getId(), button);
			idList.add(button.getId());
			return true;
		}
	}
	
	public int size(){
		return buttonList.size();
	}
	
	public boolean contains(Button button) {
		return buttonList.containsValue(button);
	}
	
	public boolean mouseClick(Point p) {
		Point correctedPoint = new Point(p.x-this.getX(), p.y-this.getY());
		boolean ret = false;
		for (BigInteger id : idList) {
			Button button = buttonList.get(id);
			if (button.contains(correctedPoint)) {
				/** now change the animation and status */
//				if (button.isActivated()) {
//					button.setInactive();
//				} else {
//					button.setActive();
//				}
				buttonClick(button.getId());
				ret = true;
			}
		}
		return ret;
	}
	
	/** click different buttons */
	public void buttonClick(BigInteger id) {
		/** click the gameMaster-button */
		if (get(id).getText().equals("Gamemaster")){
			GameWindow.gw.setGameMaster(get(id).getStrValue());
//			System.out.println(get(id).getStrValue());
		}
		
		/** click animation  */
		if (get(id).getText().equals("go to bed")){
			GameWindow.gw.gameInfoConsole.appendInfo("go to bed");
			GamePanel.gp.getPlayerEntity().setContinuousState(EntityStates.SLEEPING);
			GamePanel.gp.getPlayerEntity().setChangedStates(true);
//			System.out.println(get(id).getStrValue());
		}
		
		if (get(id).getText().equals("drop cloak")) {
			BigInteger itemId = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
			
			int putX = 0;
			int putY = 0;
			switch (GamePanel.gp.getPlayerEntity().getOrientation()) {
			case RIGHT:
				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 + GameStates.dropDistance);
				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - GameStates.inventoryItemTileHeight/2);
				break;
			case LEFT:
				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - (GameStates.dropDistance+GameStates.inventoryItemTileWidth));
				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 -GamePanel.gp.getMouseItem().getItemEntity().getHeight()/2);
				break;
			case UP:
				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2- GameStates.inventoryItemTileWidth/2);
				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 - (GameStates.dropDistance+GamePanel.gp.getMouseItem().getItemEntity().getHeight()));
				break;
			case DOWN:
				putX = (int) (GamePanel.gp.getPlayerEntity().getX() + GamePanel.gp.getPlayerEntity().getWidth()/2 - GameStates.inventoryItemTileWidth/2);
				putY = (int) (GamePanel.gp.getPlayerEntity().getY() + GamePanel.gp.getPlayerEntity().getHeight()*3/4 + (GameStates.dropDistance));
			}
				
			Cloak cloak = new Cloak(itemId, putX, putY, new float[]{1.3f, 0.5f, 30.45f});
			System.out.println("added new cloak object: "+itemId);
//			Wood item = new Wood();
//			TileSet woodTileSet = new TileSet(GameStates.standardTilePathItems+"wood2.png", "WoodPileTileSet", GameStates.itemTileWidth, GameStates.itemTileHeight);
//			Entity itemEntity = new Entity(woodTileSet, "wood", itemId, p.x+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), p.y+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), GameStates.animationDelayItems);
//			WorldItem wood = new WorldItem(itemId, item, itemEntity);
//			WorldItemManager.add(wood);
			
			/** send the complete Item to all players of the channel */
			if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
				/** first send to server for the itemList */
				GameWindow.gw.send(ClientMessages.addItem(cloak.getId(), cloak.getItemCode(), cloak.getCount(), cloak.getCapacity(), cloak.getItemEntity().getX(), cloak.getItemEntity().getY(), cloak.getItemEntity().getMX(), cloak.getItemEntity().getMY(), cloak.getName(), cloak.getItemEntity().getTileSet().getFileName(), cloak.getItemEntity().getName(), cloak.getStates()));
				
//				GameWindow.gw.send(ClientMessages.addItem(itemId));
				
				for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
					ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
					try {
						channel.send(ClientMessages.addCompleteItem(cloak.getItemCode(), cloak.getId(), cloak.getName(), cloak.getX(), cloak.getY(), cloak.getCount(), cloak.getStates()));
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			}
		}
	}
	
	/** mouseOver effect*/
	public void mouseOver(Point p) {
		Point correctedPoint = new Point(p.x-this.getX(), p.y-this.getY());
		if (backgroundSpace.contains(correctedPoint) && this.isVisible()) {
			for (BigInteger id : idList) {
				Button button = buttonList.get(id);
				if (button.contains(correctedPoint)) {
					/** now change the animation and status */
					button.setMouseover();
				} else {
					if (button.isActivated()) {
						button.setActive();
					} else {
						button.setInactive();
					}
				}
			}
		} else {
			this.setVisible(false);
		}
	}
	
	public void deactivateOthers(Button button) {
		for(Button otherButton : buttonList.values()) {
			if (otherButton != button) {
				otherButton.setInactive();
			}
		}
	}
	
	public void paint(Graphics2D g, int x, int y, ImageObserver iObserver) {
//		System.out.println("paint context GUI");
		if (!idList.isEmpty() && this.isVisible()) {
			g.setPaintMode();
			backgroundSpace.paint(g, x+ this.x, y + this.y);
			g.setPaintMode();
			for(BigInteger id : idList) {
				Button button = get(id);
				g.drawImage(button.getSprite().getImage(), x + this.x + (int) (button.getX()), y + this.y + (int) (button.getY()), iObserver);
				
				g.setPaintMode();
				g.setColor(GameStates.standardFontColor);
				g.setFont(new Font(GameStates.standardFont, Font.PLAIN , GameStates.contextMenuFontSize));
//				drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
				g.drawString(button.getText(), GameStates.contextMenuTextDistX + x + this.x + (int) (button.getX()), GameStates.contextMenuTextDistY + y + this.y + (int) (button.getY()) + g.getFontMetrics().getHeight());
			}
		}
	}

	public Space getBackgroundSpace() {
		return backgroundSpace;
	}

	public void setBackgroundSpace(Space backgroundSpace) {
		this.backgroundSpace = backgroundSpace;
	}

	public LocalObject getLocalObject() {
		return localObject;
	}

	public void setLocalObject(LocalObject localObject) {
		this.localObject = localObject;
	}

	public Button standardButton(BigInteger id, String strValue, int intValue, int x, int y, String text) {
		TileSet tileSetStandardButton = new TileSet("tilesets/buttons/contextButton.png", "contextButton", GameStates.contextMenuButtonWidth, GameStates.contextMenuButtonHeight);
		Button standardButtonGUI = new Button(tileSetStandardButton, "standardButton", id, x, y, GameStates.animationDelay, strValue, intValue, text);
//		standardButtonGUI.setInactive();
		return standardButtonGUI;		
		
//		ArrayList<Polygon> editorGUISpacePolygon = new ArrayList<Polygon>();
//		editorGUISpacePolygon.add((new Polygon(new int[]{GameStates.width -250 , GameStates.width -250 ,GameStates.width -25, GameStates.width -25}, new int[]{40 , 85 ,85, 40}, 4) ));
//		Space editorGUISpace = new Space(editorGUISpacePolygon, 0, 0, "editorGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
//		
//		EditorGUI floorEditor = new EditorGUI("floor", "cobble", 110, editorGUISpace);
//		floorEditor.add(cobbleButtonGUI);
//		floorEditor.add(grassButtonGUI);
//		floorEditor.add(snowButtonGUI);
//		floorEditor.add(treeButtonGUI);
//		floorEditor.add(snowTreeButtonGUI);
//		floorEditor.add(undergroundGrassButtonGUI);
//		EditorGUIManager.add(floorEditor);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
