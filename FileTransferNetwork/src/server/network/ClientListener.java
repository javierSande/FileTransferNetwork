/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import common.User;
import common.exceptions.MessageException;
import common.messages.*;
import server.Server;
import server.console.ServerConsole;

public class ClientListener extends Thread {
	
	private boolean active;
	
	private Server server;
	private User user;
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	public ClientListener(Server server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	/* private boolean startConnection() throws Exception
	 * 
	 * Creates the connection between every new client and the server.
	 * 
	 * It is called from the run() method of the thread, so it works as a receptor of the
	 * ConnectionMessage from a new connection.
	 * Once the message is received, a new user is created with all its data (contained in
	 * the message) and it is stored in the server so that the application can work with
	 * this client from now on.
	 */
	
	private boolean startConnection() throws Exception {
		Message m = (Message) in.readObject();
		
		if (m.type == MessageType.CONNECTION) {
			ConnectionMessage cm = (ConnectionMessage) m;
			user = new User(cm.getName(), socket, in, out, cm.getDataToShare());
			server.addUser(user);
			
			user.sendMessage(new ConfirmConnectionMessage(server.getIp(), user.getIp(), user));
		} else {
			throw new MessageException("Failed to connect with client");
		}
		return true;
	}
	
	public void endConnection() throws Exception {
		try {
			user.sendMessage(new TerminateMessage(server.getIp(), user.getIp()));
		} catch (Exception e) {
			ServerConsole.print("Failed to disconnect from server!");
			e.printStackTrace();
		}
	}
	
	public void update(Server s) {
		try {
			user.sendMessage(new ServerUpdateMessage(server.getIp(), user.getIp(), server.getUsers(), server.getFiles()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {	
		try {
			ServerConsole.print("Client requests connection");
			
			active = startConnection();
			
			server.addConnection(this);
			
			ServerConsole.print(String.format("Client %d connects to server", user.getId()));
			
			while (active) {
				try {
					Message m = (Message) in.readObject();
					switch(m.type) {
					case CLIENT_SERVER_READY:
						// Message used by the sender client of a request to tell us that it is available to send
						ClientServerReadyMessage crm = (ClientServerReadyMessage) m;
						
						server.sendMessageToUser(crm.getReceiver().getId(), new ServerClientReadyMessage(server.getIp(), crm.getReceiver().getIp(), user.getIp(), crm.getPort(), crm.getFile()));
						break;
						
					case CONFIRM_TERMINATE:
						// Confirmation of end of session received by the server from every client when the server
						// wants to end the connection (it previously telled all the users that the session is going
						// to finish and every client will confirm its closure through this message)
						server.removeConnection(this);
						server.removeUser(user);
						
						out.close();
						in.close();
						socket.close();
						
						ServerConsole.print(String.format("Client %d disconnects from server", user.getId()));
						
						active = false;
						break;
						
					case FILE_REQUEST:
						// Message received by the server from a client that wants to request a file
						String file = ((FileRequestMessage)m).getFile();
						ServerConsole.print(String.format("Client %d requests file %s", user.getId(), file));
						
						// Pick a random sender client from those clients that have available the requested file
						User sender = server.getSender(file);
						if (sender == null)
							// if such sender does not exist, send an error message to the user
							server.sendMessageToUser(user.getId(), new ErrorMessage(server.getIp(), user.getIp(), "File not available"));
						else
							// otherwise, send a request message to the randomly selected sender
							server.sendMessageToUser(sender.getId(), new SendRequestMessage(server.getIp(), sender.getIp(), user, file));
						break;
						
					case TERMINATE:
						// Message received by the server from a client that wants to close its connection. The difference
						// with CONFIRM_TERMINATE is that, in this case, it is an independent client who wants to close the
						// connection with the server.
						user.sendMessage(new ConfirmTerminateMessage(server.getIp(), user.getIp()));
						server.removeConnection(this);
						server.removeUser(user);
						
						ServerConsole.print(String.format("Client %d disconnects from server", user.getId()));
						active = false;
						break;
						
					case USER_LIST:
						// Request of the list of users connected from a client. This request is done the moment a client is
						// created, so it will be done by all clients.
						user.sendMessage(new ConfirmUserListMessage(server.getIp(), user.getIp(), server.getUsers(), server.getFiles()));
						break;
					
					case USER_UPDATE:
						// Message received by the server from a client that has done an update of its shared files list
						UserUpdateMessage um = (UserUpdateMessage) m;
						server.updateUser(um.getId(), um.getDataToShare());
						break;
						
					case ERROR:
						// Error message, mainly for unavailable files purposes
						ServerConsole.print(((ErrorMessage)m).getMessage());
						break;
						
					default:
						throw new MessageException("Invalid message");
					} 
				} catch (MessageException e){
					user.sendMessage(new ErrorMessage(server.getIp(), user.getIp(), e.getMessage()));
					System.err.println(e.getMessage());
					endConnection();
				}
			}		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			server.removeConnection(this);
			server.removeUser(user);
			e.printStackTrace();
		}
	}
}
