package de.svenheins.managers;

//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.svenheins.animation.Animation;

import de.svenheins.main.GameStates;

import de.svenheins.objects.Sprite;
import de.svenheins.objects.TileSet;

public class AnimationManager {
	public static AnimationManager manager = new AnimationManager();
	private HashMap<String, Animation> map = new HashMap<String, Animation>();
	
	public Animation getAnimation(String name, String[] src){
//		System.out.println(name);
		if(map.get(name) != null) return map.get(name);
		else {
			List<Sprite> spriteList = new ArrayList<Sprite>();
			for (int i=0; i<src.length; i++) {
//				ImageIcon icon = new ImageIcon(getClass().getResource(GameStates.resourcePath+"images/"+src[i]));
				BufferedImage img;
				try {
					img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
					spriteList.add(new Sprite(img));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
			Animation ani = new Animation(spriteList, 300);
			ani.start();
			map.put(name, ani);
			return ani;
		}
	}
	
	public Animation getAnimation(String src){
		if(map.get(src) != null) return map.get(src);
		else {
//			System.out.println(map.containsKey(src)+src);
//			ImageIcon icon = new ImageIcon(getClass().getResource(GameStates.resourcePath+"images/"+src));
			BufferedImage img;
			try {
				img = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
				Sprite s = new Sprite(img);
				Animation ani = new Animation(s);
//				String [] srcArray = {src};
				map.put(src, ani);
				return ani;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
				
			}
			
		}
	}
	
	/** construct an animation from TileSet */
	public Animation getAnimation(String name, TileSet tile, int begin, int end, long delay){
		String[] src = tile.getTileNames(begin, end);
		String idString = tile.getName()+ "_" + name;
		if(map.get(idString) != null) {
			return map.get(idString);
		}
		else {
			/** create new Animation */
			List<Sprite> spriteList = new ArrayList<Sprite>();
			for (int i=begin; i<=end; i++) {
				spriteList.add(new Sprite(tile.getTileImage(i)));
//				System.out.println("Added "+src[i]);
			}
			Animation ani = new Animation(spriteList, delay);
			ani.start();
			map.put(idString, ani);
			return ani;
		}
	}
	
	
}
