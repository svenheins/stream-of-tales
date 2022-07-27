package de.svenheins.objects;
import de.svenheins.managers.EnemyManager;



public class AlienEntity extends Entity {

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private int row, position;
	public static int aliens;
	
	public AlienEntity(String src, double x, double y, int row, int pos) {
		super(src, 0, x, y);
		this.row = row;
		this.position = pos;
		this.setHorizontalMovement(0);
		this.setVerticalMovement(0);
	}
	
	public AlienEntity(String[] src, double x, double y, int row, int pos) {
		super(src, 0, x, y);
		this.row = row;
		this.position = pos;
		this.setHorizontalMovement(0);
		this.setVerticalMovement(0);
	}
	
	@Override
	public void changeDirection() {
		// TODO Auto-generated method stub
		this.mx = -mx;
		//this.y +=10;
	}
	
	public boolean canShoot() {
		for (int i = 0; i<EnemyManager.enemyList.size(); i++){
			Entity e = EnemyManager.enemyList.get(i);
			if(e instanceof AlienEntity){
				AlienEntity a = (AlienEntity)e;
				if(position == a.position && a.row >row) return false;
			}
			
		}
		return true;
	}

}
