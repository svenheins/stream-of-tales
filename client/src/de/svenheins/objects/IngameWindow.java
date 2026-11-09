/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.awt.Polygon;
import java.util.ArrayList;

import de.svenheins.main.GameWindow;

public class IngameWindow extends Space{

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;

	public IngameWindow() {
		super();
		
		Polygon polyWindow = new Polygon(new int[]{20,20, GameWindow.gw.width-20, GameWindow.gw.width-20}, new int[]{20,GameWindow.gw.height-40, GameWindow.gw.height-40, 20}, 4);
		ArrayList<Polygon> polyList = new ArrayList<Polygon>();
		polyList.add(polyWindow);
		this.setPolygon(polyList);
		this.setRGB(new int[]{120, 120, 120});
		this.setTrans(0.75f);
	}
}
