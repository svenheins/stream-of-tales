package de.svenheins.animation;


import de.svenheins.objects.Space;

public class SpaceAnimation {
	protected Space space;
	
	protected double instantOfAnimation;
	protected boolean running;
	protected boolean ending;
	
	protected double[] time;
	
	public SpaceAnimation(Space space) {
		this.space = space;
		this.time = new double[]{0};
		this.instantOfAnimation = System.currentTimeMillis();
		this.running = false;
	}
	
	public void update(double timeNow) {

	}
	
	public double[] getTime() {
		return time;
	}
	
	/**
	 * @return last timePoint from a SORTED time array "time"
	 * 
	 */
	public double getLength() {
		double maxTimePoint = time[time.length-1];
		return maxTimePoint;
	}
	
	public void setTime(double[] time) {
		this.time= time;
	}
	
	public void start() {
		this.instantOfAnimation = System.currentTimeMillis();
		this.setRunning(true);
	}
	
	public void start( double waitMillis) {
		this.instantOfAnimation = System.currentTimeMillis() + waitMillis;
		this.setRunning(true);
	}
	
	public void end() {
		setRunning(false);
		setEnding(true);
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

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isEnding() {
		return ending;
	}

	public void setEnding(boolean ending) {
		this.ending = ending;
	}
}
