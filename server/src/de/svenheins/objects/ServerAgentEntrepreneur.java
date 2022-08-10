package de.svenheins.objects;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.ArrayList;
import com.sun.sgs.app.ManagedReference;
import de.svenheins.functions.MyMath;

public class ServerAgentEntrepreneur extends ServerAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** settled */
	private boolean settled;


	public ServerAgentEntrepreneur(ServerSprite sprite, BigInteger id, String tileSetName, String tileSetPathName, float x,
			float y, float mx, float my) {
		super(sprite, id, tileSetName, tileSetPathName, x, y, mx, my);
		this.settled = false;
	}

	
	@Override
	public void updateLocation() {
		/** update Location only if not settled yet */
		if (!isSettled()) {
			if (satisfaction > 0.6) { // (this.getHorizontalMovement() != 0 || this.getVerticalMovement() !=0)) {
				/** be lucky and stay here */
//				setDesiredPosition((float)this.getX(), (float)this.getY());
//				setMovement(0, 0);
//				if( this.getRegion().addBlockedPoint(new Point((int) this.getX(), (int) this.getY()))) {
//					/** be happy */
//					this.setSatisfaction(1.0f);
//					this.setSettled(true);
//				} else
//				{
//					this.setSatisfaction(0.0f);
//					this.setGoal(false);
//				}
			} else
			{
				if(!hasGoal()){
					/** find a better place */
//					searchBetterLocation();
					/** reset Movement speed to max speed*/
//					this.setHorizontalMovement(this.getSpeed()/2);
//					this.setVerticalMovement(this.getSpeed()/2);
				} else {
	//				if (this instanceof ServerAgentEntrepreneur) {
	//					if(((ServerAgentEntrepreneur)this).validateGoal() == true) {
	//						updateMovement();
	//					} else {
	//						this.setGoal(false);
	//					}
	//				} else {
//						/** the agent has a goal, so he can try to go there */
//						/** first the agent has to check if the goal is achievable */
//						if(validateGoal() == true) {
////							if(this.getHorizontalMovement() < 1 && this.getVerticalMovement() <1 ) {
////								this.setMovement(this.speed/2, this.speed/2);
////							}
//							updateMovement();
//						} else {
//							/** the goal is not achievable, so the agent has to search another goal */
//							this.setGoal(false);
//						}
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
		for(ManagedReference<ServerSpace> serverSpace : this.getRoom().getSpaces().get().values()) {
			ServerSpace s_space = serverSpace.get();
			if(s_space instanceof ServerRegion) {
				/** Create a Space, to compute some stuff (faster and fewer errors) */
				Space space = new Space(s_space.getName(), s_space.getId(), new int[]{0,0,0}, true, 0.5f, s_space.getScale());
				space.setAllXY(s_space.getX(), s_space.getY());
				ArrayList<Point> blockedPoints = ((ServerRegion) s_space).getBlockedPoints();
				/** if capacity of region is reached, continue with next region */
				if (blockedPoints.size() >= ((ServerRegion) s_space).getCapacity()) {
					continue;
				}
//				logger.log(Level.INFO, "Space found: "+serverSpace.get().getName());
				/** iterate all the space-polygons */
				for (int i = 0; i< space.getPolygon().size(); i++) {
					Polygon polygon = space.getPolygon().get(i);
					Point point = new Point((int) localX, (int) localY);
//					/** if the enterprise is in a polygon */
//					if (polygon.contains(point)) {
//						this.setSatisfaction(0.9f);
//						desX = localX;
//						desY = localY;
//						return;
//					} else {
//						/** calculate distance from actual polygon */
//						tempDistance = (float) MyMath.getDistance(polygon, point) ;
					Rectangle rect = polygon.getBounds();
					int rectSize = Math.max(rect.height, rect.width);
					float middleX = (float) rect.getCenterX();
					float middleY = (float) rect.getCenterY();
					tempDistance = (float) MyMath.getDistance(point, new Point((int) middleX, (int) middleY));
//					/** and save it if its smaller than minimum */
//					if (tempDistance < distance) {
						distance = tempDistance;
//						Point desPoint = MyMath.getNearest(polygon, point);
						Point potentialPoint = new Point((int) middleX, (int) middleY);
						/** we found a suitable potential polygon, so we search a free spot */
						for( int distancePoint = rectSize/10; distancePoint < rectSize;distancePoint += rectSize/10) {
							boolean pointInsidePolygon = false;
							int intShots = 0;
							while(!pointInsidePolygon && intShots <10 && distancePoint >0) {
								intShots ++;
//								potentialPoint= MyMath.randomShotQuadrat(new Point((int) middleX, (int) middleY), (int) (distancePoint*Math.random()));
								potentialPoint= MyMath.randomShotQuadrat(new Point((int) middleX, (int) middleY), (int) ((rectSize/2)*Math.random()));
								if (polygon.contains(potentialPoint)) pointInsidePolygon = true;
							}
							boolean blocked = false;
							float mySize = Math.max(this.height, this.width);
							for (Point blockedPoint : blockedPoints) {
								/** check if any blocked Position gets in the way */
								if (MyMath.getDistance(blockedPoint, potentialPoint)< mySize) blocked = true;
							}
							if (blocked) {
								/** if the agent is blocked 
								 * we try with a next randomShot
								 * */
//								logger.log(Level.INFO, "Something is in my way!");
								continue;
							} else
							{
								/** the goal isnt blocked (yet) 
								 * so we can set the desired Position and create a reference to the target region
								 * */
								desX = (float) potentialPoint.x;
								desY = (float) potentialPoint.y;
								setDesiredPosition(desX, desY);
								this.setRegion( s_space.getId());
								if( ((ServerRegion) this.getRoom().getSpaces().get().get(this.getRegion()).getForUpdate()).addBlockedPoint(new Point((int) this.getX(), (int) this.getY()))) {
									/** be happy */
									this.setSatisfaction(1.0f);
									this.setSettled(true);
									/** directly travel to desiredPosition */
									this.setX(this.desiredX);
									this.setY(this.desiredY);
									this.setMovement(0, 0);
								} else
								{
									this.setSatisfaction(0.0f);
									this.setGoal(false);
								}
								return;
							}
						}
						/** if we come here, no free point was found inside the POLYGON*/
						
//						desX = (float) middleX;
//						desY = (float) middleY;
//					}
				}
				/** if we come here, no free point was found inside the REGION*/
//				tempDistance = getDistance((float)space.getX(),(float) space.getY()) ;
//				if (tempDistance < distance) {
//					distance = tempDistance;
//					s_space.
//					desX = (float) s_space.getX();
//					desY = (float) s_space.getY();
//				}
			}
		}
		/** if we come here, no free point was found inside the REGION
		 * so we have to take our own position and wait until something changes
		 * */
		setDesiredPosition(desX, desY);
	}
	
	@Override
	public boolean validateGoal() {
		Point myPosition = new Point ((int) this.getX(), (int) this.getY());
		float mySize = Math.max(this.height, this.width);
		
		boolean blocked = false;
		ArrayList<Point> blockedPoints = ((ServerRegion)  this.getRoom().getSpaces().get().get(this.getRegion()).getForUpdate()).getBlockedPoints();
		for(Point blockedPoint : blockedPoints) {
			/** check if any blocked Position gets in the way */			
			if (MyMath.getDistance(blockedPoint, myPosition)< mySize) {
				blocked = true;
//				logger.log(Level.INFO, "blocked = true; size: "+ blockedPoints.size()+" region: "+this.getRegion().getName());
				break;
			}
		}
		/** if blocked == true, then return FALSE (Goal couldn't be validated) */
		return (!blocked);
	}
	
	@Override
	public void updateMovement() {
		// TODO Auto-generated method stub
//		super.updateMovement();
		
//		/** update distance to desired position */
//		float dis = this.getDistance(this.desiredX, this.desiredY);
//		float disLimit = this.speed*3;
//		if( dis < disLimit) {
//			this.setMovement(mx*(dis/disLimit), my*(dis/disLimit));
//			if ( dis < 5) {
//	//			setMovement(mx*(dis/50), my*(dis/100));
//				if(validateGoal() == true) {
//					setSatisfaction(1.0f);
//				} else {
//					this.setGoal(false);
//				}
//			}
//		}
		if( ((ServerRegion) this.getRoom().getSpaces().get().get(this.getRegion()).getForUpdate()).addBlockedPoint(new Point((int) this.getX(), (int) this.getY()))) {
			/** be happy */
			this.setSatisfaction(1.0f);
			this.setSettled(true);
			/** directly travel to desiredPosition */
			this.setX(this.desiredX);
			this.setY(this.desiredY);
			this.setMovement(0, 0);
		} else
		{
			this.setSatisfaction(0.0f);
			this.setGoal(false);
		}
		
		
	}
	
	public void setSettled(boolean settled) {
		this.settled = settled;
	}
	
	public boolean isSettled() {
		return this.settled;
	}
}
