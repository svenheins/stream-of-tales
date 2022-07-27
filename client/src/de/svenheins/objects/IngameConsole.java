package de.svenheins.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

import de.svenheins.main.GameWindow;

public class IngameConsole extends Space {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private StringBuffer consoleText;
	private boolean deleteable;
	
	public IngameConsole() {
		super();
		Polygon polyWindow = new Polygon(new int[]{20,20, GameWindow.gw.breite-40, GameWindow.gw.breite-40}, new int[]{20,GameWindow.gw.hoehe-40, GameWindow.gw.hoehe-40, 20}, 4);
		ArrayList<Polygon> polyList = new ArrayList<Polygon>();
		polyList.add(polyWindow);
		this.setPolygon(polyList);
		this.setRGB(new int[]{120, 120, 220});
		this.setTrans(0.75f);
		
		// init consoleText (tabulator is not shown, and this bugfixes the firstline-bug
		consoleText = new StringBuffer("");
		this.update();
	}
	
	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		g.setPaintMode();
		g.setColor(new Color(250, 250, 250));
		g.setFont(new Font("Arial", Font.PLAIN , 20));
		drawConsoleText(g, 30, 30);
	}
	
	public StringBuffer getConsoleText() {
		return this.consoleText;
	}
	
	/**
	 * Update the console, and delete to big content (greater than 28 lines)
	 */
	public void update() {
		String text = this.consoleText.toString();
	    StringBuffer temp = this.consoleText;
	    int count = text.split("\n").length;
	    int enters = this.getEnters();
		count += enters;
		
		// Handle the empty StringBuffer-Bug
		// TODO
		// Allow deletion only if there are other chars than \n on the last position
		if (enters >0) setDeleteable(false);
			else setDeleteable(true);
		int firstEnter = -1;
		if (count > 28) {
			firstEnter = text.indexOf("\n");
			// delete the first line
			if (enters == 28) temp = temp.replace(0, firstEnter+1, "\n");
			else temp = temp.delete(0, firstEnter+1);		
		}
		// After all update the StringBuffer consoleText
		this.consoleText = temp;
	}
	
	/**
	 * @return the number of "\n"s (Enters) inside the console
	 */
	public int getEnters() {
		String text = this.consoleText.toString();
		int enters = 0;
		if (text.endsWith("\n")) {
			while(text.endsWith("\n")) {
				enters += 1;
				text = text.substring(0, text.lastIndexOf("\n"));
			}
		} 
		return enters;
	}
	
	/**
	 * @param g Graphic2D-Object that paints the content
	 * @param textBuffer: this should be the consoleText
	 * @param x
	 * @param y
	 */
	private void drawConsoleText(Graphics2D g, int x, int y) {
	    String text = this.getConsoleText().toString();
		int count = text.split("\n").length;
		int enters = this.getEnters();
		count += enters;

		int j = 0;
	    for (String line : this.getConsoleText().toString().split("\n")) {
	    	j++;
	    	// if the string ends with some new lines
	    	if(enters >0) {
	    		g.drawString(line, x, y += g.getFontMetrics().getHeight());
	    		// only when we reach the last line do the following
	    		if (j == count-enters && System.currentTimeMillis() % 1000 >500) {
	    			// draw cursor on the actual line
	    			g.drawString("|", x, y+= (enters)*g.getFontMetrics().getHeight());
	    		}
	    	} else {
	    		// -> if enters == 0
	    		if(j== count && System.currentTimeMillis() % 1000 >500) g.drawString(line+"|", x, y+=g.getFontMetrics().getHeight());
				else g.drawString(line, x, y += g.getFontMetrics().getHeight());
	    	}
		}
	    // Handle empty "Enter-Screen" below 28 rows
	    if (enters == count && System.currentTimeMillis() % 1000 >500) {
	    	g.drawString("|", x, y+= (enters)*g.getFontMetrics().getHeight());
	    }
	}
	
	public void append(String str){
		this.consoleText.append(str);
	}
	
	public void append(char c){
		this.consoleText.append(c);
	}
	
	public void deleteLast(){
			if (this.consoleText.length()>1) {
				this.consoleText.deleteCharAt(this.consoleText.length()-1);
			} else
				this.consoleText = new StringBuffer("");
	}

	public boolean isDeleteable() {
		return deleteable;
	}

	public void setDeleteable(boolean deleteable) {
		this.deleteable = deleteable;
	}


}
