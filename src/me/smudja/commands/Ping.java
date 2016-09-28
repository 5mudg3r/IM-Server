package me.smudja.commands;

import me.smudja.ClientConnection;
import me.smudja.Command;

public class Ping implements Command {
	
	private String name = "ping";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void handle(ClientConnection client, String[] args) {
		client.sendMessage("\n Pong!");
	}

}
