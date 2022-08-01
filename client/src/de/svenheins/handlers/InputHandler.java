package de.svenheins.handlers;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class InputHandler implements KeyListener{

	private int keyLeft, keyRight, keyUp, keyDown, keyAttack, keyPause, keyOptions, keyInfoConsole, keyZoomIn, keyZoomOut, keyInventory;
	public boolean up, down, left, right, attack, pause, options, infoConsole, zoomIn, zoomOut, inventory;
	
	
	public InputHandler(int keyLeft, int keyRight, int keyUp, int keyDown, int keyAttack, int keyPause, int keyOptions, int keyInfoConsole, int keyZoomIn, int keyZoomOut, int keyInventory) {
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
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println("pressed");
		int key = e.getKeyCode();
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
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
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
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	

}
