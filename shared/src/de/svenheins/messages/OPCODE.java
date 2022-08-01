package de.svenheins.messages;

public enum OPCODE {
	/**
     * Client to server opcodes.
     */
//	EDIT_OBJECT, 
	EDIT_SPACE_ADDONS,
	EDIT_PLAYER_STATES,
    MOVEME, 
    UPLOAD_OBJECT, 
    UPLOAD_TEXTURE,
    SEND_MISSING_TEXTURES,
    PLAYERDATA,
    EDIT_PLAYER_ADDONS, 
    INITME,
    LOGOUT,
    
    /**
     * Client to client messages
     */
    SEND_MAP,
    
    //ATTACK, GETFLAG, SCORE,
    /**
     * Server to client opcodes.
     */
    READY_FOR_NEXT_TEXTURE_PACKET,
    READY_FOR_NEXT_TEXTURE, 
    SEND_AVAILABLE_TEXTURES, 
    SENDTEXTURE,
    
    ADDITEM, ADDCOMPLETEITEM, TAKEITEM, TOOKITEM, 
    
    OBJECTSTATE, NEWGAME, STARTGAME, ENDGAME, ADDMOB, REMOVEMOB, MOVEMOB, STOPMOB, ATTACHOBJ, ATTACKED, RESPAWN, OBJECTDELETE,
    /**
     * Common opcodes.
     */
    READY, CHAT, INITENTITIES, INITSPACES, INITTEXTURES, INITPLAYERS  //, SPACE, EYE
, JOINSPACECHANNEL, LEAVESPACECHANNEL

}
