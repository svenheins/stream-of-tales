/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javafx.embed.swing.JFXPanel;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import de.svenheins.handlers.ConsoleInputHandler;
import de.svenheins.handlers.InputHandler;
import de.svenheins.handlers.MouseHandler;
import de.svenheins.objects.IngameConsole;
import de.svenheins.objects.MessageWindow;

public class GUI extends Canvas {

	public static GUI gui;
	public static boolean running = false;
	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
	
	private int frames;
	private int addSecond;
	
	private JFrame container;
	public  JPanel panel;
	private JPanel sp;
	private GameWindow gw;
	
	
	private StatPanel menuPanel;
	private JFXPanel jfxPanel;
	
	private InputHandler inputHandler;
    private ConsoleInputHandler consoleInput;
    public IngameConsole gameConsole;
    public IngameConsole gameInfoConsole;
    public MessageWindow messageWindow;
    private boolean showConsole, showInfoConsole;
	
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static final String MOVE_UP_D = "move up down";
    private static final String MOVE_UP_R = "move up released";
    private static final String MOVE_DOWN_D = "move down down";
    private static final String MOVE_DOWN_R = "move down released";
    private static final String MOVE_LEFT_D = "move left down";
    private static final String MOVE_LEFT_R = "move left released";
    private static final String MOVE_RIGHT_D = "move right down";
    private static final String MOVE_RIGHT_R = "move right released";
    
    private static final String INVENTORY_D = "inventory down";
    private static final String INVENTORY_R = "inventory released";
    private static final String ZOOM_IN_D = "zoom in down";
    private static final String ZOOM_IN_R = "zoom in released";
    private static final String ZOOM_OUT_D = "zoom out down";
    private static final String ZOOM_OUT_R = "zoom out released";
    private static final String INFO_D = "info down";
    private static final String INFO_R = "info released";
    private static final String LIGHT_D = "light down";
    private static final String LIGHT_R = "light released";
    
    /*KeyEvent.VK_A,
	KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE,
	KeyEvent.VK_P, KeyEvent.VK_ESCAPE, KeyEvent.VK_I,
	KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_Q, KeyEvent.VK_M, KeyEvent.VK_L);*/
    
	public GUI() {
		gui = this;
		System.setProperty("sun.java2d.opengl","True");
		addSecond = 0;
		frames = 0;
		
		// create a frame to contain our game
		container = new JFrame("Stream of Tales");
		
		/************************/
		/** add the menuPanel, that lies above the GamePanel 
		 * it is used for menus, infoboxes and consoles
		 * */
		menuPanel = new StatPanel();
		sp = new JPanel();
		sp.setOpaque(false);
		sp.setBackground(new Color(0,0,0,64));
		container.setGlassPane(sp);
		sp.setVisible(true);
		sp.setFocusable(false);
		
		/** JFX Init */
		jfxPanel = new JFXPanel();
		container.getContentPane().add(jfxPanel);
		/******************************/
		
		// get hold the content of the frame and set up the resolution of the game
		panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(GameStates.width,GameStates.height));
		panel.setLayout(null);
		
		// setup our canvas size and put it into the content of the frame
		setBounds(0,0,GameStates.width,GameStates.height);
		panel.add(this);
		
//		container.add(panelMouse);
		
		initKeys(panel);
		
		inputHandler = new InputHandler(KeyEvent.VK_A,
				KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE,
				KeyEvent.VK_P, KeyEvent.VK_ESCAPE, KeyEvent.VK_I,
				KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_Q, KeyEvent.VK_M, KeyEvent.VK_L);
		
		gw = new GameWindow("Stream of Tales", GameStates.width, GameStates.height);
		/************************/
		// Modify the Cursor
		Cursor cursor = getToolkit().createCustomCursor(new ImageIcon(getClass().getResource(GameStates.resourcePath+"images/"+"cursor.png")).getImage(),
				 new Point(0,0), "Cursor");
				 panel.setCursor(cursor);
				 sp.setCursor(cursor);
				 
//		// Mouse
		
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());
		
		gw.gp.setMouseCoordinates(new Point(0, 0));
		/**********************************/
		
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		
		// finally make the window visible 
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
//		addKeyListener(new KeyInputHandler());
		
		this.config();
		
		consoleInput = new ConsoleInputHandler();
		gameConsole = new IngameConsole(new Point(20, GameStates.getHeight()-220), GameStates.getWidth()/2-40, 160, new int[]{120, 120, 220}, 0.5f, true, true, 14);
		container.addKeyListener(consoleInput);
		this.showConsole = false;
		
//		container.addKeyListener(GamePanel.gp.getPlayer().getInputHandler());

		
		/** InfoConsole */
		gameInfoConsole = new IngameConsole(new Point(GameStates.getWidth()/2 + 20, GameStates.getHeight()-220), GameStates.getWidth()/2-40, 160, new int[]{120, 120, 120}, 0.5f, false, false, 14);
        
		/** messageWindow */
		int messageWindowWidth = 300;
		int messageWindowHeight = 200;
		messageWindow = new MessageWindow(new Point(GameStates.getWidth()/2 - messageWindowWidth/2, GameStates.getHeight()/2 - 3*messageWindowHeight/2), messageWindowWidth, messageWindowHeight, new int[]{120, 120, 120}, 0.5f, 14);
		
		
		// request the focus so key events come to us
		container.setFocusable(true);
		container.requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		// initialise the entities in our game so there's something
		// to see at startup
//		initEntities();
		
		
	}
	
	
	private void initKeys(JPanel panel) {
		  /*KeyEvent.VK_A,
			KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE,
			KeyEvent.VK_P, KeyEvent.VK_ESCAPE, KeyEvent.VK_I,
			KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_Q, KeyEvent.VK_M, KeyEvent.VK_L);*/

		
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("W"), MOVE_UP_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released W"), MOVE_UP_R);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("S"), MOVE_DOWN_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released S"), MOVE_DOWN_R);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("A"), MOVE_LEFT_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released A"), MOVE_LEFT_R);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("D"), MOVE_RIGHT_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released D"), MOVE_RIGHT_R);		
		panel.getActionMap().put(MOVE_UP_D, new KeyPressed(KeyEvent.VK_W));
		panel.getActionMap().put(MOVE_UP_R, new KeyReleased(KeyEvent.VK_W));
		panel.getActionMap().put(MOVE_DOWN_D, new KeyPressed(KeyEvent.VK_S));
		panel.getActionMap().put(MOVE_DOWN_R, new KeyReleased(KeyEvent.VK_S));
		panel.getActionMap().put(MOVE_LEFT_D, new KeyPressed(KeyEvent.VK_A));
		panel.getActionMap().put(MOVE_LEFT_R, new KeyReleased(KeyEvent.VK_A));
		panel.getActionMap().put(MOVE_RIGHT_D, new KeyPressed(KeyEvent.VK_D));
		panel.getActionMap().put(MOVE_RIGHT_R, new KeyReleased(KeyEvent.VK_D));
		
		
		/*
		 private static final String INVENTORY_D = "inventory down";
		    private static final String INVENTORY_R = "inventory released";
		    private static final String ZOOM_IN_D = "zoom in down";
		    private static final String ZOOM_IN_R = "zoom in released";
		    private static final String ZOOM_OUT_D = "zoom out down";
		    private static final String ZOOM_OUT_R = "zoom out released";
		    private static final String INFO_D = "info down";
		    private static final String INFO_R = "info released";
		    private static final String LIGHT_D = "light down";
		    private static final String LIGHT_R = "light released";*/
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("Q"), INVENTORY_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released Q"), INVENTORY_R);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("I"), INFO_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released I"), INFO_R);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("L"), LIGHT_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released L"), LIGHT_R);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("1"), ZOOM_IN_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released 1"), ZOOM_IN_R);	
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("2"), ZOOM_OUT_D);
		panel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released 2"), ZOOM_OUT_R);	
		panel.getActionMap().put(INVENTORY_D, new KeyPressed(KeyEvent.VK_Q));
		panel.getActionMap().put(INVENTORY_R, new KeyReleased(KeyEvent.VK_Q));
		panel.getActionMap().put(INFO_D, new KeyPressed(KeyEvent.VK_I));
		panel.getActionMap().put(INFO_R, new KeyReleased(KeyEvent.VK_I));
		panel.getActionMap().put(LIGHT_D, new KeyPressed(KeyEvent.VK_L));
		panel.getActionMap().put(LIGHT_R, new KeyReleased(KeyEvent.VK_L));
		panel.getActionMap().put(ZOOM_IN_D, new KeyPressed(KeyEvent.VK_1));
		panel.getActionMap().put(ZOOM_IN_R, new KeyReleased(KeyEvent.VK_1));
		panel.getActionMap().put(ZOOM_OUT_D, new KeyPressed(KeyEvent.VK_2));
		panel.getActionMap().put(ZOOM_OUT_R, new KeyReleased(KeyEvent.VK_2));
	}
	
	 private class KeyPressed extends AbstractAction {

	        int keyEvent;

	        KeyPressed(int e) {
	            this.keyEvent = e;
	        }

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				GamePanel.gp.getPlayer().getInputHandler().keyPressed(keyEvent);
			}
	    }
	 
	 private class KeyReleased extends AbstractAction {

	        int keyEvent;

	        KeyReleased(int e) {
	            this.keyEvent = e;
	        }

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				GamePanel.gp.getPlayer().getInputHandler().keyReleased(keyEvent);
			}
	    }
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		running = true;
//		
//		
//		new GameWindow("Client", GameStates.width, GameStates.height);
		
		GUI g =new GUI();

		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		g.gameLoop();
	}
	
	
	public void config() {
	    this.showInfoConsole = true;
	    
	    /** config the GamePanel */
	    gw.gp.config();
	}
	
	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		
		// keep looping round til the game ends
		while (running) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			addSecond += delta;
			frames++;
			if (addSecond>=1000) {
				System.out.println("Frames = "+frames);
				addSecond =0;
				frames = 0;
			}
			
			// Get hold of a graphics context for the accelerated 
			// surface and blank it out
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,GameStates.width,GameStates.height);
			
			Graphics2D g2 = (Graphics2D) strategy.getDrawGraphics();
			g2.setColor(Color.black);
			g2.fillRect(0,0,GameStates.width,GameStates.height);

//			long dist0 = System.currentTimeMillis();
			gw.gp.paintComponent(g);
			
//			long dist1 = System.currentTimeMillis()-dist0;
//			
//			System.out.println("GAMEPANEL: sums up to = "+dist1);
//
//			dist0 = System.currentTimeMillis();
			
			menuPanel.paintComponent(g2);
//			dist1 = System.currentTimeMillis()-dist0;
//			System.out.println("STATPANEL: sums up to = "+dist1);
			
			container.requestFocus();
			
			// finally, we've completed drawing so clear up the graphics
			// and flip the buffer over
			g.dispose();
			g2.dispose();
			strategy.show();
			

			
//			System.out.println("Back: "+((Graphics2D) strategy.getDrawGraphics()).getDeviceConfiguration().getBufferCapabilities().getBackBufferCapabilities().isAccelerated());
//			System.out.println("Front: "+((Graphics2D) strategy.getDrawGraphics()).getDeviceConfiguration().getBufferCapabilities().getFrontBufferCapabilities().isAccelerated());
			
			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			try { Thread.sleep(5); } catch (Exception e) {}
		}
	}

	
	public void requestFocus() {
		container.requestFocus();
	}

	public void dispose() {
		container.dispose();
	}
	
    /**
     * Appends the given message to the output text pane.
     *
     * @param x the message to append to the output text pane
     */
    public void appendOutput(String x) {
//        outputArea.append(x + "\n");
    	gameInfoConsole.appendSimpleDate(x);
    }
    
    /**
     * Displays the given string in this client's status bar.
     *
     * @param status the status message to set
     */
    public void setStatus(String status) {
        appendOutput("Status Set: " + status);
//        statusLabel.setText("Status: " + status);
    }
    
    public void setShowConsole(boolean showConsole){
		this.showConsole = showConsole;
	}
	
	public boolean getShowConsole() {
		return showConsole;
	}
	
	public void setShowInfoConsole(boolean showInfoConsole){
		this.showInfoConsole = showInfoConsole;
	}
	
	public boolean getShowInfoConsole() {
		return showInfoConsole;
	}
	
	public ConsoleInputHandler getConsoleInputHandler() {
		return this.consoleInput;
	}
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}
}
