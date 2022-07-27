package de.svenheins.main;


//import de.svenheins.Client.NullClientChannelListener;
import de.svenheins.WorldClient;
import de.svenheins.handlers.ClientMessageHandler;
import de.svenheins.handlers.ConsoleInputHandler;
import de.svenheins.managers.EntityManager;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.objects.Entity;
import de.svenheins.objects.IngameConsole;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;


public class GameWindow extends JFrame implements SimpleClientListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private String title;
	public final int hoehe;
	public final int breite;
	private Dimension dim;
	private GamePanel panel;
	private StatPanel menuPanel;
	public final JMenuItem item21= new JMenuItem("Pause");
	public final JMenuItem item22= new JMenuItem("Server aktualisieren");
	
	public static GameWindow gw;
	private GraphicsDevice device;
    private boolean isFullScreen = false;
    private boolean loggedIn, superUser;
    private boolean showConsole, showInfoConsole;
    
    private ConsoleInputHandler consoleInput;
    public IngameConsole gameConsole;
    public IngameConsole gameInfoConsole;


    /** The name of the host property. */
    public static final String HOST_PROPERTY = "tutorial.host";

    /** The default hostname. */
    public static final String DEFAULT_HOST = "localhost";//"193.174.43.177";
    private String hostName;

    /** The name of the port property. */
    public static final String PORT_PROPERTY = "tutorial.port";

    /** The default port. */
    public static final String DEFAULT_PORT = "1139";//"62964";//"1139";
    private String portNumber;
    
    private String loginName = "";
    private String loginPassword = "";
    

    /** The message encoding. */
    public static final String MESSAGE_CHARSET = "UTF-8";

    /** The {@link SimpleClient} instance for this client. */
    protected final SimpleClient simpleClient;
    
    /** The random number generator for login names. */
    private final Random random = new Random();
    
    /** The Player Name*/
    protected String player;
    
    /** Map that associates a channel name with a {@link ClientChannel}. */
    protected final Map<String, ClientChannel> channelsByName =
        new HashMap<String, ClientChannel>();
    
    /** Sequence generator for counting channels. */
    protected final AtomicInteger channelNumberSequence =
        new AtomicInteger(1);
    
    /** HelloChannelClient instance for the player name etc*/
    protected static WorldClient worldClient;

	
	public GameWindow(String title, int breite, int hoehe){
		super(title);

		gw = this;
		
		this.breite = breite;
		this.hoehe = hoehe;
		//this.title = title;
		dim = new Dimension(this.breite, this.hoehe);
		
		simpleClient = new SimpleClient(this);
		setLoggedIn(false);
		setSuperUser(true);

		/** Main-GamePanel
		 * this panel will be scaled or rotated*/
		panel = new GamePanel();
		panel.setBackground(new Color(0,0,0));
		panel.setDoubleBuffered(true);
		
		/** add the menuPanel, that lies above the GamePanel */
		menuPanel = new StatPanel();
		setGlassPane(menuPanel);
		menuPanel.setVisible(true);
		
		JInternalFrame frame1;
		frame1 = new JInternalFrame("Frame1", true, true, true, true);
        frame1.setBounds(20, 200, 150, 100);
        frame1.setVisible(true);
		
		setMaximumSize(dim);
		setMinimumSize(dim);
		setPreferredSize(dim);
		
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device =ge.getDefaultScreenDevice();
//        GameStates.setWidth(ge.getMaximumWindowBounds().width);
//        GameStates.setHeight(ge.getMaximumWindowBounds().height);
        GameStates.setWidth(breite);
        GameStates.setHeight(hoehe);
 
		consoleInput = new ConsoleInputHandler();
		gameConsole = new IngameConsole(new Point(20, 20), GameStates.getWidth()/2-60, GameStates.getHeight()-60, new int[]{120, 120, 220}, 0.75f, true, 20);
		gw.addKeyListener(consoleInput);
		this.showConsole = false;
		
		gw.addKeyListener(GamePanel.gp.getPlayer().getInputHandler());
		
		/** InfoConsole */
		gameInfoConsole = new IngameConsole(new Point(GameStates.getWidth()/2, 20), GameStates.getWidth()/2-60, GameStates.getHeight()-60, new int[]{120, 120, 120}, 0.75f, false, 14);
        this.showInfoConsole = false;
		
//        if(device.isFullScreenSupported()){
//            device.setFullScreenWindow(this);
//        }
		
		setResizable(true);
		
		add(panel);
		
		// Add main-menu
		JMenuBar mbar = new JMenuBar();
		JMenu menu = new JMenu("File");
		// Add "open" to import a *.svg-file
//		JMenuItem item = new JMenuItem("Open");
//		item.addActionListener(new FileOpenAction(panel.getSpace()));
//		menu.add(item);
//		JMenuItem item2 = new JMenuItem("Add");
//		item2.addActionListener(new FileAddAction(panel.getSpaceAdd()));
//		menu.add(item2);
		JMenuItem mainMenuItem = new JMenuItem("Menu");
		mainMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 GameModus.modus = GameModus.MAINMENU;
			}
		});
		menu.add(mainMenuItem);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dispose();
		         System.exit(0); //calling the method is a must				
			}
		});
		menu.add(exitItem);
		
		// Next Menu-Item
		JMenu menu2 = new JMenu("Simulation");
		item21.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (GamePanel.gp.isPaused()) {
            		item21.setText("Pause");
	            	GamePanel.gp.setPause(false);
            	} else {
	            	item21.setText("Start");
	            	GamePanel.gp.setPause(true);
            	}
            }
		});
		item22.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	EntityManager.emptyAll();
            	SpaceManager.emptyAll();
            	GamePanel.gp.setServerInitialized(false);
            	
            }
		});
		menu2.add(item21);
		menu2.add(item22);

		mbar.add(menu);
		mbar.add(menu2);
		// Comment to unsupport Menu
		this.setJMenuBar(mbar);
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		setVisible(true);
		gw.setFocusable(true);
		gw.requestFocus();
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
     * Appends the given message to the output text pane.
     *
     * @param x the message to append to the output text pane
     */
    public void appendOutput(String x) {
//        outputArea.append(x + "\n");
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

    /**
     * Displays the given string in this client's status bar.
     *
     * @param status the status message to set
     */
    public void setStatus(String status) {
        appendOutput("Status Set: " + status);
//        statusLabel.setText("Status: " + status);
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
        this.player = this.getLoginName();//"guest-" + random.nextInt(1000);
//    	this.player = "player";
        setStatus("Logging in as " + player);
        String password = this.getLoginPassword();
        return new PasswordAuthentication(player, password.toCharArray());
    	
    }

    /**
     * {@inheritDoc}
     * <p>
     * Enables input and updates the status message on successful login.
     */
    public void loggedIn() {
//        inputPanel.setEnabled(true);
        setStatus("Logged in");
        setLoggedIn(true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the status message on failed login.
     */
    public void loginFailed(String reason) {
        setStatus("Login failed: " + reason);
        setLoggedIn(false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Disables input and updates the status message on disconnect.
     */
    public void disconnected(boolean graceful, String reason) {
//        inputPanel.setEnabled(false);
        setStatus("Disconnected: " + reason);
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
        setStatus("reconnected");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the status message when reconnection is attempted.
     */
    public void reconnecting() {
        setStatus("reconnecting");
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
        appendOutput("Joined to channel " + channelName);
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
            appendOutput("Removed from channel " + channel.getName());
        }

        /**
         * {@inheritDoc}
         * <p>
         * Formats and displays messages received on a channel.
         */
        public void receivedMessage(ClientChannel channel, ByteBuffer message) {
            appendOutput("[" + channel.getName() + "/ " + channelNumber +
                "] " + decodeString(message));
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


	public boolean isSuperUser() {
		return superUser;
	}


	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}


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
}
