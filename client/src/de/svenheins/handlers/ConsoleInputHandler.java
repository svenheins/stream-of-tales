/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

import de.svenheins.main.GUI;
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
		if(GUI.gui.getShowConsole()==true) {
			GUI.gui.gameConsole.update();
			/** the limit was calculated with @-chars (biggest char) */
			
			// ((float)(GameStates.getWidth()-240)/1366)*36
			if (e.getKeyCode() != keyDeleteBackwards && !delete && ((GUI.gui.gameConsole.getActualCharCount() < GameStates.consoleMaxCharsPerLine) || (e.getKeyChar() =='\n')) )
				GUI.gui.gameConsole.append(e.getKeyChar());
			else if (delete) {
				GUI.gui.gameConsole.setLastLineSend(true);
				if (GUI.gui.gameConsole.isDeleteable()) GUI.gui.gameConsole.deleteLast();
			}
			GUI.gui.gameConsole.update();
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
