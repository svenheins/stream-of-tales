package de.svenheins.objects;


import java.awt.Point;
import java.awt.Rectangle;

import de.svenheins.animation.Animation;

import de.svenheins.main.GameStates;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.SpriteManager;


public class Entity extends LocalObject {
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	protected Sprite sprite;
	protected String[] standardAnimation;
	protected Animation animation;
	protected boolean b_stdAni;
	
	//protected AnimationManager animationManager;
	
	public final static int DEFAULT_MOVEMENT_ON_X = 300;
	public final static int DEFAULT_MOVEMENT_ON_Y = 300;
	
	public Entity(String src, int id, double x, double y) {
		standardAnimation = new String[1];
		standardAnimation[0] = src;
		animation = AnimationManager.manager.getAnimation(src);
		sprite = SpriteManager.manager.getSprite(src);
		this.x = x;
		this.y = y;
		this.setId(id);
		this.setName(src);
		//my = DEFAULT_MOVEMENT_ON_Y;
		//mx = DEFAULT_MOVEMENT_ON_X;
		my = 0;
		mx = 0;
		this.height = sprite.getHeight();
		this.width = sprite.getWidth();
	}
	
	public Entity(String[] src, int id, double x, double y) {
		standardAnimation = src;
		animation = AnimationManager.manager.getAnimation(src);
		sprite = SpriteManager.manager.getSprite(src[0]);
		this.x = x;
		this.y = y;
		this.setId(id);
		//my = DEFAULT_MOVEMENT_ON_Y;
		//mx = DEFAULT_MOVEMENT_ON_X;
		my = 0;
		mx = 0;
		this.height = sprite.getHeight();
		this.width = sprite.getWidth();
	}
	
//	public void moveOnX(long duration){
//		double movement = duration * mx/1000;
//		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
//		if(GameStates.getWidth()>0) {
//			if(x+movement >0 ) {
//				if ( x+movement+sprite.getWidth() < GameStates.getWidth()) {
//					x+=movement;
//				}
//				else {
//					x = GameStates.getWidth()-sprite.getWidth();
//				}
//			}
//			else x = 0;
//		}
//	}
//	
//	public void moveOnY(long duration){
//		double movement = duration * my/1000;
//		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
//		if(GameStates.getHeight() != 0) {
//			if(y+movement >0 ) {
//				if ( y+movement+sprite.getHeight() < GameStates.getHeight()) {
//					setY(getY()+movement);
//				}
//				else {
//					y = GameStates.getHeight()-sprite.getHeight();
//				}
//			}
//			else y = 0;
//		}
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
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
		this.setHeight(sprite.getHeight());
		this.setWidth(sprite.getWidth());
	}
	
	public void updateSprite() {
		double timeNow = System.currentTimeMillis();
		double complete = timeNow - animation.getInstantOfAnimation();
		if (this.animation != AnimationManager.manager.getAnimation(standardAnimation) && complete>0) {
			if(complete < animation.getLength()*animation.getTimeBetweenAnimation())
				this.setSprite(animation.getSprite(timeNow, this.standardAnimation));
			else {
				animation = AnimationManager.manager.getAnimation(standardAnimation);
				this.setSprite(animation.getSprite(timeNow, standardAnimation));
			}
		} else
			this.setSprite(AnimationManager.manager.getAnimation(standardAnimation).getSprite(timeNow, this.standardAnimation));
	}
	
	
	
	public Animation getAnimation() {
		return this.animation;
	}
	
	public void changeAnimation(String[] src, double timeBetweenAnimation, double instantOfAnimation) {
		animation = AnimationManager.manager.getAnimation(src);
		animation.setTimeBetweenAnimation(timeBetweenAnimation);
		animation.setInstantOfAnimation(instantOfAnimation);
	}
	
	public void changeAnimation(String src, double timeBetweenAnimation, double instantOfAnimation) {
		animation = AnimationManager.manager.getAnimation(src);
		animation.setTimeBetweenAnimation(timeBetweenAnimation);
		animation.setInstantOfAnimation(instantOfAnimation);
	}
	
	public void runAnimationOnce(String[] src, double timeBetweenAnimation, double instantOfAnimation){
		animation = AnimationManager.manager.getAnimation(src);
		animation.setTimeBetweenAnimation(timeBetweenAnimation);
		animation.setInstantOfAnimation(instantOfAnimation);
		animation.start();
		
	}
	
	public void startAnimation() {
		animation.start();
	}
	
	public void endAnimation() {
		animation.end();
	}
	
	@Override
	public void setY(double d) {
		this.y = d;
		this.setZIndex(d+this.getHeight());
	}

	public void changeDirection() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean collides(Entity otherEntity) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
		Rectangle r2 = new Rectangle((int) otherEntity.x, (int) otherEntity.y, otherEntity.getSprite().getWidth(), otherEntity.getSprite().getHeight());
		return r1.intersects(r2);
	}
	
	public boolean contains(Point p) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
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
