package de.svenheins.handlers;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigInteger;
import java.net.MalformedURLException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import de.svenheins.main.GameWindow;
import de.svenheins.managers.SpaceManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;

import de.svenheins.objects.Space;

/**
 * @author Sven Heins
 * 
 * opens a svg and parse it into a space (with the space-constructor)
 *
 */
public class FileAddAction implements ActionListener {
	private Space spaceAdd;
	
	public FileAddAction(Space spaceAdd){
		this.spaceAdd = spaceAdd;
	}
	
	public void actionPerformed( ActionEvent e){
		
		JFileChooser d = new JFileChooser();
		d.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
//				return "*.svg;*.txt";
				return "*.svg";
			}
			
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
//				return f.isDirectory() || f.getName().toLowerCase().endsWith(".svg") || f.getName().toLowerCase().endsWith(".txt");
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".svg");
			}
		} );
		
		d.showOpenDialog(null);
		File file = d.getSelectedFile();
		if(file != null) {
			spaceAdd = new Space("Start.svg", BigInteger.valueOf(0), new int[]{20, 230, 40}, true, 0.5f, 1.0f);
			try {
				spaceAdd.setPolygon(file.toURL());
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			spaceAdd.setId(SpaceManager.getMax().add(BigInteger.valueOf(1)));
			spaceAdd.setPolyX(0);
			spaceAdd.setPolyY(0);
			System.out.println("send ID: "+spaceAdd.getId());
			SpaceManager.add(spaceAdd);
			/** send space to server */ 
			GameWindow.gw.send(ClientMessages.uploadObject(OBJECTCODE.SPACE, spaceAdd.getId(), spaceAdd));
		}
	}
	
	
}
