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
	
	public Server getServer() {
		return server;
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
				if(msg.substring(0, 1).compareTo("/") == 0) {
					int splitIndex = msg.indexOf(' ');
					String cmdName;
					String[] args;
					if(splitIndex == -1) {
						cmdName = msg.substring(1);
						args = new String[]{};
					}
					else {
						cmdName = msg.substring(1, splitIndex);
						args = msg.substring(splitIndex).split("\\s+");
					}
					for(Command cmd : CommandManager.INSTANCE.getCommands()) {
						if(cmd.getName().compareTo(cmdName) == 0) {
							cmd.handle(this, args);
						}
					}
				}
				else {
					server.sendToAll("\n [" + thread.getName() + "] " + msg);
				}
			} catch (ClassNotFoundException cnfExc) {
				server.log("\n ERROR: Client send invalid message! ");
			} catch(EOFException eofExc) {
				server.log("\n " + getName() + " closed the connection");
				break;
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
