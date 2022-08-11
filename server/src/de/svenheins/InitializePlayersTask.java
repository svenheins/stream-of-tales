package de.svenheins;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

public class InitializePlayersTask  implements Task, Serializable, ManagedObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int begin, end;
	int index;
	int packageSize;
	private ManagedReference<WorldRoom> room = null;

	private long lastTimestamp = System.currentTimeMillis();
	
	private static final Logger logger = Logger.getLogger(WorldRoom.class.getName());
	
	public InitializePlayersTask(WorldRoom worldRoom, int begin, int end, int packageSize) {
		this.begin = begin;
		this.end = end;
		this.index = begin;
		this.packageSize = packageSize;
		DataManager dataManager = AppContext.getDataManager();
		this.room = dataManager.createReference(worldRoom);
		lastTimestamp = System.currentTimeMillis();
	}
			
	@Override
	public void run() throws Exception {

//		
//		int endIndex = this.index + this.packageSize;
//		if (endIndex > this.end) endIndex =this.end;
		
		this.room.get().initializePlayersEntities();
		this.room.get().initializePlayersItems();
		this.room.get().initializePlayersAreaInfluences();
		
//		this.index += this.packageSize;
//		if (this.index >= this.end) this.index = this.begin;


	}

}
