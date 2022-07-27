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
	int begin, end;
	private ManagedReference<WorldRoom> room = null;

	private long lastTimestamp = System.currentTimeMillis();
	
	private static final Logger logger = Logger.getLogger(WorldRoom.class.getName());
	
	public UpdateWorldSpaces(WorldRoom worldRoom, int begin, int end) {
		this.begin = begin;
		this.end = end;
		DataManager dataManager = AppContext.getDataManager();
		this.room = dataManager.createReference(worldRoom);
		lastTimestamp = System.currentTimeMillis();
	}
			
	@Override
	public void run() throws Exception {
//		long timestamp = System.currentTimeMillis();
//		long delta = timestamp -lastTimestamp;
		
//		lastTimestamp = timestamp;
		this.room.get().updateSpaces(begin, end);


	}

}
