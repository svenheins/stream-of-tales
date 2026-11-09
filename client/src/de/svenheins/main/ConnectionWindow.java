/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class ConnectionWindow {
	
	public ConnectionWindow() {
		// Erzeugung eines neuen Dialoges
	    final JDialog connectionDialog = new JDialog();
	    connectionDialog.setModal(true);
	    connectionDialog.setTitle("Connection Settings");
	    connectionDialog.setSize(450,300);
	
	    // Hier erzeugen wir unsere JPanels
	    JPanel panel = new JPanel();
	    JPanel twitchPanel = new JPanel();
		 // Erzeugung eines JTabbedPane-Objektes
	    JTabbedPane tabpane = new JTabbedPane
	        (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
	    
	    final JTextField hostField = new JTextField("localhost", 30);
	    final JTextField portField = new JTextField("1139", 6);
	    final JTextField loginNameField = new JTextField("Arthur", 10);
	    final JPasswordField loginPasswordField = new JPasswordField("123456", 10);
	    JButton connectButton = new JButton("Connect");
	    connectButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	String strHost = hostField.getText();
	        	String strPort = portField.getText();
	        	String strLoginName = loginNameField.getText();
	        	if (strLoginName.length() >15) strLoginName = strLoginName.substring(0, 15);
	        	char[] strLoginPasswordChar = loginPasswordField.getPassword();
//	        	char[] twitchPWDChar = twitchPWDButton.getPassword();
	        	String strLoginPassword = String.valueOf(strLoginPasswordChar);
	        	GameWindow.gw.setHostName(strHost);
	        	GameWindow.gw.setPortNumber(strPort);
	        	GameWindow.gw.setLoginName(strLoginName);
	        	GameWindow.gw.setLoginPassword(strLoginPassword);
	        	// GameWindow.gw.MessageHandler.login(strHost, strPort);
	        	GameWindow.gw.login();
	        	
	        	connectionDialog.dispose();
	        	GameModus.modus = GameModus.LOADING;
	        }
	    });
	    JButton cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	connectionDialog.dispose();
	        }
	    });
	    panel.add(hostField);
	    panel.add(portField);
	    panel.add(loginNameField);
	    panel.add(loginPasswordField);
	    panel.add(connectButton);
	    panel.add(cancelButton);
	    

	    
	    final JTextField twitchUserNameButton = new JTextField("TwitchUserName", 10);
	    final JPasswordField twitchPWDButton = new JPasswordField("123456", 10);
	    final JTextField twitchChannelNameButton = new JTextField("TwitchUserName", 10);
	    final JTextField twitchHostNameButton = new JTextField("irc.twitch.tv", 10);
	    JButton connectTwitchButton = new JButton("Connect with IRC-Host");
	    connectTwitchButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	String twitchUserName = twitchUserNameButton.getText();
	        	char[] twitchPWDChar = twitchPWDButton.getPassword();
	        	String twitchPWD = String.valueOf(twitchPWDChar);
	        	String twitchChannelName = twitchChannelNameButton.getText();
	        	String hostName = twitchHostNameButton.getText();
	        	GameWindow.gw.setTwitchUserName(twitchUserName);
	        	GameWindow.gw.setTwitchPWD(twitchPWD);
	        	GameWindow.gw.setTwitchChannel(twitchChannelName);
	        	GameWindow.gw.twitchLogin(hostName, twitchUserName, twitchPWD, twitchChannelName);
//	        	tabpane.setSelectedIndex(0);
//	        	GameModus.modus = GameModus.LOADING;
	        }
	    });
	    
	    JButton loginTwitchButton = new JButton("Login");
	    loginTwitchButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	String twitchUserName = twitchUserNameButton.getText();
	        	char[] twitchPWDChar = twitchPWDButton.getPassword();
	        	String twitchPWD = String.valueOf(twitchPWDChar);
	        	String twitchChannelName = twitchChannelNameButton.getText();
	        	String hostName = twitchHostNameButton.getText();
	        	GameWindow.gw.setTwitchUserName(twitchUserName);
	        	GameWindow.gw.setTwitchPWD(twitchPWD);
	        	GameWindow.gw.setTwitchChannel(twitchChannelName);
	        	GameWindow.gw.getTwitchBot().login();
	        	GameWindow.gw.getTwitchBot().joinChannel(twitchChannelName);
	        	tabpane.setSelectedIndex(0);
//	        	GameModus.modus = GameModus.LOADING;
	        }
	    });
//	    login();
//		joinChannel(this.getChannelName());
	    
	    JButton twitchCancelButton = new JButton("Cancel");
	    twitchCancelButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	connectionDialog.dispose();
	        }
	    });
	    twitchPanel.add(twitchUserNameButton);
	    twitchPanel.add(twitchPWDButton);
	    twitchPanel.add(twitchChannelNameButton);
	    twitchPanel.add(twitchHostNameButton);
	    twitchPanel.add(connectTwitchButton);
	    twitchPanel.add(twitchCancelButton);
	    twitchPanel.add(loginTwitchButton);
	    
	
	    // Hier werden die JPanels als Registerkarten hinzugefügt
	    tabpane.addTab("Connection", panel);
	    tabpane.addTab("IRC-Channel", twitchPanel);
	
	    // JTabbedPane wird unserem Dialog hinzugefügt
	    connectionDialog.add(tabpane);
	    // Wir lassen unseren Dialog anzeigen
	    connectionDialog.setVisible(true);
	}
}
