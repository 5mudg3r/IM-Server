package me.smudja;

public interface Command {
	
	String getName();
	
	void handle(ClientConnection client, String[] args);

}
