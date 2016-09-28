package me.smudja;

public interface Command {
	
	String getName();
	
	String getDescription();
	
	void handle(ClientConnection client, String[] args);

}
