package de.svenheins.main;

public class GameStates {
	public static String resourcePath = "/resources/";
	public static String svgPath = "/resources/svg/";
	public static String externalImagesPath = "./ressources/images/";
	public static String standardBackgroundTexture = "grass_01.png";
    /** The message encoding. */
    public static final String MESSAGE_CHARSET = "UTF-8";
	public static int width = 800;
	public static int height = 600;
	public static int tileSetWidth = 32;
	public static int tileSetHeight = 32;
	public static int tileWidth = 32;
	public static int tileHeight = 64;
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
	public static String standardTilePathPlayers = "tilesets/entities/";
	public static String standardTileNamePlayer = standardTilePathPlayers+"player.png";
	public static String standardTilePathMaps = "tilesets/maps/";
	public static String tileSetFile = standardTilePathMaps + "tileSet2.png";
	public static boolean useChannels = true;
	public static int mapWidth = 32; // EVEN and devide-able through 4!!!
	public static int mapHeight = 20; // EVEN!!!
	public static int mapTileSetWidth = 32;
	public static int mapTileSetHeight = 32;
	public static int mapTotalWidth = mapWidth*mapTileSetWidth;
	public static int mapTotalHeight = mapHeight*mapTileSetHeight;
	public static String standardMapFolder = "./ressources/maps/";
	public static int overlayOffsetY = -2*mapTileSetWidth;
	public static int factorOfViewDeleteDistance = 3;
	public static int distanceOfSecondTreeLayer = 6;
	
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
