/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import java.awt.Color;

import de.svenheins.messages.ITEMCODE;

public class GameStates {
	
	/** public font type */
	public static String standardFont = "Cut-cut";
	public static Color standardFontColor = new Color(250, 250, 250);
	public static int[] standardContainerColor = new int[]{200, 200, 200};
	public static float standardContainerTranparency = 0.3f;
	public static int standardFontSize = 12;
	public static int standardEffectTextSize = 36;
	public static long standardEffectTextFade = 750;
	public static long standardEffectTextStay = 3000;
	
	/** capturing mouseCursor */
	public static boolean extraMouseCursorForCapturing = false;
	
	/** path settings */
	public static String resourcePath = "/resources/";
	public static String resourcePathPresets = "/res/Presets/";
	public static String svgPath = "/resources/svg/";
	public static String externalImagesPath = "./ressources/images/";
	public static String standardTilePathPlayers = "tilesets/entities/";
	public static String standardTilePathItems = "tilesets/items/";
	public static String standardSoundEffectPath = "/resources/soundeffects/";
	public static String standardMusicPath = "/resources/music/";
	public static String tileNameStandardPlayer = standardTilePathPlayers+"standardPlayer_20Width52Height_final_150108.png";
	public static String nameStandardPlayer = "standardPlayer";
	public static String roomStandardPlayer = "standardRoom";
	public static String nameDwarf = "dwarf";
	public static String nameWolf = "wolf";
	public static String nameGreyWolf = "greyWolf";
	public static String nameDarkWolf = "darkWolf";
	public static String nameWolfBig = "wolfBig";
	public static String nameGreyWolfBig = "greyWolfBig";
	public static String nameDarkWolfBig = "darkWolfBig";
	public static String nameRabbit = "rabbit";
	public static String tileNameDwarf = standardTilePathPlayers+"dwarf.png";
	public static String tileNameWolf = standardTilePathPlayers+"wolf.png";
	public static String tileNameGreyWolf = standardTilePathPlayers+"greyWolf.png";
	public static String tileNameDarkWolf = standardTilePathPlayers+"darkWolf.png";
	public static String tileNameWolfBig = standardTilePathPlayers+"wolfBig.png";
	public static String tileNameGreyWolfBig = standardTilePathPlayers+"greyWolfBig.png";
	public static String tileNameDarkWolfBig = standardTilePathPlayers+"darkWolfBig.png";
	public static String tileNameRabbit = standardTilePathPlayers + "rabbit.png";
	public static String standardTileNameAgent = standardTilePathPlayers+"standardPlayer_20Width52Height_final_red.png";
	public static String standardTileNameAgentBlue = standardTilePathPlayers+"standardPlayer_20Width52Height_final_red.png";
	public static String standardTilePathMaps = "tilesets/maps/";
	public static String tileSetFile = standardTilePathMaps + "meinTileSet25.png";//"meinTileSet18_Satt35Kontrast5.png";//"meinTileSet17.png";
	public static String tileSetFileUnderground = standardTilePathMaps + "tileSetUnderground_128_5_Satt35Kontrast5_2.png";//"tileSetUnderground_128_4.png";
	public static String standardMapFolder = "./ressources/maps/";
	
	/** standard filenames */
	public static String standardBackgroundTexture = "standardBackgroundTexture.png";
   
	/** Main Menu */
	public static int introSpeedMainMenu = 40;
	public static int overlayCloudSpeed = 20;
	public static String cloudName = "clouds.png";
	public static String tileNameCloud = standardTilePathPlayers+"clouds2.png";
	public static String cloudNameOverlay = "cloudsOverlay.png";
	public static String tileNameCloudOverlay = standardTilePathPlayers+"cloudsOverlay.png";
	public static int cloudWidth = 128;
	public static int cloudHeight = 96;
	public static String skyName = "starsky.png";
	public static String tileNameSky = standardTilePathPlayers+"starsky.png";
	public static int skyWidth = 1920;
	public static int skyHeight = 1200;
	public static String starBlueName = "star_blue_2.png";
	public static String starBlueTileName = standardTilePathPlayers+"star_blue_2.png";
	public static int starBlueWidth = 64;
	public static int starBlueHeight = 64;
	public static String starBlue3Name = "star_blue_3.png";
	public static String starBlue3TileName = standardTilePathPlayers+"star_blue_3.png";
	public static String starBlue4Name = "star_blue_4.png";
	public static String starBlue4TileName = standardTilePathPlayers+"star_blue_4.png";
	public static String starBlue5Name = "star_blue_5.png";
	public static String starBlue5TileName = standardTilePathPlayers+"star_blue_5.png";
	
	public static String starBlueBigName = "star_blue_big.png";
	public static String starBlueBigTileName = standardTilePathPlayers+"star_blue_big.png";
	public static int starBlueBigWidth = 128;
	public static int starBlueBigHeight = 128;
	public static String starBlueBig2Name = "star_blue_big2.png";
	public static String starBlueBig2TileName = standardTilePathPlayers+"star_blue_big2.png";
	public static String starBlueBig3Name = "star_blue_big3.png";
	public static String starBlueBig3TileName = standardTilePathPlayers+"star_blue_big3.png";
	public static String starBlueBig4Name = "star_blue_big4.png";
	public static String starBlueBig4TileName = standardTilePathPlayers+"star_blue_big4.png";
	
	/** The message encoding. */
    public static final String MESSAGE_CHARSET = "UTF-8";
	
    /** resolution */
    public static int width = 1366;//1024;//1366; // 1100
	public static int height = 768;//600;//768; // 700
	public static float standardZoomFactor = 1.0f;
	public static float scaleGenerationCountObjects = 1.0f;
	public static float scaleTileSize = 1.0f;
	
	/** music and sound stuff */
	public static long fadeOutDuration = 4000;
	public static long fadeInDuration = 4000;
	public static long fadeOutDurationMovingsound = 200;
	
	/** GUI stuff */
	public static int lifeManaFontSize = 18;
	
	/** console stuff */
	public static int consoleMaxCharsPerLine = 80;
	
	/** standard tileSetHeight and width */
	public static int tileSetWidth = 32;
	public static int tileSetHeight = 32;
	public static int ugrTileSetWidth = 6;
	public static int ugrTileSetHeight = 3;
	
	
	
	/** values for item entities */
	public static int itemTileWidth = 16;
	public static int itemTileHeight = 16;
	
	/** movement properties */
//	public static int DEFAULT_MOVEMENT_ON_X = 100;
//	public static int DEFAULT_MOVEMENT_ON_Y = 100;
	public static float entityMaxSpeed = 0.0f;
	public static int buildDistance = 100;
	public static float timeTillStarving = 60.0f; // in seconds
	public static long timespanOfOneDayInMilliseconds = 120000;
	public static float maxDay = 0.8f;//-1;//0.80f;
	public static float minNight = 0;//0.05f;
	public static float timeStep = (maxDay-minNight)/19;
	public static float transparentLoad = 1.0f; // time in seconds to load
	
	/** Twitch stream properties */
	public static long streamTimeInterval = 6000;
	public static int streamGoldPerInterval = 1;
	public static int streamExperiencePerInterval = 1;
	/** costs for stream viewers */
	public static int streamCostCreate = 10;
	public static int streamCostDropItem = 10;
	public static int streamCostStealItem = 20;
	public static int streamCostHideItem = 3;
	public static int streamCostRespawn = 2;
	public static int streamCostChangeGroup = 5;
	public static int streamCostChangeColor = 3;
	public static int streamCostFind = 5;
	public static int streamCostSendGold = 10;
	
	public static int streamExperienceForKillingPlayer = 50;
	public static int streamExperienceForKillingAgent = 1;
	public static int streamExperienceForKillingAgentPerLevel = 1;
	
	public static int streamHostileAgentSpawnDistance = 500;
	public static int streamFriendlyAgentSpawnDistance = 400;
	
	/** item/ inventory stuff */ 
	/**take distance */
	public static int takeDistance = 32;
	public static int dropDistance = 20;
	public static int dropDistanceStream = 100;
	public static int dropDistanceHideItems = 500;
	public static int interactDistance = 48;
	public static long attackDelay = 1000;
	public static float minSlideFactor = 1.0f;
	public static float maxSlideFactor = 2.0f;
	public static float slideFactorAttacking = 10.0f;
	public static int inventoryPlayerX = width * 15 / 20;
	public static int inventoryPlayerY = 120;
	public static int inventoryWidthPlayer = 7;
	public static int inventoryHeightPlayer = 10;
	public static int inventoryDistToFrameX = 10;
	public static int inventoryDistToFrameY = 10;
	public static int inventoryItemTileWidth = 32;
	public static int inventoryItemTileHeight = 32;
	public static int inventorySlotDistX = 5;
	public static int inventorySlotDistY = 5;
	public static int inventoryFontSize = 12;
	public static int inventoryFontDistanceX = 10;
	public static int inventoryFontDistanceY = 0;//inventoryFontSize;
	
	public static int inventoryUseWidthPlayer = 5;
	public static int inventoryUseHeightPlayer = 1;
	public static int inventoryUsePlayerX = width/2 - ( inventoryUseWidthPlayer*(inventoryItemTileWidth+2*inventorySlotDistX)+ 2*inventoryDistToFrameX)/2;
	public static int inventoryUsePlayerY = height - 2*(inventoryItemTileHeight+ 2 * inventoryDistToFrameY + 2* inventorySlotDistY);
	
	public static int inventoryEqBodyPlayerX = 150;
	public static int inventoryEqBodyPlayerY = 175;
	public static int inventoryEqWeaponPlayerX = 225;
	public static int inventoryEqWeaponPlayerY = 175;
	public static int inventoryEqHelmetPlayerX = 150;
	public static int inventoryEqHelmetPlayerY = 100;
	public static int inventoryEqFeetPlayerX = 150;
	public static int inventoryEqFeetPlayerY = 400;
	public static int inventoryEqShieldPlayerX = 75;
	public static int inventoryEqShieldPlayerY = 175;
	
	public static int inventoryEqTrousersPlayerX = 150;
	public static int inventoryEqTrousersPlayerY = 325;
	public static int inventoryEqShirtPlayerX = 150;
	public static int inventoryEqShirtPlayerY = 250;
	public static int inventoryEqGlovePlayerX = 75;
	public static int inventoryEqGlovePlayerY = 250;
	public static int inventoryEqAmuletPlayerX = 75;
	public static int inventoryEqAmuletPlayerY = 100;
	
	/** item properties */
	public static String itemEquipPrefix = "equip_";
	
	
	public static int itemMaterialCapacity = 50;
	public static long maximumLifeDurationItems = 600000; // milliseconds life duration
	
	/** interaction properties */
	public static long interactionMaximumLifeDuration = 20000; // milliseconds life duration
	public static int interactionLimit = 100;
	
	/** animation stuff */
	public static int animationDelay = 200;
	public static int animationDelayItems = 200;
	public static int animationDelayFire = 75;
	public static int ani_standard_start = 0;
	public static int ani_standard_end = 3;
//	public static int ani_l_start = 28;
//	public static int ani_l_end = 31;
//	public static int ani_dl_start = 24;
//	public static int ani_dl_end = 27;
//	public static int ani_ul_start = 32;
//	public static int ani_ul_end = 35;
//	public static int ani_r_start = 12;
//	public static int ani_r_end = 15;
//	public static int ani_dr_start = 16;
//	public static int ani_dr_end = 19;
//	public static int ani_ur_start = 8;
//	public static int ani_ur_end = 11;
//	public static int ani_u_start = 4;
//	public static int ani_u_end = 7;
//	public static int ani_d_start = 20;
//	public static int ani_d_end = 23;
	
	/** animation setup standing and walking */
	public static int ani_standing_d_start = 0;
	public static int ani_standing_d_end = 3;
	public static int ani_standing_u_start = 4;
	public static int ani_standing_u_end = 7;
	public static int ani_standing_l_start = 8;
	public static int ani_standing_l_end = 11;
	public static int ani_standing_r_start = 12;
	public static int ani_standing_r_end = 15;
	public static int ani_walking_d_start = 16;
	public static int ani_walking_d_end = 19;
	public static int ani_walking_u_start = 20;
	public static int ani_walking_u_end = 23;
	public static int ani_walking_l_start = 24;
	public static int ani_walking_l_end = 27;
	public static int ani_walking_r_start = 28;
	public static int ani_walking_r_end = 31;
	
	/** attacking **/
	public static int ani_attacking_d_start = 32;
	public static int ani_attacking_d_end = 35;
	public static int ani_attacking_u_start = 36;
	public static int ani_attacking_u_end = 39;
	public static int ani_attacking_l_start = 40;
	public static int ani_attacking_l_end = 43;
	public static int ani_attacking_r_start = 44;
	public static int ani_attacking_r_end = 47;
	
	/** hurting **/
	public static int ani_hurting_d_start = 48;
	public static int ani_hurting_d_end = 51;
	public static int ani_hurting_u_start = 52;
	public static int ani_hurting_u_end = 55;
	public static int ani_hurting_l_start = 56;
	public static int ani_hurting_l_end = 59;
	public static int ani_hurting_r_start = 60;
	public static int ani_hurting_r_end = 63;
	
	/** working **/
	public static int ani_working_d_start = 64;
	public static int ani_working_d_end = 67;
	public static int ani_working_u_start = 68;
	public static int ani_working_u_end = 71;
	public static int ani_working_l_start = 72;
	public static int ani_working_l_end = 75;
	public static int ani_working_r_start = 76;
	public static int ani_working_r_end = 79;
	
	
	
	/** sleeping **/
	public static int ani_sleeping_l_start = 16;
	public static int ani_sleeping_l_end = 19;
	public static int ani_sleeping_r_start = 20;
	public static int ani_sleeping_r_end = 23;
	public static int ani_sleeping_u_start = 24;
	public static int ani_sleeping_u_end = 27;
	public static int ani_sleeping_d_start = 28;
	public static int ani_sleeping_d_end = 31;
	
	/** dead **/
	public static int ani_dead_l_start = 80;
	public static int ani_dead_l_end = 83;
	public static int ani_dead_r_start = 80;
	public static int ani_dead_r_end = 83;
	public static int ani_dead_u_start = 80;
	public static int ani_dead_u_end = 83;
	public static int ani_dead_d_start = 80;
	public static int ani_dead_d_end = 83;
	
	
	/** item animation stuff **/
	public static int ani_inventory_simple_start = 4;
	public static int ani_inventory_simple_end = 4;
	public static int ani_inventory_good_start = 5;
	public static int ani_inventory_good_end = 5;
	public static int ani_inventory_perfect_start = 6;
	public static int ani_inventory_perfect_end = 6;
	public static int ani_inventory_magic_start = 7;
	public static int ani_inventory_magic_end = 7;
	public static int ani_inventory_destroyed_start = 8;
	public static int ani_inventory_destroyed_end = 8;
	public static int ani_inventory_damaged_start = 9;
	public static int ani_inventory_damaged_end = 9;
	
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
	public static int contextMenuButtonDistYBetweenButtons = 10;
	public static int contextMenuTextDistX = 5;
	public static int contextMenuTextDistY = 0;
	public static int contextMenuFrameDistX = 5;
	public static int contextMenuFrameDistY = 5;
	
	/** infoBoxGUI settings */
	public static int infoBoxHeadingSize = 16;
	public static int infoBoxFontSize = 12;
	public static int infoBoxTextWidth = 256;
	public static int infoBoxTextDistance = 5;
	public static int infoBoxTextDistanceX = 10;
	public static int infoBoxTextSpaceX = 130;
	public static int infoBoxTextSpaceY = 3;
	
	/** channel settings */
	public static boolean useChannels = true;
	
	/** zoom properties */
	public static float maxZoomFactor = 15.0f;
	public static float minZoomFactor = 0.1f;
	public static int factorOfViewDeleteDistance = 1;
	
	/** standard map properties (small floor and object elements) */
	public static int mapWidth = (int) (48/scaleTileSize);//32; // EVEN and devide-able through 4!!!
	public static int mapHeight = (int) (32/scaleTileSize);//20; // EVEN!!!
	public static int mapTileSetWidth = 32;
	public static int mapTileSetHeight = 32;
	public static int mapTotalWidth = mapWidth*mapTileSetWidth;
	public static int mapTotalHeight = mapHeight*mapTileSetHeight;
	
//	public static int mapSizeXRepetition = 4;
//	public static int mapSizeYRepetition = 4;
	
	/** underground maps properties */
	public static int ugrMapWidth = (int) (12/scaleTileSize); // depends strongly on mapWidth * mapTileSetWidth (must be the same)
	public static int ugrMapHeight = (int) (8/scaleTileSize); // depends strongly on mapHeight * mapTileSetHeight (must be the same)
	public static int ugrMapTileSetWidth = 128; // depends strongly on mapWidth * mapTileSetWidth (must be the same)
	public static int ugrMapTileSetHeight = 128; // depends strongly on mapHeight * mapTileSetHeight (must be the same)
	public static int ugrTotalMapWith = ugrMapWidth*ugrMapTileSetWidth; 
	public static int ugrTotalMapHeight = ugrMapWidth*ugrMapTileSetHeight;
	
	public static int ugrMapTotalWidth = ugrMapWidth*ugrMapTileSetWidth;
	public static int ugrMapTotalHeight = ugrMapHeight*ugrMapTileSetHeight;
	
	public static int overlayOffsetY = -2*mapTileSetWidth;
	public static int distanceOfSecondTreeLayer = 0;
	
	public static int maximumLayersPerWorldType = 3;
	
	/** tileType properties */
	public static int treeTile = 501;
	public static int stoneTile = 891;
	public static int smallTreeTile = 505;
	public static int smallStoneTile = 636;
	public static int soilBlockTile = 634;
	public static int grassTile = 558;
	public static int lightTile = 745;
	public static int cobbleTile = 47;
	public static int dirtTrackTile = 548;
	public static int snowTile = 654;
	public static int holeTile = 649;
	public static int snowTreeTile = 498;
	public static int campfireTile = 626;
	public static int smallBushTile = 670;
	public static int waterTile = 760;
//	public static int thickWoodWallTile = 20000;
	public static int bigBushTile = 798;
	public static int undergroundGrassTile = 10;
	public static int undergroundEarthTile = 28;
	public static int undergroundStoneTile = 46;
	public static int collectStoneTile1 = 893;
	public static int collectStoneTile2 = 894;
	public static int collectStoneTile3 = 862;
//	public static int collectStoneTile4 = 861;
	public static int collectArtifactStoneTile1 = 762;
	public static int collectArtifactStoneTile2 = 763;
	public static int collectArtifactStoneTile3 = 731;
//	public static int collectArtifactStoneTile4 = 730;
	public static int fluidWoodTile = 163;
	public static int fluidSoilTile = 168;
	public static int fluidStoneTile = 417;
	
	public static int[] fluidObjects = new int[]{fluidWoodTile,fluidSoilTile,fluidStoneTile};
	
	/** light settings */
	public static String lightTileSetNameDay = "tilesets/light16day.png";
	public static String lightTileSetNameNight = "tilesets/light16night.png";
	public static String lightTileSetName00 = "tilesets/light00.png";
	public static String lightTileSetName01 = "tilesets/light01.png";
	public static String lightTileSetName02 = "tilesets/light02.png";
	public static String lightTileSetName03 = "tilesets/light03.png";
	public static String lightTileSetName04 = "tilesets/light04.png";
	public static String lightTileSetName05 = "tilesets/light05.png";
	public static String lightTileSetName06 = "tilesets/light06.png";
	public static String lightTileSetName07 = "tilesets/light07.png";
	public static String lightTileSetName08 = "tilesets/light08.png";
	public static String lightTileSetName09 = "tilesets/light09.png";
	public static String lightTileSetName10 = "tilesets/light10.png";
	public static String lightTileSetName11 = "tilesets/light11.png";
	public static String lightTileSetName12 = "tilesets/light12.png";
	public static String lightTileSetName13 = "tilesets/light13.png";
	public static String lightTileSetName14 = "tilesets/light14.png";
	public static String lightTileSetName15 = "tilesets/light15.png";
	public static String lightTileSetName16 = "tilesets/light16.png";
	public static String lightTileSetName17 = "tilesets/light17.png";
	public static String lightTileSetName18 = "tilesets/light18.png";
	public static String lightTileSetName19 = "tilesets/light19.png";
	public static String fireTileSetName = "tilesets/fire.png";
	public static int maxLightIntensityCutoff = 23;
	public static int lightMapWidth = (int) ((((float) width)/1)*(2/scaleTileSize));
	public static int lightMapHeight = (int) ((((float) height)/1)*(2/scaleTileSize));
	public static int lightTileWidth = 16;
	public static int lightTileHeight = 16;
	public static int fireTileWidth = 32;
	public static int fireTileHeight = 50;
	

	
	/** agent properties */
	public static int agentMaxCount = 250;
	public static float agentGoalDistance = 16.0f;
	public static float agentEntityVisionDistance = 400.0f;//Float.MAX_VALUE;//400.0f/scaleTileSize;
	public static float agentDeleteDistance = mapTotalWidth*3;
	public static float agentAfraidOfLightDistance = 100.0f;
	public static float agentRunAwayFromFire = 300.0f; 
	public static float agentGoalAttackDistance = 48.0f;
	public static float agentGoalDecelerationResetDistance = 64.0f;
	public static float agentGoalDecelerationMinValue = 0.05f;
	public static int agentInfoIconDistanceY = 5;
	public static String agentInfoIconTileSetName = standardTilePathPlayers+"infoIcon.png";
	public static String agentInfoIconName = "infoIcon";
	public static int agentInfoIconTileSetWidth = 32;
	public static int agentInfoIconTileSetHeight=32;
	
	
	
	/** agent path properties */
	public static float pathMinAcceptanceDistance = 4.0f;
	public static long timeForPathCalculation = 10000; // milliseconds
	
	public static long timeAfterDeathTillDeletion = 1000; // milliseconds
	
	/** server-property for deletion of instantaneous areaInfluences */
	public static long timespanDeleteInstantAreaInfluence = 1000;
	public static long timespanDeleteInstantGrowthSpot = 1000;
	
	/** mathematical properties */
	public static float standardDeviationItemCount = (float)1/10;
	
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
	
	/** differences between player classes for the start */
	public static float getPlayerProperty(String playerTileSetName, PlayerProperty playerProperty) {
		float retValue = 0.0f;
		if (playerTileSetName.equals(nameStandardPlayer)) {
			switch (playerProperty) {
			case WIDTH: retValue = 20.0f;
			break;
			case HEIGHT: retValue = 52.0f;
			break;
			case MAXLIFE: retValue = 100.0f;
			break;
			case MAXMANA: retValue = 10.0f;
			break;
			case MAXSPEED : retValue = 150.0f;
			break;
			case STRENGH: retValue = 10.0f;
			break;
			case ARMOR: retValue = 0.0f;
			break;
			case ATTACKSPERSECOND: retValue = 2.0f;
			break;
			case LIFEREGENERATION: retValue = 0.01f;
			break;
			case MANAREGENERATION: retValue = 0.03f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 5.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 32.0f;
			break;
			case ATTACKDISTANCE: retValue = 24.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case CRAFTLEVEL: retValue = 1.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = -0.001f;
			break;
			case INTERACTSOIL: retValue = 20.0f;
			break;
			case INTERACTSTONE: retValue = 0.0f;
			break;
			case INTERACTWOOD: retValue = 0.0f;
			break;
			default:
				;
			}
		} else if (playerTileSetName.equals(nameDwarf)) {
			switch (playerProperty) {
			case WIDTH: retValue = 52.0f;
			break;
			case HEIGHT: retValue = 64.0f;
			break;
			case MAXLIFE: retValue = 200.0f;
			break;
			case MAXMANA: retValue = 5.0f;
			break;
			case MAXSPEED : retValue = 50.0f;
			break;
			case STRENGH: retValue = 30.0f;
			break;
			case ATTACKSPERSECOND: retValue = 2.0f;
			break;
			case LIFEREGENERATION: retValue = 0.02f;
			break;
			case MANAREGENERATION: retValue = 0.01f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 5.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 16.0f;
			break;
			case ATTACKDISTANCE: retValue = 16.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = -0.01f;
			break;
			default:
				;
			}
		}  else if (playerTileSetName.equals(nameWolf)) {
			switch (playerProperty) {
			case WIDTH: retValue = 64.0f;
			break;
			case HEIGHT: retValue = 64.0f;
			break;
			case MAXLIFE: retValue = 35.0f;
			break;
			case MAXMANA: retValue = 0.0f;
			break;
			case MAXSPEED : retValue = 120.0f;
			break;
			case STRENGH: retValue = 10.0f;
			break;
			case ARMOR: retValue = 0.0f;
			break;
			case ATTACKSPERSECOND: retValue = 1.0f;
			break;
			case LIFEREGENERATION: retValue = 0.01f;
			break;
			case MANAREGENERATION: retValue = 0.00f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 10.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 32.0f;
			break;
			case ATTACKDISTANCE: retValue = 16.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = 0.0f;
			break;
			default:
				;
			}
		} else if (playerTileSetName.equals(nameGreyWolf)) {
			switch (playerProperty) {
			case WIDTH: retValue = 64.0f;
			break;
			case HEIGHT: retValue = 64.0f;
			break;
			case MAXLIFE: retValue = 50.0f;
			break;
			case MAXMANA: retValue = 0.0f;
			break;
			case MAXSPEED : retValue = 140.0f;
			break;
			case STRENGH: retValue = 15.0f;
			break;
			case ARMOR: retValue = 3.0f;
			break;
			case ATTACKSPERSECOND: retValue = 1.2f;
			break;
			case LIFEREGENERATION: retValue = 0.01f;
			break;
			case MANAREGENERATION: retValue = 0.00f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 10.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 32.0f;
			break;
			case ATTACKDISTANCE: retValue = 16.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = 0.0f;
			break;
			default:
				;
			}
		} else if (playerTileSetName.equals(nameDarkWolf)) {
			switch (playerProperty) {
			case WIDTH: retValue = 64.0f;
			break;
			case HEIGHT: retValue = 64.0f;
			break;
			case MAXLIFE: retValue = 80.0f;
			break;
			case MAXMANA: retValue = 0.0f;
			break;
			case MAXSPEED : retValue = 120.0f;
			break;
			case STRENGH: retValue = 25.0f;
			break;
			case ARMOR: retValue = 4.0f;
			break;
			case ATTACKSPERSECOND: retValue = 1.2f;
			break;
			case LIFEREGENERATION: retValue = 0.02f;
			break;
			case MANAREGENERATION: retValue = 0.00f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 10.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 32.0f;
			break;
			case ATTACKDISTANCE: retValue = 16.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = 0.0f;
			break;
			default:
				;
			}
		} else if (playerTileSetName.equals(nameWolfBig)) {
			switch (playerProperty) {
			case WIDTH: retValue = 64.0f;
			break;
			case HEIGHT: retValue = 64.0f;
			break;
			case MAXLIFE: retValue = 100.0f;
			break;
			case MAXMANA: retValue = 0.0f;
			break;
			case MAXSPEED : retValue = 120.0f;
			break;
			case STRENGH: retValue = 10.0f;
			break;
			case ARMOR: retValue = 5.0f;
			break;
			case ATTACKSPERSECOND: retValue = 1.0f;
			break;
			case LIFEREGENERATION: retValue = 0.01f;
			break;
			case MANAREGENERATION: retValue = 0.00f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 10.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 32.0f;
			break;
			case ATTACKDISTANCE: retValue = 16.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = 0.0f;
			break;
			default:
				;
			}
		} else if (playerTileSetName.equals(nameGreyWolfBig)) {
			switch (playerProperty) {
			case WIDTH: retValue = 64.0f;
			break;
			case HEIGHT: retValue = 64.0f;
			break;
			case MAXLIFE: retValue = 120.0f;
			break;
			case MAXMANA: retValue = 0.0f;
			break;
			case MAXSPEED : retValue = 120.0f;
			break;
			case STRENGH: retValue = 10.0f;
			break;
			case ARMOR: retValue = 6.0f;
			break;
			case ATTACKSPERSECOND: retValue = 1.0f;
			break;
			case LIFEREGENERATION: retValue = 0.01f;
			break;
			case MANAREGENERATION: retValue = 0.00f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 10.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 32.0f;
			break;
			case ATTACKDISTANCE: retValue = 16.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = 0.0f;
			break;
			default:
				;
			}
		} else if (playerTileSetName.equals(nameDarkWolfBig)) {
			switch (playerProperty) {
			case WIDTH: retValue = 64.0f;
			break;
			case HEIGHT: retValue = 64.0f;
			break;
			case MAXLIFE: retValue = 140.0f;
			break;
			case MAXMANA: retValue = 0.0f;
			break;
			case MAXSPEED : retValue = 120.0f;
			break;
			case STRENGH: retValue = 10.0f;
			break;
			case ARMOR: retValue = 6.0f;
			break;
			case ATTACKSPERSECOND: retValue = 1.0f;
			break;
			case LIFEREGENERATION: retValue = 0.01f;
			break;
			case MANAREGENERATION: retValue = 0.00f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 10.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 32.0f;
			break;
			case ATTACKDISTANCE: retValue = 16.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = 0.0f;
			break;
			default:
				;
			}
		} else if (playerTileSetName.equals(nameRabbit)) {
			switch (playerProperty) {
			case WIDTH: retValue = 12.0f;
			break;
			case HEIGHT: retValue = 26.0f;
			break;
			case MAXLIFE: retValue = 30.0f;
			break;
			case MAXMANA: retValue = 0.0f;
			break;
			case MAXSPEED : retValue = 40.0f;
			break;
			case STRENGH: retValue = 5.0f;
			break;
			case ARMOR: retValue = 0.0f;
			break;
			case ATTACKSPERSECOND: retValue = 1.0f;
			break;
			case LIFEREGENERATION: retValue = 0.01f;
			break;
			case MANAREGENERATION: retValue = 0.00f;
			break;
			case MINATTACKSPEED: retValue = 0.1f;
			break;
			case MAXATTACKSPEED: retValue = 10.0f;
			break;
			case SLIDEFACTOR: retValue = 5.0f;
			break;
			case ATTACKSQUARESIZE: retValue = 16.0f;
			break;
			case ATTACKDISTANCE: retValue = 12.0f;
			break;
			case LIFELEECH: retValue = 0.0f;
			break;
			case MAXENERGY: retValue = 100.0f;
			break;
			case ENERGYREGERERATION: retValue = 0.0f;
			break;
			default:
				;
			}
		}
		return retValue;
	}
	
	/** get itemProperties of different playerTileSets = classes of players */
	public static int getItemProperty(String playerTileSetName, ItemProperty itemProperty) {
		int retValue = 0;
		if (playerTileSetName.equals(nameStandardPlayer)) {
			switch(itemProperty) {
			case WIDTHARMOR: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH) + 6;
				break;
			}
			case HEIGHTARMOR: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XARMOR: {
				retValue = -3;
				break;
			}
			case YARMOR: {
				retValue = 0;
				break;
			}
			case WIDTHWEAPON: {
				retValue = (int) 78;
				break;
			}
			case HEIGHTWEAPON: {
				retValue = (int) 104;
				break;
			}
			case XWEAPON: {
				retValue = -29;
				break;
			}
			case YWEAPON: {
				retValue = -26;
				break;
			}
			case WIDTHHELMET: {
				retValue = (int) 26;
				break;
			}
			case HEIGHTHELMET: {
				retValue = (int) 39;
				break;
			}
			case XHELMET: {
				retValue = -3;
				break;
			}
			case YHELMET: {
				retValue = -13;
				break;
			}
			case WIDTHFEET: {
				retValue = (int) 26;
				break;
			}
			case HEIGHTFEET: {
				retValue = (int) 26;
				break;
			}
			case XFEET: {
				retValue = -3;
				break;
			}
			case YFEET: {
				retValue = 26;
				break;
			}
			case WIDTHSHIELD: {
				retValue = 36;
				break;
			}
			case HEIGHTSHIELD: {
				retValue = 52;
				break;
			}
			case XSHIELD: {
				retValue = -8;
				break;
			}
			case YSHIELD: {
				retValue = 0;
				break;
			}
			case WIDTHAMULET: {
				retValue = 26;
				break;
			}
			case HEIGHTAMULET: {
				retValue = 26;
				break;
			}
			case XAMULET: {
				retValue = -3;
				break;
			}
			case YAMULET: {
				retValue = 17;
				break;
			}
			case WIDTHGLOVE: {
				retValue = (int) 26;
				break;
			}
			case HEIGHTGLOVE: {
				retValue = (int) 26;
				break;
			}
			case XGLOVE: {
				retValue = -3;
				break;
			}
			case YGLOVE: {
				retValue = 17;
				break;
			}
			case WIDTHSHIRT: {
				retValue = (int) 26;
				break;
			}
			case HEIGHTSHIRT: {
				retValue = (int) 26;
				break;
			}
			case XSHIRT: {
				retValue = -3;
				break;
			}
			case YSHIRT: {
				retValue = 17;
				break;
			}
			case WIDTHTROUSERS: {
				retValue = (int) 26;
				break;
			}
			case HEIGHTTROUSERS: {
				retValue = (int) 26;
				break;
			}
			case XTROUSERS: {
				retValue = -3;
				break;
			}
			case YTROUSERS: {
				retValue = 26;
				break;
			}
			default:  
				break;
			}
		} else if (playerTileSetName.equals(GameStates.nameDwarf)) {
			switch(itemProperty) {
			case WIDTHARMOR: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTARMOR: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XARMOR: {
				retValue = -10;
				break;
			}
			case YARMOR: {
				retValue = 0;
				break;
			}
			case WIDTHWEAPON: {
				retValue = (int) 78;
				break;
			}
			case HEIGHTWEAPON: {
				retValue = (int) 78;
				break;
			}
			case XWEAPON: {
				retValue = -29;
				break;
			}
			case YWEAPON: {
				retValue = -26;
				break;
			}
			case WIDTHHELMET: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTHELMET: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XHELMET: {
				retValue = -10;
				break;
			}
			case YHELMET: {
				retValue = 0;
				break;
			}
			case WIDTHFEET: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTFEET: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XFEET: {
				retValue = -10;
				break;
			}
			case YFEET: {
				retValue = 0;
				break;
			}
			case WIDTHSHIELD: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTSHIELD: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XSHIELD: {
				retValue = -10;
				break;
			}
			case YSHIELD: {
				retValue = 0;
				break;
			}
			case WIDTHAMULET: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTAMULET: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XAMULET: {
				retValue = -10;
				break;
			}
			case YAMULET: {
				retValue = 0;
				break;
			}
			case WIDTHGLOVE: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTGLOVE: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XGLOVE: {
				retValue = -10;
				break;
			}
			case YGLOVE: {
				retValue = 0;
				break;
			}
			case WIDTHSHIRT: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTSHIRT: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XSHIRT: {
				retValue = -10;
				break;
			}
			case YSHIRT: {
				retValue = 0;
				break;
			}
			case WIDTHTROUSERS: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.WIDTH);
				break;
			}
			case HEIGHTTROUSERS: {
				retValue = (int) getPlayerProperty(playerTileSetName, PlayerProperty.HEIGHT);
				break;
			}
			case XTROUSERS: {
				retValue = -10;
				break;
			}
			case YTROUSERS: {
				retValue = 0;
				break;
			}
			default:  
				break;
			}
		}
		
		return retValue;
	}
	
	public static String getStringRepresentationOfAttribute(AttributeType attribute) {
		String retString = "";
		switch (attribute) {
		case ANIMALCAMOUFLAGE: {
			retString = "Animal Camouflage";
			break;
		}
		case ARMOR: {
			retString = "Armor";
			break;
		}
		case ATTACKDISTANCE: {
			retString = "Attack Distance";
			break;
		}
		case ATTACKSPEED: {
			retString = "Attack Speed";
			break;
		}
		case ATTACKSQUARESIZE: {
			retString = "Attack Size";
			break;
		}
		case BLOCKCHANCE: {
			retString = "Blockchance";
			break;
		}
		case GOLDVISIONRADIUS: {
			retString = "Goldvision";
			break;
		}
		case HUMANCAMOUFLAGE: {
			retString = "Human Camouflage";
			break;
		}
		case LIFE: {
			retString = "Life";
			break;
		}
		case LIFEREGENERATION: {
			retString = "Life Regeneration";
			break;
		}
		case LIFEVISIONRADIUS: {
			retString = "Life Vision Radius";
			break;
		}
		case LIGHTRADIUS: {
			retString = "Light Radius";
			break;
		}
		case MANA: {
			retString = "Mana";
			break;
		}
		case MANAREGENERATION: {
			retString = "Mana Regeneration";
			break;
		}
		case MAXLIFE: {
			retString = "Maximum Life";
			break;
		}
		case MAXMANA: {
			retString = "Maximum Mana";
			break;
		}
		case MAXSPEED: {
			retString = "Maximum Speed";
			break;
		}
		case MX: {
			retString = "MX";
			break;
		}
		case MY: {
			retString = "MY";
			break;
		}
		case SENDMX : {
			retString = "SENDMX";
			break;
		}
		case SENDMY: {
			retString = "SENDMY";
			break;
		}
		case SPEED: {
			retString = "Speed";
			break;
		}
		case STRENGH: {
			retString = "Strengh";
			break;
		}
		case VISIBILITY: {
			retString = "Visibility";
			break;
		}
		case WILLPOWER: {
			retString = "Willpower";
			break;
		}
		case LIFELEECH:  {
			retString = "Lifeleech";
			break;
		}
		case CRAFTLEVEL: {
			retString = "Craft-Level";
			break;
		}
		case ENERGY: {
			retString = "Energy";
			break;
		}
		case MAXENERGY: {
			retString = "Maximum Energy";
			break;
		}
		case ENERGYREGENERATION: {
			retString = "Energy Regeneration";
			break;
		}
		case INTERACTSOIL: {
			retString = "Soil Interaction";
			break;
		}
		case INTERACTSTONE: {
			retString = "Stone Interaction";
			break;
		}
		case INTERACTWOOD: {
			retString = "Wood Interaction";
			break;
		}
		default: retString = "unknown attribute";
		}
		return retString;
	}
	
	public static ItemProperty getItemPropertyType(ITEMCODE itemCode, String data) {
		ItemProperty retProperty = null;
		switch (itemCode) {
		case AMULET: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHAMULET;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTAMULET;
			if (data.equals("x")) retProperty = ItemProperty.XAMULET;
			if (data.equals("y")) retProperty = ItemProperty.YAMULET;
			break;
		}
		case BODY: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHARMOR;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTARMOR;
			if (data.equals("x")) retProperty = ItemProperty.XARMOR;
			if (data.equals("y")) retProperty = ItemProperty.YARMOR;
			break;
		}
		case FEET: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHFEET;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTFEET;
			if (data.equals("x")) retProperty = ItemProperty.XFEET;
			if (data.equals("y")) retProperty = ItemProperty.YFEET;
			break;
		}
		case FINGER: {
			
			break;
		}
		case GLOVE: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHGLOVE;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTGLOVE;
			if (data.equals("x")) retProperty = ItemProperty.XGLOVE;
			if (data.equals("y")) retProperty = ItemProperty.YGLOVE;
			break;
		}
		case HELMET: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHHELMET;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTHELMET;
			if (data.equals("x")) retProperty = ItemProperty.XHELMET;
			if (data.equals("y")) retProperty = ItemProperty.YHELMET;
			break;
		}
		case SHIELD: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHSHIELD;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTSHIELD;
			if (data.equals("x")) retProperty = ItemProperty.XSHIELD;
			if (data.equals("y")) retProperty = ItemProperty.YSHIELD;
			break;
		}
		case SHIRT: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHSHIRT;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTSHIRT;
			if (data.equals("x")) retProperty = ItemProperty.XSHIRT;
			if (data.equals("y")) retProperty = ItemProperty.YSHIRT;
			break;
		}
		case STONE: {
			
			break;
		}
		case TROUSERS: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHTROUSERS;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTTROUSERS;
			if (data.equals("x")) retProperty = ItemProperty.XTROUSERS;
			if (data.equals("y")) retProperty = ItemProperty.YTROUSERS;
			break;
		}
		case WEAPON: {
			if (data.equals("width")) retProperty = ItemProperty.WIDTHWEAPON;
			if (data.equals("height")) retProperty = ItemProperty.HEIGHTWEAPON;
			if (data.equals("x")) retProperty = ItemProperty.XWEAPON;
			if (data.equals("y")) retProperty = ItemProperty.YWEAPON;
			break;
		}
		case WOOD: {
			
			break;
		}
		case ARTIFACTSTONE: {
			
			break;
		}
		default: ;
		}
		
		return retProperty; 
	}
	
	public static double getVolumeByMusicType(MusicType musicType) {
		double retValue = 0.0;
		switch (musicType) {
		case BACKGROUND: {
			retValue = 0.8;
			break;
		}
		case ATMOSPHERE: {
			retValue = 0.2;
			break;
		}
		case EMPTY: {
			retValue = 1.0;
			break;
		}
		case INSTRUMENT: {
			retValue = 1.0;
			break;
		}
		case MOVINGSOUND: {
			retValue = 0.9;
			break;
		}
		case CAMPFIRE: {
			retValue = 1.0;
			break;
		}
		default: retValue = 1.0;
		break;
		}
		return retValue;
	}
	
}
