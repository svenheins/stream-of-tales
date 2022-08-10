package de.svenheins.objects;


import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;

import de.svenheins.animation.Animation;

import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.TileSetManager;


public class Entity extends LocalObject {
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	protected Sprite sprite;
	protected String[] standardAnimation;
	protected Animation animation;
	protected Animation singleAnimation;
	
	protected TileSet tile;
	protected boolean b_stdAni;
	/** variables for the entity appearance */
	protected EntityStates orientation;
	protected EntityStates singleState;
	protected EntityStates continuousState;

	protected boolean changedStates;
	protected boolean waitingForSingleAnimation;

	
//	public Entity(String src, BigInteger id, float x, float y, float mx, float my) {
//		standardAnimation = new String[1];
//		standardAnimation[0] = src;
//		animation = AnimationManager.manager.getAnimation(src);
//		sprite = SpriteManager.manager.getSprite(src);
//		this.x = x;
//		this.y = y;
//		this.setId(id);
//		this.setName(src);
//		int spriteWidth = sprite.getWidth();
//		int spriteHeight = sprite.getHeight();
//		this.tile = TileSetManager.manager.getTileSet(src, spriteWidth,spriteHeight);
//		this.my = my;
//		this.mx = mx;
//		this.height = sprite.getHeight();
//		this.width = sprite.getWidth();
//	}
	
//	public Entity(String animationName, String[] src, BigInteger id, float x, float y) {
//		standardAnimation = src;
//		animation = AnimationManager.manager.getAnimation(animationName, src);
//		sprite = SpriteManager.manager.getSprite(src[0]);
//		int spriteWidth = sprite.getWidth();
//		int spriteHeight = sprite.getHeight();
//		this.tile = TileSetManager.manager.getTileSet(src[0], spriteWidth,spriteHeight);
//		this.x = x;
//		this.y = y;
//		this.setId(id);
//		my = 0;
//		mx = 0;
//		this.height = sprite.getHeight();
//		this.width = sprite.getWidth();
//	}
	
	
	/** standard constructor for entities */
	public Entity(TileSet tileSet, String name, BigInteger id, float x, float y, long animationDelay) {
		super();
		standardAnimation = tileSet.getTileNames(GameStates.ani_standard_start, GameStates.ani_standard_end);
		animation = AnimationManager.manager.getAnimation("standard", tileSet,  GameStates.ani_standard_start, GameStates.ani_standard_end, animationDelay);
		TileSetManager.manager.getTileSet(tileSet);
		sprite = new Sprite(tileSet.getTileImage(0));
		this.x = x;
		this.y = y;
		this.setId(id);
		this.setName(name);
		this.tile = tileSet;
		my = 0;
		mx = 0;
		this.setMaxSpeed(GameStates.entityMaxSpeed);
		this.height = sprite.getHeight();
		this.width = sprite.getWidth();
		this.setOrientation(EntityStates.DOWN);
		this.setSingleState(EntityStates.EMPTY);
		this.setContinuousState(EntityStates.STANDING);
		changedStates = false;
		singleAnimation = null;
		this.setWaitingForSingleAnimation(false);
	}
	
	public EntityStates getOrientation() {
		return orientation;
	}

	public void setOrientation(EntityStates orientation) {
		this.orientation = orientation;
	}

	public EntityStates getSingleState() {
		return singleState;
	}

	public void setSingleState(EntityStates state) {
		this.singleState = state;
	}

	public void move(long duration) {
		float movementX = duration * mx/1000;
		float movementY = duration * my/1000;
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
		long timeNow = System.currentTimeMillis();
//		long complete = timeNow - animation.getInstantOfAnimation();
		
		/** check if there is a single Animation */
		if (singleAnimation != null) {
			/** update SingleAnimation */
			if ( (timeNow-this.getSingleAnimation().getInstantOfAnimation()) >= singleAnimation.getTimeBetweenAnimation()*singleAnimation.getLength() ) {
				/** here the animation ends so we start with standard animation at 0*/
				this.singleAnimation = null;
				this.singleState = EntityStates.EMPTY;
				this.setWaitingForSingleAnimation(false);
				this.startAnimation();
				this.setChangedStates(true);
				this.setSprite(animation.getSprite(timeNow));
			} else {
				/** continue with the single animation */
				this.setSprite(singleAnimation.getSprite(timeNow));
			}
		} else {
			this.setSprite(animation.getSprite(timeNow));
		}
	}
	
	
	
	public Animation getAnimation() {
		return this.animation;
	}
	
	public void changeAnimation(String name, String[] src, long timeBetweenAnimation, long instantOfAnimation) {
		animation = AnimationManager.manager.getAnimation(name, src);
		animation.setTimeBetweenAnimation(timeBetweenAnimation);
		animation.setInstantOfAnimation(instantOfAnimation);
	}
	
	public void changeAnimation(String src, long timeBetweenAnimation, long instantOfAnimation) {
		animation = AnimationManager.manager.getAnimation(src);
		animation.setTimeBetweenAnimation(timeBetweenAnimation);
		animation.setInstantOfAnimation(instantOfAnimation);
	}
	
	public void runAnimationOnce(String name, String[] src, long timeBetweenAnimation, long instantOfAnimation){
		animation = AnimationManager.manager.getAnimation(name, src);
		animation.setTimeBetweenAnimation(timeBetweenAnimation);
		animation.setInstantOfAnimation(instantOfAnimation);
		animation.start();
		
	}
	
	public void setAnimation(Animation animationNew) {
		this.animation= animationNew;
//		animation.setTimeBetweenAnimation(this.getAnimation().getTimeBetweenAnimation());
//		animation.setInstantOfAnimation(this.getAnimation().getInstantOfAnimation());
//		animation.start();
	}
	
	public void startAnimation() {
		animation.start();
	}
	
	public void endAnimation() {
		animation.end();
	}
	
	@Override
	public void setY(float d) {
		this.y = d;
		this.setZIndex(d+this.getHeight());
	}

	public void changeDirection() {
		// TODO Auto-generated method stub
	}
	
//	public boolean collides(Entity otherEntity) {
//		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
//		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
//		Rectangle r2 = new Rectangle((int) otherEntity.x, (int) otherEntity.y, otherEntity.getSprite().getWidth(), otherEntity.getSprite().getHeight());
//		return r1.intersects(r2);
//	}
//	
//	public boolean contains(Point p) {
//		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
//		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
//		return r1.contains(p);
//	}
	
	@Override
	public float getHeight() {
		return this.getSprite().getHeight();
	}
	
	
	@Override
	public float getWidth() {
		return this.getSprite().getWidth();
	}
	
	public TileSet getTileSet() {
		return tile;
	}
	
	public void setTileSet(TileSet tile) {
		this.tile = tile;
	}
	
	public void setChangedStates(boolean b) {
		this.changedStates = b;
	}
	
	public boolean hasChangedStates() {
		return this.changedStates;
	}
	
	public Animation getSingleAnimation() {
		return singleAnimation;
	}

	public void setSingleAnimation(Animation singleAnimation) {
		this.singleAnimation = singleAnimation;
	}
	
	public EntityStates getContinuousState() {
		return continuousState;
	}

	public void setContinuousState(EntityStates continuousState) {
		this.continuousState = continuousState;
	}

	public boolean isWaitingForSingleAnimation() {
		return waitingForSingleAnimation;
	}

	public void setWaitingForSingleAnimation(boolean waitingForSingleAnimation) {
		this.waitingForSingleAnimation = waitingForSingleAnimation;
	}
	
	public void determineOrientation(Point p) {
		int distX = Math.abs((int) (this.getX()+this.getWidth()/2)-p.x);
		int distY = Math.abs((int) (this.getY()+this.getHeight()*3/4)-p.y);
		
		/** define orientation by point of the mouse */
		if (distX > distY) {
			if ((this.getX()+this.getWidth()/2) < p.x){
				if (this.getOrientation() != EntityStates.RIGHT)  {
//					this.startAnimation();
					this.setOrientation(EntityStates.RIGHT);
					this.setChangedStates(true);
//					System.out.println("dist: "+(System.currentTimeMillis() -entity.getAnimation().getInstantOfAnimation()));
					
				}
			} else {
				if (this.getOrientation() != EntityStates.LEFT) {
//					this.startAnimation();
					this.setOrientation(EntityStates.LEFT);
					this.setChangedStates(true);
					
				}
			}
		} else {
			if ((this.getY()+this.getHeight()*3/4) < p.y){
				if (this.getOrientation() != EntityStates.DOWN) {
//					this.startAnimation();
					this.setOrientation(EntityStates.DOWN);
					this.setChangedStates(true);
					
				}
			} else  {
				if (this.getOrientation() != EntityStates.UP) {
//					this.startAnimation();
					this.setOrientation(EntityStates.UP);
					this.setChangedStates(true);
					
				}
				
			}
		}
	}
}
