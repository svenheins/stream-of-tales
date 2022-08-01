package de.svenheins.main;

public enum EntityStates {
	EMPTY,
	/** states of an entity */
	STANDING,
	WALKING,
	RUNNING,
	ATTACKING,
	THROWING,
	SHOOTING, 		/** bow */
	HURTING,
	DYING,
	DEAD,
	WORKING,
	CRAFTING,
	CONJURING,		/** performing magic  */
	SWIMMING,
	SLEEPING,
	EATING,
	TALKING,
	RIDING,
	SITTING, 		/** fishing the same */
	SEARCHING, 		/** gold digging*/
	HIDING,
	DANCING,
	PANICING,
	WAVING, 		/** (winken) */
	LAUGHING,
	SHOWING,
	
	
	/** directions */
	LEFT,
	RIGHT,
	UP,
	DOWN
}
