package me.smudja.commands;

import java.util.ArrayList;

import me.smudja.ClientConnection;
import me.smudja.Command;

public class List implements Command {
	
	public String name = "list";
	
	public String desc = "Lists all clients connected to the chat server.";

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
		ArrayList<ClientConnection> connections = client.getServer().getConnections();
		if(connections.size() <= 1) {
			client.sendMessage("\n You are the only person online.");
		}
		else {
			client.sendMessage("\n There are " + connections.size() + " people online: ");
			for(ClientConnection connection : client.getServer().getConnections()) {
				client.sendMessage("\n - " + connection.getName());
			}
		}
		
	}

}
