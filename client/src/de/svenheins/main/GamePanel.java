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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;

import de.svenheins.objects.Entity;
import de.svenheins.objects.IngameConsole;
import de.svenheins.objects.IngameWindow;
import de.svenheins.objects.LocalMap;
import de.svenheins.objects.Player;
import de.svenheins.objects.PlayerEntity;
import de.svenheins.objects.Space;
import de.svenheins.objects.TileSet;

import de.svenheins.threads.AnimationThread;
import de.svenheins.threads.ChannelUpdateMapsThread;
import de.svenheins.threads.ChannelUpdateThread;
import de.svenheins.threads.CollisionThread;
import de.svenheins.threads.GraphicThread;
import de.svenheins.threads.InputThread;
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
	public PlayerEntity playerEntity;
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
	public static GamePanel gp;
	public static String resourcePath = "/resources/";
	public static String svgPath = "/resources/svg/";
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
	private String paintLayer = "cobble";
	private int paintType = 110;
	private boolean paintEditSpaceArea = true;
	
	private ArrayList<Polygon> editSpaceAreaPolygon = new ArrayList<Polygon>();
	private Space editSpaceArea;
	
	
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
	}
	
	/**
	 * init(): Initialize all variables for the first time
	 */
	public void init() {

		p = new Player("Spieler1", new InputHandler(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE, KeyEvent.VK_P, KeyEvent.VK_ESCAPE, KeyEvent.VK_I, KeyEvent.VK_1, KeyEvent.VK_2));
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
		
		/** init GUI elements */
		connectButton = new Space("rechteckButton.svg", BigInteger.valueOf(0), "connect.png", 0.5f);
		connectButton.setAllXY(100, 50);
		
		ClientTextureManager.manager.getTexture(GameStates.standardBackgroundTexture);
		
		/** space that describes the paint area */
		editSpaceAreaPolygon.add((new Polygon(new int[]{0 , 0 , 2* GameStates.factorOfViewDeleteDistance*GameStates.mapTotalWidth,2* GameStates.factorOfViewDeleteDistance*GameStates.mapTotalWidth}, new int[]{0,2*GameStates.factorOfViewDeleteDistance*GameStates.mapTotalHeight, 2*GameStates.factorOfViewDeleteDistance*GameStates.mapTotalHeight,0}, 4)));
		editSpaceArea = new Space(editSpaceAreaPolygon, 0, 0, "editSpaceRegion", BigInteger.valueOf(0), new int[]{0, 60, 20}, true, 0.2f, 1.0f, 1.0f, "empty");
		
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
		this.setViewPoint(362, 181);
		this.minViewPointX = -30000;//362;
		this.minViewPointY = -30000;//181;
		this.maxViewPointX = 34416;
		this.maxViewPointY = 26169;
		this.setZoomFactor(1.0f);
		this.setRotationDegree(0);
		this.setDeleteModus(false);
		this.setPaintType(110);
		this.setPaintLayer("cobble");
		
		// Modify the Cursor
		//Cursor cursor = getToolkit().createCustomCursor(new ImageIcon(getClass().getResource(resourcePath+"images/"+"cursor.png")).getImage(), new Point(0,0), "Cursor");
		//this.setCursor(cursor);
		
		mainMenu = new IngameWindow();
		
		// Config PlayerManager
//		PlayerManager.playerList.add(s);
//		PlayerManager.playerList.add(s2);

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
		g.scale(this.getZoomFactor(), this.getZoomFactor());
		if (playerEntity != null) {
			GamePanel.gp.setViewPoint((int)playerEntity.getX()+(int)(playerEntity.getWidth()/2)-(int)(GameStates.getWidth()/2/zoomFactor), (int) playerEntity.getY()+(int)(playerEntity.getHeight()/2)-(int)(GameStates.getHeight()/2/zoomFactor));
		}
		/** Paint Spaces */
//		GameWindow.gw.gameInfoConsole.appendInfo("IdList Spaces: "+SpaceManager.idList.size());
		g.setPaintMode();
		List<BigInteger> idListTempSpaces = new ArrayList<BigInteger>(SpaceManager.idList);
		for (BigInteger i: idListTempSpaces){
			Space space = SpaceManager.get(i);
			if(space != null) {
				if(space.getPolygon() != null) {
//					GameWindow.gw.gameInfoConsole.appendInfo("Space "+space.getId());
					space.paint(g, (int) (-viewPointX),(int) (-viewPointY));
				}
			}
			else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Space");
		}
		

		
		/** define min and max for view distance */
		int localWidth = GameStates.mapWidth * GameStates.mapTileSetWidth;
		int localHeight = GameStates.mapHeight * GameStates.mapTileSetHeight;
		int minX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) - GameStates.factorOfViewDeleteDistance*localWidth;
		int maxX = ((int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getX() / (localWidth)) * localWidth) + GameStates.factorOfViewDeleteDistance*localWidth;
		int minY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight - GameStates.factorOfViewDeleteDistance*localHeight;
		int maxY = (int) Math.floor( (float) GamePanel.gp.getPlayerEntity().getY() / (localHeight)) * localHeight + GameStates.factorOfViewDeleteDistance*localHeight;
		Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
		
		if (isPaintEditSpaceArea() == true) {
			/** paint the editSpaceArea */
			g.setPaintMode();
			editSpaceArea.setAllXY(minX, minY);
			editSpaceArea.paint(g, (int) (-viewPointX),(int) (-viewPointY));
		}
		
		/** Paint Maps */
		g.setPaintMode();
		MapManager cobbleManager = GameWindow.gw.mapManagers.get("cobble");
		List<Point> idListcobbleMap = new ArrayList<Point>(cobbleManager.pointList);
		for (Point p: idListcobbleMap){
			LocalMap localMap = cobbleManager.get(p);
			if(localMap != null) {
				if ( rect.contains(p)) {
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
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
				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
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
					grassManager.remove(p);
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
//				if ( ((int) Math.abs(playerEntity.getX()-GameStates.mapTotalWidth/2 -localMap.getOrigin().x) < (GameStates.mapTotalWidth*GameStates.factorOfViewDeleteDistance)) && ((int) Math.abs(playerEntity.getY()-GameStates.mapTotalHeight/2 -localMap.getOrigin().y) < (GameStates.mapTotalHeight*GameStates.factorOfViewDeleteDistance))) {
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
								g.drawImage(tile1, (int) (localMap1.getOrigin().x + k*32-viewPointX), (int) (localMap1.getOrigin().y + l*32-viewPointY), this);	
							}
							if(tile2 != null) {
								g.drawImage(tile2, (int) (localMap2.getOrigin().x + k*32-viewPointX), (int) (localMap2.getOrigin().y+ GameStates.distanceOfSecondTreeLayer + l*32-viewPointY), this);	
							}
						}
					}
				} else {
					/** remove if too far away */
					tree1MapManager.remove(p);
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
					tree2MapManager.remove(p);
				}
			}
//				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Map");
		}	
		

		/** Paint Server-Entities */
		g.setPaintMode();
//		GameWindow.gw.gameInfoConsole.appendInfo("IdList Entities: "+EntityManager.size());
//		for (int i = 0; i < EntityManager.size(); i++) {
		List<BigInteger> idListTempEntities = new ArrayList<BigInteger>(EntityManager.idList);
		for (BigInteger i: idListTempEntities) {
			Entity e= EntityManager.get(i);
			if(e != null) {
				if(e.getSprite().getImage() != null) {
//					GameWindow.gw.gameInfoConsole.appendInfo("Entity "+e.getId());
					g.drawImage(e.getSprite().getImage(), (int) (e.getX()-viewPointX), (int) (e.getY()-viewPointY), this);
				}
			}
			else
			GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
		}
		
		/** paint player Entity */
		g.setPaintMode();
		if(playerEntity != null) {
			if(playerEntity.getSprite().getImage() != null) {
				g.drawImage(playerEntity.getSprite().getImage(), (int) (playerEntity.getX()-viewPointX), (int) (playerEntity.getY()-viewPointY), this);
			}
		}else
			GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
		
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
					overlayTree1MapManager.remove(p);
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
	}
	
	public void setPlayerEntity (PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	
	public PlayerEntity getPlayerEntity() {
		return this.playerEntity;
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
}
