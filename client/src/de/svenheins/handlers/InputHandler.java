package de.svenheins.handlers;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class InputHandler implements KeyListener{

	private int k1, k2, k3, k4, k5, k6, k7;
	public boolean up, down, left, right, attack, pause, options;
	
	
	public InputHandler(int k1, int k2, int k3, int k4, int k5, int k6, int k7) {
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.k4 = k4;
		this.k5 = k5;
		this.k6 = k6;
		this.k7 = k7;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		if (key== k1) {
			left = true;
		}
		if (key== k2) {
			right = true;
		}
		if (key== k3) {
			up = true;
		}
		if (key== k4) {
			down = true;
		}
		if (key== k5) {
			attack = true;
		}
		if (key== k6) {
			pause = true;
		}
		if (key== k7) {
			options = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		if (key== k1) {
			left = false;
		}
		if (key== k2) {
			right = false;
		}
		if (key== k3) {
			up = false;
		}
		if (key== k4) {
			down = false;
		}
		if (key== k5) {
			attack = false;
		}
		if (key== k6) {
			pause = false;
		}
		if (key== k7) {
			options = false;
		}

		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	

}
