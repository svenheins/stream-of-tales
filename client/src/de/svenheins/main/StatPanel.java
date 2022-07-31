package de.svenheins.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigInteger;

import javax.swing.JPanel;

import de.svenheins.main.gui.Button;
import de.svenheins.main.gui.ContextMenuGUI;
import de.svenheins.main.gui.EditorGUI;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUI;
import de.svenheins.main.gui.PlayerListGUIManager;

public class StatPanel  extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static StatPanel sp;
	public ContextMenuGUI contextMenu;
	
	
	public StatPanel() { 
		super();
		sp = this;
		
		this.init();
		this.config();
	}


	private void config() {
		setOpaque(false);
		setBackground(new Color(0,0,0,64));
	}


	private void init() {
		contextMenu = new ContextMenuGUI();
	}
	
	
	
	protected void paintComponent(Graphics g2) {
        Graphics2D g = (Graphics2D) g2;
		// Set Options of the Graphics-Object
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);

		// Paint in each GameModus
//		if (GameModus.modus == GameModus.MAINMENU) {
//			mainMenuPaint(g);
//		} else 
		if (GameModus.modus == GameModus.GAME) {
			gamePaint(g);
			//GamePanel.gp.loadEntityList();
		}
		g.dispose();
	}

	public void gamePaint(Graphics2D g){
//		g.setPaintMode();
		// paint the Menu
		if (GamePanel.gp.isMenu()) {
			GamePanel.gp.mainMenu.paint(g, 0, 0);
		}
		// paint the info-console
		if (GameWindow.gw.getShowInfoConsole()) {
			GameWindow.gw.gameInfoConsole.paint(g, 0, 0);
		}
		if (GameWindow.gw.getShowConsole()) {
			GameWindow.gw.gameConsole.paint(g, 0, 0);
		}
		
		/** only paint the painting tiles if we are the gameMaster */
		if (GameWindow.gw.isGameMaster()) {
			for (String str : EditorGUIManager.idList) {
				EditorGUI editorGUI = EditorGUIManager.get(str);
				editorGUI.paint(g, 0, 22, this);
			}
		}
		for (String str : PlayerListGUIManager.idList) {
			PlayerListGUI playerListGUI = PlayerListGUIManager.get(str);
			playerListGUI.paint(g, 5, 30, this);
		}
		/** paint the context menu */
		contextMenu.paint(g, 0, 22,  this);
	}


	public ContextMenuGUI getContextMenu() {
		return contextMenu;
	}


	public void setContextMenu(ContextMenuGUI contextMenu) {
		this.contextMenu = contextMenu;
	}
}
