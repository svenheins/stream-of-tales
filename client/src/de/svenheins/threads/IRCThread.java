/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import de.svenheins.handlers.IRCGetMessageHandler;
import de.svenheins.handlers.IRCSendMessageHandler;
import de.svenheins.main.GUI;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.StreamViewerManager;

public abstract class IRCThread implements Runnable{
	private String hostName;
	private int port;
	private String nickName;
	private String realName;
	private String oauthPWD;
	private String channelName;
	private Socket ircSocket;
	private Thread ircThread;
	private BufferedReader messageFromServer;
	private PrintWriter messageToServer;
	
	/** constructor 
	 * connecting, starting, login in and joining the given channel
	 * */
	public IRCThread(String hostName, int port, String nickName, String oauthPWD, String realName, String channelName){
		this.setHostName(hostName);
		this.setPort(port);
		this.setNickName(nickName);
		this.setOauthPWD(oauthPWD);
		this.setRealname(realName);
		this.setChannelName(channelName);
		connectToServer(this.getHostName(), this.getPort());
		start();
//		login();
//		joinChannel(this.getChannelName());
	}

	/** how to handle channel messages */
	public abstract void onMessage(String channel, String sender, String message);
	
    /** connect with server on port */
	private void connectToServer(String hostName, int port)	{
		try
		{
			ircSocket = new Socket(hostName, port);
			messageFromServer = new BufferedReader(new InputStreamReader( ircSocket.getInputStream()));
            messageToServer = new PrintWriter(new OutputStreamWriter(ircSocket.getOutputStream()));
		}
		catch(IOException e)
		{
			System.err.println("Error: Cannot connect to" + hostName + ":" + port);
		}
	}

	/** login on the server with a given oauth-pwd-phrase */
	public void login() {
        sendMessageToServer("PASS "+oauthPWD);
        sendMessageToServer("NICK " + nickName);
        sendMessageToServer("USER " + nickName + " 0 * :" + realName);
	}


	public String getNickName()	{
	   return nickName;
	}

	public void setNickName(String nickName) {
	   this.nickName = nickName;
	}

	public String getRealname() {
	   return realName;
	}
	
	public void setRealname(String realName) {
	   this.realName = realName;
	}

	/** start the thread */
	public void start() {
		ircThread = new Thread(this);
		ircThread.setDaemon(true);
		ircThread.start();
	}
	
	/** run the thread
	 * messageFromServer takes the input and processes it
	 */
	public void run() {
		try {
			while(!Thread.interrupted()) {
				String line = messageFromServer.readLine();
				if (line != null) {
					parseFromServer(line);
				}
				else {
				   /** something went wrong */
				   closeAll();
				   try
				   {
				      ircThread.sleep(10000);
				   }
				   catch(InterruptedException e) {
					   System.err.println(e);
				   }
				   ircThread = null;

				   /** retry to connect */
				   connectToServer(this.getHostName(), this.getPort());
				   start();
				   login();
				}
			}
		}
		
		catch(IOException e)
		{
		   System.err.println(e);
		}
	}

	/** shut down connections */
	public void closeAll()
	{
	   try
	   {
	      ircSocket.close();
	      messageFromServer.close();
	      messageToServer.close();
	   }
	   catch(IOException e)
	   {
	      System.err.println("IOException from socket, messageFromServer or messageToServer");
	   }
	   ircSocket = null;
	   messageFromServer = null;
	   messageToServer = null;
	}


	/** handle messages from server */
	public void parseFromServer(String message)
	{
		IRCGetMessageHandler messageHandler = new IRCGetMessageHandler(message);
		String command = messageHandler.getCommand();
		System.out.println(command);
		if (command.equals("PING")) {
			System.out.println("got PING");
			sendMessageToServer("PONG :" + messageHandler.getTrailing());
		} else if (command.equals("PRIVMSG")) {
			/** private message */
			String destination = messageHandler.getMiddle();
			if (destination.startsWith("#")) {
				String channelName = destination.substring(1);		       
				this.onMessage(channelName, messageHandler.getNick(), messageHandler.getTrailing());
			} else {
			   /** handling user-messages */
		      System.out.println("getPrefix: " + messageHandler.getPrefix());
		      System.out.println("getParams: " + messageHandler.getParams());
		      System.out.println("getServer: " + messageHandler.getServer());
		      System.out.println("getNick: " + messageHandler.getNick());
		      System.out.println("getUser: " + messageHandler.getUser());
		      System.out.println("getHost: " + messageHandler.getHost());
		      System.out.println("getTrailing: " + messageHandler.getTrailing());
		      System.out.println("getMiddle: " + messageHandler.getMiddle());
		   }
		}
		else if (command.equals("JOIN")) {
			/** user joined channel */
			String twitchUser = messageHandler.getNick().substring(0, 1).toUpperCase() + messageHandler.getNick().substring(1);
			System.out.println(twitchUser + " joined your channel");
			GUI.gui.gameInfoConsole.appendSimpleDate(twitchUser + " joined your channel");
//			TextEffectManager.add(new TextEffect(twitchUser + " joined your channel"));
		}
		else if (command.equals("PART")) {
			/** handling leaving channel */
//		   System.out.println("PART: getPrefix: " + messageHandler.getPrefix());
//		   System.out.println("PART: getServer: " + messageHandler.getServer());
//		   System.out.println("PART: getNick: " + messageHandler.getNick());
//		   System.out.println("PART: getUser: " + messageHandler.getUser());
//		   System.out.println("PART: getHost: " + messageHandler.getHost());
//		   System.out.println("PART: getTrailing: " + messageHandler.getTrailing());
//		   System.out.println("PART: getMiddle: " + messageHandler.getTrailing());  
			String twitchUser = messageHandler.getNick().substring(0, 1).toUpperCase() + messageHandler.getNick().substring(1);			
			
		   System.out.println(twitchUser + " left channel " + channelName + " " + messageHandler.getTrailing());
		   GUI.gui.gameInfoConsole.appendSimpleDate(twitchUser + " left channel " + messageHandler.getTrailing());
//		   TextEffectManager.add(new TextEffect(twitchUser + " left channel " + messageHandler.getTrailing()));
		   StreamViewerManager.remove(twitchUser);
		}
		else if (command.equals("QUIT")) {
			String twitchUser = messageHandler.getNick().substring(0, 1).toUpperCase() + messageHandler.getNick().substring(1);		
		   System.out.println(twitchUser+ " has quit " + messageHandler.getTrailing());
		   StreamViewerManager.remove(twitchUser);
		}
		else if (command.equals("NICK")){
			String twitchUser = messageHandler.getNick().substring(0, 1).toUpperCase() + messageHandler.getNick().substring(1);		
			System.out.println(twitchUser + " change nick to " + messageHandler.getTrailing());
		}
		else if (command.equals("NOTICE")) {
			System.out.println(messageHandler.getTrailing());
		}
		else if (command.equals("KICK")) {
			String twitchUser = messageHandler.getNick().substring(0, 1).toUpperCase() + messageHandler.getNick().substring(1);		
			System.out.println(twitchUser + " got kicked from " + messageHandler.getMiddle() + " for " + messageHandler.getTrailing());
			StreamViewerManager.remove(twitchUser);
		}
		else if (command.equals("001") ||  // RPL_WELCOME
		         command.equals("002") ||  // RPL_YOURHOST
                         command.equals("003") ||  // RPL_CREATED
                         command.equals("004"))    // RPL_MYINFO
                {
			System.out.println(messageHandler.getTrailing());
		}
		else if (command.equals("005")) {
			System.out.println("other server: ");
			System.out.println(messageHandler.getTrailing());
		}
		else if (command.equals("251") || command.equals("252") || command.equals("253") || command.equals("254") || command.equals("255") || command.equals("256") || command.equals("257") || command.equals("258") || command.equals("259") ) {
			System.out.println(messageHandler.getTrailing());
		}
		else if (command.equals("371") || command.equals("372") || command.equals("374") || command.equals("375") ||command.equals("376")
			) {
			System.out.println(messageHandler.getTrailing());
		}
		else if (command.equals("311") ) {
		   System.out.println("lineFromServer: " + message);
		   System.out.println("WHOIS:	getPrefix: " + messageHandler.getPrefix());
		   System.out.println("WHOIS:	getServer: " + messageHandler.getServer());
		   System.out.println("WHOIS:	getNick: " + messageHandler.getNick());
		   System.out.println("WHOIS:	getUser: " + messageHandler.getUser());
		   System.out.println("WHOIS:	getHost: " + messageHandler.getHost());
		   System.out.println("WHOIS:	getTrailing: " + messageHandler.getTrailing());
		   System.out.println("WHOIS:	getMiddle: " + messageHandler.getTrailing());
		   System.out.println("WHOIS:	getParams: " + messageHandler.getParams());
		}
		else if (command.equals("312"))	{
		   
		}
		else if (command.equals("313")) {
			System.out.println("  Operator: " + message);
		}
		else if (command.equals("317")) {

		}
		else if (command.equals("318")){
			System.out.println(messageHandler.getTrailing());
		}
		else if (command.equals("319"))	{
			System.out.println("  Channels: " + messageHandler.getTrailing());
		}
		else if (command.equals("301")) { // RPL_AWAY
			System.out.println("301: " + message);
		}
		else if (command.equals("305")) { // RPL_UNAWAY
		   System.out.println("305: " + message);
		}
		else if (command.equals("332")) {  // RPL_TOPIC
		   String topicChannelName = "";
		   StringTokenizer st = new StringTokenizer(messageHandler.getParams(), " \r\n");
		   for (int i = 0; st.hasMoreTokens();) {
			   String aToken = st.nextToken();
			   if (i == 1) {
				   topicChannelName = aToken;
				   break;
			   }
			   i++;
		   }
		   System.out.println("Topic of channel " + topicChannelName + " is: " + messageHandler.getTrailing());
		}
		else if (command.equals("TOPIC")) {
			String topicChannelName = "";
			StringTokenizer st = new StringTokenizer(messageHandler.getParams(), " \r\n");
			if (st.hasMoreTokens())
				topicChannelName = st.nextToken();
			System.out.println(messageHandler.getNick() + " has changed the topic of " + topicChannelName + " to: " + messageHandler.getTrailing());
		}
		else if (command.equals("353")) {
		   System.out.println("Line From Server: >" + message + "<");
		   String channelName = messageHandler.getMiddle();
		   int index = channelName.lastIndexOf("#");
		   channelName = channelName.substring(index);
		}
		else if (command.equals("433")) {
		   System.out.println("ERR_NICKNAMEINUSE: " + message);
		   System.out.println("trying to connect again.... " + hostName + ":" + port);
		   login();
		} else {
			System.out.println(message);
			System.out.println(messageHandler.getTrailing());
		}
	}

	/** send a message to the server 
	 * the message has to be in the irc-specific format
	 * i.e. channel-messages = "PRIVMSG #channelName : text"
	 * */ 
	public void sendMessageToServer(String message) {
		messageToServer.print(message + "\r\n");
		messageToServer.flush();
	}

	/** join a channel */
	public void joinChannel(String channelName) {
		if (channelName.startsWith("#")) {
			sendMessageToServer("JOIN " + channelName);
        } else {
        	sendMessageToServer("JOIN #" + channelName);
        }
	}
	
	/** handle special sendMessages */
	private void handleSendMessage(String message)
	{
	   IRCSendMessageHandler ircSendMessage = new IRCSendMessageHandler(message);
	   if (ircSendMessage.getCommand().equals("JOIN"))
	   {
	      sendMessageToServer(ircSendMessage.getCommand() + " " + ircSendMessage.getParam1()  + " " + ircSendMessage.getMessage());
	   }
	   else if (ircSendMessage.getCommand().equals("PART") || ircSendMessage.getCommand().equals("LEAVE")) {
	      sendMessageToServer("PART " + ircSendMessage.getParam1() + " :" +ircSendMessage.getMessage());
	   } else if (ircSendMessage.getCommand().equals("QUIT")) {
		   sendMessageToServer("QUIT :" + ircSendMessage.getParam1() + " " + ircSendMessage.getMessage());
	   } else if (ircSendMessage.getCommand().equals("WHOIS")) {
		   sendMessageToServer("WHOIS " + ircSendMessage.getParam1() + " " + ircSendMessage.getMessage());
	   } else if (ircSendMessage.getCommand().equals("MSG")) {
		   sendMessageToServer("PRIVMSG " + ircSendMessage.getParam1() + " :" + ircSendMessage.getMessage());
	   } else if (ircSendMessage.getCommand().equals("NICK")) {
		   sendMessageToServer("NICK " + ircSendMessage.getParam1());
	   } else if (ircSendMessage.getCommand().equals("TOPIC")){
		   System.out.println("Got a message: " + message);
		   if (ircSendMessage.getMessage().equals("")) {
	         sendMessageToServer(ircSendMessage.getCommand() + " " + ircSendMessage.getParam1() + " :");
	       } else {
	    	   System.out.println("Sending a message: " + ircSendMessage.getCommand() + " " + ircSendMessage.getParam1() + " :" + ircSendMessage.getMessage());
	    	   sendMessageToServer(ircSendMessage.getCommand() + " " + ircSendMessage.getParam1() + " :" +ircSendMessage.getMessage());
	       }
	   } else {
		   sendMessageToServer(ircSendMessage.getCommand() + " :" + ircSendMessage.getParam1()  + " " +ircSendMessage.getMessage());
	   }
	}

	protected String getOauthPWD() {
		return oauthPWD;
	}

	public void setOauthPWD(String oauthPWD) {
		this.oauthPWD = oauthPWD;
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
