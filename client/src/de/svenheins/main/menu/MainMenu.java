package de.svenheins.main.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.svenheins.handlers.FileAddAction;
import de.svenheins.handlers.FileAddTextureAction;
import de.svenheins.main.GameModus;
import de.svenheins.main.GamePanel;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.MapManager;
import de.svenheins.managers.PlayerManager;
import de.svenheins.managers.TileSetManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;

public class MainMenu extends JMenuBar {

	public MainMenu(GamePanel panel) {
		JMenu menu = new JMenu("File");
		// Add "open" to import a *.svg-file
//		JMenuItem item = new JMenuItem("Open");
//		item.addActionListener(new FileOpenAction(panel.getSpace()));
//		menu.add(item);
		JMenuItem item2 = new JMenuItem("Add Space");
		item2.addActionListener(new FileAddAction(panel.getSpaceAdd()));
		menu.add(item2);
		
		JMenuItem itemAddTexture = new JMenuItem("Add Texture");
		itemAddTexture.addActionListener(new FileAddTextureAction());
		menu.add(itemAddTexture);
		
		JMenuItem mainMenuItem = new JMenuItem("Menu");
		mainMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 GameModus.modus = GameModus.MAINMENU;
			}
		});
		menu.add(mainMenuItem);
		
		final JMenuItem setGameMaster = new JMenuItem("Set Game-Master");
		setGameMaster.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (!GameWindow.gw.getGameMasterName().equals(GameWindow.gw.getPlayerName())) {
            		GameWindow.gw.setGameMasterName(GameWindow.gw.getPlayerName());
            		setGameMaster.setText("Set Player");
            	} else {
            		GameWindow.gw.setGameMasterName("standard");
            		setGameMaster.setText("Set Game-Master");
            	}
            	
            }
		});
		menu.add(setGameMaster);
		
		JMenuItem logoutItem = new JMenuItem("Logout");
		logoutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameModus.modus = GameModus.MAINMENU;
				GameWindow.gw.logout();
				
			}

			
		});
		menu.add(logoutItem);
		
		JMenuItem propertiesItem = new JMenuItem("Properties");
		propertiesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configureProperties();
			}
		});
		menu.add(propertiesItem);
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameWindow.gw.dispose();
		         System.exit(0); //calling the method is a must				
			}
		});
		menu.add(exitItem);
		
		// Next Menu-Item
		JMenu menu2 = new JMenu("View");
		final JMenuItem item21 = new JMenuItem("reset zoom");
		item21.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            		GamePanel.gp.setZoomFactor(1.0f);
            }
		});
		menu2.add(item21);
		
		final JMenuItem deleteTile = new JMenuItem("Delete - Off");
		// Next Menu-Item
		JMenu tileMenu = new JMenu("Tile");
		deleteTile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (GamePanel.gp.isDeleteModus()) {
            		GamePanel.gp.setDeleteModus(false);
            		deleteTile.setText("Delete - Off");
            	} else {
            		GamePanel.gp.setDeleteModus(true);
            		deleteTile.setText("Delete - On");
            	}
            	
            }
		});
		tileMenu.add(deleteTile);
		
		final JMenuItem loadTiles = new JMenuItem("Load Tiles of this player");
		// Next Menu-Item
		loadTiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	MapManager mapManager = GameWindow.gw.getMapManagers().get(GamePanel.gp.getPaintLayer());
            	mapManager.emptyAll();
            	mapManager.loadLocalMaps(GameWindow.gw.getPlayerName());
            	
            }
		});
		tileMenu.add(loadTiles);

		this.add(menu);
		this.add(menu2);
		this.add(tileMenu);
		
	}
	
	public void configureProperties() {

		/** create dialog */
		final JDialog propertyJDialog = new JDialog();
		propertyJDialog.setModal(true);
		propertyJDialog.setTitle("Properties-Editor");
		propertyJDialog.setSize(450,300);
	
		/** create JPanels */
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		
		JComboBox comboMapPLayer = new JComboBox();
		List<BigInteger> sortedPlayerList = PlayerManager.idList;
//		Arrays.sort(sortedPlayerList);
		for (int i =0; i < sortedPlayerList.size(); i++) {
			comboMapPLayer.addItem(PlayerManager.get(sortedPlayerList.get(i)).getName());
		}
		comboMapPLayer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
	//				System.out.println((String) ((JComboBox) e.getSource()).getSelectedItem());
//					entity.setTileSet( TileSetManager.manager.getTileSet((String) ((JComboBox) e.getSource()).getSelectedItem()));
	//				GameWindow.gw.send(ClientMessages.editPlayerAddons(space.getId(), space.getTextureName(), space.getRGB(), space.getTrans(), isFilled, space.getScale(), space.getArea()));
					if (GameWindow.gw.isLoggedIn()) {
						/** create map folder for the chosen player 
						 * and download all relevant maps */
						
						
//						GameWindow.gw.send(ClientMessages.editPlayerAddons(entity.getId(), GameWindow.gw.getPlayerName(), entity.getTileSet().getName(), entity.getTileSet().getFileName(), (int) entity.getWidth(), (int) entity.getHeight(), entity.getCountry(), entity.getGroupName(), entity.getExperience()));
					}
				}
		});
		panel1.add(comboMapPLayer);
	
		final JTextField textFieldPaintType = new JTextField(""+(int)GamePanel.gp.getPaintType(), 6);
		final JTextField textFieldPaintLayer = new JTextField(""+GamePanel.gp.getPaintLayer(), 6);
		JButton confirmButton = new JButton("Ok");
		confirmButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	String strPaintType = textFieldPaintType.getText();
		    	String strLayer = textFieldPaintLayer.getText();
				try {
					int editPaint = Integer.parseInt(strPaintType);
					GamePanel.gp.setPaintType(editPaint);
					GamePanel.gp.setPaintLayer(strLayer);
				} catch (NumberFormatException numberException) {
					System.out.println("Wrong number format!");
				}
				propertyJDialog.dispose();
		    }
		});
		JButton button2 = new JButton("Abbrechen");
	
		
	
		panel2.add(new JTextArea("Paint-Type:"));
		panel2.add(textFieldPaintType);
		panel2.add(new JTextArea("Paint-Layer:"));
		panel2.add(textFieldPaintLayer);
		panel2.add(confirmButton);
		panel2.add(button2);
	
		/** create jtabbedPane */
		JTabbedPane tabpane = new JTabbedPane
		    (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
	
		/** add tabs */
		tabpane.addTab("Map", panel1);
		tabpane.addTab("TileSet", panel2);
		tabpane.addTab("Stuff", panel3);
		tabpane.addTab("More Stuff", panel4);
	
		/** add to menu */
		propertyJDialog.add(tabpane);
		/** show it */
		propertyJDialog.setVisible(true);
}
}


