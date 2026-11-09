/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main.gui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import de.svenheins.main.AttributeType;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.objects.Entity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.items.Item;

public class InfoBoxGUI {
//	public HashMap<BigInteger, Button> buttonList = new HashMap<BigInteger, Button>();
//	public List<BigInteger> idList = new ArrayList<BigInteger>();
	private HashMap<String, ArrayList<String>> infoList = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> headingList = new ArrayList<String>();
	private Space backgroundSpace;
	private LocalObject localObject;
	private boolean visible;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public InfoBoxGUI() {
		setVisible(false);
	}
	
	/** show the contextMenu for the corresponding entity */
	public void create(LocalObject localObject, int x, int y) {
		if (x > GameStates.width-GameStates.infoBoxTextWidth-GameStates.infoBoxTextDistanceX-GameStates.infoBoxTextDistance*2) {
			x = GameStates.width-GameStates.infoBoxTextWidth-GameStates.infoBoxTextDistanceX-GameStates.infoBoxTextDistance*2;
		}
		
		this.setX(x);
		this.setY(y);
		infoList = new HashMap<String, ArrayList<String>>();
		ArrayList<Polygon> infoBoxSpacePolygon = new ArrayList<Polygon>();
		ArrayList<String> tempInfos;
		int guiHeight = GameStates.infoBoxTextDistance;
		
		tempInfos = new ArrayList<String>();
		headingList = new ArrayList<String>();
		
		/** playerEntities */
		if (localObject instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity) localObject;
			
			/** if i clicked onto another playerEntity */
			tempInfos = new ArrayList<String>();
			
			tempInfos.add("Groupname: ");
			tempInfos.add(playerEntity.getGroupName());
			guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
			tempInfos.add("Room: ");
			tempInfos.add(playerEntity.getRoom());
			guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
			tempInfos.add("Entity-State: ");
			tempInfos.add(playerEntity.getContinuousState().name()+" Orientation: "+playerEntity.getOrientation().name());
			guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
			tempInfos.add("Entity-Single-State: ");
			tempInfos.add(playerEntity.getSingleState().name()+" Orientation: "+playerEntity.getOrientation().name());
			guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
			/** if it is an Agent */
			if (localObject instanceof Agent) {
				if (((PlayerEntity) localObject).isAlive()) {
					tempInfos.add("Level: ");
					tempInfos.add(""+playerEntity.getLevel());
					guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
				}
				tempInfos.add("Goal: ");
				if (((Agent) playerEntity).getActualGoal() != null) {
					if (((Agent) playerEntity).getActualGoal().getEntity() != null) {
						tempInfos.add(((Agent) playerEntity).getActualGoal().getEntity().getName());
					} else if (((Agent) playerEntity).getActualPathElement() != null){
						tempInfos.add(((Agent) playerEntity).getActualPathElement().getRoom() + " x="+((Agent) playerEntity).getActualPathElement().getX() + " y="+((Agent) playerEntity).getActualPathElement().getY()+" | my Pos = "+((Agent) playerEntity).getX()+"|"+(((Agent) playerEntity).getY()+((Agent) playerEntity).getHeight()/2));
					} else {
						tempInfos.add("no info");
					}
					
				} else {
					tempInfos.add("no actual goal!");
				}
				
//				tempInfos.add("AreaInfluence MX: ");
//				tempInfos.add(""+playerEntity.getTotalAreaInfluences()[AttributeType.MX.ordinal()]);
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("AreaInfluence MY: ");
//				tempInfos.add(""+playerEntity.getTotalAreaInfluences()[AttributeType.MY.ordinal()]);
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("ItemInfluence MX: ");
//				tempInfos.add(""+playerEntity.getTotalItemInfluences()[AttributeType.MX.ordinal()]);
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("ItemInfluence MY: ");
//				tempInfos.add(""+playerEntity.getTotalItemInfluences()[AttributeType.MY.ordinal()]);
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				
//				tempInfos.add("MX: ");
//				tempInfos.add(""+playerEntity.getMX());
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("MY: ");
//				tempInfos.add(""+playerEntity.getMY());
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("Attributes MX: ");
//				tempInfos.add(""+playerEntity.getAttributes()[AttributeType.MX.ordinal()]);
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("Attributes MY: ");
//				tempInfos.add(""+playerEntity.getAttributes()[AttributeType.MY.ordinal()]);
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("Collided: ");
//				tempInfos.add(""+playerEntity.getCollided());
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("width and height: ");
//				tempInfos.add(""+playerEntity.getWidth()+" | "+playerEntity.getHeight());
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				
//				tempInfos.add("has Goal? ");
//				tempInfos.add(""+((Agent)playerEntity).hasGoal());
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("is alive? ");
//				tempInfos.add(""+((NormalAgent)playerEntity).isAlive());
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
//				tempInfos.add("dead in ");
//				tempInfos.add(""+((System.currentTimeMillis()-((NormalAgent)playerEntity).getDeadSince())));
//				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;

				
				guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
			}
			
			infoList.put(playerEntity.getName(), tempInfos);
			headingList.add(playerEntity.getName());
			guiHeight += GameStates.infoBoxHeadingSize+ GameStates.infoBoxTextSpaceY;
			
			/** if it is me myself */
			if ( playerEntity.getName().equals(GameWindow.gw.getPlayerName())) {				
				
				/** only allow this button for the mapMaster */
				if (GameWindow.gw.isMapMaster()) {

				}	
			} else {
				
			}
			
			
			
			guiHeight += GameStates.infoBoxTextDistance*2;
		} else 		
		/** items */
		if (localObject instanceof Item) {
			Item item = (Item) localObject;
			tempInfos = new ArrayList<String>();
//			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
//	        Date resultdate = new Date(item.getCreationTime());
//			tempInfos.add("CreationTime: "+sdf.format(resultdate));
			tempInfos.add("Level:");
			tempInfos.add(""+item.getLevel().intValue());
			guiHeight += GameStates.infoBoxFontSize;
			guiHeight += GameStates.infoBoxTextSpaceY;
			infoList.put(item.getName()+item.getComment(), tempInfos);
			headingList.add(item.getName()+item.getComment());
			guiHeight += GameStates.infoBoxHeadingSize + GameStates.infoBoxTextSpaceY;
			
			int index = 0;
			tempInfos = new ArrayList<String>();
			for (float attribute : item.getAttributes()) {
				if (attribute != 0 ) {
					String attributeString = ""+attribute;
					if (attribute - (int)attribute == 0) attributeString = ""+ ((int) attribute);
					tempInfos.add(GameStates.getStringRepresentationOfAttribute(AttributeType.values()[index])+": ");
					tempInfos.add(attributeString);
					guiHeight += GameStates.infoBoxFontSize + GameStates.infoBoxTextSpaceY;
				}
				index++;
			}
			
			if (tempInfos.size() > 0) {
				infoList.put("Attributes", tempInfos);
				headingList.add("Attributes");
				guiHeight += GameStates.infoBoxHeadingSize+ GameStates.infoBoxTextSpaceY;
			}
			
			guiHeight += GameStates.infoBoxTextDistance*2;
		} else if (localObject instanceof Entity) {
			/** infoIcon */
			tempInfos = new ArrayList<String>();
//			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
//	        Date resultdate = new Date(item.getCreationTime());
//			tempInfos.add("CreationTime: "+sdf.format(resultdate));
			tempInfos.add("Name:");
			tempInfos.add(""+localObject.getName());
			guiHeight += GameStates.infoBoxFontSize;
			guiHeight += GameStates.infoBoxTextSpaceY;
			infoList.put(localObject.getName(), tempInfos);
			headingList.add(localObject.getName());
			guiHeight += GameStates.infoBoxHeadingSize + GameStates.infoBoxTextSpaceY;
			guiHeight += GameStates.infoBoxTextDistance*2;
		}
		
		
//		this.setLocalObject(object);
		infoBoxSpacePolygon.add((new Polygon(new int[]{0 , 0 , GameStates.infoBoxTextWidth + GameStates.infoBoxTextDistance *2, GameStates.infoBoxTextWidth+ GameStates.infoBoxTextDistance *2}, new int[]{0 ,  guiHeight, guiHeight, 0}, 4) ));
		backgroundSpace = new Space(infoBoxSpacePolygon, 0, 0, "backgroundGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
		this.setVisible(true);
	}
	
	public void paint(Graphics2D g, int x, int y, ImageObserver iObserver) {
//		System.out.println("paint context GUI");
		if (this.isVisible() && headingList.size() > 0) {
			g.setPaintMode();
			backgroundSpace.paint(g, x+ this.x, y + this.y);
			g.setPaintMode();
			ArrayList<String> infoDetails;
			int actualLine = 0;
			ArrayList<String> tempHeadingList = new ArrayList<>(headingList);
			for(String heading : tempHeadingList) {
				g.setPaintMode();
				g.setColor(GameStates.standardFontColor);
				g.setFont(new Font(GameStates.standardFont, Font.PLAIN , GameStates.infoBoxHeadingSize));
				g.drawString(heading, GameStates.infoBoxTextDistanceX + GameStates.infoBoxTextDistance + x + this.x, actualLine + GameStates.infoBoxTextDistance + y + this.y + g.getFontMetrics().getHeight());
				actualLine += GameStates.infoBoxHeadingSize + GameStates.infoBoxTextSpaceY;
				infoDetails = infoList.get(heading);
				g.setPaintMode();
				g.setColor(GameStates.standardFontColor);
				g.setFont(new Font(GameStates.standardFont, Font.PLAIN , GameStates.infoBoxFontSize));
				
				String infoName;
				String infoValue;
				if (infoDetails != null) {
					for (int i = 0; i < infoDetails.size(); i++) {
						infoName = infoDetails.get(i);
						i++;
						infoValue = infoDetails.get(i);
						g.drawString(infoName, GameStates.infoBoxTextDistanceX+GameStates.infoBoxTextDistance + x + this.x, actualLine + GameStates.infoBoxTextDistance + y + this.y + g.getFontMetrics().getHeight());
						g.drawString(infoValue, GameStates.infoBoxTextSpaceX + GameStates.infoBoxTextDistanceX+GameStates.infoBoxTextDistance + x + this.x, actualLine + GameStates.infoBoxTextDistance + y + this.y + g.getFontMetrics().getHeight());
						actualLine += GameStates.infoBoxFontSize+ GameStates.infoBoxTextSpaceY;
					}
				}
				
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

	public HashMap<String, ArrayList<String>> getInfoList() {
		return infoList;
	}

	public void setInfoList(HashMap<String, ArrayList<String>> infoList) {
		this.infoList = infoList;
	}
}
