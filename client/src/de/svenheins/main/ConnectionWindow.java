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
	    final JDialog meinJDialog = new JDialog();
	    meinJDialog.setModal(true);
	    meinJDialog.setTitle("Connection Settings");
	    meinJDialog.setSize(450,300);
	
	    // Hier erzeugen wir unsere JPanels
	    JPanel panel = new JPanel();
	    
	    final JTextField hostField = new JTextField("localhost", 30);
	    final JTextField portField = new JTextField("1139", 6);
	    JButton connectButton = new JButton("Connect");
	    connectButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	String strHost = hostField.getText();
	        	String strPort = portField.getText();
	        	GameWindow.gw.setHostName(strHost);
	        	GameWindow.gw.setPortNumber(strPort);
	        	// GameWindow.gw.MessageHandler.login(strHost, strPort);
	        	GameWindow.gw.login();
	        	
	        	meinJDialog.dispose();
	        }
	    });
	    JButton cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	meinJDialog.dispose();
	        }
	    });
	    panel.add(hostField);
	    panel.add(portField);
	    panel.add(connectButton);
	    panel.add(cancelButton);
	    
	    // Erzeugung eines JTabbedPane-Objektes
	    JTabbedPane tabpane = new JTabbedPane
	        (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
	
	    // Hier werden die JPanels als Registerkarten hinzugefügt
	    tabpane.addTab("Connection", panel);
	
	    // JTabbedPane wird unserem Dialog hinzugefügt
	    meinJDialog.add(tabpane);
	    // Wir lassen unseren Dialog anzeigen
	    meinJDialog.setVisible(true);
	}
}
