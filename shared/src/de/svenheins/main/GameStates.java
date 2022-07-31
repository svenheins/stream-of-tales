package de.svenheins.main;

import java.awt.Color;

public class GameStates {
	
	/** public font type */
	public static String standardFont = "Arial";
	public static Color standardFontColor = new Color(250, 250, 250);
	
	/** path settings */
	public static String resourcePath = "/resources/";
	public static String svgPath = "/resources/svg/";
	public static String externalImagesPath = "./ressources/images/";
	public static String standardTilePathPlayers = "tilesets/entities/";
	public static String standardTileNamePlayer = standardTilePathPlayers+"playerTile.png";
	public static String standardTilePathMaps = "tilesets/maps/";
	public static String tileSetFile = standardTilePathMaps + "tileSet2.png";
	public static String tileSetFileUnderground = standardTilePathMaps + "tileSetUnderground_128.png";
	public static String standardMapFolder = "./ressources/maps/";
	
	/** standard filenames */
	public static String standardBackgroundTexture = "grass_01.png";
   
	/** The message encoding. */
    public static final String MESSAGE_CHARSET = "UTF-8";
	
    /** resolution */
    public static int width = 1100;
	public static int height = 700;
	
	/** standard tileSetHeight and width */
	public static int tileSetWidth = 32;
	public static int tileSetHeight = 32;
	public static int ugrTileSetWidth = 6;
	public static int ugrTileSetHeight = 3;
	
	/** values for player entities */
	public static int playerTileWidth = 32;
	public static int playerTileHeight = 64;
	
	/** movement properties */
	public static int DEFAULT_MOVEMENT_ON_X = 100;
	public static int DEFAULT_MOVEMENT_ON_Y = 100;
	
	/** animation stuff */
	public static int animationDelay = 200;
	public static int ani_standard_start = 0;
	public static int ani_standard_end = 3;
	public static int ani_l_start = 28;
	public static int ani_l_end = 31;
	public static int ani_dl_start = 24;
	public static int ani_dl_end = 27;
	public static int ani_ul_start = 32;
	public static int ani_ul_end = 35;
	public static int ani_r_start = 12;
	public static int ani_r_end = 15;
	public static int ani_dr_start = 16;
	public static int ani_dr_end = 19;
	public static int ani_ur_start = 8;
	public static int ani_ur_end = 11;
	public static int ani_u_start = 4;
	public static int ani_u_end = 7;
	public static int ani_d_start = 20;
	public static int ani_d_end = 23;
	
	/** button animation */
	public static int button_inactive_start = 0;
	public static int button_inactive_end = 0;
	public static int button_active_start = 1;
	public static int button_active_end = 1;
	public static int button_mouseOver_start = 2;
	public static int button_mouseOver_end = 2;
	
	/** contextMenu settings */
	public static int contextMenuButtonHeight = 16;
	public static int contextMenuButtonWidth = 128;
	public static int contextMenuFontSize = 10;
	public static int contextMenuButtonDistX = 5;
	public static int contextMenuButtonDistY = 5;
	public static int contextMenuTextDistX = 5;
	public static int contextMenuTextDistY = 0;
	
	/** channel settings */
	public static boolean useChannels = true;
	
	/** zoom properties */
	public static float maxZoomFactor = 5.0f;
	public static float minZoomFactor = 0.0000001f;
	public static int factorOfViewDeleteDistance = 2;
	
	/** standard map properties (small floor and object elements) */
	public static int mapWidth = 32; // EVEN and devide-able through 4!!!
	public static int mapHeight = 20; // EVEN!!!
	public static int mapTileSetWidth = 32;
	public static int mapTileSetHeight = 32;
	public static int mapTotalWidth = mapWidth*mapTileSetWidth;
	public static int mapTotalHeight = mapHeight*mapTileSetHeight;
	
	/** underground maps properties */
	public static int ugrMapWidth = 8;
	public static int ugrMapHeight = 5;
	public static int ugrMapTileSetWidth = 128;
	public static int ugrMapTileSetHeight = 128;
	public static int ugrMapTotalWidth = ugrMapWidth*ugrMapTileSetWidth;
	public static int ugrMapTotalHeight = ugrMapHeight*ugrMapTileSetHeight;
	
	public static int overlayOffsetY = -2*mapTileSetWidth;
	public static int distanceOfSecondTreeLayer = 6;
	
	/** light settings */
	public static String lightTileSetNameDay = "tilesets/light16day.png";
	public static String lightTileSetNameNight = "tilesets/light16night.png";
	public static int maxLightIntenityCutoff = 23;
	public static int lightMapWidth = (int) (2* ((float) width));
	public static int lightMapHeight = (int) (2* ((float) height));
	public static int lightTileWidth = 16;
	public static int lightTileHeight = 16;
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public static void setHeight(int height) {
		GameStates.height = height;
	}
	
	public static void setWidth(int width) {
		GameStates.width = width;
	}
	
	
}
