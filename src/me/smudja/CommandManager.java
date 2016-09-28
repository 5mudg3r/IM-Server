package me.smudja;

import java.util.ArrayList;

public enum CommandManager {
	
	INSTANCE;
	
	private ArrayList<Command> commands = new ArrayList<Command>();
	
	public ArrayList<Command> getCommands() {
		return commands;
	}
	
	public void add(Command cmd) {
		if(!commands.contains(cmd)) {
			commands.add(cmd);
		}
	}
}
