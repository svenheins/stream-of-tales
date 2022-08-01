package de.svenheins.animation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import de.svenheins.objects.Sprite;
import de.svenheins.objects.TileSet;

public class Animation {
	
	//private static final long serialVersionUID = 1L;
	public List<Sprite> spriteList;
	private long timeBetweenAnimation;
	private long instantOfAnimation;
	private boolean running;
	private boolean ending;
	private String name;
	
	//public Sprite sprite;
	
	// Standard Constructor
	public Animation(Sprite sprite) {
		this.timeBetweenAnimation = -1;
		this.spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		this.instantOfAnimation = System.currentTimeMillis();
		this.running = false;
	}
	
	// Full Constructor
	public Animation(List<Sprite> spriteList, long delay) {
		this.timeBetweenAnimation = delay;
		this.spriteList = spriteList;
		this.instantOfAnimation  = System.currentTimeMillis();
		this.running = false;
	}
	
	/** constructor for TileSet */
	public Animation(TileSet tileAnimation, long delay) {
		this.timeBetweenAnimation = delay;
		this.spriteList = new ArrayList<Sprite>();
		for (BufferedImage image: tileAnimation.getTileSet()) {
			spriteList.add(new Sprite(image));
		}
		this.instantOfAnimation  = System.currentTimeMillis();
		this.running = false;
		this.setName(tileAnimation.getName());
	}
	
	public Sprite getSprite(long timeNow) {
		if (spriteList.size() > 0) {
			if ((running == true || ending == true) && timeBetweenAnimation > 0 && instantOfAnimation <= timeNow) {
				long difference = timeNow - this.instantOfAnimation;
				int loop = ((int) Math.floor(difference / this.timeBetweenAnimation));
				int index =  loop % spriteList.size();
//				if (difference<5000)System.out.println(index);
				// Check if the last Spriteframe was reached
				if(ending == true && loop == spriteList.size() ) ending = false;
				return spriteList.get(index);
			} else {
				return spriteList.get(0);
			}
		}
		else 
			return null;
	}
	
	public long getTimeBetweenAnimation() {
		return timeBetweenAnimation;
	}
	
	public void setTimeBetweenAnimation(long timeBetweenAnimation) {
		this.timeBetweenAnimation = timeBetweenAnimation;
	}
	
	public void start() {
		this.instantOfAnimation = System.currentTimeMillis();
		this.running = true;
	}
	
	public void start( long waitMillis) {
		this.instantOfAnimation = System.currentTimeMillis() + waitMillis;
		this.running = true;
	}
	
	public void end() {
		running = false;
		ending = true;
	}
	
	public void addSprite(Sprite sprite){
		spriteList.add(sprite);
	}
	
	public void runOnce() {
		start();
		end();
	}
	public long getInstantOfAnimation() {
		return instantOfAnimation;
	}
	public void setInstantOfAnimation(long instantOfAnimation) {
		this.instantOfAnimation = instantOfAnimation;
	}
	
	public int getLength() {
		return this.spriteList.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
