package de.svenheins;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

import de.svenheins.managers.SpaceManager;

public class UpdateWorldSpaces  implements Task, Serializable, ManagedObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int begin, end, index, packageSize, counter, countTasks;
	private ManagedReference<WorldRoom> room = null;

	private long lastTimestamp = System.currentTimeMillis();
	
	private static final Logger logger = Logger.getLogger(WorldRoom.class.getName());
	
	public UpdateWorldSpaces(WorldRoom worldRoom, int counter, int packageSize, int countTasks) {
		this.begin = 0;
		this.end = 0;
		DataManager dataManager = AppContext.getDataManager();
		this.room = dataManager.createReference(worldRoom);
		lastTimestamp = System.currentTimeMillis();
		this.packageSize = packageSize;
		this.index = 0;
		this.countTasks= countTasks;
		this.counter = counter;
	}
			
	@Override
	public void run() throws Exception {

		int sizeSpaceManager = SpaceManager.size();
		int sizeThisTask = sizeSpaceManager/this.countTasks;
		this.begin = counter*(sizeThisTask);
		this.end = this.begin + sizeThisTask;
		
		/** if we are in the last Task, add the missing Spaces to this one 
		 * counter goes from (0)-(N-1) and countTasks is the number of tasks
		 * */
		if (this.counter == this.countTasks-1) this.end += sizeSpaceManager % this.countTasks;
	
		int startIndex = this.index*packageSize + this.begin;
		int endIndex = startIndex + this.packageSize;
		
		if (endIndex > this.end) endIndex = this.end;
		
		
//		this.room.get().updateSpaces(0, 0);
		this.room.get().updateSpaces();
		
		this.index += 1;
		if (this.index >= (sizeThisTask/ packageSize)+1) this.index = 0;



	}

}
