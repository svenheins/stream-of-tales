package de.svenheins.threads;
import de.svenheins.main.GUI;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.managers.PlayerManager;
import de.svenheins.objects.Entity;


public class CollisionThread implements Runnable {
	private long duration, last; 
	private long millis, frames;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (GUI.running) {
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			millis += duration;
			frames +=1;
			if(!GamePanel.gp.isPaused() && GameModus.modus == GameModus.GAME) {
//				for (int i = EnemyManager.size()-1; i>=0 ; i--) {
//					// test it the list was shortened since the loop start
//					if (i< EnemyManager.size()) {
//						Entity e= EnemyManager.get(i);
//						if(e != null) {
//							for (int j=PlayerManager.size()-1;j>=0;j--){
//								if (j< PlayerManager.size()) {
//									Entity e2 = PlayerManager.get(j);
//									if(e2 != null) {
//										if(e.collides(e2)){
//											/*if (EntityManager.entitiesCanDestroyEachOther(e, e2))*/
//											if (e instanceof BossEntity) {
//												BossEntity a = (BossEntity) e;
//												System.out.println("Boss getroffen");
//												a.setLife(a.getLife()-1);
//												System.out.println(a.getLife());
//												PlayerManager.remove(e2);
//												if(a.getLife() <= 0) EnemyManager.remove(e);
//											} else if(e instanceof AlienEntity && e2 instanceof ShotEntity){
//												EnemyManager.remove(e);
//												AlienEntity.aliens--;
//											}
//											else {
//												// Enemy Shot and Player disappear
//												EnemyManager.remove(e);
//												PlayerManager.remove(e2);
//												
//											}
//										}
//									} else {
//										// remove null-Object
//										PlayerManager.remove(e2);
//									}
//								// do nothing, because we met an index, that exceeds the dimension of the array!!!
//								} else {}
//							}
//						}
//						else EnemyManager.remove(e);
//					
//					// do nothing, because we met an index, that exceeds the dimension of the array!!!
//					} else {}
//				}
//				if( EnemyManager.size() == 0){
//					BossEntity boss = new BossEntity("boss.png", 300, 70);
//					boss.setHorizontalMovement(350);
//					EnemyManager.add(boss);
//				}
			}
			if (millis >1000 && GamePanel.gp.showStats ==true) {
				System.out.println("Collision-Frames: "+ frames);
				frames = 0;
				millis =0;
			}
			try {
				Thread.sleep(5);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}	
	}

}
