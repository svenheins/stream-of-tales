package de.svenheins;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

public class UpdateWorldTaskSimple  implements Task, Serializable, ManagedObject{
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
	
	public UpdateWorldTaskSimple(WorldRoom worldRoom, int begin, int end, int packageSize) {
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
//		long timestamp = System.currentTimeMillis();
//		long delta = timestamp -lastTimestamp;
//		
//		lastTimestamp = timestamp;
		
		int endIndex = this.index + this.packageSize;
		if (endIndex > this.end) endIndex =this.end;
		
		this.room.get().updateEntities(index, endIndex);
		
		this.index += this.packageSize;
		if (this.index >= this.end) this.index = this.begin;
//		this.room.get().updateEntities(delta, begin, end);
//			phase ++;
//			if (!AppContext.getTaskManager().shouldContinue()) {
//				AppContext.getTaskManager().scheduleTask(this);
//				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
//				return;
//			}
//		case 1:
//			this.room.get().update(delta, (maxIndexEntities/5), (phase+1)*(maxIndexEntities/5));
//			phase++;
//			if (!AppContext.getTaskManager().shouldContinue()) {
//				AppContext.getTaskManager().scheduleTask(this);
//				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
//				return;
//			}
//		case 2:
//			this.room.get().update(delta, (phase)*(maxIndexEntities/5), (phase+1)*(maxIndexEntities/5));
//			phase++;
//			if (!AppContext.getTaskManager().shouldContinue()) {
//				AppContext.getTaskManager().scheduleTask(this);
//				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
//				return;
//			}
//		case 3:
//			this.room.get().update(delta, (phase)*(maxIndexEntities/5) , (phase+1)*(maxIndexEntities/5));
//			phase++;
//			if (!AppContext.getTaskManager().shouldContinue()) {
//				AppContext.getTaskManager().scheduleTask(this);
//				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
//				return;
//			}
//		case 4:
//			this.room.get().update(delta, (phase)*(maxIndexEntities/5), (phase+1)*(maxIndexEntities/5));
//			phase=0;
//			if (!AppContext.getTaskManager().shouldContinue()) {
//				AppContext.getTaskManager().scheduleTask(this);
//				logger.log(Level.INFO, "Phase: {0}", new Object[]{phase});
//				return;
//			}
//		}

	}

}
