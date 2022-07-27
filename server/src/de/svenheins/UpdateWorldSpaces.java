package de.svenheins;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

public class UpdateWorldSpaces  implements Task, Serializable, ManagedObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int begin, end, index, packageSize;
	private ManagedReference<WorldRoom> room = null;

	private long lastTimestamp = System.currentTimeMillis();
	
	private static final Logger logger = Logger.getLogger(WorldRoom.class.getName());
	
	public UpdateWorldSpaces(WorldRoom worldRoom, int begin, int end, int packageSize) {
		this.begin = begin;
		this.end = end;
		DataManager dataManager = AppContext.getDataManager();
		this.room = dataManager.createReference(worldRoom);
		lastTimestamp = System.currentTimeMillis();
		this.packageSize = packageSize;
		this.index = begin;
	}
			
	@Override
	public void run() throws Exception {
//		long timestamp = System.currentTimeMillis();
//		long delta = timestamp -lastTimestamp;
		
//		lastTimestamp = timestamp;
		int endIndex = this.index + this.packageSize;
		if (endIndex > this.end) endIndex =this.end;
		
		this.room.get().updateSpaces(index, endIndex);
		
		this.index += this.packageSize;
		if (this.index >= this.end) this.index = this.begin;
//		this.room.get().updateSpaces(begin, end);


	}

}
