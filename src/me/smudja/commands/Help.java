package me.smudja.commands;

import me.smudja.ClientConnection;
import me.smudja.Command;
import me.smudja.CommandManager;

public class Help implements Command {
	
	private final String name = "help";
	
	private final String desc = "Displays all available commands and their descriptions";

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public void handle(ClientConnection client, String[] args) {
		for(Command cmd : CommandManager.INSTANCE.getCommands()) {
			client.sendMessage("\n /" + cmd.getName() + " - " + cmd.getDescription());
		}
	}

}
