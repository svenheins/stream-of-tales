package de.svenheins.threads;

import javax.swing.JComponent;

import de.svenheins.main.GUI;
import de.svenheins.main.GamePanel;


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
			duration = System.currentTimeMillis() - last;
			last = System.currentTimeMillis();
			millis += duration;
			frames +=1;
			if (millis >1000 && GamePanel.gp.showStats ==true) {
				System.out.println("Graphic-Frames: "+ frames);
				frames = 0;
				millis =0;
			}
			comp.repaint();
			try {
				Thread.sleep(5);
			}
			catch(InterruptedException exception) {
				System.out.println(exception);
			}
		}
	}
	
	

}
