package de.svenheins.objects;

import java.awt.Color;
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
		
		Polygon polyWindow = new Polygon(new int[]{20,20, GameWindow.gw.breite-20, GameWindow.gw.breite-20}, new int[]{20,GameWindow.gw.hoehe-40, GameWindow.gw.hoehe-40, 20}, 4);
		ArrayList<Polygon> polyList = new ArrayList<Polygon>();
		polyList.add(polyWindow);
		this.setPolygon(polyList);
		this.setRGB(new int[]{120, 120, 120});
		this.setTrans(0.75f);
	}
}
