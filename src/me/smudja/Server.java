package me.smudja;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import me.smudja.commands.*;

public class Server extends JFrame {

	private String name;
	private ArrayList<ClientConnection> connections;
	private JTextField userText; // text input field for messages
	private JTextArea chatDisplay; // message display
	private ServerSocket server; // server socket

	// constructor
	public Server(String name) {
		super("IM Server - " + name); // title of window
		this.name = name;
		registerCommands();
		connections = new ArrayList<ClientConnection>();
		userText = new JTextField();
		userText.setEditable(false); // initialise to be uneditable, we will
										// make it editable later
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendToAll("\n [Server] " + event.getActionCommand()); // when enter is pressed,
														// send message in text
														// field
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);

		chatDisplay = new JTextArea();
		chatDisplay.setEditable(false);
		add(new JScrollPane(chatDisplay));

		setSize(450, 300);
		setVisible(true);
	}
	
	public ServerSocket getSocket() {
		return this.server;
	}

	// set up and run server
	public void startServer() {
		try {
			server = new ServerSocket(6789, 100); // 1st number is port number,
													// 2nd is max number of
													// people waiting at port
													// (queue)
			while (true) {
				waitForConnection();
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	// wait for connection, then display connection info
	private void waitForConnection() throws IOException {
		log("\n Waiting for client connection... ");
		connections.add(new ClientConnection(this, server.accept())); // this waits for a connection request
										// then accepts it and creates the
										// connection
		ableToType(true);
	}

	// send message to client
	public synchronized void sendToAll(String msg) {
		log(msg);
		for(ClientConnection client : connections) {
			client.sendMessage(msg);
		}
	}

	// updates chat display with new message
	public synchronized void log(final String msg) { // final so that it can be seen
													// by runnable??
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatDisplay.append(msg);
			}
		});
	}

	// changes whether the user (server) is able to type into the userText text
	// field
	private void ableToType(final boolean state) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(state);
			}
		});
	}
	
	public synchronized void removeClient(ClientConnection client) {
		connections.remove(client);
		if(connections.isEmpty()) {
			ableToType(false);
		}
		sendToAll("\n " + client.getName() + " has disconnected.");	
	}
	
	private void registerCommands() {
		CommandManager.INSTANCE.add(new Ping());
		CommandManager.INSTANCE.add(new Help());
	}
}
