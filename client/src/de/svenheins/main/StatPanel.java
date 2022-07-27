package de.svenheins.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class StatPanel  extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static StatPanel sp;
	
	
	public StatPanel() { 
		super();
		sp = this;
		
		this.init();
		this.config();
	}


	private void config() {
		// TODO Auto-generated method stub
		setOpaque(false);
		setBackground(new Color(0,0,0,64));
	}


	private void init() {
		// TODO Auto-generated method stub
		
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
		// paint the info-console
		if (GameWindow.gw.getShowInfoConsole()) {
			GameWindow.gw.gameInfoConsole.paint(g, 0, 0);
		}
		if (GameWindow.gw.getShowConsole()) {
			GameWindow.gw.gameConsole.paint(g, 0, 0);
		}
	}
}
