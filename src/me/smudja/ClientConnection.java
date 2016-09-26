package me.smudja;

import java.io.*;
import java.net.*;

public class ClientConnection implements Runnable {
	
	private Server server;
	private Thread thread;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public ClientConnection(Server server, Socket connection) {
		thread = new Thread(this, connection.getInetAddress().getHostName());
		this.server = server;
		this.connection = connection;
		server.log("\n Connection to " + connection.getInetAddress().getHostAddress() + " established ");
		thread.start();		
	}

	public void run() {
		try {
			setupStreams();
			chat();
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
		finally {
			cleanup();
		}
	}
	
	public String getName() {
		return thread.getName();
	}

	private void setupStreams() throws IOException {
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			server.log("\n Data streams established ");
	}
	

	private void chat() throws IOException {
		String msg = "\n You are now connected to " + thread.getName();
		server.log(msg);
		server.sendToAll("\n " + thread.getName() + " has joined.");
		
		do {
			// have conversation
			try {
				msg = (String) input.readObject();
				server.sendToAll("\n [" + thread.getName() + "] " + msg);
			} catch (ClassNotFoundException exc) {
				server.log("\n ERROR: Client send invalid message! ");
			}
		} while (!msg.equals("END"));	
	}
	
	public void sendMessage(String msg) {
		try {
			output.writeObject(msg);
			output.flush();
		} catch (IOException exc) {
			server.log("\n ERROR: Unable to send message to " + thread.getName());
		}
	}
	

	private void cleanup() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		finally {
			server.removeClient(this);
		}
	}

}
