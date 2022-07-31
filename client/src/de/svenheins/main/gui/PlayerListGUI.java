package de.svenheins.main.gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;

import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;


public class PlayerListGUI {
	public HashMap<BigInteger, Button> buttonList = new HashMap<BigInteger, Button>();
	public List<BigInteger> idList = new ArrayList<BigInteger>();
	private String strValue;
	private int intValue;
	private int maxYValue;
//	private int xValue
	private int fontSize;
	private String name;
	private Space backgroundSpace;
	
	public PlayerListGUI(String name, String strValue, int intValue, Space backgroundSpace) {
		this.name = name;
		this.strValue = strValue;
		this.intValue = intValue;
		this.backgroundSpace = backgroundSpace;
		this.setMaxYValue(0);
//		this.setxValue(0);
		this.fontSize = 12;
	}
	
	public PlayerListGUI(String name, String strValue, int intValue) {
		this.name = name;
		this.strValue = strValue;
		this.intValue = intValue;
		this.backgroundSpace = null;
		this.setMaxYValue(0);
		this.fontSize = 12;
//		this.setxValue(0);
//		this.backgroundSpace = backgroundSpace;
	}
	
	public void remove(BigInteger index) throws IllegalArgumentException {
		try {
			Button button = buttonList.get(index);
			buttonList.remove(index);
			idList.remove(index);
			
			this.setMaxYValue((int) (this.getMaxYValue()-button.getY()-button.getHeight()-fontSize));
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
			this.setMaxYValue((int) (this.getMaxYValue()+button.getY()+button.getHeight()+fontSize));
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
	
	public PlayerEntity mouseClick(Point p) {
		PlayerEntity retEntity = null;
		for (BigInteger id : idList) {
//			System.out.println(p.x +" "+ p.y);
			Button button = buttonList.get(id);
			if (button.contains(p)) {
//				System.out.println("inside");
				this.setIntValue(button.getIntValue());
				this.setStrValue(button.getStrValue());
				if (button.getId() == BigInteger.valueOf(0)) {
//					this.setStrValue(GameWindow.gw.getPlayerName());
					retEntity = GamePanel.gp.getPlayerEntity();
				} else {
					retEntity = PlayerManager.get(button.getId());	
				}
				this.setStrValue(retEntity.getName());
				
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
			}
		}
		return retEntity;
	}
	
	public void deactivateOthers(Button button) {
		for(Button otherButton : buttonList.values()) {
			if (otherButton != button) {
				otherButton.setInactive();
			}
		}
	}
	
	public void paint(Graphics2D g, int x, int y, ImageObserver iObserver) {
		/** paint backgroundSpace only if it exists */
		if (backgroundSpace != null) {
			g.setPaintMode();
			backgroundSpace.paint(g, backgroundSpace.getPolyX(), backgroundSpace.getPolyY());
		}
		g.setPaintMode();
		for(BigInteger id : idList) {
			Button button = get(id);
			g.drawImage(button.getSprite().getImage(), x + (int) (button.getX()), y + (int) (button.getY()), iObserver);
			g.setPaintMode();
			g.setColor(new Color(250, 250, 250));
			g.setFont(new Font("Arial", Font.PLAIN , fontSize));
//			drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
			g.drawString(button.getStrValue(), x + button.getX(), y + button.getY() + button.getHeight() + g.getFontMetrics().getHeight());
//			System.out.println(button.getY());
		}
		
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

	public int getMaxYValue() {
		return maxYValue;
	}

	public void setMaxYValue(int maxYValue) {
		this.maxYValue = maxYValue;
	}


	
}
