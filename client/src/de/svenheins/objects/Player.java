package de.svenheins.objects;
import de.svenheins.handlers.InputHandler;
import de.svenheins.main.GameStates;



public class Player {
	private String name;
	private InputHandler input;
	
	private long timebetweenAttacks = 500; //(GameStates.ani_attacking_d_end-GameStates.ani_attacking_d_start+2)*GameStates.animationDelay;
	private long lastAttack;
	
	public Player(String name, InputHandler input){
		this.setName(name);
		this.input = input;
	}
	
	public InputHandler getInputHandler(){
		return input;
	}

	public long getTimebetweenAttacks() {
		return timebetweenAttacks;
	}

	public void setTimebetweenAttacks(long timebetweenAttacks) {
		this.timebetweenAttacks = timebetweenAttacks;
	}

	public long getLastAttack() {
		return lastAttack;
	}

	public void setLastAttack(long lastAttack) {
		this.lastAttack = lastAttack;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
