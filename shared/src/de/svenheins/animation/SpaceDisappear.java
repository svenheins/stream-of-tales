package de.svenheins.animation;

import de.svenheins.objects.Space;

public class SpaceDisappear extends SpaceAnimation {
	private int[] colorBefore;
	private float transBefore;
	private Boolean filledBefore;
	
	
	public SpaceDisappear(Space space) {
		super(space);
		this.time = new double[]{0, 300};
		this.colorBefore = space.getRGB();
		this.transBefore = space.getTrans();
		this.filledBefore = true;
		this.instantOfAnimation = System.currentTimeMillis();
		// execute once
		this.runOnce();
	}

	@Override
	public void update(double timeNow) {
		if ((running == true || ending == true) && instantOfAnimation <= timeNow) {
			double difference = timeNow - this.instantOfAnimation;
			if (difference < this.time[1] && difference > this.time[0])
				this.space.animate(this.space.getRGB(), this.space.getTrans(), false);
			else 
				//this.space.animate(this.space.getColor(), this.space.getTrans(), true);
			
			if(ending == true && this.getLength() < difference ) ending = false;
		} else {
			this.space.animate(this.colorBefore, this.transBefore, this.filledBefore);
		}
	}
	
	public Boolean getFilledBefore() {
		return filledBefore;
	}
	
	public void setRGBBefore(int[] colorBefore) {
		this.colorBefore = colorBefore;
	}
	
	public int[] getColorBefore() {
		return colorBefore;
	}
	
	public void setFilledBefore(Boolean filledBefore) {
		this.filledBefore = filledBefore;
	}
	
	public void setTransBefore(float transBefore) {
		this.transBefore = transBefore;
	}
	
	public float getTransBefore() {
		return transBefore;
	}
}
