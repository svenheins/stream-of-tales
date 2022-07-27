package de.svenheins.main;
import de.svenheins.handlers.ConsoleInputHandler;
import de.svenheins.handlers.InputHandler;
import de.svenheins.handlers.MouseHandler;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;

import de.svenheins.objects.Entity;
import de.svenheins.objects.IngameConsole;
import de.svenheins.objects.IngameWindow;
import de.svenheins.objects.Player;
import de.svenheins.objects.Space;

import de.svenheins.threads.AnimationThread;
import de.svenheins.threads.CollisionThread;
import de.svenheins.threads.GraphicThread;
import de.svenheins.threads.InputThread;
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
	public Entity playerEntity;
//	public Entity eye;
//	public Space space;

	
//	private ConsoleInputHandler consoleInput;
	private MoveThread moveThread;
	private InputThread inputThread;
	private MouseHandler mouseHandler;
	
	public static ServerUpdateThread serverUpdateThread;
	private GraphicThread graphicThread;
	private CollisionThread collisionThread;
	private AnimationThread animationThread;
	public static GamePanel gp;
	public static String resourcePath = "/resources/";
	public static String svgPath = "/resources/svg/";
	public long last;
	public boolean showStats;
	private boolean serverInitialized;
	private boolean pause, menu;
	private boolean allwaysAnimated;
	private int viewPointX, viewPointY;
	private int maxViewPointX, maxViewPointY, minViewPointX, minViewPointY;
	
	public IngameWindow mainMenu;
	
	public Space startButton;
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
	}
	
	/**
	 * init(): Initialize all variables for the first time
	 */
	public void init() {
		
		// Object Initialization
//		space = new Space("Zeichnung.svg", new Color(20, 230, 40), true, 0.9f);
//		space1 = new Space("Zeichnung.svg", new Color(100, 120, 240), true, 0.4f);
//		space2 = new Space("Quadrat.svg", new Color(230, 20, 40), false, 1.0f);
//		space3 = new Space("Quadratx1_einrast.svg", new Color(20, 230, 230), true, 0.5f);
//		space4 = new Space("Quadratx1_shift.svg", new Color(20, 230, 230), true, 0.5f);
//		// GameObjects (Entities)
//		shipArray = new String[]{"ship.png", "ship2.png", "ship3.png", "ship2.png"};
//		s = new ShipEntity(shipArray, 500, 0);
//		s.getAnimation().setTimeBetweenAnimation(100);
//		s.setY(GameWindow.gw.hoehe-s.getHeight()-30);
//		s2 = new ShipEntity("ship.png", 600, 0);
//		s2.setY(GameWindow.gw.hoehe-s2.getHeight()-30);
		p = new Player("Spieler1", new InputHandler(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE, KeyEvent.VK_P, KeyEvent.VK_ESCAPE, KeyEvent.VK_I));
//		p2 = new Player("Spieler2", new InputHandler(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S,KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_O));
		players = new Player[]{p}; 
		playerEntity = new Entity("ship.png", BigInteger.valueOf(0), 500, 200, 0, 0);
//		ships = new ShipEntity[]{s, s2};
//		eye = new Entity("eye.png", 1, 150, 100);
//		space = new Space("Start.svg", 1000000, new Color(150, 30, 40), true, 0.3f);
		
//		consoleInput = new ConsoleInputHandler();
		
		// Init GUI-Objects
		startButton = new Space("rechteckButton.svg", BigInteger.valueOf(0), "Start.png", 0.5f);
		startButton.setAllXY(100, 120);
		connectButton = new Space("rechteckButton.svg", BigInteger.valueOf(0), "connect.png", 0.5f);
		connectButton.setAllXY(100, 50);
		
//		gameConsole = new IngameConsole();
		
		// Init the input
		// Keyboard
		GameWindow.gw.addKeyListener(p.getInputHandler());
//		gp.addKeyListener(p2.getInputHandler());
//		gp.addKeyListener(consoleInput);
		// Mouse
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());
		
		// Init Time
		gp.last = System.currentTimeMillis();
		
		// Init Threads
		moveThread = new MoveThread(System.currentTimeMillis());
		inputThread = new InputThread(players, playerEntity);
		graphicThread = new GraphicThread(this);
		collisionThread = new CollisionThread();
		animationThread = new AnimationThread();
		serverUpdateThread = new ServerUpdateThread(System.currentTimeMillis());
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
		this.setViewPoint(362, 181);
		this.minViewPointX = 362;
		this.minViewPointY = 181;
		this.maxViewPointX = 34416;
		this.maxViewPointY = 26169;
		
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
	public void paint(Graphics g2){
		Graphics2D g = (Graphics2D) g2;
		// Set Options of the Graphics-Object
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g);

		// Paint in each GameModus
		if (GameModus.modus == GameModus.MAINMENU) {
			mainMenuPaint(g);
		} else 
		if (GameModus.modus == GameModus.GAME) {
			gamePaint(g);
			//GamePanel.gp.loadEntityList();
		}
		g.dispose();
	}
	

	/**
	 * @param g: Paint Objects to g
	 * @START-Modus
	 */	
	public void mainMenuPaint(Graphics2D g) {
		g.setPaintMode();
		startButton.paint(g, 0, 0);
		connectButton.paint(g, 0, 0);
	}
	
	/**
	 * @param g: Paint Objects to g
	 * @GAME-Modus
	 */
	public void gamePaint(Graphics2D g){
		if (playerEntity != null) {
			GamePanel.gp.setViewPoint((int)playerEntity.getX()-(GameStates.getWidth()/2), (int) playerEntity.getY()-(GameStates.getHeight()/2));
		}
//		System.out.println("Viewpoint x="+viewPointX+" y="+viewPointY);
		/** Paint Spaces */
		g.setPaintMode();
		// First sort by Z-index then draw objects
//		SpaceManager.sortZIndex();
//		for (int i = 0; i< SpaceManager.sizeSorted(); i++){
//			Space space = SpaceManager.getSorted(i);
//			if(space != null) {
//				if(space.getPolygon() != null) {
//					space.paint(g, (int) (viewPointX),(int) (viewPointY));
//				}
//			}
//		}
		
//		SpaceManager.sortZIndex();
//		for (int i = 0; i< SpaceManager.size(); i++){
//		GameWindow.gw.gameInfoConsole.appendInfo("IdList Spaces: "+SpaceManager.idList.size());
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
		
		
		g.setPaintMode();
		if(playerEntity != null) {
			
			if(playerEntity.getSprite().getImage() != null) {
//				GameWindow.gw.gameInfoConsole.appendInfo("Entity "+e.getId());
				g.drawImage(playerEntity.getSprite().getImage(), (int) (playerEntity.getX()-viewPointX), (int) (playerEntity.getY()-viewPointY), this);
			}
		}else
			GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
		
		g.setPaintMode();
		if(PlayerManager.size() >0) {
			List<BigInteger> idListTempPlayers = new ArrayList<BigInteger>(PlayerManager.idList);
			for (BigInteger i: idListTempPlayers) {
				Entity playerEntity= PlayerManager.get(i);
				if(playerEntity != null) {
					if(playerEntity.getSprite().getImage() != null) {
//						GameWindow.gw.gameInfoConsole.appendInfo("Entity "+e.getId());
						g.drawImage(playerEntity.getSprite().getImage(), (int) (playerEntity.getX()-viewPointX), (int) (playerEntity.getY()-viewPointY), this);
					}
				}
				else
				GameWindow.gw.gameInfoConsole.appendInfo("I got a NULL Entity");
			}
		}
		
		g.setPaintMode();
		// paint the Menu
		if (menu) {
			mainMenu.paint(g, 0, 0);
		}
		// paint the console
		if (GameWindow.gw.getShowConsole()) {
			GameWindow.gw.gameConsole.paint(g, 0, 0);
		}
		// paint the info-console
		if (GameWindow.gw.getShowInfoConsole()) {
//			GameWindow.gw.gameInfoConsole.update();
			GameWindow.gw.gameInfoConsole.paint(g, 0, 0);
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
	
	public void setPause(boolean pause) {
		this.pause = pause;
		if (pause) GameWindow.gw.item21.setText("Start");
		else GameWindow.gw.item21.setText("Pause");
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
}
