package de.svenheins.objects;



import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

import de.svenheins.WorldRoom;
import de.svenheins.main.EntityStates;
import de.svenheins.main.GameStates;



public class ServerEntity extends WorldObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ManagedReference<ServerSprite> sprite;
	protected String[] standardAnimation;
	protected String[] animation;
	protected boolean b_stdAni;
	protected long lastTimestamp;
	protected String tileSetPathName;
	protected String tileSetName;
	/** variables for the entity appearance */
	protected EntityStates orientation;
	protected EntityStates state;
	
    

	/** The {@link WorldRoom} this player is in, or null if none. */
    protected ManagedReference<WorldRoom> currentRoomRef = null;
    /** The {@link Logger} for this class. */
    protected static final Logger logger =
        Logger.getLogger(ServerEntity.class.getName());
	
	public final static int DEFAULT_MOVEMENT_ON_X = 300;
	public final static int DEFAULT_MOVEMENT_ON_Y = 300;
	
	public ServerEntity(ServerSprite sprite, BigInteger id, String tileSetName, String tileSetPathName, float x, float y, float mx, float my) {
		standardAnimation = new String[1];
		standardAnimation[0] = sprite.getStrImg();
		animation = standardAnimation;
		this.sprite = AppContext.getDataManager().createReference(sprite);
		this.id = id;
		this.setName(sprite.getStrImg());
		this.x = x;
		this.y = y;
		this.my = mx;
		this.mx = my;
		this.speed = mx+my;
		this.height = sprite.getHeight();
		this.width = sprite.getWidth();
		this.lastTimestamp = System.currentTimeMillis();
		this.setTileSetName(tileSetName);
		this.setTileSetPathName(tileSetPathName);
		this.setOrientation(EntityStates.DOWN);
		this.setState(EntityStates.STANDING);
	}
	
//	public void moveOnX(long duration){
//		float movement = duration * mx/1000;
//		setX(getX()+movement);
//	}
//	
//	public void moveOnY(long duration){
//		float movement = duration * my/1000;
//		setY(getY()+movement);
//	}
	
	public void move(long timestamp) {
		long duration = timestamp - this.lastTimestamp;
		float movementX = duration * mx/1000;
		float movementY = duration * my/1000;
		// Always update
		if(GameStates.getWidth()>0 && GameStates.getHeight()>0) {
			setX(this.getX()+movementX);
			setY(this.getY()+movementY);
		}
		this.lastTimestamp = timestamp;
	}
	
	public ServerSprite getSprite() {
		return this.sprite.get();
	}
	
	public void setSprite(ServerSprite sprite){
		this.sprite = AppContext.getDataManager().createReference(sprite);
		this.setHeight(sprite.getHeight());
		this.setWidth(sprite.getWidth());
	}
	
	@Override
	public void setY(float d) {
		this.y = d;
		this.setZIndex(d+this.getHeight());
	}

	public void changeDirection() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean collides(ServerEntity otherEntity) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.get().getWidth(), sprite.get().getHeight());
		Rectangle r2 = new Rectangle((int) otherEntity.x, (int) otherEntity.y, otherEntity.getSprite().getWidth(), otherEntity.getSprite().getHeight());
		return r1.intersects(r2);
	}
	
	public boolean contains(Point p) {
		//Sprite sprite = animation.getSprite(System.currentTimeMillis(), this.standardAnimation);
		Rectangle r1 = new Rectangle((int) x, (int) y, sprite.get().getWidth(), sprite.get().getHeight());
		return r1.contains(p);
	}
	

	public float getHeight() {
		return this.getSprite().getHeight();
	}
	
	

	public float getWidth() {
		return this.getSprite().getWidth();
	}
	
	 /**
     * Returns the room this player is currently in, or {@code null} if
     * this player is not in a room.
     * <p>
     * @return the room this player is currently in, or {@code null}
     */
    public WorldRoom getRoom() {
        if (currentRoomRef == null) {
            return null;
        }

        return currentRoomRef.get();
    }

    /**
     * Sets the room this player is currently in.  If the room given
     * is null, marks the player as not in any room.
     * <p>
     * @param room the room this player should be in, or {@code null}
     */
    public void setRoom(WorldRoom room) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        if (room == null) {
            currentRoomRef = null;
            return;
        }

        currentRoomRef = dataManager.createReference(room);
    }
    
    public void setTileSetPathName(String pathName) {
    	this.tileSetPathName = pathName;
    }
    
    public String getTileSetPathName() {
    	return tileSetPathName;
    }
    
    public void setTileSetName(String name) {
    	this.tileSetName = name;
    }
    
    public String getTileSetName() {
    	return tileSetName;
    }
    
    public EntityStates getOrientation() {
		return orientation;
	}

	public void setOrientation(EntityStates orientation) {
		this.orientation = orientation;
	}

	public EntityStates getState() {
		return state;
	}

	public void setState(EntityStates state) {
		this.state = state;
	}
	

}
