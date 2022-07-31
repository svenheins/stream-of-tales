package de.svenheins.main;

public class GUI {

	public static boolean running = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		running = true;
		new GameWindow("Client", GameStates.width, GameStates.height);
	}

}
