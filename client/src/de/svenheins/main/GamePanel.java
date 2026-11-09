/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import de.svenheins.functions.MyUtil;
import de.svenheins.handlers.ClientMessageHandler;
import de.svenheins.handlers.InputHandler;
import de.svenheins.handlers.MouseHandler;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.gui.Button;
import de.svenheins.main.gui.ContainerGUI;
import de.svenheins.main.gui.ContainerGUIManager;
import de.svenheins.main.gui.EditorGUI;
import de.svenheins.main.gui.EditorGUIManager;
import de.svenheins.main.gui.PlayerListGUI;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.managers.AgentManager;
import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.LightManager;
import de.svenheins.managers.MainMenuEntityManager;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerAgentManager;
import de.svenheins.managers.PlayerAgentManagerManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.StreamViewerManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.managers.UndergroundMapManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.IngameWindow;
import de.svenheins.objects.Light;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.LocalUndergroundMap;
import de.svenheins.objects.MiniMap;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.Sprite;
import de.svenheins.objects.StreamViewer;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.agents.AggressiveAgent;
import de.svenheins.objects.agents.NormalAgent;
import de.svenheins.objects.agents.SimpleAgent;
import de.svenheins.objects.agents.StupidAgent;
import de.svenheins.objects.agents.goals.Goal;
import de.svenheins.objects.items.Container;
import de.svenheins.objects.items.Item;
import de.svenheins.objects.items.ItemFactoryCollection;
import de.svenheins.objects.items.equipment.AmuletFactory;
import de.svenheins.objects.items.equipment.BodyArmorFactory;
import de.svenheins.objects.items.equipment.FeetFactory;
import de.svenheins.objects.items.equipment.GloveFactory;
import de.svenheins.objects.items.equipment.HelmetFactory;
import de.svenheins.objects.items.equipment.ShieldFactory;
import de.svenheins.objects.items.equipment.ShirtFactory;
import de.svenheins.objects.items.equipment.TrousersFactory;
import de.svenheins.objects.items.equipment.WeaponFactory;
import de.svenheins.objects.items.food.FoodFactory;
import de.svenheins.objects.items.materials.MaterialFactory;
import de.svenheins.threads.AgentThread;
import de.svenheins.threads.AnimationThread;
import de.svenheins.threads.AreaInfluenceThread;
import de.svenheins.threads.ChannelUpdateMapsThread;
import de.svenheins.threads.ChannelUpdateThread;
import de.svenheins.threads.GraphicThread;
import de.svenheins.threads.GrowthSpotThread;
import de.svenheins.threads.InfluenceThread;
import de.svenheins.threads.InputThread;
import de.svenheins.threads.InteractionThread;
import de.svenheins.threads.ItemInfluenceThread;
import de.svenheins.threads.LightThread;
import de.svenheins.threads.MapUpdateThread;
import de.svenheins.threads.ModuloMapThread;
import de.svenheins.threads.MoveThread;
import de.svenheins.threads.ServerUpdateThread;
import de.svenheins.threads.SoundThread;
import de.svenheins.threads.StreamViewerThread;

public class GamePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Player p;
	private Player[] players;
	private PlayerEntity playerEntity;
	private Space spaceAdd;
	private MoveThread moveThread;
	private InputThread inputThread;
	private MouseHandler mouseHandler;
	
	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy;

	public static ServerUpdateThread serverUpdateThread;
	private GraphicThread graphicThread;
//	private CollisionThread collisionThread;
	private AnimationThread animationThread;
	private ChannelUpdateThread channelUpdateThread;
	private ChannelUpdateMapsThread channelUpdateMapsThread;
	private MapUpdateThread mapUpdateThread;
//	private LightThread lightThread;
	private ModuloMapThread moduloMapThread;
	private AgentThread agentThread;
	private InteractionThread interactionThread;
	private ItemInfluenceThread itemInfluenceThread;
	private AreaInfluenceThread areaInfluenceThread;
	private GrowthSpotThread growthSpotThread;
	private InfluenceThread influenceThread;
	private SoundThread soundThread;
	private StreamViewerThread streamViewerThread;
	public static GamePanel gp;

	public long last;
	public boolean showStats;
	private boolean serverInitialized;
	private boolean initializedPlayer;
	private boolean pause, menu;
	private boolean allwaysAnimated;
	private int viewPointX, viewPointY;
	private float zoomFactor;
	private double rotationDegree;
	private int maxViewPointX, maxViewPointY, minViewPointX, minViewPointY;
	private boolean deleteModus = false;
	private String paintLayer;
	private int paintType;
	private boolean paintingBlocked = false;
	private boolean smallPaint;
	private boolean paintEditSpaceArea = true;
	private Point mouseCoordinates;
	private Item mouseItem = null;
	private FoodFactory foodFactory;
	private MaterialFactory materialFactory;
	private BodyArmorFactory bodyArmorFactory; 
	private HelmetFactory helmetFactory;
	private WeaponFactory weaponFactory;
	private FeetFactory feetFactory;
	private ShieldFactory shieldFactory;
	private AmuletFactory amuletFactory;
	private GloveFactory gloveFactory;
	private TrousersFactory trousersFactory;
	private ShirtFactory shirtFactory;
	private Light playerLight;
	private TileSet lightTileDay;
	private TileSet lightTileNight;
	private int[][] lightMap;
	private TileSet lightTile;

	private int countFrames = 0;
	private long duration = 0;
	private long wholeDuration = 0;
	private long oldTime = System.currentTimeMillis();

	public IngameWindow mainMenu;
	public Space connectButton;
	
	/** global time on the server */
    private long worldTime = Long.MIN_VALUE; 
	private long systemNow = System.currentTimeMillis();
	private final long millis = 60*60*24*1000;
	private float factorSeconds = millis/GameStates.timespanOfOneDayInMilliseconds; 
	private long twentyfourTime = (worldTime) % millis;
	private long twentyfourFaster = (long) (twentyfourTime * factorSeconds);
	private double piTime = ((double)(twentyfourFaster)/millis)*Math.PI*2; 
	private double lightIntensity = (Math.sin(piTime)+1)/2;
	private float transparentLoad = 0;
	public Space loadingScreenSpace = null;
	
	/** map properties */
	private MiniMap miniMap;
	private MiniMap dungeonMiniMap;

	/**
	 * Constructor of the GamePanel 1) Init 2) Config 3) Start the Threads
	 */
	public GamePanel() {
		super();
		gp = this;
		
		this.init();
		this.config();
		
		
		new Thread(moveThread).start();
//		new Thread(collisionThread).start();
		new Thread(graphicThread).start();
		new Thread(inputThread).start();
		new Thread(animationThread).start();
		new Thread(channelUpdateThread).start();
		new Thread(mapUpdateThread).start();
		new Thread(channelUpdateMapsThread).start();
//		new Thread(lightThread).start();
		new Thread(moduloMapThread).start();
		new Thread(agentThread).start();
		new Thread(interactionThread).start();
		new Thread(itemInfluenceThread).start();
		new Thread(areaInfluenceThread).start();
		new Thread(growthSpotThread).start();
		new Thread(influenceThread).start();
		new Thread(soundThread).start();
		new Thread(streamViewerThread).start();
	}

	/**
	 * init(): Initialize all variables for the first time
	 */
	public void init() {
		p = new Player("Player1", GUI.gui.getInputHandler());
		players = new Player[] { p };
		this.pause=false;
		
		ItemFactoryCollection.init();
		
		/** TileSetManager init */
		TileSetManager.manager.init();
		
		/** init the playerEntity + set his hair to BlondHair*/
		this.setPlayerEntity(new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.nameStandardPlayer), GameStates.nameStandardPlayer, GameStates.roomStandardPlayer, BigInteger.valueOf(0), 0, 0, GameStates.animationDelay));
		
		
		this.miniMap = new MiniMap();
		this.dungeonMiniMap = new MiniMap(3);
		/** init Managers and GUIs */
		this.initManagers();
		this.initGUI();
		
//		this.initItemFactories();
		
		this.setWorldTime(Long.MIN_VALUE);
		
		ArrayList<Polygon> loadingScreenPolygon = new ArrayList<Polygon>();
		loadingScreenPolygon.add((new Polygon(new int[]{0 , 0 , GameStates.width, GameStates.width}, new int[]{0 ,  GameStates.height, GameStates.height, 0}, 4) ));
		
		loadingScreenSpace = new Space(loadingScreenPolygon, 0, 0, "loadingScreenSpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 1.0f, 1.0f, 1.0f, "empty");;
		
		/** Init the input */
		// Keyboard must not be added here
//		addMouseListener(new MouseHandler());
//		addMouseMotionListener(new MouseHandler());

		// Init Time
		gp.last = System.currentTimeMillis();
		// Init Threads
		moveThread = new MoveThread(System.currentTimeMillis());
		inputThread = new InputThread(players);
//		graphicThread = new GraphicThread(this);
//		collisionThread = new CollisionThread();
		animationThread = new AnimationThread();
		serverUpdateThread = new ServerUpdateThread(System.currentTimeMillis());
		channelUpdateThread = new ChannelUpdateThread();
		mapUpdateThread = new MapUpdateThread();
		channelUpdateMapsThread = new ChannelUpdateMapsThread();
//		lightThread = new LightThread();
		moduloMapThread = new ModuloMapThread();
		agentThread = new AgentThread();
		interactionThread = new InteractionThread();
		itemInfluenceThread = new ItemInfluenceThread();
		areaInfluenceThread = new AreaInfluenceThread();
		growthSpotThread = new GrowthSpotThread();
		influenceThread = new InfluenceThread();
		soundThread = new SoundThread();
		streamViewerThread = new StreamViewerThread();
	}

	public void config() {
//		gp.setFocusable(true);
		GameModus.modus = GameModus.MAINMENU;
		// config the global vars
		this.setPause(false);
		this.setAllwaysAnimated(true);
		this.setShowStats(false);
		this.setMenu(false);
		this.serverInitialized = false;
		this.setInitializedPlayer(false);
		this.setViewPoint(0, 0);
		this.minViewPointX = Integer.MIN_VALUE;// 362;
		this.minViewPointY = Integer.MIN_VALUE;// 181;
		this.maxViewPointX = Integer.MAX_VALUE;
		this.maxViewPointY = Integer.MAX_VALUE;
		this.setZoomFactor(GameStates.standardZoomFactor);
		this.setRotationDegree(0);
		this.setDeleteModus(false);
		this.setPaintLayer("empty");
		this.setPaintType(0);
		this.setSmallPaint(false);

		/** set all Managers and GUI elements to a standard state */
		configGUI();



		mainMenu = new IngameWindow();

	}

	public void initGUI() {
		TileSet tileSetCobbleButton = new TileSet(
				"tilesets/buttons/cobbleButton.png", "cobbleButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetGrassButton = new TileSet(
				"tilesets/buttons/grassButton.png", "grassButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetSnowButton = new TileSet(
				"tilesets/buttons/snowButton.png", "snowButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetTreeButton = new TileSet(
				"tilesets/buttons/treeButton.png", "treeButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetSnowTreeButton = new TileSet(
				"tilesets/buttons/snowTreeButton.png", "snowTreeButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetHoleButton = new TileSet(
				"tilesets/buttons/holeButton.png", "holeButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetUndergroundGrassButton = new TileSet(
				"tilesets/buttons/undergroundGrassButton.png",
				"undergroundGrassButton", GameStates.tileSetWidth,
				GameStates.tileSetHeight);
		TileSet tileSetPlayerSymbolButton = new TileSet(
				"tilesets/entities/playerSymbol.png",
				"playerSymbol", GameStates.tileSetWidth,
				GameStates.tileSetHeight);
		TileSet tileSetSmallTreeButton = new TileSet(
				"tilesets/buttons/smallTreeButton.png",
				"smallTreeButton", GameStates.tileSetWidth,
				GameStates.tileSetHeight);
		TileSet tileSetSmallStoneButton = new TileSet(
				"tilesets/buttons/smallStoneButton.png",
				"smallStoneButton", GameStates.tileSetWidth,
				GameStates.tileSetHeight);
		TileSet tileSetStoneButton = new TileSet(
				"tilesets/buttons/stoneButton.png",
				"stoneButton", GameStates.tileSetWidth,
				GameStates.tileSetHeight);
		TileSet tileSetWaterButton = new TileSet(
				"tilesets/buttons/waterButton.png", "waterButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetBushButton = new TileSet(
				"tilesets/buttons/bushButton.png", "bushButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetSmallBushButton = new TileSet(
				"tilesets/buttons/smallBushButton.png", "smallBushButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetCampfireButton = new TileSet(
				"tilesets/buttons/campfireButton.png", "campfireButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetSoilButton = new TileSet(
				"tilesets/buttons/soilButton.png", "soilButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetWoodenWallButton = new TileSet(
				"tilesets/buttons/woodenWallButton.png", "woodenWallButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetDirtTrackButton = new TileSet(
				"tilesets/buttons/dirtTrackButton.png", "dirtTrackButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetFluidStoneButton = new TileSet(
				"tilesets/buttons/fluidStoneButton.png", "fluidStoneButton",
				GameStates.tileSetWidth, GameStates.tileSetHeight);
//		TileSet tileSetFluidSoilButton = new TileSet(
//				"tilesets/buttons/fluidWoodButton.png", "fluidWoodButton",
//				GameStates.tileSetWidth, GameStates.tileSetHeight);
		
		BigInteger runningID = BigInteger.valueOf(0);
		int xPosition = GameStates.width - 64;
		int yPosition = 25;
		
		
		Button campFireButtonGUI = new Button(tileSetCampfireButton, "campfireButton",runningID, xPosition, yPosition,GameStates.animationDelay, "tree", GameStates.campfireTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button bushButtonGUI = new Button(tileSetBushButton, "bushButton",runningID, xPosition, yPosition,GameStates.animationDelay, "tree", GameStates.bigBushTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button smallBushButtonGUI = new Button(tileSetSmallBushButton, "smallBushButton",runningID, xPosition, yPosition,GameStates.animationDelay, "tree", GameStates.smallBushTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button snowButtonGUI = new Button(tileSetSnowButton, "snowButton",runningID, xPosition, yPosition,GameStates.animationDelay, "grass", GameStates.snowTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button cobbleButtonGUI = new Button(tileSetCobbleButton,"cobbleButton", runningID, xPosition,yPosition, GameStates.animationDelay, "cobble", GameStates.cobbleTile,	"");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button dirtTrackButtonGUI = new Button(tileSetDirtTrackButton,"dirtTrackButton", runningID, xPosition,yPosition, GameStates.animationDelay, "cobble", GameStates.dirtTrackTile,	"");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button grassButtonGUI = new Button(tileSetGrassButton, "grassButton", runningID, xPosition, yPosition, GameStates.animationDelay, "grass", GameStates.grassTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button holeButtonGUI = new Button(tileSetHoleButton, "holeButton",runningID, xPosition, yPosition,GameStates.animationDelay, "hole", GameStates.holeTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button waterButtonGUI = new Button(tileSetWaterButton, "waterButton",runningID, xPosition, yPosition,GameStates.animationDelay, "hole", GameStates.waterTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button snowTreeButtonGUI = new Button(tileSetSnowTreeButton,"snowTreeButton", runningID, xPosition,yPosition, GameStates.animationDelay, "tree", GameStates.snowTreeTile,"");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button soilButtonGUI = new Button(tileSetSoilButton, "soilButton",runningID, xPosition, yPosition,	GameStates.animationDelay, "tree", GameStates.fluidSoilTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button woodenWallButtonGUI = new Button(tileSetWoodenWallButton, "woodenWallButton",runningID, xPosition, yPosition,	GameStates.animationDelay, "tree", GameStates.fluidWoodTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button stoneButtonGUI = new Button(tileSetStoneButton, "stoneButton",runningID, xPosition, yPosition,	GameStates.animationDelay, "tree", GameStates.stoneTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button smallStoneButtonGUI = new Button(tileSetSmallStoneButton, "smallStoneButton",runningID, xPosition, yPosition,GameStates.animationDelay, "tree", GameStates.smallStoneTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button fluidStoneButtonGUI = new Button(tileSetFluidStoneButton, "fluidStoneButton",
		runningID, xPosition, yPosition,
		GameStates.animationDelay, "tree", GameStates.fluidStoneTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button treeButtonGUI = new Button(tileSetTreeButton, "treeButton",runningID, xPosition, yPosition,GameStates.animationDelay, "tree", GameStates.treeTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button smallTreeButtonGUI = new Button(tileSetSmallTreeButton, "smallTreeButton",
				runningID, xPosition, yPosition,
				GameStates.animationDelay, "tree", GameStates.smallTreeTile, "");
		xPosition -= 36;
		runningID = runningID.add(BigInteger.valueOf(1));
		
//		Button fluidWoodButtonGUI = new Button(tileSetFluidWoodButton, "fluidWoodButton",
//				runningID, xPosition, yPosition,
//				GameStates.animationDelay, "tree", GameStates.fluidWoodTile, "");
//		xPosition -= 36;
//		runningID = runningID.add(BigInteger.valueOf(1));
//		
//		Button fluidSoilButtonGUI = new Button(tileSetFluidSoilButton, "fluidSoilButton",
//				runningID, xPosition, yPosition,
//				GameStates.animationDelay, "tree", GameStates.fluidStoneTile, "");
//		xPosition -= 36;
//		runningID = runningID.add(BigInteger.valueOf(1));
		
		Button undergroundGrassButtonGUI = new Button(
				tileSetUndergroundGrassButton, "undergroundGrassButton",
				runningID, xPosition, yPosition,
				GameStates.animationDelay, "underground",
				GameStates.undergroundGrassTile, "");

		ArrayList<Polygon> editorGUISpacePolygon = new ArrayList<Polygon>();
		editorGUISpacePolygon.add((new Polygon(new int[] {xPosition-6, xPosition-6,	GameStates.width - 25, GameStates.width - 25 }, new int[] {25 - 5, 25 + GameStates.tileSetHeight + 5,25 + GameStates.tileSetHeight + 5, 25 - 5 }, 4)));
		Space editorGUISpace = new Space(editorGUISpacePolygon, 0, 0,"editorGUISpace", BigInteger.valueOf(0), new int[] { 0, 0, 0 },true, 0.6f, 1.0f, 1.0f, "empty");

		EditorGUI floorEditor = new EditorGUI("floor", "empty", 0,	editorGUISpace);
		floorEditor.add(cobbleButtonGUI);
		floorEditor.add(grassButtonGUI);
		floorEditor.add(dirtTrackButtonGUI);
		floorEditor.add(snowButtonGUI);
		floorEditor.add(treeButtonGUI);
		floorEditor.add(smallTreeButtonGUI);
		floorEditor.add(stoneButtonGUI);
		floorEditor.add(smallStoneButtonGUI);
		floorEditor.add(snowTreeButtonGUI);
		floorEditor.add(undergroundGrassButtonGUI);
		floorEditor.add(holeButtonGUI);
		floorEditor.add(waterButtonGUI);
		floorEditor.add(smallBushButtonGUI);
		floorEditor.add(bushButtonGUI);
		floorEditor.add(campFireButtonGUI);
		floorEditor.add(soilButtonGUI);
		floorEditor.add(woodenWallButtonGUI);
//		floorEditor.add(fluidWoodButtonGUI);
		floorEditor.add(fluidStoneButtonGUI);
		EditorGUIManager.add(floorEditor);

		Button playerMeGUIButton = new Button(tileSetPlayerSymbolButton,"playerMeButton", BigInteger.valueOf(0), 0, 0,GameStates.animationDelay, "Me", 0, "");

		PlayerListGUI playerEditorGUI = new PlayerListGUI("playerList",	"standard", 0);
		playerEditorGUI.add(playerMeGUIButton);
		PlayerListGUIManager.add(playerEditorGUI);

		this.initContainers();

		/** there is no item at the mouseCursor */
		this.setMouseItem(null);

		/** init GUI elements */
		connectButton = new Space("rechteckButton.svg", BigInteger.valueOf(0),
				"connect.png", 0.5f);
		connectButton.setAllXY(GameStates.width/2-connectButton.getWidth()/2, GameStates.height/2-connectButton.getHeight()/2);
	}

	public void initContainers() {
		int[] containerColor = GameStates.standardContainerColor;
		float containerTransparency = GameStates.standardContainerTranparency;
		ContainerGUIManager.containerGUIList = new HashMap<String, ContainerGUI>();
		ContainerGUIManager.idList = new ArrayList<String>();
		/** inventory GUI */
		int totalWidthInventory = GameStates.inventoryDistToFrameX	* 2	+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getInventory().getWidth();
		int totalHeightInventory = GameStates.inventoryDistToFrameY	* 2	+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getInventory().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygon = new ArrayList<Polygon>();
		inventoryGUISpacePolygon.add((new Polygon(new int[] { 0, 0,	totalWidthInventory, totalWidthInventory }, new int[] { 0,	totalHeightInventory, totalHeightInventory, 0 }, 4)));
		Space inventoryGUISpace = new Space(inventoryGUISpacePolygon, 0, 0,	"inventoryGUISpace", BigInteger.valueOf(0),	containerColor, true, containerTransparency, 1.0f, 1.0f, "empty");

		ContainerGUI playerInventoryGUI = new ContainerGUI(this.getPlayerEntity().getInventory(), "Player Inventory",GameStates.inventoryPlayerX, GameStates.inventoryPlayerY, "",	0, inventoryGUISpace);
		ContainerGUIManager.add(playerInventoryGUI);

		/** use inventory GUI */
		int totalWidthInventoryUse = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getInventoryUse().getWidth();
		int totalHeightInventoryUse = GameStates.inventoryDistToFrameY	* 2	+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getInventoryUse().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonUse = new ArrayList<Polygon>();
		inventoryGUISpacePolygonUse.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryUse, totalWidthInventoryUse }, new int[] {0, totalHeightInventoryUse, totalHeightInventoryUse, 0 }, 4)));
		Space inventoryGUISpaceUse = new Space(inventoryGUISpacePolygonUse, 0,	0, "inventoryGUISpace", BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f, 1.0f, "empty");

		ContainerGUI playerInventoryUseGUI = new ContainerGUI(this.getPlayerEntity().getInventoryUse(), "Player Use Inventory",	GameStates.inventoryUsePlayerX, GameStates.inventoryUsePlayerY,	"", 0, inventoryGUISpaceUse);
		ContainerGUIManager.add(playerInventoryUseGUI);

		/** body equipment GUI */
		int totalWidthInventoryEqBody = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentBody().getWidth();
		int totalHeightInventoryEqBody = GameStates.inventoryDistToFrameY* 2+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentBody().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqBody = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqBody.add((new Polygon(new int[] { 0, 0,totalWidthInventoryEqBody, totalWidthInventoryEqBody },new int[] { 0, totalHeightInventoryEqBody,	totalHeightInventoryEqBody, 0 }, 4)));
		Space inventoryGUISpaceEqBody = new Space(inventoryGUISpacePolygonEqBody, 0, 0, "inventoryGUISpace",BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqBodyGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentBody(),"Player EqBody Inventory", GameStates.inventoryEqBodyPlayerX,GameStates.inventoryEqBodyPlayerY, "", 0,inventoryGUISpaceEqBody);
		ContainerGUIManager.add(playerInventoryEqBodyGUI);

		/** weapon equipment GUI */
		int totalWidthInventoryEqWeapon = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentWeapon().getWidth();
		int totalHeightInventoryEqWeapon = GameStates.inventoryDistToFrameY	* 2+ (GameStates.inventorySlotDistY * 2	+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentWeapon().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqWeapon = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqWeapon.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryEqWeapon, totalWidthInventoryEqWeapon },new int[] { 0, totalHeightInventoryEqWeapon,	totalHeightInventoryEqWeapon, 0 }, 4)));
		Space inventoryGUISpaceEqWeapon = new Space(inventoryGUISpacePolygonEqWeapon, 0, 0, "inventoryGUISpace",BigInteger.valueOf(1), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqWeaponGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentWeapon(),	"Player EqWeapon Inventory",GameStates.inventoryEqWeaponPlayerX,GameStates.inventoryEqWeaponPlayerY, "", 0,	inventoryGUISpaceEqWeapon);
		ContainerGUIManager.add(playerInventoryEqWeaponGUI);

		/** helmet equipment GUI */
		int totalWidthInventoryEqHelmet = GameStates.inventoryDistToFrameX	* 2	+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentHelmet().getWidth();
		int totalHeightInventoryEqHelmet = GameStates.inventoryDistToFrameY	* 2+ (GameStates.inventorySlotDistY * 2	+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentHelmet().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqHelmet = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqHelmet.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryEqHelmet, totalWidthInventoryEqHelmet },	new int[] { 0, totalHeightInventoryEqHelmet,totalHeightInventoryEqHelmet, 0 }, 4)));
		Space inventoryGUISpaceEqHelmet = new Space(inventoryGUISpacePolygonEqHelmet, 0, 0, "inventoryGUISpace",BigInteger.valueOf(2), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqHelmetGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentHelmet(),	"Player EqHelmet Inventory",GameStates.inventoryEqHelmetPlayerX,GameStates.inventoryEqHelmetPlayerY, "", 0,	inventoryGUISpaceEqHelmet);
		ContainerGUIManager.add(playerInventoryEqHelmetGUI);

		/** feet equipment GUI */
		int totalWidthInventoryEqFeet = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentFeet().getWidth();
		int totalHeightInventoryEqFeet = GameStates.inventoryDistToFrameY* 2+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentFeet().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqFeet = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqFeet.add((new Polygon(new int[] { 0, 0,totalWidthInventoryEqFeet, totalWidthInventoryEqFeet },new int[] { 0, totalHeightInventoryEqFeet,	totalHeightInventoryEqFeet, 0 }, 4)));
		Space inventoryGUISpaceEqFeet = new Space(	inventoryGUISpacePolygonEqFeet, 0, 0, "inventoryGUISpace",BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f,1.0f, "empty");

		ContainerGUI playerInventoryEqFeetGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentFeet(),	"Player EqFeet Inventory", GameStates.inventoryEqFeetPlayerX,GameStates.inventoryEqFeetPlayerY, "", 0,inventoryGUISpaceEqFeet);
		ContainerGUIManager.add(playerInventoryEqFeetGUI);

		/** shield equipment GUI */
		int totalWidthInventoryEqShield = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentShield().getWidth();	
		int totalHeightInventoryEqShield = GameStates.inventoryDistToFrameY	* 2	+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentShield().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqShield = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqShield.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryEqShield, totalWidthInventoryEqShield },	new int[] { 0, totalHeightInventoryEqShield,totalHeightInventoryEqShield, 0 }, 4)));
		Space inventoryGUISpaceEqShield = new Space(inventoryGUISpacePolygonEqShield, 0, 0, "inventoryGUISpace",BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqShieldGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentShield(),	"Player EqShield Inventory",GameStates.inventoryEqShieldPlayerX,GameStates.inventoryEqShieldPlayerY, "", 0,inventoryGUISpaceEqShield);
		ContainerGUIManager.add(playerInventoryEqShieldGUI);
		
		/** amulet equipment GUI */
		int totalWidthInventoryEqAmulet = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentAmulet().getWidth();	
		int totalHeightInventoryEqAmulet = GameStates.inventoryDistToFrameY	* 2	+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentAmulet().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqAmulet = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqAmulet.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryEqAmulet, totalWidthInventoryEqAmulet },	new int[] { 0, totalHeightInventoryEqAmulet,totalHeightInventoryEqAmulet, 0 }, 4)));
		Space inventoryGUISpaceEqAmulet = new Space(inventoryGUISpacePolygonEqAmulet, 0, 0, "inventoryGUISpace",BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqAmuletGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentAmulet(),	"Player EqAmulet Inventory",GameStates.inventoryEqAmuletPlayerX,GameStates.inventoryEqAmuletPlayerY, "", 0,inventoryGUISpaceEqAmulet);
		ContainerGUIManager.add(playerInventoryEqAmuletGUI);
		
		/** Glove equipment GUI */
		int totalWidthInventoryEqGlove = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentGlove().getWidth();	
		int totalHeightInventoryEqGlove = GameStates.inventoryDistToFrameY	* 2	+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentGlove().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqGlove = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqGlove.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryEqGlove, totalWidthInventoryEqGlove },	new int[] { 0, totalHeightInventoryEqGlove,totalHeightInventoryEqGlove, 0 }, 4)));
		Space inventoryGUISpaceEqGlove = new Space(inventoryGUISpacePolygonEqGlove, 0, 0, "inventoryGUISpace",BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqGloveGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentGlove(),	"Player EqGlove Inventory",GameStates.inventoryEqGlovePlayerX,GameStates.inventoryEqGlovePlayerY, "", 0,inventoryGUISpaceEqGlove);
		ContainerGUIManager.add(playerInventoryEqGloveGUI);
		
		/** Shirt equipment GUI */
		int totalWidthInventoryEqShirt = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentShirt().getWidth();	
		int totalHeightInventoryEqShirt = GameStates.inventoryDistToFrameY	* 2	+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentShirt().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqShirt = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqShirt.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryEqShirt, totalWidthInventoryEqShirt },	new int[] { 0, totalHeightInventoryEqShirt,totalHeightInventoryEqShirt, 0 }, 4)));
		Space inventoryGUISpaceEqShirt = new Space(inventoryGUISpacePolygonEqShirt, 0, 0, "inventoryGUISpace",BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqShirtGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentShirt(),	"Player EqShirt Inventory",GameStates.inventoryEqShirtPlayerX,GameStates.inventoryEqShirtPlayerY, "", 0,inventoryGUISpaceEqShirt);
		ContainerGUIManager.add(playerInventoryEqShirtGUI);
		
		/** Trousers equipment GUI */
		int totalWidthInventoryEqTrousers = GameStates.inventoryDistToFrameX* 2+ (GameStates.inventorySlotDistX * 2 + GameStates.inventoryItemTileWidth)* getPlayerEntity().getEquipmentTrousers().getWidth();	
		int totalHeightInventoryEqTrousers = GameStates.inventoryDistToFrameY	* 2	+ (GameStates.inventorySlotDistY * 2+ GameStates.inventoryItemTileHeight + GameStates.inventoryFontDistanceY)* getPlayerEntity().getEquipmentTrousers().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqTrousers = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqTrousers.add((new Polygon(new int[] { 0, 0,	totalWidthInventoryEqTrousers, totalWidthInventoryEqTrousers },	new int[] { 0, totalHeightInventoryEqTrousers,totalHeightInventoryEqTrousers, 0 }, 4)));
		Space inventoryGUISpaceEqTrousers = new Space(inventoryGUISpacePolygonEqTrousers, 0, 0, "inventoryGUISpace",BigInteger.valueOf(0), containerColor, true, containerTransparency, 1.0f,	1.0f, "empty");

		ContainerGUI playerInventoryEqTrousersGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentTrousers(),	"Player EqTrousers Inventory",GameStates.inventoryEqTrousersPlayerX,GameStates.inventoryEqTrousersPlayerY, "", 0,inventoryGUISpaceEqTrousers);
		ContainerGUIManager.add(playerInventoryEqTrousersGUI);
	}
	
	public void restartAfterDeath() {
		/** init the playerEntity + set his hair to BlondHair*/
//		this.setPlayerEntity(new PlayerEntity(TileSetManager.manager.getTileSet(GameStates.nameStandardPlayer), GameStates.nameStandardPlayer, GameStates.roomStandardPlayer, BigInteger.valueOf(0), 0, 0, GameStates.animationDelay));
		this.getPlayerEntity().setX(0);
		this.getPlayerEntity().setY(0);
		this.getPlayerEntity().getAttributes()[AttributeType.LIFE.ordinal()] = this.getPlayerEntity().getAttributes()[AttributeType.MAXLIFE.ordinal()];
		this.getPlayerEntity().getAttributes()[AttributeType.ENERGY.ordinal()] = this.getPlayerEntity().getAttributes()[AttributeType.MAXENERGY.ordinal()];
		/** update server */
		for (Container playerEntityContainer: this.getPlayerEntity().getEquippedContainers()) {
			playerEntityContainer.reset();
			ClientMessageHandler.containerUpdateSendBrutal(playerEntityContainer);
		}
		playerEntity.emptyEquipment();
		playerEntity.resetInfluences();
		playerEntity.resetAttributes();
		this.initContainers();
//		GamePanel.gp.getPlayerEntity().calculateTotalEquipmentInfluenceAndUpdateVisuals();
	}
	
	/** init the ItemFactories */
	public void initItemFactories() {
		materialFactory = new MaterialFactory();
		foodFactory = new FoodFactory();
		weaponFactory = new WeaponFactory();
		bodyArmorFactory = new BodyArmorFactory();
		helmetFactory = new HelmetFactory();
		shieldFactory = new ShieldFactory();
		feetFactory = new FeetFactory();
		amuletFactory = new AmuletFactory();
		gloveFactory = new GloveFactory();
		trousersFactory = new TrousersFactory();
		shirtFactory = new ShirtFactory();
	}

	/** config can be started allways (also for reset) */
	public void configGUI() {
		Button cobbleButtonGUI = EditorGUIManager.get("floor").get(
				BigInteger.valueOf(0));
		cobbleButtonGUI.setInactive();
		EditorGUIManager.get("floor").deactivateOthers(cobbleButtonGUI);
		EditorGUIManager.get("floor").setStrValue("empty");
		EditorGUIManager.get("floor").setIntValue(0);

		Button playerMeGUIButton = PlayerListGUIManager.get("playerList").get(
				BigInteger.valueOf(0));
		playerMeGUIButton.setInactive();
		PlayerListGUIManager.get("playerList").deactivateOthers(
				playerMeGUIButton);
		PlayerListGUIManager.get("playerList").setStrValue("standard");
		PlayerListGUIManager.get("playerList").setIntValue(0);
	}

	public void initManagers() {
		/** clear the managers */
		LightManager.clear();
		ClientTextureManager.clear();

		lightTileDay = TileSetManager.manager.getTileSet(GameStates.lightTileSetNameDay, GameStates.lightTileWidth,	GameStates.lightTileHeight);
		lightTileNight = TileSetManager.manager.getTileSet(	GameStates.lightTileSetNameNight, GameStates.lightTileWidth,GameStates.lightTileHeight);
		int standardLight = GameStates.maxLightIntensityCutoff + 2;
//		Light light1 = new Light(BigInteger.valueOf(0), standardLight,	new Point(0, 0));
//		LightManager.add(light1);
//		Light light2 = new Light(BigInteger.valueOf(1), standardLight,	new Point(200, 300));
//		LightManager.add(light2);
		playerLight = new Light(BigInteger.valueOf(-1), standardLight, new Point(800,	200));
		LightManager.add(playerLight);
//		Light light4 = new Light(BigInteger.valueOf(3), standardLight,new Point(2000, 3000));
//		LightManager.add(light4);
//		Light light3 = new Light(BigInteger.valueOf(4), standardLight,new Point(200, 1000));
//		LightManager.add(light3);

		this.lightTile = null;
		if (GameWindow.gw.isNight())
			lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetNameNight,GameStates.lightTileWidth, GameStates.lightTileHeight);
		else lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetNameDay,GameStates.lightTileWidth, GameStates.lightTileHeight);
		
//		float[] tempAttributes = new float[AttributeType.values().length];
//		tempAttributes[AttributeType.MX.ordinal()] = 20.0f;
//		AreaInfluence areaInfluence1 = new AreaInfluence(BigInteger.valueOf(0),	System.currentTimeMillis(),System.currentTimeMillis() + 120000, new LocalObject(BigInteger.valueOf(0), "", this.getPlayerEntity().getRoom(), 0, 0, 500, 500, 0, 0, 0, 0),"movers", true, tempAttributes, Priority.LOW);
//
//		float[] tempAttributes2 = new float[AttributeType.values().length];
//		tempAttributes2[AttributeType.MX.ordinal()] = -80.0f;
//		AreaInfluence areaInfluence2 = new AreaInfluence(BigInteger.valueOf(1),	System.currentTimeMillis(),System.currentTimeMillis() + 120000,	new LocalObject(BigInteger.valueOf(0), "", this.getPlayerEntity().getRoom(), 250, 0, 500, 500, 0,0, 0, 0), "movers", true, tempAttributes2, Priority.LOW);
		// AreaInfluenceManager.add(areaInfluence2);

		/** add standard background texture */
		ClientTextureManager.manager.getTexture(GameStates.standardBackgroundTexture);
		
		/** init the mainmenu entity manager */
		MainMenuEntityManager.init();
		
		/** init the clouds/ weather effects */
//		OverlayEntityManager.init();
	}
	
	
	/** init the agentmanager later */
	public void initAgentManager() {
		/** add some agents to the Agentmanager */
		NormalAgent agent;
		AggressiveAgent aggressiveAgent;
		StupidAgent stupidAgent;
		SimpleAgent simpleAgent;
		BigInteger runningID = BigInteger.valueOf(0);
		TileSet tileSet_SA = TileSetManager.manager.getTileSet(GameStates.nameStandardPlayer);
//		TileSet tileSet_SA = new TileSet(GameStates.standardTileNamePlayer,
//				"standardPlayer", 32, 64);
		for (int i = 0; i < 0; i++) {
			float x = i*400 + (int) (Math.random() * 100) - 100;
			float y = i*200 + (int) (Math.random() * 200) - 100;
			agent = new NormalAgent(tileSet_SA, "normalAgent", this.getPlayerEntity().getRoom(), runningID, x, y,
					GameStates.animationDelay, true);
			/** increase ID, so the AgentManager can identify the agent */
			runningID = runningID.add(BigInteger.valueOf(1));
			x = 400 + (int) (Math.random() * 100) - 10;
			stupidAgent = new StupidAgent(tileSet_SA, "stupidAgent", this.getPlayerEntity().getRoom(), runningID,
					x, y, GameStates.animationDelay);
			runningID = runningID.add(BigInteger.valueOf(1));
			simpleAgent = new SimpleAgent(tileSet_SA, "simpleAgent", this.getPlayerEntity().getRoom(), runningID,
					x, y, GameStates.animationDelay);
			runningID = runningID.add(BigInteger.valueOf(1));
			aggressiveAgent = new AggressiveAgent(tileSet_SA, "aggressiveAgent",this.getPlayerEntity().getRoom(), runningID , x, y, GameStates.animationDelay, true);
			runningID = runningID.add(BigInteger.valueOf(1));

			Goal goal = new Goal(new WorldPosition(-100, 100)/*
															 * ,
															 * playerEntity.getId
															 * (), playerEntity
															 */);
			Goal additionalGoal = new Goal(new WorldPosition(400, 200));
			Goal additionalGoal0 = new Goal(new WorldPosition(100, 100));
			Goal additionalGoal01 = new Goal(new WorldPosition(300, 100));
			Goal additionalGoal02 = new Goal(new WorldPosition(100, 100));
			Goal additionalGoal03 = new Goal(new WorldPosition(200, 200));
			Goal additionalGoal2 = new Goal(new WorldPosition(-300, 300));
			Goal additionalGoal3 = new Goal(new WorldPosition(-200
					+ (int) (Math.random() * 2000) - 100, -100
					+ (int) (Math.random() * 2000) - 100));
			Goal additionalGoal4 = new Goal(new WorldPosition(0, 0));
			Goal additionalGoal5 = new Goal(new WorldPosition(-200
					+ (int) (Math.random() * 2000) - 100, -100
					+ (int) (Math.random() * 2000) - 100));
			Goal entityGoal = new Goal(playerEntity);
			
			Goal aggressiveAgentGoal = new Goal(playerEntity);
			aggressiveAgentGoal.setAttackModus(true);
			
			// Goal playerEntityPosition = new Goal(new
			// WorldPosition(playerEntity.getX(),playerEntity.getY()), entityID,
			// entity)
			agent.setActualGoal(goal);
			agent.addGoal(goal);
			agent.addGoal(additionalGoal);
			// agent.addGoal(additionalGoal0);
			// agent.addGoal(additionalGoal01);
			// agent.addGoal(additionalGoal02);
			// agent.addGoal(additionalGoal03);
			// agent.addGoal(additionalGoal2);
			// agent.addGoal(additionalGoal3);
			// agent.addGoal(additionalGoal4);
			agent.addGoal(additionalGoal5);
			agent.addGoal(entityGoal);
			// stupidAgent.setActualGoal(goal);
			// stupidAgent.addGoal(goal);
			// stupidAgent.addGoal(additionalGoal);
			// stupidAgent.addGoal(additionalGoal2);
			// stupidAgent.addGoal(additionalGoal3);
			// simpleAgent.setActualGoal(goal);
			// simpleAgent.addGoal(goal);
			// simpleAgent.addGoal(additionalGoal);
			// simpleAgent.addGoal(additionalGoal2);
			// simpleAgent.addGoal(additionalGoal3);

			aggressiveAgent.setActualGoal(aggressiveAgentGoal);
//			aggressiveAgent.addGoal(aggressiveAgentGoal);
			
//			AgentManager.add(agent);
			AgentManager.add(aggressiveAgent);
			// AgentManager.add(stupidAgent);
			// AgentManager.add(simpleAgent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 *
	 * Paints all important Objects to the GamePanel depends on the GameModus
	 */
	public void paintComponent(Graphics2D g) {
//		Graphics2D g = (Graphics2D) g2;
//		Graphics2D g = (Graphics2D) GameWindow.gw.getStrategy().getDrawGraphics();
		/** update worldTime */
		worldTime += (System.currentTimeMillis()-systemNow);
		systemNow = System.currentTimeMillis();
		twentyfourTime = (worldTime) % millis;
		twentyfourFaster = (long) (twentyfourTime * factorSeconds);
		piTime = ((double)(twentyfourFaster)/millis)*Math.PI*2; 
		lightIntensity = (Math.sin(piTime)+1)/2;
//		System.out.println("synchronized time = "+worldTime + " converted to: "+twentyfourTime+" faster: "+twentyfourFaster+" light="+lightIntensity);
		
		if (GameWindow.gw.isUsingLight()) {
			determineDayNightLight();
		} else {
			GameWindow.gw.setLight(false);
		}
		
		// Set Options of the Graphics-Object
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//		super.paintComponent(g);

		// Paint in each GameModus
		if (GameModus.modus == GameModus.MAINMENU) {
			mainMenuPaint(g);
			GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.MAINMENU);
		} else if (GameModus.modus == GameModus.LOADING) {
			loadingPaint(g);
			// GamePanel.gp.loadEntityList();
		} else if (GameModus.modus == GameModus.GAME) {
			
			gamePaint(g);
//			loadingScreenPaint(g);

		}

//		g.dispose();
	}

	/**
	 * @param g
	 *            : Paint Objects to g
	 * @START-Modus
	 */
	public void mainMenuPaint(Graphics2D g) {
		float tempZoom = 1.0f;
		
		
		if (playerEntity != null) {
			// System.out.println(playerEntity.getName());
			GamePanel.gp.setViewPoint((GameStates.getWidth() / 2)-(int) (GameStates.getWidth() / 2 / tempZoom),	(GameStates.getHeight()/2)-(int) (GameStates.getHeight() / 2 / tempZoom));		
		}
		
		/** Paint Agents */
		g.setPaintMode();
		List<BigInteger> idListTempAgents = new ArrayList<BigInteger>(
				MainMenuEntityManager.idList);
		for (BigInteger i : idListTempAgents) {
			Entity e  = MainMenuEntityManager.get(i);
//			tempZoom =4* Math.abs(e.getY())/GameStates.height;
//			g.scale(tempZoom, tempZoom);
			
			if (e != null) {
				if (e.getSprite().getImage() != null) {
					g.drawImage(e.getSprite().getImage(),
							(int) (e.getX()-viewPointX),
							(int) (e.getY()-viewPointY), null);
				}
			}
//			g.scale(1/tempZoom, 1/tempZoom);
//			tempZoom = 1.0f;
			
		}
//		g = (Graphics2D) g.create();
		
//		g.setPaintMode();
//		// startButton.paint(g, 0, 0);
//		// simulationButton.paint(g, 0, 0);
		connectButton.paint(g, 0, 0);
		
	}

	/**
	 * @param g
	 *            : Paint Objects to g
	 * @GAME-Modus
	 */
	public void gamePaint(Graphics2D g) {
		long timeStamp = System.currentTimeMillis();
		long countDraw = 0;
		long countDrawAll = 0;
		
//		Graphics2D g = (Graphics2D) GameWindow.gw.getStrategy().getDrawGraphics();
		boolean paintUnderground = true;
		boolean paintFloor = true;
		boolean paintObjects = true;
		boolean showTimeStamps = false;
		
		
		
		String room = this.getPlayerEntity().getRoom();
		
		duration = System.currentTimeMillis() - oldTime;
		wholeDuration += duration;
		oldTime = System.currentTimeMillis();
		countFrames++;
		if (wholeDuration >= 1000) {
			// GameWindow.gw.gameInfoConsole.appendInfo("Framerate: "+countFrames);
			System.out.println("Framerate: " + countFrames);
			countFrames = 0;
			wholeDuration = 0;
		}

		g.scale(this.getZoomFactor(), this.getZoomFactor());
		
		
		int totalX = GameStates.mapTotalWidth * GamePanel.gp.getMiniMap().getWidth();
		int totalY = GameStates.mapTotalHeight * GamePanel.gp.getMiniMap().getHeight();
		if (playerEntity != null) {
			
			
			Point moduloPointPlayer = new Point( ( (int) GamePanel.gp.getPlayerEntity().getX() % totalX+totalX) % totalX , ((int) GamePanel.gp.getPlayerEntity().getY() % totalY+totalY) % totalY);
			GamePanel.gp.getPlayerEntity().setX(moduloPointPlayer.x);
			GamePanel.gp.getPlayerEntity().setY(moduloPointPlayer.y);
			
			// System.out.println(playerEntity.getName());
			GamePanel.gp.setViewPoint((int) playerEntity.getX()	+ (int) (playerEntity.getWidth() / 2)- (int) (GameStates.getWidth() / 2 / zoomFactor),	(int) playerEntity.getY()+ (int) (playerEntity.getHeight() / 2)	- (int) (GameStates.getHeight() / 2 / zoomFactor));		
		}
		Point oldViewPoint = new Point(this.getViewPointX(),this.getViewPointY());
		float tempZoom = GamePanel.gp.getZoomFactor();

//		/** Paint Spaces */
//		g.setPaintMode();
//		List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(
//				SpaceManager.idList);
//		for (BigInteger i : idListTempSpaces) {
//			Space space = SpaceManager.get(i);
//			if (space != null) {
//				if (space.getPolygon() != null) {
//					// space.paint(g, (int) (-viewPointX),(int) (-viewPointY));
//				}
//			} else
////				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Space");
//				System.out.println("I got a null space");
//		}
		
		

//		int latticePointXUnderground = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+GameStates.ugrMapTotalWidth/2) / (GameStates.ugrMapTotalWidth)) * GameStates.ugrMapTotalWidth;
//		int latticePointYUnderground = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+GameStates.ugrMapTotalHeight/2) / (GameStates.ugrMapTotalHeight)) * GameStates.ugrMapTotalHeight;
//		int minXUnderground = latticePointXUnderground-((int)GameStates.factorOfViewDeleteDistance*GameStates.ugrMapTotalWidth);
//		int maxXUnderground = latticePointXUnderground+((int)GameStates.factorOfViewDeleteDistance*GameStates.ugrMapTotalWidth);
//		int minYUnderground = latticePointYUnderground-((int)GameStates.factorOfViewDeleteDistance*GameStates.ugrMapTotalHeight);
//		int maxYUnderground = latticePointYUnderground+((int)GameStates.factorOfViewDeleteDistance*GameStates.ugrMapTotalHeight);
//		
//		Rectangle rectUnderground = new Rectangle(minXUnderground,	minYUnderground, maxXUnderground - minXUnderground-1,	maxYUnderground - minYUnderground-1);
		/** define min and max for view distance */
		int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
		int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
		int latticePointX = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidth/2) / (localWidth)) * localWidth;
		int latticePointY = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeight/2) / (localHeight)) * localHeight;
		
		/** latticePointX and Y is the corresponding origin of the main characters position on the lattice*/
		int minX = latticePointX-((int)GameStates.factorOfViewDeleteDistance*localWidth);
		int maxX = latticePointX+((int)GameStates.factorOfViewDeleteDistance*localWidth);
		int minY = latticePointY-((int)GameStates.factorOfViewDeleteDistance*localHeight);
		int maxY = latticePointY+((int)GameStates.factorOfViewDeleteDistance*localHeight);
		Rectangle rect = new Rectangle(minX, minY, maxX - minX-1, maxY - minY-1);
		
		/** for repetitive maps */ 
		/** latticePointX and Y is the corresponding origin of the main characters position on the lattice*/
		/** moduloX is in [0,(miniMapWidth-1)*1024] */
		/** nextToModZero is in [-(miniMapWidth)*1024,0] */
		int moduloX = ((minX/GameStates.mapTotalWidth)%GamePanel.gp.getMiniMap().getWidth()+GamePanel.gp.getMiniMap().getWidth())%GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth;
		int nextToModZeroX = minX - moduloX;
		int moduloY = ((minY/GameStates.mapTotalHeight)%GamePanel.gp.getMiniMap().getHeight()+GamePanel.gp.getMiniMap().getHeight())%GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight;
		int nextToModZeroY = minY - moduloY;
		
		Rectangle rectWindowUnderground = new Rectangle((int) (-GameStates.ugrMapTileSetWidth*GameStates.standardZoomFactor),(int) (-GameStates.ugrMapTileSetHeight*GameStates.standardZoomFactor), (int) (GameStates.ugrMapTileSetWidth*GameStates.standardZoomFactor + ((float)GameStates.width + (float) Math.ceil((float)GameStates.ugrMapTileSetWidth*GameStates.standardZoomFactor))/this.getZoomFactor()),(int)(GameStates.ugrMapTileSetWidth*GameStates.standardZoomFactor + ((float) GameStates.height+ (float) Math.ceil((float) GameStates.ugrMapTileSetHeight*GameStates.standardZoomFactor))/this.getZoomFactor()));
		Rectangle rectWindow = new Rectangle((int) (-GameStates.mapTileSetWidth*GameStates.standardZoomFactor),(int) (-GameStates.mapTileSetHeight*GameStates.standardZoomFactor),(int) (GameStates.mapTileSetWidth*GameStates.standardZoomFactor+ (GameStates.width+(GameStates.mapTileSetWidth*GameStates.standardZoomFactor))/this.getZoomFactor()), (int) (GameStates.mapTileSetHeight*GameStates.standardZoomFactor + (GameStates.height +(GameStates.mapTileSetHeight*GameStates.standardZoomFactor))/this.getZoomFactor()));
//		Rectangle rectWindowUnderground = new Rectangle((int) (-GameStates.ugrMapTileSetWidth*GameStates.standardZoomFactor),(int) (-GameStates.ugrMapTileSetHeight*GameStates.standardZoomFactor),(int) (((GameStates.width + GameStates.ugrMapTileSetWidth)*GameStates.standardZoomFactor)/this.getZoomFactor()),(int)( ((GameStates.height + GameStates.ugrMapTileSetHeight)*GameStates.standardZoomFactor)/this.getZoomFactor()));
//		Rectangle rectWindow = new Rectangle((int) (-GameStates.mapTileSetWidth*GameStates.standardZoomFactor),(int) (-GameStates.mapTileSetHeight*GameStates.standardZoomFactor),(int) ((GameStates.width+(GameStates.mapTileSetWidth*GameStates.standardZoomFactor))/this.getZoomFactor()), (int) ((GameStates.height +(GameStates.mapTileSetHeight*GameStates.standardZoomFactor))/this.getZoomFactor()));
//		Rectangle rectWindowUnderground = new Rectangle((int) (-GameStates.ugrMapTileSetWidth*GameStates.standardZoomFactor/getZoomFactor()),(int) (-GameStates.ugrMapTileSetHeight*GameStates.standardZoomFactor/getZoomFactor()),(int) ((GameStates.width + Math.ceil((float)GameStates.ugrMapTileSetWidth*GameStates.standardZoomFactor))/this.getZoomFactor()),(int)( (GameStates.height+Math.ceil((float)GameStates.ugrMapTileSetHeight*GameStates.standardZoomFactor))/this.getZoomFactor()));
//		Rectangle rectWindow = new Rectangle((int) (-GameStates.mapTileSetWidth*GameStates.standardZoomFactor),(int) (-GameStates.mapTileSetHeight*GameStates.standardZoomFactor),(int) ((GameStates.width+(GameStates.mapTileSetWidth*GameStates.standardZoomFactor))/this.getZoomFactor()), (int) ((GameStates.height +(GameStates.mapTileSetHeight*GameStates.standardZoomFactor))/this.getZoomFactor()));
		Point tilePosition = null;
		
		
		/** Paint Maps */
		g.setPaintMode();
		UndergroundMapManager undergroundManager = GameWindow.gw.undergroundMapManagers.get("underground");
		List<Point> idListUndergroundMap = new ArrayList<Point>(undergroundManager.pointList);
		for (Point p : idListUndergroundMap) {
			LocalUndergroundMap localMap = undergroundManager.get(p);
			if (localMap != null) {
				
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;
				
//				if (rectUnderground.contains(p)) {
					BufferedImage tile = null;
					int tileInt = -1;
					tilePosition = null;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							/** only load if new tile */
							if (localMap.getLocalMap()[k][l] != tileInt) {
								tile = localMap.getTileImage(k, l, GameWindow.gw.getTileUndergroundMapManager());
								tileInt = localMap.getLocalMap()[k][l];
							}
							tilePosition = new Point( (shiftedPoint.x + k * GameStates.ugrMapTileSetWidth - viewPointX), (shiftedPoint.y + l * GameStates.ugrMapTileSetHeight - viewPointY));
							
							if (tile != null && rectWindowUnderground.contains(tilePosition)) {
								if (paintUnderground)
								{
//									GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
									g.drawImage(tile,tilePosition.x,tilePosition.y,	null);
									countDraw++;
//									System.out.println("is image accelerated? -"+tile.getCapabilities(gc).isAccelerated());
									
								}
							}
						}
					}
				} else {
					/** remove if too far away */
					/**
					 * but only if the localMap is not on the stayList (which is
					 * used for distant maps)
					 */
					if (!undergroundManager.getStayList().contains(p))
						undergroundManager.remove(p);
				}
			} else {
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map underground");
				System.out.println("null map underground");
				undergroundManager.remove(p);
			}

		}

		if (showTimeStamps) {
			timeStamp = System.currentTimeMillis()-timeStamp;
			System.out.println("underground: "+timeStamp + " for "+countDraw+" objects");
			countDrawAll+=countDraw;
			countDraw=0;
			timeStamp = System.currentTimeMillis();
		}
		
		/** Paint Maps */
		g.setPaintMode();
		MapManager cobbleManager = GameWindow.gw.mapManagers.get("cobble");
		List<Point> idListcobbleMap = new ArrayList<Point>(
				cobbleManager.pointList);
		for (Point p : idListcobbleMap) {
			LocalMap localMap = cobbleManager.get(p);
			if (localMap != null) {
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;
				
					BufferedImage tile = null;
					tilePosition = null;
					int tileInt = -1;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							/** only load if new tile */
							if (localMap.getLocalMap()[k][l] != tileInt) {
								tile = localMap.getTileImage(k, l,GameWindow.gw.getTileMapManager());
								tileInt = localMap.getLocalMap()[k][l];
							}
							tilePosition = new Point( (shiftedPoint.x + k * GameStates.mapTileSetWidth - viewPointX),(shiftedPoint.y + l * GameStates.mapTileSetHeight - viewPointY));
							if (tile != null &&  rectWindow.contains(tilePosition)) {	
								if (paintFloor) {
									g.drawImage(tile,tilePosition.x,tilePosition.y,	null);
									countDraw++;
								}
								
								/** check if there is a light source at this light tile */
								if (localMap.getLocalMap()[k][l] == GameStates.lightTile) {
//									System.out.println("painted light tile ");
									if (!LightManager.hasLightAtPosition(tilePosition.x+viewPointX, tilePosition.y+viewPointY, totalX, totalY)) {
										/** create the light */
										
										int standardLight = GameStates.maxLightIntensityCutoff + 2;
										BigInteger id = LightManager.maxIDValue;
										id = id.add(BigInteger.ONE);
										Light light1 = new Light(id, standardLight,	new Point(tilePosition.x+viewPointX, tilePosition.y+viewPointY));
										LightManager.add(light1);
									} else {
										// nothing, there is already one light at this point!
									}	
								}
							}
						}
					}
				} else {
					
					/** remove if too far away */
					/**
					 * but only if the localMap is not on the stayList (which is
					 * used for distant maps)
					 */
					if (!cobbleManager.getStayList().contains(p))
						cobbleManager.remove(p);
				}
			} else {
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map cobble");
				System.out.println("null map cobble");
				cobbleManager.remove(p);
			}
		}

		MapManager grassManager = GameWindow.gw.mapManagers.get("grass");
		List<Point> idListGrassMap = new ArrayList<Point>(grassManager.pointList);
		for (Point p : idListGrassMap) {
			LocalMap localMap = grassManager.get(p);
			if (localMap != null) {
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;
					
					BufferedImage tile = null;
					tilePosition = null;
					int tileInt = -1;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							/** only load if new tile */
							if (localMap.getLocalMap()[k][l] != tileInt) {
								tile = localMap.getTileImage(k, l,GameWindow.gw.getTileMapManager());
								tileInt = localMap.getLocalMap()[k][l];
							}
							tilePosition = new Point( (shiftedPoint.x  + k * GameStates.mapTileSetWidth - viewPointX),(shiftedPoint.y + l * GameStates.mapTileSetHeight - viewPointY));
							if (tile != null && rectWindow.contains(tilePosition)) {	
								if (paintFloor) {
									g.drawImage(tile,tilePosition.x,tilePosition.y,	null);
									countDraw++;
								}
							}
						}
					}
				} else {
					/** remove if too far away */
					/**
					 * but only if the localMap is not on the stayList (which is
					 * used for distant maps)
					 */
					if (!grassManager.getStayList().contains(p))
						grassManager.remove(p);
				}
			} else {
				System.out.println("I got a NULL Map grass");
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map grass");
				grassManager.remove(p);
			}
		}


//		g.scale(3.0f, 3.0f);
		MapManager holeManager = GameWindow.gw.mapManagers.get("hole");
		List<Point> idListholeMap = new ArrayList<Point>(holeManager.pointList);
		for (Point p : idListholeMap) {
			LocalMap localMap = holeManager.get(p);
			if (localMap != null) {
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;

					BufferedImage tile = null;
					tilePosition = null;
					int tileInt = -1;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							/** only load if new tile */
							if (localMap.getLocalMap()[k][l] != tileInt) {
								tile = localMap.getTileImage(k, l,GameWindow.gw.getTileMapManager());
								tileInt = localMap.getLocalMap()[k][l];
							}
							tilePosition = new Point( (shiftedPoint.x + k * GameStates.mapTileSetWidth - viewPointX),(shiftedPoint.y + l * GameStates.mapTileSetHeight - viewPointY));
							if (tile != null && rectWindow.contains(tilePosition)) {	
								if (paintFloor) {
									g.drawImage(tile,tilePosition.x,tilePosition.y,	null);
									countDraw++;	
								}
							}
						}
					}
				} else {
					/** remove if too far away */
					/**
					 * but only if the localMap is not on the stayList (which is
					 * used for distant maps)
					 */
					if (!holeManager.getStayList().contains(p))
						holeManager.remove(p);
				}
			} else {
				System.out.println("I got a NULL Map hole");
				holeManager.remove(p);
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map hole");
//				desertManager.remove(p);
			}
		}
		
		if (showTimeStamps) {
			timeStamp = System.currentTimeMillis()-timeStamp;
			System.out.println("maps: "+timeStamp +" for "+countDraw + " objects");
			countDrawAll += countDraw;
			countDraw = 0;
			timeStamp = System.currentTimeMillis();
		}
		
		/** Paint Trees */
		g.setPaintMode();
		ObjectMapManager tree1MapManager = GameWindow.gw.objectMapManagers.get("tree1");
		ObjectMapManager tree2MapManager = GameWindow.gw.objectMapManagers.get("tree2");
		ArrayList<Point> idListTree1Map = new ArrayList<Point>(	tree1MapManager.pointList);
		ArrayList<Point> idListTree2Map = new ArrayList<Point>(	tree2MapManager.pointList);
		ArrayList<Point> pointSetTree = MyUtil.unionListNoDuplicates(idListTree1Map, idListTree2Map);
		for (Point p : pointSetTree) {
			LocalMap localMap1 = tree1MapManager.get(p);
			LocalMap localMap2 = tree2MapManager.get(p);
			/** paint both tree parts simultaneously */
			if (localMap1 != null && localMap2 != null) {
				
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;
				
				
//				if (rect.contains(p)) {
					BufferedImage tile1 = null;
					BufferedImage tile2 = null;
					
					for (int k = 0; k < localMap1.getLocalMap().length; k++) {
						for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
							int tileType1 = LocalMap.getPaintType(localMap1.getLocalMap()[k][l]);
							int tileType2 = LocalMap.getPaintType(localMap2.getLocalMap()[k][l]);
							tile1 = localMap1.getTileImage(k, l,GameWindow.gw.getTileMapManager());
							tile2 = localMap2.getTileImage(k, l,GameWindow.gw.getTileMapManager());
							tilePosition = new Point((shiftedPoint.x + k * GameStates.mapTileSetWidth - viewPointX), (shiftedPoint.y + l * GameStates.mapTileSetHeight - viewPointY));
//							tilePosition = new Point(tilePosition.x - (tilePosition.x % GameStates.lightTileWidth), tilePosition.y - (tilePosition.y % GameStates.lightTileHeight));
							
							
							if ((tile1 != null) && (LocalMap.getTileType(tileType1) != TileType.HOLE) && (LocalMap.getTileType(tileType1) != TileType.WATER)&& rectWindow.contains(tilePosition)) {
								if (paintObjects) {								
									g.drawImage(tile1,tilePosition.x,tilePosition.y,null);
									countDraw++;
								}
									
								/** check if there is a light source at this light tile */
								if (LocalMap.isLighning(localMap1.getLocalMap()[k][l])) {
//									System.out.println("painted light tile ");
									Point lightPosition = new Point(p.x+ k * GameStates.mapTileSetWidth, p.y+ l * GameStates.mapTileSetHeight );
									if (!LightManager.hasLightAtPosition(lightPosition.x, lightPosition.y, totalX, totalY)) {
										/** create the light */
										int standardLight = GameStates.maxLightIntensityCutoff + 2;
										BigInteger id = LightManager.maxIDValue;
										id = id.add(BigInteger.ONE);
										Light light1 = new Light(id, standardLight,	new Point(lightPosition.x, lightPosition.y));
										LightManager.add(light1);
//										System.out.println("lights: "+LightManager.size());
//										for (Light light: LightManager.lightList.values() ) {
//											System.out.println("light at "+light.getLocation().x+"|"+light.getLocation().y);
//										}
//										for (Light light: LightManager.lightList.values()) {
//											System.out.println("light: "+light.getLocation().x+"|"+light.getLocation().y);
//										}
										
										/** add burning flame if the tile is burnning */
										if (LocalMap.isBurning(localMap1.getLocalMap()[k][l])) {
											TileSet fireTileSet = TileSetManager.manager.getTileSet(GameStates.fireTileSetName);
											Entity entity = new Entity(fireTileSet, "campfire", GamePanel.gp.getPlayerEntity().getRoom(), id,light1.getLocation().x+GameStates.mapTileSetWidth/2, light1.getLocation().y+GameStates.mapTileSetHeight/2, GameStates.animationDelayFire); 
											entity.setX(entity.getX()-entity.getWidth()/2);
											entity.setY(entity.getY()-entity.getHeight()+10);
											entity.setMovement(0, 0);
											entity.setContinuousState(EntityStates.FIRE);
											entity.determineAnimation();
											EntityManager.add(entity);
										}
									} else {
										// nothing, there is already one light at this point!
									}	
								}
							}
							if (tile2 != null && (LocalMap.getTileType(tileType2) != TileType.HOLE) && (LocalMap.getTileType(tileType2) != TileType.WATER)&& rectWindow.contains(tilePosition) ) {
								if (paintObjects)
								g.drawImage(tile2,tilePosition.x,tilePosition.y, null);
								
								/** check if there is a light source at this light tile */
								if (LocalMap.isLighning(localMap2.getLocalMap()[k][l])) {
//									System.out.println("painted light tile ");
									Point lightPosition = new Point(p.x+ k * GameStates.mapTileSetWidth, p.y+ l * GameStates.mapTileSetHeight );
									if (!LightManager.hasLightAtPosition(lightPosition.x, lightPosition.y, totalX, totalY)) {
										/** create the light */
										int standardLight = GameStates.maxLightIntensityCutoff + 2;
										BigInteger id = LightManager.maxIDValue;
										id = id.add(BigInteger.ONE);
										Light light1 = new Light(id, standardLight,	new Point(lightPosition.x, lightPosition.y));
										LightManager.add(light1);
//										System.out.println("lights: "+LightManager.size());
//										for (Light light: LightManager.lightList.values() ) {
//											System.out.println("light at "+light.getLocation().x+"|"+light.getLocation().y);
//										}
										/** add fire */
										if (LocalMap.isBurning(localMap2.getLocalMap()[k][l])) {
											TileSet fireTileSet = TileSetManager.manager.getTileSet(GameStates.fireTileSetName);
											Entity entity = new Entity(fireTileSet, "campfire", GamePanel.gp.getPlayerEntity().getRoom(), id,light1.getLocation().x+GameStates.mapTileSetWidth/2, light1.getLocation().y+GameStates.mapTileSetHeight/2, GameStates.animationDelayFire); 
											entity.setX(entity.getX()-entity.getWidth()/2);
											entity.setY(entity.getY()-entity.getHeight()+10);
											entity.setMovement(0, 0);
											entity.setContinuousState(EntityStates.FIRE);
											entity.determineAnimation();
											EntityManager.add(entity);
										}
									} else {
										// nothing, there is already one light at this point!
									}	
								}
							}
						}
					}
				} else {
					/** remove if too far away */
					/**
					 * but only if the localMap is not on the stayList (which is
					 * used for distant maps)
					 */
					if (!tree1MapManager.getStayList().contains(p))
						tree1MapManager.remove(p);
					if (!tree2MapManager.getStayList().contains(p))
						tree2MapManager.remove(p);
				}
			} else if (localMap1 != null) {
				
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;
				
//				if (rect.contains(p)) {
					BufferedImage tile1 = null;
					for (int k = 0; k < localMap1.getLocalMap().length; k++) {
						for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
							int tileType1 = LocalMap.getPaintType(localMap1.getLocalMap()[k][l]);
							tile1 = localMap1.getTileImage(k, l,GameWindow.gw.getTileMapManager());
							tilePosition = new Point((shiftedPoint.x + k * 32 - viewPointX),(shiftedPoint.y + l * 32 - viewPointY) );
							if (tile1 != null && (LocalMap.getTileType(tileType1) != TileType.HOLE) && (LocalMap.getTileType(tileType1) != TileType.WATER)&& rectWindow.contains(tilePosition) ) {
								if (paintObjects) {
									g.drawImage(tile1,tilePosition.x,tilePosition.y,null);
									countDraw ++;
								}
								
								/** check if there is a light source at this light tile */
								if (LocalMap.isLighning(localMap1.getLocalMap()[k][l])) {
//									System.out.println("painted light tile ");
									Point lightPosition = new Point(p.x+ k * GameStates.mapTileSetWidth, p.y+ l * GameStates.mapTileSetHeight );
									if (!LightManager.hasLightAtPosition(lightPosition.x, lightPosition.y, totalX, totalY)) {
										/** create the light */
										int standardLight = GameStates.maxLightIntensityCutoff + 2;
										BigInteger id = LightManager.maxIDValue;
										id = id.add(BigInteger.ONE);
										Light light1 = new Light(id, standardLight,	new Point(lightPosition.x, lightPosition.y));
										LightManager.add(light1);
										/** add fire */
										if (LocalMap.isBurning(localMap1.getLocalMap()[k][l])) {
											TileSet fireTileSet = TileSetManager.manager.getTileSet(GameStates.fireTileSetName);
											Entity entity = new Entity(fireTileSet, "campfire", GamePanel.gp.getPlayerEntity().getRoom(), id,light1.getLocation().x+GameStates.mapTileSetWidth/2, light1.getLocation().y+GameStates.mapTileSetHeight/2, GameStates.animationDelayFire); 
											entity.setX(entity.getX()-entity.getWidth()/2);
											entity.setY(entity.getY()-entity.getHeight()+10);
											entity.setMovement(0, 0);
											entity.setContinuousState(EntityStates.FIRE);
											entity.determineAnimation();
											EntityManager.add(entity);
										}
									} else {
										// nothing, there is already one light at this point!
									}	
								}
							}
						}
					}
				} else {
					/** remove if too far away */
					/**
					 * but only if the localMap is not on the stayList (which is
					 * used for distant maps)
					 */
					if (!tree1MapManager.getStayList().contains(p))
						tree1MapManager.remove(p);
				}
				/** remove the null object in the other manager */
				tree2MapManager.remove(p);
			} else if (localMap2 != null) {
				
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;
				
//				if (rect.contains(p)) {
					BufferedImage tile2 = null;
					for (int k = 0; k < localMap2.getLocalMap().length; k++) {
						for (int l = 0; l < localMap2.getLocalMap()[0].length; l++) {
							int tileType2 = LocalMap.getPaintType(localMap2.getLocalMap()[k][l]);
							tile2 = localMap2.getTileImage(k, l,GameWindow.gw.getTileMapManager());
							tilePosition = new Point((shiftedPoint.x + k * 32 - viewPointX), (shiftedPoint.y + l * 32 - viewPointY));
							
							if (tile2 != null && (LocalMap.getTileType(tileType2) != TileType.HOLE)&& (LocalMap.getTileType(tileType2) != TileType.WATER) && rectWindow.contains(tilePosition)) {
								if (paintObjects) {
									g.drawImage(tile2,tilePosition.x,tilePosition.y, null);
									countDraw++;
								}
								
								/** check if there is a light source at this light tile */
								if (LocalMap.isLighning(localMap2.getLocalMap()[k][l])) {
//									System.out.println("painted light tile ");
									Point lightPosition = new Point(p.x+ k * GameStates.mapTileSetWidth, p.y+ l * GameStates.mapTileSetHeight );
									if (!LightManager.hasLightAtPosition(lightPosition.x, lightPosition.y, totalX, totalY)) {
										/** create the light */
										int standardLight = GameStates.maxLightIntensityCutoff + 2;
										BigInteger id = LightManager.maxIDValue;
										id = id.add(BigInteger.ONE);
										Light light1 = new Light(id, standardLight,	new Point(lightPosition.x, lightPosition.y));
										LightManager.add(light1);
										/** add fire */
										if (LocalMap.isBurning(localMap2.getLocalMap()[k][l])) {
											TileSet fireTileSet = TileSetManager.manager.getTileSet(GameStates.fireTileSetName);
											Entity entity = new Entity(fireTileSet, "campfire", GamePanel.gp.getPlayerEntity().getRoom(), id,light1.getLocation().x+GameStates.mapTileSetWidth/2, light1.getLocation().y+GameStates.mapTileSetHeight/2, GameStates.animationDelayFire); 
											entity.setX(entity.getX()-entity.getWidth()/2);
											entity.setY(entity.getY()-entity.getHeight()+10);
											entity.setMovement(0, 0);
											entity.setContinuousState(EntityStates.FIRE);
											entity.determineAnimation();
											EntityManager.add(entity);
										}
									} else {
										// nothing, there is already one light at this point!
									}	
								}
							}
						}
					}
				} else {
					/** remove if too far away */
					/**
					 * but only if the localMap is not on the stayList (which is
					 * used for distant maps)
					 */
					if (!tree2MapManager.getStayList().contains(p))
						tree2MapManager.remove(p);
				}
				
				/** remove the null object in the other manager */
				tree1MapManager.remove(p);
			} else { 
				System.out.println("I got a NULL Map all localMaps are null");
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map overlay all localMaps are null");
				tree1MapManager.remove(p);
				tree2MapManager.remove(p);
			}
		}
		
		if (showTimeStamps) {
			timeStamp = System.currentTimeMillis()-timeStamp;
			System.out.println("trees: "+timeStamp + " for "+countDraw+" objects");
			countDrawAll+=countDraw;
			countDraw=0;
			timeStamp = System.currentTimeMillis();
		}
		/** Paint Items */
		g.setPaintMode();
		List<BigInteger> idListTempItems = new ArrayList<BigInteger>(
				ItemManager.idList);
		for (BigInteger i : idListTempItems) {
			Item tempItem = ItemManager.get(i);
			if (tempItem != null )  {				
				if (tempItem.isVisible() && room.equals(tempItem.getRoom())) {
					Entity e = tempItem.getEntity();
					Point p = new Point((int) e.getX(),(int)  e.getY());
					Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
					Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
					Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					boolean shift1 = rect.contains(shiftedPoint);
					boolean shift2 = rect.contains(shiftedPoint2);
					boolean shift3 = rect.contains(shiftedPoint3);
					boolean shift4 = rect.contains(shiftedPoint4); 
					
					if ( shift1|| shift2 || shift3 || shift4) {
						if (shift2) shiftedPoint = shiftedPoint2;
						else if (shift3) shiftedPoint = shiftedPoint3;
						else if (shift4) shiftedPoint = shiftedPoint4;
					
						if (e != null ){//&& rectWindow.contains(new Point((int) e.getX()- viewPointX,(int) e.getY()- viewPointY))) {
							if (e.getSprite().getImage() != null) {
								g.drawImage(e.getSprite().getImage(),
										(int) (shiftedPoint.x - viewPointX),
										(int) (shiftedPoint.y - viewPointY), null);
								countDraw++;
							}
						}
					}
					
				} else {
					// not visible to this player
				}
//					GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Item");
			} else {
				System.out.println("i got a null item");
//				ItemManager.remove(i);
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Item");
			}
		}
		
		/** paint the entities */
		g.setPaintMode();
		if (EntityManager.size() > 0) {
			List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(EntityManager.idList);
			boolean anyFire = false;
			BigInteger delID = null;
			for (BigInteger i : idListTempPlayers) {
				Entity e = EntityManager.get(i);
				if (e != null && room.equals(e.getRoom())&& rectWindow.contains(new Point( (int) e.getX()- viewPointX , (int) e.getY()- viewPointY ))) {
					if (e != null && rectWindow.contains(new Point( (int) e.getX()- viewPointX, (int) e.getY() - viewPointY))) {
						if (e.getSprite().getImage() != null) {
							g.drawImage(e.getSprite().getImage(),
									(int) (e.getX() - viewPointX),
									(int) (e.getY() - viewPointY), null);
							countDraw++;
							if (e.getName().equals("campfire")) {
								if (LightManager.idList.contains(i)) ;else delID = i;
								anyFire = true;
							}
						}
					} else
						System.out.println("null entity from entityManager");
				}

			}
			
			/** delete those campfires that have no lightsource */
			if (delID != null) EntityManager.remove(delID);
			
			if (anyFire) {
				GameWindow.gw.sound.startMusic(MusicType.CAMPFIRE, MusicName.CAMPFIRE);
			} else {
				GameWindow.gw.sound.startMusic(MusicType.CAMPFIRE, MusicName.EMPTY);
			}
		}

		/** Paint Agents */
		g.setPaintMode();
		List<BigInteger> idListTempAgents = new ArrayList<BigInteger>(AgentManager.idList);
		for (BigInteger i : idListTempAgents) {
			Agent tempAgent = AgentManager.get(i);
			if (tempAgent != null && tempAgent.isVisible() && room.equals(tempAgent.getRoom())) {
				Entity e = tempAgent;
				Point p = new Point((int) e.getX(),(int)  e.getY());
				Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
				Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
				Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
				boolean shift1 = rect.contains(shiftedPoint);
				boolean shift2 = rect.contains(shiftedPoint2);
				boolean shift3 = rect.contains(shiftedPoint3);
				boolean shift4 = rect.contains(shiftedPoint4); 
				
				if ( shift1|| shift2 || shift3 || shift4) {
					if (shift2) shiftedPoint = shiftedPoint2;
					else if (shift3) shiftedPoint = shiftedPoint3;
					else if (shift4) shiftedPoint = shiftedPoint4;
				
					if (e != null) {// && rectWindow.contains(new Point( (int) e.getX()- viewPointX, (int) e.getY()- viewPointY)) ) {
						if (e.getSprite().getImage() != null) {
							g.drawImage(e.getSprite().getImage(),
									(int) (shiftedPoint.x - viewPointX),
									(int) (shiftedPoint.y - viewPointY), null);
							countDraw++;
							if (e.getName().contains(":") && GUI.gui.getShowInfoConsole()) {
								String twitchViewerName = e.getName().substring(0, e.getName().indexOf(":"));
								StreamViewer streamViewer = StreamViewerManager.get(twitchViewerName);
								if (streamViewer != null) {
									g.setColor(new Color(streamViewer.getRed(), streamViewer.getGreen(), streamViewer.getBlue()));
									g.setFont(new Font(GameStates.standardFont, Font.PLAIN , GameStates.inventoryFontSize));
						//			drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
									FontMetrics fm2 = g.getFontMetrics();
									int nameWidth = fm2.stringWidth(twitchViewerName);
		//							int nameHeight = fm2.getHeight();
									
									g.drawString(twitchViewerName, shiftedPoint.x+e.getWidth()/2-nameWidth/2 - viewPointX, shiftedPoint.y-5 - viewPointY);
									countDraw ++;
								}
							}
						}
						
						if (e instanceof Agent) {
							Entity infoIcon = ((Agent) e).getInfoIcon();
							
							Point p1 = new Point((int) e.getX(),(int)  e.getY());
							Point shiftedPoint1 = new Point(p1.x+nextToModZeroX, p1.y + nextToModZeroY);
							Point shiftedPoint21 = new Point(p1.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p1.y + nextToModZeroY);
							Point shiftedPoint31 = new Point(p1.x+nextToModZeroX , p1.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
							Point shiftedPoint41 = new Point(p1.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p1.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
							boolean shift11 = rect.contains(shiftedPoint1);
							boolean shift21 = rect.contains(shiftedPoint21);
							boolean shift31 = rect.contains(shiftedPoint31);
							boolean shift41 = rect.contains(shiftedPoint41); 
							
							if ( shift11|| shift21 || shift31 || shift41) {
								if (shift21) shiftedPoint1 = shiftedPoint21;
								else if (shift31) shiftedPoint1 = shiftedPoint31;
								else if (shift41) shiftedPoint1 = shiftedPoint41;
							
							if (infoIcon != null) {
								if (infoIcon.getSprite().getImage() != null) {
									g.drawImage(infoIcon.getSprite().getImage(),
											(int) (shiftedPoint1.x - viewPointX),
											(int) (shiftedPoint1.y - viewPointY), null);
									countDraw++;
									
								}
							}
						}
					} //else
					//System.out.println("entity is null");
//					GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Agent");
				}
				}
			} else {
//				System.out.println("entity is null");
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Agent");
			}
		}

		/** Paint Agents of other players */
		g.setPaintMode();
		for (PlayerAgentManager playerAgentManager : PlayerAgentManagerManager.playerAgentManagerList
				.values()) {
			List<BigInteger> idListTempPlayerAgents = new ArrayList<BigInteger>(
					playerAgentManager.getIdList());
			for (BigInteger i : idListTempPlayerAgents) {
				PlayerEntity tempAgent = playerAgentManager.get(i);
				if (tempAgent != null && tempAgent.isVisible() && room.equals(tempAgent.getRoom())) {
					Entity e = tempAgent;
					if (e != null && rectWindow.contains(new Point( (int) e.getX()- viewPointX, (int) e.getY() - viewPointY))) {
						if (e.getSprite().getImage() != null) {
							g.drawImage(e.getSprite().getImage(),
									(int) (e.getX() - viewPointX),
									(int) (e.getY() - viewPointY), null);
							countDraw++;
						}
					} else
						System.out.println("null entity from playerAgents");
//						GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Agent");
				} else {
//					GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Agent");
				}
			}
		}
		
		/** player painting */
		if (playerEntity != null) {
			GamePanel.gp.setViewPoint(	(int) playerEntity.getX()+ (int) (playerEntity.getWidth() / 2)- (int) (GameStates.getWidth() / 2 / zoomFactor),	(int) playerEntity.getY()+ (int) (playerEntity.getHeight() / 2)	- (int) (GameStates.getHeight() / 2 / zoomFactor));
			/** paint player Entity */
			if (tempZoom == GamePanel.gp.getZoomFactor()) {
				drawWithEquipment(g, playerEntity);
				countDraw++;
			}
		}

		/** reset ViewPoint */
		GamePanel.gp.setViewPoint(oldViewPoint.x, oldViewPoint.y);

		
		
		/** paint the other players */
		g.setPaintMode();
		if (PlayerManager.size() > 0) {
			List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(
					PlayerManager.idList);
			for (BigInteger i : idListTempPlayers) {
				PlayerEntity playerEntity = PlayerManager.get(i);
				if (playerEntity != null && room.equals(playerEntity.getRoom())&& rectWindow.contains(new Point( (int) playerEntity.getX()- viewPointX , (int) playerEntity.getY()- viewPointY ))) {
					drawWithEquipment(g, playerEntity);
					countDraw++;
				}

			}
		}
		/** reset Scale for all GUI-Elements */
		
		
		if (showTimeStamps) {
			timeStamp = System.currentTimeMillis()-timeStamp;
			System.out.println("items entities players viewpoint: "+timeStamp + " for "+countDraw+" objects");
			countDrawAll+=countDraw;
			countDraw=0;
			timeStamp = System.currentTimeMillis();
		}
		
		
		
		/** paint overlay-Tiles */
		g.setPaintMode();
		ObjectMapManager overlayTree1MapManager = GameWindow.gw.objectMapManagers
				.get("overlayTree1");
		ObjectMapManager overlayTree2MapManager = GameWindow.gw.objectMapManagers
				.get("overlayTree2");
		if (overlayTree1MapManager.pointList != null && overlayTree2MapManager.pointList != null) {
			ArrayList<Point> idListOverlayTree1Map = new ArrayList<Point>(
					overlayTree1MapManager.pointList);
			ArrayList<Point> idListOverlayTree2Map = new ArrayList<Point>(
					overlayTree2MapManager.pointList);
			ArrayList<Point> pointSetOverlayTree = MyUtil.unionListNoDuplicates(
					idListOverlayTree1Map, idListOverlayTree2Map);
			int distanceOverlayTree = GameStates.overlayOffsetY;
			for (Point p : pointSetOverlayTree) {
				LocalMap localMap1 = overlayTree1MapManager.get(p);
				LocalMap localMap2 = overlayTree2MapManager.get(p);
				/** paint both tree parts simultaneously */
				if (localMap1 != null && localMap2 != null) {
					
					Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
					Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
					Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					boolean shift1 = rect.contains(shiftedPoint);
					boolean shift2 = rect.contains(shiftedPoint2);
					boolean shift3 = rect.contains(shiftedPoint3);
					boolean shift4 = rect.contains(shiftedPoint4); 
					
					if ( shift1|| shift2 || shift3 || shift4) {
						if (shift2) shiftedPoint = shiftedPoint2;
						else if (shift3) shiftedPoint = shiftedPoint3;
						else if (shift4) shiftedPoint = shiftedPoint4;
					
	//				if (rect.contains(p)) {
	
						BufferedImage tile1 = null;
						BufferedImage tile2 = null;
						for (int k = 0; k < localMap1.getLocalMap().length; k++) {
							for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
								tile1 = localMap1.getTileImage(k, l,
										GameWindow.gw.getTileMapManager());
								tile2 = localMap2.getTileImage(k, l,
										GameWindow.gw.getTileMapManager());
								tilePosition = new Point((shiftedPoint.x + k * 32 - viewPointX), distanceOverlayTree+(shiftedPoint.y + l * 32 - viewPointY));
	//							Point tilePosition2 = new Point((localMap2.getOrigin().x + k * 32 - viewPointX), (localMap2.getOrigin().y + l * 32 - viewPointY));
								
								if (tile1 != null && rectWindow.contains(tilePosition)) {
									if (paintObjects) {
	//									GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
										g.drawImage(tile1, tilePosition.x,	tilePosition.y,	null);
										countDraw++;
	//									System.out.println("is image accelerated? -"+tile1.getCapabilities(gc).isAccelerated());
										
									}
								}
								if (tile2 != null && rectWindow.contains(tilePosition)) {
									if (paintObjects) {
	//									GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
										g.drawImage(tile2, tilePosition.x,	tilePosition.y,	null);
										countDraw++;
	//									System.out.println("is image accelerated? -"+tile2.getCapabilities(gc).isAccelerated());
									}
								}
							}
						}
					} else {
						/** remove if too far away */
						/**
						 * but only if the localMap is not on the stayList (which is
						 * used for distant maps)
						 */
						if (!overlayTree1MapManager.getStayList().contains(p))
							overlayTree1MapManager.remove(p);
						/**
						 * but only if the localMap is not on the stayList (which is
						 * used for distant maps)
						 */
						if (!overlayTree2MapManager.getStayList().contains(p))
							overlayTree2MapManager.remove(p);
	
					}
				} else if (localMap1 != null) {
					
					Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
					Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
					Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					boolean shift1 = rect.contains(shiftedPoint);
					boolean shift2 = rect.contains(shiftedPoint2);
					boolean shift3 = rect.contains(shiftedPoint3);
					boolean shift4 = rect.contains(shiftedPoint4); 
					
					if ( shift1|| shift2 || shift3 || shift4) {
						if (shift2) shiftedPoint = shiftedPoint2;
						else if (shift3) shiftedPoint = shiftedPoint3;
						else if (shift4) shiftedPoint = shiftedPoint4;
					
	//				if (rect.contains(p)) {
						BufferedImage tile1 = null;
						for (int k = 0; k < localMap1.getLocalMap().length; k++) {
							for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
								tile1 = localMap1.getTileImage(k, l,GameWindow.gw.getTileMapManager());
								tilePosition = new Point((int) (shiftedPoint.x + k * 32 - viewPointX), distanceOverlayTree+(int) (shiftedPoint.y + l * 32 - viewPointY));
								
								if (tile1 != null && rectWindow.contains(tilePosition) ) {
									if (paintObjects)
									{
	//									GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
										
										
										g.drawImage(tile1, tilePosition.x,	tilePosition.y,
												null);
										countDraw++;
	//									System.out.println("is image accelerated? -"+tile1.getCapabilities(gc).isAccelerated());
									}
								}
							}
						}
					} else {
						/** remove if too far away */
						/**
						 * but only if the localMap is not on the stayList (which is
						 * used for distant maps)
						 */
						if (!overlayTree1MapManager.getStayList().contains(p))
							overlayTree1MapManager.remove(p);
					}
					
					/** remove the null overlayManager */
					overlayTree2MapManager.remove(p);
				} else if (localMap2 != null) {
					
					Point shiftedPoint = new Point(p.x+nextToModZeroX, p.y + nextToModZeroY);
					Point shiftedPoint2 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY);
					Point shiftedPoint3 = new Point(p.x+nextToModZeroX , p.y + nextToModZeroY  +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					Point shiftedPoint4 = new Point(p.x+nextToModZeroX +GamePanel.gp.getMiniMap().getWidth()*GameStates.mapTotalWidth, p.y + nextToModZeroY +GamePanel.gp.getMiniMap().getHeight()*GameStates.mapTotalHeight);
					boolean shift1 = rect.contains(shiftedPoint);
					boolean shift2 = rect.contains(shiftedPoint2);
					boolean shift3 = rect.contains(shiftedPoint3);
					boolean shift4 = rect.contains(shiftedPoint4); 
					
					if ( shift1|| shift2 || shift3 || shift4) {
						if (shift2) shiftedPoint = shiftedPoint2;
						else if (shift3) shiftedPoint = shiftedPoint3;
						else if (shift4) shiftedPoint = shiftedPoint4;
					
	//				if (rect.contains(p)) {
						BufferedImage tile2 = null;
						for (int k = 0; k < localMap2.getLocalMap().length; k++) {
							for (int l = 0; l < localMap2.getLocalMap()[0].length; l++) {
								tile2 = localMap2.getTileImage(k, l,
										GameWindow.gw.getTileMapManager());		
								tilePosition = new Point((int) (shiftedPoint.x + k * 32 - viewPointX),distanceOverlayTree+ (int) (shiftedPoint.y + l * 32 - viewPointY));
								if (tile2 != null  && rectWindow.contains(tilePosition)) {
									if (paintObjects) {
										GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
																			
										g.drawImage(tile2, tilePosition.x,	tilePosition.y,
												null);
										countDraw++;
		//								g.drawImage(tile,tilePosition.x,tilePosition.y,	null);
	//									System.out.println("is image accelerated? -"+tile2.getCapabilities(gc).isAccelerated());
										
	//									g.drawImage(tile2,tilePosition.x,tilePosition.y,null);
									}
								}
							}
						}
					} else {
						/** remove if too far away */
						/**
						 * but only if the localMap is not on the stayList (which is
						 * used for distant maps)
						 */
						if (!overlayTree2MapManager.getStayList().contains(p))
							overlayTree2MapManager.remove(p);
					}
					/** remove the null overlayManager */
					overlayTree1MapManager.remove(p);
				} else { 
	//				System.out.println("I got a NULL Map overlay all localMaps are null");
	//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map overlay all localMaps are null");
					overlayTree1MapManager.remove(p);
					overlayTree2MapManager.remove(p);
				}
			}
		}
		
		if (showTimeStamps) {
			timeStamp = System.currentTimeMillis()-timeStamp;
			System.out.println("overlay: "+timeStamp + " for "+countDraw+" objects");
			countDrawAll+=countDraw;
			countDraw=0;
			timeStamp = System.currentTimeMillis();
		}
		
		/** LIGHT PART 
		 * **********************
		 * */
		if (GameWindow.gw.hasLight()) {
			g.setPaintMode();
			/** calc region */
			playerLight.setLocation(new Point((int) (playerEntity.getX() + playerEntity.getWidth() / 2),	(int) (playerEntity.getY() + playerEntity.getHeight() / 2)));
			if (!LightManager.overwrite(playerLight)) LightManager.add(playerLight);
			Rectangle lightRect = new Rectangle((int) (playerEntity.getX()+playerEntity.getWidth()/2	- ((float) GameStates.lightMapWidth / 2) - ((playerEntity.getX()+playerEntity.getWidth()/2) % GameStates.mapTileSetWidth)),	(int) (playerEntity.getY()+playerEntity.getHeight()/2- ((float) GameStates.lightMapHeight / 2) - ((playerEntity.getY()+playerEntity.getHeight()/2)% GameStates.mapTileSetHeight)),GameStates.lightMapWidth, GameStates.lightMapHeight);
			int[][] lightMap = LightManager.getLightMap(lightRect);//this.getLightMap();//LightManager.getLightMap(lightRect);
			
			if (showTimeStamps) {
				timeStamp = System.currentTimeMillis()-timeStamp;
				System.out.println("get Light map: "+timeStamp);
				timeStamp = System.currentTimeMillis();
			}
			
			BufferedImage lightImage = null;
			int lightX = 0;
			int lightY = 0;
//			// obtain the current system graphical settings
//			GraphicsConfiguration gfx_config = GraphicsEnvironment.
//				getLocalGraphicsEnvironment().getDefaultScreenDevice().
//				getDefaultConfiguration();
			
			Point checkPoint = new Point(0,0);
			if (lightMap != null) {
				for (int i = 0; i < lightMap.length; i++) {
					for (int j = 0; j < lightMap[0].length; j++) {
						lightX = lightRect.x + i * GameStates.lightTileWidth;
						lightY = lightRect.y + j * GameStates.lightTileHeight;
						checkPoint.x = lightX - viewPointX;
						checkPoint.y = lightY - viewPointY;
						if (rectWindow.contains(checkPoint)) {
							if (lightMap[i][j] < GameStates.maxLightIntensityCutoff ) {
								lightImage = lightTile.getTileImage(lightMap[i][j]);
							} else {
								lightImage = lightTile.getTileImage(GameStates.maxLightIntensityCutoff);
							}
//							lightImage = lightTile.getTileImage(Math.min(lightMap[i][j], GameStates.maxLightIntensityCutoff));
//							BufferedImage lightImage2 = toCompatibleImage(lightImage, g, gfx_config);
							g.drawImage(lightImage, lightX - viewPointX, lightY- viewPointY, null);
							countDraw++;
						}
					}
				}
			}
		}
		// }
		
		if (showTimeStamps) {
			timeStamp = System.currentTimeMillis()-timeStamp;
			System.out.println("light: "+timeStamp + " for "+countDraw+" objects");
			countDrawAll+=countDraw;
			countDraw=0;
			System.out.println("All objects = "+countDrawAll);
			timeStamp = System.currentTimeMillis();
		}
		
		loadingScreenPaint(g);
		
		GUI.gui.requestFocus();
	}
	
	private BufferedImage toCompatibleImage(BufferedImage image, Graphics2D g2d, GraphicsConfiguration gfx_config)
	{
		/*
		 * if image is already compatible and optimized for current system 
		 * settings, simply return it
		 */
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;

		// image is not optimized, so create a new image that is
		BufferedImage new_image = gfx_config.createCompatibleImage(
				image.getWidth(), image.getHeight(), image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image; 
	}
	
	

	/** draw a playerEntity with its equipment (if existent) */
	private void drawWithEquipment(Graphics2D g, PlayerEntity p) {
		int playerX = (int) p.getX();
		int playerY = (int) p.getY();
		boolean weaponBackground = false;
		boolean shieldBackground = false;
		if ((p.getOrientation() == EntityStates.LEFT) || (p.getOrientation() == EntityStates.UP)) weaponBackground = true;
		if ((p.getOrientation() == EntityStates.RIGHT) || (p.getOrientation() == EntityStates.UP)) shieldBackground = true;
		
		/** paint the shield now if its in the background */
		if (shieldBackground) {
			/** now paint the Shield */
			if (p.getShieldEntity() != null) {
				if (p.getShieldEntity().getSprite().getImage() != null) {
					g.setPaintMode();
					g.drawImage(
							p.getShieldEntity().getSprite().getImage(),
							(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XSHIELD)),
							(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YSHIELD)),
							null);
				}
			}
		}
		
		/** paint the weapon now if its in the background */
		if (weaponBackground) {
			/** now paint the Weapon */
			if (p.getWeaponEntity() != null) {
				if (p.getWeaponEntity().getSprite().getImage() != null) {
					g.setPaintMode();
					g.drawImage(
							p.getWeaponEntity().getSprite().getImage(),
							(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XWEAPON)),
							(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YWEAPON)),
							null);
				}
			}
		}
		
		if (p.getSprite().getImage() != null) {
			g.setPaintMode();
			g.drawImage(p.getSprite().getImage(),
					(int) (playerX - viewPointX),
					(int) (playerY - viewPointY), null);
		}
		
		
		/** now paint the Trousers */
		if (p.getTrousersEntity() != null) {
			if (p.getTrousersEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(
						p.getTrousersEntity().getSprite().getImage(),
						(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XTROUSERS)),
						(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YTROUSERS)),
						null);
			}
		}
		
		/** now paint the Feet */
		if (p.getFeetEntity() != null) {
			if (p.getFeetEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(
						p.getFeetEntity().getSprite().getImage(),
						(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XFEET)),
						(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YFEET)),
						null);
			}
		}
		
		/** now paint the Shirt */
		if (p.getShirtEntity() != null) {
			if (p.getShirtEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(
						p.getShirtEntity().getSprite().getImage(),
						(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XSHIRT)),
						(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YSHIRT)),
						null);
			}
		}
		
		/** now paint the Amulet */
		if (p.getAmuletEntity() != null) {
			if (p.getAmuletEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(
						p.getAmuletEntity().getSprite().getImage(),
						(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XAMULET)),
						(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YAMULET)),
						null);
			}
		}
		
		/** now paint the Glove */
		if (p.getGloveEntity() != null) {
			if (p.getGloveEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(
						p.getGloveEntity().getSprite().getImage(),
						(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XGLOVE)),
						(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YGLOVE)),
						null);
			}
		}

		/** now paint the armor */
		if (p.getArmorEntity() != null) {
			if (p.getArmorEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(
						p.getArmorEntity().getSprite().getImage(),
						(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XARMOR)),
						(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YARMOR)),
						null);
			}
		}
		
//		/** now paint the Helmet */
//		if (p.getHelmetEntity() != null) {
//			if (p.getHelmetEntity().getSprite().getImage() != null) {
//				g.setPaintMode();
//				g.drawImage(p.getHelmetEntity().getSprite().getImage(),(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XHELMET)),(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YHELMET)),	this);
//			}
//		} else {
//			if (p.getHairEntity() != null){
//				if (p.getHairEntity().getSprite().getImage() != null) {
//					g.setPaintMode();
//					g.drawImage(p.getHairEntity().getSprite().getImage(),(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XHELMET)),(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YHELMET)),this);
//				}
//			}
//		}
		
		/** paint the hair in every case */
		if (p.getHairEntity() != null){
			if (p.getHairEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(p.getHairEntity().getSprite().getImage(),(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XHELMET)),(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YHELMET)),null);
			}
		}
		/** now paint the Helmet */
		if (p.getHelmetEntity() != null) {
			if (p.getHelmetEntity().getSprite().getImage() != null) {
				g.setPaintMode();
				g.drawImage(p.getHelmetEntity().getSprite().getImage(),(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XHELMET)),(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YHELMET)),	null);
			}
		}
		
		/** paint the shield now if its in the foreground */
		if (!shieldBackground) {
			/** now paint the Shield */
			if (p.getShieldEntity() != null) {
				if (p.getShieldEntity().getSprite().getImage() != null) {
					g.setPaintMode();
					g.drawImage(
							p.getShieldEntity().getSprite().getImage(),
							(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XSHIELD)),
							(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YSHIELD)),
							null);
				}
			}
		}
		
		/** paint the weapon now if its in the foreground */
		if (!weaponBackground) {
			/** now paint the Weapon */
			if (p.getWeaponEntity() != null) {
				if (p.getWeaponEntity().getSprite().getImage() != null) {
					g.setPaintMode();
					g.drawImage(
							p.getWeaponEntity().getSprite().getImage(),
							(int) (playerX - viewPointX + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.XWEAPON)),
							(int) (playerY - viewPointY + GameStates.getItemProperty(p.getTileSet().getName(), ItemProperty.YWEAPON)),
							null);
				}
			}
		}
	}

	/**
	 * @param g
	 *            : Paint Objects to g
	 * @GAME-Modus
	 */
	public void loadingPaint(Graphics2D g) {
		g.scale(this.getZoomFactor(), this.getZoomFactor());
		g.setPaintMode();
//		GUI.gui.requestFocus();
	}
	
	public void loadingScreenPaint(Graphics2D g) {
		
		if (this.getTransparentLoad() < GameStates.transparentLoad) {
//			g.scale(100.0f, 100.0f);
//			if (this.getTransparentLoad() > GameStates.transparentLoad/2) {
//				int index =(int) (24.0 * ( (this.getTransparentLoad())/GameStates.transparentLoad));
//				System.out.println("index="+index);
//				if (loadingScreenTileset.getTileSet().size() > index) {
//					loadingScreenEntity = loadingScreenTileset.getTileImage(index);
//					if (loadingScreenEntity != null) {
//						g.setPaintMode();
//						g.drawImage(loadingScreenEntity,0,0, null);
//					}
//				}
//				else {
//					loadingScreenEntity = loadingScreenTileset.getTileImage(0);
//					if (loadingScreenEntity != null) {
////						float alpha = (this.getTransparentLoad())/GameStates.transparentLoad;
////						int type = AlphaComposite.SRC_OVER; 
////						AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
//						g.setPaintMode();
//						g.drawImage(loadingScreenEntity,0,0, null);
//					}
//				}
//			} else {
//				loadingScreenEntity = loadingScreenTileset.getTileImage(0);
//				if (loadingScreenEntity != null) {
//					g.setPaintMode();
//					g.drawImage(loadingScreenEntity,0,0, null);
//				}
//			}

			g.setPaintMode();
			if (this.getTransparentLoad() > GameStates.transparentLoad/2) {
				loadingScreenSpace.setTrans(1-((GamePanel.gp.getTransparentLoad()-GameStates.transparentLoad/2)/(GameStates.transparentLoad/2)));
			} else {
				loadingScreenSpace.setTrans(1);
			}
			loadingScreenSpace.scale(1/zoomFactor);
			loadingScreenSpace.paint(g, 0, 0);
			
			if (GamePanel.gp.getTransparentLoad() < GameStates.transparentLoad) {
				GamePanel.gp.setTransparentLoad(GamePanel.gp.getTransparentLoad()+((float) duration)/1000);
				GamePanel.gp.setPause(true);
			} else {
				GamePanel.gp.setTransparentLoad(GameStates.transparentLoad);
				GamePanel.gp.setPause(false);
			}
//			g.setPaintMode();
		} else {
			GamePanel.gp.setPause(false);
		}
	}

	/** load the entities */
	public void loadEntityList(Entity[] entities) {
		if (entities != null) {
			EntityManager.createEntitiesFromArray(entities);
		}
	}

	/** load the spaces */
	public void loadSpaceList(Space[] spaces) {
		if (spaces != null) {
			SpaceManager.createSpacesFromArray(spaces);
		}
	}

	/** load the spaces */
	public void loadPlayerList(PlayerEntity[] players) {
		if (players != null) {
			PlayerManager.createPlayersFromArray(players);
		}
	}

	public void setPause(boolean pause) {
		this.pause = pause;
		// if (pause) GameWindow.gw.item21.setText("Start");
		// else GameWindow.gw.item21.setText("Pause");
	}

	public boolean isPaused() {
		return this.pause;
	}

	public boolean isAllwaysAnimated() {
		return this.allwaysAnimated;
	}

	public void setAllwaysAnimated(boolean allwaysAnimated) {
		this.allwaysAnimated = allwaysAnimated;
	}

	public void setShowStats(boolean showStats) {
		this.showStats = showStats;
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}

	public void setMouseHandler(MouseHandler mouseHandler) {
		this.mouseHandler = mouseHandler;
	}

	public void setMenu(boolean menu) {
		this.menu = menu;
	}

	public boolean isMenu() {
		return this.menu;
	}

	public boolean isServerInitialized() {
		return serverInitialized;
	}

	public void setServerInitialized(boolean b) {
		serverInitialized = b;
	}

	public void setViewPoint(int x, int y) {
		this.viewPointX = x;
		this.viewPointY = y;
		if (viewPointX < minViewPointX)
			viewPointX = minViewPointX;
		if (viewPointY < minViewPointY)
			viewPointY = minViewPointY;
		if (viewPointX > maxViewPointX)
			viewPointX = maxViewPointX;
		if (viewPointY > maxViewPointY)
			viewPointY = maxViewPointY;
	}

	public void setLimitViewPoint(int minX, int minY, int maxX, int maxY) {
		this.minViewPointX = minX;
		this.minViewPointY = minY;
		this.maxViewPointX = maxX;
		this.maxViewPointY = maxY;
	}

	public int getViewPointX() {
		return this.viewPointX;
	}

	public int getViewPointY() {
		return this.viewPointY;
	}

	public Player getPlayer() {
		return p;
	}

	public float getZoomFactor() {
		return this.zoomFactor;
	}

	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	public void setRotationDegree(double degree) {
		this.rotationDegree = degree;
	}

	public double getRotationDegree() {
		return this.rotationDegree;
	}

	public Space getSpaceAdd() {
		return spaceAdd;
	}

	public void setSpaceAdd(Space spaceAdd) {
		this.spaceAdd = spaceAdd;
	}

	public void updatePlayerSprite() {
		this.playerEntity.updateSprite();
	}

	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
		playerEntity.updateVisualsHair("ShortBlondHair");
	}

	public PlayerEntity getPlayerEntity() {
		return this.playerEntity;
	}

	public void setPlayerEntityName(String strname) {
		this.playerEntity.setName(strname);
	}

	public void setPlayerGroupName(String groupName) {
		this.playerEntity.setGroupName(groupName);
	}

	public boolean isInitializedPlayer() {
		return initializedPlayer;
	}

	public void setInitializedPlayer(boolean initializedPlayer) {
		this.initializedPlayer = initializedPlayer;
	}

	public boolean isDeleteModus() {
		return deleteModus;
	}

	public void setDeleteModus(boolean deleteModus) {
		this.deleteModus = deleteModus;
	}

	public String getPaintLayer() {
		return paintLayer;
	}

	public void setPaintLayer(String paintLayer) {
		this.paintLayer = paintLayer;
	}

	public int getPaintType() {
		return paintType;
	}

	public void setPaintType(int paintType) {
		this.paintType = paintType;
	}

	public boolean isPaintEditSpaceArea() {
		return paintEditSpaceArea;
	}

	public void setPaintEditSpaceArea(boolean paintEditSpaceArea) {
		this.paintEditSpaceArea = paintEditSpaceArea;
	}

	public TileSet getLightTileDay() {
		return lightTileDay;
	}

	public void setLightTileDay(TileSet lightTile) {
		this.lightTileDay = lightTile;
	}

	public TileSet getLightTileNight() {
		return lightTileNight;
	}

	public void setLightTileNight(TileSet lightTile) {
		this.lightTileNight = lightTile;
	}

	public int[][] getLightMap() {
		return lightMap;
	}

	public void setLightMap(int[][] lightMap) {
		this.lightMap = lightMap;
	}

	public Item getMouseItem() {
		return mouseItem;
	}

	public void setMouseItem(Item mouseItem) {
		this.mouseItem = mouseItem;
	}

	public void dropMouseItem(Point p) {
		/** send the complete Item to all players of the channel */
		if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
			/** first send to server for the itemList */
			GameWindow.gw.send(ClientMessages.addItem(mouseItem.getId(),mouseItem.getItemCode(), mouseItem.getCount(), mouseItem.getCapacity(), p.x, p.y, mouseItem.getEntity().getMX(), mouseItem.getEntity().getMY(), mouseItem.getName(), mouseItem.getComment(), this.getPlayerEntity().getRoom(), mouseItem.getEntity().getTileSet().getFileName(), mouseItem.getEntity().getName(),mouseItem.getAttributes()));

			// GameWindow.gw.send(ClientMessages.addItem(mouseItem.getId()));
			for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
				ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
				try {
					channel.send(ClientMessages.addCompleteItem(
							mouseItem.getItemCode(), mouseItem.getId(),	mouseItem.getName(), mouseItem.getComment(),this.getPlayerEntity().getRoom(), p.x, p.y,mouseItem.getCount(), mouseItem.getAttributes()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			this.mouseItem = null;
		}
	}

	public InfluenceThread getInfluenceThread() {
		return influenceThread;
	}

	public void setInfluenceThread(InfluenceThread influenceThread) {
		this.influenceThread = influenceThread;
	}

	public Point getMouseCoordinates() {
		return mouseCoordinates;
	}

	public void setMouseCoordinates(Point mouseCoordinates) {
		this.mouseCoordinates = mouseCoordinates;
	}
	
	/** painting on point with the actual paintLayer on the corresponding paintType */
	public boolean paintTiles(Point point, String paintLayer, int paintType, boolean deleteModus) {
		boolean paintedAnythingNew = false;
		if (!isPaintingBlocked()) {
			/** only paint if the paintLayer is not "empty" */
			if (!paintLayer.equals("empty")) {
				Point correctedPoint = new Point( (int) (point.x/GamePanel.gp.getZoomFactor()) +GamePanel.gp.getViewPointX(), (int) (point.y/GamePanel.gp.getZoomFactor())+GamePanel.gp.getViewPointY());
//				point = correctedPoint;
				if (paintLayer.equals("underground")) {
					int localWidthUnderground = GameStates.ugrMapWidth * GameStates.ugrMapTileSetWidth;
					int localHeightUnderground = GameStates.ugrMapHeight * GameStates.ugrMapTileSetHeight;
					int latticePointMapXUnderground = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidthUnderground/2) / (localWidthUnderground)) * localWidthUnderground;
					int latticePointMapYUnderground = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeightUnderground/2) / (localHeightUnderground)) * localHeightUnderground;
					int minXUnderground = latticePointMapXUnderground-((int)GameStates.factorOfViewDeleteDistance*localWidthUnderground);
					int maxXUnderground = latticePointMapXUnderground+((int)GameStates.factorOfViewDeleteDistance*localWidthUnderground);
					int minYUnderground = latticePointMapYUnderground-((int)GameStates.factorOfViewDeleteDistance*localHeightUnderground);
					int maxYUnderground = latticePointMapYUnderground+((int)GameStates.factorOfViewDeleteDistance*localHeightUnderground);
					Rectangle rectUnderground = new Rectangle(minXUnderground+GameStates.ugrMapTileSetWidth,minYUnderground+GameStates.ugrMapTileSetHeight, maxXUnderground - minXUnderground-GameStates.ugrMapTileSetWidth-1,	maxYUnderground - minYUnderground-GameStates.ugrMapTileSetHeight-1);
	
					int latticePointXUnderground = (int) Math.floor( (float) correctedPoint.x / (localWidthUnderground)) * localWidthUnderground;
					int latticePointYUnderground = (int) Math.floor( (float) correctedPoint.y / (localHeightUnderground)) * localHeightUnderground;
					int localXUnderground = (int) Math.floor( (float) (correctedPoint.x - latticePointXUnderground )/ GameStates.ugrMapTileSetWidth);
					int localYUnderground = (int) Math.floor( (float) (correctedPoint.y - latticePointYUnderground )/ GameStates.ugrMapTileSetHeight);
					Point pUnderground = new Point(latticePointXUnderground, latticePointYUnderground);
	
					if ( rectUnderground.contains(correctedPoint) ) {	
						UndergroundMapManager mapManager = GameWindow.gw.getUndergroundMapManagers().get(paintLayer);
						/** create Map if not yet created */
						if (!mapManager.contains(pUnderground)) {
							mapManager.createMap(pUnderground);
						}
						
						/** check if deleteModus or paintModus */
						if (!deleteModus) {
							mapManager.get(pUnderground).setUl(localXUnderground, localYUnderground, paintType);
							mapManager.get(pUnderground).setUr(localXUnderground, localYUnderground, paintType);
							mapManager.get(pUnderground).setDl(localXUnderground, localYUnderground, paintType);
							mapManager.get(pUnderground).setDr(localXUnderground, localYUnderground, paintType);
							mapManager.get(pUnderground).setIdByCorners(localXUnderground, localYUnderground, paintType);
							mapManager.adjustSurrounding(mapManager.get(pUnderground), localXUnderground, localYUnderground, paintType);
						} else {
							//MapManager.get(pUnderground).setTile(localXUnderground, localYUnderground, null);
							mapManager.get(pUnderground).setUl(localXUnderground, localYUnderground, 0);
							mapManager.get(pUnderground).setUr(localXUnderground, localYUnderground, 0);
							mapManager.get(pUnderground).setDl(localXUnderground, localYUnderground, 0);
							mapManager.get(pUnderground).setDr(localXUnderground, localYUnderground, 0);
							mapManager.get(pUnderground).setIdByCorners(localXUnderground, localYUnderground, paintType);
							mapManager.deleteSurrounding(mapManager.get(pUnderground), localXUnderground, localYUnderground, paintType);
							//MapManager.adjustSurrounding(MapManager.get(pUnderground), localXUnderground, localYUnderground);
						}
						/** register lattice Point for save - and send procedure */
						mapManager.addChangedList(pUnderground);
	//					System.out.println(mapManager.changedList.size());
						/** add corners to saveList and send-procedure if needed */
						if ((localXUnderground == 0) && (localYUnderground == 0)) {
							// ul
							Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y-localHeightUnderground);
							mapManager.addChangedList(p2);
						} 
						if ( (localXUnderground == GameStates.ugrMapWidth-1) && (localYUnderground == 0)) {
							// ur
							Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y-localHeightUnderground);
							mapManager.addChangedList(p2);
						}
						if (localYUnderground == 0) {
							// u
							Point p2 = new Point(pUnderground.x, pUnderground.y-localHeightUnderground);
							mapManager.addChangedList(p2);
						} 
						if ( (localXUnderground == 0) && (localYUnderground == GameStates.ugrMapHeight-1)) {
							// dl
							Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y+localHeightUnderground);
							mapManager.addChangedList(p2);
						}
						if ( (localXUnderground == 0)) {
							// l
							Point p2 = new Point(pUnderground.x-localWidthUnderground, pUnderground.y);
							mapManager.addChangedList(p2);
						}
						if ( (localXUnderground == GameStates.ugrMapWidth-1) && (localYUnderground == GameStates.ugrMapHeight-1) ) {
							// dr
							Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y+localHeightUnderground);
							mapManager.addChangedList(p2);
						}
						if ( (localXUnderground == GameStates.ugrMapWidth-1) ) {
							// r
							Point p2 = new Point(pUnderground.x+localWidthUnderground, pUnderground.y);
							mapManager.addChangedList(p2);
						}
						if (localYUnderground == GameStates.ugrMapHeight-1) {
							// d
							Point p2 = new Point(pUnderground.x, pUnderground.y+localHeightUnderground);
							mapManager.addChangedList(p2);
						}
					}
				} else 	if (paintLayer.equals("hole")) {
					this.setSmallPaint(false);
					/** we need to paint on two layers: hole and tree */
					/** define min and max for view distance */
					int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
					int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
					int latticePointMapX = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidth/2) / (localWidth)) * localWidth;
					int latticePointMapY = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeight/2) / (localHeight)) * localHeight;
					int minX = latticePointMapX-((int)GameStates.factorOfViewDeleteDistance*localWidth);
					int maxX = latticePointMapX+((int)GameStates.factorOfViewDeleteDistance*localWidth);
					int minY = latticePointMapY-((int)GameStates.factorOfViewDeleteDistance*localHeight);
					int maxY = latticePointMapY+((int)GameStates.factorOfViewDeleteDistance*localHeight);
					Rectangle rect = new Rectangle(minX+GameStates.mapTileSetWidth, minY+GameStates.mapTileSetHeight, maxX - minX-GameStates.mapTileSetWidth-1, maxY - minY-GameStates.mapTileSetHeight-1);
	
					
					/** here start the small tiles (32x32 normally) */
	//				int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
	//				int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
					int latticePointX = (int) Math.floor( (float) correctedPoint.x / (localWidth)) * localWidth;
					int latticePointY = (int) Math.floor( (float) correctedPoint.y / (localHeight)) * localHeight;
					int localX = (int) Math.floor( (float) (correctedPoint.x - latticePointX )/ GameStates.mapTileSetWidth);
					int localY = (int) Math.floor( (float) (correctedPoint.y - latticePointY )/ GameStates.mapTileSetHeight);
					
					Point p = new Point(latticePointX, latticePointY);

					if ( rect.contains(correctedPoint) ) {			
//						if (paintLayer.equals("tree")) {
							/** allways paint trees */
							if ( localX % 2 != 0 ) {
								if (localX < GameStates.mapWidth-1)
									localX++;
								else localX--;
							}
							if (localY % 2 != 0 ) {
								if (localY < GameStates.mapHeight-1)
									localY++;
								else localY--;
							}
								ObjectMapManager objectMapManager = null;
								ObjectMapManager overlayMapManager = null;
								ObjectMapManager objectMapManagerLowerRoom = null;
								ObjectMapManager overlayMapManagerLowerRoom = null;
								
								/** choose the correct ObjectMapManager */
								if (localX % 4 == 0) {
									objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree1");
									overlayMapManager = GameWindow.gw.getObjectMapManagers().get("overlayTree1");	
								}
								if (localX % 4 == 2) {
									objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree2");
									overlayMapManager = GameWindow.gw.getObjectMapManagers().get("overlayTree2");
//									objectMapManagerLowerRoom = GameWindow.gw.getObjectMapManagersLowerRoom().get("tree2");
//									overlayMapManagerLowerRoom = GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree2");
								}
								objectMapManagerLowerRoom = GameWindow.gw.getObjectMapManagersLowerRoom().get("tree1");
								overlayMapManagerLowerRoom = GameWindow.gw.getObjectMapManagersLowerRoom().get("overlayTree1");
								
								/** create Map if not yet created */
								if (!objectMapManager.contains(p)) {
									objectMapManager.createMap(p);
								}
								
								/** create Map if not yet created */
								if (!overlayMapManager.contains(p)) {
									overlayMapManager.createMap(p);
								}
								
								/** create Map if not yet created */
								if (!objectMapManagerLowerRoom.contains(p)) {
									objectMapManagerLowerRoom.createMap(p);
								}
								
								/** create Map if not yet created */
								if (!overlayMapManagerLowerRoom.contains(p)) {
									overlayMapManagerLowerRoom.createMap(p);
								}
								
								/** check if deleteModus or paintModus */
								if (!deleteModus) {
//									if (LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == paintType || LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == 0) {
//										if (GamePanel.gp.isSmallPaint()) {
//											mapManager.get(p).setUl(localX, localY, paintType);
//										} else {
//											mapManager.get(p).setUl(localX, localY, paintType);
//											mapManager.get(p).setUr(localX, localY, paintType);
//											mapManager.get(p).setDl(localX, localY, paintType);
//											mapManager.get(p).setDr(localX, localY, paintType);
//										}
//			//							mapManager.get(p).setUr(localX, localY, 0);
//			//							mapManager.get(p).setDl(localX, localY, 0);
//			//							mapManager.get(p).setDr(localX, localY, 0);
//										mapManager.get(p).setIdByCorners(localX, localY, paintType);
//										mapManager.adjustSurrounding(mapManager.get(p), localX, localY, paintType);
//										
//										paintedAnythingNew = true;
//									}
//									
									
									if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == paintType || LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == 0) {
										TileDimensions tileDimensions = LocalMap.getTileDimensions(paintType);
										objectMapManager.get(p).setUl(localX, localY, paintType);
										if (tileDimensions == TileDimensions.RowCol2x2) objectMapManager.get(p).setUl(localX, localY, 0);
										objectMapManager.get(p).setUr(localX, localY, paintType);
										objectMapManager.get(p).setDl(localX, localY, 0);
										objectMapManager.get(p).setDr(localX, localY, 0);
										objectMapManager.get(p).setIdByCornersObject(localX, localY, paintType, tileDimensions);
										objectMapManager.adjustSurrounding(objectMapManager.get(p), localX, localY, paintType, tileDimensions);
										
										overlayMapManager.get(p).setUl(localX, localY, paintType-GameStates.mapTileSetWidth*2);
										if (tileDimensions == TileDimensions.RowCol2x2) overlayMapManager.get(p).setUl(localX, localY, 0);
										overlayMapManager.get(p).setUr(localX, localY, paintType-GameStates.mapTileSetWidth*2);
										overlayMapManager.get(p).setDl(localX, localY, 0);
										overlayMapManager.get(p).setDr(localX, localY, 0);
										overlayMapManager.get(p).setIdByCornersObject(localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
										overlayMapManager.adjustSurrounding(overlayMapManager.get(p), localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
										
										
										TileDimensions tileDimensionSoilBlock = TileDimensions.RowCol2x3;
										/** delete soilTile from the lowerRoom if there is no other hole*/
										if (LocalMap.getPaintType(objectMapManagerLowerRoom.get(p).getLocalMap()[localX][localY]) != GameStates.holeTile) {
//											objectMapManagerLowerRoom.get(p).setUl(localX, localY, 0);
//											objectMapManagerLowerRoom.get(p).setUr(localX, localY, 0);
//											objectMapManagerLowerRoom.get(p).setDl(localX, localY, 0);
//											objectMapManagerLowerRoom.get(p).setDr(localX, localY, 0);
//											objectMapManagerLowerRoom.get(p).setIdByCornersObject(localX, localY, paintType, tileDimensionSoilBlock);
//											objectMapManagerLowerRoom.deleteSurrounding(objectMapManagerLowerRoom.get(p), localX, localY, GameStates.soilBlockTile, tileDimensionSoilBlock);
//											
//											overlayMapManagerLowerRoom.get(p).setUl(localX, localY, 0);
//											overlayMapManagerLowerRoom.get(p).setUr(localX, localY, 0);
//											overlayMapManagerLowerRoom.get(p).setDl(localX, localY, 0);
//											overlayMapManagerLowerRoom.get(p).setDr(localX, localY, 0);
//											overlayMapManagerLowerRoom.get(p).setIdByCornersObject(localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensionSoilBlock);
//											overlayMapManagerLowerRoom.deleteSurrounding(overlayMapManagerLowerRoom.get(p), localX, localY, GameStates.soilBlockTile-GameStates.mapTileSetWidth*2, tileDimensionSoilBlock);
//											paintedAnythingNew = true;

										/** only allow deletions for the actual PaintType */
										int fluidTile = GameStates.fluidSoilTile;
										if (LocalMap.getPaintType(objectMapManagerLowerRoom.get(p).getTile(localX, localY)) == fluidTile) {
											int localX2x2 = (localX < GameStates.mapWidth-1) ? localX+1 : localX-1 ;
											int localY2x2 = (localY < GameStates.mapHeight-1) ? localY+1 : localY-1 ;
													
											int[] arrayPosX = new int[]{localX,localX,localX2x2, localX2x2};
											int[] arrayPosY = new int[]{localY,localY2x2,localY, localY2x2};
											int localXTemp = 0;
											int localYTemp = 0;
											
											
											/** delete 2x2 tiles of the lower room */
											for (int i = 0; i < arrayPosX.length; i++) {
												localXTemp = arrayPosX[i];
												localYTemp = arrayPosY[i];
												
												objectMapManagerLowerRoom.get(p).setTile(localXTemp, localYTemp, 0);
												objectMapManagerLowerRoom.adjustSurrounding(objectMapManagerLowerRoom.get(p), localXTemp, localYTemp, fluidTile, TileDimensions.Fluid);
												
												overlayMapManagerLowerRoom.get(p).setTile(localXTemp, localYTemp, 0);
												overlayMapManagerLowerRoom.adjustSurrounding(overlayMapManagerLowerRoom.get(p), localXTemp, localYTemp, fluidTile-4*GameStates.tileSetWidth, TileDimensions.Fluid);
												paintedAnythingNew = true;
																								
												/** define the relevant checkPositions */
												int checkPositionX = localXTemp;
												if ( checkPositionX % 2 != 0 ) {
													if (checkPositionX < GameStates.mapWidth-1)
														checkPositionX++;
													else checkPositionX--;
												}
												int checkPositionY = localYTemp;
												if ( checkPositionY % 2 != 0 ) {
													if (checkPositionY < GameStates.mapHeight-1)
														checkPositionY++;
													else checkPositionY--;
												}
												
												/** now check all six 2x2 fields, that correspond to the actual localX|localY and delete the 1 if there is no other paintTily */
												if (checkPositionX == 0 || checkPositionY == 0) {
													/** do nothing on the border */
												} else {
													if ((LocalMap.isEmptyOrAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY)) ) && (LocalMap.isEmptyOrAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX, checkPositionY-1))) && (LocalMap.isEmptyOrAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY-1))) && (objectMapManagerLowerRoom.get(p).getTile(checkPositionX, checkPositionY) <= 1)) {
														objectMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 0);
														overlayMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 0);
													} else if ((checkPositionX == localXTemp && checkPositionY == localYTemp) && (LocalMap.isEmptyOrAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX, checkPositionY-1))) ) {
														/** only set as 1, if there is a fluid Tile nearby */
														if( LocalMap.isFluid(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY)) || LocalMap.isFluid(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY-1)) )		
															objectMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 1);
															overlayMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 0);
													}
													
													if (checkPositionX == localXTemp && checkPositionY == localYTemp+1 && (LocalMap.isEmptyOrAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX, checkPositionY)) ))  {
														if( LocalMap.isFluid(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY)) || LocalMap.isFluid(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY-1)) ) {	
															objectMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 1);
															overlayMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 0);
														}
														if ((objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY) == 0 || LocalMap.isAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY)) ) && (objectMapManagerLowerRoom.get(p).getTile(checkPositionX, checkPositionY-1) == 0 || LocalMap.isAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX, checkPositionY-1))) && (objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY-1) == 0 || LocalMap.isAddon(objectMapManagerLowerRoom.get(p).getTile(checkPositionX-1, checkPositionY-1))) && (objectMapManagerLowerRoom.get(p).getTile(checkPositionX, checkPositionY) <= 1)) {
															objectMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 0);
															overlayMapManagerLowerRoom.get(p).setTile(checkPositionX, checkPositionY, 0);
														}
													}
												}
											}
										}
										}
									}
								} else {
									/** DELETE-Mode */
									/** only allow deletions for the actual PaintType */
									if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == paintType) {
										//MapManager.get(p).setTile(localX, localY, null);
										if (objectMapManager.get(p).getLocalMap()[localX][localY] != 0) {
	
										}
										TileDimensions tileDimensions = LocalMap.getTileDimensions(objectMapManager.get(p).getTile(localX, localY));
										objectMapManager.get(p).setUl(localX, localY, 0);
										objectMapManager.get(p).setUr(localX, localY, 0);
										objectMapManager.get(p).setDl(localX, localY, 0);
										objectMapManager.get(p).setDr(localX, localY, 0);
										objectMapManager.get(p).setIdByCornersObject(localX, localY, paintType, tileDimensions);
										objectMapManager.deleteSurrounding(objectMapManager.get(p), localX, localY, paintType, tileDimensions);
										
										overlayMapManager.get(p).setUl(localX, localY, 0);
										overlayMapManager.get(p).setUr(localX, localY, 0);
										overlayMapManager.get(p).setDl(localX, localY, 0);
										overlayMapManager.get(p).setDr(localX, localY, 0);
										overlayMapManager.get(p).setIdByCornersObject(localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
										overlayMapManager.deleteSurrounding(overlayMapManager.get(p), localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);

									}
								}
								/** register lattice Point for save - and send procedure */
								objectMapManager.addChangedList(p);
								overlayMapManager.addChangedList(p);
								objectMapManagerLowerRoom.addChangedList(p);
								overlayMapManagerLowerRoom.addChangedList(p);
								/** now check the corners */
								if ((localX == 0) && (localY == 0)) {
									// ul
									Point p2 = new Point(p.x-localWidth, p.y-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
									objectMapManagerLowerRoom.addChangedList(p2);
									overlayMapManagerLowerRoom.addChangedList(p2);
								} 
								if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
									// ur
									Point p2 = new Point(p.x+localWidth, p.y-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
									objectMapManagerLowerRoom.addChangedList(p2);
									overlayMapManagerLowerRoom.addChangedList(p2);
								}
								if (localY == 0) {
									// u
									Point p2 = new Point(p.x, p.y-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
									objectMapManagerLowerRoom.addChangedList(p2);
									overlayMapManagerLowerRoom.addChangedList(p2);
								} 
								if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
									// dl
									// no need to create because trees dont have a dl tile
								}
								if ( (localX == 0)) {
									// l
									Point p2 = new Point(p.x-localWidth, p.y);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
									objectMapManagerLowerRoom.addChangedList(p2);
									overlayMapManagerLowerRoom.addChangedList(p2);
			
								}
								if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
									// dr
									// no need to create because trees dont have a dr tile
								}
								if ( (localX == GameStates.mapWidth-1) ) {
									// r
									Point p2 = new Point(p.x+localWidth, p.y);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
									objectMapManagerLowerRoom.addChangedList(p2);
									overlayMapManagerLowerRoom.addChangedList(p2);
								}
								if (localY == GameStates.mapHeight-1) {
									// d
									// no need to create because trees dont have a d tile
								}
			//				}
//						} else {
								
						/**
						 * now create the MapManager-Overlay for holes and water
						 */
						if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == 0 || LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == GameStates.holeTile || LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == GameStates.waterTile) {
							MapManager mapManager = GameWindow.gw.getMapManagers().get(paintLayer);
							/** create Map if not yet created */
							if (!mapManager.contains(p)) {
								mapManager.createMap(p);
							}
							
							/** check if deleteModus or paintModus */
							if (!deleteModus) {
								if (LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == paintType || LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == 0) {
									if (GamePanel.gp.isSmallPaint()) {
										mapManager.get(p).setUl(localX, localY, paintType);
									} else {
										mapManager.get(p).setUl(localX, localY, paintType);
										mapManager.get(p).setUr(localX, localY, paintType);
										mapManager.get(p).setDl(localX, localY, paintType);
										mapManager.get(p).setDr(localX, localY, paintType);
									}
									mapManager.get(p).setIdByCorners(localX, localY, paintType);
									mapManager.adjustSurrounding(mapManager.get(p), localX, localY, paintType);
									paintedAnythingNew = true;
								}
							} else {
								if (LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == paintType || LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == 0) {
									if (GamePanel.gp.isSmallPaint()) {
										mapManager.get(p).setDl(localX, localY, 0);
									} else {
										mapManager.get(p).setUl(localX, localY, 0);
										mapManager.get(p).setUr(localX, localY, 0);
										mapManager.get(p).setDl(localX, localY, 0);
										mapManager.get(p).setDr(localX, localY, 0);
									}
									mapManager.get(p).setIdByCorners(localX, localY, paintType);
									mapManager.deleteSurrounding(mapManager.get(p), localX, localY, paintType);
								}
							}
							/** register lattice Point for save - and send procedure */
							mapManager.addChangedList(p);
							/** add corners to saveList and send-procedure if needed */
							if ((localX == 0) && (localY == 0)) {
								// ul
								Point p2 = new Point(p.x-localWidth, p.y-localHeight);
								mapManager.addChangedList(p2);
							} 
							if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
								// ur
								Point p2 = new Point(p.x+localWidth, p.y-localHeight);
								mapManager.addChangedList(p2);
							}
							if (localY == 0) {
								// u
								Point p2 = new Point(p.x, p.y-localHeight);
								mapManager.addChangedList(p2);
							} 
							if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
								// dl
								Point p2 = new Point(p.x-localWidth, p.y+localHeight);
								mapManager.addChangedList(p2);
							}
							if ( (localX == 0)) {
								// l
								Point p2 = new Point(p.x-localWidth, p.y);
								mapManager.addChangedList(p2);
							}
							if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
								// dr
								Point p2 = new Point(p.x+localWidth, p.y+localHeight);
								mapManager.addChangedList(p2);
							}
							if ( (localX == GameStates.mapWidth-1) ) {
								// r
								Point p2 = new Point(p.x+localWidth, p.y);
								mapManager.addChangedList(p2);
							}
							if (localY == GameStates.mapHeight-1) {
								// d
								Point p2 = new Point(p.x, p.y+localHeight);
								mapManager.addChangedList(p2);
							}
//						}
						}
					}
					
//					/** now take the room under this room and delete one field of the mudBlocks 
//					 * ***********************************************************************
//					 * */
//					String lowerRoom = GameWindow.gw.getLowerRoom(this.getPlayerEntity().getRoom());
//					/** first try to load the map from localFileSystem */
//					String mapFileName1 = "tree1"+"_"+p.x+"_"+p.y+".map";
//					String mapFileName2 = "tree2"+"_"+p.x+"_"+p.y+".map";
//					String overlayMapFileName1 = "overlayTree1"+"_"+p.x+"_"+p.y+".map";
//					String overlayMapFileName2 = "overlayTree2"+"_"+p.x+"_"+p.y+".map";
//					LocalMap holeMap1 = ObjectMapManager.loadMap(GameStates.standardMapFolder+GameWindow.gw.getMapMasterName()+"/"+lowerRoom+"/"+mapFileName1);
//					LocalMap overLayHoleMap1 = ObjectMapManager.loadMap(GameStates.standardMapFolder+GameWindow.gw.getMapMasterName()+"/"+lowerRoom+"/"+overlayMapFileName1);
//					LocalMap holeMap2 = ObjectMapManager.loadMap(GameStates.standardMapFolder+GameWindow.gw.getMapMasterName()+"/"+lowerRoom+"/"+mapFileName2);
//					LocalMap overLayHoleMap2 = ObjectMapManager.loadMap(GameStates.standardMapFolder+GameWindow.gw.getMapMasterName()+"/"+lowerRoom+"/"+overlayMapFileName2);
//					if (holeMap1 != null) {
//						
//					} else {
//						/** create map */
//						holeMap1 = ObjectMapManager.getPaintFullTilesOnMap(p, "tree1", GameStates.smallStoneTile);
//						/** delete the field that is a hole */
//						
//					}				
					
					/** else generate new map */
					
					
				/** end of hole generation */	
				} else 	{
					/** HERE BEGINS the distinction between trees and mapManagers 
					 * **********************************************************
					 * */
					/** define min and max for view distance */
					int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
					int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
					int latticePointMapX = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidth/2) / (localWidth)) * localWidth;
					int latticePointMapY = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeight/2) / (localHeight)) * localHeight;
					int minX = latticePointMapX-((int)GameStates.factorOfViewDeleteDistance*localWidth);
					int maxX = latticePointMapX+((int)GameStates.factorOfViewDeleteDistance*localWidth);
					int minY = latticePointMapY-((int)GameStates.factorOfViewDeleteDistance*localHeight);
					int maxY = latticePointMapY+((int)GameStates.factorOfViewDeleteDistance*localHeight);
					Rectangle rect = new Rectangle(minX+GameStates.mapTileSetWidth, minY+GameStates.mapTileSetHeight, maxX - minX-GameStates.mapTileSetWidth-1, maxY - minY-GameStates.mapTileSetHeight-1);
	
					
					/** here start the small tiles (32x32 normally) */
	//				int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
	//				int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
					int latticePointX = (int) Math.floor( (float) correctedPoint.x / (localWidth)) * localWidth;
					int latticePointY = (int) Math.floor( (float) correctedPoint.y / (localHeight)) * localHeight;
					int localX = (int) Math.floor( (float) (correctedPoint.x - latticePointX )/ GameStates.mapTileSetWidth);
					int localY = (int) Math.floor( (float) (correctedPoint.y - latticePointY )/ GameStates.mapTileSetHeight);
					int localDownX = localX;
					int localDownY = (localY >= GameStates.mapHeight-1) ? 0 : localY+1;
					
					Point p = new Point(latticePointX, latticePointY);
					/** modulo is applied */
					Point pDownWithoutModulo = (localY<GameStates.mapHeight-1) ? p : new Point(latticePointMapX, latticePointMapY+localHeight);
					Point pDown = new Point(((pDownWithoutModulo.x/GameStates.mapTotalWidth) % getMiniMap().getWidth() +getMiniMap().getWidth()) % getMiniMap().getWidth() *GameStates.mapTotalWidth, ((pDownWithoutModulo.y/GameStates.mapTotalHeight) % getMiniMap().getHeight() +getMiniMap().getHeight()) % getMiniMap().getHeight() *GameStates.mapTotalHeight);
					
					
	//				int minX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) - GameStates.factorOfViewDeleteDistance*localWidth + GameStates.mapTileSetWidth;
	//				int maxX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) + GameStates.factorOfViewDeleteDistance*localWidth - GameStates.mapTileSetWidth;
	//				int minY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight - GameStates.factorOfViewDeleteDistance*localHeight + GameStates.mapTileSetHeight;
	//				int maxY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight + GameStates.factorOfViewDeleteDistance*localHeight - GameStates.mapTileSetHeight;
	//				Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
					
					if ( rect.contains(correctedPoint) ) {				
						if (paintLayer.equals("tree")) {
							/** distinction between normal tree-Objects and fluid Objects */
							if (LocalMap.isFluid(paintType)) {
								/** FLUID OBJECTS */
								/** define the relevant checkPositions */
								int checkPositionX = localX;
								if ( checkPositionX % 2 != 0 ) {
									if (checkPositionX < GameStates.mapWidth-1)
										checkPositionX++;
									else checkPositionX--;
								}
								int checkPositionY = localY;
								if ( checkPositionY % 2 != 0 ) {
									if (checkPositionY < GameStates.mapHeight-1)
										checkPositionY++;
									else checkPositionY--;
								}
								
								ObjectMapManager objectMapManager1 = GameWindow.gw.getObjectMapManagers().get("tree1");
								ObjectMapManager overlayMapManager1 = GameWindow.gw.getObjectMapManagers().get("overlayTree1");
								ObjectMapManager objectMapManager2 = GameWindow.gw.getObjectMapManagers().get("tree2");
								ObjectMapManager overlayMapManager2 = GameWindow.gw.getObjectMapManagers().get("overlayTree2");
								
								/** create Map if not yet created */
								if (!objectMapManager1.contains(p)) {
									objectMapManager1.createMap(p);
								}
								/** create Map if not yet created */
								if (!overlayMapManager1.contains(p)) {
									overlayMapManager1.createMap(p);
								}
								if (!objectMapManager2.contains(p)) {
									objectMapManager2.createMap(p);
								}
								/** create Map if not yet created */
								if (!overlayMapManager2.contains(p)) {
									overlayMapManager2.createMap(p);
								}
								
								/** if the DownTile is on the next Map */
								if (localY>=GameStates.mapHeight-1) {
									/** create Map if not yet created */
									if (!objectMapManager1.contains(pDown)) {
										objectMapManager1.createMap(pDown);
									}
									/** create Map if not yet created */
									if (!overlayMapManager1.contains(pDown)) {
										overlayMapManager1.createMap(pDown);
									}
									/** same for map2 */
									if (!objectMapManager2.contains(pDown)) {
										objectMapManager2.createMap(pDown);
									}
									/** create Map if not yet created */
									if (!overlayMapManager2.contains(pDown)) {
										overlayMapManager2.createMap(pDown);
									}
								}
								
								/** check if deleteModus or paintModus */
								if (!deleteModus) {
									if (  (LocalMap.isEmptyOrAddon(objectMapManager1.get(p).getTile(localX, localY)) && LocalMap.getPaintType(objectMapManager2.get(p).getTile(localX, localY)) == 0) 
											&& (LocalMap.isEmptyOrAddon(objectMapManager1.get(pDown).getTile(localDownX, localDownY))|| LocalMap.getPaintType(objectMapManager1.get(pDown).getTile(localDownX, localDownY)) == paintType)
											&& LocalMap.getPaintType(objectMapManager2.get(pDown).getTile(localDownX, localDownY)) == 0
											) {
										objectMapManager1.get(p).setTile(localX, localY, paintType);
										objectMapManager1.adjustSurrounding(objectMapManager1.get(p), localX, localY, paintType, TileDimensions.Fluid);

										overlayMapManager1.get(p).setTile(localX, localY, paintType-4*GameStates.tileSetWidth);
										overlayMapManager1.adjustSurrounding(overlayMapManager1.get(p), localX, localY, paintType-4*GameStates.tileSetWidth, TileDimensions.Fluid);

										paintedAnythingNew = true;
										
										if (LocalMap.getPaintType(objectMapManager1.get(p).getTile(checkPositionX, checkPositionY)) == 0) {
											objectMapManager1.get(p).setTile(checkPositionX, checkPositionY, 1);
										}
									}
									
									
									if ( (LocalMap.getPaintType(objectMapManager1.get(p).getTile(localX, localY)) == GameStates.holeTile || LocalMap.getPaintType(objectMapManager2.get(p).getTile(localX, localY)) == GameStates.holeTile) && this.getPaintType() == GameStates.fluidSoilTile) {
//										System.out.println("close the hole");
										this.setPaintLayer("hole"); 
										this.setPaintType(GameStates.holeTile);
										this.setDeleteModus(true);
										this.paintTiles(point, "hole", GameStates.holeTile, true);
										paintedAnythingNew = true;
									}
								} else {
									/** only allow deletions for the actual PaintType */
									if (LocalMap.getPaintType(objectMapManager1.get(p).getTile(localX, localY)) == paintType) {
										objectMapManager1.get(p).setTile(localX, localY, 0);
										objectMapManager1.adjustSurrounding(objectMapManager1.get(p), localX, localY, paintType, TileDimensions.Fluid);
										
										overlayMapManager1.get(p).setTile(localX, localY, 0);
										overlayMapManager1.adjustSurrounding(overlayMapManager1.get(p), localX, localY, paintType-4*GameStates.tileSetWidth, TileDimensions.Fluid);
										paintedAnythingNew = true;
										
										/** now check all six 2x2 fields, that correspond to the actual localX|localY and delete the 1 if there is no other paintTily */
										if (checkPositionX == 0 || checkPositionY == 0) {
											/** do nothing on the border */
										} else {
											if ((LocalMap.isEmptyOrAddon(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY)) ) && (LocalMap.isEmptyOrAddon(objectMapManager1.get(p).getTile(checkPositionX, checkPositionY-1))) && (LocalMap.isEmptyOrAddon(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY-1))) && (objectMapManager1.get(p).getTile(checkPositionX, checkPositionY) <= 1)) {
												objectMapManager1.get(p).setTile(checkPositionX, checkPositionY, 0);
												overlayMapManager1.get(p).setTile(checkPositionX, checkPositionY, 0);
											} else if ((checkPositionX == localX && checkPositionY == localY) && (LocalMap.isEmptyOrAddon(objectMapManager1.get(p).getTile(checkPositionX, checkPositionY-1))) ) {
												/** only set as 1, if there is a fluid Tile nearby */
												if( LocalMap.isFluid(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY)) || LocalMap.isFluid(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY-1)) )		
												objectMapManager1.get(p).setTile(checkPositionX, checkPositionY, 1);
												overlayMapManager1.get(p).setTile(checkPositionX, checkPositionY, 0);
											}
											
											if (checkPositionX == localX && checkPositionY == localY+1 && (LocalMap.isEmptyOrAddon(objectMapManager1.get(p).getTile(checkPositionX, checkPositionY)) ))  {
												if( LocalMap.isFluid(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY)) || LocalMap.isFluid(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY-1)) ) {	
													objectMapManager1.get(p).setTile(checkPositionX, checkPositionY, 1);
													overlayMapManager1.get(p).setTile(checkPositionX, checkPositionY, 0);
												}
												if ((objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY) == 0 || LocalMap.isAddon(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY)) ) && (objectMapManager1.get(p).getTile(checkPositionX, checkPositionY-1) == 0 || LocalMap.isAddon(objectMapManager1.get(p).getTile(checkPositionX, checkPositionY-1))) && (objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY-1) == 0 || LocalMap.isAddon(objectMapManager1.get(p).getTile(checkPositionX-1, checkPositionY-1))) && (objectMapManager1.get(p).getTile(checkPositionX, checkPositionY) <= 1)) {
													objectMapManager1.get(p).setTile(checkPositionX, checkPositionY, 0);
													overlayMapManager1.get(p).setTile(checkPositionX, checkPositionY, 0);
												}
											}
										}
									}
								}
								/** register lattice Point for save - and send procedure */
								objectMapManager1.addChangedList(p);
								overlayMapManager1.addChangedList(p);
								/** if the DownTile is on the next Map */
								if (localY>=GameStates.mapHeight-1) {
									objectMapManager1.addChangedList(pDown);
									overlayMapManager1.addChangedList(pDown);
								}
								
								/** now check the corners */
								if ((localX == 0) && (localY == 0)) {
									// ul
									Point p2 = new Point(p.x-localWidth, p.y-localHeight);
									objectMapManager1.addChangedList(p2);
									overlayMapManager1.addChangedList(p2);
								} 
								if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
									// ur
									Point p2 = new Point(p.x+localWidth, p.y-localHeight);
									objectMapManager1.addChangedList(p2);
									overlayMapManager1.addChangedList(p2);
								}
								if (localY == 0) {
									// u
									Point p2 = new Point(p.x, p.y-localHeight);
									objectMapManager1.addChangedList(p2);
									overlayMapManager1.addChangedList(p2);
								} 
								if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
									// dl
									// no need to create because trees dont have a dl tile
								}
								if ( (localX == 0)) {
									// l
									Point p2 = new Point(p.x-localWidth, p.y);
									objectMapManager1.addChangedList(p2);
									overlayMapManager1.addChangedList(p2);
			
								}
								if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
									// dr
									// no need to create because trees dont have a dr tile
								}
								if ( (localX == GameStates.mapWidth-1) ) {
									// r
									Point p2 = new Point(p.x+localWidth, p.y);
									objectMapManager1.addChangedList(p2);
									overlayMapManager1.addChangedList(p2);
			
								}
								if (localY == GameStates.mapHeight-1) {
									// d
									// no need to create because trees dont have a d tile
								}
								
							} /** end of fluid objects */
							else {
								/** normal Tree-Objects */
								/** allways paint trees */
								if ( localX % 2 != 0 ) {
									if (localX < GameStates.mapWidth-1)
										localX++;
									else localX--;
								}
								if (localY % 2 != 0 ) {
									if (localY < GameStates.mapHeight-1)
										localY++;
									else localY--;
								}
								
								ObjectMapManager objectMapManager = null;
								ObjectMapManager overlayMapManager = null;
								ObjectMapManager objectMapManager1 = GameWindow.gw.getObjectMapManagers().get("tree1");
								
								/** choose the correct ObjectMapManager */
								if (localX % 4 == 0) {
									objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree1");
									overlayMapManager = GameWindow.gw.getObjectMapManagers().get("overlayTree1");
								}
								if (localX % 4 == 2) {
									objectMapManager = GameWindow.gw.getObjectMapManagers().get("tree2");
									overlayMapManager = GameWindow.gw.getObjectMapManagers().get("overlayTree2");
								}
								
								/** create Map if not yet created */
								if (!objectMapManager.contains(p)) {
									objectMapManager.createMap(p);
								}
								/** create Map if not yet created */
								if (!overlayMapManager.contains(p)) {
									overlayMapManager.createMap(p);
								}
								
								/** create Map if not yet created */
								if (!objectMapManager1.contains(p)) {
									objectMapManager1.createMap(p);
								}
								
								int boundaryCorrection = (localY == 0) ? 1 : -1;
								/** check if deleteModus or paintModus */
								if (!deleteModus) {
									if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == 0 && LocalMap.getPaintType(objectMapManager1.get(p).getTile(localX, localY)) == 0
											&& !LocalMap.isFluid(objectMapManager1.get(p).getTile(localX+1, localY))
											&& !LocalMap.isFluid(objectMapManager1.get(p).getTile(localX+1, localY+boundaryCorrection))
											&& !LocalMap.isFluid(objectMapManager1.get(p).getTile(localX, localY+boundaryCorrection))
											&& !LocalMap.isFluid(objectMapManager1.get(p).getTile(localX, localY))
											) {
										TileDimensions tileDimensions = LocalMap.getTileDimensions(paintType);
										
										if (tileDimensions == TileDimensions.RowCol1x1) {
											objectMapManager.get(p).setTile(localX, localY, paintType);
											paintedAnythingNew = true;
										} else {
											objectMapManager.get(p).setUl(localX, localY, paintType);
											if (tileDimensions == TileDimensions.RowCol2x2) objectMapManager.get(p).setUl(localX, localY, 0);
											objectMapManager.get(p).setUr(localX, localY, paintType);
											objectMapManager.get(p).setDl(localX, localY, 0);
											objectMapManager.get(p).setDr(localX, localY, 0);
											objectMapManager.get(p).setIdByCornersObject(localX, localY, paintType, tileDimensions);
											objectMapManager.adjustSurrounding(objectMapManager.get(p), localX, localY, paintType, tileDimensions);
											
											overlayMapManager.get(p).setUl(localX, localY, paintType-GameStates.mapTileSetWidth*2);
											if (tileDimensions == TileDimensions.RowCol2x2) overlayMapManager.get(p).setUl(localX, localY, 0);
											overlayMapManager.get(p).setUr(localX, localY, paintType-GameStates.mapTileSetWidth*2);
											overlayMapManager.get(p).setDl(localX, localY, 0);
											overlayMapManager.get(p).setDr(localX, localY, 0);
											overlayMapManager.get(p).setIdByCornersObject(localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
											overlayMapManager.adjustSurrounding(overlayMapManager.get(p), localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
											
											paintedAnythingNew = true;
										}
									}
									
									if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == GameStates.holeTile && this.getPaintType() == GameStates.fluidSoilTile) {
//										System.out.println("close the hole");
										this.setPaintLayer("hole"); 
										this.setPaintType(GameStates.holeTile);
										this.setDeleteModus(true);
										this.paintTiles(point, "hole", GameStates.holeTile, true);
										paintedAnythingNew = true;
									}
								} else {
									/** only allow deletions for the actual PaintType */
									if (LocalMap.getPaintType(objectMapManager.get(p).getTile(localX, localY)) == paintType) {
									//MapManager.get(p).setTile(localX, localY, null);
									if (objectMapManager.get(p).getLocalMap()[localX][localY] != 0) {
	////									System.out.println("mouse: localX/Y = "+localX+"/"+localY);
	//									// create drop 
	//									BigInteger itemId = ItemManager.getMaxIDValue().add(GamePanel.gp.getPlayerEntity().getId());
	//									Item woodItem = Item.getItem(ITEMCODE.WOOD, itemId, "wood", (int) (Math.random()*20+20), 0, p.x+localX*GameStates.mapTileSetWidth+(int) (Math.random()*(GameStates.mapTileSetWidth-GameStates.itemTileWidth)), p.y+localY*GameStates.mapTileSetHeight +(int) (Math.random()*(GameStates.mapTileSetHeight-GameStates.itemTileHeight)), System.currentTimeMillis(), new float[0]);
	//									
	////									System.out.println("added new wood object: "+itemId);
	//									
	//									/** send the complete Item to all players of the channel */
	//									if (GameWindow.gw.isLoggedIn() && GamePanel.gp.isInitializedPlayer()) {
	//										/** first send to server for the itemList */
	//										GameWindow.gw.send(ClientMessages.addItem(woodItem.getId(), woodItem.getItemCode(), woodItem.getCount(), woodItem.getCapacity(), woodItem.getEntity().getX(), woodItem.getEntity().getY(), woodItem.getEntity().getMX(), woodItem.getEntity().getMY(), woodItem.getName(), woodItem.getEntity().getTileSet().getFileName(), woodItem.getEntity().getName(), woodItem.getAttributes()));
	//										for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
	//											ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
	//											try {
	//												channel.send(ClientMessages.addCompleteItem(woodItem.getItemCode(), woodItem.getId(), woodItem.getName(), woodItem.getEntity().getX(), woodItem.getEntity().getY(), woodItem.getCount(), woodItem.getAttributes()));
	//											} catch (IOException e) {
	//												e.printStackTrace();
	//											}	
	//										}
	//									}
									}
									TileDimensions tileDimensions = LocalMap.getTileDimensions(objectMapManager.get(p).getTile(localX, localY));
									
									objectMapManager.get(p).setUl(localX, localY, 0);
									objectMapManager.get(p).setUr(localX, localY, 0);
									objectMapManager.get(p).setDl(localX, localY, 0);
									objectMapManager.get(p).setDr(localX, localY, 0);
									objectMapManager.get(p).setIdByCornersObject(localX, localY, paintType, tileDimensions);
									objectMapManager.deleteSurrounding(objectMapManager.get(p), localX, localY, paintType, tileDimensions);
									
									overlayMapManager.get(p).setUl(localX, localY, 0);
									overlayMapManager.get(p).setUr(localX, localY, 0);
									overlayMapManager.get(p).setDl(localX, localY, 0);
									overlayMapManager.get(p).setDr(localX, localY, 0);
									overlayMapManager.get(p).setIdByCornersObject(localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
									overlayMapManager.deleteSurrounding(overlayMapManager.get(p), localX, localY, paintType-GameStates.mapTileSetWidth*2, tileDimensions);
									}
								}
								/** register lattice Point for save - and send procedure */
								objectMapManager.addChangedList(p);
								overlayMapManager.addChangedList(p);
								/** now check the corners */
								if ((localX == 0) && (localY == 0)) {
									// ul
									Point p2 = new Point(p.x-localWidth, p.y-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								} 
								if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
									// ur
									Point p2 = new Point(p.x+localWidth, p.y-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								}
								if (localY == 0) {
									// u
									Point p2 = new Point(p.x, p.y-localHeight);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
								} 
								if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
									// dl
									// no need to create because trees dont have a dl tile
								}
								if ( (localX == 0)) {
									// l
									Point p2 = new Point(p.x-localWidth, p.y);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
			
								}
								if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
									// dr
									// no need to create because trees dont have a dr tile
								}
								if ( (localX == GameStates.mapWidth-1) ) {
									// r
									Point p2 = new Point(p.x+localWidth, p.y);
									objectMapManager.addChangedList(p2);
									overlayMapManager.addChangedList(p2);
			
								}
								if (localY == GameStates.mapHeight-1) {
									// d
									// no need to create because trees dont have a d tile
								}
							} /** end of tree objects */
							
							
							
						} else {
							/**
							 * normal MapManager 
							 */
							
							
							MapManager mapManager = GameWindow.gw.getMapManagers().get(paintLayer);
					//		System.out.println("writing on "+paintLayer);
							/** create Map if not yet created */
							if (!mapManager.contains(p)) {
								mapManager.createMap(p);
							}
							
							/** check if deleteModus or paintModus */
							if (!deleteModus) {
								//Tile tile = new Tile(62, true, true, true, true);
					//			MapManager.get(p).setTile(localX, localY, 62);
								if (LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == paintType || LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == 0) {
									if (GamePanel.gp.isSmallPaint()) {
										mapManager.get(p).setUl(localX, localY, paintType);
									} else {
										mapManager.get(p).setUl(localX, localY, paintType);
										mapManager.get(p).setUr(localX, localY, paintType);
										mapManager.get(p).setDl(localX, localY, paintType);
										mapManager.get(p).setDr(localX, localY, paintType);
									}
		//							mapManager.get(p).setUr(localX, localY, 0);
		//							mapManager.get(p).setDl(localX, localY, 0);
		//							mapManager.get(p).setDr(localX, localY, 0);
									mapManager.get(p).setIdByCorners(localX, localY, paintType);
									mapManager.adjustSurrounding(mapManager.get(p), localX, localY, paintType);
									
									paintedAnythingNew = true;
								}
							} else {
								/**
								 * DELETE MODUS
								 */
								//MapManager.get(p).setTile(localX, localY, null);
								
								if (LocalMap.getPaintType(mapManager.get(p).getLocalMap()[localX][localY]) == paintType ) {
									if (GamePanel.gp.isSmallPaint()) {
										mapManager.get(p).setDl(localX, localY, 0);
									} else {
										mapManager.get(p).setUl(localX, localY, 0);
										mapManager.get(p).setUr(localX, localY, 0);
										mapManager.get(p).setDl(localX, localY, 0);
										mapManager.get(p).setDr(localX, localY, 0);
									}
									mapManager.get(p).setIdByCorners(localX, localY, paintType);
									mapManager.deleteSurrounding(mapManager.get(p), localX, localY, paintType);
									//MapManager.adjustSurrounding(MapManager.get(p), localX, localY);
								}
							}
							/** register lattice Point for save - and send procedure */
							mapManager.addChangedList(p);
							/** add corners to saveList and send-procedure if needed */
							if ((localX == 0) && (localY == 0)) {
								// ul
								Point p2 = new Point(p.x-localWidth, p.y-localHeight);
								mapManager.addChangedList(p2);
							} 
							if ( (localX == GameStates.mapWidth-1) && (localY == 0)) {
								// ur
								Point p2 = new Point(p.x+localWidth, p.y-localHeight);
								mapManager.addChangedList(p2);
							}
							if (localY == 0) {
								// u
								Point p2 = new Point(p.x, p.y-localHeight);
								mapManager.addChangedList(p2);
							} 
							if ( (localX == 0) && (localY == GameStates.mapHeight-1)) {
								// dl
								Point p2 = new Point(p.x-localWidth, p.y+localHeight);
								mapManager.addChangedList(p2);
							}
							if ( (localX == 0)) {
								// l
								Point p2 = new Point(p.x-localWidth, p.y);
								mapManager.addChangedList(p2);
							}
							if ( (localX == GameStates.mapWidth-1) && (localY == GameStates.mapHeight-1) ) {
								// dr
								Point p2 = new Point(p.x+localWidth, p.y+localHeight);
								mapManager.addChangedList(p2);
							}
							if ( (localX == GameStates.mapWidth-1) ) {
								// r
								Point p2 = new Point(p.x+localWidth, p.y);
								mapManager.addChangedList(p2);
							}
							if (localY == GameStates.mapHeight-1) {
								// d
								Point p2 = new Point(p.x, p.y+localHeight);
								mapManager.addChangedList(p2);
							}
						}
					} else {
						GUI.gui.gameInfoConsole.appendInfo("outside of paint area");
					}
				}
			} else {
				/** paintLayer is "empty" -> do nothing */
			}
		}
		return paintedAnythingNew;
	}
	

	
	/** first delete concurring tiles and then paint onto the layer */
	public boolean paintAfterDeleteTiles(Point point, String paintLayer, int paintType, boolean deleteModus) {
		boolean paintedAnythingNew = false;
		int[] concurringPaintTypes = getConcurringPaintTypes(paintType);
		boolean isHoleOrWater = (LocalMap.getPaintType(paintType) == GameStates.holeTile || LocalMap.getPaintType(paintType) == GameStates.waterTile);
		if (concurringPaintTypes.length > 0) {
			Point shiftedPoint = new Point();
			int maxX = 3;
			int maxY = 3;
			int minusX = 1;
			int minusY = 1;
			if (isHoleOrWater) {
				maxX = 5;
				maxY = 5;
				minusX = 2;
				minusY = 2;
			}
			for(int i = 0; i< concurringPaintTypes.length; i++) {
				for (int j = 0; j <maxX; j++) {
					for (int k = 0; k <maxY; k++) {
						shiftedPoint.x = (int) (point.x + (j-minusX) * ((float)GameStates.mapTileSetWidth*zoomFactor));
						shiftedPoint.y = (int) (point.y + (k-minusY) * ((float)GameStates.mapTileSetHeight*zoomFactor));
						if(paintTiles(shiftedPoint, paintLayer, concurringPaintTypes[i], true)) paintedAnythingNew = true;
					}
				}
			}
			paintedAnythingNew = paintTiles(point, paintLayer, paintType, deleteModus);
		} else {
			// just paint the tiles
			paintedAnythingNew = paintTiles(point, paintLayer, paintType, deleteModus);
		}
		
		return paintedAnythingNew;
	}
	
	private int[] getConcurringPaintTypes(int paintType) {
		int[] retArray = new int[]{};
		if (paintType == GameStates.dirtTrackTile) {
			retArray = new int[]{GameStates.cobbleTile};
		}
		if (paintType == GameStates.cobbleTile) {
			retArray = new int[]{GameStates.dirtTrackTile};
		}
		if (paintType == GameStates.waterTile) {
			retArray = new int[]{GameStates.holeTile};
		}
		if (paintType == GameStates.holeTile) {
			retArray = new int[]{GameStates.waterTile};
		}
		
		if (paintType == GameStates.grassTile) {
			retArray = new int[]{GameStates.snowTile};
		}
		if (paintType == GameStates.snowTile) {
			retArray = new int[]{GameStates.grassTile};
		}
		
		return retArray;
	}

	public BodyArmorFactory getBodyArmorFactory() {
		return bodyArmorFactory;
	}

	public void setBodyArmorFactory(BodyArmorFactory bodyArmorFactory) {
		this.bodyArmorFactory = bodyArmorFactory;
	}

	public HelmetFactory getHelmetFactory() {
		return helmetFactory;
	}

	public void setHelmetFactory(HelmetFactory helmetFactory) {
		this.helmetFactory = helmetFactory;
	}

	public WeaponFactory getWeaponFactory() {
		return weaponFactory;
	}

	public void setWeaponFactory(WeaponFactory weaponFactory) {
		this.weaponFactory = weaponFactory;
	}

	public FeetFactory getFeetFactory() {
		return feetFactory;
	}

	public void setFeetFactory(FeetFactory feetFactory) {
		this.feetFactory = feetFactory;
	}

	public ShieldFactory getShieldFactory() {
		return shieldFactory;
	}

	public void setShieldFactory(ShieldFactory shieldFactory) {
		this.shieldFactory = shieldFactory;
	}

	public AmuletFactory getAmuletFactory() {
		return amuletFactory;
	}

	public void setAmuletFactory(AmuletFactory amuletFactory) {
		this.amuletFactory = amuletFactory;
	}

	public GloveFactory getGloveFactory() {
		return gloveFactory;
	}

	public void setGloveFactory(GloveFactory gloveFactory) {
		this.gloveFactory = gloveFactory;
	}

	public TrousersFactory getTrousersFactory() {
		return trousersFactory;
	}

	public void setTrousersFactory(TrousersFactory trousersFactory) {
		this.trousersFactory = trousersFactory;
	}

	public ShirtFactory getShirtFactory() {
		return shirtFactory;
	}

	public void setShirtFactory(ShirtFactory shirtFactory) {
		this.shirtFactory = shirtFactory;
	}

	public boolean isSmallPaint() {
		return smallPaint;
	}

	public void setSmallPaint(boolean smallPaint) {
		this.smallPaint = smallPaint;
	}

	public boolean isPaintingBlocked() {
		return paintingBlocked;
	}

	public void setPaintingBlocked(boolean paintingBlocked) {
		this.paintingBlocked = paintingBlocked;
	}

	public TileSet getLightTile() {
		return lightTile;
	}

	public void setLightTile(TileSet lightTile) {
		this.lightTile = lightTile;
	}
	
	public long getWorldTime() {
		return worldTime;
	}

	public void setWorldTime(long worldTime) {
		this.worldTime = worldTime;
	}
	
	public void determineDayNightLight() {
		float maxDay = GameStates.maxDay;
		float timeStep = GameStates.timeStep;
		
		if (!GameWindow.gw.isGameMaster()) {
			if (GameWindow.gw.getWorldType(this.getPlayerEntity().getRoom()) == WorldType.BASE) {
				if (lightIntensity > maxDay) {
					GameWindow.gw.setLight(false);
				} else if (lightIntensity > maxDay-(1*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName19,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(2*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName18,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(3*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName17,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(4*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName16,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(5*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName15,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(6*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName14,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(7*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName13,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(8*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName12,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(9*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName11,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(10*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName10,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(11*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName09,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(12*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName08,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(13*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName07,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(14*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName06,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(15*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName05,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(16*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName04,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(17*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName03,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(18*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName02,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else if (lightIntensity > maxDay-(19*timeStep)) {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName01,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} else {
					GameWindow.gw.setLight(true);
					lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName00,GameStates.lightTileWidth, GameStates.lightTileHeight);
				} 
			} else {
				// no light-changes underworld
				GameWindow.gw.setLight(true);
				lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetName00,GameStates.lightTileWidth, GameStates.lightTileHeight);
			}
		} else {
			GameWindow.gw.setLight(false);
		}
			
		if (GameModus.modus == GameModus.GAME) {
			GameWindow.gw.selectBackgroundMusicByEnvironment();
		} else if (GameModus.modus == GameModus.MAINMENU) {
			GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.MAINMENU);
		}
	}

	public double getLightIntensity() {
		return lightIntensity;
	}

	public void setLightIntensity(double lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	public FoodFactory getFoodFactory() {
		return foodFactory;
	}

	public void setFoodFactory(FoodFactory foodFactory) {
		this.foodFactory = foodFactory;
	}

	public MaterialFactory getMaterialFactory() {
		return materialFactory;
	}

	public void setMaterialFactory(MaterialFactory materialFactory) {
		this.materialFactory = materialFactory;
	}
	
	public void setPlayerLight(Light light) {
		this.playerLight = light;
	}
	
	public Light getPlayerLight() {
		return playerLight;
	}

	public MiniMap getMiniMap() {
		return miniMap;
	}

	public void setMiniMap(MiniMap miniMap) {
		this.miniMap = miniMap;
	}
	
	public MiniMap getDungeonMiniMap() {
		return dungeonMiniMap;
	}

	public void setDungeonMiniMap(MiniMap miniMap) {
		this.dungeonMiniMap = miniMap;
	}

	public float getTransparentLoad() {
		return transparentLoad;
	}

	public void setTransparentLoad(float transparentLoad) {
		this.transparentLoad = transparentLoad;
	}

}
