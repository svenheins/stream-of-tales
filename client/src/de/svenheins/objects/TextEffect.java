/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.math.BigInteger;

import de.svenheins.main.GameStates;
import de.svenheins.managers.TextEffectManager;

public class TextEffect extends LocalObject {
	private float transparency;
	private long fadeInStart;
	private long fadeInEnd;
	private long fadeOutStart;
	private long fadeOutEnd;
	private boolean visible;
	private String text;
	
	public TextEffect(BigInteger id, String text, float x, float y, long fade, long stayLength) {
		super();
		this.setId(id);
		this.setText(text);
		this.setX(x);
		this.setY(y);
		this.fadeInStart = System.currentTimeMillis();
		this.fadeInEnd = fadeInStart + fade;
		this.fadeOutStart = fadeInEnd + stayLength;
		this.fadeOutEnd = fadeOutStart + fade;
		this.setVisible(true);
		this.setTransparency(0.0f);
	}
	
	public TextEffect(String text) {
		super();
		long fade = GameStates.standardEffectTextFade;
		long stayLength = GameStates.standardEffectTextStay;
		this.setId(TextEffectManager.maxID.add(BigInteger.valueOf(1)));
		this.setText(text);
		this.setX(0);
		this.setY(0);
		this.fadeInStart = System.currentTimeMillis();
		this.fadeInEnd = fadeInStart + fade;
		this.fadeOutStart = fadeInEnd + stayLength;
		this.fadeOutEnd = fadeOutStart + fade;
		this.setVisible(true);
		this.setTransparency(0.0f);
	}
	
	public void paint(Graphics2D g, float x, float y) {
		g.setColor(new Color(250, 250, 250));
		g.setFont(new Font(GameStates.standardFont, Font.PLAIN , GameStates.standardEffectTextSize));

		// a composite with transparency.
	    Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.getTransparency());
	    g.setComposite(c);
		FontMetrics fm2 = g.getFontMetrics();
		int nameWidth = fm2.stringWidth(this.getText());
		
		g.drawString(this.getText(), x-nameWidth/2 + this.getX(), y+this.getY());
	}
	
	/** update the transparency depending on time */
	public void update() {
		long timeNow = System.currentTimeMillis();
		if (timeNow >fadeOutEnd) {
			this.setVisible(false);
			this.setTransparency(0.0f);
		} else if (timeNow > fadeOutStart) {
			this.setVisible(true);
			this.setTransparency(((float)((fadeOutEnd-fadeOutStart)-(timeNow-this.fadeOutStart))/(fadeOutEnd-fadeOutStart)));
		} else if (timeNow > fadeInEnd) {
			this.setVisible(true);
			this.setTransparency(1.0f);
		} else if (timeNow > fadeInStart) {
			this.setVisible(true);
			this.setTransparency(((float)(timeNow-this.fadeInStart)/(fadeInEnd-fadeInStart)));
		} else {
			this.setVisible(true);
			this.setTransparency(0.0f);
		}
	}
	
	public float getTransparency() {
		return transparency;
	}
	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}
	public long getFadeInStart() {
		return fadeInStart;
	}
	public void setFadeInStart(long fadeInStart) {
		this.fadeInStart = fadeInStart;
	}
	public long getFadeInEnd() {
		return fadeInEnd;
	}
	public void setFadeInEnd(long fadeInEnd) {
		this.fadeInEnd = fadeInEnd;
	}
	public long getFadeOutStart() {
		return fadeOutStart;
	}
	public void setFadeOutStart(long fadeOutStart) {
		this.fadeOutStart = fadeOutStart;
	}
	public long getFadeOutEnd() {
		return fadeOutEnd;
	}
	public void setFadeOutEnd(long fadeOutEnd) {
		this.fadeOutEnd = fadeOutEnd;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
