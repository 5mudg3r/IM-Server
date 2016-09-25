package me.smudja;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {

	private JTextField userText; // text input field for messages
	private JTextArea chatDisplay; // message display
	private ObjectOutputStream output; // data output stream to send messages
										// away
	private ObjectInputStream input; // data input stream to receive messages
	private ServerSocket server; // server socket
	private Socket connection; // socket (basically the connection)

	// constructor
	public Server() {
		super("IM Server"); // title of window
		userText = new JTextField();
		userText.setEditable(false); // initialise to be uneditable, we will
										// make it editable later
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand()); // when enter is pressed,
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

	// set up and run server
	public void startServer() {
		try {
			server = new ServerSocket(6789, 100); // 1st number is port number,
													// 2nd is max number of
													// people waiting at port
													// (queue)
			while (true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException exc) {
					showMessage("\n Server ended the connection... ");
				} finally {
					cleanup();
				}
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	// wait for connection, then display connection info
	private void waitForConnection() throws IOException {
		showMessage(" Waiting for client connection... \n");
		connection = server.accept(); // this waits for a connection request
										// then accepts it and creates the
										// connection
		showMessage(" Connection with " + connection.getInetAddress().getHostName() + " established \n");
	}

	// create streams to send and receive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Data streams established \n");
	}

	// during the chat conversation
	private void whileChatting() throws IOException {
		String msg = " You are now connected ";
		sendMessage(msg);
		ableToType(true);

		do {
			// have conversation
			try {
				msg = (String) input.readObject();
				showMessage("\n" + msg);
			} catch (ClassNotFoundException exc) {
				showMessage("\n ERROR: Client send invalid message! ");
			}
		} while (!msg.equals("CLIENT - END"));
	}

	// close streams and sockets, end connection
	private void cleanup() {
		showMessage("\n Closing connection... ");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	// send message to client
	private void sendMessage(String msg) {
		try {
			output.writeObject("SERVER - " + msg);
			output.flush();
			showMessage("\nSERVER - " + msg);
		} catch (IOException exc) {
			showMessage("\n ERROR: Unable to send message ");
		}
	}

	// updates chat display with new message
	private void showMessage(final String msg) { // final so that it can be seen
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
}
