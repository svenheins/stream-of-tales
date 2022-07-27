package de.svenheins.managers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

import de.svenheins.animation.Animation;

import de.svenheins.main.GameStates;

import de.svenheins.objects.Sprite;

public class AnimationManager {
	public static AnimationManager manager = new AnimationManager();
	private HashMap<String[], Animation> map = new HashMap<String[], Animation>();
	
	public Animation getAnimation(String[] src){
		if(map.get(src) != null) return map.get(src);
		else {
			List<Sprite> spriteList = new ArrayList<Sprite>();
			for (int i=0; i<src.length; i++) {
				ImageIcon icon = new ImageIcon(getClass().getResource(GameStates.resourcePath+"images/"+src[i]));
				Image img = icon.getImage();
				spriteList.add(new Sprite(img));
			}
			Animation ani = new Animation(spriteList, 300);
			ani.start();
			map.put(src, ani);
			return ani;
		}
	}
	
	public Animation getAnimation(String src){
		if(map.get(src) != null) return map.get(src);
		else {
			ImageIcon icon = new ImageIcon(getClass().getResource(GameStates.resourcePath+"images/"+src));
			Image img = icon.getImage();
			Sprite s = new Sprite(img);
			Animation ani = new Animation(s);
			String [] srcArray = {src};
			map.put(srcArray, ani);
			return ani;
		}
	}
}
