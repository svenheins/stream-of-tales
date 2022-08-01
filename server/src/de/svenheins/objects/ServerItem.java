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
import de.svenheins.messages.ITEMCODE;



public class ServerItem extends WorldObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int count;
	private int capacity;
	private String name;
	private BigInteger id;
	private ITEMCODE itemCode;
	private ManagedReference<ServerEntity> itemEntity;
	private long creationTime;
	
    

	/** The {@link WorldRoom} this player is in, or null if none. */
    protected ManagedReference<WorldRoom> currentRoomRef = null;
    /** The {@link Logger} for this class. */
    protected static final Logger logger =
        Logger.getLogger(ServerEntity.class.getName());
	
	
    public ServerItem (BigInteger id, ITEMCODE itemCode, String name, ServerEntity itemEntity, int count, int capacity) {
    	setId(id);
    	setItemCode(itemCode);
    	setName(name);
    	setItemEntity(AppContext.getDataManager().createReference(itemEntity));
    	setCount(count);
    	setCapacity(capacity);
    }


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public BigInteger getId() {
		return id;
	}


	public void setId(BigInteger id) {
		this.id = id;
	}


	public ITEMCODE getItemCode() {
		return itemCode;
	}


	public void setItemCode(ITEMCODE itemCode) {
		this.itemCode = itemCode;
	}


	public ManagedReference<ServerEntity> getItemEntity() {
		return itemEntity;
	}


	public void setItemEntity(ManagedReference<ServerEntity> itemEntity) {
		this.itemEntity = itemEntity;
	}


	public long getCreationTime() {
		return creationTime;
	}


	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
}
