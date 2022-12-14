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
public class SendUpdatePlayers
    implements Serializable,  // for persistence, as required by ManagedObject.
               Task,           // to schedule future calls to our run() method.
               ManagedObject
{
    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The {@link Logger} for this class. */
    private static final Logger logger =
        Logger.getLogger(SendUpdatePlayersTaskSimple.class.getName());

    /**  The timestamp when this task was last run. */
    private long lastTimestamp = System.currentTimeMillis();
    
    private int begin, end;
    int index;
	int packageSize;
    
    /** The  room. */
    private ManagedReference<WorldRoom> room = null;
        
    
    public SendUpdatePlayers(WorldRoom worldRoom, int begin, int end, int packageSize) {
    	DataManager dataManager = AppContext.getDataManager();
    	this.room = dataManager.createReference(worldRoom);
    	this.begin = begin;
    	this.end = end;
    	this.packageSize = packageSize;
    	this.index = this.begin;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Each time this {@code Task} is run, logs the current timestamp and
     * the delta from the timestamp of the previous run.
     */
    public void run() throws Exception {
        // We will be modifying this object.
//        AppContext.getDataManager().markForUpdate(this);
        long timestamp = System.currentTimeMillis();
        long delta = timestamp - lastTimestamp;

        // Update the field holding the most recent timestamp.
        lastTimestamp = timestamp;

//        logger.log(Level.INFO,
//            "UpdateWorldTask timestamp = {0,number,#}, delta = {1,number,#}",
//            new Object[] { timestamp, delta }
//        );
        
        this.update(delta);
    }

	private void update(long delta) {
		// TODO Auto-generated method stub
		if (!(room.get() == null)) {
			int endIndex = this.index + this.packageSize;
			if (endIndex > this.end) endIndex =this.end;
			
//			room.get().updateSendPlayers(index, endIndex);
			
			this.index += this.packageSize;
			if (this.index >= this.end) this.index = this.begin;
        }
	}
    
    
}
