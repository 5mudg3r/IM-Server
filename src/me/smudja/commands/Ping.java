package me.smudja.commands;

import me.smudja.ClientConnection;
import me.smudja.Command;

public class Ping implements Command {
	
	private final String name = "ping";
	
	private final String desc = "Replies with 'Pong!'";

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
		client.sendMessage("\n Pong!");
	}

}
