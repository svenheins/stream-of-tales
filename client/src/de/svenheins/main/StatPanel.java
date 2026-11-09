/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.svenheins.main.gui.ContainerGUIManager;
import de.svenheins.main.gui.ContextMenuGUI;
import de.svenheins.main.gui.EditorGUI;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.InfoBoxGUI;
import de.svenheins.main.gui.PlayerListGUI;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.TextEffectManager;
import de.svenheins.objects.Entity;
import de.svenheins.objects.Space;
import de.svenheins.objects.TextEffect;

public class StatPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static StatPanel sp;
	public Graphics2D graphics2D;
	public ContextMenuGUI contextMenu;
	public InfoBoxGUI infoBox;
	private Space lifeBar;
	private Space lifeBarBackground;
	private Space manaBar;
	private Space manaBarBackground;
	private Space energyBar;
	private Space energyBarBackground;
	private BufferedImage mouseCursorForCapturing;
	private BufferedImage barOverlay;
	
	
	public StatPanel() { 
		super();
		sp = this;
		
		this.init();
		this.config();
	}


	private void config() {
//		setOpaque(false);
//		setBackground(new Color(0,0,0,64));
	}


	private void init() {
		
//		Cursor cursor = getToolkit().createCustomCursor(new ImageIcon(getClass().getResource(GameStates.resourcePath+"images/"+"cursor.png")).getImage(),
//				 new Point(0,0), "Cursor");
//				 this.setCursor(cursor);
		
		contextMenu = new ContextMenuGUI();
		infoBox = new InfoBoxGUI();
		
		if (GameStates.extraMouseCursorForCapturing) {
			try {
				this.setMouseCursorForCapturing(ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+"cursor2x.png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			this.setMouseCursorForCapturing(null);
		}
		
		/** init LifeBar elements */
		lifeBar = new Space("lifeManaBar.svg", BigInteger.valueOf(0), "lifeBar.png", 1.0f);
		lifeBar.setAllXY(20, GameStates.height-120);
		lifeBarBackground = new Space("lifeManaBar.svg", BigInteger.valueOf(0), "lifeBarBackground.png", 0.7f);
		lifeBarBackground.setAllXY(20, GameStates.height-120);
		/** init ManaBar elements */
		manaBar = new Space("lifeManaBar.svg", BigInteger.valueOf(0), "manaBar.png", 1.0f);
		manaBar.setAllXY(20, GameStates.height-90);
		manaBarBackground = new Space("lifeManaBar.svg", BigInteger.valueOf(0), "manaBarBackground.png", 0.7f);
		manaBarBackground.setAllXY(20, GameStates.height-90);
		/** init EnergyBar elements */
		energyBar = new Space("lifeManaBar.svg", BigInteger.valueOf(0), "hungerBar.png", 1.0f);
		energyBar.setAllXY(20, GameStates.height-60);
		energyBarBackground = new Space("lifeManaBar.svg", BigInteger.valueOf(0), "hungerBarBackground.png", 0.7f);
		energyBarBackground.setAllXY(20, GameStates.height-60);
		
		try {
			barOverlay = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+"barOverlay_black3.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	protected void paintComponent(Graphics2D g) {
//        Graphics2D g = (Graphics2D) g2;
        graphics2D = g;
		// Set Options of the Graphics-Object
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		super.paintComponent(g);

		// Paint in each GameModus
//		if (GameModus.modus == GameModus.MAINMENU) {
//			mainMenuPaint(g);
//		} else 
		if (GameModus.modus == GameModus.GAME) {
			gamePaint(g);
			
		}
//		g.dispose();
	}

	public void gamePaint(Graphics2D g){
//		g.setPaintMode();
		
//		g.scale(1/GamePanel.gp.getZoomFactor(), 1/GamePanel.gp.getZoomFactor());
		// paint the Menu
		if (GamePanel.gp.isMenu()) {
			GamePanel.gp.mainMenu.paint(g, 0, 0);
		}
		
		
		/** only paint the painting tiles if we are the gameMaster */
		if (GameWindow.gw.isGameMaster()) {
			for (String str : EditorGUIManager.idList) {
				EditorGUI editorGUI = EditorGUIManager.get(str);
				editorGUI.paint(g, 0, 0, null);
			}
		}
		for (String str : PlayerListGUIManager.idList) {
			PlayerListGUI playerListGUI = PlayerListGUIManager.get(str);
			playerListGUI.paint(g, 0, 0, null);
		}
		for (String str : ContainerGUIManager.idList) {
			/** only paint the inventories, that are visible */
			if (ContainerGUIManager.get(str).isVisible()) {
				ContainerGUIManager.get(str).paint(g, null);
			}
		}
		
		
		
		
//		/** paint weatherEffects */
//		g.setPaintMode();
//		List<BigInteger> idListTempAgents = new ArrayList<BigInteger>(OverlayEntityManager.idList);
//		for (BigInteger i : idListTempAgents) {
//			Entity e = OverlayEntityManager.get(i);
//			if (e != null && GameWindow.gw.getWorldType(GamePanel.gp.getPlayerEntity().getRoom()) == WorldType.BASE) {
//				if (e != null ) {
//					if (e.getSprite().getImage() != null) {
//						g.drawImage(e.getSprite().getImage(),(int) (e.getX()-GamePanel.gp.getViewPointX()*2),	(int) (e.getY()-GamePanel.gp.getViewPointY()*2), this);
//					}
//				}
//					
//			} else {
//			}
//		}
		
		
//		g.scale(1/GamePanel.gp.getZoomFactor(), 1/GamePanel.gp.getZoomFactor());
		
		
		/** paint life and mana if the GamePanel.gp.getPlayerEntity != null */
		if (GamePanel.gp.getPlayerEntity() != null) {
			float life = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.LIFE.ordinal()];
			float maxLife = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.MAXLIFE.ordinal()];
			float mana = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.MANA.ordinal()];
			float maxMana = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.MAXMANA.ordinal()];
			float energy = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.ENERGY.ordinal()];
			float maxEnergy = GamePanel.gp.getPlayerEntity().getAttributes()[AttributeType.MAXENERGY.ordinal()];
			/** update length of lifeBar */
			Polygon polygonLife = lifeBarBackground.getPolygon().get(0);
			int barLengthLife = (int) ((life/maxLife)*(polygonLife.xpoints[2]-polygonLife.xpoints[1]) + polygonLife.xpoints[1]);
			lifeBar.getPolygon().get(0).xpoints[2] = barLengthLife;
			lifeBar.getPolygon().get(0).xpoints[3] = barLengthLife;
			/** update length of manaBar */
			Polygon polygonMana = manaBarBackground.getPolygon().get(0);
			int barLengthMana = (int) ((mana/maxMana)*(polygonMana.xpoints[2]-polygonMana.xpoints[1]) + polygonMana.xpoints[1]);
			manaBar.getPolygon().get(0).xpoints[2] = barLengthMana;
			manaBar.getPolygon().get(0).xpoints[3] = barLengthMana;
			/** update length of energyBar */
			Polygon polygonEnergy = energyBarBackground.getPolygon().get(0);
			int barLengthEnergy = (int) ((energy/maxEnergy)*(polygonEnergy.xpoints[2]-polygonEnergy.xpoints[1]) + polygonEnergy.xpoints[1]);
			energyBar.getPolygon().get(0).xpoints[2] = barLengthEnergy;
			energyBar.getPolygon().get(0).xpoints[3] = barLengthEnergy;
			
			lifeBarBackground.paint(g, 0,0);
			lifeBar.paint(g, 0,0);
			manaBarBackground.paint(g, 0,0);
			manaBar.paint(g, 0,0);
			energyBarBackground.paint(g, 0,0);
			energyBar.paint(g, 0,0);
			
			int lifeRoundedDown = (int) life;
			String lifeString = ""+lifeRoundedDown;
			int manaRoundedDown = (int) mana;
			String manaString = ""+manaRoundedDown;
			int energyRoundedDown = (int) energy;
			String energyString = ""+energyRoundedDown;
			
			/** draw the numbers */
			g.setColor(new Color(250, 250, 250));
			g.setFont(new Font(GameStates.standardFont, Font.BOLD , GameStates.lifeManaFontSize));
			
			g.drawImage(barOverlay,13, GameStates.height-127, null);
			
//			drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
			g.drawString(lifeString,barLengthLife-lifeString.length()*10, polygonLife.ypoints[1]-6);
			g.drawString(manaString,barLengthMana-manaString.length()*10, polygonMana.ypoints[1]-6);
			g.drawString(energyString,barLengthEnergy-energyString.length()*10, polygonEnergy.ypoints[1]-6);
		}
		
		// paint the info-console
		if (GUI.gui.getShowInfoConsole()) {
			GUI.gui.gameInfoConsole.paint(g, 0, 0);
		}
		if (GUI.gui.getShowConsole()) {
			GUI.gui.gameConsole.paint(g, 0, 0);
		}
		if (GUI.gui.messageWindow.isVisible()) {
			GUI.gui.messageWindow.paint(g, 0, 0);
		}
		
		/** paint the context menu */
		contextMenu.paint(g, 0, 0,  null);
		
		/** paint the infoBox */
		infoBox.paint(g, 0, 0, null);

	/** paint the mouse item */
	if(GamePanel.gp.getMouseItem() != null) {
		Entity mouseItemEntity = GamePanel.gp.getMouseItem().getEntity();
		g.drawImage(mouseItemEntity.getSprite().getImage(), (int) (mouseItemEntity.getX()), (int) (mouseItemEntity.getY()), null);
		g.setPaintMode();
		if (GamePanel.gp.getMouseItem().getCount() >1) {
			g.setColor(new Color(250, 250, 250));
			g.setFont(new Font(GameStates.standardFont, Font.PLAIN , GameStates.inventoryFontSize));
	//			drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
			g.drawString(""+GamePanel.gp.getMouseItem().getCount()/*+" "+item.getName()*/,GameStates.inventoryFontDistanceX + (int)mouseItemEntity.getX(), GameStates.inventoryFontDistanceY +(int) mouseItemEntity.getY() + mouseItemEntity.getHeight() /*+ g.getFontMetrics().getHeight()*/);
		}
}
		
		if (TextEffectManager.size() > 0) {
			int line = 0;
			for (TextEffect textEffect : TextEffectManager.textEffectList.values()) {
				if (textEffect != null) {
					textEffect.paint(g, GameStates.width/2, 50+line);
					line += GameStates.standardEffectTextSize;
				}
			}
		}
		
		
		/** paint the cursor twice if we need it for the video capturing */ 
		if (this.getMouseCursorForCapturing() != null) {
			g.drawImage(this.getMouseCursorForCapturing(), GamePanel.gp.getMouseCoordinates().x, GamePanel.gp.getMouseCoordinates().y, null);
		}
	}


//	public ContextMenuGUI getContextMenu() {
//		return contextMenu;
//	}
//
//
//	public void setContextMenu(ContextMenuGUI contextMenu) {
//		this.contextMenu = contextMenu;
//	}


	public Space getLifeBar() {
		return lifeBar;
	}


	public void setLifeBar(Space lifeBar) {
		this.lifeBar = lifeBar;
	}


	public Space getLifeBarBackground() {
		return lifeBarBackground;
	}


	public void setLifeBarBackground(Space lifeBarBackground) {
		this.lifeBarBackground = lifeBarBackground;
	}


	public Space getEnergyBar() {
		return energyBar;
	}


	public void setEnergyBar(Space energyBar) {
		this.energyBar = energyBar;
	}


	public Space getEnergyBarBackground() {
		return energyBarBackground;
	}


	public void setEnergyBarBackground(Space energyBarBackground) {
		this.energyBarBackground = energyBarBackground;
	}


	public BufferedImage getMouseCursorForCapturing() {
		return mouseCursorForCapturing;
	}


	public void setMouseCursorForCapturing(BufferedImage mouseCursorForCapturing) {
		this.mouseCursorForCapturing = mouseCursorForCapturing;
	}
}
