package de.svenheins.objects;


public class AlienShot extends ShotEntity{

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;

	public AlienShot(String src, double x, double y) {
		super(src, x, y);
		// TODO Auto-generated constructor stub
		this.setHorizontalMovement(0);
		this.setVerticalMovement(80);
	}
	
	

}
