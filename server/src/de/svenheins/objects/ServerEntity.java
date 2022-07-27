package de.svenheins.objects;



import java.awt.Point;
import java.awt.Rectangle;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.main.GameStates;



public class ServerEntity extends WorldObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ManagedReference<ServerSprite> sprite;
	protected String[] standardAnimation;
	protected String[] animation;
	protected boolean b_stdAni;
	
	
	public final static int DEFAULT_MOVEMENT_ON_X = 300;
	public final static int DEFAULT_MOVEMENT_ON_Y = 300;
	
	public ServerEntity(ServerSprite sprite, int id, double x, double y, double mx, double my) {
		standardAnimation = new String[1];
		standardAnimation[0] = sprite.getStrImg();
		animation = standardAnimation;
		this.sprite = AppContext.getDataManager().createReference(sprite);
		this.id = id;
		this.setName(sprite.getStrImg());
		this.x = x;
		this.y = y;
		this.my = mx;
		this.mx = my;
		this.height = sprite.getHeight();
		this.width = sprite.getWidth();
	}
	
//	public void moveOnX(long duration){
//		double movement = duration * mx/1000;
//		setX(getX()+movement);
//	}
//	
//	public void moveOnY(long duration){
//		double movement = duration * my/1000;
//		setY(getY()+movement);
//	}
	
	public void move(long duration) {
		double movementX = duration * mx/1000;
		double movementY = duration * my/1000;
		// Always update
		if(GameStates.getWidth()>0 && GameStates.getHeight()>0) {
			setX(this.getX()+movementX);
			setY(this.getY()+movementY);
		}
	}
	
	public ServerSprite getSprite() {
		return this.sprite.get();
	}
	
	public void setSprite(ServerSprite sprite){
		this.sprite = AppContext.getDataManager().createReference(sprite);
		this.setHeight(sprite.getHeight());
		this.setWidth(sprite.getWidth());
	}
	
	@Override
	public void setY(double d) {
		this.y = d;
		this.setZIndex(d+this.getHeight());
	}

	public void changeDirection() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean collides(ServerEntity otherEntity) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.get().getWidth(), sprite.get().getHeight());
		Rectangle r2 = new Rectangle((int) otherEntity.x, (int) otherEntity.y, otherEntity.getSprite().getWidth(), otherEntity.getSprite().getHeight());
		return r1.intersects(r2);
	}
	
	public boolean contains(Point p) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.get().getWidth(), sprite.get().getHeight());
		return r1.contains(p);
	}
	
	@Override
	public double getHeight() {
		return this.getSprite().getHeight();
	}
	
	
	@Override
	public double getWidth() {
		return this.getSprite().getWidth();
	}
	
}
