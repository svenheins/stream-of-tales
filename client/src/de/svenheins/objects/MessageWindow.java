/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import de.svenheins.main.GameStates;
import de.svenheins.managers.StreamViewerManager;

public class MessageWindow extends IngameConsole {
	public static int maxCharsPerLine = 50;
	private String sender = "";
	private int headerSize;
	private int maxLineSize;
	
	public MessageWindow(Point position, int width, int height, int[] color,
			float trans, int fontSize) {
		super(position, width, height, color, trans, false, false, fontSize);
		this.setDistanceTextY(30);
		this.setHeaderSize(16);
	}
	
	/** paint-method */
	@Override
	public void paint(Graphics2D g, int x, int y) {
		super.paint(g, x, y);
		g.setPaintMode();
		StreamViewer streamViewer = StreamViewerManager.get(this.getSender());
		if (streamViewer != null) {
			g.setColor(new Color(streamViewer.getRed(), streamViewer.getGreen(), streamViewer.getBlue()));
		} else g.setColor(new Color(250, 250, 250));
		g.setFont(new Font(GameStates.standardFont, Font.PLAIN , this.getHeaderSize()));
		g.drawString(this.getSender(), this.getPosition().x+this.getDistanceTextX(), this.getPosition().y + g.getFontMetrics().getHeight());
	}


	/** format the message for the window */
	public void writeMessageToWindow(Graphics2D g, String sender, String message) {
		this.setSender(sender);
		String convertedMessage ="";
		if (message.length() > maxCharsPerLine) {
			String[] messageWord = message.split(" ");
			int actualChars = 0;
			for (int i = 0; i < messageWord.length; i++) {
				if (i == 0) actualChars += messageWord[i].length();
				else actualChars += 1+ messageWord[i].length();
				if (actualChars > maxCharsPerLine) {
					if (messageWord[i].length() > maxCharsPerLine) {
						String partOfMessageWord = messageWord[i];
						if (i==0) ;
						else convertedMessage = convertedMessage + "\n";
						int partCount = (int) Math.ceil((double)messageWord[i].length() / (double)maxCharsPerLine);
						String addString;
						for (int j = 0; j < partCount; j++) {
							addString = partOfMessageWord.substring(0, Math.min(maxCharsPerLine, partOfMessageWord.length()));
							if (addString.length() < maxCharsPerLine) {
								convertedMessage = convertedMessage + addString;
								actualChars = addString.length();
							} else {
								convertedMessage = convertedMessage + addString + "\n";
								partOfMessageWord = partOfMessageWord.substring(maxCharsPerLine);
								actualChars = 0;
							}	
						}
					} else {
						convertedMessage = convertedMessage + "\n" + messageWord[i];
						actualChars = messageWord[i].length();
					}
				} else {
					/** OK, append the word */
					if (i == 0) convertedMessage = convertedMessage+messageWord[i];
					else convertedMessage = convertedMessage + " " +messageWord[i];
				}
				
				
			}
			this.setConsoleText(convertedMessage);
		} else {
			this.setConsoleText(message);
			convertedMessage = message;
		}
		
		int maxWidth = 30;
		int maxHeight = this.getDistanceTextY();
		g.setFont(new Font(GameStates.standardFont, Font.PLAIN , this.getHeaderSize()));
		FontMetrics fm = g.getFontMetrics();
		int headerSize = fm.stringWidth(sender);
		maxHeight += fm.getHeight();
		
		if (headerSize> maxWidth) maxWidth = headerSize;
		
		g.setPaintMode();
		g.setFont(new Font(GameStates.standardFont, Font.PLAIN , this.fontSize));
		FontMetrics fm2 = g.getFontMetrics();
		String[] lines = convertedMessage.split("\n");
		for (int i = 0; i < lines.length; i++) {
			int lineWidth = fm2.stringWidth(lines[i]);
			if (lineWidth > maxWidth) maxWidth = lineWidth;
			maxHeight += fm2.getHeight();
			System.out.println("lines[i] = " + lines[i]);
		}
		
		int newWidth = 2*this.getDistanceTextX() + maxWidth;
		int newHeight = maxHeight;
		this.getPolygon().get(0).xpoints[0] = GameStates.getWidth()/2 - newWidth/2;
		this.getPolygon().get(0).xpoints[1] = this.getPolygon().get(0).xpoints[0];
		this.getPolygon().get(0).ypoints[0] = GameStates.getHeight()/2-3*(newHeight/2);
		this.getPolygon().get(0).ypoints[3] = this.getPolygon().get(0).ypoints[0];
		
		this.getPolygon().get(0).xpoints[2] = this.getPolygon().get(0).xpoints[0] + newWidth;
		this.getPolygon().get(0).xpoints[3] = this.getPolygon().get(0).xpoints[2];
		this.getPolygon().get(0).ypoints[1] = this.getPolygon().get(0).ypoints[0] + maxHeight;
		this.getPolygon().get(0).ypoints[2] = this.getPolygon().get(0).ypoints[1];
		
		this.setPosition(new Point(this.getPolygon().get(0).xpoints[0], this.getPolygon().get(0).ypoints[0]));
		
	}


	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public int getMaxLineSize() {
		return maxLineSize;
	}

	public void setMaxLineSize(int maxLineSize) {
		this.maxLineSize = maxLineSize;
	}

}
