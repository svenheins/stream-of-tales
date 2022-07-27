package de.svenheins.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
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
	    
	    final JTextField hostField = new JTextField("localhost", 30);
	    final JTextField portField = new JTextField("1139", 6);
	    final JTextField loginNameField = new JTextField("user1", 10);
	    final JTextField loginPasswordField = new JTextField("pwd1", 10);
	    JButton connectButton = new JButton("Connect");
	    connectButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	String strHost = hostField.getText();
	        	String strPort = portField.getText();
	        	String strLoginName = loginNameField.getText();
	        	String strLoginPassword = loginPasswordField.getText();
	        	GameWindow.gw.setHostName(strHost);
	        	GameWindow.gw.setPortNumber(strPort);
	        	GameWindow.gw.setLoginName(strLoginName);
	        	GameWindow.gw.setLoginPassword(strLoginPassword);
	        	// GameWindow.gw.MessageHandler.login(strHost, strPort);
	        	GameWindow.gw.login();
	        	
	        	connectionDialog.dispose();
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
	    
	    // Erzeugung eines JTabbedPane-Objektes
	    JTabbedPane tabpane = new JTabbedPane
	        (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
	
	    // Hier werden die JPanels als Registerkarten hinzugefügt
	    tabpane.addTab("Connection", panel);
	
	    // JTabbedPane wird unserem Dialog hinzugefügt
	    connectionDialog.add(tabpane);
	    // Wir lassen unseren Dialog anzeigen
	    connectionDialog.setVisible(true);
	}
}
