package de.svenheins.animation;

import java.util.ArrayList;
import java.util.List;

import de.svenheins.objects.Sprite;

public class Animation {
	
	//private static final long serialVersionUID = 1L;
	public List<Sprite> spriteList;
	private double timeBetweenAnimation;
	private double instantOfAnimation;
	private boolean running;
	private boolean ending;
	
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
	public Animation(List<Sprite> spriteList, double delay) {
		this.timeBetweenAnimation = delay;
		this.spriteList = spriteList;
		this.instantOfAnimation  = System.currentTimeMillis();
		this.running = false;
	}
	
	public Sprite getSprite(double timeNow, String[] stdAnimation) {
		if (spriteList.size() > 0) {
			if ((running == true || ending == true) && timeBetweenAnimation > 0 && instantOfAnimation <= timeNow) {
				double difference = timeNow - this.instantOfAnimation;
				int loop = ((int) Math.floor(difference / this.timeBetweenAnimation));
				int index =  loop % spriteList.size();
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
	
	public double getTimeBetweenAnimation() {
		return timeBetweenAnimation;
	}
	
	public void setTimeBetweenAnimation(double timeBetweenAnimation) {
		this.timeBetweenAnimation = timeBetweenAnimation;
	}
	
	public void start() {
		this.instantOfAnimation = System.currentTimeMillis();
		this.running = true;
	}
	
	public void start( double waitMillis) {
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
	public double getInstantOfAnimation() {
		return instantOfAnimation;
	}
	public void setInstantOfAnimation(double instantOfAnimation) {
		this.instantOfAnimation = instantOfAnimation;
	}
	
	public int getLength() {
		return this.spriteList.size();
	}
}
