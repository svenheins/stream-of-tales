/*
 * Copyright 2007-2010 Sun Microsystems, Inc.
 *
 * This file is part of Project Darkstar Server.
 *
 * Project Darkstar Server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Project Darkstar Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * --
 */

package de.svenheins;

import com.sun.sgs.app.AppContext;
import java.io.Serializable;
import java.util.logging.Logger;

import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

/**
 * A simple repeating Task that tracks and prints the time since it was
 * last run.
 */
public class UpdateWorldTask
    implements Serializable,  // for persistence, as required by ManagedObject.
               ManagedObject, // to let the SGS manage our persistence.
               Task           // to schedule future calls to our run() method.
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The {@link Logger} for this class. */
    private static final Logger logger =
        Logger.getLogger(UpdateWorldTask.class.getName());

    /**  The timestamp when this task was last run. */
    private long lastTimestamp = System.currentTimeMillis();
    
    /** The  room. */
    private ManagedReference<WorldRoom> room = null;
        
    
    public UpdateWorldTask(WorldRoom worldRoom) {
    	DataManager dataManager = AppContext.getDataManager();
    	this.room = dataManager.createReference(worldRoom);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Each time this {@code Task} is run, logs the current timestamp and
     * the delta from the timestamp of the previous run.
     */
    public void run() throws Exception {
        // We will be modifying this object.
        AppContext.getDataManager().markForUpdate(this);
        long timestamp = System.currentTimeMillis();
        long delta = timestamp - lastTimestamp;

        // Update the field holding the most recent timestamp.
        lastTimestamp = timestamp;

//        logger.log(Level.INFO,
//            "UpdateWorldTask timestamp = {0,number,#}, delta = {1,number,#}",
//            new Object[] { timestamp, delta }
//        );
        
        this.update(delta);
//    	if (!AppContext.getTaskManager().shouldContinue()) {
//            AppContext.getTaskManager().scheduleTask(this);
//            return;
//        }
    }

	private void update(long delta) {
		// TODO Auto-generated method stub
		if (!(room.get() == null)) {
			room.get().update(delta);
		
        }
	}
    
    
}
