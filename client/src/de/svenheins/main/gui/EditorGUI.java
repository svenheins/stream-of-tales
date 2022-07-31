package de.svenheins.main.gui;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.main.GameStates;
import de.svenheins.managers.AnimationManager;
import de.svenheins.objects.Space;


public class EditorGUI {
	public HashMap<BigInteger, Button> buttonList = new HashMap<BigInteger, Button>();
	public List<BigInteger> idList = new ArrayList<BigInteger>();
	private String strValue;
	private int intValue;
	private String name;
	private Space backgroundSpace;
	
	public EditorGUI(String name, String strValue, int intValue, Space backgroundSpace) {
		this.name = name;
		this.strValue = strValue;
		this.intValue = intValue;
		this.backgroundSpace = backgroundSpace;
	}
	
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
		boolean ret = false;
		for (BigInteger id : idList) {
			Button button = buttonList.get(id);
			if (button.contains(p)) {
				this.setIntValue(button.getIntValue());
				this.setStrValue(button.getStrValue());
				/** now change the animation and status */
				button.setActive();
				deactivateOthers(button);
				ret = true;
			}
		}
		
		return ret;
	}
	
	public void deactivateOthers(Button button) {
		for(Button otherButton : buttonList.values()) {
			if (otherButton != button) {
				otherButton.setInactive();
			}
		}
	}
	
	public void paint(Graphics2D g, int x, int y, ImageObserver iObserver) {
		g.setPaintMode();
		backgroundSpace.paint(g, backgroundSpace.getPolyX(), backgroundSpace.getPolyY());
		g.setPaintMode();
		for(BigInteger id : idList) {
			Button button = get(id);
			g.drawImage(button.getSprite().getImage(), x + (int) (button.getX()), y + (int) (button.getY()), iObserver);
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
	
}
