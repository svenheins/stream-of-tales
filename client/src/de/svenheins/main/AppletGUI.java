package de.svenheins.main;

import javax.swing.JApplet;

import de.svenheins.handlers.MouseHandler;

public class AppletGUI extends JApplet {

	public static boolean running = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		running = true;
		AppletGUI ag = new AppletGUI();
		ag.init();
//		new GameWindow("Grafikengine", GameStates.getWidth(), GameStates.getHeight());
//		GameWindow.setSize(400,600);
	}

	 public void init()
     {
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());
 	    System.out.println("Applet initializing");
 	    // getContentPane().add(new GamePanel());
 	    new GameWindow("Grafikengine", GameStates.getWidth(), GameStates.getHeight());
     }
	 	        //start
	 public void start()
	 {
		 System.out.println("Applet starting");
	 }
	 //stop
	 public void stop()
	 {
		 System.out.println("Applet stopping");
	 }
	 //destroy
	 public void destroy()
	 {
		 System.out.println("Applet destroyed");
	 }
}
