/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.svenheins.main.GameWindow;
import de.svenheins.main.SoundEffect;
import de.svenheins.objects.TextEffect;

public class TextEffectManager {
	public static HashMap<BigInteger, TextEffect> textEffectList = new HashMap<BigInteger, TextEffect>();
	public static List<BigInteger> idList = new ArrayList<BigInteger>();
	public static BigInteger maxID = BigInteger.valueOf(0);
	
	
	public static void remove(BigInteger index) throws IllegalArgumentException {
		try {
			textEffectList.remove(index);
			idList.remove(index);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}

	
	public static boolean add(TextEffect textEffect) {
		if (textEffectList.containsKey(textEffect.getId())) {
			return false;
		} else {
			textEffectList.put(textEffect.getId(), textEffect);
			idList.add(textEffect.getId());
//			System.out.println("added textEffect");
			GameWindow.gw.sound.addStartingSoundEffect(SoundEffect.BIG_SWOOSH);
			if (maxID.compareTo(textEffect.getId())< 0) {
				maxID = textEffect.getId();
			}
			return true;
		}
	}
	
	public static TextEffect get(BigInteger index){
		try {
			return (TextEffect) textEffectList.get(index);
//			return (Entity) entityList.values().toArray()[index];
		}
		catch(IndexOutOfBoundsException e){
			//System.out.println("Null-Object returned");
			return null;
		}
		
	}
	
	public static boolean overwrite(TextEffect textEffect){
		if (!textEffectList.containsKey(textEffect.getId())) {
			/** do nothing if key is not yet set*/
			return false;
		} else {
			textEffectList.put(textEffect.getId(), textEffect);
			idList.add(textEffect.getId());
			return true;
		}
	}
	
	
	public static int size(){
		return textEffectList.size();
	}

	
	public static boolean contains(TextEffect textEffect) {
		return textEffectList.containsValue(textEffect);
	}
	

	/** whole update procedure */
	public static void update() {
		ArrayList<BigInteger> deleteList = new ArrayList<BigInteger>();
		for (BigInteger id : textEffectList.keySet()) {
			textEffectList.get(id).update();
			if (!textEffectList.get(id).isVisible()) deleteList.add(id);
		}
		
		for (BigInteger id : deleteList) {
			remove(id);
		}
	}

	/** update the Entity with the ID objectId */
	public static void updateTextEffect(BigInteger objectId) {
		TextEffect textEffect = textEffectList.get(objectId);
		if (textEffect != null) {
			textEffect.update();
		}

	}
}
