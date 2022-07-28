package de.svenheins.managers;


//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.svenheins.main.GameStates;

import de.svenheins.objects.Sprite;

public class SpriteManager {
	public static SpriteManager manager = new SpriteManager();
	private HashMap<String, Sprite> map = new HashMap<String, Sprite>();
	
	public Sprite getSprite(String src) {
		if(map.get(src) != null) return map.get(src);
		else {
//			ImageIcon icon = new ImageIcon(getClass().getResource(GameStates.resourcePath+"images/"+src));
			BufferedImage img;
			try {
				img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
				Sprite s = new Sprite(img);
				map.put(src, s);
				return s;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
	}

}
