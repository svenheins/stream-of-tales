package de.svenheins.managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.svenheins.main.GameStates;


public class TextureManager {
	public static TextureManager manager = new TextureManager();
	private HashMap<String, BufferedImage> map = new HashMap<String, BufferedImage>();
	
	public BufferedImage getTexture(String src) {
		if(map.get(src) != null) return map.get(src);
		else {
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
				map.put(src, bufferedImage);
				return bufferedImage;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
	}
}
