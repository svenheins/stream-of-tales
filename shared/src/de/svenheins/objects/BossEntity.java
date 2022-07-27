package de.svenheins.objects;


public class BossEntity extends Entity{

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private int life;
	
	public BossEntity(String src, double x, double y) {
		super(src, 0, x, y);
		this.setLife(5);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void changeDirection() {
		// TODO Auto-generated method stub
		mx = -mx;
		
	}

	public boolean canShoot() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

}
