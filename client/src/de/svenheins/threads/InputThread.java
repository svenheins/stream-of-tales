package de.svenheins.threads;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.PlayerManager;
import de.svenheins.handlers.InputHandler;
import de.svenheins.objects.Entity;
import de.svenheins.objects.Player;


public class InputThread implements Runnable{
	
	
	private long duration, last;
	private Player[] players;
//	private ShipEntity[] ships;
	
	public InputThread(Player[] players) {
		this.players = players;
//		this.ships = ships;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (GUI.running) {
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
//			System.out.println("input-thread runs");
			if(GameModus.modus == GameModus.GAME) {
				gameRun();
			}
			
			try {
				Thread.sleep(25);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
		
	}
	
	public void gameRun() {
		
		// Handle Game-Input
		if(GameWindow.gw.getShowConsole() == false) {
			for (int h=0; h< players.length; h++) {
//				ShipEntity s = ships[h];
				InputHandler input = players[h].getInputHandler();
				Player p = players[h];
//				p.setLastAttack(p.getLastAttack()+duration);
//				s.setHorizontalMovement(0);
//				s.setVerticalMovement(0);
//				if ( PlayerManager.contains(s)){
					if (input.down && !input.up){
//						s.setVerticalMovement(Entity.DEFAULT_MOVEMENT_ON_Y);
						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX(), GamePanel.gp.getViewPointY()-20);
//						System.out.println("down");
					}
					if (input.up && !input.down){
//						s.setVerticalMovement(-Entity.DEFAULT_MOVEMENT_ON_Y);
						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX(), GamePanel.gp.getViewPointY()+20);
//						System.out.println("up");
					}
					if (input.left && !input.right){
//						s.setHorizontalMovement(-Entity.DEFAULT_MOVEMENT_ON_X);
						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX()+20, GamePanel.gp.getViewPointY());
					}
					if (input.right && !input.left){
//						s.setHorizontalMovement(Entity.DEFAULT_MOVEMENT_ON_X);
						GamePanel.gp.setViewPoint(GamePanel.gp.getViewPointX()-20, GamePanel.gp.getViewPointY());
					}
					if (input.attack && p.getLastAttack() > p.getTimebetweenAttacks() && !GamePanel.gp.isPaused()){
//						ShotEntity shot = new ShotEntity("shot3.png", s.getX()+s.getSprite().getWidth()/2-12, s.getY()-20);
//						shot.setHorizontalMovement(0);
//						shot.setVerticalMovement(-150);
//						PlayerManager.add(shot);
//						// Add Animation sequence
//						String[] shotAni = {"ship_shoot1.png","ship_shoot2.png","ship_shoot2.png","ship_shoot1.png"};
//						s.runAnimationOnce(shotAni, 50, System.currentTimeMillis());
//						
//						p.setLastAttack(0);
					}
					if (input.pause ){
						GamePanel.gp.setPause(!GamePanel.gp.isPaused());
						input.pause = false;
					}
					if (input.options ){
						GamePanel.gp.setPause(!GamePanel.gp.isPaused());
						GamePanel.gp.setMenu(!GamePanel.gp.isMenu());
						input.options = false;
					}
					if (input.infoConsole ){
						GameWindow.gw.setShowInfoConsole(!GameWindow.gw.getShowInfoConsole());
//						GamePanel.gp.setMenu(!GamePanel.gp.isMenu());
						input.infoConsole = false;
					}
//				}
			}
		}
		else if (GameWindow.gw.getShowConsole() == true) {
			// Handle Input inside the console

		}

		if (GameWindow.gw.getConsoleInputHandler().console ){
			GameWindow.gw.setShowConsole(!GameWindow.gw.getShowConsole());
//			System.out.println("show console: "+GameWindow.gw.getShowConsole());
			GameWindow.gw.getConsoleInputHandler().console = false;
			//GamePanel.gp.setShowConsole(true);
		}

		
	}

}
