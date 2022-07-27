package de.svenheins.threads;


import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.messages.ClientMessages;

public class ServerUpdateThread implements Runnable {

	private long duration, last; 
	private long millis, frames;
		

	public void setLast(long last) {
		this.last = last;
	}
	
	
	/**
	 * @param last
	 * needs the currentTimeMillis(), to avoid the bug at the first frame
	 */
	public ServerUpdateThread(long last) {
		this.last = last;
	}
	
	public void run() {
		while (GUI.running) {			
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			millis += duration;
			frames +=1;
			if(!GamePanel.gp.isPaused() && GameModus.modus == GameModus.GAME ) {
				if (!GamePanel.gp.isServerInitialized()) {
					GameWindow.gw.send(ClientMessages.initEntities());
					GameWindow.gw.send(ClientMessages.initSpaces());
				}

			}
			try {
				Thread.sleep(50);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}
}
