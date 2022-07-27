package de.svenheins.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.svenheins.main.GamePanel;

public class ConsoleInputHandler implements KeyListener {

	public boolean console, delete;
	
	@Override
	public void keyTyped(KeyEvent e) {
		if(GamePanel.gp.getShowConsole()==true) {
			GamePanel.gp.gameConsole.update();
			if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE && !delete)
			GamePanel.gp.gameConsole.append(e.getKeyChar());
			else {
				if (GamePanel.gp.gameConsole.isDeleteable()) GamePanel.gp.gameConsole.deleteLast();
			}
			GamePanel.gp.gameConsole.update();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {	
		if(e.getKeyCode() == KeyEvent.VK_F1) {
			console = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			delete = true;
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_F1) {
			console = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			delete= false;
		}
	}

}
