package de.svenheins;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

public class UpdateWorldAreaInfluence implements Task, Serializable, ManagedObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int begin, end;
	int index;
	int packageSize;
	private ManagedReference<WorldRoom> room = null;
	private Iterator<BigInteger> itKeys;

	private long lastTimestamp = System.currentTimeMillis();
	
	private static final Logger logger = Logger.getLogger(WorldRoom.class.getName());
	
	
	public UpdateWorldAreaInfluence(WorldRoom worldRoom, int begin, int end, int packageSize) {
		this.begin = begin;
		this.end = end;
		this.index = begin;
		this.packageSize = packageSize;
		DataManager dataManager = AppContext.getDataManager();
		this.room = dataManager.createReference(worldRoom);
		lastTimestamp = System.currentTimeMillis();
		itKeys = room.get().getAreaInfluenceList().get().keySet().iterator();
	}
	
	@Override
	public void run() throws Exception {
		/** update length */
		this.end = this.room.get().getAreaInfluenceList().get().size();
		
		int endIndex = this.index + this.packageSize;
		if (endIndex > this.end) endIndex =this.end;
		
		this.room.get().updateAreaInfluences(this.index, endIndex, itKeys);
		
		this.index += this.packageSize;
		if (this.index >= this.end) {
			this.index = this.begin;
			itKeys = room.get().getItemList().get().keySet().iterator();
		}
	}

}
