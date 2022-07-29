package de.svenheins.objects;


import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;

import de.svenheins.animation.Animation;

import de.svenheins.main.GameStates;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.SpriteManager;
import de.svenheins.managers.TileSetManager;


public class Entity extends LocalObject {
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	protected Sprite sprite;
	protected String[] standardAnimation;
	protected Animation animation;
	protected TileSet tile;
	protected boolean b_stdAni;
	
	//protected AnimationManager animationManager;
	
	public final static int DEFAULT_MOVEMENT_ON_X = 150;
	public final static int DEFAULT_MOVEMENT_ON_Y = 150;
	
	public Entity(String src, BigInteger id, float x, float y, float mx, float my) {
		standardAnimation = new String[1];
		standardAnimation[0] = src;
		animation = AnimationManager.manager.getAnimation(src);
		sprite = SpriteManager.manager.getSprite(src);
		this.x = x;
		this.y = y;
		this.setId(id);
		this.setName(src);
		int spriteWidth = sprite.getWidth();
		int spriteHeight = sprite.getHeight();
		this.tile = TileSetManager.manager.getTileSet(src, spriteWidth,spriteHeight);
		//my = DEFAULT_MOVEMENT_ON_Y;
		//mx = DEFAULT_MOVEMENT_ON_X;
		this.my = my;
		this.mx = mx;
		this.height = sprite.getHeight();
		this.width = sprite.getWidth();
	}
	
	public Entity(String animationName, String[] src, BigInteger id, float x, float y) {
		standardAnimation = src;
		animation = AnimationManager.manager.getAnimation(animationName, src);
		sprite = SpriteManager.manager.getSprite(src[0]);
		int spriteWidth = sprite.getWidth();
		int spriteHeight = sprite.getHeight();
		this.tile = TileSetManager.manager.getTileSet(src[0], spriteWidth,spriteHeight);
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
	
	public Entity(TileSet tileSet, String name, BigInteger id, float x, float y, long animationDelay) {
//		AnimationManager.manager.getAnimation(name, tileSet, 0, 0, animationDelay);
//		AnimationManager.manager.getAnimation(name+"standard", tileSet, 0, 3, animationDelay);
//		AnimationManager.manager.getAnimation(name+"standard", tileSet, 0, 3, animationDelay);
//		System.out.println(name+"standard");
		standardAnimation = tileSet.getTileNames(GameStates.ani_standard_start, GameStates.ani_standard_end);
//		System.out.println(standardAnimation[0]+" "+standardAnimation[1]+" "+standardAnimation[2]+" "+standardAnimation[3]);
		animation = AnimationManager.manager.getAnimation("standard", tileSet,  GameStates.ani_standard_start, GameStates.ani_standard_end, animationDelay);
		TileSetManager.manager.getTileSet(tileSet);
		sprite = new Sprite(tileSet.getTileImage(0));
		this.x = x;
		this.y = y;
		this.setId(id);
		this.setName(name);
		this.tile = tileSet;
		//my = DEFAULT_MOVEMENT_ON_Y;
		//mx = DEFAULT_MOVEMENT_ON_X;
		my = 0;
		mx = 0;
		this.height = sprite.getHeight();
		this.width = sprite.getWidth();
	}
	
//	public void moveOnX(long duration){
//		float movement = duration * mx/1000;
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
//		float movement = duration * my/1000;
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
		long complete = timeNow - animation.getInstantOfAnimation();
//		if (this.animation != AnimationManager.manager.getAnimation("standard", this.getTileSet(), 0, 3, 300) && complete>0) {
//			if(complete < animation.getLength()*animation.getTimeBetweenAnimation())
//				this.setSprite(animation.getSprite(timeNow));
//			else {
//				animation = AnimationManager.manager.getAnimation("standard", this.getTileSet(), GameStates.ani_standard_start, GameStates.ani_standard_end, GameStates.animationDelay);
//				this.setSprite(animation.getSprite(timeNow));
//			}
//		} else
//			// set the actual Animation back to standard animation
//			this.setSprite(AnimationManager.manager.getAnimation("standard", this.getTileSet(), GameStates.ani_standard_start, GameStates.ani_standard_end, GameStates.animationDelay).getSprite(timeNow));
		this.setSprite(animation.getSprite(timeNow));
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
	
	public void setAnimation(Animation animation) {
		this.animation= animation;
		animation.setTimeBetweenAnimation(this.getAnimation().getTimeBetweenAnimation());
		animation.setInstantOfAnimation(this.getAnimation().getInstantOfAnimation());
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
	
}
