package de.svenheins.handlers;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import de.svenheins.objects.Space;

/**
 * @author Sven Heins
 * 
 * opens a svg and parse it into a space (with the space-constructor)
 *
 */
public class FileOpenAction implements ActionListener {
	private final Space space;
	
	public FileOpenAction( Space space){
		this.space = space;
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
		if(file != null)
			try {
				space.setPolygon(file.toURL());
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	
	
}
