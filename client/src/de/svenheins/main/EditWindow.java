package de.svenheins.main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import de.svenheins.functions.MyStrings;


import de.svenheins.animation.SpaceAnimation;
import de.svenheins.animation.SpaceDisappear;

import de.svenheins.objects.AlienEntity;
import de.svenheins.objects.LocalObject;
import de.svenheins.objects.ShipEntity;
import de.svenheins.objects.Space;

public class EditWindow {
	//private Color color;

	public EditWindow(final LocalObject o){
		
		if (o instanceof AlienEntity && o != null) {
			editAlien((AlienEntity) o );
		}
		
		if (o instanceof Space && o != null) {
			editSpace((Space) o);
		}
		if (o instanceof ShipEntity && o != null) {
			editShip((ShipEntity) o);
		}
	}
	
	private void editSpace(final Space space) {
				
		// Erzeugung eines neuen Dialoges
        final JDialog spaceDialog = new JDialog();
        spaceDialog.setModal(true);
        spaceDialog.setTitle("Space Editor");
        spaceDialog.setSize(450,300);
 
        final JPanel spacePanel = new JPanel();
        JTabbedPane tabpane = new JTabbedPane
                (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
        
        JButton colorButton = new JButton("Change Color");
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Color s_color = new Color(space.getRGB()[0], space.getRGB()[1], space.getRGB()[2]);
            	Color color = JColorChooser.showDialog(GamePanel.gp, "Choose Color", s_color);
            	int[] rgb = new int[]{color.getRed(), color.getGreen(), color.getBlue()};
        		space.setRGB(rgb);
        		SpaceAnimation spaceAni = space.getSpaceAnimation();
        		if (spaceAni instanceof SpaceDisappear) {
        			((SpaceDisappear) spaceAni).setRGBBefore(rgb);
        		}
            }
        });
        spacePanel.add(colorButton);
        
        JButton saveButton = new JButton("Save Space");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// TODO: Save Space under chosen directory
            	JFileChooser fc = new JFileChooser();

            	int returnVal = fc.showSaveDialog(spacePanel);

            	if (returnVal == JFileChooser.APPROVE_OPTION) 
            	{
            		try {
	            	    File toSave = fc.getSelectedFile();
	            	    FileWriter fstream = new FileWriter(toSave);
	            	    BufferedWriter out = new BufferedWriter(fstream);
//	            	    out.write("Hello Java");
	            	    out.write(MyStrings.Space2SVG(space));
	            	    //Close the output stream
	            	    out.close();
            		} catch(Exception error) {
            			System.err.println("Error: "+error.getMessage());
            		}
            	}
            	else
            	{
            	    //user pressed cancel
            	}

            }
        });
        spacePanel.add(saveButton);
     
        tabpane.addTab("Allgemein", spacePanel);
 
        spaceDialog.add(tabpane);
        spaceDialog.setVisible(true);
	}
	
	private void editShip(final ShipEntity ship) {

		// Erzeugung eines neuen Dialoges
        final JDialog meinJDialog = new JDialog();
        meinJDialog.setModal(true);
        meinJDialog.setTitle("Ship Editor");
        meinJDialog.setSize(450,300);
 
        // Hier erzeugen wir unsere JPanels
        JPanel panelRot = new JPanel();
        JPanel panelBlue = new JPanel();
        JPanel panelGreen = new JPanel();
        JPanel panelDark = new JPanel();
        
        final JTextField textField = new JTextField("100", 10);
        JButton button1 = new JButton("Ok");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String str = textField.getText();
    			try {
    				int editY = Integer.parseInt(str);
    		    //Integer integerZahl = new Integer(stringZahl);
    				ship.setY((double) editY);
    			} catch (NumberFormatException numberException) {
    				System.out.println("Wrong number format!");
    			}
            	meinJDialog.dispose();
            }
        });
        JButton button2 = new JButton("Abbrechen");
        panelBlue.add(textField);
        panelBlue.add(button1);
        panelBlue.add(button2);
        
        // Erzeugung eines JTabbedPane-Objektes
        JTabbedPane tabpane = new JTabbedPane
            (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
 
        // Hier werden die JPanels als Registerkarten hinzugefügt
        tabpane.addTab("Allgemein", panelRot);
        tabpane.addTab("Aussehen", panelBlue);
        tabpane.addTab("Verhalten", panelDark);
        tabpane.addTab("Sonstiges", panelGreen);
 
        // JTabbedPane wird unserem Dialog hinzugefügt
        meinJDialog.add(tabpane);
        // Wir lassen unseren Dialog anzeigen
        meinJDialog.setVisible(true);
	}
	
	private void editAlien(AlienEntity alien) {
		String str = JOptionPane.showInputDialog( "Bitte Zahl eingeben" );
		try {
			int editY = Integer.parseInt(str);
	    //Integer integerZahl = new Integer(stringZahl);
			alien.setY((double) editY);
		} catch (NumberFormatException e) {
			System.out.println("Wrong number format!");
		}
	}
}
