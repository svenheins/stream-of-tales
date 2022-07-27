package de.svenheins.objects;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import com.sun.sgs.app.ManagedReference;

import de.svenheins.World;
import de.svenheins.functions.MyMath;
public class ServerAgentEmployee extends ServerAgent  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** settled */
	private boolean settled;


	public ServerAgentEmployee(ServerSprite sprite, BigInteger id, float x,
			float y, float mx, float my) {
		super(sprite, id, x, y, mx, my);
		this.settled = false;
	}

	
	@Override
	public void updateLocation() {
		/** update Location only if not settled yet */
		if (!isSettled()) {
			if (satisfaction > 0.6) { // (this.getHorizontalMovement() != 0 || this.getVerticalMovement() !=0)) {
				/** be lucky and stay here */
				setDesiredPosition((float)this.getX(), (float)this.getY());
				setMovement(0, 0);
				if( ((ServerRegion) this.getRoom().getSpaces().get(this.getRegion()).getForUpdate()).addResident(new Point((int) this.getX(), (int) this.getY()))) {
					/** be happy */
					this.setSatisfaction(1.0f);
					this.setSettled(true);
				} else
				{
					this.setSatisfaction(0.0f);
					this.setGoal(false);
				}
			} else
			{
				if(!hasGoal()){
					/** find a better place */
					searchBetterLocation();
					/** reset Movement speed to max speed*/
					this.setHorizontalMovement(this.getSpeed()/2);
					this.setVerticalMovement(this.getSpeed()/2);
				} else {
	//				if (this instanceof ServerAgentEntrepreneur) {
	//					if(((ServerAgentEntrepreneur)this).validateGoal() == true) {
	//						updateMovement();
	//					} else {
	//						this.setGoal(false);
	//					}
	//				} else {
						/** the agent has a goal, so he can try to go there */
						/** first the agent has to check if the goal is achievable */
						if(validateGoal() == true) {
							if(this.getHorizontalMovement() < 4 && this.getVerticalMovement() <4 ) {
								this.setMovement(this.speed/2, this.speed/2);
							}
							updateMovement();
						} else {
							/** the goal is not achievable, so the agent has to search another goal */
							this.setGoal(false);
						}
	//				}
					
				}
			}
		}
	}
	
	
	@Override
	/** find a location based on needs */
	public void searchBetterLocation(){
		float localX = (float) this.getX();
		float localY = (float) this.getY();
		float desX = localX;
		float desY = localY;
		float distance = this.range;
		float tempDistance =0;
		for(ManagedReference<ServerSpace> serverSpace : this.getRoom().getSpaces().values()) {
//			if(new Random().nextBoolean()) continue;
			ServerSpace s_space = serverSpace.get();
			
			if(s_space instanceof ServerRegion) {
				/** Create a Space, to compute some stuff (faster and fewer errors) */
				Space space = new Space(s_space.getName(), s_space.getId(), new int[]{0,0,0}, true, 0.5f, s_space.getScale());
				space.setAllXY(s_space.getX(), s_space.getY());
//				ArrayList<Point> blockedPoints = ((ServerRegion) s_space).getBlockedPoints();
//				/** if capacity of region is reached, continue with next region */
//				if (blockedPoints.size() < ((ServerRegion) s_space).getResidents().size()*3) {
//					continue;
//				}
//					logger.log(Level.INFO, "Space found: "+serverSpace.get().getName());
				/** iterate all the space-polygons */
				for (int i = 0; i< space.getPolygon().size(); i++) {
					Polygon polygon = space.getPolygon().get(i);
					Point point = new Point((int) localX, (int) localY);
//						/** if the enterprise is in a polygon */
//						if (polygon.contains(point)) {
//							this.setSatisfaction(0.9f);
//							desX = localX;
//							desY = localY;
//							return;
//						} else {
//							/** calculate distance from actual polygon */
//							tempDistance = (float) MyMath.getDistance(polygon, point) ;
					Rectangle rect = polygon.getBounds();
					int rectSize = Math.max(rect.height, rect.width);
					float middleX = (float) rect.getCenterX();
					float middleY = (float) rect.getCenterY();
					tempDistance = (float) MyMath.getDistance(point, new Point((int) middleX, (int) middleY));
//						/** and save it if its smaller than minimum */
//						if (tempDistance < distance) {
					distance = tempDistance;
//							Point desPoint = MyMath.getNearest(polygon, point);
					Point potentialPoint = new Point((int) middleX, (int) middleY);
					/** we found a suitable potential polygon, so we search a free spot */
//						for( int distancePoint = 0; distancePoint < rectSize;distancePoint += rectSize/10) {
					int distancePoint = (int) (Math.random()*rectSize/5);
					boolean pointInsidePolygon = false;
					int intShots = 0;
					while(!pointInsidePolygon && intShots <3) {
						intShots ++;
						potentialPoint= MyMath.randomShot(new Point((int) middleX, (int) middleY), distancePoint);
						if (polygon.contains(potentialPoint)) pointInsidePolygon = true;
					}
					
					if (!pointInsidePolygon) break;
						/** the goal isnt blocked (yet) 
						 * so we can set the desired Position and create a reference to the target region
						 * */
					desX = (float) potentialPoint.x;
					desY = (float) potentialPoint.y;
					setDesiredPosition(desX, desY);
					this.setRegion(s_space.getId());
					return;
//						}
						/** if we come here, no free point was found inside the POLYGON*/
						
//							desX = (float) middleX;
//							desY = (float) middleY;
//						}
				}
				/** if we come here, no free point was found inside the REGION*/
//					tempDistance = getDistance((float)space.getX(),(float) space.getY()) ;
//					if (tempDistance < distance) {
//						distance = tempDistance;
//						s_space.
//						desX = (float) s_space.getX();
//						desY = (float) s_space.getY();
//					}
			}
		}
		/** if we come here, no free point was found inside the REGION
		 * so we have to take our own position and wait until something changes
		 * */
		this.setGoal(false);
		setDesiredPosition(-100, -100);
	}
	
	@Override
	public boolean validateGoal() {
//		Point myPosition = new Point ((int) this.getX(), (int) this.getY());
//		if (this.getRegion() != null) {
//			ArrayList<Point> blockedPoints = this.getRegion().getBlockedPoints();
//			if (blockedPoints.size() < this.getRegion().getResidents().size()*3) {
//				return false;
//			} else
//				return true;
//		} else
//			return false;
		return true;
	}
	
	@Override
	public void updateMovement() {
		// TODO Auto-generated method stub
		super.updateMovement();
		
		/** update distance to desired position */
		float dis = this.getDistance(this.desiredX, this.desiredY);
		float disLimit = this.speed*3;
		if( dis < disLimit) {
			this.setMovement(mx*(dis/disLimit), my*(dis/disLimit));
			if ( dis < 5) {
	//			setMovement(mx*(dis/50), my*(dis/100));
				if(validateGoal() == true) {
					setSatisfaction(1.0f);
				} else {
					this.setGoal(false);
				}
			}
		}
			
	}
	
	public void setSettled(boolean settled) {
		this.settled = settled;
	}
	
	public boolean isSettled() {
		return this.settled;
	}

}
