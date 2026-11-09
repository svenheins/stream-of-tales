/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 */
package de.svenheins.main;


import de.svenheins.WorldClient;
import de.svenheins.functions.MyMath;
import de.svenheins.functions.MyStrings;
import de.svenheins.functions.MyUtil;
import de.svenheins.handlers.ClientMessageHandler;
import de.svenheins.handlers.ConsoleInputHandler;
import de.svenheins.main.gui.PlayerListGUIManager;
import de.svenheins.main.menu.MainMenu;
import de.svenheins.managers.ClientTextureManager;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.LightManager;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.MusicManager;
import de.svenheins.managers.ObjectMapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.RessourcenManager;
import de.svenheins.managers.SoundEffectManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.managers.TextEffectManager;
import de.svenheins.managers.TileMapManager;
import de.svenheins.managers.UndergroundMapManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.IngameConsole;












import de.svenheins.objects.MessageWindow;
import de.svenheins.objects.MiniMap;
import de.svenheins.objects.TextEffect;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.embed.swing.JFXPanel;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;


public class GameWindow implements SimpleClientListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private String title;
	public final int height;
	public final int width;
	private Dimension dim;


	public Sound sound;
//	public final JMenuItem item21= new JMenuItem("reset zoom");
	
	public static GameWindow gw;
	public static GamePanel gp;
//	private GraphicsDevice device;
    private boolean isFullScreen = false;
    private boolean loggedIn; //, superUser;

    
    private boolean readyForNextMessage;
    
//    private ConsoleInputHandler consoleInput;
//    public IngameConsole gameConsole;
//    public IngameConsole gameInfoConsole;
//    public MessageWindow messageWindow;
    
    private int[] loadingStates = new int[4];


    /** The name of the host property. */
    public static final String HOST_PROPERTY = "tutorial.host";

    /** The default hostname. */
    public static final String DEFAULT_HOST = "localhost";
    private String hostName;

    /** The name of the port property. */
    public static final String PORT_PROPERTY = "tutorial.port";

    /** The default port. */
    public static final String DEFAULT_PORT = "1139";
    private String portNumber;
    
    private String loginName = "";
    private String loginPassword = "";
    private TwitchBot twitchBot;
    private boolean usingTwitch = true;
    private String twitchUserName = "";
    private String twitchPWD = "";
    private String twitchChannel = "";
    private MySQLConnector mySQL;
    
    
    

    /** The message encoding. */
    public static final String MESSAGE_CHARSET = "UTF-8";

    /** The {@link SimpleClient} instance for this client. */
    protected final SimpleClient simpleClient;
    
    /** The random number generator for login names. */
    private final Random random = new Random();
    
    /** The Player Name*/
    protected String playerName;
    /** mapMasterName */
    protected String mapMasterName;
    private boolean gameMaster;
    
    protected boolean night = false;
    private boolean light = true;
    private boolean usingLight = true;
    private boolean usingWeather = true;
    
	private boolean initingLocalMapFileList = false;
    
    /** Map that associates a channel name with a {@link ClientChannel}. */
    protected final Map<String, ClientChannel> channelsByName =
        new HashMap<String, ClientChannel>();
    
    /** channel-map */
    protected HashMap<BigInteger, String> spaceChannels =
            new HashMap<BigInteger, String>();
    
    /** mapManagers */
    protected HashMap<String, MapManager> mapManagers = new HashMap<String, MapManager>();
    /** objectMapManager */
    protected HashMap<String, ObjectMapManager> objectMapManagers = new HashMap<String, ObjectMapManager>();
    /** objectMapManager of the lower room*/
    protected HashMap<String, ObjectMapManager> objectMapManagersLowerRoom = new HashMap<String, ObjectMapManager>();
    /** undergroundMapManagers */
    protected HashMap<String, UndergroundMapManager> undergroundMapManagers = new HashMap<String, UndergroundMapManager>();
    
    /** ArrayList of filenames that need to be send to other channel players */
    private ArrayList<String> sendMapList = new ArrayList<String>();
    
    private ArrayList<BigInteger> sendItemList = new ArrayList<BigInteger>();
    
    /** TileMapManger */
   protected TileMapManager tileMapManager = new TileMapManager(GameStates.tileSetFile, GameStates.mapTileSetWidth, GameStates.mapTileSetHeight);
   /** TileUndergroundMapManger */
   protected TileMapManager tileUndergroundMapManager = new TileMapManager(GameStates.tileSetFileUnderground, GameStates.ugrMapTileSetWidth, GameStates.ugrMapTileSetHeight);
    
    /** Sequence generator for counting channels. */
    protected final AtomicInteger channelNumberSequence =
        new AtomicInteger(1);
    
    /** HelloChannelClient instance for the player name etc*/
    protected static WorldClient worldClient;
    
    

	
	public GameWindow(String title, int width, int height) {
//		super(title);
		gw = this;

		this.width = width;
		this.height = height;
		dim = new Dimension(this.width, this.height);
		
		simpleClient = new SimpleClient(this);
		
//		/** add the menuPanel, that lies above the GamePanel 
//		 * it is used for menus, infoboxes and consoles
//		 * */
//		menuPanel = new StatPanel();
//		setGlassPane(menuPanel);
//		menuPanel.setVisible(true);
//		
//		/** JFX Init */
//		jfxPanel = new JFXPanel();
//		this.getContentPane().add(jfxPanel);
		
		/** init sound */
		SoundEffectManager.init();
		MusicManager.init();
		this.sound = new Sound();
		this.setMySQL(new MySQLConnector("com.mysql.jdbc.Driver", "jdbc:mysql://192.168.56.101/stream", "streammanager", "test1234", "streamviewers"));
		
//		/** Main-GamePanel
//		 * this panel will be scaled or rotated*/
//		panel = new GamePanel(strategy);
//		panel.setBackground(new Color(0,0,0));
//		panel.setDoubleBuffered(true);

		gp = new GamePanel();
		

		/** configuration */
		this.config();
		
//		JInternalFrame frame1;
//		frame1 = new JInternalFrame("Frame1", true, true, true, true);
//        frame1.setBounds(20, 200, 150, 100);
//        frame1.setVisible(true);
		
//		setMaximumSize(dim);
//		setMinimumSize(dim);
//		setPreferredSize(dim);
//		
//		setLocationRelativeTo(null);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//      device =ge.getDefaultScreenDevice();

        
        GameStates.setWidth(width);
        GameStates.setHeight(height);
        
        
        /** create an image-folder */
     	/** all non-existent ancestor directories are automatically created */
	    boolean success = (new File("./ressources/images")).mkdirs();
	    if (!success) {
	         // Directory creation failed
	    }
	    /** init the external images */
	    ClientTextureManager.manager.initExternalImages(GameStates.externalImagesPath);
	    
	    /** create a map-folder */
     	/** all non-existent ancestor directories are automatically created */
	    boolean createMapFolderSccess = (new File(GameStates.standardMapFolder)).mkdirs();
	    if (!createMapFolderSccess) {
	         // Directory creation failed
	    }
//		consoleInput = new ConsoleInputHandler();
//		gameConsole = new IngameConsole(new Point(20, GameStates.getHeight()-220), GameStates.getWidth()/2-40, 160, new int[]{120, 120, 220}, 0.5f, true, true, 14);
//		gw.addKeyListener(consoleInput);
//		this.showConsole = false;
//		
//		gw.addKeyListener(GamePanel.gp.getPlayer().getInputHandler());
//		
//		/** InfoConsole */
//		gameInfoConsole = new IngameConsole(new Point(GameStates.getWidth()/2 + 20, GameStates.getHeight()-220), GameStates.getWidth()/2-40, 160, new int[]{120, 120, 120}, 0.5f, false, false, 14);
//        
//		/** messageWindow */
//		int messageWindowWidth = 300;
//		int messageWindowHeight = 200;
//		messageWindow = new MessageWindow(new Point(GameStates.getWidth()/2 - messageWindowWidth/2, GameStates.getHeight()/2 - 3*messageWindowHeight/2), messageWindowWidth, messageWindowHeight, new int[]{120, 120, 120}, 0.5f, 14);
		
//		setResizable(false);
//		
//		add(panel);
		
		/** create MainMenu */
//		MainMenu menuBar = new MainMenu(panel);
		// Comment to unsupport Menu
//		this.setJMenuBar(menuBar);

//		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		
//		setVisible(true);
//		gw.setFocusable(true);
//		gw.requestFocus();
		
//		// Tell AWT not to bother repainting our canvas since we're
//		// going to do that our self in accelerated mode
//		gw.setIgnoreRepaint(true);
//		// setup our canvas size and put it into the content of the frame
//				gw.setBounds(0,0,width,height);
//				// request the focus so key events come to us
//				gw.requestFocus();		
//				
//		// create the buffering strategy which will allow AWT
//		// to manage our accelerated graphics
//		gw.createBufferStrategy(2);
//		gw.strategy = getBufferStrategy();
	}
	
	public void config() {
		setLoggedIn(false);
		setMapMasterName("standard");
		setGameMaster(false);
	    this.setReadyForNextMessage(true);
	    /** prepare the loading states */
	    loadingStates = new int[]{0,0,0,0};

	}
	
	public void logout() {
		simpleClient.logout(true);
		gw.send(ClientMessages.logMeOut());
		GamePanel.gp.setInitializedPlayer(false);
		GamePanel.gp.setServerInitialized(false);
		clearAllManagers();
		this.config();
	}
	
	public void clearAllManagers() {
		PlayerManager.emptyAll();
		EntityManager.emptyAll();
		SpaceManager.emptyAll();
		resetMapManagers();
//		/** prepare the loading states */
//	    loadingStates = new int[]{0,0,0,0};
	}
	
	
	 /**
     * Allows subclasses to populate the input panel with
     * additional UI elements.  The base implementation
     * simply adds the input text field to the center of the panel.
     *
     * @param panel the panel to populate
     */
	public void superPopulateInputPanel(JPanel panel) {
//        panel.add(inputField, BorderLayout.CENTER);
    }



    /**
     * Initiates asynchronous login to the SGS server specified by
     * the host and port properties.
     */
    protected void login() {
        String host = System.getProperty(HOST_PROPERTY, hostName);
        String port = System.getProperty(PORT_PROPERTY, portNumber);

        try {
            Properties connectProps = new Properties();
            connectProps.put("host", host);
            connectProps.put("port", port);
            simpleClient.login(connectProps);
//            try {
//            	synchronized (simpleClient) {
//            		simpleClient.wait(5000L);
//            	}
//            } catch (InterruptedException e) {
//            	e.printStackTrace();
//            }
            
            new Thread(GamePanel.serverUpdateThread).start();
            
//            new Thread(GamePanel.inputThread).start();
            
            
        } catch (Exception e) {
            e.printStackTrace();
            disconnected(false, e.getMessage());
        }
    }
    
    protected void twitchLogin(String hostName, String name, String pwd, String channel) {
    	if (usingTwitch) {
        	this.connectTwitchBot(hostName, name, pwd, channel);
        }
    }
    
    public void connectTwitchBot(String hostName, String name, String pwd, String channel) {
    	if (!name.equals("") && (!pwd.equals(""))) {
//	    	twitchBot = new TwitchBot(name, false);
//			twitchBot.setVerbose(true);
//			try {
//				twitchBot.connect("irc.twitch.tv", 6667, pwd);
//			} catch (NickAlreadyInUseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IrcException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if (!channel.equals("")) {
//				twitchBot.joinChannel("#"+channel);
//			} else {
//				twitchBot.joinChannel("#"+name);
//			}
    		
    		String channelNameCorrected;
    		if (!channel.equals("")) {
				channelNameCorrected="#"+channel;
			} else {
				channelNameCorrected="#"+name;
			}
    		twitchBot = new TwitchBot(hostName, 6667, name, pwd, name, channelNameCorrected, true);// "irc.twitch.tv"
			
    	} else {
    		System.out.println("no connection with twitch because of empty fields");
    	}
    }

    

    /**
     * Encodes a {@code String} into a {@link ByteBuffer}.
     *
     * @param s the string to encode
     * @return the {@code ByteBuffer} which encodes the given string
     */
    public static ByteBuffer encodeString(String s) {
        try {
            return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET +
                " not found", e);
        }
    }

    /**
     * Decodes a {@link ByteBuffer} into a {@code String}.
     *
     * @param buf the {@code ByteBuffer} to decode
     * @return the decoded string
     */
    public static String decodeString(ByteBuffer buf) {
        try {
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            return new String(bytes, MESSAGE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET +
                " not found", e);
        }
    }

    /**
     * Returns the user-supplied text from the input field, and clears
     * the field to prepare for more input.
     *
     * @return the user-supplied text from the input field
     */
    public String getInputText() {
//        try {
//            return inputField.getText();
//        } finally {
//            inputField.setText("");
//        }
    	return "test";
    }

    // Implement SimpleClientListener

    /**
     * {@inheritDoc}
     * <p>
     * Returns dummy credentials where user is "guest-&lt;random&gt;"
     * and the password is "guest."  Real-world clients are likely
     * to pop up a login dialog to get these fields from the player.
     */
    public PasswordAuthentication getPasswordAuthentication() {
        this.playerName = this.getLoginName();//"guest-" + random.nextInt(1000);
//    	this.player = "player";
        GUI.gui.setStatus("Logging in as " + playerName);
        String password = this.getLoginPassword();
        return new PasswordAuthentication(playerName, password.toCharArray());
    	
    }

    /**
     * {@inheritDoc}
     * <p>
     * Enables input and updates the status message on successful login.
     */
    public void loggedIn() {
//        inputPanel.setEnabled(true);
        GUI.gui.setStatus("Logged in");
        setLoggedIn(true);
        /** create a map-folder */
     	/** all non-existent ancestor directories are automatically created */
	    boolean createMapFolderSuccess = (new File(GameStates.standardMapFolder+"/"+this.getMapMasterName())).mkdirs();
	    if (!createMapFolderSuccess) {
	         // Directory creation failed
	    	System.out.println("Error at creating game-master map-folder!");
	    }
	    createMapFolderSuccess = (new File(GameStates.standardMapFolder+"/"+this.getPlayerName())).mkdirs();
	    if (!createMapFolderSuccess) {
	         // Directory creation failed
	    	System.out.println("Error at creating playerName map-folder!");
	    }
	    String standardRoom = this.getPlayerName() + "0000";
	    createMapFolderSuccess = (new File(GameStates.standardMapFolder+"/"+this.getPlayerName()+"/"+standardRoom)).mkdirs();
	    if (!createMapFolderSuccess) {
	         // Directory creation failed
	    	System.out.println("Error at creating playerName map-folder!");
	    }
	    createMapFolderSuccess = (new File(GameStates.standardMapFolder+"/"+this.getPlayerName()+"/"+this.getLowerRoom(standardRoom))).mkdirs();
	    if (!createMapFolderSuccess) {
	         // Directory creation failed
	    	System.out.println("Error at creating playerName lowerRoom map-folder!");
	    }
	    System.out.println("Playername: "+ this.getPlayerName());
	    GamePanel.gp.setPlayerEntityName(this.getPlayerName());
	    GamePanel.gp.getPlayerEntity().setRoom(standardRoom);
	    GamePanel.gp.setPlayerGroupName(this.getPlayerName());
	    PlayerListGUIManager.get("playerList").get(BigInteger.valueOf(0)).setStrValue(this.getPlayerName());
	    
	    String folderPath = "./ressources/maps/";
		String src = this.getPlayerName()+".png";
		MiniMap	miniMap = new MiniMap(folderPath, src);
		MiniMap dungeonMiniMap = new MiniMap(folderPath, "dungeon"+src);
		if (miniMap.getMap() != null) ; 
		else {
			System.out.println("couldn't get the file "+folderPath+src+" -> take the standard 10x10 miniMap with green color");
			miniMap = new MiniMap();
		}
		if (dungeonMiniMap.getMap() != null) ; 
		else {
			System.out.println("couldn't get the file "+folderPath+src+" -> take the standard 30x30 Dungeon miniMap with color");
			dungeonMiniMap = new MiniMap("dungeon.png");
		}
	    GamePanel.gp.setMiniMap(miniMap);
	    GamePanel.gp.setDungeonMiniMap(dungeonMiniMap);
//	    System.out.println("00: "+dungeonMiniMap.getMapType(0, 0));
//	    System.out.println("01: "+dungeonMiniMap.getMapType(0, 1));
//	    System.out.println("10: "+dungeonMiniMap.getMapType(1, 0));
//	    System.out.println("11: "+dungeonMiniMap.getMapType(1, 1));
	    
	    /** reset the maps */
	    this.resetMapManagers();
	    this.setMapMaster(this.getPlayerName());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the status message on failed login.
     */
    public void loginFailed(String reason) {
        GUI.gui.setStatus("Login failed: " + reason);
        setLoggedIn(false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Disables input and updates the status message on disconnect.
     */
    public void disconnected(boolean graceful, String reason) {
//        inputPanel.setEnabled(false);
        GUI.gui.setStatus("Disconnected: " + reason);
        setLoggedIn(false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Decodes the message data and acts depending on its content  */
    public void receivedMessage(ByteBuffer message) {
    	ClientMessageHandler.parseClientPacket(message);
    }
   

    /**
     * {@inheritDoc}
     * <p>
     * Updates the status message on successful reconnect.
     */
    public void reconnected() {
        GUI.gui.setStatus("reconnected");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the status message when reconnection is attempted.
     */
    public void reconnecting() {
        GUI.gui.setStatus("reconnecting");
    }


    /**
     * Encodes the given text and sends it to the server.
     * 
     * @param text the text to send.
     */
    public void send(String text) {
        try {
            ByteBuffer message = encodeString(text);
            simpleClient.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void send(ByteBuffer message) {
    	try {
			simpleClient.send(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	
	/**
     * {@inheritDoc}
     * <p>
     * This implementation adds a channel selector component next
     * to the input text field to allow users to choose between
     * direct-to-server messages and channel broadcasts.
     */
    public void populateInputPanel(JPanel panel) {
        superPopulateInputPanel(panel);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a listener that formats and displays received channel
     * messages in the output text pane.
     */
    @Override
    public ClientChannelListener joinedChannel(ClientChannel channel) {
        String channelName = channel.getName();
        channelsByName.put(channelName, channel);
        GUI.gui.appendOutput("Joined to channel " + channelName);
        if (channelName.startsWith("SpaceChannel_")) {
        	System.out.println("Got Space-Channel");
        	joinSpaceChannel(BigInteger.valueOf(Long.parseLong(channelName.substring(13))));
        }
//        channelSelectorModel.addElement(channelName);
        return new WorldClientChannelListener();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (!simpleClient.isConnected()) {
            return;
        }

        String text = getInputText();
        String channelName = "<DIRECT>";
//            (String) channelSelector.getSelectedItem();
        if (channelName.equalsIgnoreCase("<DIRECT>")) {
            send(text);
        } else {
            ClientChannel channel = channelsByName.get(channelName);
            try {
                channel.send(encodeString(text));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * A simple listener for channel events.
     */
    public class WorldClientChannelListener
        implements ClientChannelListener
    {
        /**
         * An example of per-channel state, recording the number of
         * channel joins when the client joined this channel.
         */
        private final int channelNumber;

        /**
         * Creates a new {@code HelloChannelListener}. Note that
         * the listener will be given the channel on its callback
         * methods, so it does not need to record the channel as
         * state during the join.
         */
        public WorldClientChannelListener() {
            channelNumber = channelNumberSequence.getAndIncrement();
        }

        /**
         * {@inheritDoc}
         * <p>
         * Displays a message when this client leaves a channel.
         */
        public void leftChannel(ClientChannel channel) {
            GUI.gui.appendOutput("Removed from channel " + channel.getName());
            if (channel.getName().startsWith("SpaceChannel_")) {
            	leaveSpaceChannel(BigInteger.valueOf(Long.parseLong(channel.getName().substring(13))));
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * Formats and displays messages received on a channel.
         */
        public void receivedMessage(ClientChannel channel, ByteBuffer message) {
//            appendOutput("[" + channel.getName() + "/ " + channelNumber +
//                "] " + decodeString(message));
//            appendOutput(decodeString(message));
            ClientMessageHandler.parseClientPacket(message);
        }
    }
    
    public void setHostName(String hostName) {
    	this.hostName = hostName;
    }
    
    public String getHostName() {
    	return this.hostName;
    }
    
    public void setPortNumber(String portNumber) {
    	this.portNumber = portNumber;
    }
    
    public String getPortNumber() {
    	return this.portNumber;
    }


	public boolean isLoggedIn() {
		return loggedIn;
	}


	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
    
   
//     
//    public void setFullscreen() {
//        isFullScreen = device.isFullScreenSupported();
//        setUndecorated(isFullScreen);
//        setResizable(!isFullScreen);
//        if (isFullScreen) {
//            // Full-screen mode
//            device.setFullScreenWindow(this);
//            validate();
//        } else {
//            // Windowed mode
//            pack();
//            setVisible(true);
//        }
//    }
	


//	public boolean isSuperUser() {
//		return superUser;
//	}
//
//
//	public void setSuperUser(boolean superUser) {
//		this.superUser = superUser;
//	}


	public String getLoginName() {
		return loginName;
	}


	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}


	public String getLoginPassword() {
		return loginPassword;
	}


	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	
	private void initializeRessources() {
		// Directory path to svg-files
		URL pathSVG = getClass().getResource(GameStates.resourcePath+"svg/");//GameStates.resourcePath+"svg/"; 
		 
		  String fileName;
		  File folder;
		try {
			folder = new File(pathSVG.toURI());
			File[] listOfFiles = folder.listFiles(); 
			 
			  for (int i = 0; i < listOfFiles.length; i++) 
			  {
			 
			   if (listOfFiles[i].isFile()) 
			   {
			   fileName = listOfFiles[i].getName();
			       if (fileName.endsWith(".svg") || fileName.endsWith(".SVG"))
			       {
			          // add to ArrayList
			    	   RessourcenManager.addSVG(fileName);
			        }
			     }
			  }
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		// Directory path to image-files
		URL pathImages = getClass().getResource(GameStates.resourcePath+"images/");//GameStates.resourcePath+"images/";
		  try {
			folder = new File(pathImages.toURI());
			File[] listOfFiles = folder.listFiles(); 
			 
			  for (int i = 0; i < listOfFiles.length; i++) 
			  {
			 
			   if (listOfFiles[i].isFile()) 
			   {
			   fileName = listOfFiles[i].getName();
			       if (fileName.endsWith(".png") || fileName.endsWith(".PNG"))
			       {
			          // add to ArrayList
			    	   RessourcenManager.addImage(fileName);
			        }
			     }
			  }
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	}
	
	public String getPlayerName(){
		return this.playerName;
	}


	public boolean isReadyForNextMessage() {
		return readyForNextMessage;
	}


	public void setReadyForNextMessage(boolean readyForNextMessage) {
		this.readyForNextMessage = readyForNextMessage;
	}
	
	public ClientChannel getChannelByName(String name) {
		return channelsByName.get(name);
	}
	
	public HashMap<BigInteger, String> getSpaceChannels() {
		return this.spaceChannels;
	}
	
	public void joinSpaceChannel(BigInteger spaceId) {
		if (!spaceChannels.containsKey(spaceId)) {
			this.spaceChannels.put(spaceId, "SpaceChannel_"+spaceId);
		}
	}
	
	public void leaveSpaceChannel(BigInteger spaceId) {
		if (spaceChannels.containsKey(spaceId)) {
			this.spaceChannels.remove(spaceId);
		}
	}
	
	public void updateGameModus() {
		switch (GameModus.modus) {
		case GameModus.LOADING:
			if(MyMath.sum(loadingStates)<(loadingStates.length*100)) {
				// do nothing and wait
			} else {
				// OK, ready for GAME
				GameModus.modus = GameModus.GAME;
				GamePanel.gp.setTransparentLoad(-7.0f);
				this.selectBackgroundMusicByEnvironment();
//				GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.GAME);
			}
			break;
		case GameModus.GAME:
		default:
			break;
		}
	}
	
	public void setLoadingStates(int index, int value) {
		loadingStates[index] = value;
	}
	
	public int getLoadingState(int index) {
		return loadingStates[index];
	}
	
	public String getMapMasterName() {
		return mapMasterName;
	}
	
	/** return the mapMasterID depending on if its me or another player */
	public BigInteger getMapMasterID() {
		if (isMapMaster()) {
			return GamePanel.gp.getPlayerEntity().getId();
		} else {
			return PlayerManager.get(GameWindow.gw.getMapMasterName()).getId();
		}
	}
	
	public void setMapMasterName(String name) {
		this.mapMasterName = name;
	}
	
	public void setMapMaster(String playerName) {
		String standardRoomName = playerName+"0000";
		if (this.isLoggedIn()) {
			GamePanel.gp.setPaintLayer("empty");
			GamePanel.gp.setPaintType(0);
			GamePanel.gp.configGUI();
			/** create map folder for the chosen player */
		    boolean createMapFolderSccess = (new File(GameStates.standardMapFolder+playerName)).mkdirs();
		    if (!createMapFolderSccess) {
		         // Directory creation failed
		    }
		    /** delete only the Maps, not the changedPoints */
		    GameWindow.gw.setMapMasterName(playerName);
		    
		    /** if the player entered his own world */
		    if (GameWindow.gw.getPlayerName().equals(playerName)) {
		    	GamePanel.gp.getPlayerEntity().setRoom(standardRoomName);
		    	GamePanel.gp.getPlayerEntity().setX(0);
		    	GamePanel.gp.getPlayerEntity().setY(0);
		    	GameWindow.gw.initLocalMapFileList(playerName, standardRoomName);
		    } else {
		    	/** world of another player */
		    	String playerRoom = PlayerManager.get(playerName).getRoom(); // we dont need this, because the player could be in another world
		    	boolean createMapSubFolderSccess = (new File(GameStates.standardMapFolder+playerName+"/"+standardRoomName)).mkdirs();
			    if (!createMapSubFolderSccess) {
			         // Directory creation failed
			    }
			    boolean createMapSubFolderSccessLowerRoom = (new File(GameStates.standardMapFolder+playerName+"/"+this.getLowerRoom(standardRoomName))).mkdirs();
			    if (!createMapSubFolderSccessLowerRoom) {
			         // Directory creation failed
			    }
			    System.out.println("loaded "+GameStates.standardMapFolder+playerName+"/"+standardRoomName);
		    	GamePanel.gp.getPlayerEntity().setX(0);
		    	GamePanel.gp.getPlayerEntity().setY(0);
		    	GameWindow.gw.initLocalMapFileList(playerName, standardRoomName);
		    	GamePanel.gp.getPlayerEntity().setRoom(standardRoomName);
		    }
		    
		    GameWindow.gw.send(ClientMessages.editPlayerRoom(GamePanel.gp.getPlayerEntity().getId(), this.getPlayerName(), standardRoomName));
		    
		    
		    if (!isGameMaster()) GamePanel.gp.setZoomFactor(GameStates.standardZoomFactor);
		    this.selectLightByEnvironment();
		}
	}
	
	public void enterRoom(String room) {
		if (this.isLoggedIn()) {
			GamePanel.gp.setTransparentLoad(0.0f);
			GamePanel.gp.setPaintLayer("empty");
			GamePanel.gp.setPaintType(0);
			GamePanel.gp.configGUI();
			
			/** create map folder for the chosen player */
		    boolean createMapFolderSuccess = (new File(GameStates.standardMapFolder+GameWindow.gw.getMapMasterName())).mkdirs();
		    if (!createMapFolderSuccess) {
		         // Directory creation failed
		    }
		    boolean createMapSubFolderSuccess = (new File(GameStates.standardMapFolder+GameWindow.gw.getMapMasterName()+"/"+room)).mkdirs();
		    if (!createMapSubFolderSuccess) {
		         // Directory creation failed
		    }
		    boolean createMapSubFolderSuccessLowerRoom = (new File(GameStates.standardMapFolder+GameWindow.gw.getMapMasterName()+"/"+this.getLowerRoom(room))).mkdirs();
		    if (!createMapSubFolderSuccessLowerRoom) {
		         // Directory creation failed
		    }
		    
		    /** if the player enter his own world */
		    if (GameWindow.gw.getPlayerName().equals(GameWindow.gw.getMapMasterName())) {
		    	
		    	GamePanel.gp.getPlayerEntity().setRoom(room);
//		    	GamePanel.gp.getPlayerEntity().setX(0);
//		    	GamePanel.gp.getPlayerEntity().setY(0);
		    	GameWindow.gw.initLocalMapFileList(playerName, room);
		    } else {
		    	/** world of another player */
//			    System.out.println("loaded "+GameStates.standardMapFolder+playerName+"/"+playerName);
//		    	GamePanel.gp.getPlayerEntity().setX(0);
//		    	GamePanel.gp.getPlayerEntity().setY(0);
		    	GameWindow.gw.initLocalMapFileList(GameWindow.gw.getMapMasterName(), room);
		    	GamePanel.gp.getPlayerEntity().setRoom(room);
		    }
		    
		    GameWindow.gw.send(ClientMessages.editPlayerRoom(GamePanel.gp.getPlayerEntity().getId(), this.getPlayerName(), GamePanel.gp.getPlayerEntity().getRoom()));
		    
		    LightManager.clear();
		    this.selectLightByEnvironment();
		    if (!isGameMaster()) GamePanel.gp.setZoomFactor(GameStates.standardZoomFactor);
		    this.selectBackgroundMusicByEnvironment();
		}
	}
	
	
	
	public String getLowerRoom(String actualRoomName) {
		String retString =MyStrings.increaseStringNumerator(actualRoomName);
		return retString;
	}
	
	public String getUpperRoom(String actualRoomName) {
		String retString =MyStrings.decreaseStringNumerator(actualRoomName);
		return retString;
	}
	
	public boolean isMapMaster() {
		return (this.getPlayerName().equals(this.getMapMasterName()));
	}
	
	public void initMapManagers() {
//		System.out.println(GamePanel.gp.getMiniMap().getWidth()+" "+GamePanel.gp.getMiniMap().getHeight());
		MapManager cobbleStoneLayer = new MapManager("cobble", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		mapManagers.put(cobbleStoneLayer.getPaintLayer(), cobbleStoneLayer);
		MapManager grassLayer = new MapManager("grass", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		mapManagers.put(grassLayer.getPaintLayer(), grassLayer);
//		MapManager snowLayer = new MapManager("snow");
//		mapManagers.put(snowLayer.getPaintLayer(), snowLayer);
		MapManager holeLayer = new MapManager("hole", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		mapManagers.put(holeLayer.getPaintLayer(), holeLayer);
		ObjectMapManager treeLayer1 = new ObjectMapManager("tree1", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		ObjectMapManager treeLayer1LowerRoom = new ObjectMapManager("tree1", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		objectMapManagers.put(treeLayer1.getPaintLayer(), treeLayer1);
		objectMapManagersLowerRoom.put(treeLayer1.getPaintLayer(), treeLayer1LowerRoom);
		ObjectMapManager treeLayer2 = new ObjectMapManager("tree2", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		ObjectMapManager treeLayer2LowerRoom = new ObjectMapManager("tree2", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		objectMapManagers.put(treeLayer2.getPaintLayer(), treeLayer2);
		objectMapManagersLowerRoom.put(treeLayer2.getPaintLayer(), treeLayer2LowerRoom);
		ObjectMapManager overlayTreeLayer1 = new ObjectMapManager("overlayTree1", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		ObjectMapManager overlayTreeLayer1LowerRoom = new ObjectMapManager("overlayTree1", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		objectMapManagers.put(overlayTreeLayer1.getPaintLayer(), overlayTreeLayer1);
		objectMapManagersLowerRoom.put(overlayTreeLayer1.getPaintLayer(), overlayTreeLayer1LowerRoom);
		ObjectMapManager overlayTreeLayer2 = new ObjectMapManager("overlayTree2", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		ObjectMapManager overlayTreeLayer2LowerRoom = new ObjectMapManager("overlayTree2", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		objectMapManagers.put(overlayTreeLayer2.getPaintLayer(), overlayTreeLayer2);
		objectMapManagersLowerRoom.put(overlayTreeLayer2.getPaintLayer(), overlayTreeLayer2LowerRoom);
		UndergroundMapManager undergroundMapManager = new UndergroundMapManager("underground", GamePanel.gp.getMiniMap().getWidth(), GamePanel.gp.getMiniMap().getHeight());
		undergroundMapManagers.put(undergroundMapManager.getPaintLayer(), undergroundMapManager);
	}
	
	public void initLocalMapFileList(String playerName, String room) {
		this.setInitingLocalMapFileList(true);
		this.resetMapManagers();
		for (ObjectMapManager oManager : objectMapManagers.values()) {
			oManager.initLocalMapFileList(playerName, room);
			GUI.gui.gameInfoConsole.appendInfo("Initialized files of "+oManager.paintLayer);
		}
		for (ObjectMapManager oManager : objectMapManagersLowerRoom.values()) {
			oManager.initLocalMapFileList(playerName, this.getLowerRoom(room));
			GUI.gui.gameInfoConsole.appendInfo("Initialized lowerRoom files of "+oManager.paintLayer);
		}
		for (MapManager mManager : mapManagers.values()) {
			mManager.initLocalMapFileList(playerName, room);
			GUI.gui.gameInfoConsole.appendInfo("Initialized files of "+mManager.paintLayer);
		}
		for (UndergroundMapManager ugrmManager : undergroundMapManagers.values()) {
			ugrmManager.initLocalUndergroundMapFileList(playerName, room);
			GUI.gui.gameInfoConsole.appendInfo("Initialized files of "+ugrmManager.paintLayer);
		}
		this.setInitingLocalMapFileList(false);
		GamePanel.gp.setTransparentLoad(0.0f);
	}
	
//	public void deleteOnlyMapsOfMapManagers() {
//		for (MapManager mapManager: mapManagers.values()) {
//			mapManager.pointList = new ArrayList<Point>();
//			mapManager.localMapList = new HashMap<Point, LocalMap>();
//		}
//		for (ObjectMapManager objectMapManager: objectMapManagers.values()) {
////			for (Point p : objectMapManager.pointList) {
////				objectMapManager.remove(p);
////			}
//			objectMapManager.pointList = new ArrayList<Point>();
//			objectMapManager.localMapList = new HashMap<Point, LocalMap>();
//		}
//		for (UndergroundMapManager undergroundMapManager: undergroundMapManagers.values()) {
////			for (Point p : undergroundMapManager.pointList) {
////				undergroundMapManager.remove(p);
////			}
//			undergroundMapManager.pointList = new ArrayList<Point>();
//			undergroundMapManager.localMapList = new HashMap<Point, LocalUndergroundMap>();
//		}
//	}
	
	public void resetMapManagers() {
		mapManagers.clear();
	    objectMapManagers.clear();
	    objectMapManagersLowerRoom.clear();
	    undergroundMapManagers.clear();
	    this.initMapManagers();
	}
	
	public HashMap<String, MapManager> getMapManagers() {
		return mapManagers;
	}
	
	public void setMapManagers(HashMap<String, MapManager> mapManagers) {
		this.mapManagers = mapManagers;
	}

	public HashMap<String, ObjectMapManager> getObjectMapManagers() {
		return objectMapManagers;
	}
	
	public HashMap<String, ObjectMapManager> getObjectMapManagersLowerRoom() {
		return objectMapManagersLowerRoom;
	}
	
	public HashMap<String, UndergroundMapManager> getUndergroundMapManagers() {
		return undergroundMapManagers;
	}

	
	public TileMapManager getTileMapManager() {
		return tileMapManager;
	}
	
		
	public void setTileMapManager(TileMapManager t) {
		tileMapManager = t;
	}
	
	public TileMapManager getTileUndergroundMapManager() {
		return tileUndergroundMapManager;
	}
	
	public void setTileUndergroundMapManager(TileMapManager t) {
		tileUndergroundMapManager = t;
	}

	public ArrayList<String> getSendMapList() {
		return sendMapList;
	}

	public void setSendMapList(ArrayList<String> sendMapList) {
		this.sendMapList = sendMapList;
	}
	
	public void initSendMapList() {
		File folder = new File(GameStates.standardMapFolder+playerName+"/"+GamePanel.gp.getPlayerEntity().getRoom());
		ArrayList<String> folderContent = MyUtil.listFilesForFolder(folder);
		ArrayList<String> sendMapListNew = new ArrayList<String>();
		for (String folderContentEntry : folderContent) {
			sendMapListNew.add(GamePanel.gp.getPlayerEntity().getRoom()+"/"+folderContentEntry);
		}
		setSendMapList(sendMapListNew);
	}
	
	public String takeFirstSendMap() {
		String retString =  null;
		if (sendMapList.size() > 0) {
			retString = sendMapList.get(0);
			sendMapList.remove(0);
		} 
		return retString;
		
	}
	
	public void addSendMapListEntry(String mapFile) {
//		System.out.println(mapFile);
		if (!this.sendMapList.contains(mapFile)) this.sendMapList.add(mapFile);
	}
	
	public void setNight(boolean b) {
		this.night = b;
	}
	
	public boolean isNight() {
		return this.night;
	}

	public boolean hasLight() {
		return light;
	}

	public void setLight(boolean light) {
		this.light = light;
	}

	public ArrayList<BigInteger> getSendItemList() {
		return sendItemList;
	}

	public void setSendItemList(ArrayList<BigInteger> sendItemList) {
		this.sendItemList = sendItemList;
	}

	public BigInteger takeFirstSendItem() {
		BigInteger retID = BigInteger.valueOf(0);
		if (sendItemList.size() > 0) {
			retID = sendItemList.get(0);
			sendItemList.remove(0);
		} 
		return retID;
		
	}
	
	public void addSendItemListEntry(BigInteger itemID) {
//		System.out.println(mapFile);
		if (!this.sendItemList.contains(itemID)) this.sendItemList.add(itemID);
	}

	public boolean isGameMaster() {
		return gameMaster;
	}

	public void setGameMaster(boolean gameMaster) {
		this.gameMaster = gameMaster;
		if (!isGameMaster()) {
			GamePanel.gp.setZoomFactor(GameStates.standardZoomFactor);
		}
		TextEffectManager.add(new TextEffect("GameMaster-Mode is "+ (isGameMaster() ? "ON (light = 100%)":"OFF")));
		this.selectLightByEnvironment();
		/** take care for the music and effects */
		if (GameModus.modus == GameModus.GAME) {
			this.selectBackgroundMusicByEnvironment();
		} else if (GameModus.modus == GameModus.MAINMENU) {
			GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.MAINMENU);
		}
	}
	
	public void selectBackgroundMusicByEnvironment() {
		if (!isGameMaster()) {
			WorldType worldType = this.getWorldType(GamePanel.gp.getPlayerEntity().getRoom());
			switch (worldType) {
			case BASE:  {
				if (GamePanel.gp.getLightIntensity() > GameStates.maxDay-(3*GameStates.timeStep)) {
					GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.FORESTBIRDS);
					GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.GAME);
				} else {
					// nightsound
					GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.NIGHTOWL);
					GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.EMPTY);
				}
				break;
			}
			case UNDERGROUND_A: {
				GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.DUNGEON);
				GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.EMPTY);
				break;
			}
			case UNDERGROUND_B: {
				GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.DUNGEON);
				GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.EMPTY);
				break;
			}
			case UNDERGROUND_C: {
				GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.DUNGEON);
				GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.EMPTY);
				break;
			}
			default: ;
			break;
			}
		} else {
			GameWindow.gw.sound.startMusic(MusicType.BACKGROUND, MusicName.GAMEMASTER);
			WorldType worldType = this.getWorldType(GamePanel.gp.getPlayerEntity().getRoom());
			switch (worldType) {
			case BASE:  {
				if (GamePanel.gp.getLightIntensity() > GameStates.maxDay-(3*GameStates.timeStep)) {
					GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.FORESTBIRDS);
				} else {
					// nightsound
					GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.NIGHTOWL);
				}
				break;
			}
			case UNDERGROUND_A: {
				GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.EMPTY);
				break;
			}
			case UNDERGROUND_B: {
				GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.EMPTY);
				break;
			}
			case UNDERGROUND_C: {
				GameWindow.gw.sound.startMusic(MusicType.ATMOSPHERE, MusicName.EMPTY);
				break;
			}
			default: ;
			break;
			}
		}
	}
	
	public void selectLightByEnvironment() {
		if (!isGameMaster()) {
			WorldType worldType = this.getWorldType(GamePanel.gp.getPlayerEntity().getRoom());
			switch (worldType) {
			case BASE:  {
				this.setLight(false);
				break;
			}
			default: {
				if (this.isUsingLight()) this.setLight(true); else this.setLight(false);
			};
			break;
			}
		} else {
			this.setLight(false);
		}
	}

	public boolean isInitingLocalMapFileList() {
		return initingLocalMapFileList;
	}

	public void setInitingLocalMapFileList(boolean initingLocalMapFileList) {
		this.initingLocalMapFileList = initingLocalMapFileList;
	}
	
	public WorldType getWorldType(String roomName) {
		WorldType worldType = WorldType.BASE;
		if (roomName.charAt(roomName.length()-4) == '0' ) {
			worldType = WorldType.BASE;
		}
		if (roomName.charAt(roomName.length()-4) == 'A' ) {
			worldType = WorldType.UNDERGROUND_A;
		}
		if (roomName.charAt(roomName.length()-4) == 'B' ) {
			worldType = WorldType.UNDERGROUND_B;
		}
		if (roomName.charAt(roomName.length()-4) == 'C' ) {
			worldType = WorldType.UNDERGROUND_C;
		}
		return worldType;
	}

	public int getUndergroundPaintTypeOfWorldType(WorldType worldType) {
		int paintType = GameStates.undergroundGrassTile;
		switch (worldType) {
		case BASE: {
			paintType = GameStates.undergroundGrassTile;
			break;
		}
		case UNDERGROUND_A: {
			paintType = GameStates.undergroundEarthTile;
			break;
		}
		case UNDERGROUND_B: {
			paintType = GameStates.undergroundStoneTile;
			break;
		}
		case UNDERGROUND_C: {
			paintType = GameStates.undergroundEarthTile;
			break;
		}
		default: paintType = GameStates.undergroundEarthTile;
		break;
		}
		return paintType;
	}

	public String getTwitchUserName() {
		return twitchUserName;
	}

	public void setTwitchUserName(String twitchUserName) {
		this.twitchUserName = twitchUserName;
	}

	public String getTwitchPWD() {
		return twitchPWD;
	}

	public void setTwitchPWD(String twitchPWD) {
		this.twitchPWD = twitchPWD;
	}

	public boolean isUsingTwitch() {
		return usingTwitch;
	}

	public void setUsingTwitch(boolean usingTwitch) {
		this.usingTwitch = usingTwitch;
	}

	public String getTwitchChannel() {
		return twitchChannel;
	}

	public void setTwitchChannel(String twitchChannel) {
		this.twitchChannel = twitchChannel;
	}
	
	public TwitchBot getTwitchBot() {
		return twitchBot;
	}

	public void setTwitchBot(TwitchBot twitchBot) {
		this.twitchBot = twitchBot;
	}

	public boolean isUsingLight() {
		return usingLight;
	}

	public void setUsingLight(boolean usingLight) {
		this.usingLight = usingLight;
	}

	public MySQLConnector getMySQL() {
		return mySQL;
	}

	public void setMySQL(MySQLConnector mySQL) {
		this.mySQL = mySQL;
	}

	public boolean isUsingWeather() {
		return usingWeather;
	}

	public void setUsingWeather(boolean usingWeather) {
		this.usingWeather = usingWeather;
	}
	

}
