package de.svenheins.messages;

public enum OPCODE {
	/**
     * Client to server opcodes.
     */
	EDIT_OBJECT, 
    MOVEME, //ATTACK, GETFLAG, SCORE,
    /**
     * Server to client opcodes.
     */
    OBJECTSTATE, NEWGAME, STARTGAME, ENDGAME, ADDMOB, REMOVEMOB, MOVEMOB, STOPMOB, ATTACHOBJ, ATTACKED, RESPAWN, OBJECTDELETE,
    /**
     * Common opcodes.
     */
    READY, CHAT, INITENTITIES, INITSPACES  //, SPACE, EYE
}
