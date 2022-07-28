package de.svenheins.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.sun.sgs.client.ClientChannel;

import de.svenheins.main.GamePanel;
import de.svenheins.main.GameStates;
import de.svenheins.main.GameWindow;

public class IngameConsole extends Space {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private StringBuffer consoleText;
	private boolean deleteable, writable, sendToServer, lastLineSend;
	private int maxEnters;
	private Point position;
	private int height, width;
	private int fontSize;
	
	public IngameConsole(Point position, int width, int height, int[] color, float trans, boolean writable, boolean sendToServer, int fontSize) {
		super();
		Polygon polyWindow = new Polygon(new int[]{position.x,position.x, position.x+width, position.x+width}, new int[]{position.y,position.y+height, position.y+height, position.y}, 4);
		ArrayList<Polygon> polyList = new ArrayList<Polygon>();
		polyList.add(polyWindow);
		this.setPolygon(polyList);
		this.setRGB(color);
		this.setTrans(trans);
//		float enterScale = 32.0f* (20.0f/(float)fontSize);
//		this.maxEnters =(int) (((float)GameStates.getHeight()/768)*enterScale);
		this.maxEnters = 8;
		this.position = position;
		this.height = height;
		this.width = width;
		this.writable = writable;
		if (!writable) this.maxEnters += 1;
		this.fontSize = fontSize;
		this.sendToServer = sendToServer;
		lastLineSend = false;
		// init consoleText (tabulator is not shown, and this bugfixes the firstline-bug
		consoleText = new StringBuffer("");
		this.update();
	}
	
	@Override
	public void paint(Graphics2D g, int x, int y) {
//		this.setPolygon(getPolygon().get(0).translate(arg0, arg1))
		super.paint(g, x, y);
		g.setPaintMode();
		g.setColor(new Color(250, 250, 250));
		g.setFont(new Font("Arial", Font.PLAIN , fontSize));
//		drawConsoleText(g, (int)((this.position.x+10)/GamePanel.gp.getZoomFactor()), (int)((this.position.y+10)/GamePanel.gp.getZoomFactor()));
		drawConsoleText(g, this.position.x+10, this.position.y+10);
	}
	
	public StringBuffer getConsoleText() {
		return this.consoleText;
	}
	
	/**
	 * Update the console, and delete too big content (i.e. greater than 28 lines)
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
		if (enters >0) {
			setDeleteable(false);
			// send the chat message once if there is exactly 1 new line
			if (enters == 1 && sendToServer== true && lastLineSend == false) {
				lastLineSend = true;
				ClientChannel channel = GameWindow.gw.getChannelByName("GLOBAL CHAT");
	            try {
	            	ByteBuffer lastLineAsBytes = this.getLastLineAsBytes();
	            	if (lastLineAsBytes != null) {
	            		channel.send(lastLineAsBytes);
	            	}
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
			}
		} else {
			// here we got some chars at the last position
			setDeleteable(true);
			lastLineSend = false;
		}
		int firstEnter = -1;
		if (count > maxEnters) {
			firstEnter = text.indexOf("\n");
			// delete the first line
			if (enters == maxEnters) temp = temp.replace(0, firstEnter+1, "\n");
			else temp = temp.delete(0, firstEnter+1);		
		}
		// After all update the StringBuffer consoleText
		this.consoleText = temp;
	}
	
	private ByteBuffer getLastLineAsBytes() {
		String text = this.consoleText.toString();
//	    StringBuffer temp = this.consoleText;
		int enters = this.getEnters();
		if (enters == 1 && text.split("\n").length > 0) {
			// last line of the text + the players prefix
			String lastLine = GameWindow.gw.getPlayer() +": " + text.split("\n")[text.split("\n").length-1];
			try {
	            return ByteBuffer.wrap(lastLine.getBytes(GameStates.MESSAGE_CHARSET));
	        } catch (UnsupportedEncodingException e) {
	            throw new Error("Required character set " + GameStates.MESSAGE_CHARSET +
	                " not found", e);
	        }
		} else return null;
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
	 * @return the number of "\n"s (Enters) inside the console
	 */
	public int getActualCharCount() {
		String text = this.consoleText.toString();
		int charCount;
		if (text.contains("\n")) {
			text = text.substring(text.lastIndexOf("\n"), text.length()-1);
		}
		charCount = text.length();
//		System.out.println("charcount: "+charCount);
		
		return charCount;
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
	    		if (j == count-enters && System.currentTimeMillis() % 1000 >500 && writable) {
	    			// draw cursor on the actual line
	    			g.drawString("|", x, y+= (enters)*g.getFontMetrics().getHeight());
	    		}
	    	} else {
	    		// -> if enters == 0
	    		if(j== count && System.currentTimeMillis() % 1000 >500 && writable) g.drawString(line+"|", x, y+=g.getFontMetrics().getHeight());
				else g.drawString(line, x, y += g.getFontMetrics().getHeight());
	    	}
		}
	    // Handle empty "Enter-Screen" below 28 rows
	    if (enters == count && System.currentTimeMillis() % 1000 >500 && writable) {
	    	g.drawString("|", x, y+= (enters)*g.getFontMetrics().getHeight());
	    }
	}
	
	public void append(String str){
		this.consoleText.append(str);
	}
	
	public void appendLine(String str){
		this.consoleText.append(str+"\n");
	}
	
	public void appendInfo(String str){
		Date date = new Date();
		this.consoleText.append(date+": "+str+"\n");
		this.update();
	}
	
	public void appendSimpleDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat();
	    sdf.applyPattern( "HH:mm:ss  " );
	    System.out.println(  ); // Donnerstag, 14. Juli 2005 um 06:36:10
	    this.consoleText.append(sdf.format(new Date()) + str+"\n");
		this.update();
	}
	
	public void append(char c){
		this.consoleText.append(c);
	}
	
	public void deleteLast(){
			if (this.consoleText.length()>1) {
				this.consoleText.deleteCharAt(this.consoleText.length()-1);
			} else {
				this.consoleText = new StringBuffer("");
			}
	}

	public boolean isDeleteable() {
		return deleteable;
	}

	public void setDeleteable(boolean deleteable) {
		this.deleteable = deleteable;
	}
	
	public boolean getLastLineSend() {
		return lastLineSend;
	}
	
	public void setLastLineSend(boolean b) {
		this.lastLineSend = b;
	}


}
