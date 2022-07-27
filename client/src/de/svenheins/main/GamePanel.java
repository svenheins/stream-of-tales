package de.svenheins.main;
import de.svenheins.handlers.ConsoleInputHandler;
import de.svenheins.handlers.MouseHandler;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import de.svenheins.managers.EnemyManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.SpaceManager;

import de.svenheins.objects.Entity;
import de.svenheins.objects.IngameConsole;
import de.svenheins.objects.IngameWindow;
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
//	private Player p;
//	private Player p2;
//	private Player[] players;
//	private ShipEntity[] ships;
//	public Entity eye;
//	public Space space;

	
	private ConsoleInputHandler consoleInput;
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
	private boolean pause, menu, showConsole;
	private boolean allwaysAnimated;
	
	public IngameWindow mainMenu;
	
	public Space startButton;
	public Space connectButton;
	
	public IngameConsole gameConsole;
	
	
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
//		p = new Player("Spieler1", new InputHandler(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE, KeyEvent.VK_P, KeyEvent.VK_ESCAPE));
//		p2 = new Player("Spieler2", new InputHandler(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S,KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_O));
//		players = new Player[]{p, p2};
//		ships = new ShipEntity[]{s, s2};
//		eye = new Entity("eye.png", 1, 150, 100);
//		space = new Space("Start.svg", 1000000, new Color(150, 30, 40), true, 0.3f);
		
		consoleInput = new ConsoleInputHandler();
		
		// Init GUI-Objects
		startButton = new Space("Start.svg", 0, "Start.png", 0.5f);
		connectButton = new Space("connect.svg", 0, "connect.png", 0.5f);
		connectButton.setAllXY(10, 400);
		gameConsole = new IngameConsole();
		
		// Init the input
		// Keyboard
//		gp.addKeyListener(p.getInputHandler());
//		gp.addKeyListener(p2.getInputHandler());
		gp.addKeyListener(consoleInput);
		// Mouse
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());
		
		// Init Time
		gp.last = System.currentTimeMillis();
		
		// Init Threads
		moveThread = new MoveThread(System.currentTimeMillis());
//		inputThread = new InputThread(players, ships);
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
		this.setMenu(false);
		this.serverInitialized = false;
		
		// Modify the Cursor
		//Cursor cursor = getToolkit().createCustomCursor(new ImageIcon(getClass().getResource(resourcePath+"images/"+"cursor.png")).getImage(), new Point(0,0), "Cursor");
		//this.setCursor(cursor);
		
		mainMenu = new IngameWindow();
		
		// Config PlayerManager
//		PlayerManager.playerList.add(s);
//		PlayerManager.playerList.add(s2);
		
		// Config EnemyManager
		configEnemyManager();
		
		// Create a tree-structure of Spaces and paint it to the Graphic2D-instance
//		land = new Tree<Space>(space1);
		// fill the tree
//		Space[] spaces = {space2,space3};
//		land.getRoot().addChildren(spaces);
//		Node<Space> n = land.getRoot().getChildren().get(0);
//		n.addChild(space4);
		
		// Config SpaceManager
		configSpaceManager();
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
		startButton.paint(g);
		connectButton.paint(g);
	}
	
	/**
	 * @param g: Paint Objects to g
	 * @GAME-Modus
	 */
	public void gamePaint(Graphics2D g){
		/** Paint Enemies */
		g.setPaintMode();
		EnemyManager.sortZIndex();
		for (int i = 0; i< EnemyManager.sizeSorted(); i++){
			Entity e= EnemyManager.getSorted(i);
			if(e != null) {
				if(e.getSprite().getImage() != null) {
					g.drawImage(e.getSprite().getImage(), (int) e.getX(), (int) e.getY(), this);
				}
			}
		}
		for (int i = 0; i< PlayerManager.size(); i++){
			Entity e= PlayerManager.get(i);
			if(e != null) {
				if(e.getSprite().getImage() != null) {
					g.drawImage(e.getSprite().getImage(), (int) e.getX(), (int) e.getY(), this);
				}
			}
		}	
		/** Paint Server-Entities */
		for (int i = 0; i < EntityManager.size(); i++) {
			Entity e= EntityManager.get(i);
			if(e != null) {
				if(e.getSprite().getImage() != null) {
					g.drawImage(e.getSprite().getImage(), (int) e.getX(), (int) e.getY(), this);
				}
			}
		}
		/** Paint Spaces */
		g.setPaintMode();
		// First sort by Z-index then draw objects
		SpaceManager.sortZIndex();
		for (int i = 0; i< SpaceManager.sizeSorted(); i++){
			Space space = SpaceManager.getSorted(i);
			if(space != null) {
				if(space.getPolygon() != null) {
					space.paint(g);
				}
			}
		}
		
		g.setPaintMode();
		// paint the Menu
		if (menu) {
			mainMenu.paint(g);
		}
		// paint the console
		if (showConsole) {
			gameConsole.paint(g);
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
	
	public void configEnemyManager() {
//		int startx = 50;
//		int rows = 100;
//		int arows = 100;
//		double randomLength;
//		for(int i_i = 0; i_i< rows; i_i++) {
//			for (int i_j=0;i_j<arows;i_j++ ) {
//				String[] alienString;
//				alienString = new String[]{"eye.png", "eye2.png"};
//				randomLength = 300+ Math.random()*700;
//				AlienEntity alien = new AlienEntity(alienString, startx+i_j*7, 5+i_i*4, i_i+1, i_j+1);
//				// Configure the individual animation
//				double randomStart = Math.random()*5000;
//				alien.getAnimation().setTimeBetweenAnimation(randomLength);
//				alien.getAnimation().start(randomStart);
//				alien.setHorizontalMovement(10);
//				alien.setVerticalMovement(0);
//				// Alien is ready to be added
//				EnemyManager.enemyList.add(alien);
//				AlienEntity.aliens++;
//			}	
//		}
	}
	
	public void configSpaceManager() {
//		spaceAdd = new Space("Zeichnung-4.svg", new Color(230, 20, 40), true, 1.0f);
//		spaceAdd.setHorizontalMovement(10);
//		SpaceManager.add(spaceAdd);
//		spaceAdd = new Space("Zeichnung-3.svg", new Color(20, 230, 40), true, 1.0f);
//		spaceAdd.setHorizontalMovement(5);
//		SpaceManager.add(spaceAdd);
//		spaceAdd = new Space("Zeichnung-2.svg", new Color(20, 20, 240), true, 1.0f);
//		spaceAdd.setHorizontalMovement(100);
//		SpaceManager.add(spaceAdd);
//		spaceAdd = new Space("Quadrat.svg","texture.png", 0.8f);
//		spaceAdd.setHorizontalMovement(5);
//		SpaceManager.add(spaceAdd);
	}
	
//	public Space getSpace() {
//		return space;
//	}
//	
//	public void setSpace(Space space) {
//		this.space = space;
//	}
	
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

//	public Space getSpaceAdd() {
//		return spaceAdd;
//	}
//
//	public void setSpaceAdd(Space spaceAdd) {
//		this.spaceAdd = spaceAdd;
//	}
	
	public void setMenu(boolean menu) {
		this.menu = menu;
	}
	
	public boolean isMenu() {
		return this.menu;
	}
	
	public void setShowConsole(boolean showConsole){
		this.showConsole = showConsole;
	}
	
	public boolean getShowConsole() {
		return showConsole;
	}
	
	public ConsoleInputHandler getConsoleInputHandler() {
		return this.consoleInput;
	}
	
	public boolean isServerInitialized(){
		return serverInitialized;
	}

	public void setServerInitialized(boolean b) {
		serverInitialized = b;
	}
}
