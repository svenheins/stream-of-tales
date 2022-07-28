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
	
	private final int keyOpenConsole = KeyEvent.VK_ENTER;
	private final int keyDeleteBackwards = KeyEvent.VK_BACK_SPACE;
	
	/** The {@link Logger} for this class. */
    private static final Logger logger =
        Logger.getLogger(ConsoleInputHandler.class.getName());
	
	@Override
	public void keyTyped(KeyEvent e) {
		if(GameWindow.gw.getShowConsole()==true) {
			GameWindow.gw.gameConsole.update();
			/** the limit was calculated with @-chars (biggest char) */
			if (e.getKeyCode() != keyDeleteBackwards && !delete && ((GameWindow.gw.gameConsole.getActualCharCount() < ((float)(GameStates.getWidth()-240)/1366)*36) || (e.getKeyChar() =='\n')) )
				GameWindow.gw.gameConsole.append(e.getKeyChar());
			else if (delete) {
				GameWindow.gw.gameConsole.setLastLineSend(true);
				if (GameWindow.gw.gameConsole.isDeleteable()) GameWindow.gw.gameConsole.deleteLast();
			}
			GameWindow.gw.gameConsole.update();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {	
//		System.out.println("pressed");
		if(e.getKeyCode() == keyOpenConsole) {
			console = true;
		}
		
		if(e.getKeyCode() == keyDeleteBackwards) {
			delete = true;
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == keyOpenConsole) {
			console = false;
		}
		
		if(e.getKeyCode() == keyDeleteBackwards) {
			delete= false;
		}
	}

}
