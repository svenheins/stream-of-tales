/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.threads;

import javax.swing.JComponent;

import de.svenheins.main.GUI;


public class GraphicThread implements Runnable{

	private JComponent comp;
	private long duration, last; 
	private long millis, frames;


	
	public GraphicThread(JComponent comp) {
		// TODO Auto-generated constructor stub
		this.comp = comp;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (GUI.running) {
			comp.repaint();
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}
	
	

}
