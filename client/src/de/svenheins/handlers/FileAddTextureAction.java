package de.svenheins.handlers;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import de.svenheins.functions.MyUtil;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;
import de.svenheins.managers.ClientTextureManager;
//import de.svenheins.managers.TextureManager;
import de.svenheins.messages.ClientMessages;
import de.svenheins.messages.OBJECTCODE;
import de.svenheins.objects.Space;



public class FileAddTextureAction implements ActionListener {
	private BufferedImage image;

	public FileAddTextureAction() {
		this.image = null;
	}
	
	public FileAddTextureAction(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		JFileChooser d = new JFileChooser();
		d.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
//				return "*.svg;*.txt";
				return "*.jpg;*.png";
			}
			
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
//				return f.isDirectory() || f.getName().toLowerCase().endsWith(".svg") || f.getName().toLowerCase().endsWith(".txt");
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".png");
			}
		} );
		
		d.showOpenDialog(null);
		File file = d.getSelectedFile();
		if (file != null) {
			String fileName= file.getName();
			String fileFormat = "";
			
			if (file.getName().toLowerCase().endsWith(".jpg")) fileFormat = "jpg"; else fileFormat = "png";
			
			if(file != null) {
				BufferedImage in;
				BufferedImage newImage = null;
				try {
					in = ImageIO.read(file);
					if (fileFormat == "jpg") newImage = new BufferedImage(in.getWidth(null), in.getHeight(null), BufferedImage.TYPE_INT_RGB);
			        else if (fileFormat == "png") newImage = new BufferedImage(in.getWidth(null), in.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			        else System.out.println("wrong file format!");
					Graphics2D g = newImage.createGraphics();
					g.drawImage(in, 0, 0, null);
					g.dispose();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
				/** here we ask if we got an image file */
				if (newImage != null) {
					/** then we can try to add it to the TextureManager under the players-prefix + fileName*/
					String userNameFileName = GameWindow.gw.getPlayer() + "_" + fileName;
					if (ClientTextureManager.manager.addTexture(userNameFileName, newImage)== true ) {
						/** local copy of image */
						GameWindow.gw.gameInfoConsole.appendInfo("Got the new texture: "+userNameFileName+" from local disk");
						try {
							MyUtil.copyFile(file, new File(GameStates.externalImagesPath+userNameFileName));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
					} else
					{
						System.out.println("Could not add the texture locally, because of existing filename!");
						
					}
					/** send space to server */ 
					/** get next Packet and send it to the server */			
					ClientTextureManager.manager.prepareTextureForUpload(userNameFileName);
					
		    		byte[] imagePacket = ClientTextureManager.manager.getTexturePacket(0);
		    		String textureName = ClientTextureManager.manager.getUploadTextureName();
		    		/** send the next packet */
		    		GameWindow.gw.gameInfoConsole.appendInfo("Sending texture "+userNameFileName+" to server");
		    		GameWindow.gw.send(ClientMessages.uploadTexture(textureName, 0, ClientTextureManager.manager.getNumberOfPacketsUploadTexture() , imagePacket.length, imagePacket, GameWindow.gw.getPlayer()));
		    		
				}
			}
			
		}
	}
	
}
