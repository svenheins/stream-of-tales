/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;


import java.awt.Point;
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
	private boolean symmetricalEntity;
	
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
	public Entity(TileSet tileSet, String name, String room, BigInteger id, float x, float y, long animationDelay) {
		super(id, name, room, x, y, 0, 0, 0, 0, 0, 0);
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
		this.setSymmetricalEntity(true);
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
		if (this.getSprite()!= null)
		return this.getSprite().getHeight();
		else {
			return 0.0f;
		}
	}
	
	
	@Override
	public float getWidth() {
		if (this.getSprite()!= null)
		return this.getSprite().getWidth();
		else return 0.0f;
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
		if (distX > 2*distY) {
			if ((this.getX()+this.getWidth()/2) < p.x){
				if (this.getOrientation() != EntityStates.RIGHT)  {
//					this.startAnimation();
					if (!hasSymmetricalEntity()) this.calcShift(EntityStates.RIGHT);
					this.setOrientation(EntityStates.RIGHT);
					this.setChangedStates(true);
//					System.out.println("dist: "+(System.currentTimeMillis() -entity.getAnimation().getInstantOfAnimation()));
					
				}
			} else {
				if (this.getOrientation() != EntityStates.LEFT) {
//					this.startAnimation();
					if (!hasSymmetricalEntity()) this.calcShift(EntityStates.LEFT);
					this.setOrientation(EntityStates.LEFT);
					this.setChangedStates(true);
					
				}
			}
		} else if (distY > distX){
			if ((this.getY()+this.getHeight()*3/4) < p.y){
				if (this.getOrientation() != EntityStates.DOWN) {
//					this.startAnimation();
					if (!hasSymmetricalEntity()) this.calcShift(EntityStates.DOWN);
					this.setOrientation(EntityStates.DOWN);
					this.setChangedStates(true);
					
				}
			} else  {
				if (this.getOrientation() != EntityStates.UP) {
//					this.startAnimation();
					if (!hasSymmetricalEntity()) this.calcShift(EntityStates.UP);
					this.setOrientation(EntityStates.UP);
					this.setChangedStates(true);
					
				}
				
			}
		} else {
			// no changes, because the direction change was not strong enough
		}
	}
	
	public void determineAnimation() {
		/** check if there is a singleState */
		if (this.getSingleState() != EntityStates.EMPTY && this.getSingleAnimation() == null) {
			switch(this.getSingleState()) {
			case ATTACKING:
				this.determineAttackingAnimation();
				break;
			case HURTING:
				this.determineHurtingAnimation();
				break;
			default: ;
				break;
			}
		} else {
			/** only animate continuously, if no single State is executed */

			/** continuous animations */
			switch(this.getContinuousState()) {
			case STANDING:
				switch(this.getOrientation()) {
				case LEFT:
					this.setAnimation(AnimationManager.manager.getAnimation("standing_l", this.getTileSet(), GameStates.ani_standing_l_start, GameStates.ani_standing_l_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					this.setAnimation(AnimationManager.manager.getAnimation("standing_r", this.getTileSet(), GameStates.ani_standing_r_start, GameStates.ani_standing_r_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					this.setAnimation(AnimationManager.manager.getAnimation("standing_u", this.getTileSet(), GameStates.ani_standing_u_start, GameStates.ani_standing_u_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					this.setAnimation(AnimationManager.manager.getAnimation("standing_d", this.getTileSet(), GameStates.ani_standing_d_start, GameStates.ani_standing_d_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case WALKING:
				switch(this.getOrientation()) {
				case LEFT:
					this.setAnimation(AnimationManager.manager.getAnimation("walking_l", this.getTileSet(), GameStates.ani_walking_l_start, GameStates.ani_walking_l_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					this.setAnimation(AnimationManager.manager.getAnimation("walking_r", this.getTileSet(), GameStates.ani_walking_r_start, GameStates.ani_walking_r_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					this.setAnimation(AnimationManager.manager.getAnimation("walking_u", this.getTileSet(), GameStates.ani_walking_u_start, GameStates.ani_walking_u_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					this.setAnimation(AnimationManager.manager.getAnimation("walking_d", this.getTileSet(), GameStates.ani_walking_d_start, GameStates.ani_walking_d_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case SLEEPING:
				switch(this.getOrientation()) {
				case LEFT:
					this.setAnimation(AnimationManager.manager.getAnimation("sleeping_l", this.getTileSet(), GameStates.ani_sleeping_l_start, GameStates.ani_sleeping_l_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					this.setAnimation(AnimationManager.manager.getAnimation("sleeping_r", this.getTileSet(), GameStates.ani_sleeping_r_start, GameStates.ani_sleeping_r_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					this.setAnimation(AnimationManager.manager.getAnimation("sleeping_u", this.getTileSet(), GameStates.ani_sleeping_u_start, GameStates.ani_sleeping_u_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					this.setAnimation(AnimationManager.manager.getAnimation("sleeping_d", this.getTileSet(), GameStates.ani_sleeping_d_start, GameStates.ani_sleeping_d_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case DEAD:
				switch(this.getOrientation()) {
				case LEFT:
					this.setAnimation(AnimationManager.manager.getAnimation("dead_l", this.getTileSet(), GameStates.ani_dead_l_start, GameStates.ani_dead_l_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case RIGHT:
					this.setAnimation(AnimationManager.manager.getAnimation("dead_r", this.getTileSet(), GameStates.ani_dead_r_start, GameStates.ani_dead_r_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case UP:
					this.setAnimation(AnimationManager.manager.getAnimation("dead_u", this.getTileSet(), GameStates.ani_dead_u_start, GameStates.ani_dead_u_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				case DOWN:
					this.setAnimation(AnimationManager.manager.getAnimation("dead_d", this.getTileSet(), GameStates.ani_dead_d_start, GameStates.ani_dead_d_end, this.getAnimation().getTimeBetweenAnimation()));
					break;
				default: ;
				}
				break;
			case INVENTORY_DESTROYED:
				this.setAnimation(AnimationManager.manager.getAnimation("inventory_destroyed", this.getTileSet(), GameStates.ani_inventory_destroyed_start, GameStates.ani_inventory_destroyed_end, this.getAnimation().getTimeBetweenAnimation()));
				break;
			case INVENTORY_DAMAGED:
				this.setAnimation(AnimationManager.manager.getAnimation("inventory_damaged", this.getTileSet(), GameStates.ani_inventory_damaged_start, GameStates.ani_inventory_damaged_end, this.getAnimation().getTimeBetweenAnimation()));
				
				break;
			case INVENTORY_SIMPLE:
				this.setAnimation(AnimationManager.manager.getAnimation("inventory_simple", this.getTileSet(), GameStates.ani_inventory_simple_start, GameStates.ani_inventory_simple_end, this.getAnimation().getTimeBetweenAnimation()));
				
				break;
			case INVENTORY_GOOD:
				this.setAnimation(AnimationManager.manager.getAnimation("inventory_good", this.getTileSet(), GameStates.ani_inventory_good_start, GameStates.ani_inventory_good_end, this.getAnimation().getTimeBetweenAnimation()));
				
				break;
			case INVENTORY_PERFECT:
				this.setAnimation(AnimationManager.manager.getAnimation("inventory_perfect", this.getTileSet(), GameStates.ani_inventory_perfect_start, GameStates.ani_inventory_perfect_end, this.getAnimation().getTimeBetweenAnimation()));
				
				break;
			case INVENTORY_MAGIC:
				this.setAnimation(AnimationManager.manager.getAnimation("inventory_magic", this.getTileSet(), GameStates.ani_inventory_magic_start, GameStates.ani_inventory_magic_end, this.getAnimation().getTimeBetweenAnimation()));
				
				break;
			case FIRE:
				this.setAnimation(AnimationManager.manager.getAnimation("fire", this.getTileSet(), 0, 7, this.getAnimation().getTimeBetweenAnimation()));
				
				break;
			default: ;
					
			}
		}
	}
	
	public void setStandingAnimation () {
		if (this.getContinuousState() == EntityStates.WALKING) {
			this.setChangedStates(true);
			this.setContinuousState(EntityStates.STANDING);
			this.setSingleState(EntityStates.EMPTY);
		}
	}
	
	public void setWalkingAnimation() {
			if (this.getContinuousState() != EntityStates.WALKING) {
				this.setChangedStates(true);
				this.setContinuousState(EntityStates.WALKING);
				this.setSingleState(EntityStates.EMPTY);
			}
	}

	
	protected void determineAttackingAnimation() {
		long aniDuration = this.getAnimation().getTimeBetweenAnimation()/2;
		switch(this.getOrientation()) {
		case LEFT:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_l", this.getTileSet(), GameStates.ani_attacking_l_start, GameStates.ani_attacking_l_end, aniDuration));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("attacking_l", this.getTileSet(), GameStates.ani_attacking_l_start, GameStates.ani_attacking_l_end, aniDuration);
			break;
		case RIGHT:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_r", this.getTileSet(), GameStates.ani_attacking_r_start, GameStates.ani_attacking_r_end, aniDuration));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("attacking_r", this.getTileSet(), GameStates.ani_attacking_r_start, GameStates.ani_attacking_r_end, aniDuration);
			break;
		case UP:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_u", this.getTileSet(), GameStates.ani_attacking_u_start, GameStates.ani_attacking_u_end, aniDuration));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("attacking_u", this.getTileSet(), GameStates.ani_attacking_u_start, GameStates.ani_attacking_u_end, aniDuration);
			break;
		case DOWN:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("attacking_d", this.getTileSet(), GameStates.ani_attacking_d_start, GameStates.ani_attacking_d_end, aniDuration));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("attacking_d", this.getTileSet(), GameStates.ani_attacking_d_start, GameStates.ani_attacking_d_end, aniDuration);
			break;
			
		default: ;
		}
	}
	
	protected void determineHurtingAnimation() {
		switch(this.getOrientation()) {
		case LEFT:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("hurting_l", this.getTileSet(), GameStates.ani_hurting_l_start, GameStates.ani_hurting_l_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("hurting_l", this.getTileSet(), GameStates.ani_hurting_l_start, GameStates.ani_hurting_l_end, this.getAnimation().getTimeBetweenAnimation());
			break;
		case RIGHT:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("hurting_r", this.getTileSet(), GameStates.ani_hurting_r_start, GameStates.ani_hurting_r_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("hurting_r", this.getTileSet(), GameStates.ani_hurting_r_start, GameStates.ani_hurting_r_end, this.getAnimation().getTimeBetweenAnimation());
			break;
		case UP:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("hurting_u", this.getTileSet(), GameStates.ani_hurting_u_start, GameStates.ani_hurting_u_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("hurting_u", this.getTileSet(), GameStates.ani_hurting_u_start, GameStates.ani_hurting_u_end, this.getAnimation().getTimeBetweenAnimation());
			break;
		case DOWN:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("hurting_d", this.getTileSet(), GameStates.ani_hurting_d_start, GameStates.ani_hurting_d_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("hurting_d", this.getTileSet(), GameStates.ani_hurting_d_start, GameStates.ani_hurting_d_end, this.getAnimation().getTimeBetweenAnimation());
			break;
			
		default: ;
		}
	}
	
	protected void determineWorkingAnimation() {
		switch(this.getOrientation()) {
		case LEFT:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("working_l", this.getTileSet(), GameStates.ani_working_l_start, GameStates.ani_working_l_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("working_l", this.getTileSet(), GameStates.ani_working_l_start, GameStates.ani_working_l_end, this.getAnimation().getTimeBetweenAnimation());
			break;
		case RIGHT:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("working_r", this.getTileSet(), GameStates.ani_working_r_start, GameStates.ani_working_r_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("working_r", this.getTileSet(), GameStates.ani_working_r_start, GameStates.ani_working_r_end, this.getAnimation().getTimeBetweenAnimation());
			break;
		case UP:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("working_u", this.getTileSet(), GameStates.ani_working_u_start, GameStates.ani_working_u_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("working_u", this.getTileSet(), GameStates.ani_working_u_start, GameStates.ani_working_u_end, this.getAnimation().getTimeBetweenAnimation());
			break;
		case DOWN:
			this.setSingleAnimation(AnimationManager.manager.getAnimation("working_d", this.getTileSet(), GameStates.ani_working_d_start, GameStates.ani_working_d_end, this.getAnimation().getTimeBetweenAnimation()));
			if (this.getSingleAnimation() != null) this.getSingleAnimation().start();
			else AnimationManager.manager.getAnimation("working_d", this.getTileSet(), GameStates.ani_working_d_start, GameStates.ani_working_d_end, this.getAnimation().getTimeBetweenAnimation());
			break;
			
		default: ;
		}
	}

	public boolean hasSymmetricalEntity() {
		return symmetricalEntity;
	}

	public void setSymmetricalEntity(boolean symmetricalEntity) {
		this.symmetricalEntity = symmetricalEntity;
	}
	
	
	public int determineAnimationWidth(EntityStates orientation) {
		int retWidth = 0;
		switch(orientation) {
		case LEFT:
			retWidth =  AnimationManager.manager.getAnimation("standing_l", this.getTileSet(), GameStates.ani_standing_l_start, GameStates.ani_standing_l_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getWidth();
			break;
		case RIGHT:
			retWidth =  AnimationManager.manager.getAnimation("standing_r", this.getTileSet(), GameStates.ani_standing_r_start, GameStates.ani_standing_r_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getWidth();
			break;
		case UP:
			retWidth =  AnimationManager.manager.getAnimation("standing_u", this.getTileSet(), GameStates.ani_standing_u_start, GameStates.ani_standing_u_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getWidth();
			break;
		case DOWN:
			retWidth =  AnimationManager.manager.getAnimation("standing_d", this.getTileSet(), GameStates.ani_standing_d_start, GameStates.ani_standing_d_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getWidth();
			break;
		default: ;
		}	
		return retWidth;
	}
	
	public int determineAnimationHeight(EntityStates orientation) {
		int retHeight = 0;
		switch(orientation) {
		case LEFT:
			retHeight =  AnimationManager.manager.getAnimation("standing_l", this.getTileSet(), GameStates.ani_standing_l_start, GameStates.ani_standing_l_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getHeight();
			break;
		case RIGHT:
			retHeight =  AnimationManager.manager.getAnimation("standing_r", this.getTileSet(), GameStates.ani_standing_r_start, GameStates.ani_standing_r_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getHeight();
			break;
		case UP:
			retHeight =  AnimationManager.manager.getAnimation("standing_u", this.getTileSet(), GameStates.ani_standing_u_start, GameStates.ani_standing_u_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getHeight();
			break;
		case DOWN:
			retHeight =  AnimationManager.manager.getAnimation("standing_d", this.getTileSet(), GameStates.ani_standing_d_start, GameStates.ani_standing_d_end, this.getAnimation().getTimeBetweenAnimation()).getSprite(0).getHeight();
			break;
		default: ;
		}	
		return retHeight;
	}
	
	/** rotate the entity if it is unsymmetrical and walking on all four */
	public void calcShift(EntityStates nextOrientation) {
		float widthNow = this.getSprite().getWidth();
		float heightNow = this.getSprite().getHeight();
		float widthNext = determineAnimationWidth(nextOrientation);
		float heightNext = determineAnimationHeight(nextOrientation);
		if (widthNow != widthNext || heightNow != heightNext) {
			this.setX(this.getX()+widthNow/2 - widthNext/2);
			this.setY(this.getY()+heightNow/2 - heightNext/2);
		}
	}
}
