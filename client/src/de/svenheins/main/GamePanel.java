package de.svenheins.main;
import de.svenheins.functions.MyMath;
import de.svenheins.functions.MyUtil;
import de.svenheins.handlers.ConsoleInputHandler;
import de.svenheins.handlers.InputHandler;
import de.svenheins.handlers.MouseHandler;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import de.svenheins.managers.AnimationManager;
import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.ItemManager;
import de.svenheins.managers.LightManager;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.managers.UndergroundMapManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.ITEMCODE;

import de.svenheins.objects.Entity;
import de.svenheins.objects.IngameConsole;
import de.svenheins.objects.IngameWindow;
import de.svenheins.objects.Light;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.LocalUndergroundMap;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.TileSet;
import de.svenheins.objects.WorldPosition;
import de.svenheins.objects.agents.Agent;
import de.svenheins.objects.agents.SimpleAgent;
import de.svenheins.objects.agents.goals.Goal;
import de.svenheins.objects.items.Item;

import de.svenheins.threads.AgentThread;
import de.svenheins.threads.AnimationThread;
import de.svenheins.threads.ChannelUpdateMapsThread;
import de.svenheins.threads.ChannelUpdateThread;
import de.svenheins.threads.CollisionThread;
import de.svenheins.threads.GraphicThread;
import de.svenheins.threads.InputThread;
import de.svenheins.threads.InteractionThread;
import de.svenheins.threads.LightThread;
import de.svenheins.threads.MapUpdateThread;
import de.svenheins.threads.MoveThread;
import de.svenheins.threads.ServerUpdateThread;


public class GamePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private String[] shipArray;
//	private ShipEntity s;
//	private ShipEntity s2;
	private Player p;
//	private Player p2;
	private Player[] players;
	private PlayerEntity playerEntity;
//	public Entity eye;
//	public Space space;
	private Space spaceAdd;

	
//	private ConsoleInputHandler consoleInput;
	private MoveThread moveThread;
	private InputThread inputThread;
	private MouseHandler mouseHandler;
	
	public static ServerUpdateThread serverUpdateThread;
	private GraphicThread graphicThread;
	private CollisionThread collisionThread;
	private AnimationThread animationThread;
	private ChannelUpdateThread channelUpdateThread;
	private ChannelUpdateMapsThread channelUpdateMapsThread;
	private MapUpdateThread mapUpdateThread;
	private LightThread lightThread;
	private AgentThread agentThread;
	private InteractionThread interactionThread;
	public static GamePanel gp;
//	public static String resourcePath = "/resources/";
//	public static String svgPath = "/resources/svg/";
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
	private boolean paintEditSpaceArea = true;
	
	private Item mouseItem = null;
	
//	private ArrayList<Polygon> editSpaceAreaPolygon = new ArrayList<Polygon>();
//	private Space editSpaceArea;
	private Light light3;
	private TileSet lightTileDay;
	private TileSet lightTileNight;
	private int[][] lightMap;
	
	
	private int countFrames = 0;
	private long duration = 0;
	private long wholeDuration = 0;
	private long oldTime = System.currentTimeMillis();
	
	
	public IngameWindow mainMenu;
	
//	public Space startButton;
//	public Space simulationButton;
	public Space connectButton;
	
//	public IngameConsole gameConsole;
	
	
	/**
	 * Constructor of the GamePanel
	 * 1) Init
	 * 2) Config
	 * 3) Start the Threads
	 */
	public GamePanel(){
		super();
		gp = this;
		
		this.init();
		this.config();
		
		new Thread(moveThread).start();
		new Thread(collisionThread).start();
		new Thread(graphicThread).start();
		new Thread(inputThread).start();
		new Thread(animationThread).start();
		new Thread(channelUpdateThread).start();
		new Thread(mapUpdateThread).start();
		new Thread(channelUpdateMapsThread).start();
		new Thread(lightThread).start();
		new Thread(agentThread).start();
		new Thread(interactionThread).start();
	}
	
	/**
	 * init(): Initialize all variables for the first time
	 */
	public void init() {

		p = new Player("Spieler1", new InputHandler(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE, KeyEvent.VK_P, KeyEvent.VK_ESCAPE, KeyEvent.VK_I, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_Q));
//		p2 = new Player("Spieler2", new InputHandler(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S,KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_O));
		players = new Player[]{p}; 
		TileSet tileSet = new TileSet(GameStates.standardTileNamePlayer, "standardPlayer", 32, 64);
		TileSet tileSet2 = new TileSet("tilesets/entities/standardShip.png", "shipTileName2", 50, 50);
		TileSet tileSet_green = new TileSet("tilesets/entities/standardShip_green.png", "shipTileName_green", 50, 50);
		TileSet tileSet_yellow = new TileSet("tilesets/entities/standardShip_yellow.png", "shipTileName_yellow", 50, 50);
		TileSet tileSet_blue = new TileSet("tilesets/entities/standardShip_blue.png", "shipTileName_blue", 50, 50);
		playerEntity = new PlayerEntity(tileSet, "localPlayer", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
		Entity playerEntity2 = new Entity(tileSet2, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
		playerEntity2 = new Entity(tileSet_green, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
		playerEntity2 = new Entity(tileSet_yellow, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
		playerEntity2 = new Entity(tileSet_blue, "localPlayer2", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay);
		
		
		
//		/** space that describes the paint area */
//		editSpaceAreaPolygon.add((new Polygon(new int[]{0 , 0 , 2* GameStates.factorOfViewDeleteDistance*GameStates.mapTotalWidth,2* GameStates.factorOfViewDeleteDistance*GameStates.mapTotalWidth}, new int[]{0,2*GameStates.factorOfViewDeleteDistance*GameStates.mapTotalHeight, 2*GameStates.factorOfViewDeleteDistance*GameStates.mapTotalHeight,0}, 4)));
//		editSpaceArea = new Space(editSpaceAreaPolygon, 0, 0, "editSpaceRegion", BigInteger.valueOf(0), new int[]{0, 60, 20}, true, 0.2f, 1.0f, 1.0f, "empty");
		
		/** init Managers and GUIs */
		this.initGUI();
		this.initManagers();
		
		/** Init the input */
		// Keyboard must not be added here
		// Mouse
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());
		
		// Init Time
		gp.last = System.currentTimeMillis();
		
		// Init Threads
		moveThread = new MoveThread(System.currentTimeMillis());
		inputThread = new InputThread(players);
		graphicThread = new GraphicThread(this);
		collisionThread = new CollisionThread();
		animationThread = new AnimationThread();
		serverUpdateThread = new ServerUpdateThread(System.currentTimeMillis());
		channelUpdateThread = new ChannelUpdateThread();
		mapUpdateThread = new MapUpdateThread();
		channelUpdateMapsThread = new ChannelUpdateMapsThread();
		lightThread = new LightThread();
		agentThread = new AgentThread();
		interactionThread = new InteractionThread();
	}
	
	public void config() {		
		gp.setFocusable(true);
		GameModus.modus = GameModus.MAINMENU;
		// config the global vars
		this.setPause(false);
		this.setAllwaysAnimated(true);
		this.setShowStats(false);
//		this.showConsole = false;
		this.setMenu(false);
		this.serverInitialized = false;
		this.setInitializedPlayer(false);
		this.setViewPoint(0, 0);
		this.minViewPointX = -1000000;//362;
		this.minViewPointY = -1000000;//181;
		this.maxViewPointX = 1000000;
		this.maxViewPointY = 1000000;
		this.setZoomFactor(1.0f);
		this.setRotationDegree(0);
		this.setDeleteModus(false);
		this.setPaintType(110);
		this.setPaintLayer("cobble");
		
		/** set all Managers and GUI elements to a standard state */
		configGUI();
		

		// Modify the Cursor
		//Cursor cursor = getToolkit().createCustomCursor(new ImageIcon(getClass().getResource(resourcePath+"images/"+"cursor.png")).getImage(), new Point(0,0), "Cursor");
		//this.setCursor(cursor);
		
		mainMenu = new IngameWindow();

	}
	
	private void initGUI() {	
		TileSet tileSetCobbleButton = new TileSet("tilesets/buttons/cobbleButton.png", "cobbleButton", GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetGrassButton = new TileSet("tilesets/buttons/grassButton.png", "grassButton", GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetSnowButton = new TileSet("tilesets/buttons/snowButton.png", "snowButton", GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetTreeButton = new TileSet("tilesets/buttons/treeButton.png", "treeButton", GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetSnowTreeButton = new TileSet("tilesets/buttons/snowTreeButton.png", "snowTreeButton", GameStates.tileSetWidth, GameStates.tileSetHeight);
		TileSet tileSetUndergroundGrassButton = new TileSet("tilesets/buttons/undergroundGrassButton.png", "undergroundGrassButton", GameStates.tileSetWidth, GameStates.tileSetHeight);
		Button cobbleButtonGUI = new Button(tileSetCobbleButton, "cobbleButton", BigInteger.valueOf(0), GameStates.width - 208, 25, GameStates.animationDelay, "cobble", GameStates.cobbleTile, "");
		Button grassButtonGUI = new Button(tileSetGrassButton, "grassButton", BigInteger.valueOf(1), GameStates.width - 172, 25, GameStates.animationDelay, "grass", GameStates.grassTile, "");
		Button snowButtonGUI = new Button(tileSetSnowButton, "snowButton", BigInteger.valueOf(2), GameStates.width-100 , 25, GameStates.animationDelay, "snow", GameStates.snowTile, "");
		Button treeButtonGUI = new Button(tileSetTreeButton, "treeButton", BigInteger.valueOf(3), GameStates.width- 136, 25, GameStates.animationDelay, "tree", GameStates.treeTile, "");
		Button snowTreeButtonGUI = new Button(tileSetSnowTreeButton, "snowTreeButton", BigInteger.valueOf(4), GameStates.width - 64, 25, GameStates.animationDelay, "tree", GameStates.snowTreeTile, "");
		Button undergroundGrassButtonGUI = new Button(tileSetUndergroundGrassButton, "undergroundGrassButton", BigInteger.valueOf(5), GameStates.width - 244, 25, GameStates.animationDelay, "underground", GameStates.undergroundGrassTile, "");
				
		ArrayList<Polygon> editorGUISpacePolygon = new ArrayList<Polygon>();
		editorGUISpacePolygon.add((new Polygon(new int[]{GameStates.width -250 , GameStates.width -250 ,GameStates.width -25, GameStates.width -25}, new int[]{25-5 , 25+GameStates.tileSetHeight+5 ,25+GameStates.tileSetHeight+5, 25-5}, 4) ));
		Space editorGUISpace = new Space(editorGUISpacePolygon, 0, 0, "editorGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
		
		EditorGUI floorEditor = new EditorGUI("floor", "cobble", 110, editorGUISpace);
		floorEditor.add(cobbleButtonGUI);
		floorEditor.add(grassButtonGUI);
		floorEditor.add(snowButtonGUI);
		floorEditor.add(treeButtonGUI);
		floorEditor.add(snowTreeButtonGUI);
		floorEditor.add(undergroundGrassButtonGUI);
		EditorGUIManager.add(floorEditor);
		
		Button playerMeGUIButton = new Button(tileSetUndergroundGrassButton, "playerMeButton", BigInteger.valueOf(0), 0, 0, GameStates.animationDelay, "Me", 0, "");
		
		PlayerListGUI playerEditorGUI = new PlayerListGUI("playerList", "standard", 0);
		playerEditorGUI.add(playerMeGUIButton);
		PlayerListGUIManager.add(playerEditorGUI);
		
		this.initContainers();
		
		/** there is no item at the mouseCursor */
		this.setMouseItem(null);
		
		/** init GUI elements */
		connectButton = new Space("rechteckButton.svg", BigInteger.valueOf(0), "connect.png", 0.5f);
		connectButton.setAllXY(100, 50);
	}
	
	public void initContainers() {
		ContainerGUIManager.containerGUIList = new HashMap<String, ContainerGUI>();
		ContainerGUIManager.idList = new ArrayList<String>();
		/** inventory GUI */
		int totalWidthInventory = GameStates.inventoryDistToFrameX*2 + (GameStates.inventorySlotDistX*2+GameStates.inventoryItemTileWidth)*getPlayerEntity().getInventory().getWidth();
		int totalHeightInventory = GameStates.inventoryDistToFrameY*2 + (GameStates.inventorySlotDistY*2+GameStates.inventoryItemTileHeight+GameStates.inventoryFontDistanceY)*getPlayerEntity().getInventory().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygon = new ArrayList<Polygon>();
		inventoryGUISpacePolygon.add((new Polygon(new int[]{0 , 0 , totalWidthInventory, totalWidthInventory}, new int[]{0 , totalHeightInventory, totalHeightInventory, 0}, 4) ));
		Space inventoryGUISpace = new Space(inventoryGUISpacePolygon, 0, 0, "inventoryGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
		
		ContainerGUI playerInventoryGUI = new ContainerGUI(this.getPlayerEntity().getInventory(), "Player Inventory", GameStates.inventoryPlayerX, GameStates.inventoryPlayerY, "", 0, inventoryGUISpace);
		ContainerGUIManager.add(playerInventoryGUI);
		
		/** use inventory GUI */
		int totalWidthInventoryUse = GameStates.inventoryDistToFrameX*2 + (GameStates.inventorySlotDistX*2+GameStates.inventoryItemTileWidth)*getPlayerEntity().getInventoryUse().getWidth();
		int totalHeightInventoryUse = GameStates.inventoryDistToFrameY*2 + (GameStates.inventorySlotDistY*2+GameStates.inventoryItemTileHeight+GameStates.inventoryFontDistanceY)*getPlayerEntity().getInventoryUse().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonUse = new ArrayList<Polygon>();
		inventoryGUISpacePolygonUse.add((new Polygon(new int[]{0 , 0 , totalWidthInventoryUse, totalWidthInventoryUse}, new int[]{0 , totalHeightInventoryUse, totalHeightInventoryUse, 0}, 4) ));
		Space inventoryGUISpaceUse = new Space(inventoryGUISpacePolygonUse, 0, 0, "inventoryGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
		
		ContainerGUI playerInventoryUseGUI = new ContainerGUI(this.getPlayerEntity().getInventoryUse(), "Player Use Inventory", GameStates.inventoryUsePlayerX, GameStates.inventoryUsePlayerY, "", 0, inventoryGUISpaceUse);
		ContainerGUIManager.add(playerInventoryUseGUI);
		
		/** body equipment GUI */
		int totalWidthInventoryEqBody = GameStates.inventoryDistToFrameX*2 + (GameStates.inventorySlotDistX*2+GameStates.inventoryItemTileWidth)*getPlayerEntity().getEquipmentBody().getWidth();
		int totalHeightInventoryEqBody = GameStates.inventoryDistToFrameY*2 + (GameStates.inventorySlotDistY*2+GameStates.inventoryItemTileHeight+GameStates.inventoryFontDistanceY)*getPlayerEntity().getEquipmentBody().getHeight();
		ArrayList<Polygon> inventoryGUISpacePolygonEqBody = new ArrayList<Polygon>();
		inventoryGUISpacePolygonEqBody.add((new Polygon(new int[]{0 , 0 , totalWidthInventoryEqBody, totalWidthInventoryEqBody}, new int[]{0 , totalHeightInventoryEqBody, totalHeightInventoryEqBody, 0}, 4) ));
		Space inventoryGUISpaceEqBody = new Space(inventoryGUISpacePolygonEqBody, 0, 0, "inventoryGUISpace", BigInteger.valueOf(0), new int[]{0, 0, 0}, true, 0.6f, 1.0f, 1.0f, "empty");
		
		ContainerGUI playerInventoryEqBodyGUI = new ContainerGUI(this.getPlayerEntity().getEquipmentBody(), "Player EqBody Inventory", GameStates.inventoryEqBodyPlayerX, GameStates.inventoryEqBodyPlayerY, "", 0, inventoryGUISpaceEqBody);
		ContainerGUIManager.add(playerInventoryEqBodyGUI);
	}
	
	/** config can be started allways (also for reset) */
	public void configGUI() {
		Button cobbleButtonGUI = EditorGUIManager.get("floor").get(BigInteger.valueOf(0));
//		cobbleButtonGUI.setAnimation(AnimationManager.manager.getAnimation(cobbleButtonGUI.getName()+"active", cobbleButtonGUI.getTileSet(), GameStates.button_active_start, GameStates.button_active_end, cobbleButtonGUI.getAnimation().getTimeBetweenAnimation()));
		cobbleButtonGUI.setActive();
		EditorGUIManager.get("floor").deactivateOthers(cobbleButtonGUI);
		EditorGUIManager.get("floor").setStrValue("cobble");
		EditorGUIManager.get("floor").setIntValue(110);
		
		Button playerMeGUIButton = PlayerListGUIManager.get("playerList").get(BigInteger.valueOf(0));
//		playerMeGUIButton.setAnimation(AnimationManager.manager.getAnimation(playerMeGUIButton.getName()+"passive", playerMeGUIButton.getTileSet(), GameStates.button_std_start, GameStates.button_std_end, playerMeGUIButton.getAnimation().getTimeBetweenAnimation()));
		playerMeGUIButton.setInactive();
		PlayerListGUIManager.get("playerList").deactivateOthers(playerMeGUIButton);
		PlayerListGUIManager.get("playerList").setStrValue("standard");
		PlayerListGUIManager.get("playerList").setIntValue(0);
	}
	
	public void initManagers() {
		/** clear the managers */
		LightManager.clear();
		ClientTextureManager.clear();
		
		lightTileDay = TileSetManager.manager.getTileSet(GameStates.lightTileSetNameDay, GameStates.lightTileWidth, GameStates.lightTileHeight);
		lightTileNight = TileSetManager.manager.getTileSet(GameStates.lightTileSetNameNight, GameStates.lightTileWidth, GameStates.lightTileHeight);
		int standardLight = GameStates.maxLightIntenityCutoff+2;
		Light light1 = new Light(BigInteger.valueOf(0), standardLight, new Point(0,0));
		LightManager.add(light1);
		Light light2 = new Light(BigInteger.valueOf(1), standardLight, new Point(200,300));
		LightManager.add(light2);
		light3 = new Light(BigInteger.valueOf(2), standardLight, new Point(800, 200));
		LightManager.add(light3);
		Light light4 = new Light(BigInteger.valueOf(3), standardLight, new Point(2000,3000));
		LightManager.add(light4);
		Light light3 = new Light(BigInteger.valueOf(4), standardLight, new Point(200,1000));
		LightManager.add(light3);
		
		/** add some agents to the Agentmanager */
		SimpleAgent agent;
		BigInteger runningID = BigInteger.valueOf(0);
		TileSet tileSet_SA = new TileSet(GameStates.standardTileNamePlayer, "standardPlayer", 32, 64);
		for (int i = 0; i <1; i++ ){
			float x = 400+ (int) (Math.random()*100) -100; 
			float y = -32+ (int) (Math.random()*200) -100;
			agent = new SimpleAgent(tileSet_SA, "simpleAgent", runningID, x, y, GameStates.animationDelay);
			Goal goal = new Goal(new WorldPosition(-500, -300)/*, playerEntity.getId(), playerEntity*/);
			Goal additionalGoal = new Goal(new WorldPosition(200,  0));
			Goal additionalGoal2 = new Goal(new WorldPosition(-500, 300));
			Goal additionalGoal3 = new Goal(new WorldPosition(400, -100+ (int) (Math.random()*20) -100));
			agent.setActualGoal(goal);
			agent.addGoal(additionalGoal);
			agent.addGoal(additionalGoal2);
			agent.addGoal(additionalGoal3);
			runningID = runningID.add(BigInteger.valueOf(1));
			AgentManager.add(agent);
		}
		
		
		/** add standard background texture */
		ClientTextureManager.manager.getTexture(GameStates.standardBackgroundTexture);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 * 
	 * Paints all important Objects to the GamePanel
	 * depends on the GameModus
	 */
	public void paintComponent(Graphics g2){
		Graphics2D g = (Graphics2D) g2;
		// Set Options of the Graphics-Object
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);

		// Paint in each GameModus
		if (GameModus.modus == GameModus.MAINMENU) {
			mainMenuPaint(g);
		} else 
		if (GameModus.modus == GameModus.LOADING) {
			loadingPaint(g);
			//GamePanel.gp.loadEntityList();
		} else
		if (GameModus.modus == GameModus.GAME) {
			gamePaint(g);
			//GamePanel.gp.loadEntityList();
		}
//		if (GameModus.modus == GameModus.SIM) {
//			simPaint(g);
//		}
		g.dispose();
	}
	

	/**
	 * @param g: Paint Objects to g
	 * @START-Modus
	 */	
	public void mainMenuPaint(Graphics2D g) {
		g.setPaintMode();
//		startButton.paint(g, 0, 0);
//		simulationButton.paint(g, 0, 0);
		connectButton.paint(g, 0, 0);
	}
	
	/**
	 * @param g: Paint Objects to g
	 * @GAME-Modus
	 */
	public void gamePaint(Graphics2D g){
		duration = System.currentTimeMillis() - oldTime;
		wholeDuration += duration;
		oldTime = System.currentTimeMillis();
		countFrames++;
		if(wholeDuration >= 1000) {
			GameWindow.gw.gameInfoConsole.appendInfo("Framerate: "+countFrames);
			System.out.println("Framerate: "+countFrames);
			countFrames = 0;
			wholeDuration = 0;
		}
		
		g.scale(this.getZoomFactor(), this.getZoomFactor());
		
//		g.clipRect((int) (2/this.getZoomFactor()), (int) (2/this.getZoomFactor()), (int) (GameStates.width/this.getZoomFactor()) ,(int) (GameStates.height/ this.getZoomFactor()));
//		g.drawOval((int) (2/this.getZoomFactor()), (int) (2/this.getZoomFactor()), (int) (GameStates.width/this.getZoomFactor()) ,(int) (GameStates.height/ this.getZoomFactor()));

		
		
		if (playerEntity != null) {
//			System.out.println(playerEntity.getName());
			GamePanel.gp.setViewPoint((int)playerEntity.getX()+(int)(playerEntity.getWidth()/2)-(int)(GameStates.getWidth()/2/zoomFactor), (int) playerEntity.getY()+(int)(playerEntity.getHeight()/2)-(int)(GameStates.getHeight()/2/zoomFactor));
		}
		Point oldViewPoint = new Point(this.getViewPointX(), this.getViewPointY());
		float tempZoom = GamePanel.gp.getZoomFactor();
		
		/** Paint Spaces */
		g.setPaintMode();
		List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
		for (BigInteger i: idListTempSpaces){
			Space space = SpaceManager.get(i);
			if(space != null) {
				if(space.getPolygon() != null) {
//					space.paint(g, (int) (-viewPointX),(int) (-viewPointY));
				}
			}
			else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Space");
		}
		
		/** define rect for underground */
		int localWidthUnderground = GameStates.ugrMapWidth * GameStates.ugrMapTileSetWidth;
		int localHeightUnderground = GameStates.ugrMapHeight * GameStates.ugrMapTileSetHeight;

		int minXUnderground = ((int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidthUnderground/2) / (localWidthUnderground)) * localWidthUnderground) - GameStates.factorOfViewDeleteDistance*localWidthUnderground;
		int maxXUnderground = ((int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidthUnderground/2) / (localWidthUnderground)) * localWidthUnderground) + GameStates.factorOfViewDeleteDistance*localWidthUnderground;
		int minYUnderground = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeightUnderground/2) / (localHeightUnderground)) * localHeightUnderground - GameStates.factorOfViewDeleteDistance*localHeightUnderground;
		int maxYUnderground = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeightUnderground/2) / (localHeightUnderground)) * localHeightUnderground + GameStates.factorOfViewDeleteDistance*localHeightUnderground;
		Rectangle rectUnderground = new Rectangle(minXUnderground, minYUnderground, maxXUnderground-minXUnderground, maxYUnderground-minYUnderground);

		
		/** define min and max for view distance */
		int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
		int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
		int minX = ((int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidth/2) / (localWidth)) * localWidth) - GameStates.factorOfViewDeleteDistance*localWidth;
		int maxX = ((int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getX()+localWidth/2) / (localWidth)) * localWidth) + GameStates.factorOfViewDeleteDistance*localWidth;
		int minY = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeight/2) / (localHeight)) * localHeight - GameStates.factorOfViewDeleteDistance*localHeight;
		int maxY = (int) Math.floor( (float) (GamePanel.gp.getPlayerEntity().getY()+localHeight/2) / (localHeight)) * localHeight + GameStates.factorOfViewDeleteDistance*localHeight;
		Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
		
//		if (isPaintEditSpaceArea() == true) {
//			/** paint the editSpaceArea */
//			g.setPaintMode();
//			editSpaceArea.setAllXY(minX, minY);
//			editSpaceArea.paint(g, (int) (-viewPointX),(int) (-viewPointY));
//		}
		/** Paint Maps */
		g.setPaintMode();
		UndergroundMapManager undergroundManager = GameWindow.gw.undergroundMapManagers.get("underground");
		List<Point> idListUndergroundMap = new ArrayList<Point>(undergroundManager.pointList);
		for (Point p: idListUndergroundMap){
			LocalUndergroundMap localMap = undergroundManager.get(p);
			if(localMap != null) {
				if ( rectUnderground.contains(p)) {
					BufferedImage tile = null;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							tile = localMap.getTileImage(k, l, GameWindow.gw.getTileUndergroundMapManager());
							if( tile != null) {
								g.drawImage(tile, (int) (localMap.getOrigin().x + k*GameStates.ugrMapTileSetWidth-viewPointX), (int) (localMap.getOrigin().y + l*GameStates.ugrMapTileSetHeight-viewPointY), this);
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!undergroundManager.getStayList().contains(p))
					undergroundManager.remove(p);
				}
			}
			else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
		}
		
		/** Paint Maps */
		g.setPaintMode();
		MapManager cobbleManager = GameWindow.gw.mapManagers.get("cobble");
		List<Point> idListcobbleMap = new ArrayList<Point>(cobbleManager.pointList);
		for (Point p: idListcobbleMap){
			LocalMap localMap = cobbleManager.get(p);
			if(localMap != null) {
				if ( rect.contains(p)) {
					BufferedImage tile = null;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							tile = localMap.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if( tile != null) {
								g.drawImage(tile, (int) (localMap.getOrigin().x + k*32-viewPointX), (int) (localMap.getOrigin().y + l*32-viewPointY), this);
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!cobbleManager.getStayList().contains(p))
					cobbleManager.remove(p);
				}
			}
			else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
		}
		
		MapManager grassManager = GameWindow.gw.mapManagers.get("grass");
		List<Point> idListGrassMap = new ArrayList<Point>(grassManager.pointList);
		for (Point p: idListGrassMap){
			LocalMap localMap = grassManager.get(p);
			if(localMap != null) {
				if ( rect.contains(p)) {
					BufferedImage tile = null;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							tile = localMap.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if( tile != null) {
								g.drawImage(tile, (int) (localMap.getOrigin().x + k*32-viewPointX), (int) (localMap.getOrigin().y + l*32-viewPointY), this);
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!grassManager.getStayList().contains(p))
					grassManager.remove(p);
				}
			}
			else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
		}
		MapManager desertManager = GameWindow.gw.mapManagers.get("desert");
		List<Point> idListdesertMap = new ArrayList<Point>(desertManager.pointList);
		for (Point p: idListdesertMap){
			LocalMap localMap = desertManager.get(p);
			if(localMap != null) {
				if ( rect.contains(p)) {
					BufferedImage tile = null;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							tile = localMap.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if( tile != null) {
								g.drawImage(tile, (int) (localMap.getOrigin().x + k*32-viewPointX), (int) (localMap.getOrigin().y + l*32-viewPointY), this);
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!desertManager.getStayList().contains(p))
					desertManager.remove(p);
				}
			}
			else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
		}
		MapManager snowManager = GameWindow.gw.mapManagers.get("snow");
		List<Point> idListSnowMap = new ArrayList<Point>(snowManager.pointList);
		for (Point p: idListSnowMap){
			LocalMap localMap = snowManager.get(p);
			if(localMap != null) {
				if ( rect.contains(p)) {
					BufferedImage tile = null;
					for (int k = 0; k < localMap.getLocalMap().length; k++) {
						for (int l = 0; l < localMap.getLocalMap()[0].length; l++) {
							tile = localMap.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if( tile != null) {
								g.drawImage(tile, (int) (localMap.getOrigin().x + k*32-viewPointX), (int) (localMap.getOrigin().y + l*32-viewPointY), this);
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!snowManager.getStayList().contains(p))
					snowManager.remove(p);
				}
			}
			else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
		}

		/** Paint Trees */
		g.setPaintMode();
		ObjectMapManager tree1MapManager = GameWindow.gw.objectMapManagers.get("tree1");
		ObjectMapManager tree2MapManager = GameWindow.gw.objectMapManagers.get("tree2");
		ArrayList<Point> idListTree1Map = new ArrayList<Point>(tree1MapManager.pointList);
		ArrayList<Point> idListTree2Map = new ArrayList<Point>(tree2MapManager.pointList);
		ArrayList<Point> pointSetTree = MyUtil.unionListNoDuplicates(idListTree1Map, idListTree2Map);
		for (Point p: pointSetTree){
			LocalMap localMap1 = tree1MapManager.get(p);
			LocalMap localMap2 = tree2MapManager.get(p);
			/** paint both tree parts simultaneously */
			if(localMap1 != null && localMap2 != null) {
				if ( rect.contains(p)) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap1.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap1.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
					BufferedImage tile1 = null;
					BufferedImage tile2 = null;
					for (int k = 0; k < localMap1.getLocalMap().length; k++) {
						for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
							tile1 = localMap1.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							tile2 = localMap2.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if(tile1 != null) {
								g.drawImage(tile1,(int) (localMap1.getOrigin().x + k*32-viewPointX), (int) (localMap1.getOrigin().y + l*32-viewPointY), this);	
							}
							if(tile2 != null) {
								g.drawImage(tile2, (int) (localMap2.getOrigin().x + k*32-viewPointX), (int) (localMap2.getOrigin().y+ GameStates.distanceOfSecondTreeLayer + l*32-viewPointY), this);	
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!tree1MapManager.getStayList().contains(p))
					tree1MapManager.remove(p);
					if (!tree2MapManager.getStayList().contains(p))
					tree2MapManager.remove(p);
				}
			}
			else if(localMap1 != null) {
				if ( rect.contains(p)) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap1.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap1.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
					BufferedImage tile1 = null;
					for (int k = 0; k < localMap1.getLocalMap().length; k++) {
						for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
							tile1 = localMap1.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if(tile1 != null) {
								g.drawImage(tile1, (int) (localMap1.getOrigin().x + k*32-viewPointX), (int) (localMap1.getOrigin().y + l*32-viewPointY), this);	
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!tree1MapManager.getStayList().contains(p))
					tree1MapManager.remove(p);
				}
			} else if(localMap2 != null) {
				if ( rect.contains(p)) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap2.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap2.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
					BufferedImage tile2 = null;
					for (int k = 0; k < localMap2.getLocalMap().length; k++) {
						for (int l = 0; l < localMap2.getLocalMap()[0].length; l++) {
							tile2 = localMap2.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if(tile2 != null) {
								g.drawImage(tile2, (int) (localMap2.getOrigin().x + k*32-viewPointX), (int) (localMap2.getOrigin().y+ GameStates.distanceOfSecondTreeLayer + l*32-viewPointY), this);	
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!tree2MapManager.getStayList().contains(p))
					tree2MapManager.remove(p);
				}
			}
		}	
		

		/** Paint Server-Entities */
		g.setPaintMode();
		List<BigInteger> idListTempEntities = new ArrayList<BigInteger>(EntityManager.idList);
		for (BigInteger i: idListTempEntities) {
			Entity e= EntityManager.get(i);
			if(e != null) {
				if(e.getSprite().getImage() != null) {
					g.drawImage(e.getSprite().getImage(), (int) (e.getX()-viewPointX), (int) (e.getY()-viewPointY), this);
				}
			}
			else
			GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
		}
		
		/** Paint Items */
		g.setPaintMode();
		List<BigInteger> idListTempItems = new ArrayList<BigInteger>(ItemManager.idList);
		for (BigInteger i: idListTempItems) {
			Item tempItem = ItemManager.get(i);
			if (tempItem != null && tempItem.isVisible()) {
				Entity e= tempItem.getEntity();
				if(e != null) {
					if(e.getSprite().getImage() != null) {
						g.drawImage(e.getSprite().getImage(), (int) (e.getX()-viewPointX), (int) (e.getY()-viewPointY), this);
					}
				}
				else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
			} else {
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Item");
			}
		}
		
		/** Paint Agents */
		g.setPaintMode();
		List<BigInteger> idListTempAgents = new ArrayList<BigInteger>(AgentManager.idList);
		for (BigInteger i: idListTempAgents) {
			Agent tempAgent = AgentManager.get(i);
			if (tempAgent != null && tempAgent.isVisible()) {
				Entity e= tempAgent;
				if(e != null) {
					if(e.getSprite().getImage() != null) {
						g.drawImage(e.getSprite().getImage(), (int) (e.getX()-viewPointX), (int) (e.getY()-viewPointY), this);
					}
				}
				else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Agent");
			} else {
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Agent");
			}
		}
		
		
		/** player painting */
		if (playerEntity != null) {
			GamePanel.gp.setViewPoint((int)playerEntity.getX()+(int)(playerEntity.getWidth()/2)-(int)(GameStates.getWidth()/2/zoomFactor), (int) playerEntity.getY()+(int)(playerEntity.getHeight()/2)-(int)(GameStates.getHeight()/2/zoomFactor));
			/** paint player Entity */
			if(tempZoom == GamePanel.gp.getZoomFactor()) {
				if(playerEntity.getSprite().getImage() != null) {
					g.setPaintMode();
					g.drawImage(playerEntity.getSprite().getImage(), (int) (playerEntity.getX()-viewPointX), (int) (playerEntity.getY()-viewPointY), this);
				}
			}
		}
		
		/** reset ViewPoint */
		GamePanel.gp.setViewPoint(oldViewPoint.x, oldViewPoint.y);

		/** paint the other players */
		g.setPaintMode();
		if(PlayerManager.size() >0) {
			List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
			for (BigInteger i: idListTempPlayers) {
				PlayerEntity playerEntity= PlayerManager.get(i);
				if(playerEntity != null) {
					if(playerEntity.getSprite().getImage() != null && playerEntity.isVisible()) {
						g.drawImage(playerEntity.getSprite().getImage(), (int) (playerEntity.getX()-viewPointX), (int) (playerEntity.getY()-viewPointY), this);
					}
				}
				else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
			}
		}
		/** reset Scale for all GUI-Elements */
		
		/** paint overlay-Tiles */
		g.setPaintMode();
		ObjectMapManager overlayTree1MapManager = GameWindow.gw.objectMapManagers.get("overlayTree1");
		ObjectMapManager overlayTree2MapManager = GameWindow.gw.objectMapManagers.get("overlayTree2");
		ArrayList<Point> idListOverlayTree1Map = new ArrayList<Point>(overlayTree1MapManager.pointList);
		ArrayList<Point> idListOverlayTree2Map = new ArrayList<Point>(overlayTree2MapManager.pointList);
		ArrayList<Point> pointSetOverlayTree = MyUtil.unionListNoDuplicates(idListOverlayTree1Map, idListOverlayTree2Map);
		int distanceOverlayTree = GameStates.overlayOffsetY;
		for (Point p: pointSetOverlayTree){
			LocalMap localMap1 = overlayTree1MapManager.get(p);
			LocalMap localMap2 = overlayTree2MapManager.get(p);
			/** paint both tree parts simultaneously */
			if(localMap1 != null && localMap2 != null) {
				if ( rect.contains(p)) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap1.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap1.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
					BufferedImage tile1 = null;
					BufferedImage tile2 = null;
					for (int k = 0; k < localMap1.getLocalMap().length; k++) {
						for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
							tile1 = localMap1.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							tile2 = localMap2.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if(tile1 != null) {
								g.drawImage(tile1, (int) (localMap1.getOrigin().x + k*32-viewPointX), distanceOverlayTree +(int) (localMap1.getOrigin().y + l*32-viewPointY), this);	
							}
							if(tile2 != null) {
								g.drawImage(tile2, (int) (localMap2.getOrigin().x + k*32-viewPointX), distanceOverlayTree +(int) (localMap2.getOrigin().y+ GameStates.distanceOfSecondTreeLayer + l*32-viewPointY), this);	
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!overlayTree1MapManager.getStayList().contains(p))
					overlayTree1MapManager.remove(p);
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!overlayTree2MapManager.getStayList().contains(p))
					overlayTree2MapManager.remove(p);
					
				}
			}
			else if(localMap1 != null) {
				if ( rect.contains(p)) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap1.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap1.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
					BufferedImage tile1 = null;
					for (int k = 0; k < localMap1.getLocalMap().length; k++) {
						for (int l = 0; l < localMap1.getLocalMap()[0].length; l++) {
							tile1 = localMap1.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if(tile1 != null) {
								g.drawImage(tile1, (int) (localMap1.getOrigin().x + k*32-viewPointX), distanceOverlayTree+  (int) (localMap1.getOrigin().y + l*32-viewPointY), this);	
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!overlayTree1MapManager.getStayList().contains(p))
					overlayTree1MapManager.remove(p);
				}
			} else if(localMap2 != null) {
				if ( rect.contains(p)) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap2.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap2.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
					BufferedImage tile2 = null;
					for (int k = 0; k < localMap2.getLocalMap().length; k++) {
						for (int l = 0; l < localMap2.getLocalMap()[0].length; l++) {
							tile2 = localMap2.getTileImage(k, l, GameWindow.gw.getTileMapManager());
							if(tile2 != null) {
								g.drawImage(tile2, (int) (localMap2.getOrigin().x + k*32-viewPointX), distanceOverlayTree + (int) (localMap2.getOrigin().y+ GameStates.distanceOfSecondTreeLayer + l*32-viewPointY), this);	
							}
						}
					}
				} else {
					/** remove if too far away */
					/** but only if the localMap is not on the stayList (which is used for distant maps) */
					if (!overlayTree2MapManager.getStayList().contains(p))
					overlayTree2MapManager.remove(p);
				}
			}
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
		}
//		ObjectMapManager overlayTree1MapManager = GameWindow.gw.objectMapManagers.get("overlayTree1");
//		List<Point> idListOverlayTree1Map = new ArrayList<Point>(overlayTree1MapManager.pointList);
//		for (Point p : idListOverlayTree1Map) {
//			LocalMap localOverlayMap = overlayTree1MapManager.get(p);
//			if(localOverlayMap != null) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localOverlayMap.getOrigin().x) < (GameStates.mapTotalWidth*2)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localOverlayMap.getOrigin().y) < (GameStates.mapTotalHeight*2))) {
//					BufferedImage overlayTile = null;
//					for (int k = 0; k < localOverlayMap.getLocalMap().length; k++) {
//						for (int l = 0; l < localOverlayMap.getLocalMap()[0].length; l++) {
//							overlayTile = localOverlayMap.getTileImage(k, l);
//							if (localOverlayMap != null) overlayTile = localOverlayMap.getTileImage(k, l);
//							if( (overlayTile != null) && (overlayTile != null)) {
//								g.drawImage(overlayTile, (int) (localOverlayMap.getOrigin().x + k*32-viewPointX), (int) (localOverlayMap.getOrigin().y + l*32-viewPointY), this);
//							}
//						}
//					}
//				} else {
//					/** remove if too far away */
//					overlayTree1MapManager.remove(p);
//				}
//			}
//			else
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
//		}
		
		// paint the console
//		if (GameWindow.gw.getShowConsole()) {
//			GameWindow.gw.gameConsole.paint(g, 0, 0);
//		}
//		// paint the info-console
//		if (GameWindow.gw.getShowInfoConsole()) {
////			GameWindow.gw.gameInfoConsole.update();
//			GameWindow.gw.gameInfoConsole.paint(g, 0, 0);
//		}
//		GameWindow.gw.setFocusable(true);
		if (GameWindow.gw.hasLight()) {
			g.setPaintMode();
			/** calc region */
			light3.setLocation(new Point((int ) (playerEntity.getX()+playerEntity.getWidth()/2) , (int) (playerEntity.getY()+playerEntity.getHeight()/2) )) ;
			LightManager.overwrite(light3);
			Rectangle lightRect = new Rectangle((int)(playerEntity.getX()-((float)GameStates.lightMapWidth/2) - (playerEntity.getX() % GameStates.mapTileSetWidth)), (int)(playerEntity.getY()-((float)GameStates.lightMapHeight/2) - (playerEntity.getY() % GameStates.mapTileSetHeight)), GameStates.lightMapWidth, GameStates.lightMapHeight);
	//		if (!LightManager.getLightsOfRegion(lightRect).isEmpty()) {
			/** get shadow tiles depending on day or night */
			TileSet lightTile = null;
			if (GameWindow.gw.isNight()) lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetNameNight, GameStates.lightTileWidth, GameStates.lightTileHeight);
			else lightTile = TileSetManager.manager.getTileSet(GameStates.lightTileSetNameDay, GameStates.lightTileWidth, GameStates.lightTileHeight);
			int[][] lightMap = LightManager.getLightMap(lightRect);
			BufferedImage lightImage = null;
			int lightX = 0;
			int lightY = 0;
			for(int i = 0; i < lightMap.length; i ++ ) {
				for (int j = 0; j < lightMap[0].length; j++) {
					lightX = lightRect.x+i *GameStates.lightTileWidth;
					lightY = lightRect.y+j *GameStates.lightTileHeight;
					lightImage = lightTile.getTileImage(Math.min(lightMap[i][j], GameStates.maxLightIntenityCutoff)) ;
					g.drawImage(lightImage, lightX-viewPointX, lightY-viewPointY, this);
//					 g.drawImage(lightImage, lightX-viewPointX, lightY-viewPointY, this);
				}
			}
		}
//		}
		
		
		GameWindow.gw.requestFocus();
	}
	
	/**
	 * @param g: Paint Objects to g
	 * @GAME-Modus
	 */
	public void loadingPaint(Graphics2D g){
		g.scale(this.getZoomFactor(), this.getZoomFactor());

		g.setPaintMode();
//		List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
//		for (BigInteger i: idListTempSpaces){
//			Space space = SpaceManager.get(i);
//			if(space != null) {
//				if(space.getPolygon() != null) {
////					GameWindow.gw.gameInfoConsole.appendInfo("Space "+space.getId());
//					space.paint(g, (int) (-viewPointX),(int) (-viewPointY));
//				}
//			}
//			else
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Space");
//		}
		GameWindow.gw.requestFocus();
	}
	
//	/** paint the simulation modus */
//	public void simPaint(Graphics2D g) {
//		g.scale(this.getZoomFactor(), this.getZoomFactor());
//		if (playerEntity != null) {
//			GamePanel.gp.setViewPoint((int)playerEntity.getX()+(int)(playerEntity.getWidth()/2)-(int)(GameStates.getWidth()/2/zoomFactor), (int) playerEntity.getY()+(int)(playerEntity.getHeight()/2)-(int)(GameStates.getHeight()/2/zoomFactor));
//		}
//		/** Paint Spaces */
////		GameWindow.gw.gameInfoConsole.appendInfo("IdList Spaces: "+SpaceManager.idList.size());
//		g.setPaintMode();
//		List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
//		for (BigInteger i: idListTempSpaces){
//			Space space = SpaceManager.get(i);
//			if(space != null) {
//				if(space.getPolygon() != null) {
////					GameWindow.gw.gameInfoConsole.appendInfo("Space "+space.getId());
//					space.paint(g, (int) (-viewPointX),(int) (-viewPointY));
//				}
//			}
//			else
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Space");
//		}
//
//		/** Paint Server-Entities */
//		g.setPaintMode();
////		GameWindow.gw.gameInfoConsole.appendInfo("IdList Entities: "+EntityManager.size());
////		for (int i = 0; i < EntityManager.size(); i++) {
//		List<BigInteger> idListTempEntities = new ArrayList<BigInteger>(EntityManager.idList);
//		for (BigInteger i: idListTempEntities) {
//			Entity e= EntityManager.get(i);
//			if(e != null) {
//				if(e.getSprite().getImage() != null) {
////					GameWindow.gw.gameInfoConsole.appendInfo("Entity "+e.getId());
//					g.drawImage(e.getSprite().getImage(), (int) (e.getX()-viewPointX), (int) (e.getY()-viewPointY), this);
//				}
//			}
//			else
//			GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
//		}
//		
//		
//		g.setPaintMode();
//		if(playerEntity != null) {
//			
//			if(playerEntity.getSprite().getImage() != null) {
////				GameWindow.gw.gameInfoConsole.appendInfo("Entity "+e.getId());
//				g.drawImage(playerEntity.getSprite().getImage(), (int) (playerEntity.getX()-viewPointX), (int) (playerEntity.getY()-viewPointY), this);
//			}
//		}else
//			GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
//		
//		g.setPaintMode();
//		if(PlayerManager.size() >0) {
//			List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
//			for (BigInteger i: idListTempPlayers) {
//				Entity playerEntity= PlayerManager.get(i);
//				if(playerEntity != null) {
//					if(playerEntity.getSprite().getImage() != null) {
//						g.drawImage(playerEntity.getSprite().getImage(), (int) (playerEntity.getX()-viewPointX), (int) (playerEntity.getY()-viewPointY), this);
//					}
//				}
//				else
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
//			}
//		}
//		/** reset Scale for all GUI-Elements */
//		GameWindow.gw.requestFocus();
//	}

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
//		if (pause) GameWindow.gw.item21.setText("Start");
//		else GameWindow.gw.item21.setText("Pause");
	}
	
	public boolean isPaused() {
		return this.pause;
	}
	
	public boolean isAllwaysAnimated() {
		return this.allwaysAnimated;
	}
	
	public void setAllwaysAnimated(boolean allwaysAnimated){
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
	
	
	
	public boolean isServerInitialized(){
		return serverInitialized;
	}

	public void setServerInitialized(boolean b) {
		serverInitialized = b;
	}
	
	public void setViewPoint(int x, int y) {
		this.viewPointX = x;
		this.viewPointY = y;
		if (viewPointX < minViewPointX) viewPointX = minViewPointX;
		if (viewPointY < minViewPointY) viewPointY = minViewPointY;
		if (viewPointX > maxViewPointX) viewPointX = maxViewPointX;
		if (viewPointY > maxViewPointY) viewPointY = maxViewPointY;
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
//		System.out.println("dist: "+(System.currentTimeMillis() -playerEntity.getAnimation().getInstantOfAnimation()));
//		System.out.println("nr.: "+((System.currentTimeMillis() - (int) Math.floor(playerEntity.getAnimation().getInstantOfAnimation()))/ playerEntity.getAnimation().getTimeBetweenAnimation())%4);
		
	}
	
	public void setPlayerEntity (PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	
	public PlayerEntity getPlayerEntity() {
		return this.playerEntity;
	}
	
	public void setPlayerEntityName(String strname) {
		this.playerEntity.setName(strname);
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
			GameWindow.gw.send(ClientMessages.addItem(mouseItem.getId(), mouseItem.getItemCode(), mouseItem.getCount(), mouseItem.getCapacity(), p.x, p.y, mouseItem.getEntity().getMX(), mouseItem.getEntity().getMY(), mouseItem.getName(), mouseItem.getEntity().getTileSet().getFileName(), mouseItem.getEntity().getName(), mouseItem.getStates()));
			
//			GameWindow.gw.send(ClientMessages.addItem(mouseItem.getId()));
			for (String channelName : GameWindow.gw.getSpaceChannels().values()) {
				ClientChannel channel = GameWindow.gw.getChannelByName(channelName);
				try {
					channel.send(ClientMessages.addCompleteItem(mouseItem.getItemCode(), mouseItem.getId(), mouseItem.getName(), p.x, p.y, mouseItem.getCount(), new float[1]));
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			this.mouseItem = null;
		}
	}
}
