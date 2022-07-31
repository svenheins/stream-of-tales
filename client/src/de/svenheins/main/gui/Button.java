package de.svenheins.main.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;

import de.svenheins.animation.Animation;
import de.svenheins.main.GameStates;
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.Sprite;
import de.svenheins.objects.TileSet;

public class Button extends LocalObject{		
		/**
		 * 
		 */
		//private static final long serialVersionUID = 1L;
		protected Sprite sprite;
		protected String[] standardAnimation;
		protected Animation animation;
		protected TileSet tile;
		protected boolean b_stdAni;
		
		protected String strValue;
		protected int intValue;
		protected boolean activated;
		protected String text;
		
		
		public Button(TileSet tileSet, String name, BigInteger id, float x, float y, long animationDelay, String strValue, int intValue, String text) {
			standardAnimation = tileSet.getTileNames(GameStates.button_inactive_start, GameStates.button_inactive_end);
//			System.out.println(standardAnimation[0]+" "+standardAnimation[1]+" "+standardAnimation[2]+" "+standardAnimation[3]);
			animation = AnimationManager.manager.getAnimation("inactive", tileSet,  GameStates.button_inactive_start, GameStates.button_inactive_end, animationDelay);
			TileSetManager.manager.getTileSet(tileSet);
			sprite = new Sprite(tileSet.getTileImage(0));
			this.x = x;
			this.y = y;
			this.setId(id);
			this.setName(name);
			this.tile = tileSet;
			my = 0;
			mx = 0;
			this.height = sprite.getHeight();
			this.width = sprite.getWidth();
			this.strValue = strValue;
			this.intValue = intValue;
			setInactive();
			this.text = text;
//			this.setActivated(false);
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
			long complete = timeNow - animation.getInstantOfAnimation();
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
//			animation.start();
		}
		
		public void setInactive() {
			setActivated(false);
			setAnimation(AnimationManager.manager.getAnimation("inactive", getTileSet(), GameStates.button_inactive_start, GameStates.button_inactive_end, getAnimation().getTimeBetweenAnimation()));
		}
		
		public void setActive() {
			setActivated(true);
			setAnimation(AnimationManager.manager.getAnimation("active", getTileSet(), GameStates.button_active_start, GameStates.button_active_end, getAnimation().getTimeBetweenAnimation()));
		}
		
		public void setMouseover() {
			setAnimation(AnimationManager.manager.getAnimation("mouseOver", getTileSet(), GameStates.button_mouseOver_start, GameStates.button_mouseOver_end, getAnimation().getTimeBetweenAnimation()));
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
		
		public boolean collides(Button otherButton) {
			//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
			Rectangle r1 = new Rectangle((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
			Rectangle r2 = new Rectangle((int) otherButton.x, (int) otherButton.y, otherButton.getSprite().getWidth(), otherButton.getSprite().getHeight());
			return r1.intersects(r2);
		}
		
		public boolean contains(Point p) {
			//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
			Rectangle r1 = new Rectangle((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
			if (r1.contains(p))
			return true;
			else return false;
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

		public boolean isActivated() {
			return activated;
		}
		
		public void setActivated(boolean b) {
			this.activated = b;
		}
		
		public void setIntValue(int i) {
			this.intValue = i;
		}
		public int getIntValue() {
			return this.intValue;
		}
		
		public void setStrValue(String str) {
			this.strValue = str;
		}
		
		public String getStrValue() {
			return this.strValue;
		}
		
		public String getText() {
			return text;
		}
		
		public void setText( String text) {
			this.text = text;
		}

}
