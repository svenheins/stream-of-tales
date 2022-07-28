package de.svenheins.messages;

public enum OPCODE {
	/**
     * Client to server opcodes.
     */
	EDIT_OBJECT, 
	EDIT_SPACE_ADDONS,
    MOVEME, 
    UPLOAD_OBJECT, 
    UPLOAD_TEXTURE,
    SEND_MISSING_TEXTURES,
    PLAYERDATA,
    EDIT_PLAYER_ADDONS, 
    INITME,
    LOGOUT,
    
    //ATTACK, GETFLAG, SCORE,
    /**
     * Server to client opcodes.
     */
    READY_FOR_NEXT_TEXTURE_PACKET,
    READY_FOR_NEXT_TEXTURE, 
    SEND_AVAILABLE_TEXTURES, 
    SENDTEXTURE,
    
    OBJECTSTATE, NEWGAME, STARTGAME, ENDGAME, ADDMOB, REMOVEMOB, MOVEMOB, STOPMOB, ATTACHOBJ, ATTACKED, RESPAWN, OBJECTDELETE,
    /**
     * Common opcodes.
     */
    READY, CHAT, INITENTITIES, INITSPACES, INITTEXTURES, INITPLAYERS  //, SPACE, EYE

}
