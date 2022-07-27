package de.svenheins.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

//import de.svenheins.World;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;

public class ConsoleInputHandler implements KeyListener {

	public boolean console, delete;
	
	/** The {@link Logger} for this class. */
    private static final Logger logger =
        Logger.getLogger(ConsoleInputHandler.class.getName());
	
	@Override
	public void keyTyped(KeyEvent e) {
		if(GameWindow.gw.getShowConsole()==true) {
			GameWindow.gw.gameConsole.update();
			/** the limit was calculated with @-chars (biggest char) */
			if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE && !delete && ((GameWindow.gw.gameConsole.getActualCharCount() < ((float)(GameStates.getWidth()-240)/1366)*36) || (e.getKeyChar() =='\n')) )
				GameWindow.gw.gameConsole.append(e.getKeyChar());
			else if (delete) {
				if (GameWindow.gw.gameConsole.isDeleteable()) GameWindow.gw.gameConsole.deleteLast();
			}
			GameWindow.gw.gameConsole.update();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {	
//		System.out.println("pressed");
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
