package de.svenheins;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

public class UpdateWorldTaskSimple  implements Task, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int phase = 0;
	int entityCount = 0;
	private ManagedReference<WorldRoom> room = null;

	private long lastTimestamp = System.currentTimeMillis();
	
	private static final Logger logger = Logger.getLogger(WorldRoom.class.getName());
	
	public UpdateWorldTaskSimple(WorldRoom worldRoom) {
		entityCount = 0;
		DataManager dataManager = AppContext.getDataManager();
		this.room = dataManager.createReference(worldRoom);
		lastTimestamp = System.currentTimeMillis();
	}
			
	@Override
	public void run() throws Exception {
		long timestamp = System.currentTimeMillis();
		long delta = timestamp -lastTimestamp;
		
		lastTimestamp = timestamp;
		
		switch (phase) {
		case 0:
//			this.room.get().update(delta);
			phase++;
			if (!AppContext.getTaskManager().shouldContinue()) {
				AppContext.getTaskManager().scheduleTask(this);
				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
				return;
			}
		case 1:
			this.room.get().update(delta);
			phase++;
			if (!AppContext.getTaskManager().shouldContinue()) {
				AppContext.getTaskManager().scheduleTask(this);
				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
				return;
			}
//			break;
		case 2:
//			this.room.get().update(delta);
			phase = 0;
			if (!AppContext.getTaskManager().shouldContinue()) {
				AppContext.getTaskManager().scheduleTask(this);
				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
				return;
			}
			break;
		}
	

	}

}
