package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;

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
	
	public boolean isActive() {
		return active;
	}
	
	private boolean startConnection() throws Exception {
		Message m = (Message) in.readObject();
		
		if (m.type == MessageType.CONNECTION) {
			ConnectionMessage cm = (ConnectionMessage) m;
			user = new User(cm.getName(), socket, in, out, cm.getDataToShare());
			server.addUser(user);
			out.writeObject(new ConfirmConnectionMessage(server.getIp(), user.getIp(), user));
			out.flush();
		} else {
			throw new MessageException("Failed to connect with client");
		}
		return true;
	}
	
	public void endConnection() throws Exception {
		try {
			out.writeObject(new TerminateMessage(server.getIp(), user.getIp()));
			out.flush();
			
		} catch (Exception e) {
			ServerConsole.print("Failed to disconnect from server!");
			e.printStackTrace();
		}
	}
	
	public void update(Server s) {
		try {
			synchronized(user) { 
				out.writeObject(new ServerUpdateMessage(server.getIp(), user.getIp(), server.getUsers(), server.getFiles()));
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {	
		try {
			ServerConsole.print("Client requests connection");
			
			active = startConnection();
			
			ServerConsole.print(String.format("Client %d connects to server", user.getId()));
			
			while (active) {
				try {
					Message m = (Message) in.readObject();
					switch(m.type) {
					case CLIENT_SERVER_READY:
						ClientServerReadyMessage crm = (ClientServerReadyMessage) m;
						
						server.sendMessageToUser(crm.getReceiver().getId(), new ServerClientReadyMessage(server.getIp(), crm.getReceiver().getIp(), user.getIp(), crm.getPort(), crm.getFile()));
						break;
						
					case CONFIRM_TERMINATE:
						server.removeConnection(this);
						server.removeUser(user);
						
						out.close();
						in.close();
						socket.close();
						
						ServerConsole.print(String.format("Client %d disconnects from server", user.getId()));
						
						active = false;
						break;
						
					case FILE_REQUEST:
						String file = ((FileRequestMessage)m).getFile();
						ServerConsole.print(String.format("Client %d requests file %s", user.getId(), file));
						
						User sender = server.getSender(file);
						if (sender == null)
							server.sendMessageToUser(user.getId(), new ErrorMessage(server.getIp(), user.getIp(), "File not available"));
						else
							server.sendMessageToUser(sender.getId(), new SendRequestMessage(server.getIp(), sender.getIp(), user, file));
						break;
						
					case TERMINATE:
						synchronized(user) { 
							out.writeObject(new ConfirmTerminateMessage(server.getIp(), user.getIp()));
							out.flush();
						}
						server.removeConnection(this);
						server.removeUser(user);
						
						ServerConsole.print(String.format("Client %d disconnects from server", user.getId()));
						active = false;
						break;
						
					case USER_LIST:
						synchronized(user) { 
							out.writeObject(new ConfirmUserListMessage(server.getIp(), user.getIp(), server.getUsers(), server.getFiles()));
							out.flush();
						}
						break;
					
					case USER_UPDATE:
						UserUpdateMessage um = (UserUpdateMessage) m;
						server.updateUser(um.getId(), um.getDataToShare());
						break;
						
					case ERROR:
						ServerConsole.print(((ErrorMessage)m).getMessage());
						break;
						
					default:
						throw new MessageException("Invalid message");
					} 
				} catch (MessageException e){
					synchronized(user) { 
						out.writeObject(new ErrorMessage(server.getIp(), user.getIp(), e.getMessage()));
						out.flush();
					}
					System.err.println(e.getMessage());
					endConnection();
				}
			}		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			server.removeUser(user);
			server.removeConnection(this);
			e.printStackTrace();
		}
	}
}
