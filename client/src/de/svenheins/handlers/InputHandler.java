/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.handlers;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class InputHandler {

	private int keyLeft, keyRight, keyUp, keyDown, keyAttack, keyPause, keyOptions, keyInfoConsole, keyZoomIn, keyZoomOut, keyInventory, keyMessageWindow, keyLightOnOff;
	public boolean up, down, left, right, attack, pause, options, infoConsole, zoomIn, zoomOut, inventory, messageWindow, lightOnOff;
	
	
	public InputHandler(int keyLeft, int keyRight, int keyUp, int keyDown, int keyAttack, int keyPause, int keyOptions, int keyInfoConsole, int keyZoomIn, int keyZoomOut, int keyInventory, int keyMessageWindow, int keyLightOnOff) {
		this.keyLeft = keyLeft;
		this.keyRight = keyRight;
		this.keyUp = keyUp;
		this.keyDown = keyDown;
		this.keyAttack = keyAttack;
		this.keyPause = keyPause;
		this.keyOptions = keyOptions;
		this.keyInfoConsole = keyInfoConsole;
		this.keyZoomIn = keyZoomIn;
		this.keyZoomOut = keyZoomOut;
		this.keyInventory = keyInventory;
		this.keyMessageWindow = keyMessageWindow;
		this.keyLightOnOff = keyLightOnOff;
	}
	
	public void keyPressed(int key) {
		// TODO Auto-generated method stub
//		System.out.println("down");
//		int key = e.getKeyCode();
		if (key== keyLeft) {
			left = true;
		}
		if (key== keyRight) {
			right = true;
		}
		if (key== keyUp) {
			up = true;
		}
		if (key== keyDown) {
			down = true;
		}
		if (key== keyAttack) {
			attack = true;
		}
		if (key== keyPause) {
			pause = true;
		}
		if (key== keyOptions) {
			options = true;
		}
		if (key== keyInfoConsole) {
			infoConsole = true;
		}
		if (key== keyZoomIn) {
			zoomIn= true;
		}
		if (key== keyZoomOut) {
			zoomOut = true;
		}
		if (key== keyInventory) {
			inventory = true;
		}
		if (key== keyMessageWindow) {
			messageWindow = true;
		}
		if (key== keyLightOnOff) {
			lightOnOff = true;
		}
	}

	public void keyReleased(int key) {
		// TODO Auto-generated method stub
//		System.out.println("up");
//		int key = e.getKeyCode();
		if (key== keyLeft) {
			left = false;
		}
		if (key== keyRight) {
			right = false;
		}
		if (key== keyUp) {
			up = false;
		}
		if (key== keyDown) {
			down = false;
		}
		if (key== keyAttack) {
			attack = false;
		}
		if (key== keyPause) {
			pause = false;
		}
		if (key== keyOptions) {
			options = false;
		}
		if (key== keyInfoConsole) {
			infoConsole = false;
		}
		if (key == keyZoomIn) {
			zoomIn = false;
		}
		if (key == keyZoomOut) {
			zoomOut = false;
		}
		if (key == keyInventory) {
			inventory = false;
		}
		if (key == keyMessageWindow) {
			messageWindow = false;
		}
		if (key== keyLightOnOff) {
			lightOnOff = false;
		}
	}

	
	public void keyTyped(KeyEvent e) {

	}
	

}
